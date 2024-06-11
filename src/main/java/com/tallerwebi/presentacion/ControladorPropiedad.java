package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.*;
import com.tallerwebi.dominio.excepcion.CRUDPropiedadExcepcion;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Controller
public class ControladorPropiedad {

    private final ServicioPropiedad servicioPropiedad;
    private final ServicioUsuario servicioUsuario;
    private final String CARPETA_IMAGENES = "src/main/webapp/resources/core/img/propiedades/";

    public ControladorPropiedad(ServicioPropiedad servicioPropiedad, ServicioUsuario servicioUsuario) {
        this.servicioPropiedad = servicioPropiedad;
        this.servicioUsuario = servicioUsuario;
    }

    @RequestMapping(path = "/home", method = RequestMethod.GET)
    public ModelAndView vistaHome(HttpSession session) {
        ModelMap model = new ModelMap();
        Usuario usuarioAutenticado = (Usuario) session.getAttribute("usuario");

        model.put("usuario", usuarioAutenticado);
        return new ModelAndView("home", model);
    }

    @RequestMapping(path = "/lista-propiedades", method = RequestMethod.GET)
    public ModelAndView vistaListadoPropiedades(HttpSession session) {
        ModelMap model = new ModelMap();
        Usuario usuarioAutenticado = (Usuario) session.getAttribute("usuario");

        try {
            List<Propiedad> propiedades = servicioPropiedad.listarPropiedadesAceptadas();
            model.put("propiedades", propiedades);
            model.put("usuario", usuarioAutenticado);
        } catch (Exception e){
            model.put("message", "Ha Ocurrido un Error Inesperado");
        }

        if(usuarioAutenticado != null){
            try{
                Set<Propiedad> favoritos =  servicioUsuario.listarFavoritos(usuarioAutenticado);
                model.put("favoritos", favoritos);
            }catch(CRUDPropiedadExcepcion e){
                model.put("error", e.getMessage());
            }
        }

        return new ModelAndView("lista-propiedades", model);
    }

    @GetMapping("/propiedad/{id}")
    public ModelAndView verPropiedad(@PathVariable Long id, HttpSession session) {
        ModelMap model = new ModelMap();
        Usuario usuarioAutenticado = (Usuario) session.getAttribute("usuario");

        String pathImagenes = CARPETA_IMAGENES + id; // Obtengo la direccion donde deberian estar almacenadas las imagenes referidas a la propiedad
        File carpetaImagenes = new File(pathImagenes); // Obtengo la carpeta
        File[] listOfFiles = carpetaImagenes.listFiles(); // Transformo el contenido de la carpeta a una lista de archivos(en este caso de imagen)
        List<String> imagenes = new ArrayList<>(); // Creo un array donde iran almacenadas todas las referencias a las imagenes que queremos mostrar

        if(listOfFiles != null){
            for(File image : listOfFiles){
                if(image.isFile()){
                    imagenes.add(image.getName());
                }
            }
        }

        try {
            Propiedad propiedad = servicioPropiedad.buscarPropiedad(id);
            model.put("messageSuccess", "Detalles de la Propiedad.");
            model.put("propiedad", propiedad);
            model.put("usuario", usuarioAutenticado);
            model.put("imagenes", imagenes);
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
            model.put("usuario", usuarioAutenticado);
        }catch(CRUDPropiedadExcepcion e){
            model.put("message", e.getMessage());
        }catch (Exception e){
            model.put("message", "Ha Ocurrido un Error Inesperado");
        }

        if(usuarioAutenticado != null){
            try{
                Set<Propiedad> favoritos =  servicioUsuario.listarFavoritos(usuarioAutenticado);
                model.put("favoritos", favoritos);
            }catch(CRUDPropiedadExcepcion e){
                model.put("error", e.getMessage());
            }
        }

        return new ModelAndView("lista-propiedades", model);
    }


