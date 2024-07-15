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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoleServiceImplTest {

    @InjectMocks
    private RoleServiceImpl service;

    @Mock
    private RoleRepository repository;

    @Test
    void getAllRoles() {
        Role role1 = Role.builder().roleId(1).roleName("ROLE_USER").build();
        Role role2 = Role.builder().roleId(2).roleName("ROLE_ADMIN").build();
        List<Role> roles = Arrays.asList(role1, role2);

        when(repository.findAll()).thenReturn(roles);

        List<Role> result = service.getAllRoles();

        assertEquals(2, result.size());
        assertTrue(result.contains(role1));
        assertTrue(result.contains(role2));
        verify(repository, times(1)).findAll();
    }

    @Test
    void findRoleById() {
        Integer roleId = 1;
        Role role = Role.builder().roleId(roleId).roleName("ROLE_USER").build();

        when(repository.findById(roleId)).thenReturn(Optional.of(role));

        Role result = service.findRoleById(roleId);

        assertEquals(role, result);
        verify(repository, times(1)).findById(roleId);
    }

    @Test
    void getRoleByName() {
        String roleName = "ROLE_USER";
        Role role = Role.builder().roleId(1).roleName(roleName).build();

        when(repository.findByName(roleName)).thenReturn(role);

        Role result = service.getRoleByName(roleName);

        assertEquals(role, result);
        verify(repository, times(1)).findByName(roleName);
    }

    @Test
    void getRoleById() {
        Integer roleId = 1;
        Role role = Role.builder().roleId(roleId).roleName("ROLE_USER").build();

        when(repository.findById(roleId)).thenReturn(Optional.of(role));

        Role result = service.getRoleById(roleId);

        assertEquals(role, result);
        verify(repository, times(1)).findById(roleId);
    }
}
