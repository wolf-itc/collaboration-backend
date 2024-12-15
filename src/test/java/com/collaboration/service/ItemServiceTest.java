/**
 *  ItemServiceTest
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

import com.collaboration.config.AppConfig;
import com.collaboration.config.CollaborationException;
import com.collaboration.config.CollaborationException.CollaborationExceptionReason;
import com.collaboration.model.*;

import jakarta.persistence.EntityManager;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private Item2OrgaRepository item2OrgaRepository;

    @Mock
    private Item2RoleRepository item2RoleRepository;

    @Mock
    private RoleService roleService;

    @Mock
    private OrganizationService organizationService;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private ItemService itemService;

    private ItemDTO itemDTO;
    private Item item;
    private OrganizationDTO organizationDTO;
    private RoleDTO roleDTO;

    @BeforeEach
    void setUp() {
        organizationDTO = new OrganizationDTO();
        organizationDTO.setId(10L);

        roleDTO = new RoleDTO();
        roleDTO.setId(20L);

        itemDTO = new ItemDTO();
        itemDTO.setId(1L);
        itemDTO.setItId(2L);
        itemDTO.setUserOrNonuserId(3L);
        itemDTO.getOrganizationDTOs().add(organizationDTO);
        itemDTO.getRoleDTOs().add(roleDTO);

        item = new Item();
        item.setId(1L);
        item.setItId(2L);
        item.setUserOrNonuserId(3L);
    }

    @Test
    void testCreateItem_success() throws CollaborationException {
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(item2OrgaRepository.save(any(Item2Orga.class))).thenReturn(new Item2Orga());
        when(item2RoleRepository.save(any(Item2Role.class))).thenReturn(new Item2Role());

        ItemDTO result = itemService.createItem(itemDTO);

        assertNotNull(result);
        assertEquals(itemDTO.getId(), result.getId());
        verify(itemRepository).save(any(Item.class));
        verify(item2OrgaRepository).save(any(Item2Orga.class));
        verify(item2RoleRepository).save(any(Item2Role.class));
    }

    @Test
    void testUpdateItem_success() throws CollaborationException {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDTO result = itemService.updateItem(itemDTO);

        assertNotNull(result);
        assertEquals(itemDTO.getId(), result.getId());
        verify(itemRepository).findById(1L);
        verify(itemRepository).save(any(Item.class));
        verify(item2OrgaRepository, atLeastOnce()).save(any(Item2Orga.class));
        verify(item2RoleRepository, atLeastOnce()).save(any(Item2Role.class));
    }

    @Test
    void testUpdateItem_notFound() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        CollaborationException exception = assertThrows(CollaborationException.class, () -> itemService.updateItem(itemDTO));
        assertEquals(CollaborationExceptionReason.NOT_FOUND, exception.getExceptionReason());
        verify(itemRepository).findById(1L);
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void testGetItemById_success() throws CollaborationException {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ItemDTO result = itemService.getItemById(1L, List.of());

        assertNotNull(result);
        assertEquals(itemDTO.getId(), result.getId());
        verify(itemRepository).findById(1L);
    }

    @Test
    void testGetItemById_notFound() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        CollaborationException exception = assertThrows(CollaborationException.class, () -> itemService.getItemById(1L, List.of()));
        assertEquals(CollaborationExceptionReason.NOT_FOUND, exception.getExceptionReason());
        verify(itemRepository).findById(1L);
    }

    @Test
    void testDeleteItemByUserId_success() throws CollaborationException {
        when(itemRepository.findByItIdAndUserOrNonuserId(AppConfig.ITEMTYPE_USER, 3L)).thenReturn(Optional.of(item));
        when(item2OrgaRepository.deleteAllByItemId(1L)).thenReturn(1);
        when(item2RoleRepository.deleteAllByItemId(1L)).thenReturn(1);
        when(itemRepository.deleteByItIdAndUserOrNonuserId(AppConfig.ITEMTYPE_USER, 3L)).thenReturn(1);

        int result = itemService.deleteItemByUserId(3L);

        assertEquals(1, result);
        verify(item2OrgaRepository).deleteAllByItemId(1L);
        verify(item2RoleRepository).deleteAllByItemId(1L);
        verify(itemRepository).deleteByItIdAndUserOrNonuserId(AppConfig.ITEMTYPE_USER, 3L);
    }

    @Test
    void testDeleteItemByUserId_notFound() {
        when(itemRepository.findByItIdAndUserOrNonuserId(AppConfig.ITEMTYPE_USER, 3L)).thenReturn(Optional.empty());

        CollaborationException exception = assertThrows(CollaborationException.class, () -> itemService.deleteItemByUserId(3L));
        assertEquals(CollaborationExceptionReason.NOT_FOUND, exception.getExceptionReason());
        verify(itemRepository).findByItIdAndUserOrNonuserId(AppConfig.ITEMTYPE_USER, 3L);
    }
}