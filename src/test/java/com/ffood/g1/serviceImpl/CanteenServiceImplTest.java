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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CanteenServiceImplTest {

    @InjectMocks
    private CanteenServiceImpl canteenService;

    @Mock
    private CanteenRepository canteenRepository;

    @Test
    void testGetAllCanteens() {
        List<Canteen> expectedCanteens = Arrays.asList(new Canteen(), new Canteen());

        when(canteenRepository.findAll()).thenReturn(expectedCanteens);

        List<Canteen> result = canteenService.getAllCanteens();

        assertEquals(expectedCanteens, result);
        verify(canteenRepository, times(1)).findAll();
    }

    @Test
    void testGetAllCanteensPage() {
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);
        Page<Canteen> expectedPage = new PageImpl<>(Collections.emptyList());

        when(canteenRepository.findAll(pageable)).thenReturn(expectedPage);

        Page<Canteen> result = canteenService.getAllCanteensPage(page, size);

        assertEquals(expectedPage, result);
        verify(canteenRepository, times(1)).findAll(pageable);
    }

    @Test
    void testSaveCanteen() {
        Canteen canteen = new Canteen();

        canteenService.saveCanteen(canteen);

        verify(canteenRepository, times(1)).save(canteen);
    }

    @Test
    void testSearchCanteens() {
        String keyword = "test";
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);
        Page<Canteen> expectedPage = new PageImpl<>(Collections.emptyList());

        when(canteenRepository.findByCanteenNameContainingIgnoreCaseOrLocationContainingIgnoreCaseOrCanteenPhoneContainingIgnoreCase(
                keyword, keyword, keyword, pageable)).thenReturn(expectedPage);

        Page<Canteen> result = canteenService.searchCanteens(keyword, page, size);

        assertEquals(expectedPage, result);
        verify(canteenRepository, times(1)).findByCanteenNameContainingIgnoreCaseOrLocationContainingIgnoreCaseOrCanteenPhoneContainingIgnoreCase(
                keyword, keyword, keyword, pageable);
    }

    @Test
    void testGetCanteenById() {
        Integer canteenId = 1;
        Canteen expectedCanteen = new Canteen();

        when(canteenRepository.findById(canteenId)).thenReturn(Optional.of(expectedCanteen));

        Canteen result = canteenService.getCanteenById(canteenId);

        assertEquals(expectedCanteen, result);
        verify(canteenRepository, times(1)).findById(canteenId);
    }

    @Test
    void testUpdateCanteen() {
        Canteen canteen = new Canteen();

        canteenService.updateCanteen(canteen);

        verify(canteenRepository, times(1)).save(canteen);
    }

    @Test
    void testCountCanteens() {
        long expectedCount = 10L;

        when(canteenRepository.count()).thenReturn(expectedCount);

        int result = canteenService.countCanteens();

        assertEquals((int) expectedCount, result);
        verify(canteenRepository, times(1)).count();
    }


    @Test
    void testLoadCanteenId() {
        Integer canteenId = 1;
        Canteen expectedCanteen = new Canteen();

        when(canteenRepository.findById(canteenId)).thenReturn(Optional.of(expectedCanteen));

        Canteen result = canteenService.loadCanteenId(canteenId);

        assertEquals(expectedCanteen, result);
        verify(canteenRepository, times(1)).findById(canteenId);
    }

    @Test
    void testIsPhoneExist() {
        String phone = "123456789";
        Canteen canteen = new Canteen();

        when(canteenRepository.findByCanteenPhone(phone)).thenReturn(canteen);

        boolean result = canteenService.isPhoneExist(phone);

        assertTrue(result);
        verify(canteenRepository, times(1)).findByCanteenPhone(phone);
    }

    @Test
    void testIsCanteenNameExist() {
        String canteenName = "Test Canteen";
        Canteen canteen = new Canteen();

        when(canteenRepository.findByCanteenName(canteenName)).thenReturn(canteen);

        boolean result = canteenService.isCanteenNameExist(canteenName);

        assertTrue(result);
        verify(canteenRepository, times(1)).findByCanteenName(canteenName);
    }
}
