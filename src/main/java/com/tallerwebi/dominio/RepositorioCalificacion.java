package com.tallerwebi.dominio;

import java.util.List;

public interface RepositorioCalificacion {
    List<CalificacionPropiedad> listarCalificacionesPorPropiedad(Long propiedadId);
    List<CalificacionPropiedad> listarCalificacionesPorUsuario(Long usuarioId);
    void agregarNuevaCalificacion(CalificacionPropiedad calificacion);
}
