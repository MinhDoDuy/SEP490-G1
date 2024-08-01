package com.ffood.g1.service.impl;

import com.ffood.g1.entity.Role;
import com.ffood.g1.repository.RoleRepository;
import com.ffood.g1.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }


    @Override
    public Role findRoleById(Integer roleId) {
        return roleRepository.findById(roleId).orElse(null);
    }

    @Override
    public Role getRoleByName(String roleName) {
        return roleRepository.findByName(roleName);
    }

    public Role getRoleById(Integer roleId) {
        return roleRepository.findById(roleId).orElse(null);
    }
}
