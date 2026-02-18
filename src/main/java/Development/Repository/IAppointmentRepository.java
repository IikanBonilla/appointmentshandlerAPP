/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Development.Repository;

import Development.Model.Appointment;
import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author iikan
 */
public interface IAppointmentRepository extends JpaRepository<Appointment, String>{
    
    public boolean existsByDateAndTime(LocalDate date, LocalTime time);

    
}
