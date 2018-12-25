package silentflame.configurations;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import silentflame.bot.VkBot;
import silentflame.database.StorageService;
import silentflame.database.entities.Lang;
import silentflame.database.entities.User;

import java.util.Optional;

//@Configuration
//@Slf4j
public class VkBotService {
//    @Autowired
    /*public void initVk(VkBot vkBot, StorageService storageService) {
        vkBot.onCommand(message -> {
            User user = vkBot.getUserFromVkApi(message.authorId());
            if (!storageService.getUser(message.authorId()).isPresent()) {
                storageService.createUser(user);
                vkBot.sendMessage(message.authorId(),
                        "User " + user.getFirstName() + " " + user.getLastName() + " saved");
            } else {
                vkBot.sendMessage(message.authorId(), message.getText());
            }
        }, "Привет", "Hello", "Hi");

        vkBot.onCommand(message -> {
            Lang lang = storageService.getUser(message.authorId()).map(User::getLang).orElse(Lang.ENG);
            String text;
            if (lang == Lang.RUS) {
                text = "Список комманд:\n" +
                        "Помощь         вывод этого сообщения\n" +
                        "Язык           язык общения\n" +
                        "Обращение      Как к вам обращаться\n" +
                        "Подписки       Вывод подписок";
            } else {
                text = "Command list\n" +
                        "Help                print this message\n" +
                        "Language,Lang       communication language\n" +
                        "Nickname            How can I call you?\n" +
                        "Subscriptions       control of your subscriptions";
            }
            vkBot.sendMessage(message.authorId(), text);
        }, "Помощь", "Help");
        vkBot.onCommand(message -> {
            Lang lang = storageService.getUser(message.authorId()).map(User::getLang).orElse(Lang.ENG);
            String text;
            if (lang == Lang.RUS) {
                text = "rus,рус        Сменить язык на русский\n" +
                        "eng,анг        Сменить язык на английский";
            } else {
                text = "rus,рус        switch to russian\n" +
                        "eng,анг        switch to english";
            }
            vkBot.sendMessage(message.authorId(), text);
        }, "Lang", "Language");
        vkBot.onCommand(message -> {
            Optional<User> userOptional = storageService.getUser(message.authorId());
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                user.setLang(Lang.RUS);
                storageService.updateUser(user);
                vkBot.sendMessage(message.authorId(), "Язык переключен на русский");
            } else {
                vkBot.sendMessage(message.authorId(), "Please send Hello to adding in database");
            }
        }, "rus", "рус");
        vkBot.onCommand(message -> {
            Optional<User> userOptional = storageService.getUser(message.authorId());
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                user.setLang(Lang.ENG);
                storageService.updateUser(user);
                vkBot.sendMessage(message.authorId(), "Switched to english");
            } else {
                vkBot.sendMessage(message.authorId(), "Please send Hello to adding in database");
            }
        }, "eng", "анг");

        vkBot.onMessage(message -> {
            User author = vkBot.getUserFromVkApi(message.authorId());
            log.info("Retrieved message {} from author={}", message, author);
            storageService.getUser(author.getId());

            vkBot.sendMessage(
                    message.authorId(),
                    "Hello " + author.getFirstName() + " " + author.getLastName());
        });
    }*/
}