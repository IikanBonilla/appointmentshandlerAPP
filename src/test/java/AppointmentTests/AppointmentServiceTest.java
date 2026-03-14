/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package AppointmentTests;


import Development.DTOs.AuthResponseDTO;
import Development.DTOs.AvailableDateDTO;
import Development.DTOs.CreateAppointDTO;
import Development.DTOs.LoginRequestDTO;
import Development.DTOs.RegisterRequestDTO;
import Development.Model.Appointment;
import Development.Model.Gender;
import Development.Model.Role;
import Development.Model.Status;
import Development.Model.User;
import Development.Repository.IAppointmentRepository;
import Development.Repository.IUserRepository;
import Development.Service.AppointmentService;
import Development.Service.AuthService;
import Development.Service.PasswordValidatorService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class ServiceTests {

    // ============================================
    // MOCKS PARA APPOINTMENT SERVICE
    // ============================================
    @Mock
    private IAppointmentRepository appointmentRepository;
    
    @Mock
    private IUserRepository userRepository;
    
    // ============================================
    // MOCKS PARA AUTH SERVICE
    // ============================================
    @Mock
    private AuthenticationManager authenticationManager;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private PasswordValidatorService passwordValidator;
    
    @Mock
    private Authentication authentication;
    
    // ============================================
    // INJECT MOCKS
    // ============================================
    @InjectMocks
    private AppointmentService appointmentService;
    
    @InjectMocks
    private AuthService authService;
    
    // ============================================
    // DATOS DE PRUEBA
    // ============================================
    private User testUser;
    private Appointment testAppointment;
    private CreateAppointDTO createAppointDTO;
    private LoginRequestDTO loginRequestDTO;
    private RegisterRequestDTO registerRequestDTO;
    
    @BeforeEach
    void setUp() {
        // Usuario de prueba
        testUser = new User();
        testUser.setId("user123");
        testUser.setUserName("juanperez");
        testUser.setPassword("encodedPassword");
        testUser.setFullName("Juan Pérez");
        testUser.setRole(Role.ADMIN);
        testUser.setStatus(Status.ACTIVE);
        
        // Cita de prueba
        testAppointment = new Appointment();
        testAppointment.setId("app123");
        testAppointment.setIdentificacion("12345678");
        testAppointment.setFullName("Carlos Ruiz");
        testAppointment.setProfessionalName("Dr. García");
        testAppointment.setPhoneNumber("555-1234");
        testAppointment.setGender(Gender.MALE);
        testAppointment.setDate(LocalDate.now().plusDays(1));
        testAppointment.setTime(LocalTime.of(10, 0));
        testAppointment.setUser(testUser);
        
        // DTO para crear cita
        createAppointDTO = new CreateAppointDTO();
        createAppointDTO.setIdentificacion("87654321");
        createAppointDTO.setFullName("Ana López");
        createAppointDTO.setProfessionalName("Dra. Martínez");
        createAppointDTO.setPhoneNumber("555-5678");
        createAppointDTO.setGender(Gender.FEMALE);
        createAppointDTO.setDate(LocalDate.now().plusDays(2));
        createAppointDTO.setTime(LocalTime.of(11, 30));
        
        // DTO para login
        loginRequestDTO = new LoginRequestDTO();
        loginRequestDTO.setUserName("juanperez");
        loginRequestDTO.setPassword("password123");
        
        // DTO para registro
        registerRequestDTO = new RegisterRequestDTO();
        registerRequestDTO.setUserName("maria123");
        registerRequestDTO.setPassword("Password123");
        registerRequestDTO.setFullName("María Gómez");
        registerRequestDTO.setRole(Role.DOCTOR);
    }
    
    // =================================================================
    // TESTS PARA APPOINTMENT SERVICE
    // =================================================================
    
    // ---------------------------------
    // listAppointments
    // ---------------------------------
    @Test
    void listAppointments_ShouldReturnAllAppointments() {
        // Arrange
        when(appointmentRepository.findAll()).thenReturn(List.of(testAppointment));
        
        // Act
        List<Appointment> result = appointmentService.listAppointments();
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testAppointment.getId(), result.get(0).getId());
        verify(appointmentRepository).findAll();
    }
    
    @Test
    void listAppointments_ShouldThrowExceptionWhenRepositoryFails() {
        // Arrange
        when(appointmentRepository.findAll()).thenThrow(new RuntimeException("DB error"));
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> appointmentService.listAppointments());
        assertTrue(exception.getMessage().contains("Error al buscar citas"));
    }
    
    // ---------------------------------
    // createAppoint
    // ---------------------------------
    @Test
    void createAppoint_ShouldCreateAppointmentSuccessfully() {
        // Arrange
        when(userRepository.findById("user123")).thenReturn(Optional.of(testUser));
        when(appointmentRepository.existsByDateAndTime(any(LocalDate.class), any(LocalTime.class)))
            .thenReturn(false);
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> {
            Appointment saved = invocation.getArgument(0);
            saved.setId("newApp123");
            return saved;
        });
        
        // Act
        Appointment result = appointmentService.createAppoint("user123", createAppointDTO);
        
        // Assert
        assertNotNull(result);
        assertEquals(createAppointDTO.getFullName(), result.getFullName());
        assertEquals(createAppointDTO.getIdentificacion(), result.getIdentificacion());
        assertEquals(testUser, result.getUser());
        verify(appointmentRepository).save(any(Appointment.class));
    }
    
    @Test
    void createAppoint_ShouldThrowExceptionWhenUserNotFound() {
        // Arrange
        when(userRepository.findById("user123")).thenReturn(Optional.empty());
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> appointmentService.createAppoint("user123", createAppointDTO));
        assertTrue(exception.getMessage().contains("No existe un usuario"));
    }
    
    @Test
    void createAppoint_ShouldThrowExceptionWhenAppointmentExists() {
        // Arrange
        when(userRepository.findById("user123")).thenReturn(Optional.of(testUser));
        when(appointmentRepository.existsByDateAndTime(createAppointDTO.getDate(), createAppointDTO.getTime()))
            .thenReturn(true);
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> appointmentService.createAppoint("user123", createAppointDTO));
        assertTrue(exception.getMessage().contains("Ya existe una cita"));
    }
    
    // ---------------------------------
    // deleteAppoint
    // ---------------------------------
    @Test
    void deleteAppoint_ShouldDeleteSuccessfully() {
        // Arrange
        doNothing().when(appointmentRepository).deleteById("app123");
        
        // Act
        appointmentService.deleteAppoint("app123");
        
        // Assert
        verify(appointmentRepository).deleteById("app123");
    }
    
    @Test
    void deleteAppoint_ShouldThrowExceptionWhenDeleteFails() {
        // Arrange
        doThrow(new RuntimeException("DB error")).when(appointmentRepository).deleteById("app123");
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> appointmentService.deleteAppoint("app123"));
        assertTrue(exception.getMessage().contains("Error inesperado"));
    }
    
    // ---------------------------------
    // generatePossibleTimes (método privado, probado indirectamente)
    // ---------------------------------
    @Test
    void getAvailableSlotsForWeek_ShouldGenerateCorrectTimes() {
        // Este test verifica que generatePossibleTimes() funciona correctamente
        // a través del método público que lo usa
        
        // Arrange
        LocalDate today = LocalDate.now();
        when(appointmentRepository.findByDate(any(LocalDate.class))).thenReturn(List.of());
        
        // Act
        List<AvailableDateDTO> result = appointmentService.getAvailableSlotsForWeek();
        
        // Assert
        assertNotNull(result);
        // Verificar que hay días (entre 5 y 6 dependiendo de getWeekDays)
        assertTrue(result.size() >= 5);
    }
    
    @Test
    void getAvailableSlotsForWeek_ShouldFilterOutBookedTimes() {
        // Arrange
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        
        // Crear una cita ocupada para mañana a las 10:00
        Appointment bookedApp = new Appointment();
        bookedApp.setDate(tomorrow);
        bookedApp.setTime(LocalTime.of(10, 0));
        
        when(appointmentRepository.findByDate(tomorrow)).thenReturn(List.of(bookedApp));
        when(appointmentRepository.findByDate(any(LocalDate.class))).thenReturn(List.of());
        
        // Act
        List<AvailableDateDTO> result = appointmentService.getAvailableSlotsForWeek();
        
        // Assert
        // Buscar el día de mañana y verificar que 10:00 NO esté disponible
        for (AvailableDateDTO dto : result) {
            if (dto.getDate().equals(tomorrow)) {
                assertFalse(dto.getAvailableTimes().contains(LocalTime.of(10, 0)));
            }
        }
    }
    
    // =================================================================
    // TESTS PARA AUTH SERVICE
    // =================================================================
    
    // ---------------------------------
    // login
    // ---------------------------------
    @Test
    void login_ShouldReturnAuthResponseWhenCredentialsValid() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
        
        // Act
        AuthResponseDTO result = authService.login(loginRequestDTO);
        
        // Assert
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getFullName(), result.getFullName());
        assertEquals(testUser.getRole(), result.getRole());
        assertEquals(testUser.getStatus(), result.getStatus());
    }
    
    @Test
    void login_ShouldThrowExceptionWhenBadCredentials() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenThrow(new BadCredentialsException("Bad credentials"));
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> authService.login(loginRequestDTO));
        assertEquals("Bad credentials", exception.getMessage());
    }
    
    @Test
    void login_ShouldThrowExceptionWhenAuthenticationFails() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenThrow(new org.springframework.security.authentication.AccountStatusException("Account locked") {});
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> authService.login(loginRequestDTO));
        assertEquals("Usuario o contraseña incorrectos", exception.getMessage());
    }
    
    // ---------------------------------
    // register - Primer usuario (ADMIN)
    // ---------------------------------
    @Test
    void register_ShouldCreateAdminWhenFirstUser() {
        // Arrange
        when(userRepository.count()).thenReturn(0L);
        when(passwordValidator.isValidPassword(registerRequestDTO.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(registerRequestDTO.getPassword())).thenReturn("encodedPass");
        
        // Act
        authService.register(registerRequestDTO);
        
        // Assert
        verify(userRepository).save(argThat(user -> 
            user.getRole() == Role.ADMIN &&
            user.getStatus() == Status.ACTIVE &&
            user.getUserName().equals(registerRequestDTO.getUserName())
        ));
    }
    
    @Test
    void register_ShouldThrowExceptionWhenFirstUserPasswordInvalid() {
        // Arrange
        when(userRepository.count()).thenReturn(0L);
        when(passwordValidator.isValidPassword(registerRequestDTO.getPassword())).thenReturn(false);
        when(passwordValidator.getPasswordRequirements()).thenReturn("Password requirements not met");
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> authService.register(registerRequestDTO));
        assertEquals("Password requirements not met", exception.getMessage());
        verify(userRepository, never()).save(any());
    }
    
    // ---------------------------------
    // register - Usuarios subsiguientes
    // ---------------------------------
    @Test
    void register_ShouldCreateUserWithGivenRoleWhenNotFirstUser() {
        // Arrange
        when(userRepository.count()).thenReturn(5L); // Ya hay usuarios
        when(userRepository.existsByUserName(registerRequestDTO.getUserName())).thenReturn(false);
        when(passwordValidator.isValidPassword(registerRequestDTO.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(registerRequestDTO.getPassword())).thenReturn("encodedPass");
        
        // Act
        authService.register(registerRequestDTO);
        
        // Assert
        verify(userRepository).save(argThat(user -> 
            user.getRole() == Role.DOCTOR && // El rol del DTO
            user.getStatus() == Status.ACTIVE &&
            user.getUserName().equals(registerRequestDTO.getUserName())
        ));
    }
    
    @Test
    void register_ShouldThrowExceptionWhenUsernameAlreadyExists() {
        // Arrange
        when(userRepository.count()).thenReturn(5L); // Ya hay usuarios
        when(userRepository.existsByUserName(registerRequestDTO.getUserName())).thenReturn(true);
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> authService.register(registerRequestDTO));
        assertTrue(exception.getMessage().contains("Ya existe un usuario"));
        verify(userRepository, never()).save(any());
    }
    
    @Test
    void register_ShouldThrowExceptionWhenPasswordInvalid() {
        // Arrange
        when(userRepository.count()).thenReturn(5L); // Ya hay usuarios
        when(userRepository.existsByUserName(registerRequestDTO.getUserName())).thenReturn(false);
        when(passwordValidator.isValidPassword(registerRequestDTO.getPassword())).thenReturn(false);
        when(passwordValidator.getPasswordRequirements()).thenReturn("Password requirements not met");
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> authService.register(registerRequestDTO));
        assertEquals("Password requirements not met", exception.getMessage());
        verify(userRepository, never()).save(any());
    }
    
    // ---------------------------------
    // register - Integración de casos borde
    // ---------------------------------
    @Test
    void register_ShouldHandleNullRoleForSubsequentUsers() {
        // Arrange
        registerRequestDTO.setRole(null); // Rol nulo
        
        when(userRepository.count()).thenReturn(5L);
        when(userRepository.existsByUserName(registerRequestDTO.getUserName())).thenReturn(false);
        when(passwordValidator.isValidPassword(registerRequestDTO.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(registerRequestDTO.getPassword())).thenReturn("encodedPass");
        
        // Act
        authService.register(registerRequestDTO);
        
        // Assert - El usuario se guarda con rol null (luego se manejará en la UI)
        verify(userRepository).save(argThat(user -> 
            user.getRole() == null &&
            user.getUserName().equals(registerRequestDTO.getUserName())
        ));
    }
    
    @Test
    void register_ShouldHandleAdminRegistrationEvenWithRoleInDTO() {
        // Arrange
        registerRequestDTO.setRole(Role.DOCTOR); // DTO dice DOCTOR pero es primer usuario
        
        when(userRepository.count()).thenReturn(0L); // Primer usuario
        when(passwordValidator.isValidPassword(registerRequestDTO.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(registerRequestDTO.getPassword())).thenReturn("encodedPass");
        
        // Act
        authService.register(registerRequestDTO);
        
        // Assert - Se fuerza ADMIN a pesar del DTO
        verify(userRepository).save(argThat(user -> 
            user.getRole() == Role.ADMIN // Se ignora el rol del DTO
        ));
    }
}