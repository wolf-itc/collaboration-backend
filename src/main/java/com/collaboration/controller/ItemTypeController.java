/**
 *  ItemTypeController
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
import com.collaboration.model.ItemTypeDTO;
import com.collaboration.service.ItemTypeService;

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
public class ItemTypeController {

  @Autowired
  private ItemTypeService itemTypeService;

  @Operation(summary = "Create new itemtype")
  @SecurityRequirement(name = "basicAuth")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "201", description = "ok", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ItemTypeDTO.class))),
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(type = "string")))
  })
  @PostMapping
  public ResponseEntity<Object> createItemType(@RequestBody ItemTypeDTO itemTypeDTO) {
    try {
      itemTypeDTO = itemTypeService.createItemType(itemTypeDTO);
      log.info("ItemType created successfully");
      return new ResponseEntity<>(itemTypeDTO, HttpStatus.CREATED);
    } catch (Exception e) {
      log.error(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Update itemtype")
  @SecurityRequirement(name = "basicAuth")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "ok", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ItemTypeDTO.class))),
    @ApiResponse(responseCode = "400", description = "failed", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"))),
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(type = "string")))
  })
  @PutMapping("/{id}")
  public ResponseEntity<Object> updateItemType(final @PathVariable long id, @RequestBody ItemTypeDTO itemTypeDTO) {
    try {
      itemTypeDTO.setId(id);
      itemTypeService.updateItemType(itemTypeDTO);
      log.info("ItemType updated successfully");
      return new ResponseEntity<>("ItemType updated successfully", HttpStatus.OK);
    } catch (CollaborationException e) {
      log.error(e.getMessage());
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
  @DeleteMapping("/deleteItemType/{id}")
  public ResponseEntity<Object> deleteItemType(final @PathVariable long id) {
    try {
      itemTypeService.deleteItemType(id);
      log.info("ItemType deleted successfully");
      return new ResponseEntity<>(String.format("Itemtype id=%d deleted successfully", id), HttpStatus.OK);
    } catch (CollaborationException e) {
      log.error(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Retrieve itemtype")
  @SecurityRequirement(name = "basicAuth")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "ok", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ItemTypeDTO.class))),
    @ApiResponse(responseCode = "400", description = "failed", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"))),
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(type = "string")))
  })
  @GetMapping("/{id}")
  public ResponseEntity<Object> retrieveItemType(final @PathVariable long id) {
    try {
      ItemTypeDTO itemType = itemTypeService.getItemTypeById(id);
      log.info("ItemType retrieved successfully");
      return new ResponseEntity<>(itemType, HttpStatus.OK);
    } catch (CollaborationException e) {
      log.error(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Retrieve all itemtypes")
  @SecurityRequirement(name = "basicAuth")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "ok", content = @Content(mediaType = "application/json", schema = @Schema(type = "array", implementation = ItemTypeDTO.class))),
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(type = "string")))
  })
  @GetMapping
  public ResponseEntity<Object> retrieveAllItemTypes() {
    try {
      List<ItemTypeDTO> itemTypes = itemTypeService.getAllItemTypes();
      log.info("ItemTypes retrieved successfully");
      return new ResponseEntity<>(itemTypes, HttpStatus.OK);
    } catch (Exception e) {
      log.error(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Retrieve all itemtypes by orgaid")
  @SecurityRequirement(name = "basicAuth")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "ok", content = @Content(mediaType = "application/json", schema = @Schema(type = "array", implementation = ItemTypeDTO.class))),
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(type = "string")))
  })
  @GetMapping("/by-orgaid/{orgaid}")
  public ResponseEntity<Object> retrieveItemTypesByOrgaId(final @PathVariable long orgaid) {
    try {
      List<ItemTypeDTO> itemTypes = itemTypeService.getItemTypesByOrgaId(orgaid);
      log.info("ItemTypes retrieved for orgaId: {}", orgaid);
      return new ResponseEntity<>(itemTypes, HttpStatus.OK);
    } catch (Exception e) {
      log.error(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
