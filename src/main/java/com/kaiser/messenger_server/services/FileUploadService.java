package com.kaiser.messenger_server.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import com.kaiser.messenger_server.enums.FileType;
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
    Map<FileType, List<String>> ALLOWED_EXTENSIONS = Map.of(
        FileType.IMAGE, Arrays.asList("jpg", "jpeg", "png"),
        FileType.TRACK, Arrays.asList("mp3", "wav", "ogg")
    );
    
    long MAX_FILE_SIZE_IMAGE = 5 * 1024 * 1024;
    long MAX_FILE_SIZE_TRACK = 15 * 1024 * 1024;

    @Value("${file.upload-dir}")
    @NonFinal
    private String UPLOAD_DIR_IMAGE ;

    @Value("${track.upload-dir}")
    @NonFinal
    private String UPLOAD_DIR_TRACK ;

    public String uploadFile(MultipartFile file, String folderType, FileType type) {
        // Validate file type
        validateFileType(file.getOriginalFilename(), type);
        
        // Validate file size
        validateFileSize(file.getSize(), type);
        
        // Prepare upload directory
        String uploadPath = prepareUploadDirectory(folderType, type);
        
        // Generate unique filename
        String finalName = generateUniqueFilename(file.getOriginalFilename());
        
        // Save file
        saveFile(file, uploadPath, finalName);
        
        return finalName;
    }

    private void validateFileType(String filename, FileType type) {
        if (filename == null || filename.isEmpty() || !filename.contains(".")) {
            throw new AppException(ErrorCode.INVALID_FILE_NAME);
        }
        
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        if (!ALLOWED_EXTENSIONS.get(type).contains(extension)) {
            throw new AppException(ErrorCode.INVALID_FILE_TYPE);
        }
    }

    private void validateFileSize(long size, FileType type) {
        long validSize = type == FileType.IMAGE ? MAX_FILE_SIZE_IMAGE : MAX_FILE_SIZE_TRACK;
        if (size > validSize) {
            throw new MaxUploadSizeExceededException(validSize);
        }
    }

    private String prepareUploadDirectory(String folderType, FileType type) {
        String folder = folderType != null ? folderType : "default";
        String baseDir = type == FileType.IMAGE ? UPLOAD_DIR_IMAGE : UPLOAD_DIR_TRACK;
        String uploadPath = System.getProperty("user.dir") + "/" + baseDir + "/" + folder;
        
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
