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

public class Mecanico implements Serializable {
    private final int id;
    private boolean disponible;
    private OrdenTrabajo ordenActual;

    public Mecanico(int id) {
        this.id = id;
        this.disponible = true;
        this.ordenActual = null;
    }

    public int getId() {
        return id;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void asignarOrden(OrdenTrabajo orden) {
        this.ordenActual = orden;
        this.disponible = false;
    }

    public void liberar() {
        this.ordenActual = null;
        this.disponible = true;
    }

    public OrdenTrabajo getOrdenActual() {
        return ordenActual;
    }
}
