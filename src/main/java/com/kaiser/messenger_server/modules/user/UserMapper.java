package com.kaiser.messenger_server.modules.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import com.kaiser.messenger_server.annotations.IgnoreAuditableFields;
import com.kaiser.messenger_server.modules.user.dto.CreateUserRequest;
import com.kaiser.messenger_server.modules.user.dto.UpdateUserRequest;
import com.kaiser.messenger_server.modules.user.dto.UserResponse;
import com.kaiser.messenger_server.modules.user.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @IgnoreAuditableFields
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "track", ignore = true)
    @Mapping(target = "codeExpire", ignore = true)
    @Mapping(target = "codeId", ignore = true)
    User toCreateUser(CreateUserRequest request);
    
    UserResponse toUserResponse(User user);

    @IgnoreAuditableFields
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "track", ignore = true)
    @Mapping(target = "codeExpire", ignore = true)
    @Mapping(target = "codeId", ignore = true)
    void toUpdateUser(@MappingTarget User user, UpdateUserRequest request);
}
