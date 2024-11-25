/**
 *  PermissionController
 *
 *  @author Martin Wolf
 *  
 *  (C) 2024 Claus Hansen & Martin Wolf IT-Consulting (www.wolf-itc.de)
 * ***************************************************************************/
package com.collaboration.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.collaboration.model.PermissionDTO;
import com.collaboration.service.PermissionService;

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

  @Autowired
  private PermissionService permissionService;

  @Operation(summary = "Create new permission")
  @SecurityRequirement(name = "basicAuth")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "201", description = "ok", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PermissionDTO.class))),
    @ApiResponse(responseCode = "400", description = "failed", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"))),
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(type = "string")))
  })
  @PostMapping
  public ResponseEntity<Object> createPermission(@RequestBody PermissionDTO permissionDTO) {
    try {
      permissionDTO = permissionService.createPermission(permissionDTO);
      log.info("ok");
      return new ResponseEntity<>(permissionDTO, HttpStatus.CREATED);
    } catch (CollaborationException e) {
      log.error(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error(e.getMessage());
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
    try {
      permissionDTO.setId(id);
      PermissionDTO newPermissionDTO = permissionService.updatePermission(permissionDTO);
      log.info("ok");
      return new ResponseEntity<>(newPermissionDTO, HttpStatus.OK);
    } catch (CollaborationException e) {
      log.error(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error(e.getMessage());
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
    try {
      permissionService.deletePermission(id);
      log.info("ok");
      return new ResponseEntity<>(String.format("Permission id=%d deleted successfully", id), HttpStatus.OK);
    } catch (CollaborationException e) {
      log.error(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error(e.getMessage());
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
    try {
      PermissionDTO permission = permissionService.getPermissionById(id);
      log.info("ok");
      return new ResponseEntity<>(permission, HttpStatus.OK);
    } catch (CollaborationException e) {
      log.error(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Retrieve all permissions")
  @SecurityRequirement(name = "basicAuth")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "ok", content = @Content(mediaType = "application/json", schema = @Schema(type = "array", implementation = PermissionDTO.class))),
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(type = "string")))
  })
  @GetMapping
  public ResponseEntity<Object> retrieveAllPermissions() {
    try {
      List<PermissionDTO> permissions = permissionService.getAllPermissions();
      log.info("ok");
      return new ResponseEntity<>(permissions, HttpStatus.OK);
    } catch (Exception e) {
      log.error(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
