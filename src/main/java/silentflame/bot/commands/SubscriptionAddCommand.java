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
public class SubscriptionAddCommand implements Command {
  private final VkBotService vkBotService;
  private final StorageService storageService;
  private String keyword = "subadd";
  private String description = "Add subscription for hashtag (example 'subadd #hashtag')";

  public SubscriptionAddCommand(VkBotService vkBotService, StorageService storageService) {
    this.vkBotService = vkBotService;
    this.storageService = storageService;
  }

  @Override
  public void execute(VkMessage message) {
    try {
      User author = storageService.getUser(message.getFromId()).orElseThrow(IllegalStateException::new);
      String[] args = message.getText().split(" ");
      List<String> subscriptions = new ArrayList<>();
      for (String arg : args) {
        if (arg.startsWith("#")) {
          subscriptions.add(arg);
          author.getSubscriptions().add(arg);
        }
      }
      storageService.updateUser(author);
      vkBotService.sendMessage(
        message.getFromId(),
        "Subscriptions : " + String.join(" ", subscriptions) + " saved");
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