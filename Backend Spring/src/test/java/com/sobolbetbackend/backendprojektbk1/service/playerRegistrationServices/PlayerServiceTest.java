package com.sobolbetbackend.backendprojektbk1.service.playerRegistrationServices;

import com.sobolbetbackend.backendprojektbk1.entity.Player;
import com.sobolbetbackend.backendprojektbk1.entity.common.UserE;
import com.sobolbetbackend.backendprojektbk1.entity.other.Role;
import com.sobolbetbackend.backendprojektbk1.exception.EmailAlreadyExistsException;
import com.sobolbetbackend.backendprojektbk1.exception.UserAlreadyRegisteredException;
import com.sobolbetbackend.backendprojektbk1.repository.authenticationRepos.UserRepo;
import com.sobolbetbackend.backendprojektbk1.repository.otherRepos.RoleRepo;
import com.sobolbetbackend.backendprojektbk1.repository.playerRegistrationRepos.PlayerRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlayerServiceTest {

    @Mock
    private PlayerRepo playerRepo;

    @Mock
    private UserRepo userRepo;

    @Mock
    private RoleRepo roleRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PlayerService playerService;

    private UserE user;
    private Role playerRole;

    @BeforeEach
    void setUp() {
        user = createUser();

        playerRole = new Role();
        playerRole.setId(1L);
        playerRole.setName("ROLE_PLAYER");
    }

    @Test
    void registration_ValidUser_ShouldEncodePasswordAssignRoleSaveUserAndPlayer()
            throws EmailAlreadyExistsException, UserAlreadyRegisteredException {
        when(userRepo.findByEmail("player@mail.com")).thenReturn(null);
        when(userRepo.findByNumberOfPassport("AA123456")).thenReturn(null);
        when(roleRepo.findByName("ROLE_PLAYER")).thenReturn(playerRole);
        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");
        when(userRepo.save(user)).thenReturn(user);

        playerService.registration(user);

        assertEquals("encodedPassword", user.getPassword());
        assertEquals(List.of(playerRole), user.getRoles());

        ArgumentCaptor<Player> playerCaptor = ArgumentCaptor.forClass(Player.class);
        verify(playerRepo).save(playerCaptor.capture());
        assertSame(user, playerCaptor.getValue().getUser());

        InOrder inOrder = inOrder(userRepo, roleRepo, passwordEncoder, playerRepo);
        inOrder.verify(userRepo).findByEmail("player@mail.com");
        inOrder.verify(userRepo).findByNumberOfPassport("AA123456");
        inOrder.verify(roleRepo).findByName("ROLE_PLAYER");
        inOrder.verify(passwordEncoder).encode("rawPassword");
        inOrder.verify(userRepo).save(user);
        inOrder.verify(playerRepo).save(any(Player.class));
    }

    @Test
    void registration_UserRepoReturnsSavedUser_ShouldAttachSavedUserToPlayer()
            throws EmailAlreadyExistsException, UserAlreadyRegisteredException {
        UserE savedUser = createUser();
        savedUser.setId(99L);
        savedUser.setEmail("saved@mail.com");

        when(userRepo.findByEmail("player@mail.com")).thenReturn(null);
        when(userRepo.findByNumberOfPassport("AA123456")).thenReturn(null);
        when(roleRepo.findByName("ROLE_PLAYER")).thenReturn(playerRole);
        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");
        when(userRepo.save(user)).thenReturn(savedUser);

        playerService.registration(user);

        ArgumentCaptor<Player> playerCaptor = ArgumentCaptor.forClass(Player.class);
        verify(playerRepo).save(playerCaptor.capture());
        assertSame(savedUser, playerCaptor.getValue().getUser());
    }

    @Test
    void registration_EmailAlreadyExists_ShouldThrowEmailAlreadyExistsException()
            throws EmailAlreadyExistsException, UserAlreadyRegisteredException {
        when(userRepo.findByEmail("player@mail.com")).thenReturn(new UserE());

        EmailAlreadyExistsException exception = assertThrows(EmailAlreadyExistsException.class,
                () -> playerService.registration(user));

        assertEquals("User with this email already exists!", exception.getMessage());
        verify(userRepo, never()).findByNumberOfPassport(any());
        verify(roleRepo, never()).findByName(any());
        verify(passwordEncoder, never()).encode(any());
        verify(userRepo, never()).save(any());
        verify(playerRepo, never()).save(any());
    }

    @Test
    void registration_PassportAlreadyRegistered_ShouldThrowUserAlreadyRegisteredException()
            throws EmailAlreadyExistsException, UserAlreadyRegisteredException {
        when(userRepo.findByEmail("player@mail.com")).thenReturn(null);
        when(userRepo.findByNumberOfPassport("AA123456")).thenReturn(new UserE());

        UserAlreadyRegisteredException exception = assertThrows(UserAlreadyRegisteredException.class,
                () -> playerService.registration(user));

        assertEquals("You already have an account on our service", exception.getMessage());
        verify(roleRepo, never()).findByName(any());
        verify(passwordEncoder, never()).encode(any());
        verify(userRepo, never()).save(any());
        verify(playerRepo, never()).save(any());
    }

    @Test
    void registration_PlayerRoleMissing_ShouldThrowRuntimeException()
            throws EmailAlreadyExistsException, UserAlreadyRegisteredException {
        when(userRepo.findByEmail("player@mail.com")).thenReturn(null);
        when(userRepo.findByNumberOfPassport("AA123456")).thenReturn(null);
        when(roleRepo.findByName("ROLE_PLAYER")).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> playerService.registration(user));

        assertEquals("ROLE_PLAYER not found in the database", exception.getMessage());
        verify(passwordEncoder, never()).encode(any());
        verify(userRepo, never()).save(any());
        verify(playerRepo, never()).save(any());
    }

    private UserE createUser() {
        UserE user = new UserE();
        user.setName("John");
        user.setSurname("Doe");
        user.setEmail("player@mail.com");
        user.setPassword("rawPassword");
        user.setNumberOfPassport("AA123456");
        user.setPassportIssueDate(LocalDate.of(2020, 1, 1));
        user.setPassportIssuingAuthority("Authority");
        return user;
    }
}
