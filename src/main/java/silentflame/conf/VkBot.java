package silentflame.conf;

import com.petersamokhin.bots.sdk.clients.Group;
import com.petersamokhin.bots.sdk.objects.Message;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class VkBot {
    private final Group group;

    public VkBot(
            @Value("${vkbot.groupid}")
                    Integer groupId,
            @Value("${vkbot.token}")
                    String token) {
        this.group = new Group(groupId, token);
    }


    public void init() {
        group.onMessage(message -> {
            new Message()
                    .from(group)
                    .to(message.authorId())
                    .text("Hello")
                    .send();
            getUser(message.authorId());
            System.out.println(message);
            log.info("Retrieved message {}", message);
        });
    }

    public void sendMessage(Integer userId, String text) {
        new Message()
                .from(group)
                .to(userId)
                .text(text)
                .send();
    }

    public void getUser(Integer userId) {
        JSONObject params = new JSONObject();
        params.put("user_ids", userId);
        params.put("name_case", "nom");
        group.api().call("users.get", params, response -> log.info("Callback response {}",response));

    }
}
