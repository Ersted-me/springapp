package com.ersted.springapp.dto;

import com.ersted.springapp.model.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class UserDto {
    private Long id;
    private String username;
    private String firstname;
    private String lastname;
    private String email;

    public static UserDto fromUser(User user){
        return UserDto.builder()
                .id(user.getId())
                .username(user.getLogin())
                .firstname(user.getFirstName())
                .lastname(user.getLastName())
                .email(user.getEmail())
                .build();
    }

    public User toUser(){
        return User.builder()
                .id(id)
                .login(username)
                .firstName(firstname)
                .lastName(lastname)
                .email(email)
                .build();
    }
}
