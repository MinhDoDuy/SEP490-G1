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

	@Query("SELECT u FROM User u WHERE " +
			"(LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
			"OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
			"OR LOWER(u.codeName) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
			"AND (:roleId IS NULL OR u.role.roleId = :roleId) " +
			"AND (:canteenId IS NULL OR u.canteen.canteenId = :canteenId)")
	Page<User> searchUsers(String keyword, Integer roleId, Integer canteenId, Pageable pageable);

	@Query("SELECT u FROM User u WHERE u.canteen.canteenId = :canteenId AND u.role.roleId = 2")
	Page<User> findAllStaffByCanteenId(@Param("canteenId") Integer canteenId, Pageable pageable);

	@Query("SELECT u FROM User u WHERE u.role.roleName = :roleName")
	Page<User> findAllByRoleName(@Param("roleName") String roleName, Pageable pageable);


	@Query("SELECT COUNT(u) FROM User u WHERE u.role.roleId = 2 AND u.canteen.canteenId = :canteenId")
	Integer countStaffByCanteenId(@Param("canteenId") Integer canteenId);

	@Query("SELECT u FROM User u WHERE u.canteen.canteenId = :canteenId AND u.role.roleId = 2")
	List<User> findAllStaffByCanteenIdToShip(@Param("canteenId") Integer canteenId);
}
