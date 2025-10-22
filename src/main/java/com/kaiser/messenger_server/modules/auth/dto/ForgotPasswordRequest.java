package com.kaiser.messenger_server.modules.auth.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ForgotPasswordRequest {
    String codeId;

    String password;

    String confirmPassword;

    String email;
}
