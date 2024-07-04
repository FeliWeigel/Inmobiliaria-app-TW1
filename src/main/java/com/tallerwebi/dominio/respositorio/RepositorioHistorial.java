package com.tallerwebi.dominio.respositorio;

import com.tallerwebi.dominio.dto.FechasAlquilerDTO;
import com.tallerwebi.dominio.entidades.AlquilerPropiedad;
import com.tallerwebi.dominio.entidades.Propiedad;
import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.entidades.Visita;

import java.util.List;

public interface RepositorioHistorial {
    List <Visita> buscarPorUsuarioId(Long usuarioId);
    void agregarVisita(Visita visita);
}
