package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Propiedad;
import com.tallerwebi.dominio.RepositorioPropiedad;
import com.tallerwebi.dominio.ServicioPropiedad;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ControladorPropiedadTest {
    private ControladorPropiedad controladorPropiedad;
    private ServicioPropiedad servicioPropiedad;

    @BeforeEach
    public void init(){
        this.servicioPropiedad = mock(ServicioPropiedad.class);
        this.controladorPropiedad = new ControladorPropiedad(this.servicioPropiedad);
    }

    @Test
    public void queAlBuscarUnaPropiedadLleveASuVista() {

        Long idMock = 1L;
        Propiedad propiedadMock = mock(Propiedad.class);
        ModelAndView modelAndView = crearModelAndViewMock("Detalles de la Propiedad.", propiedadMock);

        when(this.servicioPropiedad.buscarPropiedad(idMock)).thenReturn(modelAndView);

        ModelAndView mav = this.controladorPropiedad.verPropiedad(idMock);

        assertThat(mav.getViewName(), equalTo("propiedad"));
    }

    @Test
    public void queSeMuestreUnMensajeDeErrorCuandoSeSolicitaVerUnaPropiedadInexistente() {

        Long idInexistente = 12L;
        ModelAndView modelAndView = crearModelAndViewMock("La Propiedad Buscada no Existe.", null);

        when(this.servicioPropiedad.buscarPropiedad(idInexistente)).thenReturn(modelAndView);

        ModelAndView mav = this.controladorPropiedad.verPropiedad(idInexistente);
        String error = mav.getModel().get("message").toString();

        assertThat(mav.getViewName(), equalToIgnoringCase("propiedad"));
        assertThat(error, equalTo("La Propiedad Buscada no Existe."));
    }


    @Test
    public void queLosDatosDeUnaPropiedadExistenteSolicitadaSeMuestrenCorrectamente() {

        Long idMock = 1L;
        Propiedad propiedadMock = new Propiedad(idMock, "Casa 1", 2, 3, 4,
                200.0, 150000.0, "Ubicacion 1");
        ModelAndView modelAndViewMock = crearModelAndViewMock("Detalles de la Propiedad.", propiedadMock);

        when(this.servicioPropiedad.buscarPropiedad(idMock)).thenReturn(modelAndViewMock);

        ModelAndView mav = this.controladorPropiedad.verPropiedad(idMock);
        Propiedad propiedadDevuelta = (Propiedad) mav.getModel().get("propiedad");

        assertThat(propiedadDevuelta.getNombre(), equalToIgnoringCase("Casa 1"));
        assertThat(propiedadDevuelta.getPisos(), equalTo(2));
        assertThat(propiedadDevuelta.getBanios(), equalTo(3));
        assertThat(propiedadDevuelta.getHabitaciones(), equalTo(4));
        assertThat(propiedadDevuelta.getSuperficie(), equalTo(200.0));
        assertThat(propiedadDevuelta.getPrecio(), equalTo(150000.0));
        assertThat(propiedadDevuelta.getUbicacion(), equalToIgnoringCase("Ubicacion 1"));
    }


    @Test
    public void queSeMuestreUnMensajeDeErrorEnCasoDeExcepcionNoEsperada() {

        Long idPropiedad = 1L;
        Propiedad propiedadMock = mock(Propiedad.class);
        ModelAndView modelAndView = crearModelAndViewMock("Error al Mostrar la Propiedad.", propiedadMock);

        when(this.servicioPropiedad.buscarPropiedad(idPropiedad)).thenReturn(modelAndView);

        ModelAndView mav = this.controladorPropiedad.verPropiedad(idPropiedad);
        String error = mav.getModel().get("message").toString();

        assertThat(mav.getViewName(), equalToIgnoringCase("propiedad"));
        assertThat(error, equalTo("Error al Mostrar la Propiedad."));
    }



    private ModelAndView crearModelAndViewMock(String mensaje, Propiedad propiedadMock) {
        ModelAndView modelAndView = new ModelAndView("propiedad");
        modelAndView.addObject("message", mensaje);
        modelAndView.addObject("propiedad", propiedadMock);

        return modelAndView;
    }

}


