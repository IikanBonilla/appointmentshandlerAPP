/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Development.Service;

import Development.DTOs.EmailBodyDTO;

/**
 *
 * @author iikan
 */
public interface IEmailService {
    public void sendPlainTextEmail(EmailBodyDTO emailBody);
}
