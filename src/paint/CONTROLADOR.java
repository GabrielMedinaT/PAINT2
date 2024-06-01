package paint;

import java.awt.Color;
import java.awt.Graphics;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JComboBox;

public class CONTROLADOR {

    private MODELO modelo = new MODELO();
    private PAINT paint;

    public CONTROLADOR(PAINT paint) {
        this.paint = paint;
    }

    public void dibujarPunto(int x, int y, Graphics g, Color color) {
        paint.dibujarPunto(x, y, g, color);
    }

    public void dibujarRecta(int x, int y, int x2, int y2, Graphics g, Color color) {
        paint.dibujarRecta(x, y, x2, y2, g, color, paint.isRelleno());
    }

    public void dibujarCircunferencia(int x, int y, int x2, int y2, Graphics g, Color color) {
        paint.dibujarCircunferencia(x, y, x2, y2, g, color, paint.isRelleno());
    }

    public void dibujarPoligonoR(int x, int y, int x2, int y2, int nPoints, Graphics g, Color color) {
        paint.dibujarPoligonoR(x, y, x2, y2, nPoints, g, color, paint.isRelleno());
    }

    public void dibujarPoligonoI(int[] xPoints, int[] yPoints, int nPoints, Graphics g, Color color) {
        paint.dibujarPoligonoI(xPoints, yPoints, nPoints, g, color, paint.isRelleno());
    }

    public void guardarFicheroYPoligonos(String nombre) throws SQLException {
        int ficheroId = modelo.guardarFichero(nombre);
        List<PAINT.Figura> figuras = paint.obtenerFigurasDibujadas();
        for (PAINT.Figura figura : figuras) {
            int cantidadLados = figura.getCantidadLados();
            int poligonoId = modelo.guardarPoligono(cantidadLados, ficheroId, figura.isRelleno(), figura.getColor());
            for (int i = 0; i < figura.getPuntos().length; i += 2) {
                modelo.guardarPunto(poligonoId, figura.getPuntos()[i], figura.getPuntos()[i + 1]);
            }
        }
    }

    public void poblarComboBox(JComboBox<String> comboBox) throws SQLException {
        List<String> nombresFicheros = modelo.obtenerNombresFicheros();
        comboBox.removeAllItems(); // Limpiar los elementos existentes
        for (String nombre : nombresFicheros) {
            comboBox.addItem(nombre);
        }
    }

    public void cargarDibujo(String nombreFichero, Graphics g) throws SQLException {
        int ficheroId = modelo.obtenerFicheroIdPorNombre(nombreFichero);
        List<PAINT.Figura> figuras = modelo.obtenerFigurasPorFicheroId(ficheroId);
        for (PAINT.Figura figura : figuras) {
            int[] puntos = figura.getPuntos();
            Color color = Color.decode(figura.getColor());
            boolean relleno = figura.isRelleno();
            int cantidadLados = figura.getCantidadLados();

            if (cantidadLados == 0) {
                paint.dibujarCircunferencia(puntos[0], puntos[1], puntos[2], puntos[3], g, color, relleno);
            } else if (cantidadLados == 1) {
                paint.dibujarPunto(puntos[0], puntos[1], g, color);
            } else if (cantidadLados == 2) {
                paint.dibujarRecta(puntos[0], puntos[1], puntos[2], puntos[3], g, color, relleno);
            } else {
                int[] xPoints = new int[cantidadLados];
                int[] yPoints = new int[cantidadLados];
                for (int i = 0; i < cantidadLados; i++) {
                    xPoints[i] = puntos[2 * i];
                    yPoints[i] = puntos[2 * i + 1];
                }
                paint.dibujarPoligonoI(xPoints, yPoints, cantidadLados, g, color, relleno);
            }
        }
    }
}
