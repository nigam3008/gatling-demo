package com.demo.galting.controller;

import com.demo.galting.dto.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    @GetMapping
    public List<User> getUsers() {
        User user = User.builder()
                .firstName("vishal")
                .lastName("nigam")
                .country("INDIA")
                .build();
        return Arrays.asList(user);
    }

    @PostMapping
    public ResponseEntity createUser(@RequestBody User user) {
        log.info("User Detail: {}", user);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity deleteUser() {
        return ResponseEntity.ok().build();
    }
}
