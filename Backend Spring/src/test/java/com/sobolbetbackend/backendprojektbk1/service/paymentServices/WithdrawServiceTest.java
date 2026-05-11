package com.sobolbetbackend.backendprojektbk1.service.paymentServices;

import com.sobolbetbackend.backendprojektbk1.dto.payment.Withdraw.WithdrawRequestDTO;
import com.sobolbetbackend.backendprojektbk1.entity.Player;
import com.sobolbetbackend.backendprojektbk1.entity.events.payment.Withdraw;
import com.sobolbetbackend.backendprojektbk1.entity.other.PaymentMethod;
import com.sobolbetbackend.backendprojektbk1.exception.LowBalanceException;
import com.sobolbetbackend.backendprojektbk1.repository.payment.PaymentMethodRepo;
import com.sobolbetbackend.backendprojektbk1.repository.payment.WithdrawRepo;
import com.sobolbetbackend.backendprojektbk1.repository.playerRegistrationRepos.PlayerRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WithdrawServiceTest {

    @Mock
    private PlayerRepo playerRepo;

    @Mock
    private WithdrawRepo withdrawRepo;

    @Mock
    private PaymentMethodRepo paymentMethodRepo;

    @Mock
    private WithdrawRequestDTO withdrawRequestDTO;

    @InjectMocks
    private WithdrawService withdrawService;

    private Player player;
    private PaymentMethod paymentMethod;

    @BeforeEach
    void setUp() {
        player = new Player();
        player.setId(1L);
        player.setBalance(100.0);

        paymentMethod = new PaymentMethod();
        paymentMethod.setName_en("Visa");
    }

    @Test
    void declareWithdraw_SufficientBalance_ShouldSaveWithdraw() throws LowBalanceException {
        when(withdrawRequestDTO.getUserId()).thenReturn(10L);
        when(withdrawRequestDTO.getAmount()).thenReturn(50.0);
        when(withdrawRequestDTO.getPaymentMethod()).thenReturn("Visa");
        when(withdrawRequestDTO.getAccountNumber()).thenReturn(123456789L);
        when(playerRepo.findByUserId(10L)).thenReturn(player);
        when(paymentMethodRepo.findById("Visa")).thenReturn(Optional.of(paymentMethod));

        withdrawService.declareWithdraw(withdrawRequestDTO);

        ArgumentCaptor<Withdraw> withdrawCaptor = ArgumentCaptor.forClass(Withdraw.class);
        verify(withdrawRepo).save(withdrawCaptor.capture());

        Withdraw savedWithdraw = withdrawCaptor.getValue();
        assertSame(player, savedWithdraw.getPlayer());
        assertEquals(50.0, savedWithdraw.getWithdrawalAmount());
        assertSame(paymentMethod, savedWithdraw.getPaymentMethod());
        assertEquals(123456789L, savedWithdraw.getAccountNumber());
        assertNull(savedWithdraw.getPaymentSuccessful());
        assertNotNull(savedWithdraw.getDateOfWithdraw());
    }

    @Test
    void declareWithdraw_InsufficientBalance_ShouldThrowLowBalanceException() {
        when(withdrawRequestDTO.getUserId()).thenReturn(10L);
        when(withdrawRequestDTO.getAmount()).thenReturn(150.0);
        when(playerRepo.findByUserId(10L)).thenReturn(player);

        LowBalanceException exception = assertThrows(LowBalanceException.class,
                () -> withdrawService.declareWithdraw(withdrawRequestDTO));

        assertEquals("Insufficient funds in the account", exception.getMessage());
        verify(paymentMethodRepo, never()).findById(any());
        verify(withdrawRepo, never()).save(any());
    }

    @Test
    void declareWithdraw_MissingPaymentMethod_ShouldThrowNoSuchElementException() {
        when(withdrawRequestDTO.getUserId()).thenReturn(10L);
        when(withdrawRequestDTO.getAmount()).thenReturn(50.0);
        when(withdrawRequestDTO.getPaymentMethod()).thenReturn("Unknown");
        when(playerRepo.findByUserId(10L)).thenReturn(player);
        when(paymentMethodRepo.findById("Unknown")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> withdrawService.declareWithdraw(withdrawRequestDTO));

        verify(withdrawRepo, never()).save(any());
    }

    @Test
    void getWithdrawStory_ExistingUser_ShouldReturnWithdraws() {
        Withdraw firstWithdraw = new Withdraw();
        Withdraw secondWithdraw = new Withdraw();
        player.setWithdraws(Arrays.asList(firstWithdraw, secondWithdraw));
        when(playerRepo.findByUserId(10L)).thenReturn(player);

        List<Withdraw> result = withdrawService.getWithdrawStory(10L);

        assertEquals(2, result.size());
        assertTrue(result.contains(firstWithdraw));
        assertTrue(result.contains(secondWithdraw));
    }
}
