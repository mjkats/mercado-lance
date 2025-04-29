package br.com.katsilis.mercadolance.service.impl;

import br.com.katsilis.mercadolance.dto.creation.CreatePaymentInfoDto;
import br.com.katsilis.mercadolance.dto.response.PaymentInfoResponseDto;
import br.com.katsilis.mercadolance.dto.response.UserResponseDto;
import br.com.katsilis.mercadolance.enums.PaymentMethod;
import br.com.katsilis.mercadolance.exception.DatabaseException;
import br.com.katsilis.mercadolance.exception.notfound.PaymentInfoNotFoundException;
import br.com.katsilis.mercadolance.model.PaymentInfo;
import br.com.katsilis.mercadolance.model.User;
import br.com.katsilis.mercadolance.repository.PaymentInfoRepository;
import br.com.katsilis.mercadolance.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentInfoServiceImplTest {

    @Mock
    private PaymentInfoRepository paymentInfoRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private PaymentInfoServiceImpl paymentInfoService;

    private CreatePaymentInfoDto createPaymentInfoDto;
    private PaymentInfo paymentInfo;
    private User user;
    private PaymentInfoResponseDto paymentInfoResponseDto;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).name("Michel").email("teste@teste.com").build();
        createPaymentInfoDto = CreatePaymentInfoDto.builder().userId(1L).amount(100.0).paymentMethod(PaymentMethod.CREDIT_CARD).amount(100.0).build();
        paymentInfo = new PaymentInfo(1L, user, PaymentMethod.CREDIT_CARD, 100.0, LocalDateTime.now());

        paymentInfoResponseDto = new PaymentInfoResponseDto(
            new UserResponseDto(user.getName(), user.getEmail()),
            PaymentMethod.CREDIT_CARD,
            100.0,
            paymentInfo.getPaymentDate()
        );
    }

    @Test
    void testFindAll() {
        UserResponseDto userResponseDto = new UserResponseDto(user.getName(), user.getEmail());
        when(userService.userToResponseDto(any(User.class))).thenReturn(userResponseDto);

        when(paymentInfoRepository.findAll()).thenReturn(List.of(paymentInfo));

        List<PaymentInfoResponseDto> result = paymentInfoService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(paymentInfoResponseDto, result.getFirst());
    }

    @Test
    void testFindById_Success() {
        UserResponseDto userResponseDto = new UserResponseDto(user.getName(), user.getEmail());
        when(userService.userToResponseDto(any(User.class))).thenReturn(userResponseDto);

        when(paymentInfoRepository.findById(1L)).thenReturn(Optional.of(paymentInfo));

        PaymentInfoResponseDto result = paymentInfoService.findById(1L);

        assertNotNull(result);
        assertEquals(paymentInfoResponseDto, result);
    }


    @Test
    void testFindById_NotFound() {
        when(paymentInfoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PaymentInfoNotFoundException.class, () -> paymentInfoService.findById(1L));
    }

    @Test
    void testCreate() {
        when(userService.findOriginalById(1L)).thenReturn(user);
        when(paymentInfoRepository.save(any(PaymentInfo.class))).thenReturn(paymentInfo);

        paymentInfoService.create(createPaymentInfoDto);

        verify(paymentInfoRepository, times(1)).save(any(PaymentInfo.class));
    }

    @Test
    void testCreate_Exception() {
        when(userService.findOriginalById(1L)).thenThrow(new RuntimeException("User not found"));

        assertThrows(DatabaseException.class, () -> paymentInfoService.create(createPaymentInfoDto));
    }

    @Test
    void testDeleteById() {
        doNothing().when(paymentInfoRepository).deleteById(1L);

        paymentInfoService.deleteById(1L);

        verify(paymentInfoRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteById_Exception() {
        doThrow(new RuntimeException("Delete failed")).when(paymentInfoRepository).deleteById(1L);

        assertThrows(DatabaseException.class, () -> paymentInfoService.deleteById(1L));
    }

    @Test
    void testPaymentInfoToResponseDto() {
        when(userService.userToResponseDto(user)).thenReturn(new UserResponseDto(user.getName(), user.getEmail()));

        PaymentInfoResponseDto result = paymentInfoService.paymentInfoToResponseDto(paymentInfo);

        assertNotNull(result);
        assertEquals(paymentInfoResponseDto, result);
    }
}
