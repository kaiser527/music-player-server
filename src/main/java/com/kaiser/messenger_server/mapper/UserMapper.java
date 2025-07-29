package com.kaiser.messenger_server.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import com.kaiser.messenger_server.dto.request.CreateUserRequest;
import com.kaiser.messenger_server.dto.request.UpdateUserRequest;
import com.kaiser.messenger_server.dto.response.UserResponse;
import com.kaiser.messenger_server.entities.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "track", ignore = true)
    @Mapping(target = "playlist", ignore = true)
    @Mapping(target = "codeExpire", ignore = true)
    @Mapping(target = "codeId", ignore = true)
    User toCreateUser(CreateUserRequest request);
    
    UserResponse toUserResponse(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "track", ignore = true)
    @Mapping(target = "playlist", ignore = true)
    @Mapping(target = "codeExpire", ignore = true)
    @Mapping(target = "codeId", ignore = true)
    void toUpdateUser(@MappingTarget User user, UpdateUserRequest request);
}
