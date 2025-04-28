/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package V;
import C.ControladorClientes;
import C.ControladorServicios;
import C.ControladorVehiculos;
import C.GestorTaller;
import M.Cliente;
import M.Factura;
import M.OrdenTrabajo;
import M.Servicio;
import M.Vehiculo;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
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
import javax.swing.Timer;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.text.NumberFormat;
/**
 *
 * @author gervi
 */
public class VentanaCliente extends javax.swing.JFrame {
    private ControladorClientes controlador;
    private ControladorVehiculos controladorVehiculos;
    private Cliente clienteActual;
    private DefaultTableModel tableModel;
    private Timer timerProgreso;

    public VentanaCliente(Cliente cliente) {
        initComponents();
        this.clienteActual = cliente;
        controlador = ControladorClientes.getInstancia();
        controladorVehiculos = new ControladorVehiculos(controlador);
        
        configurarTablaVehiculos();
        configurarTablaProgreso();
        cargarVehiculosCliente();
        configurarProgreso();
        
        this.setLocationRelativeTo(null);
        this.setTitle("Taller Mecánico USAC - Cliente: " + cliente.getNombreCompleto());
    }
    public class CustomTableCellRenderer extends DefaultTableCellRenderer {
    private NumberFormat numberFormat = NumberFormat.getCurrencyInstance();
    
