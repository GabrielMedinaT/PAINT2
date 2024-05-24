package paint;

import java.awt.Color;
import java.awt.Graphics;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class PAINT {

    private int x;
    private int y;
    private Graphics g;
    private int R;

    public PAINT() {
    }

    public void dibujarPunto(int x, int y, Graphics g, Color color) {
        g.setColor(color);
        g.fillOval(x, y, 5, 5);
        System.out.println(x + " " + y);
    }

    public void dibujarRecta(int x, int y, int x2, int y2, Graphics g, Color color, boolean fill) {
        g.setColor(color);
        g.drawLine(x, y, x2, y2);
        System.out.println(x + " " + y + " " + x2 + " " + y2);
    }

    public void dibujarCircunferencia(int x, int y, int x2, int y2, Graphics g, Color color, boolean fill) {
        g.setColor(color);
        R = (int) (Math.sqrt(Math.pow((Double.valueOf(x2) - Double.valueOf(x)), 2) + Math.pow((Double.valueOf(y2) - Double.valueOf(y)), 2)));
        if (fill == true) {
            g.fillOval(x2 - R, y2 - R, 2 * R, 2 * R);
        } else {
            g.drawOval(x2 - R, y2 - R, 2 * R, 2 * R);
        }
        System.out.println(x + " " + y + " " + x2 + " " + y2);
    }

    public void dibujarPoligonoR(int x, int y, int x2, int y2, int nPoints, Graphics g, Color color, boolean fill) {
        g.setColor(color);

        int[] puntosX = new int[nPoints];
        int[] puntosY = new int[nPoints];

        double radio = Math.sqrt(Math.pow((x2 - x), 2) + Math.pow((y2 - y), 2));
        double anguloInicial = -Math.PI / 2;
        double anguloIncremento = 2 * Math.PI / nPoints;
        for (int i = 0; i < nPoints; i++) {
            double angulo = anguloInicial + i * anguloIncremento;
            puntosX[i] = (int) (x2 + radio * Math.cos(angulo));
            puntosY[i] = (int) (y2 + radio * Math.sin(angulo));
        }

        if (fill == true) {
            g.fillPolygon(puntosX, puntosY, nPoints);
        } else {
            g.drawPolygon(puntosX, puntosY, nPoints);
        }
    }

    public void dibujarPoligonoI(int[] xPoints, int[] yPoints, int nPoints, Graphics g, Color color, boolean fill) {
        g.setColor(color);

        if (fill == true) {
            g.fillPolygon(xPoints, yPoints, nPoints);
        } else {
            for (int i = 0; i < nPoints; i++) {
                int siguienteIndice = (i + 1) % nPoints;
                g.drawLine(xPoints[i], yPoints[i], xPoints[siguienteIndice], yPoints[siguienteIndice]);
            }
        }
    }

}
