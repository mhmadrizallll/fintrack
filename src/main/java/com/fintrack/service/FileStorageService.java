package com.fintrack.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path uploadPath;

    private static final Set<String> ALLOWED_EXTENSIONS =
            Set.of(
                    "jpg",
                    "jpeg",
                    "png",
                    "pdf"
            );

    public FileStorageService(
            @Value("${fintrack.upload.dir}") String uploadDir) {

        try {

            this.uploadPath =
                    Paths.get(uploadDir)
                            .toAbsolutePath()
                            .normalize();

            Files.createDirectories(uploadPath);

        } catch (IOException e) {

            throw new RuntimeException(
                    "Gagal membuat folder upload.",
                    e
            );
        }
    }

    public String storeFile(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "File tidak boleh kosong."
            );
        }

        String originalFilename =
                file.getOriginalFilename();

        if (originalFilename == null
                || originalFilename.isBlank()) {

            throw new IllegalArgumentException(
                    "Nama file tidak valid."
            );
        }

        String extension =
                getExtension(originalFilename);

        if (!ALLOWED_EXTENSIONS.contains(
                extension.toLowerCase())) {

            throw new IllegalArgumentException(
                    "Format file tidak diperbolehkan. "
                    + "Gunakan JPG, JPEG, PNG, atau PDF."
            );
        }
        
        String contentType =
        file.getContentType();

        if (contentType == null
                || !isAllowedContentType(contentType)) {
        
            throw new IllegalArgumentException(
                    "Tipe file tidak diperbolehkan."
            );
        }

        String storedFilename =
                UUID.randomUUID()
                        + "."
                        + extension.toLowerCase();

        Path targetPath =
                uploadPath.resolve(storedFilename)
                        .normalize();

        if (!targetPath.startsWith(uploadPath)) {

            throw new IllegalArgumentException(
                    "Lokasi file tidak valid."
            );
        }

        try {

            Files.copy(
                    file.getInputStream(),
                    targetPath,
                    StandardCopyOption.REPLACE_EXISTING
            );

            return storedFilename;

        } catch (IOException e) {

            throw new RuntimeException(
                    "Gagal menyimpan file.",
                    e
            );
        }
    }

    public Path getFile(String filename) {

        if (filename == null
                || filename.isBlank()) {

            throw new IllegalArgumentException(
                    "Nama file tidak valid."
            );
        }

        Path filePath =
                uploadPath
                        .resolve(filename)
                        .normalize();

        if (!filePath.startsWith(uploadPath)) {

            throw new IllegalArgumentException(
                    "Lokasi file tidak valid."
            );
        }

        return filePath;
    }

    public void deleteFile(String filename) {

        if (filename == null
                || filename.isBlank()) {

            return;
        }

        try {

            Path filePath =
                    getFile(filename);

            Files.deleteIfExists(filePath);

        } catch (IOException e) {

            throw new RuntimeException(
                    "Gagal menghapus file.",
                    e
            );
        }
    }

    private String getExtension(
            String filename) {

        int lastDot =
                filename.lastIndexOf('.');

        if (lastDot == -1
                || lastDot == filename.length() - 1) {

            return "";
        }

        return filename
                .substring(lastDot + 1)
                .toLowerCase();
    }
    
    private boolean isAllowedContentType(
        String contentType) {

    return contentType.equalsIgnoreCase("image/jpeg")
            || contentType.equalsIgnoreCase("image/png")
            || contentType.equalsIgnoreCase("application/pdf");
}
}