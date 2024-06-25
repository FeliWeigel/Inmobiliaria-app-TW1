package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.entidades.AlquilerPropiedad;
import com.tallerwebi.dominio.dto.FechasAlquilerDTO;
import com.tallerwebi.dominio.entidades.Propiedad;
import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.excepcion.AlquilerDenegadoExcepcion;
import com.tallerwebi.dominio.excepcion.CRUDPropiedadExcepcion;
import com.tallerwebi.dominio.excepcion.UsuarioNoIdentificadoExcepcion;
import com.tallerwebi.dominio.respositorio.RepositorioAlquiler;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;

@Service
public class ServicioAlquiler {

    private final RepositorioAlquiler repositorioAlquiler;
    private final ServicioPropiedad servicioPropiedad;


    public ServicioAlquiler(RepositorioAlquiler repositorioAlquiler, ServicioPropiedad servicioPropiedad) {
        this.repositorioAlquiler = repositorioAlquiler;
        this.servicioPropiedad = servicioPropiedad;
    }

    public void agregarNuevoAlquiler(Usuario usuario, Long propiedadId, Date fechaInicio, Date fechaFin) throws  UsuarioNoIdentificadoExcepcion {
        if(usuario == null){
            throw new UsuarioNoIdentificadoExcepcion();
        }
        if(propiedadId == null){
            throw new CRUDPropiedadExcepcion("El id de la propiedad es nulo!");
        }
        if(fechaInicio.after(fechaFin)){
            throw new AlquilerDenegadoExcepcion("La fecha de inicio del contrato no puede ser posterior a la fecha de finalizacion del alquiler.");
        }

        Propiedad propiedadAlmacenada = servicioPropiedad.buscarPropiedad(propiedadId);
        if(propiedadAlmacenada == null){
            throw new CRUDPropiedadExcepcion("La propiedad no ha sido encontrada.");
        }

        List<AlquilerPropiedad> alquileresExistentes = repositorioAlquiler.getAlquileresByPropiedad(propiedadId);
        for(AlquilerPropiedad alquiler : alquileresExistentes){
            if(
                fechaInicio.equals(alquiler.getFechaInicio()) || fechaFin.equals(alquiler.getFechaFin())
                    || (fechaInicio.after(alquiler.getFechaInicio()) && fechaInicio.before(alquiler.getFechaFin()))
                        || (fechaFin.after(alquiler.getFechaInicio()) && fechaFin.before(alquiler.getFechaFin()))
            ){
                throw new AlquilerDenegadoExcepcion("Las fechas seleccionadas ya estan reservadas. Intentelo nuevamente.");
            }
        }
        AlquilerPropiedad nuevoAlquiler = new AlquilerPropiedad();
        nuevoAlquiler.setPropiedad(propiedadAlmacenada);
        nuevoAlquiler.setUsuario(usuario);
        nuevoAlquiler.setFechaInicio(fechaInicio);
        nuevoAlquiler.setFechaFin(fechaFin);

        repositorioAlquiler.nuevoAlquiler(nuevoAlquiler);
        usuario.getAlquileres().add(nuevoAlquiler);
        propiedadAlmacenada.getAlquileres().add(nuevoAlquiler);
    }

    public List<AlquilerPropiedad> listarAlquileresPorPropiedad(Long propiedadId){
        return repositorioAlquiler.getAlquileresByPropiedad(propiedadId);
    }

    public List<FechasAlquilerDTO> fechasReservadasPorPropiedad(Long propiedadId){
        if(propiedadId == null){
            throw new CRUDPropiedadExcepcion("El id de la propiedad es nulo!");
        }

        return repositorioAlquiler.getFechasByPropiedad(propiedadId);
    }
}
