/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/springframework/Component.java to edit this template
 */
package Listeners;

import Development.Service.EmailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 *
 * @author iikan
 */
@Component
public class AppointmentEmailListener {
    
    @Autowired
    private EmailService emailService;
    
    @EventListener
    public void handleAppointmentCreated(){
        
    }
}
