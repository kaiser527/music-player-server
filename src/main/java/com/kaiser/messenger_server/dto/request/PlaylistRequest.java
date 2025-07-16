package com.kaiser.messenger_server.dto.request;

import java.util.Set;
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
}
