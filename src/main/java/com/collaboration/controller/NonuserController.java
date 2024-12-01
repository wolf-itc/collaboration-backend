/**
 *  NonuserController
 *
 *  @author Martin Wolf
 *  
 *  (C) 2024 Claus Hansen & Martin Wolf IT-Consulting (www.wolf-itc.de)
 * ***************************************************************************/
package com.collaboration.controller;

import java.util.ArrayList;
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
import com.collaboration.config.CollaborationException;
import com.collaboration.config.CollaborationException.CollaborationExceptionReason;
import com.collaboration.model.NonuserDTO;
import com.collaboration.service.NonuserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@Tag(name = "Nonusers (that means groups and other things) Management", description = "APIs for managing non-users")
@RequestMapping("/v1/nonusers")
public class NonuserController {

  private final NonuserService nonuserService;
  private final PermissionEvaluator permissionEvaluator;
  
  public NonuserController(final NonuserService nonuserService, final PermissionEvaluator permissionEvaluator) {
    this.nonuserService = nonuserService;
    this.permissionEvaluator=  permissionEvaluator;
  }

  @Operation(summary = "Create new nonuser")
  @SecurityRequirement(name = "basicAuth")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "201", description = "ok", content = @Content(mediaType = "application/json", schema = @Schema(implementation = NonuserDTO.class))),
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(type = "string")))
  })
  @PostMapping
  public ResponseEntity<Object> createNonuser(@RequestBody NonuserDTO nonuserDTO) {
    try {
      // Check access
      permissionEvaluator.mayCreate(nonuserDTO.getOrgaId(), nonuserDTO.getItemtypeId());
      
      nonuserDTO = nonuserService.createNonuser(nonuserDTO);
      log.info("Nonuser created successfully");
      return new ResponseEntity<>(nonuserDTO, HttpStatus.CREATED);
    } catch (CollaborationException e) {
      log.error(e.getMessage());
      if (e.getExceptionReason() == CollaborationExceptionReason.ACCESS_DENIED) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
      }
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Update nonuser")
  @SecurityRequirement(name = "basicAuth")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "ok", content = @Content(mediaType = "application/json", schema = @Schema(implementation = NonuserDTO.class))),
    @ApiResponse(responseCode = "400", description = "failed", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"))),
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(type = "string")))
  })
  @PutMapping("/{id}")
  public ResponseEntity<Object> updateNonuser(final @PathVariable long id, @RequestBody NonuserDTO nonuserDTO) {
    try {
      // Check access
      NonuserDTO currentNonuserDTO = nonuserService.getNonuserById(id);
      permissionEvaluator.mayUpdate(currentNonuserDTO.getOrgaId(), currentNonuserDTO.getItemtypeId(), id);
      
      nonuserDTO.setId(id);
      nonuserDTO = nonuserService.updateNonuser(nonuserDTO);
      log.info("Nonuser updated successfully");
      return new ResponseEntity<>(nonuserDTO, HttpStatus.OK);
    } catch (CollaborationException e) {
      log.error(e.getMessage());
      if (e.getExceptionReason() == CollaborationExceptionReason.ACCESS_DENIED) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
      }
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Delete a Nonuser")
  @SecurityRequirement(name = "basicAuth")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "ok"),
    @ApiResponse(responseCode = "400", description = "failed", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"))),
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
  })
  @DeleteMapping("/{id}")
  public ResponseEntity<Object> deleteNonuser(@PathVariable long id) {
    try {
      // Check access
      NonuserDTO nonuserDTO = nonuserService.getNonuserById(id);
      permissionEvaluator.mayDelete(nonuserDTO.getOrgaId(), nonuserDTO.getItemtypeId(), id);

      nonuserService.deleteNonuser(id);
      log.info("Nonuser deleted successfully");
      return new ResponseEntity<>("Nonuser deleted successfully", HttpStatus.OK);
    } catch (CollaborationException e) {
      log.error(e.getMessage());
      if (e.getExceptionReason() == CollaborationExceptionReason.ACCESS_DENIED) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
      }
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Retrieve nonuser")
  @SecurityRequirement(name = "basicAuth")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "ok", content = @Content(mediaType = "application/json", schema = @Schema(implementation = NonuserDTO.class))),
    @ApiResponse(responseCode = "400", description = "failed", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"))),
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(type = "string")))
  })
  @GetMapping("/{id}")
  public ResponseEntity<Object> retrieveNonuser(final @PathVariable long id) {
    try {
      NonuserDTO nonuserDTO = nonuserService.getNonuserById(id);

      // Check access
      permissionEvaluator.mayRead(nonuserDTO.getOrgaId(), nonuserDTO.getItemtypeId(), id);

      log.info("Nonuser retrieved successfully");
      return new ResponseEntity<>(nonuserDTO, HttpStatus.OK);
    } catch (CollaborationException e) {
      log.error(e.getMessage());
      if (e.getExceptionReason() == CollaborationExceptionReason.ACCESS_DENIED) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
      }
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Retrieve all non-users for an organization")
  @SecurityRequirement(name = "basicAuth")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "ok", content = @Content(mediaType = "application/json", schema = @Schema(type = "array", implementation = NonuserDTO.class))),
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(type = "string")))
  })
  @GetMapping("/by-orgaid/{orgaid}")
  public ResponseEntity<Object> retrieveAllNonusers(@Parameter(description = "The orgaId here is not the orgaId in the nonuser itself, it is the orgaId connected over item2orga") final @PathVariable long orgaid) {
    try {
      List<NonuserDTO> nonuserDTOs = nonuserService.getAllNonusersByOrgaId(orgaid);

      // Check access for each found nonuser
      var nonusersAllowed = new ArrayList<NonuserDTO>();
      for ( NonuserDTO nonuserDTO: nonuserDTOs) {
        try {
          permissionEvaluator.mayRead(nonuserDTO.getItemtypeId(), nonuserDTO.getItemtypeId(), null);
          nonusersAllowed.add(nonuserDTO);
        } catch ( Exception ex ) {
          log.error(ex.getMessage());
        }
      }
      
      log.info("Nonusers retrieved successfully");
      return new ResponseEntity<>(nonusersAllowed, HttpStatus.OK);
    } catch (Exception e) {
      log.error(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Retrieve a Nonuser by name")
  @SecurityRequirement(name = "basicAuth")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "ok", content = @Content(mediaType = "application/json", schema = @Schema(implementation = NonuserDTO.class))),
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
  })
  @GetMapping("/by-name/{name}")
  public ResponseEntity<Object> retrieveNonusersByName(@PathVariable String name) {
    try {
      List<NonuserDTO> nonuserDTOs = nonuserService.getNonusersByName(name);

      // Check access for each found nonuser
      var nonusersAllowed = new ArrayList<NonuserDTO>();
      for ( NonuserDTO nonuserDTO: nonuserDTOs) {
        try {
          permissionEvaluator.mayRead(nonuserDTO.getItemtypeId(), nonuserDTO.getItemtypeId(), null);
          nonusersAllowed.add(nonuserDTO);
        } catch ( Exception ex ) {}
      }
      
      log.info("Nonusers retrieved for name: {}", name);
      return new ResponseEntity<>(nonusersAllowed, HttpStatus.OK);
    } catch (Exception e) {
      log.error(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Retrieve all nonusers by userId")
  @SecurityRequirement(name = "basicAuth")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "ok", content = @Content(mediaType = "application/json", schema = @Schema(implementation = NonuserDTO.class))),
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
  })
  @GetMapping("/by-userId/{userId}")
  public ResponseEntity<Object> retrieveNonusersByuserId(@PathVariable int userId) {
    try {
      List<NonuserDTO> nonuserDTOs = nonuserService.getNonusersByuserId(userId);

      // Check access for each found nonuser
      var nonusersAllowed = new ArrayList<NonuserDTO>();
      for ( NonuserDTO nonuserDTO: nonuserDTOs) {
        try {
          permissionEvaluator.mayRead(nonuserDTO.getItemtypeId(), nonuserDTO.getItemtypeId(), null);
          nonusersAllowed.add(nonuserDTO);
        } catch ( Exception ex ) {}
      }
      
      log.info("Nonusers retrieved for userId: {}", userId);
      return new ResponseEntity<>(nonusersAllowed, HttpStatus.OK);
    } catch (Exception e) {
      log.error(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
