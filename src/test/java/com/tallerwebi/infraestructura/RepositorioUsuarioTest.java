package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.entidades.AlquilerPropiedad;
import com.tallerwebi.dominio.entidades.Propiedad;
import com.tallerwebi.dominio.respositorio.RepositorioHistorial;
import com.tallerwebi.dominio.respositorio.RepositorioPropiedad;
import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.excepcion.*;
import com.tallerwebi.infraestructura.config.HibernateTestInfraestructuraConfig;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.fail;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {HibernateTestInfraestructuraConfig.class})
public class RepositorioUsuarioTest {

    @Autowired
    private SessionFactory sessionFactory;
    private RepositorioPropiedad repositorioPropiedad;
    private RepositorioUsuarioImpl repositorioUsuarioImpl;
    private RepositorioHistorialImpl repositorioHistorial;
    private Usuario usuario;
    private Session session;

    @BeforeEach
    public void init() {
        this.repositorioPropiedad = mock(RepositorioPropiedad.class);
        this.repositorioHistorial = mock(RepositorioHistorialImpl.class);
        this.repositorioUsuarioImpl = new RepositorioUsuarioImpl(this.sessionFactory, this.repositorioPropiedad);

        this.usuario = new Usuario();
        this.usuario.setNombre("usuario");
        this.usuario.setApellido("apellido");
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

        Usuario usuarioGuardado = (Usuario) session.createCriteria(Usuario.class)
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
    public void queSePuedaEditarUnUsuario() throws CredencialesInvalidasExcepcion, PasswordInvalidaExcepcion, UsuarioExistenteExcepcion {
        this.usuario.setPassword("newPassword.456");
        this.repositorioUsuarioImpl.editarPerfil(this.usuario);

        Usuario usuarioModificado = (Usuario) this.sessionFactory.getCurrentSession().createCriteria(Usuario.class)
                .add(Restrictions.eq("email", "test@example.com"))
                .uniqueResult();

        assertThat(usuarioModificado, notNullValue());
        assertThat(usuarioModificado.getEmail(), is("test@example.com"));
        assertThat(usuarioModificado.getPassword(), is("newPassword.456"));
    }

    @Transactional
    @Rollback
    @Test
    public void queSePuedaEliminarUnUsuario() throws UsuarioInexistenteExcepcion {
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setEmail("newuser@example.com");
        nuevoUsuario.setPassword("newpassword123");
        nuevoUsuario.setId(3L);
        nuevoUsuario.setAlquileres(new ArrayList<>());
        nuevoUsuario.setVisitas(new ArrayList<>());
        nuevoUsuario.setFavoritos(new HashSet<>());

        this.repositorioUsuarioImpl.guardar(nuevoUsuario);

        Usuario usuarioAEliminar = this.session.get(Usuario.class, nuevoUsuario.getId());
        assertThat(usuarioAEliminar, notNullValue());

        this.repositorioUsuarioImpl.eliminarUsuario(usuarioAEliminar.getId());

        Usuario usuarioEliminado = this.session.get(Usuario.class, nuevoUsuario.getId());
        assertThat(usuarioEliminado, nullValue());
    }


    @Test
    @Transactional
    @Rollback
    public void queLanceCredencialesInvalidasExcepcionCuandoNombreTieneNumeros() {
        Usuario usuarioConNombreInvalido = new Usuario();
        usuarioConNombreInvalido.setId(this.usuario.getId());
        usuarioConNombreInvalido.setEmail("test@example.com");
        usuarioConNombreInvalido.setPassword("validPassword123");
        usuarioConNombreInvalido.setNombre("Nombre123");
        usuarioConNombreInvalido.setApellido("Apellido");

        assertThrows(CredencialesInvalidasExcepcion.class, () -> {
            this.repositorioUsuarioImpl.editarPerfil(usuarioConNombreInvalido);
        });
    }


    @Test
    @Transactional
    @Rollback
    public void queLanceCredencialesInvalidasExcepcionCuandoApellidoTieneNumeros() {
        Usuario usuarioConApellidoInvalido = new Usuario();
        usuarioConApellidoInvalido.setId(this.usuario.getId());
        usuarioConApellidoInvalido.setEmail("test@example.com");
        usuarioConApellidoInvalido.setPassword("validPassword123");
        usuarioConApellidoInvalido.setNombre("Nombre");
        usuarioConApellidoInvalido.setApellido("Apellido123");

        assertThrows(CredencialesInvalidasExcepcion.class, () -> {
            this.repositorioUsuarioImpl.editarPerfil(usuarioConApellidoInvalido);
        });
    }


    @Test
    @Transactional
    @Rollback
    public void queLancePasswordInvalidaExcepcionCuandoPasswordEsCorta() {
        Usuario usuarioConPasswordCorta = new Usuario();
        usuarioConPasswordCorta.setId(this.usuario.getId());
        usuarioConPasswordCorta.setEmail("test@example.com");
        usuarioConPasswordCorta.setPassword("12345");
        usuarioConPasswordCorta.setNombre("Nombre");
        usuarioConPasswordCorta.setApellido("Apellido");

        assertThrows(PasswordInvalidaExcepcion.class, () -> {
            this.repositorioUsuarioImpl.editarPerfil(usuarioConPasswordCorta);
        });
    }


    @Test
    @Transactional
    @Rollback
    public void queLancePasswordInvalidaExcepcionCuandoPasswordEsInvalida() {
        Usuario usuarioConPasswordInvalida = new Usuario();
        usuarioConPasswordInvalida.setId(this.usuario.getId());
        usuarioConPasswordInvalida.setEmail("test@example.com");
        usuarioConPasswordInvalida.setPassword("invalidPass");
        usuarioConPasswordInvalida.setNombre("Nombre");
        usuarioConPasswordInvalida.setApellido("Apellido");

        assertThrows(PasswordInvalidaExcepcion.class, () -> {
            this.repositorioUsuarioImpl.editarPerfil(usuarioConPasswordInvalida);
        });
    }


    @Test
    @Transactional
    @Rollback
    public void queLanceUsuarioExistenteExcepcionCuandoEmailYaExiste() {
        Usuario otroUsuario = new Usuario();
        otroUsuario.setEmail("otro@example.com");
        otroUsuario.setPassword("newPassword.456");
        otroUsuario.setNombre("Otro");
        otroUsuario.setApellido("Usuario");
        sessionFactory.getCurrentSession().save(otroUsuario);

        Usuario usuarioConEmailExistente = new Usuario();
        usuarioConEmailExistente.setId(this.usuario.getId());
        usuarioConEmailExistente.setEmail("otro@example.com");
        usuarioConEmailExistente.setPassword("newPassword.456");
        usuarioConEmailExistente.setNombre("Nombre");
        usuarioConEmailExistente.setApellido("Apellido");

        assertThrows(UsuarioExistenteExcepcion.class, () -> {
            this.repositorioUsuarioImpl.editarPerfil(usuarioConEmailExistente);
        });
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
    public void queLanceCRUDPropiedadExcepcionCuandoPropiedadNoPuedeSerEncontrada() {
        Long propiedadIdInexistente = 999L;

        assertThrows(CRUDPropiedadExcepcion.class, () -> {
            this.repositorioUsuarioImpl.agregarFavorito(this.usuario, propiedadIdInexistente);
        });
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
    public void queLanceCRUDPropiedadExcepcionCuandoPropiedadNoPuedeSerEncontradaParaEliminar() {
        Long propiedadIdInexistente = 999L;

        assertThrows(CRUDPropiedadExcepcion.class, () -> {
            this.repositorioUsuarioImpl.eliminarFavorito(this.usuario, propiedadIdInexistente);
        });
    }


    @Test
    @Transactional
    @Rollback
    public void queLanceCRUDPropiedadExcepcionCuandoPropiedadNoEstaEnFavoritosParaEliminar() {
        Propiedad propiedadNoEnFavoritos = new Propiedad();
        propiedadNoEnFavoritos.setNombre("Propiedad No En Favoritos");
        sessionFactory.getCurrentSession().save(propiedadNoEnFavoritos);

        assertThrows(CRUDPropiedadExcepcion.class, () -> {
            this.repositorioUsuarioImpl.eliminarFavorito(this.usuario, propiedadNoEnFavoritos.getId());
        });
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


    @Test
    @Transactional
    @Rollback
    public void queLanceUsuarioNoIdentificadoExcepcionCuandoUsuarioNoEstaIdentificado() {
        Usuario usuarioNoIdentificado = new Usuario();
        usuarioNoIdentificado.setId(999L);

        assertThrows(UsuarioNoIdentificadoExcepcion.class, () -> {
            this.repositorioUsuarioImpl.cerrarSesion(usuarioNoIdentificado);
        });
    }


    @Test
    @Transactional
    @Rollback
    public void queLanceCRUDPropiedadExcepcionCuandoUsuarioNoPuedeSerEncontrado() {
        Usuario usuarioNoExistente = new Usuario();
        usuarioNoExistente.setId(999L);

        assertThrows(CRUDPropiedadExcepcion.class, () -> {
            this.repositorioUsuarioImpl.listarFavoritos(usuarioNoExistente);
        });
    }


    @Test
    @Transactional
    @Rollback
    public void queListeUsuariosDesbloqueados() {
        Usuario usuario1 = new Usuario();
        usuario1.setActivo(true);
        sessionFactory.getCurrentSession().save(usuario1);

        List<Usuario> usuariosDesbloqueados = repositorioUsuarioImpl.listarUsuariosDesbloqueados();

        assertThat(usuariosDesbloqueados, contains(usuario1));
    }


    @Test
    @Transactional
    @Rollback
    public void queSePuedaBloquearUnUsuarioExistente() {
        Usuario usuario = new Usuario();
        usuario.setActivo(true);
        sessionFactory.getCurrentSession().save(usuario);

        repositorioUsuarioImpl.bloquearUsuario(usuario.getId());

        Usuario usuarioBloqueado = sessionFactory.getCurrentSession().get(Usuario.class, usuario.getId());
        assertThat(usuarioBloqueado.getActivo(), is(false));
    }

    @Test
    @Transactional
    @Rollback
    public void queSePuedaDesbloquearUnUsuarioExistente() {
        Usuario usuario = new Usuario();
        usuario.setActivo(false);
        sessionFactory.getCurrentSession().save(usuario);

        repositorioUsuarioImpl.desbloquearUsuario(usuario.getId());

        Usuario usuarioDesbloqueado = sessionFactory.getCurrentSession().get(Usuario.class, usuario.getId());
        assertThat(usuarioDesbloqueado.getActivo(), is(true));
    }



    @Test
    @Transactional
    @Rollback
    public void queListeUsuariosBloqueados() {
        this.usuario.setActivo(false);

        List<Usuario> usuariosBloqueados = this.repositorioUsuarioImpl.listarUsuariosBloqueados();

        assertThat(usuariosBloqueados.size(), is(1) );
    }


}
