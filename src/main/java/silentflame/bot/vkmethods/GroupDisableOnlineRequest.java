package silentflame.bot.vkmethods;

import com.vk.api.sdk.client.AbstractQueryBuilder;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.objects.base.responses.OkResponse;

import java.util.Arrays;
import java.util.Collection;

public class GroupDisableOnlineRequest extends AbstractQueryBuilder<GroupDisableOnlineRequest, OkResponse> {
  /**
   * Builder of "groups.disableOnline" VK request
   */
  public GroupDisableOnlineRequest(VkApiClient client, GroupActor groupActor) {
    super(client, "groups.disableOnline", OkResponse.class);
    accessToken(groupActor.getAccessToken());
    groupId(groupActor.getGroupId());
  }

  private GroupDisableOnlineRequest groupId(int groupId) {
    return unsafeParam("group_id", groupId);
  }

  @Override
  protected GroupDisableOnlineRequest getThis() {
    return this;
  }

  @Override
  protected Collection<String> essentialKeys() {
    return Arrays.asList("group_id", "access_token");
  }
}
