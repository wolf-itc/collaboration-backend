/**
 *  UserServiceTest
 *
 *  @author Martin Wolf
 *  
 *  (C) 2024 Claus Hansen & Martin Wolf IT-Consulting (www.wolf-itc.de)
 * ***************************************************************************/
package com.collaboration.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.collaboration.config.CollaborationException;
import com.collaboration.config.CollaborationException.CollaborationExceptionReason;
import com.collaboration.model.Authority;
import com.collaboration.model.AuthorityRepository;
import com.collaboration.model.Item2OrgaRepository;
import com.collaboration.model.OrganizationDTO;
import com.collaboration.model.RoleDTO;
import com.collaboration.model.User;
import com.collaboration.model.UserDTO;
import com.collaboration.model.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthorityRepository authorityRepository;

    @Mock
    private ItemService itemService;

    @Mock
    private RoleService roleService;

    @Mock
    private OrganizationService organizationService;

    @Mock
    private Item2OrgaRepository item2OrgaRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserDTO userDTO;
    private User user;
    private OrganizationDTO organizationDTO;
    private RoleDTO roleDTO;

    @BeforeEach
    void setUp() {
        userDTO = new UserDTO();
        userDTO.setUsername("testUser");
        userDTO.setPassword("testPassword");
        userDTO.setOrgaId(1L);

        user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setPassword("encodedPassword");
        
        organizationDTO = new OrganizationDTO();
        organizationDTO.setId(1L);
        organizationDTO.setName("orga1");
        
        roleDTO = new RoleDTO();
        roleDTO.setId(1L);
        roleDTO.setOrgaId(1L);
        roleDTO.setRolename("User");
    }

    @Test
    void testCreateUser_success() throws CollaborationException {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("testPassword")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(organizationService.getOrganizationById(Mockito.anyLong())).thenReturn(organizationDTO);
        when(roleService.getRoleByRolenameAndOrgaId(any(), Mockito.anyLong())).thenReturn(roleDTO);
        
        UserDTO result = userService.createUser(userDTO);

        assertNotNull(result);
        assertEquals(userDTO.getUsername(), result.getUsername());
        verify(userRepository).findByUsername("testUser");
        verify(userRepository).save(any(User.class));
        verify(authorityRepository).save(any(Authority.class));
    }

    @Test
    void testCreateUser_usernameAlreadyExists() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));

        CollaborationException exception = assertThrows(CollaborationException.class, () -> userService.createUser(userDTO));
        assertEquals(CollaborationExceptionReason.USERNAME_ALREADY_EXISTS, exception.getExceptionReason());
        verify(userRepository).findByUsername("testUser");
        verifyNoMoreInteractions(userRepository, authorityRepository);
    }

    @Test
    void testUpdateUser_success() throws CollaborationException {
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDTO result = userService.updateUser(userDTO);

        assertNotNull(result);
        assertEquals(userDTO.getUsername(), result.getUsername());
        verify(userRepository, Mockito.never()).findByUsername("testUser");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testDeleteAccount_success() throws CollaborationException {
        when(userRepository.findByActivationkey("activationKey")).thenReturn(Optional.of(user));

        userService.deleteAccount("activationKey");

        verify(userRepository).findByActivationkey("activationKey");
        verify(authorityRepository).deleteById(user.getId());
        verify(itemService).deleteItemByUserId(user.getId());
        verify(userRepository).deleteById(user.getId());
    }

    @Test
    void testDeleteAccount_invalidActivationKey() {
        when(userRepository.findByActivationkey("activationKey")).thenReturn(Optional.empty());

        CollaborationException exception = assertThrows(CollaborationException.class, () -> userService.deleteAccount("activationKey"));
        assertEquals(CollaborationExceptionReason.INVALID_ACTIVATIONKEY, exception.getExceptionReason());
        verify(userRepository).findByActivationkey("activationKey");
        verifyNoInteractions(authorityRepository, itemService);
    }
    
    @Test
    void testDoLogin_success() throws CollaborationException {
        user.setEnabled(true);
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("testPassword", "encodedPassword")).thenReturn(true);

        UserDTO result = userService.doLogin(userDTO);

        assertNotNull(result);
        assertEquals(user.getUsername(), result.getUsername());
        verify(userRepository).findByUsername("testUser");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testDoLogin_wrongPassword() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("testPassword", "encodedPassword")).thenReturn(false);

        CollaborationException exception = assertThrows(CollaborationException.class, () -> userService.doLogin(userDTO));
        assertEquals(CollaborationExceptionReason.WRONG_PASSWORD, exception.getExceptionReason());
        verify(userRepository).findByUsername("testUser");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testDoLogin_accountLocked() {
        user.setFailloginCount(UserService.MAX_ALLOWED_FAIL_LOGINS + 1);
        user.setEnabled(false);
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));

        CollaborationException exception = assertThrows(CollaborationException.class, () -> userService.doLogin(userDTO));
        assertEquals(CollaborationExceptionReason.ACCOUNT_HAS_BEEN_LOCKED, exception.getExceptionReason());
        verify(userRepository).findByUsername("testUser");
    }
}
