/**
 *  PermissionController
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

import com.collaboration.PermissionEvaluator;
import com.collaboration.config.AppConfig;
import com.collaboration.config.CollaborationException;
import com.collaboration.config.CollaborationException.CollaborationExceptionReason;
import com.collaboration.model.PermissionDTO;
import com.collaboration.service.PermissionService;
import com.collaboration.service.RoleService;

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
@Tag(name = "Permissions Management", description = "APIs for managing permissions")
@RequestMapping("/v1/permissions")
public class PermissionController {

  private final PermissionService permissionService;
  private final RoleService roleService;
  private final PermissionEvaluator permissionEvaluator;
  
  public PermissionController(final PermissionService permissionService, final RoleService roleService, final PermissionEvaluator permissionEvaluator) {
    this.permissionService = permissionService;
    this.roleService = roleService;
    this.permissionEvaluator = permissionEvaluator;
  }

  @Operation(summary = "Create new permission")
  @SecurityRequirement(name = "basicAuth")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "201", description = "ok", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PermissionDTO.class))),
    @ApiResponse(responseCode = "400", description = "failed", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"))),
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(type = "string")))
  })
  @PostMapping
  public ResponseEntity<Object> createPermission(@RequestBody PermissionDTO permissionDTO) {
    log.trace("> createPermission: permissionDTO={}", permissionDTO);

    try {
      // Check access
      // First, get the orga from the role
      var orgaId = roleService.getRoleById(permissionDTO.getRoleId()).getOrgaId();
      permissionEvaluator.mayCreate(orgaId, AppConfig.ITEMTYPE_PERMISSION);
      
      permissionDTO = permissionService.createPermission(permissionDTO);
      log.trace("< createPermission: ok");
      return new ResponseEntity<>(permissionDTO, HttpStatus.CREATED);
    } catch (CollaborationException e) {
      log.error("< createPermission: error={}", e.getMessage(), e);
      if (e.getExceptionReason() == CollaborationExceptionReason.ACCESS_DENIED) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
      }
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error("< createPermission: error={}", e.getMessage(), e);
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Update permission")
  @SecurityRequirement(name = "basicAuth")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "ok", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PermissionDTO.class))),
    @ApiResponse(responseCode = "400", description = "failed", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"))),
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(type = "string")))
  })
  @PutMapping("/{id}")
  public ResponseEntity<Object> updatePermission(final @PathVariable long id, @RequestBody PermissionDTO permissionDTO) {
    log.trace("> updatePermission: id={} permissionDTO={}", id, permissionDTO);

    try {
      // Check access
      // First, get the orga from the role
      var orgaId = roleService.getRoleById(permissionDTO.getRoleId()).getOrgaId();
      permissionEvaluator.mayUpdate(orgaId, AppConfig.ITEMTYPE_PERMISSION, id);
      
      permissionDTO.setId(id);
      PermissionDTO newPermissionDTO = permissionService.updatePermission(permissionDTO);
      log.trace("< updatePermission: ok");
      return new ResponseEntity<>(newPermissionDTO, HttpStatus.OK);
    } catch (CollaborationException e) {
      log.error("< updatePermission: error={}", e.getMessage(), e);
      if (e.getExceptionReason() == CollaborationExceptionReason.ACCESS_DENIED) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
      }
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error("< updatePermission: error={}", e.getMessage(), e);
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Delete permission")
  @SecurityRequirement(name = "basicAuth")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "ok", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"))),
    @ApiResponse(responseCode = "400", description = "failed", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"))),
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(type = "string")))
  })
  @DeleteMapping("/{id}")
  public ResponseEntity<Object> deletePermission(final @PathVariable long id) {
    log.trace("> deletePermission: id={}", id);

    try {
      PermissionDTO permissionDTO = permissionService.getPermissionById(id);

      // Check access
      // First, get the orga from the role
      var orgaId = roleService.getRoleById(permissionDTO.getRoleId()).getOrgaId();
      permissionEvaluator.mayDelete(orgaId, AppConfig.ITEMTYPE_PERMISSION, id);

      permissionService.deletePermission(id);
      log.trace("< deletePermission: ok");
      return new ResponseEntity<>(String.format("Permission id=%d deleted successfully", id), HttpStatus.OK);
    } catch (CollaborationException e) {
      log.error("< deletePermission: error={}", e.getMessage(), e);
      if (e.getExceptionReason() == CollaborationExceptionReason.ACCESS_DENIED) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
      }
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error("< deletePermission: error={}", e.getMessage(), e);
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Retrieve permission")
  @SecurityRequirement(name = "basicAuth")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "ok", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PermissionDTO.class))),
    @ApiResponse(responseCode = "400", description = "failed", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"))),
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(type = "string")))
  })
  @GetMapping("/{id}")
  public ResponseEntity<Object> retrievePermission(final @PathVariable long id) {
    log.trace("> retrievePermission: id={}", id);

    try {
      PermissionDTO permissionDTO = permissionService.getPermissionById(id);

      // Check access
      // First, get the orga from the role
      var orgaId = roleService.getRoleById(permissionDTO.getRoleId()).getOrgaId();
      permissionEvaluator.mayRead(orgaId, AppConfig.ITEMTYPE_PERMISSION, id);

      log.trace("< retrievePermission: ok");
      return new ResponseEntity<>(permissionDTO, HttpStatus.OK);
    } catch (CollaborationException e) {
      log.error("< retrievePermission: error={}", e.getMessage(), e);
      if (e.getExceptionReason() == CollaborationExceptionReason.ACCESS_DENIED) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
      }
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error("< retrievePermission: error={}", e.getMessage(), e);
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Retrieve all permissions")
  @SecurityRequirement(name = "basicAuth")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "ok", content = @Content(mediaType = "application/json", schema = @Schema(type = "array", implementation = PermissionDTO.class))),
    @ApiResponse(responseCode = "400", description = "failed", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"))),
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(type = "string")))
  })
  @GetMapping("/by-orgaid/{orgaid}")
  public ResponseEntity<Object> retrieveAllPermissions(final @PathVariable long orgaid) {
    log.trace("> retrieveAllPermissions: orgaid={}", orgaid);

    try {
      // Check access
      permissionEvaluator.mayRead(orgaid, AppConfig.ITEMTYPE_PERMISSION, null);
      
      List<PermissionDTO> permissions = permissionService.getAllPermissionsByOrgaId(orgaid);
      log.trace("< retrieveAllPermissions: ok");
      return new ResponseEntity<>(permissions, HttpStatus.OK);
    } catch (CollaborationException e) {
      log.error("< retrieveAllPermissions: error={}", e.getMessage(), e);
      if (e.getExceptionReason() == CollaborationExceptionReason.ACCESS_DENIED) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
      }
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error("< retrieveAllPermissions: error={}", e.getMessage(), e);
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
