package com.asistencia.service;

import com.asistencia.model.AttendanceRecord;
import com.asistencia.model.Meeting;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class AttendanceService {

    private final Map<Long, List<AttendanceRecord>> recordsByMeeting = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public AttendanceService(MeetingService meetingService) {
        initSampleData(meetingService);
    }

    private void initSampleData(MeetingService meetingService) {
        // Datos de prueba eliminados para paso a producción.
    }

    private void registerInternal(Long meetingId, String username, String firstName, String lastName, String email, String studentId, String career) {
        recordsByMeeting.computeIfAbsent(meetingId, k -> new ArrayList<>())
                .add(new AttendanceRecord(idCounter.getAndIncrement(), meetingId, username, firstName, lastName, email, studentId, career,
                        LocalDateTime.now().minusHours(new Random().nextInt(72))));
    }

    public List<AttendanceRecord> findByMeeting(Long meetingId) {
        return recordsByMeeting.getOrDefault(meetingId, Collections.emptyList());
    }

    public boolean hasRegistered(Long meetingId, String username) {
        return findByMeeting(meetingId).stream()
                .anyMatch(r -> r.getUsername().equals(username));
    }

    public void register(Long meetingId, String username, String firstName, String lastName, String email, String studentId, String career, Meeting meeting) {
        if (meeting.isLocked()) {
            throw new IllegalStateException("Esta reunión está bloqueada. No se puede registrar asistencia.");
        }
        if (hasRegistered(meetingId, username)) {
            throw new IllegalStateException("Ya registraste tu asistencia para este encuentro.");
        }
        registerInternal(meetingId, username, firstName, lastName, email, studentId, career);
    }

    public int countByMeeting(Long meetingId) {
        return findByMeeting(meetingId).size();
    }
}
