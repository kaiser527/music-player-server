package com.kaiser.messenger_server.modules.auth.entity;

import java.util.Date;
import com.kaiser.messenger_server.utils.Model;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BlacklistToken extends Model {
    Date expiryTime;
}
