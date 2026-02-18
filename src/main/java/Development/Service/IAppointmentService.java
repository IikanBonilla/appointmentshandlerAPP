/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Development.Service;

import Development.DTO.CreateAppointDTO;
import Development.Model.Appointment;
import java.util.List;

/**
 *
 * @author iikan
 */
public interface IAppointmentService {
    public List<Appointment> listAppointments();
    public Appointment createAppoint(Appointment obj);
    public Appointment updateAppoint(String id, CreateAppointDTO obj);
    public void deleteAppoint(String id);
    
    
}
