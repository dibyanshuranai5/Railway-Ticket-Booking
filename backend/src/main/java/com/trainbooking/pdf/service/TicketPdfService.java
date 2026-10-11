package com.trainbooking.pdf.service;

import com.trainbooking.ticket.dto.TicketResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

@Service
public class TicketPdfService {
    public byte[] create(TicketResponse ticket) {
        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            PDPage page = new PDPage();
            document.addPage(page);
            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                float y = 740;
                y = line(content, "RAILWAY E-TICKET", 52, y, 22, true);
                y = line(content, "PNR: " + ticket.pnr(), 52, y - 16, 15, true);
                y = line(content, "Booking ID: " + ticket.bookingId(), 52, y - 6, 10, false);
                y = line(content, ticket.trainNumber() + "  " + ticket.trainName(), 52, y - 18, 14, true);
                y = line(content, ticket.fromStation() + "  ->  " + ticket.toStation(), 52, y - 8, 12, false);
                y = line(content, "Journey: " + ticket.journeyDate() + "   Class: " + ticket.classCode(), 52, y - 8, 11, false);
                y = line(content, "Departure: " + ticket.departure() + "   Arrival: " + ticket.arrival(), 52, y - 8, 11, false);
                y = line(content, "Passengers", 52, y - 20, 13, true);
                for (int i = 0; i < ticket.passengers().size(); i++) {
                    var passenger = ticket.passengers().get(i);
                    String seat = passenger.seatNumber() == null ? "Seat assigned by provider" : passenger.seatNumber();
                    y = line(content,
                            (i + 1) + ". " + passenger.name() + "  |  Age " + passenger.age()
                                    + "  |  " + passenger.gender() + "  |  " + seat,
                            52, y - 8, 10, false);
                }
                y = line(content, "Total fare: INR " + ticket.fareINR(), 52, y - 24, 12, true);
                y = line(content, "Payment: " + ticket.paymentStatus() + "   Booking: " + ticket.bookingStatus(), 52, y - 8, 10, false);
                line(content, "Keep this ticket available during your journey.", 52, y - 26, 9, false);
            }
            document.save(output);
            return output.toByteArray();
        } catch (IOException exception) {
            throw new IllegalStateException("Ticket PDF could not be generated.", exception);
        }
    }

    private static float line(
            PDPageContentStream content,
            String text,
            float x,
            float y,
            int size,
            boolean bold) throws IOException {
        content.beginText();
        content.setFont(bold ? PDType1Font.HELVETICA_BOLD : PDType1Font.HELVETICA, size);
        content.newLineAtOffset(x, y);
        content.showText(safeAscii(text));
        content.endText();
        return y;
    }

    private static String safeAscii(String value) {
        return value == null ? "" : value.replaceAll("[^\\x20-\\x7E]", "?");
    }
}
