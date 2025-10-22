package com.kaiser.messenger_server.modules.playlist.dto;

import java.time.LocalDateTime;
import java.util.Set;
import com.kaiser.messenger_server.modules.track.dto.TrackResponse;
import com.kaiser.messenger_server.modules.user.dto.UserTrackResponse;
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
