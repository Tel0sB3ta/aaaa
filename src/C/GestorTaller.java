/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package C;

/**
 *
 * @author gervi
 */
import M.*;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import javax.swing.JOptionPane;

public class GestorTaller {
    private static GestorTaller instancia;
    private final Vector<Mecanico> mecanicos;
    private final BlockingQueue<OrdenTrabajo> colaEspera;
    private final Vector<OrdenTrabajo> ordenesCompletadas;
    private final Vector<Factura> facturasGeneradas;
    
    // Tiempos para la sección D
    private static final int TIEMPO_ESPERA = 11; // segundos
    private static final int TIEMPO_SERVICIO = 5; // segundos
    private static final int TIEMPO_LISTO = 2; // segundos

    private GestorTaller() {
        mecanicos = new Vector<>();
        colaEspera = new LinkedBlockingQueue<>();
        ordenesCompletadas = new Vector<>();
        facturasGeneradas = new Vector<>();
        
        // Crear 3 mecánicos
        for (int i = 1; i <= 3; i++) {
            mecanicos.add(new Mecanico(i));
        }
        
        // Iniciar hilo para procesar órdenes
        new Thread(this::procesarOrdenes).start();
    }
    
    public static synchronized GestorTaller getInstancia() {
        if (instancia == null) {
            instancia = new GestorTaller();
        }
        return instancia;
    }


    public void agregarOrden(OrdenTrabajo orden) {
        // Verificar si el cliente es Oro para priorizar
        if (orden.getCliente().getTipo().equalsIgnoreCase("Oro")) {
            // Insertar al frente de la cola
            colaEspera.add(orden);
            // Reorganizar la cola para poner al cliente Oro al frente
            reordenarCola();
        } else {
            colaEspera.add(orden);
        }
        
        orden.setEstado("En espera");
    }

    private void reordenarCola() {
        // Buscar el último cliente Oro en la cola y moverlo al frente
        OrdenTrabajo ultimoOro = null;
        for (OrdenTrabajo orden : colaEspera) {
            if (orden.getCliente().getTipo().equalsIgnoreCase("Oro")) {
                ultimoOro = orden;
            }
        }
        
        if (ultimoOro != null) {
            colaEspera.remove(ultimoOro);
            colaEspera.add(ultimoOro);
        }
    }

