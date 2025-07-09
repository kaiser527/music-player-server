package com.kaiser.messenger_server.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import com.kaiser.messenger_server.exception.AppException;
import com.kaiser.messenger_server.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PACKAGE, makeFinal = true)
public class FileUploadService {
    List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png");
    long MAX_FILE_SIZE = 5 * 1024 * 1024;

    @Value("${file.upload-dir}")
    @NonFinal
    private String UPLOAD_DIR ;

    public String uploadFile(MultipartFile file, String folderType) {
        // Validate file type
        validateFileType(file.getOriginalFilename());
        
        // Validate file size
        validateFileSize(file.getSize());
        
        // Prepare upload directory
        String uploadPath = prepareUploadDirectory(folderType);
        
        // Generate unique filename
        String finalName = generateUniqueFilename(file.getOriginalFilename());
        
        // Save file
        saveFile(file, uploadPath, finalName);
        
        return finalName;
    }

    private void validateFileType(String filename) {
        if (filename == null || !filename.contains(".")) {
            throw new AppException(ErrorCode.INVALID_FILE_NAME);
        }
        
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new AppException(ErrorCode.INVALID_FILE_TYPE);
        }
    }

    private void validateFileSize(long size) {
        if (size > MAX_FILE_SIZE) {
            throw new MaxUploadSizeExceededException(MAX_FILE_SIZE);
        }
    }

    private String prepareUploadDirectory(String folderType) {
        String folder = folderType != null ? folderType : "default";
        String uploadPath = System.getProperty("user.dir") + "/" + UPLOAD_DIR + "/" + folder;
        
        try {
            Files.createDirectories(Paths.get(uploadPath));
        } catch (IOException e) {
            throw new AppException(ErrorCode.FAILED_TO_CREATE_DIRECTORY);
        }
        
        return uploadPath;
    }

    private String generateUniqueFilename(String originalFilename) {
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String baseName = originalFilename.substring(0, originalFilename.lastIndexOf("."));
        return baseName + "-" + System.currentTimeMillis() + extension;
    }

    private void saveFile(MultipartFile file, String uploadPath, String filename) {
        try {
            Path path = Paths.get(uploadPath, filename);
            Files.copy(file.getInputStream(), path);
        } catch (IOException e) {
            throw new AppException(ErrorCode.FAILED_TO_SAVE_FILE);
        }
    }
}
