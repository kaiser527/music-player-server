package com.kaiser.messenger_server.modules.role.dto;

import com.kaiser.messenger_server.utils.FilterRequest;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleFilterRequest extends FilterRequest {
    String name;

    Boolean isActive;
}
