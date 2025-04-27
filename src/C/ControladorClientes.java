/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package C;

/**
 *
 * @author gervi
 */
import M.Cliente;
import M.PersistenciaClientes;
import M.Vehiculo;
import java.io.IOException;
import java.util.Vector;
import java.util.stream.Collectors;

public class ControladorClientes {
    private static ControladorClientes instancia;
    private Vector<Cliente> clientes;

    // ---- Constructor ----
    public ControladorClientes() {
        cargarClientesDesdeArchivo();
    }
    

    // ---- Serialización ----
        public static ControladorClientes getInstancia() {
        if (instancia == null) {
            instancia = new ControladorClientes();
        }
        return instancia;
    }
        public Cliente autenticarCliente(String usuario, String contraseña) {
    return clientes.stream()
        .filter(c -> c.getUsuario().equalsIgnoreCase(usuario) && 
                     c.getContraseña().equals(contraseña))
        .findFirst()
        .orElse(null);
}

    public void cargarClientesDesdeArchivo() {
        try {
            this.clientes = PersistenciaClientes.cargarClientes();
        } catch (IOException | ClassNotFoundException e) {
            this.clientes = new Vector<>();
            System.err.println("Archivo de clientes no encontrado. Se creará uno nuevo.");
        }
    }

    public void guardarClientes() {
        try {
            PersistenciaClientes.guardarClientes(clientes);
        } catch (IOException e) {
            System.err.println("Error al guardar clientes: " + e.getMessage());
        }
    }

    // ---- Métodos CRUD para clientes ----
    public boolean actualizarVehiculo(String dpi, String placaAntigua, String nuevaPlaca, 
                                String nuevaMarca, String nuevoModelo, String nuevaRutaFoto) {
    Cliente cliente = buscarClientePorDPI(dpi);
    if (cliente == null) return false;

    // Buscar el vehículo por placa antigua
    Vehiculo vehiculoExistente = cliente.getVehiculos().stream()
        .filter(v -> v.getPlaca().equalsIgnoreCase(placaAntigua))
        .findFirst()
        .orElse(null);

    if (vehiculoExistente == null) return false;

    // Verificar si la nueva placa ya existe (y no es la misma que la antigua)
    if (!nuevaPlaca.equalsIgnoreCase(placaAntigua) && 
        existePlacaEnOtroVehiculo(dpi, nuevaPlaca)) {
        return false;
    }

    // Actualizar los datos del vehículo
    vehiculoExistente.setMarca(nuevaMarca);
    vehiculoExistente.setModelo(nuevoModelo);
    vehiculoExistente.setRutaFoto(nuevaRutaFoto);
    
    // Si cambió la placa, necesitamos eliminar y recrear el vehículo
    if (!nuevaPlaca.equalsIgnoreCase(placaAntigua)) {
        cliente.eliminarVehiculo(placaAntigua);
        Vehiculo vehiculoActualizado = new Vehiculo(nuevaPlaca, nuevaMarca, nuevoModelo, nuevaRutaFoto);
        cliente.agregarVehiculo(vehiculoActualizado);
    }

    guardarClientes();
    return true;
}

private boolean existePlacaEnOtroVehiculo(String dpiCliente, String placa) {
    return clientes.stream()
        .filter(c -> !c.getDpi().equals(dpiCliente)) // Excluir al cliente actual
        .flatMap(c -> c.getVehiculos().stream())
        .anyMatch(v -> v.getPlaca().equalsIgnoreCase(placa));
}
public boolean registrarCliente(String dpi, String nombreCompleto, String usuario, String contraseña) {
    try {
        // Verificar si el DPI ya existe en la lista de clientes
        if (clientes.stream().anyMatch(c -> c.getDpi().equals(dpi))) {
            throw new IllegalArgumentException("El DPI ya está registrado."); // Mensaje de error si está duplicado
        }

        // Crear un nuevo cliente
        Cliente nuevoCliente = new Cliente(dpi, nombreCompleto, usuario, contraseña);
        clientes.add(nuevoCliente); // Añadir cliente a la lista
        guardarClientes(); // Serializar los datos para persistencia
        return true; // Éxito al registrar
    } catch (IllegalArgumentException e) {
        System.err.println("Error al registrar cliente: " + e.getMessage());
        return false; // Indicar fallo en el registro
    }
}

