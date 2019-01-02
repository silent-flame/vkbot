package silentflame.bot;

import com.google.gson.annotations.SerializedName;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class VkMessage {
  @SerializedName("from_id")
  private Integer fromId;
  @SerializedName("peer_id")
  private Integer peerId;
  @SerializedName("text")
  private String text;
}