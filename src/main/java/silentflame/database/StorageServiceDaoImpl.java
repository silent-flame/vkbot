package silentflame.database;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import silentflame.database.entities.User;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class StorageServiceDaoImpl implements StorageServiceDao {
  private final JdbcTemplate jdbcTemplate;

  public StorageServiceDaoImpl(DataSource dataSource) {
    this.jdbcTemplate = new JdbcTemplate(dataSource);
  }

  @Override
  public void createUser(User user) {
    jdbcTemplate.update(
      "INSERT INTO users (id, first_name, last_name, subscriptions) VALUES (?, ?, ?, ?)",
      user.getId(), user.getFirstName(), user.getLastName(), user.getSubscriptions());
  }

  @Override
  public Optional<User> getUser(Integer id) {
    try {
      return jdbcTemplate.query("SELECT id, first_name, last_name, subscriptions FROM users WHERE id=?",
        new Object[] {id}, resultSet -> {
          if (resultSet.getInt("id") != 0) {
            return Optional.of(retrieveUser(resultSet));
          } else {
            return Optional.empty();
          }
        });
    } catch (Throwable t) {
      log.error("Database error", t);
      return Optional.empty();
    }
  }

  @Override
  public void updateUser(User user) {
    try {
      jdbcTemplate.update("UPDATE users SET subscriptions=? WHERE id=?",
        String.join(",", user.getSubscriptions()), user.getId());
    } catch (Throwable t) {
      log.error("Error of updating user=" + user, t);
    }
  }

  @Override
  public void deleteUser(User user) {
    jdbcTemplate.update("DELETE FROM users WHERE id=?", user.getId());
  }

  @Override
  public List<User> getAllUsers() {
    List<User> users = new ArrayList<>();
    jdbcTemplate.query("SELECT * FROM users", resultSet -> {
      do {
        users.add(retrieveUser(resultSet));
      } while (resultSet.next());
    });
    return users;
  }

  private static User retrieveUser(ResultSet resultSet) throws SQLException {
    return User.builder()
      .id(resultSet.getInt("id"))
      .firstName(resultSet.getString("first_name"))
      .lastName(resultSet.getString("last_name"))
      .subscriptions(Optional.ofNullable(resultSet.getString("subscriptions"))
        .map(field -> new ArrayList<>(Arrays.asList(field.split(","))))
        .orElse(new ArrayList<>()))
      .build();
  }
}
