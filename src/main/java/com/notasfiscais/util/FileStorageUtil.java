package com.notasfiscais.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class FileStorageUtil {

    @Value("${upload.dir}")
    private String uploadDir;

    public String save(MultipartFile file) throws IOException {
        LocalDateTime now = LocalDateTime.now();
        String year = String.valueOf(now.getYear());
        String month = String.format("%02d", now.getMonthValue());
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();

        Path targetDir = Paths.get(uploadDir, year, month);
        Files.createDirectories(targetDir);
        Files.write(targetDir.resolve(filename), file.getBytes());

        return year + "/" + month + "/" + filename;
    }

    public byte[] read(String relativePath) throws IOException {
        return Files.readAllBytes(Paths.get(uploadDir).resolve(relativePath));
    }

    public void delete(String relativePath) throws IOException {
        Files.deleteIfExists(Paths.get(uploadDir).resolve(relativePath));
    }
}
