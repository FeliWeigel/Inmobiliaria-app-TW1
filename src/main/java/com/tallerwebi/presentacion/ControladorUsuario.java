package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.*;
import com.tallerwebi.dominio.excepcion.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
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
    private final ServicioLogin servicioLogin;

    public ControladorUsuario(RepositorioUsuario repositorioUsuario, ServicioPropiedad servicioPropiedad, ServicioLogin servicioLogin) {
        this.repositorioUsuario = repositorioUsuario;
        this.servicioPropiedad = servicioPropiedad;
        this.servicioLogin = servicioLogin;
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

    @RequestMapping("/perfil")
    public ModelAndView irAPerfil(HttpSession session) {
        ModelMap model = new ModelMap();
        Usuario usuarioAutenticado = (Usuario) session.getAttribute("usuario");
        model.put("usuario", usuarioAutenticado);
        return new ModelAndView("perfil", model);
    }

    @RequestMapping(path = "/editar-perfil", method = RequestMethod.POST)
    public ModelAndView perfil(@ModelAttribute("usuario") Usuario usuario, HttpSession session) throws CredencialesInvalidasExcepcion, PasswordInvalidaExcepcion, EdadInvalidaExcepcion {
        ModelMap model = new ModelMap();
        Usuario usuarioAutenticado = (Usuario) session.getAttribute("usuario");
        usuario.setId(usuarioAutenticado.getId());

        try {
            servicioLogin.editarPerfil(usuario);
        }catch(PasswordInvalidaExcepcion e){
            model.put("error", "Error! La contraseña debe contener al menos: 6 digitos, una mayuscula, un numero y un caracter especial.");
            return new ModelAndView("perfil", model);
        }catch(EdadInvalidaExcepcion e){
            model.put("error", "Cuidado! Debe ser mayor de 18 años.");
            return new ModelAndView("perfil", model);
        }catch(CredencialesInvalidasExcepcion e){
            model.put("error", "Debe completar todos los campos con datos validos!");
            return new ModelAndView("perfil", model);
        }

        return new ModelAndView("perfil");
    }

}
