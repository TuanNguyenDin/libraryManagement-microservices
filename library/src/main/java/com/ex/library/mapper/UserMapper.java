package com.ex.library.mapper;

import com.ex.library.dto.request.UserCreateRequest;
import com.ex.library.dto.response.UserResponse;
import com.ex.library.entity.Users;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    Users toUser(UserCreateRequest request);
    UserResponse toUserResponse(Users user);
    Users toUserResponse(UserResponse response);
    void updateUser(@MappingTarget Users users, UserCreateRequest request);
}
