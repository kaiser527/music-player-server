package com.kaiser.messenger_server.dto.request.playlist;

import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlaylistFilterRequest {
    String name;

    Boolean sortByUpdatedAt;  

    Boolean sortByCreatedAt;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) 
    LocalDate startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) 
    LocalDate endDate;
}
