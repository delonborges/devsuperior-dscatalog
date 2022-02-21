package com.delonborges.dscatalog.services;

import com.delonborges.dscatalog.dto.InsertUserDTO;
import com.delonborges.dscatalog.dto.RoleDTO;
import com.delonborges.dscatalog.dto.UpdateUserDTO;
import com.delonborges.dscatalog.dto.UserDTO;
import com.delonborges.dscatalog.entities.Role;
import com.delonborges.dscatalog.entities.User;
import com.delonborges.dscatalog.repositories.RoleRepository;
import com.delonborges.dscatalog.repositories.UserRepository;
import com.delonborges.dscatalog.services.exceptions.DatabaseIntegrityViolationException;
import com.delonborges.dscatalog.services.exceptions.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
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
    public UserDTO insert(InsertUserDTO dtoItem) {
        User entity = new User();
        dtoToEntity(dtoItem, entity);
        entity.setPassword(passwordEncoder.encode(dtoItem.getPassword()));
        entity = userRepository.save(entity);
        return new UserDTO(entity);
    }

    @Transactional
    public UserDTO update(Long id, UpdateUserDTO dtoItem) {
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

    @Override
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            logger.error("User not found: " + userEmail);
            throw new UsernameNotFoundException("Email not found");
        }
        logger.info("User found: " + userEmail);
        return user;
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
