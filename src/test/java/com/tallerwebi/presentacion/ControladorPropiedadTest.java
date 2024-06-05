package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Propiedad;
import com.tallerwebi.dominio.RepositorioUsuario;
import com.tallerwebi.dominio.ServicioPropiedad;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.CRUDPropiedadExcepcion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.mockito.Mockito.*;

public class ControladorPropiedadTest {
    private ControladorPropiedad controladorPropiedad;
    private ServicioPropiedad servicioPropiedad;
    private RepositorioUsuario repositorioUsuario;
    private HttpSession session;

    @BeforeEach
    public void init(){
        this.servicioPropiedad = mock(ServicioPropiedad.class);
        this.session = mock(HttpSession.class);
        this.controladorPropiedad = new ControladorPropiedad(this.servicioPropiedad, repositorioUsuario);
    }


    @Test
    public void queSeMuestreElHome(){

        ModelAndView mav = this.controladorPropiedad.vistaHome(this.session);

        assertThat(mav.getViewName(), equalTo("home"));
    }


    @Test
    public void queSeDevuelvaUnaExcepcionAlHaberUnErrorInesperado(){

        when(this.servicioPropiedad.listarPropiedades()).thenThrow(RuntimeException.class);

        ModelAndView mav = this.controladorPropiedad.vistaHome(this.session);

        assertThat(mav.getModel().get("message"), equalTo("Ha Ocurrido un Error Inesperado"));
    }


    @Test
    public void queSeListenTodasLasPropiedaesExistentes(){

        List<Propiedad> propiedades = crearPropiedades();

        when(servicioPropiedad.listarPropiedades()).thenReturn(propiedades);
        ModelAndView mav = this.controladorPropiedad.vistaHome(this.session);
        List<Propiedad> propiedaesDevueltas = (List<Propiedad>) mav.getModel().get("propiedades");

        assertThat(mav.getViewName(), equalTo("home"));
        assertThat(propiedaesDevueltas.size(), equalTo(3));
    }


    @Test
    public void queAlBuscarUnaPropiedadLleveASuVista() {

        Long idMock = 1L;
        Propiedad propiedadMock = mock(Propiedad.class);

        when(this.servicioPropiedad.buscarPropiedad(idMock)).thenReturn(propiedadMock);

        ModelAndView mav = this.controladorPropiedad.verPropiedad(idMock);

        assertThat(mav.getViewName(), equalTo("propiedad"));
        assertThat(mav.getModel().get("messageSuccess"), equalTo("Detalles de la Propiedad."));
    }


    @Test
    public void queLosDatosDeUnaPropiedadExistenteSolicitadaSeMuestrenCorrectamente() {

        Long idMock = 1L;
        Propiedad propiedadMock = new Propiedad(idMock, "Casa 1", 2, 3, 4,
                200.0, 150000.0, "Ubicacion 1");

        when(this.servicioPropiedad.buscarPropiedad(idMock)).thenReturn(propiedadMock);

        Propiedad propiedadDevuelta = (Propiedad) this.controladorPropiedad.verPropiedad(idMock).getModel().get("propiedad");

        assertThat(propiedadDevuelta.getNombre(), equalToIgnoringCase("Casa 1"));
        assertThat(propiedadDevuelta.getPisos(), equalTo(2));
        assertThat(propiedadDevuelta.getBanios(), equalTo(3));
        assertThat(propiedadDevuelta.getHabitaciones(), equalTo(4));
        assertThat(propiedadDevuelta.getSuperficie(), equalTo(200.0));
        assertThat(propiedadDevuelta.getPrecio(), equalTo(150000.0));
        assertThat(propiedadDevuelta.getUbicacion(), equalToIgnoringCase("Ubicacion 1"));
    }


    @Test
    public void queSeMuestreUnMensajeDeErrorCuandoSeSolicitaVerUnaPropiedadInexistente() {

        Long idInexistente = 12L;

        when(this.servicioPropiedad.buscarPropiedad(idInexistente)).thenThrow(new CRUDPropiedadExcepcion("La Propiedad Buscada no Existe."));

        ModelAndView mav = this.controladorPropiedad.verPropiedad(idInexistente);
        String error = mav.getModel().get("messageError").toString();

        assertThat(mav.getViewName(), equalToIgnoringCase("propiedad"));
        assertThat(error, equalTo("La Propiedad Buscada no Existe."));
    }


