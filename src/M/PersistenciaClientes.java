/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package M;

/**
 *
 * @author gervi
 */
import java.io.*;
import java.util.Vector;

public class PersistenciaClientes {
    private static final String ARCHIVO = "clientes.dat";

    public static void guardarClientes(Vector<Cliente> clientes) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(ARCHIVO))) {
            out.writeObject(clientes);
        }
    }

    public static Vector<Cliente> cargarClientes() throws IOException, ClassNotFoundException {
        File archivo = new File(ARCHIVO);
        if (!archivo.exists()) {
            return new Vector<>();
        }
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(ARCHIVO))) {
            return (Vector<Cliente>) in.readObject();
        }
    }
}

