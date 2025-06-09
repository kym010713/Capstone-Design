package com.jang.ykk.login.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jang.ykk.login.entity.SharedEvent;
import com.jang.ykk.login.model.Event;
import com.jang.ykk.login.repository.EventRepository;

@Service
public class EventService {

    private final EventRepository eventRepository;

    @Autowired
    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }
    
    public Event addResidentEvent(Long userId, LocalDate eventDate, String title, String description) {
        Event newEvent = new Event();
        newEvent.setUserId(userId);
        newEvent.setEventDate(eventDate);
        newEvent.setTitle(title);
        newEvent.setDescription(description);
        newEvent.setDelYn("n");
        
        eventRepository.saveResidentEvent(newEvent);
        return newEvent;
    }


	/*
	 * public Event addEvent(LocalDate eventDate, String title, String description)
	 * { Event newEvent = new Event(); newEvent.setEventDate(eventDate);
	 * newEvent.setTitle(title); newEvent.setDescription(description);
	 * newEvent.setDelYn("n");
	 * 
	 * eventRepository.save(newEvent); return newEvent; }
	 */

    public List<Event> findByMonthAndUserId(Long userId, int year, int month) {
        return eventRepository.findByMonthAndUserId(userId, year, month);
    }

    public List<Event> findByDateAndUserId(LocalDate date, Long userId) {
        return eventRepository.findByDateAndUserId(date, userId);
    }
    
    public List<Event> convertSharedEventsToEvents(List<SharedEvent> sharedEvents) {
        return sharedEvents.stream()
            .map(sharedEvent -> new Event(
                sharedEvent.getId(),
                sharedEvent.getEventDate().toLocalDate(),
                sharedEvent.getTitle(),
                sharedEvent.getDescription(),
                "n",
                null
            ))
            .collect(Collectors.toList());
    }


    public boolean updateEvent(Long userId, Long id, String title, String description) {
        Event event = eventRepository.findById(id);
        if (event != null && event.getUserId().equals(userId)) {
            event.setTitle(title);
            event.setDescription(description);
            eventRepository.saveResidentEvent(event);
            return true;
        }
        return false;
    }

    public boolean deleteEvent(Long userId, Long id) {
        Event event = eventRepository.findById(id);
        if (event != null && event.getUserId().equals(userId)) {
            eventRepository.delete(id);
            return true;
        }
        return false;
    }
}

