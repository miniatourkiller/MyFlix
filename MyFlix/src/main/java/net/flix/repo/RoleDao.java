package net.flix.repo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.flix.entities.Role;

@Service
public class RoleDao {
	@Autowired
RoleRepo roleRepo;
	
	public void save(Role role) {
		List<Role> roles = new ArrayList<>();
		roles = roleRepo.findAll();
		if(roles.size() == 0) {
			roleRepo.insert(role);
		}else {
			int index = roles.size()-1;
			Role role2 = roles.get(index);
			role.setId(role2.getId()+1);
			roleRepo.insert(role);
		}
	}
}
