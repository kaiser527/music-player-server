package com.kaiser.messenger_server.modules.user.entity;

import java.util.Date;
import java.util.Set;
import jakarta.persistence.*;
import com.kaiser.messenger_server.enums.AccountType;
import com.kaiser.messenger_server.modules.role.entity.Role;
import com.kaiser.messenger_server.modules.track.entity.Track;
import com.kaiser.messenger_server.utils.Model;
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
public class User extends Model {
    @NonNull
    String email;

    @NonNull
    String password;

    @NonNull
    String username;

    @NonNull
    String image;

    @NonNull
    Boolean isActive;

    String codeId;

    Date codeExpire;

    @NonNull
    @Enumerated(EnumType.STRING)
    AccountType accountType;

    @ManyToOne
    Role role;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    @EqualsAndHashCode.Exclude
    Set<Track> track;
}
