package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Propiedad;
import com.tallerwebi.dominio.RepositorioPropiedad;
import com.tallerwebi.dominio.ServicioPropiedad;
import com.tallerwebi.dominio.excepcion.CRUDPropiedadExcepcion;
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

    @Test
    public void queAlFiltrarPorPrecioMinimoSeListenLasPropiedadesQueCumplanConElRequisito(){
        // cuando corre mostrarPropiedadesFiltradas llama al metodo listarPropiedades del
        // servicioPropiedad. Siempre devuelve 0 porque el servicioPropiedad no tiene ninguna
        // propiedad cargada. El tema es que no se como simular esa base de datos. Intenté con
        // hacer este código:
        // List<Propiedad> propiedades = crearPropiedades();
        // when(servicioPropiedad.listarPropiedades()).thenReturn(propiedades);
        // Pero no funciona porque no se llama a listarPropiedades desde este test, sino que
        // es llamado desde el servicioPropiedad, y siempre devulve 0.
        // El que me pueda ayudar buenisimo, yo ya me re quemé


        DatosFiltro datosFiltroMock = new DatosFiltro(TipoDeFiltro.PRECIO);
        datosFiltroMock.setPrecio(10000.0);
        datosFiltroMock.setFiltroPorPrecio(FiltroPorPrecio.MINIMO);

        // Ejecutar el método que deseas probar
        ModelAndView mav = this.controladorPropiedad.mostrarPropiedadesFiltradas(datosFiltroMock);

        // Verificar los resultados
        List<Propiedad> propiedadesFiltradas = (List<Propiedad>) mav.getModel().get("propiedades");
        assertThat(mav.getViewName(), equalTo("home"));
        assertThat(propiedadesFiltradas.size(), equalTo(3));
    }
}

