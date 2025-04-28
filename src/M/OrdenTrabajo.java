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
import java.util.Date;

public class OrdenTrabajo implements Serializable {
    private static int contador = 1;
    private final int numeroOrden;
    private final Vehiculo vehiculo;
    private final Cliente cliente;
    private Servicio servicio;
    private final Date fecha;
    private Mecanico mecanico;
    private String estado; // "En espera", "En servicio", "Completado"

    public OrdenTrabajo(Vehiculo vehiculo, Cliente cliente, Servicio servicio) {
        this.numeroOrden = contador++;
        this.vehiculo = vehiculo;
        this.cliente = cliente;
        this.servicio = servicio;
        this.fecha = new Date();
        this.estado = "En espera";
    }

    // Getters y setters
    public int getNumeroOrden() { return numeroOrden; }
    public Vehiculo getVehiculo() { return vehiculo; }
    public Cliente getCliente() { return cliente; }
    public Servicio getServicio() { return servicio; }
    public void setServicio(Servicio servicio) { this.servicio = servicio; }
    public Date getFecha() { return fecha; }
    public Mecanico getMecanico() { return mecanico; }
    public void setMecanico(Mecanico mecanico) { this.mecanico = mecanico; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}