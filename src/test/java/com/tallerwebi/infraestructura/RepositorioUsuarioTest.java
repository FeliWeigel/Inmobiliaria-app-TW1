package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.Propiedad;
import com.tallerwebi.dominio.RepositorioPropiedad;
import com.tallerwebi.dominio.RepositorioUsuario;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.CRUDPropiedadExcepcion;
import com.tallerwebi.infraestructura.config.HibernateTestInfraestructuraConfig;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {HibernateTestInfraestructuraConfig.class})
public class RepositorioUsuarioTest {

    @Autowired
    private SessionFactory sessionFactory;
    private RepositorioPropiedad repositorioPropiedad;
    private RepositorioUsuarioImpl repositorioUsuarioImpl;
    private Usuario usuario;
    private Session session;

    @BeforeEach
    public void init() {
        this.repositorioPropiedad = mock(RepositorioPropiedad.class);
        this.repositorioUsuarioImpl = new RepositorioUsuarioImpl(this.sessionFactory, this.repositorioPropiedad);

        this.usuario = new Usuario();
        this.usuario.setEmail("test@example.com");
        this.usuario.setPassword("password123");

        this.session = this.sessionFactory.getCurrentSession();
        this.session.save(this.usuario);
    }

    @Test
    @Transactional
    @Rollback
    public void queSePuedaBuscarUnUsuarioExistente() {
        Usuario usuarioEncontrado = this.repositorioUsuarioImpl.buscarUsuario("test@example.com", "password123");

        assertThat(usuarioEncontrado, notNullValue());
        assertThat(usuarioEncontrado.getEmail(), is("test@example.com"));
        assertThat(usuarioEncontrado.getPassword(), is("password123"));
    }

    @Test
    @Transactional
    @Rollback
    public void queSePuedaGuardarUnUsuario() {
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setEmail("newuser@example.com");
        nuevoUsuario.setPassword("newpassword123");

        this.repositorioUsuarioImpl.guardar(nuevoUsuario);

        Usuario usuarioGuardado = (Usuario) this.sessionFactory.getCurrentSession().createCriteria(Usuario.class)
                .add(Restrictions.eq("email", "newuser@example.com"))
                .uniqueResult();

        assertThat(usuarioGuardado, notNullValue());
        assertThat(usuarioGuardado.getEmail(), is("newuser@example.com"));
        assertThat(usuarioGuardado.getPassword(), is("newpassword123"));
    }

    @Test
    @Transactional
    @Rollback
    public void queSePuedaBuscarUsuarioPorEmail() {
        Usuario usuarioEncontrado = this.repositorioUsuarioImpl.buscarPorEmail("test@example.com");

        assertThat(usuarioEncontrado, notNullValue());
        assertThat(usuarioEncontrado.getEmail(), is("test@example.com"));
        assertThat(usuarioEncontrado.getPassword(), is("password123"));
    }

    @Test
    @Transactional
    @Rollback
    public void queSePuedaModificarUnUsuario() {
        this.usuario.setPassword("newpassword456");
        repositorioUsuarioImpl.editarPerfil(this.usuario);

        Usuario usuarioModificado = (Usuario) this.sessionFactory.getCurrentSession().createCriteria(Usuario.class)
                .add(Restrictions.eq("email", "test@example.com"))
                .uniqueResult();

        assertThat(usuarioModificado, notNullValue());
        assertThat(usuarioModificado.getEmail(), is("test@example.com"));
        assertThat(usuarioModificado.getPassword(), is("newpassword456"));
    }

    @Test
    @Transactional
    @Rollback
    public void queSePuedaAgregarUnaPropiedadAFavoritos() {
        Propiedad propiedad = new Propiedad();
        propiedad.setNombre("Propiedad 1");
        session.save(propiedad);

        when(this.repositorioPropiedad.buscarPropiedad(propiedad.getId())).thenReturn(propiedad);
        this.repositorioUsuarioImpl.agregarFavorito(this.usuario, propiedad.getId());

        Usuario usuarioConFavoritos = session.get(Usuario.class, this.usuario.getId());
        assertThat(usuarioConFavoritos.getFavoritos(), hasItem(propiedad));
    }

    @Test
    @Transactional
    @Rollback
    public void queSePuedaEliminarUnaPropiedadDeFavoritos() {
        Propiedad propiedad = new Propiedad();
        propiedad.setNombre("Propiedad 1");
        session.save(propiedad);

        when(this.repositorioPropiedad.buscarPropiedad(propiedad.getId())).thenReturn(propiedad);
        this.repositorioUsuarioImpl.agregarFavorito(this.usuario, propiedad.getId());
        this.repositorioUsuarioImpl.eliminarFavorito(this.usuario, propiedad.getId());

        Usuario usuarioSinFavoritos = session.get(Usuario.class, this.usuario.getId());
        assertThat(usuarioSinFavoritos.getFavoritos(), not(hasItem(propiedad)));
    }

    @Test
    @Transactional
    @Rollback
    public void queSePuedanListarLasPropiedadesDeFavoritos() {
        Propiedad propiedad1 = new Propiedad();
        propiedad1.setNombre("Propiedad 1");
        session.save(propiedad1);

        Propiedad propiedad2 = new Propiedad();
        propiedad2.setNombre("Propiedad 2");
        session.save(propiedad2);

        when(this.repositorioPropiedad.buscarPropiedad(propiedad1.getId())).thenReturn(propiedad1);
        when(this.repositorioPropiedad.buscarPropiedad(propiedad2.getId())).thenReturn(propiedad2);
        this.repositorioUsuarioImpl.agregarFavorito(this.usuario, propiedad1.getId());
        this.repositorioUsuarioImpl.agregarFavorito(this.usuario, propiedad2.getId());

        Set<Propiedad> favoritos = this.repositorioUsuarioImpl.listarFavoritos(this.usuario);

        assertThat(favoritos, hasItems(propiedad1, propiedad2));
    }
}
