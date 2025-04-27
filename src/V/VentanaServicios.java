/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package V;
import java.awt.event.KeyEvent;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
/**
 *
 * @author gervi
 */
public class VentanaServicios extends javax.swing.JFrame {
    private DefaultTableModel modeloTabla;
    private static final String ARCHIVO_SERVICIOS = "src/resources/servicios.tms";
    private static final String ARCHIVO_REPUESTOS = "src/resources/repuestos.tmr";
    private Vector<Vector<String>> serviciosEnMemoria = new Vector<>();
    private Vector<Vector<String>> repuestosDisponibles = new Vector<>();
    private DefaultListModel<String> listModelRepuestos = new DefaultListModel<>();
    private Random rand = new Random();

    public VentanaServicios() {
        initComponents();
         jList1.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyPressed(java.awt.event.KeyEvent evt) {
            jList1KeyPressed(evt);
        }
    });
        configurarTabla();
        cargarRepuestosDisponibles();
        cargarDatosDesdeArchivo();
        jList1.setModel(listModelRepuestos);
        
        // Configurar visibilidad inicial de precios
        jLabel9.setVisible(false);
        jLabel10.setVisible(false);
        jLabel7.setVisible(false);
        jLabel8.setVisible(false);
        
        this.setLocationRelativeTo(null);
        this.setTitle("Gestión de Servicios");
    }
     
    private void configurarTabla() {
        modeloTabla = new DefaultTableModel(
            new Vector<>(Arrays.asList("ID", "Servicio", "Marca", "Modelo", "Repuestos", "Mano Obra", "Precio Total")), 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 5 || columnIndex == 6) return Double.class;
                return String.class;
            }
        };
        jTable1.setModel(modeloTabla);
    }

    private void cargarRepuestosDisponibles() {
        repuestosDisponibles.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(ARCHIVO_REPUESTOS))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] campos = linea.split("-");
                if (campos.length >= 6) {
                    Vector<String> repuesto = new Vector<>(Arrays.asList(campos));
                    repuestosDisponibles.add(repuesto);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar repuestos: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String generarNuevoId() {
        return String.format("BS%04d", rand.nextInt(10000));
    }

    private int generarPrecioManoObra() {
        return 100 + rand.nextInt(201); // 100-300
    }

    private void cargarDatosDesdeArchivo() {
        modeloTabla.setRowCount(0);
        serviciosEnMemoria = new Vector<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(ARCHIVO_SERVICIOS))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                Vector<String> campos = new Vector<>(Arrays.asList(linea.split("-")));
                
                if (campos.size() >= 6 && campos.get(0).matches("BS\\d{4}")) {
                    serviciosEnMemoria.add(campos);
                    
                    try {
                        double manoObra = Double.parseDouble(campos.get(5));
                        double totalRepuestos = calcularTotalRepuestos(campos.get(4));
                        
                        modeloTabla.addRow(new Object[]{
                            campos.get(0), campos.get(1), campos.get(2),
                            campos.get(3), formatListaRepuestos(campos.get(4)),
                            manoObra, manoObra + totalRepuestos
                        });
                    } catch (NumberFormatException e) {
                        System.err.println("Error en formato numérico: " + campos);
                    }
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al leer archivo de servicios", 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private double calcularTotalRepuestos(String idsRepuestos) {
        return Arrays.stream(idsRepuestos.split(";"))
            .map(String::trim)
            .filter(id -> !id.isEmpty())
            .mapToDouble(this::obtenerPrecioRepuesto)
            .sum();
    }

    private double obtenerPrecioRepuesto(String idRepuesto) {
        for (Vector<String> rep : repuestosDisponibles) {
            if (rep.get(0).equals(idRepuesto)) {
                try {
                    return Double.parseDouble(rep.get(5));
                } catch (NumberFormatException e) {
                    return 0;
                }
            }
        }
        return 0;
    }

    private String formatListaRepuestos(String idsRepuestos) {
        return Arrays.stream(idsRepuestos.split(";"))
            .map(String::trim)
            .filter(id -> !id.isEmpty())
            .map(id -> {
                for (Vector<String> rep : repuestosDisponibles) {
                    if (rep.get(0).equals(id)) {
                        return rep.get(1) + " (" + id + ")";
                    }
                }
                return id + " (No encontrado)";
            })
            .collect(Collectors.joining(", "));
    }

    private void guardarDatosEnArchivo() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO_SERVICIOS))) {
            serviciosEnMemoria.forEach(serv -> pw.println(String.join("-", serv)));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al guardar servicios", 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarCampos() {
        jTextField1.setText("");
        jTextField2.setText("");
        jTextField3.setText("");
        jTextField4.setText("");
        jTextField5.setText("");
        listModelRepuestos.clear();
        jLabel9.setVisible(false);
        jLabel10.setVisible(false);
        jLabel7.setVisible(false);
        jLabel8.setVisible(false);
    }

    private void mostrarDialogoDiagnostico() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Crear servicio de diagnóstico?\nPrecio fijo: Q150.00", 
            "Confirmar diagnóstico", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            Vector<String> nuevoServicio = new Vector<>(Arrays.asList(
                generarNuevoId(), "Diagnóstico", "Cualquiera", "Cualquiera", "", "150.00"
            ));
            
            serviciosEnMemoria.add(nuevoServicio);
            guardarDatosEnArchivo();
            cargarDatosDesdeArchivo();
            JOptionPane.showMessageDialog(this, "Diagnóstico creado exitosamente");
        }
    }

    private void mostrarSeleccionRepuestos() {
        String marca = jTextField4.getText().trim();
        String modelo = jTextField5.getText().trim();
        
        if (marca.isEmpty() || modelo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese marca y modelo primero", 
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Vector<String> repuestosFiltrados = new Vector<>();
        for (Vector<String> rep : repuestosDisponibles) {
            if (rep.get(2).equalsIgnoreCase("cualquiera") || 
                (rep.get(2).equalsIgnoreCase(marca) && rep.get(3).equalsIgnoreCase(modelo))) {
                repuestosFiltrados.add(rep.get(0) + " - " + rep.get(1));
            }
        }
        
        if (repuestosFiltrados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay repuestos compatibles", 
                "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String seleccion = (String) JOptionPane.showInputDialog(this,
            "Seleccione repuestos compatibles:", "Agregar Repuesto",
            JOptionPane.PLAIN_MESSAGE, null, repuestosFiltrados.toArray(), null);
        
        if (seleccion != null) {
            String idRepuesto = seleccion.split(" - ")[0];
            if (!listModelRepuestos.contains(idRepuesto)) {
                listModelRepuestos.addElement(idRepuesto);
                actualizarPrecios();
            }
        }
    }

    private void actualizarPrecios() {
        if (listModelRepuestos.isEmpty()) {
            jLabel9.setVisible(false);
            jLabel10.setVisible(false);
            jLabel7.setVisible(false);
            jLabel8.setVisible(false);
            return;
        }
        
        double totalRepuestos = calcularTotalRepuestos(obtenerListaRepuestos());
        int manoObra = generarPrecioManoObra();
        double precioTotal = manoObra + totalRepuestos;
        
        jLabel9.setText(String.valueOf(manoObra));
        jLabel10.setText(String.format("%.2f", precioTotal));
        
        jLabel9.setVisible(true);
        jLabel10.setVisible(true);
        jLabel7.setVisible(true);
        jLabel8.setVisible(true);
    }

    private String obtenerListaRepuestos() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < listModelRepuestos.size(); i++) {
            if (i > 0) sb.append(";");
            sb.append(listModelRepuestos.getElementAt(i));
        }
        return sb.toString();
    }

    private boolean validarCampos() {
        if (jTextField3.getText().trim().isEmpty() ||
            jTextField4.getText().trim().isEmpty() ||
            jTextField5.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private void buscarServicio() {
        String id = jTextField1.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese ID a buscar", 
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        for (Vector<String> serv : serviciosEnMemoria) {
            if (serv.get(0).equals(id)) {
                jTextField2.setText(serv.get(0));
                jTextField3.setText(serv.get(1));
                jTextField4.setText(serv.get(2));
                jTextField5.setText(serv.get(3));
                
                listModelRepuestos.clear();
                Arrays.stream(serv.get(4).split(";"))
                    .filter(r -> !r.trim().isEmpty())
                    .forEach(listModelRepuestos::addElement);
                
                jLabel9.setText(serv.get(5));
                jLabel10.setText(String.format("%.2f", 
                    Double.parseDouble(serv.get(5)) + calcularTotalRepuestos(serv.get(4))));
                
                jLabel9.setVisible(true);
                jLabel10.setVisible(true);
                jLabel7.setVisible(true);
                jLabel8.setVisible(true);
                
                return;
            }
        }
        
        JOptionPane.showMessageDialog(this, "Servicio no encontrado", 
            "No encontrado", JOptionPane.INFORMATION_MESSAGE);
    }
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

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
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jButton6 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

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
        jLabel1.setText("ID Servicio:");

        jTextField1.setBackground(new java.awt.Color(255, 255, 255));
        jTextField1.setForeground(new java.awt.Color(0, 0, 0));

        jButton2.setBackground(new java.awt.Color(255, 255, 255));
        jButton2.setFont(new java.awt.Font("Dialog", 3, 14)); // NOI18N
        jButton2.setForeground(new java.awt.Color(0, 0, 0));
        jButton2.setText("BUSCAR POR ID");
        jButton2.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 0, 0));
        jLabel2.setText("ID Servicio");

        jTextField2.setEditable(false);
        jTextField2.setBackground(new java.awt.Color(255, 255, 255));
        jTextField2.setForeground(new java.awt.Color(0, 0, 0));

        jLabel3.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 0, 0));
        jLabel3.setText("Servicio");

        jTextField3.setBackground(new java.awt.Color(255, 255, 255));
        jTextField3.setForeground(new java.awt.Color(0, 0, 0));

        jLabel4.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 0, 0));
        jLabel4.setText("Marca");

        jTextField4.setBackground(new java.awt.Color(255, 255, 255));
        jTextField4.setForeground(new java.awt.Color(0, 0, 0));

        jLabel5.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 0, 0));
        jLabel5.setText("Modelo");

        jTextField5.setBackground(new java.awt.Color(255, 255, 255));
        jTextField5.setForeground(new java.awt.Color(0, 0, 0));

        jLabel6.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 0, 0));
        jLabel6.setText("Lista de repuestos");

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
        jLabel7.setText("Mano de obra");

        jLabel8.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(0, 0, 0));
        jLabel8.setText("Mano total");

        jLabel9.setFont(new java.awt.Font("Dialog", 2, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 0, 0));
        jLabel9.setText("0");

        jLabel10.setFont(new java.awt.Font("Dialog", 2, 14)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 0, 0));
        jLabel10.setText("0");

        jButton6.setBackground(new java.awt.Color(255, 255, 255));
        jButton6.setFont(new java.awt.Font("Dialog", 3, 14)); // NOI18N
        jButton6.setForeground(new java.awt.Color(0, 0, 0));
        jButton6.setText("DIAGNOSTICO");
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
                                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addContainerGap()
                                                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(34, 34, 34)
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
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                                .addComponent(jLabel4)
                                                .addGap(53, 53, 53))))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(30, 30, 30)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel7)
                                            .addComponent(jLabel9))
                                        .addGap(77, 77, 77)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel10)
                                            .addComponent(jLabel8))))
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(12, 12, 12)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGap(18, 18, 18)
                                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(62, 62, 62)
                                        .addComponent(jLabel5)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 116, Short.MAX_VALUE)
                                        .addComponent(jLabel6)
                                        .addGap(53, 53, 53)))))
                        .addGap(16, 16, 16))
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
                    .addComponent(jButton6))
                .addGap(36, 36, 36)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6))
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
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel8))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel9)
                                    .addComponent(jLabel10))))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE)
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
    buscarServicio();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        if (!validarCampos()) return;
        
        String id = jTextField1.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Busque un servicio primero", 
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Vector<String> servicioActualizado = new Vector<>(Arrays.asList(
            id, jTextField3.getText().trim(), jTextField4.getText().trim(),
            jTextField5.getText().trim(), obtenerListaRepuestos(), jLabel9.getText()
        ));
        
        for (int i = 0; i < serviciosEnMemoria.size(); i++) {
            if (serviciosEnMemoria.get(i).get(0).equals(id)) {
                serviciosEnMemoria.set(i, servicioActualizado);
                guardarDatosEnArchivo();
                cargarDatosDesdeArchivo();
                JOptionPane.showMessageDialog(this, "Servicio actualizado");
                limpiarCampos();
                return;
            }
        }
        
        JOptionPane.showMessageDialog(this, "Error al actualizar", 
            "Error", JOptionPane.ERROR_MESSAGE);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        if (!validarCampos()) return;
        
        Vector<String> nuevoServicio = new Vector<>(Arrays.asList(
            generarNuevoId(), jTextField3.getText().trim(), jTextField4.getText().trim(),
            jTextField5.getText().trim(), obtenerListaRepuestos(), jLabel9.getText()
        ));
        
        serviciosEnMemoria.add(nuevoServicio);
        guardarDatosEnArchivo();
        cargarDatosDesdeArchivo();
        JOptionPane.showMessageDialog(this, "Servicio creado con ID: " + nuevoServicio.get(0));
        limpiarCampos();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
String id = jTextField1.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Busque un servicio primero", 
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Eliminar este servicio?", "Confirmar", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            serviciosEnMemoria.removeIf(serv -> serv.get(0).equals(id));
            guardarDatosEnArchivo();
            cargarDatosDesdeArchivo();
            limpiarCampos();
            JOptionPane.showMessageDialog(this, "Servicio eliminado");
        }
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
    mostrarDialogoDiagnostico();
    
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jList1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jList1MouseClicked
    if (evt.getClickCount() == 1) {
        mostrarSeleccionRepuestos();
    }
    }//GEN-LAST:event_jList1MouseClicked

    private void jList1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jList1KeyPressed
 if (evt.getKeyCode() == KeyEvent.VK_ENTER && jList1.getSelectedIndex() != -1) {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Eliminar este repuesto?", "Confirmar", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            listModelRepuestos.remove(jList1.getSelectedIndex());
            actualizarPrecios();
        }
    }
    }//GEN-LAST:event_jList1KeyPressed



    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new VentanaServicios().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JList<String> jList1;
    private javax.swing.JPanel jPanel1;
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
