package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Propiedad;
import com.tallerwebi.dominio.ServicioPropiedad;
import com.tallerwebi.dominio.SubirImagenServicio;
import com.tallerwebi.dominio.excepcion.CRUDPropiedadExcepcion;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.List;

@Controller
public class ControladorPropiedad {

    private final ServicioPropiedad servicioPropiedad;

    public ControladorPropiedad(ServicioPropiedad servicioPropiedad) {
        this.servicioPropiedad = servicioPropiedad;
    }

    @RequestMapping(path = "/home", method = RequestMethod.GET)
    public ModelAndView irAHome() {

        ModelMap model = new ModelMap();

        try {
            List<Propiedad> propiedades = servicioPropiedad.listarPropiedades();
            model.put("propiedades", propiedades);
        } catch (Exception e){
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
    public ModelAndView filtrarPropiedadesPorPrecio(@RequestParam("ubicacion") String ubicacion) {
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

    @GetMapping("/propiedad/{id}")
    public ModelAndView verPropiedad(@PathVariable Long id) {

        ModelMap model = new ModelMap();

        try {
            Propiedad propiedad = servicioPropiedad.buscarPropiedad(id);
            model.put("message", "Detalles de la Propiedad.");
            model.put("propiedad", propiedad);
            return new ModelAndView("propiedad", model);
        } catch (CRUDPropiedadExcepcion e) {
            model.put("message", e.getMessage());
            return new ModelAndView("propiedad", model);
        } catch (Exception e) {
            model.put("message", "Error al Mostrar la Propiedad.");
            return new ModelAndView("propiedad", model);
        }
    }

    /*
    @RequestMapping(path = "/filtrado", method = RequestMethod.POST)
    public ModelAndView mostrarPropiedadesFiltradas(@ModelAttribute("datosFiltro") DatosFiltro datosFiltro) {
        FiltroPropiedad filtro = getFiltroPropiedad(datosFiltro);
        ModelMap model = new ModelMap();
        List<Propiedad> propiedades = servicioPropiedad.filtrar(filtro, datosFiltro);
        model.put("propiedades", propiedades);
        return new ModelAndView("home", model);
    }

    private static FiltroPropiedad getFiltroPropiedad(DatosFiltro datosFiltro) {
        FiltroPropiedad filtro;

        if (datosFiltro.getFiltrarPorPrecio() != null) {
            datosFiltro.setTipoDeFiltro(TipoDeFiltro.PRECIO);
        } else {
            datosFiltro.setPrecio(0.0);
        }

        if (datosFiltro.getTipoDeFiltro() == TipoDeFiltro.PRECIO) {
            filtro = new FiltroPorPrecio();
        } else {
            filtro = new FiltroPorArea();
        }
        return filtro;
    }
    */

}


