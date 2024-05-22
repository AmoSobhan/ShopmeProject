package com.shopme.admin.user;

import com.shopme.commen.entity.User;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.*;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class UserExcelExporter extends AbstractExporter {

    private XSSFWorkbook workbook;
    private XSSFSheet sheet;

    public UserExcelExporter() {
        this.workbook = new XSSFWorkbook();
    }

    private void writeHeaderLine() {
        sheet = workbook.createSheet("Users");
        XSSFRow row = sheet.createRow(0);

        XSSFCellStyle cellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        cellStyle.setFont(font);

        createCell(row, 0, "User ID", cellStyle);
        createCell(row, 1, "Email", cellStyle);
        createCell(row, 2, "First Name", cellStyle);
        createCell(row, 3, "Last Name", cellStyle);
        createCell(row, 4, "Roles", cellStyle);
        createCell(row, 5, "Enabled", cellStyle);
    }

    private void createCell(XSSFRow row, int columnIndex, Object value, CellStyle cellStyle) {
        XSSFCell cell = row.createCell(columnIndex);
        sheet.autoSizeColumn(columnIndex);

        if (value instanceof Integer)
            cell.setCellValue((Integer) value);
        else if (value instanceof Boolean)
            cell.setCellValue((Boolean) value);
        else
            cell.setCellValue((String) value);

        cell.setCellStyle(cellStyle);

    }

    private void writeDataLine(List<User> usersList) {
        XSSFCellStyle cellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        cellStyle.setFont(font);

        int rowIndex = 1;
        for (User user : usersList) {
            XSSFRow row = sheet.createRow(rowIndex);
            rowIndex++;
            int columIndex = 0;

            createCell(row, columIndex, user.getId(), cellStyle);
            createCell(row, columIndex++, user.getEmail(), cellStyle);
            createCell(row, columIndex++, user.getFirstName(), cellStyle);
            createCell(row, columIndex++, user.getLastName(), cellStyle);
            createCell(row, columIndex++, user.getRoles().toString(), cellStyle);
            createCell(row, columIndex, user.isEnabled(), cellStyle);

        }


    }

    public void export(List<User> userList, HttpServletResponse response) throws IOException {
        super.setResponseHeader(response, "application/octet-stream", ".xlsx");

        writeHeaderLine();
        writeDataLine(userList);

        ServletOutputStream servletOutputStream = response.getOutputStream();
        workbook.write(servletOutputStream);
        workbook.close();
        servletOutputStream.close();
    }
}
