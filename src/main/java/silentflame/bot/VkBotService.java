package silentflame.bot;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.objects.users.UserXtrCounters;
import com.vk.api.sdk.queries.users.UsersNameCase;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import silentflame.bot.vkmethods.GroupDisableOnlineRequest;
import silentflame.bot.vkmethods.GroupEnableOnlineRequest;
import silentflame.database.entities.User;

import java.util.List;

@Slf4j
@Component
public class VkBotService {
  private static final Integer MY_ID = 24917833;
  private final VkApiClient vkApiClient;
  private final GroupActor groupActor;

  public VkBotService(GroupActor groupActor, VkApiClient vkApiClient) {
    this.groupActor = groupActor;
    this.vkApiClient = vkApiClient;
  }

  public void enableOnline() {
    Try.run(() -> {
        new GroupEnableOnlineRequest(vkApiClient, groupActor).execute();
        sendMessage(MY_ID, "VK Bot is online now");
        log.info("VK Bot is online now");
      }
    ).onFailure(t -> log.error("Error while notifying group to online", t));
  }

  public void disableOnline() {
    Try.run(() -> {
      new GroupDisableOnlineRequest(vkApiClient, groupActor).execute();
      sendMessage(MY_ID, "VK Bot is offline now");
    }).onFailure(t ->
      log.error("Error while notifying group to offline", t));
  }

  public void sendMessage(Integer userId, String text) {
    Try.run(() ->
      vkApiClient.messages().send(groupActor)
        .peerId(userId)
        .message(text)
        .execute())
      .onFailure(t -> log.error("Error while sending message", t));
  }

  public void sendWallPost(Integer userId, Integer wallPostId) {
    log.info("Sending attachment={}", "wall-" + groupActor.getGroupId() + "_" + wallPostId);
    Try.run(() ->
      vkApiClient.messages().send(groupActor)
        .peerId(userId)
        .attachment("wall-" + groupActor.getGroupId() + "_" + wallPostId)
        .execute())
      .onFailure(t -> log.error("Error while sending wallpost", t));
  }

  public User getUserFromVkApi(Integer userId) {
    try {
      List<UserXtrCounters> response = vkApiClient.users().get(groupActor)
        .userIds(String.valueOf(userId))
        .nameCase(UsersNameCase.NOMINATIVE)
        .execute();
      return User.builder()
        .id(response.get(0).getId())
        .firstName(response.get(0).getFirstName())
        .lastName(response.get(0).getLastName())
        .build();
    } catch (Throwable t) {
      log.error("Error of retriving user from VK", t);
      throw new VkException(t);
    }
  }
}