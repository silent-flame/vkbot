package silentflame.bot.commands;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import silentflame.bot.VkBotService;
import silentflame.bot.VkMessage;
import silentflame.database.StorageService;
import silentflame.database.entities.User;

import java.util.Optional;

@Slf4j
@Component
public class HelloCommand implements Command {
  private final VkBotService vkBotService;
  private final StorageService storageService;
  private String keyword = "hello";
  private String description = "Add user to database";

  public HelloCommand(VkBotService vkBotService, StorageService storageService) {
    this.vkBotService = vkBotService;
    this.storageService = storageService;
  }

  @Override
  public void execute(VkMessage message) {
    log.debug("Event={}", message);
    User user = vkBotService.getUserFromVkApi(message.getFromId());
    Optional<User> userFromDb = storageService.getUser(message.getFromId());
    if (!userFromDb.isPresent()) {
      storageService.createUser(user);
      vkBotService.sendMessage(message.getFromId(),
        "User " + user.getFirstName() + " " + user.getLastName() + " saved");
    } else {
      vkBotService.sendMessage(message.getFromId(),
        "User " + user.getFirstName() + " " + user.getLastName() + " already exists");
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
