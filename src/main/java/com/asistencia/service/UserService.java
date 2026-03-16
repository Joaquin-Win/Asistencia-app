package com.asistencia.service;

import com.asistencia.model.AppUser;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    private final Map<String, AppUser> users = new HashMap<>();

    public UserService() {
        users.put("admin", new AppUser("admin", "Admin Sistema", "ROLE_ADMIN", "admin@asistencia.edu.ar"));
        users.put("ext1",
                new AppUser("ext1", "Carlos Extensionista", "ROLE_EXTENSIONIST", "cextensionista@asistencia.edu.ar"));
        users.put("estudiante1",
                new AppUser("estudiante1", "María Estudiante", "ROLE_STUDENT", "mestudiante@asistencia.edu.ar"));
    }

    public Optional<AppUser> findByUsername(String username) {
        return Optional.ofNullable(users.get(username));
    }

    public List<AppUser> findAll() {
        return new java.util.ArrayList<>(users.values());
    }

    public void registerUser(AppUser user) {
        users.put(user.getUsername(), user);
    }

    public void updateUserRole(String username, String role) {
        AppUser user = users.get(username);
        if (user != null) {
            user.setRole(role);
        }
    }

    public AppUser getOrDefault(String username) {
        return users.getOrDefault(username, new AppUser(username, username, "ROLE_STUDENT", ""));
    }
}