    private void procesarOrdenes() {
        while (true) {
            try {
                // Esperar si no hay órdenes o todos los mecánicos están ocupados
                if (colaEspera.isEmpty() || todosMecanicosOcupados()) {
                    Thread.sleep(1000);
                    continue;
                }

                // Tomar la siguiente orden (la cola maneja la prioridad)
                OrdenTrabajo orden = colaEspera.take();
                
                // Asignar a un mecánico disponible
                Mecanico mecanicoDisponible = obtenerMecanicoDisponible();
                if (mecanicoDisponible != null) {
                    mecanicoDisponible.asignarOrden(orden);
                    orden.setMecanico(mecanicoDisponible);
                    orden.setEstado("En servicio");
                    
                    // Iniciar hilo para procesar el servicio
                    new Thread(() -> procesarServicio(orden)).start();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private boolean todosMecanicosOcupados() {
        for (Mecanico m : mecanicos) {
            if (m.isDisponible()) {
                return false;
            }
        }
        return true;
    }

    private Mecanico obtenerMecanicoDisponible() {
        for (Mecanico m : mecanicos) {
            if (m.isDisponible()) {
                return m;
            }
        }
        return null;
    }

private void procesarServicio(OrdenTrabajo orden) {
        try {
            // Simular tiempo en cola de espera (ya se hizo antes de asignar)
            TimeUnit.SECONDS.sleep(TIEMPO_ESPERA);
            
            // Verificar si es un diagnóstico
            if (orden.getServicio().getNombreServicio().equalsIgnoreCase("Diagnóstico")) {
                Servicio servicioDiagnosticado = diagnosticarServicio(orden.getVehiculo());
                orden.setServicio(servicioDiagnosticado);
                
                // Mostrar al cliente el servicio diagnosticado
                mostrarDiagnosticoAlCliente(orden, servicioDiagnosticado);
                
                // Actualizar factura con el nuevo servicio
                Factura facturaActualizada = new Factura(orden);
                facturasGeneradas.removeIf(f -> f.getOrden().equals(orden));
                facturasGeneradas.add(facturaActualizada);
                mostrarFactura(facturaActualizada);
            }
            
            // Simular tiempo de servicio
            TimeUnit.SECONDS.sleep(TIEMPO_SERVICIO);
            
            // Completar el servicio
            orden.setEstado("Completado");
            orden.getMecanico().liberar();
            
            // Incrementar servicios completados del cliente
            Cliente cliente = orden.getCliente();
            cliente.incrementarServicios();
            
            // Verificar promoción a cliente Oro (4 servicios para sección D)
            if (cliente.getServiciosCompletados() >= 4 && !cliente.getTipo().equalsIgnoreCase("Oro")) {
                cliente.setTipo("Oro");
                JOptionPane.showMessageDialog(null, 
                    "¡Felicidades! El cliente " + cliente.getNombreCompleto() + 
                    " ha sido promovido a Cliente Oro por completar " + 
                    cliente.getServiciosCompletados() + " servicios.",
                    "Promoción a Cliente Oro", JOptionPane.INFORMATION_MESSAGE);
            }
            
            // Mover a lista de completados
            ordenesCompletadas.add(orden);
            
            // Simular tiempo de entrega
            TimeUnit.SECONDS.sleep(TIEMPO_LISTO);
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
private void mostrarFactura(Factura factura) {
        String mensaje = "FACTURA GENERADA\n\n" +
                         "Número: " + factura.getNumeroFactura() + "\n" +
                         "Cliente: " + factura.getOrden().getCliente().getNombreCompleto() + "\n" +
                         "Vehículo: " + factura.getOrden().getVehiculo().getPlaca() + "\n" +
                         "Servicio: " + factura.getOrden().getServicio().getNombreServicio() + "\n" +
                         "Total: Q" + factura.getOrden().getServicio().getPrecioTotal() + "\n" +
                         "Estado: Pendiente de pago";
        
        JOptionPane.showMessageDialog(null, mensaje, "Factura Generada", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarDiagnosticoAlCliente(OrdenTrabajo orden, Servicio servicioDiagnosticado) {
        String mensaje = "DIAGNÓSTICO COMPLETO\n\n" +
                         "Vehículo: " + orden.getVehiculo().getPlaca() + "\n" +
                         "Servicio recomendado: " + servicioDiagnosticado.getNombreServicio() + "\n" +
                         "Precio estimado: Q" + servicioDiagnosticado.getPrecioTotal();
        
        JOptionPane.showMessageDialog(null, mensaje, "Resultado de Diagnóstico", JOptionPane.INFORMATION_MESSAGE);
    }
    private Servicio diagnosticarServicio(Vehiculo vehiculo) {
        Vector<Servicio> serviciosDisponibles = ControladorServicios.getInstancia().getServiciosDisponibles();
        Vector<Servicio> serviciosCompatibles = new Vector<>();
        
        // Filtrar servicios compatibles con el vehículo
        for (Servicio servicio : serviciosDisponibles) {
            if (servicio.getMarca().equalsIgnoreCase("cualquiera") || 
                servicio.getMarca().equalsIgnoreCase(vehiculo.getMarca())) {
                if (servicio.getModelo().equalsIgnoreCase("cualquiera") || 
                    servicio.getModelo().equalsIgnoreCase(vehiculo.getModelo())) {
                    serviciosCompatibles.add(servicio);
                }
            }
        }
        
        // Seleccionar aleatoriamente un servicio compatible
        if (!serviciosCompatibles.isEmpty()) {
            int indiceAleatorio = (int) (Math.random() * serviciosCompatibles.size());
            return serviciosCompatibles.get(indiceAleatorio);
        }
        
        // Si no hay servicios compatibles, devolver uno genérico (debería haber al menos uno)
        return serviciosDisponibles.get(0);
    }

    public Vector<OrdenTrabajo> getOrdenesEnEspera() {
        Vector<OrdenTrabajo> enEspera = new Vector<>();
        enEspera.addAll(colaEspera);
        return enEspera;
    }

    public Vector<OrdenTrabajo> getOrdenesEnServicio() {
        Vector<OrdenTrabajo> enServicio = new Vector<>();
        for (Mecanico m : mecanicos) {
            if (!m.isDisponible()) {
                enServicio.add(m.getOrdenActual());
            }
        }
        return enServicio;
    }

    public Vector<OrdenTrabajo> getOrdenesCompletadas() {
        return new Vector<>(ordenesCompletadas);
    }
}