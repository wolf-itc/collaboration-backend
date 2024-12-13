/**
 *  OrganizationController
 *  
 *  All methods here are only callable for the ADMIN of Yare
 *
 *  @author Martin Wolf
 *  
 *  (C) 2024 Claus Hansen & Martin Wolf IT-Consulting (www.wolf-itc.de)
 * ***************************************************************************/
package com.collaboration.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.collaboration.config.CollaborationException;
import com.collaboration.model.OrganizationDTO;
import com.collaboration.service.OrganizationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@Tag(name = "Organization Management (Admin only)", description = "APIs for managing organizations")
@RequestMapping("/v1/organizations")
public class OrganizationController {

  private final OrganizationService organizationService;
  
  public OrganizationController(final OrganizationService organizationService) {
    this.organizationService = organizationService;
  }

  @Operation(summary = "Create new organization")
  @SecurityRequirement(name = "basicAuth")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "ok", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrganizationDTO.class))),
      @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(type = "string")))
  })
  @PostMapping
  public ResponseEntity<Object> createOrganization(@RequestBody OrganizationDTO organizationDTO) {
    log.trace("> createOrganization: organizationDTO={}", organizationDTO);

    try {
      organizationDTO = organizationService.createOrganization(organizationDTO);
      log.trace("< createOrganization: ok");
      return new ResponseEntity<>(organizationDTO, HttpStatus.OK );
    } catch (Exception e) {
      log.error("< createOrganization: error={}", e.getMessage(), e);
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR );
    }
  }
    
  @Operation(summary = "Update organization")
  @SecurityRequirement(name = "basicAuth")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "ok", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrganizationDTO.class))),
    @ApiResponse(responseCode = "400", description = "failed", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"))),
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(type = "string")))
  })
  @PutMapping("/{id}")
  public ResponseEntity<Object> updateOrganization(final @PathVariable long id, @RequestBody OrganizationDTO organizationDTO) {
    log.trace("> updateOrganization: id={} organizationDTO={}", id, organizationDTO);

    try {
      organizationDTO.setId(id);
      organizationDTO = organizationService.updateOrganization(organizationDTO);
      log.trace("< updateOrganization: ok");
      return new ResponseEntity<>( organizationDTO, HttpStatus.OK );
    } catch (CollaborationException e) {
      log.error("< updateOrganization: error={}", e.getMessage(), e);
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error("< updateOrganization: error={}", e.getMessage(), e);
      return new ResponseEntity<>( e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR );
    }
  }
    
  @Operation(summary = "Delete organization")
  @SecurityRequirement(name = "basicAuth")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "ok", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"))),
    @ApiResponse(responseCode = "400", description = "failed", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"))),
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(type = "string")))
  })
  @DeleteMapping("/deleteOrganization/{id}")
  public ResponseEntity<Object> deleteOrganization(final @PathVariable int id) {
    log.trace("> deleteOrganization: id={}", id);

    try {
      organizationService.deleteOrganization(id);
      log.trace("< deleteOrganization: ok");
      return new ResponseEntity<>(String.format("Organization id=%d deleted successfully", id), HttpStatus.OK );
    } catch (CollaborationException e) {
      log.error("< deleteOrganization: error={}", e.getMessage(), e);
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error("< deleteOrganization: error={}", e.getMessage(), e);
      return new ResponseEntity<>( e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR );
    }
  }

  @Operation(summary = "Retrieve organization")
  @SecurityRequirement(name = "basicAuth")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "ok", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrganizationDTO.class))),
    @ApiResponse(responseCode = "400", description = "failed", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"))),
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(type = "string")))
  })
  @GetMapping("/{id}")
  public ResponseEntity<Object> retrieveOrganization(final @PathVariable int id) {
    log.trace("> retrieveOrganization: id={}", id);

    try {
      OrganizationDTO organization = organizationService.getOrganizationById(id);
      log.trace("< retrieveOrganization: ok");
      return new ResponseEntity<>(organization, HttpStatus.OK);
    } catch (CollaborationException e) {
      log.error("< retrieveOrganization: error={}", e.getMessage(), e);
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error("< retrieveOrganization: error={}", e.getMessage(), e);
      return new ResponseEntity<>( e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR );
    }
  }

  @Operation(summary = "Retrieve all organizations")
  @SecurityRequirement(name = "basicAuth")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "ok", content = @Content(mediaType = "application/json", schema = @Schema(type = "array", implementation = OrganizationDTO.class))),
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(type = "string")))
  })
  @GetMapping
  public ResponseEntity<Object> retrieveAllOrganizations() {
    log.trace("> retrieveAllOrganizations");

    try {
      List<OrganizationDTO> listOrganizations = organizationService.getAllOrganizations();
      log.trace("< retrieveAllOrganizations: ok");
      return new ResponseEntity<>(listOrganizations, HttpStatus.OK);
    } catch (Exception e) {
      log.error("< retrieveAllOrganizations: error={}", e.getMessage(), e);
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
