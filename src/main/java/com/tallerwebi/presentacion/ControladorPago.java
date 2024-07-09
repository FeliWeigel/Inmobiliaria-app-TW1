package com.tallerwebi.presentacion;
import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.time.OffsetDateTime;

import com.mercadopago.client.preference.*;
import com.mercadopago.exceptions.MPApiException;
import com.tallerwebi.dominio.entidades.Propiedad;
import com.tallerwebi.dominio.entidades.Usuario;
import com.tallerwebi.dominio.servicio.EmailServiceImpl;
import com.tallerwebi.dominio.servicio.ServicioPropiedad;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.*;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

@Controller
public class ControladorPago {

    private final EmailServiceImpl emailService;
    private final ServicioPropiedad servicioPropiedad;

    public ControladorPago(EmailServiceImpl emailService, ServicioPropiedad servicioPropiedad){
        this.emailService = emailService;
        this.servicioPropiedad = servicioPropiedad;
    }

    @PostMapping(path = "/propiedad/{id}/nueva-operacion/pago")
    public ModelAndView pagoReserva(@PathVariable Long id, HttpSession session){
        ModelMap model = new ModelMap();
        Usuario usuarioAutenticado = (Usuario) session.getAttribute("usuario");

        if(usuarioAutenticado == null){
            return new ModelAndView("redirect:/login");
        }

        if(usuarioAutenticado.getEmail() == null){
            model.put("error", "No se ha podido efectuar la reserva. Intentelo nuevamente mas tarde.");
        }

        emailService.sendSimpleMessage(
                usuarioAutenticado.getEmail(),
                "OpenDoor. Peticion de reserva de propiedad",
                "Hola " + usuarioAutenticado.getNombre() + "! Hemos recibido tu peticion de reserva. A continuacion, se detallan los pasos a seguir: \n " +
                        "1. Elige un metodo de pago y abona la reserva correspondiente \n" +
                        "2. Espera a ser contactado por el vendedor para definir los detalles contractuales \n " +
                        "3. Cierra el trato directamente con el vendedor y espera a que active la confirmacion de compra/alquiler \n" +
                        "Eso es todo! En caso de que algo no salga bien, la reserva sera desestimada y el dinero abonado devuelto via transferencia bancaria. \n" +
                        "Atte. Open Doors.");

        Propiedad propiedad = servicioPropiedad.buscarPropiedad(id);
        Usuario propietario = propiedad.getPropietario();

        if(propietario != null && propietario.getEmail() != null){
            emailService.sendSimpleMessage(
                    propietario.getEmail(),
                    "OpenDoor. Nueva peticion de reserva para su propiedad",
                    "Hola " + propietario.getNombre() + "! Ha recibido una nueva peticion de reserva para su propiedad. A continuacion, se detallan los pasos a seguir: \n " +
                            "1. Espere a que el comprador abone la reserva correspondiente \n" +
                            "2. Contacte al comprador para definir los detalles contractuales \n " +
                            "3. Active la confirmacion de compra/alquiler en la plataforma una vez que el trato haya sido cerrado \n" +
                            "Atte. Open Doors.");
        }

        model.put("success", "Peticion de reserva creada correctamente! Se ha enviado un email a su correo detallando los pasos a seguir.");
        model.put("propiedad", propiedad);

        return new ModelAndView("pago", model);
    }


    @RequestMapping(path = "/create_preference", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Preference> createPreference() {

        try {
            MercadoPagoConfig.setAccessToken("APP_USR-2054445966320899-062320-264f039524895893770ee6d1ea2233ec-1871978866");

            PreferenceItemRequest itemRequest =
                    PreferenceItemRequest.builder()
                            .id(String.valueOf(Math.random()))
                            .title("Seña alquiler")
                            .description("Seña alquiler propiedad publicada en Open Doors")
                            .pictureUrl("https://google.com")
                            .categoryId("Reserva de propiedad")
                            .quantity(1)
                            .currencyId("AR")
                            .unitPrice(BigDecimal.valueOf(12000))
                            .build();
            List<PreferenceItemRequest> items = new ArrayList<>();
            items.add(itemRequest);

            PreferenceBackUrlsRequest backUrls =
                    PreferenceBackUrlsRequest.builder()
                            .success("https://www.success.com")
                            .failure("http://www.failure.com")
                            .pending("http://www.pending.com")
                            .build();

            OffsetDateTime offsetDateTimeFrom = OffsetDateTime.of(2016, 2, 1, 12, 0, 0, 0, ZoneOffset.of("-04:00"));
            OffsetDateTime offsetDateTimeTo = OffsetDateTime.of(2016, 2, 1, 12, 0, 0, 0, ZoneOffset.of("-04:00"));



            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(items)
                    //
//                    .payer(payer)
                    .backUrls(backUrls)
                    .autoReturn("approved")
                    .notificationUrl("https://www.your-site.com/ipn")
                    .statementDescriptor("MEUNEGOCIO")
                    .externalReference("Reference_1234")
//                    .expires(true)
//                    .expirationDateFrom(offsetDateTimeFrom)
//                    .expirationDateTo(offsetDateTimeTo)
                    .build();

            PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(preferenceRequest);

//            return preference;
//            return preference.getInitPoint();
            return ResponseEntity.ok(preference);

        } catch (MPException | MPApiException e) {
            throw new RuntimeException(e);
        }
    }
}
