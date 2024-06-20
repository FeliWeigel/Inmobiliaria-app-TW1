package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.*;
import com.tallerwebi.dominio.excepcion.CRUDPropiedadExcepcion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class ControladorPropiedadTest {
    private ControladorPropiedad controladorPropiedad;
    private ServicioPropiedad servicioPropiedad;
    private ServicioUsuario servicioUsuario;
    private HttpSession session;
    private Usuario usuario;

    @BeforeEach
    public void init(){
        this.servicioPropiedad = mock(ServicioPropiedad.class);
        this.session = mock(HttpSession.class);
        this.servicioUsuario = mock(ServicioUsuario.class);
        this.usuario = mock(Usuario.class);
        this.controladorPropiedad = new ControladorPropiedad(this.servicioPropiedad, servicioUsuario);
    }


    @Test
    public void queSeMuestreElListadoDePropiedades(){

        ModelAndView mav = this.controladorPropiedad.vistaListadoPropiedades(this.session);

        assertThat(mav.getViewName(), equalTo("lista-propiedades"));
    }


    @Test
    public void queSeDevuelvaUnaExcepcionAlHaberUnErrorInesperado(){

        when(this.servicioPropiedad.listarPropiedadesAceptadas()).thenThrow(RuntimeException.class);

        ModelAndView mav = this.controladorPropiedad.vistaListadoPropiedades(this.session);

        assertThat(mav.getModel().get("message"), equalTo("Ha Ocurrido un Error Inesperado"));
    }


    @Test
    public void queSeListenTodasLasPropiedaesExistentes(){

        List<Propiedad> propiedades = crearPropiedades();

        when(servicioPropiedad.listarPropiedadesAceptadas()).thenReturn(propiedades);
        ModelAndView mav = this.controladorPropiedad.vistaListadoPropiedades(this.session);
        List<Propiedad> propiedaesDevueltas = (List<Propiedad>) mav.getModel().get("propiedades");

        assertThat(mav.getViewName(), equalTo("lista-propiedades"));
        assertThat(propiedaesDevueltas.size(), equalTo(3));
    }


    @Test
    public void queAlBuscarUnaPropiedadLleveASuVista() {

        Long idMock = 1L;
        Propiedad propiedadMock = mock(Propiedad.class);

        when(this.servicioPropiedad.buscarPropiedad(idMock)).thenReturn(propiedadMock);

        ModelAndView mav = this.controladorPropiedad.verPropiedad(idMock, session);

        assertThat(mav.getViewName(), equalTo("propiedad"));
        assertThat(mav.getModel().get("messageSuccess"), equalTo("Detalles de la Propiedad."));
    }


    @Test
    public void queLosDatosDeUnaPropiedadExistenteSolicitadaSeMuestrenCorrectamente() {

        Long idMock = 1L;
        Propiedad propiedadMock = new Propiedad(idMock, "Casa 1", 2, 3, 4,
                200.0, 150000.0, "Ubicacion 1");

        when(this.servicioPropiedad.buscarPropiedad(idMock)).thenReturn(propiedadMock);

        Propiedad propiedadDevuelta = (Propiedad) this.controladorPropiedad.verPropiedad(idMock, session).getModel().get("propiedad");

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

        ModelAndView mav = this.controladorPropiedad.verPropiedad(idInexistente, session);
        String error = mav.getModel().get("messageError").toString();

        assertThat(mav.getViewName(), equalToIgnoringCase("propiedad"));
        assertThat(error, equalTo("La Propiedad Buscada no Existe."));
    }


    @Test
    public void queSeMuestreUnMensajeDeErrorEnCasoDeExcepcionNoEsperada() {

        Long idPropiedadInexistente = 1L;

        when(this.servicioPropiedad.buscarPropiedad(idPropiedadInexistente)).thenThrow(new RuntimeException());

        ModelAndView mav = this.controladorPropiedad.verPropiedad(idPropiedadInexistente, session);
        String error = mav.getModel().get("messageError").toString();

        assertThat(mav.getViewName(), equalToIgnoringCase("propiedad"));
        assertThat(error, equalTo("Error al encontrar la propiedad seleccionada."));
    }


    @Test
    public void queSeListenLasPropiedadesFiltradas() {
        List<Propiedad> propiedadesFiltradas = crearPropiedades();
        FiltroPropiedad filtro = new FiltroPropiedad();
        filtro.setMinPrecio(1000.0);
        filtro.setMaxPrecio(25000.0);
        filtro.setUbicacion("Ubicacion");

        when(servicioPropiedad.filtrarPropiedades(any(FiltroPropiedad.class))).thenReturn(propiedadesFiltradas);

        ModelAndView mav = controladorPropiedad.filtrarPropiedades(filtro, session);
        List<Propiedad> propiedadesDevueltas = (List<Propiedad>) mav.getModel().get("propiedades");

        assertThat(mav.getViewName(), equalTo("lista-propiedades"));
        assertThat(propiedadesDevueltas.size(), equalTo(3));
    }


    @Test
    public void queSeMuestreMensajeDeErrorCuandoOcurreCRUDPropiedadExcepcionDuranteElFiltrado() {
        FiltroPropiedad filtro = new FiltroPropiedad();
        filtro.setMinPrecio(1000.0);
        filtro.setMaxPrecio(25000.0);
        filtro.setUbicacion("Ubicacion");

        when(servicioPropiedad.filtrarPropiedades(any(FiltroPropiedad.class))).thenThrow(new CRUDPropiedadExcepcion("Error al filtrar propiedades"));

        ModelAndView mav = controladorPropiedad.filtrarPropiedades(filtro, session);

        assertThat(mav.getViewName(), equalTo("lista-propiedades"));
        assertThat(mav.getModel().get("message"), equalTo("Error al filtrar propiedades"));
    }


    @Test
    public void queSeMuestreMensajeDeErrorInesperadoDuranteElFiltrado() {
        FiltroPropiedad filtro = new FiltroPropiedad();
        filtro.setMinPrecio(1000.0);
        filtro.setMaxPrecio(25000.0);
        filtro.setUbicacion("Ubicacion");

        when(servicioPropiedad.filtrarPropiedades(any(FiltroPropiedad.class))).thenThrow(new RuntimeException("Error inesperado"));

        ModelAndView mav = controladorPropiedad.filtrarPropiedades(filtro, session);

        assertThat(mav.getViewName(), equalTo("lista-propiedades"));
        assertThat(mav.getModel().get("message"), equalTo("Ha Ocurrido un Error Inesperado"));
    }


    @Test
    public void queSeMuestreMensajeDeErrorCuandoOcurreCRUDPropiedadExcepcionDuranteObtencionDeFavoritos() {
        List<Propiedad> propiedadesFiltradas = crearPropiedades();
        FiltroPropiedad filtro = new FiltroPropiedad();
        filtro.setMinPrecio(1000.0);
        filtro.setMaxPrecio(25000.0);
        filtro.setUbicacion("Ubicacion");

        Usuario usuarioAutenticado = new Usuario();
        usuarioAutenticado.setNombre("Usuario de prueba");

        when(session.getAttribute("usuario")).thenReturn(usuarioAutenticado);
        when(servicioPropiedad.filtrarPropiedades(any(FiltroPropiedad.class))).thenReturn(propiedadesFiltradas);
        when(servicioUsuario.listarFavoritos(any(Usuario.class))).thenThrow(new CRUDPropiedadExcepcion("Error al listar favoritos"));

        ModelAndView mav = controladorPropiedad.filtrarPropiedades(filtro, session);

        assertThat(mav.getViewName(), equalTo("lista-propiedades"));
        assertThat(mav.getModel().get("propiedades"), equalTo(propiedadesFiltradas));
        assertThat(mav.getModel().get("usuario"), equalTo(usuarioAutenticado));
        assertThat(mav.getModel().get("error"), equalTo("Error al listar favoritos"));
    }


    @Test
    public void queMuestreErrorCuandoOcurreCRUDPropiedadExcepcion() {
        FiltroPropiedad filtro = new FiltroPropiedad();
        filtro.setMinPrecio(1000.0);
        filtro.setMaxPrecio(25000.0);

        doThrow(new CRUDPropiedadExcepcion("Error al filtrar propiedades")).when(servicioPropiedad).filtrarPropiedades(any(FiltroPropiedad.class));

        ModelAndView mav = controladorPropiedad.filtrarPropiedades(filtro, session);

        assertThat(mav.getViewName(), equalTo("lista-propiedades"));
        assertThat(mav.getModel().get("message"), equalTo("Error al filtrar propiedades"));
    }


    @Test
    public void queMuestreErrorCuandoOcurreExcepcionInesperada() {
        FiltroPropiedad filtro = new FiltroPropiedad();
        filtro.setMinPrecio(1000.0);
        filtro.setMaxPrecio(25000.0);

        doThrow(new RuntimeException("Error inesperado")).when(servicioPropiedad).filtrarPropiedades(any(FiltroPropiedad.class));

        ModelAndView mav = controladorPropiedad.filtrarPropiedades(filtro, session);

        assertThat(mav.getViewName(), equalTo("lista-propiedades"));
        assertThat(mav.getModel().get("message"), equalTo("Ha Ocurrido un Error Inesperado"));
    }


   /*

    @Test
    public void queSeListenLasPropiedadesFiltradasPorPrecio(){

        List<Propiedad> propiedadesFiltradas = crearPropiedades();

        when(this.servicioPropiedad.listarPropiedadesPorPrecio(1000.0, 25000.0)).thenReturn(propiedadesFiltradas);
        ModelAndView mav = this.controladorPropiedad.filtrarPropiedadesPorPrecio(1000.0, 25000.0, this.session);
        List<Propiedad> propiedaesDevueltas = (List<Propiedad>) mav.getModel().get("propiedades");

        assertThat(mav.getViewName(), equalTo("lista-propiedades"));
        assertThat(propiedaesDevueltas.size(), equalTo(3));
    }


    @Test
    public void queMuestreErrorCuandoOcurreCRUDPropiedadExcepcionAlListarPropiedadesPorPrecio() {
        doThrow(new CRUDPropiedadExcepcion("Error al filtrar propiedades por precio")).when(servicioPropiedad).listarPropiedadesPorPrecio(anyDouble(), anyDouble());

        ModelAndView mav = this.controladorPropiedad.filtrarPropiedadesPorPrecio(1000.0, 25000.0, this.session);

        assertThat(mav.getViewName(), equalTo("lista-propiedades"));
        assertThat(mav.getModel().get("message"), equalTo("Error al filtrar propiedades por precio"));
    }


    @Test
    public void queMuestreErrorCuandoOcurreExcepcionInesperadaAlListarPropiedadesPorPrecio() {
        doThrow(new RuntimeException("Error inesperado")).when(servicioPropiedad).listarPropiedadesPorPrecio(anyDouble(), anyDouble());

        ModelAndView mav = this.controladorPropiedad.filtrarPropiedadesPorPrecio(1000.0, 25000.0, this.session);

        assertThat(mav.getViewName(), equalTo("lista-propiedades"));
        assertThat(mav.getModel().get("message"), equalTo("Ha Ocurrido un Error Inesperado"));
    }


    @Test
    public void queSeListenLasPropiedadesFiltradasPorUbicacion(){

        List<Propiedad> propiedadesFiltradas = crearPropiedades();

        when(this.servicioPropiedad.listarPropiedadesPorUbicacion("Ubicacion")).thenReturn(propiedadesFiltradas);
        ModelAndView mav = this.controladorPropiedad.filtrarPropiedadesPorUbicacion("Ubicacion", this.session);
        List<Propiedad> propiedaesDevueltas = (List<Propiedad>) mav.getModel().get("propiedades");

        assertThat(mav.getViewName(), equalTo("lista-propiedades"));
        assertThat(propiedaesDevueltas.size(), equalTo(3));
    }


    @Test
    public void queMuestreErrorCuandoOcurreCRUDPropiedadExcepcionAlListarPropiedadesPorUbicacion() {
        doThrow(new CRUDPropiedadExcepcion("Error al filtrar propiedades por ubicación")).when(servicioPropiedad).listarPropiedadesPorUbicacion(anyString());

        ModelAndView mav = this.controladorPropiedad.filtrarPropiedadesPorUbicacion("Ciudad", this.session);

        assertThat(mav.getViewName(), equalTo("lista-propiedades"));
        assertThat(mav.getModel().get("message"), equalTo("Error al filtrar propiedades por ubicación"));
    }


    @Test
    public void queMuestreErrorCuandoOcurreExcepcionInesperadaAlListarPropiedadesPorUbicacion() {
        doThrow(new RuntimeException("Error inesperado")).when(servicioPropiedad).listarPropiedadesPorUbicacion(anyString());

        ModelAndView mav = this.controladorPropiedad.filtrarPropiedadesPorUbicacion("Ciudad", this.session);

        assertThat(mav.getViewName(), equalTo("lista-propiedades"));
        assertThat(mav.getModel().get("message"), equalTo("Ha Ocurrido un Error Inesperado"));
    }

   * */


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
        when(session.getAttribute("usuario")).thenReturn(usuario);
        ModelAndView mav = this.controladorPropiedad.agregarPropiedad(nuevaPropiedad, imagenMock, session);

        assertThat(mav.getViewName(), equalTo("nuevaPropiedad"));
        assertThat(mav.getModel().get("success"), equalTo("La peticion ha sido registrada con exito! La propiedad sera publicada en cuanto verifiquemos los detalles de la venta."));
    }


    @Test
    public void queMuestreErrorCuandoOcurreCRUDPropiedadExcepcionAlAgregarPropiedad() throws CRUDPropiedadExcepcion, IOException {
        Propiedad nuevaPropiedad = new Propiedad();
        MultipartFile imagenMock = mock(MultipartFile.class);
        doThrow(new CRUDPropiedadExcepcion("Error al agregar la propiedad")).when(servicioPropiedad).agregarPropiedad(any(Propiedad.class), any(MultipartFile.class));
        when(session.getAttribute("usuario")).thenReturn(usuario);
        ModelAndView mav = this.controladorPropiedad.agregarPropiedad(nuevaPropiedad, imagenMock, session);

        assertThat(mav.getViewName(), equalTo("nuevaPropiedad"));
        assertThat(mav.getModel().get("error"), equalTo("Error al agregar la propiedad"));
    }


    @Test
    public void queMuestreErrorCuandoOcurreIOExceptionAlAgregarPropiedad() throws CRUDPropiedadExcepcion, IOException {
        Propiedad nuevaPropiedad = new Propiedad();
        MultipartFile imagenMock = mock(MultipartFile.class);
        doThrow(new IOException("Error de E/S")).when(servicioPropiedad).agregarPropiedad(any(Propiedad.class), any(MultipartFile.class));
        when(session.getAttribute("usuario")).thenReturn(usuario);
        ModelAndView mav = this.controladorPropiedad.agregarPropiedad(nuevaPropiedad, imagenMock, session);

        assertThat(mav.getViewName(), equalTo("nuevaPropiedad"));
        assertThat(mav.getModel().get("error"), equalTo("Error de E/S"));
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

