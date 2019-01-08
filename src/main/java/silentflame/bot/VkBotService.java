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

  public static void main(String[] args) {

    /*try {
      Integer groupId = 174706111;
      String token = "60fbb9f2566c4acbe18a44e5a9945385fc7ce07d1982edae5d2b5b763de3031953f59fc5617c83aabc8cd";
      TransportClient transportClient = HttpTransportClient.getInstance();
      VkApiClient vkApiClient = new VkApiClient(transportClient);
      GroupActor groupActor = new GroupActor(groupId, token);
      CallbackApiLongPoll callbackApiLongPoll = new CallbackApiLongPoll(vkApiClient, groupActor) {
        @Override
        public void run() throws ClientException, ApiException {
          while (true) {
            GetLongPollServerResponse longPollServer = vkApiClient.groups().getLongPollServer(groupActor).execute();
            int lastTimeStamp = longPollServer.getTs();
            while (true) {
              try {
                GetLongPollEventsResponse eventsResponse = vkApiClient.longPoll()
                  .getEvents(longPollServer.getServer(), longPollServer.getKey(), lastTimeStamp).waitTime(10).execute();
                System.out.println(eventsResponse.getUpdates().toString());
                Gson gson = new Gson();
                gson.fromJson(eventsResponse.getUpdates().get(0), CallbackMessageBase.class);
                *//*for (JsonObject jsonObject : eventsResponse.getUpdates()) {
                  parse(jsonObject);
                }*//*
                lastTimeStamp = eventsResponse.getTs();

                Thread.sleep(5000);
              } catch (LongPollServerKeyExpiredException e) {
                longPollServer = vkApiClient.groups().getLongPollServer(groupActor).execute();
                log.info("Long Poll error;" + longPollServer.toString());
                System.out.println("Long Poll error");
              } catch (Throwable t) {
                log.info("Something went wrong");
                System.out.println("Something went wrong");
              }
            }
          }
        }
      };
      callbackApiLongPoll.run();
    } catch (Throwable t) {
      System.out.println("Error" + t);
    }*/
//      Groups groups = new Groups(vk);
//      val serverResponse = groups.getLongPollServer(actor).execute();
//      val settingsResponse = groups.getLongPollSettings(actor).execute();
//      System.out.println(serverResponse);
//      System.out.println(settingsResponse);
//      GroupsSetLongPollSettingsQuery settingsQuery = new GroupsSetLongPollSettingsQuery(vk, actor);
//      settingsQuery.wallPostNew(true);
//      settingsQuery.groupId(groupId);
//    vk.videos().
//    vk.

    /*vk.longPoll().getEvents()
    GetResponse getResponse = vk.wall().get()
      .ownerId(groupId)
      .count(4)
      .execute();*/
//    System.out.println(getResponse.toString());
    /*VkBot vkBot = new VkBot(174706111, "60fbb9f2566c4acbe18a44e5a9945385fc7ce07d1982edae5d2b5b763de3031953f59fc5617c83aabc8cd", null);

    vkBot.group.onWallPostNew(post -> vkBot.sendMessage(MY_ID, post.toString()));
    vkBot.group.onWallMessage(post -> vkBot.sendMessage(MY_ID, post.toString()));*/

/*        vkBot.group.onCommand(new Object[]{"command"}, message ->
                log.info("Command retrieved={}", message)
        );

        new Message()
                .from(vkBot.group)
                .to(MY_ID)
                .text("Simple send")
                .send();
        vkBot.sendMessage(MY_ID, "method send");
        JSONObject params = new JSONObject();
        params.put("peer_id", MY_ID);

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