package com.tallerwebi.dominio;

import java.sql.Date;
import java.util.List;

public interface RepositorioAlquiler {

    void nuevoAlquiler(AlquilerPropiedad alquiler);
    List<AlquilerPropiedad> getAlquileresByPropiedad(Long propiedadId);
    List<AlquilerPropiedad> getAlquileresByUsuario(Long usuarioId);
}
