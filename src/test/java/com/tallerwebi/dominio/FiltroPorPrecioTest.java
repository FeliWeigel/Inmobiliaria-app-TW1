package com.tallerwebi.dominio;

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

public class FiltroPorPrecioTest {
    List<Propiedad> propiedades;
    DatosFiltro datosFiltro;

    @BeforeEach
    public void init() {
        propiedades = new ArrayList<>();
        propiedades.add(new Propiedad(1L, "Casa 1", 2, 3, 4, 200.0, 150000.0, "Ubicacion 1"));
        propiedades.add(new Propiedad(2L, "Casa 2", 3, 2, 5, 250.0, 180000.0, "Ubicacion 2"));
        propiedades.add(new Propiedad(3L, "Casa 3", 1, 1, 2, 120.0, 90000.0, "Ubicacion 3"));
        datosFiltro = new DatosFiltro();
    }

    @Test
    public void queSeDevuelvanLasPropiedadesFiltradasPorPrecioMinimo() {
        datosFiltro.setTipoDeFiltro(TipoDeFiltro.PRECIO);
        datosFiltro.setPrecio(100000.0);
        datosFiltro.setFiltrarPorPrecio(FiltrarPorPrecio.MINIMO);

        FiltroPropiedad filtroPorPrecio = new FiltroPorPrecio();
        List<Propiedad> propFiltradas = filtroPorPrecio.filtrar(propiedades, datosFiltro);

        Integer propiedadesEsperadas = 2;
        assertThat(propFiltradas.size(), equalTo(propiedadesEsperadas));
    }

    @Test
    public void queSeDevuelvanLasPropiedadesFiltradasPorPrecioMaximo() {
        datosFiltro.setTipoDeFiltro(TipoDeFiltro.PRECIO);
        datosFiltro.setPrecio(100000.0);
        datosFiltro.setFiltrarPorPrecio(FiltrarPorPrecio.MAXIMO);

        FiltroPropiedad filtroPorPrecio = new FiltroPorPrecio();
        List<Propiedad> propFiltradas = filtroPorPrecio.filtrar(propiedades, datosFiltro);

        Integer propiedadesEsperadas = 1;
        assertThat(propFiltradas.size(), equalTo(propiedadesEsperadas));
    }

}
