package silentflame.database.entities;

import lombok.*;

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
    private Lang lang;
    @Setter
    private List<String> subscriptions;
}
