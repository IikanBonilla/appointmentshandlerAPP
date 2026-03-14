/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Development.Service;

import Development.DTOs.AvailableDateDTO;
import Development.DTOs.CreateAppointDTO;
import jakarta.persistence.*;
import Development.Model.Appointment;
import Development.Model.User;
import Development.Repository.IAppointmentRepository;
import Development.Repository.IUserRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
/**
 *
 * @author iikan
 */


@Service
@PreAuthorize("hasAnyRole('DOCTOR', 'THERAPIST')")
public class AppointmentService implements IAppointmentService{
    
    @Autowired
    private IAppointmentRepository appointmentRepository;
    
    @Autowired
    private IUserRepository userRepository;

    @Override
    public List<Appointment> listAppointments() {
        try{
            return appointmentRepository.findAll();
        }catch(Exception ex){
            throw new RuntimeException("Error al buscar citas: " + ex.getMessage());
        }
    }

    @Override
    public Appointment createAppoint(String userId, CreateAppointDTO dto) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("No existe un usuario con ID: " + userId)
        );
                
        if(appointmentRepository.existsByDateAndTime(dto.getDate(), dto.getTime())){
            throw new IllegalArgumentException("Ya existe una cita para es hora y fecha");
        }
        
        try{
                Appointment app = new Appointment();
                app.setIdentificacion(dto.getIdentificacion());
                app.setFullName(dto.getFullName());
                app.setProfessionalName(dto.getProfessionalName());
                app.setPhoneNumber(dto.getPhoneNumber());
                app.setGender(dto.getGender());
                app.setDate(dto.getDate());
                app.setTime(dto.getTime());
                app.setUser(user); // Asumiendo que recibes el usuario en el DTO
            
            return appointmentRepository.save(app);
        }catch(Exception ex){
            throw new RuntimeException("Error al crear cita: " + ex.getMessage());
        }
    }

    /*@Override
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
    */
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
    
    private List<LocalTime> generatePossibleTimes(){
        List<LocalTime> times = new ArrayList<>();
        LocalTime start = LocalTime.of(7, 0); //7:00 AM
        LocalTime end = LocalTime.of(18, 0); //6:00 pm
        
        LocalTime current = start;
        while(!current.isAfter(end)){
            times.add(current);
            current = current.plusMinutes(30);
        }
        
        return times;
    }

    private List<LocalDate> getWeekDays(LocalDate startDate){
        List<LocalDate> weekDays = new ArrayList<>();
        LocalDate current = startDate;
  
        //Asignar de fecha actual hasta misma fecha de la siguiente semana
        while(weekDays.size() < 6){
            int dayOfWeek = current.getDayOfWeek().getValue();
            
            if(dayOfWeek >= 1 && dayOfWeek <= 5){
                weekDays.add(current);
            }
            
            current = current.plusDays(1); // Avanzar al día siguiente
        }
        
        return weekDays;
    }
    @Override
    public List<AvailableDateDTO> getAvailableSlotsForWeek() {
        List<AvailableDateDTO> availability = new ArrayList<>();
        LocalDate today = LocalDate.now();
        
        //1. Obtener todos los horarios
        List<LocalTime> allTimes = generatePossibleTimes();
        
        //2. Obtener los días de la semana
        List<LocalDate> weekDays = getWeekDays(today);
        
        //3. Para cada día ver horarios disponibles
        for(LocalDate date : weekDays){
        
            List<LocalTime> availableTimes;
            
            if(date.equals(today)){
                LocalTime now = LocalTime.now();
                
                availableTimes = allTimes.stream()
                        .filter(time -> !time.isBefore(now))
                        .collect(Collectors.toList());
            }else{
                availableTimes = new ArrayList<>(allTimes);
            }
            
            //Consultar las citas para este día
            List<Appointment> bookedAppointments = appointmentRepository.findByDate(date);
            
            //Solo agregar si hay horas disponibles
            if(!availableTimes.isEmpty()){
                availability.add(new AvailableDateDTO(date, availableTimes));
            }
        }
        
        return availability;
    }
    
    
    
}
