package silentflame.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class Conh {
    @Bean
    public VkBot initVk(VkBot vkBot) {
        vkBot.init();

        return vkBot;
    }
}
