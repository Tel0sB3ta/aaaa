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
import java.util.Objects;

public class Cliente implements Serializable {
    // Atributos (requeridos por el PDF)
    private final String dpi; // Inmutable (13 dígitos)
    private String nombreCompleto;
    public String usuario;
    private String contraseña;
    private String tipo; // "Normal" u "Oro"
    private final Vector<Vehiculo> vehiculos; // Lista dinámica
    private int serviciosCompletados; // Para promoción a "Oro" (Sección D: 4 servicios)

    // ---- Constructor ----
    public Cliente(String dpi, String nombreCompleto, String usuario, String contraseña) {
        validarDPI(dpi); // Lanza IllegalArgumentException si no cumple
        this.dpi = dpi;
        this.nombreCompleto = Objects.requireNonNull(nombreCompleto, "Nombre no puede ser nulo");
        this.usuario = Objects.requireNonNull(usuario, "Usuario no puede ser nulo");
        this.contraseña = Objects.requireNonNull(contraseña, "Contraseña no puede ser nula");
        this.tipo = "Normal";
        this.vehiculos = new Vector<>();
        this.serviciosCompletados = 0;
    }

    // ---- Validaciones ----
    private void validarDPI(String dpi) {
        if (dpi == null || !dpi.matches("\\d{13}")) {
            throw new IllegalArgumentException("DPI debe tener 13 dígitos numéricos.");
        }
    }

    // ---- Métodos para vehículos ----
    public void agregarVehiculo(Vehiculo vehiculo) {
        vehiculos.add(Objects.requireNonNull(vehiculo, "Vehículo no puede ser nulo"));
    }

public boolean eliminarVehiculo(String placa) {
    if (placa == null || placa.isEmpty()) {
        return false;
    }
    
    // Buscar el vehículo por placa (case insensitive)
    for (int i = 0; i < vehiculos.size(); i++) {
        if (vehiculos.get(i).getPlaca().equalsIgnoreCase(placa)) {
            vehiculos.remove(i);
            return true;
        }
    }
    return false;
}

    // Ordenamiento ShellSort por placa (ascendente o descendente)
    public void ordenarVehiculos(boolean ascendente) {
        int n = vehiculos.size();
        for (int gap = n / 2; gap > 0; gap /= 2) {
            for (int i = gap; i < n; i++) {
                Vehiculo temp = vehiculos.get(i);
                int j;
                for (j = i; j >= gap && 
                    (ascendente ? vehiculos.get(j - gap).getPlaca().compareTo(temp.getPlaca()) > 0
                               : vehiculos.get(j - gap).getPlaca().compareTo(temp.getPlaca()) < 0); 
                    j -= gap) {
                    vehiculos.set(j, vehiculos.get(j - gap));
                }
                vehiculos.set(j, temp);
            }
        }
    }

    // ---- Lógica de cliente "Oro" ----
    public void incrementarServicios() {
        serviciosCompletados++;
        if (serviciosCompletados >= 4) { // Sección D: 4 servicios
            this.tipo = "Oro";
        }
    }

    // ---- Getters ----
    public String getDpi() { return dpi; }
    public String getNombreCompleto() { return nombreCompleto; }
    public String getUsuario() { return usuario; }
    public String getContraseña() { return contraseña; }
    public String getTipo() { return tipo; }
    public Vector<Vehiculo> getVehiculos() { return new Vector<>(vehiculos); } // Copia defensiva
    public int getServiciosCompletados() { return serviciosCompletados; }

    // ---- Setters (controlados) ----
    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = Objects.requireNonNull(nombreCompleto);
    }

    public void setUsuario(String usuario) {
        this.usuario = Objects.requireNonNull(usuario);
    }

    public void setContraseña(String contraseña) {
        this.contraseña = Objects.requireNonNull(contraseña);
    }
    
    public void setTipo(String tipo) {
    if (!tipo.equalsIgnoreCase("Normal") && !tipo.equalsIgnoreCase("Oro")) {
        throw new IllegalArgumentException("El tipo de cliente debe ser 'Normal' u 'Oro'");
    }
    this.tipo = tipo;
}

    // ---- Sobrescritura de métodos ----
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cliente cliente = (Cliente) o;
        return dpi.equals(cliente.dpi);
    }

    @Override
    public int hashCode() {
        return dpi.hashCode();
    }
}