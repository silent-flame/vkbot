package silentflame.bot.scheduler;

import com.google.gson.Gson;
import com.vk.api.sdk.callback.objects.messages.CallbackMessageBase;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.objects.groups.responses.GetLongPollServerResponse;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import silentflame.bot.VkBotService;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.Duration;
import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;

@Slf4j
@Component
public class GetLongPollEventsScheduler {
  private final Map<String, Consumer<CallbackMessageBase>> consumers;
  private final VkApiClient vkApiClient;
  private final GroupActor groupActor;
  private final VkBotService vkBotService;
  private final Gson gson;
  private final Duration delay;
  private Scheduler scheduler;

  public GetLongPollEventsScheduler(Map<String, Consumer<CallbackMessageBase>> consumers,
                                    VkApiClient vkApiClient, GroupActor groupActor,
                                    VkBotService vkBotService,
                                    Gson gson, @Value("${vkbot.longpoll.delay}") Integer delay) {
    this.consumers = consumers;
    this.vkApiClient = vkApiClient;
    this.groupActor = groupActor;
    this.vkBotService = vkBotService;
    this.gson = gson;
    this.delay = Duration.ofMillis(delay);
  }

  @PostConstruct
  public void start() {
    try {
      log.info("Configuring and starting VK LongPoll Events Request scheduler");
      Properties properties = new Properties();
      properties.put("org.quartz.threadPool.threadCount", "1");
      StdSchedulerFactory stdSchedulerFactory = new StdSchedulerFactory(properties);
      scheduler = stdSchedulerFactory.getScheduler();
      JobDetail jobDetail = JobBuilder.newJob(GetLongPollEventsJob.class).build();
      SimpleTrigger trigger = TriggerBuilder.newTrigger()
        .withSchedule(SimpleScheduleBuilder.simpleSchedule()
          .withIntervalInMilliseconds(delay.toMillis())
          .repeatForever()
        ).build();
      scheduler.scheduleJob(jobDetail, trigger);
      scheduler.getContext().put("vkApiClient", vkApiClient);
      scheduler.getContext().put("groupActor", groupActor);
      scheduler.getContext().put("consumers", consumers);
      GetLongPollServerResponse longPollServer = vkApiClient.groups().getLongPollServer(groupActor).execute();
      Integer lastTimeStamp = longPollServer.getTs();
      scheduler.getContext().put("longPollServer", longPollServer);
      scheduler.getContext().put("lastTimeStamp", lastTimeStamp);
      scheduler.getContext().put("gson", gson);
      vkBotService.enableOnline();
      scheduler.start();
    } catch (Throwable t) {
      log.error("Error while starting scheduler", t);
      throw new RuntimeException(t);
    }
  }

  @PreDestroy
  public void stop() {
    try {
      scheduler.shutdown();
      vkBotService.disableOnline();
    } catch (SchedulerException e) {
      log.error("Error occurred while shutdown scheduler", e);
    }
  }
}