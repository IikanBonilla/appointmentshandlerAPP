package com.groupsoft.appointment_scheduler.application.service;
import com.groupsoft.appointment_scheduler.domain.model.Usuario;
import com.groupsoft.appointment_scheduler.domain.repository.UsuarioRepository;
import com.groupsoft.appointment_scheduler.application.service.AuthService;
import com.groupsoft.appointment_scheduler.application.dto.*;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository repo;

    @InjectMocks
    private AuthService service;

    @Test
    void login_ok() {

        Usuario u = new Usuario();
        u.setUsername("admin");
        u.setPassword("admin123");

        when(repo.findByUsername("admin"))
                .thenReturn(Optional.of(u));

        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setUsername("admin");
        dto.setPassword("admin123");

        assertNotNull(service.login(dto));
    }

    @Test
    void login_falla() {

        when(repo.findByUsername("admin"))
                .thenReturn(Optional.empty());

        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setUsername("admin");

        assertThrows(RuntimeException.class, () ->
                service.login(dto)
        );
    }
}