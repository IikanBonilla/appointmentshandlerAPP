/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Development.Repository;

import Development.Model.Appointment;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author iikan
 */
public interface IAppointmentRepository extends JpaRepository<Appointment, String>{
    
    public boolean existsByDateAndTime(LocalDate date, LocalTime time);
    
    @Query("""
        SELECT a
        FROM Appointment a
        WHERE a.user.id = ?1
        ORDER BY a.date ASC, a.time ASC          
            """)
    public List<Appointment> findByUserId(String idUser);
    
    // Buscar por nombre del profesional
    @Query("SELECT a FROM Appointment a WHERE a.professionalName = ?1 ORDER BY a.date ASC, a.time ASC")
    public List<Appointment> findByProfessionalName(String professionalName);
    
    // Filtrar por usuario y fecha específica
    @Query("SELECT a FROM Appointment a WHERE a.user.id = ?1 AND a.date = ?2 ORDER BY a.time ASC")
    public List<Appointment> findByUserIdAndDate(String userId, LocalDate date);
    
    public List<Appointment> findByDate(LocalDate date);
}
