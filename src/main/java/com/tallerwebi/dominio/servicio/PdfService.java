package com.tallerwebi.dominio.servicio;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

@Service
public class PdfService {

    public ByteArrayInputStream generateInvoice(Double precioReserva) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
            contentStream.newLineAtOffset(50, 750);
            contentStream.showText("Factura de Pago");
            contentStream.endText();

            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 14);
            contentStream.newLineAtOffset(50, 700);
            contentStream.showText("Valor de la reserva: $" + precioReserva);
            contentStream.endText();

            // Genera un código de barras ficticio con barras de anchuras aleatorias
            float barcodeX = 50;
            float barcodeY = 600;
            float barcodeHeight = 40;
            int numberOfBars = 150;
            Random random = new Random();

            for (int i = 0; i < numberOfBars; i++) {
                float barcodeWidth = 1 + random.nextFloat() * 4; // Genera un ancho aleatorio entre 1 y 5
                // Dibuja barras alternas
                if (i % 2 == 0) {
                    contentStream.addRect(barcodeX, barcodeY, barcodeWidth, barcodeHeight);
                    contentStream.fill();
                }
                barcodeX += barcodeWidth; // Incrementa la posición x para la siguiente barra
            }

            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.newLineAtOffset(50, 500);
            contentStream.showText("¿Cómo pagar?");
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("1. Dírigete a una sucursal de Pago Fácil");
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("2. En la caja, mostrá el codigo de barras presente en este ticket para que sea escaneado");
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("¡Listo! El pago se acreditará pasadas las 24hs");
            contentStream.newLineAtOffset(0, -40);
            contentStream.showText("Importante:");
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("El código de barras tiene una validez de 48hs.");
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Pasado ese tiempo deberás generar una nueva factura desde la web.");
            contentStream.endText();

        }

        document.save(out);
        document.close();

        return new ByteArrayInputStream(out.toByteArray());
    }
}
