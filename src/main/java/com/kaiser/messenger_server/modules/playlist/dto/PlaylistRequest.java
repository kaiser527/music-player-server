package com.kaiser.messenger_server.modules.playlist.dto;

import java.util.Set;
import com.kaiser.messenger_server.enums.PlaylistAction;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlaylistRequest {
    String name;

    Set<String> trackIds;

    PlaylistAction action;
}
