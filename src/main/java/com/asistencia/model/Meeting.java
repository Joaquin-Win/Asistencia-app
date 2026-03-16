package com.asistencia.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Meeting {

    private Long id;
    private String title;
    private String description;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String location;
    private boolean locked;
    private List<String> materials;

    public Meeting() {
        this.materials = new ArrayList<>();
    }

    public Meeting(Long id, String title, String description, LocalDate date,
                   LocalTime startTime, LocalTime endTime, String location,
                   boolean locked, List<String> materials) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.locked = locked;
        this.materials = materials != null ? materials : new ArrayList<>();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public boolean isLocked() { return locked; }
    public void setLocked(boolean locked) { this.locked = locked; }

    public List<String> getMaterials() { return materials; }
    public void setMaterials(List<String> materials) { this.materials = materials; }

    public String getStatus() {
        LocalDate today = LocalDate.now();
        if (locked) return "BLOQUEADA";
        if (date.isBefore(today)) return "PASADA";
        if (date.isEqual(today)) return "HOY";
        return "PRÓXIMA";
    }
}
