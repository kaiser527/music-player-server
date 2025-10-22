package com.kaiser.messenger_server.utils;

import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@MappedSuperclass
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuperBuilder

public class FilterRequest {
    Boolean sortByUpdatedAt;  

    Boolean sortByCreatedAt;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) 
    LocalDate startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) 
    LocalDate endDate;
}
