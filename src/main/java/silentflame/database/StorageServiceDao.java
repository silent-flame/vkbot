package silentflame.database;

import silentflame.database.entities.User;

public interface StorageServiceDao {
    void createUser(User user);

    User getUser(Integer id);

    void updateUser(User user);

    void deleteUser(User user);
}