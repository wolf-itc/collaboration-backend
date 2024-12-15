/**
 *  OrganizationService
 *
 *  @author Martin Wolf
 *  
 *  (C) 2024 Claus Hansen & Martin Wolf IT-Consulting (www.wolf-itc.de)
 * ***************************************************************************/
package com.collaboration.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.collaboration.config.CollaborationException;
import com.collaboration.model.Organization;
import com.collaboration.model.OrganizationDTO;
import com.collaboration.model.OrganizationRepository;
import com.collaboration.model.RoleDTO;

@ExtendWith(MockitoExtension.class)
class OrganizationServiceTest {

    @Mock
    private OrganizationRepository organizationRepository;
    
    @Mock
    private RoleService roleService;
    
    @InjectMocks
    private OrganizationService organizationService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void createOrganization_ShouldCreateOrganizationAndRoles() throws CollaborationException {
        // Arrange
        OrganizationDTO organizationDTO = new OrganizationDTO();
        organizationDTO.setName("Test Organization");
        organizationDTO.setDescription("Test Description");
        organizationDTO.setLogo(Base64.getEncoder().encodeToString("logo".getBytes()));

        Organization organization = new Organization();
        organization.setId(1L);
        organization.setName("Test Organization");
        organization.setDescription("Test Description");
        organization.setLogo("logo".getBytes());

        when(organizationRepository.save(any(Organization.class))).thenReturn(organization);

        // Act
        OrganizationDTO result = organizationService.createOrganization(organizationDTO);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Organization", result.getName());

        verify(roleService, times(3)).createRole(any(RoleDTO.class));
    }

    @Test
    void updateOrganization_ShouldUpdateExistingOrganization() throws CollaborationException {
        // Arrange
        OrganizationDTO organizationDTO = new OrganizationDTO();
        organizationDTO.setId(1L);
        organizationDTO.setName("Updated Organization");

        Organization existingOrganization = new Organization();
        existingOrganization.setId(1L);
        existingOrganization.setName("Updated Organization");

        when(organizationRepository.findById(1L)).thenReturn(Optional.of(existingOrganization));
        when(organizationRepository.save(any(Organization.class))).thenReturn(existingOrganization);

        // Act
        OrganizationDTO result = organizationService.updateOrganization(organizationDTO);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Updated Organization", result.getName());
        verify(organizationRepository).save(any(Organization.class));
    }

    @Test
    void deleteOrganization_ShouldDeleteOrganizationById() throws CollaborationException {
        // Arrange
        Organization existingOrganization = new Organization();
        existingOrganization.setId(1L);

        when(organizationRepository.findById(1L)).thenReturn(Optional.of(existingOrganization));

        // Act
        organizationService.deleteOrganization(1L);

        // Assert
        verify(organizationRepository).deleteById(1L);
    }

    @Test
    void getOrganizationById_ShouldReturnOrganization() throws CollaborationException {
        // Arrange
        Organization organization = new Organization();
        organization.setId(1L);
        organization.setName("Test Organization");

        when(organizationRepository.findById(1L)).thenReturn(Optional.of(organization));

        // Act
        OrganizationDTO result = organizationService.getOrganizationById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Organization", result.getName());
    }

    @Test
    void getAllOrganizations_ShouldReturnAllOrganizations() {
        // Arrange
        Organization organization1 = new Organization();
        organization1.setId(1L);
        organization1.setName("Organization 1");

        Organization organization2 = new Organization();
        organization2.setId(2L);
        organization2.setName("Organization 2");

        when(organizationRepository.findAll()).thenReturn(List.of(organization1, organization2));

        // Act
        List<OrganizationDTO> result = organizationService.getAllOrganizations();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void createOrganization_ShouldThrowException_WhenSaveFails() {
        // Arrange
        OrganizationDTO organizationDTO = new OrganizationDTO();
        organizationDTO.setName("Test Organization");

        when(organizationRepository.save(any(Organization.class))).thenThrow(new RuntimeException("DB Error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> organizationService.createOrganization(organizationDTO));
    }
}
