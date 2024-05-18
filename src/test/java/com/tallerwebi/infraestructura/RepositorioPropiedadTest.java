package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Propiedad;
import com.tallerwebi.dominio.RepositorioPropiedad;
import com.tallerwebi.dominio.excepcion.CRUDPropiedadExcepcion;
import com.tallerwebi.infraestructura.config.HibernateTestInfraestructuraConfig;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;

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

        Propiedad propiedadGuardada = this.repositorioPropiedad.buscarPropiedad(propiedad.getId());

        assertThat(propiedadGuardada, equalTo(propiedad));
    }

    /*
        Este test mas bien deberia correrse en la capa de servicio que es la que va a manejar este tipo de logica de negocio,
       el repositorio unicamente va a comunicarse con la base de datos para ejecutar las consultas necesarias

    public void queSeLanceUnaExcepcionAlIntentarAgregarUnaPropiedadInvalida(){

        Propiedad propiedad = new Propiedad(2L, "Casa 1", null, 3, 4, 200.0,
                150000.0, "Ubicacion 1");

        assertThrows(CRUDPropiedadExcepcion.class, () -> {
            this.repositorioPropiedad.agregarPropiedad(propiedad);
        });

    }

     */

    @Test
    @Transactional
    @Rollback
    public void queSePuedanBuscarPropiedadesExistentes(){
        Long id = 1L;
        Propiedad propiedad = new Propiedad(id, "Casa 1", 2, 3, 4, 200.0,
                150000.0, "Ubicacion 1");

        this.repositorioPropiedad.agregarPropiedad(propiedad);
        Propiedad propiedadBuscada = this.repositorioPropiedad.buscarPropiedad(propiedad.getId());

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
        this.repositorioPropiedad.eliminarPropiedad(propiedad.getId());

        assertThrows(CRUDPropiedadExcepcion.class, () -> this.repositorioPropiedad.buscarPropiedad(propiedad.getId()));

    }


    @Test
    @Transactional
    @Rollback
    public void queSeLanceUnaExcepcionAlIntentarEliminarUnaPropiedadInexistente(){
        Long id = 1L;

        assertThrows(CRUDPropiedadExcepcion.class, () -> {
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
        this.repositorioPropiedad.agregarPropiedad(propiedad);

        Double precioEditado = 1000.0;
        Propiedad propiedadEditada = new Propiedad(propiedad.getId(), "Casa 1", 2, 3, 4, 200.0,
                precioEditado, "Ubicacion 1");

        this.repositorioPropiedad.editarPropiedad(propiedadEditada);
        Propiedad propiedadGuardada = this.repositorioPropiedad.buscarPropiedad(propiedad.getId());
        assertThat(
                propiedadGuardada.getPrecio(),
                equalTo(precioEditado)
        );
    }

    private void guardarPropiedades(){

        Propiedad propiedad = new Propiedad(1L, "Casa 1", 1, 3, 4, 200.0,
                150000.0, "Ubicacion 1");
        Propiedad propiedad2 = new Propiedad(2L, "Casa 2", 2, 3, 4, 200.0,
                150000.0, "Ubicacion 2");
        Propiedad propiedad3 = new Propiedad(3L, "Casa 3", 2, 3, 4, 200.0,
                150000.0, "Ubicacion 3");

        this.repositorioPropiedad.agregarPropiedad(propiedad);
        this.repositorioPropiedad.agregarPropiedad(propiedad2);
        this.repositorioPropiedad.agregarPropiedad(propiedad3);
    };


}
