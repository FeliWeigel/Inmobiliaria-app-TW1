package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Propiedad;
import com.tallerwebi.dominio.RepositorioPropiedad;
import com.tallerwebi.dominio.ServicioPropiedad;
import com.tallerwebi.dominio.excepcion.CRUDPropiedadExcepcion;
import com.tallerwebi.dominio.filtro.FiltroPorPrecio;
import com.tallerwebi.dominio.filtro.FiltroPropiedad;
import org.dom4j.rule.Mode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ControladorPropiedadTest {
    private ControladorPropiedad controladorPropiedad;
    private ServicioPropiedad servicioPropiedad;
    private DatosFiltro datosFiltroMock;

    @BeforeEach
    public void init(){
        this.servicioPropiedad = mock(ServicioPropiedad.class);
        this.controladorPropiedad = new ControladorPropiedad(this.servicioPropiedad);
        this.datosFiltroMock = new DatosFiltro(TipoDeFiltro.PRECIO);
    }


    @Test
    public void queSeMuestreElHome(){

        ModelAndView mav = this.controladorPropiedad.irAHome();

        assertThat(mav.getViewName(), equalTo("home"));
    }


    @Test
    public void queSeDevuelvaUnaExcepcionAlHaberUnErrorInesperado(){

        when(this.servicioPropiedad.listarPropiedades()).thenThrow(RuntimeException.class);

        ModelAndView mav = this.controladorPropiedad.irAHome();

        assertThat(mav.getModel().get("message"), equalTo("Ha Ocurrido un Error Inesperado"));
    }


    @Test
    public void queSeListenTodasLasPropiedaesExistentes(){

        List<Propiedad> propiedades = crearPropiedades();

        when(servicioPropiedad.listarPropiedades()).thenReturn(propiedades);
        ModelAndView mav = this.controladorPropiedad.irAHome();
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
        assertThat(mav.getModel().get("message"), equalTo("Detalles de la Propiedad."));
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
        String error = mav.getModel().get("message").toString();

        assertThat(mav.getViewName(), equalToIgnoringCase("propiedad"));
        assertThat(error, equalTo("La Propiedad Buscada no Existe."));
    }


    @Test
    public void queSeMuestreUnMensajeDeErrorEnCasoDeExcepcionNoEsperada() {

        Long idPropiedadInexistente = 1L;

        when(this.servicioPropiedad.buscarPropiedad(idPropiedadInexistente)).thenThrow(new RuntimeException());

        ModelAndView mav = this.controladorPropiedad.verPropiedad(idPropiedadInexistente);
        String error = mav.getModel().get("message").toString();

        assertThat(mav.getViewName(), equalToIgnoringCase("propiedad"));
        assertThat(error, equalTo("Error al Mostrar la Propiedad."));
    }


    @Test
    public void queAlFiltrarPorPrecioSeListenLasPropiedadesQueCumplanConElRequisito(){

        // El otro test estaba haciendo algo más parecido a testear el servicio. Este test solo hace un mock de las
        // propiedades que se supone que se deben devolver y de una instancia de DatosFiltro. No importan sus valores, porque
        // que se devuelva lo correcto ya fue testeado en ServicioPropiedadTest, por lo que asumimos que las propiedades que
        // devuelve el metodo filtrarPropiedades(), ya son adecuadas y establecemos que las que tiene que devolver son los mocks)

        DatosFiltro datosFiltroMock = mock(DatosFiltro.class);
        List<Propiedad> propiedadesMock = crearPropiedades();
        FiltroPropiedad filtro = new FiltroPorPrecio();

        when(this.servicioPropiedad.filtrar(filtro, datosFiltroMock)).thenReturn(propiedadesMock);

        ModelAndView mav = this.controladorPropiedad.mostrarPropiedadesFiltradas(datosFiltroMock);

        List<Propiedad> propiedadesFiltradas = (List<Propiedad>) mav.getModel().get("propiedades");

        // Lo unico que queremos comprobar es que la cantidad de propiedades correcta esta en el ModelAndView.
        assertThat(mav.getViewName(), equalTo("home"));
        assertThat(propiedadesFiltradas.size(), equalTo(3));
        // Dejo el test anterior comentado abajo, si te parece que este esta bien, borra el test comentado.
    }


    /*@Test
    public void queAlFiltrarPorPrecioMinimoSeListenLasPropiedadesQueCumplanConElRequisito(){

        DatosFiltro datosFiltroMock = new DatosFiltro(TipoDeFiltro.PRECIO);
        datosFiltroMock.setPrecio(10000.0);
        datosFiltroMock.setFiltroPorPrecio(FiltroPorPrecio.MINIMO);
        List<Propiedad> propiedades = crearPropiedades();
        when(this.servicioPropiedad.filtrarPropiedades(datosFiltroMock)).thenReturn(propiedades);

        // Ejecutar el método que deseas probar
        ModelAndView mav = this.controladorPropiedad.mostrarPropiedadesFiltradas(datosFiltroMock);

        // Verificar los resultados
        List<Propiedad> propiedadesFiltradas = (List<Propiedad>) mav.getModel().get("propiedades");
        assertThat(mav.getViewName(), equalTo("home"));
        assertThat(propiedadesFiltradas.size(), equalTo(3));
    }*/





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

