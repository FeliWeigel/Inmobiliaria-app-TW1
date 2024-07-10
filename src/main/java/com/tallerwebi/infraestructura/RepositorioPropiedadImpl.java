package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.entidades.Propiedad;
import com.tallerwebi.dominio.excepcion.AlquilerRegistradoException;
import com.tallerwebi.dominio.respositorio.RepositorioPropiedad;
import com.tallerwebi.dominio.excepcion.CRUDPropiedadExcepcion;
import com.tallerwebi.dominio.utilidad.EstadoPropiedad;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service("repositorioPropiedad")
@Transactional
public class RepositorioPropiedadImpl implements RepositorioPropiedad {

    private SessionFactory sessionFactory;

    public RepositorioPropiedadImpl(SessionFactory sessionFactory) {

        this.sessionFactory = sessionFactory;
    }

    @Override
    public Propiedad buscarPropiedad(Long id) {
        final Session session = sessionFactory.getCurrentSession();

        Propiedad propiedadBuscada = (Propiedad) session.createCriteria(Propiedad.class)
                .add(Restrictions.eq("id", id))
                .uniqueResult();

        return propiedadBuscada;
    }

    @Override
    public void agregarPropiedad(Propiedad propiedad) {
        final Session session = sessionFactory.getCurrentSession();
        session.save(propiedad);
    }

    @Override
    public void editarPropiedad(Propiedad propiedadEditada) {
        final Session session = sessionFactory.getCurrentSession();

        if (propiedadEditada.getId() != null) {
            Propiedad propiedadAlmacenada = buscarPropiedad(propiedadEditada.getId());

            if (propiedadAlmacenada != null) {
                propiedadAlmacenada.setNombre(propiedadEditada.getNombre());
                propiedadAlmacenada.setBanios(propiedadEditada.getBanios());
                propiedadAlmacenada.setHabitaciones(propiedadEditada.getHabitaciones());
                propiedadAlmacenada.setEstado(propiedadEditada.getEstado());
                propiedadAlmacenada.setPisos(propiedadEditada.getPisos());
                propiedadAlmacenada.setSuperficie(propiedadEditada.getSuperficie());
                propiedadAlmacenada.setPrecio(propiedadEditada.getPrecio());
                propiedadAlmacenada.setUbicacion(propiedadEditada.getUbicacion());
                propiedadAlmacenada.setAceptada(propiedadEditada.isAceptada());
                propiedadAlmacenada.setRutaImagen(propiedadEditada.getRutaImagen());
                session.saveOrUpdate(propiedadAlmacenada);
            } else {
                throw new CRUDPropiedadExcepcion("La propiedad no existe en la base de datos.");
            }
        } else {
            throw new CRUDPropiedadExcepcion("La propiedad no cuenta con ID definido para su búsqueda en la base de datos.");
        }
    }


    @Override
    public List<Propiedad> listarPorRangoPrecio(Double min, Double max) {
        final Session session = sessionFactory.getCurrentSession(); // devuelvo la session(unidad de comunicacion con la base de datos) actual asociada al contexto de transacciones en el que estoy trabajando
        String query = "FROM Propiedad WHERE precio BETWEEN :min AND :max"; //creo la query que filtra los objetos

        return session.createQuery(query, Propiedad.class) //ejecuto la query y asigno el tipo de dato que espero a la salida
                .setParameter("min", min)// seteo los valores para los parametros de comparacion asignados en la query (":min y :max)
                .setParameter("max", max)
                .getResultList(); //devuelvo la lista con los objetos filtrados
    }


    @Override
    public List<Propiedad> listarPorUbicacion(String ubicacionFiltro) {
        final Session session = sessionFactory.getCurrentSession();
        String query = "FROM Propiedad WHERE LOCATE(LOWER(:ubicacionFiltro), LOWER(ubicacion)) > 0";

        return session.createQuery(query, Propiedad.class)
                .setParameter("ubicacionFiltro",ubicacionFiltro)
                .getResultList();
    }

    @Override
    public List<Propiedad> listarPorEstado(EstadoPropiedad estado) {
        final Session session = sessionFactory.getCurrentSession();
        String query = "FROM Propiedad WHERE estado = :estado";

        return session.createQuery(query, Propiedad.class)
                .setParameter("estado", estado)
                .getResultList();
    }

    @Override
    public List<Propiedad> listarPorSuperficie(Double superficie) {
        final Session session = sessionFactory.getCurrentSession();
        String query = "FROM Propiedad WHERE superficie >= :superficie";

        return session.createQuery(query, Propiedad.class)
                .setParameter("superficie",superficie)
                .getResultList();
    }


