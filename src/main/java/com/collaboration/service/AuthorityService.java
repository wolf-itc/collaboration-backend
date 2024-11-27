/**
 *  AuthorityService
 *
 *  @author Martin Wolf
 *  
 *  (C) 2024 Claus Hansen & Martin Wolf IT-Consulting (www.wolf-itc.de)
 * ***************************************************************************/
package com.collaboration.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.collaboration.config.CollaborationException;
import com.collaboration.model.Authority;
import com.collaboration.model.AuthorityDTO;
import com.collaboration.model.AuthorityRepository;
import com.collaboration.model.User;
import com.collaboration.model.UserRepository;

@Service
public class AuthorityService {

    private final ModelMapper modelMapper = new ModelMapper();
    private final AuthorityRepository authorityRepository;
    private final UserRepository userRepository;

    public AuthorityService(final AuthorityRepository authorityRepository, final UserRepository userRepository) {
      this.authorityRepository = authorityRepository;
      this.userRepository = userRepository;
    }

    public AuthorityDTO createAuthority(final AuthorityDTO authority) throws CollaborationException {
      User user = userRepository.findByUsername(authority.getUsername()).stream().findFirst().orElseThrow(() -> new CollaborationException(CollaborationException.CollaborationExceptionReason.UNKNOWN_USERNAME));

      Authority newAuthority = convertFromDTO(authority);
      newAuthority.setId(user.getId());
      
      return convertToDTO(authorityRepository.save(newAuthority));
    }

    public AuthorityDTO updateAuthority(final AuthorityDTO authority) throws CollaborationException {
      // Check if exists
      authorityRepository.findById(authority.getId()).orElseThrow(() -> new CollaborationException(CollaborationException.CollaborationExceptionReason.NOT_FOUND));
      
      return convertToDTO(authorityRepository.save(convertFromDTO(authority)));
    }

    public void deleteAuthority(final long id) throws CollaborationException {
      // Check if exists
      authorityRepository.findById(id).orElseThrow(() -> new CollaborationException(CollaborationException.CollaborationExceptionReason.NOT_FOUND));
      
      authorityRepository.deleteById(id);
    }

    public List<AuthorityDTO> getAllAuthorities() {
      return authorityRepository.findAll().stream().map( i -> convertToDTO(i)).collect(Collectors.toList());
    }

    public AuthorityDTO getAuthorityById(final long id) throws CollaborationException {
      Authority authority = authorityRepository.findById(id).orElseThrow(() -> new CollaborationException(CollaborationException.CollaborationExceptionReason.NOT_FOUND));
      return convertToDTO(authority);
    }

    public List<AuthorityDTO> getAuthoritiesByUserId(final long userid) {
        return authorityRepository.findByUserid(userid).stream().map( i -> convertToDTO(i)).collect(Collectors.toList());
    }

    private Authority convertFromDTO(final AuthorityDTO authorityDTO) {
        Authority authority = modelMapper.map(authorityDTO, Authority.class);
        return authority;
    }

    private AuthorityDTO convertToDTO(final Authority authority) {
      AuthorityDTO authorityDTO = modelMapper.map(authority, AuthorityDTO.class);
      return authorityDTO;
  }
}