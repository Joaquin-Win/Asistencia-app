package com.asistencia.service;

import com.asistencia.model.AttendanceRecord;
import com.asistencia.model.Meeting;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExcelExportService {

    private static final String[] HEADERS_SINGLE = { "Nombre", "Apellido", "Correo", "Legajo", "Carrera", "Fecha y Hora" };
    private static final String[] HEADERS_ALL = { "Encuentro", "Nombre", "Apellido", "Correo", "Legajo", "Carrera", "Fecha y Hora" };
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public ByteArrayInputStream exportMeetingAttendance(Meeting meeting, List<AttendanceRecord> records) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            
            // Reemplazar caracteres no permitidos en el nombre de la hoja
            String safeTitle = meeting.getTitle().replaceAll("[\\\\/?*:\\[\\]]", " ");
            if (safeTitle.length() > 31) safeTitle = safeTitle.substring(0, 31);
            Sheet sheet = workbook.createSheet(safeTitle);
            
            createHeaderRow(sheet, workbook, HEADERS_SINGLE);
            fillDataRows(sheet, records, false, meeting.getTitle());
            autoSizeColumns(sheet, HEADERS_SINGLE.length);

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    public ByteArrayInputStream exportAllMeetingsAttendance(List<Meeting> meetings, AttendanceService attendanceService) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            
            int sheetNum = 1;
            for (Meeting meeting : meetings) {
                List<AttendanceRecord> records = attendanceService.findByMeeting(meeting.getId());
                
                String safeTitle = meeting.getTitle().replaceAll("[\\\\/?*:\\[\\]]", " ");
                if (safeTitle.length() > 25) safeTitle = safeTitle.substring(0, 25);
                
                // Evitar nombres de hojas duplicados usando un prefijo numérico
                safeTitle = sheetNum + ". " + safeTitle;
                Sheet sheet = workbook.createSheet(safeTitle);

                createHeaderRow(sheet, workbook, HEADERS_ALL);
                fillDataRows(sheet, records, true, meeting.getTitle());
                autoSizeColumns(sheet, HEADERS_ALL.length);
                sheetNum++;
            }

            // Si no hay encuentros, al menos crear una hoja vacía para que POI no lance error
            if (meetings.isEmpty()) {
                workbook.createSheet("Sin encuentros");
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    private void createHeaderRow(Sheet sheet, Workbook workbook, String[] headers) {
        Row headerRow = sheet.createRow(0);
        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    private void fillDataRows(Sheet sheet, List<AttendanceRecord> records, boolean isAllMeetings, String meetingTitle) {
        int rowIdx = 1;
        for (AttendanceRecord record : records) {
            Row row = sheet.createRow(rowIdx++);
            int colIdx = 0;
            
            if (isAllMeetings) {
                row.createCell(colIdx++).setCellValue(meetingTitle);
            }
            
            row.createCell(colIdx++).setCellValue(record.getFirstName());
            row.createCell(colIdx++).setCellValue(record.getLastName());
            row.createCell(colIdx++).setCellValue(record.getEmail());
            row.createCell(colIdx++).setCellValue(record.getStudentId() != null ? record.getStudentId() : "");
            row.createCell(colIdx++).setCellValue(record.getCareer());
            
            if (record.getRegisteredAt() != null) {
                row.createCell(colIdx++).setCellValue(record.getRegisteredAt().format(DATE_FORMATTER));
            } else {
                row.createCell(colIdx++).setCellValue("");
            }
        }
    }

    private void autoSizeColumns(Sheet sheet, int numCols) {
        for (int i = 0; i < numCols; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}
