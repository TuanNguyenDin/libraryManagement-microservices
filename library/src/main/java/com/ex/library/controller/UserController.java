package com.ex.library.controller;

import com.ex.library.dto.response.APIResponse;
import com.ex.library.dto.request.UserCreateRequest;
import com.ex.library.dto.response.UserResponse;
import com.ex.library.entity.Users;
import com.ex.library.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    APIResponse<UserResponse> CreateUser(@RequestBody @Valid UserCreateRequest request) throws MessagingException {
        return userService.createUser(request);
    }

    @GetMapping()
    List<Users> getAll() {
        return userService.findAll();
    }

    @GetMapping("/banned")
    List<Users> getIsBanned(@RequestBody boolean isBanned) {
        return userService.findUsersBanned(isBanned);
    }

    @GetMapping("/active")
    List<Users> getActive(@RequestBody boolean isActive) {
        return userService.findUsersActive(isActive);
    }

    @GetMapping("/{id}")
    UserResponse getUser(@PathVariable("id") String id) {
        return userService.getUser(id);
    }

    @PutMapping("/{id}")
    APIResponse<Users> updateUser(@PathVariable("id") String id, @RequestBody UserCreateRequest request) {
        return userService.updateUser(id, request);
    }

    @DeleteMapping("/{id}")
    APIResponse<Users> deleteUser(@PathVariable("id") String id) {
        return userService.deleteUser(id);
    }

    @PostMapping("/search-by-date")
    APIResponse<List<Users>> searchByDate(@RequestBody Date begindate, Date endDate) {
        return userService.findUserFollowDate(begindate, endDate);
    }

    @PostMapping("/forget-password")
    APIResponse<UserResponse> forgetPassword(@RequestBody String email) throws MessagingException {
        return userService.resetPassword(email);
    }

    @PostMapping("/change-password")
    APIResponse<UserResponse> updatePassword(@RequestBody String userId, String currentPassword, String newPassword) throws MessagingException {
        return userService.updatePassword(userId, currentPassword, newPassword);
    }

    @PostMapping("/change-email")
    APIResponse<UserResponse> updateEmail(@RequestBody String userId, String newEmail) throws MessagingException {
        return userService.changeUserEmail(userId, newEmail);
    }

    @PostMapping("/mail/verify")
    APIResponse<Void> confirmChangeEmail(@RequestBody String userId, String verifyCode) throws MessagingException {
        return userService.confirmChangeEmail(userId, verifyCode);
    }

    @PostMapping("/verify/{id}")
    APIResponse<UserResponse> updateEmail(@PathVariable("id") String userId) {
        return userService.activeUser(userId);
    }
}
