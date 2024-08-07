package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.dto.FiltroPropiedadDTO;
import com.tallerwebi.dominio.entidades.Propiedad;
import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.excepcion.AlquilerRegistradoException;
import com.tallerwebi.dominio.excepcion.CRUDPropiedadExcepcion;
import com.tallerwebi.dominio.respositorio.RepositorioPropiedad;
import com.tallerwebi.dominio.utilidad.ValidarString;
import com.tallerwebi.infraestructura.RepositorioHistorialImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    public void agregarPropiedad(Propiedad propiedad, MultipartFile imagen, Usuario usuario) throws IOException {
        ValidarString validarString = new ValidarString();

        if(validarString.tieneNumeros(propiedad.getUbicacion()) || validarString.tieneNumeros(propiedad.getNombre())){
            throw new CRUDPropiedadExcepcion("Error! Debe completar todos los campos con datos válidos.");
        }

        propiedad.setPropietario(usuario);

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

    public List<Propiedad> filtrarPropiedades(FiltroPropiedadDTO filtroPropiedad){
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

    public void aceptarPropiedad(Long idPropiedad) {
        Propiedad propiedad = repositorioPropiedad.buscarPropiedad(idPropiedad);
        if (propiedad != null) {
            propiedad.setAceptada(true);
            repositorioPropiedad.editarPropiedad(propiedad);
        } else {
            throw new CRUDPropiedadExcepcion("La propiedad con ID " + idPropiedad + " no existe.");
        }
    }


    @Transactional
    public void rechazarPropiedad(Long idPropiedad) {
        if (idPropiedad != null) {
            try {
                repositorioPropiedad.eliminarVisitasPorPropiedadId(idPropiedad);

                repositorioPropiedad.eliminarCalificacionesPorPropiedadId(idPropiedad);

                repositorioPropiedad.eliminarPropiedad(idPropiedad);
            } catch (AlquilerRegistradoException e) {
                throw e;
            }
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

    public List<Propiedad> listarNovedades(){
        return repositorioPropiedad.listarNovedades();
    }


    public List<Propiedad> listarRecomendaciones(Long id) {
        return repositorioPropiedad.listarRecomendaciones(id);
    }

    public List<Propiedad> listarMasVisitadas(){
        return repositorioPropiedad.listarPropiedadesMasVisitadas();
    }
}
