package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Propiedad;
import com.tallerwebi.dominio.ServicioPropiedad;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorPropiedad {

    private ServicioPropiedad servicioPropiedad;

    public ControladorPropiedad(ServicioPropiedad servicioPropiedad) {
        this.servicioPropiedad = servicioPropiedad;
    }

    @GetMapping("/propiedad/{id}")
    public ModelAndView verPropiedad(@PathVariable Long id) {
        ModelMap model = new ModelMap();

        // Descomentar la linea de abajo para ver la vista de /propiedad/1 cuando se lanza el servidor.
        // Propiedad propiedad = new Propiedad(1L,"Casa 1",2,3,4,200.0,150000.0,"Ubicacion 1");

        try {
            Propiedad propiedad = servicioPropiedad.buscarPropiedad(id);
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


}


