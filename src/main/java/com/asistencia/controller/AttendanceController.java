package com.asistencia.controller;

import com.asistencia.model.Meeting;
import com.asistencia.service.AttendanceService;
import com.asistencia.service.MeetingService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final MeetingService meetingService;

    public AttendanceController(AttendanceService attendanceService,
            MeetingService meetingService) {
        this.attendanceService = attendanceService;
        this.meetingService = meetingService;
    }

    @PostMapping("/register/{meetingId}")
    public String register(@PathVariable Long meetingId,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String email,
            @RequestParam String studentId,
            @RequestParam String career,
            Authentication auth,
            RedirectAttributes redirectAttributes) {
        Meeting meeting = meetingService.findById(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("Encuentro no encontrado."));

        try {
            attendanceService.register(meetingId, auth.getName(), firstName, lastName, email, studentId, career, meeting);
            redirectAttributes.addFlashAttribute("successMessage",
                    "¡Asistencia registrada exitosamente para \"" + meeting.getTitle() + "\"!");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/meetings/" + meetingId;
    }
}
