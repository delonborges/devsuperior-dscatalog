package com.delonborges.dscatalog.services;

import com.delonborges.dscatalog.dto.RoleDTO;
import com.delonborges.dscatalog.dto.UserDTO;
import com.delonborges.dscatalog.entities.Role;
import com.delonborges.dscatalog.entities.User;
import com.delonborges.dscatalog.repositories.RoleRepository;
import com.delonborges.dscatalog.repositories.UserRepository;
import com.delonborges.dscatalog.services.exceptions.DatabaseIntegrityViolationException;
import com.delonborges.dscatalog.services.exceptions.ResourceNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> findAllPaged(Pageable pageable) {
        Page<User> page = userRepository.findAll(pageable);
        return page.map(UserDTO::new);
    }

    @Transactional(readOnly = true)
    public UserDTO findById(Long id) {
        Optional<User> object = userRepository.findById(id);
        User entity = object.orElseThrow(() -> new ResourceNotFoundException("Entity not found"));
        return new UserDTO(entity);
    }

    @Transactional
    public UserDTO insert(UserDTO dtoItem) {
        User entity = new User();
        dtoToEntity(dtoItem, entity);
        entity = userRepository.save(entity);
        return new UserDTO(entity);
    }

    @Transactional
    public UserDTO update(Long id, UserDTO dtoItem) {
        try {
            User entity = userRepository.getOne(id);
            dtoToEntity(dtoItem, entity);
            entity = userRepository.save(entity);
            return new UserDTO(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Id not found: " + id);
        }
    }

    public void delete(Long id) {
        try {
            userRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("Id not found: " + id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseIntegrityViolationException("Integrity violation");
        }
    }

    private void dtoToEntity(UserDTO dtoItem, User entity) {
        entity.setFirstName(dtoItem.getFirstName());
        entity.setLastName(dtoItem.getLastName());
        entity.setEmail(dtoItem.getEmail());

        entity.getRoles().clear();
        for (RoleDTO roleDTO : dtoItem.getRoles()) {
            Role role = roleRepository.getOne(roleDTO.getId());
            entity.getRoles().add(role);
        }
    }
}
