package com.asistencia.service;

import com.asistencia.model.Meeting;
import com.asistencia.model.TaskUpload;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class TaskService {

    private final Map<Long, List<TaskUpload>> tasksByMeeting = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public TaskService(MeetingService meetingService) {
        initSampleData(meetingService);
    }

    private void initSampleData(MeetingService meetingService) {
        // Datos de prueba eliminados para paso a producción.
    }

    private void saveInternal(TaskUpload task) {
        if (task.getId() == null)
            task.setId(idCounter.getAndIncrement());
        tasksByMeeting.computeIfAbsent(task.getMeetingId(), k -> new ArrayList<>()).add(task);
    }

    public List<TaskUpload> findByMeeting(Long meetingId) {
        return tasksByMeeting.getOrDefault(meetingId, Collections.emptyList());
    }

    public TaskUpload upload(Long meetingId, String username, String displayName,
            String title, String description, String fileContent,
            Meeting meeting) {
        LocalDate meetingDate = meeting.getDate();
        LocalDate deadline = meetingDate.minusDays(2);
        if (!LocalDate.now().isBefore(deadline)) {
            throw new IllegalStateException(
                    "No se puede subir la tarea. El plazo límite era 48 horas antes del encuentro (" + deadline + ").");
        }
        TaskUpload task = new TaskUpload(null, meetingId, username, displayName,
                title, description, fileContent, LocalDateTime.now());
        saveInternal(task);
        return task;
    }
}
