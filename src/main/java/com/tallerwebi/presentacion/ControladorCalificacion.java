package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.entidades.CalificacionPropiedad;
import com.tallerwebi.dominio.entidades.Propiedad;
import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.excepcion.CRUDPropiedadExcepcion;
import com.tallerwebi.dominio.excepcion.CalificacionDenegadaExcepcion;
import com.tallerwebi.dominio.excepcion.UsuarioNoIdentificadoExcepcion;
import com.tallerwebi.dominio.servicio.ServicioCalificacion;
import com.tallerwebi.dominio.servicio.ServicioPropiedad;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

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

    @GetMapping("/propiedad/{id}/calificaciones")
    public ModelAndView listarCalificaciones(
            @PathVariable("id") Long id, HttpSession session
    ){
        ModelMap model = new ModelMap();
        List<CalificacionPropiedad> calificaciones = new ArrayList<>();
        Usuario usuarioAutenticado = (Usuario) session.getAttribute("usuario");
        if(usuarioAutenticado != null){
            model.put("usuario", usuarioAutenticado);
        }

        try {
            Propiedad propiedad = servicioPropiedad.buscarPropiedad(id);
            calificaciones = servicioCalificacion.listarCalificacionesPorPropiedad(id);
            model.put("calificaciones", calificaciones);
        } catch (CRUDPropiedadExcepcion e) {
            model.put("messageError", e.getMessage());
            return new ModelAndView("propiedad", model);
        }

        if(calificaciones.size() == 0){
            model.put("listEmpty", "Todavia no se han aportado reseñas de esta propiedad.");
        }

        return new ModelAndView("listaCalificaciones",model);
    }

    @GetMapping("/propiedad/calificacion/{propiedadId}/{id}")
    public ModelAndView verCalificacion(
            @PathVariable("id") Long id,
            @PathVariable("propiedadId") Long propiedadId
    ){
        ModelMap model = new ModelMap();

        try {
            CalificacionPropiedad calificacion = servicioCalificacion.getCalificacion(id);
            model.put("calificacion", calificacion);
        } catch (CalificacionDenegadaExcepcion e) {
            model.put("messageError", e.getMessage());
            return new ModelAndView("propiedad", model);
        }

        return new ModelAndView("calificacion", model);
    }

    @PostMapping("/propiedad/calificacion/{propiedadId}/{id}/reportar")
    public ModelAndView reportarCalificacion(
            @PathVariable("id") Long id,
            @PathVariable("propiedadId") Long propiedadId,
            HttpSession session
    ){
        ModelMap model = new ModelMap();
        Usuario usuarioAutenticado = (Usuario) session.getAttribute("usuario");
        if(usuarioAutenticado == null){
            return new ModelAndView("redirect:/login");
        }

        List<CalificacionPropiedad> calificaciones = new ArrayList<>();

        try {
            servicioCalificacion.reportarCalificacion(id);
            model.put("success", "La reseña ha sido reportada correctamente! El usuario ha recibido un email con el aviso correspondiente.");
        } catch (CalificacionDenegadaExcepcion e) {
            model.put("error", e.getMessage());
            return new ModelAndView("listaCalificaciones", model);
        }

        calificaciones = servicioCalificacion.listarCalificacionesPorPropiedad(propiedadId);
        model.put("calificaciones", calificaciones);
        return new ModelAndView("listaCalificaciones", model);
    }
}
