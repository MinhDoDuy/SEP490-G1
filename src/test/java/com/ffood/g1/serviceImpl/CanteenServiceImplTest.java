package com.ffood.g1.serviceImpl;

import com.ffood.g1.entity.Canteen;
import com.ffood.g1.repository.CanteenRepository;
import com.ffood.g1.service.impl.CanteenServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CanteenServiceImplTest {

    @InjectMocks
    private CanteenServiceImpl canteenService;

    @Mock
    private CanteenRepository canteenRepository;

    // Trường hợp bình thường: Kiểm thử lấy tất cả các canteen
    @Test
    void testGetAllCanteens_Normal() {
        Canteen canteen1 = Canteen.builder().canteenId(1).canteenName("Canteen 1").build();
        Canteen canteen2 = Canteen.builder().canteenId(2).canteenName("Canteen 2").build();

        when(canteenRepository.findAll()).thenReturn(Arrays.asList(canteen1, canteen2));

        List<Canteen> result = canteenService.getAllCanteens();

        assertEquals(2, result.size());
        assertTrue(result.contains(canteen1));
        assertTrue(result.contains(canteen2));

        verify(canteenRepository, times(1)).findAll();
    }

    // Trường hợp bình thường: Kiểm thử lấy tất cả các canteen với phân trang
    @Test
    void testGetAllCanteensPage_Normal() {
        Pageable pageable = PageRequest.of(0, 10);

        Canteen canteen1 = Canteen.builder().canteenId(1).canteenName("Canteen 1").build();
        Canteen canteen2 = Canteen.builder().canteenId(2).canteenName("Canteen 2").build();

        Page<Canteen> page = new PageImpl<>(Arrays.asList(canteen1, canteen2), pageable, 2);

        when(canteenRepository.findAll(pageable)).thenReturn(page);

        Page<Canteen> result = canteenService.getAllCanteensPage(0, 10);

        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        assertTrue(result.getContent().contains(canteen1));
        assertTrue(result.getContent().contains(canteen2));

        verify(canteenRepository, times(1)).findAll(pageable);
    }

    // Trường hợp bình thường: Kiểm thử lưu canteen
    @Test
    void testSaveCanteen_Normal() {
        Canteen canteen = Canteen.builder().canteenId(1).canteenName("Canteen 1").build();

        canteenService.saveCanteen(canteen);

        verify(canteenRepository, times(1)).save(canteen);
    }

    // Trường hợp bình thường: Kiểm thử tìm kiếm canteen theo từ khóa
    @Test
    void testSearchCanteens_Normal() {
        Pageable pageable = PageRequest.of(0, 10);

        Canteen canteen1 = Canteen.builder().canteenId(1).canteenName("Canteen 1").build();
        Canteen canteen2 = Canteen.builder().canteenId(2).canteenName("Canteen 2").build();

        Page<Canteen> page = new PageImpl<>(Arrays.asList(canteen1, canteen2), pageable, 2);

        when(canteenRepository.findByCanteenNameContainingIgnoreCase("Canteen", pageable)).thenReturn(page);

        Page<Canteen> result = canteenService.searchCanteens("Canteen", 0, 10);

        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        assertTrue(result.getContent().contains(canteen1));
        assertTrue(result.getContent().contains(canteen2));

        verify(canteenRepository, times(1)).findByCanteenNameContainingIgnoreCase("Canteen", pageable);
    }

    // Trường hợp bình thường: Kiểm thử lấy canteen theo ID
    @Test
    void testGetCanteenById_Normal() {
        Canteen canteen = Canteen.builder().canteenId(1).canteenName("Canteen 1").build();

        when(canteenRepository.findById(1)).thenReturn(Optional.of(canteen));

        Canteen result = canteenService.getCanteenById(1);

        assertEquals(canteen, result);

        verify(canteenRepository, times(1)).findById(1);
    }

    // Trường hợp bình thường: Kiểm thử cập nhật canteen
    @Test
    void testUpdateCanteen_Normal() {
        Canteen canteen = Canteen.builder().canteenId(1).canteenName("Canteen 1").build();

        canteenService.updateCanteen(canteen);

        verify(canteenRepository, times(1)).save(canteen);
    }

    // Trường hợp bình thường: Kiểm thử đếm số lượng canteen
    @Test
    void testCountCanteens_Normal() {
        when(canteenRepository.count()).thenReturn(10L);

        Integer result = canteenService.countCanteens();

        assertEquals(10, result);

        verify(canteenRepository, times(1)).count();
    }

    // Trường hợp bình thường: Kiểm thử kiểm tra tồn tại của số điện thoại canteen
    @Test
    void testIsPhoneExist_Normal() {
        Canteen canteen = Canteen.builder().canteenId(1).canteenName("Canteen 1").canteenPhone("123456789").build();

        when(canteenRepository.findByCanteenPhone("123456789")).thenReturn(canteen);

        boolean result = canteenService.isPhoneExist("123456789");

        assertTrue(result);

        verify(canteenRepository, times(1)).findByCanteenPhone("123456789");
    }

    // Trường hợp bình thường: Kiểm thử kiểm tra tồn tại của tên canteen
    @Test
    void testIsCanteenNameExist_Normal() {
        Canteen canteen = Canteen.builder().canteenId(1).canteenName("Canteen 1").build();

        when(canteenRepository.findByCanteenName("Canteen 1")).thenReturn(canteen);

        boolean result = canteenService.isCanteenNameExist("Canteen 1");

        assertTrue(result);

        verify(canteenRepository, times(1)).findByCanteenName("Canteen 1");
    }
}
