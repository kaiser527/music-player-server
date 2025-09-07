package com.kaiser.messenger_server.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.kaiser.messenger_server.dto.response.FileUploadResponse;
import com.kaiser.messenger_server.dto.response.share.ApiResponse;
import com.kaiser.messenger_server.enums.FileType;
import com.kaiser.messenger_server.services.FileUploadService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("file")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PACKAGE, makeFinal = true)
public class FileUploadController {
    FileUploadService fileUploadService;

    @PostMapping("/upload")
    ApiResponse<FileUploadResponse> uploadFileImage(
        @RequestParam("fileUpload") MultipartFile file,  
        @RequestHeader(value = "folder_type", required = false) String folderType,
        @RequestParam("file_type") FileType fileType
    ){
        String fileName = fileUploadService.uploadFile(file, folderType, fileType);
        
        return ApiResponse.<FileUploadResponse>builder()
            .message("File uploaded successfully")
            .result(FileUploadResponse.builder()
                .fileName(fileName)
                .build())
            .build();
    }
}
