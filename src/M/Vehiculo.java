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
import java.util.Objects;

public class Vehiculo implements Serializable {
    private final String placa; // Inmutable
    private String marca;
    private String modelo;
    private String rutaFoto; // Puede ser null

    public Vehiculo(String placa, String marca, String modelo, String rutaFoto) {
        this.placa = Objects.requireNonNull(placa, "Placa no puede ser nula");
        this.marca = Objects.requireNonNull(marca, "Marca no puede ser nula");
        this.modelo = Objects.requireNonNull(modelo, "Modelo no puede ser nulo");
        this.rutaFoto = rutaFoto; // Opcional
    }

    // ---- Getters ----
    public String getPlaca() { return placa; }
    public String getMarca() { return marca; }
    public String getModelo() { return modelo; }
    public String getRutaFoto() { return rutaFoto; }

    // ---- Setters ----
    public void setMarca(String marca) {
        this.marca = Objects.requireNonNull(marca);
    }

    public void setModelo(String modelo) {
        this.modelo = Objects.requireNonNull(modelo);
    }

    public void setRutaFoto(String rutaFoto) {
        this.rutaFoto = rutaFoto;
    }

    // ---- Sobrescritura de métodos ----
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vehiculo vehiculo = (Vehiculo) o;
        return placa.equals(vehiculo.placa);
    }

    @Override
    public int hashCode() {
        return placa.hashCode();
    }
}
