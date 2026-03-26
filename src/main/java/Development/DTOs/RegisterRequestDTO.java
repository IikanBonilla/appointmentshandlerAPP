/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Development.DTOs;

import Development.Model.Role;
import Development.Model.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author iikan
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestDTO {
    private String userName;
    
    private String password;
    
    private String fullName;
    
    private String email;
    
    private Role role; 
}
