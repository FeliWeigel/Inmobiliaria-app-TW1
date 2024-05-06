package com.tallerwebi.dominio;

import java.util.List;

public interface ServicioPropiedad {
    Propiedad buscarPropiedad(Long id);
    List<Propiedad> listarPropiedades();
}
