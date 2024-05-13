package com.ffood.g1.repository;


import com.ffood.g1.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>, CrudRepository<User, Integer> {

	@Query("SELECT c FROM User c WHERE c.email = ?1")
	User findByEmail(String email);
}

