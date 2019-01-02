package silentflame.bot.vkmethods;

import com.vk.api.sdk.client.AbstractQueryBuilder;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.objects.base.responses.OkResponse;

import java.util.Arrays;
import java.util.Collection;

public class GroupEnableOnlineRequest extends AbstractQueryBuilder<GroupEnableOnlineRequest, OkResponse> {
  /**
   * Builder of "groups.enableOnline" VK request
   */
  public GroupEnableOnlineRequest(VkApiClient client, GroupActor groupActor) {
    super(client, "groups.enableOnline", OkResponse.class);
    accessToken(groupActor.getAccessToken());
    groupId(groupActor.getGroupId());
  }

  private GroupEnableOnlineRequest groupId(int groupId) {
    return unsafeParam("group_id", groupId);
  }

  @Override
  protected GroupEnableOnlineRequest getThis() {
    return this;
  }

  @Override
  protected Collection<String> essentialKeys() {
    return Arrays.asList("group_id", "access_token");
  }
}
