/**
 *  ItemtypeController
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
import com.collaboration.config.AppConfig;
import com.collaboration.config.CollaborationException;
import com.collaboration.config.CollaborationException.CollaborationExceptionReason;
import com.collaboration.model.ItemtypeDTO;
import com.collaboration.service.ItemtypeService;

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
@Tag(name = "Itemtypes Management", description = "APIs for managing itemtypes")
@RequestMapping("/v1/itemtypes")
public class ItemtypeController {

  private final ItemtypeService itemtypeService;
  private final PermissionEvaluator permissionEvaluator;

  public ItemtypeController(final ItemtypeService itemtypeService, final PermissionEvaluator permissionEvaluator) {
    this.itemtypeService = itemtypeService;
    this.permissionEvaluator = permissionEvaluator;
  }

  @Operation(summary = "Create new itemtype")
  @SecurityRequirement(name = "basicAuth")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "201", description = "ok", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ItemtypeDTO.class))),
    @ApiResponse(responseCode = "400", description = "failed", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"))),
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(type = "string")))
  })
  @PostMapping
  public ResponseEntity<Object> createItemtype(@RequestBody ItemtypeDTO itemtypeDTO) {
    try {
      // Check access
      permissionEvaluator.mayCreate(itemtypeDTO.getOrgaId(), AppConfig.ITEMTYPE_ITEMTYPE);

      itemtypeDTO = itemtypeService.createItemtype(itemtypeDTO);
      log.info("itemtype created successfully");
      return new ResponseEntity<>(itemtypeDTO, HttpStatus.CREATED);
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

  @Operation(summary = "Update itemtype")
  @SecurityRequirement(name = "basicAuth")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "ok", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ItemtypeDTO.class))),
    @ApiResponse(responseCode = "400", description = "failed", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"))),
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(type = "string")))
  })
  @PutMapping("/{id}")
  public ResponseEntity<Object> updateItemtype(final @PathVariable long id, @RequestBody ItemtypeDTO itemtypeDTO) {
    try {
      // Check access
      permissionEvaluator.mayUpdate(itemtypeDTO.getOrgaId(), AppConfig.ITEMTYPE_ITEMTYPE, id);
      
      itemtypeDTO.setId(id);
      itemtypeService.updateItemtype(itemtypeDTO);
      log.info("itemtype updated successfully");
      return new ResponseEntity<>("itemtype updated successfully", HttpStatus.OK);
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

  @Operation(summary = "Delete itemtype")
  @SecurityRequirement(name = "basicAuth")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "ok", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"))),
    @ApiResponse(responseCode = "400", description = "failed", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"))),
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(type = "string")))
  })
  @DeleteMapping("/deleteItemtype/{id}")
  public ResponseEntity<Object> deleteItemtype(final @PathVariable long id) {
    try {
      // Check access
      ItemtypeDTO itemtypeDTO = itemtypeService.getItemtypeById(id);
      permissionEvaluator.mayDelete(itemtypeDTO.getOrgaId(), AppConfig.ITEMTYPE_ITEMTYPE, id);

      itemtypeService.deleteItemtype(id);
      log.info("itemtype deleted successfully");
      return new ResponseEntity<>(String.format("itemtype id=%d deleted successfully", id), HttpStatus.OK);
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

  @Operation(summary = "Retrieve itemtype")
  @SecurityRequirement(name = "basicAuth")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "ok", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ItemtypeDTO.class))),
    @ApiResponse(responseCode = "400", description = "failed", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"))),
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(type = "string")))
  })
  @GetMapping("/{id}")
  public ResponseEntity<Object> retrieveItemtype(final @PathVariable long id) {
    try {
      ItemtypeDTO itemtypeDTO = itemtypeService.getItemtypeById(id);

      // Check access
      permissionEvaluator.mayRead(itemtypeDTO.getOrgaId(), AppConfig.ITEMTYPE_ITEMTYPE, id);

      log.info("itemtype retrieved successfully");
      return new ResponseEntity<>(itemtypeDTO, HttpStatus.OK);
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

  @Operation(summary = "Retrieve all itemtypes")
  @SecurityRequirement(name = "basicAuth")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "ok", content = @Content(mediaType = "application/json", schema = @Schema(type = "array", implementation = ItemtypeDTO.class))),
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(type = "string")))
  })
  @GetMapping("/by-orgaid/{orgaid}")
  public ResponseEntity<Object> retrieveAllItemtypes(final @PathVariable long orgaid) {
    try {
      List<ItemtypeDTO> itemtypeDTOs = itemtypeService.getAllItemtypes();

      // Check access for each found itemtypes
      var itemtypeAllowed = new ArrayList<ItemtypeDTO>();
      for ( ItemtypeDTO itemtypeDTO: itemtypeDTOs) {
        try {
          permissionEvaluator.mayRead(itemtypeDTO.getOrgaId(), AppConfig.ITEMTYPE_ITEMTYPE, itemtypeDTO.getId());
          itemtypeAllowed.add(itemtypeDTO);
        } catch ( Exception ex ) {
          log.error(ex.getMessage());
        }
      }
      
      log.info("Itemtypes retrieved successfully");
      return new ResponseEntity<>(itemtypeAllowed, HttpStatus.OK);
    } catch (Exception e) {
      log.error(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
