package com.kaiser.messenger_server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public enum ErrorCode {
    USER_EXISTED(1001, "User is already exist", HttpStatus.BAD_REQUEST),
    USER_NOT_EXIST(1006, "User is not exist", HttpStatus.NOT_FOUND),
    UNCATEGORZIED_EXCEPTION(999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1002, "Invalid message key", HttpStatus.BAD_REQUEST),
    EMAIL_INVALID(1003, "Invalid email format", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1004, "Username must be atleast {min} letters", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1005, "Password must be at least {min} letters", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1006, "Invalid email or password", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN(1007,"Missing token at header or token is expired", HttpStatus.UNAUTHORIZED),
    PERMISSION_EXIST(1008,"Permission is already exist", HttpStatus.BAD_REQUEST),
    PERMISSION_NOT_EXIST(1010,"Permission is not exist", HttpStatus.BAD_REQUEST),
    INVALID_API_PATH(1009,"apiPath must start with '/api/v1/' followed by a module name", HttpStatus.BAD_REQUEST),
    ROLE_NOT_EXIST(1011,"Role is not exist", HttpStatus.BAD_REQUEST),
    DELETE_ADMIN_USER(1012,"Cannot delete admin user", HttpStatus.BAD_REQUEST),
    ROLE_EXIST(1013,"Role is already exist", HttpStatus.BAD_REQUEST),
    DELETE_ADMIN_ROLE(1014,"Cannot delete admin role", HttpStatus.BAD_REQUEST),
    MAX_UPLOAD_SIZE(1015,"Max upload file size is 5MB", HttpStatus.BAD_REQUEST),
    INVALID_FILE_NAME(1016,"Invalid file name", HttpStatus.BAD_REQUEST),
    INVALID_FILE_TYPE(1017,"Invalid file type", HttpStatus.BAD_REQUEST),
    FAILED_TO_CREATE_DIRECTORY(1018,"Failed to create upload directory", HttpStatus.BAD_REQUEST),
    FAILED_TO_SAVE_FILE(1019,"Failed to save file", HttpStatus.BAD_REQUEST),
    PLAYLIST_EXIST(1020,"Playlist is already exist", HttpStatus.BAD_REQUEST),
    PLAYLIST_NOT_EXIST(1021,"Playlist is not exist", HttpStatus.BAD_REQUEST),
    TRACK_EXIST(1022,"Track is already exist", HttpStatus.BAD_REQUEST),
    TRACK_NOT_EXIST(1023,"Track is not exist", HttpStatus.BAD_REQUEST),
    ACCESS_DENIED(1024,"You are not permitted to access this endpoint", HttpStatus.FORBIDDEN),
    ACCOUNT_NOT_ACTIVATED(1025,"Your account is not activated", HttpStatus.BAD_REQUEST),
    SEND_MAIL_FAILED(1026,"Failed to send email", HttpStatus.BAD_REQUEST),
    CODE_INVALID(1027,"Your code is expired or invalid", HttpStatus.BAD_REQUEST),
    ;

    int code;
    String message;
    HttpStatusCode statusCode;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }
}
