package com.asistencia.controller;

import com.asistencia.model.AppUser;
import com.asistencia.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UserService userService;
    private final InMemoryUserDetailsManager userDetailsManager;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserService userService, InMemoryUserDetailsManager userDetailsManager, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.userDetailsManager = userDetailsManager;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String displayName,
            @RequestParam String email,
            RedirectAttributes redirectAttributes) {

        if (userDetailsManager.userExists(username)) {
            redirectAttributes.addFlashAttribute("error", "El nombre de usuario ya está en uso.");
            return "redirect:/register";
        }

        // Crear credenciales en Spring Security
        var newUser = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .roles("STUDENT")
                .build();
        userDetailsManager.createUser(newUser);

        // Crear AppUser para metadata del sistema (visualización)
        AppUser appUser = new AppUser(username, displayName, "ROLE_STUDENT", email);
        userService.registerUser(appUser);

        redirectAttributes.addFlashAttribute("success", "Cuenta creada exitosamente. Inicie sesión.");
        return "redirect:/login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        AppUser user = userService.getOrDefault(auth.getName());
        model.addAttribute("currentUser", user);

        if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return "redirect:/admin/dashboard";
        } else if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_EXTENSIONIST"))) {
            return "redirect:/meetings";
        } else {
            return "redirect:/meetings";
        }
    }
}
