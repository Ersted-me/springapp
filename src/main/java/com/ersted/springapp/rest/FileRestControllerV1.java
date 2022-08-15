package com.ersted.springapp.rest;

import com.ersted.springapp.dto.FileDto;
import com.ersted.springapp.model.Action;
import com.ersted.springapp.model.Event;
import com.ersted.springapp.model.File;
import com.ersted.springapp.model.User;
import com.ersted.springapp.service.EventService;
import com.ersted.springapp.service.FileService;
import com.ersted.springapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/v1/files/")
public class FileRestControllerV1 {

    private final FileService fileService;
    private final EventService eventService;
    private final UserService userService;

    @Autowired
    public FileRestControllerV1(
            FileService fileService,
            EventService eventService,
            UserService userService) {

        this.fileService = fileService;
        this.eventService = eventService;
        this.userService = userService;
    }

    @GetMapping("{id}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable("id") Long id) {
        String filename = fileService.getOriginalFileNameById(id);

        if (filename == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        byte[] bytes = fileService.getById(id);

        if (bytes == null)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", MediaType.ALL_VALUE);
        headers.add("Content-Disposition", "attachment; filename=" + filename);

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);

        File file = fileService.getFileById(id);
        Event event = Event.builder()
                .file(file)
                .user(user)
                .action(Action.DOWNLOAD)
                .build();
        eventService.create(event);

        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(bytes);
    }

    @GetMapping("")
    public ResponseEntity<List<FileDto>> getAllFiles() {
        List<File> all = fileService.getAll();

        if (all == null)
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);

        List<FileDto> dtos = all.stream()
                .map(FileDto::fromFile)
                .collect(Collectors.toList());

        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<File> deleteUser(@PathVariable("id") Long id) {

        File file = fileService.getFileById(id);

        boolean isDeleted = fileService.deleteById(id);

        if(!isDeleted){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);

        Event event = Event.builder()
                .file(file)
                .user(user)
                .action(Action.DELETE)
                .build();
        eventService.create(event);

        return new ResponseEntity<>(HttpStatus.OK);

    }

    @PostMapping("")
    public ResponseEntity<FileDto> saveFile(@RequestParam("file") MultipartFile multipartFile){
        File file = fileService.create(multipartFile);

        if(file == null)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);

        Event event = Event.builder()
                .file(file)
                .user(user)
                .action(Action.LOAD)
                .build();
        eventService.create(event);

        FileDto dto = FileDto.fromFile(file);

        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @PutMapping("{id}")
    public ResponseEntity<FileDto> updateFile(
            @PathVariable("id") Long id,
            @RequestParam("file") MultipartFile multipartFile){

        File file = fileService.update(multipartFile, id);

        if(file == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByUsername(username);

        Event event = Event.builder()
                .file(file)
                .user(user)
                .action(Action.UPDATE)
                .build();
        eventService.create(event);

        FileDto dto = FileDto.fromFile(file);

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

}
