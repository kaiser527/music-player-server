package com.kaiser.messenger_server.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import com.kaiser.messenger_server.dto.request.permission.PermissionRequest;
import com.kaiser.messenger_server.dto.response.PermissionResponse;
import com.kaiser.messenger_server.entities.Permission;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    Permission toCreatePermission(PermissionRequest request);

    PermissionResponse toPermissionResponse(Permission permission);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    void toUpdatePermission(@MappingTarget Permission permission, PermissionRequest request);
}
