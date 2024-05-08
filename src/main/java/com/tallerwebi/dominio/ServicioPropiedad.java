package com.tallerwebi.dominio;

import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Service
public class ServicioPropiedad {

    private RepositorioPropiedad repositorioPropiedad;

    public ServicioPropiedad(RepositorioPropiedad repositorioPropiedad) {

        this.repositorioPropiedad = repositorioPropiedad;
    }


    public ModelAndView buscarPropiedad(Long id) {

        ModelMap model = new ModelMap();
        try {
             // Propiedad propiedad = new Propiedad(1L, "Casa 1", 2, 3, 4, 200.0, 150000.0, "Ubicacion 1");
            Propiedad propiedad = repositorioPropiedad.buscarPropiedad(id);
            if (propiedad != null) {
                model.put("message", "Detalles de la Propiedad.");
                model.put("propiedad", propiedad);
                return new ModelAndView("propiedad", model);
            } else {
                model.put("message", "La Propiedad Buscada no Existe.");
            }
        } catch (Exception e) {
            model.put("message", "Error al Mostrar la Propiedad.");
        }

        return new ModelAndView("propiedad", model);
    }


    public List<Propiedad> listarPropiedades() {

        return this.repositorioPropiedad.listarPropiedades();
    }
}
