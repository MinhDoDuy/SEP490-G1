package com.ffood.g1.service;

import com.ffood.g1.exception.SpringBootFileUploadException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileS3Service {
    String uploadFile(MultipartFile multipartFile) throws SpringBootFileUploadException, IOException;

    Object downloadFile(String fileName) throws SpringBootFileUploadException, IOException;

    boolean delete(String fileName);
}
