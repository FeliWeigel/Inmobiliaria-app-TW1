package com.tallerwebi.dominio;

import com.tallerwebi.dominio.entidades.CalificacionPropiedad;
import com.tallerwebi.dominio.entidades.Propiedad;
import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.excepcion.CRUDPropiedadExcepcion;
import com.tallerwebi.dominio.excepcion.CalificacionDenegadaExcepcion;
import com.tallerwebi.dominio.excepcion.UsuarioNoIdentificadoExcepcion;
import com.tallerwebi.dominio.servicio.ServicioCalificacion;
import com.tallerwebi.dominio.servicio.ServicioPropiedad;
import com.tallerwebi.infraestructura.RepositorioCalificacionImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ServicioCalificacionTest {

    @Autowired
    private RepositorioCalificacionImpl repositorioCalificacion;

    private ServicioPropiedad servicioPropiedad;
    private ServicioCalificacion servicioCalificacion;

    @BeforeEach
    public void init() {
        this.repositorioCalificacion =  mock(RepositorioCalificacionImpl.class);
        this.servicioPropiedad = mock(ServicioPropiedad.class);

        servicioCalificacion = new ServicioCalificacion(repositorioCalificacion, servicioPropiedad);
    }

    @Test
    public void queAgregueNuevaCalificacionCorrectamente() throws UsuarioNoIdentificadoExcepcion, CalificacionDenegadaExcepcion, CRUDPropiedadExcepcion {
        Long propiedadId = 1L;
        Usuario usuario = new Usuario();
        String descripcion = "Buena propiedad";
        Double puntaje = 4.5;

        Propiedad propiedadMock = new Propiedad();
        when(servicioPropiedad.buscarPropiedad(propiedadId)).thenReturn(propiedadMock);

        servicioCalificacion.agregarNuevaCalificacion(propiedadId, usuario, descripcion, puntaje);


        List<CalificacionPropiedad> calificaciones = new ArrayList<>();
        calificaciones.add(mock(CalificacionPropiedad.class));
        when(repositorioCalificacion.listarCalificacionesPorPropiedad(propiedadId)).thenReturn(calificaciones);

        assertEquals(1, calificaciones.size());
        verify(repositorioCalificacion, times(1)).agregarNuevaCalificacion(any());
    }

    @Test
    public void queLanceExcepcionSiUsuarioNoAutenticado() {
        Long propiedadId = 1L;
        Usuario usuario = null;
        String descripcion = "Buena propiedad";
        Double puntaje = 4.5;

        assertThrows(UsuarioNoIdentificadoExcepcion.class, () -> {
            servicioCalificacion.agregarNuevaCalificacion(propiedadId, usuario, descripcion, puntaje);
        });
    }

    @Test
    public void queLanceExcepcionSiPropiedadNoExiste() {
        Long propiedadId = 1L;
        Usuario usuario = new Usuario();
        String descripcion = "Buena propiedad";
        Double puntaje = 4.5;

        when(servicioPropiedad.buscarPropiedad(propiedadId)).thenReturn(null);

        assertThrows(CRUDPropiedadExcepcion.class, () -> {
            servicioCalificacion.agregarNuevaCalificacion(propiedadId, usuario, descripcion, puntaje);
        });
    }

    @Test
    public void queLanceExcepcionSiDescripcionOuPuntajeNulo() {
        Long propiedadId = 1L;
        Usuario usuario = new Usuario();
        String descripcion = null;
        Double puntaje = null;

        assertThrows(CalificacionDenegadaExcepcion.class, () -> {
            servicioCalificacion.agregarNuevaCalificacion(propiedadId, usuario, descripcion, puntaje);
        });
    }


    @Test
    public void testListarCalificacionesPorPropiedad() throws CRUDPropiedadExcepcion {
        Long propiedadId = 1L;
        Propiedad propiedad = new Propiedad();
        propiedad.setId(propiedadId);
        propiedad.setNombre("Propiedad de prueba");

        when(servicioPropiedad.buscarPropiedad(propiedadId)).thenReturn(propiedad);

        java.util.Date fechaUtil = new java.util.Date(2022 - 1900, 0, 10);
        java.sql.Date fecha = new java.sql.Date(fechaUtil.getTime());

        CalificacionPropiedad calificacion1 = new CalificacionPropiedad();
        calificacion1.setUsuario(new Usuario());
        calificacion1.setPropiedad(propiedad);
        calificacion1.setDescripcion("Calificación 1");
        calificacion1.setPuntaje(4.0);
        calificacion1.setFecha(fecha);

        CalificacionPropiedad calificacion2 = new CalificacionPropiedad();
        calificacion2.setUsuario(new Usuario());
        calificacion2.setPropiedad(propiedad);
        calificacion2.setDescripcion("Calificación 2");
        calificacion2.setPuntaje(3.5);
        calificacion2.setFecha(fecha);

        when(repositorioCalificacion.listarCalificacionesPorPropiedad(propiedadId))
                .thenReturn(Arrays.asList(calificacion1, calificacion2));

        List<CalificacionPropiedad> calificaciones = servicioCalificacion.listarCalificacionesPorPropiedad(propiedadId);

        assertThat(calificaciones, hasSize(2));
        assertThat(calificaciones, containsInAnyOrder(calificacion1, calificacion2));
    }

}

