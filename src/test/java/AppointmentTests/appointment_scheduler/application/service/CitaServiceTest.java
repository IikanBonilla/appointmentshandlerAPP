package com.groupsoft.appointment_scheduler.application.service;

import com.groupsoft.appointment_scheduler.application.dto.*;
import com.groupsoft.appointment_scheduler.domain.model.*;
import com.groupsoft.appointment_scheduler.domain.repository.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CitaServiceTest {

    @Mock private CitaRepository citaRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private MedicoRepository medicoRepository;

    @InjectMocks
    private CitaService service;

    @Test
    void crear_cita_exitoso() {

        CitaRequestDTO dto = new CitaRequestDTO();
        dto.setUsuarioId(1L);
        dto.setMedicoId(2L);
        dto.setFechaHora(LocalDateTime.now());

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(new Usuario()));

        when(medicoRepository.findById(2L))
                .thenReturn(Optional.of(new Medico()));

        when(citaRepository.existsByMedicoIdAndFechaHora(any(), any()))
                .thenReturn(false);

        when(citaRepository.save(any()))
                .thenAnswer(inv -> inv.getArgument(0)); // devuelve la cita guardada

        CitaResponseDTO result = service.crear(dto);

        assertNotNull(result);
        verify(citaRepository).save(any());
    }

    @Test
    void no_crear_si_medico_ocupado() {

        CitaRequestDTO dto = new CitaRequestDTO();
        dto.setUsuarioId(1L);
        dto.setMedicoId(2L);
        dto.setFechaHora(LocalDateTime.now());

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(new Usuario()));

        when(medicoRepository.findById(2L))
                .thenReturn(Optional.of(new Medico()));

        when(citaRepository.existsByMedicoIdAndFechaHora(any(), any()))
                .thenReturn(true);

        assertThrows(RuntimeException.class, () ->
                service.crear(dto)
        );
    }

    @Test
    void no_crear_si_usuario_no_existe() {

        CitaRequestDTO dto = new CitaRequestDTO();
        dto.setUsuarioId(1L);
        dto.setMedicoId(2L);
        dto.setFechaHora(LocalDateTime.now());

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                service.crear(dto)
        );
    }

    @Test
    void no_crear_si_medico_no_existe() {

        CitaRequestDTO dto = new CitaRequestDTO();
        dto.setUsuarioId(1L);
        dto.setMedicoId(2L);
        dto.setFechaHora(LocalDateTime.now());

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(new Usuario()));

        when(medicoRepository.findById(2L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                service.crear(dto)
        );
    }
}