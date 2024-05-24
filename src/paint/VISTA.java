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

public class VISTA extends javax.swing.JFrame {

    private PAINT paint;
    private int nPoints = 3;
    private int clickCount = 0;
    private int[] vLinea = new int[3];
    private int[] xPoints = new int[nPoints];
    private int[] yPoints = new int[nPoints];
    private boolean MouseClickedOnce = false;
    private boolean fill = false;
    private List<Point> polygonPoints = new ArrayList<>();
    
    public VISTA() {
        CONTROLADOR control = new CONTROLADOR();
        initComponents();

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

        seleccionarPunto.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    jPanel4.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            Color color = jColorChooser2.getColor();
                            Graphics g = jPanel4.getGraphics();
                            control.dibujarPunto(e.getX(), e.getY(), g, color);
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
                            if (MouseClickedOnce == true) {
                                Graphics g = jPanel4.getGraphics();
                                control.dibujarRecta(e.getX(), e.getY(), vLinea[1], vLinea[2], g, color, fill);
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
                            if (MouseClickedOnce == true) {
                                Graphics g = jPanel4.getGraphics();
                                control.dibujarCircunferencia(e.getX(), e.getY(), vLinea[1], vLinea[2], g, color, fill);
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
                            if (MouseClickedOnce == true) {
                                Graphics g = jPanel4.getGraphics();
                                control.dibujarPoligonoR(e.getX(), e.getY(), vLinea[1], vLinea[2], nPoints, g, color, fill);
                                MouseClickedOnce = false;
                                polygonPoints.add(new Point(e.getX(), e.getY())); // Añadir el punto final
                                savePolygonCoordinates(); // Guardar las coordenadas en un archivo
                                System.out.println("punto 1: " + e.getX() + " punto 2: " + e.getY() + " numero Lados " +nPoints  );
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
                                    control.dibujarPoligonoI(xPoints, yPoints, nPoints, g, color, fill);
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
        jCheckBox1 = new javax.swing.JCheckBox();
        jLabel2 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        etiquetaNombre = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();

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
        setResizable(false);

        jSlider1.setMaximum(20);
        jSlider1.setMinimum(3);
        jSlider1.setPaintLabels(true);
        jSlider1.setSnapToTicks(true);

        etiquetaNumeroVertices.setText("VERTICES POLIGONO:");

        buttonGroup1.add(seleccionarPunto);
        seleccionarPunto.setText("Punto");
        seleccionarPunto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                seleccionarPuntoActionPerformed(evt);
            }
        });

        buttonGroup1.add(seleccionarRecta);
        seleccionarRecta.setText("Recta");
        seleccionarRecta.setToolTipText("");

        buttonGroup1.add(seleccionarCircunferencia);
        seleccionarCircunferencia.setText("Circunferencia");

        buttonGroup1.add(seleccionarPoligonoRegular);
        seleccionarPoligonoRegular.setText("Polígono Regular");
        seleccionarPoligonoRegular.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                seleccionarPoligonoRegularActionPerformed(evt);
            }
        });

        buttonGroup1.add(seleccionarPoligonoIrregular);
        seleccionarPoligonoIrregular.setText("Polígono Irregular");

        jPanel4.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 579, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 509, Short.MAX_VALUE)
        );

        jColorChooser2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jCheckBox1.setText("Llenar Figura");
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });

        jLabel2.setText("20");
        jLabel2.setToolTipText("");
        jLabel2.setVerifyInputWhenFocusTarget(false);

        jButton1.setText("ELIMINAR");

        jButton2.setText("GUARDAR");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("CARGAR DIBUJO");

        etiquetaNombre.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        etiquetaNombre.setText("NOMBRE");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 14, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(etiquetaNombre, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(seleccionarPoligonoIrregular, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(seleccionarCircunferencia, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(seleccionarRecta, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(seleccionarPunto, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(seleccionarPoligonoRegular, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jCheckBox1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(etiquetaNumeroVertices, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jTextField1, javax.swing.GroupLayout.Alignment.LEADING))
                        .addGap(16, 16, 16)))
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(jColorChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, 529, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 287, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jColorChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, 315, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(etiquetaNombre)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(19, 19, 19)
                        .addComponent(seleccionarPunto)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(seleccionarRecta)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(seleccionarCircunferencia)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(seleccionarPoligonoRegular)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(seleccionarPoligonoIrregular)
                        .addGap(9, 9, 9)
                        .addComponent(etiquetaNumeroVertices)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2)
                            .addComponent(jSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheckBox1)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
        if (jCheckBox1.isSelected()) {
            fill = true;
        } else {
            fill = false;
        }
    }//GEN-LAST:event_jCheckBox1ActionPerformed

    private void seleccionarPuntoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_seleccionarPuntoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_seleccionarPuntoActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        String Nombre = jTextField1.getText();
        boolean VALIDO = true;

        if ("".equals(Nombre)) {
            VALIDO = false;
            JOptionPane.showMessageDialog(null, "EL DIBUJO DEBE TENER UN NOMBRE PARA SER GUARDADO", "ERROR", JOptionPane.ERROR_MESSAGE);
        } else {
            if (Nombre.matches("[a-zA-ZñÑ1234567890_]+") & Nombre != "") {
                System.out.println("NOMBRE VALIDO");
            } else {
                VALIDO = false;
                JOptionPane.showMessageDialog(null, "NOMBRE DE DIBUJO CON CARACTERES INVÁLIDOS, SOLO NÚMEROS, LETRAS Y _ PERMITIDOS", "ERROR", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void seleccionarPoligonoRegularActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_seleccionarPoligonoRegularActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_seleccionarPoligonoRegularActionPerformed

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(VISTA.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VISTA.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VISTA.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VISTA.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new VISTA().setVisible(true);
            }
        });

        
    }

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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel etiquetaNombre;
    private javax.swing.JLabel etiquetaNumeroVertices;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JColorChooser jColorChooser1;
    private javax.swing.JColorChooser jColorChooser2;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JSlider jSlider1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JRadioButton seleccionarCircunferencia;
    private javax.swing.JRadioButton seleccionarPoligonoIrregular;
    private javax.swing.JRadioButton seleccionarPoligonoRegular;
    private javax.swing.JRadioButton seleccionarPunto;
    private javax.swing.JRadioButton seleccionarRecta;
    // End of variables declaration//GEN-END:variables
}
