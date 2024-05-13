package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Propiedad;
import com.tallerwebi.dominio.ServicioPropiedad;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.infraestructura.ServicioUsuario;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ControladorUsuario {

    private ServicioUsuario servicioUsuario;



    @RequestMapping(path = "/favorite", method = RequestMethod.POST)
    public ResponseEntity<String> addToFavorites(@RequestBody String favorite) {
        Usuario usuario = null;
        Propiedad propiedad = null;
        servicioUsuario.agregarFavorito(usuario, propiedad);
        return ResponseEntity.ok("Added to favorites");
    }

    @RequestMapping(path = "/unfavorite", method = RequestMethod.POST)
    public ResponseEntity<String> removeFromFavorites(@RequestBody String favorite) {
        Usuario usuario = null;
        Propiedad propiedad = null;
        servicioUsuario.eliminarFavorito(usuario, propiedad);
        return ResponseEntity.ok("Removed from favorites");
    }
}
