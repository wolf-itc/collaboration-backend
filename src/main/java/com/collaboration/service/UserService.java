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
import java.util.Set;
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
import com.collaboration.model.Item2OrgaDTO;
import com.collaboration.model.Item2OrgaRepository;
import com.collaboration.model.Item2RoleDTO;
import com.collaboration.model.ItemDTO;
import com.collaboration.model.User;
import com.collaboration.model.UserDTO;
import com.collaboration.model.UserRepository;

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

  public UserService(final UserRepository userRepository, final AuthorityRepository authorityRepository, final ItemService itemService, 
      final RoleService roleService, final Item2OrgaRepository item2OrgaRepository, final PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.authorityRepository = authorityRepository;
    this.itemService = itemService;
    this.roleService = roleService;
    this.item2OrgaRepository = item2OrgaRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public UserDTO createUser(final UserDTO userDTO) throws CollaborationException {
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

    // Retrieve the 'USER'-role for current organization
    final var roleUser = roleService.getRoleByRolenameAndOrgaId("USER", userDTO.getOrgaId());
    
    // Also all users are items automatically
    ItemDTO item = new ItemDTO(0, AppConfig.ITEMTYPE_USER, user.getId());
    if (userDTO.getOrgaId() != 0) {
      Item2OrgaDTO item2orga = new Item2OrgaDTO(0L, item.getId(), userDTO.getOrgaId());
      item2orga.setItem(item);
      item.setItem2Orgas(Set.of(item2orga));
    }
    // Entry in table 'Authority' is only used for basic login, all other things go via the item2role
    // So give role USER here, too
    Item2RoleDTO item2role = new Item2RoleDTO(0L, item.getId(), roleUser.getId());
    item.setItem2Roles(Set.of(item2role));
    
    item = itemService.createItem(item);

    return convertToDTO(user);
  }

  public UserDTO updateUser(final UserDTO user) throws CollaborationException {
    // Retrieve current user
    var currentUser = userRepository.findByUsername(user.getUsername()).orElseThrow(() -> new CollaborationException(CollaborationException.CollaborationExceptionReason.UNKNOWN_USERNAME));

    // Verify that username is unique
    Optional<User> maybeExistingOtherUser = Optional.empty();
    if (!user.getCurrentUsername().equals(user.getUsername())) {
      maybeExistingOtherUser = userRepository.findByUsername(user.getUsername());
    }
    if (!maybeExistingOtherUser.isEmpty()) {
      throw new CollaborationException(CollaborationException.CollaborationExceptionReason.USERNAME_ALREADY_EXISTS);
    }

    // Verify that eMail is unique
    maybeExistingOtherUser = userRepository.findByEmail(user.getEmail());
    if (maybeExistingOtherUser.stream().filter(u -> u.getId() != currentUser.getId()).count() > 0) {
      throw new CollaborationException(CollaborationException.CollaborationExceptionReason.EMAIL_ALREADY_EXISTS);
    }

    // These 4 fields cannot be set by the user. They are handles by the application
    user.setPassword(currentUser.getPassword());
    user.setLastLogin(currentUser.getLastLogin());
    user.setLastNotifications(currentUser.getLastNotifications());
    user.setEnabled(true);
    userRepository.save(convertFromDTO(user));
    
    return user;
  }

  public UserDTO deleteAccount(final String activationKey) throws CollaborationException {
    // Check if exists
    var user = userRepository.findByActivationkey(activationKey).orElseThrow(() -> new CollaborationException(CollaborationException.CollaborationExceptionReason.INVALID_ACTIVATIONKEY));

    authorityRepository.deleteAllByUserName(user.getUsername());

    // First delete the item
    itemService.deleteItemByUserId(user.getId());

    // Then the non-user itself
    userRepository.deleteById(user.getId());
    
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

  public String doLogin(final UserDTO user) throws CollaborationException {
    var foundUser = userRepository.findByUsername(user.getUsername()).orElseThrow(() -> new CollaborationException(CollaborationException.CollaborationExceptionReason.UNKNOWN_USERNAME));

    if (!passwordEncoder.matches(user.getPassword(), foundUser.getPassword())) {
      int newFaillogincount = foundUser.getFailloginCount()+ 1;
      foundUser.setFailloginCount(newFaillogincount);
      foundUser.setLastFaillogin(OffsetDateTime.now(ZoneOffset.UTC));
      if (newFaillogincount > MAX_ALLOWED_FAIL_LOGINS) {
        foundUser.setEnabled(false);
      }
      userRepository.save(foundUser);
      if (newFaillogincount > MAX_ALLOWED_FAIL_LOGINS) {
        throw new CollaborationException(CollaborationException.CollaborationExceptionReason.ACCOUNT_HAS_BEEN_LOCKED);
      } else {
        throw new CollaborationException(CollaborationException.CollaborationExceptionReason.WRONG_PASSWORD);
      }
    }

    if (!foundUser.isEnabled()) {
      if (foundUser.getFailloginCount() > MAX_ALLOWED_FAIL_LOGINS) {
        throw new CollaborationException(CollaborationException.CollaborationExceptionReason.ACCOUNT_IS_LOCKED);
      } else {
        throw new CollaborationException(CollaborationException.CollaborationExceptionReason.ACCOUNT_IS_NOT_ENABLED);
      }
    }

    foundUser.setFailloginCount(0);
    foundUser.setLastFaillogin(null);
    foundUser.setLastLogin(OffsetDateTime.now(ZoneOffset.UTC));
    userRepository.save(foundUser);

    return foundUser.getLanguage().trim();
  }

  public void activateAccount(final String activationKey) throws CollaborationException {
    // Check if exists
    var user = userRepository.findByActivationkey(activationKey).orElseThrow(() -> new CollaborationException(CollaborationException.CollaborationExceptionReason.INVALID_ACTIVATIONKEY));

    if (user.getFailloginCount() > MAX_ALLOWED_FAIL_LOGINS) {
      throw new CollaborationException(CollaborationException.CollaborationExceptionReason.ACCOUNT_IS_LOCKED);
    } else {
      user.setEnabled(true);
      user.setActivationkey(activationKey);
      userRepository.save(user);
    }
  }

  public String resetPassword(final String activationKey) throws CollaborationException {
    // Check if exists
    var user = userRepository.findByActivationkey(activationKey).orElseThrow(() -> new CollaborationException(CollaborationException.CollaborationExceptionReason.INVALID_ACTIVATIONKEY));

    user.setEnabled(true);
    String tempPassword = generateTempPassword();
    String enc = passwordEncoder.encode(tempPassword);
    user.setPassword(enc);
    userRepository.save(user);

    return tempPassword;
  }

  public void preparePasswordReset(final UserDTO user) throws CollaborationException {
    // Check if exists
    var foundUser = userRepository.findByEmail(user.getEmail()).orElseThrow(() -> new CollaborationException(CollaborationException.CollaborationExceptionReason.INVALID_ACTIVATIONKEY));

    foundUser.setActivationkey(user.getActivationkey());
    userRepository.save(foundUser);
  }

  public void changePassword(final UserDTO user) throws CollaborationException {
    // Check if exists
    var oldUser = userRepository.findByUsername(user.getUsername()).orElseThrow(() -> new CollaborationException(CollaborationException.CollaborationExceptionReason.UNKNOWN_USERNAME));

    if (!passwordEncoder.matches(user.getOldPassword(), oldUser.getPassword())) {
      throw new CollaborationException(CollaborationException.CollaborationExceptionReason.WRONG_PASSWORD);
    }

    oldUser.setPassword(passwordEncoder.encode(user.getPassword()));
    userRepository.save(oldUser);
  }

  public long getCurrentUserId() throws CollaborationException {
    var username = SecurityContextHolder.getContext().getAuthentication().getName();
    var user = getUserByUsername(username);
    return user.getId();
  }

  public void leaveOrganization(final UserDTO userDTO) throws CollaborationException {
    // Check if exists
    userRepository.findByUsername(userDTO.getUsername()).orElseThrow(() -> new CollaborationException(CollaborationException.CollaborationExceptionReason.NOT_FOUND));
    
    // Delete the link to orga
    item2OrgaRepository.deleteByItemIdAndOrgaId(userDTO.getId(), userDTO.getOrgaId());
  }

  public void joinOrganization(final UserDTO userDTO) throws CollaborationException {
    // Check if exists
    userRepository.findByUsername(userDTO.getUsername()).orElseThrow(() -> new CollaborationException(CollaborationException.CollaborationExceptionReason.NOT_FOUND));
    
    // Insert the link to orga
    var item2orga = new Item2Orga(0, userDTO.getId(), userDTO.getOrgaId());
    item2OrgaRepository.save(item2orga);
  }

  private String generateTempPassword() {
    int leftLimit = 48; // numeral '0'
    int rightLimit = 122; // letter 'z'
    int targetStringLength = 16;
    Random random = new Random();

    String generatedString = random.ints(leftLimit, rightLimit + 1)
        .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
        .limit(targetStringLength)
        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
        .toString();

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
