package com.kaiser.messenger_server.dto.response.track;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TrackUserResponse {
    String id;

    String title;

    String url;

    String artwork;
}
