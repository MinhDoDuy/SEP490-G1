package com.ffood.g1.repository;

import com.ffood.g1.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    @Query("SELECT r FROM Role r WHERE r.roleName = ?1")
    Role findByName(String name);

    List<Role> findByRoleIdNot(Integer roleId);

}



