package com.notasfiscais.application.port.in;
import org.springframework.web.multipart.MultipartFile;
public interface IFileStorageService {
    String save(MultipartFile file) throws Exception;
    byte[] read(String relativePath) throws Exception;
    void delete(String relativePath) throws Exception;
}
