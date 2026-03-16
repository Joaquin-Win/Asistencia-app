package com.asistencia.model;

public class AppUser {

    private String username;
    private String displayName;
    private String role;
    private String email;

    public AppUser() {}

    public AppUser(String username, String displayName, String role, String email) {
        this.username = username;
        this.displayName = displayName;
        this.role = role;
        this.email = email;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRoleLabel() {
        return switch (role) {
            case "ROLE_ADMIN" -> "Administrador";
            case "ROLE_EXTENSIONIST" -> "Extensionista";
            case "ROLE_STUDENT" -> "Estudiante";
            default -> role;
        };
    }
}
