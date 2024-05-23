package com.tallerwebi.dominio;

import java.util.List;

public interface RepositorioPropiedad {

    Propiedad buscarPropiedad(Long id);
    void agregarPropiedad(Propiedad propiedad);
    void eliminarPropiedad(Long propiedadId);
    List<Propiedad> listarPropiedades();
    void editarPropiedad(Propiedad propiedad);

    Propiedad buscarPorId(long propiedadId);
    List<Propiedad> listarPorRangoPrecio(Double min, Double max);
    List<Propiedad> listarPorUbicacion(String ubicacion);
}
