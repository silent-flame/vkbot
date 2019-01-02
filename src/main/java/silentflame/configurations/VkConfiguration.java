package silentflame.configurations;

import com.google.gson.Gson;
import com.vk.api.sdk.callback.objects.messages.CallbackMessageBase;
import com.vk.api.sdk.callback.objects.messages.CallbackMessageType;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import silentflame.bot.VkEventsProcessor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Configuration
public class VkConfiguration {
  @Bean
  VkApiClient vkApiClient() {
    TransportClient transportClient = HttpTransportClient.getInstance();
    return new VkApiClient(transportClient);
  }

  @Bean
  Gson getGson(VkApiClient vkApiClient) {
    return vkApiClient.getGson();
  }

  @Bean
  GroupActor groupActor(@Value("${vkbot.groupid}")
                          Integer groupId,
                        @Value("${vkbot.token}")
                          String token) {
    return new GroupActor(groupId, token);
  }

  @Bean
  Map<String, Consumer<CallbackMessageBase>> consumers(VkEventsProcessor vkEventsProcessor) {
    Map<String, Consumer<CallbackMessageBase>> consumers = new HashMap<>();
    consumers.put(CallbackMessageType.MESSAGE_NEW.getValue(), vkEventsProcessor::onMessage);
    consumers.put(CallbackMessageType.WALL_POST_NEW.getValue(), vkEventsProcessor::onWallPostNew);
    return consumers;
  }
}
