package com.tallerwebi.dominio;

import java.util.List;

public interface RepositorioPropiedad {

    Propiedad buscarPropiedad(String nombre);
    void eliminarPropiedad(Long propiedadId);
    List<Propiedad> listarPropiedades();
}
