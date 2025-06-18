package com.tienda.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// DTO para representar una fila en el informe de ventas (ahora por detalle de venta)
public class VentaReporteDTO {
    // Información de la Venta
    private Long codVenta;
    private LocalDateTime fechaVenta;
    private BigDecimal montoTotalVenta; // El total de la venta original
    private boolean pagada;

    // Información del Producto en el detalle de venta
    private Long idProducto;
    private String nombreProducto;
    private BigDecimal precioUnitarioProducto;
    private Integer cantidadProducto;
    private String unidadProducto;
    private BigDecimal subtotalLinea; // Precio Unitario * Cantidad

    // Constructor para mapear desde ContieneV y sus relaciones
    public VentaReporteDTO(Long codVenta, LocalDateTime fechaVenta, BigDecimal montoTotalVenta, boolean pagada,
                           Long idProducto, String nombreProducto, BigDecimal precioUnitarioProducto,
                           Integer cantidadProducto, String unidadProducto, BigDecimal subtotalLinea) {
        this.codVenta = codVenta;
        this.fechaVenta = fechaVenta;
        this.montoTotalVenta = montoTotalVenta;
        this.pagada = pagada;
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.precioUnitarioProducto = precioUnitarioProducto;
        this.cantidadProducto = cantidadProducto;
        this.unidadProducto = unidadProducto;
        this.subtotalLinea = subtotalLinea;
    }

    // Getters (necesarios para acceder a los datos al generar el CSV)
    public Long getCodVenta() { return codVenta; }
    public LocalDateTime getFechaVenta() { return fechaVenta; }
    public BigDecimal getMontoTotalVenta() { return montoTotalVenta; }
    public boolean isPagada() { return pagada; }
    public Long getIdProducto() { return idProducto; }
    public String getNombreProducto() { return nombreProducto; }
    public BigDecimal getPrecioUnitarioProducto() { return precioUnitarioProducto; }
    public Integer getCantidadProducto() { return cantidadProducto; }
    public String getUnidadProducto() { return unidadProducto; }
    public BigDecimal getSubtotalLinea() { return subtotalLinea; }

    // Setters (opcionales para DTOs de reporte, pero se incluyen por si acaso)
    public void setCodVenta(Long codVenta) { this.codVenta = codVenta; }
    public void setFechaVenta(LocalDateTime fechaVenta) { this.fechaVenta = fechaVenta; }
    public void setMontoTotalVenta(BigDecimal montoTotalVenta) { this.montoTotalVenta = montoTotalVenta; }
    public void setPagada(boolean pagada) { this.pagada = pagada; }
    public void setIdProducto(Long idProducto) { this.idProducto = idProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }
    public void setPrecioUnitarioProducto(BigDecimal precioUnitarioProducto) { this.precioUnitarioProducto = precioUnitarioProducto; }
    public void setCantidadProducto(Integer cantidadProducto) { this.cantidadProducto = cantidadProducto; }
    public void setUnidadProducto(String unidadProducto) { this.unidadProducto = unidadProducto; }
    public void setSubtotalLinea(BigDecimal subtotalLinea) { this.subtotalLinea = subtotalLinea; }
}
