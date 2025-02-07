package com.ex.library.controller;

import com.ex.library.dto.request.UserCreateRequest;
import com.ex.library.dto.response.APIResponse;
import com.ex.library.dto.response.UserResponse;
import com.ex.library.entity.Users;
import com.ex.library.exception.CustomException;
import com.ex.library.exception.ErrorCode;
import com.ex.library.mapper.UserMapper;
import com.ex.library.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.HttpClientErrorException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@Nested
@SpringBootTest
@AutoConfigureMockMvc
class userControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private UserMapper mapper;
    @MockBean
    private UserService userService;

    private UserCreateRequest userCreateRequest;
    private UserResponse userResponse;

    @BeforeEach
    void initData() throws Exception {
        userCreateRequest = UserCreateRequest.builder()
                .name("Test User")
                .email("test@example.com")
                .password("12345678")
                .build();
        userResponse = UserResponse.builder()
                .name("Test User")
                .email("test@example.com")
                .build();
    }

    @Test
    void createUser() throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(userCreateRequest);

        Mockito.when(userService.createUser(ArgumentMatchers.any())).thenReturn(
                APIResponse.<UserResponse>builder()
                        .code(200)
                        .message("User created successfully!")
                        .response(userResponse)
                        .build()
        );

        mvc.perform(post("/api/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User created successfully!"))
                .andExpect(jsonPath("$.response.name").value("Test User"))
                .andExpect(jsonPath("$.response.email").value("test@example.com"));
    }


    @WithMockUser
    @Test
    void testGetAllUsers() throws Exception {
        List<Users> expectedUsers = List.of(
                Users.builder().id("1").name("User 1").email("user1@example.com").build(),
                Users.builder().id("2").name("User 2").email("user2@example.com").build()
        );

        Mockito.when(userService.findAll()).thenReturn(expectedUsers);

        mvc.perform(get("/api/user")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].name").value("User 1"))
                .andExpect(jsonPath("$[0].email").value("user1@example.com"))
                .andExpect(jsonPath("$[1].id").value("2"))
                .andExpect(jsonPath("$[1].name").value("User 2"))
                .andExpect(jsonPath("$[1].email").value("user2@example.com"));
    }


    @Test
    void testGetUser_unauthenticated() throws Exception {
        mvc.perform(get("/api/user/1234567890"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void testGetUser_notFound() throws Exception {
        Mockito.when(userService.getUser(ArgumentMatchers.anyString()))
                .thenThrow(new CustomException(ErrorCode.USER_NOT_EXISTED));
        mvc.perform(get("/api/user/1234567890"))
                .andExpect(jsonPath("$.code").value(1001))
                .andExpect(jsonPath("$.message").value("user not exist!"));
    }

    @WithMockUser
    @Test
    void testGetUser_invalidUUID() throws Exception {
        Mockito.when(userService.getUser(ArgumentMatchers.anyString()))
                .thenThrow(new CustomException(ErrorCode.INVALID_VALUE_INPUT));
        mvc.perform(get("/api/user/invalid-uuid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("invalid value input!"));
    }

    @WithMockUser
    @Test
    void testGetUser_authenticatedAndAuthorized() throws Exception {
        Mockito.when(userService.getUser(ArgumentMatchers.anyString())).thenReturn(userResponse);

        mvc.perform(MockMvcRequestBuilders.get("/api/user/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @WithMockUser
    @Test
    void testGetUser_notReturnPassword() throws Exception {
        String userId = "b02c3e6d-67cd-4f5b-b366-b50fa0507ae5";

        mvc.perform(get("/api/user/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @WithMockUser
    @Test
    void updateUser_invalidUUID_returns400() throws Exception {
        Mockito.when(userService.updateUser(ArgumentMatchers.anyString(), ArgumentMatchers.any()))
                .thenThrow(new CustomException(ErrorCode.INVALID_VALUE_INPUT));

        mvc.perform(put("/api/user/invalid-uuid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Test User\",\"email\":\"test@example.com\",\"password\":\"12345678\"}"))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser
    @Test
    void updateUser_notFound_returns404() throws Exception {
        String userId = "1234567890";

        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(userCreateRequest);

        Mockito.when(userService.updateUser(ArgumentMatchers.eq(userId), ArgumentMatchers.any()))
                .thenThrow(
                        new CustomException(ErrorCode.USER_NOT_EXISTED)
                );

        mvc.perform(put("/api/user/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(jsonPath("$.message").value("user not exist!"));
    }

    @WithMockUser
    @Test
    void updateUser_emptyRequestBody_returns400() throws Exception {
        String userId = "1234567890";

        Mockito.when(userService.updateUser(ArgumentMatchers.eq(userId), ArgumentMatchers.any()))
                .thenThrow(new CustomException(ErrorCode.INVALID_VALUE_INPUT));

        mvc.perform(put("/api/user/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()
                );
    }

    @WithMockUser
    @Test
    void updateUser_returns200_whenUserIsUpdatedSuccessfully() throws Exception {
        String userId = "1234567890";

        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(userCreateRequest);

        Mockito.when(userService.updateUser(ArgumentMatchers.eq(userId), ArgumentMatchers.any()))
                .thenReturn(APIResponse.<Users>builder()
                        .code(200)
                        .message("User updated successfully!")
                        .response(mapper.toUserResponse(userResponse))
                        .build());

        mvc.perform(put("/api/user/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User updated successfully!"))
                .andExpect(jsonPath("$.response.name").value("Test User"))
                .andExpect(jsonPath("$.response.email").value("test@example.com"));
    }

    @WithMockUser
    @Test
    void deleteUser_notFound_returns404() throws Exception {
        String userId = "1234567890";

        Mockito.when(userService.deleteUser(ArgumentMatchers.eq(userId)))
                .thenThrow(new CustomException(ErrorCode.USER_NOT_EXISTED));

        mvc.perform(delete("/api/user/{id}", userId))
                .andExpect(jsonPath("$.message").value("user not exist!"));
    }

    @WithMockUser
    @Test
    void deleteUser_invalidUUID_returns400() throws Exception {
        String invalidUUID = "invalid-uuid";

        Mockito.when(userService.deleteUser(ArgumentMatchers.eq(invalidUUID)))
                .thenThrow(new CustomException(ErrorCode.INVALID_VALUE_INPUT));

        mvc.perform(MockMvcRequestBuilders.delete("/api/user/" + invalidUUID))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void deleteUser_returns200_whenUserIsDeletedSuccessfully() throws Exception {
        String userId = "1234567890";

        Mockito.when(userService.deleteUser(ArgumentMatchers.eq(userId)))
                .thenReturn(APIResponse.<Users>builder()
                        .code(200)
                        .message("User deleted successfully!")
                        .build());

        mvc.perform(MockMvcRequestBuilders.delete("/api/user/{id}", userId)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User deleted successfully!"));
    }

    @Test
    @WithMockUser
    void searchByDate_invalidDateFormat_returns400() throws Exception {
        String invalidDateFormat = "2022/01/01";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date beginDate = null;
        Date endDate = null;

        try {
            beginDate = dateFormat.parse(invalidDateFormat);
        } catch (ParseException e) {
            // Do nothing, as we are testing the invalid date format
        }

        Mockito.when(userService.findUserFollowDate(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenThrow(IllegalArgumentException.class);

        mvc.perform(MockMvcRequestBuilders.post("/api/user/search-by-date")
                        .contentType("application/json")
                        .content("{\"beginDate\":\"" + invalidDateFormat + "\", \"endDate\":\"2022-01-31\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void searchByDate_noUsersFound_returnsEmptyList() throws Exception {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date beginDate = new Date(System.currentTimeMillis() - 1000000000);
        Date endDate = new Date(System.currentTimeMillis() + 1000000000);
        log.info("beginDate: " + dateFormat.format(beginDate));
        log.info("endDate" + dateFormat.format(endDate));

        Mockito.when(userService.findUserFollowDate(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(APIResponse.<List<Users>>builder()
                        .code(200)
                        .message("Users found successfully!")
                        .response(Collections.emptyList())
                        .build());

        mvc.perform(MockMvcRequestBuilders.post("/api/user/search-by-date")
                        .contentType("application/json")
                        .content("{\"beginDate\":" + dateFormat.format(beginDate) + ", \"endDate\":" + dateFormat.format(endDate) + "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Users found successfully!"))
                .andExpect(jsonPath("$.response").value(Collections.emptyList()));
    }

    @Test
    @WithMockUser
    public void forgetPassword_invalidEmailFormat_returns400() throws Exception {
        String invalidEmailRequest = "{\"email\":\"invalid_email_format\"}";

        Mockito.when(userService.resetPassword(ArgumentMatchers.eq(invalidEmailRequest)))
                .thenThrow(new CustomException(ErrorCode.INVALID_VALUE_INPUT));

        mvc.perform(MockMvcRequestBuilders.post("/api/user/forget-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidEmailRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void forgetPassword_nonexistentEmail_returns404() throws Exception {
        String nonexistentEmail = "nonexistent@example.com";

        Mockito.when(userService.resetPassword(ArgumentMatchers.eq(nonexistentEmail)))
                .thenThrow(
                        new CustomException(ErrorCode.USER_NOT_EXISTED)
                );

        mvc.perform(MockMvcRequestBuilders.post("/api/user/forget-password")
                        .contentType("application/json")
                        .content(nonexistentEmail))
                .andExpect(jsonPath("$.message").value("user not exist!"))
                .andExpect(jsonPath("$.code").value(1001));
    }

    @Test
    @WithMockUser
    void updatePassword_invalidPasswordFormat_returns400() throws Exception {
        String invalidPasswordFormat = "weak";
        Mockito.when(userService.updatePassword(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.eq(invalidPasswordFormat)))
                .thenThrow(new CustomException(ErrorCode.WEAK_PASSWORD));
        mvc.perform(MockMvcRequestBuilders.post("/api/user/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":\"1234567890\",\"currentPassword\":\"oldPassword\",\"newPassword\":\"" + invalidPasswordFormat + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Your password is weak"))
                .andExpect(jsonPath("$.code").value(1010));
    }
}