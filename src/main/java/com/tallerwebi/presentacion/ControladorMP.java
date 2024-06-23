package com.tallerwebi.presentacion;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


import com.mercadopago.client.preference.*;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.resources.common.Address;
import com.mercadopago.resources.common.Identification;
import com.mercadopago.resources.common.Phone;
import org.springframework.stereotype.Controller;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.*;
import com.mercadopago.resources.preference.PreferenceItem;
import com.mercadopago.resources.preference.PreferencePayer;
import com.mercadopago.resources.preference.PreferenceBackUrls;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ControladorMP {
    @Value("${mercadopago.access.token}")
    private String accessToken;

    @GetMapping("/create_preference")
    public String createPreference() {
        try {

            // Agrega credenciales
            MercadoPagoConfig.setAccessToken("PROD_ACCESS_TOKEN");

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
//            PreferencePayer payer = new PreferencePayer();
//            payer.setName("João");
//            payer.setSurname("Silva");
//            payer.setEmail("user@email.com");
//            Phone phone = new Phone();
//            phone.setAreaCode("11");
//            phone.setNumber("4444-4444");
//            payer.setPhone(phone);
//            Identification identification = new Identification();
//            identification.setType("CPF");
//            identification.setNumber("19119119100");
//            payer.setIdentification(identification);
//            Address address = new Address();
//            address.setStreetName("Street");
//            address.setStreetNumber(123);
//            address.setZipCode("06233200");
//            payer.setAddress(address);
//
//            PreferenceBackUrls backUrls = new PreferenceBackUrls();
//            backUrls.setSuccess("https://www.success.com");
//            backUrls.setFailure("http://www.failure.com");
//            backUrls.setPending("http://www.pending.com");
            //

            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(items)
                    //
//                    .payer(payer)
//                    .backUrls(backUrls)
//                    .autoReturn("approved")
//                    .paymentMethods(paymentMethods)
//                    .notificationUrl("https://www.your-site.com/ipn")
//                    .statementDescriptor("MEUNEGOCIO")
//                    .externalReference("Reference_1234")
//                    .expires(true)
//                    .expirationDateFrom("2016-02-01T12:00:00.000-04:00")
//                    .expirationDateTo("2016-02-28T12:00:00.000-04:00")
                    //
                    .build();

            PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(preferenceRequest);
            return preference.getInitPoint();




//            Preference preference = new Preference();

//            PreferencePayer payer = new PreferencePayer();
//            payer.setName("João");
//            payer.setSurname("Silva");
//            payer.setEmail("user@email.com");
//            Phone phone = new Phone();
//            phone.setAreaCode("11");
//            phone.setNumber("4444-4444");
//            payer.setPhone(phone);
//            Identification identification = new Identification();
//            identification.setType("CPF");
//            identification.setNumber("19119119100");
//            payer.setIdentification(identification);
//            Address address = new Address();
//            address.setStreetName("Street");
//            address.setStreetNumber(123);
//            address.setZipCode("06233200");
//            payer.setAddress(address);
//
//            PreferenceBackUrls backUrls = new PreferenceBackUrls();
//            backUrls.setSuccess("https://www.success.com");
//            backUrls.setFailure("http://www.failure.com");
//            backUrls.setPending("http://www.pending.com");
//
//            List<PreferencePaymentMethodRequest> excludedPaymentMethods = new ArrayList<>();
//            List<PreferencePaymentMethodRequest> paymentMethods = new ArrayList<>();
//
//            List<PreferencePaymentTypeRequest> excludedPaymentTypes = new ArrayList<>();
//
//
//            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
//                    .items(items)
//                    .payer(payer)
//                    .backUrls(backUrls)
//                    .autoReturn("approved")
//                    .paymentMethods(paymentMethods)
//                    .notificationUrl("https://www.your-site.com/ipn")
//                    .statementDescriptor("MEUNEGOCIO")
//                    .externalReference("Reference_1234")
//                    .expires(true)
//                    .expirationDateFrom("2016-02-01T12:00:00.000-04:00")
//                    .expirationDateTo("2016-02-28T12:00:00.000-04:00")
//                    .build();
//
//
//            return preference.getInitPoint();
//
//        } catch (MPException e) {
//            e.printStackTrace();
//            return "Error creating preference: " + e.getMessage();
//        }
        } catch (MPException | MPApiException e) {
            throw new RuntimeException(e);
        }
        }
    }
