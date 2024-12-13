/**
 *  NonuserService
 *
 *  @author Martin Wolf
 *  
 *  (C) 2024 Claus Hansen & Martin Wolf IT-Consulting (www.wolf-itc.de)
 * ***************************************************************************/
package com.collaboration.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.collaboration.config.CollaborationException;
import com.collaboration.model.ItemDTO;
import com.collaboration.model.Nonuser;
import com.collaboration.model.NonuserDTO;
import com.collaboration.model.NonuserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class NonuserService {

  private final ModelMapper modelMapper = new ModelMapper();

  private final NonuserRepository nonuserRepository;
  private final ItemService itemService;
  private final UserService userService;
  private final OrganizationService organizationService;

  public NonuserService(final NonuserRepository nonuserRepository, final ItemService itemService, 
      final UserService userService, final OrganizationService organizationService) {
    this.nonuserRepository = nonuserRepository;
    this.itemService = itemService;
    this.organizationService = organizationService;
    this.userService = userService;
  }

  // Create a new Nonuser
  @Transactional
  public NonuserDTO createNonuser(final NonuserDTO nonuserDTO) throws CollaborationException {
    log.trace("> createNonuser");

    Nonuser nonuser = convertFromDTO(nonuserDTO);
    nonuserRepository.save(nonuser);

    // Also all non-users are items automatically
    ItemDTO item = new ItemDTO(0, nonuserDTO.getItemtypeId(), nonuser.getId());
    if (nonuserDTO.getOrgaId() != 0) {
      var orga = organizationService.getOrganizationById(nonuserDTO.getOrgaId());
      item.setOrganizationDTOs(List.of(orga));
    }
    item = itemService.createItem(item);

    log.trace("< createNonuser");
    return convertToDTO(nonuser);
  }

  // Update an existing Nonuser
  @Transactional
  public NonuserDTO updateNonuser(final NonuserDTO nonuserDTO) throws CollaborationException {
    log.trace("> updateNonuser");

    Optional<Nonuser> existingNonuser = nonuserRepository.findById(nonuserDTO.getId());
    if (existingNonuser.isEmpty()) {
      throw new CollaborationException(CollaborationException.CollaborationExceptionReason.NOT_FOUND);
    }
    Nonuser nonuser = convertFromDTO(nonuserDTO);

    // The orgaId cannot be changed while updating, because it indicates which organization created this non-user
    nonuser.setOrgaId(existingNonuser.get().getOrgaId());
    nonuserRepository.save(nonuser);
    
    // Also update the itemtype
    ItemDTO item = itemService.getItemByNonuserId(nonuser.getId(), List.of());
    item.setItId(nonuserDTO.getItemtypeId());
    itemService.updateItem(item);

    log.trace("< updateNonuser");
    return convertToDTO(nonuser);
  }

  // Delete a Nonuser by Id
  @Transactional
  public void deleteNonuser(final long id) throws CollaborationException {
    log.trace("> deleteNonuser");

    nonuserRepository.findById(id).orElseThrow(() -> new CollaborationException(CollaborationException.CollaborationExceptionReason.NOT_FOUND));

    // First delete the item
    itemService.deleteItemByNonuserId(id);

    // Then the non-user itself
    nonuserRepository.deleteById(id);

    log.trace("< deleteNonuser");
  }

  // Get a Nonuser by Id
  @Transactional
  public NonuserDTO getNonuserById(final long id) throws CollaborationException {
    log.trace("> getNonuserById");

    Nonuser nonuser = nonuserRepository.findById(id).orElseThrow(() -> new CollaborationException(CollaborationException.CollaborationExceptionReason.NOT_FOUND));

    log.trace("< getNonuserById");
    return convertToDTO(nonuser);
  }

  // Get all Nonusers
  @Transactional
  public List<NonuserDTO> getAllNonusers() {
    return nonuserRepository.findAll().stream().map( i -> convertToDTO(i)).collect(Collectors.toList());
  }

  @Transactional
  public List<NonuserDTO> getAllNonusersByOrgaId(final long orgaid) {
    return nonuserRepository.findByOrgaId(orgaid).stream().map( i -> convertToDTO(i)).collect(Collectors.toList());
  }

  // Find Nonusers by name
  @Transactional
  public List<NonuserDTO> getNonusersByName(final String name) {
    List<Nonuser> nonusers = nonuserRepository.findByName(name);
    return nonusers.stream().map(this::convertToDTO).toList();
  }

  // Find Nonusers by userId
  @Transactional
  public List<NonuserDTO> getNonusersByuserId(final int userId) {
    List<Nonuser> nonusers = nonuserRepository.findByUserId(userId);
    return nonusers.stream().map(this::convertToDTO).toList();
  }

  // Convert from Nonuser entity to DTO
  private NonuserDTO convertToDTO(final Nonuser nonuser) {
    log.trace("> convertToDTO");

    var nonuserDTO = modelMapper.map(nonuser, NonuserDTO.class);

    // Get the item for the nonuser in order to set the itemtype in the DTO
    try {
      var item = itemService.getItemByNonuserId(nonuser.getId(), List.of());
      nonuserDTO.setItemtypeId(item.getItId());
    } catch (CollaborationException e) {
      log.error("Item for nonuserId={} was not found, should not happen!", nonuser.getId(), e);
    }
    
    log.trace("< convertToDTO");
    return nonuserDTO;
  }

  // Convert from DTO to Nonuser entity
  private Nonuser convertFromDTO(final NonuserDTO nonuserDTO) throws CollaborationException {
    log.trace("> convertFromDTO");

    var nonuser = modelMapper.map(nonuserDTO, Nonuser.class);
    nonuser.setUserId(userService.getCurrentUserId());

    log.trace("< convertFromDTO");
    return nonuser;
  }
}
