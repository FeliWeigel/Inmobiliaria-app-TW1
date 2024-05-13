package com.tallerwebi.dominio;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

public interface SubirImagenServicio {
    String subirImagen(Long propiedadId, MultipartFile imagen) throws IOException;
}
