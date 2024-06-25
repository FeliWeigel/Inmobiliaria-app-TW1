package com.tallerwebi.presentacion;


import com.tallerwebi.dominio.dto.DatosLoginDTO;
import com.tallerwebi.dominio.servicio.ServicioLogin;
import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.excepcion.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class ControladorLogin {

    private ServicioLogin servicioLogin;

    @Autowired
    public ControladorLogin(ServicioLogin servicioLogin){
        this.servicioLogin = servicioLogin;
    }

    @RequestMapping("/login")
    public ModelAndView irALogin() {
        ModelMap modelo = new ModelMap();
        modelo.put("datosLogin", new DatosLoginDTO());
        return new ModelAndView("login", modelo);
    }

    @RequestMapping("/registrarme")
    public ModelAndView irARegistrarme() {
        ModelMap modelo = new ModelMap();
        modelo.put("usuario", new Usuario());
        return new ModelAndView("registrarme", modelo);
    }

    @RequestMapping(path = "/logout", method = RequestMethod.POST)
    public ModelAndView logout(HttpSession session) {
        ModelMap modelo = new ModelMap();
        Usuario usuarioAutenticado = (Usuario) session.getAttribute("usuario");

        try{
            servicioLogin.cerrarSesion(usuarioAutenticado);
            session.invalidate();
        }catch (UsuarioNoIdentificadoExcepcion e) {
            modelo.put("error", "La sesion no se cerro correctamente, el usuario no ha podido ser identificado.");
            return new ModelAndView("perfil", modelo);
        }

        return new ModelAndView("redirect:/login");
    }

    @RequestMapping(path = "/validar-login", method = RequestMethod.POST)
    public ModelAndView validarLogin(@ModelAttribute("datosLogin") DatosLoginDTO datosLogin, HttpServletRequest request) {
        ModelMap model = new ModelMap();

        Usuario usuarioBuscado = servicioLogin.consultarUsuario(datosLogin.getEmail(), datosLogin.getPassword());
        if (usuarioBuscado != null) {
            if (usuarioBuscado.getActivo()) {
                request.getSession().setAttribute("usuario", usuarioBuscado);
                request.getSession().setAttribute("ROL", usuarioBuscado.getRol());
                return new ModelAndView("redirect:/home");
            } else {
                model.put("error", "El usuario se encuentra bloqueado");
            }
        } else {
            model.put("error", "Usuario o clave incorrecta");
        }
        return new ModelAndView("login", model);
    }


    @RequestMapping(path = "/registrarse", method = RequestMethod.POST)
    public ModelAndView registrarse(@ModelAttribute("usuario") Usuario usuario) {
        ModelMap model = new ModelMap();
        try{
            servicioLogin.registrar(usuario);
        } catch (UsuarioExistenteExcepcion e){
            model.put("error", "El email ingresado esta asociado a una cuenta existente!");
            return new ModelAndView("registrarme", model);
        }catch(CredencialesInvalidasExcepcion e){
            model.put("error", "Debe completar todos los campos con datos validos!");
            return new ModelAndView("registrarme", model);
        }catch(EdadInvalidaExcepcion e){
            model.put("error", "Cuidado! Debe ser mayor de 18 años para registrarse.");
            return new ModelAndView("registrarme", model);
        }catch(PasswordInvalidaExcepcion e){
            model.put("error", "Error! La contraseña debe contener al menos: 6 digitos, una mayuscula, un numero y un caracter especial.");
            return new ModelAndView("registrarme", model);
        } catch (Exception e){
            model.put("error", "Error al registrar el nuevo usuario");
            return new ModelAndView("registrarme", model);
        }

        return new ModelAndView("redirect:/login");
    }

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public ModelAndView inicio() {
        return new ModelAndView("redirect:/login");
    }
}

