package com.kaiser.messenger_server.modules.playlist.entity;

import java.util.Set;
import jakarta.persistence.*;
import com.kaiser.messenger_server.modules.track.entity.Track;
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
public class Playlist extends Model {
    @NonNull
    String name;

    @ManyToOne
    User user;

    @ManyToMany
    Set<Track> track;
}
