package com.kaiser.messenger_server.modules.permission.dto;

import com.kaiser.messenger_server.enums.ApiMethod;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PermissionRequest {
    String name;

    @Pattern(regexp = "^/api/v1/[a-zA-Z0-9_-]+(/.*)?$", message = "INVALID_API_PATH")
    String apiPath;

    ApiMethod method;

    String module;
}
