package com.kaiser.messenger_server.modules.permission.entity;

import com.kaiser.messenger_server.enums.ApiMethod;
import com.kaiser.messenger_server.utils.Model;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Permission extends Model {
    @NonNull
    String name;

    @NonNull
    String apiPath;

    @NonNull
    @Enumerated(EnumType.STRING)
    ApiMethod method;

    @NonNull
    String module;
}
