package com.tallerwebi.dominio.filtro;

import com.tallerwebi.dominio.Propiedad;
import com.tallerwebi.presentacion.DatosFiltro;
import java.util.List;

public interface FiltroPropiedad {
    List<Propiedad> filtrar(List<Propiedad> propiedades, DatosFiltro datosFiltro);
}
