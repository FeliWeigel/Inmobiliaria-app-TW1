package com.tallerwebi.infraestructura;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.mock;

import java.util.List;

import com.tallerwebi.dominio.entidades.CalificacionPropiedad;
import com.tallerwebi.dominio.entidades.Propiedad;
import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.infraestructura.config.HibernateTestInfraestructuraConfig;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {HibernateTestInfraestructuraConfig.class})
public class RepositorioCalificacionTest {

    @Autowired
    private SessionFactory sessionFactory;
    private RepositorioCalificacionImpl repositorioCalificacionPropiedad;

    @BeforeEach
    public void init(){
        this.repositorioCalificacionPropiedad = new RepositorioCalificacionImpl(this.sessionFactory);
    }


    // EJECUTAR INDIVIDUALMENTE LOS TESTS, SINO DAN ERROR.


    @Test
    @Transactional
    @Rollback
    public void queSePuedanListarCalificacionesPorPropiedad() {
        Long propiedadId = 1L;
        Propiedad propiedad = mock(Propiedad.class);
        propiedad.setId(propiedadId);
        this.sessionFactory.getCurrentSession().save(propiedad);

        Usuario usuario = mock(Usuario.class);
        usuario.setId(1L);
        this.sessionFactory.getCurrentSession().save(usuario);

        CalificacionPropiedad calificacion1 = new CalificacionPropiedad();
        calificacion1.setPropiedad(propiedad);
        calificacion1.setUsuario(usuario);

        CalificacionPropiedad calificacion2 = new CalificacionPropiedad();
        calificacion2.setPropiedad(propiedad);
        calificacion2.setUsuario(usuario);

        this.repositorioCalificacionPropiedad.agregarNuevaCalificacion(calificacion1);
        this.repositorioCalificacionPropiedad.agregarNuevaCalificacion(calificacion2);

        List<CalificacionPropiedad> calificaciones = this.repositorioCalificacionPropiedad.listarCalificacionesPorPropiedad(propiedadId);

        assertThat(calificaciones, hasSize(2));
        assertThat(calificaciones.get(0), equalTo(calificacion1));
        assertThat(calificaciones.get(1), equalTo(calificacion2));
    }


    @Test
    @Transactional
    @Rollback
    public void queSePuedanListarCalificacionesPorUsuario() {
        Propiedad propiedad = mock(Propiedad.class);
        propiedad.setId(1L);
        this.sessionFactory.getCurrentSession().save(propiedad);

        Long usuarioId = 1L;
        Usuario usuario = mock(Usuario.class);
        usuario.setId(usuarioId);
        this.sessionFactory.getCurrentSession().save(usuario);

        this.sessionFactory.getCurrentSession().save(usuario);

        CalificacionPropiedad calificacion1 = new CalificacionPropiedad();
        calificacion1.setPropiedad(propiedad);
        calificacion1.setUsuario(usuario);

        CalificacionPropiedad calificacion2 = new CalificacionPropiedad();
        calificacion2.setPropiedad(propiedad);
        calificacion2.setUsuario(usuario);

        this.repositorioCalificacionPropiedad.agregarNuevaCalificacion(calificacion1);
        this.repositorioCalificacionPropiedad.agregarNuevaCalificacion(calificacion2);

        List<CalificacionPropiedad> calificaciones = this.repositorioCalificacionPropiedad.listarCalificacionesPorUsuario(usuarioId);

        assertThat(calificaciones, hasSize(2));
        assertThat(calificaciones.get(0), equalTo(calificacion1));
        assertThat(calificaciones.get(1), equalTo(calificacion2));
    }

    @Test
    @Transactional
    @Rollback
    public void queSePuedaAgregarNuevaCalificacion() {
        Long propiedadId = 1L;
        Long usuarioId = 1L;
        Propiedad propiedad = new Propiedad();
        propiedad.setId(propiedadId);
        propiedad.setNombre("Propiedad de prueba");

        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setEmail("usuario@prueba.com");

        this.sessionFactory.getCurrentSession().save(propiedad);
        this.sessionFactory.getCurrentSession().save(usuario);

        CalificacionPropiedad calificacion = new CalificacionPropiedad();
        calificacion.setPropiedad(propiedad);
        calificacion.setUsuario(usuario);
        calificacion.setPuntaje(5.0);
        calificacion.setDescripcion("Excelente propiedad");

        this.repositorioCalificacionPropiedad.agregarNuevaCalificacion(calificacion);

        List<CalificacionPropiedad> calificaciones = this.repositorioCalificacionPropiedad.listarCalificacionesPorPropiedad(propiedadId);

        assertThat(calificaciones, hasSize(1));
        assertThat(calificaciones.get(0), equalTo(calificacion));
    }
}
