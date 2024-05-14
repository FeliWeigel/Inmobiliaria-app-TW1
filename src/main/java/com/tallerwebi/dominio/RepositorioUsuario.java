package com.tallerwebi.dominio;

public interface RepositorioUsuario {

    Usuario buscarUsuario(String email, String password);
    void guardar(Usuario usuario);
    Usuario buscarPorEmail(String email);
    void modificar(Usuario usuario);

    void agregarFavorito(Propiedad propiedad);
    void eliminarFavorito(Propiedad propiedad);
}

