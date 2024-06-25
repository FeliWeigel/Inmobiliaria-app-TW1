package com.tallerwebi.dominio.respositorio;

import com.tallerwebi.dominio.entidades.AlquilerPropiedad;
import com.tallerwebi.dominio.dto.FechasAlquilerDTO;

import java.util.List;

public interface RepositorioAlquiler {

    void nuevoAlquiler(AlquilerPropiedad alquiler);
    List<AlquilerPropiedad> getAlquileresByPropiedad(Long propiedadId);
    List<AlquilerPropiedad> getAlquileresByUsuario(Long usuarioId);
    List<FechasAlquilerDTO> getFechasByPropiedad(Long propiedadId);
}
