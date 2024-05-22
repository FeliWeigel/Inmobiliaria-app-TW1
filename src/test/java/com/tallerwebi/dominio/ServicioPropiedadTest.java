package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.CRUDPropiedadExcepcion;
import com.tallerwebi.dominio.excepcion.CredencialesInvalidasExcepcion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ServicioPropiedadTest {
    private RepositorioPropiedad repositorioPropiedad;
    private ServicioPropiedad servicioPropiedad;

    List<Propiedad> propiedadesMock;
    private SubirImagenServicio imagenServicio;

    @BeforeEach
    public void init() {
        this.repositorioPropiedad = mock(RepositorioPropiedad.class);
        this.imagenServicio= mock(SubirImagenServicio.class);
        this.servicioPropiedad = new ServicioPropiedad(this.repositorioPropiedad, imagenServicio);

        propiedadesMock = new ArrayList<>();
        propiedadesMock.add(new Propiedad(1L, "Casa 1", 2, 3, 4, 200.0, 150000.0, "Ubicacion 1"));
        propiedadesMock.add(new Propiedad(2L, "Casa 2", 3, 2, 5, 250.0, 180000.0, "Ubicacion 2"));
        propiedadesMock.add(new Propiedad(3L, "Casa 3", 1, 1, 2, 120.0, 90000.0, "Ubicacion 3"));
    }


    @Test
    public void queSeDevuelvaLaPropiedadBuscada() {

        Long idMock = 1L;
        Propiedad propiedadMock = mock(Propiedad.class);

        when(this.repositorioPropiedad.buscarPropiedad(idMock)).thenReturn(propiedadMock);
        Propiedad propiedadBuscada = this.servicioPropiedad.buscarPropiedad(idMock);

        assertThat(propiedadBuscada, equalTo(propiedadMock));
    }


    @Test
    public void queSeLanceUnaExcepcionCuandoLaPropiedadBuscadaNoExiste() {

        Long idInexistente = 1L;

        assertThrows(CRUDPropiedadExcepcion.class, () -> {
            this.servicioPropiedad.buscarPropiedad(idInexistente);
        });
    }


    @Test
    public void queSeDevuelvanLasPropiedadesListadas() {

        when(this.repositorioPropiedad.listarPropiedades()).thenReturn(propiedadesMock);
        List<Propiedad> propiedadesListadas = this.servicioPropiedad.listarPropiedades();

        assertThat(propiedadesListadas, equalTo(propiedadesMock));
    }


    @Test
    public void queSeDevuelvanLasPropiedadesFiltradasPorPrecio() {

        when(this.repositorioPropiedad.listarPorRangoPrecio(1000.0, 25000.0)).thenReturn(propiedadesMock);
        List<Propiedad> propiedadesListadas = this.servicioPropiedad.listarPropiedadesPorPrecio(1000.0, 25000.0);

        assertThat(propiedadesListadas, equalTo(propiedadesMock));
    }


    @Test
    public void queSeLanceUnaExcepcionAlIntentarFiltradasPorPrecioDeFormaInvalida() {


        assertThrows(CRUDPropiedadExcepcion.class, () -> {
            this.servicioPropiedad.listarPropiedadesPorPrecio(-1.0, 25000.0);
        });
    }


    @Test
    public void queSeDevuelvanLasPropiedadesFiltradasPorUbicacion() {
        when(this.repositorioPropiedad.listarPorUbicacion("Moron")).thenReturn(propiedadesMock);
        List<Propiedad> propiedadesListadas = this.servicioPropiedad.listarPropiedadesPorUbicacion("Moron");

        assertThat(propiedadesListadas, equalTo(propiedadesMock));
    }


    @Test
    public void queSeLanceUnaExcepcionAlIntentarFiltradasPorUbicacionDeFormaInvalida() {


        assertThrows(CRUDPropiedadExcepcion.class, () -> {
            this.servicioPropiedad.listarPropiedadesPorUbicacion("");
        });
    }


    @Test
    public void queSePuedaAgregarUnaPropiedadValida() throws IOException {

        MultipartFile imageMock = mock(MultipartFile.class);
        Long id = 2L;
        Propiedad propiedad = new Propiedad(id, "Casa", 2, 3, 4, 200.0,
                150000.0, "Ubicacion");

        this.servicioPropiedad.agregarPropiedad(propiedad, imageMock);

        when(this.repositorioPropiedad.buscarPropiedad(id)).thenReturn(propiedad);
        Propiedad propiedadAgregada = this.servicioPropiedad.buscarPropiedad(id);

        assertThat(propiedadAgregada, equalTo(propiedad));
    }


    @Test
    public void queSeLanceUnaExcepcionAlIntentarAgregarUnaPropiedadInvalida() throws IOException {
        String nombreInvalido = "32131312";
        MultipartFile imageMock = mock(MultipartFile.class);

        Propiedad propiedad = new Propiedad(2L, nombreInvalido, 2, 3, 4, 200.0,
                150000.0, "Ubicacion");


        assertThrows(CRUDPropiedadExcepcion.class, () -> {
            this.servicioPropiedad.agregarPropiedad(propiedad, imageMock);
        });
    }
}
