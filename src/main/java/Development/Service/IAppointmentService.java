/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Development.Service;

import Development.DTOs.AvailableDateDTO;
import Development.DTOs.CreateAppointDTO;
import Development.Model.Appointment;
import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author iikan
 */
public interface IAppointmentService {
    public List<Appointment> listAppointments();
    public Appointment createAppoint(String userId, CreateAppointDTO dto);
    //public void updateAppoint(String id, UpAppointDTO obj);
    public void deleteAppoint(String id);
    public List<AvailableDateDTO> getAvailableSlotsForWeek();
    public List<Appointment> findAllForSpecificDate(LocalDate date);
    public List<Appointment> findForSpecificDateAndUser(String userId, LocalDate date);
    public List<Appointment> findByUser(String userId);
    
}
