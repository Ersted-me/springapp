package com.ersted.springapp.service.impl;

import com.ersted.springapp.model.Event;
import com.ersted.springapp.model.Status;
import com.ersted.springapp.repository.EventRepository;
import com.ersted.springapp.service.EventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EventServiceImpl implements EventService {
    private EventRepository eventRepository;

    @Autowired
    public EventServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public Event create(Event event) {
        event.setStatus(Status.ACTIVE);
        event.setDate(LocalDateTime.now());

        event = eventRepository.save(event);
        log.info("IN EventServiceImpl:create - event: {} successfully created", event);
        return event;
    }

    @Override
    public Event getById(Long id) {
        Event event = eventRepository
                .findById(id)
                .filter(e -> e.getStatus().equals(Status.ACTIVE))
                .orElse(null);

        if(event == null){
            log.info("IN EventServiceImpl:getById - event with id: {} not found", id);
            return null;
        }

        log.info("IN EventServiceImpl:getById - event: {} was found", event);

        return event;
    }

    @Override
    public Event update(Event event) {
        Event current = eventRepository
                .findById(event.getId())
                .filter(e -> e.getStatus().equals(Status.ACTIVE))
                .orElse(null);

        if (current == null) {
            log.info("IN EventServiceImpl:update - event with id: {} not found", event.getId());
            return null;
        }

        event.setStatus(current.getStatus());

        current = eventRepository.save(event);
        return current;
    }

    @Override
    public boolean deleteById(Long id) {
        Event current = eventRepository
                .findById(id)
                .filter(e -> e.getStatus().equals(Status.ACTIVE))
                .orElse(null);

        if (current == null) {
            log.info("IN EventServiceImpl:deleteById - event with id: {} not found", id);
            return false;
        }
        current.setStatus(Status.DELETED);
        eventRepository.save(current);
        return true;
    }

    @Override
    public List<Event> getAll() {
        List<Event> result = eventRepository.findAll().stream()
                .filter(file -> file.getStatus().equals(Status.ACTIVE))
                .collect(Collectors.toList());
        log.info("IN AwsS3FileServiceImpl:getAll - {} files found", result.size());
        return result;
    }
}
