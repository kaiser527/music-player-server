package com.kaiser.messenger_server.utils;

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

    String createdAtRange;

    String updatedAtRange;
}
