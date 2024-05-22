package com.shopme.admin.user;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.shopme.commen.entity.User;
import jakarta.servlet.http.HttpServletResponse;

import java.awt.*;
import java.io.IOException;
import java.util.List;

public class UserPdfExporter extends AbstractExporter {

    public void export(List<User> userList, HttpServletResponse response) throws IOException {
        super.setResponseHeader(response, "application/pdf", ".pdf");

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();
        writePdfHeader(document);
        writePdfTable(document, userList);
        document.close();
    }

    private void writePdfHeader(Document document) {
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font.setColor(Color.GREEN);
        font.setSize(18);

        Paragraph paragraph = new Paragraph("List of Users", font);
        paragraph.setAlignment(Paragraph.ALIGN_CENTER);

        document.add(paragraph);
    }

    private void writePdfTable(Document document, List<User> userList) {
        var table = new PdfPTable(6);
        table.setWidthPercentage(100f);
        table.setSpacingBefore(10);
        table.setWidths(new float[]{1.0f, 3.8f, 3.0f, 3.0f, 3.0f, 1.6f});

        writeTableHeader(table);
        writeTableData(table, userList);

        document.add(table);
    }

    private void writeTableHeader(PdfPTable table) {
        var cell = new PdfPCell();
        cell.setBackgroundColor(Color.GREEN);
        cell.setPadding(5);

        Font font = FontFactory.getFont(FontFactory.HELVETICA);
        font.setColor(Color.BLACK);

        cell.setPhrase(new Phrase("ID", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Email", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("First Name", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Last Name", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Roles", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Enabled", font));
        table.addCell(cell);

    }

    private void writeTableData(PdfPTable table, List<User> userList) {
        for (User user: userList){
            table.addCell(String.valueOf(user.getId()));
            table.addCell(user.getEmail());
            table.addCell(user.getFirstName());
            table.addCell(user.getLastName());
            table.addCell(user.getRoles().toString());
            table.addCell(String.valueOf(user.isEnabled()));
        }
    }
}
