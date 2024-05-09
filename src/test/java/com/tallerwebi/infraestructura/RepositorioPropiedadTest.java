package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Propiedad;
import com.tallerwebi.dominio.RepositorioPropiedad;
import com.tallerwebi.dominio.excepcion.CRUDPropiedadExcepcion;
import com.tallerwebi.infraestructura.config.HibernateTestInfraestructuraConfig;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.NoResultException;
import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {HibernateTestInfraestructuraConfig.class})
public class RepositorioPropiedadTest {

    @Autowired
    private SessionFactory sessionFactory;
    private RepositorioPropiedad repositorioPropiedad;

    @BeforeEach
    public void init(){
        this.repositorioPropiedad = new RepositorioPropiedadImpl(this.sessionFactory);
    }


    @Test
    @Transactional
    @Rollback
    public void queSePuedaAgregarUnaPropiedadValida(){

        Propiedad propiedad = new Propiedad(1L, "Casa 1", 2, 3, 4, 200.0,
                150000.0, "Ubicacion 1");

        this.repositorioPropiedad.agregarPropiedad(propiedad);

        Propiedad propiedadGuardada = (Propiedad) this.sessionFactory.getCurrentSession()
                .createQuery("FROM Propiedad where id = 1")
                .getSingleResult();

        assertThat(propiedadGuardada, equalTo(propiedad));
    }


    @Test
    @Transactional
    @Rollback
    public void queSeLanceUnaExcepcionAlIntentarAgregarUnaPropiedadInvalida(){

        Propiedad propiedad = new Propiedad(2L, "Casa 1", null, 3, 4, 200.0,
                150000.0, "Ubicacion 1");

        assertThrows(CRUDPropiedadExcepcion.class, () -> {
            this.repositorioPropiedad.agregarPropiedad(propiedad);
        });

    }


    @Test
    @Transactional
    @Rollback
    public void queSePuedanBuscarPropiedadesExistentes(){

        Long id = 1L;
        Propiedad propiedad = new Propiedad(id, "Casa 1", 2, 3, 4, 200.0,
                150000.0, "Ubicacion 1");

        this.sessionFactory.getCurrentSession().save(propiedad);
        Propiedad propiedadBuscada = this.repositorioPropiedad.buscarPropiedad(id);

        assertThat(propiedad, equalTo(propiedadBuscada));
    }


    @Test
    @Transactional
    @Rollback
    public void queAlBuscarUnaPropiedadInexistenteLanceUnaExcepcion(){

        Long id = 1L;

        assertThrows(CRUDPropiedadExcepcion.class, () -> {
            Propiedad propiedadBuscada = this.repositorioPropiedad.buscarPropiedad(id);
        });
    }


    @Test
    @Transactional
    @Rollback
    public void queSePuedaEliminarUnaPropiedadExistente(){

        Long id = 1L;
        Propiedad propiedad = new Propiedad(id, "Casa 1", null, 3, 4, 200.0,
                150000.0, "Ubicacion 1");

        this.sessionFactory.getCurrentSession().save(propiedad);
        this.repositorioPropiedad.eliminarPropiedad(id);


        assertThrows(NoResultException.class, () -> {
                this.sessionFactory.getCurrentSession()
                    .createQuery("FROM Propiedad where id = 1")
                    .getSingleResult();
        });
    }


    @Test
    @Transactional
    @Rollback
    public void queSeLanceUnaExcepcionAlIntentarEliminarUnaPropiedadInexistente(){

        Long id = 1L;


        assertThrows(CRUDPropiedadExcepcion.class, ()-> {
            this.repositorioPropiedad.eliminarPropiedad(id);
        });
    }

    @Test
    @Transactional
    @Rollback
    public void queSePuedanListarLasPropiedadesExistentes(){

        guardarPropiedades();

        List <Propiedad> propiedadesBuscadas = this.repositorioPropiedad.listarPropiedades();

        assertThat(propiedadesBuscadas.size(), equalTo(3));
    }


    @Test
    @Transactional
    @Rollback
    public void queSePuedaEditarUnaPropiedadExistente(){

        Long id = 1L;
        Propiedad propiedad = new Propiedad(id, "Casa 1", 2, 3, 4, 200.0,
                150000.0, "Ubicacion 1");
        this.sessionFactory.getCurrentSession().save(propiedad);

        Double precioEditado = 1000.0;
        Propiedad edicion = new Propiedad(id, "Casa 1", 2, 3, 4, 200.0,
                precioEditado, "Ubicacion 1");
        this.repositorioPropiedad.editarPropiedad(edicion);

        Propiedad propiedadEditada = (Propiedad) this.sessionFactory.getCurrentSession()
                .createQuery("FROM Propiedad where id = 1")
                .getSingleResult();


        assertThat(propiedadEditada.getPrecio(), equalTo(precioEditado));
    }


    private void guardarPropiedades(){

        Propiedad propiedad = new Propiedad(1L, "Casa 1", 1, 3, 4, 200.0,
                150000.0, "Ubicacion 1");
        Propiedad propiedad2 = new Propiedad(2L, "Casa 2", 2, 3, 4, 200.0,
                150000.0, "Ubicacion 2");
        Propiedad propiedad3 = new Propiedad(3L, "Casa 3", 2, 3, 4, 200.0,
                150000.0, "Ubicacion 3");

        this.sessionFactory.getCurrentSession().save(propiedad);
        this.sessionFactory.getCurrentSession().save(propiedad2);
        this.sessionFactory.getCurrentSession().save(propiedad3);
    };


}
