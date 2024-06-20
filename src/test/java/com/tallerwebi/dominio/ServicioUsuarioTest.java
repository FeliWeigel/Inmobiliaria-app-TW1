package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class ServicioUsuarioTest {

    private ServicioUsuario servicioUsuario;
    private RepositorioUsuario repositorioUsuario;
    Long idUsuario;
    Usuario usuario;


    @BeforeEach
    public void init() {
        this.repositorioUsuario = mock(RepositorioUsuario.class);
        this.servicioUsuario = new ServicioUsuario(repositorioUsuario);

        this.idUsuario = 1L;
        this.usuario = mock(Usuario.class);
        usuario.setId(idUsuario);
    }

    @Test
    public void queAlBuscarPorIdDevuelvaElUsuarioEsperado() throws UsuarioInexistenteExcepcion {
        when(this.repositorioUsuario.buscarPorId(idUsuario)).thenReturn(usuario);
        Usuario usuarioObtenido = this.servicioUsuario.buscarPorId(idUsuario);

        assertThat(usuarioObtenido, equalTo(usuario));
    }

    @Test
    public void queLanceExcepcionSiSeBuscaUsuarioInexistente() throws UsuarioInexistenteExcepcion {
        Long idInexistente = 2L;
        assertThrows(UsuarioInexistenteExcepcion.class, () -> {
            Usuario usuarioObtenido = this.servicioUsuario.buscarPorId(idInexistente);
        });
    }

    @Test
    public void quePuedaAgregarFavorito(){
        Long idPropiedad = 2L;
        Propiedad propiedad = mock(Propiedad.class);
        propiedad.setId(idPropiedad);

        servicioUsuario.agregarFavorito(usuario, idPropiedad);
        verify(repositorioUsuario).agregarFavorito(usuario, idPropiedad);
    }

    @Test
    public void quePuedaEliminarFavorito(){
        Long idPropiedad = 2L;
        Propiedad propiedad = mock(Propiedad.class);
        propiedad.setId(idPropiedad);

        servicioUsuario.eliminarFavorito(usuario, idPropiedad);
        verify(repositorioUsuario).eliminarFavorito(usuario, idPropiedad);
    }

    @Test
    public void quePuedaListarFavorito(){
        Long idPropiedad1 = 1L;
        Propiedad propiedad1 = mock(Propiedad.class);
        propiedad1.setId(idPropiedad1);
        Long idPropiedad2 = 2L;
        Propiedad propiedad2 = mock(Propiedad.class);
        propiedad2.setId(idPropiedad2);

        Set<Propiedad> favoritas = new HashSet<Propiedad>();
        favoritas.add(propiedad1);
        favoritas.add(propiedad2);

        when(usuario.getFavoritos()).thenReturn(favoritas);
        servicioUsuario.agregarFavorito(usuario, idPropiedad1);
        servicioUsuario.agregarFavorito(usuario, idPropiedad2);

        assertThat(usuario.getFavoritos(), hasItems(propiedad1, propiedad2));
        assertThat(usuario.getFavoritos().size(), equalTo(favoritas.size()));
    }

    @Test
    public void queSePuedaEliminarUnUsuario(){
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setEmail("newuser@example.com");
        nuevoUsuario.setPassword("newpassword123");
        nuevoUsuario.setId(3L);

        when(repositorioUsuario.buscarUsuario("newuser@example.com", "newpassword123"))
                .thenReturn(nuevoUsuario)
                .thenReturn(null);

        repositorioUsuario.guardar(nuevoUsuario);
        Usuario encontrado = repositorioUsuario.buscarUsuario("newuser@example.com", "newpassword123");
        assertThat(encontrado, is(notNullValue()));

        repositorioUsuario.eliminarUsuario(nuevoUsuario.getId());
        Usuario eliminado = repositorioUsuario.buscarUsuario("newuser@example.com", "newpassword123");
        assertThat(eliminado, is(nullValue()));
    }

    @Test
    public void queSePuedaBloquearUnUsuario(){
        repositorioUsuario.bloquearUsuario(idUsuario);
        verify(repositorioUsuario).bloquearUsuario(idUsuario);
    }

    @Test
    public void queSePuedaDesbloquearUnUsuario(){
        repositorioUsuario.desbloquearUsuario(idUsuario);
        verify(repositorioUsuario).desbloquearUsuario(idUsuario);
    }

    @Test
    public void queSeListenLosUsuariosBloqueados(){
        List<Usuario> bloqueados = new ArrayList<Usuario>();
        bloqueados.add(usuario);

        when(repositorioUsuario.listarUsuariosBloqueados()).thenReturn(bloqueados);
        servicioUsuario.listarUsuariosBloqueados();

        verify(repositorioUsuario).listarUsuariosBloqueados();
        assertThat(bloqueados.size(), equalTo(1));
    }

    @Test
    public void queSeListenLosUsuariosDesloqueados(){
        List<Usuario> desbloqueados = new ArrayList<Usuario>();
        desbloqueados.add(usuario);

        when(repositorioUsuario.listarUsuariosBloqueados()).thenReturn(desbloqueados);
        servicioUsuario.listarUsuariosBloqueados();

        verify(repositorioUsuario).listarUsuariosBloqueados();
        assertThat(desbloqueados.size(), equalTo(1));
    }

    @Test
    public void queSePuedaModificarUnPerfil() throws CredencialesInvalidasExcepcion, PasswordInvalidaExcepcion, UsuarioExistenteExcepcion {
        usuario.setNombre("NombreNuevo");
        usuario.setApellido("ApellidoNuevo");

        when(usuario.getNombre()).thenReturn("NombreNuevo");
        when(usuario.getApellido()).thenReturn("ApellidoNuevo");
        servicioUsuario.editarPerfil(usuario);

        verify(repositorioUsuario).editarPerfil(usuario);
        assertThat(usuario.getNombre(), equalTo("NombreNuevo"));
        assertThat(usuario.getApellido(), equalTo("ApellidoNuevo"));
    }

    @Test
    public void perfilEditadoConNombreInvalidoLanzaExcepcion() throws CredencialesInvalidasExcepcion, UsuarioExistenteExcepcion, PasswordInvalidaExcepcion {
        doThrow(CredencialesInvalidasExcepcion.class).when(repositorioUsuario).editarPerfil(usuario);

        assertThrows(CredencialesInvalidasExcepcion.class, () -> {
            servicioUsuario.editarPerfil(usuario);
        });
    }

    @Test
    public void perfilEditadoConEmailExistenteLanzaExcepcion() throws CredencialesInvalidasExcepcion, UsuarioExistenteExcepcion, PasswordInvalidaExcepcion {
        doThrow(UsuarioExistenteExcepcion.class).when(repositorioUsuario).editarPerfil(usuario);

        assertThrows(UsuarioExistenteExcepcion.class, () -> {
            servicioUsuario.editarPerfil(usuario);
        });
    }

    @Test
    public void perfilEditadoConContraseniaInvalidaLanzaExcepcion() throws CredencialesInvalidasExcepcion, UsuarioExistenteExcepcion, PasswordInvalidaExcepcion {
        doThrow(PasswordInvalidaExcepcion.class).when(repositorioUsuario).editarPerfil(usuario);

        assertThrows(PasswordInvalidaExcepcion.class, () -> {
            servicioUsuario.editarPerfil(usuario);
        });
    }

}
