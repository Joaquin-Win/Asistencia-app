package com.asistencia.controller;

import com.asistencia.model.AppUser;
import com.asistencia.model.Meeting;
import com.asistencia.service.MeetingService;
import com.asistencia.service.TaskService;
import com.asistencia.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/extensionist")
@PreAuthorize("hasRole('EXTENSIONIST')")
public class TaskController {

    private final TaskService taskService;
    private final MeetingService meetingService;
    private final UserService userService;

    public TaskController(TaskService taskService, MeetingService meetingService, UserService userService) {
        this.taskService = taskService;
        this.meetingService = meetingService;
        this.userService = userService;
    }

    @GetMapping("/tasks/{meetingId}")
    public String uploadForm(@PathVariable Long meetingId, Authentication auth, Model model) {
        Meeting meeting = meetingService.findById(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("Encuentro no encontrado."));
        AppUser user = userService.getOrDefault(auth.getName());

        model.addAttribute("currentUser", user);
        model.addAttribute("meeting", meeting);
        model.addAttribute("existingTasks", taskService.findByMeeting(meetingId));
        return "extensionist/upload-task";
    }

    @PostMapping("/tasks/upload")
    public String upload(@RequestParam Long meetingId,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam(required = false, defaultValue = "") String fileContent,
            Authentication auth,
            RedirectAttributes redirectAttributes) {

        Meeting meeting = meetingService.findById(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("Encuentro no encontrado."));
        AppUser user = userService.getOrDefault(auth.getName());

        try {
            taskService.upload(meetingId, auth.getName(), user.getDisplayName(),
                    title, description, fileContent, meeting);
            redirectAttributes.addFlashAttribute("successMessage",
                    "¡Tarea \"" + title + "\" subida exitosamente!");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/extensionist/tasks/" + meetingId;
    }
}
