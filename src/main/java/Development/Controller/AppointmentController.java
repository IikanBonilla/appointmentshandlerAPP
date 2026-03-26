package Development.Controller;

import Development.DTOs.AvailableDateDTO;
import Development.DTOs.CreateAppointDTO;
import Development.Service.AppointmentService;
import Development.Model.Appointment;
import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDate;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController 
@RequestMapping("/api/appointments")
@PreAuthorize("permitAll()")
public class AppointmentController {
    
    private final Logger logger = LoggerFactory.getLogger(AppointmentController.class);
    
    @Autowired
    private AppointmentService appointmentService;
    
    // GET /api/appointments - Listar todas las citas
    @GetMapping("/all")
    public ResponseEntity<?> listAllAppointments() {
        try {
            logger.info("Listando todas las citas");
            List<Appointment> appointments = appointmentService.listAppointments();
            return ResponseEntity.ok(appointments);
            
        } catch (Exception e) {
            logger.error("Error al listar citas: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body("Error al listar las citas: " + e.getMessage());
        }
    }
    
    // GET /api/appointments/user/{userId} - Listar citas por usuario
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getAppointmentsByUser(@PathVariable String userId) {
        try {
            logger.info("Buscando citas para usuario ID: {}", userId);
            List<Appointment> appointments = appointmentService.findByUser(userId);
            
            return ResponseEntity.ok(appointments);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Error de validación: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error al buscar citas por usuario: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body("Error al buscar citas: " + e.getMessage());
        }
    }
    
    // GET /api/appointments/date/{date} - Listar citas por fecha específica
    @GetMapping("/date/{date}")
    public ResponseEntity<?> getAppointmentsByDate(@PathVariable String date) {
        try {
            LocalDate parsedDate = LocalDate.parse(date);
            logger.info("Buscando citas para fecha: {}", parsedDate);
            
            List<Appointment> appointments = appointmentService.findAllForSpecificDate(parsedDate);
            
            return ResponseEntity.ok(appointments);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Formato de fecha inválido: {}", date);
            return ResponseEntity.badRequest().body("Formato de fecha inválido. Use yyyy-MM-dd");
        } catch (Exception e) {
            logger.error("Error al buscar citas por fecha: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body("Error al buscar citas: " + e.getMessage());
        }
    }
    
    // GET /api/appointments/user/{userId}/date/{date} - Listar citas por usuario y fecha específica
    @GetMapping("/user/{userId}/date/{date}")
    public ResponseEntity<?> getAppointmentsByUserAndDate(
            @PathVariable String userId, 
            @PathVariable String date) {
        try {
            LocalDate parsedDate = LocalDate.parse(date);
            logger.info("Buscando citas para usuario: {} en fecha: {}", userId, parsedDate);
            
            List<Appointment> appointments = appointmentService.findForSpecificDateAndUser(userId, parsedDate);
           
            return ResponseEntity.ok(appointments);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Error de validación: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error al buscar citas por usuario y fecha: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body("Error al buscar citas: " + e.getMessage());
        }
    }
    
    // GET /api/appointments/available/week - Horarios disponibles de la semana
    @GetMapping("/available/week")
    public ResponseEntity<?> getAvailableSlotsForWeek() {
        try {
            logger.info("Consultando horarios disponibles para la semana");
            List<AvailableDateDTO> availableSlots = appointmentService.getAvailableSlotsForWeek();
            return ResponseEntity.ok(availableSlots);
            
        } catch (Exception ex) {
            logger.error("Error al obtener horarios disponibles: {}", ex.getMessage());
            return ResponseEntity.internalServerError().body("Error al obtener horarios");
        }
    }
    
    // POST /api/appointments/create/{userId} - Crear nueva cita
    @PostMapping("/create/{userId}")
    public ResponseEntity<?> createAppointment(@PathVariable String userId, @RequestBody CreateAppointDTO dto) {
        try {
            logger.info("Creando cita para paciente: {} con usuario ID: {}", dto.getFullName(), userId);
            
            Appointment savedAppointment = appointmentService.createAppoint(userId, dto);
            
            logger.info("Cita creada exitosamente con ID: {}", savedAppointment.getId());
            return ResponseEntity.ok(savedAppointment);
            
        } catch (IllegalArgumentException ex) {
            logger.warn("Error en validación al crear cita: {}", ex.getMessage());
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception ex) {
            logger.error("Error al guardar cita: {}", ex.getMessage());
            return ResponseEntity.internalServerError().body("Error al crear cita: " + ex.getMessage());
        }
    }
    
    // DELETE /api/appointments/{id} - Eliminar cita
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAppointment(@PathVariable String id) {
        try {
            logger.info("Eliminando cita con ID: {}", id);
            appointmentService.deleteAppoint(id);
            
            logger.info("Cita eliminada exitosamente: {}", id);
            return ResponseEntity.ok("Cita cancelada exitosamente");
            
        } catch (EntityNotFoundException ex) {
            logger.warn("Cita no encontrada con ID: {}", id);
            return ResponseEntity.notFound().build();
            
        } catch (Exception ex) {
            logger.error("Error al eliminar cita: {}", ex.getMessage());
            return ResponseEntity.internalServerError()
                    .body("Error al eliminar la cita: " + ex.getMessage());
        }
    }
}