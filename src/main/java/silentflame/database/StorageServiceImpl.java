package silentflame.database;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Component;
import silentflame.database.entities.User;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class StorageServiceImpl implements StorageService {
  private final StorageServiceDao storageServiceDao;

  public StorageServiceImpl(StorageServiceDao storageServiceDao) {
    this.storageServiceDao = storageServiceDao;
  }

  @Override
  public void createUser(User user) {
    log.info("Creating user={}", user);
    storageServiceDao.createUser(user);
  }

  @Override
  public Optional<User> getUser(Integer id) {
    val user = storageServiceDao.getUser(id);
    log.info("User={} retrieved by id", user);
    return user;
  }

  @Override
  public void updateUser(User user) {
    log.info("Updating user={}", user);
    storageServiceDao.updateUser(user);
  }

  @Override
  public void deleteUser(User user) {
    log.info("Deleting user={}", user);
    storageServiceDao.deleteUser(user);
  }

  @Override
  public List<User> getAllUsers() {
    log.info("Retrieving all users");
    return storageServiceDao.getAllUsers();
  }
}
