package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.CRUDPropiedadExcepcion;
import com.tallerwebi.dominio.utilidad.ValidarString;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ServicioPropiedad {

    private final RepositorioPropiedad repositorioPropiedad;
    private final SubirImagenServicio imagenServicio;

    public ServicioPropiedad(RepositorioPropiedad repositorioPropiedad, SubirImagenServicio imagenServicio) {
        this.repositorioPropiedad = repositorioPropiedad;
        this.imagenServicio = imagenServicio;
    }

    public Propiedad buscarPropiedad(Long id) {
        Propiedad propiedad = this.repositorioPropiedad.buscarPropiedad(id);

        if (propiedad != null) {
            return propiedad;
        } else {
            throw new CRUDPropiedadExcepcion("Error al encontrar la propiedad seleccionada.");
        }

    }

    public void agregarPropiedad(Propiedad propiedad, MultipartFile imagen) throws IOException {
        ValidarString validarString = new ValidarString();

        if(validarString.tieneNumeros(propiedad.getUbicacion()) || validarString.tieneNumeros(propiedad.getNombre())){
            throw new CRUDPropiedadExcepcion("Error! Debe completar todos los campos con datos validos.");
        }
        repositorioPropiedad.agregarPropiedad(propiedad);

        try {
            imagenServicio.subirImagenPropiedad(propiedad.getId(), imagen);
        } catch (IOException e) {
            repositorioPropiedad.eliminarPropiedad(propiedad.getId());
            throw new IOException(e.getMessage());
        }
    }


    public List<Propiedad> listarPropiedades() {
        return this.repositorioPropiedad.listarPropiedades();
    }


    public List<Propiedad> listarPropiedadesPendientes() {
        return this.repositorioPropiedad.listarPropiedadesPendientes();
    }


    public List<Propiedad> listarPropiedadesAceptadas() {
        return repositorioPropiedad.listarPropiedadesAceptadas();
    }

    public List<Propiedad> filtrarPropiedades(FiltroPropiedad filtroPropiedad){
        Set<Propiedad> propiedadesFiltradas = new HashSet<>(
                this.repositorioPropiedad.listarPropiedades()
        );

        if(filtroPropiedad.getMinPrecio() != null && filtroPropiedad.getMaxPrecio() != null){
            propiedadesFiltradas.retainAll(this.repositorioPropiedad.listarPorRangoPrecio(filtroPropiedad.getMinPrecio(), filtroPropiedad.getMaxPrecio()));
        }
        if(filtroPropiedad.getEstado() != null){
            propiedadesFiltradas.retainAll(this.repositorioPropiedad.listarPorEstado(filtroPropiedad.getEstado()));
        }
        if(filtroPropiedad.getSuperficie() != null){
            propiedadesFiltradas.retainAll(this.repositorioPropiedad.listarPorSuperficie(filtroPropiedad.getSuperficie()));
        }
        if(filtroPropiedad.getUbicacion() != null){
            propiedadesFiltradas.retainAll(this.repositorioPropiedad.listarPorUbicacion(filtroPropiedad.getUbicacion()));
        }

        return new ArrayList<>(propiedadesFiltradas);
    }

    public List<Propiedad> listarPropiedadesPorPrecio(Double min, Double max){
        if(min >= 0.0 && max >= 0.0){
            return this.repositorioPropiedad.listarPorRangoPrecio(min, max);
        }
        throw new CRUDPropiedadExcepcion("No se ha podido aplicar el filtro de precio correctamente, revise los datos enviados.");
    }


    public List<Propiedad> listarPropiedadesPorUbicacion(String ubicacion){
        if(!ubicacion.isBlank()){
            return this.repositorioPropiedad.listarPorUbicacion(ubicacion);
        }
        throw new CRUDPropiedadExcepcion("No se ha podido aplicar el filtro de ubicacion correctamente, revise los datos enviados.");
    }


    public void aceptarPropiedad(Long idPropiedad) {
        Propiedad propiedad = repositorioPropiedad.buscarPropiedad(idPropiedad);
        if (propiedad != null) {
            propiedad.setAceptada(true);
            repositorioPropiedad.editarPropiedad(propiedad);
        } else {
            throw new CRUDPropiedadExcepcion("La propiedad con ID " + idPropiedad + " no existe.");
        }
    }


    public void rechazarPropiedad(Long idPropiedad) {
        if (idPropiedad != null) {
            repositorioPropiedad.eliminarPropiedad(idPropiedad);
        } else {
            throw new CRUDPropiedadExcepcion("La propiedad no existe.");
        }
    }


    public void modificarPropiedad(Propiedad propiedadEditada) {
        if (propiedadEditada != null) {
            repositorioPropiedad.editarPropiedad(propiedadEditada);
        } else {
            throw new CRUDPropiedadExcepcion("Propiedad inexistente");
        }
    }


}
