package com.ersted.springapp.dto;

import com.ersted.springapp.model.User;
import lombok.Data;

@Data
public class RegistrationDto {
    private Long id;
    private String username;
    private String firstname;
    private String lastname;
    private String password;
    private String email;

    public User toUser(){
        return User.builder()
                .id(id)
                .login(username)
                .firstName(firstname)
                .lastName(lastname)
                .password(password)
                .email(email)
                .build();
    }
}
