package com.kaiser.messenger_server.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kaiser.messenger_server.dto.response.user.UserResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthResponse {
    String access_token;

    String refresh_token;

    boolean isAuthenticated;

    UserResponse user;
}
