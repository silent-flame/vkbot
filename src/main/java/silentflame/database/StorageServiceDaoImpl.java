package silentflame.database;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import silentflame.database.entities.Lang;
import silentflame.database.entities.User;

import javax.sql.DataSource;

@Component
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
    public User getUser(Integer id) {
        return jdbcTemplate.query("SELECT id, first_name, last_name, lang, subscriptions FROM users WHERE id=?",
                new Object[]{id}, resultSet -> {
                    return User.builder()
                            .id(resultSet.getInt("id"))
                            .firstName(resultSet.getString("first_name"))
                            .lastName(resultSet.getString("last_name"))
                            .lang(Lang.valueOf(resultSet.getString("lang")))
                            .subscriptions(resultSet.getString("subscriptions"))
                            .build();
                });
    }

    @Override
    public void updateUser(User user) {
        jdbcTemplate.update("UPDATE users SET lang=?, subscriptions=? WHERE id=?",
                user.getLang().getValue(), user.getSubscriptions(), user.getId());
    }

    @Override
    public void deleteUser(User user) {
        jdbcTemplate.update("DELETE FROM users WHERE id=?", user.getId());
    }
}
