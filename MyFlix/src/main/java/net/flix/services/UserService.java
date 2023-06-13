package net.flix.services;

import java.util.List;

import net.flix.entities.MyUser;
import net.flix.entities.Role;

public interface UserService {
MyUser saveUser(MyUser user);
Role saveRole(Role role);
void AddRoleToUser(String userName, String roleName);
MyUser getUser(String userName);
List<MyUser> getUsers();
Role getRole(String roleName);
void updateOldPasswords(String userName);
}
