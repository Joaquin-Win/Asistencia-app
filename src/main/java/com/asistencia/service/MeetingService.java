package com.asistencia.service;

import com.asistencia.model.Meeting;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class MeetingService {

    private final Map<Long, Meeting> meetings = new LinkedHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public MeetingService() {
        initSampleData();
    }

    private void initSampleData() {
        // Datos de prueba eliminados para paso a producción.
    }

    public List<Meeting> findAll() {
        return new ArrayList<>(meetings.values());
    }

    public Optional<Meeting> findById(Long id) {
        return Optional.ofNullable(meetings.get(id));
    }

    public Meeting save(Meeting meeting) {
        if (meeting.getId() == null) {
            meeting.setId(idCounter.getAndIncrement());
        }
        meetings.put(meeting.getId(), meeting);
        return meeting;
    }

    public void toggleLock(Long id) {
        Meeting meeting = meetings.get(id);
        if (meeting != null) {
            meeting.setLocked(!meeting.isLocked());
        }
    }

    public void delete(Long id) {
        meetings.remove(id);
    }
}
