/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/springframework/Component.java to edit this template
 */
package Development.Config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author iikan
 */
@Component
public class EmailConfig {
    
    @Value("${app.email.enabled}")
    private boolean emailEnabled;
        
    public boolean isEmailEnable(){
            return emailEnabled;
    }
    
}
