package belcer.remoteserverconnector.model.dao.oracle_impl;

import belcer.remoteserverconnector.model.dao.UserDao;
import belcer.remoteserverconnector.model.entity.User;

import java.util.List;

public class UserDaoImpl implements UserDao {
  OracleConnector connector = new OracleConnector();

  public User get(String username) {
    return connector.getUser(username);
  }

  public List<User> getAll() throws Exception {
    return connector.getAllUsers();
  }

  public void delete(String username) {
    connector.deleteUser(username);
  }

  public void save(User user) {
    System.out.println("UserDaoImpl.save: "+ user);
    connector.saveUser(user);
  }

  public User update(User user) {
    User updated = connector.updateUser(user.getUsername(), user);
    return updated;
  }

  @Override
  public boolean isUserWithSuchUsernameExist(String username) {
    boolean userExist;
    User user = connector.getUser(username);
    userExist = user != null;
    return userExist;
  }
}
