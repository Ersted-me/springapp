package com.ersted.springapp.service;

import com.ersted.springapp.model.File;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {
    File create(MultipartFile obj);

    byte[] getById(Long id);

    File getFileById(Long id);

    String getOriginalFileNameById(Long id);

    File update(MultipartFile obj, Long id);

    boolean deleteById(Long id);

    List<File> getAll();
}
