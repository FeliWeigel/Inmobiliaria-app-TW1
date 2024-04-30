package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.UsuarioExistenteExcepcion;

public interface RepositorioLogin {

    Usuario consultarUsuario(String email, String password);
    void registrar(Usuario usuario) throws UsuarioExistenteExcepcion;

}
