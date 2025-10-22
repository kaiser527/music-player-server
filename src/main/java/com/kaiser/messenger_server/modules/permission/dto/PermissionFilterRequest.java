package com.kaiser.messenger_server.modules.permission.dto;

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
public class PermissionFilterRequest extends FilterRequest  {
    String name;

    String apiPath;

    String module;

    String method;
}
