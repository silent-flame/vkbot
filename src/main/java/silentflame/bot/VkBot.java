package silentflame.bot;

import com.petersamokhin.bots.sdk.clients.Group;
import com.petersamokhin.bots.sdk.objects.Message;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import silentflame.database.entities.User;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Slf4j
@Component
public class VkBot {
    private final Group group;
    private final Integer groupId;
    private static Integer myId = 24917833;

    public VkBot(
            @Value("${vkbot.groupid}")
                    Integer groupId,
            @Value("${vkbot.token}")
                    String token) {
        this.group = new Group(groupId, token);
        this.groupId = groupId;
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
        JSONObject params = new JSONObject();
        params.put("group_id", groupId);
        group.api().call("groups.enableOnline", params, response -> {
            log.info("VK Bot is online now");
            sendMessage(myId, "VK Bot is online now");
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

    public static void main(String[] args) {
        VkBot vkBot = new VkBot(174706111, "60fbb9f2566c4acbe18a44e5a9945385fc7ce07d1982edae5d2b5b763de3031953f59fc5617c83aabc8cd");
        vkBot.group.onCommand(new Object[]{"command"}, message ->
                log.info("Command retrieved={}", message)
        );

        new Message()
                .from(vkBot.group)
                .to(myId)
                .text("Simple send")
                .send();
      vkBot.sendMessage(myId,"method send");
       /* JSONObject params = new JSONObject();
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
        });*/

    }
}