/**
 *  OrganizationService
 *
 *  @author Martin Wolf
 *  
 *  (C) 2024 Claus Hansen & Martin Wolf IT-Consulting (www.wolf-itc.de)
 * ***************************************************************************/
package com.collaboration.service;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.collaboration.config.CollaborationException;
import com.collaboration.model.Organization;
import com.collaboration.model.OrganizationDTO;
import com.collaboration.model.OrganizationRepository;
import com.collaboration.model.RoleDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OrganizationService {

  private final ModelMapper modelMapper = new ModelMapper();

  private final OrganizationRepository organizationRepository;
  private final RoleService roleService;

  public OrganizationService(final OrganizationRepository organizationRepository, RoleService roleService) {
    this.organizationRepository = organizationRepository;
    this.roleService = roleService;
  }

  public OrganizationDTO createOrganization(final OrganizationDTO organizationDTO) throws CollaborationException {
    log.trace("> createOrganization");

    organizationDTO.setId(0);
    var organization = organizationRepository.save(convertFromDTO(organizationDTO));
    
    // Create the two main-roles for the organization
    RoleDTO roleDTO = new RoleDTO(0L, organization.getId(), "USER");
    roleService.createRole(roleDTO);
    roleDTO = new RoleDTO(0L, organization.getId(), "ADMIN");
    roleService.createRole(roleDTO);
    roleDTO = new RoleDTO(0L, organization.getId(), "GUEST");
    roleService.createRole(roleDTO);

    log.trace("< createOrganization");
    return convertToDTO(organization);
  }

  public OrganizationDTO updateOrganization(final OrganizationDTO organizationDTO) throws CollaborationException {
    log.trace("> updateOrganization");

    // Check if exists
    getOrganizationById(organizationDTO.getId());
    
    log.trace("< updateOrganization");
    return convertToDTO(organizationRepository.save(convertFromDTO(organizationDTO)));
  }

  public void deleteOrganization(final long id) throws CollaborationException {
    log.trace("> deleteOrganization");

    // Check if exists
    getOrganizationById(id);
    
    organizationRepository.deleteById(id);

    log.trace("< deleteOrganization");
  }

  public OrganizationDTO getOrganizationById(final long id) throws CollaborationException {
    Organization organization = organizationRepository.findById(id).orElseThrow(() -> new CollaborationException(CollaborationException.CollaborationExceptionReason.NOT_FOUND));
    return convertToDTO(organization);
  }

  public List<OrganizationDTO> getAllOrganizations(){
    return organizationRepository.findAll().stream().map( i -> convertToDTO(i)).collect(Collectors.toList());
  }
  
  private OrganizationDTO convertToDTO(final Organization organization) {
    log.trace("> convertToDTO");

    OrganizationDTO organizationDTO = modelMapper.map(organization, OrganizationDTO.class);
    if (organization.getLogo() != null) {
      String base64Logo = Base64.getEncoder().encodeToString(organization.getLogo());
      organizationDTO.setLogo(base64Logo);
    }

    log.trace("< convertToDTO");
    return organizationDTO;
  }
  
  private Organization convertFromDTO(final OrganizationDTO organizationDTO) {
    log.trace("> convertFromDTO");

    Organization organization = modelMapper.map(organizationDTO, Organization.class);
    if (StringUtils.isNotBlank(organizationDTO.getLogo())) {
      byte[] decodedBytes = Base64.getDecoder().decode(organizationDTO.getLogo());
      organization.setLogo(decodedBytes);
    }

    log.trace("< convertFromDTO");
    return organization;
  }
}
