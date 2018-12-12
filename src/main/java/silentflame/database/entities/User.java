package silentflame.database.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Builder
@Getter
@ToString
public class User {
    private Integer id;
    private String firstName;
    private String lastName;
    private Lang lang;
    private String subscriptions;

}
