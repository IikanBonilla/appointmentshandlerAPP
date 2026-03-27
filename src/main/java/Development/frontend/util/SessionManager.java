package Development.frontend.util;

import Development.Model.Role;
import Development.Model.Status;
import org.springframework.stereotype.Component;

@Component
public class SessionManager {
    
    private String userId;
    private String fullName;
    private String userName;
    private Role role;
    private Status status;
    private boolean isLoggedIn = false;
    
    /**
     * Guarda la sesión del usuario después de login exitoso
     */
    public void login(String userId, String userName, String fullName, Role role, Status status) {
        this.userId = userId;
        this.userName = userName;
        this.fullName = fullName;
        this.role = role;
        this.status = status;
        this.isLoggedIn = true;
        
        System.out.println("✅ Sesión iniciada - Usuario: " + userName + " (ID: " + userId + ") - Rol: " + role);
    }
    
    /**
     * Cierra la sesión del usuario
     */
    public void logout() {
        this.userId = null;
        this.userName = null;
        this.fullName = null;
        this.role = null;
        this.status = null;
        this.isLoggedIn = false;
        
        System.out.println("🔒 Sesión cerrada");
    }
    
    public String getCurrentUserId() {
        if (!isLoggedIn) {
            throw new IllegalStateException("No hay usuario autenticado");
        }
        return userId;
    }
    
    public String getCurrentUserName() {
        return userName;
    }
    
    public String getCurrentUserFullName() {
        return fullName;
    }
    
    public Role getCurrentUserRole() {
        return role;
    }
    
    public boolean isLoggedIn() {
        return isLoggedIn;
    }
    
    public boolean isDoctor() {
        return role == Role.DOCTOR;
    }
    
    public boolean isTherapist() {
        return role == Role.THERAPIST;
    }
    
    public boolean isAdmin() {
        return role == Role.ADMIN;
    }
}