    @Test
    public void queSeMuestreUnMensajeDeErrorEnCasoDeExcepcionNoEsperada() {

        Long idPropiedadInexistente = 1L;

        when(this.servicioPropiedad.buscarPropiedad(idPropiedadInexistente)).thenThrow(new RuntimeException());

        ModelAndView mav = this.controladorPropiedad.verPropiedad(idPropiedadInexistente);
        String error = mav.getModel().get("messageError").toString();

        assertThat(mav.getViewName(), equalToIgnoringCase("propiedad"));
        assertThat(error, equalTo("Error al encontrar la propiedad seleccionada."));
    }


    @Test
    public void queSeListenLasPropiedadesFiltradasPorPrecio(){

        List<Propiedad> propiedadesFiltradas = crearPropiedades();

        when(this.servicioPropiedad.listarPropiedadesPorPrecio(1000.0, 25000.0)).thenReturn(propiedadesFiltradas);
        ModelAndView mav = this.controladorPropiedad.filtrarPropiedadesPorPrecio(1000.0, 25000.0, this.session);
        List<Propiedad> propiedaesDevueltas = (List<Propiedad>) mav.getModel().get("propiedades");

        assertThat(mav.getViewName(), equalTo("home"));
        assertThat(propiedaesDevueltas.size(), equalTo(3));
    }


    @Test
    public void queMuestreErrorCuandoOcurreCRUDPropiedadExcepcionAlListarPropiedadesPorPrecio() {
        doThrow(new CRUDPropiedadExcepcion("Error al filtrar propiedades por precio")).when(servicioPropiedad).listarPropiedadesPorPrecio(anyDouble(), anyDouble());

        ModelAndView mav = this.controladorPropiedad.filtrarPropiedadesPorPrecio(1000.0, 25000.0, this.session);

        assertThat(mav.getViewName(), equalTo("home"));
        assertThat(mav.getModel().get("message"), equalTo("Error al filtrar propiedades por precio"));
    }


    @Test
    public void queMuestreErrorCuandoOcurreExcepcionInesperadaAlListarPropiedadesPorPrecio() {
        doThrow(new RuntimeException("Error inesperado")).when(servicioPropiedad).listarPropiedadesPorPrecio(anyDouble(), anyDouble());

        ModelAndView mav = this.controladorPropiedad.filtrarPropiedadesPorPrecio(1000.0, 25000.0, this.session);

        assertThat(mav.getViewName(), equalTo("home"));
        assertThat(mav.getModel().get("message"), equalTo("Ha Ocurrido un Error Inesperado"));
    }


    @Test
    public void queSeListenLasPropiedadesFiltradasPorUbicacion(){

        List<Propiedad> propiedadesFiltradas = crearPropiedades();

        when(this.servicioPropiedad.listarPropiedadesPorUbicacion("Ubicacion")).thenReturn(propiedadesFiltradas);
        ModelAndView mav = this.controladorPropiedad.filtrarPropiedadesPorUbicacion("Ubicacion", this.session);
        List<Propiedad> propiedaesDevueltas = (List<Propiedad>) mav.getModel().get("propiedades");

        assertThat(mav.getViewName(), equalTo("home"));
        assertThat(propiedaesDevueltas.size(), equalTo(3));
    }


    @Test
    public void queMuestreErrorCuandoOcurreCRUDPropiedadExcepcionAlListarPropiedadesPorUbicacion() {
        doThrow(new CRUDPropiedadExcepcion("Error al filtrar propiedades por ubicación")).when(servicioPropiedad).listarPropiedadesPorUbicacion(anyString());

        ModelAndView mav = this.controladorPropiedad.filtrarPropiedadesPorUbicacion("Ciudad", this.session);

        assertThat(mav.getViewName(), equalTo("home"));
        assertThat(mav.getModel().get("message"), equalTo("Error al filtrar propiedades por ubicación"));
    }


    @Test
    public void queMuestreErrorCuandoOcurreExcepcionInesperadaAlListarPropiedadesPorUbicacion() {
        doThrow(new RuntimeException("Error inesperado")).when(servicioPropiedad).listarPropiedadesPorUbicacion(anyString());

        ModelAndView mav = this.controladorPropiedad.filtrarPropiedadesPorUbicacion("Ciudad", this.session);

        assertThat(mav.getViewName(), equalTo("home"));
        assertThat(mav.getModel().get("message"), equalTo("Ha Ocurrido un Error Inesperado"));
    }


