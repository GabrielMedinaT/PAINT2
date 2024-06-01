package paint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.sql.*;

public class VISTA extends javax.swing.JFrame {

    private PAINT paint;
    private CONTROLADOR controlador; // Variable de instancia para CONTROLADOR
   
    private JButton cargarBoton;
    private int nPoints = 3;
    private int clickCount = 0;
    private int[] vLinea = new int[3];
    private int[] xPoints = new int[nPoints];
    private int[] yPoints = new int[nPoints];
    private boolean MouseClickedOnce = false;
    private boolean fill = false;
    private List<Point> polygonPoints = new ArrayList<>();
    private boolean esCircunferencia;

    public VISTA() {
        paint = new PAINT();
        controlador = new CONTROLADOR(paint); // Inicializa la variable de instancia controlador
        initComponents();
        try {
            controlador.poblarComboBox(cargar);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        cargar.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cargarActionPerformed(evt);
            }
        });

        jPanel4.setBackground(Color.WHITE);
        jColorChooser2.setColor(Color.black);
        jSlider1.setValue(3);
        jLabel2.setText("3");
        jSlider1.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                int value = jSlider1.getValue();
                jLabel2.setText(Integer.toString(value));
                nPoints = value;
            }
        });

        jColorChooser2.getSelectionModel().addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                Color newColor = jColorChooser2.getColor();
                paint.setColor(newColor);
            }
        });

        seleccionarPunto.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    jPanel4.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            Color color = jColorChooser2.getColor();
                            Graphics g = jPanel4.getGraphics();
                            controlador.dibujarPunto(e.getX(), e.getY(), g, color);
                        }
                    });
                } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                    jPanel4.removeMouseListener(jPanel4.getMouseListeners()[0]);
                }
            }
        });

        seleccionarRecta.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    jPanel4.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            Color color = jColorChooser2.getColor();
                            if (MouseClickedOnce) {
                                Graphics g = jPanel4.getGraphics();
                                controlador.dibujarRecta(e.getX(), e.getY(), vLinea[1], vLinea[2], g, color);
                                MouseClickedOnce = false;
                            } else {
                                Graphics g = jPanel4.getGraphics();
                                vLinea[1] = e.getX();
                                vLinea[2] = e.getY();
                                MouseClickedOnce = true;
                            }
                        }
                    });
                } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                    jPanel4.removeMouseListener(jPanel4.getMouseListeners()[0]);
                }
            }
        });

        seleccionarCircunferencia.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    jPanel4.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            Color color = jColorChooser2.getColor();
                            if (MouseClickedOnce) {
                                Graphics g = jPanel4.getGraphics();
                                controlador.dibujarCircunferencia(e.getX(), e.getY(), vLinea[1], vLinea[2], g, color);
                                MouseClickedOnce = false;
                            } else {
                                Graphics g = jPanel4.getGraphics();
                                vLinea[1] = e.getX();
                                vLinea[2] = e.getY();
                                MouseClickedOnce = true;
                            }
                        }
                    });
                } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                    jPanel4.removeMouseListener(jPanel4.getMouseListeners()[0]);
                }
            }
        });

        seleccionarPoligonoRegular.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    jPanel4.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            Color color = jColorChooser2.getColor();
                            if (MouseClickedOnce) {
                                Graphics g = jPanel4.getGraphics();
                                controlador.dibujarPoligonoR(e.getX(), e.getY(), vLinea[1], vLinea[2], nPoints, g, color);
                                MouseClickedOnce = false;
                                polygonPoints.add(new Point(e.getX(), e.getY())); // Añadir el punto final
                                savePolygonCoordinates(); // Guardar las coordenadas en un archivo
                                System.out.println("punto 1: " + e.getX() + " punto 2: " + e.getY() + " numero Lados " + nPoints);
                            } else {
                                Graphics g = jPanel4.getGraphics();
                                vLinea[1] = e.getX();
                                vLinea[2] = e.getY();
                                MouseClickedOnce = true;
                                polygonPoints.clear(); // Limpiar la lista de puntos para un nuevo polígono
                                polygonPoints.add(new Point(vLinea[1], vLinea[2])); // Añadir el punto inicial
                            }
                        }
                    });
                } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                    jPanel4.removeMouseListener(jPanel4.getMouseListeners()[0]);
                }
            }
        });

        seleccionarPoligonoIrregular.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    xPoints = new int[nPoints];
                    yPoints = new int[nPoints];
                    clickCount = 0;

                    jPanel4.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            if (clickCount < nPoints) {
                                xPoints[clickCount] = e.getX();
                                yPoints[clickCount] = e.getY();
                                clickCount++;
                                System.out.println("PUNTO CREADO: (" + e.getX() + ", " + e.getY() + ")");

                                if (clickCount == nPoints) {
                                    Graphics g = jPanel4.getGraphics();
                                    Color color = jColorChooser2.getColor();
                                    controlador.dibujarPoligonoI(xPoints, yPoints, nPoints, g, color);
                                    System.out.println("FIGURA CREADA");

                                    jPanel4.removeMouseListener(this);
                                }
                            }
                        }
                    });

                } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                    for (MouseListener listener : jPanel4.getMouseListeners()) {
                        jPanel4.removeMouseListener(listener);
                    }
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jRadioButton1 = new javax.swing.JRadioButton();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel5 = new javax.swing.JPanel();
        jColorChooser1 = new javax.swing.JColorChooser();
        jSlider1 = new javax.swing.JSlider();
        etiquetaNumeroVertices = new javax.swing.JLabel();
        seleccionarPunto = new javax.swing.JRadioButton();
        seleccionarRecta = new javax.swing.JRadioButton();
        seleccionarCircunferencia = new javax.swing.JRadioButton();
        seleccionarPoligonoRegular = new javax.swing.JRadioButton();
        seleccionarPoligonoIrregular = new javax.swing.JRadioButton();
        jPanel4 = new javax.swing.JPanel();
        jColorChooser2 = new javax.swing.JColorChooser();
        rellenarFigura = new javax.swing.JCheckBox();
        jLabel2 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        guardar = new javax.swing.JButton();
        cargar = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        etiquetaNombre = new javax.swing.JLabel();
        nombreArchivo = new javax.swing.JTextField();
        CARGARBOTONDEM = new javax.swing.JButton();

        jRadioButton1.setText("jRadioButton1");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 200, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 271, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(1280, 720));
        setResizable(false);

        jSlider1.setMaximum(20);
        jSlider1.setMinimum(3);
        jSlider1.setPaintLabels(true);
        jSlider1.setSnapToTicks(true);

        etiquetaNumeroVertices.setText("LADOS");

        buttonGroup1.add(seleccionarPunto);
        seleccionarPunto.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        seleccionarPunto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/paint/Images/Punto.png"))); // NOI18N
        seleccionarPunto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                seleccionarPuntoActionPerformed(evt);
            }
        });

        buttonGroup1.add(seleccionarRecta);
        seleccionarRecta.setToolTipText("");
        seleccionarRecta.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        seleccionarRecta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/paint/Images/Linea.png"))); // NOI18N

        buttonGroup1.add(seleccionarCircunferencia);
        seleccionarCircunferencia.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        seleccionarCircunferencia.setIcon(new javax.swing.ImageIcon(getClass().getResource("/paint/Images/Circunferencia.png"))); // NOI18N
        seleccionarCircunferencia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                seleccionarCircunferenciaActionPerformed(evt);
            }
        });

        buttonGroup1.add(seleccionarPoligonoRegular);
        seleccionarPoligonoRegular.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        seleccionarPoligonoRegular.setIcon(new javax.swing.ImageIcon(getClass().getResource("/paint/Images/poligono.png"))); // NOI18N
        seleccionarPoligonoRegular.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                seleccionarPoligonoRegularActionPerformed(evt);
            }
        });

        buttonGroup1.add(seleccionarPoligonoIrregular);
        seleccionarPoligonoIrregular.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        seleccionarPoligonoIrregular.setIcon(new javax.swing.ImageIcon(getClass().getResource("/paint/Images/Irregular.png"))); // NOI18N
        seleccionarPoligonoIrregular.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                seleccionarPoligonoIrregularActionPerformed(evt);
            }
        });

        jPanel4.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 827, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jColorChooser2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        rellenarFigura.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        rellenarFigura.setIcon(new javax.swing.ImageIcon(getClass().getResource("/paint/Images/sinRelleno.png"))); // NOI18N
        rellenarFigura.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rellenarFiguraActionPerformed(evt);
            }
        });

        jLabel2.setText("20");
        jLabel2.setToolTipText("");
        jLabel2.setVerifyInputWhenFocusTarget(false);

        jButton1.setText("ELIMINAR");

        guardar.setText("GUARDAR");
        guardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardarActionPerformed(evt);
            }
        });

        cargar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cargarActionPerformed(evt);
            }
        });

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Seleccionar Dibujo");

        etiquetaNombre.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        etiquetaNombre.setText("NOMBRE");

        nombreArchivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nombreArchivoActionPerformed(evt);
            }
        });

        CARGARBOTONDEM.setText("Cargar");
        CARGARBOTONDEM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CARGARBOTONDEMActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rellenarFigura)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(seleccionarPoligonoRegular, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(seleccionarCircunferencia, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(seleccionarRecta, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(seleccionarPunto, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jSlider1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addComponent(seleccionarPoligonoIrregular, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel2)
                        .addComponent(etiquetaNumeroVertices, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(18, 18, 18)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jColorChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, 529, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(guardar)
                        .addGap(99, 99, 99)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(nombreArchivo, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(etiquetaNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(114, 114, 114)
                                .addComponent(jButton1))))
                    .addComponent(CARGARBOTONDEM)
                    .addComponent(cargar, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(seleccionarPunto)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(seleccionarRecta)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(seleccionarCircunferencia)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(seleccionarPoligonoRegular)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(seleccionarPoligonoIrregular)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(etiquetaNumeroVertices)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(rellenarFigura))
                            .addComponent(jColorChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton1)
                            .addComponent(etiquetaNombre)
                            .addComponent(guardar))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(nombreArchivo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(41, 41, 41)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cargar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(CARGARBOTONDEM)
                        .addGap(0, 186, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void rellenarFiguraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rellenarFiguraActionPerformed
        if (rellenarFigura.isSelected()) {
            fill = true;
            paint.setRelleno(true);
        } else {
            fill = false;
            paint.setRelleno(false);
        }
    }//GEN-LAST:event_rellenarFiguraActionPerformed

    private void seleccionarPuntoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_seleccionarPuntoActionPerformed
        if (seleccionarPunto.isSelected()) {
            seleccionarPunto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/paint/Images/puntoSelecionado.png")));
        } else {
            seleccionarPunto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/paint/Images/Punto.png")));
        }
    }//GEN-LAST:event_seleccionarPuntoActionPerformed

    private void guardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarActionPerformed
        String nombre = nombreArchivo.getText();
        boolean valido = true;

        if ("".equals(nombre)) {
            valido = false;
            JOptionPane.showMessageDialog(null, "EL DIBUJO DEBE TENER UN NOMBRE PARA SER GUARDADO", "ERROR", JOptionPane.ERROR_MESSAGE);
        } else {
            if (nombre.matches("[a-zA-ZñÑ1234567890_]+")) {
                System.out.println("NOMBRE VALIDO");
            } else {
                valido = false;
                JOptionPane.showMessageDialog(null, "NOMBRE DE DIBUJO CON CARACTERES INVÁLIDOS, SOLO NÚMEROS, LETRAS Y _ PERMITIDOS", "ERROR", JOptionPane.ERROR_MESSAGE);
            }
        }

        if (valido) {
            try {
                controlador.guardarFicheroYPoligonos(nombre);
                JOptionPane.showMessageDialog(null, "Dibujo guardado correctamente.", "ÉXITO", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error al guardar el dibujo: " + e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_guardarActionPerformed

    private void seleccionarPoligonoRegularActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_seleccionarPoligonoRegularActionPerformed

    }//GEN-LAST:event_seleccionarPoligonoRegularActionPerformed

    private void nombreArchivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nombreArchivoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_nombreArchivoActionPerformed

    private void cargarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cargarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cargarActionPerformed

    private void seleccionarCircunferenciaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_seleccionarCircunferenciaActionPerformed

    }//GEN-LAST:event_seleccionarCircunferenciaActionPerformed

    private void seleccionarPoligonoIrregularActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_seleccionarPoligonoIrregularActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_seleccionarPoligonoIrregularActionPerformed

    private void cargarBotonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cargarBotonActionPerformed
        // TODO add your handling code here:
        String selectedFichero = (String) cargar.getSelectedItem();
        try {
            controlador.cargarDibujo(selectedFichero, jPanel4.getGraphics());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_cargarBotonActionPerformed

    private void CARGARBOTONDEMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CARGARBOTONDEMActionPerformed
        // TODO add your handling code here:
        
          String selectedFichero = (String) cargar.getSelectedItem();
    try {
        Graphics g = jPanel4.getGraphics();
        //limpiarPanel(g); // Limpiar el panel antes de cargar el nuevo dibujo
        controlador.cargarDibujo(selectedFichero, g);
    } catch (SQLException e) {
        e.printStackTrace();
    }
    }//GEN-LAST:event_CARGARBOTONDEMActionPerformed

    private void savePolygonCoordinates() {
        try (FileWriter writer = new FileWriter("polygon_coordinates.txt", true)) {
            writer.write("Polígono Regular: \n");
            for (Point point : polygonPoints) {
                writer.write("Punto: (" + point.x + ", " + point.y + ")\n");
            }
            writer.write("\n");
            writer.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
private void limpiarPanel(Graphics g) {
    // Establecer el color a blanco y llenar un rectángulo del tamaño del panel para "limpiarlo"
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, jPanel4.getWidth(), jPanel4.getHeight());
    jPanel4.revalidate();
    jPanel4.repaint();
}





    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton CARGARBOTONDEM;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox<String> cargar;
    private javax.swing.JLabel etiquetaNombre;
    private javax.swing.JLabel etiquetaNumeroVertices;
    private javax.swing.JButton guardar;
    private javax.swing.JButton jButton1;
    private javax.swing.JColorChooser jColorChooser1;
    private javax.swing.JColorChooser jColorChooser2;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JSlider jSlider1;
    private javax.swing.JTextField nombreArchivo;
    private javax.swing.JCheckBox rellenarFigura;
    private javax.swing.JRadioButton seleccionarCircunferencia;
    private javax.swing.JRadioButton seleccionarPoligonoIrregular;
    private javax.swing.JRadioButton seleccionarPoligonoRegular;
    private javax.swing.JRadioButton seleccionarPunto;
    private javax.swing.JRadioButton seleccionarRecta;
    // End of variables declaration//GEN-END:variables
}
//VSCODE