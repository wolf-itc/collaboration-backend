/**
 *  NonUserService
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

import com.collaboration.config.CollaborationException;
import com.collaboration.model.NonUser;
import com.collaboration.model.NonUserDTO;
import com.collaboration.model.NonUserRepository;

@Service
public class NonUserService {

  private final ModelMapper modelMapper = new ModelMapper();

  private final NonUserRepository nonUserRepository;

  public NonUserService(final NonUserRepository nonUserRepository) {
    this.nonUserRepository = nonUserRepository;
  }

  // Create a new NonUser
  public NonUserDTO createNonUser(final NonUserDTO nonUserDTO) {
    NonUser nonUser = convertFromDTO(nonUserDTO);
    return convertToDTO(nonUserRepository.save(nonUser));
  }

  // Update an existing NonUser
  public NonUserDTO updateNonUser(final NonUserDTO nonUserDTO) throws CollaborationException {
    Optional<NonUser> existingNonUser = nonUserRepository.findById(nonUserDTO.getId());
    if (existingNonUser.isEmpty()) {
      throw new CollaborationException(CollaborationException.CollaborationExceptionReason.NOT_FOUND);
    }
    NonUser nonUser = convertFromDTO(nonUserDTO);
    return convertToDTO(nonUserRepository.save(nonUser));
  }

  // Delete a NonUser by ID
  public void deleteNonUser(final long id) throws CollaborationException {
    Optional<NonUser> existingNonUser = nonUserRepository.findById(id);
    if (existingNonUser.isEmpty()) {
      throw new CollaborationException(CollaborationException.CollaborationExceptionReason.NOT_FOUND);
    }
    nonUserRepository.deleteById(id);
  }

  // Get a NonUser by ID
  public NonUserDTO getNonUserById(final long id) throws CollaborationException {
    NonUser nonUser = nonUserRepository.findById(id).orElseThrow(() -> new CollaborationException(CollaborationException.CollaborationExceptionReason.NOT_FOUND));
    return convertToDTO(nonUser);
  }

  // Get all NonUsers
  public List<NonUserDTO> getAllNonUsers() {
    return nonUserRepository.findAll().stream().map( i -> convertToDTO(i)).collect(Collectors.toList());
  }

  public List<NonUserDTO> getAllNonUsersByOrgaId(final long orgaid) {
    return nonUserRepository.findByOrgaid(orgaid).stream().map( i -> convertToDTO(i)).collect(Collectors.toList());
  }

  // Find NonUsers by name
  public List<NonUserDTO> findNonUsersByName(final String name) {
    List<NonUser> nonUsers = nonUserRepository.findByName(name);
    return nonUsers.stream().map(this::convertToDTO).toList();
  }

  // Find NonUsers by createdById
  public List<NonUserDTO> findNonUsersByCreatedById(final int createdById) {
    List<NonUser> nonUsers = nonUserRepository.findByCreatedById(createdById);
    return nonUsers.stream().map(this::convertToDTO).toList();
  }

  // Convert from NonUser entity to DTO
  private NonUserDTO convertToDTO(final NonUser nonUser) {
    return modelMapper.map(nonUser, NonUserDTO.class);
  }

  // Convert from DTO to NonUser entity
  private NonUser convertFromDTO(final NonUserDTO nonUserDTO) {
    return modelMapper.map(nonUserDTO, NonUser.class);
  }
}
