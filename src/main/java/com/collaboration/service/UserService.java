/**
 *  UserService
 *
 *  @author Martin Wolf
 *  
 *  (C) 2024 Claus Hansen & Martin Wolf IT-Consulting (www.wolf-itc.de)
 * ***************************************************************************/
package com.collaboration.service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.collaboration.config.AppConfig;
import com.collaboration.config.CollaborationException;
import com.collaboration.model.Authority;
import com.collaboration.model.AuthorityRepository;
import com.collaboration.model.Item2Orga;
import com.collaboration.model.Item2OrgaRepository;
import com.collaboration.model.ItemDTO;
import com.collaboration.model.User;
import com.collaboration.model.UserDTO;
import com.collaboration.model.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService {

  public static final int MAX_ALLOWED_FAIL_LOGINS = 4;

  private ModelMapper modelMapper = new ModelMapper();

  private final UserRepository userRepository;
  private final AuthorityRepository authorityRepository;
  private final ItemService itemService;
  private final RoleService roleService;
  private final Item2OrgaRepository item2OrgaRepository;
  private final PasswordEncoder passwordEncoder;
  private final OrganizationService organizationService;

  public UserService(final UserRepository userRepository, final AuthorityRepository authorityRepository, final ItemService itemService, 
      final RoleService roleService, final OrganizationService organizationService,
      final Item2OrgaRepository item2OrgaRepository, final PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.authorityRepository = authorityRepository;
    this.itemService = itemService;
    this.roleService = roleService;
    this.organizationService= organizationService;
    this.item2OrgaRepository = item2OrgaRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public UserDTO createUser(final UserDTO userDTO) throws CollaborationException {
    log.trace("> createUser");

    // Check if exists
    var userCheck = userRepository.findByUsername(userDTO.getUsername());
    if (!userCheck.isEmpty()) {
      throw new CollaborationException(CollaborationException.CollaborationExceptionReason.USERNAME_ALREADY_EXISTS);
    }

    String enc = passwordEncoder.encode(userDTO.getPassword());
    User user = convertFromDTO(userDTO);
    user.setPassword(enc);
    user.setEnabled(false);
    user.setShowNotifications("D");
    user.setShowClearname(false);
    user.setShowContact(false);
    user.setShowHelp(true);
    user.setShowIntro(false);
    user.setFailloginCount(0);
    userRepository.save(user); 

    // All new users get role USER for default
    Authority authority = new Authority(user.getId(), user.getUsername(), "ROLE_USER");
    authorityRepository.save(authority);

    // Also all users are items automatically
    ItemDTO item = new ItemDTO(0, AppConfig.ITEMTYPE_USER, user.getId());
    if (userDTO.getOrgaId() != 0) {
      var orga = organizationService.getOrganizationById(userDTO.getOrgaId());
      item.setOrganizationDTOs(List.of(orga));
    }
    // Entry in table 'Authority' is only used for basic login, all other things go via the item2role
    // So give role USER here, too
    // Retrieve the 'USER'-role for current organization
    final var roleUser = roleService.getRoleByRolenameAndOrgaId("USER", userDTO.getOrgaId());
    item.setRoleDTOs(List.of(roleUser));
    
    item = itemService.createItem(item);

    log.trace("< createUser");
    return convertToDTO(user);
  }

  public UserDTO updateUser(final UserDTO userDTO) throws CollaborationException {
    log.trace("> updateUser");

    Optional<User> maybeExistingOtherUser = Optional.empty();

    // Retrieve current user
    var currentUser = userRepository.findById(userDTO.getId()).orElseThrow(() -> new CollaborationException(CollaborationException.CollaborationExceptionReason.UNKNOWN_USERNAME));
    if (currentUser.getUsername() != userDTO.getUsername()) {
      // Verify that username is unique
      maybeExistingOtherUser = userRepository.findByUsername(userDTO.getUsername());
      if (!maybeExistingOtherUser.isEmpty()) {
        throw new CollaborationException(CollaborationException.CollaborationExceptionReason.USERNAME_ALREADY_EXISTS);
      }
    }

    if (currentUser.getEmail() != userDTO.getEmail()) {
      // Verify that eMail is unique
      maybeExistingOtherUser = userRepository.findByEmail(userDTO.getEmail());
      if (maybeExistingOtherUser.stream().filter(u -> u.getId() != currentUser.getId()).count() > 0) {
        throw new CollaborationException(CollaborationException.CollaborationExceptionReason.EMAIL_ALREADY_EXISTS);
      }
    }

    // These 4 fields cannot be set by the user. They are handles by the application
    userDTO.setPassword(currentUser.getPassword());
    userDTO.setLastLogin(currentUser.getLastLogin());
    userDTO.setLastNotifications(currentUser.getLastNotifications());
    userDTO.setEnabled(true);
    userRepository.save(convertFromDTO(userDTO));
    
    log.trace("< updateUser");
    return userDTO;
  }

  // TODO: Really hard deletion?
  public UserDTO deleteAccount(final String activationKey) throws CollaborationException {
    log.trace("> deleteAccount");

    // Check if exists
    var user = userRepository.findByActivationkey(activationKey).orElseThrow(() -> new CollaborationException(CollaborationException.CollaborationExceptionReason.INVALID_ACTIVATIONKEY));

    authorityRepository.deleteById(user.getId());

    // First delete the item
    itemService.deleteItemByUserId(user.getId());

    // Then the user itself
    userRepository.deleteById(user.getId());
    
    log.trace("< deleteAccount");
    return convertToDTO(user);
  }

  public UserDTO getUserById(final long id) throws CollaborationException {
    User user = userRepository.findById(id).orElseThrow(() -> new CollaborationException(CollaborationException.CollaborationExceptionReason.NOT_FOUND));
    return convertToDTO(user);
  }

  public UserDTO getUserByUsername(final String username) throws CollaborationException {
    // Check if exists
    var user = userRepository.findByUsername(username).orElseThrow(() -> new CollaborationException(CollaborationException.CollaborationExceptionReason.UNKNOWN_USERNAME));

    return convertToDTO(user);
  }

  public List<UserDTO> getAllUser(){
    return userRepository.findAll().stream().map( i -> convertToDTO(i)).collect(Collectors.toList());
  }

  public UserDTO doLogin(final UserDTO userDTO) throws CollaborationException {
    log.trace("> doLogin");

    var user = userRepository.findByUsername(userDTO.getUsername()).orElseThrow(() -> new CollaborationException(CollaborationException.CollaborationExceptionReason.UNKNOWN_USERNAME));

    if (!passwordEncoder.matches(userDTO.getPassword(), user.getPassword())) {
      int newFaillogincount = user.getFailloginCount()+ 1;
      user.setFailloginCount(newFaillogincount);
      user.setLastFaillogin(OffsetDateTime.now(ZoneOffset.UTC));
      if (newFaillogincount > MAX_ALLOWED_FAIL_LOGINS) {
        user.setEnabled(false);
      }
      userRepository.save(user);
      if (newFaillogincount > MAX_ALLOWED_FAIL_LOGINS) {
        throw new CollaborationException(CollaborationException.CollaborationExceptionReason.ACCOUNT_HAS_BEEN_LOCKED);
      } else {
        throw new CollaborationException(CollaborationException.CollaborationExceptionReason.WRONG_PASSWORD);
      }
    }

    if (!user.isEnabled()) {
      if (user.getFailloginCount() > MAX_ALLOWED_FAIL_LOGINS) {
        throw new CollaborationException(CollaborationException.CollaborationExceptionReason.ACCOUNT_IS_LOCKED);
      } else {
        throw new CollaborationException(CollaborationException.CollaborationExceptionReason.ACCOUNT_IS_NOT_ENABLED);
      }
    }

    user.setFailloginCount(0);
    user.setLastFaillogin(null);
    user.setLastLogin(OffsetDateTime.now(ZoneOffset.UTC));
    userRepository.save(user);

    log.trace("< doLogin");
    return convertToDTO(user);
  }

  public void activateAccount(final String activationKey) throws CollaborationException {
    log.trace("> activateAccount");

    // Check if exists
    var user = userRepository.findByActivationkey(activationKey).orElseThrow(() -> new CollaborationException(CollaborationException.CollaborationExceptionReason.INVALID_ACTIVATIONKEY));

    if (user.getFailloginCount() > MAX_ALLOWED_FAIL_LOGINS) {
      throw new CollaborationException(CollaborationException.CollaborationExceptionReason.ACCOUNT_IS_LOCKED);
    } else {
      user.setEnabled(true);
      user.setActivationkey(activationKey);
      userRepository.save(user);
    }

    log.trace("< activateAccount");
  }

  public String resetPassword(final String activationKey) throws CollaborationException {
    log.trace("> resetPassword");

    // Check if exists
    var user = userRepository.findByActivationkey(activationKey).orElseThrow(() -> new CollaborationException(CollaborationException.CollaborationExceptionReason.INVALID_ACTIVATIONKEY));

    user.setEnabled(true);
    String tempPassword = generateTempPassword();
    String enc = passwordEncoder.encode(tempPassword);
    user.setPassword(enc);
    userRepository.save(user);

    log.trace("< resetPassword");
    return tempPassword;
  }

  public void preparePasswordReset(final UserDTO user) throws CollaborationException {
    log.trace("> preparePasswordReset");

    // Check if exists
    var foundUser = userRepository.findByEmail(user.getEmail()).orElseThrow(() -> new CollaborationException(CollaborationException.CollaborationExceptionReason.INVALID_ACTIVATIONKEY));

    foundUser.setActivationkey(user.getActivationkey());
    userRepository.save(foundUser);

    log.trace("< preparePasswordReset");
  }

  public void changePassword(final UserDTO user) throws CollaborationException {
    log.trace("> changePassword");

    // Check if exists
    var oldUser = userRepository.findByUsername(user.getUsername()).orElseThrow(() -> new CollaborationException(CollaborationException.CollaborationExceptionReason.UNKNOWN_USERNAME));

    if (!passwordEncoder.matches(user.getOldPassword(), oldUser.getPassword())) {
      throw new CollaborationException(CollaborationException.CollaborationExceptionReason.WRONG_PASSWORD);
    }

    oldUser.setPassword(passwordEncoder.encode(user.getPassword()));
    userRepository.save(oldUser);

    log.trace("< changePassword");
  }

  public long getCurrentUserId() throws CollaborationException {
    log.trace("> getCurrentUserId");

    var username = SecurityContextHolder.getContext().getAuthentication().getName();
    var user = getUserByUsername(username);

    log.trace("< getCurrentUserId");
    return user.getId();
  }

  public void leaveOrganization(final UserDTO userDTO) throws CollaborationException {
    // Check if exists
    userRepository.findByUsername(userDTO.getUsername()).orElseThrow(() -> new CollaborationException(CollaborationException.CollaborationExceptionReason.NOT_FOUND));
    
    // Delete the link to orga
    item2OrgaRepository.deleteByItemIdAndOrgaId(userDTO.getId(), userDTO.getOrgaId());
  }

  public void joinOrganization(final UserDTO userDTO) throws CollaborationException {
    log.trace("> joinOrganization");

    // Check if exists
    userRepository.findByUsername(userDTO.getUsername()).orElseThrow(() -> new CollaborationException(CollaborationException.CollaborationExceptionReason.NOT_FOUND));
    
    // Insert the link to orga
    var item2orga = new Item2Orga(0, userDTO.getId(), userDTO.getOrgaId());
    item2OrgaRepository.save(item2orga);

    log.trace("< joinOrganization");
  }

  private String generateTempPassword() {
    log.trace("> generateTempPassword");

    int leftLimit = 48; // numeral '0'
    int rightLimit = 122; // letter 'z'
    int targetStringLength = 16;
    Random random = new Random();

    String generatedString = random.ints(leftLimit, rightLimit + 1)
        .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
        .limit(targetStringLength)
        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
        .toString();

    log.trace("< generateTempPassword");
    return generatedString;
  }

  private UserDTO convertToDTO(final User user) {
   UserDTO userDTO = modelMapper.map(user, UserDTO.class);
   return userDTO;
  }
  
  private User convertFromDTO(final UserDTO userDTO) {
   User user = modelMapper.map(userDTO, User.class);
    return user;
  }
}
