package com.example.demo.controller;

import com.example.demo.TestUtils;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController userController;

    private UserRepository userRepository = mock(UserRepository.class);

    private CartRepository cartRepository = mock(CartRepository.class);

    private BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);


    @Before
    public void setUp() {
        userController = new UserController();
        TestUtils.injectObject(userController, "userRepository", userRepository);
        TestUtils.injectObject(userController, "cartRepository", cartRepository);
        TestUtils.injectObject(userController, "bCryptPasswordEncoder", bCryptPasswordEncoder);

    }

    @Test
    public void create_user_happy_path() {
        final ResponseEntity<User> userResponse = createUser();

        assertNotNull(userResponse);
        assertEquals(200, userResponse.getStatusCodeValue());

        User user = userResponse.getBody();

        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals("Test", user.getUsername());
        assertEquals("thisishashed", user.getPassword());
    }

    private ResponseEntity<User> createUser() {
        when(bCryptPasswordEncoder.encode("testPassword")).thenReturn("thisishashed");

        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setUsername("Test");
        userRequest.setPassword("testPassword");
        userRequest.setConfirmPassword("testPassword");

        return userController.createUser(userRequest);
    }

    @Test
    public void verifyFindByUsername() {
        final ResponseEntity<User> userResponse = createUser();

        assertNotNull(userResponse);
        assertEquals(200, userResponse.getStatusCodeValue());

        User user = userResponse.getBody();

        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals("Test", user.getUsername());
        assertEquals("thisishashed", user.getPassword());

        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);


        ResponseEntity<User> userResponseEntity = userController.findByUserName("Test");

        assertNotNull(userResponseEntity);
        assertEquals(200, userResponseEntity.getStatusCodeValue());

        User userFound = userResponseEntity.getBody();
        assertNotNull(user);
        assertEquals("Test", user.getUsername());
        assertEquals("thisishashed", user.getPassword());

    }


    @Test
    public void verifyFindById() {
        final ResponseEntity<User> userResponse = createUser();

        assertNotNull(userResponse);
        assertEquals(200, userResponse.getStatusCodeValue());

        User user = userResponse.getBody();

        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals("Test", user.getUsername());
        assertEquals("thisishashed", user.getPassword());

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));


        ResponseEntity<User> userResponseEntity = userController.findById(0L);

        assertNotNull(userResponseEntity);
        assertEquals(200, userResponseEntity.getStatusCodeValue());

        User userFound = userResponseEntity.getBody();
        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals("Test", user.getUsername());
        assertEquals("thisishashed", user.getPassword());


    }
}
