/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package AppointmentTests;

import Development.Service.AppointmentService;

import Development.DTO.CreateAppointDTO;
import Development.Model.Appointment;
import Development.Model.Gender;
import Development.Repository.IAppointmentRepository;
import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private IAppointmentRepository appointmentRepository;

    @InjectMocks
    private AppointmentService appointmentService;


    private Appointment appointment;

    @BeforeEach
    void setUp() {

        appointment = new Appointment();
        appointment.setId("1");
        appointment.setIdentificacion(123);
        appointment.setFullName("Juan Perez");
        appointment.setProfessionalName("Doctor A");
        appointment.setPhoneNumber(55555);
        appointment.setGender(Gender.MALE);
        appointment.setDate(LocalDate.now());
        appointment.setTime(LocalTime.of(10, 0));
    }

    // ===============================
    // listAppointments
    // ===============================
    @Test
    void shouldReturnAppointments() {

        when(appointmentRepository.findAll())
                .thenReturn(List.of(appointment));

        List<Appointment> result =
                appointmentService.listAppointments();

        assertEquals(1, result.size());
        verify(appointmentRepository).findAll();
    }

    // ===============================
    // createAppoint SUCCESS
    // ===============================
    @Test
    void shouldCreateAppointment() {

        when(appointmentRepository.existsByDateAndTime(any(), any()))
                .thenReturn(true);

        when(appointmentRepository.save(any()))
                .thenReturn(appointment);

        Appointment result =
                appointmentService.createAppoint(appointment);

        assertNotNull(result);
        verify(appointmentRepository).save(appointment);
    }

    // ===============================
    // createAppoint ERROR
    // ===============================
    @Test
    void shouldThrowExceptionIfAppointmentExists() {

        when(appointmentRepository.existsByDateAndTime(any(), any()))
                .thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> appointmentService.createAppoint(appointment));
    }

    // ===============================
    // updateAppoint SUCCESS
    // ===============================
    @Test
    void shouldUpdateAppointment() {

        CreateAppointDTO dto = new CreateAppointDTO();
        dto.setFullName("Carlos Ruiz");
        dto.setProfessionalName("Doctor B");
        dto.setPhoneNumber(99999);
        dto.setGender(Gender.FEMALE);
        dto.setDate(LocalDate.now().plusDays(1));
        dto.setTime(LocalTime.NOON);

        when(appointmentRepository.findById("1"))
                .thenReturn(Optional.of(appointment));

        when(appointmentRepository.save(any()))
                .thenReturn(appointment);

        Appointment updated =
                appointmentService.updateAppoint("1", dto);

        assertEquals("Carlos Ruiz", updated.getFullName());
        assertEquals(Gender.FEMALE, updated.getGender());

        verify(appointmentRepository).save(appointment);
    }

    // ===============================
    // updateAppoint ERROR
    // ===============================
    @Test
    void shouldThrowIfAppointmentNotFound() {

        when(appointmentRepository.findById("1"))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> appointmentService.updateAppoint("1",
                        new CreateAppointDTO()));
    }

    // ===============================
    // deleteAppoint SUCCESS
    // ===============================
    @Test
    void shouldDeleteAppointment() {

        doNothing().when(appointmentRepository)
                .deleteById("1");

        appointmentService.deleteAppoint("1");

        verify(appointmentRepository).deleteById("1");
    }

    // ===============================
    // deleteAppoint ERROR
    // ===============================
    @Test
    void shouldThrowExceptionWhenDeleteFails() {

        doThrow(RuntimeException.class)
                .when(appointmentRepository)
                .deleteById("1");

        assertThrows(RuntimeException.class,
                () -> appointmentService.deleteAppoint("1"));
    }
}
