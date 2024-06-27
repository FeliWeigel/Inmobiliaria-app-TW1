package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.entidades.Propiedad;
import com.tallerwebi.dominio.servicio.ServicioPropiedad;
import com.tallerwebi.dominio.servicio.ServicioUsuario;
import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.excepcion.CRUDPropiedadExcepcion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class ControladorPanelTest {
    private ControladorPanel controladorPanel;
    private ServicioPropiedad servicioPropiedad;
    private ServicioUsuario servicioUsuario;
    private HttpSession session;

    @BeforeEach
    public void init(){
        this.servicioPropiedad = mock(ServicioPropiedad.class);
        this.session = mock(HttpSession.class);
        this.servicioUsuario = mock(ServicioUsuario.class);
        this.controladorPanel = new ControladorPanel(this.servicioPropiedad, servicioUsuario);
    }

    @Test
    public void queRedirijaAHomeSiUsuarioNoAutenticadoEnPanelAdminPropiedades() {
        when(session.getAttribute("usuario")).thenReturn(null);

        ModelAndView modelAndView = controladorPanel.panelAdminPropiedades(session);

        assertThat(modelAndView.getViewName(), is("redirect:/home"));
    }

    @Test
    public void queRedirijaAHomeSiUsuarioNoEsAdminEnPanelAdminPropiedades() {
        Usuario usuario = new Usuario();
        usuario.setRol("USER");
        when(session.getAttribute("usuario")).thenReturn(usuario);

        ModelAndView modelAndView = controladorPanel.panelAdminPropiedades(session);

        assertThat(modelAndView.getViewName(), is("redirect:/home"));
    }

    @Test
    public void queMuestrePanelAdminSiUsuarioEsAdminPropiedades() {
        Usuario usuario = new Usuario();
        usuario.setRol("ADMIN");
        when(session.getAttribute("usuario")).thenReturn(usuario);

        List<Propiedad> propiedadesAceptadas = new ArrayList<>();
        propiedadesAceptadas.add(new Propiedad());
        when(servicioPropiedad.listarPropiedadesAceptadas()).thenReturn(propiedadesAceptadas);

        List<Propiedad> propiedadesPendientes = new ArrayList<>();
        propiedadesAceptadas.add(new Propiedad());
        when(servicioPropiedad.listarPropiedadesPendientes()).thenReturn(propiedadesPendientes);

        ModelAndView modelAndView = controladorPanel.panelAdminPropiedades(session);

        assertThat(modelAndView.getViewName(), is("panelAdminPropiedades"));
        assertThat(modelAndView.getModel().get("propiedadesAceptadas"), is(propiedadesAceptadas));
        assertThat(modelAndView.getModel().get("propiedadesPendientes"), is(propiedadesPendientes));
    }

    @Test
    public void queMuestreMensajeDeErrorEnPanelAdminPropiedadesSiHayExcepcion() {
        Usuario usuario = new Usuario();
        usuario.setRol("ADMIN");
        when(session.getAttribute("usuario")).thenReturn(usuario);

        when(servicioPropiedad.listarPropiedadesAceptadas()).thenThrow(new RuntimeException("Error inesperado"));

        ModelAndView modelAndView = controladorPanel.panelAdminPropiedades(session);

        assertThat(modelAndView.getViewName(), is("panelAdminPropiedades"));
        assertThat(modelAndView.getModel().get("message"), is("Ha ocurrido un error inesperado"));
    }


    @Test
    public void queRedirijaAHomeSiUsuarioNoAutenticadoEnAceptarPropiedad() {
        when(session.getAttribute("usuario")).thenReturn(null);

        ModelAndView modelAndView = controladorPanel.aceptarPropiedad(1L, session);

        assertThat(modelAndView.getViewName(), is("redirect:/home"));
    }

    @Test
    public void queRedirijaAHomeSiUsuarioNoEsAdminEnAceptarPropiedad() {
        Usuario usuario = new Usuario();
        usuario.setRol("USER");
        when(session.getAttribute("usuario")).thenReturn(usuario);

        ModelAndView modelAndView = controladorPanel.aceptarPropiedad(1L, session);

        assertThat(modelAndView.getViewName(), is("redirect:/home"));
    }

    @Test
    public void queAceptePropiedadSiUsuarioEsAdmin() {
        Usuario usuario = new Usuario();
        usuario.setRol("ADMIN");
        when(session.getAttribute("usuario")).thenReturn(usuario);

        ModelAndView modelAndView = controladorPanel.aceptarPropiedad(1L, session);

        verify(servicioPropiedad, times(1)).aceptarPropiedad(1L);
        assertThat(modelAndView.getViewName(), is("redirect:/panel-admin/propiedades"));
    }

    @Test
    public void queMuestreErrorEnPanelAdminPropiedadesSiExcepcionEnAceptarPropiedad() {
        Usuario usuario = new Usuario();
        usuario.setRol("ADMIN");
        when(session.getAttribute("usuario")).thenReturn(usuario);

        doThrow(new CRUDPropiedadExcepcion("Error")).when(servicioPropiedad).aceptarPropiedad(1L);

        ModelAndView modelAndView = controladorPanel.aceptarPropiedad(1L, session);

        assertThat(modelAndView.getViewName(), is("redirect:/panel-admin/propiedades"));
        assertThat(modelAndView.getModel().get("error"), is("Error"));
    }


    @Test
    public void queRedirijaAHomeSiUsuarioNoAutenticadoEnRechazarPropiedad() {
        when(session.getAttribute("usuario")).thenReturn(null);

        ModelAndView modelAndView = controladorPanel.rechazarPropiedad(1L, session);

        assertThat(modelAndView.getViewName(), is("redirect:/home"));
    }

    @Test
    public void queRedirijaAHomeSiUsuarioNoEsAdminEnRechazarPropiedad() {
        Usuario usuario = new Usuario();
        usuario.setRol("USER");
        when(session.getAttribute("usuario")).thenReturn(usuario);

        ModelAndView modelAndView = controladorPanel.rechazarPropiedad(1L, session);

        assertThat(modelAndView.getViewName(), is("redirect:/home"));
    }

    @Test
    public void queRechacePropiedadSiUsuarioEsAdmin() {
        Usuario usuario = new Usuario();
        usuario.setRol("ADMIN");
        when(session.getAttribute("usuario")).thenReturn(usuario);

        ModelAndView modelAndView = controladorPanel.rechazarPropiedad(1L, session);

        verify(servicioPropiedad, times(1)).rechazarPropiedad(1L);
        assertThat(modelAndView.getViewName(), is("redirect:/panel-admin/propiedades"));
    }

    @Test
    public void queMuestreErrorEnPanelAdminPropiedadesSiExcepcionEnRechazarPropiedad() {
        Usuario usuario = new Usuario();
        usuario.setRol("ADMIN");
        when(session.getAttribute("usuario")).thenReturn(usuario);

        doThrow(new CRUDPropiedadExcepcion("Error")).when(servicioPropiedad).rechazarPropiedad(1L);

        ModelAndView modelAndView = controladorPanel.rechazarPropiedad(1L, session);

        assertThat(modelAndView.getViewName(), is("redirect:/panel-admin/propiedades"));
        assertThat(modelAndView.getModel().get("error"), is("Error"));
    }


    @Test
    public void queMuestrePanelAdminUsuariosSiUsuarioEsAdmin() {
        Usuario usuario = new Usuario();
        usuario.setRol("ADMIN");
        when(session.getAttribute("usuario")).thenReturn(usuario);

        List<Usuario> usuariosBloqueados = new ArrayList<>();
        usuariosBloqueados.add(new Usuario());
        when(this.servicioUsuario.listarUsuariosBloqueados()).thenReturn(usuariosBloqueados);

        List<Usuario> usuariosDesbloqueados = new ArrayList<>();
        usuariosDesbloqueados.add(new Usuario());
        when(this.servicioUsuario.listarUsuariosDesbloqueados()).thenReturn(usuariosDesbloqueados);

        ModelAndView modelAndView = controladorPanel.panelAdminUsuarios(session);

        assertThat(modelAndView.getViewName(), is("panelAdminUsuarios"));
        assertThat(modelAndView.getModel().get("usuariosDesbloqueados"), is(usuariosDesbloqueados));
        assertThat(modelAndView.getModel().get("usuariosBloqueados"), is(usuariosBloqueados));
    }


    @Test
    public void queBloqueeUsuarioSiUsuarioEsAdmin() {
        Usuario usuario = new Usuario();
        usuario.setRol("ADMIN");
        when(session.getAttribute("usuario")).thenReturn(usuario);

        Long usuarioId = 1L;

        ModelAndView modelAndView = controladorPanel.bloquearUsuario(usuarioId, session);

        verify(servicioUsuario, times(1)).bloquearUsuario(usuarioId);
        assertThat(modelAndView.getViewName(), is("redirect:/panel-admin/usuarios"));
    }

    @Test
    public void queAdminPuedaEliminarUsuarioSiUsuarioEstaBloqueado() {
        Usuario usuario = new Usuario();
        usuario.setRol("ADMIN");
        when(session.getAttribute("usuario")).thenReturn(usuario);

        Long usuarioId = 1L;

        ModelAndView modelAndView = controladorPanel.eliminarUsuario(usuarioId, session);

        verify(servicioUsuario, times(1)).eliminarUsuario(usuarioId);
        assertThat(modelAndView.getViewName(), is("redirect:/panel-admin/usuarios"));
    }

    @Test
    public void queDesbloqueeUsuarioSiUsuarioEsAdmin() {
        Usuario usuario = new Usuario();
        usuario.setRol("ADMIN");
        when(session.getAttribute("usuario")).thenReturn(usuario);

        Long usuarioId = 1L;

        ModelAndView modelAndView = controladorPanel.desbloquearUsuario(usuarioId, session);

        verify(servicioUsuario, times(1)).desbloquearUsuario(usuarioId);
        assertThat(modelAndView.getViewName(), is("redirect:/panel-admin/usuarios"));
    }

    @Test
    public void queRedireccioneAInicioSiUsuarioNoEsAdmin() {
        Usuario usuario = new Usuario();
        usuario.setRol("CLIENTE");
        session.setAttribute("usuario", usuario);

        Long usuarioId = 1L;

        ModelAndView modelAndViewBloquear = controladorPanel.bloquearUsuario(usuarioId, session);
        ModelAndView modelAndViewDesbloquear = controladorPanel.desbloquearUsuario(usuarioId, session);

        assertThat(modelAndViewDesbloquear.getViewName(), is("redirect:/home"));
        assertThat(modelAndViewBloquear.getViewName(), is("redirect:/home"));
    }

    @Test
    public void queManejeExcepcionAlBloquearUsuario() {
        Usuario usuario = new Usuario();
        usuario.setRol("ADMIN");
        when(session.getAttribute("usuario")).thenReturn(usuario);

        Long usuarioId = 1L;

        doThrow(new CRUDPropiedadExcepcion("Error al bloquear usuario")).when(servicioUsuario).bloquearUsuario(usuarioId);

        ModelAndView modelAndView = controladorPanel.bloquearUsuario(usuarioId, session);

        assertThat(modelAndView.getViewName(), is("redirect:/panel-admin/usuarios"));
        assertThat(modelAndView.getModel().get("error"), is("Error al bloquear usuario"));
    }

    @Test
    public void queManejeExcepcionAlDesbloquearUsuario() {
        Usuario usuario = new Usuario();
        usuario.setRol("ADMIN");
        when(session.getAttribute("usuario")).thenReturn(usuario);

        Long usuarioId = 1L;

        doThrow(new CRUDPropiedadExcepcion("Error al desbloquear usuario")).when(servicioUsuario).desbloquearUsuario(usuarioId);

        ModelAndView modelAndView = controladorPanel.desbloquearUsuario(usuarioId, session);

        assertThat(modelAndView.getViewName(), is("redirect:/panel-admin/usuarios"));
        assertThat(modelAndView.getModel().get("error"), is("Error al desbloquear usuario"));
    }

    @Test
    public void queElAdminPuedaEliminarUnaPropiedad() {
        Usuario usuario = new Usuario();
        usuario.setRol("ADMIN");
        when(session.getAttribute("usuario")).thenReturn(usuario);

        ModelAndView modelAndView = controladorPanel.rechazarPropiedad(1L, session);

        verify(servicioPropiedad, times(1)).rechazarPropiedad(1L);
        assertThat(modelAndView.getViewName(), is("redirect:/panel-admin/propiedades"));
    }


    @Test
    public void testModificarPropiedad() {
        Usuario usuario = new Usuario();
        usuario.setRol("ADMIN");
        when(session.getAttribute("usuario")).thenReturn(usuario);
        Propiedad propiedad = new Propiedad();
        propiedad.setId(1L);

        ModelAndView modelAndView = controladorPanel.modificarPropiedad(propiedad, session);
        assertThat("redirect:/panel-admin/propiedades", equalTo(modelAndView.getViewName()));
        verify(servicioPropiedad, times(1)).modificarPropiedad(propiedad);
    }

    @Test
    public void testModificarPropiedadThrowsException() {
        Usuario usuario = new Usuario();
        usuario.setRol("ADMIN");
        when(session.getAttribute("usuario")).thenReturn(usuario);
        Propiedad propiedad = new Propiedad();
        propiedad.setId(1L);
        doThrow(new CRUDPropiedadExcepcion("Error")).when(servicioPropiedad).modificarPropiedad(propiedad);

        ModelAndView modelAndView = controladorPanel.modificarPropiedad(propiedad, session);
        assertThat("redirect:/panel-admin/propiedades",equalTo(modelAndView.getViewName()));
        assertNotNull(modelAndView.getModel().get("error"));
    }

    @Test
    public void testModificarPropiedadNoAdmin() {
        Usuario usuarioNormal = new Usuario();
        usuarioNormal.setRol("USER");
        session.setAttribute("usuario", usuarioNormal);

        Propiedad propiedad = new Propiedad();
        propiedad.setId(1L);

        ModelAndView modelAndView = controladorPanel.modificarPropiedad(propiedad, session);
        verify(servicioPropiedad, never()).modificarPropiedad(any());
        assertThat("redirect:/home", equalTo(modelAndView.getViewName()));
    }

}

