/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Development.Events;

import Development.DTOs.EmailBodyDTO;
import Development.Model.Appointment;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;

@Getter
public class AppointmentCreatedEvent extends ApplicationEvent{
    
    private final Appointment appointment;

    private final String clientEmail;
    
    public AppointmentCreatedEvent(Object source, Appointment appointment, String clientEmail){
        super(source);
        this.appointment = appointment;
        this.clientEmail = clientEmail;
    }
}
