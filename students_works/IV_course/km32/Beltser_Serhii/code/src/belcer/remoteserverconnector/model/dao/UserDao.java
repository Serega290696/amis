package belcer.remoteserverconnector.model.dao;

import belcer.remoteserverconnector.model.entity.User;

import java.util.List;

public interface UserDao {
  User get(String username);

  List<User> getAll() throws Exception;

  void delete(String username);

  void save(User user);

  User update(User user);

  boolean isUserWithSuchUsernameExist(String username);
}
