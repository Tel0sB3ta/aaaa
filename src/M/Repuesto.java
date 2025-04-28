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

public class Repuesto implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String codigo;
    private String nombre;
    private String marca;
    private String modelo;
    private double precio;

    public Repuesto(String codigo, String nombre, String marca, String modelo, double precio) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.marca = marca;
        this.modelo = modelo;
        this.precio = precio;
    }

    // Getters
    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public String getMarca() { return marca; }
    public String getModelo() { return modelo; }
    public double getPrecio() { return precio; }

    // Setters
    public void setPrecio(double precio) { this.precio = precio; }

    @Override
    public String toString() {
        return String.format("%s - %s %s %s ($%.2f)", codigo, marca, modelo, nombre, precio);
    }
}
