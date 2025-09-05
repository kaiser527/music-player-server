package com.kaiser.messenger_server.dto.request;

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
public class TrackFilterRequest {
    String title;

    String user;

    Boolean sortByUpdatedAt;  

    Boolean sortByCreatedAt;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) 
    LocalDate startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) 
    LocalDate endDate;
}
