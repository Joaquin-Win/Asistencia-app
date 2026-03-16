package com.asistencia.controller;

import com.asistencia.model.AppUser;
import com.asistencia.model.Meeting;
import com.asistencia.service.AttendanceService;
import com.asistencia.service.MeetingService;
import com.asistencia.service.TaskService;
import com.asistencia.service.UserService;
import com.asistencia.service.ExcelExportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final MeetingService meetingService;
    private final AttendanceService attendanceService;
    private final TaskService taskService;
    private final UserService userService;
    private final ExcelExportService excelExportService;
    private final InMemoryUserDetailsManager userDetailsManager;

    public AdminController(MeetingService meetingService, AttendanceService attendanceService,
            TaskService taskService, UserService userService, ExcelExportService excelExportService, InMemoryUserDetailsManager userDetailsManager) {
        this.meetingService = meetingService;
        this.attendanceService = attendanceService;
        this.taskService = taskService;
        this.userService = userService;
        this.excelExportService = excelExportService;
        this.userDetailsManager = userDetailsManager;
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        AppUser user = userService.getOrDefault(auth.getName());
        List<Meeting> meetings = meetingService.findAll();

        long totalAttendees = meetings.stream().mapToLong(m -> attendanceService.countByMeeting(m.getId())).sum();
        long lockedCount = meetings.stream().filter(Meeting::isLocked).count();
        long upcomingCount = meetings.stream().filter(m -> m.getDate().isAfter(java.time.LocalDate.now())).count();

        model.addAttribute("currentUser", user);
        model.addAttribute("meetings", meetings);
        model.addAttribute("attendanceService", attendanceService);
        model.addAttribute("taskService", taskService);
        model.addAttribute("totalAttendees", totalAttendees);
        model.addAttribute("lockedCount", lockedCount);
        model.addAttribute("upcomingCount", upcomingCount);
        return "admin/dashboard";
    }

    @GetMapping("/meeting/{id}")
    public String meetingDetail(@PathVariable Long id, Authentication auth, Model model) {
        Meeting meeting = meetingService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Encuentro no encontrado: " + id));
        AppUser user = userService.getOrDefault(auth.getName());

        model.addAttribute("currentUser", user);
        model.addAttribute("meeting", meeting);
        model.addAttribute("attendees", attendanceService.findByMeeting(id));
        model.addAttribute("tasks", taskService.findByMeeting(id));
        return "admin/meeting-detail";
    }

    @PostMapping("/meeting/{id}/toggle-lock")
    public String toggleLock(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        meetingService.toggleLock(id);
        Meeting m = meetingService.findById(id).orElseThrow();
        redirectAttributes.addFlashAttribute("successMessage",
                "Encuentro " + (m.isLocked() ? "bloqueado" : "desbloqueado") + " exitosamente.");
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/meetings/new")
    public String newMeetingForm(Authentication auth, Model model) {
        model.addAttribute("currentUser", userService.getOrDefault(auth.getName()));
        model.addAttribute("meeting", new Meeting());
        model.addAttribute("formTitle", "Nuevo Encuentro");
        model.addAttribute("formAction", "/admin/meetings/save");
        return "admin/meeting-form";
    }

    @GetMapping("/meetings/edit/{id}")
    public String editMeetingForm(@PathVariable Long id, Authentication auth, Model model) {
        Meeting meeting = meetingService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Encuentro no encontrado: " + id));
        model.addAttribute("currentUser", userService.getOrDefault(auth.getName()));
        model.addAttribute("meeting", meeting);
        model.addAttribute("formTitle", "Editar Encuentro");
        model.addAttribute("formAction", "/admin/meetings/save");
        return "admin/meeting-form";
    }

    @PostMapping("/meetings/save")
    public String saveMeeting(
            @RequestParam(required = false) Long id,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime,
            @RequestParam String location,
            @RequestParam(required = false, defaultValue = "false") boolean locked,
            @RequestParam(required = false) List<String> materials,
            RedirectAttributes redirectAttributes) {

        Meeting meeting = new Meeting();
        meeting.setId(id);
        meeting.setTitle(title);
        meeting.setDescription(description);
        meeting.setDate(date);
        meeting.setStartTime(startTime);
        meeting.setEndTime(endTime);
        meeting.setLocation(location);
        meeting.setLocked(locked);
        meeting.setMaterials(materials != null ? materials : List.of());
        meetingService.save(meeting);

        redirectAttributes.addFlashAttribute("successMessage", "Encuentro guardado exitosamente.");
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/meetings/delete/{id}")
    public String deleteMeeting(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        meetingService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Encuentro eliminado.");
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/meetings/export/{id}")
    public ResponseEntity<InputStreamResource> exportMeetingAttendance(@PathVariable Long id) throws Exception {
        Meeting meeting = meetingService.findById(id).orElseThrow();
        var records = attendanceService.findByMeeting(id);
        
        ByteArrayInputStream in = excelExportService.exportMeetingAttendance(meeting, records);
        
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=asistencia_encuentro_" + id + ".xlsx");
        
        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }

    @GetMapping("/meetings/export-all")
    public ResponseEntity<InputStreamResource> exportAllMeetingsAttendance() throws Exception {
        List<Meeting> meetings = meetingService.findAll();
        ByteArrayInputStream in = excelExportService.exportAllMeetingsAttendance(meetings, attendanceService);
        
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=asistencia_todos_encuentros.xlsx");
        
        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }

    @GetMapping("/users")
    public String usersList(Authentication auth, Model model) {
        model.addAttribute("currentUser", userService.getOrDefault(auth.getName()));
        model.addAttribute("users", userService.findAll());
        return "admin/users";
    }

    @PostMapping("/users/role")
    public String updateUserRole(@RequestParam String username, @RequestParam String role, RedirectAttributes redirectAttributes) {
        if (!userDetailsManager.userExists(username)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Usuario no encontrado.");
            return "redirect:/admin/users";
        }
        
        // Actualizar en Spring Security (requiere borrar y recrear con el nuevo rol)
        org.springframework.security.core.userdetails.UserDetails oldUser = userDetailsManager.loadUserByUsername(username);
        var updatedUser = User.builder()
                .username(oldUser.getUsername())
                .password(oldUser.getPassword())
                .roles(role.replace("ROLE_", ""))
                .build();
                
        userDetailsManager.deleteUser(username);
        userDetailsManager.createUser(updatedUser);

        // Actualizar metadata local
        userService.updateUserRole(username, role);

        redirectAttributes.addFlashAttribute("successMessage", "Rol de " + username + " actualizado a " + role);
        return "redirect:/admin/users";
    }
}
