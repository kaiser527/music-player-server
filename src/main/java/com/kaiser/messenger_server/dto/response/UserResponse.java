package com.kaiser.messenger_server.dto.response;

import java.time.LocalDateTime;
import java.util.Set;
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
