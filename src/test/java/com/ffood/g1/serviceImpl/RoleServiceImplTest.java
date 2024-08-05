package com.ffood.g1.serviceImpl;

import com.ffood.g1.entity.Role;
import com.ffood.g1.repository.RoleRepository;
import com.ffood.g1.service.impl.RoleServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoleServiceImplTest {

    @InjectMocks
    private RoleServiceImpl roleService;

    @Mock
    private RoleRepository roleRepository;

    @Test
    void testGetAllRoles() {
        List<Role> expectedRoles = Arrays.asList(new Role(), new Role());

        when(roleRepository.findAll()).thenReturn(expectedRoles);

        List<Role> result = roleService.getAllRoles();

        assertEquals(expectedRoles, result);
        verify(roleRepository, times(1)).findAll();
    }

    @Test
    void testFindRoleById() {
        Integer roleId = 1;
        Role expectedRole = new Role();

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(expectedRole));

        Role result = roleService.findRoleById(roleId);

        assertEquals(expectedRole, result);
        verify(roleRepository, times(1)).findById(roleId);
    }

    @Test
    void testGetRoleByName() {
        String roleName = "ROLE_USER";
        Role expectedRole = new Role();

        when(roleRepository.findByName(roleName)).thenReturn(expectedRole);

        Role result = roleService.getRoleByName(roleName);

        assertEquals(expectedRole, result);
        verify(roleRepository, times(1)).findByName(roleName);
    }

    @Test
    void testGetRoleById() {
        Integer roleId = 1;
        Role expectedRole = new Role();

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(expectedRole));

        Role result = roleService.getRoleById(roleId);

        assertEquals(expectedRole, result);
        verify(roleRepository, times(1)).findById(roleId);
    }
}
