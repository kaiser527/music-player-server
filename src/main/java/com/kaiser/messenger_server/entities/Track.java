package com.kaiser.messenger_server.entities;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Track {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @NonNull
    String title;

    @NonNull
    String url;

    @NonNull
    String artwork;

    String createdBy;

    String updatedBy;

    @CreationTimestamp
    LocalDateTime createdAt;

    @UpdateTimestamp
    LocalDateTime updatedAt;

    @ManyToOne
    User user;
}
