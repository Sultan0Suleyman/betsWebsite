package com.sobolbetbackend.backendprojektbk1.service.paymentServices;

import com.sobolbetbackend.backendprojektbk1.dto.payment.Refill.RefillRequestDTO;
import com.sobolbetbackend.backendprojektbk1.entity.Player;
import com.sobolbetbackend.backendprojektbk1.entity.events.payment.Refill;
import com.sobolbetbackend.backendprojektbk1.entity.other.PaymentMethod;
import com.sobolbetbackend.backendprojektbk1.repository.payment.PaymentMethodRepo;
import com.sobolbetbackend.backendprojektbk1.repository.payment.RefillRepo;
import com.sobolbetbackend.backendprojektbk1.repository.playerRegistrationRepos.PlayerRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefillServiceTest {

    @Mock
    private PlayerRepo playerRepo;

    @Mock
    private RefillRepo refillRepo;

    @Mock
    private PaymentMethodRepo paymentMethodRepo;

    @InjectMocks
    private RefillService refillService;

    private Player player;
    private PaymentMethod paymentMethod;
    private RefillRequestDTO refillRequestDTO;

    @BeforeEach
    void setUp() {
        player = new Player();
        player.setId(1L);

        paymentMethod = new PaymentMethod();
        paymentMethod.setName_en("Visa");

        refillRequestDTO = new RefillRequestDTO();
    }

    @Test
    void declareRefill_ValidRequest_ShouldSaveRefill() {
        ReflectionTestUtils.setField(refillRequestDTO, "userId", 10L);
        ReflectionTestUtils.setField(refillRequestDTO, "amount", 75.0);
        ReflectionTestUtils.setField(refillRequestDTO, "paymentMethod", "Visa");
        ReflectionTestUtils.setField(refillRequestDTO, "isPaymentSuccessful", "true");
        when(playerRepo.findByUserId(10L)).thenReturn(player);
        when(paymentMethodRepo.findById("Visa")).thenReturn(Optional.of(paymentMethod));

        refillService.declareRefill(refillRequestDTO);

        ArgumentCaptor<Refill> refillCaptor = ArgumentCaptor.forClass(Refill.class);
        verify(refillRepo).save(refillCaptor.capture());

        Refill savedRefill = refillCaptor.getValue();
        assertSame(player, savedRefill.getPlayer());
        assertEquals(75.0, savedRefill.getReplenishmentAmount());
        assertSame(paymentMethod, savedRefill.getPaymentMethod());
        assertTrue(savedRefill.getPaymentSuccessful());
        assertNotNull(savedRefill.getDateOfRefill());
    }

    @Test
    void declareRefill_FailedPaymentString_ShouldSaveUnsuccessfulRefill() {
        ReflectionTestUtils.setField(refillRequestDTO, "userId", 10L);
        ReflectionTestUtils.setField(refillRequestDTO, "amount", 75.0);
        ReflectionTestUtils.setField(refillRequestDTO, "paymentMethod", "Visa");
        ReflectionTestUtils.setField(refillRequestDTO, "isPaymentSuccessful", "false");
        when(playerRepo.findByUserId(10L)).thenReturn(player);
        when(paymentMethodRepo.findById("Visa")).thenReturn(Optional.of(paymentMethod));

        refillService.declareRefill(refillRequestDTO);

        ArgumentCaptor<Refill> refillCaptor = ArgumentCaptor.forClass(Refill.class);
        verify(refillRepo).save(refillCaptor.capture());

        assertFalse(refillCaptor.getValue().getPaymentSuccessful());
    }

    @Test
    void declareRefill_MissingPaymentMethod_ShouldThrowNoSuchElementException() {
        ReflectionTestUtils.setField(refillRequestDTO, "userId", 10L);
        ReflectionTestUtils.setField(refillRequestDTO, "paymentMethod", "Unknown");
        when(playerRepo.findByUserId(10L)).thenReturn(player);
        when(paymentMethodRepo.findById("Unknown")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> refillService.declareRefill(refillRequestDTO));

        verify(refillRepo, never()).save(any());
    }

    @Test
    void getRefillStory_ExistingUser_ShouldReturnRefills() {
        Refill firstRefill = new Refill();
        Refill secondRefill = new Refill();
        player.setRefills(Arrays.asList(firstRefill, secondRefill));
        when(playerRepo.findByUserId(10L)).thenReturn(player);

        List<Refill> result = refillService.getRefillStory(10L);

        assertEquals(2, result.size());
        assertTrue(result.contains(firstRefill));
        assertTrue(result.contains(secondRefill));
    }
}
