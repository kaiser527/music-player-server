package com.kaiser.messenger_server.modules.role.entity;

import java.util.Set;
import com.kaiser.messenger_server.modules.permission.entity.Permission;
import com.kaiser.messenger_server.modules.user.entity.User;
import com.kaiser.messenger_server.utils.Model;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
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
public class Role extends Model {
    @NonNull
    String name;

    @NonNull
    String description;

    @NonNull
    Boolean isActive;

    @ManyToMany
    Set<Permission> permission;

    @OneToMany(mappedBy = "role", fetch = FetchType.EAGER)
    @EqualsAndHashCode.Exclude
    Set<User> user;
}
