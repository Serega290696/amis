package belcer.remoteserverconnector.model.dao.oracle_impl;

import belcer.remoteserverconnector.model.dao.RoleDao;
import belcer.remoteserverconnector.model.dao.UserDao;
import belcer.remoteserverconnector.model.entity.Role;
import belcer.remoteserverconnector.model.entity.User;

import java.util.List;

public class RoleDaoImpl implements RoleDao {
  OracleConnector connector = new OracleConnector();


  @Override
  public Role get(String roleTitle) {
    return null;
  }

  @Override
  public List<Role> getAll() {
    return connector.getAllRoles();
  }

  @Override
  public void delete(String roleTitle) {

  }

  public void save(Role role) {
    System.out.println("RoleDaoImpl.save: " + role);
    connector.saveRole(role);
  }

  @Override
  public Role update(Role role) {
    return null;
  }

  @Override
  public boolean isRoleWithSuchRoleTitleExist(String roleTitle) {
    return false;
  }

}
