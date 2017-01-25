package belcer.remoteserverconnector.model.dao;

import belcer.remoteserverconnector.model.entity.Role;
import belcer.remoteserverconnector.model.entity.User;

import java.util.List;

public interface RoleDao {
  Role get(String roleTitle);

  List<Role> getAll() ;

  void delete(String roleTitle);

  void save(Role role);

  Role update(Role role);

  boolean isRoleWithSuchRoleTitleExist(String roleTitle);
}
