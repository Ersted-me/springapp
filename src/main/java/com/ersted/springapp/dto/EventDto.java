package com.ersted.springapp.dto;

import com.ersted.springapp.model.Action;
import com.ersted.springapp.model.Event;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class EventDto {
    private Long id;
    private UserDto user;
    private FileDto file;
    private Action action;
    private LocalDateTime dateTime;

    public static EventDto fromEvent(Event event) {
        return EventDto.builder()
                .id(event.getId())
                .user(UserDto.fromUser(event.getUser()))
                .file(FileDto.fromFile(event.getFile()))
                .action(event.getAction())
                .dateTime(event.getDate())
                .build();
    }

    public Event toEvent(){
        return Event.builder()
                .id(id)
                .user(user.toUser())
                .file(file.toFile())
                .action(action)
                .date(dateTime)
                .build();
    }

}
