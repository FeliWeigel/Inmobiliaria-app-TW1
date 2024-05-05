package com.tallerwebi.dominio;

import java.util.List;

public interface RepositorioPropiedad {

    Propiedad buscarPropiedad(Long id);
    void eliminarPropiedad(Long propiedadId);
    List<Propiedad> listarPropiedades();
    Propiedad agregarPropiedad(Propiedad propiedad);
    Propiedad editarPropiedad(Propiedad propiedad);
}
