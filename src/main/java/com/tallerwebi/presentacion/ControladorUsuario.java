package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.*;
import com.tallerwebi.dominio.excepcion.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class ControladorUsuario {
    private final ServicioUsuario servicioUsuario;
    private final ServicioPropiedad servicioPropiedad;
    private final SubirImagenServicio imagenServicio;

    public ControladorUsuario(ServicioUsuario servicioUsuario, ServicioPropiedad servicioPropiedad, SubirImagenServicio imagenServicio) {
        this.servicioUsuario = servicioUsuario;
        this.servicioPropiedad = servicioPropiedad;
        this.imagenServicio = imagenServicio;
    }

    @RequestMapping(path = "/favoritos", method = RequestMethod.GET)
    public ModelAndView vistaFavoritos(HttpSession session) {
        ModelMap model = new ModelMap();
        Usuario usuarioAutenticado = (Usuario) session.getAttribute("usuario");
        Set<Propiedad> favoritos = new HashSet<>();

        if(usuarioAutenticado == null){
            return new ModelAndView("redirect:/login");
        }

        try {
            favoritos = servicioUsuario.listarFavoritos(usuarioAutenticado);
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
        List<Propiedad> propiedades = servicioPropiedad.listarPropiedadesAceptadas();

        if (usuarioAutenticado == null){
            return new ModelAndView("redirect:/login");
        }

        try {
            servicioUsuario.agregarFavorito(usuarioAutenticado, propiedadId);
            model.put("success", "La propiedad ha sido agregada a tu lista de favoritos correctamente!");
        }catch(CRUDPropiedadExcepcion e){
            model.put("error", e.getMessage());
        }

        try{
            Set<Propiedad> favoritos =  servicioUsuario.listarFavoritos(usuarioAutenticado);
            model.put("favoritos", favoritos);
        }catch(CRUDPropiedadExcepcion e){
            model.put("error", e.getMessage());
        }

        model.put("propiedades", propiedades);
        return new ModelAndView("lista-propiedades", model);
    }


    @RequestMapping(path = "/favoritos/eliminar/{propiedadId}", method = RequestMethod.POST)
    public ModelAndView eliminarFavorito(@PathVariable Long propiedadId, HttpSession session){
        ModelMap model = new ModelMap();
        Usuario usuarioAutenticado = (Usuario) session.getAttribute("usuario");
        List<Propiedad> propiedades = servicioPropiedad.listarPropiedadesAceptadas();

        if (usuarioAutenticado == null){
            return new ModelAndView("redirect:/login");
        }

        try {
            servicioUsuario.eliminarFavorito(usuarioAutenticado, propiedadId);
            model.put("success", "La propiedad ha sido eliminada de tu lista de favoritos correctamente.");
        }catch(CRUDPropiedadExcepcion e){
            model.put("error", e.getMessage());
        }

        try{
            Set<Propiedad> favoritos =  servicioUsuario.listarFavoritos(usuarioAutenticado);
            model.put("favoritos", favoritos);
        }catch(CRUDPropiedadExcepcion e){
            model.put("error", e.getMessage());
        }

        model.put("propiedades", propiedades);
        return new ModelAndView("lista-propiedades", model);
    }


    @RequestMapping("/perfil")
    public ModelAndView irAPerfil(HttpSession session) {
        ModelMap model = new ModelMap();
        Usuario usuarioAutenticado = (Usuario) session.getAttribute("usuario");

        if (usuarioAutenticado == null) {
            return new ModelAndView("redirect:/login");
        }

        model.put("usuario", usuarioAutenticado);
        return new ModelAndView("perfil", model);
    }

    @RequestMapping(path = "/perfil/editar/foto-perfil", method = RequestMethod.POST)
    public ModelAndView nuevaFotoPerfil(@RequestParam("foto")MultipartFile foto, HttpSession session) throws IOException {
        ModelMap model = new ModelMap();
        Usuario usuarioAutenticado = (Usuario) session.getAttribute("usuario");

        if(usuarioAutenticado == null){
            return new ModelAndView("redirect:/login");
        }

        imagenServicio.subirImagenUsuario(usuarioAutenticado.getId(), foto);
        Usuario usuarioActualizado = servicioUsuario.buscarPorId(usuarioAutenticado.getId());
        session.setAttribute("usuario", usuarioActualizado);
        model.put("usuario", usuarioActualizado);

        return new ModelAndView("perfil", model);
    }

    @RequestMapping(path = "/editar-perfil", method = RequestMethod.POST)
    public ModelAndView perfil(@ModelAttribute("usuario") Usuario usuario, HttpSession session) throws CredencialesInvalidasExcepcion, PasswordInvalidaExcepcion, EdadInvalidaExcepcion {
        ModelMap model = new ModelMap();
        Usuario usuarioAutenticado = (Usuario) session.getAttribute("usuario");
        usuario.setId(usuarioAutenticado.getId());

        try {
            servicioUsuario.editarPerfil(usuario);
        }catch(PasswordInvalidaExcepcion e){
            model.put("error", "Error! La contraseña debe contener al menos: 6 digitos, una mayuscula, un numero y un caracter especial.");
            return new ModelAndView("perfil", model);
        }catch(CredencialesInvalidasExcepcion e){
            model.put("error", "Debe completar todos los campos con datos validos!");
            return new ModelAndView("perfil", model);
        } catch (UsuarioExistenteExcepcion e) {
            model.put("error", "El email ya esta asociado a una cuenta existente.");
            return new ModelAndView("perfil", model);
        }

        return new ModelAndView("perfil");
    }

}
