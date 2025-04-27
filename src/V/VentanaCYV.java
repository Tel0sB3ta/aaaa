/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package V;
import C.ControladorClientes;
import C.ControladorVehiculos;
import M.Cliente;
import M.Vehiculo;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author gervi
 */
public class VentanaCYV extends javax.swing.JFrame {
    private ControladorClientes controlador;
    private ControladorVehiculos controladorVehiculos;
    private DefaultListModel<String> listModelVehiculos;
    private Cliente clienteActual;
    
    public VentanaCYV() {
        initComponents();
        controlador = new ControladorClientes();
        controladorVehiculos = new ControladorVehiculos(controlador);
        listModelVehiculos = new DefaultListModel<>();
        jList1.setModel(listModelVehiculos);
        jRadioButton1.setSelected(true);
        configurarTablaClientes();
        cargarClientesDesdeArchivo();
        this.setLocationRelativeTo(null);
        this.setTitle("Gestión de Clientes y Vehículos");
    }
    
    private void configurarTablaClientes() {
        DefaultTableModel model = new DefaultTableModel(
            new Object[]{"DPI", "Nombre", "Usuario", "Tipo", "Vehículos"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        jTable1.setModel(model);
    }
    
    private void cargarClientesDesdeArchivo() {
        try {
            controlador.ordenarClientesPorDPI();
            mostrarClientesEnTabla();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar clientes: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void mostrarClientesEnTabla() {
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);
        
        for (Cliente cliente : controlador.obtenerTodosClientes()) {
            model.addRow(new Object[]{
                cliente.getDpi(),
                cliente.getNombreCompleto(),
                cliente.getUsuario(),
                cliente.getTipo(),
                cliente.getVehiculos().size()
            });
        }
    }
    
    private void buscarCliente() {
        String dpi = jTextField1.getText().trim();
        if (dpi.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un DPI para buscar", 
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        clienteActual = controlador.buscarClientePorDPI(dpi);
        if (clienteActual != null) {
            mostrarDatosCliente();
        } else {
            JOptionPane.showMessageDialog(this, "Cliente no encontrado", 
                "No encontrado", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void mostrarDatosCliente() {
        jTextField2.setText(clienteActual.getDpi());
        jTextField3.setText(clienteActual.getUsuario());
        jTextField4.setText(clienteActual.getNombreCompleto());
        jTextField5.setText(clienteActual.getContraseña()); // Mostrar contraseña
        
        if (clienteActual.getTipo().equalsIgnoreCase("Oro")) {
            jRadioButton2.setSelected(true);
        } else {
            jRadioButton1.setSelected(true);
        }
        
        actualizarListaVehiculos();
    }
    
private void actualizarListaVehiculos() {
        listModelVehiculos.clear();
        if (clienteActual != null) {
            controladorVehiculos.obtenerVehiculosPorCliente(clienteActual.getDpi())
                .forEach(v -> listModelVehiculos.addElement(v.getPlaca()));
        }
    }
    
    private void guardarCliente() {
        try {
            String dpi = jTextField2.getText().trim();
            String usuario = jTextField3.getText().trim();
            String nombre = jTextField4.getText().trim();
            String contraseña = jTextField5.getText().trim();
            String tipo = jRadioButton2.isSelected() ? "Oro" : "Normal";
            
            if (dpi.isEmpty() || usuario.isEmpty() || nombre.isEmpty() || contraseña.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!dpi.matches("\\d{13}")) {
                JOptionPane.showMessageDialog(this, "El DPI debe tener 13 dígitos numéricos", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (clienteActual == null) {
                boolean registrado = controlador.registrarCliente(dpi, nombre, usuario, contraseña);
                if (registrado) {
                    clienteActual = controlador.buscarClientePorDPI(dpi);
                    clienteActual.setTipo(tipo);
                    controlador.guardarClientes();
                    JOptionPane.showMessageDialog(this, "Cliente registrado exitosamente");
                    cargarClientesDesdeArchivo();
                }
            } else {
                clienteActual.setNombreCompleto(nombre);
                clienteActual.setUsuario(usuario);
                clienteActual.setContraseña(contraseña);
                clienteActual.setTipo(tipo);
                controlador.guardarClientes();
                JOptionPane.showMessageDialog(this, "Cliente actualizado exitosamente");
                cargarClientesDesdeArchivo();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al guardar cliente: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void eliminarCliente() {
        if (clienteActual == null) {
            JOptionPane.showMessageDialog(this, "No hay cliente seleccionado", 
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Está seguro de eliminar este cliente?", "Confirmar", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean eliminado = controlador.eliminarCliente(clienteActual.getDpi());
            if (eliminado) {
                limpiarCampos();
                JOptionPane.showMessageDialog(this, "Cliente eliminado exitosamente");
                cargarClientesDesdeArchivo();
            }
        }
    }
    
    private void limpiarCampos() {
        jTextField1.setText("");
        jTextField2.setText("");
        jTextField3.setText("");
        jTextField4.setText("");
        jTextField5.setText("");
        jRadioButton1.setSelected(true);
        listModelVehiculos.clear();
        clienteActual = null;
    }
    
private void mostrarDialogoVehiculo(boolean esNuevo, int indiceVehiculo) {
    class DialogData {
        String placa;
        String marca;
        String modelo;
        String rutaFoto;
        
        void cargarDatosVehiculo(Vehiculo vehiculo) {
            this.placa = vehiculo.getPlaca();
            this.marca = vehiculo.getMarca();
            this.modelo = vehiculo.getModelo();
            this.rutaFoto = vehiculo.getRutaFoto();
        }
    }
    
    JPanel panel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.fill = GridBagConstraints.HORIZONTAL;
    
    JTextField txtPlaca = new JTextField(15);
    JTextField txtMarca = new JTextField(15);
    JTextField txtModelo = new JTextField(15);
    JLabel lblFoto = new JLabel("Sin imagen seleccionada", SwingConstants.CENTER);
    lblFoto.setPreferredSize(new Dimension(150, 150));
    JButton btnSeleccionarFoto = new JButton("Seleccionar Foto");
    JButton btnGuardar = new JButton("Guardar");
    JButton btnEliminar = new JButton("Eliminar");
    DialogData datos = new DialogData();
    
    // Configurar acción al presionar Enter en cualquier campo
    KeyListener enterKeyListener = new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                btnGuardar.doClick();
            }
        }
    };
    
    txtPlaca.addKeyListener(enterKeyListener);
    txtMarca.addKeyListener(enterKeyListener);
    txtModelo.addKeyListener(enterKeyListener);
    
    // Panel para contener la imagen con scroll
    JScrollPane scrollPane = new JScrollPane(lblFoto);
    scrollPane.setPreferredSize(new Dimension(150, 150));
    
    if (!esNuevo && clienteActual != null && indiceVehiculo >= 0) {
        Vehiculo vehiculo = clienteActual.getVehiculos().get(indiceVehiculo);
        datos.cargarDatosVehiculo(vehiculo);
        txtPlaca.setText(datos.placa);
        txtMarca.setText(datos.marca);
        txtModelo.setText(datos.modelo);
        if (datos.rutaFoto != null && !datos.rutaFoto.isEmpty()) {
            cargarImagen(datos.rutaFoto, lblFoto);
        }
    } else {
        // Ocultar botón Eliminar si es nuevo vehículo
        btnEliminar.setVisible(false);
    }
    
    btnSeleccionarFoto.addActionListener(e -> {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "Imágenes (PNG, JPG, JPEG)", "png", "jpg", "jpeg");
        fileChooser.setFileFilter(filter);
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            datos.rutaFoto = fileChooser.getSelectedFile().getAbsolutePath();
            cargarImagen(datos.rutaFoto, lblFoto);
        }
    });
    
    // Configurar acción del botón Guardar
    btnGuardar.addActionListener(e -> {
    String placa = txtPlaca.getText().trim();
    String marca = txtMarca.getText().trim();
    String modelo = txtModelo.getText().trim();
    
    if (placa.isEmpty() || marca.isEmpty() || modelo.isEmpty()) {
        JOptionPane.showMessageDialog(panel, "Placa, marca y modelo son obligatorios", 
            "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }
    
    if (!placa.matches("[A-Za-z0-9]{6,8}")) {
        JOptionPane.showMessageDialog(panel, 
            "Formato de placa inválido. Debe contener 6-8 caracteres alfanuméricos", 
            "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }
    
    // Verificar placa única solo si es nuevo o la placa cambió
    if (esNuevo || !placa.equalsIgnoreCase(datos.placa)) {
        if (controladorVehiculos.existePlaca(placa)) {
            JOptionPane.showMessageDialog(panel, 
                "La placa ya está registrada en otro vehículo", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    }
    
    // Crear o actualizar el vehículo
    Vehiculo vehiculo = new Vehiculo(placa, marca, modelo, datos.rutaFoto);
    
    try {
        if (esNuevo) {
            // Agregar nuevo vehículo
            controlador.agregarVehiculo(clienteActual.getDpi(), placa, marca, modelo, datos.rutaFoto);
        } else {
            // Actualizar vehículo existente
            // Primero eliminar el vehículo antiguo
            controladorVehiculos.eliminarVehiculo(clienteActual.getDpi(), datos.placa);
            // Luego agregar el vehículo actualizado
            controlador.agregarVehiculo(clienteActual.getDpi(), placa, marca, modelo, datos.rutaFoto);
        }
        
        // Actualizar la lista de vehículos
        actualizarListaVehiculos();
        
        // Cerrar el diálogo
        Window window = SwingUtilities.getWindowAncestor(panel);
        if (window != null) {
            window.dispose();
        }
        
        JOptionPane.showMessageDialog(panel, 
            esNuevo ? "Vehículo agregado exitosamente" : "Vehículo actualizado exitosamente",
            "Éxito", JOptionPane.INFORMATION_MESSAGE);
        
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(panel, 
            "Error al guardar el vehículo: " + ex.getMessage(),
            "Error", JOptionPane.ERROR_MESSAGE);
    }
});

    
    // Configurar acción del botón Eliminar
    btnEliminar.addActionListener(e -> {
        int confirm = JOptionPane.showConfirmDialog(panel, 
            "¿Está seguro de eliminar este vehículo?", "Confirmar", 
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean eliminado = controladorVehiculos.eliminarVehiculo(
                    clienteActual.getDpi(), datos.placa);
                
                if (eliminado) {
                    actualizarListaVehiculos();
                    
                    // Cerrar el diálogo después de eliminar
                    Window window = SwingUtilities.getWindowAncestor(panel);
                    if (window != null) {
                        window.dispose();
                    }
                    
                    JOptionPane.showMessageDialog(panel, 
                        "Vehículo eliminado exitosamente",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(panel, 
                        "No se pudo eliminar el vehículo",
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, 
                    "Error al eliminar el vehículo: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    });
    
    // Añadir componentes con GridBagLayout
    gbc.gridx = 0;
    gbc.gridy = 0;
    panel.add(new JLabel("Placa:"), gbc);
    
    gbc.gridx = 1;
    panel.add(txtPlaca, gbc);
    
    gbc.gridx = 0;
    gbc.gridy = 1;
    panel.add(new JLabel("Marca:"), gbc);
    
    gbc.gridx = 1;
    panel.add(txtMarca, gbc);
    
    gbc.gridx = 0;
    gbc.gridy = 2;
    panel.add(new JLabel("Modelo:"), gbc);
    
    gbc.gridx = 1;
    panel.add(txtModelo, gbc);
    
    gbc.gridx = 0;
    gbc.gridy = 3;
    panel.add(new JLabel("Foto:"), gbc);
    
    gbc.gridx = 1;
    panel.add(scrollPane, gbc);
    
    gbc.gridx = 0;
    gbc.gridy = 4;
    gbc.gridwidth = 2;
    gbc.fill = GridBagConstraints.CENTER;
    panel.add(btnSeleccionarFoto, gbc);
    
    // Panel para botones Guardar y Eliminar
    JPanel panelBotones = new JPanel(new GridLayout(1, 2, 10, 0));
    panelBotones.add(btnGuardar);
    if (!esNuevo) {
        panelBotones.add(btnEliminar);
    }
    
    gbc.gridy = 5;
    panel.add(panelBotones, gbc);
    
    // Crear el diálogo con tamaño fijo
    JDialog dialog = new JDialog();
    dialog.setTitle(esNuevo ? "Agregar Vehículo" : "Modificar Vehículo");
    dialog.setModal(true);
    dialog.getContentPane().add(panel);
    dialog.pack();
    dialog.setSize(400, 450);
    dialog.setLocationRelativeTo(this);
    dialog.setResizable(false);
    dialog.setVisible(true);
}


// Método auxiliar para cargar la imagen
private void cargarImagen(String rutaFoto, JLabel lblFoto) {
    try {
        ImageIcon icon = new ImageIcon(rutaFoto);
        // Escalar la imagen manteniendo el aspect ratio
        Image img = icon.getImage();
        int ancho = lblFoto.getWidth();
        int alto = lblFoto.getHeight();
        
        if (ancho <= 0) ancho = 150;
        if (alto <= 0) alto = 150;
        
        // Calcular dimensiones manteniendo proporciones
        double ratio = Math.min((double)ancho / icon.getIconWidth(), 
                              (double)alto / icon.getIconHeight());
        int nuevoAncho = (int)(icon.getIconWidth() * ratio);
        int nuevoAlto = (int)(icon.getIconHeight() * ratio);
        
        Image scaledImg = img.getScaledInstance(nuevoAncho, nuevoAlto, Image.SCALE_SMOOTH);
        lblFoto.setIcon(new ImageIcon(scaledImg));
        lblFoto.setText("");
    } catch (Exception ex) {
        lblFoto.setText("Error cargando imagen");
        lblFoto.setIcon(null);
    }
}
    
private void agregarVehiculo() {
    if (clienteActual == null) {
        JOptionPane.showMessageDialog(this, "No hay cliente seleccionado", 
            "Advertencia", JOptionPane.WARNING_MESSAGE);
        return;
    }
    mostrarDialogoVehiculo(true, -1); // -1 indica que es un vehículo nuevo
}
    
    private void eliminarVehiculo() {
        if (clienteActual == null) {
            JOptionPane.showMessageDialog(this, "No hay cliente seleccionado", 
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int indice = jList1.getSelectedIndex();
        if (indice == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un vehículo para eliminar", 
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Vehiculo vehiculo = clienteActual.getVehiculos().get(indice);
        String mensaje = String.format(
            "¿Está seguro de eliminar este vehículo?\n\n" +
            "Placa: %s\nMarca: %s\nModelo: %s",
            vehiculo.getPlaca(), vehiculo.getMarca(), vehiculo.getModelo());

        int confirm = JOptionPane.showConfirmDialog(this, 
            mensaje, "Confirmar Eliminación", 
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean eliminado = controladorVehiculos.eliminarVehiculo(
                clienteActual.getDpi(), vehiculo.getPlaca());
            
            if (eliminado) {
                actualizarListaVehiculos();
                JOptionPane.showMessageDialog(this, 
                    "Vehículo eliminado exitosamente", 
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Error al eliminar el vehículo", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void cargarArchivoTMCA() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "Archivos TMCA (*.tmca)", "tmca");
        fileChooser.setFileFilter(filter);
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    String[] partes = linea.split("-");
                    if (partes.length >= 6) {
                        String dpi = partes[0];
                        String nombre = partes[1];
                        String usuario = partes[2];
                        String contraseña = partes[3];
                        String tipo = partes[4];
                        String vehiculosStr = partes[5];
                        
                        controlador.registrarCliente(dpi, nombre, usuario, contraseña);
                        Cliente cliente = controlador.buscarClientePorDPI(dpi);
                        if (cliente != null) {
                            cliente.setTipo(tipo);
                            
                            String[] vehiculos = vehiculosStr.split(";");
                            for (String vehiculoStr : vehiculos) {
                                String[] datosVehiculo = vehiculoStr.split(",");
                                if (datosVehiculo.length >= 4) {
                                    String placa = datosVehiculo[0].trim();
                                    String marca = datosVehiculo[1].trim();
                                    String modelo = datosVehiculo[2].trim();
                                    String rutaFoto = datosVehiculo[3].trim();
                                    
                                    Vehiculo vehiculo = new Vehiculo(placa, marca, modelo, rutaFoto);
                                    cliente.agregarVehiculo(vehiculo);
                                }
                            }
                        }
                    }
                }
                
                controlador.guardarClientes();
                JOptionPane.showMessageDialog(this, "Clientes cargados exitosamente desde el archivo");
                cargarClientesDesdeArchivo();
                limpiarCampos();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al procesar archivo: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void ordenarVehiculos(boolean ascendente) {
        if (clienteActual == null) {
            JOptionPane.showMessageDialog(this, "No hay cliente seleccionado", 
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (clienteActual.getVehiculos().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El cliente no tiene vehículos para ordenar", 
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            clienteActual.ordenarVehiculos(ascendente);
            controlador.guardarClientes();
            actualizarListaVehiculos();
            JOptionPane.showMessageDialog(this, "Vehículos ordenados " + (ascendente ? "ascendentemente" : "descendentemente"));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al ordenar vehículos: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        jLabel7 = new javax.swing.JLabel();
        jButton6 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(204, 204, 204));

        jButton1.setBackground(new java.awt.Color(204, 204, 204));
        jButton1.setFont(new java.awt.Font("Dialog", 3, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(0, 0, 0));
        jButton1.setText("REGRESAR");
        jButton1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 0));
        jLabel1.setText("CUI Cliente:");

        jTextField1.setBackground(new java.awt.Color(255, 255, 255));
        jTextField1.setForeground(new java.awt.Color(0, 0, 0));

        jButton2.setBackground(new java.awt.Color(255, 255, 255));
        jButton2.setFont(new java.awt.Font("Dialog", 3, 14)); // NOI18N
        jButton2.setForeground(new java.awt.Color(0, 0, 0));
        jButton2.setText("BUSCAR POR CUI");
        jButton2.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 0, 0));
        jLabel2.setText("CUI");

        jTextField2.setBackground(new java.awt.Color(255, 255, 255));
        jTextField2.setForeground(new java.awt.Color(0, 0, 0));

        jLabel3.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 0, 0));
        jLabel3.setText("Usuario");

        jTextField3.setBackground(new java.awt.Color(255, 255, 255));
        jTextField3.setForeground(new java.awt.Color(0, 0, 0));
        jTextField3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField3ActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 0, 0));
        jLabel4.setText("Nombre completo");

        jTextField4.setBackground(new java.awt.Color(255, 255, 255));
        jTextField4.setForeground(new java.awt.Color(0, 0, 0));

        jLabel5.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 0, 0));
        jLabel5.setText("Contraseña");

        jTextField5.setBackground(new java.awt.Color(255, 255, 255));
        jTextField5.setForeground(new java.awt.Color(0, 0, 0));

        jLabel6.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 0, 0));
        jLabel6.setText("VEHICULOS");

        jButton3.setBackground(new java.awt.Color(255, 255, 255));
        jButton3.setFont(new java.awt.Font("Dialog", 3, 14)); // NOI18N
        jButton3.setForeground(new java.awt.Color(0, 0, 0));
        jButton3.setText("MODIFICAR");
        jButton3.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setBackground(new java.awt.Color(255, 255, 255));
        jButton4.setFont(new java.awt.Font("Dialog", 3, 14)); // NOI18N
        jButton4.setForeground(new java.awt.Color(0, 0, 0));
        jButton4.setText("GUARDAR");
        jButton4.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setBackground(new java.awt.Color(255, 255, 255));
        jButton5.setFont(new java.awt.Font("Dialog", 3, 14)); // NOI18N
        jButton5.setForeground(new java.awt.Color(0, 0, 0));
        jButton5.setText("ELIMINAR");
        jButton5.setToolTipText("");
        jButton5.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jList1.setBackground(new java.awt.Color(255, 255, 255));
        jList1.setBorder(new javax.swing.border.MatteBorder(null));
        jList1.setForeground(new java.awt.Color(0, 0, 0));
        jList1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jList1MouseClicked(evt);
            }
        });
        jList1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jList1KeyPressed(evt);
            }
        });
        jScrollPane2.setViewportView(jList1);

        jLabel7.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(0, 0, 0));
        jLabel7.setText("Tipo de cliente");

        jButton6.setBackground(new java.awt.Color(255, 255, 255));
        jButton6.setFont(new java.awt.Font("Dialog", 3, 14)); // NOI18N
        jButton6.setForeground(new java.awt.Color(0, 0, 0));
        jButton6.setText("ORDENAR");
        jButton6.setToolTipText("");
        jButton6.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Repuesto", "Marca", "Modelo", "Existencias", "Precio"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Double.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jRadioButton1.setBackground(new java.awt.Color(204, 204, 204));
        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setForeground(new java.awt.Color(0, 0, 0));
        jRadioButton1.setText("Normal");
        jRadioButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton1ActionPerformed(evt);
            }
        });

        jRadioButton2.setBackground(new java.awt.Color(204, 204, 204));
        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setForeground(new java.awt.Color(0, 0, 0));
        jRadioButton2.setText("Oro");
        jRadioButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton2ActionPerformed(evt);
            }
        });

        jButton7.setBackground(new java.awt.Color(255, 255, 255));
        jButton7.setFont(new java.awt.Font("Dialog", 3, 14)); // NOI18N
        jButton7.setForeground(new java.awt.Color(0, 0, 0));
        jButton7.setText("CARGAR");
        jButton7.setToolTipText("");
        jButton7.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton8.setBackground(new java.awt.Color(255, 255, 255));
        jButton8.setFont(new java.awt.Font("Dialog", 3, 14)); // NOI18N
        jButton8.setForeground(new java.awt.Color(0, 0, 0));
        jButton8.setText("AGREGAR");
        jButton8.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(17, 17, 17)
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addContainerGap()
                                                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(64, 64, 64)
                                                .addComponent(jLabel2)))
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jLabel3)
                                                .addGap(55, 55, 55)))
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(12, 12, 12)
                                                .addComponent(jLabel4))))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(30, 30, 30)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel7)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(6, 6, 6)
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(jRadioButton2)
                                                    .addComponent(jRadioButton1))))))
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(62, 62, 62)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(45, 45, 45)
                                        .addComponent(jLabel5)))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel6)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGap(11, 11, 11))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jButton1))
                            .addComponent(jScrollPane1))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2)
                    .addComponent(jButton6)
                    .addComponent(jButton7))
                .addGap(33, 33, 33)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6)
                            .addComponent(jButton8))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jButton4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton5))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jRadioButton1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jRadioButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        VentanaAdmin v1 = new VentanaAdmin();
        v1.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
