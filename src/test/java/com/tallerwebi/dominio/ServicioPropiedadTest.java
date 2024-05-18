package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.CRUDPropiedadExcepcion;
import com.tallerwebi.dominio.filtro.FiltroPorPrecio;
import com.tallerwebi.dominio.filtro.FiltroPropiedad;
import com.tallerwebi.presentacion.DatosFiltro;
import com.tallerwebi.presentacion.FiltrarPorPrecio;
import com.tallerwebi.presentacion.TipoDeFiltro;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    private FiltroPropiedad filtro;

    @BeforeEach
    public void init() {
        this.repositorioPropiedad = mock(RepositorioPropiedad.class);
        this.servicioPropiedad = new ServicioPropiedad(this.repositorioPropiedad);
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
    public void queSeDevuelvanLasPropiedadesFiltradasPorPrecioMinimo() {
        filtro = new FiltroPorPrecio();
        DatosFiltro datosFiltro = new DatosFiltro(TipoDeFiltro.PRECIO);
        datosFiltro.setPrecio(100000.0);
        datosFiltro.setFiltroPorPrecio(FiltrarPorPrecio.MINIMO);
        when(this.repositorioPropiedad.listarPropiedades()).thenReturn(propiedadesMock);

        List<Propiedad> propFiltradas = servicioPropiedad.filtrar(filtro, datosFiltro);
        Integer propiedadesEsperadas = 2;

        assertThat(propFiltradas.size(), equalTo(propiedadesEsperadas));
    }


    @Test
    public void queSeDevuelvanLasPropiedadesFiltradasPorPrecioMaximo() {
        filtro = new FiltroPorPrecio();
        DatosFiltro datosFiltro = new DatosFiltro(TipoDeFiltro.PRECIO);
        datosFiltro.setPrecio(100000.0);
        datosFiltro.setFiltroPorPrecio(FiltrarPorPrecio.MAXIMO);
        when(this.repositorioPropiedad.listarPropiedades()).thenReturn(propiedadesMock);

        List<Propiedad> propFiltradas = servicioPropiedad.filtrar(filtro, datosFiltro);
        Integer propiedadesEsperadas = 1;

        assertThat(propFiltradas.size(), equalTo(propiedadesEsperadas));
    }
}
