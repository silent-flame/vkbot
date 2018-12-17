package silentflame.database;

import silentflame.database.entities.User;

import java.util.Optional;

public interface StorageService {
    void createUser(User user);

    Optional<User> getUser(Integer id);

    void updateUser(User user);

    void deleteUser(User user);
}