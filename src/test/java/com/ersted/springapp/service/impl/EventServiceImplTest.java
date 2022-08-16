package com.ersted.springapp.service.impl;

import com.ersted.springapp.model.Event;
import com.ersted.springapp.model.Status;
import com.ersted.springapp.repository.EventRepository;
import com.ersted.springapp.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class EventServiceImplTest {
    @Mock
    private EventRepository eventRepository;
    private EventService eventService;


    @BeforeEach
    void setUp() {
        eventService = new EventServiceImpl(eventRepository);
    }

    @Test
    void createEventThenReturnCreatedEvent() {
        Event newEvent = new Event();
        Event expected = Event.builder()
                .id(1L)
                .status(Status.ACTIVE)
                .build();

        newEvent.setId(1L);
        when(eventRepository.save(newEvent)).thenReturn(newEvent);

        Event actual = eventService.create(newEvent);
        actual.setDate(null);
        assertEquals(expected, actual);
    }

    @Test
    void createEventWhenReferenceIsNullThenReturnNull() {
        Event newEvent = null;
        Event expected = null;

        Event actual = eventService.create(newEvent);
        assertEquals(expected, actual);
    }

    @Test
    void getExistEventByIdThenReturnEvent() {
        Long existId = 1L;
        Event existEvent = Event.builder()
                .id(existId)
                .status(Status.ACTIVE)
                .build();
        Event expect = Event.builder()
                .id(existId)
                .status(Status.ACTIVE)
                .build();
        when(eventRepository.findById(existId))
                .thenReturn(Optional.of(existEvent));

        Event actual = eventService.getById(existId);
        assertEquals(expect, actual);
    }

    @Test
    void getNotExistEventByIdThenReturnNull() {
        Long notExistId = 1L;
        Event expected = null;
        when(eventRepository.findById(notExistId))
                .thenReturn(Optional.empty());
        Event actual = eventService.getById(notExistId);
        assertEquals(expected, actual);
    }

    @Test
    void updateNotExistEventThenReturnNull(){
        Long notExistId = 1L;
        Event notExistEvent = Event.builder()
                .id(notExistId)
                .build();
        Event expected = null;
        when(eventRepository.findById(notExistId))
                .thenReturn(Optional.empty());

        Event actual = eventService.update(notExistEvent);
        assertEquals(expected, actual);
    }

    @Test
    void updateExistEventThenReturnUpdatedEvent(){
        Long existId = 1L;
        Event existEvent = Event.builder()
                .id(existId)
                .status(Status.DELETED)
                .build();
        Event expected = Event.builder()
                .id(existId)
                .status(Status.ACTIVE)
                .build();
        when(eventRepository.findById(existId))
                .thenReturn(Optional.of(existEvent));
        when(eventRepository.save(existEvent))
                .thenReturn(existEvent);
        Event actual = eventService.update(expected);
        assertEquals(expected, actual);
    }

    @Test
    void deleteNotExistEventByIdThenReturnNull(){
        Long notExistId = 1L;

        when(eventRepository.findById(notExistId))
                .thenReturn(Optional.empty());

        boolean actual = eventService.deleteById(notExistId);

        assertFalse(actual);
    }

    @Test
    void deleteExistEventThenReturnTrue(){
        Long existId = 1L;
        Event existEvent = Event.builder()
                .status(Status.ACTIVE)
                .build();

        when(eventRepository.findById(existId))
                .thenReturn(Optional.of(existEvent));

        boolean actual = eventService.deleteById(existId);
        assertTrue(actual);
    }

    @Test
    void getEventListWhenListNotEmptyThenReturnNotEmptyList() {
        Event existEvent = Event.builder().status(Status.ACTIVE).build();
        List<Event> events = Collections.singletonList(existEvent);
        List<Event> expected = Collections.singletonList(existEvent);
        when(eventRepository.findAll())
                .thenReturn(events);
        List<Event> actual = eventService.getAll();
        assertEquals(expected, actual);
    }

    @Test
    void getEventListWhenDBIsEmptyThenReturnEmptyList(){
        List<Event> expected = Collections.emptyList();
        when(eventRepository.findAll())
                .thenReturn(expected);
        List<Event> actual = eventService.getAll();
        assertEquals(expected, actual);
    }

}