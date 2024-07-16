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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CanteenServiceImplTest {

    @InjectMocks
    CanteenServiceImpl service;

    @Mock
    CanteenRepository repository;

    @Test
    void getAllCanteens_nomal() {
        Canteen canteen1 = Canteen.builder().canteenId(1).canteenName("Canteen1").isActive(true).build();
        Canteen canteen2 = Canteen.builder().canteenId(2).canteenName("Canteen2").isActive(true).build();

        List<Canteen> canteens = Arrays.asList(canteen1, canteen2);

        when(repository.findAll()).thenReturn(canteens);

        //test
        List<Canteen> empList = service.getAllCanteens();

        assertEquals(2, empList.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    void getAllCanteens_binalry() {
        Canteen canteen1 = Canteen.builder().canteenId(1).canteenName("Canteen1").isActive(true).build();
        Canteen canteen2 = Canteen.builder().canteenId(2).canteenName("Canteen2").isActive(true).build();

        List<Canteen> canteens = Arrays.asList(canteen1, canteen2);

        when(repository.findAll()).thenReturn(canteens);

        //test
        List<Canteen> empList = service.getAllCanteens();

        assertEquals(2, empList.size());
        verify(repository, times(1)).findAll();
    }


    @Test
    void searchCanteens() {
        String keyword = "Canteen";
        int page = 0;
        int size = 10;

        Canteen canteen1 = Canteen.builder().canteenId(1).canteenName("Canteen One").location("Location One").canteenPhone("123456789").build();
        Canteen canteen2 = Canteen.builder().canteenId(2).canteenName("Canteen Two").location("Location Two").canteenPhone("987654321").build();
        Canteen canteen3 = Canteen.builder().canteenId(3).canteenName("Canteen Two").location("Location Two").canteenPhone("987654321").build();
        Canteen canteen4 = Canteen.builder().canteenId(4).canteenName("Canteen Two").location("Location Two").canteenPhone("987654321").build();
        Canteen canteen5 = Canteen.builder().canteenId(5).canteenName("Canteen Two").location("Location Two").canteenPhone("987654321").build();
        Canteen canteen6 = Canteen.builder().canteenId(6).canteenName("Canteen Two").location("Location Two").canteenPhone("987654321").build();
        Canteen canteen7 = Canteen.builder().canteenId(7).canteenName("Canteen Two").location("Location Two").canteenPhone("987654321").build();
        Canteen canteen8 = Canteen.builder().canteenId(8).canteenName("Canteen Two").location("Location Two").canteenPhone("987654321").build();


        List<Canteen> canteens = Arrays.asList(canteen1, canteen2,canteen3,canteen4,canteen5,canteen6,canteen7,canteen8);
        Page<Canteen> canteenPage = new PageImpl<>(canteens);

        Pageable pageable = PageRequest.of(page, size);
        when(repository.findByCanteenNameContainingIgnoreCaseOrLocationContainingIgnoreCaseOrCanteenPhoneContainingIgnoreCase(
                keyword, keyword, keyword, pageable)).thenReturn(canteenPage);

        //test
        Page<Canteen> result = service.searchCanteens(keyword, page, size);

        assertEquals(9, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(canteens, result.getContent());
        verify(repository, times(1)).findByCanteenNameContainingIgnoreCaseOrLocationContainingIgnoreCaseOrCanteenPhoneContainingIgnoreCase(
                keyword, keyword, keyword, pageable);
    }

    @Test
    void updateCanteen_HappyCase() {
        Canteen canteen = Canteen.builder().canteenId(1).canteenName("Canteen Updated").build();

        service.updateCanteen(canteen);

        verify(repository, times(1)).save(canteen);
    }

    @Test
    void countCanteens_HappyCase() {
        Long count = 3L;

        when(repository.count()).thenReturn(count);

        Integer result = service.countCanteens();

        assertEquals(3, result);
        verify(repository, times(1)).count();
    }

    @Test
    void loadCanteenId_HappyCase() {
        Integer canteenId = 1;
        Canteen canteen = Canteen.builder().canteenId(canteenId).canteenName("Canteen").build();

        when(repository.findById(canteenId)).thenReturn(Optional.of(canteen));

        Canteen result = service.loadCanteenId(canteenId);

        assertEquals(canteen, result);
        verify(repository, times(1)).findById(canteenId);
    }

    @Test
    void loadCanteenId_NotFound() {
        Integer canteenId = 1;

        when(repository.findById(canteenId)).thenReturn(Optional.empty());

        Canteen result = service.loadCanteenId(canteenId);

        assertNull(result);
        verify(repository, times(1)).findById(canteenId);
    }

    @Test
    void isPhoneExist_HappyCase() {
        String phone = "123456789";
        Canteen canteen = Canteen.builder().canteenId(1).canteenPhone(phone).build();

        when(repository.findByCanteenPhone(phone)).thenReturn(canteen);

        boolean result = service.isPhoneExist(phone);

        assertTrue(result);
        verify(repository, times(1)).findByCanteenPhone(phone);
    }

    @Test
    void isPhoneExist_NotFound() {
        String phone = "123456789";

        when(repository.findByCanteenPhone(phone)).thenReturn(null);

        boolean result = service.isPhoneExist(phone);

        assertFalse(result);
        verify(repository, times(1)).findByCanteenPhone(phone);
    }

    @Test
    void isCanteenNameExist_HappyCase() {
        String canteenName = "Canteen Name";
        Canteen canteen = Canteen.builder().canteenId(1).canteenName(canteenName).build();

        when(repository.findByCanteenName(canteenName)).thenReturn(canteen);

        boolean result = service.isCanteenNameExist(canteenName);

        assertTrue(result);
        verify(repository, times(1)).findByCanteenName(canteenName);
    }

    @Test
    void isCanteenNameExist_NotFound() {
        String canteenName = "Canteen Name";

        when(repository.findByCanteenName(canteenName)).thenReturn(null);

        boolean result = service.isCanteenNameExist(canteenName);

        assertFalse(result);
        verify(repository, times(1)).findByCanteenName(canteenName);
    }

}