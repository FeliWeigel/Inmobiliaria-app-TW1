package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.dto.FiltroPropiedadDTO;
import com.tallerwebi.dominio.entidades.Propiedad;
import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.entidades.Visita;
import com.tallerwebi.dominio.excepcion.CRUDPropiedadExcepcion;
import com.tallerwebi.dominio.respositorio.RepositorioHistorial;
import com.tallerwebi.dominio.respositorio.RepositorioPropiedad;
import com.tallerwebi.dominio.utilidad.ValidarString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ServicioHistorial {

    private final RepositorioHistorial repositorioHistorial;

    @Autowired
    @Lazy
    public ServicioHistorial(RepositorioHistorial repositorioHistorial) {
        this.repositorioHistorial = repositorioHistorial;
    }

    public void registrarVisita(Usuario usuario, Propiedad propiedad) {
        if (usuario==null || propiedad==null){
            return;
        }
        Visita visita = new Visita();
        visita.setUsuario(usuario);
        visita.setPropiedad(propiedad);
        visita.setFechaVisita(new Timestamp(System.currentTimeMillis()));
        repositorioHistorial.agregarVisita(visita);
    }

    public List<Visita> obtenerHistorial(Long usuarioId) {
        return repositorioHistorial.buscarPorUsuarioId(usuarioId);
    }
}
