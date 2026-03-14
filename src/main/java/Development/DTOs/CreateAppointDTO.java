/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Development.DTOs;

import Development.Model.Gender;
import java.time.LocalDate;
import java.time.LocalTime;
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
public class CreateAppointDTO {
    
    private String identificacion;
    
    private String fullName;
    
    private String professionalName;
    
    private String phoneNumber;
    
    private Gender gender;
    
    private LocalDate date;
    
    private LocalTime time;  
    
}
