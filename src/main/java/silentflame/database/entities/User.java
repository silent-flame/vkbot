package silentflame.database.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@Builder
@Getter
@ToString
public class User {
  private Integer id;
  private String firstName;
  private String lastName;
  @Setter
  private List<String> subscriptions;
}
