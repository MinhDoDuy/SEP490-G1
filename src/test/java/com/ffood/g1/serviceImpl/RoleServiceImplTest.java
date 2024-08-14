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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoleServiceImplTest {

    @InjectMocks
    private RoleServiceImpl roleService;

    @Mock
    private RoleRepository roleRepository;

    // Kiểm thử lấy tất cả các Role
    @Test
    void testGetAllRoles_Normal() {
        Role role1 = new Role();
        role1.setRoleId(1);
        role1.setRoleName("ROLE_USER");

        Role role2 = new Role();
        role2.setRoleId(2);
        role2.setRoleName("ROLE_ADMIN");

        when(roleRepository.findAll()).thenReturn(Arrays.asList(role1, role2));

        List<Role> result = roleService.getAllRoles();

        assertEquals(2, result.size());
        assertEquals("ROLE_USER", result.get(0).getRoleName());
        assertEquals("ROLE_ADMIN", result.get(1).getRoleName());

        verify(roleRepository, times(1)).findAll();
    }

    // Kiểm thử tìm Role theo ID
    @Test
    void testFindRoleById_Normal() {
        Role role = new Role();
        role.setRoleId(1);
        role.setRoleName("ROLE_USER");

        when(roleRepository.findById(1)).thenReturn(Optional.of(role));

        Role result = roleService.findRoleById(1);

        assertEquals("ROLE_USER", result.getRoleName());
        verify(roleRepository, times(1)).findById(1);
    }

    // Kiểm thử khi không tìm thấy Role theo ID
    @Test
    void testFindRoleById_NotFound() {
        when(roleRepository.findById(1)).thenReturn(Optional.empty());

        Role result = roleService.findRoleById(1);

        assertNull(result);
        verify(roleRepository, times(1)).findById(1);
    }

    // Kiểm thử tìm Role theo tên
    @Test
    void testGetRoleByName_Normal() {
        Role role = new Role();
        role.setRoleName("ROLE_USER");

        when(roleRepository.findByName("ROLE_USER")).thenReturn(role);

        Role result = roleService.getRoleByName("ROLE_USER");

        assertEquals("ROLE_USER", result.getRoleName());
        verify(roleRepository, times(1)).findByName("ROLE_USER");
    }

    // Kiểm thử tìm Role theo ID (Phương thức getRoleById)
    @Test
    void testGetRoleById_Normal() {
        Role role = new Role();
        role.setRoleId(1);
        role.setRoleName("ROLE_USER");

        when(roleRepository.findById(1)).thenReturn(Optional.of(role));

        Role result = roleService.getRoleById(1);

        assertEquals("ROLE_USER", result.getRoleName());
        verify(roleRepository, times(1)).findById(1);
    }

    // Kiểm thử khi không tìm thấy Role theo tên
    @Test
    void testGetRoleByName_NotFound() {
        when(roleRepository.findByName("ROLE_UNKNOWN")).thenReturn(null);

        Role result = roleService.getRoleByName("ROLE_UNKNOWN");

        assertNull(result);
        verify(roleRepository, times(1)).findByName("ROLE_UNKNOWN");
    }
}
