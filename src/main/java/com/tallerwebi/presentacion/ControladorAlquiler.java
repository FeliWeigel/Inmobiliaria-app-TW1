package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.FechasAlquiler;
import com.tallerwebi.dominio.ServicioAlquiler;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.AlquilerDenegadoExcepcion;
import com.tallerwebi.dominio.excepcion.CRUDPropiedadExcepcion;
import com.tallerwebi.dominio.excepcion.UsuarioNoIdentificadoExcepcion;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.sql.Date;
import java.util.List;

@Controller
public class ControladorAlquiler {

    private final ServicioAlquiler servicioAlquiler;

    public ControladorAlquiler(ServicioAlquiler servicioAlquiler) {
        this.servicioAlquiler = servicioAlquiler;
    }

    @RequestMapping(path = "/propiedad/{id}/nuevo-alquiler", method = RequestMethod.POST)
    public ModelAndView nuevoAlquiler(
            @PathVariable Long id, HttpSession session,
            @RequestParam Date fechaInicio, @RequestParam Date fechaFin
    ){
        ModelMap model = new ModelMap();
        Usuario usuarioAutenticado = (Usuario) session.getAttribute("usuario");
        if(usuarioAutenticado == null){
            return new ModelAndView("redirect:/login");
        }
        model.put("usuario", usuarioAutenticado);

        try {
            servicioAlquiler.agregarNuevoAlquiler(usuarioAutenticado, id, fechaInicio, fechaFin);
        }catch(UsuarioNoIdentificadoExcepcion | CRUDPropiedadExcepcion e){
            model.put("error", "Ha ocurrido un error inesperado, vuelva a intentarlo en unos minutos.");
            return new ModelAndView("alquiler", model);
        }catch(AlquilerDenegadoExcepcion e){
            model.put("error", e.getMessage());
            return new ModelAndView("alquiler", model);
        }

        model.put("success", "Alquiler efectuado correctamente! Sera contactado por el propietario en las proximas 72hs.");
        return new ModelAndView("alquiler", model);
    }

    @RequestMapping(path = "/propiedad/{id}/alquiler", method = RequestMethod.GET)
    public ModelAndView vistaAlquilerPropiedad(@PathVariable Long id, HttpSession session) {
        ModelMap model = new ModelMap();
        Usuario usuarioAutenticado = (Usuario) session.getAttribute("usuario");
        if(usuarioAutenticado == null){
            return new ModelAndView("redirect:/login");
        }
        if(id == null){
            return new ModelAndView("redirect:/home");
        }
        model.put("usuario", usuarioAutenticado);
        model.put("propiedadId", id);
        return new ModelAndView("alquiler", model);
    }

    @RequestMapping(path = "/propiedad/{id}/alquiler/fechas", method = RequestMethod.GET)
    public List<FechasAlquiler> fechasReservadasPorPropiedad(@PathVariable Long id){
        return servicioAlquiler.fechasReservadasPorPropiedad(id);
    }
}
