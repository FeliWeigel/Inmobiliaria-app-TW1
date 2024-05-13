package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.SubirImagenServicio;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Service
public class SubirImagenServicioImpl implements SubirImagenServicio {

    private final String CARPETA = "C:\\proyecto TW1\\imagenes\\";

    @Override
    public String subirImagen(Long propiedadId, MultipartFile imagen) throws IOException {
        // Verificar si el archivo está vacío
        if (imagen.isEmpty()) {
            throw new IOException("Error! No se ha proporcionado una imagen de la propiedad.");
        }

        Path carpetaDestino = Paths.get(CARPETA + propiedadId);
        if (!Files.exists(carpetaDestino)) {
            try {
                Files.createDirectories(carpetaDestino);
            } catch (IOException e) {
                throw new IOException("Error al crear la carpeta de destino para la propiedad: " + propiedadId);
            }
        }

        try {
            // Obtener el nombre original del archivo
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(imagen.getOriginalFilename()));
            // Construir la ruta completa del archivo
            Path path = carpetaDestino.resolve(fileName);
            // Copiar el contenido del archivo al directorio de destino
            Files.copy(imagen.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            // Devolver la ruta del archivo guardado
            return path.toString();
        } catch (IOException e) {
            throw new IOException("Error al subir archivo de la imagen.");
        }
    }
}


