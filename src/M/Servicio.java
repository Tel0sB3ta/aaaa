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
import java.util.Vector;

public class Servicio implements Serializable {
    private final String id;
    private final String nombreServicio;
    private final String marca;
    private final String modelo;
    private final Vector<Repuesto> repuestos;
    private final double precioManoDeObra;
    private final double precioTotal;

    public Servicio(String id, String nombreServicio, String marca, String modelo, 
                   Vector<Repuesto> repuestos, double precioManoDeObra) {
        this.id = id;
        this.nombreServicio = nombreServicio;
        this.marca = marca;
        this.modelo = modelo;
        this.repuestos = repuestos;
        this.precioManoDeObra = precioManoDeObra;
        
        // Calcular precio total
        double precioRepuestos = repuestos.stream().mapToDouble(Repuesto::getPrecio).sum();
        this.precioTotal = precioRepuestos + precioManoDeObra;
    }

    // Getters
    public String getId() { return id; }
    public String getNombreServicio() { return nombreServicio; }
    public String getMarca() { return marca; }
    public String getModelo() { return modelo; }
    public Vector<Repuesto> getRepuestos() { return new Vector<>(repuestos); }
    public double getPrecioManoDeObra() { return precioManoDeObra; }
    public double getPrecioTotal() { return precioTotal; }
}