package com.kaiser.messenger_server.modules.user.dto;

import java.time.LocalDateTime;
import java.util.Set;

import com.kaiser.messenger_server.modules.role.dto.RoleResponse;
import com.kaiser.messenger_server.modules.track.dto.TrackUserResponse;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    String id;

    String email;

    String username;

    String image;

    Boolean isActive;

    String accountType;

    LocalDateTime createdAt;

    LocalDateTime updatedAt;

    RoleResponse role;

    Set<TrackUserResponse> track;
}
