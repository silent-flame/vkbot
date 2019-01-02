package silentflame.bot;
/*
import com.petersamokhin.bots.sdk.clients.Group;
import com.petersamokhin.bots.sdk.objects.Message;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import silentflame.database.StorageService;
import silentflame.database.entities.Lang;
import silentflame.database.entities.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;*/

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

 /* public void init() {
    try {
      CallbackApiLongPoll callbackApiLongPoll = new CallbackApiLongPoll(vkApiClient, groupActor) {
        @Override
        public void run() throws ClientException, ApiException {
          GetLongPollServerResponse longPollServer = vkApiClient.groups().getLongPollServer(groupActor).execute();
          int lastTimeStamp = longPollServer.getTs();

          while (true) {
            try {
              GetLongPollEventsResponse eventsResponse = vkApiClient.longPoll()
                .getEvents(longPollServer.getServer(), longPollServer.getKey(), lastTimeStamp).waitTime(25).execute();
              System.out.println(eventsResponse.getUpdates().toString());
              log.info("Events={}", eventsResponse.getUpdates());
              Gson gson = new Gson();
              for (JsonObject event : eventsResponse.getUpdates()) {
                Optional.ofNullable(consumers.get(event.get("type").getAsString()))
                  .ifPresent(consumer -> consumer.accept(gson.fromJson(event, CallbackMessageBase.class)));
              }
              lastTimeStamp = eventsResponse.getTs();
              Thread.sleep(requestLongPollDelay.toMillis());
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
      };
      callbackApiLongPoll.run();

    } catch (Throwable t) {
      log.error("Something went wrong", t);
      throw new VkException(t);
    }*/
    /*JSONObject params = new JSONObject();
    params.put("group_id", groupId);
    group.api().call("groups.enableOnline", params, response -> {
      log.info("VK Bot is online now");
    });*/

    /*

    onMessage(message -> {
      String text = message.getText().toLowerCase();
      if (text.startsWith("subadd ")) {
        try {

          User author = getUserFromVkApi(message.authorId());
          author = storageService.getUser(author.getId()).get();

          String args[] = message.getText().split(" ");
          List<String> subscriptions = new ArrayList<>();
          for (String arg : args) {
            if (arg.startsWith("#")) {
              subscriptions.add(arg);
              author.getSubscriptions().add(arg);
            }
          }
          storageService.updateUser(author);
          sendMessage(
            message.authorId(),
            "Subscriptions " + String.join(" ", subscriptions) + " saved");
        } catch (Throwable t) {
          log.error("Something went wrong", t);
        }
      }
      if (text.startsWith("subget")) {
        User author = getUserFromVkApi(message.authorId());
        author = storageService.getUser(author.getId()).get();
        sendMessage(author.getId(), "Your subscriptions=" + String.join(" ", author.getSubscriptions()));
      }
      if (text.startsWith("subdel")) {
        User author = getUserFromVkApi(message.authorId());
        User author1 = storageService.getUser(author.getId()).get();
        String args[] = message.getText().split(" ");
        List<String> subscriptions = new ArrayList<>();
        for (String arg : args) {
          if (arg.startsWith("#")) {
            subscriptions.add(arg);
          }
        }
        List<String> subsForDel = new ArrayList<>();
        subscriptions.forEach(subscription -> {
          if (author1.getSubscriptions().contains(subscription)) {
            subsForDel.add(subscription);
            author1.getSubscriptions().remove(subscription);
          }
        });
        storageService.updateUser(author1);
        sendMessage(author.getId(), "Your subscriptions=" + String.join(" ", subsForDel) + " deleted");
      }
    });

    group.onWallMessage(post -> {
      log.info("wall msg {}", post);
      sendMessage(MY_ID, post.toString());
    });
//    group.setCallbackApiSettings("https://vk.com/public174706111");
    group.setCallbackApiSettings("localhost");
    group.onWallPostNew(post -> {
      log.info("wall post {}", post);
      sendMessage(MY_ID, post.toString());
    });*/
//  }

  public static void main(String[] args) {
/*    GroupAuthResponse authResponse = vk.oauth()
      .groupAuthorizationCodeFlow(APP_ID, CLIENT_SECRET, REDIRECT_URI, code)
      .execute();*/
//    callbackApiLongPoll.wallPostNew();
//    CallbackApiLongPoll callbackApiLongPoll=new CallbackApiLongPoll(vk,actor);

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