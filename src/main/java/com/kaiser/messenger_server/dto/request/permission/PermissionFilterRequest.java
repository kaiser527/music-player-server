package com.kaiser.messenger_server.dto.request.permission;

import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PermissionFilterRequest  {
    String name;

    String apiPath;

    String module;

    String method;

    Boolean sortByUpdatedAt;  

    Boolean sortByCreatedAt;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) 
    LocalDate startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) 
    LocalDate endDate;
}
