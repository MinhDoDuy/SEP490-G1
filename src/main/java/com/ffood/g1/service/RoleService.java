package com.ffood.g1.service;

import com.ffood.g1.entity.Role;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface RoleService {
    List<Role> getAllRoles();


}