    @Test
    public void queRetorneVistaAgregarPropiedadCorrectamente() {
        ModelAndView mav = this.controladorPropiedad.vistaAgregarPropiedad();

        assertThat(mav.getViewName(), equalTo("nuevaPropiedad"));
        assertThat(mav.getModel().get("propiedad"), instanceOf(Propiedad.class));
    }


    @Test
    public void queMuestreMensajeDeExitoAlAgregarPropiedad() throws CRUDPropiedadExcepcion, IOException {
        Propiedad nuevaPropiedad = new Propiedad();
        MultipartFile imagenMock = mock(MultipartFile.class);

        ModelAndView mav = this.controladorPropiedad.agregarPropiedad(nuevaPropiedad, imagenMock);

        assertThat(mav.getViewName(), equalTo("nuevaPropiedad"));
        assertThat(mav.getModel().get("success"), equalTo("La Propiedad ha sido agregada con exito!"));
    }


    @Test
    public void queMuestreErrorCuandoOcurreCRUDPropiedadExcepcionAlAgregarPropiedad() throws CRUDPropiedadExcepcion, IOException {
        Propiedad nuevaPropiedad = new Propiedad();
        MultipartFile imagenMock = mock(MultipartFile.class);
        doThrow(new CRUDPropiedadExcepcion("Error al agregar la propiedad")).when(servicioPropiedad).agregarPropiedad(any(Propiedad.class), any(MultipartFile.class));

        ModelAndView mav = this.controladorPropiedad.agregarPropiedad(nuevaPropiedad, imagenMock);

        assertThat(mav.getViewName(), equalTo("nuevaPropiedad"));
        assertThat(mav.getModel().get("error"), equalTo("Error al agregar la propiedad"));
    }


    @Test
    public void queMuestreErrorCuandoOcurreIOExceptionAlAgregarPropiedad() throws CRUDPropiedadExcepcion, IOException {
        Propiedad nuevaPropiedad = new Propiedad();
        MultipartFile imagenMock = mock(MultipartFile.class);
        doThrow(new IOException("Error de E/S")).when(servicioPropiedad).agregarPropiedad(any(Propiedad.class), any(MultipartFile.class));

        ModelAndView mav = this.controladorPropiedad.agregarPropiedad(nuevaPropiedad, imagenMock);

        assertThat(mav.getViewName(), equalTo("nuevaPropiedad"));
        assertThat(mav.getModel().get("error"), equalTo("Error de E/S"));
    }


    @Test
    public void queRedirijaAHomeSiUsuarioNoAutenticadoEnPanelAdmin() {
        when(session.getAttribute("usuario")).thenReturn(null);

        ModelAndView modelAndView = controladorPropiedad.panelAdmin(session);

        assertThat(modelAndView.getViewName(), is("redirect:/home"));
    }

    @Test
    public void queRedirijaAHomeSiUsuarioNoEsAdminEnPanelAdmin() {
        Usuario usuario = new Usuario();
        usuario.setRol("USER");
        when(session.getAttribute("usuario")).thenReturn(usuario);

        ModelAndView modelAndView = controladorPropiedad.panelAdmin(session);

        assertThat(modelAndView.getViewName(), is("redirect:/home"));
    }

    @Test
    public void queMuestrePanelAdminSiUsuarioEsAdmin() {
        Usuario usuario = new Usuario();
        usuario.setRol("ADMIN");
        when(session.getAttribute("usuario")).thenReturn(usuario);

        List<Propiedad> propiedades = new ArrayList<>();
        propiedades.add(new Propiedad());
        when(servicioPropiedad.listarPropiedades()).thenReturn(propiedades);

        ModelAndView modelAndView = controladorPropiedad.panelAdmin(session);

        assertThat(modelAndView.getViewName(), is("panelAdmin"));
        assertThat(modelAndView.getModel().get("propiedades"), is(propiedades));
    }

    @Test
    public void queMuestreMensajeDeErrorEnPanelAdminSiHayExcepcion() {
        Usuario usuario = new Usuario();
        usuario.setRol("ADMIN");
        when(session.getAttribute("usuario")).thenReturn(usuario);

        when(servicioPropiedad.listarPropiedades()).thenThrow(new RuntimeException("Error inesperado"));

        ModelAndView modelAndView = controladorPropiedad.panelAdmin(session);

        assertThat(modelAndView.getViewName(), is("panelAdmin"));
        assertThat(modelAndView.getModel().get("message"), is("Ha ocurrido un error inesperado"));
    }


