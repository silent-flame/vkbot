package silentflame.bot;

import com.google.gson.Gson;
import com.vk.api.sdk.callback.objects.messages.CallbackMessageBase;
import com.vk.api.sdk.objects.wall.WallPost;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import silentflame.bot.commands.Command;
import silentflame.bot.commands.HelloCommand;
import silentflame.bot.commands.SubscriptionAddCommand;
import silentflame.bot.commands.SubscriptionDelCommand;
import silentflame.bot.commands.SubscriptionGetCommand;
import silentflame.database.StorageService;
import silentflame.database.entities.User;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class VkEventsProcessor {
  private final VkBotService vkBotService;
  private final StorageService storageService;
  private final Gson gson;
  @Getter
  private final Set<Command> commands = new HashSet<>();

  public VkEventsProcessor(VkBotService vkBotService, StorageService storageService, @Qualifier("vkGson") Gson gson,
                           HelloCommand helloCommand, SubscriptionAddCommand subAddCommand,
                           SubscriptionGetCommand subGetCommand, SubscriptionDelCommand subDelCommand) {
    this.vkBotService = vkBotService;
    this.storageService = storageService;
    this.gson = gson;
    this.commands.add(helloCommand);
    this.commands.add(subAddCommand);
    this.commands.add(subGetCommand);
    this.commands.add(subDelCommand);
  }

  public void onMessage(CallbackMessageBase event) {
    try {
      log.debug("onMessage event={}", event);
      String firstWord = event.getObject().get("text").getAsString().trim().toLowerCase().split(" ")[0];
      Command command = commands.stream().filter(itemCommand -> itemCommand.getKeyWord().equals(firstWord))
        .findFirst().orElseThrow(IllegalArgumentException::new);
      command.execute(gson.fromJson(event.getObject(), VkMessage.class));
    } catch (IllegalArgumentException e) {
      vkBotService.sendMessage(gson.fromJson(event.getObject(), VkMessage.class).getFromId(), "Command unsupported.\n" +
        "Type 'help' to get command list");
    } catch (Throwable t) {
      log.error("Something went wrong", t);
    }
  }

  public void onWallPostNew(CallbackMessageBase event) {
    try {
      log.info("onWallPostNew event={}", event);
      WallPost wallPost = gson.fromJson(event.getObject(), WallPost.class);
      List<User> allUsers = storageService.getAllUsers();
      Arrays.stream(wallPost.getText().split(" "))
        .forEach(word -> {
          log.debug("Word={}", word);
          if (word.startsWith("#")) {
            allUsers.stream().filter(user -> user.getSubscriptions().contains(word)).findFirst().ifPresent(user -> {
              vkBotService.sendWallPost(user.getId(), wallPost.getId());
            });
          }
        });
    } catch (Throwable t) {
      log.error("Something went wrong", t);
    }
  }
}