package net.flix.repo;

import org.springframework.data.mongodb.repository.MongoRepository;

import net.flix.entities.Role;

public interface RoleRepo extends MongoRepository<Role, Integer>{
Role findByName(String name);
}
