package com.kaiser.messenger_server.entities;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.kaiser.messenger_server.enums.AccountType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

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

    String createdBy;

    String updatedBy;

    @CreationTimestamp
    LocalDateTime createdAt;

    @UpdateTimestamp
    LocalDateTime updatedAt;

    @ManyToOne
    Role role;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    @EqualsAndHashCode.Exclude
    Set<Track> track;
}
