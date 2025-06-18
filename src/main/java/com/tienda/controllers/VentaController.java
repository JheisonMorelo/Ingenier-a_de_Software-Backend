package com.tienda.controllers;


import com.tienda.dto.VentaReporteDTO;
import com.tienda.models.Venta;
import com.tienda.services.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders; // Para Content-Disposition
import com.tienda.reports.ReportGeneratorService; // Importar ReportGeneratorService

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/ventas")
@CrossOrigin(origins = "http://localhost:4200")
public class VentaController {

    @Autowired
    private VentaService ventaService;

    @Autowired
    private ReportGeneratorService reportGeneratorService; // Inyectar el nuevo servicio

    @GetMapping
    public List<Venta> getAllVentas() {
        return ventaService.getAllVentas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Venta> getVentaById(@PathVariable Long id) {
        return ventaService.getVentaById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/ganancias")
    public ResponseEntity<List<Venta>> getVentasForProfit() {
        List<Venta> ventasGanancia = ventaService.getVentasForProfitCalculation();
        return ResponseEntity.ok(ventasGanancia);
    }

    @GetMapping("/ganancias/total")
    public ResponseEntity<BigDecimal> getTotalProfit() {
        BigDecimal totalProfit = ventaService.calculateTotalProfit();
        return ResponseEntity.ok(totalProfit);
    }

    @PostMapping
    public ResponseEntity<Object> createVenta(@RequestBody Venta venta) {
        try {
            Venta savedVenta = ventaService.createVenta(venta);
            return new ResponseEntity<>(savedVenta, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Venta> updateVenta(@PathVariable Long id, @RequestBody Venta ventaDetails) {
        return ventaService.updateVenta(id, ventaDetails)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVenta(@PathVariable Long id) {
        try {
            ventaService.deleteVenta(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build(); // O un HttpStatus.INTERNAL_SERVER_ERROR si el error es otro
        }
    }

    // NUEVO ENDPOINT: Descargar el informe general de ventas en CSV
    @GetMapping("/reporte/general-ventas/csv")
    public ResponseEntity<String> getGeneralSalesReportCsv() {
        try {
            List<VentaReporteDTO> ventasData = ventaService.getGeneralSalesReportData();
            String csvContent = reportGeneratorService.generateVentasCsv(ventasData);

            HttpHeaders headers = new HttpHeaders();
            // Configurar el Content-Disposition para que el navegador descargue el archivo
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=informe_general_ventas.csv");
            // Configurar el Content-Type para indicar que es un archivo CSV
            headers.add(HttpHeaders.CONTENT_TYPE, "text/csv");

            return new ResponseEntity<>(csvContent, headers, HttpStatus.OK);
        } catch (Exception e) {
            // Manejar cualquier error en la generación del reporte
            return new ResponseEntity<>("Error al generar el informe CSV: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}