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
    public void queSePuedanBuscarPropiedadesExistentes(){
        Long id = 1L;
        Propiedad propiedad = new Propiedad(id, "Casa 1", 2, 3, 4, 200.0,
                150000.0, "Ubicacion 1");

        this.sessionFactory.getCurrentSession().save(propiedad);

        Propiedad propiedadBuscada = this.repositorioPropiedad.buscarPropiedad(propiedad.getId());

        assertThat(propiedad, equalTo(propiedadBuscada));
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


    @Test
    @Transactional
    @Rollback
    public void queLanceExcepcionCuandoLaPropiedadAEditarNoExiste() {
        Long idInexistente = 999L;
        Propiedad propiedadEditada = new Propiedad(idInexistente, "Casa 2", 2, 3, 4, 200.0, 1000.0, "Ubicacion 2");

        assertThrows(CRUDPropiedadExcepcion.class, () -> {
            this.repositorioPropiedad.editarPropiedad(propiedadEditada);
        });
    }


    @Test
    @Transactional
    @Rollback
    public void queLanceExcepcionCuandoLaPropiedadAEditarNoTieneID() {
        Propiedad propiedadSinID = new Propiedad(null, "Casa 3", 2, 3, 4, 200.0, 1000.0, "Ubicacion 3");

        assertThrows(CRUDPropiedadExcepcion.class, () -> {
            this.repositorioPropiedad.editarPropiedad(propiedadSinID);
        });
    }


    @Test
    @Transactional
    @Rollback
    public void queSePuedanListarLasPropiedadesPorRangoPrecio(){

        Integer numeroDePropiedadesQueCumplenElFiltro = this.sessionFactory.getCurrentSession()
                .createQuery("FROM Propiedad WHERE precio BETWEEN 1000 AND 25000").getResultList().size();

        List <Propiedad> propiedadesBuscadas = this.repositorioPropiedad.listarPorRangoPrecio(1000.0, 25000.0);

        assertThat(propiedadesBuscadas.size(), equalTo(numeroDePropiedadesQueCumplenElFiltro));
    }

    @Test
    @Transactional
    @Rollback
    public void queSePuedanListarLasPropiedadesPorUbicacion(){

        Integer numeroDePropiedadesQueCumplenElFiltro = this.sessionFactory.getCurrentSession()
                .createQuery("FROM Propiedad WHERE LOCATE(LOWER('Moron'), LOWER(ubicacion)) > 0").getResultList().size();

        List <Propiedad> propiedadesBuscadas = this.repositorioPropiedad.listarPorUbicacion("Moron");

        assertThat(propiedadesBuscadas.size(), equalTo(numeroDePropiedadesQueCumplenElFiltro));
    }


    @Test
    @Transactional
    @Rollback
    public void queSePuedaEliminarUnaPropiedadExistente(){
        Propiedad propiedad = new Propiedad();

        this.sessionFactory.getCurrentSession().save(propiedad);
        this.repositorioPropiedad.eliminarPropiedad(propiedad.getId());

        assertThat(null, equalTo(this.repositorioPropiedad.buscarPropiedad(propiedad.getId())));
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

        Integer numeroDePropiedadesAlmacenadas = contarPropiedadesEnLaBaseDeDatos();

        List <Propiedad> propiedadesBuscadas = this.repositorioPropiedad.listarPropiedades();

        assertThat(propiedadesBuscadas.size(), equalTo(numeroDePropiedadesAlmacenadas));
    }


    private Integer contarPropiedadesEnLaBaseDeDatos(){

        return this.sessionFactory.getCurrentSession().createQuery("FROM Propiedad").getResultList().size();
    };


}