    public boolean actualizarCliente(String dpi, String nuevoNombre, String nuevoUsuario, String nuevaContraseña) {
        Cliente cliente = buscarClientePorDPI(dpi);
        if (cliente == null) return false;

        try {
            cliente.setNombreCompleto(nuevoNombre);
            cliente.setUsuario(nuevoUsuario);
            cliente.setContraseña(nuevaContraseña);
            guardarClientes();
            return true;
        } catch (IllegalArgumentException e) {
            System.err.println("Error al actualizar cliente: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarCliente(String dpi) {
        boolean eliminado = clientes.removeIf(c -> c.getDpi().equals(dpi));
        if (eliminado) guardarClientes();
        return eliminado;
    }

    // ---- Búsquedas ----
    public Cliente buscarClientePorDPI(String dpi) {
        return clientes.stream()
            .filter(c -> c.getDpi().equals(dpi))
            .findFirst()
            .orElse(null);
    }

    public boolean existeUsuario(String usuario) {
        return clientes.stream().anyMatch(c -> c.getUsuario().equalsIgnoreCase(usuario));
    }

    // ---- Métodos para vehículos ----
public boolean agregarVehiculo(String dpi, String placa, String marca, String modelo, String rutaFoto) {
    Cliente cliente = buscarClientePorDPI(dpi);
    if (cliente == null) return false;

    // Verificar si la placa ya existe en cualquier cliente
    for (Cliente c : clientes) {
        for (Vehiculo v : c.getVehiculos()) {
            if (v.getPlaca().equalsIgnoreCase(placa)) {
                return false; // Placa ya existe
            }
        }
    }

    try {
        Vehiculo vehiculo = new Vehiculo(placa, marca, modelo, rutaFoto);
        cliente.agregarVehiculo(vehiculo);
        guardarClientes();
        return true;
    } catch (IllegalArgumentException e) {
        System.err.println("Error al agregar vehículo: " + e.getMessage());
        return false;
    }
}

// Añadir este método a la clase ControladorClientes
public boolean eliminarVehiculo(String dpi, String placa) {
    Cliente cliente = buscarClientePorDPI(dpi);
    if (cliente == null) return false;

    boolean eliminado = cliente.eliminarVehiculo(placa);
    if (eliminado) {
        guardarClientes();
    }
    return eliminado;
}

    // ---- Ordenamientos ----
    public void ordenarClientesPorDPI() {
        // Bubble Sort ascendente
        for (int i = 0; i < clientes.size() - 1; i++) {
            for (int j = 0; j < clientes.size() - i - 1; j++) {
                if (clientes.get(j).getDpi().compareTo(clientes.get(j + 1).getDpi()) > 0) {
                    Cliente temp = clientes.get(j);
                    clientes.set(j, clientes.get(j + 1));
                    clientes.set(j + 1, temp);
                }
            }
        }
    }

    public void ordenarVehiculosCliente(String dpi, boolean ascendente) {
        Cliente cliente = buscarClientePorDPI(dpi);
        if (cliente != null) {
            cliente.ordenarVehiculos(ascendente);
        }
    }

    // ---- Reportes ----
    public Vector<Cliente> obtenerClientesOro() {
        return clientes.stream()
            .filter(c -> c.getTipo().equalsIgnoreCase("Oro"))
            .collect(Collectors.toCollection(Vector::new));
    }

    public Vector<Cliente> obtenerTodosClientes() {
        return new Vector<>(clientes); // Copia defensiva
    }
}
