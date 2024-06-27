package com.tallerwebi.dominio;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.servicio.EmailServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.reflect.Field;

public class ServicioEmailTest {

    private JavaMailSender emailSender;
    private EmailServiceImpl emailService;

    @BeforeEach
    public void setUp() throws Exception {
        emailSender = mock(JavaMailSender.class);
        emailService = new EmailServiceImpl();

        Field emailSenderField = EmailServiceImpl.class.getDeclaredField("emailSender");
        emailSenderField.setAccessible(true);
        emailSenderField.set(emailService, emailSender);
    }

    @Test
    public void testSendSimpleMessage() {
        String to = "mail@falso.com";
        String subject = "Test";
        String text = "Test";

        emailService.sendSimpleMessage(to, subject, text);
        verify(emailSender).send(any(SimpleMailMessage.class));
    }
}

