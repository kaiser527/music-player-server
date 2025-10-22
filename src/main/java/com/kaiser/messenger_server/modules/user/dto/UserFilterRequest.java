package com.kaiser.messenger_server.modules.user.dto;

import com.kaiser.messenger_server.utils.FilterRequest;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserFilterRequest extends FilterRequest {
    String username;

    String email;

    String role;
}
