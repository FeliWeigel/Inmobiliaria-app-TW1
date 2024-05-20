package com.tallerwebi.dominio.filtro;

import com.tallerwebi.dominio.Propiedad;
import com.tallerwebi.presentacion.DatosFiltro;

import java.util.List;

public class FiltroPorArea implements FiltroPropiedad {
    @Override
    public List<Propiedad> filtrar(List<Propiedad> propiedades, DatosFiltro datosFiltro) {
        return List.of();
    }
}
