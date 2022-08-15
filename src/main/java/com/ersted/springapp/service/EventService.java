package com.ersted.springapp.service;

import com.ersted.springapp.model.Event;

import java.util.List;

public interface EventService {
    Event create(Event event);

    Event getById(Long id);

    Event update(Event event);

    boolean deleteById(Long id);

    List<Event> getAll();
}