    @RequestMapping(path = "/filtro/ubicacion", method = RequestMethod.POST)
    public ModelAndView filtrarPropiedadesPorUbicacion(@RequestParam("ubicacion") String ubicacion, HttpSession session) {
        ModelMap model = new ModelMap();
        Usuario usuarioAutenticado = (Usuario) session.getAttribute("usuario");

        try {
            List<Propiedad> propiedadesFiltradas = servicioPropiedad.listarPropiedadesPorUbicacion(ubicacion);
            model.put("propiedades", propiedadesFiltradas);
            model.put("usuario", usuarioAutenticado);
        }catch(CRUDPropiedadExcepcion e){
            model.put("message", e.getMessage());
        }
        catch (Exception e){
            model.put("message", "Ha Ocurrido un Error Inesperado");
        }

        if(usuarioAutenticado != null){
            try{
                Set<Propiedad> favoritos =  servicioUsuario.listarFavoritos(usuarioAutenticado);
                model.put("favoritos", favoritos);
            }catch(CRUDPropiedadExcepcion e){
                model.put("error", e.getMessage());
            }
        }

        return new ModelAndView("lista-propiedades", model);
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

    @RequestMapping(path = "/propiedad/{id}/alquiler", method = RequestMethod.GET)
    public ModelAndView vistaAlquilerPropiedad(HttpSession session) {
        ModelMap model = new ModelMap();
        Usuario usuarioAutenticado = (Usuario) session.getAttribute("usuario");

        model.put("usuario", usuarioAutenticado);
        return new ModelAndView("alquiler", model);
    }

    @RequestMapping(path = "/panel-admin/aceptar-propiedad", method = RequestMethod.POST)
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


    @RequestMapping(path = "/panel-admin/rechazar-propiedad", method = RequestMethod.POST)
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


    @RequestMapping(path = "/panel-admin/modificar-propiedad", method = RequestMethod.POST)
    public ModelAndView modificarPropiedad(@ModelAttribute("propiedad") Propiedad propiedad, HttpSession session) {
        ModelMap model = new ModelMap();
        Usuario usuarioAutenticado = (Usuario) session.getAttribute("usuario");

        if (usuarioAutenticado == null || !usuarioAutenticado.getRol().equals("ADMIN")) {
            return new ModelAndView("redirect:/home");
        }

        try {
            this.servicioPropiedad.modificarPropiedad(propiedad);
        } catch (CRUDPropiedadExcepcion e) {
            model.put("error", e.getMessage());
            return new ModelAndView("redirect:/panel-admin/propiedades", model);
        }
        return new ModelAndView("redirect:/panel-admin/propiedades");
    }


    @RequestMapping(path = "/panel-admin/modificar-propiedad/{id}", method = RequestMethod.GET)
    public ModelAndView mostrarFormularioModificarPropiedad(@PathVariable("id") Long id, HttpSession session) {
        ModelMap model = new ModelMap();
        Usuario usuarioAutenticado = (Usuario) session.getAttribute("usuario");

        if (usuarioAutenticado == null || !usuarioAutenticado.getRol().equals("ADMIN")) {
            return new ModelAndView("redirect:/home");
        }

        Propiedad propiedad = this.servicioPropiedad.buscarPropiedad(id);
        if (propiedad != null) {
            model.put("propiedad", propiedad);
            return new ModelAndView("modificarPropiedad", model);
        } else {
            model.put("error", "La propiedad no existe.");
            return new ModelAndView("redirect:/panel-admin/propiedades", model);
        }
    }



    @RequestMapping(path = "/panel-admin/usuarios", method = RequestMethod.GET)
    public ModelAndView panelAdminUsuarios(HttpSession session) {
        ModelMap model = new ModelMap();
        Usuario usuarioAutenticado = (Usuario) session.getAttribute("usuario");

        if (usuarioAutenticado == null || !usuarioAutenticado.getRol().equals("ADMIN")) {
            return new ModelAndView("redirect:/home");
        }

        try {
            List<Usuario> usuariosBloqueados = servicioUsuario.listarUsuariosBloqueados();
            List<Usuario> usuariosDesbloqueados = servicioUsuario.listarUsuariosDesbloqueados();
            model.put("usuariosBloqueados", usuariosBloqueados);
            model.put("usuariosDesbloqueados", usuariosDesbloqueados);
        } catch (Exception e) {
            model.put("message", "Ha ocurrido un error inesperado");
        }

        return new ModelAndView("panelAdminUsuarios", model);
    }


    @RequestMapping(path = "/panel-admin/bloquear-usuario", method = RequestMethod.POST)
    public ModelAndView bloquearUsuario(@RequestParam("id") Long usuarioId, HttpSession session) {
        ModelMap model = new ModelMap();
        Usuario usuarioAutenticado = (Usuario) session.getAttribute("usuario");

        if (usuarioAutenticado == null || !usuarioAutenticado.getRol().equals("ADMIN")) {
            return new ModelAndView("redirect:/home");
        }

        try {
            this.servicioUsuario.bloquearUsuario(usuarioId);
        } catch (CRUDPropiedadExcepcion e) {
            model.put("error", e.getMessage());
            return new ModelAndView("redirect:/panel-admin/usuarios", model);
        }

        return new ModelAndView("redirect:/panel-admin/usuarios");
    }


    @RequestMapping(path = "/panel-admin/desbloquear-usuario", method = RequestMethod.POST)
    public ModelAndView desbloquearUsuario(@RequestParam("id") Long usuarioId, HttpSession session) {
        ModelMap model = new ModelMap();
        Usuario usuarioAutenticado = (Usuario) session.getAttribute("usuario");

        if (usuarioAutenticado == null || !usuarioAutenticado.getRol().equals("ADMIN")) {
            return new ModelAndView("redirect:/home");
        }

        try {
            this.servicioUsuario.desbloquearUsuario(usuarioId);
        } catch (CRUDPropiedadExcepcion e) {
            model.put("error", e.getMessage());
            return new ModelAndView("redirect:/panel-admin/usuarios", model);
        }
        return new ModelAndView("redirect:/panel-admin/usuarios");
    }




}


