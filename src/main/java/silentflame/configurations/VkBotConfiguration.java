package silentflame.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import silentflame.bot.VkBot;

@Component
public class VkBotConfiguration {
    @Bean
    public VkBot initVk(VkBot vkBot) {
        vkBot.init();

        return vkBot;
    }
}
