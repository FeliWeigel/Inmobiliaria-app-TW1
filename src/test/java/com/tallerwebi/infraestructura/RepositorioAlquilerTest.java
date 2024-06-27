package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.entidades.AlquilerPropiedad;
import com.tallerwebi.dominio.dto.FechasAlquilerDTO;
import com.tallerwebi.dominio.entidades.Propiedad;
import com.tallerwebi.dominio.entidades.Usuario;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class RepositorioAlquilerTest {

    @Mock
    private SessionFactory sessionFactory;

    @Mock
    private Session session;

    @InjectMocks
    private RepositorioAlquilerImpl repositorioAlquiler;

    private AlquilerPropiedad alquiler;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        when(sessionFactory.getCurrentSession()).thenReturn(session);

        alquiler = new AlquilerPropiedad();
        alquiler.setId(1L);
        alquiler.setPropiedad(new Propiedad(1L, "Casa 1", 3, 2, 4, 200.0, 150000.0, "Ubicacion 1"));
        alquiler.setUsuario(new Usuario(1L, "usuario", "password"));
        alquiler.setFechaInicio(Date.valueOf("2022-01-01"));
        alquiler.setFechaFin(Date.valueOf("2022-01-10"));
    }

    @Test
    @Transactional
    public void queSePuedaAgregarUnNuevoAlquiler() {
        repositorioAlquiler.nuevoAlquiler(alquiler);

        verify(session, times(1)).save(alquiler);
    }

    @Test
    @Transactional
    public void queSePuedanObtenerAlquileresPorPropiedad() {
        List<AlquilerPropiedad> alquileres = new ArrayList<>();
        alquileres.add(alquiler);

        Query<AlquilerPropiedad> queryMock = mock(Query.class);
        when(session.createQuery(anyString(), eq(AlquilerPropiedad.class))).thenReturn(queryMock);
        when(queryMock.setParameter(anyString(), anyLong())).thenReturn(queryMock);
        when(queryMock.getResultList()).thenReturn(alquileres);

        List<AlquilerPropiedad> alquileresObtenidos = repositorioAlquiler.getAlquileresByPropiedad(1L);

        assertThat(alquileresObtenidos.size(), equalTo(1));
    }

    @Test
    @Transactional
    public void queSePuedanObtenerAlquileresPorUsuario() {
        List<AlquilerPropiedad> alquileres = new ArrayList<>();
        alquileres.add(alquiler);

        Query<AlquilerPropiedad> queryMock = mock(Query.class);
        when(session.createQuery(anyString(), eq(AlquilerPropiedad.class))).thenReturn(queryMock);
        when(queryMock.setParameter(anyString(), anyLong())).thenReturn(queryMock);
        when(queryMock.getResultList()).thenReturn(alquileres);

        List<AlquilerPropiedad> alquileresObtenidos = repositorioAlquiler.getAlquileresByUsuario(1L);

        assertThat(alquileresObtenidos.size(), equalTo(1));
    }

    @Test
    @Transactional
    public void queSePuedanObtenerFechasDeAlquilerPorPropiedad() {
        List<AlquilerPropiedad> alquileres = new ArrayList<>();
        alquileres.add(alquiler);

        Query<AlquilerPropiedad> queryMock = mock(Query.class);
        when(session.createQuery(anyString(), eq(AlquilerPropiedad.class))).thenReturn(queryMock);
        when(queryMock.setParameter(anyString(), anyLong())).thenReturn(queryMock);
        when(queryMock.getResultList()).thenReturn(alquileres);

        List<FechasAlquilerDTO> fechasAlquileres = repositorioAlquiler.getFechasByPropiedad(1L);

        assertThat(fechasAlquileres.size(), equalTo(1));
        assertThat(fechasAlquileres.get(0).getFechaInicio(), equalTo(alquiler.getFechaInicio()));
        assertThat(fechasAlquileres.get(0).getFechaFin(), equalTo(alquiler.getFechaFin()));
    }
}
