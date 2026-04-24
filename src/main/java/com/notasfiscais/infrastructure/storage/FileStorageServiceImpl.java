package com.notasfiscais.infrastructure.storage;
import com.notasfiscais.application.port.in.IFileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;
@Service
public class FileStorageServiceImpl implements IFileStorageService {
    @Value("${upload.dir}")
    private String uploadDir;
    @Override
    public String save(MultipartFile file) throws Exception {
        LocalDateTime now = LocalDateTime.now();
        String year = String.valueOf(now.getYear());
        String month = String.format("%02d", now.getMonthValue());
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path targetDir = Paths.get(uploadDir, year, month);
        Files.createDirectories(targetDir);
        Files.write(targetDir.resolve(filename), file.getBytes());
        return year + "/" + month + "/" + filename;
    }
    @Override
    public byte[] read(String relativePath) throws Exception {
        return Files.readAllBytes(Paths.get(uploadDir).resolve(relativePath));
    }
    @Override
    public void delete(String relativePath) throws Exception {
        Files.deleteIfExists(Paths.get(uploadDir).resolve(relativePath));
    }
}
