package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.servicio.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Controller
public class ControladorPdf {

    @Autowired
    private PdfService pdfService;

    @GetMapping("/generate_invoice")
    public ResponseEntity<InputStreamResource> generateInvoice(@RequestParam("precioReserva") Double precioReserva) {
        try {
            ByteArrayInputStream bis = pdfService.generateInvoice(precioReserva);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=factura.pdf");

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(new InputStreamResource(bis));
        } catch (IOException e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }
}
