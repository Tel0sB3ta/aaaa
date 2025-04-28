/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package M;

/**
 *
 * @author gervi
 */
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Factura implements Serializable {
    private final int numeroFactura;
    private final OrdenTrabajo orden;
    private final Date fecha;
    private boolean pagada;

    public Factura(OrdenTrabajo orden) {
        this.numeroFactura = orden.getNumeroOrden(); // Usamos el mismo número que la orden
        this.orden = orden;
        this.fecha = new Date();
        this.pagada = false;
    }

    public int getNumeroFactura() { return numeroFactura; }
    public OrdenTrabajo getOrden() { return orden; }
    public Date getFecha() { return fecha; }
    public boolean isPagada() { return pagada; }
    public void setPagada(boolean pagada) { this.pagada = pagada; }

    public String generarFacturaFormateada() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        StringBuilder sb = new StringBuilder();
        
        sb.append("================================\n");
        sb.append("      TALLER MECÁNICO USAC      \n");
        sb.append("================================\n");
        sb.append("Factura #: ").append(numeroFactura).append("\n");
        sb.append("Fecha: ").append(sdf.format(fecha)).append("\n");
        sb.append("--------------------------------\n");
        sb.append("CLIENTE\n");
        sb.append("Nombre: ").append(orden.getCliente().getNombreCompleto()).append("\n");
        sb.append("Tipo: ").append(orden.getCliente().getTipo()).append("\n");
        sb.append("--------------------------------\n");
        sb.append("VEHÍCULO\n");
        sb.append("Placa: ").append(orden.getVehiculo().getPlaca()).append("\n");
        sb.append("Marca: ").append(orden.getVehiculo().getMarca()).append("\n");
        sb.append("Modelo: ").append(orden.getVehiculo().getModelo()).append("\n");
        sb.append("--------------------------------\n");
        sb.append("SERVICIO\n");
        sb.append("Descripción: ").append(orden.getServicio().getNombreServicio()).append("\n");
        sb.append("Mecánico: #").append(orden.getMecanico().getId()).append("\n");
        sb.append("--------------------------------\n");
        sb.append("TOTAL: Q").append(String.format("%.2f", orden.getServicio().getPrecioTotal())).append("\n");
        sb.append("--------------------------------\n");
        sb.append("Estado: ").append(pagada ? "PAGADA" : "PENDIENTE").append("\n");
        sb.append("================================\n");
        
        return sb.toString();
    }
}

