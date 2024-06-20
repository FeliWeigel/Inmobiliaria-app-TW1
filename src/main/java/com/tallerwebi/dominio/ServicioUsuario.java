package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.CredencialesInvalidasExcepcion;
import com.tallerwebi.dominio.excepcion.PasswordInvalidaExcepcion;
import com.tallerwebi.dominio.excepcion.UsuarioExistenteExcepcion;
import com.tallerwebi.dominio.excepcion.UsuarioInexistenteExcepcion;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class ServicioUsuario {

    private final RepositorioUsuario repositorioUsuario;

    public ServicioUsuario (RepositorioUsuario repositorioUsuario) {
        this.repositorioUsuario = repositorioUsuario;
    }

    public Usuario buscarPorId(Long id) throws UsuarioInexistenteExcepcion {
        Usuario usuario = repositorioUsuario.buscarPorId(id);

        if (usuario != null){
            return usuario;
        } else {
            throw new UsuarioInexistenteExcepcion();
        }
    }

    public void agregarFavorito(Usuario usuarioAutenticado, Long propiedadId) {
        repositorioUsuario.agregarFavorito(usuarioAutenticado, propiedadId);
    }

    public void eliminarFavorito(Usuario usuarioAutenticado, Long propiedadId) {
        repositorioUsuario.eliminarFavorito(usuarioAutenticado, propiedadId);
    }

    public Set<Propiedad> listarFavoritos(Usuario usuario) {
        return repositorioUsuario.listarFavoritos(usuario);
    }

    public void bloquearUsuario(Long usuarioId) {
        repositorioUsuario.bloquearUsuario(usuarioId);
    }

    public void eliminarUsuario(Long usuarioId) {
        repositorioUsuario.eliminarUsuario(usuarioId);
    }

    public void desbloquearUsuario(Long usuarioId) {
        repositorioUsuario.desbloquearUsuario(usuarioId);
    }

    public List<Usuario> listarUsuariosBloqueados() {
        return repositorioUsuario.listarUsuariosBloqueados();
    }

    public List<Usuario> listarUsuariosDesbloqueados() {
        return repositorioUsuario.listarUsuariosDesbloqueados();
    }

    public void editarPerfil(Usuario usuario) throws CredencialesInvalidasExcepcion, PasswordInvalidaExcepcion, UsuarioExistenteExcepcion {
        repositorioUsuario.editarPerfil(usuario);
    }
}
