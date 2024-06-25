package com.tallerwebi.presentacion;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java. time. OffsetDateTime;


import com.mercadopago.client.common.AddressRequest;
import com.mercadopago.client.common.IdentificationRequest;
import com.mercadopago.client.common.PhoneRequest;
import com.mercadopago.client.preference.*;
import com.mercadopago.exceptions.MPApiException;
import org.springframework.stereotype.Controller;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ControladorMP {
    @Value("${mercadopago.access.token}")
    private String accessToken;

//    @RequestMapping(value = "/create_preference", method = RequestMethod.POST)
    @GetMapping("/create_preference")
    @ResponseBody
    public String createPreference() {
//    public ResponseEntity<Preference> createPreference() {
        try {

            // Agrega credenciales
            MercadoPagoConfig.setAccessToken("APP_USR-2054445966320899-062320-264f039524895893770ee6d1ea2233ec-1871978866");

            PreferenceItemRequest itemRequest =
                    PreferenceItemRequest.builder()
                            .id("1234")
                            .title("Games")
                            .description("PS5")
                            .pictureUrl("http://picture.com/PS5")
                            .categoryId("games")
                            .quantity(2)
                            .currencyId("BRL")
                            .unitPrice(new BigDecimal("4000"))
                            .build();
            List<PreferenceItemRequest> items = new ArrayList<>();
            items.add(itemRequest);

            //
//            PhoneRequest phone =
//                    PhoneRequest.builder()
//                            .areaCode("11")
//                            .number("4444-4444")
//                            .build();
//            IdentificationRequest identification =
//                    IdentificationRequest.builder()
//                            .type("CPF")
//                            .number("19119119100")
//                            .build();
//            AddressRequest address =
//                    AddressRequest.builder()
//                            .streetName("Street")
//                            .streetNumber("123")
//                            .zipCode("06233200")
//                            .build();
//            PreferencePayerRequest payer =
//                    PreferencePayerRequest.builder()
//                            .name("João")
//                            .surname("Silva")
//                            .email("user@email.com")
//                            .phone(phone)
//                            .identification(identification)
//                            .address(address)
//                            .build();


            PreferenceBackUrlsRequest backUrls =
                    PreferenceBackUrlsRequest.builder()
                            .success("https://www.success.com")
                            .failure("http://www.failure.com")
                            .pending("http://www.pending.com")
                            .build();

            //
            String expirationDateFrom = "2016-02-01T12:00:00.000-04:00";
            // Parse the date-time string to OffsetDateTime
            OffsetDateTime offsetDateTimeFrom = OffsetDateTime.parse(expirationDateFrom, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            String expirationDateTo = "2016-02-01T12:00:00.000-04:00";
            // Parse the date-time string to OffsetDateTime
            OffsetDateTime offsetDateTimeTo = OffsetDateTime.parse(expirationDateFrom, DateTimeFormatter.ISO_OFFSET_DATE_TIME);

            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(items)
                    //
//                    .payer(payer)
                    .backUrls(backUrls)
                    .autoReturn("approved")
                    .notificationUrl("https://www.your-site.com/ipn")
                    .statementDescriptor("MEUNEGOCIO")
                    .externalReference("Reference_1234")
                    .expires(true)
                    .expirationDateFrom(offsetDateTimeFrom)
                    .expirationDateTo(offsetDateTimeTo)
                    .build();

            PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(preferenceRequest);
//            return preference;
            return preference.getInitPoint();
//            return ResponseEntity.ok(preference);

        } catch (MPException | MPApiException e) {
            throw new RuntimeException(e);
        }
        }
    }