    @Override
    protected void setValue(Object value) {
        if (value instanceof Number) {
            setText(numberFormat.format(value));
            setHorizontalAlignment(RIGHT);
        } else {
            setText(value == null ? "" : value.toString());
            setHorizontalAlignment(LEFT);
        }
    }
}
        private void configurarTablaVehiculos() {
        tableModel = new DefaultTableModel(
            new Object[]{"Placa", "Marca", "Modelo", "Imagen"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int column) {
                return String.class;
            }
        };
        
        jTable1.setModel(tableModel);
        
        // Configurar el renderizador personalizado
        CustomTableCellRenderer renderer = new CustomTableCellRenderer();
        for (int i = 0; i < jTable1.getColumnCount(); i++) {
            jTable1.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
        
        // Ajustar el ancho de las columnas
        jTable1.getColumnModel().getColumn(0).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(1).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(2).setPreferredWidth(150);
        jTable1.getColumnModel().getColumn(3).setPreferredWidth(200);
        
        // Listener para mostrar imágenes
        jTable1.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = jTable1.getSelectedRow();
                if (selectedRow != -1) {
                    String rutaFoto = (String) tableModel.getValueAt(selectedRow, 3);
                    mostrarImagenVehiculo(rutaFoto);
                }
            }
        });
    }

    private void mostrarFacturasCliente() {
    StringBuilder sb = new StringBuilder();
    sb.append("<html><h2>Facturas del Cliente</h2><ul>");
    
    // Obtener todas las facturas del cliente
    for (OrdenTrabajo orden : GestorTaller.getInstancia().getOrdenesCompletadas()) {
        if (orden.getCliente().equals(clienteActual)) {
            Factura factura = new Factura(orden);
            sb.append("<li>")
              .append("Orden #").append(orden.getNumeroOrden()).append(" - ")
              .append(orden.getServicio().getNombreServicio()).append(" - ")
              .append("Q").append(orden.getServicio().getPrecioTotal())
              .append("</li>");
        }
    }
    
    sb.append("</ul></html>");
    
    JOptionPane.showMessageDialog(this, sb.toString(), 
        "Facturas de " + clienteActual.getNombreCompleto(), 
        JOptionPane.INFORMATION_MESSAGE);
}
    private void configurarProgreso() {
        // Configurar combo boxes
        actualizarComboVehiculos();
        actualizarComboServicios();
        
        // Configurar timer para actualizar progreso cada segundo
        timerProgreso = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualizarProgreso();
            }
        });
        timerProgreso.start();
    }
    
    private void actualizarComboVehiculos() {
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        model.addElement(""); // Elemento vacío inicial
        
        for (Vehiculo v : clienteActual.getVehiculos()) {
            model.addElement(v.getPlaca());
        }
        jComboBox1.setModel(model);
        jComboBox1.setSelectedIndex(0); // Seleccionar el elemento vacío
    }
    
    private void actualizarComboServicios() {
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        model.addElement(""); // Elemento vacío inicial
        
        if (jComboBox1.getSelectedItem() != null && !jComboBox1.getSelectedItem().toString().isEmpty()) {
            String placa = (String) jComboBox1.getSelectedItem();
            Vehiculo vehiculo = obtenerVehiculoPorPlaca(placa);
            
            if (vehiculo != null) {
                Vector<Servicio> servicios = ControladorServicios.getInstancia()
                    .getServiciosCompatibles(vehiculo);
                
                for (Servicio s : servicios) {
                    model.addElement(s.getNombreServicio());
                }
            }
        }
        jComboBox2.setModel(model);
        jComboBox2.setSelectedIndex(0); // Seleccionar el elemento vacío
    }
    
    private Vehiculo obtenerVehiculoPorPlaca(String placa) {
        for (Vehiculo v : clienteActual.getVehiculos()) {
            if (v.getPlaca().equalsIgnoreCase(placa)) {
                return v;
            }
        }
        return null;
    }
    
    private void actualizarProgreso() {
        // Actualizar tablas de progreso
        Vector<OrdenTrabajo> enEspera = GestorTaller.getInstancia().getOrdenesEnEspera();
        Vector<OrdenTrabajo> enServicio = GestorTaller.getInstancia().getOrdenesEnServicio();
        Vector<OrdenTrabajo> completadas = GestorTaller.getInstancia().getOrdenesCompletadas();
        
        // Actualizar barras de progreso
        actualizarBarraProgreso(jProgressBar1, enEspera.size());
        actualizarBarraProgreso(jProgressBar2, enServicio.size());
        actualizarBarraProgreso(jProgressBar3, completadas.size());
        
        // Actualizar tabla de progreso
        DefaultTableModel model = (DefaultTableModel) jTable2.getModel();
        model.setRowCount(0);
        
        // Mostrar solo las órdenes del cliente actual
        agregarOrdenesATabla(model, enEspera, "En espera");
        agregarOrdenesATabla(model, enServicio, "En servicio");
        agregarOrdenesATabla(model, completadas, "Completado");
    }
    
    private void actualizarBarraProgreso(javax.swing.JProgressBar barra, int cantidad) {
        // Máximo 10 órdenes mostradas en la barra de progreso
        int valor = Math.min(cantidad * 10, 100);
        barra.setValue(valor);
        barra.setString(cantidad + " órdenes");
        barra.setStringPainted(true);
    }
    
    private void agregarOrdenesATabla(DefaultTableModel model, Vector<OrdenTrabajo> ordenes, String estado) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
        
        for (OrdenTrabajo orden : ordenes) {
            if (orden.getCliente().equals(clienteActual)) {
                model.addRow(new Object[]{
                    orden.getNumeroOrden(),
                    orden.getVehiculo().getPlaca(),
                    estado,
                    orden.getServicio().getNombreServicio(),
                    "Q" + currencyFormat.format(orden.getServicio().getPrecioTotal())
                });
            }
        }
    }
    
    private void solicitarServicio() {
    String placa = (String) jComboBox1.getSelectedItem();
    String nombreServicio = (String) jComboBox2.getSelectedItem();
    
    if (placa == null || placa.isEmpty() || nombreServicio == null || nombreServicio.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Seleccione un vehículo y un servicio", 
            "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }
    
    Vehiculo vehiculo = obtenerVehiculoPorPlaca(placa);
    Servicio servicio = ControladorServicios.getInstancia().buscarServicioPorNombre(nombreServicio);
    
    if (vehiculo == null || servicio == null) {
        JOptionPane.showMessageDialog(this, "Error al seleccionar vehículo o servicio", 
            "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }
    
    // Mostrar confirmación con precio
    int confirm = JOptionPane.showConfirmDialog(this, 
        "¿Confirmar servicio?\n\n" +
        "Vehículo: " + vehiculo.getPlaca() + "\n" +
        "Servicio: " + servicio.getNombreServicio() + "\n" +
        "Precio: Q" + servicio.getPrecioTotal(),
        "Confirmar Servicio", JOptionPane.YES_NO_OPTION);
    
    if (confirm != JOptionPane.YES_OPTION) {
        return;
    }
    
    // Crear orden de trabajo
    OrdenTrabajo orden = new OrdenTrabajo(vehiculo, clienteActual, servicio);
    GestorTaller.getInstancia().agregarOrden(orden);
    
    // La factura se muestra automáticamente en agregarOrden()
}
    
    private void configurarTablaProgreso() {
        DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Orden", "Placa", "Estado", "Servicio", "Total"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int column) {
                switch (column) {
                    case 0: return Integer.class;
                    case 1: return String.class;
                    case 2: return String.class;
                    case 3: return String.class;
                    case 4: return String.class;
                    default: return Object.class;
                }
            }
        };
        
        jTable2.setModel(model);
        
        // Configurar renderizador personalizado
        CustomTableCellRenderer renderer = new CustomTableCellRenderer();
        for (int i = 0; i < jTable2.getColumnCount(); i++) {
            jTable2.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
    }
    
    private void cargarVehiculosCliente() {
        tableModel.setRowCount(0);
        
        if (clienteActual != null) {
            for (Vehiculo vehiculo : clienteActual.getVehiculos()) {
                tableModel.addRow(new Object[]{
                    vehiculo.getPlaca(),
                    vehiculo.getMarca(),
                    vehiculo.getModelo(),
                    vehiculo.getRutaFoto()
                });
            }
        }
    }
    
    private void mostrarImagenVehiculo(String rutaFoto) {
        if (rutaFoto != null && !rutaFoto.isEmpty()) {
            try {
                ImageIcon icon = new ImageIcon(rutaFoto);
                Image img = icon.getImage();
                
                int ancho = jLabel1.getWidth();
                int alto = jLabel1.getHeight();
                
                if (ancho <= 0) ancho = 200;
                if (alto <= 0) alto = 150;
                
                double ratio = Math.min((double)ancho / icon.getIconWidth(), 
                                      (double)alto / icon.getIconHeight());
                int nuevoAncho = (int)(icon.getIconWidth() * ratio);
                int nuevoAlto = (int)(icon.getIconHeight() * ratio);
                
                Image scaledImg = img.getScaledInstance(nuevoAncho, nuevoAlto, Image.SCALE_SMOOTH);
                jLabel1.setIcon(new ImageIcon(scaledImg));
                jLabel1.setText("");
            } catch (Exception ex) {
                jLabel1.setText("Error cargando imagen");
                jLabel1.setIcon(null);
            }
        } else {
            jLabel1.setText("No hay imagen");
            jLabel1.setIcon(null);
        }
    }
    
    private void agregarVehiculo() {
        if (clienteActual == null) {
            JOptionPane.showMessageDialog(this, "No hay cliente autenticado", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        mostrarDialogoVehiculo(true, -1);
    }
    
    private void modificarVehiculo() {
        if (clienteActual == null) {
            JOptionPane.showMessageDialog(this, "No hay cliente autenticado", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un vehículo para modificar", 
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        mostrarDialogoVehiculo(false, selectedRow);
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
        
        JScrollPane scrollPane = new JScrollPane(lblFoto);
        scrollPane.setPreferredSize(new Dimension(150, 150));
        
        if (!esNuevo && indiceVehiculo >= 0) {
            Vehiculo vehiculo = clienteActual.getVehiculos().get(indiceVehiculo);
            datos.cargarDatosVehiculo(vehiculo);
            txtPlaca.setText(datos.placa);
            txtMarca.setText(datos.marca);
            txtModelo.setText(datos.modelo);
            if (datos.rutaFoto != null && !datos.rutaFoto.isEmpty()) {
                cargarImagen(datos.rutaFoto, lblFoto);
            }
        } else {
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
            
            if (esNuevo || !placa.equalsIgnoreCase(datos.placa)) {
                if (controladorVehiculos.existePlaca(placa)) {
                    JOptionPane.showMessageDialog(panel, 
                        "La placa ya está registrada en otro vehículo", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            try {
                if (esNuevo) {
                    controlador.agregarVehiculo(clienteActual.getDpi(), placa, marca, modelo, datos.rutaFoto);
                } else {
                    controladorVehiculos.eliminarVehiculo(clienteActual.getDpi(), datos.placa);
                    controlador.agregarVehiculo(clienteActual.getDpi(), placa, marca, modelo, datos.rutaFoto);
                }
                
                cargarVehiculosCliente();
                actualizarComboVehiculos();
                
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
        
        btnEliminar.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(panel, 
                "¿Está seguro de eliminar este vehículo?", "Confirmar", 
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    boolean eliminado = controladorVehiculos.eliminarVehiculo(
                        clienteActual.getDpi(), datos.placa);
                    
                    if (eliminado) {
                        cargarVehiculosCliente();
                        actualizarComboVehiculos();
                        
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
        
        JPanel panelBotones = new JPanel(new GridLayout(1, 2, 10, 0));
        panelBotones.add(btnGuardar);
        if (!esNuevo) {
            panelBotones.add(btnEliminar);
        }
        
        gbc.gridy = 5;
        panel.add(panelBotones, gbc);
        
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
    
    private void cargarImagen(String rutaFoto, JLabel lblFoto) {
        try {
            ImageIcon icon = new ImageIcon(rutaFoto);
            Image img = icon.getImage();
            
            int ancho = lblFoto.getWidth();
            int alto = lblFoto.getHeight();
            
            if (ancho <= 0) ancho = 150;
            if (alto <= 0) alto = 150;
            
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

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jButton6 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox<>();
        jButton3 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jProgressBar1 = new javax.swing.JProgressBar();
        jLabel4 = new javax.swing.JLabel();
        jProgressBar2 = new javax.swing.JProgressBar();
        jLabel5 = new javax.swing.JLabel();
        jProgressBar3 = new javax.swing.JProgressBar();
        jLabel6 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTabbedPane1.setBackground(new java.awt.Color(204, 204, 204));

        jPanel2.setBackground(new java.awt.Color(204, 204, 204));

        jButton6.setBackground(new java.awt.Color(255, 255, 255));
        jButton6.setFont(new java.awt.Font("Dialog", 3, 14)); // NOI18N
        jButton6.setForeground(new java.awt.Color(0, 0, 0));
        jButton6.setText("MODIFICAR");
        jButton6.setToolTipText("");
        jButton6.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(255, 255, 255));
        jButton2.setFont(new java.awt.Font("Dialog", 3, 14)); // NOI18N
        jButton2.setForeground(new java.awt.Color(0, 0, 0));
        jButton2.setText("AGREGAR");
        jButton2.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
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
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jLabel1.setForeground(new java.awt.Color(51, 51, 51));
        jLabel1.setText("jLabel1");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 440, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 160, Short.MAX_VALUE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(jButton6))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addGap(186, 186, 186))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Registrar automoviles", jPanel1);

        jPanel4.setBackground(new java.awt.Color(204, 204, 204));

        jComboBox1.setBackground(new java.awt.Color(255, 255, 255));
        jComboBox1.setForeground(new java.awt.Color(0, 0, 0));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jLabel2.setForeground(new java.awt.Color(0, 0, 0));
        jLabel2.setText("Automovil");

        jLabel3.setForeground(new java.awt.Color(0, 0, 0));
        jLabel3.setText("Servicio");

        jComboBox2.setBackground(new java.awt.Color(255, 255, 255));
        jComboBox2.setForeground(new java.awt.Color(0, 0, 0));
        jComboBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox2ActionPerformed(evt);
            }
        });

        jButton3.setBackground(new java.awt.Color(255, 255, 255));
        jButton3.setFont(new java.awt.Font("Dialog", 3, 14)); // NOI18N
        jButton3.setForeground(new java.awt.Color(0, 0, 0));
        jButton3.setText("<html><center>Solicitar<br>servicios</center></html> ");
        jButton3.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
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
        jTable2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable2MouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jTable2);

        jProgressBar1.setBackground(new java.awt.Color(204, 204, 204));
        jProgressBar1.setForeground(new java.awt.Color(153, 255, 153));
        jProgressBar1.setToolTipText("");

        jLabel4.setForeground(new java.awt.Color(0, 0, 0));
        jLabel4.setText("Cola de espera");

        jProgressBar2.setBackground(new java.awt.Color(204, 204, 204));
        jProgressBar2.setForeground(new java.awt.Color(153, 255, 153));
        jProgressBar2.setToolTipText("");

        jLabel5.setForeground(new java.awt.Color(0, 0, 0));
        jLabel5.setText("En servicio");

        jProgressBar3.setBackground(new java.awt.Color(204, 204, 204));
        jProgressBar3.setForeground(new java.awt.Color(153, 255, 153));
        jProgressBar3.setToolTipText("");

        jLabel6.setForeground(new java.awt.Color(0, 0, 0));
        jLabel6.setText("Entrega");

        jButton4.setBackground(new java.awt.Color(255, 255, 255));
        jButton4.setFont(new java.awt.Font("Dialog", 3, 14)); // NOI18N
        jButton4.setForeground(new java.awt.Color(0, 0, 0));
        jButton4.setText("Facturas");
        jButton4.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addComponent(jLabel2)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 706, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addComponent(jLabel3)
                                        .addGap(18, 18, 18)
                                        .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 710, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel4Layout.createSequentialGroup()
                                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(jProgressBar2, javax.swing.GroupLayout.DEFAULT_SIZE, 700, Short.MAX_VALUE)
                                        .addComponent(jProgressBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel4)
                                        .addComponent(jLabel5)))
                                .addGroup(jPanel4Layout.createSequentialGroup()
                                    .addComponent(jProgressBar3, javax.swing.GroupLayout.PREFERRED_SIZE, 700, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(jLabel6)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap(16, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addComponent(jLabel4)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jProgressBar2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jProgressBar3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel6)
                        .addComponent(jButton4)))
                .addGap(0, 38, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Progreso", jPanel3);

        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jTabbedPane1.addTab("Cerrar sesión", jButton1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        timerProgreso.stop();
        Login v1 = new Login();
        v1.setVisible(true);
        this.dispose();

    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
    agregarVehiculo();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
    modificarVehiculo();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
            if (evt.getClickCount() == 2) { // Doble clic para modificar
            modificarVehiculo();
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jTable2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable2MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jTable2MouseClicked

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
    solicitarServicio();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jComboBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox2ActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
    actualizarComboServicios();
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
    mostrarFacturasCliente();
    }//GEN-LAST:event_jButton4ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new VentanaCliente(null).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton6;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JProgressBar jProgressBar2;
    private javax.swing.JProgressBar jProgressBar3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    // End of variables declaration//GEN-END:variables
}
