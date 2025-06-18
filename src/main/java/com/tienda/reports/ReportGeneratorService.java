package com.tienda.reports;

import com.tienda.dto.VentaReporteDTO;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReportGeneratorService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Genera un informe CSV de ventas detallado (por cada producto en cada venta).
     * @param ventasDetalles Lista de DTOs de ventas detalladas (cada DTO representa un ContieneV).
     * @return Una cadena que representa el contenido del archivo CSV.
     */
    public String generateVentasCsv(List<VentaReporteDTO> ventasDetalles) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        // Encabezado del CSV
        pw.print("ID Venta");
        pw.print(";");
        pw.print("Fecha Venta");
        pw.print(",");
        pw.print("Monto Total Venta"); // Total de la venta completa
        pw.print(",");
        pw.print("Pagada");
        pw.print(",");
        pw.print("ID Producto");
        pw.print(",");
        pw.print("Nombre Producto");
        pw.print(",");
        pw.print("Precio Unitario");
        pw.print(",");
        pw.print("Cantidad");
        pw.print(",");
        pw.print("Unidad");
        pw.print(",");
        pw.println("Subtotal Linea"); // Subtotal de esta línea de producto

        // Cuerpo del CSV
        for (VentaReporteDTO detalle : ventasDetalles) {
            pw.print(escapeCsvField(String.valueOf(detalle.getCodVenta())));
            pw.print(",");
            pw.print(escapeCsvField(detalle.getFechaVenta().format(DATE_TIME_FORMATTER)));
            pw.print(",");
            pw.print(escapeCsvField(String.valueOf(detalle.getMontoTotalVenta())));
            pw.print(",");
            pw.print(escapeCsvField(detalle.isPagada() ? "Sí" : "No"));
            pw.print(",");
            pw.print(escapeCsvField(String.valueOf(detalle.getIdProducto())));
            pw.print(",");
            pw.print(escapeCsvField(detalle.getNombreProducto()));
            pw.print(",");
            pw.print(escapeCsvField(String.valueOf(detalle.getPrecioUnitarioProducto())));
            pw.print(",");
            pw.print(escapeCsvField(String.valueOf(detalle.getCantidadProducto())));
            pw.print(",");
            pw.print(escapeCsvField(detalle.getUnidadProducto()));
            pw.print(",");
            pw.println(escapeCsvField(String.valueOf(detalle.getSubtotalLinea())));
        }

        return sw.toString();
    }

    // Método auxiliar para escapar campos CSV (maneja comas y comillas dentro del texto)
    private String escapeCsvField(String field) {
        if (field == null) {
            return "";
        }
        // Si el campo contiene comas, comillas dobles o saltos de línea,
        // debe encerrarse entre comillas dobles y las comillas dobles internas se duplican.
        if (field.contains(",") || field.contains("\"") || field.contains("\n") || field.contains("\r")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
    }
}
