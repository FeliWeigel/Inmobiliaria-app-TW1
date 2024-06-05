package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Propiedad;
import com.tallerwebi.dominio.RepositorioUsuario;
import com.tallerwebi.dominio.ServicioPropiedad;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.CRUDPropiedadExcepcion;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Set;

@Controller
public class ControladorPropiedad {

    private final ServicioPropiedad servicioPropiedad;
    private final RepositorioUsuario repositorioUsuario;

    public ControladorPropiedad(ServicioPropiedad servicioPropiedad, RepositorioUsuario repositorioUsuario) {
        this.servicioPropiedad = servicioPropiedad;
        this.repositorioUsuario = repositorioUsuario;
    }

    @RequestMapping(path = "/home", method = RequestMethod.GET)
    public ModelAndView vistaHome(HttpSession session) {
        ModelMap model = new ModelMap();
        Usuario usuarioAutenticado = (Usuario) session.getAttribute("usuario");

        try {
            List<Propiedad> propiedades = servicioPropiedad.listarPropiedadesAceptadas();
            model.put("propiedades", propiedades);
        } catch (Exception e){
            model.put("message", "Ha Ocurrido un Error Inesperado");
        }

        if(usuarioAutenticado != null){
            try{
                Set<Propiedad> favoritos =  repositorioUsuario.listarFavoritos(usuarioAutenticado);
                model.put("favoritos", favoritos);
            }catch(CRUDPropiedadExcepcion e){
                model.put("error", e.getMessage());
            }
        }

        return new ModelAndView("home", model);
    }

    @RequestMapping(path = "/filtro/precio", method = RequestMethod.POST)
    public ModelAndView filtrarPropiedadesPorPrecio(
            @RequestParam("min") Double min,
            @RequestParam("max") Double max,
            HttpSession session
    ) {
        ModelMap model = new ModelMap();
        Usuario usuarioAutenticado = (Usuario) session.getAttribute("usuario");
        try {
            List<Propiedad> propiedadesFiltradas = servicioPropiedad.listarPropiedadesPorPrecio(min, max);
            model.put("propiedades", propiedadesFiltradas);
        }catch(CRUDPropiedadExcepcion e){
            model.put("message", e.getMessage());
        }catch (Exception e){
            model.put("message", "Ha Ocurrido un Error Inesperado");
        }

        if(usuarioAutenticado != null){
            try{
                Set<Propiedad> favoritos =  repositorioUsuario.listarFavoritos(usuarioAutenticado);
                model.put("favoritos", favoritos);
            }catch(CRUDPropiedadExcepcion e){
                model.put("error", e.getMessage());
            }
        }

        return new ModelAndView("home", model);
    }


    @RequestMapping(path = "/filtro/ubicacion", method = RequestMethod.POST)
    public ModelAndView filtrarPropiedadesPorUbicacion(@RequestParam("ubicacion") String ubicacion, HttpSession session) {
        ModelMap model = new ModelMap();
        Usuario usuarioAutenticado = (Usuario) session.getAttribute("usuario");

        try {
            List<Propiedad> propiedadesFiltradas = servicioPropiedad.listarPropiedadesPorUbicacion(ubicacion);
            model.put("propiedades", propiedadesFiltradas);
        }catch(CRUDPropiedadExcepcion e){
            model.put("message", e.getMessage());
        }
        catch (Exception e){
            model.put("message", "Ha Ocurrido un Error Inesperado");
        }

        if(usuarioAutenticado != null){
            try{
                Set<Propiedad> favoritos =  repositorioUsuario.listarFavoritos(usuarioAutenticado);
                model.put("favoritos", favoritos);
            }catch(CRUDPropiedadExcepcion e){
                model.put("error", e.getMessage());
            }
        }

        return new ModelAndView("home", model);
    }


    @RequestMapping(path = "/agregar-propiedad", method = RequestMethod.GET)
    public ModelAndView vistaAgregarPropiedad() {
        ModelMap model = new ModelMap();
        model.put("propiedad", new Propiedad());
        return new ModelAndView("nuevaPropiedad", model);
    }


    @RequestMapping(path = "/agregar-propiedad", method = RequestMethod.POST)
    public ModelAndView agregarPropiedad(
            @ModelAttribute("propiedad") Propiedad propiedad ,
            @RequestParam("imagen") MultipartFile imagen
    ){
        ModelMap model = new ModelMap();

        try{
            servicioPropiedad.agregarPropiedad(propiedad, imagen);
        }catch(CRUDPropiedadExcepcion | IOException e){
            model.put("error", e.getMessage());
            return new ModelAndView("nuevaPropiedad", model);
        }

        model.put("success", "La Propiedad ha sido agregada con exito!");
        return new ModelAndView("nuevaPropiedad", model);
    }


    @GetMapping("/propiedad/{id}")
    public ModelAndView verPropiedad(@PathVariable Long id) {
        ModelMap model = new ModelMap();

        try {
            Propiedad propiedad = servicioPropiedad.buscarPropiedad(id);
            model.put("messageSuccess", "Detalles de la Propiedad.");
            model.put("propiedad", propiedad);
        } catch (CRUDPropiedadExcepcion e) {
            model.put("messageError", e.getMessage());
            model.put("propiedad", null);
            return new ModelAndView("propiedad", model);
        } catch (Exception e) {
            model.put("messageError", "Error al encontrar la propiedad seleccionada.");
            model.put("propiedad", null);
            return new ModelAndView("propiedad", model);
        }

        return new ModelAndView("propiedad", model);
    }


