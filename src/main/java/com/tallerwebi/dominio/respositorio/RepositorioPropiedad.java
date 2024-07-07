package com.tallerwebi.dominio.respositorio;

import com.tallerwebi.dominio.entidades.Propiedad;
import com.tallerwebi.dominio.utilidad.EstadoPropiedad;

import java.util.List;

public interface RepositorioPropiedad {

    Propiedad buscarPropiedad(Long id);
    void agregarPropiedad(Propiedad propiedad);
    void eliminarPropiedad(Long propiedadId);

    void eliminarVisitasPorPropiedadId(Long propiedadId);

    List<Propiedad> listarPropiedades();
    void editarPropiedad(Propiedad propiedad);
    List<Propiedad> listarPorRangoPrecio(Double min, Double max);
    List<Propiedad> listarPorUbicacion(String ubicacion);
    List<Propiedad> listarPorEstado(EstadoPropiedad estado);
    List<Propiedad> listarPorSuperficie(Double superficie);
    List<Propiedad> listarPropiedadesAceptadas();
    List<Propiedad> listarPropiedadesPendientes();
    List<Propiedad> listarNovedades();
}
