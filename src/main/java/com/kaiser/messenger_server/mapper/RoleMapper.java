package com.kaiser.messenger_server.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import com.kaiser.messenger_server.dto.request.role.RoleRequest;
import com.kaiser.messenger_server.dto.response.RoleResponse;
import com.kaiser.messenger_server.entities.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "permission", ignore = true)
    Role toCreateRole(RoleRequest request);

    RoleResponse toRoleResponse(Role role);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "permission", ignore = true)
    void toUpdateRole(@MappingTarget Role role, RoleRequest request);
}
