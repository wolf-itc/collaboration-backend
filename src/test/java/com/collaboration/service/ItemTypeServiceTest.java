/**
 *  ItemtypeServiceTest
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
import com.collaboration.model.Itemtype;
import com.collaboration.model.ItemtypeDTO;
import com.collaboration.model.ItemtypeRepository;

@ExtendWith(MockitoExtension.class)
class ItemtypeServiceTest {

    @Mock
    private ItemtypeRepository itemtypeRepository;

    @InjectMocks
    private ItemtypeService itemtypeService;

    private Itemtype itemtype;
    private ItemtypeDTO itemtypeDTO;

    @BeforeEach
    void setUp() {
        itemtype = new Itemtype();
        itemtype.setId(1L);
        itemtype.setOrgaId(10L);
        itemtype.setName("Test Itemtype");

        itemtypeDTO = new ItemtypeDTO();
        itemtypeDTO.setId(1L);
        itemtypeDTO.setOrgaId(10L);
        itemtypeDTO.setName("Test Itemtype");
    }

    @Test
    void testCreateItemtype_success() {
        when(itemtypeRepository.save(any(Itemtype.class))).thenReturn(itemtype);

        ItemtypeDTO result = itemtypeService.createItemtype(itemtypeDTO);

        assertNotNull(result);
        assertEquals(itemtypeDTO.getId(), result.getId());
        verify(itemtypeRepository).save(any(Itemtype.class));
    }

    @Test
    void testUpdateItemtype_success() throws CollaborationException {
        when(itemtypeRepository.findById(1L)).thenReturn(Optional.of(itemtype));

        assertDoesNotThrow(() -> itemtypeService.updateItemtype(itemtypeDTO));

        verify(itemtypeRepository).findById(1L);
        verify(itemtypeRepository).save(any(Itemtype.class));
    }

    @Test
    void testUpdateItemtype_notFound() {
        when(itemtypeRepository.findById(1L)).thenReturn(Optional.empty());

        CollaborationException exception = assertThrows(CollaborationException.class, () -> itemtypeService.updateItemtype(itemtypeDTO));
        assertEquals(CollaborationExceptionReason.NOT_FOUND, exception.getExceptionReason());

        verify(itemtypeRepository).findById(1L);
        verifyNoMoreInteractions(itemtypeRepository);
    }

    @Test
    void testDeleteItemtype_success() throws CollaborationException {
        when(itemtypeRepository.findById(1L)).thenReturn(Optional.of(itemtype));

        assertDoesNotThrow(() -> itemtypeService.deleteItemtype(1L));

        verify(itemtypeRepository).findById(1L);
        verify(itemtypeRepository).deleteById(1L);
    }

    @Test
    void testDeleteItemtype_notFound() {
        when(itemtypeRepository.findById(1L)).thenReturn(Optional.empty());

        CollaborationException exception = assertThrows(CollaborationException.class, () -> itemtypeService.deleteItemtype(1L));
        assertEquals(CollaborationExceptionReason.NOT_FOUND, exception.getExceptionReason());

        verify(itemtypeRepository).findById(1L);
        verifyNoMoreInteractions(itemtypeRepository);
    }

    @Test
    void testGetAllItemtypes_success() {
        when(itemtypeRepository.findAll()).thenReturn(List.of(itemtype));

        List<ItemtypeDTO> result = itemtypeService.getAllItemtypes();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(itemtypeDTO.getId(), result.get(0).getId());
        verify(itemtypeRepository).findAll();
    }

    @Test
    void testGetItemtypeById_success() throws CollaborationException {
        when(itemtypeRepository.findById(1L)).thenReturn(Optional.of(itemtype));

        ItemtypeDTO result = itemtypeService.getItemtypeById(1L);

        assertNotNull(result);
        assertEquals(itemtypeDTO.getId(), result.getId());
        verify(itemtypeRepository).findById(1L);
    }

    @Test
    void testGetItemtypeById_notFound() {
        when(itemtypeRepository.findById(1L)).thenReturn(Optional.empty());

        CollaborationException exception = assertThrows(CollaborationException.class, () -> itemtypeService.getItemtypeById(1L));
        assertEquals(CollaborationExceptionReason.NOT_FOUND, exception.getExceptionReason());

        verify(itemtypeRepository).findById(1L);
    }

    @Test
    void testGetItemtypesByOrgaId_success() {
        when(itemtypeRepository.findByOrgaId(10L)).thenReturn(List.of(itemtype));

        List<ItemtypeDTO> result = itemtypeService.getItemtypesByOrgaId(10L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(itemtypeDTO.getId(), result.get(0).getId());
        verify(itemtypeRepository).findByOrgaId(10L);
    }
}
