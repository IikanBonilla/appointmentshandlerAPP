/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Development.Service;

import org.springframework.stereotype.Service;

/**
 *
 * @author iikan
 */
@Service
public class PasswordValidatorService {
    
    public boolean isValidPassword(String password){
        if(password == null || password.length() < 8) return false;
        
        String pattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[^\\s]{8,}$";
        
        return password.matches(pattern);
    }
    
    public String getPasswordRequirements(){
        return "La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula y un número (NO SE PERMITEN ESPACIOS)";  
    }
}
