package com.tallerwebi.dominio.respositorio;

import com.tallerwebi.dominio.entidades.Propiedad;
import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.excepcion.CredencialesInvalidasExcepcion;
import com.tallerwebi.dominio.excepcion.PasswordInvalidaExcepcion;
import com.tallerwebi.dominio.excepcion.UsuarioExistenteExcepcion;
import com.tallerwebi.dominio.excepcion.UsuarioNoIdentificadoExcepcion;

import java.util.List;
import java.util.Set;

public interface RepositorioUsuario {

    Usuario buscarUsuario(String email, String password);
    void guardar(Usuario usuario);
    void eliminarUsuario(Long id);

    void eliminarVisitasPorUsuarioId(Long usuarioId);

    void eliminarCalificacionesPorUsuarioId(Long usuarioId);

    Usuario buscarPorEmail(String email);
    Usuario buscarPorId(Long id);
    void agregarFavorito(Usuario usuario, Long propiedadId);
    void eliminarFavorito(Usuario usuario, Long propiedadId);
    Set<Propiedad> listarFavoritos(Usuario usuario);
    void cerrarSesion(Usuario usuario) throws UsuarioNoIdentificadoExcepcion ;
    void editarPerfil(Usuario usuario) throws CredencialesInvalidasExcepcion, PasswordInvalidaExcepcion, UsuarioExistenteExcepcion;
    void bloquearUsuario(Long id);
    void desbloquearUsuario(Long id);
    List<Usuario> listarUsuariosDesbloqueados();
    List<Usuario> listarUsuariosBloqueados();

}

