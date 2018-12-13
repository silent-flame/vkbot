package silentflame.configurations;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import silentflame.bot.VkBot;
import silentflame.database.entities.User;

@Component
@Slf4j
public class VkBotConfiguration {
    @Autowired
    public void initVk(VkBot vkBot) {
        vkBot.onMessage(message -> {
            User author = vkBot.getUserFromVkApi(message.authorId());
            vkBot.sendMessage(
                    message.authorId(),
                    "Hello " + author.getFirstName() + " " + author.getLastName());
            log.info("Retrieved message {} from author={}", message, author);
        });
    }
}