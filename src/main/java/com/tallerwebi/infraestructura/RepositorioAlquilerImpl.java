package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.entidades.AlquilerPropiedad;
import com.tallerwebi.dominio.dto.FechasAlquilerDTO;
import com.tallerwebi.dominio.respositorio.RepositorioAlquiler;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Repository("RepositorioAlquiler")
@Transactional
public class RepositorioAlquilerImpl implements RepositorioAlquiler {

    private final SessionFactory sessionFactory;

    public RepositorioAlquilerImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    @Override
    public void nuevoAlquiler(AlquilerPropiedad alquiler) {
        final Session session = sessionFactory.getCurrentSession();
        session.save(alquiler);
    }

    @Override
    public List<AlquilerPropiedad> getAlquileresByPropiedad(Long propiedadId) {
        final Session session = sessionFactory.getCurrentSession();
        String query = "FROM AlquilerPropiedad WHERE propiedad.id = :propiedadId";
        return session.createQuery(query, AlquilerPropiedad.class)
                .setParameter("propiedadId", propiedadId)
                .getResultList();
    }

    @Override
    public List<AlquilerPropiedad> getAlquileresByUsuario(Long usuarioId) {
        final Session session = sessionFactory.getCurrentSession();
        String query = "FROM AlquilerPropiedad WHERE usuario = :usuarioId";
        return session.createQuery(query, AlquilerPropiedad.class)
                .setParameter("usuarioId", usuarioId)
                .getResultList();
    }

    @Override
    public List<FechasAlquilerDTO> getFechasByPropiedad(Long propiedadId) {
        final Session session = sessionFactory.getCurrentSession();
        List<AlquilerPropiedad> alquileres = this.getAlquileresByPropiedad(propiedadId);
        List<FechasAlquilerDTO> fechasAlquileres = new ArrayList<>();

        for (AlquilerPropiedad alquiler : alquileres){
            FechasAlquilerDTO nuevaFecha = new FechasAlquilerDTO();
            nuevaFecha.setFechaInicio(alquiler.getFechaInicio());
            nuevaFecha.setFechaFin(alquiler.getFechaFin());
            fechasAlquileres.add(nuevaFecha);
        }

        return fechasAlquileres;
    }
}
