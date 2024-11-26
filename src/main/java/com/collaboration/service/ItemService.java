/**
 *  ItemService
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
import com.collaboration.model.Item;
import com.collaboration.model.ItemDTO;
import com.collaboration.model.ItemRepository;

@Service
public class ItemService {

  private final ModelMapper modelMapper = new ModelMapper();

  @Autowired
  private ItemRepository itemRepository;

  public ItemDTO createItem(final ItemDTO itemDTO) throws CollaborationException {
    Item item = convertFromDTO(itemDTO);
    itemRepository.save(item);
    
    return convertToDTO(item);
  }

  public ItemDTO updateItem(final ItemDTO itemDTO) throws CollaborationException {
    // Überprüfen, ob das Item existiert
    itemRepository.findById(itemDTO.getId()).orElseThrow(() -> new CollaborationException(CollaborationException.CollaborationExceptionReason.NOT_FOUND));

    return convertToDTO(itemRepository.save(convertFromDTO(itemDTO)));
  }

  public void deleteItem(final long id) throws CollaborationException {
    // Überprüfen, ob das Item existiert
    itemRepository.findById(id).orElseThrow(() -> new CollaborationException(CollaborationException.CollaborationExceptionReason.NOT_FOUND));
    
    itemRepository.deleteById(id);
  }

  public List<ItemDTO> getAllItems() {
    return itemRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
  }

  public ItemDTO getItemById(final long id) throws CollaborationException {
    Item item = itemRepository.findById(id).orElseThrow(() -> new CollaborationException(CollaborationException.CollaborationExceptionReason.NOT_FOUND));
    return convertToDTO(item);
  }
  
  public ItemDTO getByItidAndUserornonuserid(final long itId, final long userId) throws CollaborationException {
    Item item = itemRepository.findByItidAndUserornonuserid(itId, userId).orElseThrow(() -> new CollaborationException(CollaborationException.CollaborationExceptionReason.NOT_FOUND));
    return convertToDTO(item);
  }

  private Item convertFromDTO(final ItemDTO itemDTO) {
    return modelMapper.map(itemDTO, Item.class);
  }

  private ItemDTO convertToDTO(final Item item) {
    return modelMapper.map(item, ItemDTO.class);
  }
}
