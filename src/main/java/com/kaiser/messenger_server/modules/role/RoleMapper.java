package com.kaiser.messenger_server.modules.role;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import com.kaiser.messenger_server.annotations.IgnoreAuditableFields;
import com.kaiser.messenger_server.modules.role.dto.RoleRequest;
import com.kaiser.messenger_server.modules.role.dto.RoleResponse;
import com.kaiser.messenger_server.modules.role.entity.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @IgnoreAuditableFields
    @Mapping(target = "permission", ignore = true)
    @Mapping(target = "user", ignore = true)
    Role toCreateRole(RoleRequest request);

    RoleResponse toRoleResponse(Role role);

    @IgnoreAuditableFields
    @Mapping(target = "permission", ignore = true)
    @Mapping(target = "user", ignore = true)
    void toUpdateRole(@MappingTarget Role role, RoleRequest request);
}
