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

    // Trường hợp bình thường: Kiểm thử việc lấy tất cả các canteen
    @Test
    void testGetAllCanteens_Normal() {
        // Tạo các đối tượng Canteen sử dụng builder
        Canteen canteen1 = Canteen.builder()
                .canteenId(1)
                .canteenName("Canteen1")
                .isActive(true)
                .build();

        Canteen canteen2 = Canteen.builder()
                .canteenId(2)
                .canteenName("Canteen2")
                .isActive(true)
                .build();

        // Tạo danh sách các đối tượng Canteen để giả lập kết quả trả về từ repository
        List<Canteen> expectedCanteens = Arrays.asList(canteen1, canteen2);

        // Giả lập phương thức findAll() của repository để trả về danh sách Canteen
        when(canteenRepository.findAll()).thenReturn(expectedCanteens);

        // Gọi phương thức service
        List<Canteen> result = canteenService.getAllCanteens();

        // Kiểm tra xem danh sách trả về có khớp với danh sách mong đợi hay không
        assertEquals(expectedCanteens, result);

        // Xác minh rằng phương thức findAll() của repository đã được gọi chính xác một lần
        verify(canteenRepository, times(1)).findAll();
    }

    // Trường hợp bất thường: Kiểm thử cách xử lý khi repository ném ra ngoại lệ
    @Test
    void testGetAllCanteens_Abnormal() {
        // Giả lập repository ném ra một RuntimeException khi gọi findAll()
        when(canteenRepository.findAll()).thenThrow(new RuntimeException("Database Error"));

        // Gọi phương thức service và kiểm tra xem có ngoại lệ nào bị ném ra hay không
        Exception exception = assertThrows(RuntimeException.class, () -> {
            canteenService.getAllCanteens();
        });

        // Kiểm tra xem thông điệp ngoại lệ có khớp với thông điệp mong đợi hay không
        assertEquals("Database Error", exception.getMessage());

        // Xác minh rằng phương thức findAll() của repository đã được gọi chính xác một lần
        verify(canteenRepository, times(1)).findAll();
    }

    // Trường hợp ranh giới: Kiểm thử việc xử lý khi repository trả về danh sách rỗng
    @Test
    void testGetAllCanteens_Boundary() {
        // Tạo một danh sách rỗng để giả lập tình huống ranh giới
        List<Canteen> expectedCanteens = Collections.emptyList();

        // Giả lập phương thức findAll() của repository để trả về danh sách rỗng
        when(canteenRepository.findAll()).thenReturn(expectedCanteens);

        // Gọi phương thức service
        List<Canteen> result = canteenService.getAllCanteens();

        // Kiểm tra xem danh sách trả về có rỗng hay không
        assertTrue(result.isEmpty());

        // Xác minh rằng phương thức findAll() của repository đã được gọi chính xác một lần
        verify(canteenRepository, times(1)).findAll();
    }

    // Trường hợp bình thường: Kiểm thử việc phân trang khi lấy tất cả các canteen
    @Test
    void testGetAllCanteensPage_Normal() {
        // Tạo các đối tượng Canteen sử dụng builder
        Canteen canteen1 = Canteen.builder()
                .canteenId(1)
                .canteenName("Canteen1")
                .isActive(true)
                .build();

        Canteen canteen2 = Canteen.builder()
                .canteenId(2)
                .canteenName("Canteen2")
                .isActive(true)
                .build();

        Canteen canteen3 = Canteen.builder()
                .canteenId(3)
                .canteenName("Canteen3")
                .isActive(false)
                .build();

        // Tạo Pageable và một Page giả lập chứa danh sách các Canteen
        Pageable pageable = PageRequest.of(0, 10);
        Page<Canteen> expectedPage = new PageImpl<>(Arrays.asList(canteen1, canteen2, canteen3));

        // Giả lập phương thức findAll(pageable) của repository để trả về trang giả lập
        when(canteenRepository.findAll(pageable)).thenReturn(expectedPage);

        // Gọi phương thức service
        Page<Canteen> result = canteenService.getAllCanteensPage(0, 10);

        // Kiểm tra xem trang trả về có khớp với trang mong đợi hay không
        assertEquals(expectedPage, result);

        // Xác minh rằng phương thức findAll(pageable) của repository đã được gọi chính xác một lần
        verify(canteenRepository, times(1)).findAll(pageable);
    }

    // Trường hợp bất thường: Kiểm thử cách xử lý khi repository ném ra ngoại lệ trong khi phân trang
    @Test
    void testGetAllCanteensPage_Abnormal() {
        // Tạo ra một Pageable giả lập
        Pageable pageable = PageRequest.of(0, 10);

        // Giả lập repository ném ra một RuntimeException khi gọi findAll(pageable)
        when(canteenRepository.findAll(pageable)).thenThrow(new RuntimeException("Database Error"));

        // Gọi phương thức service và kiểm tra xem có ngoại lệ nào bị ném ra hay không
        Exception exception = assertThrows(RuntimeException.class, () -> {
            canteenService.getAllCanteensPage(0, 10);
        });

        // Kiểm tra xem thông điệp ngoại lệ có khớp với thông điệp mong đợi hay không
        assertEquals("Database Error", exception.getMessage());

        // Xác minh rằng phương thức findAll(pageable) của repository đã được gọi chính xác một lần
        verify(canteenRepository, times(1)).findAll(pageable);
    }

    // Trường hợp ranh giới: Kiểm thử việc phân trang khi không có canteen nào
    @Test
    void testGetAllCanteensPage_Boundary() {
        // Tạo một Pageable và một Page rỗng giả lập
        Pageable pageable = PageRequest.of(0, 10);
        Page<Canteen> expectedPage = new PageImpl<>(Collections.emptyList());

        // Giả lập phương thức findAll(pageable) của repository để trả về trang rỗng
        when(canteenRepository.findAll(pageable)).thenReturn(expectedPage);

        // Gọi phương thức service
        Page<Canteen> result = canteenService.getAllCanteensPage(0, 10);

        // Kiểm tra xem trang trả về có rỗng hay không
        assertTrue(result.isEmpty());

        // Xác minh rằng phương thức findAll(pageable) của repository đã được gọi chính xác một lần
        verify(canteenRepository, times(1)).findAll(pageable);
    }

    // Trường hợp bình thường: Kiểm thử việc lưu một canteen mới
    @Test
    void testSaveCanteen_Normal() {
        // Tạo một đối tượng Canteen mới sử dụng builder
        Canteen canteen = Canteen.builder()
                .canteenId(1)
                .canteenName("Canteen1")
                .isActive(true)
                .build();

        // Gọi phương thức saveCanteen của service
        canteenService.saveCanteen(canteen);

        // Xác minh rằng phương thức save(canteen) của repository đã được gọi chính xác một lần
        verify(canteenRepository, times(1)).save(canteen);
    }

    // Trường hợp bất thường: Kiểm thử cách xử lý khi repository ném ra ngoại lệ khi lưu canteen
    @Test
    void testSaveCanteen_Abnormal() {
        // Tạo một đối tượng Canteen mới sử dụng builder
        Canteen canteen = Canteen.builder()
                .canteenId(1)
                .canteenName("Canteen1")
                .isActive(true)
                .build();

        // Giả lập repository ném ra một RuntimeException khi gọi save(canteen)
        doThrow(new RuntimeException("Database Error")).when(canteenRepository).save(canteen);

        // Gọi phương thức service và kiểm tra xem có ngoại lệ nào bị ném ra hay không
        Exception exception = assertThrows(RuntimeException.class, () -> {
            canteenService.saveCanteen(canteen);
        });

        // Kiểm tra xem thông điệp ngoại lệ có khớp với thông điệp mong đợi hay không
        assertEquals("Database Error", exception.getMessage());

        // Xác minh rằng phương thức save(canteen) của repository đã được gọi chính xác một lần
        verify(canteenRepository, times(1)).save(canteen);
    }

    // Trường hợp ranh giới: Kiểm thử việc lưu khi đối tượng canteen là null
    @Test
    void testSaveCanteen_Boundary() {
        // Đặt canteen bằng null để kiểm thử trường hợp ranh giới
        Canteen canteen = null;

        // Kiểm tra xem NullPointerException có bị ném ra hay không khi gọi phương thức saveCanteen với giá trị null
        assertThrows(NullPointerException.class, () -> {
            canteenService.saveCanteen(canteen);
        });

        // Xác minh rằng phương thức save(canteen) của repository không được gọi
        verify(canteenRepository, times(0)).save(canteen);
    }

    // Trường hợp bình thường: Kiểm thử việc tìm kiếm canteen với từ khóa
    @Test
    void testSearchCanteens_Normal() {
        // Tạo một Pageable và một Page giả lập chứa danh sách các canteen
        Pageable pageable = PageRequest.of(0, 10);
        Canteen canteen1 = Canteen.builder().canteenId(1).canteenName("Canteen1").isActive(true).build();
        Canteen canteen2 = Canteen.builder().canteenId(2).canteenName("Canteen2").isActive(true).build();
        Page<Canteen> expectedPage = new PageImpl<>(Arrays.asList(canteen1, canteen2));

        // Giả lập phương thức tìm kiếm của repository để trả về trang giả lập
        when(canteenRepository.findByCanteenNameContainingIgnoreCaseOrLocationContainingIgnoreCaseOrCanteenPhoneContainingIgnoreCase(
                "keyword", "keyword", "keyword", pageable)).thenReturn(expectedPage);

        // Gọi phương thức service
        Page<Canteen> result = canteenService.searchCanteens("keyword", 0, 10);

        // Kiểm tra xem trang trả về có khớp với trang mong đợi hay không
        assertEquals(expectedPage, result);

        // Xác minh rằng phương thức tìm kiếm của repository đã được gọi chính xác một lần
        verify(canteenRepository, times(1)).findByCanteenNameContainingIgnoreCaseOrLocationContainingIgnoreCaseOrCanteenPhoneContainingIgnoreCase(
                "keyword", "keyword", "keyword", pageable);
    }

    // Trường hợp bất thường: Kiểm thử cách xử lý khi repository ném ra ngoại lệ khi tìm kiếm
    @Test
    void testSearchCanteens_Abnormal() {
        // Tạo một Pageable giả lập
        Pageable pageable = PageRequest.of(0, 10);

        // Giả lập repository ném ra một RuntimeException khi gọi phương thức tìm kiếm
        when(canteenRepository.findByCanteenNameContainingIgnoreCaseOrLocationContainingIgnoreCaseOrCanteenPhoneContainingIgnoreCase(
                "keyword", "keyword", "keyword", pageable)).thenThrow(new RuntimeException("Database Error"));

        // Gọi phương thức service và kiểm tra xem có ngoại lệ nào bị ném ra hay không
        Exception exception = assertThrows(RuntimeException.class, () -> {
            canteenService.searchCanteens("keyword", 0, 10);
        });

        // Kiểm tra xem thông điệp ngoại lệ có khớp với thông điệp mong đợi hay không
        assertEquals("Database Error", exception.getMessage());

        // Xác minh rằng phương thức tìm kiếm của repository đã được gọi chính xác một lần
        verify(canteenRepository, times(1)).findByCanteenNameContainingIgnoreCaseOrLocationContainingIgnoreCaseOrCanteenPhoneContainingIgnoreCase(
                "keyword", "keyword", "keyword", pageable);
    }

    // Trường hợp ranh giới: Kiểm thử việc tìm kiếm khi không có canteen nào khớp với từ khóa
    @Test
    void testSearchCanteens_Boundary() {
        // Tạo một Pageable và một Page rỗng giả lập
        Pageable pageable = PageRequest.of(0, 10);
        Page<Canteen> expectedPage = new PageImpl<>(Collections.emptyList());

        // Giả lập phương thức tìm kiếm của repository để trả về trang rỗng
        when(canteenRepository.findByCanteenNameContainingIgnoreCaseOrLocationContainingIgnoreCaseOrCanteenPhoneContainingIgnoreCase(
                "keyword", "keyword", "keyword", pageable)).thenReturn(expectedPage);

        // Gọi phương thức service
        Page<Canteen> result = canteenService.searchCanteens("keyword", 0, 10);

        // Kiểm tra xem trang trả về có rỗng hay không
        assertTrue(result.isEmpty());

        // Xác minh rằng phương thức tìm kiếm của repository đã được gọi chính xác một lần
        verify(canteenRepository, times(1)).findByCanteenNameContainingIgnoreCaseOrLocationContainingIgnoreCaseOrCanteenPhoneContainingIgnoreCase(
                "keyword", "keyword", "keyword", pageable);
    }

    // Trường hợp bình thường: Kiểm thử việc lấy canteen theo ID
    @Test
    void testGetCanteenById_Normal() {
        // Tạo một đối tượng Canteen giả lập
        Canteen expectedCanteen = Canteen.builder()
                .canteenId(1)
                .canteenName("Canteen1")
                .isActive(true)
                .build();

        // Giả lập phương thức findById của repository để trả về đối tượng canteen giả lập
        when(canteenRepository.findById(1)).thenReturn(Optional.of(expectedCanteen));

        // Gọi phương thức service
        Canteen result = canteenService.getCanteenById(1);

        // Kiểm tra xem canteen trả về có khớp với canteen mong đợi hay không
        assertEquals(expectedCanteen, result);

        // Xác minh rằng phương thức findById của repository đã được gọi chính xác một lần
        verify(canteenRepository, times(1)).findById(1);
    }

    // Trường hợp bất thường: Kiểm thử cách xử lý khi repository ném ra ngoại lệ khi lấy canteen theo ID
    @Test
    void testGetCanteenById_Abnormal() {
        // Giả lập repository ném ra một RuntimeException khi gọi phương thức findById
        when(canteenRepository.findById(1)).thenThrow(new RuntimeException("Database Error"));

        // Gọi phương thức service và kiểm tra xem có ngoại lệ nào bị ném ra hay không
        Exception exception = assertThrows(RuntimeException.class, () -> {
            canteenService.getCanteenById(1);
        });

        // Kiểm tra xem thông điệp ngoại lệ có khớp với thông điệp mong đợi hay không
        assertEquals("Database Error", exception.getMessage());

        // Xác minh rằng phương thức findById của repository đã được gọi chính xác một lần
        verify(canteenRepository, times(1)).findById(1);
    }

    // Trường hợp ranh giới: Kiểm thử việc lấy canteen khi không có canteen nào khớp với ID
    @Test
    void testGetCanteenById_Boundary() {
        // Giả lập phương thức findById của repository để trả về Optional rỗng
        when(canteenRepository.findById(1)).thenReturn(Optional.empty());

        // Gọi phương thức service
        Canteen result = canteenService.getCanteenById(1);

        // Kiểm tra xem kết quả trả về có là null hay không
        assertNull(result);

        // Xác minh rằng phương thức findById của repository đã được gọi chính xác một lần
        verify(canteenRepository, times(1)).findById(1);
    }

    // Trường hợp bình thường: Kiểm thử việc cập nhật một canteen
    @Test
    void testUpdateCanteen_Normal() {
        // Tạo một đối tượng Canteen giả lập
        Canteen canteen = Canteen.builder()
                .canteenId(1)
                .canteenName("Canteen1")
                .isActive(true)
                .build();

        // Gọi phương thức updateCanteen của service
        canteenService.updateCanteen(canteen);

        // Xác minh rằng phương thức save(canteen) của repository đã được gọi chính xác một lần
        verify(canteenRepository, times(1)).save(canteen);
    }

    // Trường hợp bất thường: Kiểm thử cách xử lý khi repository ném ra ngoại lệ khi cập nhật canteen
    @Test
    void testUpdateCanteen_Abnormal() {
        // Tạo một đối tượng Canteen giả lập
        Canteen canteen = Canteen.builder()
                .canteenId(1)
                .canteenName("Canteen1")
                .isActive(true)
                .build();

        // Giả lập repository ném ra một RuntimeException khi gọi save(canteen)
        doThrow(new RuntimeException("Database Error")).when(canteenRepository).save(canteen);

        // Gọi phương thức service và kiểm tra xem có ngoại lệ nào bị ném ra hay không
        Exception exception = assertThrows(RuntimeException.class, () -> {
            canteenService.updateCanteen(canteen);
        });

        // Kiểm tra xem thông điệp ngoại lệ có khớp với thông điệp mong đợi hay không
        assertEquals("Database Error", exception.getMessage());

        // Xác minh rằng phương thức save(canteen) của repository đã được gọi chính xác một lần
        verify(canteenRepository, times(1)).save(canteen);
    }

    // Trường hợp ranh giới: Kiểm thử việc cập nhật khi đối tượng canteen là null
    @Test
    void testUpdateCanteen_Boundary() {
        // Đặt canteen bằng null để kiểm thử trường hợp ranh giới
        Canteen canteen = null;

        // Kiểm tra xem NullPointerException có bị ném ra hay không khi gọi phương thức updateCanteen với giá trị null
        assertThrows(NullPointerException.class, () -> {
            canteenService.updateCanteen(canteen);
        });

        // Xác minh rằng phương thức save(canteen) của repository không được gọi
        verify(canteenRepository, times(0)).save(canteen);
    }

    // Trường hợp bình thường: Kiểm thử việc đếm số lượng canteen
    @Test
    void testCountCanteens_Normal() {
        // Giả lập phương thức count của repository để trả về số lượng canteen
        when(canteenRepository.count()).thenReturn(10L);

        // Gọi phương thức service
        Integer result = canteenService.countCanteens();

        // Kiểm tra xem số lượng trả về có khớp với số lượng mong đợi hay không
        assertEquals(10, result);

        // Xác minh rằng phương thức count của repository đã được gọi chính xác một lần
        verify(canteenRepository, times(1)).count();
    }

    // Trường hợp bất thường: Kiểm thử cách xử lý khi repository ném ra ngoại lệ khi đếm canteen
    @Test
    void testCountCanteens_Abnormal() {
        // Giả lập repository ném ra một RuntimeException khi gọi phương thức count
        when(canteenRepository.count()).thenThrow(new RuntimeException("Database Error"));

        // Gọi phương thức service và kiểm tra xem có ngoại lệ nào bị ném ra hay không
        Exception exception = assertThrows(RuntimeException.class, () -> {
            canteenService.countCanteens();
        });

        // Kiểm tra xem thông điệp ngoại lệ có khớp với thông điệp mong đợi hay không
        assertEquals("Database Error", exception.getMessage());

        // Xác minh rằng phương thức count của repository đã được gọi chính xác một lần
        verify(canteenRepository, times(1)).count();
    }

    // Trường hợp ranh giới: Kiểm thử việc đếm khi không có canteen nào
    @Test
    void testCountCanteens_Boundary() {
        // Giả lập phương thức count của repository để trả về số lượng canteen là 0
        when(canteenRepository.count()).thenReturn(0L);

        // Gọi phương thức service
        Integer result = canteenService.countCanteens();

        // Kiểm tra xem số lượng trả về có khớp với số lượng mong đợi là 0 hay không
        assertEquals(0, result);

        // Xác minh rằng phương thức count của repository đã được gọi chính xác một lần
        verify(canteenRepository, times(1)).count();
    }

    // Trường hợp bình thường: Kiểm thử việc lấy canteen theo ID (phương thức loadCanteenId)
    @Test
    void testLoadCanteenId_Normal() {
        // Tạo một đối tượng Canteen giả lập
        Canteen expectedCanteen = Canteen.builder()
                .canteenId(1)
                .canteenName("Canteen1")
                .isActive(true)
                .build();

        // Giả lập phương thức findById của repository để trả về đối tượng canteen giả lập
        when(canteenRepository.findById(1)).thenReturn(Optional.of(expectedCanteen));

        // Gọi phương thức service
        Canteen result = canteenService.loadCanteenId(1);

        // Kiểm tra xem canteen trả về có khớp với canteen mong đợi hay không
        assertEquals(expectedCanteen, result);

        // Xác minh rằng phương thức findById của repository đã được gọi chính xác một lần
        verify(canteenRepository, times(1)).findById(1);
    }

    // Trường hợp bất thường: Kiểm thử cách xử lý khi repository ném ra ngoại lệ khi lấy canteen theo ID
    @Test
    void testLoadCanteenId_Abnormal() {
        // Giả lập repository ném ra một RuntimeException khi gọi phương thức findById
        when(canteenRepository.findById(1)).thenThrow(new RuntimeException("Database Error"));

        // Gọi phương thức service và kiểm tra xem có ngoại lệ nào bị ném ra hay không
        Exception exception = assertThrows(RuntimeException.class, () -> {
            canteenService.loadCanteenId(1);
        });

        // Kiểm tra xem thông điệp ngoại lệ có khớp với thông điệp mong đợi hay không
        assertEquals("Database Error", exception.getMessage());

        // Xác minh rằng phương thức findById của repository đã được gọi chính xác một lần
        verify(canteenRepository, times(1)).findById(1);
    }

    // Trường hợp ranh giới: Kiểm thử việc lấy canteen khi không có canteen nào khớp với ID
    @Test
    void testLoadCanteenId_Boundary() {
        // Giả lập phương thức findById của repository để trả về Optional rỗng
        when(canteenRepository.findById(1)).thenReturn(Optional.empty());

        // Gọi phương thức service
        Canteen result = canteenService.loadCanteenId(1);

        // Kiểm tra xem kết quả trả về có là null hay không
        assertNull(result);

        // Xác minh rằng phương thức findById của repository đã được gọi chính xác một lần
        verify(canteenRepository, times(1)).findById(1);
    }

    // Trường hợp bình thường: Kiểm thử việc kiểm tra sự tồn tại của số điện thoại canteen
    @Test
    void testIsPhoneExist_Normal() {
        // Giả lập phương thức findByCanteenPhone của repository để trả về một đối tượng Canteen
        when(canteenRepository.findByCanteenPhone("123456789")).thenReturn(Canteen.builder().canteenId(1).canteenName("Canteen1").isActive(true).build());

        // Gọi phương thức service
        boolean result = canteenService.isPhoneExist("123456789");

        // Kiểm tra xem kết quả trả về có đúng là số điện thoại tồn tại hay không
        assertTrue(result);

        // Xác minh rằng phương thức findByCanteenPhone của repository đã được gọi chính xác một lần
        verify(canteenRepository, times(1)).findByCanteenPhone("123456789");
    }

    // Trường hợp bất thường: Kiểm thử cách xử lý khi repository ném ra ngoại lệ khi kiểm tra số điện thoại
    @Test
    void testIsPhoneExist_Abnormal() {
        // Giả lập repository ném ra một RuntimeException khi gọi phương thức findByCanteenPhone
        when(canteenRepository.findByCanteenPhone("123456789")).thenThrow(new RuntimeException("Database Error"));

        // Gọi phương thức service và kiểm tra xem có ngoại lệ nào bị ném ra hay không
        Exception exception = assertThrows(RuntimeException.class, () -> {
            canteenService.isPhoneExist("123456789");
        });

        // Kiểm tra xem thông điệp ngoại lệ có khớp với thông điệp mong đợi hay không
        assertEquals("Database Error", exception.getMessage());

        // Xác minh rằng phương thức findByCanteenPhone của repository đã được gọi chính xác một lần
        verify(canteenRepository, times(1)).findByCanteenPhone("123456789");
    }

    // Trường hợp ranh giới: Kiểm thử việc kiểm tra số điện thoại khi không có canteen nào khớp với số điện thoại
    @Test
    void testIsPhoneExist_Boundary() {
        // Giả lập phương thức findByCanteenPhone của repository để trả về null
        when(canteenRepository.findByCanteenPhone("123456789")).thenReturn(null);

        // Gọi phương thức service
        boolean result = canteenService.isPhoneExist("123456789");

        // Kiểm tra xem kết quả trả về có đúng là số điện thoại không tồn tại hay không
        assertFalse(result);

        // Xác minh rằng phương thức findByCanteenPhone của repository đã được gọi chính xác một lần
        verify(canteenRepository, times(1)).findByCanteenPhone("123456789");
    }

    // Trường hợp bình thường: Kiểm thử việc kiểm tra sự tồn tại của tên canteen
    @Test
    void testIsCanteenNameExist_Normal() {
        // Giả lập phương thức findByCanteenName của repository để trả về một đối tượng Canteen
        when(canteenRepository.findByCanteenName("Canteen1")).thenReturn(Canteen.builder().canteenId(1).canteenName("Canteen1").isActive(true).build());

        // Gọi phương thức service
        boolean result = canteenService.isCanteenNameExist("Canteen1");

        // Kiểm tra xem kết quả trả về có đúng là tên canteen tồn tại hay không
        assertTrue(result);

        // Xác minh rằng phương thức findByCanteenName của repository đã được gọi chính xác một lần
        verify(canteenRepository, times(1)).findByCanteenName("Canteen1");
    }

    // Trường hợp bất thường: Kiểm thử cách xử lý khi repository ném ra ngoại lệ khi kiểm tra tên canteen
    @Test
    void testIsCanteenNameExist_Abnormal() {
        // Giả lập repository ném ra một RuntimeException khi gọi phương thức findByCanteenName
        when(canteenRepository.findByCanteenName("Canteen1")).thenThrow(new RuntimeException("Database Error"));

        // Gọi phương thức service và kiểm tra xem có ngoại lệ nào bị ném ra hay không
        Exception exception = assertThrows(RuntimeException.class, () -> {
            canteenService.isCanteenNameExist("Canteen1");
        });

        // Kiểm tra xem thông điệp ngoại lệ có khớp với thông điệp mong đợi hay không
        assertEquals("Database Error", exception.getMessage());

        // Xác minh rằng phương thức findByCanteenName của repository đã được gọi chính xác một lần
        verify(canteenRepository, times(1)).findByCanteenName("Canteen1");
    }

    // Trường hợp ranh giới: Kiểm thử việc kiểm tra tên canteen khi không có canteen nào khớp với tên
    @Test
    void testIsCanteenNameExist_Boundary() {
        // Giả lập phương thức findByCanteenName của repository để trả về null
        when(canteenRepository.findByCanteenName("Canteen1")).thenReturn(null);

        // Gọi phương thức service
        boolean result = canteenService.isCanteenNameExist("Canteen1");

        // Kiểm tra xem kết quả trả về có đúng là tên canteen không tồn tại hay không
        assertFalse(result);

        // Xác minh rằng phương thức findByCanteenName của repository đã được gọi chính xác một lần
        verify(canteenRepository, times(1)).findByCanteenName("Canteen1");
    }
}
