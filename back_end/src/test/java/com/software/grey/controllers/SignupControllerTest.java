package com.software.grey.controllers;

import com.software.grey.exceptions.UserExistsException;
import com.software.grey.models.enums.Role;
import com.software.grey.models.entities.User;
import com.software.grey.models.dtos.UserDTO;
import com.software.grey.repositories.UserRepo;
import com.software.grey.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Objects;

import static com.software.grey.utils.EndPoints.SIGNUP;
import static com.software.grey.utils.JsonUtil.asJsonString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SignupControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    private UserService userService;
    @Autowired
    private SignupController signupController;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Test
    void signupCorrectUser() {
        UserDTO myUser = new UserDTO("mockEmail12@gmail.com", "testUser", "mock Password test");
        signupController.signup(myUser);
        User user = userRepo.findByUsername("testUser");
        assert(Objects.equals(user.getUsername(), "testUser"));
        assert(Objects.equals(user.getEmail(), "mockEmail12@gmail.com"));
        assertTrue(bCryptPasswordEncoder.matches("mock Password test", user.getPassword()));
        assert(user.getRole() == Role.ROLE_USER);
    }

    @Test
    void signUpDuplicateUsername() {
        UserDTO myUser = new UserDTO("mockEmail@gmail.com", "mockUser", "mock Password test");
        signupController.signup(myUser);
        myUser.email = "mockEmail2@gmail.com";
        assertThrows(UserExistsException.class, () -> signupController.signup(myUser));
    }

    @Test
    void signUpDuplicateEmail() {
        UserDTO myUser = new UserDTO("mockEmail132@gmail.com", "mockUser1243", "mock Password test");
        signupController.signup(myUser);
        myUser.username = "notMockUser";
        assertThrows(UserExistsException.class, () -> signupController.signup(myUser));
    }

    @Test
    void signupWithNonValidEmail_ShouldFail() throws Exception {
        UserDTO myUser = new UserDTO("mockEmail", "mockUser12", "mock Password test");
        mockMvc.perform(post(SIGNUP)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(myUser)))
                .andExpect(status().isBadRequest());
//                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("Email form not valid"));
    }

    @Test
    void signupWithNonValidEmail2_ShouldFail() throws Exception {
        UserDTO myUser = new UserDTO("_mockE@m_ail@gmail.com", "mockUser13", "mock Password test");
        mockMvc.perform(post(SIGNUP)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(myUser)))
                .andExpect(status().isBadRequest());
//                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("Email form not valid"));
    }

    @Test
    void signupWithEmptyEmail_ShouldFail() throws Exception {
        UserDTO myUser = new UserDTO("", "mockUser14", "mock Password test");
        mockMvc.perform(post(SIGNUP)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(myUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void signupWithEmptyUsername_ShouldFail() throws Exception {
        UserDTO myUser = new UserDTO("valid@gmail.com", "", "mock Password test");
        mockMvc.perform(post(SIGNUP)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(myUser)))
                .andExpect(status().isBadRequest());
//                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("Username is mandatory"));
    }
}