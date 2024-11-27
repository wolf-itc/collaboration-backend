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

@Service
public class OrganizationService {

  private final ModelMapper modelMapper = new ModelMapper();

  private final OrganizationRepository organizationRepository;

  public OrganizationService(final OrganizationRepository organizationRepository) {
    this.organizationRepository = organizationRepository;
  }

  public OrganizationDTO createOrganization(final OrganizationDTO organization) {
    organization.setId(0);
    return convertToDTO(organizationRepository.save(convertFromDTO(organization)));
  }

  public OrganizationDTO updateOrganization(final OrganizationDTO organization) throws CollaborationException {
    // Check if exists
    getOrganizationById(organization.getId());
    
    return convertToDTO(organizationRepository.save(convertFromDTO(organization)));
  }

  public void deleteOrganization(final long id) throws CollaborationException {
    // Check if exists
    getOrganizationById(id);
    
    organizationRepository.deleteById(id);
  }

  public OrganizationDTO getOrganizationById(final long id) throws CollaborationException {
    Organization organization = organizationRepository.findById(id).orElseThrow(() -> new CollaborationException(CollaborationException.CollaborationExceptionReason.NOT_FOUND));
    return convertToDTO(organization);
  }

  public List<OrganizationDTO> getAllOrganizations(){
    return organizationRepository.findAll().stream().map( i -> convertToDTO(i)).collect(Collectors.toList());
  }
  
  public String getOrganizationPrefix(final long orgaid) throws CollaborationException{
    return getOrganizationById(orgaid).getPrefix();
  }
  
  private OrganizationDTO convertToDTO(final Organization organization) {
    OrganizationDTO organizationDTO = modelMapper.map(organization, OrganizationDTO.class);
    if (organization.getLogo() != null) {
      String base64Logo = Base64.getEncoder().encodeToString(organization.getLogo());
      organizationDTO.setLogo(base64Logo);
    }
    return organizationDTO;
  }
  
  private Organization convertFromDTO(final OrganizationDTO organizationDTO) {
      Organization organization = modelMapper.map(organizationDTO, Organization.class);
      if (StringUtils.isNotBlank(organizationDTO.getLogo())) {
        byte[] decodedBytes = Base64.getDecoder().decode(organizationDTO.getLogo());
        organization.setLogo(decodedBytes);
      }
      return organization;
  }
}
