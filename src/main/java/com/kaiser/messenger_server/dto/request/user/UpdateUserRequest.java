package com.kaiser.messenger_server.dto.request.user;

import com.kaiser.messenger_server.enums.AccountType;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateUserRequest {
    @Size(min = 4, message = "USERNAME_INVALID")
    String username;

    String image;

    Boolean isActive;

    AccountType accountType;

    String roleId;
}
