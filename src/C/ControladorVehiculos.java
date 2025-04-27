/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package C;

/**
 *
 * @author gervi
 */
import M.Vehiculo;
import M.PersistenciaClientes;
import java.util.List;
import java.util.stream.Collectors;

public class ControladorVehiculos {
    private ControladorClientes controladorClientes;

    public ControladorVehiculos(ControladorClientes controladorClientes) {
        this.controladorClientes = controladorClientes;
    }
public boolean actualizarVehiculo(String dpiCliente, String placaAntigua, 
                                String nuevaPlaca, String nuevaMarca, 
                                String nuevoModelo, String nuevaRutaFoto) {
    try {
        // Buscar el cliente
        var cliente = controladorClientes.buscarClientePorDPI(dpiCliente);
        if (cliente == null) {
            return false;
        }

        // Verificar si la nueva placa ya existe en otro vehículo
        if (!nuevaPlaca.equalsIgnoreCase(placaAntigua) && 
            controladorClientes.obtenerTodosClientes().stream()
                .filter(c -> !c.getDpi().equals(dpiCliente))
                .flatMap(c -> c.getVehiculos().stream())
                .anyMatch(v -> v.getPlaca().equalsIgnoreCase(nuevaPlaca))) {
            return false;
        }

        // Actualizar el vehículo
        boolean actualizado = controladorClientes.actualizarVehiculo(
            dpiCliente, placaAntigua, nuevaPlaca, nuevaMarca, nuevoModelo, nuevaRutaFoto);

        if (actualizado) {
            controladorClientes.guardarClientes();
            return true;
        }
        return false;
    } catch (Exception e) {
        System.err.println("Error al actualizar vehículo: " + e.getMessage());
        return false;
    }
}
    public boolean eliminarVehiculo(String dpiCliente, String placa) {
        try {
            // Buscar el cliente por DPI
            var cliente = controladorClientes.buscarClientePorDPI(dpiCliente);
            if (cliente == null) {
                return false;
            }

            // Eliminar el vehículo por placa
            boolean eliminado = cliente.eliminarVehiculo(placa);
            
            if (eliminado) {
                // Guardar los cambios
                controladorClientes.guardarClientes();
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error al eliminar vehículo: " + e.getMessage());
            return false;
        }
    }

    public List<Vehiculo> obtenerVehiculosPorCliente(String dpiCliente) {
        var cliente = controladorClientes.buscarClientePorDPI(dpiCliente);
        if (cliente != null) {
            return cliente.getVehiculos();
        }
        return List.of();
    }

    public boolean existePlaca(String placa) {
        return controladorClientes.obtenerTodosClientes().stream()
            .flatMap(c -> c.getVehiculos().stream())
            .anyMatch(v -> v.getPlaca().equalsIgnoreCase(placa));
    }
}