package net.flix.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.qos.logback.classic.Logger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.flix.entities.MyUser;
import net.flix.entities.Role;
import net.flix.repo.MyUserDao;
import net.flix.repo.MyUserRepo;
import net.flix.repo.RoleDao;
import net.flix.repo.RoleRepo;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class UserServiceImp implements UserService, UserDetailsService{
@Autowired
MyUserDao userDao;
@Autowired
RoleDao roleDao;
@Autowired
MyUserRepo userRepo;
@Autowired
RoleRepo roleRepo;
@Autowired
PasswordEncoder passwordEncoder;
org.slf4j.Logger log=  LoggerFactory.getLogger(UserServiceImp.class);
	@Override
	public MyUser saveUser(MyUser user) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		userDao.save(user);
		return user;
	}
	
	

	@Override
	public Role saveRole(Role role) {
		roleDao.save(role);
		return role;
	}

	@Override
	public void AddRoleToUser(String userName, String roleName) {
		MyUser user = userRepo.findByUserName(userName);
		userRepo.delete(user);
		Role role = roleRepo.findByName(roleName);
		Collection<Role> roles = new ArrayList<>();
		roles.addAll(user.getRoles());
		roles.add(role);
		user.setRoles(roles);
		userRepo.insert(user);
	}

	@Override
	public MyUser getUser(String userName) {
		
		return userRepo.findByUserName(userName);
	}

	@Override
	public List<MyUser> getUsers() {
		return userRepo.findAll();
	}

	@Override
	public Role getRole(String roleName) {
		return roleRepo.findByName(roleName);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		MyUser user = userRepo.findByUserName(username);
		if(user == null) {
			log.warn("User not found");
		}else {
			log.info("User found");
		}
		Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
		Collection<Role> roles = user.getRoles();
		for(Role role: roles) {
			authorities.add(new SimpleGrantedAuthority(role.getName()));
		}
		return new User(user.getUserName(), user.getPassword(), authorities);
	}



	@Override
	public void updateOldPasswords(String userName) {
		MyUser user = userRepo.findByUserName(userName);
		userRepo.delete(user);
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		log.info("new password is: {}", user.getPassword());
		userRepo.insert(user);
	}

}
