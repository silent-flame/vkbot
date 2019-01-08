package silentflame.bot.scheduler;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.vk.api.sdk.callback.longpoll.responses.GetLongPollEventsResponse;
import com.vk.api.sdk.callback.objects.messages.CallbackMessageBase;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.LongPollServerKeyExpiredException;
import com.vk.api.sdk.objects.groups.responses.GetLongPollServerResponse;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import lombok.var;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@Component
@Slf4j
public class GetLongPollEventsJob implements Job {

  @Override
  public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    try {
      log.debug("Starting job execution");
      VkApiClient vkApiClient = (VkApiClient) jobExecutionContext.getScheduler().getContext().get("vkApiClient");
      GroupActor groupActor = (GroupActor) jobExecutionContext.getScheduler().getContext().get("groupActor");
      val consumers = (Map<String, Consumer<CallbackMessageBase>>) jobExecutionContext.getScheduler().getContext().get("consumers");

      var longPollServer = (GetLongPollServerResponse) jobExecutionContext.getScheduler().getContext().get("longPollServer");
      Integer lastTimeStamp = (Integer) jobExecutionContext.getScheduler().getContext().get("lastTimeStamp");
      Gson gson = (Gson) jobExecutionContext.getScheduler().getContext().get("gson");
      try {
        GetLongPollEventsResponse eventsResponse = vkApiClient.longPoll()
          .getEvents(longPollServer.getServer(), longPollServer.getKey(), lastTimeStamp).waitTime(25).execute();
        log.debug("Events={}", eventsResponse.getUpdates());
        for (JsonObject event : eventsResponse.getUpdates()) {
          Optional.ofNullable(consumers.get(event.get("type").getAsString()))
            .ifPresent(consumer -> consumer.accept(gson.fromJson(event, CallbackMessageBase.class)));
        }
        lastTimeStamp = eventsResponse.getTs();
        jobExecutionContext.getScheduler().getContext().put("lastTimeStamp", lastTimeStamp);
      } catch (LongPollServerKeyExpiredException e) {
        log.error("Long Poll error", e);
        longPollServer = vkApiClient.groups().getLongPollServer(groupActor).execute();
        lastTimeStamp = longPollServer.getTs();
        jobExecutionContext.getScheduler().getContext().put("lastTimeStamp", lastTimeStamp);
      } catch (Throwable t) {
        log.error("Something went wrong", t);
      }
      log.debug("Ended job execution");
    } catch (Throwable t) {
      log.error("Something went wrong", t);
    }
  }
}