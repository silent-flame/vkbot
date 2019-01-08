package silentflame.bot.commands;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import silentflame.bot.VkBotService;
import silentflame.bot.VkMessage;
import silentflame.database.StorageService;
import silentflame.database.entities.User;

@Slf4j
@Component
public class SubscriptionGetCommand implements Command {
  private final VkBotService vkBotService;
  private final StorageService storageService;
  private String keyword = "subget";
  private String description = "Retrieving user subscriptions from database";

  public SubscriptionGetCommand(VkBotService vkBotService, StorageService storageService) {
    this.vkBotService = vkBotService;
    this.storageService = storageService;
  }

  @Override
  public void execute(VkMessage message) {
    try {
      User author = storageService.getUser(message.getFromId()).orElseThrow(IllegalStateException::new);
      vkBotService.sendMessage(author.getId(), "Your subscriptions=" + String.join(" ", author.getSubscriptions()));
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