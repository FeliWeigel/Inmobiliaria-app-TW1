package com.tallerwebi.dominio;

import com.tallerwebi.dominio.dto.FiltroPropiedadDTO;
import com.tallerwebi.dominio.entidades.Propiedad;
import com.tallerwebi.dominio.excepcion.CRUDPropiedadExcepcion;
import com.tallerwebi.dominio.respositorio.RepositorioPropiedad;
import com.tallerwebi.dominio.servicio.ServicioPropiedad;
import com.tallerwebi.dominio.servicio.SubirImagenServicio;
import com.tallerwebi.dominio.utilidad.EstadoPropiedad;
import com.tallerwebi.infraestructura.RepositorioHistorialImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class ServicioPropiedadTest {
    private RepositorioPropiedad repositorioPropiedad;
    private ServicioPropiedad servicioPropiedad;
    private RepositorioHistorialImpl repositorioHistorial;

    List<Propiedad> propiedadesMock;
    List<Propiedad> propiedadesMockAceptadas;
    private SubirImagenServicio imagenServicio;

    @BeforeEach
    public void init() {
        this.repositorioPropiedad = mock(RepositorioPropiedad.class);
        this.imagenServicio= mock(SubirImagenServicio.class);
        this.repositorioHistorial = mock(RepositorioHistorialImpl.class);
        this.servicioPropiedad = new ServicioPropiedad(this.repositorioPropiedad,imagenServicio);

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
    public void queAlBuscarUnaPropiedadInexistenteSeLanceUnaExcepcion(){
        Long id = 1L;
        assertThrows(CRUDPropiedadExcepcion.class, () -> {
            Propiedad propiedadBuscada = this.servicioPropiedad.buscarPropiedad(id);
        });
    }


    @Test
    public void queSeDevuelvanLasPropiedadesListadas() {

        when(this.repositorioPropiedad.listarPropiedades()).thenReturn(propiedadesMock);
        List<Propiedad> propiedadesListadas = this.servicioPropiedad.listarPropiedades();

        assertThat(propiedadesListadas, equalTo(propiedadesMock));
    }


    @Test
    public void queSeDevuelvanLasPropiedadesListadasPendientes() {

        when(this.repositorioPropiedad.listarPropiedadesPendientes()).thenReturn(propiedadesMock);
        List<Propiedad> propiedadesListadas = this.servicioPropiedad.listarPropiedadesPendientes();

        assertThat(propiedadesListadas, equalTo(propiedadesMock));
    }


    @Test
    public void queSeDevuelvanLasPropiedadesListadasAceptadas() {

        when(this.repositorioPropiedad.listarPropiedadesAceptadas()).thenReturn(propiedadesMock);
        List<Propiedad> propiedadesListadas = this.servicioPropiedad.listarPropiedadesAceptadas();

        assertThat(propiedadesListadas, equalTo(propiedadesMock));
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


    @Test
    public void queSePuedaAceptarUnaPropiedadExistente() {
        Long id = 1L;
        Propiedad propiedad = new Propiedad();
        propiedad.setId(id);

        when(repositorioPropiedad.buscarPropiedad(id)).thenReturn(propiedad);
        servicioPropiedad.aceptarPropiedad(id);

        Integer result = 0;
        boolean value = propiedad.isAceptada();
        if (value) {
            result = 1;
        }

        verify(repositorioPropiedad, times(1)).buscarPropiedad(id);
        verify(repositorioPropiedad, times(1)).editarPropiedad(propiedad);
        assertThat(result, equalTo(1));

    }


    @Test
    public void queNoSePuedaAceptarUnaPropiedadInexistente() {
        Long id = 1L;

        when(repositorioPropiedad.buscarPropiedad(id)).thenReturn(null);

        CRUDPropiedadExcepcion exception = assertThrows(CRUDPropiedadExcepcion.class, () -> {
            servicioPropiedad.aceptarPropiedad(id);
        });

        assertThat(exception.getMessage(), containsString("La propiedad con ID " + id + " no existe."));
        verify(repositorioPropiedad, times(1)).buscarPropiedad(id);
        verify(repositorioPropiedad, never()).editarPropiedad(any());
    }


    @Test
    public void queSePuedaRechazarUnaPropiedadExistente() {
        Long id = 1L;

        when(repositorioPropiedad.buscarPropiedad(id)).thenReturn(new Propiedad());

        servicioPropiedad.rechazarPropiedad(id);

        verify(repositorioPropiedad, times(1)).eliminarPropiedad(id);
    }


    @Test
    public void queNoSePuedaRechazarUnaPropiedadInexistente() {
        Long id = 1L;

        doThrow(new CRUDPropiedadExcepcion("La propiedad no existe."))
                .when(repositorioPropiedad).eliminarPropiedad(id);

        CRUDPropiedadExcepcion exception = assertThrows(CRUDPropiedadExcepcion.class, () -> {
            servicioPropiedad.rechazarPropiedad(id);
        });

        assertThat(exception.getMessage(), containsString("La propiedad no existe."));
        verify(repositorioPropiedad, times(1)).eliminarPropiedad(id);
    }


    @Test
    public void testModificarPropiedad() {
        Propiedad propiedad = new Propiedad();
        propiedad.setBanios(2);
        propiedad.setId(1L);

        propiedad.setBanios(4);
        servicioPropiedad.modificarPropiedad(propiedad);

        verify(this.repositorioPropiedad, times(1)).editarPropiedad(propiedad);
        assertThat(propiedad.getBanios(), equalTo(4));
    }

    @Test
    public void queSeDevuelvanLasPropiedadesFiltradasPorPrecio() {
        FiltroPropiedadDTO filtro = new FiltroPropiedadDTO();
        filtro.setMinPrecio(1000.0);
        filtro.setMaxPrecio(25000.0);

        when(this.repositorioPropiedad.listarPropiedades()).thenReturn(propiedadesMock);
        when(this.repositorioPropiedad.listarPorRangoPrecio(1000.0, 25000.0)).thenReturn(propiedadesMock);

        List<Propiedad> propiedadesFiltradas = this.servicioPropiedad.filtrarPropiedades(filtro);

        assertThat(propiedadesFiltradas, equalTo(propiedadesMock));
    }


    @Test
    public void queSeDevuelvanLasPropiedadesFiltradasPorEstado() {
        FiltroPropiedadDTO filtro = new FiltroPropiedadDTO();
        filtro.setEstado(EstadoPropiedad.VENTA);

        when(this.repositorioPropiedad.listarPropiedades()).thenReturn(propiedadesMock);
        when(this.repositorioPropiedad.listarPorEstado(EstadoPropiedad.VENTA)).thenReturn(propiedadesMock);

        List<Propiedad> propiedadesFiltradas = this.servicioPropiedad.filtrarPropiedades(filtro);

        assertThat(propiedadesFiltradas, equalTo(propiedadesMock));
    }


    @Test
    public void queSeDevuelvanLasPropiedadesFiltradasPorSuperficie() {
        FiltroPropiedadDTO filtro = new FiltroPropiedadDTO();
        filtro.setSuperficie(100.0);

        when(this.repositorioPropiedad.listarPropiedades()).thenReturn(propiedadesMock);
        when(this.repositorioPropiedad.listarPorSuperficie(100.0)).thenReturn(propiedadesMock);

        List<Propiedad> propiedadesFiltradas = this.servicioPropiedad.filtrarPropiedades(filtro);

        assertThat(propiedadesFiltradas, equalTo(propiedadesMock));
    }


    @Test
    public void queSeDevuelvanLasPropiedadesFiltradasPorUbicacion() {
        FiltroPropiedadDTO filtro = new FiltroPropiedadDTO();
        filtro.setUbicacion("Moron");

        when(this.repositorioPropiedad.listarPropiedades()).thenReturn(propiedadesMock);
        when(this.repositorioPropiedad.listarPorUbicacion("Moron")).thenReturn(propiedadesMock);

        List<Propiedad> propiedadesFiltradas = this.servicioPropiedad.filtrarPropiedades(filtro);

        assertThat(propiedadesFiltradas, equalTo(propiedadesMock));
    }


    @Test
    public void queSeDevuelvanLasPropiedadesFiltradasPorEstadoYPrecio() {
        FiltroPropiedadDTO filtro = new FiltroPropiedadDTO();
        filtro.setEstado(EstadoPropiedad.VENTA);
        filtro.setMinPrecio(1000.0);
        filtro.setMaxPrecio(25000.0);

        when(this.repositorioPropiedad.listarPropiedades()).thenReturn(propiedadesMock);
        when(this.repositorioPropiedad.listarPorEstado(EstadoPropiedad.VENTA)).thenReturn(propiedadesMock);
        when(this.repositorioPropiedad.listarPorRangoPrecio(1000.0, 25000.0)).thenReturn(propiedadesMock);

        List<Propiedad> propiedadesFiltradas = this.servicioPropiedad.filtrarPropiedades(filtro);

        assertThat(propiedadesFiltradas, equalTo(propiedadesMock));
    }


    @Test
    public void queSeDevuelvanLasPropiedadesFiltradasPorTodasLasCondiciones() {
        FiltroPropiedadDTO filtro = new FiltroPropiedadDTO();
        filtro.setMinPrecio(1000.0);
        filtro.setMaxPrecio(25000.0);
        filtro.setEstado(EstadoPropiedad.VENTA);
        filtro.setSuperficie(100.0);
        filtro.setUbicacion("Moron");

        Propiedad propiedad1 = new Propiedad(1L, "Casa 1", EstadoPropiedad.VENTA, 2,
                2, 1, 120.0, 20000.0, "Moron");

        Propiedad propiedad2 = new Propiedad(2L, "Casa 2", EstadoPropiedad.ALQUILER, 2,
                2, 1, 90.0, 18000.0, "Otra Ubicacion");

        when(this.repositorioPropiedad.listarPropiedades()).thenReturn(Arrays.asList(propiedad1, propiedad2));
        when(this.repositorioPropiedad.listarPorRangoPrecio(1000.0, 25000.0)).thenReturn(Arrays.asList(propiedad1, propiedad2));
        when(this.repositorioPropiedad.listarPorEstado(EstadoPropiedad.VENTA)).thenReturn(Arrays.asList(propiedad1));
        when(this.repositorioPropiedad.listarPorSuperficie(100.0)).thenReturn(Arrays.asList(propiedad1));
        when(this.repositorioPropiedad.listarPorUbicacion("Moron")).thenReturn(Arrays.asList(propiedad1));

        List<Propiedad> propiedadesFiltradas = this.servicioPropiedad.filtrarPropiedades(filtro);

        assertThat(propiedadesFiltradas.size(), equalTo(1));
        assertThat(propiedadesFiltradas.get(0), equalTo(propiedad1));
    }






}
