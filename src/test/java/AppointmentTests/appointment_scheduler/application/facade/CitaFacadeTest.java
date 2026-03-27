package com.groupsoft.appointment_scheduler.application.facade;

import com.groupsoft.appointment_scheduler.application.dto.*;
import com.groupsoft.appointment_scheduler.application.service.CitaService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CitaFacadeTest {

    @Mock
    private CitaService service;

    @InjectMocks
    private CitaFacade facade;

    @Test
    void crear_delega_correctamente() {

        CitaRequestDTO request = new CitaRequestDTO();
        request.setUsuarioId(1L);
        request.setMedicoId(2L);
        request.setFechaHora(LocalDateTime.now());

        CitaResponseDTO response = CitaResponseDTO.builder()
                .id(1L)
                .usuario("Juan")
                .medico("Dr. House")
                .estado("AGENDADA")
                .build();

        when(service.crear(request)).thenReturn(response);

        CitaResponseDTO result = facade.crear(request);

        assertNotNull(result);
        assertEquals("Juan", result.getUsuario());

        verify(service).crear(request);
    }
}