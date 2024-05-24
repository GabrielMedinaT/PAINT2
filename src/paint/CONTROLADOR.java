
package paint;

import java.awt.Color;
import java.awt.Graphics;

public class CONTROLADOR {
    
    PAINT paint = new PAINT();
    
    public void dibujarPunto(int x, int y, Graphics g, Color color) {
        paint.dibujarPunto(x, y, g, color);
    }
    
    public void dibujarRecta(int x, int y, int x2, int y2, Graphics g, Color color, boolean fill) {
        paint.dibujarRecta(x, y, x2, y2, g, color, fill);
    }

    public void dibujarCircunferencia(int x, int y, int x2, int y2, Graphics g, Color color, boolean fill) {
        paint.dibujarCircunferencia(x, y, x2, y2, g, color, fill);
    }

    public void dibujarPoligonoR(int x, int y, int x2, int y2, int nPoints, Graphics g, Color color, boolean fill) {
        paint.dibujarPoligonoR(x, y, x2, y2, nPoints, g, color, fill);
    }

    public void dibujarPoligonoI(int[] xPoints, int[] yPoints, int nPoints, Graphics g, Color color, boolean fill) {
        paint.dibujarPoligonoI(xPoints, yPoints, nPoints, g, color, fill);
    }
    
}
