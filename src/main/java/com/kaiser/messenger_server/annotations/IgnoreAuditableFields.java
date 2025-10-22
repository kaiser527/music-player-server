package com.kaiser.messenger_server.annotations;

import org.mapstruct.Mapping;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.CLASS)
@Mapping(target = "id", ignore = true)
@Mapping(target = "createdAt", ignore = true)
@Mapping(target = "updatedAt", ignore = true)
@Mapping(target = "updatedBy", ignore = true)
@Mapping(target = "createdBy", ignore = true)
public @interface IgnoreAuditableFields {
}
