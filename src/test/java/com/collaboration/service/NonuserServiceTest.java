/**
 *  NonuserServiceTest
 *
 *  @author Martin Wolf
 *  
 *  (C) 2024 Claus Hansen & Martin Wolf IT-Consulting (www.wolf-itc.de)
 * ***************************************************************************/
package com.collaboration.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.collaboration.config.CollaborationException;
import com.collaboration.model.ItemDTO;
import com.collaboration.model.Nonuser;
import com.collaboration.model.NonuserDTO;
import com.collaboration.model.NonuserRepository;
import com.collaboration.model.OrganizationDTO;

@ExtendWith(MockitoExtension.class)
class NonuserServiceTest {

    @Mock
    private NonuserRepository nonuserRepository;

    @Mock
    private ItemService itemService;

    @Mock
    private UserService userService;

    @Mock
    private OrganizationService organizationService;

    @InjectMocks
    private NonuserService nonuserService;

    private ItemDTO itemDTO;
    
    @BeforeEach
    void setUp() throws CollaborationException {
      itemDTO = new ItemDTO();
      itemDTO.setId(1L);
      itemDTO.setItId(1L);
      itemDTO.setUserOrNonuserId(1L);
    }

    @Test
    void testCreateNonuser() throws CollaborationException {
        NonuserDTO nonuserDTO = new NonuserDTO();
        nonuserDTO.setName("Test Nonuser");
        nonuserDTO.setContent("Test Content");
        nonuserDTO.setOrgaId(1L);
        nonuserDTO.setItemtypeId(2L);

        Nonuser nonuser = new Nonuser();
        nonuser.setId(1L);
        nonuser.setName("Test Nonuser");
        nonuser.setContent("Test Content");
        nonuser.setOrgaId(1L);

        when(nonuserRepository.save(any())).thenReturn(nonuser);
        when(organizationService.getOrganizationById(1L)).thenReturn(new OrganizationDTO());
        when(itemService.createItem(any())).thenReturn(new ItemDTO());
        when(itemService.getItemByNonuserId(Mockito.anyLong(), any())).thenReturn(itemDTO);

        NonuserDTO result = nonuserService.createNonuser(nonuserDTO);

        assertNotNull(result);
        assertEquals("Test Nonuser", result.getName());
        verify(nonuserRepository, times(1)).save(any());
        verify(itemService, times(1)).createItem(any());
    }

    @Test
    void testUpdateNonuser() throws CollaborationException {
        NonuserDTO nonuserDTO = new NonuserDTO();
        nonuserDTO.setId(1L);
        nonuserDTO.setName("Updated Nonuser");
        nonuserDTO.setContent("Updated Content");
        nonuserDTO.setItemtypeId(2L);

        Nonuser existingNonuser = new Nonuser();
        existingNonuser.setId(1L);
        existingNonuser.setName("Existing Nonuser");
        existingNonuser.setContent("Existing Content");
        existingNonuser.setOrgaId(1L);

        when(nonuserRepository.findById(1L)).thenReturn(Optional.of(existingNonuser));
        when(nonuserRepository.save(any())).thenReturn(existingNonuser);
        when(itemService.getItemByNonuserId(1L, List.of())).thenReturn(new ItemDTO());

        NonuserDTO result = nonuserService.updateNonuser(nonuserDTO);

        assertNotNull(result);
        assertEquals("Updated Nonuser", result.getName());
        verify(nonuserRepository, times(1)).save(any());
        verify(itemService, times(1)).updateItem(any());
    }

    @Test
    void testDeleteNonuser() throws CollaborationException {
        Nonuser existingNonuser = new Nonuser();
        existingNonuser.setId(1L);

        when(nonuserRepository.findById(1L)).thenReturn(Optional.of(existingNonuser));

        nonuserService.deleteNonuser(1L);

        verify(itemService, times(1)).deleteItemByNonuserId(1L);
        verify(nonuserRepository, times(1)).deleteById(1L);
    }

    @Test
    void testGetNonuserById() throws CollaborationException {
        Nonuser nonuser = new Nonuser();
        nonuser.setId(1L);
        nonuser.setName("Test Nonuser");

        when(nonuserRepository.findById(1L)).thenReturn(Optional.of(nonuser));
        when(itemService.getItemByNonuserId(Mockito.anyLong(), any())).thenReturn(itemDTO);

        NonuserDTO result = nonuserService.getNonuserById(1L);

        assertNotNull(result);
        assertEquals("Test Nonuser", result.getName());
        verify(nonuserRepository, times(1)).findById(1L);
    }

    @Test
    void testGetAllNonusers() throws CollaborationException {
        Nonuser nonuser = new Nonuser();
        nonuser.setId(1L);
        nonuser.setName("Test Nonuser");

        when(nonuserRepository.findAll()).thenReturn(List.of(nonuser));
        when(itemService.getItemByNonuserId(Mockito.anyLong(), any())).thenReturn(itemDTO);

        List<NonuserDTO> result = nonuserService.getAllNonusers();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(nonuserRepository, times(1)).findAll();
    }

    @Test
    void testGetAllNonusersByOrgaId() throws CollaborationException {
        Nonuser nonuser = new Nonuser();
        nonuser.setId(1L);
        nonuser.setOrgaId(1L);
        nonuser.setName("Test Nonuser");

        when(nonuserRepository.findByOrgaId(1L)).thenReturn(List.of(nonuser));
        when(itemService.getItemByNonuserId(Mockito.anyLong(), any())).thenReturn(itemDTO);

        List<NonuserDTO> result = nonuserService.getAllNonusersByOrgaId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(nonuserRepository, times(1)).findByOrgaId(1L);
    }

    @Test
    void testGetNonusersByName() throws CollaborationException {
        Nonuser nonuser = new Nonuser();
        nonuser.setId(1L);
        nonuser.setName("Test Nonuser");

        when(nonuserRepository.findByName("Test Nonuser")).thenReturn(List.of(nonuser));
        when(itemService.getItemByNonuserId(Mockito.anyLong(), any())).thenReturn(itemDTO);

        List<NonuserDTO> result = nonuserService.getNonusersByName("Test Nonuser");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(nonuserRepository, times(1)).findByName("Test Nonuser");
    }

    @Test
    void testGetNonusersByuserId() throws CollaborationException {
        Nonuser nonuser = new Nonuser();
        nonuser.setId(1L);
        nonuser.setUserId(123);

        when(nonuserRepository.findByUserId(123)).thenReturn(List.of(nonuser));
        when(itemService.getItemByNonuserId(Mockito.anyLong(), any())).thenReturn(itemDTO);

        List<NonuserDTO> result = nonuserService.getNonusersByuserId(123);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(nonuserRepository, times(1)).findByUserId(123);
    }
}
