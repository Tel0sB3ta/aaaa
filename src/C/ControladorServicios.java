/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package C;

/**
 *
 * @author gervi
 */
import M.Repuesto;
import M.Servicio;
import M.Vehiculo;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

public class ControladorServicios {
    private static ControladorServicios instancia;
    private final Vector<Servicio> servicios;
    private final Vector<Repuesto> repuestosDisponibles;
    private static final String ARCHIVO_SERVICIOS = "src/resources/servicios.tms";

    private ControladorServicios() {
        servicios = new Vector<>();
        repuestosDisponibles = new Vector<>();
        cargarServiciosDesdeArchivo();
    }

    public static synchronized ControladorServicios getInstancia() {
        if (instancia == null) {
            instancia = new ControladorServicios();
        }
        return instancia;
    }

    private void cargarServiciosDesdeArchivo() {
        try (BufferedReader br = new BufferedReader(new FileReader(ARCHIVO_SERVICIOS))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split("-");
                if (partes.length >= 5) {
                    String id = partes[0].trim();
                    String nombreServicio = partes[1].trim();
                    String marca = partes[2].trim();
                    String modelo = partes[3].trim();
                    
                    // Procesar lista de repuestos
                    String[] codigosRepuestos = partes[4].split(";");
                    Vector<Repuesto> repuestos = new Vector<>();
                    
                    for (String codigo : codigosRepuestos) {
                        Repuesto repuesto = buscarRepuestoPorCodigo(codigo.trim());
                        if (repuesto != null) {
                            repuestos.add(repuesto);
                        }
                    }
                    
                    double precioManoDeObra = Double.parseDouble(partes[5].trim());
                    
                    // Crear y agregar servicio
                    Servicio servicio = new Servicio(id, nombreServicio, marca, modelo, 
                                                      repuestos, precioManoDeObra);
                    servicios.add(servicio);
                }
            }
        } catch (IOException e) {
            System.err.println("Error al cargar servicios desde archivo: " + e.getMessage());
        }
    }

    private Repuesto buscarRepuestoPorCodigo(String codigo) {
        for (Repuesto r : repuestosDisponibles) {
            if (r.getCodigo().equalsIgnoreCase(codigo)) {
                return r;
            }
        }
        return null;
    }

    public Servicio buscarServicioPorNombre(String nombre) {
        for (Servicio s : servicios) {
            if (s.getNombreServicio().equalsIgnoreCase(nombre)) {
                return s;
            }
        }
        return null;
    }

    public Vector<Servicio> getServiciosDisponibles() {
        return new Vector<>(servicios);
    }
    
public Vector<Servicio> getServiciosCompatibles(Vehiculo vehiculo) {
    Vector<Servicio> compatibles = new Vector<>();
    for (Servicio s : servicios) {
        if (s.getMarca().equalsIgnoreCase("cualquiera") || 
            s.getMarca().equalsIgnoreCase(vehiculo.getMarca())) {
            if (s.getModelo().equalsIgnoreCase("cualquiera") || 
                s.getModelo().equalsIgnoreCase(vehiculo.getModelo())) {
                compatibles.add(s);
            }
        }
    }
    return compatibles;
}
}
