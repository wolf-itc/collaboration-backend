/**
 *  ItemTypeService
 *
 *  @author Martin Wolf
 *  
 *  (C) 2024 Claus Hansen & Martin Wolf IT-Consulting (www.wolf-itc.de)
 * ***************************************************************************/
package com.collaboration.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.collaboration.config.CollaborationException;
import com.collaboration.model.ItemType;
import com.collaboration.model.ItemTypeDTO;
import com.collaboration.model.ItemTypeRepository;

@Service
public class ItemTypeService {

  private final ModelMapper modelMapper = new ModelMapper();

  @Autowired
  private ItemTypeRepository itemTypeRepository;

  public ItemTypeDTO createItemType(ItemTypeDTO itemTypeDTO) {
    ItemType itemType = convertFromDTO(itemTypeDTO);
    itemTypeRepository.save(itemType);
    
    return convertToDTO(itemType);
  }

  public void updateItemType(final ItemTypeDTO itemTypeDTO) throws CollaborationException {
    // Check if exists
    itemTypeRepository.findById(itemTypeDTO.getId()).orElseThrow(() -> new CollaborationException(CollaborationException.CollaborationExceptionReason.NOT_FOUND));

    itemTypeRepository.save(convertFromDTO(itemTypeDTO));
  }

  public void deleteItemType(final long id) throws CollaborationException {
    // Check if exists
    itemTypeRepository.findById(id).orElseThrow(() -> new CollaborationException(CollaborationException.CollaborationExceptionReason.NOT_FOUND));
    
    itemTypeRepository.deleteById(id);
  }

  public List<ItemTypeDTO> getAllItemTypes() {
    return itemTypeRepository.findAll().stream().map( i -> convertToDTO(i)).collect(Collectors.toList());
  }

  public ItemTypeDTO getItemTypeById(final long id) throws CollaborationException {
    ItemType itemType = itemTypeRepository.findById(id).orElseThrow(() -> new CollaborationException(CollaborationException.CollaborationExceptionReason.NOT_FOUND));
    return convertToDTO(itemType);
  }

  public List<ItemTypeDTO> getItemTypesByOrgaId(final long orgaid) {
    return itemTypeRepository.findByOrgaid(orgaid).stream().map( i -> convertToDTO(i)).collect(Collectors.toList());
  }

  private ItemType convertFromDTO(final ItemTypeDTO itemTypeDTO) {
    return modelMapper.map(itemTypeDTO, ItemType.class);
  }

  private ItemTypeDTO convertToDTO(final ItemType itemType) {
    return modelMapper.map(itemType, ItemTypeDTO.class);
  }
}
