package net.flix.repo;

import org.springframework.data.mongodb.repository.MongoRepository;

import net.flix.entities.MyUser;

public interface MyUserRepo extends MongoRepository<MyUser, Integer>{
MyUser findByUserName(String username);
}
