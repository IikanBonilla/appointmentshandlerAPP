package com.groupsoft.appointment_scheduler;


import com.groupsoft.appointment_scheduler.application.facade.*;
import com.groupsoft.appointment_scheduler.presentation.controller.PacienteController;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PacienteControllerTest {

    @Test
    void controller_se_crea_correctamente() {

        UsuarioFacade usuarioFacade = mock(UsuarioFacade.class);
        CitaFacade citaFacade = mock(CitaFacade.class);
        MedicoFacade medicoFacade = mock(MedicoFacade.class);

        PacienteController controller =
                new PacienteController(usuarioFacade, citaFacade, medicoFacade);

        assertNotNull(controller);
    }
}