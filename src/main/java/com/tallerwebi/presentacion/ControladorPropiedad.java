package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.dto.DatosLoginDTO;
import com.tallerwebi.dominio.entidades.CalificacionPropiedad;
import com.tallerwebi.dominio.dto.FiltroPropiedadDTO;
import com.tallerwebi.dominio.entidades.Propiedad;
import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.entidades.Visita;
import com.tallerwebi.dominio.excepcion.CRUDPropiedadExcepcion;
import com.tallerwebi.dominio.servicio.ServicioCalificacion;
import com.tallerwebi.dominio.servicio.ServicioHistorial;
import com.tallerwebi.dominio.servicio.ServicioPropiedad;
import com.tallerwebi.dominio.servicio.ServicioUsuario;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

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
    private final ServicioHistorial servicioHistorial;
    private final ServicioCalificacion servicioCalificacion;
    private final String CARPETA_IMAGENES = "src/main/webapp/resources/core/img/propiedades/";

    public ControladorPropiedad(ServicioPropiedad servicioPropiedad, ServicioUsuario servicioUsuario, ServicioCalificacion servicioCalificacion, ServicioHistorial servicioHistorial) {
        this.servicioPropiedad = servicioPropiedad;
        this.servicioUsuario = servicioUsuario;
        this.servicioCalificacion = servicioCalificacion;
        this.servicioHistorial = servicioHistorial;
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

    @RequestMapping(path = "/lista-propiedades/filtro", method = RequestMethod.POST)
    public ModelAndView filtrarPropiedades(@ModelAttribute("filtroPropiedad") FiltroPropiedadDTO filtro, HttpSession session){
        ModelMap model = new ModelMap();
        Usuario usuarioAutenticado = (Usuario) session.getAttribute("usuario");

        try {
            List<Propiedad> propiedadesFiltradas = servicioPropiedad.filtrarPropiedades(filtro);
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
        List<CalificacionPropiedad> calificaciones = new ArrayList<>();

        try {
            Propiedad propiedad = servicioPropiedad.buscarPropiedad(id);
            calificaciones = servicioCalificacion.listarCalificacionesPorPropiedad(id);
            model.put("calificaciones", calificaciones);
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

        if(calificaciones.size() == 0){
            model.put("listEmpty", "Todavia no se han aportado reseñas de esta propiedad.");
        }

        servicioHistorial.registrarVisita(usuarioAutenticado, servicioPropiedad.buscarPropiedad(id));

        return new ModelAndView("propiedad", model);
    }

    @RequestMapping(path = "/agregar-propiedad", method = RequestMethod.GET)
    public ModelAndView vistaAgregarPropiedad() {
        ModelMap model = new ModelMap();
        model.put("propiedad", new Propiedad());
        return new ModelAndView("nuevaPropiedad", model);
    }


    @RequestMapping(path = "/agregar-propiedad", method = RequestMethod.POST)
    public ModelAndView agregarPropiedad(
            @ModelAttribute("propiedad") Propiedad propiedad,
            @RequestParam("imagen") MultipartFile imagen,
            HttpSession session
    ){
        ModelMap model = new ModelMap();
        Usuario usuarioAutenticado = (Usuario) session.getAttribute("usuario");

        if (usuarioAutenticado == null) {
            return new ModelAndView("redirect:/login");
        }

        try{
            servicioPropiedad.agregarPropiedad(propiedad, imagen);
        }catch(CRUDPropiedadExcepcion | IOException e){
            model.put("error", e.getMessage());
            return new ModelAndView("nuevaPropiedad", model);
        }

        model.put("success", "La peticion ha sido registrada con exito! La propiedad sera publicada en cuanto verifiquemos los detalles de la venta.");
        return new ModelAndView("nuevaPropiedad", model);
    }


    @RequestMapping(path = "/historial", method = RequestMethod.GET)
    public ModelAndView irAlHistorial(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        ModelMap model = new ModelMap();
        if(usuario==null){
            DatosLoginDTO datosLogin = new DatosLoginDTO();
            model.put("datosLogin", datosLogin);
            return new ModelAndView("login", model);
        }
        List<Visita> historial = servicioHistorial.obtenerHistorial(usuario.getId());
        model.put("historial", historial);
        return new ModelAndView("historial", model);
    }
}