buscarCliente();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        if (clienteActual == null) {
            JOptionPane.showMessageDialog(this, "No hay cliente seleccionado", 
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        mostrarDialogoVehiculo(false, jList1.getSelectedIndex());
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
guardarCliente();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
  eliminarCliente();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        if (clienteActual == null) {
            JOptionPane.showMessageDialog(this, "No hay cliente seleccionado", 
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Object[] options = {"Ascendente", "Descendente"};
        int opcion = JOptionPane.showOptionDialog(this, 
            "Seleccione el orden de los vehículos:", 
            "Ordenar Vehículos", 
            JOptionPane.DEFAULT_OPTION, 
            JOptionPane.QUESTION_MESSAGE, 
            null, options, options[0]);
        
        if (opcion == 0) {
            ordenarVehiculos(true);
        } else if (opcion == 1) {
            ordenarVehiculos(false);
        }                                                                           
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jList1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jList1MouseClicked
    if (evt.getClickCount() == 2) { // Doble clic para modificar
        if (clienteActual != null && jList1.getSelectedIndex() != -1) {
            mostrarDialogoVehiculo(false, jList1.getSelectedIndex());
        }
    } else if (evt.getClickCount() == 1 && jList1.getSelectedIndex() == -1) {
        // Un clic en área vacía para agregar nuevo
        agregarVehiculo();
    }
    }//GEN-LAST:event_jList1MouseClicked

    private void jList1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jList1KeyPressed
    if (evt.getKeyCode() == KeyEvent.VK_DELETE && jList1.getSelectedIndex() != -1) {
        eliminarVehiculo();
    } else if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
        if (jList1.getSelectedIndex() != -1) {
            mostrarDialogoVehiculo(false, jList1.getSelectedIndex());
        } else {
            agregarVehiculo();
        }
    } else if (evt.getKeyCode() == KeyEvent.VK_INSERT) {
        agregarVehiculo();
    }
    }//GEN-LAST:event_jList1KeyPressed

    private void jTextField3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField3ActionPerformed

    private void jRadioButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton1ActionPerformed

    }//GEN-LAST:event_jRadioButton1ActionPerformed

    private void jRadioButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton2ActionPerformed

    }//GEN-LAST:event_jRadioButton2ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        cargarArchivoTMCA();
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
    agregarVehiculo();
    }//GEN-LAST:event_jButton8ActionPerformed



    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new VentanaCYV().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JList<String> jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    // End of variables declaration//GEN-END:variables
}
