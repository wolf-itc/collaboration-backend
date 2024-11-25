/**
 *  NonUserController
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
import com.collaboration.model.NonUserDTO;
import com.collaboration.service.NonUserService;

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
@Tag(name = "NonUsers (that means groups and other things) Management", description = "APIs for managing non-users")
@RequestMapping("/v1/nonusers")
public class NonUserController {

  @Autowired
  private NonUserService nonUserService;

  @Operation(summary = "Create new nonuser")
  @SecurityRequirement(name = "basicAuth")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "201", description = "ok", content = @Content(mediaType = "application/json", schema = @Schema(implementation = NonUserDTO.class))),
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(type = "string")))
  })
  @PostMapping
  public ResponseEntity<Object> createNonUser(@RequestBody NonUserDTO nonuserDTO) {
    try {
      nonuserDTO = nonUserService.createNonUser(nonuserDTO);
      log.info("NonUser created successfully");
      return new ResponseEntity<>(nonuserDTO, HttpStatus.CREATED);
    } catch (Exception e) {
      log.error(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Update nonuser")
  @SecurityRequirement(name = "basicAuth")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "ok", content = @Content(mediaType = "application/json", schema = @Schema(implementation = NonUserDTO.class))),
    @ApiResponse(responseCode = "400", description = "failed", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"))),
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(type = "string")))
  })
  @PutMapping("/{id}")
  public ResponseEntity<Object> updateNonUser(final @PathVariable long id, @RequestBody NonUserDTO nonuserDTO) {
    try {
      nonuserDTO.setId(id);
      nonuserDTO = nonUserService.updateNonUser(nonuserDTO);
      log.info("NonUser updated successfully");
      return new ResponseEntity<>(nonuserDTO, HttpStatus.OK);
    } catch (CollaborationException e) {
      log.error(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Delete a NonUser")
  @SecurityRequirement(name = "basicAuth")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "ok"),
    @ApiResponse(responseCode = "400", description = "failed", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"))),
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
  })
  @DeleteMapping("/{id}")
  public ResponseEntity<Object> deleteNonUser(@PathVariable long id) {
    try {
      nonUserService.deleteNonUser(id);
      log.info("NonUser deleted successfully");
      return new ResponseEntity<>("NonUser deleted successfully", HttpStatus.OK);
    } catch (CollaborationException e) {
      log.error(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Retrieve nonuser")
  @SecurityRequirement(name = "basicAuth")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "ok", content = @Content(mediaType = "application/json", schema = @Schema(implementation = NonUserDTO.class))),
    @ApiResponse(responseCode = "400", description = "failed", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"))),
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(type = "string")))
  })
  @GetMapping("/{id}")
  public ResponseEntity<Object> retrieveNonUser(final @PathVariable long id) {
  try {
      NonUserDTO nonUserDTO = nonUserService.getNonUserById(id);
      log.info("NonUser retrieved successfully");
      return new ResponseEntity<>(nonUserDTO, HttpStatus.OK);
    } catch (CollaborationException e) {
      log.error(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Retrieve all NonUsers")
  @SecurityRequirement(name = "basicAuth")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "ok", content = @Content(mediaType = "application/json", schema = @Schema(type = "array", implementation = NonUserDTO.class))),
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(type = "string")))
  })
  @GetMapping
  public ResponseEntity<Object> retrieveAllNonUsers() {
    try {
      List<NonUserDTO> nonUsers = nonUserService.getAllNonUsers();
      log.info("NonUsers retrieved successfully");
      return new ResponseEntity<>(nonUsers, HttpStatus.OK);
    } catch (Exception e) {
      log.error(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Retrieve a NonUser by name")
  @SecurityRequirement(name = "basicAuth")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "ok", content = @Content(mediaType = "application/json", schema = @Schema(implementation = NonUserDTO.class))),
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
  })
  @GetMapping("/by-name/{name}")
  public ResponseEntity<Object> retrieveNonUsersByName(@PathVariable String name) {
    try {
      List<NonUserDTO> nonUsers = nonUserService.findNonUsersByName(name);
      log.info("NonUsers retrieved for name: {}", name);
      return new ResponseEntity<>(nonUsers, HttpStatus.OK);
    } catch (Exception e) {
      log.error(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Retrieve all nonusers by createdById")
  @SecurityRequirement(name = "basicAuth")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "ok", content = @Content(mediaType = "application/json", schema = @Schema(implementation = NonUserDTO.class))),
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
  })
  @GetMapping("/by-createdbyid/{createdById}")
  public ResponseEntity<Object> retrieveNonUsersByCreatedById(@PathVariable int createdById) {
    try {
      List<NonUserDTO> nonUsers = nonUserService.findNonUsersByCreatedById(createdById);
      log.info("NonUsers retrieved for createdById: {}", createdById);
      return new ResponseEntity<>(nonUsers, HttpStatus.OK);
    } catch (Exception e) {
      log.error(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
