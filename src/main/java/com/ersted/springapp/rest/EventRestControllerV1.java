package com.ersted.springapp.rest;

import com.ersted.springapp.dto.EventDto;
import com.ersted.springapp.model.Event;
import com.ersted.springapp.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/events/")
public class EventRestControllerV1 {
    private final EventService eventService;

    @Autowired
    public EventRestControllerV1(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("{id}")
    public ResponseEntity<EventDto> getEvent(@PathVariable("id") Long eventId) {

        Event event = this.eventService.getById(eventId);

        if (event == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        EventDto result = EventDto.fromEvent(event);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<List<EventDto>> getAllEvents() {
        List<Event> all = eventService.getAll();

        if (all == null)
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);

        List<EventDto> dtos = all.stream()
                .map(EventDto::fromEvent)
                .collect(Collectors.toList());

        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Event> deleteEvent(@PathVariable("id") Long id) {
        boolean isDeleted = eventService.deleteById(id);

        return isDeleted
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PutMapping("{id}")
    public ResponseEntity<EventDto> updateEvent(@RequestBody EventDto eventDto) {
        HttpHeaders headers = new HttpHeaders();

        if (eventDto == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Event updated = this.eventService.update(eventDto.toEvent());

        EventDto updatedEventDto = EventDto.fromEvent(updated);

        return new ResponseEntity<>(updatedEventDto, headers, HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<EventDto> createEvent(@RequestBody EventDto eventDto){
        if(eventDto == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        Event created = eventService.create(eventDto.toEvent());
        EventDto createdDto = EventDto.fromEvent(created);

        return new ResponseEntity<>(createdDto, HttpStatus.CREATED);

    }
}
