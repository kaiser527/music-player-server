package com.kaiser.messenger_server.modules.role.dto;

import java.time.LocalDateTime;
import java.util.Set;
import com.kaiser.messenger_server.modules.permission.dto.PermissionResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleResponse {
    String id;

    String name;

    String description;

    Boolean isActive;

    LocalDateTime createdAt;

    LocalDateTime updatedAt;

    Set<PermissionResponse> permission;
}
