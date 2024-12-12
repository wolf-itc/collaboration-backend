/**
 *  ItemtypeService
 *
 *  @author Martin Wolf
 *  
 *  (C) 2024 Claus Hansen & Martin Wolf IT-Consulting (www.wolf-itc.de)
 * ***************************************************************************/
package com.collaboration.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.collaboration.config.CollaborationException;
import com.collaboration.model.Itemtype;
import com.collaboration.model.ItemtypeDTO;
import com.collaboration.model.ItemtypeRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ItemtypeService {

  private final ModelMapper modelMapper = new ModelMapper();

  private final ItemtypeRepository itemtypeRepository;
  
  public ItemtypeService(final ItemtypeRepository itemtypeRepository) {
    this.itemtypeRepository = itemtypeRepository;
  }

  public ItemtypeDTO createItemtype(ItemtypeDTO itemtypeDTO) {
    log.trace("> createItemtype");

    Itemtype itemtype = convertFromDTO(itemtypeDTO);
    itemtypeRepository.save(itemtype);
    
    log.trace("< createItemtype");
    return convertToDTO(itemtype);
  }

  public void updateItemtype(final ItemtypeDTO itemtypeDTO) throws CollaborationException {
    log.trace("> updateItemtype");

    // Check if exists
    itemtypeRepository.findById(itemtypeDTO.getId()).orElseThrow(() -> new CollaborationException(CollaborationException.CollaborationExceptionReason.NOT_FOUND));

    itemtypeRepository.save(convertFromDTO(itemtypeDTO));

    log.trace("< updateItemtype");
  }

  public void deleteItemtype(final long id) throws CollaborationException {
    log.trace("> deleteItemtype");

    // Check if exists
    itemtypeRepository.findById(id).orElseThrow(() -> new CollaborationException(CollaborationException.CollaborationExceptionReason.NOT_FOUND));
    
    itemtypeRepository.deleteById(id);

    log.trace("< deleteItemtype");
  }

  public List<ItemtypeDTO> getAllItemtypes() {
    return itemtypeRepository.findAll().stream().map( i -> convertToDTO(i)).collect(Collectors.toList());
  }

  public ItemtypeDTO getItemtypeById(final long id) throws CollaborationException {
    Itemtype itemtype = itemtypeRepository.findById(id).orElseThrow(() -> new CollaborationException(CollaborationException.CollaborationExceptionReason.NOT_FOUND));
    return convertToDTO(itemtype);
  }

  public List<ItemtypeDTO> getItemtypesByOrgaId(final long orgaid) {
    return itemtypeRepository.findByOrgaId(orgaid).stream().map( i -> convertToDTO(i)).collect(Collectors.toList());
  }

  private Itemtype convertFromDTO(final ItemtypeDTO itemtypeDTO) {
    return modelMapper.map(itemtypeDTO, Itemtype.class);
  }

  private ItemtypeDTO convertToDTO(final Itemtype itemtype) {
    return modelMapper.map(itemtype, ItemtypeDTO.class);
  }
}
