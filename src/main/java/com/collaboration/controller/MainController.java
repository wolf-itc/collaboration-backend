/**
 *  MainController
 *
 *  @author Martin Wolf
 *  
 *  (C) 2024 Claus Hansen & Martin Wolf IT-Consulting (www.wolf-itc.de)
 * ***************************************************************************/
package com.collaboration.controller;

import java.util.List;
import java.util.Map;

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
import com.collaboration.config.EmailServiceImpl;
import com.collaboration.model.RoleDTO;
import com.collaboration.model.UserDTO;
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
public class MainController {

  @Autowired
  UserService userService;

  @Autowired
  EmailServiceImpl emailService;

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
    try {
      userDTO = userService.createUser(userDTO);
      try {
        emailService.sendSimpleMessage(userDTO.getEmail(), userDTO.getMailSubject(), userDTO.getMailBody());
      } catch (Exception e) {
        userService.deleteAccount(userDTO.getActivationkey());
        var msg = String.format("eMail could not be send, user could not be created! Exception was '%s'", e.getMessage());
        log.error(msg);
        return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
      }
      log.info("ok");
      return new ResponseEntity<>(userDTO, HttpStatus.OK);
    } catch (CollaborationException e) {
      log.error(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error(e.getMessage());
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
    try {
      if (id != 0) {
        userDTO.setId(id);
      }
      userDTO = userService.updateUser(userDTO);
      log.info("ok");
      return new ResponseEntity<>(userDTO, HttpStatus.OK);
    } catch (CollaborationException e) {
      log.error(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error(e.getMessage());
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
    try {
      UserDTO userDTO = userService.deleteAccount(activationKey);
      log.info("ok");
      return new ResponseEntity<>(String.format("User name=%s deleted successfully", userDTO.getUsername()), HttpStatus.OK);
    } catch (CollaborationException e) {
      log.error(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error(e.getMessage());
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
    try {
      UserDTO u = userService.getUserById(id);
      log.info("ok");
      return new ResponseEntity<>(u, HttpStatus.OK);
    } catch (CollaborationException e) {
      log.error(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error(e.getMessage());
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
    try {
      List<UserDTO> users = userService.getAllUser();
      log.info("Users retrieved successfully");
      return new ResponseEntity<>(users, HttpStatus.OK);
    } catch (Exception e) {
      log.error(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Retrieve user by username")
  @SecurityRequirement(name = "basicAuth")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "ok", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RoleDTO.class))),
    @ApiResponse(responseCode = "400", description = "failed", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"))),
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(type = "string")))
  })
  @GetMapping("/by-username/{username}")
  public ResponseEntity<Object> retrieveUser(final @PathVariable("username") String username) {
    try {
      UserDTO u = userService.getUserByUsername(username);
      log.info("ok");
      return new ResponseEntity<>(u, HttpStatus.OK);
    } catch (CollaborationException e) {
      log.error(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "User login (only username and password has to be set here)")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "ok (returning user's language)", content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "en-US"))),
    @ApiResponse(responseCode = "400", description = "failed", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"))),
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(type = "string")))
  })
  @PostMapping("/login")
  public ResponseEntity<Object> doLogin(final @RequestBody UserDTO user) {
    try {
      String language = userService.doLogin(user);
      log.info("ok");
      return new ResponseEntity<>(language, HttpStatus.OK);
    } catch (CollaborationException e) {
      log.error(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error(e.getMessage());
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
  public ResponseEntity<Object> preparePasswordReset(final @RequestBody UserDTO user) {
    try {
      userService.preparePasswordReset(user);
      emailService.sendSimpleMessage(user.getEmail(), user.getMailSubject(), user.getMailBody());
      log.info("ok");
      return ResponseEntity.noContent().build();
    } catch (CollaborationException e) {
      log.error(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error(e.getMessage());
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
    try {
      userService.activateAccount(params.get("activationKey"));
      return ResponseEntity.noContent().build();
    } catch (CollaborationException e) {
      log.error(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error(e.getMessage());
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
    try {
      String tempPassword = userService.resetPassword(params.get("activationKey"));
      return new ResponseEntity<>(tempPassword, HttpStatus.OK);
    } catch (CollaborationException e) {
      log.error(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error(e.getMessage());
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
  public ResponseEntity<Object> changePassword(final @RequestBody UserDTO user) {
    try {
      if (!user.getUsername().equals("entable")) { // what for that?
        userService.changePassword(user);
        log.info("ok");
        return ResponseEntity.noContent().build();
      } else {
        throw new CollaborationException(CollaborationException.CollaborationExceptionReason.PW_NOT_CHANGEABLE);
      }
    } catch (CollaborationException e) {
      log.error(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error(e.getMessage());
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
    try {
      userService.leaveOrganization(userDTO);
      return ResponseEntity.noContent().build();
    } catch (CollaborationException e) {
      log.error(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Retrieve all role for giben user")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "ok", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"))),
    @ApiResponse(responseCode = "400", description = "failed", content = @Content(mediaType = "application/json", schema = @Schema(type = "string"))),
    @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(type = "string")))
  })
  @GetMapping("/{id}/roles")
  public ResponseEntity<Object> retrieveUserRoles(final @PathVariable("id") long id) {
    try {
      List<String> userRoles = userService.retrieveUserRoles(id);
      return new ResponseEntity<>(userRoles, HttpStatus.OK);
    } catch (CollaborationException e) {
      log.error(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      log.error(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
