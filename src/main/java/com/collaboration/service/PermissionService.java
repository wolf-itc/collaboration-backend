/**
 *  PermissionService
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
import com.collaboration.model.Permission;
import com.collaboration.model.PermissionDTO;
import com.collaboration.model.PermissionRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PermissionService {

    private final ModelMapper modelMapper = new ModelMapper();
    
    private final PermissionRepository permissionRepository;

    public PermissionService(final PermissionRepository permissionRepository) {
      this.permissionRepository = permissionRepository;
    }

    public PermissionDTO createPermission(final PermissionDTO permissionDTO) throws CollaborationException {
      log.trace("> createPermission");

      Permission permission = convertFromDTO(permissionDTO);

      log.trace("< createPermission");
      return convertToDTO(permissionRepository.save(permission));
    }

    public PermissionDTO updatePermission(final PermissionDTO permissionDTO) throws CollaborationException {
      log.trace("> updatePermission");

      // Check if exists
      permissionRepository.findById(permissionDTO.getId()).orElseThrow(() -> new CollaborationException(CollaborationException.CollaborationExceptionReason.NOT_FOUND));
      
      log.trace("< updatePermission");
      return convertToDTO(permissionRepository.save(convertFromDTO(permissionDTO)));
    }

    public void deletePermission(final long id) throws CollaborationException {
      log.trace("> deletePermission");

      // Check if exists
      permissionRepository.findById(id).orElseThrow(() -> new CollaborationException(CollaborationException.CollaborationExceptionReason.NOT_FOUND));

      permissionRepository.deleteById(id);

      log.trace("< deletePermission");
    }

    public List<PermissionDTO> getAllPermissions() {
      return permissionRepository.findAll().stream().map( i -> convertToDTO(i)).collect(Collectors.toList());
    }

    public PermissionDTO getPermissionById(final long id) throws CollaborationException {
      Permission permission = permissionRepository.findById(id).orElseThrow(() -> new CollaborationException(CollaborationException.CollaborationExceptionReason.NOT_FOUND));
      return convertToDTO(permission);
    }

    public List<PermissionDTO> getAllPermissionsByOrgaId(final long orgaid) {
      return permissionRepository.findByOrgaId(orgaid).stream().map( i -> convertToDTO(i)).collect(Collectors.toList());
    }

    public List<PermissionDTO> getByItemtypeIdAndItemIdAndRoleIdsIn(final long itemtypeId, final long itemId, final List<Long> roleIds) {
      return permissionRepository.findByItemtypeIdAndItemIdAndRoleIdIn(itemtypeId, itemId, roleIds).stream().map( i -> convertToDTO(i)).collect(Collectors.toList());
    }
    
    public List<PermissionDTO> getByItemtypeIdAndRoleIdsIn(final long itemtypeId, final List<Long> roleIds) {
      return permissionRepository.findByItemtypeIdAndRoleIdIn(itemtypeId, roleIds).stream().map( i -> convertToDTO(i)).collect(Collectors.toList());
    }

    
    private Permission convertFromDTO(final PermissionDTO permissionDTO) {
      return modelMapper.map(permissionDTO, Permission.class);
    }

    private PermissionDTO convertToDTO(final Permission permission) {
      return modelMapper.map(permission, PermissionDTO.class);
    }
}
