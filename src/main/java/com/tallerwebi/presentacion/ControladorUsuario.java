package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Propiedad;
import com.tallerwebi.dominio.RepositorioUsuario;
import com.tallerwebi.dominio.ServicioPropiedad;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.CRUDPropiedadExcepcion;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class ControladorUsuario {
    private final RepositorioUsuario repositorioUsuario;
    private final ServicioPropiedad servicioPropiedad;

    public ControladorUsuario(RepositorioUsuario repositorioUsuario, ServicioPropiedad servicioPropiedad) {
        this.repositorioUsuario = repositorioUsuario;
        this.servicioPropiedad = servicioPropiedad;
    }


    @RequestMapping(path = "/favoritos", method = RequestMethod.GET)
    public ModelAndView vistaFavoritos(HttpSession session) {
        ModelMap model = new ModelMap();
        Usuario usuarioAutenticado = (Usuario) session.getAttribute("usuario");
        Set<Propiedad> favoritos = new HashSet<>();

        try {
            favoritos = repositorioUsuario.listarFavoritos(usuarioAutenticado);
            model.put("listaFavoritos", favoritos);
        }catch(CRUDPropiedadExcepcion e){
            model.put("error", e.getMessage());
        }
        catch (Exception e){
            model.put("error", "Ha Ocurrido un Error Inesperado");
        }

        if(favoritos.size() == 0){
            model.put("listEmpty", "Todavia no has agregado propiedades a la lista de favoritos.");
        }

        return new ModelAndView("favoritos", model);
    }


    @RequestMapping(path = "/favoritos/agregar/{propiedadId}", method = RequestMethod.POST)
    public ModelAndView agregarFavorito(@PathVariable Long propiedadId, HttpSession session){
        ModelMap model = new ModelMap();
        Usuario usuarioAutenticado = (Usuario) session.getAttribute("usuario");
        List<Propiedad> propiedades = servicioPropiedad.listarPropiedades();

        try {
            repositorioUsuario.agregarFavorito(usuarioAutenticado, propiedadId);
            model.put("success", "La propiedad ha sido agregada a tu lista de favoritos correctamente!");
        }catch(CRUDPropiedadExcepcion e){
            model.put("error", e.getMessage());
        }

        model.put("propiedades", propiedades);
        return new ModelAndView("home", model);
    }


    @RequestMapping(path = "/favoritos/eliminar/{propiedadId}", method = RequestMethod.DELETE)
    public ModelAndView eliminarFavorito(@PathVariable Long propiedadId, HttpSession session){
        ModelMap model = new ModelMap();
        Usuario usuarioAutenticado = (Usuario) session.getAttribute("usuario");

        try {
            repositorioUsuario.eliminarFavorito(usuarioAutenticado, propiedadId);
            model.put("success", "La propiedad ha sido eliminada de tu lista de favoritos correctamente.");
        }catch(CRUDPropiedadExcepcion e){
            model.put("error", e.getMessage());
        }

        return new ModelAndView("home", model);
    }



}