    @RequestMapping(path = "/panel-admin/propiedades", method = RequestMethod.GET)
    public ModelAndView panelAdminPropiedades(HttpSession session) {
        ModelMap model = new ModelMap();
        Usuario usuarioAutenticado = (Usuario) session.getAttribute("usuario");

        if (usuarioAutenticado == null || !usuarioAutenticado.getRol().equals("ADMIN")) {
            return new ModelAndView("redirect:/home");
        }

        try {
            List<Propiedad> propiedadesPendientes = servicioPropiedad.listarPropiedadesPendientes();
            List<Propiedad> propiedadesAceptadas = servicioPropiedad.listarPropiedadesAceptadas();
            model.put("propiedadesPendientes", propiedadesPendientes);
            model.put("propiedadesAceptadas", propiedadesAceptadas);
        } catch (Exception e) {
            model.put("message", "Ha ocurrido un error inesperado");
        }

        return new ModelAndView("panelAdminPropiedades", model);
    }


    @RequestMapping(path = "/aceptar-propiedad", method = RequestMethod.POST)
    public ModelAndView aceptarPropiedad(@RequestParam("id") Long propiedadId, HttpSession session) {
        ModelMap model = new ModelMap();
        Usuario usuarioAutenticado = (Usuario) session.getAttribute("usuario");

        if (usuarioAutenticado == null || !usuarioAutenticado.getRol().equals("ADMIN")) {
            return new ModelAndView("redirect:/home");
        }

        try {
            this.servicioPropiedad.aceptarPropiedad(propiedadId);
        } catch (CRUDPropiedadExcepcion e) {
            model.put("error", e.getMessage());
            return new ModelAndView("redirect:/panel-admin/propiedades", model);
        }

        return new ModelAndView("redirect:/panel-admin/propiedades");
    }


    @RequestMapping(path = "/rechazar-propiedad", method = RequestMethod.POST)
    public ModelAndView rechazarPropiedad(@RequestParam("id") Long propiedadId, HttpSession session) {
        ModelMap model = new ModelMap();
        Usuario usuarioAutenticado = (Usuario) session.getAttribute("usuario");

        if (usuarioAutenticado == null || !usuarioAutenticado.getRol().equals("ADMIN")) {
            return new ModelAndView("redirect:/home");
        }

        try {
            this.servicioPropiedad.rechazarPropiedad(propiedadId);
        } catch (CRUDPropiedadExcepcion e) {
            model.put("error", e.getMessage());
            return new ModelAndView("redirect:/panel-admin/propiedades", model);
        }
        return new ModelAndView("redirect:/panel-admin/propiedades");
    }




    @RequestMapping(path = "/panel-admin/usuarios", method = RequestMethod.GET)
    public ModelAndView panelAdminUsuarios(HttpSession session) {
        ModelMap model = new ModelMap();
        Usuario usuarioAutenticado = (Usuario) session.getAttribute("usuario");

        if (usuarioAutenticado == null || !usuarioAutenticado.getRol().equals("ADMIN")) {
            return new ModelAndView("redirect:/home");
        }

        try {
            List<Usuario> usuariosBloqueados = repositorioUsuario.listarUsuariosBloqueados();
            List<Usuario> usuariosDesbloqueados = repositorioUsuario.listarUsuariosDesbloqueados();
            model.put("usuariosBloqueados", usuariosBloqueados);
            model.put("usuariosDesbloqueados", usuariosDesbloqueados);
        } catch (Exception e) {
            model.put("message", "Ha ocurrido un error inesperado");
        }

        return new ModelAndView("panelAdminUsuarios", model);
    }


    @RequestMapping(path = "/bloquear-usuario", method = RequestMethod.POST)
    public ModelAndView bloquearUsuario(@RequestParam("id") Long usuarioId, HttpSession session) {
        ModelMap model = new ModelMap();
        Usuario usuarioAutenticado = (Usuario) session.getAttribute("usuario");

        if (usuarioAutenticado == null || !usuarioAutenticado.getRol().equals("ADMIN")) {
            return new ModelAndView("redirect:/home");
        }

        try {
            this.repositorioUsuario.bloquearUsuario(usuarioId);
        } catch (CRUDPropiedadExcepcion e) {
            model.put("error", e.getMessage());
            return new ModelAndView("redirect:/panel-admin/usuarios", model);
        }

        return new ModelAndView("redirect:/panel-admin/usuarios");
    }


    @RequestMapping(path = "/desbloquear-usuario", method = RequestMethod.POST)
    public ModelAndView desbloquearUsuario(@RequestParam("id") Long usuarioId, HttpSession session) {
        ModelMap model = new ModelMap();
        Usuario usuarioAutenticado = (Usuario) session.getAttribute("usuario");

        if (usuarioAutenticado == null || !usuarioAutenticado.getRol().equals("ADMIN")) {
            return new ModelAndView("redirect:/home");
        }

        try {
            this.repositorioUsuario.desbloquearUsuario(usuarioId);
        } catch (CRUDPropiedadExcepcion e) {
            model.put("error", e.getMessage());
            return new ModelAndView("redirect:/panel-admin/usuarios", model);
        }
        return new ModelAndView("redirect:/panel-admin/usuarios");
    }




}


