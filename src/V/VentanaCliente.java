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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
public class VentanaCliente extends javax.swing.JFrame {
    private ControladorClientes controlador;
    private ControladorVehiculos controladorVehiculos;
    private Cliente clienteActual;
    private DefaultTableModel tableModel;
    
    // Constructor modificado para recibir el cliente
    public VentanaCliente(Cliente cliente) {
        initComponents();
        this.clienteActual = cliente;
        controlador = ControladorClientes.getInstancia();
        controladorVehiculos = new ControladorVehiculos(controlador);
        
        // Configurar la tabla
        configurarTablaVehiculos();
        cargarVehiculosCliente();
        
        this.setLocationRelativeTo(null);
        this.setTitle("Taller Mecánico USAC - Cliente: " + cliente.getNombreCompleto());
    }
    
    private void configurarTablaVehiculos() {
        tableModel = new DefaultTableModel(
            new Object[]{"Placa", "Marca", "Modelo", "Imagen"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        jTable1.setModel(tableModel);
        
        // Ajustar el ancho de las columnas
        jTable1.getColumnModel().getColumn(0).setPreferredWidth(100); // Placa
        jTable1.getColumnModel().getColumn(1).setPreferredWidth(100); // Marca
        jTable1.getColumnModel().getColumn(2).setPreferredWidth(150); // Modelo
        jTable1.getColumnModel().getColumn(3).setPreferredWidth(200); // Imagen
        
        // Agregar listener para mostrar la imagen cuando se selecciona un vehículo
        jTable1.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = jTable1.getSelectedRow();
                if (selectedRow != -1) {
                    String rutaFoto = (String) jTable1.getValueAt(selectedRow, 3);
                    mostrarImagenVehiculo(rutaFoto);
                }
            }
        });
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
                
                // Escalar la imagen para que se ajuste al JLabel
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
                cargarVehiculosCliente();
                
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
                        cargarVehiculosCliente();
                        
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
        jPanel7 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 104, Short.MAX_VALUE)
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

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jLabel2.setForeground(new java.awt.Color(0, 0, 0));
        jLabel2.setText("Automovil");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(735, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(258, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Progreso", jPanel3);

        jPanel8.setBackground(new java.awt.Color(204, 204, 204));

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 876, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 290, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Facturas", jPanel7);

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
        Login v1 = new Login();
        v1.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox1ActionPerformed

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
    private javax.swing.JButton jButton6;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
