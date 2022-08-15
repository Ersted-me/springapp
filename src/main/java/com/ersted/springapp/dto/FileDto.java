package com.ersted.springapp.dto;

import com.ersted.springapp.model.File;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class FileDto {
    private Long id;
    private String originalFileName;

    public File toFile(){
        return File.builder()
                .id(id)
                .fileName(originalFileName)
                .build();
    }

    public static FileDto fromFile(File file){
        return FileDto.builder()
                .id(file.getId())
                .originalFileName(file.getFileName())
                .build();
    }
}
