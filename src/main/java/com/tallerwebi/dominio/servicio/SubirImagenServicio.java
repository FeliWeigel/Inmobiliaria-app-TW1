package com.tallerwebi.dominio.servicio;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface SubirImagenServicio {
    String subirImagenPropiedad(Long propiedadId, MultipartFile imagen) throws IOException;
    String subirImagenUsuario(Long usuarioId, MultipartFile imagen) throws IOException;
}
