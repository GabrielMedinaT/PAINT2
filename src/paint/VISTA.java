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
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.sql.*;

public class VISTA extends javax.swing.JFrame {

    private PAINT paint;
    private CONTROLADOR controlador; // Variable de instancia para CONTROLADOR

    private int nPoints = 3;
    private int clickCount = 0;
    private int[] vLinea = new int[3];
    private int[] xPoints = new int[nPoints];
    private int[] yPoints = new int[nPoints];
    private boolean MouseClickedOnce = false;
    private boolean fill = false;
    private List<Point> polygonPoints = new ArrayList<>();
    private JDialog colorDialog; // Diálogo para el selector de color
    private List<Point> puntosPoligono = new ArrayList<>();
    private boolean poligonoEnProgreso = false;
    private String nombreArchivoBBDD;
    private ImageIcon cargarImagen(String ruta) {
    return new ImageIcon(getClass().getResource("/paint/Images/lagrimas.png"));
}

    public VISTA() {
        paint = new PAINT();
        controlador = new CONTROLADOR(paint, this); // Inicializa la variable de instancia controlador
        initComponents();
        initColorDialog(); // Inicializa el diálogo del selector de color
        initUndoAction();  // Inicializa el diálogo del selector de color
        nombreArchivoBBDD = "Dibujo sin titulo ";
        setTitle("PAINT | " + " | "  + nombreArchivoBBDD);
        

        // Establecer el color inicial a negro
        jColorChooser2.setColor(Color.BLACK);
        paint.setColor(Color.BLACK);

        // Deshabilitar la función de pegar
        areaDibujo.setBackground(Color.WHITE);
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
                    areaDibujo.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            Color color = jColorChooser2.getColor();
                            Graphics g = areaDibujo.getGraphics();
                            controlador.dibujarPunto(e.getX(), e.getY(), g, color);
                        }
                    });
                } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                    areaDibujo.removeMouseListener(areaDibujo.getMouseListeners()[0]);
                }
            }
        });
        limpiar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limpiarPanel(areaDibujo);
            }
        });
        seleccionarRecta.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    areaDibujo.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            Color color = jColorChooser2.getColor();
                            if (MouseClickedOnce) {
                                Graphics g = areaDibujo.getGraphics();
                                controlador.dibujarRecta(e.getX(), e.getY(), vLinea[1], vLinea[2], g, color);
                                MouseClickedOnce = false;
                            } else {
                                Graphics g = areaDibujo.getGraphics();
                                vLinea[1] = e.getX();
                                vLinea[2] = e.getY();
                                MouseClickedOnce = true;
                            }
                        }
                    });
                } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                    areaDibujo.removeMouseListener(areaDibujo.getMouseListeners()[0]);
                }
            }
        });

        seleccionarCircunferencia.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    areaDibujo.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            Color color = jColorChooser2.getColor();
                            if (MouseClickedOnce) {
                                Graphics g = areaDibujo.getGraphics();
                                controlador.dibujarCircunferencia(e.getX(), e.getY(), vLinea[1], vLinea[2], g, color);
                                MouseClickedOnce = false;
                            } else {
                                Graphics g = areaDibujo.getGraphics();
                                vLinea[1] = e.getX();
                                vLinea[2] = e.getY();
                                MouseClickedOnce = true;
                            }
                        }
                    });
                } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                    areaDibujo.removeMouseListener(areaDibujo.getMouseListeners()[0]);
                }
            }
        });

        seleccionarPoligonoRegular.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    areaDibujo.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            Color color = jColorChooser2.getColor();
                            if (MouseClickedOnce) {
                                // Segundo clic: vértice exterior
                                int x2 = e.getX();
                                int y2 = e.getY();
                                int x = vLinea[1]; // Centro
                                int y = vLinea[2];

                                Graphics g = areaDibujo.getGraphics();
                                paint.dibujarPoligonoR(x, y, x2, y2, nPoints, g, color, paint.isRelleno());
                                MouseClickedOnce = false;
                                polygonPoints.add(new Point(x2, y2)); // Añadir el punto final
                                savePolygonCoordinates(); // Guardar las coordenadas en un archivo
                                System.out.println("Centro: (" + x + ", " + y + ") Vértice: (" + x2 + ", " + y2 + ") Número de Lados: " + nPoints);
                            } else {
                                // Primer clic: centro
                                vLinea[1] = e.getX();
                                vLinea[2] = e.getY();
                                MouseClickedOnce = true;
                                polygonPoints.clear(); // Limpiar la lista de puntos para un nuevo polígono
                                polygonPoints.add(new Point(vLinea[1], vLinea[2])); // Añadir el punto inicial
                            }
                        }
                    });
                } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                    for (MouseListener listener : areaDibujo.getMouseListeners()) {
                        if (listener instanceof MouseAdapter) {
                            areaDibujo.removeMouseListener(listener);
                        }
                    }
                }
            }
        });

    }

    public class LineIntersection {

        public static boolean doIntersect(Point p1, Point q1, Point p2, Point q2) {
            int o1 = orientation(p1, q1, p2);
            int o2 = orientation(p1, q1, q2);
            int o3 = orientation(p2, q2, p1);
            int o4 = orientation(p2, q2, q1);

            if (o1 != o2 && o3 != o4) {
                return true;
            }

            if (o1 == 0 && onSegment(p1, p2, q1)) {
                return true;
            }
            if (o2 == 0 && onSegment(p1, q2, q1)) {
                return true;
            }
            if (o3 == 0 && onSegment(p2, p1, q2)) {
                return true;
            }
            if (o4 == 0 && onSegment(p2, q1, q2)) {
                return true;
            }

            return false;
        }

        private static boolean onSegment(Point p, Point q, Point r) {
            return q.x <= Math.max(p.x, r.x) && q.x >= Math.min(p.x, r.x)
                    && q.y <= Math.max(p.y, r.y) && q.y >= Math.min(p.y, r.y);
        }

        private static int orientation(Point p, Point q, Point r) {
            int val = (q.y - p.y) * (r.x - q.x) - (q.x - p.x) * (r.y - q.y);
            if (val == 0) {
                return 0;
            }
            return (val > 0) ? 1 : 2;
        }
    }

    private void initColorDialog() {
        jColorChooser2.setColor(Color.BLACK); // Establecer el color predeterminado a negro
        colorDialog = new JDialog(this, "Seleccionar Color", Dialog.ModalityType.APPLICATION_MODAL);
        colorDialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        colorDialog.getContentPane().add(jColorChooser2);
        colorDialog.pack();
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
        jColorChooser2 = new javax.swing.JColorChooser();
        seleccionarRecta = new javax.swing.JRadioButton();
        areaDibujo = new javax.swing.JPanel();
        seleccionarPunto = new javax.swing.JRadioButton();
        seleccionarCircunferencia = new javax.swing.JRadioButton();
        seleccionarPoligonoRegular = new javax.swing.JRadioButton();
        seleccionarPoligonoIrregular = new javax.swing.JRadioButton();
        etiquetaNumeroVertices = new javax.swing.JLabel();
        jSlider1 = new javax.swing.JSlider();
        jLabel2 = new javax.swing.JLabel();
        rellenarFigura = new javax.swing.JCheckBox();
        limpiar = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        NombreDibujo = new javax.swing.JLabel();
        nombreDibujo = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem4 = new javax.swing.JMenuItem();
        guardarEnMenu = new javax.swing.JMenuItem();
        actualizarEnMenu = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();

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

        jColorChooser2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMaximumSize(new java.awt.Dimension(1280, 720));
        setResizable(false);

        buttonGroup1.add(seleccionarRecta);
        seleccionarRecta.setToolTipText("");
        seleccionarRecta.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        seleccionarRecta.setIcon(new javax.swing.ImageIcon(getClass().getResource("/paint/Images/Linea.png"))); // NOI18N
        seleccionarRecta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                seleccionarRectaActionPerformed(evt);
            }
        });

        areaDibujo.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        javax.swing.GroupLayout areaDibujoLayout = new javax.swing.GroupLayout(areaDibujo);
        areaDibujo.setLayout(areaDibujoLayout);
        areaDibujoLayout.setHorizontalGroup(
            areaDibujoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        areaDibujoLayout.setVerticalGroup(
            areaDibujoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 681, Short.MAX_VALUE)
        );

        buttonGroup1.add(seleccionarPunto);
        seleccionarPunto.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        seleccionarPunto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/paint/Images/Punto.png"))); // NOI18N
        seleccionarPunto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                seleccionarPuntoActionPerformed(evt);
            }
        });

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

        etiquetaNumeroVertices.setText("LADOS");

        jSlider1.setMaximum(20);
        jSlider1.setMinimum(3);
        jSlider1.setPaintLabels(true);
        jSlider1.setSnapToTicks(true);

        jLabel2.setText("20");
        jLabel2.setToolTipText("");
        jLabel2.setVerifyInputWhenFocusTarget(false);

        rellenarFigura.setText("Rellenar");
        rellenarFigura.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        rellenarFigura.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rellenarFiguraActionPerformed(evt);
            }
        });

        limpiar.setText("BORRAR DIBUJO");
        limpiar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                limpiarActionPerformed(evt);
            }
        });

        jButton2.setText("Color");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        nombreDibujo.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        nombreDibujo.setText("Dibujo sin título");

        jMenu1.setText("Archivo");

        jMenuItem4.setText("Cargar");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem4);

        guardarEnMenu.setText("Guardar");
        guardarEnMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardarEnMenuActionPerformed(evt);
            }
        });
        jMenu1.add(guardarEnMenu);

        actualizarEnMenu.setText("Actualizar");
        actualizarEnMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                actualizarEnMenuActionPerformed(evt);
            }
        });
        jMenu1.add(actualizarEnMenu);

        jMenuItem2.setText("Eliminar");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuItem1.setText("Salir");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(seleccionarPunto)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(seleccionarRecta)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(seleccionarCircunferencia)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(seleccionarPoligonoRegular)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(seleccionarPoligonoIrregular, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(etiquetaNumeroVertices)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rellenarFigura)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(limpiar)
                        .addGap(202, 202, 202)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(NombreDibujo, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(nombreDibujo))
                        .addGap(0, 402, Short.MAX_VALUE))
                    .addComponent(areaDibujo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(etiquetaNumeroVertices)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(rellenarFigura)
                            .addComponent(jButton2)
                            .addComponent(limpiar)
                            .addComponent(NombreDibujo)
                            .addComponent(nombreDibujo))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(seleccionarRecta, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(seleccionarPunto, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(seleccionarCircunferencia, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(seleccionarPoligonoRegular, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(seleccionarPoligonoIrregular)
                            .addComponent(jSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addComponent(areaDibujo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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

    private void seleccionarPoligonoRegularActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_seleccionarPoligonoRegularActionPerformed

    }//GEN-LAST:event_seleccionarPoligonoRegularActionPerformed

    private void seleccionarCircunferenciaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_seleccionarCircunferenciaActionPerformed

    }//GEN-LAST:event_seleccionarCircunferenciaActionPerformed
// Método para mostrar el JDialog con la lista de archivos

    private void showFileSelectionDialog() {

        JDialog dialog = new JDialog((Frame) null, "Seleccionar fichero", true);
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(null);

        try {
            List<String> fileList = controlador.obtenerNombresFicheros(); // Llamar al nuevo método
            JList<String> list = new JList<>(fileList.toArray(new String[0]));
            JScrollPane scrollPane = new JScrollPane(list);

            JButton selectButton = new JButton("Seleccionar");
            selectButton.addActionListener(e -> {
                String selectedFile = list.getSelectedValue();
                if (selectedFile != null) {
                    limpiarPanel();
                    cargarDibujo(selectedFile);
                     setTitle("PAINT | " + " | "  + nombreArchivoBBDD);

                    dialog.dispose();
                }
            });

            dialog.setLayout(new BorderLayout());
            dialog.add(scrollPane, BorderLayout.CENTER);
            dialog.add(selectButton, BorderLayout.SOUTH);

            dialog.setVisible(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void cargarDibujo(String selectedFichero) {
        try {
            Graphics g = areaDibujo.getGraphics();
            controlador.cargarDibujo(selectedFichero, g);
            nombreArchivoBBDD = selectedFichero;  // Actualizar el nombre del archivo
            nombreDibujo.setText(nombreArchivoBBDD); // Actualizar el título de la ventana
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void seleccionarPoligonoIrregularActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_seleccionarPoligonoIrregularActionPerformed

        if (seleccionarPoligonoIrregular.isSelected()) {
            puntosPoligono.clear();
            poligonoEnProgreso = true;

            MouseAdapter mouseAdapter = new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    Point nuevoPunto = e.getPoint();
                    System.out.println("Nuevo punto: " + nuevoPunto);

                    if (e.getClickCount() == 2 && poligonoEnProgreso) { // Doble click para finalizar el polígono
                        puntosPoligono.add(nuevoPunto);
                        int nPoints = puntosPoligono.size();
                        int[] xPoints = new int[nPoints];
                        int[] yPoints = new int[nPoints];
                        for (int i = 0; i < nPoints; i++) {
                            xPoints[i] = puntosPoligono.get(i).x;
                            yPoints[i] = puntosPoligono.get(i).y;
                        }
                        Graphics g = areaDibujo.getGraphics();
                        Color color = jColorChooser2.getColor();
                        controlador.dibujarPoligonoI(xPoints, yPoints, nPoints, g, color, paint.isRelleno(), true);
                        areaDibujo.removeMouseListener(this);
                        puntosPoligono.clear();
                        poligonoEnProgreso = false;
                    } else {
                        boolean interseccion = false;
                        int n = puntosPoligono.size();
                        if (n > 1) {
                            for (int i = 0; i < n - 2; i++) {
                                System.out.println("comprobar interseccion entre segmento (" + puntosPoligono.get(i) + ", " + puntosPoligono.get(i + 1) + ") y degmento (" + puntosPoligono.get(n - 1) + ", " + nuevoPunto + ")");
                                if (LineIntersection.doIntersect(puntosPoligono.get(i), puntosPoligono.get(i + 1), puntosPoligono.get(n - 1), nuevoPunto)) {
                                    interseccion = true;
                                    System.out.println("Interseccion detectada, no dibujar !");
                                    break;
                                }
                            }
                            // Verificar intersección con el primer segmento para cerrar el polígono, pero omitir el último segmento agregado
                            if (!interseccion && n > 2) {
                                System.out.println("Checking intersection with first segment (" + puntosPoligono.get(0) + ", " + puntosPoligono.get(1) + ")");
                                if (LineIntersection.doIntersect(puntosPoligono.get(n - 1), nuevoPunto, puntosPoligono.get(0), puntosPoligono.get(1))) {
                                    interseccion = true;
                                    System.out.println("Intersection detected with first segment!");
                                }
                            }
                        }
                        if (!interseccion) {
                            puntosPoligono.add(nuevoPunto);
                            if (puntosPoligono.size() > 1) { // Dibuja línea provisional entre el último y penúltimo punto
                                Graphics g = areaDibujo.getGraphics();
                                g.setColor(jColorChooser2.getColor());
                                g.drawLine(puntosPoligono.get(puntosPoligono.size() - 2).x, puntosPoligono.get(puntosPoligono.size() - 2).y, puntosPoligono.get(puntosPoligono.size() - 1).x, puntosPoligono.get(puntosPoligono.size() - 1).y);
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "Intersección detectada. No se puede agregar este punto.");
                        }
                    }
                }
            };

            areaDibujo.addMouseListener(mouseAdapter);
        } else {
            for (MouseListener listener : areaDibujo.getMouseListeners()) {
                if (listener instanceof MouseAdapter) {
                    areaDibujo.removeMouseListener(listener);
                }
            }
            puntosPoligono.clear();
            poligonoEnProgreso = false;
        }


    }//GEN-LAST:event_seleccionarPoligonoIrregularActionPerformed

    private void cargarBotonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cargarBotonActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_cargarBotonActionPerformed

    private void limpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_limpiarActionPerformed
        // TODO add your handling code here:

        limpiarPanel(areaDibujo);
    }//GEN-LAST:event_limpiarActionPerformed

    private void seleccionarRectaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_seleccionarRectaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_seleccionarRectaActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        colorDialog.setLocationRelativeTo(this);
        colorDialog.setVisible(true);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void guardarEnMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarEnMenuActionPerformed
       // Mostrar un cuadro de diálogo para que el usuario ingrese el nombre del archivo
    JTextField nombreArchivo = new JTextField(30);
    ((AbstractDocument) nombreArchivo.getDocument()).setDocumentFilter(new DocumentFilter() {
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if ((fb.getDocument().getLength() + string.length()) <= 30) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if ((fb.getDocument().getLength() + text.length() - length) <= 30) {
                super.replace(fb, offset, length, text, attrs);
            }
        }
    });

    // Añadir un KeyListener para evitar el pegado
    InputMap inputMap = nombreArchivo.getInputMap(JComponent.WHEN_FOCUSED);
    inputMap.put(KeyStroke.getKeyStroke("ctrl V"), "none");
    inputMap.put(KeyStroke.getKeyStroke("meta V"), "none");

    boolean nombreValido = false;
    while (!nombreValido) {
        // Mostrar un cuadro de diálogo con el JTextField personalizado
        int result = JOptionPane.showConfirmDialog(null, nombreArchivo, "Ingrese el nombre del dibujo:", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        // Verificar si el usuario ingresó un nombre y presionó OK
        if (result == JOptionPane.OK_OPTION) {
            String nombre = nombreArchivo.getText();
             setTitle("PAINT | " + " | "  + nombreArchivoBBDD);

            boolean valido = true;

            if ("".equals(nombre)) {
                valido = false;
                JOptionPane.showMessageDialog(null, "El dibujo ha de tener un nombre", "ERROR", JOptionPane.ERROR_MESSAGE);
            } else {
                if (nombre.matches("[a-zA-ZñÑ1234567890_ ]+")) {
                    // Nombre válido
                } else {
                    valido = false;
                    JOptionPane.showMessageDialog(null, "Nombre no válido, no se permiten caracteres especiales", "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            }

            if (valido) {
                try {
                    // Verificar si el nombre ya existe
                    if (controlador.nombreFicheroExiste(nombre)) {
                        JOptionPane.showMessageDialog(null, "Ya existe un dibujo con este nombre. Por favor, elija otro nombre.", "ERROR", JOptionPane.ERROR_MESSAGE);
                    } else {
                        controlador.guardarFicheroYPoligonos(nombre);
                        nombreArchivoBBDD = nombre;  // Actualizar el nombre del archivo
                         setTitle("PAINT | " + " | "  + nombreArchivoBBDD);
 // Actualizar el título de la ventana
                        JOptionPane.showMessageDialog(null, "Dibujo guardado correctamente.", "ÉXITO", JOptionPane.INFORMATION_MESSAGE);
                        nombreDibujo.setText(nombreArchivoBBDD);
                        nombreValido = true; // Salir del bucle
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Error al guardar el dibujo: " + e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            nombreValido = true; // Salir del bucle si se cancela la operación
        }
    } 
    }//GEN-LAST:event_guardarEnMenuActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        // TODO add your handling code here:
        String selectedFichero = nombreArchivoBBDD;

        // Mostrar ventana de advertencia para confirmar la eliminación
        int confirm = JOptionPane.showConfirmDialog(this, "¿Estás seguro de que deseas eliminar el fichero " + selectedFichero + "? Esta acción no se puede deshacer ", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Llamar al controlador para eliminar el fichero
                controlador.eliminarFichero(selectedFichero);

                // Actualizar el JComboBox después de la eliminación
                // Mostrar mensaje de éxito
                JOptionPane.showMessageDialog(this, "Fichero eliminado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                nombreDibujo.setText("Dibujo sin título");
                 setTitle("PAINT | " + " | "  + "Dibujo sin título");

            } catch (SQLException e) {
                // Manejar errores de la base de datos
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error al eliminar el fichero: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        limpiarPanel();
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void actualizarEnMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_actualizarEnMenuActionPerformed
        // TODO add your handling code here:
        actualizarDibujo();

    }//GEN-LAST:event_actualizarEnMenuActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        // TODO add your handling code here:
        showFileSelectionDialog();
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
        mostrarDialogoSalida();
        
    }//GEN-LAST:event_jMenuItem1ActionPerformed

private void mostrarDialogoSalida() {
    // Cargar la imagen
    ImageIcon icono = cargarImagen("paint/Images/lagrimas.png");

    // Crear el mensaje y el icono en un panel
    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());

    JLabel etiquetaImagen = new JLabel(icono);
    panel.add(etiquetaImagen, BorderLayout.WEST);

    JTextArea mensaje = new JTextArea("¿Seguro que quiere salir del programa? Cualquier cambio que no haya guardado previamente se perderá. Todos esos momentos se perderán en el tiempo, como lágrimas en la lluvia.");
    mensaje.setWrapStyleWord(true);
    mensaje.setLineWrap(true);
    mensaje.setEditable(false);
    mensaje.setFocusable(false);
    mensaje.setBackground(UIManager.getColor("Label.background"));
    mensaje.setFont(UIManager.getFont("Label.font"));
    mensaje.setBorder(UIManager.getBorder("Label.border"));
    panel.add(mensaje, BorderLayout.CENTER);

    // Mostrar el cuadro de diálogo
    int option = JOptionPane.showConfirmDialog(
            this,
            panel,
            "Confirmar salida",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
    );

    if (option == JOptionPane.YES_OPTION) {
        System.exit(0);
    }
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

    private void limpiarPanel(JPanel panel) {
        controlador.limpiarDibujos(); // Limpia el estado interno
        panel.repaint(); // Forza el repintado del panel
    }

    public int getAreaDibujoWidth() {
        return areaDibujo.getWidth();
    }

    public int getAreaDibujoHeight() {
        return areaDibujo.getHeight();
    }

    public Color getSelectedColor() {
        return jColorChooser2.getColor();
    }
// Método para limpiar el área de dibujo

    public void limpiarPanel() {
        Graphics g = areaDibujo.getGraphics();
        g.setColor(areaDibujo.getBackground());
        g.fillRect(0, 0, areaDibujo.getWidth(), areaDibujo.getHeight());
        g.dispose(); // Libera los recursos del Graphics
    }

// Método para inicializar la acción de deshacer
    private void initUndoAction() {
        KeyStroke undoKeyStrokeCtrl = KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK);
        KeyStroke undoKeyStrokeCmd = KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.META_DOWN_MASK);
        Action undoAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controlador.deshacer();
            }
        };
        areaDibujo.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(undoKeyStrokeCtrl, "UNDO_CTRL");
        areaDibujo.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(undoKeyStrokeCmd, "UNDO_CMD");
        areaDibujo.getActionMap().put("UNDO_CTRL", undoAction);
        areaDibujo.getActionMap().put("UNDO_CMD", undoAction);
    }

// Método para obtener el área de dibujo
    public JPanel getAreaDibujo() {
        return areaDibujo;
    }

    private void actualizarDibujo() {
        String selectedFichero = nombreArchivoBBDD;
        if (selectedFichero == null || selectedFichero.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione un dibujo para actualizar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            controlador.actualizarDibujo(selectedFichero);
            JOptionPane.showMessageDialog(this, "Dibujo actualizado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al actualizar el dibujo: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel NombreDibujo;
    private javax.swing.JMenuItem actualizarEnMenu;
    private javax.swing.JPanel areaDibujo;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel etiquetaNumeroVertices;
    private javax.swing.JMenuItem guardarEnMenu;
    private javax.swing.JButton jButton2;
    private javax.swing.JColorChooser jColorChooser2;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JSlider jSlider1;
    private javax.swing.JButton limpiar;
    private javax.swing.JLabel nombreDibujo;
    private javax.swing.JCheckBox rellenarFigura;
    private javax.swing.JRadioButton seleccionarCircunferencia;
    private javax.swing.JRadioButton seleccionarPoligonoIrregular;
    private javax.swing.JRadioButton seleccionarPoligonoRegular;
    private javax.swing.JRadioButton seleccionarPunto;
    private javax.swing.JRadioButton seleccionarRecta;
    // End of variables declaration//GEN-END:variables
}
//VSCODE
