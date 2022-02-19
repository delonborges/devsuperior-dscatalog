package com.delonborges.dscatalog.resources;

import com.delonborges.dscatalog.dto.InsertUserDTO;
import com.delonborges.dscatalog.dto.UserDTO;
import com.delonborges.dscatalog.services.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/users")
public class UserResource {

    private final UserService userService;

    public UserResource(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<Page<UserDTO>> findAllPaged(Pageable pageable) {
        Page<UserDTO> dtoList = userService.findAllPaged(pageable);
        return ResponseEntity.ok().body(dtoList);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<UserDTO> findById(@PathVariable Long id) {
        UserDTO dtoItem = userService.findById(id);
        return ResponseEntity.ok().body(dtoItem);
    }

    @PostMapping
    public ResponseEntity<UserDTO> insert(@RequestBody InsertUserDTO dtoItem) {
        UserDTO userDTO = userService.insert(dtoItem);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(userDTO.getId()).toUri();
        return ResponseEntity.created(uri).body(userDTO);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<UserDTO> update(@PathVariable Long id, @RequestBody UserDTO dtoItem) {
        dtoItem = userService.update(id, dtoItem);
        return ResponseEntity.ok().body(dtoItem);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
