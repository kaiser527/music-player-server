package com.kaiser.messenger_server.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import com.kaiser.messenger_server.exception.AppException;
import com.kaiser.messenger_server.exception.ErrorCode;

public final class Helper {
    private Helper() {}

    public static LocalDateTime[] parseDateRange(String range) {
        if (range == null || range.isBlank()) {
            return null;
        }
        String[] parts = range.split(",");
        if (parts.length != 2) {
            throw new AppException(ErrorCode.INVALID_DATE_FORMAT);
        }
        try {
            LocalDate startDate = LocalDate.parse(parts[0].trim());
            LocalDate endDate = LocalDate.parse(parts[1].trim());
            if (startDate.isAfter(endDate)) {
                throw new AppException(ErrorCode.INVALID_DATE_FORMAT);
            }
            return new LocalDateTime[]{
                startDate.atStartOfDay(),
                endDate.atTime(LocalTime.MAX)
            };
        } catch (Exception e) {
            throw new AppException(ErrorCode.INVALID_DATE_FORMAT);
        }
    }
}
