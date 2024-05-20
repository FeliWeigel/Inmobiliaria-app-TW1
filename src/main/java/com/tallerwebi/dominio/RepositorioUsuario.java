package com.tallerwebi.dominio;

public interface RepositorioUsuario {

    Usuario buscarUsuario(String email, String password);
    void guardar(Usuario usuario);
    Usuario buscarPorEmail(String email);
    void modificar(Usuario usuario);

    void agregarFavorito(Usuario usuario, Propiedad propiedad);
    void eliminarFavorito(Usuario usuario, Propiedad propiedad);
}

