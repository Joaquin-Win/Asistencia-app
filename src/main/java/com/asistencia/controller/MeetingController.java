package com.asistencia.controller;

import com.asistencia.model.AppUser;
import com.asistencia.model.Meeting;
import com.asistencia.service.AttendanceService;
import com.asistencia.service.MeetingService;
import com.asistencia.service.TaskService;
import com.asistencia.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/meetings")
public class MeetingController {

    private final MeetingService meetingService;
    private final AttendanceService attendanceService;
    private final TaskService taskService;
    private final UserService userService;

    public MeetingController(MeetingService meetingService, AttendanceService attendanceService,
            TaskService taskService, UserService userService) {
        this.meetingService = meetingService;
        this.attendanceService = attendanceService;
        this.taskService = taskService;
        this.userService = userService;
    }

    @GetMapping
    public String listMeetings(Authentication auth, Model model) {
        AppUser user = userService.getOrDefault(auth.getName());
        List<Meeting> meetings = meetingService.findAll();

        model.addAttribute("currentUser", user);
        model.addAttribute("meetings", meetings);
        model.addAttribute("attendanceService", attendanceService);
        model.addAttribute("username", auth.getName());
        return "meetings/list";
    }

    @GetMapping("/{id}")
    public String meetingDetail(@PathVariable Long id, Authentication auth, Model model) {
        Meeting meeting = meetingService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Encuentro no encontrado: " + id));
        AppUser user = userService.getOrDefault(auth.getName());

        model.addAttribute("currentUser", user);
        model.addAttribute("meeting", meeting);
        model.addAttribute("tasks", taskService.findByMeeting(id));
        model.addAttribute("hasRegistered", attendanceService.hasRegistered(id, auth.getName()));
        model.addAttribute("attendeeCount", attendanceService.countByMeeting(id));
        return "meetings/detail";
    }
}
