package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Propiedad;
import com.tallerwebi.dominio.ServicioPropiedad;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.infraestructura.ServicioUsuario;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import java.util.Map;


@Controller
public class ControladorUsuario {

    private ServicioUsuario servicioUsuario;

    public ControladorUsuario(ServicioUsuario servicioUsuario) {
        this.servicioUsuario = servicioUsuario;
    }


    @RequestMapping(path = "/favorite", method = RequestMethod.POST)
    public ResponseEntity<String> addToFavorites(@RequestBody String favoriteId) {
        long usuarioId = 1;
        long favoriteIdParsed = Long.parseLong(favoriteId);

        servicioUsuario.agregarFavorito(usuarioId, favoriteIdParsed);
        return ResponseEntity.ok("Added to favorites");

    }

    @RequestMapping(path = "/unfavorite", method = RequestMethod.POST)
    public ResponseEntity<String> removeFromFavorites(@RequestBody String favoriteId) {
        long usuarioId = 1;
        long favoriteIdParsed = Long.parseLong(favoriteId);
        servicioUsuario.eliminarFavorito(usuarioId, favoriteIdParsed);
        return ResponseEntity.ok("Removed from favorites");
    }
}
