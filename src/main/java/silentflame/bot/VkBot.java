package silentflame.bot;

import com.petersamokhin.bots.sdk.clients.Group;
import com.petersamokhin.bots.sdk.objects.Message;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import silentflame.database.StorageService;
import silentflame.database.entities.Lang;
import silentflame.database.entities.User;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Slf4j
@Component
public class VkBot {
    private final Group group;
    private final Integer groupId;
    private static Integer myId = 24917833;
    private StorageService storageService;

    public VkBot(
            @Value("${vkbot.groupid}")
                    Integer groupId,
            @Value("${vkbot.token}")
                    String token, StorageService storageService) {
        this.group = new Group(groupId, token);
        this.groupId = groupId;
        this.storageService = storageService;
    }

    public void onMessage(Consumer<Message> consumer) {
        group.onMessage(consumer::accept);
    }

    public void onCommand(Consumer<Message> consumer, Object... commands) {
        group.onCommand(consumer::accept, commands);
    }

    public void sendMessage(Integer userId, String text) {
        new Message()
                .from(group)
                .to(userId)
                .text(text)
                .send();
    }

    public User getUserFromVkApi(Integer userId) {
        CompletableFuture<User> result = new CompletableFuture<>();
        JSONObject params = new JSONObject();
        params.put("user_ids", userId);
        params.put("name_case", "nom");
        group.api().call("users.get", params, response -> {
            JSONArray jsonArray = (JSONArray) response;
            User user = User.builder()
                    .id(userId)
                    .firstName(jsonArray.getJSONObject(0).getString("first_name"))
                    .lastName(jsonArray.getJSONObject(0).getString("last_name"))
                    .build();
            result.complete(user);
            log.info("Callback response class={} content={}", response.getClass(), response);
        });
        return result.join();
    }

    @PostConstruct
    public void enableOnline() {

        sendMessage(myId, "VK Bot is online now");
        JSONObject params = new JSONObject();
        params.put("group_id", groupId);
        group.api().call("groups.enableOnline", params, response -> {
            log.info("VK Bot is online now");
        });

        onCommand(message -> {
            User user = getUserFromVkApi(message.authorId());
            if (!storageService.getUser(message.authorId()).isPresent()) {
                storageService.createUser(user);
                sendMessage(message.authorId(),
                        "User " + user.getFirstName() + " " + user.getLastName() + " saved");
            } else {
                sendMessage(message.authorId(), message.getText());
            }
        }, "Привет", "Hello", "Hi");

        onCommand(message -> {
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
            sendMessage(message.authorId(), text);
        }, "Помощь", "Help");
        onCommand(message -> {
            Lang lang = storageService.getUser(message.authorId()).map(User::getLang).orElse(Lang.ENG);
            String text;
            if (lang == Lang.RUS) {
                text = "rus,рус        Сменить язык на русский\n" +
                        "eng,анг        Сменить язык на английский";
            } else {
                text = "rus,рус        switch to russian\n" +
                        "eng,анг        switch to english";
            }
            sendMessage(message.authorId(), text);
        }, "Lang", "Language");
        onCommand(message -> {
            Optional<User> userOptional = storageService.getUser(message.authorId());
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                user.setLang(Lang.RUS);
                storageService.updateUser(user);
                sendMessage(message.authorId(), "Язык переключен на русский");
            } else {
                sendMessage(message.authorId(), "Please send Hello to adding in database");
            }
        }, "rus", "рус");
        onCommand(message -> {
            Optional<User> userOptional = storageService.getUser(message.authorId());
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                user.setLang(Lang.ENG);
                storageService.updateUser(user);
                sendMessage(message.authorId(), "Switched to english");
            } else {
                sendMessage(message.authorId(), "Please send Hello to adding in database");
            }
        }, "eng", "анг");

        onMessage(message -> {
            User author = getUserFromVkApi(message.authorId());
            log.info("Retrieved message {} from author={}", message, author);
            storageService.getUser(author.getId());

            sendMessage(
                    message.authorId(),
                    "Hello " + author.getFirstName() + " " + author.getLastName());
        });
    }

    @PreDestroy
    public void disableOnline() {
        JSONObject params = new JSONObject();
        params.put("group_id", groupId);
        group.api().call("groups.disableOnline", params, response -> {
            log.info("VK Bot is offline now");
            sendMessage(myId, "VK Bot is offline now");
        });
    }

/*    public static void main(String[] args) {
        VkBot vkBot = new VkBot(174706111, "60fbb9f2566c4acbe18a44e5a9945385fc7ce07d1982edae5d2b5b763de3031953f59fc5617c83aabc8cd");
        vkBot.group.onCommand(new Object[]{"command"}, message ->
                log.info("Command retrieved={}", message)
        );

        new Message()
                .from(vkBot.group)
                .to(myId)
                .text("Simple send")
                .send();
        vkBot.sendMessage(myId, "method send");
       *//* JSONObject params = new JSONObject();
        params.put("peer_id", myId);

        params.put("title", " ... ");
        params.put("message", "Good night");
//        params.

        JSONObject action = new JSONObject();
        action.put("type", "text");
//        JSONObject button1=new JSONObject();
//        button1.put("button","1");
        action.put("payload", "{\"button\":\"1\"}");
//        action.put("payload", "{\"button\": \"1\"}");
        action.put("label", "Red");
        JSONObject button = new JSONObject();
        button.put("action", action);
        button.put("color", "negative");
        JSONArray buttonsLine = new JSONArray();
        buttonsLine.put(button);
        JSONArray buttons = new JSONArray();
        buttons.put(buttonsLine);
        JSONObject keyboard = new JSONObject();
        keyboard.put("one_time", false);
        keyboard.put("buttons", buttons);
        params.put("keyboard", keyboard);
        log.info("Params={}", params);
        System.out.println("Params=" + params);
        vkBot.group.api().call("messages.send", params, response -> {
            log.info("Response={}", response);
        });*//*

    }*/
}