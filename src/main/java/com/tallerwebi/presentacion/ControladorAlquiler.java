package com.tallerwebi.presentacion;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tallerwebi.dominio.dto.FechasAlquilerDTO;
import com.tallerwebi.dominio.servicio.ServicioAlquiler;
import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.excepcion.AlquilerDenegadoExcepcion;
import com.tallerwebi.dominio.excepcion.CRUDPropiedadExcepcion;
import com.tallerwebi.dominio.excepcion.UsuarioNoIdentificadoExcepcion;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.util.List;

@Controller
public class ControladorAlquiler {
    private final ServicioAlquiler servicioAlquiler;
    public ControladorAlquiler(ServicioAlquiler servicioAlquiler) {
        this.servicioAlquiler = servicioAlquiler;
    }

//    @RequestMapping(path = "/propiedad/{id}/nuevo-alquiler", method = RequestMethod.GET)
//    public ModelAndView vistaNuevoAlquiler(@PathVariable Long id, HttpSession session) {
//        ModelMap model = new ModelMap();
//        Usuario usuarioAutenticado = (Usuario) session.getAttribute("usuario");
//        if(usuarioAutenticado == null){
//            return new ModelAndView("redirect:/login");
//        }
//        if(id == null){
//            return new ModelAndView("redirect:/home");
//        }
//        model.put("usuario", usuarioAutenticado);
//        model.put("propiedadId", id);
//        return new ModelAndView("pago", model);
//
//    }

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
        return new ModelAndView("pago", model);
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

    @RequestMapping(path = "/propiedad/{id}/reservas", method = RequestMethod.GET)
    public void fechasReservadasPorPropiedad(@PathVariable Long id, HttpServletResponse response) throws IOException {
        List<FechasAlquilerDTO> fechasReservadas = servicioAlquiler.fechasReservadasPorPropiedad(id);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(out, fechasReservadas);
    }
}
