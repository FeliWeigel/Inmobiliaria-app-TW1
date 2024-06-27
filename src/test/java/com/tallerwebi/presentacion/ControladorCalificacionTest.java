package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.excepcion.CalificacionDenegadaExcepcion;
import com.tallerwebi.dominio.excepcion.UsuarioNoIdentificadoExcepcion;
import com.tallerwebi.dominio.servicio.ServicioCalificacion;
import com.tallerwebi.dominio.servicio.ServicioPropiedad;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class ControladorCalificacionTest {

    private ServicioCalificacion servicioCalificacion;
    private ServicioPropiedad servicioPropiedad;
    private ControladorCalificacion controladorCalificacion;
    private HttpSession session;

    @BeforeEach
    public void setup() {
        this.servicioPropiedad = mock(ServicioPropiedad.class);
        this.servicioCalificacion = mock(ServicioCalificacion.class);
        this.session = mock(HttpSession.class);

        this.controladorCalificacion = new ControladorCalificacion(servicioCalificacion, servicioPropiedad);


    }

    @Test
    public void queVistaAgregarCalificacionLleveASuPagina() {
        Long idMock = 1L;

        ModelAndView mav = controladorCalificacion.vistaAgregarCalificacion(idMock);

        assertThat(mav.getViewName(), equalTo("agregarCalificacion"));
        assertThat(mav.getModel().get("id"), equalTo(idMock));
    }

    @Test
    public void queSeAgregueNuevaCalificacionCorrectamente() throws UsuarioNoIdentificadoExcepcion {
        Long idMock = 1L;
        Usuario usuarioMock = new Usuario(); // Mockear un usuario según tus necesidades
        String descripcion = "Buena propiedad";
        Double puntaje = 4.5;

        when(session.getAttribute("usuario")).thenReturn(usuarioMock);

        ModelAndView mav = controladorCalificacion.agregarNuevaCalificacion(idMock, descripcion, puntaje, session);

        assertThat(mav.getViewName(), equalTo("agregarCalificacion"));
        verify(servicioCalificacion, times(1)).agregarNuevaCalificacion(eq(idMock), eq(usuarioMock), eq(descripcion), eq(puntaje));
    }

    @Test
    public void queRedirijaALoginSiUsuarioNoAutenticado() {
        Long idMock = 1L;

        when(session.getAttribute("usuario")).thenReturn(null);

        ModelAndView mav = controladorCalificacion.agregarNuevaCalificacion(idMock, "Buena propiedad", 4.5, session);

        assertThat(mav.getViewName(), equalTo("redirect:/login"));
    }

    @Test
    public void queManejeExcepcionesAlAgregarCalificacion() throws Exception {
        Long idMock = 1L;
        Usuario usuarioMock = new Usuario(); // Mockear un usuario según tus necesidades

        when(session.getAttribute("usuario")).thenReturn(usuarioMock);
        doThrow(new CalificacionDenegadaExcepcion("Error en la calificación")).when(servicioCalificacion).agregarNuevaCalificacion(anyLong(), any(), anyString(), anyDouble());

        ModelAndView mav = controladorCalificacion.agregarNuevaCalificacion(idMock, "Buena propiedad", 4.5, session);

        assertThat(mav.getViewName(), equalTo("agregarCalificacion"));
        assertThat(mav.getModel().get("error"), equalTo("Error en la calificación"));
    }
}
