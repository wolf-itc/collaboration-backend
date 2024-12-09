/**
 *  ItemService
 *
 *  @author Martin Wolf
 *
 *  (C) 2024 Claus Hansen & Martin Wolf IT-Consulting (www.wolf-itc.de)
 * ***************************************************************************/
package com.collaboration.service;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.collaboration.config.AppConfig;
import com.collaboration.config.CollaborationException;
import com.collaboration.model.Item;
import com.collaboration.model.Item2Orga;
import com.collaboration.model.Item2OrgaDTO;
import com.collaboration.model.Item2OrgaRepository;
import com.collaboration.model.Item2Role;
import com.collaboration.model.Item2RoleDTO;
import com.collaboration.model.Item2RoleRepository;
import com.collaboration.model.ItemDTO;
import com.collaboration.model.ItemRepository;
import com.collaboration.model.OrganizationDTO;
import com.collaboration.model.RoleDTO;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ItemService {

  public enum SubItems {
    ROLES,
    ORGANIZATIONS
  }

  private final ModelMapper modelMapper = new ModelMapper();

  private final ItemRepository itemRepository;
  private final Item2OrgaRepository item2OrgaRepository;
  private final Item2RoleRepository item2RoleRepository;
  private final EntityManager entityManager;
  private final RoleService roleService;
  private final OrganizationService organizationService;

  public ItemService(final ItemRepository itemRepository, final Item2OrgaRepository item2OrgaRepository,
      final Item2RoleRepository item2RoleRepository, RoleService roleService, OrganizationService organizationService,
      EntityManager entityManager) {
    this.itemRepository = itemRepository;
    this.item2OrgaRepository = item2OrgaRepository;
    this.item2RoleRepository  =item2RoleRepository;
    this.roleService= roleService;
    this.organizationService = organizationService;
    this.entityManager = entityManager;
  }

  @Transactional
  public ItemDTO createItem(final ItemDTO itemDTO) throws CollaborationException {
    final Item item = convertFromDTO(itemDTO);
    itemRepository.save(item);

    // If present, save connections to oraganizations
    itemDTO.getOrganizationDTOs().forEach(orgaDTO -> {
      Item2Orga item2orga = new Item2Orga(0, item.getId(), orgaDTO.getId());
      item2OrgaRepository.save(item2orga);
    });

    // If present, save connections to roles
    itemDTO.getRoleDTOs().forEach(roleDTO -> {
      Item2Role item2role = new Item2Role(0, item.getId(), roleDTO.getId());
      item2RoleRepository.save(item2role);
    });

    return convertToDTO(item, List.of());
  }

  @Transactional
  public ItemDTO updateItem(final ItemDTO itemDTO) throws CollaborationException {
    // Check if exists
    itemRepository.findById(itemDTO.getId()).orElseThrow(() -> new CollaborationException(CollaborationException.CollaborationExceptionReason.NOT_FOUND));

    var item = itemRepository.save(convertFromDTO(itemDTO));

    // If present, save connections to oraganizations
    itemDTO.getOrganizationDTOs().forEach(orgaDTO -> {
      Item2Orga item2orga = new Item2Orga(0, item.getId(), orgaDTO.getId());
      item2OrgaRepository.save(item2orga);
    });

    // If present, save connections to roles
    itemDTO.getRoleDTOs().forEach(roleDTO -> {
      Item2Role item2role = new Item2Role(0, item.getId(), roleDTO.getId());
      item2RoleRepository.save(item2role);
    });

    return convertToDTO(item, List.of());
  }

  @Transactional
  public List<ItemDTO> getAllItems(final List<SubItems> subItems) throws CollaborationException {
    var items = itemRepository.findAll();
    var itemDTOs = new ArrayList<ItemDTO>();
    for (Item item: items) {
      itemDTOs.add(convertToDTO(item, subItems));
    }
    return itemDTOs;
  }

  @Transactional
  public ItemDTO getItemById(final long id, final List<SubItems> subItems) throws CollaborationException {
    final Item item = itemRepository.findById(id).orElseThrow(() -> new CollaborationException(CollaborationException.CollaborationExceptionReason.NOT_FOUND));
    return convertToDTO(item, subItems);
  }

  @Transactional
  public ItemDTO getItemByUserId(final long userId, final List<SubItems> subItems) throws CollaborationException {
    final Item item = itemRepository.findByItIdAndUserOrNonuserId(AppConfig.ITEMTYPE_USER, userId).orElseThrow(() -> new CollaborationException(CollaborationException.CollaborationExceptionReason.NOT_FOUND));
    return convertToDTO(item, subItems);
  }

  @Transactional
  public ItemDTO getItemByNonuserId(final long userId, final List<SubItems> subItems) throws CollaborationException {
    final Item item = itemRepository.findByItIdNotAndUserOrNonuserId(AppConfig.ITEMTYPE_USER, userId).orElseThrow(() -> new CollaborationException(CollaborationException.CollaborationExceptionReason.NOT_FOUND));
    return convertToDTO(item, subItems);
  }

  @Transactional
  public int deleteItemByUserId(final long userId) throws CollaborationException {
    final var item = getItemByUserId(userId, List.of());

    var n = item2OrgaRepository.deleteAllByItemId(item.getId());
    log.info("{} record in item2orga were deleted.", n);

    n = item2RoleRepository.deleteAllByItemId(item.getId());
    log.info("{} record in item2role were deleted.", n);

    // Clear hibernate cache (without sometimes depending records are still in the cache and main-object cannot be deleted)
    entityManager.flush();
    entityManager.clear();

    return itemRepository.deleteByItIdAndUserOrNonuserId(AppConfig.ITEMTYPE_USER, userId);
  }

  @Transactional
  public int deleteItemByNonuserId(final long userId) throws CollaborationException {
    final var item = getItemByNonuserId(userId, List.of());

    var n = item2OrgaRepository.deleteAllByItemId(item.getId());
    log.info("{} record in item2orga were deleted.", n);

    n = item2RoleRepository.deleteAllByItemId(item.getId());
    log.info("{} record in item2role were deleted.", n);

    // Clear hibernate cache (without sometimes depending records are still in the cache and main-object cannot be deleted)
    entityManager.flush();
    entityManager.clear();

    return itemRepository.deleteByItIdNotAndUserOrNonuserId(AppConfig.ITEMTYPE_USER, userId);
  }

  private Item convertFromDTO(final ItemDTO itemDTO) {
    return modelMapper.map(itemDTO, Item.class);
  }

  private ItemDTO convertToDTO(final Item item, final List<SubItems> subItems) throws CollaborationException {
    var itemDTO = modelMapper.map(item, ItemDTO.class);
    if (subItems.contains(SubItems.ROLES)) {
      var roleDTOs = new ArrayList<RoleDTO>();
      var item2Roles = item2RoleRepository.findAllByItemId(item.getUserOrNonuserId()).stream().map(this::convertToDTO).toList();
      for(Item2RoleDTO i2r: item2Roles) {
        roleDTOs.add(roleService.getRoleById(i2r.getRoleId()));
      }
      itemDTO.setRoleDTOs(roleDTOs);
    }
    if (subItems.contains(SubItems.ORGANIZATIONS)) {
      var orgaDTOs = new ArrayList<OrganizationDTO>();
      var item2Orgas = item2OrgaRepository.findAllByItemId(item.getUserOrNonuserId()).stream().map(this::convertToDTO).toList();
      for(Item2OrgaDTO i2o: item2Orgas) {
        orgaDTOs.add(organizationService.getOrganizationById(i2o.getOrgaId()));
      }
      itemDTO.setOrganizationDTOs(orgaDTOs);
    }
    return itemDTO;
  }

  private Item2RoleDTO convertToDTO(final Item2Role item2role) {
    return modelMapper.map(item2role, Item2RoleDTO.class);
  }

  private Item2OrgaDTO convertToDTO(final Item2Orga item2orga) {
    return modelMapper.map(item2orga, Item2OrgaDTO.class);
  }
}
