package com.software.grey.controllers;

import com.software.grey.exceptions.exceptions.DataNotFoundException;
import com.software.grey.models.dtos.PostDTO;
import com.software.grey.models.dtos.UserDTO;
import com.software.grey.repositories.BasicUserRepo;
import com.software.grey.repositories.PostRepository;
import com.software.grey.repositories.UserRepo;
import com.software.grey.repositories.UserVerificationRepo;
import com.software.grey.services.UserService;
import com.software.grey.services.implementations.PostService;
import com.software.grey.utils.EndPoints;
import com.software.grey.utils.SecurityUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import static com.software.grey.models.enums.Feeling.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)

class ReportPostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private PostService postService;

    @MockBean
    private SecurityUtils securityUtils;

    private final SignupController signup;
    private final UserRepo userRepo;
    private final PostRepository postRepository;
    private BasicUserRepo basicUserRepo;
    private UserVerificationRepo userVerificationRepo;

    private ArrayList<UUID> posts;

    @Autowired
    ReportPostControllerTest(UserService userService, PostService postService, SecurityUtils securityUtils,
                             SignupController signup, UserRepo userRepo, PostRepository postRepository) {
        this.userService = userService;
        this.postService = postService;
        this.securityUtils = securityUtils;
        this.signup = signup;
        this.userRepo = userRepo;
        this.postRepository = postRepository;
    }

    @BeforeAll
    void init() {
        postRepository.deleteAll();
        posts = new ArrayList<>();
        addUser1();
    }

    void addUser1() {
        when(securityUtils.getCurrentUserName()).thenReturn("mocked User1");
        UserDTO userDTO1 = new UserDTO("mockEmail1@gmail.com", "mocked User1", "mockPas1");
        signup.signup(userDTO1);
        createPostsForUser1();
    }

    private void createPostsForUser1() {
        for (int i = 0; i < 5; i++)
            posts.add(postService
                    .add(PostDTO.builder()
                            .postText(i + " user1")
                            .postFeelings(Set.of(LOVE, HAPPY))
                            .build()));
    }

    @AfterAll
    void cleanUpAll() {
        postRepository.deleteAll();
        userRepo.deleteAll();
    }

    @Test
    @WithMockUser(username = "greyUser", roles = "ROLES_USER")
    void reportPost_shouldBeValid() throws Exception {
        when(securityUtils.getCurrentUser()).thenReturn(userRepo.findByUsername("mocked User1"));
        mockMvc.perform(MockMvcRequestBuilders.post(EndPoints.POST +
                                EndPoints.REPORT_POST + "/" + posts.get(0))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers
                        .content()
                        .string("Post reported successfully!\n" +
                                "We will review your report and take the necessary actions."));
        verify(postService, times(1)).report(anyString());
    }

    @Test
    @WithMockUser(username = "greyUser", roles = "ROLES_USER")
    void reportPost_shouldBeNotFound() throws Exception {
        UUID randomNotInPosts = UUID.randomUUID();
        while(posts.contains(randomNotInPosts))
            randomNotInPosts = UUID.randomUUID();

        when(securityUtils.getCurrentUser()).thenReturn(userRepo.findByUsername("mocked User1"));
        doThrow(new DataNotFoundException("Post not found"))
                .when(postService).report(randomNotInPosts.toString());

        String expectedErrorMessage = "{\"errorMessage\":\"Post not found\"}";
        mockMvc.perform(post(EndPoints.POST + EndPoints.REPORT_POST + "/" + randomNotInPosts)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers
                        .content()
                        .string(expectedErrorMessage));
        verify(postService, times(1)).report(anyString());
    }
}