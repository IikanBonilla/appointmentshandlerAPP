/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/springframework/Component.java to edit this template
 */
package Development.Listeners;

import Development.Config.EmailConfig;
import Development.DTOs.EmailBodyDTO;
import Development.Service.EmailService;
import Development.Events.AppointmentCreatedEvent;
import Development.Model.Appointment;
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
    
    @Autowired
    private EmailConfig emailconfig;
    
    @EventListener
    public void handleAppointmentCreated(AppointmentCreatedEvent event){
  
        //Extraer la información del evento
        var appointment = event.getAppointment();
        var clientEmail = event.getClientEmail();
        
        //Construir email de texto plano
        String emailText = buildPlainTextEmail(appointment);
        
        EmailBodyDTO emailbody = new EmailBodyDTO();
        emailbody.setTo(clientEmail);
        emailbody.setSubject("Confirmación de cita médica");
        emailbody.setText(emailText);
        
        try{
            
            emailService.sendPlainTextEmail(emailbody);
            
        }catch(Exception ex){
            System.err.println("Error al enviar email: " + ex.getMessage());
        }
        
    }
    
    private String buildPlainTextEmail(Appointment appointment) {
        return String.format("""
            ===================================
            CONFIRMACIÓN DE CITA MÉDICA
            ===================================
            
            Estimado/a %s,
            
            Su cita ha sido agendada exitosamente con los siguientes detalles:
            
            📅 Fecha: %s
            ⏰ Hora: %s
            👨‍⚕️ Profesional: %s
            
            Datos del paciente:
            - Nombre: %s
            - Identificación: %s
            - Teléfono: %s
            
            ===================================
            Por favor llegar 15 minutos antes.
            Ante cualquier inconveniente, comuníquese con la clínica.
            ===================================
            """,
            appointment.getFullName(),
            appointment.getDate(),
            appointment.getTime(),
            appointment.getProfessionalName(),
            appointment.getFullName(),
            appointment.getIdentificacion(),
            appointment.getPhoneNumber()
        );
    }
}
