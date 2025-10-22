package com.kaiser.messenger_server.modules.permission;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import com.kaiser.messenger_server.annotations.IgnoreAuditableFields;
import com.kaiser.messenger_server.modules.permission.dto.PermissionRequest;
import com.kaiser.messenger_server.modules.permission.dto.PermissionResponse;
import com.kaiser.messenger_server.modules.permission.entity.Permission;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    @IgnoreAuditableFields
    Permission toCreatePermission(PermissionRequest request);

    PermissionResponse toPermissionResponse(Permission permission);

    @IgnoreAuditableFields
    void toUpdatePermission(@MappingTarget Permission permission, PermissionRequest request);
}
