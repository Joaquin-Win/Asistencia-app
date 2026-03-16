package com.asistencia.model;

import java.time.LocalDateTime;

public class AttendanceRecord {

    private Long id;
    private Long meetingId;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String studentId; // legajo
    private String career;
    private LocalDateTime registeredAt;

    public AttendanceRecord() {}

    public AttendanceRecord(Long id, Long meetingId, String username,
                            String firstName, String lastName, String email, String studentId, String career, LocalDateTime registeredAt) {
        this.id = id;
        this.meetingId = meetingId;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.studentId = studentId;
        this.career = career;
        this.registeredAt = registeredAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getMeetingId() { return meetingId; }
    public void setMeetingId(Long meetingId) { this.meetingId = meetingId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getCareer() { return career; }
    public void setCareer(String career) { this.career = career; }

    public String getDisplayName() { return firstName + " " + lastName; }

    public LocalDateTime getRegisteredAt() { return registeredAt; }
    public void setRegisteredAt(LocalDateTime registeredAt) { this.registeredAt = registeredAt; }
}
