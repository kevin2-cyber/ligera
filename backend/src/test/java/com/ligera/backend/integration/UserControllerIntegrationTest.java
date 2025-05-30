package com.ligera.backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ligera.backend.dtos.request.LoginRequest;
import com.ligera.backend.dtos.request.RegisterRequest;
import com.ligera.backend.dtos.request.UpdateProfileRequest;
import com.ligera.backend.dtos.response.AuthResponse;
import com.ligera.backend.dtos.response.UserResponse;
import com.ligera.backend.enums.Role;
import com.ligera.backend.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for user profile operations
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private static final String TEST_NAME = "Test User";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "password123";
    private static final String REGISTER_ENDPOINT = "/api/v1/auth/register";
    private static final String LOGIN_ENDPOINT = "/api/v1/auth/login";
    private static final String USER_PROFILE_ENDPOINT = "/api/v1/users/me";
    private static final String UPDATED_NAME = "Updated User";
    private static final String UPDATED_EMAIL = "updated@example.com";

    private String authToken;

    @BeforeEach
    void setUp() throws Exception {
        // Clean database
        userRepository.deleteAll();
        
        // Register a test user
        RegisterRequest registerRequest = RegisterRequest.builder()
                .name(TEST_NAME)
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .build();

        MvcResult result = mockMvc.perform(post(REGISTER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        // Extract auth token for subsequent requests
        AuthResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(), AuthResponse.class);
        authToken = response.getToken();
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void testGetCurrentUser() throws Exception {
        // Get current user profile with auth token
        mockMvc.perform(get(USER_PROFILE_ENDPOINT)
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(TEST_NAME))
                .andExpect(jsonPath("$.email").value(TEST_EMAIL))
                .andExpect(jsonPath("$.role").value(Role.USER.name()));
    }

    @Test
    void testUpdateUserProfile() throws Exception {
        // Create update request
        UpdateProfileRequest updateRequest = UpdateProfileRequest.builder()
                .name(UPDATED_NAME)
                .email(TEST_EMAIL) // Keep same email to avoid validation errors
                .build();

        // Update user profile
        MvcResult result = mockMvc.perform(put(USER_PROFILE_ENDPOINT)
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(UPDATED_NAME))
                .andExpect(jsonPath("$.email").value(TEST_EMAIL))
                .andReturn();

        // Verify profile was updated
        UserResponse userResponse = objectMapper.readValue(
                result.getResponse().getContentAsString(), UserResponse.class);
        assertEquals(UPDATED_NAME, userResponse.getName());
    }

    @Test
    void testUpdateUserProfileWithNewEmail() throws Exception {
        // Create update request with new email
        UpdateProfileRequest updateRequest = UpdateProfileRequest.builder()
                .name(UPDATED_NAME)
                .email(UPDATED_EMAIL)
                .build();

        // Update user profile
        mockMvc.perform(put(USER_PROFILE_ENDPOINT)
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(UPDATED_NAME))
                .andExpect(jsonPath("$.email").value(UPDATED_EMAIL));

        // Verify in database
        assertTrue(userRepository.findByEmail(UPDATED_EMAIL).isPresent());
        assertFalse(userRepository.findByEmail(TEST_EMAIL).isPresent());
    }

    @Test
    void testGetUserProfileUnauthorized() throws Exception {
        // Try to get profile without token
        mockMvc.perform(get(USER_PROFILE_ENDPOINT))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testUpdateUserProfileUnauthorized() throws Exception {
        // Create update request
        UpdateProfileRequest updateRequest = UpdateProfileRequest.builder()
                .name(UPDATED_NAME)
                .email(TEST_EMAIL)
                .build();

        // Try to update profile without token
        mockMvc.perform(put(USER_PROFILE_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testUpdateUserProfileWithInvalidData() throws Exception {
        // Create update request with empty name
        UpdateProfileRequest updateRequest = UpdateProfileRequest.builder()
                .name("")
                .email(TEST_EMAIL)
                .build();

        // Try to update with invalid data
        mockMvc.perform(put(USER_PROFILE_ENDPOINT)
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());

        // Create update request with invalid email
        updateRequest = UpdateProfileRequest.builder()
                .name(UPDATED_NAME)
                .email("invalid-email")
                .build();

        // Try to update with invalid email
        mockMvc.perform(put(USER_PROFILE_ENDPOINT)
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateUserProfileWithExistingEmail() throws Exception {
        // Register another user first
        RegisterRequest registerRequest = RegisterRequest.builder()
                .name("Another User")
                .email(UPDATED_EMAIL)
                .password(TEST_PASSWORD)
                .build();

        mockMvc.perform(post(REGISTER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        // Create update request with email that already exists
        UpdateProfileRequest updateRequest = UpdateProfileRequest.builder()
                .name(UPDATED_NAME)
                .email(UPDATED_EMAIL)
                .build();

        // Try to update with email that's already in use
        mockMvc.perform(put(USER_PROFILE_ENDPOINT)
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Email already in use"));
    }
}

