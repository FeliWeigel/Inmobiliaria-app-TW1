package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.CRUDPropiedadExcepcion;
import com.tallerwebi.dominio.filtro.FiltroPropiedad;
import com.tallerwebi.dominio.utilidad.ValidarString;
import com.tallerwebi.presentacion.DatosFiltro;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ServicioPropiedad {

    private RepositorioPropiedad repositorioPropiedad;
    private SubirImagenServicio imagenServicio;

    public ServicioPropiedad(RepositorioPropiedad repositorioPropiedad, SubirImagenServicio imagenServicio) {
        this.repositorioPropiedad = repositorioPropiedad;
        this.imagenServicio = imagenServicio;
    }

    // Para probar que funcionen las views, descomentar las lineas comentadas y comentar las que estan encima de ellas.

    public Propiedad buscarPropiedad(Long id) {
        Propiedad propiedad = this.repositorioPropiedad.buscarPropiedad(id);

        if (propiedad != null) {
            return propiedad;
        }
        else throw new CRUDPropiedadExcepcion("La Propiedad Buscada no Existe.");

    }


    public List<Propiedad> listarPropiedades() {
        return this.repositorioPropiedad.listarPropiedades();
    }

    public void agregarPropiedad(Propiedad propiedad, MultipartFile imagen) throws IOException {
        ValidarString validarString = new ValidarString();

        if(validarString.tieneNumeros(propiedad.getUbicacion()) || validarString.tieneNumeros(propiedad.getNombre())){
            throw new CRUDPropiedadExcepcion("Error! Debe completar todos los campos con datos validos.");
        }
        repositorioPropiedad.agregarPropiedad(propiedad);

        try {
            imagenServicio.subirImagen(propiedad.getId(), imagen);
        } catch (IOException e) {
            repositorioPropiedad.eliminarPropiedad(propiedad.getId());
            throw new IOException(e.getMessage());
        }

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


    public List<Propiedad> filtrar(FiltroPropiedad filtro, DatosFiltro datosFiltro) {
        return filtro.filtrar(listarPropiedades(), datosFiltro);
    }

}
