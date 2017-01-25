package belcer.remoteserverconnector.model.dao.oracle_impl;

import belcer.remoteserverconnector.model.dao.ConnectionProfileDao;
import belcer.remoteserverconnector.model.entity.ConnectionProfile;

import java.util.List;

public class ConnectionProfileDaoOracleImpl implements ConnectionProfileDao {

  private OracleConnector connector = new OracleConnector();

  @Override
  public ConnectionProfile get(String title, String username) {
    return connector.getConnection(title, username);
  }

  @Override
  public List<ConnectionProfile> getAll() {
    return connector.getAllConnections();
  }

  @Override
  public void delete(String username, String title) {
    connector.deleteConnection(username, title);
  }

  @Override
  public void save(ConnectionProfile connection) {
    connector.saveConnection(connection);
  }

  @Override
  public void update(ConnectionProfile connection) {
    connector.updateConnection(connection);
  }

  @Override
  public boolean isConnectionWithSuchTitleAndUserExist(String title, String username) {
    boolean suchConnectionExist;
    ConnectionProfile connection = connector.getConnection(title, username);
    suchConnectionExist = connection != null;
    return suchConnectionExist;
  }

  @Override
  public List<ConnectionProfile> getAllForUser(String username) {
    return connector.getConnections(username);
  }

  @Override
  public void saveOrUpdate(ConnectionProfile connectionProfile) {
    ConnectionProfile connection = connector.getConnection(connectionProfile.getTitle(), connectionProfile.getUsername());
    if (connection == null) {
      connector.saveConnection(connectionProfile);
    } else {
      connector.updateConnection(connectionProfile);
    }
  }
}
