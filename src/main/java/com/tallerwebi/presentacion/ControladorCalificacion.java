package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.*;
import com.tallerwebi.dominio.excepcion.CRUDPropiedadExcepcion;
import com.tallerwebi.dominio.excepcion.CalificacionDenegadaExcepcion;
import com.tallerwebi.dominio.excepcion.UsuarioNoIdentificadoExcepcion;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

@Controller
public class ControladorCalificacion {
    private final ServicioCalificacion servicioCalificacion;
    private final ServicioPropiedad servicioPropiedad;

    public ControladorCalificacion(ServicioCalificacion servicioCalificacion, ServicioPropiedad servicioPropiedad) {
        this.servicioCalificacion = servicioCalificacion;
        this.servicioPropiedad = servicioPropiedad;
    }

    @GetMapping("/propiedad/{id}/nueva-calificacion")
    public ModelAndView vistaAgregarCalificacion(@PathVariable Long id){
        ModelMap model = new ModelMap();
        model.put("id", id);
        return new ModelAndView("agregarCalificacion", model);
    }

    @PostMapping("/propiedad/{id}/nueva-calificacion")
    public ModelAndView agregarNuevaCalificacion(
            @PathVariable("id") Long id,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("puntaje") Double puntaje,
            HttpSession session
    ){
        ModelMap model = new ModelMap();
        Usuario usuarioAutenticado = (Usuario) session.getAttribute("usuario");
        if(usuarioAutenticado == null){
            return new ModelAndView("redirect:/login");
        }
        model.put("usuario", usuarioAutenticado);

        try{
            servicioCalificacion.agregarNuevaCalificacion(id, usuarioAutenticado, descripcion, puntaje);
            model.put("success", "La calificacion ha sido publicada correctamente!");
        }catch (CalificacionDenegadaExcepcion | CRUDPropiedadExcepcion e){
            model.put("error", e.getMessage());
        } catch (UsuarioNoIdentificadoExcepcion e) {
            return new ModelAndView("redirect:/login");
        } catch (Exception e) {
            model.put("error", "Error inesperado al agregar la calificación.");
        }

        return new ModelAndView("agregarCalificacion", model);
    }
}
