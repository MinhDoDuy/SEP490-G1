package com.ffood.g1.repository;

import com.ffood.g1.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
	User findByEmail(String email);

	User findByCodeName(String codeName);

	User findByPhone(String phone);

	Page<User> findAll(Pageable pageable);

	//search list user with fullname , codename , email
	Page<User> findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrCodeNameContainingIgnoreCase
	(String fullName, String email, String codeName, Pageable pageable);

	List<User> findByRoleRoleId(Integer role_id);



	@Query("SELECT u FROM User u WHERE u.canteen.canteenId = :canteenId AND u.role.roleId = 2")
	Page<User> findAllStaffByCanteenId(@Param("canteenId") Integer canteenId, Pageable pageable);

	@Query("SELECT u FROM User u WHERE u.role.roleName = :roleName")
	Page<User> findAllByRoleName(@Param("roleName") String roleName, Pageable pageable);



}