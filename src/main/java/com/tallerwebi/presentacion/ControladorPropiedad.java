package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Propiedad;
import com.tallerwebi.dominio.ServicioPropiedad;
import com.tallerwebi.dominio.SubirImagenServicio;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.CRUDPropiedadExcepcion;
import com.tallerwebi.dominio.utilidad.EstadoPropiedad;
import com.tallerwebi.infraestructura.ServicioUsuario;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ControladorPropiedad {

    private final ServicioPropiedad servicioPropiedad;
    private final SubirImagenServicio servicioImagen;
    private final ServicioUsuario servicioUsuario;

    public ControladorPropiedad(ServicioPropiedad servicioPropiedad, SubirImagenServicio servicioImagen, ServicioUsuario servicioUsuario) {
        this.servicioPropiedad = servicioPropiedad;
        this.servicioImagen = servicioImagen;
        this.servicioUsuario = servicioUsuario;
    }

    public ControladorPropiedad(ServicioPropiedad servicioPropiedad) {
        this.servicioPropiedad = servicioPropiedad;
    }

    @RequestMapping(path = "/home", method = RequestMethod.GET)
    public ModelAndView vistaHome() {

        ModelMap model = new ModelMap();

        try {
            Usuario usuario = null;

//TODO: validar usuario loggeado

            usuario = servicioUsuario.getUsuarioByEmail("test@unlam.edu.ar");
            //fin validar usuario loggeado
            List<Propiedad> propiedades = servicioPropiedad.listarPropiedades();


            propiedades.add(new Propiedad(
                    (long)1,
                    "Casa en la playa",
                    2,
                    3,
                    4,
                    250.0,
                    300000.0,
                    "Playa del Carmen, México"
            ));

            propiedades.add(new Propiedad(
                    (long)2,
                    "Apartamento céntrico",
                    1,
                    1,
                    2,
                    80.0,
                    150000.0,
                    "Madrid, España"
            ));

            propiedades.add(new Propiedad(
                    (long)3,
                    "Chalet en la montaña",
                    3,
                    4,
                    5,
                    350.0,
                    450000.0,
                    "Sierra Nevada, España"
            ));

            propiedades.add(new Propiedad(
                    (long)4,
                    "Casa rural",
                    1,
                    2,
                    3,
                    150.0,
                    200000.0,
                    "Toscana, Italia"
            ));

            propiedades.add(new Propiedad(
                    (long)5,
                    "Penthouse de lujo",
                    1,
                    2,
                    3,
                    200.0,
                    600000.0,
                    "Nueva York, EE.UU."
            ));

            System.out.println("5");


            List<Propiedad> favoritos = usuario.getFavoritos();
            List<PropiedadDto> propiedadesDto = new ArrayList<>();


            for (Propiedad propiedad : propiedades) {
                propiedadesDto.add(new PropiedadDto(
                        propiedad.getId(),
                        propiedad.getNombre(),
                        propiedad.getPisos(),
                        propiedad.getBanios(),
                        propiedad.getHabitaciones(),
                        propiedad.getSuperficie(),
                        propiedad.getPrecio(),
                        propiedad.getUbicacion(),
                        propiedad.getEstado(),
                        propiedad.getRutaImagen(),
                        favoritos.stream()
                                .map(Propiedad::getId)
                                .anyMatch(id -> id.equals(propiedad.getId()))
                ));
            }
            System.out.println("6");

            System.out.println(propiedadesDto.size());


            model.put("propiedades", propiedadesDto);
        } catch (Exception e){
            System.out.println("ex: " + e.getMessage());

            model.put("message", "Ha Ocurrido un Error Inesperado");
        }

        return new ModelAndView("home", model);
    }

    @RequestMapping(path = "/filtro/precio", method = RequestMethod.POST)
    public ModelAndView filtrarPropiedadesPorPrecio(
            @RequestParam("min") Double min,
            @RequestParam("max") Double max
    ) {
        ModelMap model = new ModelMap();

        try {
            List<Propiedad> propiedadesFiltradas = servicioPropiedad.listarPropiedadesPorPrecio(min, max);
            model.put("propiedades", propiedadesFiltradas);
        }catch(CRUDPropiedadExcepcion e){
            model.put("message", e.getMessage());
        }
        catch (Exception e){
            model.put("message", "Ha Ocurrido un Error Inesperado");
        }

        return new ModelAndView("home", model);
    }

    @RequestMapping(path = "/filtro/ubicacion", method = RequestMethod.POST)
    public ModelAndView filtrarPropiedadesPorUbicacion(@RequestParam("ubicacion") String ubicacion) {
        ModelMap model = new ModelMap();

        try {
            List<Propiedad> propiedadesFiltradas = servicioPropiedad.listarPropiedadesPorUbicacion(ubicacion);
            model.put("propiedades", propiedadesFiltradas);
        }catch(CRUDPropiedadExcepcion e){
            model.put("message", e.getMessage());
        }
        catch (Exception e){
            model.put("message", "Ha Ocurrido un Error Inesperado");
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

}


