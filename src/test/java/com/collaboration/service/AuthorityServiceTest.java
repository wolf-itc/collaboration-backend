/**
 *  AuthorityServiceTest
 *  
 *  @author Martin Wolf
 *  
 *  (C) 2024 Claus Hansen & Martin Wolf IT-Consulting (www.wolf-itc.de)
 * ***************************************************************************/
package com.collaboration.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.collaboration.config.CollaborationException;
import com.collaboration.config.CollaborationException.CollaborationExceptionReason;
import com.collaboration.model.Authority;
import com.collaboration.model.AuthorityDTO;
import com.collaboration.model.AuthorityRepository;
import com.collaboration.model.User;
import com.collaboration.model.UserDTO;
import com.collaboration.model.UserRepository;

@ExtendWith(MockitoExtension.class)
class AuthorityServiceTest {

    @Mock
    private AuthorityRepository authorityRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthorityService authorityService;

    private AuthorityDTO authorityDTO;
    private Authority authority;
    private UserDTO userDTO;
    private User user;

    @BeforeEach
    void setUp() {
        authorityDTO = new AuthorityDTO();
        authorityDTO.setId(1L);
        authorityDTO.setUserId(2L);
        authorityDTO.setUsername("testUser");
        authorityDTO.setAuthority("ROLE_USER");

        user = new User();
        user.setId(2L);
        user.setUsername("testUser");

        userDTO = new UserDTO();
        userDTO.setId(2L);
        userDTO.setUsername("testUser");

        authority = new Authority();
        authority.setId(1L);
        authority.setUserId(2L);
        authority.setAuthority("ROLE_USER");
    }

    @Test
    void testCreateAuthority_success() throws CollaborationException {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(authorityRepository.save(any(Authority.class))).thenReturn(authority);

        AuthorityDTO result = authorityService.createAuthority(authorityDTO);

        assertNotNull(result);
        assertEquals(authorityDTO.getId(), result.getId());
        verify(userRepository).findByUsername("testUser");
        verify(authorityRepository).save(any(Authority.class));
    }

    @Test
    void testCreateAuthority_unknownUsername() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.empty());

        CollaborationException exception = assertThrows(CollaborationException.class, () -> authorityService.createAuthority(authorityDTO));
        assertEquals(CollaborationExceptionReason.UNKNOWN_USERNAME, exception.getExceptionReason());
        verify(userRepository).findByUsername("testUser");
        verifyNoInteractions(authorityRepository);
    }

    @Test
    void testUpdateAuthority_success() throws CollaborationException {
        when(authorityRepository.findById(1L)).thenReturn(Optional.of(authority));
        when(authorityRepository.save(any(Authority.class))).thenReturn(authority);

        AuthorityDTO result = authorityService.updateAuthority(authorityDTO);

        assertNotNull(result);
        assertEquals(authorityDTO.getId(), result.getId());
        verify(authorityRepository).findById(1L);
        verify(authorityRepository).save(any(Authority.class));
    }

    @Test
    void testUpdateAuthority_notFound() {
        when(authorityRepository.findById(1L)).thenReturn(Optional.empty());

        CollaborationException exception = assertThrows(CollaborationException.class, () -> authorityService.updateAuthority(authorityDTO));
        assertEquals(CollaborationExceptionReason.NOT_FOUND, exception.getExceptionReason());
        verify(authorityRepository).findById(1L);
        verifyNoMoreInteractions(authorityRepository);
    }

    @Test
    void testDeleteAuthority_success() throws CollaborationException {
        when(authorityRepository.findById(1L)).thenReturn(Optional.of(authority));

        authorityService.deleteAuthority(1L);

        verify(authorityRepository).findById(1L);
        verify(authorityRepository).deleteById(1L);
    }

    @Test
    void testDeleteAuthority_notFound() {
        when(authorityRepository.findById(1L)).thenReturn(Optional.empty());

        CollaborationException exception = assertThrows(CollaborationException.class, () -> authorityService.deleteAuthority(1L));
        assertEquals(CollaborationExceptionReason.NOT_FOUND, exception.getExceptionReason());
        verify(authorityRepository).findById(1L);
        verifyNoMoreInteractions(authorityRepository);
    }

    @Test
    void testGetAllAuthorities_success() {
        when(authorityRepository.findAll()).thenReturn(List.of(authority));

        List<AuthorityDTO> result = authorityService.getAllAuthorities();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(authorityDTO.getId(), result.get(0).getId());
        verify(authorityRepository).findAll();
    }

    @Test
    void testGetAuthorityById_success() throws CollaborationException {
        when(authorityRepository.findById(1L)).thenReturn(Optional.of(authority));

        AuthorityDTO result = authorityService.getAuthorityById(1L);

        assertNotNull(result);
        assertEquals(authorityDTO.getId(), result.getId());
        verify(authorityRepository).findById(1L);
    }

    @Test
    void testGetAuthorityById_notFound() {
        when(authorityRepository.findById(1L)).thenReturn(Optional.empty());

        CollaborationException exception = assertThrows(CollaborationException.class, () -> authorityService.getAuthorityById(1L));
        assertEquals(CollaborationExceptionReason.NOT_FOUND, exception.getExceptionReason());
        verify(authorityRepository).findById(1L);
    }

    @Test
    void testGetAuthoritiesByUserId_success() {
        when(authorityRepository.findByUserId(2L)).thenReturn(List.of(authority));

        List<AuthorityDTO> result = authorityService.getAuthoritiesByUserId(2L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(authorityDTO.getId(), result.get(0).getId());
        verify(authorityRepository).findByUserId(2L);
    }
}
