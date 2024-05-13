package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Propiedad;
import com.tallerwebi.dominio.ServicioPropiedad;
import com.tallerwebi.dominio.excepcion.CRUDPropiedadExcepcion;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@Controller
public class ControladorPropiedad {

    private ServicioPropiedad servicioPropiedad;

    public ControladorPropiedad(ServicioPropiedad servicioPropiedad) {
        this.servicioPropiedad = servicioPropiedad;
    }

    @RequestMapping(path = "/home", method = RequestMethod.GET)
    public ModelAndView irAHome() {

        ModelMap model = new ModelMap();

        try {
            List<Propiedad> propiedades = servicioPropiedad.listarPropiedades();
            model.put("propiedades", propiedades);
        } catch (Exception e){
            model.put("message", "Ha Ocurrido un Error Inesperado");
        }

        return new ModelAndView("home", model);
    }

    @GetMapping("/propiedad/{id}")
    public ModelAndView verPropiedad(@PathVariable Long id) {

        ModelMap model = new ModelMap();

        try {
            Propiedad propiedad = servicioPropiedad.buscarPropiedad(id);
            model.put("message", "Detalles de la Propiedad.");
            model.put("propiedad", propiedad);
            return new ModelAndView("propiedad", model);
        } catch (CRUDPropiedadExcepcion e) {
            model.put("message", e.getMessage());
            return new ModelAndView("propiedad", model);
        } catch (Exception e) {
            model.put("message", "Error al Mostrar la Propiedad.");
            return new ModelAndView("propiedad", model);
        }
    }

}


