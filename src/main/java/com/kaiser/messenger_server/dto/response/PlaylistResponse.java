package com.kaiser.messenger_server.dto.response;

import java.time.LocalDateTime;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlaylistResponse {
    String id;
    
    String name;

    LocalDateTime createdAt;

    LocalDateTime updatedAt;

    Set<TrackResponse> track;

    UserTrackResponse user;
}
