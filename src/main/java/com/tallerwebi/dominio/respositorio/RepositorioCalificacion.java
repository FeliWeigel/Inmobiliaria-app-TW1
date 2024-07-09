package com.tallerwebi.dominio.respositorio;

import com.tallerwebi.dominio.entidades.CalificacionPropiedad;

import java.util.List;

public interface RepositorioCalificacion {
    List<CalificacionPropiedad> listarCalificacionesPorPropiedad(Long propiedadId);
    List<CalificacionPropiedad> listarCalificacionesPorUsuario(Long usuarioId);
    CalificacionPropiedad getCalificacionPorId(Long calificacionId);
    void agregarNuevaCalificacion(CalificacionPropiedad calificacion);
    void eliminarCalificacion(Long idCalificacion);
}
