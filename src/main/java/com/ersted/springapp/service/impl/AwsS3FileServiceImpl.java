package com.ersted.springapp.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.ersted.springapp.model.File;
import com.ersted.springapp.model.Status;
import com.ersted.springapp.repository.FileRepository;
import com.ersted.springapp.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AwsS3FileServiceImpl implements FileService {

    private final FileRepository fileRepository;
    private final AmazonS3 s3;

    @Value("${aws.s3.bucketName}")
    private String location;

    @Autowired
    public AwsS3FileServiceImpl(FileRepository fileRepository, AmazonS3 s3) {
        this.fileRepository = fileRepository;
        this.s3 = s3;
    }


    @Override
    public File create(MultipartFile multipartFile) {
        File dbFile = File.builder()
                .fileName(multipartFile.getOriginalFilename())
                .uuid(UUID.randomUUID().toString())
                .location(location)
                .status(Status.ACTIVE)
                .build();

        if (!isUploadFileToAwsS3(multipartFile, dbFile.getUuid())) {
            log.error("IN AwsS3FileServiceImpl:create - file: {} was not uploaded", dbFile);
            return null;
        }

        dbFile = fileRepository.save(dbFile);
        log.info("IN AwsS3FileServiceImpl:create - file: {} successfully created", dbFile);

        return dbFile;
    }

    @Override
    public byte[] getById(Long id) {
        File foundFile = fileRepository
                .findById(id)
                .filter(file -> file.getStatus().equals(Status.ACTIVE))
                .orElse(null);

        if (foundFile == null) {
            log.warn("IN AwsS3FileServiceImpl:getById - no file found by id: {}", id);
            return null;
        }
        log.info("IN AwsS3FileServiceImpl:getById - file: {} found by id: {}", foundFile, id);

        return downloadFileFromAwsS3(foundFile);
    }

    @Override
    public File getFileById(Long id) {
        File file = fileRepository
                .findById(id)
                .filter(f -> f.getStatus().equals(Status.ACTIVE))
                .orElse(null);
        if (file == null) {
            log.warn("IN AwsS3FileServiceImpl:getFileById - no file found by id: {}", id);
            return null;
        }
        return file;
    }

    @Override
    public String getOriginalFileNameById(Long id) {
        File file = fileRepository
                .findById(id)
                .filter(f -> f.getStatus().equals(Status.ACTIVE))
                .orElse(null);
        if (file == null) {
            log.warn("IN AwsS3FileServiceImpl:getOriginalFileNameById - no file found by id: {}", id);
            return null;
        }
        return file.getFileName();
    }

    @Override
    public File update(MultipartFile multipartFile, Long id) {
        File current = fileRepository.findById(id)
                .filter(f -> f.getStatus().equals(Status.ACTIVE))
                .orElse(null);

        if (current == null) {
            log.warn("IN AwsS3FileServiceImpl:update - file with id: {} not found", id);
            return null;
        }
        current.setStatus(Status.DELETED);

        File updated = File.builder()
                .fileName(multipartFile.getOriginalFilename())
                .uuid(UUID.randomUUID().toString())
                .location(location)
                .status(Status.ACTIVE)
                .build();

        if (!isUploadFileToAwsS3(multipartFile, updated.getUuid())) {
            log.error("IN AwsS3FileServiceImpl:update - file: {} was not uploaded", updated);
            return null;
        }

        updated = fileRepository.save(updated);

        log.info("IN AwsS3FileServiceImpl:update - previousFile: {} currentFile: {}", current, updated);
        return updated;
    }

    @Override
    public boolean deleteById(Long id) {
        File active = fileRepository.findById(id).orElse(null);

        if (active == null) {
            log.warn("IN AwsS3FileServiceImpl:deleteById - file with id: {} not found", id);
            return false;
        }
        active.setStatus(Status.DELETED);

        File deleted = fileRepository.save(active);

        log.info("IN AwsS3FileServiceImpl:deleteById - file: {} successfully deleted", deleted);
        return true;
    }

    @Override
    public List<File> getAll() {
        List<File> result = fileRepository.findAll();
        log.info("IN AwsS3FileServiceImpl:getAll - {} files found", result.size());
        return result.stream()
                .filter(file -> file.getStatus().equals(Status.ACTIVE))
                .collect(Collectors.toList());
    }

    private boolean isUploadFileToAwsS3(MultipartFile multipartFile, String uuid) {
        try {
            java.io.File convFile = new java.io.File(uuid);
            FileOutputStream fos = new FileOutputStream(convFile);
            fos.write(multipartFile.getBytes());
            fos.close();

            s3.putObject(location, convFile.getName(), convFile);
            convFile.delete();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    private byte[] downloadFileFromAwsS3(File file) {
        S3Object s3Object = s3.getObject(file.getLocation(), file.getUuid());
        S3ObjectInputStream stream = s3Object.getObjectContent();
        try {
            return IOUtils.toByteArray(stream);
        } catch (IOException e) {
            log.error("IN AwsS3FileServiceImpl:downloadFileFromAwsS3 file: {} was not download", file);
            return null;
        }
    }

}
