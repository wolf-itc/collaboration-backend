/**
 *  MainController
 *
 *  @author Martin Wolf
 *  
 *  (C) 2024 Claus Hansen & Martin Wolf IT-Consulting (www.wolf-itc.de)
 * ***************************************************************************/
package com.collaboration.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import com.collaboration.config.EmailServiceImpl;
import com.collaboration.model.OrganizationDTO;
import com.collaboration.model.RoleDTO;
import com.collaboration.model.UserDTO;
import com.collaboration.service.ItemService;
import com.collaboration.service.UserService;

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
@Tag(name = "User Management", description = "APIs for managing users")
@RequestMapping("/v1/users")
public class UserController {

  private final UserService userService;
  private final ItemService itemService;
  private final EmailServiceImpl emailService;
  private final PermissionEvaluator permissionEvaluator;

  public UserController(final UserService userService, final ItemService itemService, 
      final EmailServiceImpl emailService, final PermissionEvaluator permissionEvaluator) {
    this.userService = userService;
    this.itemService = itemService;
    this.emailService = emailService;
    this.permissionEvaluator = permissionEvaluator;
}

  // APIs for guests
  // ----------------------------------------------------------

  @Operation(summary = "Create new user")
  @SecurityRequirement(name = "basicAuth")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "ok", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))),
      @ApiResponse(responseCode = "400", description = "failed", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"))),
      @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(type = "string")))
  })
  @PostMapping
  public ResponseEntity<Object> createUser(@RequestBody UserDTO userDTO) {
    log.trace("> createUser: userDTO={}", userDTO);

    try {
      // Ensure that organization-id is given
      if (userDTO.getOrgaId() == 0) {
        throw new CollaborationException(CollaborationException.CollaborationExceptionReason.INVALID_VALUE);
      }
      // Check access
      permissionEvaluator.mayCreate(userDTO.getOrgaId(), AppConfig.ITEMTYPE_USER);

      userDTO = userService.createUser(userDTO);
      try {
        emailService.sendSimpleMessage(userDTO.getEmail(), userDTO.getMailSubject(), userDTO.getMailBody());
      } catch (Exception e) {
        userService.deleteAccount(userDTO.getActivationkey());
        var msg = String.format("eMail could not be send, user could not be created! Exception was '%s'", e.getMessage());
        log.error(msg);
        return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
      }
      log.trace("< createUser: ok");
      return new ResponseEntity<>(userDTO, HttpStatus.OK);
    } catch (CollaborationException e) {
      log.error("< createUser: error={}", e.getMessage(), e);
      if (e.getExceptionReason() == CollaborationExceptionReason.ACCESS_DENIED) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
      }
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error("< createUser: error={}", e.getMessage(), e);
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Update user")
  @SecurityRequirement(name = "basicAuth")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "ok", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))),
    @ApiResponse(responseCode = "400", description = "failed", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"))),
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(type = "string")))
  })
  @PutMapping("/{id}")
  public ResponseEntity<Object> updateUser(final @PathVariable long id, @RequestBody UserDTO userDTO) {
    log.trace("> updateUser: userDTO={}", userDTO);

    try {
      // Check access. Only the user himself is allowed to update himself
      if (userService.getCurrentUserId() != id) {
        throw(new CollaborationException(CollaborationException.CollaborationExceptionReason.ACCESS_DENIED));
      }
      
      if (id != 0) {
        userDTO.setId(id);
      }
      userDTO = userService.updateUser(userDTO);
      log.trace("< updateUser: ok");
      return new ResponseEntity<>(userDTO, HttpStatus.OK);
    } catch (CollaborationException e) {
      log.error("< updateUser: error={}", e.getMessage(), e);
      if (e.getExceptionReason() == CollaborationExceptionReason.ACCESS_DENIED) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
      }
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error("< updateUser: error={}", e.getMessage(), e);
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Delete user")
  @SecurityRequirement(name = "basicAuth")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "ok", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"))),
    @ApiResponse(responseCode = "400", description = "failed", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"))),
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(type = "string")))
  })
  @DeleteMapping("/{activationkey}")
  public ResponseEntity<Object> deleteAccount(final @PathVariable("activationkey") String activationKey) {
    log.trace("> deleteAccount: activationKey={}", activationKey);

    try {
      // Access has here, whoever sends the activationkey
      
      UserDTO userDTO = userService.deleteAccount(activationKey);
      log.trace("< deleteAccount: ok");
      return new ResponseEntity<>(String.format("User name=%s deleted successfully", userDTO.getUsername()), HttpStatus.OK);
    } catch (CollaborationException e) {
      log.error("< deleteAccount: error={}", e.getMessage(), e);
      if (e.getExceptionReason() == CollaborationExceptionReason.ACCESS_DENIED) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
      }
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error("< deleteAccount: error={}", e.getMessage(), e);
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Retrieve user")
  @SecurityRequirement(name = "basicAuth")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "ok", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RoleDTO.class))),
    @ApiResponse(responseCode = "400", description = "failed", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"))),
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(type = "string")))
  })
  @GetMapping("/{id}")
  public ResponseEntity<Object> retrieveUser(final @PathVariable("id") long id) {
    log.trace("> retrieveUser: id={}", id);

    try {
      UserDTO userDTO = userService.getUserById(id);

      checkAccess(id);
      
      log.trace("< retrieveUser: ok");
      return new ResponseEntity<>(userDTO, HttpStatus.OK);
    } catch (CollaborationException e) {
      log.error("< retrieveUser: error={}", e.getMessage(), e);
      if (e.getExceptionReason() == CollaborationExceptionReason.ACCESS_DENIED) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
      }
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error("< retrieveUser: error={}", e.getMessage(), e);
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Retrieve all users")
  @SecurityRequirement(name = "basicAuth")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "ok", content = @Content(mediaType = "application/json", schema = @Schema(type = "array", implementation = UserDTO.class))),
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(type = "string")))
  })
  @GetMapping
  public ResponseEntity<Object> retrieveAllUsers() {
    log.trace("> retrieveAllUsers");

    try {
      List<UserDTO> userDTOs = userService.getAllUser();

      // Check access for each found itemtypes
      var currentUserOrgaIds = itemService.getItemByUserId(userService.getCurrentUserId(), List.of(ItemService.SubItems.ORGANIZATIONS)).getOrganizationDTOs().stream().map(OrganizationDTO::getId).toList();
      var usersAllowed = new ArrayList<UserDTO>();
      for ( UserDTO userDTO: userDTOs) {
        try {
          var accessAllowed = false;
          var userOrgaIds = itemService.getItemByUserId(userDTO.getId(), List.of(ItemService.SubItems.ORGANIZATIONS)).getOrganizationDTOs().stream().map(OrganizationDTO::getId).toList();
          for (long orgaId: currentUserOrgaIds) {
            if (userOrgaIds.contains(orgaId)) {
              try {
                permissionEvaluator.mayRead(orgaId, AppConfig.ITEMTYPE_USER, userDTO.getId());
                accessAllowed = true;
              } catch (Exception ex) {}
            }
          }
          if (accessAllowed) {
            usersAllowed.add(userDTO);
          }
        } catch ( Exception ex ) {
          log.error(ex.getMessage());
        }
      }
      
      log.trace("< retrieveAllUsers: ok");
      return new ResponseEntity<>(usersAllowed, HttpStatus.OK);
    } catch (Exception e) {
      log.error("< retrieveAllUsers: error={}", e.getMessage(), e);
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "User login (only username and password has to be set here)")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "ok", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))),
    @ApiResponse(responseCode = "400", description = "failed", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"))),
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(type = "string")))
  })
  @PostMapping("/login")
  public ResponseEntity<Object> doLogin(@RequestBody UserDTO userDTO) {
    log.trace("> doLogin: userDTO={}", userDTO);

    try {
      userDTO = userService.doLogin(userDTO);
      log.trace("< doLogin: ok");
      return new ResponseEntity<>(userDTO, HttpStatus.OK);
    } catch (CollaborationException e) {
      log.error("< doLogin: error={}", e.getMessage(), e);
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error("< doLogin: error={}", e.getMessage(), e);
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Prepare password reset")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "204", description = "ok"),
    @ApiResponse(responseCode = "400", description = "failed", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"))),
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(type = "string")))
  })
  @PostMapping("/prepare-password-reset")
  public ResponseEntity<Object> preparePasswordReset(final @RequestBody UserDTO userDTO) {
    log.trace("> preparePasswordReset: userDTO={}", userDTO);

    try {
      userService.preparePasswordReset(userDTO);
      emailService.sendSimpleMessage(userDTO.getEmail(), userDTO.getMailSubject(), userDTO.getMailBody());
      log.trace("< preparePasswordReset: ok");
      return ResponseEntity.noContent().build();
    } catch (CollaborationException e) {
      log.error("< preparePasswordReset: error={}", e.getMessage(), e);
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error("< preparePasswordReset: error={}", e.getMessage(), e);
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Activate account")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "204", description = "ok"),
    @ApiResponse(responseCode = "400", description = "failed", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"))),
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(type = "string")))
  })
  @PostMapping("/activate-account")
  public ResponseEntity<Object> activateAccount(final @RequestBody Map<String,String> params) {
    log.trace("> activateAccount: params={}", params);

    try {
      // Access has here, whoever sends the activationkey
      
      userService.activateAccount(params.get("activationKey"));
      log.trace("< activateAccount: ok");
      return ResponseEntity.noContent().build();
    } catch (CollaborationException e) {
      log.error("< activateAccount: error={}", e.getMessage(), e);
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error("< activateAccount: error={}", e.getMessage(), e);
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Reset password")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "ok (returning a generated temp-password)", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"))),
    @ApiResponse(responseCode = "400", description = "failed", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"))),
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(type = "string")))
  })
  @PostMapping("/reset-password")
  public ResponseEntity<Object> resetPassword(final @RequestBody Map<String,String> params) {
    log.trace("> resetPassword: params={}", params);

    try {
      // Access has here, whoever sends the activationkey
      
      String tempPassword = userService.resetPassword(params.get("activationKey"));
      log.trace("< resetPassword: ok");
      return new ResponseEntity<>(tempPassword, HttpStatus.OK);
    } catch (CollaborationException e) {
      log.error("< resetPassword: error={}", e.getMessage(), e);
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error("< resetPassword: error={}", e.getMessage(), e);
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Change password")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "204", description = "ok"),
    @ApiResponse(responseCode = "400", description = "failed", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"))),
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(type = "string")))
  })
  @PostMapping("/change-password")
  public ResponseEntity<Object> changePassword(final @RequestBody UserDTO userDTO) {
    log.trace("> changePassword: userDTO={}", userDTO);

    try {
      // Check access. Only the user himself is allowed to update himself
      if (userService.getCurrentUserId() != userDTO.getId()) {
        throw(new CollaborationException(CollaborationException.CollaborationExceptionReason.ACCESS_DENIED));
      }
      
      userService.changePassword(userDTO);
      log.trace("< changePassword: ok");
      return ResponseEntity.noContent().build();
    } catch (CollaborationException e) {
      log.error("< changePassword: error={}", e.getMessage(), e);
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error("< changePassword: error={}", e.getMessage(), e);
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
  
  @Operation(summary = "Remove user from given organization")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "204", description = "ok", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"))),
    @ApiResponse(responseCode = "400", description = "failed", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"))),
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(type = "string")))
  })
  @PostMapping("/leave-organization")
  public ResponseEntity<Object> leaveOrganization(final @RequestBody UserDTO userDTO) {
    log.trace("> leaveOrganization: userDTO={}", userDTO);

    try {
      // Check access. Only the user himself is allowed to update himself
      if (userService.getCurrentUserId() != userDTO.getId()) {
        throw(new CollaborationException(CollaborationException.CollaborationExceptionReason.ACCESS_DENIED));
      }
      
      userService.leaveOrganization(userDTO);
      log.trace("< leaveOrganization: ok");
      return ResponseEntity.noContent().build();
    } catch (CollaborationException e) {
      log.error("< leaveOrganization: error={}", e.getMessage(), e);
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error("< leaveOrganization: error={}", e.getMessage(), e);
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Join user to given organization")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "204", description = "ok", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"))),
    @ApiResponse(responseCode = "400", description = "failed", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"))),
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(type = "string")))
  })
  @PostMapping("/join-organization")
  public ResponseEntity<Object> joinOrganization(final @RequestBody UserDTO userDTO) {
    log.trace("> joinOrganization: userDTO={}", userDTO);

    try {
      // Check access. Only the user himself is allowed to update himself
      if (userService.getCurrentUserId() != userDTO.getId()) {
        throw(new CollaborationException(CollaborationException.CollaborationExceptionReason.ACCESS_DENIED));
      }
      
      userService.joinOrganization(userDTO);
      log.trace("< joinOrganization: ok");
      return ResponseEntity.noContent().build();
    } catch (CollaborationException e) {
      log.error("< joinOrganization: error={}", e.getMessage(), e);
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error("< joinOrganization: error={}", e.getMessage(), e);
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Retrieve all roles for given user")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "ok", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"))),
    @ApiResponse(responseCode = "400", description = "failed", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"))),
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(type = "string")))
  })
  @GetMapping("/{id}/roles")
  public ResponseEntity<Object> retrieveUserRoles(final @PathVariable("id") long id) {
    log.trace("> retrieveUserRoles: id={}", id);

    try {
      checkAccess(id);

      var userRoles = itemService.getItemByUserId(id, List.of(ItemService.SubItems.ROLES)).getRoleDTOs().stream().map(RoleDTO::getRolename).toList();

      log.trace("< retrieveUserRoles: userRoles={}", userRoles);
      return new ResponseEntity<>(userRoles, HttpStatus.OK);
    } catch (CollaborationException e) {
      log.error("< retrieveUserRoles: error={}", e.getMessage(), e);
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error("< retrieveUserRoles: error={}", e.getMessage(), e);
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
  
  private void checkAccess(final long userId) throws CollaborationException {
    log.trace("> checkAccess: userId={}", userId);

    // Check access
    var accessAllowed = false;
    if (userService.getCurrentUserId() == userId) {
      
      // The current user may read himself
      accessAllowed = true;
    } else {
  
      // If another user tries to read this user, then it is only possible if the both are in the same orga
      var currentUserOrgaIds = itemService.getItemByUserId(userService.getCurrentUserId(), List.of(ItemService.SubItems.ORGANIZATIONS)).getOrganizationDTOs().stream().map(OrganizationDTO::getId).toList();
      var userOrgaIds = itemService.getItemByUserId(userId, List.of(ItemService.SubItems.ORGANIZATIONS)).getOrganizationDTOs().stream().map(OrganizationDTO::getId).toList();
      for (long orgaId: currentUserOrgaIds) {
        if (userOrgaIds.contains(orgaId)) {
          try {
            permissionEvaluator.mayRead(orgaId, AppConfig.ITEMTYPE_USER, userId);
            accessAllowed = true;
          } catch (Exception ex) {}
        }
      }
    }
    if (!accessAllowed) {
      log.error("< checkAccess: accessDenied");
      throw(new CollaborationException(CollaborationException.CollaborationExceptionReason.ACCESS_DENIED));
    }

    log.trace("< checkAccess: ok");
  }
}
