package com.tallerwebi.dominio;

public interface RepositorioUsuario {

    Usuario buscarUsuario(String email, String password);
    void guardar(Usuario usuario);
    Usuario buscarPorEmail(String email);

    Usuario buscarPorId(long usuarioId);

    void modificar(Usuario usuario);

    void agregarFavorito(Usuario usuario, Propiedad propiedad);
    void eliminarFavorito(Usuario usuario, Propiedad propiedad);
}

