/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/springframework/Service.java to edit this template
 */
package Development.Service;

import Development.DTOs.EmailBodyDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
/**
 *
 * @author iikan
 */
@Service
public class EmailService implements IEmailService{
    
    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public void sendPlainTextEmail(EmailBodyDTO emailBody) {
        try{
            sendPlainTextMessage(emailBody);
            System.out.println("✅ Email enviado a: " + emailBody.getTo());
        }catch(Exception ex){
            System.err.println("❌ Error al enviar email: " + ex.getMessage());
            throw new RuntimeException("Error al enviar email: " + ex.getMessage());
        
        }
    }
    
    private void sendPlainTextMessage(EmailBodyDTO emailBody){
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(emailBody.getTo());
            message.setSubject(emailBody.getSubject());
            message.setText(emailBody.getText());
            message.setFrom("iikanyoharybonilla@gmail.com");
            
            javaMailSender.send(message);
    }
    
    
}
