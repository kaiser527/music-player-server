package com.kaiser.messenger_server.dto.request;

import com.kaiser.messenger_server.enums.AccountType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateUserRequest {
    @Email(message = "EMAIL_INVALID")
    String email;

    @Size(min = 4, message = "USERNAME_INVALID")
    String username;

    @Size(min = 6, message = "PASSWORD_INVALID")
    String password;

    @Builder.Default
    String image = "default-1752056150533.png";

    @Builder.Default
    Boolean isActive = false;

    @Builder.Default
    AccountType accountType = AccountType.LOCAL;

    String roleId;
}
