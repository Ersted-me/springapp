package com.ersted.springapp.service.impl;

import com.ersted.springapp.model.Role;
import com.ersted.springapp.model.Status;
import com.ersted.springapp.model.User;
import com.ersted.springapp.repository.RoleRepository;
import com.ersted.springapp.repository.UserRepository;
import com.ersted.springapp.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, roleRepository, bCryptPasswordEncoder);
    }

    @Test
    void registerUserWithExistLoginThenReturnNull() {
        User newUser = User.builder().login("login").build();
        User expected = null;

        when(userRepository.findUserByLogin("login")).thenReturn(new User());
        User actual = userService.register(newUser);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void registeredUserThenReturnDefaultUser(){
        User newUser = User.builder()
                .id(null)
                .login("login")
                .email("email@mail.ru")
                .firstName("FirstName")
                .lastName("LastName")
                .password("password")
                .build();

        Role role = new Role();
        role.setName("ROLE_USER");

        User expected = User.builder()
                .id(1L)
                .login("login")
                .email("email@mail.ru")
                .firstName("FirstName")
                .lastName("LastName")
                .password("bcryptpassword")
                .roles(Collections.singletonList(role))
                .status(Status.ACTIVE)
                .build();

        when(userRepository.findUserByLogin(any())).thenReturn(null);
        when(roleRepository.findRoleByName("ROLE_USER")).thenReturn(role);
        when(bCryptPasswordEncoder.encode(any())).thenReturn("bcryptpassword");
        newUser.setId(1L);
        when(userRepository.save(any())).thenReturn(newUser);

        User actual = userService.register(newUser);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void foundNotExitsUserThenReturnNull(){
        Long notExistId = 1L;
        User expected = null;

        when(userRepository.findById(notExistId)).thenReturn(Optional.empty());

        User actual = userService.findById(notExistId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void foundExitsUserThenReturnUser(){
        Long existId = 1L;
        User expected = User.builder().id(1L).build();

        when(userRepository.findById(existId)).thenReturn(Optional.of(expected));

        User actual = userService.findById(existId);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void foundNotExistLoginThenReturnNull(){
        String notExistLogin = "notExistLogin";
        User expected = null;

        when(userRepository.findUserByLogin(notExistLogin)).thenReturn(null);

        User actual = userService.findByUsername(notExistLogin);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void foundExistLoginThenReturnUser(){
        String existLogin = "existLogin";
        User expected = User.builder().login(existLogin).build();

        when(userRepository.findUserByLogin(existLogin)).thenReturn(expected);

        User actual = userService.findByUsername(existLogin);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void updateNotExitsUserThenReturnNull(){
        Long notExistId = 1L;
        User notExistUser = User.builder().id(notExistId).build();
        User expected = null;

        when(userRepository.findById(notExistUser.getId()))
                .thenReturn(Optional.empty());

        User actual = userService.update(notExistUser);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void updateExitsUserThenReturnUpdatedUser(){
        Long existId = 1L;

        User currentUser = User.builder()
                .id(existId)
                .firstName("firstName")
                .lastName("lastName")
                .email("email")
                .login("login")
                .build();

        User expected = User.builder()
                .id(existId)
                .firstName("updated")
                .lastName("updated")
                .email("updated")
                .login("updated")
                .build();

        when(userRepository.findById(expected.getId()))
                .thenReturn(Optional.of(currentUser));

        when(userRepository.save(currentUser)).thenReturn(currentUser);

        User actual = userService.update(expected);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void deleteNotExistUserByIdThenReturnFalse(){
        Long notExistUserId = 1L;

        when(userRepository.findById(notExistUserId)).thenReturn(Optional.empty());

        boolean actual = userService.deleteById(notExistUserId);

        assertThat(actual).isFalse();
    }

    @Test
    void deleteExistUserByIdThenReturnTrue(){
        Long existUserId = 1L;

        when(userRepository.findById(existUserId))
                .thenReturn(Optional.of(new User()));

        boolean actual = userService.deleteById(existUserId);

        assertThat(actual).isTrue();
    }

    @Test
    void deleteNotExistUserByLoginThenReturnFalse(){
        String notExistUserLogin = "notExistUserLogin";

        when(userRepository.findUserByLogin(notExistUserLogin))
                .thenReturn(null);

        boolean actual = userService.deleteByLogin(notExistUserLogin);

        assertThat(actual).isFalse();
    }

    @Test
    void deleteExistUserByLoginThenReturnTrue(){
        String existUserLogin = "existUserLogin";

        when(userRepository.findUserByLogin(existUserLogin))
                .thenReturn(new User());

        boolean actual = userService.deleteByLogin(existUserLogin);

        assertThat(actual).isTrue();
    }

    @Test
    void getEmptyListWhenThereAreNoUsers(){
        List<User> dbList = Collections.emptyList();
        List<User> expected = Collections.emptyList();

        when(userRepository.findAll()).thenReturn(dbList);

        List<User> actual = userService.getAll();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getAllUsersWhenListIsNotEmpty(){
        List<User> dbList = Collections.singletonList(new User());
        List<User> expected = Collections.singletonList(new User());

        when(userRepository.findAll()).thenReturn(dbList);

        List<User> actual = userService.getAll();
        assertThat(actual).isEqualTo(expected);
    }
}