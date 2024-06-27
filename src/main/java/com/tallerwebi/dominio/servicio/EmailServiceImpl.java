package com.tallerwebi.dominio.servicio;

import com.tallerwebi.dominio.utilidad.EmailConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

// Clase principal del servcio de email, asigna el emisor, receptor, asunto y mensaje a enviar)
@Component
public class EmailServiceImpl extends EmailConfig {

    @Autowired
    private JavaMailSender emailSender;

    public void sendSimpleMessage(String to, String subject, String text) {

        SimpleMailMessage message = new SimpleMailMessage();

        /* Este mail actuara como emisor de todos los mensajes de forma predeterminada, pero se lo puede agregar como parametro
        de la funcion para que sea un diferente emisor por mensaje enviado */
        message.setFrom("juegopw2@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }

}
