package com.ffood.g1.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.ffood.g1.exception.SpringBootFileUploadException;
import com.ffood.g1.service.FileS3Service;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

@Service
public class FileS3ServiceImpl implements FileS3Service {

    @Value("${aws.bucket.name}")
    private String bucketName;

    private final AmazonS3 s3Client;

    public FileS3ServiceImpl(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public String uploadFile(MultipartFile multipartFile) throws IOException {
        // convert multipart file  to a file
        File file = new File(multipartFile.getOriginalFilename());
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            fileOutputStream.write(multipartFile.getBytes());
        }

        // generate file name
        String fileName = generateFileName(multipartFile);

        // upload file
        PutObjectRequest request = new PutObjectRequest(bucketName, fileName, file);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("plain/" + FilenameUtils.getExtension(multipartFile.getOriginalFilename()));
        metadata.addUserMetadata("Title", "File Upload - " + fileName);
        metadata.setContentLength(file.length());
        request.setMetadata(metadata);
        s3Client.putObject(request);
        String publicUrl = s3Client.getUrl(bucketName, fileName).toString();
        // delete file
        file.delete();

        return publicUrl;
    }


    @Override
    public Object downloadFile(String fileName) throws SpringBootFileUploadException, IOException {
        return null;
    }

    @Override
    public boolean delete(String fileName) {
        return false;
    }

    private String generateFileName(MultipartFile multiPart) {
        return new Date().getTime() + "-" + multiPart.getOriginalFilename().replace(" ", "_");
    }
}
