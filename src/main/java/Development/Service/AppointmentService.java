/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Development.Service;

import Development.DTO.CreateAppointDTO;
import jakarta.persistence.*;
import Development.Model.Appointment;
import Development.Repository.IAppointmentRepository;
import java.time.LocalTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
/**
 *
 * @author iikan
 */
public class AppointmentService implements IAppointmentService{
    
    @Autowired
    private IAppointmentRepository appointmentRepository;

    @Override
    public List<Appointment> listAppointments() {
        try{
            return appointmentRepository.findAll();
        }catch(Exception ex){
            throw new RuntimeException("Error al buscar citas: " + ex.getMessage());
        }
    }

    @Override
    public Appointment createAppoint(Appointment obj) {
        if(!appointmentRepository.existsByDateAndTime(obj.getDate(), obj.getTime())){
            throw new IllegalArgumentException("Ya existe una cita para es hora y fecha");
        }
        try{
            return appointmentRepository.save(obj);
        }catch(Exception ex){
            throw new RuntimeException("Error al crear cita: " + ex.getMessage());
        }
    }

    @Override
    public Appointment updateAppoint(String id, CreateAppointDTO obj) {
        Appointment actualAppoint = appointmentRepository.findById(id).orElseThrow(
        () -> new EntityNotFoundException("No existe cita con id: " + id)
        );
        
        actualAppoint.setIdentificacion(obj.getIdentificacion());
        actualAppoint.setFullName(obj.getFullName());
        actualAppoint.setProfessionalName(obj.getProfessionalName());
        actualAppoint.setPhoneNumber(obj.getPhoneNumber());
        actualAppoint.setGender(obj.getGender());
        actualAppoint.setDate(obj.getDate());
        actualAppoint.setTime(obj.getTime());
        appointmentRepository.save(actualAppoint);
        
        return actualAppoint;
    }

    @Override
    public void deleteAppoint(String id) {
        try{
            appointmentRepository.deleteById(id);
        }catch(EntityNotFoundException ex){
            throw new EntityNotFoundException("No se encontro cliente con id: " + ex.getMessage());
        }catch(Exception ex){
            throw new RuntimeException("Error inesperado al eliminar cita: " + ex.getMessage());
        }
    }
    
    
    
}
