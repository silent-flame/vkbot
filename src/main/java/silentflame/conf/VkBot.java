package silentflame.conf;

import com.petersamokhin.bots.sdk.clients.Group;
import com.petersamokhin.bots.sdk.objects.Message;
import lombok.extern.slf4j.Slf4j;
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
            System.out.println(message);
            log.info("Retrieved message {}", message);
        });
    }
}