    @Test
    public void queRedirijaAHomeSiUsuarioNoAutenticadoEnAceptarPropiedad() {
        when(session.getAttribute("usuario")).thenReturn(null);

        ModelAndView modelAndView = controladorPropiedad.aceptarPropiedad(1L, session);

        assertThat(modelAndView.getViewName(), is("redirect:/home"));
    }

    @Test
    public void queRedirijaAHomeSiUsuarioNoEsAdminEnAceptarPropiedad() {
        Usuario usuario = new Usuario();
        usuario.setRol("USER");
        when(session.getAttribute("usuario")).thenReturn(usuario);

        ModelAndView modelAndView = controladorPropiedad.aceptarPropiedad(1L, session);

        assertThat(modelAndView.getViewName(), is("redirect:/home"));
    }

    @Test
    public void queAceptePropiedadSiUsuarioEsAdmin() {
        Usuario usuario = new Usuario();
        usuario.setRol("ADMIN");
        when(session.getAttribute("usuario")).thenReturn(usuario);

        ModelAndView modelAndView = controladorPropiedad.aceptarPropiedad(1L, session);

        verify(servicioPropiedad, times(1)).aceptarPropiedad(1L);
        assertThat(modelAndView.getViewName(), is("redirect:/panel-admin"));
    }

    @Test
    public void queMuestreErrorEnPanelAdminSiExcepcionEnAceptarPropiedad() {
        Usuario usuario = new Usuario();
        usuario.setRol("ADMIN");
        when(session.getAttribute("usuario")).thenReturn(usuario);

        doThrow(new CRUDPropiedadExcepcion("Error")).when(servicioPropiedad).aceptarPropiedad(1L);

        ModelAndView modelAndView = controladorPropiedad.aceptarPropiedad(1L, session);

        assertThat(modelAndView.getViewName(), is("redirect:/panel-admin"));
        assertThat(modelAndView.getModel().get("error"), is("Error"));
    }


    @Test
    public void queRedirijaAHomeSiUsuarioNoAutenticadoEnRechazarPropiedad() {
        when(session.getAttribute("usuario")).thenReturn(null);

        ModelAndView modelAndView = controladorPropiedad.rechazarPropiedad(1L, session);

        assertThat(modelAndView.getViewName(), is("redirect:/home"));
    }

    @Test
    public void queRedirijaAHomeSiUsuarioNoEsAdminEnRechazarPropiedad() {
        Usuario usuario = new Usuario();
        usuario.setRol("USER");
        when(session.getAttribute("usuario")).thenReturn(usuario);

        ModelAndView modelAndView = controladorPropiedad.rechazarPropiedad(1L, session);

        assertThat(modelAndView.getViewName(), is("redirect:/home"));
    }

    @Test
    public void queRechacePropiedadSiUsuarioEsAdmin() {
        Usuario usuario = new Usuario();
        usuario.setRol("ADMIN");
        when(session.getAttribute("usuario")).thenReturn(usuario);

        ModelAndView modelAndView = controladorPropiedad.rechazarPropiedad(1L, session);

        verify(servicioPropiedad, times(1)).rechazarPropiedad(1L);
        assertThat(modelAndView.getViewName(), is("redirect:/panel-admin"));
    }

    @Test
    public void queMuestreErrorEnPanelAdminSiExcepcionEnRechazarPropiedad() {
        Usuario usuario = new Usuario();
        usuario.setRol("ADMIN");
        when(session.getAttribute("usuario")).thenReturn(usuario);

        doThrow(new CRUDPropiedadExcepcion("Error")).when(servicioPropiedad).rechazarPropiedad(1L);

        ModelAndView modelAndView = controladorPropiedad.rechazarPropiedad(1L, session);

        assertThat(modelAndView.getViewName(), is("redirect:/panel-admin"));
        assertThat(modelAndView.getModel().get("error"), is("Error"));
    }







    private List<Propiedad> crearPropiedades() {
        List<Propiedad> propiedades = new ArrayList<>();

        Propiedad propiedad1 = new Propiedad(1L, "Casa 1", 2, 3, 4,
                200.0, 150000.0, "Ubicacion 1");
        Propiedad propiedad2 = new Propiedad(2L, "Casa 2", 2, 3, 4,
                200.0, 300000.0, "Ubicacion 2");
        Propiedad propiedad3 = new Propiedad(3L, "Casa 3", 2, 3, 4,
                200.0, 600000.0, "Ubicacion 3");

        propiedades.add(propiedad1);
        propiedades.add(propiedad2);
        propiedades.add(propiedad3);

        return propiedades;
    }

}

