package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.CredencialesInvalidasExcepcion;
import com.tallerwebi.dominio.excepcion.EdadInvalidaExcepcion;
import com.tallerwebi.dominio.excepcion.PasswordInvalidaExcepcion;
import com.tallerwebi.dominio.excepcion.UsuarioExistenteExcepcion;

public interface ServicioLogin {

    Usuario consultarUsuario(String email, String password);
    void registrar(Usuario usuario) throws UsuarioExistenteExcepcion, CredencialesInvalidasExcepcion, PasswordInvalidaExcepcion, EdadInvalidaExcepcion;

    void editarPerfil(Usuario usuario) throws PasswordInvalidaExcepcion, EdadInvalidaExcepcion, CredencialesInvalidasExcepcion;
}
