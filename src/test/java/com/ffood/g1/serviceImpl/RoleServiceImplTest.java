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

    // Trường hợp bình thường: Kiểm thử lấy tất cả các vai trò
    @Test
    void testGetAllRoles_Normal() {
        // Tạo đối tượng Role sử dụng builder
        Role role1 = Role.builder().roleId(1).roleName("ROLE_USER").build();
        Role role2 = Role.builder().roleId(2).roleName("ROLE_ADMIN").build();

        // Giả lập phương thức findAll của roleRepository để trả về danh sách các vai trò
        when(roleRepository.findAll()).thenReturn(Arrays.asList(role1, role2));

        // Gọi phương thức service
        List<Role> result = roleService.getAllRoles();

        // Kiểm tra xem kết quả trả về có khớp với mong đợi hay không
        assertEquals(Arrays.asList(role1, role2), result);

        // Xác minh rằng phương thức findAll của repository đã được gọi chính xác một lần
        verify(roleRepository, times(1)).findAll();
    }

    // Trường hợp bất thường: Kiểm thử lấy tất cả các vai trò khi không có vai trò nào
    @Test
    void testGetAllRoles_Abnormal() {
        // Giả lập phương thức findAll của roleRepository để trả về danh sách rỗng
        when(roleRepository.findAll()).thenReturn(Collections.emptyList());

        // Gọi phương thức service
        List<Role> result = roleService.getAllRoles();

        // Kiểm tra xem kết quả trả về có rỗng hay không
        assertTrue(result.isEmpty());

        // Xác minh rằng phương thức findAll của repository đã được gọi chính xác một lần
        verify(roleRepository, times(1)).findAll();
    }

    // Trường hợp ranh giới: Kiểm thử lấy tất cả các vai trò khi roleRepository trả về null
    @Test
    void testGetAllRoles_Boundary() {
        // Giả lập phương thức findAll của roleRepository để trả về null
        when(roleRepository.findAll()).thenReturn(null);

        // Gọi phương thức service
        List<Role> result = roleService.getAllRoles();

        // Kiểm tra xem kết quả trả về có phải là null hay không
        assertNull(result);

        // Xác minh rằng phương thức findAll của repository đã được gọi chính xác một lần
        verify(roleRepository, times(1)).findAll();
    }

    // Trường hợp bình thường: Kiểm thử tìm vai trò theo roleId
    @Test
    void testFindRoleById_Normal() {
        // Tạo đối tượng Role sử dụng builder
        Role role = Role.builder().roleId(1).roleName("ROLE_USER").build();

        // Giả lập phương thức findById của roleRepository để trả về đối tượng Role
        when(roleRepository.findById(1)).thenReturn(Optional.of(role));

        // Gọi phương thức service
        Role result = roleService.findRoleById(1);

        // Kiểm tra xem kết quả trả về có khớp với mong đợi hay không
        assertEquals(role, result);

        // Xác minh rằng phương thức findById của repository đã được gọi chính xác một lần
        verify(roleRepository, times(1)).findById(1);
    }

    // Trường hợp bất thường: Kiểm thử tìm vai trò theo roleId khi không tìm thấy vai trò
    @Test
    void testFindRoleById_Abnormal() {
        // Giả lập phương thức findById của roleRepository để trả về Optional rỗng
        when(roleRepository.findById(1)).thenReturn(Optional.empty());

        // Gọi phương thức service
        Role result = roleService.findRoleById(1);

        // Kiểm tra xem kết quả trả về có phải là null hay không
        assertNull(result);

        // Xác minh rằng phương thức findById của repository đã được gọi chính xác một lần
        verify(roleRepository, times(1)).findById(1);
    }

    // Trường hợp ranh giới: Kiểm thử tìm vai trò theo roleId với roleId không hợp lệ
    @Test
    void testFindRoleById_Boundary() {
        // Giả lập phương thức findById của roleRepository để trả về Optional rỗng
        when(roleRepository.findById(null)).thenReturn(Optional.empty());

        // Gọi phương thức service
        Role result = roleService.findRoleById(null);

        // Kiểm tra xem kết quả trả về có phải là null hay không
        assertNull(result);

        // Xác minh rằng phương thức findById của repository đã được gọi chính xác một lần
        verify(roleRepository, times(1)).findById(null);
    }

    // Trường hợp bình thường: Kiểm thử tìm vai trò theo tên
    @Test
    void testGetRoleByName_Normal() {
        // Tạo đối tượng Role sử dụng builder
        Role role = Role.builder().roleId(1).roleName("ROLE_USER").build();

        // Giả lập phương thức findByName của roleRepository để trả về đối tượng Role
        when(roleRepository.findByName("ROLE_USER")).thenReturn(role);

        // Gọi phương thức service
        Role result = roleService.getRoleByName("ROLE_USER");

        // Kiểm tra xem kết quả trả về có khớp với mong đợi hay không
        assertEquals(role, result);

        // Xác minh rằng phương thức findByName của repository đã được gọi chính xác một lần
        verify(roleRepository, times(1)).findByName("ROLE_USER");
    }

    // Trường hợp bất thường: Kiểm thử tìm vai trò theo tên khi không tìm thấy vai trò
    @Test
    void testGetRoleByName_Abnormal() {
        // Giả lập phương thức findByName của roleRepository để trả về null
        when(roleRepository.findByName("ROLE_UNKNOWN")).thenReturn(null);

        // Gọi phương thức service
        Role result = roleService.getRoleByName("ROLE_UNKNOWN");

        // Kiểm tra xem kết quả trả về có phải là null hay không
        assertNull(result);

        // Xác minh rằng phương thức findByName của repository đã được gọi chính xác một lần
        verify(roleRepository, times(1)).findByName("ROLE_UNKNOWN");
    }

    // Trường hợp ranh giới: Kiểm thử tìm vai trò theo tên với tên rỗng
    @Test
    void testGetRoleByName_Boundary() {
        // Giả lập phương thức findByName của roleRepository để trả về null
        when(roleRepository.findByName("")).thenReturn(null);

        // Gọi phương thức service
        Role result = roleService.getRoleByName("");

        // Kiểm tra xem kết quả trả về có phải là null hay không
        assertNull(result);

        // Xác minh rằng phương thức findByName của repository đã được gọi chính xác một lần
        verify(roleRepository, times(1)).findByName("");
    }

    // Các bài kiểm thử khác cũng tương tự như trên, sử dụng builder để tạo các đối tượng Role và kiểm tra các phương thức khác trong RoleServiceImpl
}
