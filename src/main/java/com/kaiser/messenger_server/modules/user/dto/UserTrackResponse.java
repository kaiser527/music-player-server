package com.kaiser.messenger_server.modules.user.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserTrackResponse {
    String id;

    String email;

    String username;

    String image;
}
