package net.flix.repo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.flix.entities.MyUser;

@Service
public class MyUserDao {
@Autowired
MyUserRepo userRepo;

public void save(MyUser user) {
	List<MyUser> users = new ArrayList<>();
	users = userRepo.findAll();
	if(users.size() == 0) {
		userRepo.insert(user);
	}else {
		int index = users.size()-1;
		MyUser user2 = users.get(index);
		user.setId(user2.getId()+1);
		userRepo.insert(user);
	}
}
}
