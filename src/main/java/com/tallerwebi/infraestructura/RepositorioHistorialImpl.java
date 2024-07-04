package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.entidades.AlquilerPropiedad;
import com.tallerwebi.dominio.entidades.Propiedad;
import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.entidades.Visita;
import com.tallerwebi.dominio.excepcion.*;
import com.tallerwebi.dominio.respositorio.RepositorioHistorial;
import com.tallerwebi.dominio.respositorio.RepositorioPropiedad;
import com.tallerwebi.dominio.respositorio.RepositorioUsuario;
import com.tallerwebi.dominio.utilidad.ValidarString;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Repository("RepositorioHistorial")
@Transactional
public class RepositorioHistorialImpl implements RepositorioHistorial {
    private final SessionFactory sessionFactory;
    private final RepositorioHistorial repositorioHistorial;

    @Autowired
    @Lazy
    public RepositorioHistorialImpl(SessionFactory sessionFactory, RepositorioHistorial repositorioHistorial){
        this.sessionFactory = sessionFactory;
        this.repositorioHistorial = repositorioHistorial;
    }

    @Override
    public List<Visita> buscarPorUsuarioId(Long id) {
        final Session session = sessionFactory.getCurrentSession();
        String query = "FROM Visita WHERE usuarioId = :id ORDER BY fechaVisita DESC";
        return session.createQuery(query, Visita.class)
                .setParameter("id", id)
                .getResultList();
    }

    @Override
    public void agregarVisita(Visita visita) {
        sessionFactory.getCurrentSession().save(visita);
    }
}
