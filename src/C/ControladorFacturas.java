/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package C;

/**
 *
 * @author gervi
 */
import M.Factura;
import java.util.Vector;

public class ControladorFacturas {
    private static ControladorFacturas instancia;
    private final Vector<Factura> facturas;

    private ControladorFacturas() {
        facturas = new Vector<>();
    }

    public static synchronized ControladorFacturas getInstancia() {
        if (instancia == null) {
            instancia = new ControladorFacturas();
        }
        return instancia;
    }

    public void agregarFactura(Factura factura) {
        facturas.add(factura);
    }

    public Vector<Factura> getFacturasPorCliente(String dpi) {
        Vector<Factura> facturasCliente = new Vector<>();
        for (Factura f : facturas) {
            if (f.getOrden().getCliente().getDpi().equals(dpi)) {
                facturasCliente.add(f);
            }
        }
        return facturasCliente;
    }

    public void marcarComoPagada(int numeroFactura) {
        for (Factura f : facturas) {
            if (f.getNumeroFactura() == numeroFactura) {
                f.setPagada(true);
                break;
            }
        }
    }
}
