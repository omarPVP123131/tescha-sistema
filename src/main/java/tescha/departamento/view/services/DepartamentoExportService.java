package tescha.departamento.view.services;

import com.itextpdf.text.Font;
import tescha.departamento.dto.DepartamentoDTO;
import com.opencsv.CSVWriter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

public class DepartamentoExportService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final String EXPORT_DIR = "exports";

    public DepartamentoExportService() {
        createExportDirectory();
    }

    private void createExportDirectory() {
        try {
            Files.createDirectories(Paths.get(EXPORT_DIR));
        } catch (IOException e) {
            System.err.println("Error creando directorio de exportación: " + e.getMessage());
        }
    }

    public File exportToCSV(List<DepartamentoDTO> departamentos) throws IOException {
        String fileName = generateFileName("departamentos", "csv");
        File file = new File(EXPORT_DIR, fileName);

        try (FileWriter fileWriter = new FileWriter(file);
             CSVWriter csvWriter = new CSVWriter(fileWriter)) {

            // Escribir encabezados
            String[] headers = {
                    "ID", "Nombre", "Descripción", "Estado", "Responsable",
                    "Presupuesto", "Fecha Creación"
            };
            csvWriter.writeNext(headers);

            // Escribir datos
            for (DepartamentoDTO dept : departamentos) {
                String[] row = {
                        String.valueOf(dept.getId()),
                        dept.getNombre(),
                        dept.getDescripcion(),
                };
                csvWriter.writeNext(row);
            }
        }

        return file;
    }

    public File exportToExcel(List<DepartamentoDTO> departamentos) throws IOException {
        String fileName = generateFileName("departamentos", "xlsx");
        File file = new File(EXPORT_DIR, fileName);

        try (Workbook workbook = new XSSFWorkbook();
             FileOutputStream fileOut = new FileOutputStream(file)) {

            Sheet sheet = workbook.createSheet("Departamentos");

            // Crear estilos
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dataStyle = createDataStyle(workbook);
            CellStyle currencyStyle = createCurrencyStyle(workbook);
            CellStyle dateStyle = createDateStyle(workbook);

            // Crear encabezados
            Row headerRow = sheet.createRow(0);
            String[] headers = {
                    "ID", "Nombre", "Descripción", "Estado", "Responsable",
                    "Presupuesto", "Fecha Creación"
            };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Llenar datos
            int rowNum = 1;
            for (DepartamentoDTO dept : departamentos) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(dept.getId());
                row.createCell(1).setCellValue(dept.getNombre());
                row.createCell(2).setCellValue(dept.getDescripcion());

                // Aplicar estilo a las celdas de datos
                for (int i = 0; i < 7; i++) {
                    Cell cell = row.getCell(i);
                    if (cell != null && cell.getCellStyle() == workbook.getCellStyleAt((short) 0)) {
                        cell.setCellStyle(dataStyle);
                    }
                }
            }

            // Ajustar ancho de columnas
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Agregar filtros
            sheet.setAutoFilter(new org.apache.poi.ss.util.CellRangeAddress(0, departamentos.size(), 0, headers.length - 1));

            workbook.write(fileOut);
        }

        return file;
    }

    public File exportToPDF(List<DepartamentoDTO> departamentos) throws IOException, DocumentException {
        String fileName = generateFileName("departamentos", "pdf");
        File file = new File(EXPORT_DIR, fileName);

        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, new FileOutputStream(file));

        document.open();

        // Título
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.DARK_GRAY);
        Paragraph title = new Paragraph("Reporte de Departamentos", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // Información del reporte
        Font infoFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY);
        Paragraph info = new Paragraph("Generado el: " + LocalDateTime.now().format(DATE_FORMATTER), infoFont);
        info.setAlignment(Element.ALIGN_RIGHT);
        info.setSpacingAfter(20);
        document.add(info);

        // Tabla
        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        // Configurar anchos de columna
        float[] columnWidths = {1f, 2.5f, 3f, 1.5f, 2f, 1.5f, 2f};
        table.setWidths(columnWidths);

        // Encabezados
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);
        String[] headers = {"ID", "Nombre", "Descripción", "Estado", "Responsable", "Presupuesto", "Fecha"};

        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(BaseColor.DARK_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(8);
            table.addCell(cell);
        }

        // Datos
        Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 9);
        for (int i = 0; i < departamentos.size(); i++) {
            DepartamentoDTO dept = departamentos.get(i);
            BaseColor bgColor = (i % 2 == 0) ? BaseColor.WHITE : new BaseColor(245, 245, 245);

            addTableCell(table, String.valueOf(dept.getId()), dataFont, bgColor, Element.ALIGN_CENTER);
            addTableCell(table, dept.getNombre(), dataFont, bgColor, Element.ALIGN_LEFT);
            addTableCell(table, truncateText(dept.getDescripcion(), 50), dataFont, bgColor, Element.ALIGN_LEFT);

        }

        document.add(table);

        document.close();
        return file;
    }

    public File exportToJSON(List<DepartamentoDTO> departamentos) throws IOException {
        String fileName = generateFileName("departamentos", "json");
        File file = new File(EXPORT_DIR, fileName);

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();

        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(departamentos, writer);
        }

        return file;
    }

    public List<DepartamentoDTO> importFromFile(File file) throws IOException {
        String extension = getFileExtension(file.getName()).toLowerCase();

        switch (extension) {
            case "csv":
                return importFromCSV(file);
            case "xlsx":
            case "xls":
                return importFromExcel(file);
            case "json":
                return importFromJSON(file);
            default:
                throw new IllegalArgumentException("Formato de archivo no soportado: " + extension);
        }
    }

    private List<DepartamentoDTO> importFromCSV(File file) throws IOException {
        List<DepartamentoDTO> departamentos = new ArrayList<>();

        try (FileReader fr = new FileReader(file);
             CSVReader csvReader = new CSVReader(fr)) {

            try {
                csvReader.readNext(); // Saltar encabezados
            } catch (CsvValidationException e) {
                throw new RuntimeException(e);
            }
            String[] row;
            while (true) {
                try {
                    row = csvReader.readNext();
                    if (row == null) break;
                } catch (CsvValidationException e) {
                    throw new IOException("CSV inválido", e);
                }
                DepartamentoDTO dept = new DepartamentoDTO();
                if (row.length > 1) dept.setNombre(row[1]);
                if (row.length > 2) dept.setDescripcion(row[2]);
                departamentos.add(dept);
            }
        }
        return departamentos;
    }


    private List<DepartamentoDTO> importFromExcel(File file) throws IOException {
        List<DepartamentoDTO> departamentos = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheetAt(0);

            // Saltar la fila de encabezados
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                DepartamentoDTO dept = new DepartamentoDTO();

                Cell nombreCell = row.getCell(1);
                if (nombreCell != null) dept.setNombre(getCellValueAsString(nombreCell));

                Cell descCell = row.getCell(2);
                if (descCell != null) dept.setDescripcion(getCellValueAsString(descCell));


                departamentos.add(dept);
            }
        }

        return departamentos;
    }

    private List<DepartamentoDTO> importFromJSON(File file) throws IOException {
        Gson gson = new Gson();

        try (FileReader reader = new FileReader(file)) {
            DepartamentoDTO[] array = gson.fromJson(reader, DepartamentoDTO[].class);
            return List.of(array);
        }
    }

    public File generateReport(List<DepartamentoDTO> departamentos, String reportType) throws IOException, DocumentException {
        switch (reportType.toLowerCase()) {
            case "summary":
                return generateSummaryReport(departamentos);
            case "detailed":
                return generateDetailedReport(departamentos);
            case "statistics":
                return generateStatisticsReport(departamentos);
            default:
                throw new IllegalArgumentException("Tipo de reporte no soportado: " + reportType);
        }
    }

    private File generateSummaryReport(List<DepartamentoDTO> departamentos) throws IOException, DocumentException {
        String fileName = generateFileName("reporte_resumen", "pdf");
        File file = new File(EXPORT_DIR, fileName);

        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(file));
        document.open();

        // Título
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BaseColor.DARK_GRAY);
        Paragraph title = new Paragraph("Reporte Resumen - Departamentos", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(30);
        document.add(title);

        // Estadísticas generales
        long total = departamentos.size();

        PdfPTable statsTable = new PdfPTable(2);
        statsTable.setWidthPercentage(60);
        statsTable.setHorizontalAlignment(Element.ALIGN_CENTER);

        addStatsRow(statsTable, "Total de Departamentos:", String.valueOf(total));

        document.add(statsTable);

        document.close();
        return file;
    }

    private File generateDetailedReport(List<DepartamentoDTO> departamentos) throws IOException, DocumentException {
        // Similar al exportToPDF pero con más detalles y análisis
        return exportToPDF(departamentos);
    }

    private File generateStatisticsReport(List<DepartamentoDTO> departamentos) throws IOException, DocumentException {
        String fileName = generateFileName("reporte_estadisticas", "pdf");
        File file = new File(EXPORT_DIR, fileName);

        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(file));
        document.open();

        // Título
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BaseColor.DARK_GRAY);
        Paragraph title = new Paragraph("Reporte Estadístico - Departamentos", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(30);
        document.add(title);


        Paragraph estadoTitle = new Paragraph("Distribución por Estado:",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
        document.add(estadoTitle);

        PdfPTable estadoTable = new PdfPTable(3);
        estadoTable.setWidthPercentage(80);
        estadoTable.setSpacingBefore(10);

        addTableHeader(estadoTable, "Estado", "Cantidad", "Porcentaje");

        document.add(estadoTable);

        document.close();
        return file;
    }

    // Métodos auxiliares
    private String generateFileName(String prefix, String extension) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return String.format("%s_%s.%s", prefix, timestamp, extension);
    }

    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        return lastDotIndex > 0 ? fileName.substring(lastDotIndex + 1) : "";
    }

    private String truncateText(String text, int maxLength) {
        if (text == null) return "";
        return text.length() > maxLength ? text.substring(0, maxLength - 3) + "..." : text;
    }

    private String getCellValueAsString(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }

    // Métodos para estilos de Excel
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        org.apache.poi.ss.usermodel.Font font = workbook.createFont();         // éste es org.apache.poi.ss.usermodel.Font
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle createCurrencyStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat("$#,##0.00"));
        style.setAlignment(HorizontalAlignment.RIGHT);
        return style;
    }

    private CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat("dd/mm/yyyy"));
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    // Métodos para PDF
    private void addTableCell(PdfPTable table, String text, Font font, BaseColor bgColor, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bgColor);
        cell.setHorizontalAlignment(alignment);
        cell.setPadding(5);
        table.addCell(cell);
    }

    private void addStatsRow(PdfPTable table, String label, String value) {
        Font labelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        labelCell.setPadding(8);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        valueCell.setPadding(8);

        table.addCell(labelCell);
        table.addCell(valueCell);
    }

    private void addTableHeader(PdfPTable table, String... headers) {
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);

        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(BaseColor.DARK_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(8);
            table.addCell(cell);
        }
    }

    private void addTableRow(PdfPTable table, String... values) {
        Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

        for (String value : values) {
            PdfPCell cell = new PdfPCell(new Phrase(value, dataFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(6);
            table.addCell(cell);
        }
    }
}