package silentflame.bot;

import com.google.gson.Gson;
import com.vk.api.sdk.callback.objects.messages.CallbackMessageBase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import silentflame.database.StorageService;
import silentflame.database.entities.Lang;
import silentflame.database.entities.User;

import java.util.Optional;

@Slf4j
@Component
public class VkEventsProcessor {
  private final VkBotService vkBotService;
  private final StorageService storageService;
  private final Gson gson;

  public VkEventsProcessor(VkBotService vkBotService, StorageService storageService, Gson gson) {
    this.vkBotService = vkBotService;
    this.storageService = storageService;
    this.gson = gson;
  }

  public void onMessage(CallbackMessageBase event) {
    log.info("onMessage event={}", event);
    String command = event.getObject().get("text").getAsString().trim().toLowerCase().split(" ")[0];
    switch (command) {
      case ("привет"):
      case ("hello"):
      case ("hi"): {
        onHelloCommand(gson.fromJson(event.getObject(), VkMessage.class));
        break;
      }
      case ("помощь"):
      case ("help"): {
        onHelpCommand(gson.fromJson(event.getObject(), VkMessage.class));
        break;
      }
      case ("язык"):
      case ("lang"):
      case ("language"): {
        onLangCommand(gson.fromJson(event.getObject(), VkMessage.class));
        break;
      }
      case ("rus"):
      case ("рус"): {
        onRusCommand(gson.fromJson(event.getObject(), VkMessage.class));
        break;
      }
      case ("eng"):
      case ("анг"): {
        onEngCommand(gson.fromJson(event.getObject(), VkMessage.class));
        break;
      }


    }
  }

  public void onWallPostNew(CallbackMessageBase event) {
    log.info("onWallPostNew event={}", event);
  }

  private void onHelpCommand(VkMessage message) {
    log.debug("HelpCommand. Event={}", message);
    Lang lang = storageService.getUser(message.getFromId()).map(User::getLang).orElse(Lang.ENG);
    String text;
    if (lang == Lang.RUS) {
      text =
        "Список комманд:\n" +
          "Помощь---------вывод этого сообщения\n" +
          "Язык-----------язык общения\n" +
          "Подписки-------Вывод подписок";
    } else {
      text =
        "Command list\n" +
          "Help----------------print this message\n" +
          "Language,Lang-------communication language\n" +
          "Subscriptions-------Control of your subscriptions";
    }
    vkBotService.sendMessage(message.getFromId(), text);
  }

  private void onHelloCommand(VkMessage message) {
    log.debug("HelloCommand. Event={}", message);
    User user = vkBotService.getUserFromVkApi(message.getFromId());
    Optional<User> userFromDb = storageService.getUser(message.getFromId());
    if (!userFromDb.isPresent()) {
      storageService.createUser(user);
      vkBotService.sendMessage(message.getFromId(),
        "User " + user.getFirstName() + " " + user.getLastName() + " saved");
    } else {
      String text = userFromDb.get().getLang() == Lang.ENG ?
        "Type help to get command list" : "Напиши помощь для получеия списка комманд";
      vkBotService.sendMessage(message.getFromId(), text);
    }
  }

  private void onLangCommand(VkMessage message) {
    Lang lang = storageService.getUser(message.getFromId()).map(User::getLang).orElse(Lang.ENG);
    String text;
    if (lang == Lang.RUS) {
      text =
        "rus,рус---------Сменить язык на русский\n" +
          "eng,анг---------Сменить язык на английский";
    } else {
      text =
        "rus,рус---------Switch to russian\n" +
          "eng,анг---------Switch to english";
    }
    vkBotService.sendMessage(message.getFromId(), text);
  }

  private void onRusCommand(VkMessage message) {
    Optional<User> userOptional = storageService.getUser(message.getFromId());
    if (userOptional.isPresent()) {
      User user = userOptional.get();
      user.setLang(Lang.RUS);
      storageService.updateUser(user);
      vkBotService.sendMessage(message.getFromId(), "Язык переключен на русский");
    } else {
      vkBotService.sendMessage(message.getFromId(), "Please send \"Hello\" to adding in database");
    }
  }

  private void onEngCommand(VkMessage message) {
    Optional<User> userOptional = storageService.getUser(message.getFromId());
    if (userOptional.isPresent()) {
      User user = userOptional.get();
      user.setLang(Lang.ENG);
      storageService.updateUser(user);
      vkBotService.sendMessage(message.getFromId(), "Switched to english");
    } else {
      vkBotService.sendMessage(message.getFromId(), "Please send \"Hello\" to adding in database");
    }
  }
}