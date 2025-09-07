package com.kaiser.messenger_server.dto.response.track;

import java.time.LocalDateTime;
import com.kaiser.messenger_server.dto.response.user.UserTrackResponse;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TrackResponse {
    String id;

    String title;

    String url;

    String artwork;
    
    LocalDateTime createdAt;

    LocalDateTime updatedAt;

    UserTrackResponse user;
}
