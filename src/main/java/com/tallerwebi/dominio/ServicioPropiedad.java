package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.CRUDPropiedadExcepcion;
import com.tallerwebi.dominio.utilidad.ValidarString;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

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
            imagenServicio.subirImagen(propiedad.getId(), imagen);
        } catch (IOException e) {
            repositorioPropiedad.eliminarPropiedad(propiedad.getId());
            throw new IOException(e.getMessage());
        }
    }


    public List<Propiedad> listarPropiedades() {

        return this.repositorioPropiedad.listarPropiedades();
        //return propiedadesFalsas();
    }
}
