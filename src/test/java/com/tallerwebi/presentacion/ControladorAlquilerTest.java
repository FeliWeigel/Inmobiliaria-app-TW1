package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.dto.FechasAlquilerDTO;
import com.tallerwebi.dominio.servicio.ServicioAlquiler;
import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.excepcion.AlquilerDenegadoExcepcion;
import com.tallerwebi.dominio.excepcion.CRUDPropiedadExcepcion;
import com.tallerwebi.dominio.excepcion.UsuarioNoIdentificadoExcepcion;
import com.tallerwebi.dominio.servicio.ServicioPropiedad;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class ControladorAlquilerTest {

    private ServicioAlquiler servicioAlquiler;
    private ControladorAlquiler controladorAlquiler;
    private HttpSession session;
    private Usuario usuario;
    private HttpServletResponse response;
    private ServicioPropiedad servicioPropiedad;

    private Date fechaInicio;
    private Date fechaFin;

    @BeforeEach
    public void init() {
        servicioAlquiler = mock(ServicioAlquiler.class);
        servicioPropiedad = mock(ServicioPropiedad.class);
        controladorAlquiler = new ControladorAlquiler(servicioAlquiler, servicioPropiedad);
        session = mock(HttpSession.class);
        usuario = mock(Usuario.class);
        response = mock(HttpServletResponse.class);

        java.util.Date utilDateInicio = new java.util.Date(2022 - 1900, 0, 1);  // 2022-01-01
        java.util.Date utilDateFin = new java.util.Date(2022 - 1900, 0, 10);    // 2022-01-10
        fechaInicio = new Date(utilDateInicio.getTime());
        fechaFin = new Date(utilDateFin.getTime());
    }


    @Test
    public void queAgregueNuevoAlquilerCorrectamente() throws Exception {
        when(session.getAttribute("usuario")).thenReturn(usuario);

        ModelAndView mav = controladorAlquiler.nuevoAlquiler(1L, session, fechaInicio, fechaFin);

        verify(servicioAlquiler, times(1)).agregarNuevoAlquiler(any(Usuario.class), anyLong(), any(Date.class), any(Date.class));
        assertThat(mav.getViewName(), equalTo("pago"));
        assertThat(mav.getModel().get("success"), equalTo("Alquiler efectuado correctamente! Sera contactado por el propietario en las proximas 72hs."));
    }


    @Test
    public void queRedirijaAlLoginCuandoUsuarioNoAutenticado() {
        when(session.getAttribute("usuario")).thenReturn(null);

        ModelAndView mav = controladorAlquiler.nuevoAlquiler(1L, session, fechaInicio, fechaFin);

        assertThat(mav.getViewName(), equalTo("redirect:/login"));
    }


    @Test
    public void queMuestreErrorCuandoOcurreUsuarioNoIdentificadoExcepcion() throws Exception {
        when(session.getAttribute("usuario")).thenReturn(usuario);
        doThrow(new UsuarioNoIdentificadoExcepcion()).when(servicioAlquiler).agregarNuevoAlquiler(any(Usuario.class), anyLong(), any(Date.class), any(Date.class));

        ModelAndView mav = controladorAlquiler.nuevoAlquiler(1L, session, fechaInicio, fechaFin);

        assertThat(mav.getModel().get("error"), equalTo("Ha ocurrido un error inesperado, vuelva a intentarlo en unos minutos."));
    }

    @Test
    public void queMuestreErrorCuandoOcurreCRUDPropiedadExcepcion() throws Exception {
        when(session.getAttribute("usuario")).thenReturn(usuario);
        doThrow(new CRUDPropiedadExcepcion("Error al agregar alquiler")).when(servicioAlquiler).agregarNuevoAlquiler(any(Usuario.class), anyLong(), any(Date.class), any(Date.class));

        ModelAndView mav = controladorAlquiler.nuevoAlquiler(1L, session, fechaInicio, fechaFin);

        assertThat(mav.getModel().get("error"), equalTo("Ha ocurrido un error inesperado, vuelva a intentarlo en unos minutos."));
    }

    @Test
    public void queMuestreErrorCuandoOcurreAlquilerDenegadoExcepcion() throws Exception {
        when(session.getAttribute("usuario")).thenReturn(usuario);
        doThrow(new AlquilerDenegadoExcepcion("Las fechas seleccionadas ya estan reservadas.")).when(servicioAlquiler).agregarNuevoAlquiler(any(Usuario.class), anyLong(), any(Date.class), any(Date.class));

        ModelAndView mav = controladorAlquiler.nuevoAlquiler(1L, session, fechaInicio, fechaFin);

        assertThat(mav.getModel().get("error"), equalTo("Las fechas seleccionadas ya estan reservadas."));
    }

    @Test
    public void queRedirijaAlLoginCuandoUsuarioNoAutenticadoEnVistaAlquilerPropiedad() {
        when(session.getAttribute("usuario")).thenReturn(null);

        ModelAndView mav = controladorAlquiler.vistaAlquilerPropiedad(1L, session);

        assertThat(mav.getViewName(), equalTo("redirect:/login"));
    }

    @Test
    public void queRedirijaHomeCuandoPropiedadIdEsNuloEnVistaAlquilerPropiedad() {
        when(session.getAttribute("usuario")).thenReturn(usuario);

        ModelAndView mav = controladorAlquiler.vistaAlquilerPropiedad(null, session);

        assertThat(mav.getViewName(), equalTo("redirect:/home"));
    }

    @Test
    public void queMuestreVistaAlquilerPropiedadCorrectamente() {
        when(session.getAttribute("usuario")).thenReturn(usuario);

        ModelAndView mav = controladorAlquiler.vistaAlquilerPropiedad(1L, session);

        assertThat(mav.getViewName(), equalTo("alquiler"));
        assertThat(mav.getModel().get("usuario"), equalTo(usuario));
        assertThat(mav.getModel().get("propiedadId"), equalTo(1L));
    }

    @Test
    public void queDevuelvaFechasReservadasPorPropiedad() throws Exception {
        List<FechasAlquilerDTO> fechasReservadas = new ArrayList<>();
        when(servicioAlquiler.fechasReservadasPorPropiedad(anyLong())).thenReturn(fechasReservadas);

        PrintWriter writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);

        controladorAlquiler.fechasReservadasPorPropiedad(1L, response);

        verify(servicioAlquiler, times(1)).fechasReservadasPorPropiedad(1L);
    }
}

