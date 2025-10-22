package com.kaiser.messenger_server.modules.track.entity;

import jakarta.persistence.*;
import com.kaiser.messenger_server.modules.user.entity.User;
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
public class Track extends Model {
    @NonNull
    String title;

    @NonNull
    String url;

    @NonNull
    String artwork;

    @ManyToOne
    User user;
}
