package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.entidades.Propiedad;
import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.excepcion.CredencialesInvalidasExcepcion;
import com.tallerwebi.dominio.excepcion.PasswordInvalidaExcepcion;
import com.tallerwebi.dominio.excepcion.UsuarioExistenteExcepcion;
import com.tallerwebi.dominio.respositorio.RepositorioPropiedad;
import com.tallerwebi.dominio.respositorio.RepositorioUsuario;
import com.tallerwebi.dominio.servicio.SubirImagenServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Service
public class SubirImagenServicioImpl implements SubirImagenServicio {

    @Autowired
    private ServletContext servletContext;
    private final String CARPETA_PROPIEDADES = "src/main/webapp/resources/core/img/propiedades";
    private final String CARPETA_USUARIOS = "src/main/webapp/resources/core/img/usuarios";
    private final RepositorioPropiedad repositorioPropiedad;
    private final RepositorioUsuario repositorioUsuario;

    public SubirImagenServicioImpl(RepositorioPropiedad repositorioPropiedad, RepositorioUsuario repositorioUsuario) {
        this.repositorioPropiedad = repositorioPropiedad;
        this.repositorioUsuario = repositorioUsuario;
    }


    @Override
    public String subirImagenPropiedad(Long propiedadId, MultipartFile imagen) throws IOException {
        Propiedad propiedad = repositorioPropiedad.buscarPropiedad(propiedadId);

        if(propiedad == null){
            throw new IOException("Error! La propiedad no existe.");
        }
        if (imagen.isEmpty()) {
            throw new IOException("Error! No se ha proporcionado una imagen de la propiedad.");
        }

        Path carpetaDestino = Paths.get(CARPETA_PROPIEDADES, String.valueOf(propiedadId));
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

            propiedad.setRutaImagen(fileName);
            repositorioPropiedad.editarPropiedad(propiedad);
            // Devolver la ruta del archivo guardado
            return path.toString();
        } catch (IOException e) {
            throw new IOException("Error al subir archivo de la imagen.");
        }
    }

    @Override
    public String subirImagenUsuario(Long usuarioId, MultipartFile imagen) throws IOException {
        Usuario usuario = repositorioUsuario.buscarPorId(usuarioId);

        if(usuario == null){
            throw new IOException("Error! El usuario no existe.");
        }
        if (imagen.isEmpty()) {
            throw new IOException("Error! No se ha proporcionado una imagen de perfil.");
        }

        Path carpetaDestino = Paths.get(CARPETA_USUARIOS, String.valueOf(usuarioId));
        if (!Files.exists(carpetaDestino)) {
            try {
                Files.createDirectories(carpetaDestino);
            } catch (IOException e) {
                throw new IOException("Error al crear la carpeta de destino para el usuario: " + usuarioId);
            }
        }
        if (usuario.getFotoPerfil() != null) {
            Path imagenAlmacenadaPath = carpetaDestino.resolve(usuario.getFotoPerfil());
            try {
                Files.deleteIfExists(imagenAlmacenadaPath);
            } catch (IOException e) {
                throw new IOException("Error al eliminar la imagen anterior: " + imagenAlmacenadaPath.toString());
            }
        }

        try {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(imagen.getOriginalFilename()));
            Path path = carpetaDestino.resolve(fileName);
            Files.copy(imagen.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            usuario.setFotoPerfil(fileName);
            repositorioUsuario.editarPerfil(usuario);

            return path.toString();
        } catch (IOException | CredencialesInvalidasExcepcion | PasswordInvalidaExcepcion | UsuarioExistenteExcepcion e) {
            throw new IOException("Error al subir archivo de la imagen.");
        }
    }
}
