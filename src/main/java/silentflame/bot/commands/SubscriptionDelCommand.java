package silentflame.bot.commands;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import silentflame.bot.VkBotService;
import silentflame.bot.VkMessage;
import silentflame.database.StorageService;
import silentflame.database.entities.User;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class SubscriptionDelCommand implements Command {
  private final StorageService storageService;
  private final VkBotService vkBotService;
  private String keyword = "subdel";
  private String description = "Deleting user subscriptions from database (example 'subdel #sub1 #sub2')";

  public SubscriptionDelCommand(StorageService storageService, VkBotService vkBotService) {
    this.storageService = storageService;
    this.vkBotService = vkBotService;
  }

  @Override
  public void execute(VkMessage message) {
    try {
      User author1 = storageService.getUser(message.getFromId()).orElseThrow(IllegalStateException::new);
      String args[] = message.getText().split(" ");
      List<String> subscriptions = new ArrayList<>();
      for (String arg : args) {
        if (arg.startsWith("#")) {
          subscriptions.add(arg);
        }
      }
      List<String> subsForDel = new ArrayList<>();
      subscriptions.forEach(subscription -> {
        if (author1.getSubscriptions().contains(subscription)) {
          subsForDel.add(subscription);
          author1.getSubscriptions().remove(subscription);
        }
      });
      storageService.updateUser(author1);
      vkBotService.sendMessage(message.getFromId(), "Your subscriptions=" + String.join(" ", subsForDel) + " deleted");
    } catch (IllegalStateException e) {
      vkBotService.sendMessage(message.getFromId(), "Firstly write 'Hello'");
    } catch (Throwable t) {
      log.error("Something went wrong", t);
    }
  }

  @Override
  public String getKeyWord() {
    return keyword;
  }

  @Override
  public String getDescription() {
    return description;
  }
}