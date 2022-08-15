package com.ersted.springapp.rest;

import com.ersted.springapp.dto.UserDto;
import com.ersted.springapp.model.User;
import com.ersted.springapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users/")
public class UserRestControllerV1 {
    private final UserService userService;

    @Autowired
    public UserRestControllerV1(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable("id") Long userId) {

        User user = this.userService.findById(userId);

        if (user == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        UserDto result = UserDto.fromUser(user);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(value = "")
    public ResponseEntity<List<UserDto>> getUsers() {
        List<UserDto> users = this.userService.getAll().stream()
                .map(UserDto::fromUser)
                .collect(Collectors.toList());

        if (users.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<User> saveUser(@RequestBody User user) {
        HttpHeaders headers = new HttpHeaders();

        if (user == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        User savedUser = this.userService.register(user);

        return new ResponseEntity<>(savedUser, headers, HttpStatus.CREATED);
    }

    @PutMapping("{id}")
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        HttpHeaders headers = new HttpHeaders();

        if (user == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        User updatedUser = this.userService.update(user);

        return new ResponseEntity<>(updatedUser, headers, HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<User> deleteUser(@PathVariable("id") Long id) {
        boolean isDeleted = userService.deleteById(id);

        return isDeleted
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