    @Override
    public void eliminarPropiedad(Long propiedadId) {
        final Session session = sessionFactory.getCurrentSession();
        Propiedad propiedadAEliminar = buscarPropiedad(propiedadId);

        if (propiedadAEliminar != null) {
            if (!propiedadAEliminar.getAlquileres().isEmpty()) {
                throw new AlquilerRegistradoException("La propiedad no puede eliminarse, tiene fechas de alquileres pendientes.");
            }
            session.delete(propiedadAEliminar);
        } else {
            throw new CRUDPropiedadExcepcion("La propiedad seleccionada para eliminar no existe en la base de datos.");
        }
    }


    @Override
    public void eliminarVisitasPorPropiedadId(Long propiedadId) {
        String hql = "DELETE FROM Visita WHERE propiedadId = :propiedadId";
        sessionFactory.getCurrentSession().createQuery(hql)
                .setParameter("propiedadId", propiedadId)
                .executeUpdate();
    }

    @Override
    public void eliminarCalificacionesPorPropiedadId(Long propiedadId) {
        String hql = "DELETE FROM CalificacionPropiedad WHERE propiedad.id = :propiedadId";
        sessionFactory.getCurrentSession().createQuery(hql)
                .setParameter("propiedadId", propiedadId)
                .executeUpdate();
    }



    @Override
    public List<Propiedad> listarPropiedades() {
        final Session session = sessionFactory.getCurrentSession();
        return session.createQuery("FROM Propiedad").getResultList();
    }

    @Override
    public List<Propiedad> listarPropiedadesAceptadas() {
        final Session session = sessionFactory.getCurrentSession();
        String query = "FROM Propiedad WHERE aceptada = true";
        return session.createQuery(query, Propiedad.class).getResultList();
    }

    @Override
    public List<Propiedad> listarPropiedadesPendientes() {
        final Session session = sessionFactory.getCurrentSession();
        String query = "FROM Propiedad WHERE aceptada = false";
        return session.createQuery(query, Propiedad.class).getResultList();
    }

    @Override
    public List<Propiedad> listarNovedades() {
        final Session session = sessionFactory.getCurrentSession();
        String queryString = "FROM Propiedad WHERE aceptada = true ORDER BY id DESC";
        Query query = session.createQuery(queryString);
        query.setMaxResults(3);
        List<Propiedad> resultado = query.list();
        return resultado;
    }

    @Override
    public List<Propiedad> listarRecomendaciones(Long usuarioId) {
        final Session session = sessionFactory.getCurrentSession();

        // Últimas tres propiedades visitadas por el usuario
        String ultimasVisitas = "SELECT DISTINCT V.propiedad FROM Visita V WHERE V.usuario.id = :usuarioId ORDER BY V.fechaVisita DESC";
        List<Propiedad> ultimasPropiedadesVisitadas = session.createQuery(ultimasVisitas, Propiedad.class)
                .setParameter("usuarioId", usuarioId)
                .setMaxResults(3)
                .getResultList();

        // LinkedHashMap para evitar duplicados y mantener el orden de inserción
        Map<Long, Propiedad> recomendacionesMap = new LinkedHashMap<>();

        // Obtener precios y ubicaciones de historial
        for (Propiedad propiedadVisitada : ultimasPropiedadesVisitadas) {
            String ubicacion = propiedadVisitada.getUbicacion();
            Double precio = propiedadVisitada.getPrecio();
            Double precioMin = precio * 0.5;
            Double precioMax = precio * 1.5;

            // Encontrar propiedades similares
            String similares = "SELECT P FROM Propiedad P WHERE P.ubicacion = :ubicacion AND P.precio BETWEEN :precioMin AND :precioMax AND P.id != :propiedadId";
            List<Propiedad> propiedadesSimilares = session.createQuery(similares, Propiedad.class)
                    .setParameter("ubicacion", ubicacion)
                    .setParameter("precioMin", precioMin)
                    .setParameter("precioMax", precioMax)
                    .setParameter("propiedadId", propiedadVisitada.getId())
                    .getResultList();

            // Agregar propiedades similares al mapa
            for (Propiedad propiedad : propiedadesSimilares) {
                recomendacionesMap.put(propiedad.getId(), propiedad);
                if (recomendacionesMap.size() >= 3) {
                    break;
                }
            }
            
            // Detener si ya tenemos tres recomendaciones
            if (recomendacionesMap.size() >= 3) {
                break;
            }
        }
        return new ArrayList<>(recomendacionesMap.values());
    }







}
