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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.collaboration.Helpers;
import com.collaboration.config.CollaborationException;
import com.collaboration.model.Role;
import com.collaboration.model.RoleDTO;
import com.collaboration.model.RoleRepository;

@Service
public class RoleService {

  private final ModelMapper modelMapper = new ModelMapper();

  @Autowired
  OrganizationService organizationService;

  @Autowired
  private RoleRepository roleRepository;

  public RoleDTO createRole(final RoleDTO roleDTO) throws CollaborationException {
    // Check role only contains valid characters
    Helpers.ensureAlphabetic(roleDTO.getRolename());
    
    // Set prefix for organization
    addOrganizationPrefix(roleDTO);
    
    Role role = convertFromDTO(roleDTO);
    roleRepository.save(role);
    
    return convertToDTO(role);
  }

  public RoleDTO updateRole(final RoleDTO roleDTO) throws CollaborationException {
    // Check if exists
    roleRepository.findById(roleDTO.getId()).orElseThrow(() -> new CollaborationException(CollaborationException.CollaborationExceptionReason.NOT_FOUND));

    // Check role only contains valid characters
    Helpers.ensureAlphabetic(roleDTO.getRolename());
    
    // Set prefix for organization
    addOrganizationPrefix(roleDTO);
    
    var prefix = organizationService.getOrganizationPrefix(roleDTO.getOrgaid());
    if (StringUtils.hasText(prefix)) {
      roleDTO.setRolename(prefix + "_" + roleDTO.getRolename());
    }
    
    return convertToDTO(roleRepository.save(convertFromDTO(roleDTO)));
  }

  public void deleteRole(final long id) throws CollaborationException {
    // Check if exists
    roleRepository.findById(id).orElseThrow(() -> new CollaborationException(CollaborationException.CollaborationExceptionReason.NOT_FOUND));
    
    roleRepository.deleteById(id);
  }

  public List<RoleDTO> getAllRoles() {
    return roleRepository.findAll().stream().map( i -> convertToDTO(i)).collect(Collectors.toList());
  }

  public RoleDTO getRoleById(final long id) throws CollaborationException {
    Role role = roleRepository.findById(id).orElseThrow(() -> new CollaborationException(CollaborationException.CollaborationExceptionReason.NOT_FOUND));
    return convertToDTO(role);
  }

  public List<RoleDTO> getAllRolesByOrgaId(final long orgaid) {
    return roleRepository.findByOrgaid(orgaid).stream().map( i -> convertToDTO(i)).collect(Collectors.toList());
  }

  private Role convertFromDTO(final RoleDTO roleDTO) {
    return modelMapper.map(roleDTO, Role.class);
  }

  private RoleDTO convertToDTO(final Role role) {
    return modelMapper.map(role, RoleDTO.class);
  }
  
  private void addOrganizationPrefix(final RoleDTO roleDTO) throws CollaborationException {
    var prefix = organizationService.getOrganizationPrefix(roleDTO.getOrgaid());
    if (StringUtils.hasText(prefix)) {
      roleDTO.setRolename(prefix + "_" + roleDTO.getRolename());
    }
  }
}
