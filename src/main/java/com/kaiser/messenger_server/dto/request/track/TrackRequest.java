package com.kaiser.messenger_server.dto.request.track;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TrackRequest {
    String title;

    String url;

    String artwork;

    String userId;
}
