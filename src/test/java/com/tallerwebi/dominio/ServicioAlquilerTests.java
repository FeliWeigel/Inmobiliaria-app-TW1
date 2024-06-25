package com.tallerwebi.dominio;

import com.tallerwebi.dominio.entidades.AlquilerPropiedad;
import com.tallerwebi.dominio.dto.FechasAlquilerDTO;
import com.tallerwebi.dominio.entidades.Propiedad;
import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.respositorio.RepositorioAlquiler;
import com.tallerwebi.dominio.excepcion.AlquilerDenegadoExcepcion;
import com.tallerwebi.dominio.excepcion.CRUDPropiedadExcepcion;
import com.tallerwebi.dominio.excepcion.UsuarioNoIdentificadoExcepcion;
import com.tallerwebi.dominio.servicio.ServicioAlquiler;
import com.tallerwebi.dominio.servicio.ServicioPropiedad;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class ServicioAlquilerTests {

    private RepositorioAlquiler repositorioAlquiler;
    private ServicioPropiedad servicioPropiedad;
    private ServicioAlquiler servicioAlquiler;

    private Usuario usuario;
    private Propiedad propiedad;
    private java.sql.Date fechaInicio;
    private java.sql.Date fechaFin;

    @BeforeEach
    public void init() throws Exception {
        repositorioAlquiler = mock(RepositorioAlquiler.class);
        servicioPropiedad = mock(ServicioPropiedad.class);
        servicioAlquiler = new ServicioAlquiler(repositorioAlquiler, servicioPropiedad);

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setAlquileres(new ArrayList<>());

        propiedad = new Propiedad();
        propiedad.setId(1L);
        propiedad.setAlquileres(new ArrayList<>());

        java.util.Date utilDateInicio = new java.util.Date(2022 - 1900, 0, 1);
        java.util.Date utilDateFin = new java.util.Date(2022 - 1900, 0, 10);
        fechaInicio = new java.sql.Date(utilDateInicio.getTime());
        fechaFin = new java.sql.Date(utilDateFin.getTime());
    }

    @Test
    public void queSeAgregueNuevoAlquiler() throws Exception {
        when(servicioPropiedad.buscarPropiedad(anyLong())).thenReturn(propiedad);
        when(repositorioAlquiler.getAlquileresByPropiedad(anyLong())).thenReturn(new ArrayList<>());

        servicioAlquiler.agregarNuevoAlquiler(usuario, 1L, fechaInicio, fechaFin);

        verify(repositorioAlquiler, times(1)).nuevoAlquiler(any(AlquilerPropiedad.class));
        assertThat(usuario.getAlquileres().size(), equalTo(1));
        assertThat(propiedad.getAlquileres().size(), equalTo(1));
    }

    @Test
    public void queSeLanceExcepcionCuandoUsuarioNoIdentificado() {
        assertThrows(UsuarioNoIdentificadoExcepcion.class, () -> {
            servicioAlquiler.agregarNuevoAlquiler(null, 1L, fechaInicio, fechaFin);
        });
    }

    @Test
    public void queSeLanceExcepcionCuandoPropiedadIdEsNulo() {
        assertThrows(CRUDPropiedadExcepcion.class, () -> {
            servicioAlquiler.agregarNuevoAlquiler(usuario, null, fechaInicio, fechaFin);
        });
    }

    @Test
    public void queSeLanceExcepcionCuandoFechaInicioPosteriorFechaFin() throws Exception {
        java.util.Date fechaInvalida = new java.util.Date(2022 - 1900, 13, 10);
        java.sql.Date fechaInicioInvalida = new java.sql.Date(fechaInvalida.getTime());

        assertThrows(AlquilerDenegadoExcepcion.class, () -> {
            servicioAlquiler.agregarNuevoAlquiler(usuario, 1L, fechaInicioInvalida, fechaFin);
        });
    }

    @Test
    public void queSeLanceExcepcionCuandoPropiedadNoEncontrada() {
        when(servicioPropiedad.buscarPropiedad(anyLong())).thenReturn(null);

        assertThrows(CRUDPropiedadExcepcion.class, () -> {
            servicioAlquiler.agregarNuevoAlquiler(usuario, 1L, fechaInicio, fechaFin);
        });
    }

    @Test
    public void queSeLanceExcepcionCuandoFechasReservadas() {
        List<AlquilerPropiedad> alquileresExistentes = new ArrayList<>();
        AlquilerPropiedad alquilerExistente = new AlquilerPropiedad();
        alquilerExistente.setFechaInicio(fechaInicio);
        alquilerExistente.setFechaFin(fechaFin);
        alquileresExistentes.add(alquilerExistente);

        when(servicioPropiedad.buscarPropiedad(anyLong())).thenReturn(propiedad);
        when(repositorioAlquiler.getAlquileresByPropiedad(anyLong())).thenReturn(alquileresExistentes);

        assertThrows(AlquilerDenegadoExcepcion.class, () -> {
            servicioAlquiler.agregarNuevoAlquiler(usuario, 1L, fechaInicio, fechaFin);
        });
    }

    @Test
    public void queSeListanAlquileresPorPropiedad() {
        List<AlquilerPropiedad> alquileres = new ArrayList<>();
        when(repositorioAlquiler.getAlquileresByPropiedad(anyLong())).thenReturn(alquileres);

        List<AlquilerPropiedad> result = servicioAlquiler.listarAlquileresPorPropiedad(1L);
        assertThat(result, equalTo(alquileres));
    }

    @Test
    public void queSeDevuelvenFechasReservadasPorPropiedad() {
        List<FechasAlquilerDTO> fechas = new ArrayList<>();
        when(repositorioAlquiler.getFechasByPropiedad(anyLong())).thenReturn(fechas);

        List<FechasAlquilerDTO> result = servicioAlquiler.fechasReservadasPorPropiedad(1L);
        assertThat(result, equalTo(fechas));
    }

    @Test
    public void queSeLanceExcepcionCuandoPropiedadIdEsNuloParaFechas() {
        assertThrows(CRUDPropiedadExcepcion.class, () -> {
            servicioAlquiler.fechasReservadasPorPropiedad(null);
        });
    }
}

