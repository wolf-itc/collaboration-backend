/**
 *  RoleService
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

import com.collaboration.Helpers;
import com.collaboration.config.CollaborationException;
import com.collaboration.model.Role;
import com.collaboration.model.RoleDTO;
import com.collaboration.model.RoleRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RoleService {

  private final ModelMapper modelMapper = new ModelMapper();

  private final RoleRepository roleRepository;

  public RoleService(final RoleRepository roleRepository) {
    this.roleRepository = roleRepository;
  }

  public RoleDTO createRole(final RoleDTO roleDTO) throws CollaborationException {
    log.trace("> createRole");

    // Check role only contains valid characters
    Helpers.ensureAlphabetic(roleDTO.getRolename());
    
    Role role = convertFromDTO(roleDTO);
    roleRepository.save(role);
    
    log.trace("< createRole");
    return convertToDTO(role);
  }

  public RoleDTO updateRole(final RoleDTO roleDTO) throws CollaborationException {
    log.trace("> updateRole");

    // Check if exists
    roleRepository.findById(roleDTO.getId()).orElseThrow(() -> new CollaborationException(CollaborationException.CollaborationExceptionReason.NOT_FOUND));

    // Check role only contains valid characters
    Helpers.ensureAlphabetic(roleDTO.getRolename());
    
    log.trace("< updateRole");
    return convertToDTO(roleRepository.save(convertFromDTO(roleDTO)));
  }

  public void deleteRole(final long id) throws CollaborationException {
    log.trace("> deleteRole");

    // Check if exists
    roleRepository.findById(id).orElseThrow(() -> new CollaborationException(CollaborationException.CollaborationExceptionReason.NOT_FOUND));
    
    roleRepository.deleteById(id);

    log.trace("< deleteRole");
  }

  public List<RoleDTO> getAllRoles() {
    return roleRepository.findAll().stream().map( i -> convertToDTO(i)).collect(Collectors.toList());
  }

  public RoleDTO getRoleById(final long id) throws CollaborationException {
    Role role = roleRepository.findById(id).orElseThrow(() -> new CollaborationException(CollaborationException.CollaborationExceptionReason.NOT_FOUND));
    return convertToDTO(role);
  }

  public List<RoleDTO> getRolesByRoleIds(final List<Long> ids) throws CollaborationException {
    return roleRepository.findAllById(ids).stream().map( i -> convertToDTO(i)).collect(Collectors.toList());
  }

  public List<RoleDTO> getAllRolesByOrgaId(final long orgaid) {
    return roleRepository.findByOrgaId(orgaid).stream().map( i -> convertToDTO(i)).collect(Collectors.toList());
  }

  public List<RoleDTO> getRolesByOrgaIdAndRoleIds(final long orgaId, List<Long> roleIds) {
    return roleRepository.findByOrgaIdAndIdIn(orgaId, roleIds).stream().map(this::convertToDTO).toList();
  }

  public RoleDTO getRoleByRolenameAndOrgaId(final String rolename, final long id) throws CollaborationException {
    Role role = roleRepository.findRoleByRolenameAndOrgaId(rolename, id).orElseThrow(() -> new CollaborationException(CollaborationException.CollaborationExceptionReason.NOT_FOUND));
    return convertToDTO(role);
  }

  private Role convertFromDTO(final RoleDTO roleDTO) {
    return modelMapper.map(roleDTO, Role.class);
  }

  private RoleDTO convertToDTO(final Role role) {
    return modelMapper.map(role, RoleDTO.class);
  }
}
