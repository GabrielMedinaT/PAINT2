package paint;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MODELO {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/paint";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root1234";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public int guardarFichero(String nombre) throws SQLException {
        String query = "INSERT INTO ficheros (nombre) VALUES (?)";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(query, new String[]{"id"})) {
            stmt.setString(1, nombre);
            stmt.executeUpdate();
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("No se pudo obtener el ID del fichero.");
                }
            }
        }
    }

    public int guardarPoligono(int cantidadLados, int ficheroId, boolean relleno, String color) throws SQLException {
        String query = "INSERT INTO poligono (cantidad_lados, fichero_id, relleno, color) VALUES (?, ?, ?, ?)";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(query, new String[]{"id"})) {
            stmt.setInt(1, cantidadLados);
            stmt.setInt(2, ficheroId);
            stmt.setBoolean(3, relleno);
            stmt.setString(4, color);
            stmt.executeUpdate();
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("No se pudo obtener el ID del polígono.");
                }
            }
        }
    }

    public void guardarPunto(int poligonoId, int x, int y) throws SQLException {
        String query = "INSERT INTO poligono_puntos (poligono_id, coordenada_x, coordenada_y) VALUES (?, ?, ?)";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setInt(1, poligonoId);
            stmt.setBigDecimal(2, BigDecimal.valueOf(x));
            stmt.setBigDecimal(3, BigDecimal.valueOf(y));
            stmt.executeUpdate();
        }
    }

    public int obtenerFicheroIdPorNombre(String nombre) throws SQLException {
        String query = "SELECT id FROM ficheros WHERE nombre = ?";
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, nombre);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                } else {
                    throw new SQLException("No se encontró el fichero con el nombre especificado.");
                }
            }
        }
    }

    public List<String> obtenerNombresFicheros() throws SQLException {
        String query = "SELECT nombre FROM ficheros";
        List<String> nombres = new ArrayList<>();
        try (Connection con = getConnection(); PreparedStatement stmt = con.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                nombres.add(rs.getString("nombre"));
            }
        }
        return nombres;
    }

    public List<PAINT.Figura> obtenerFigurasPorFicheroId(int ficheroId) throws SQLException {
        String queryPoligonos = "SELECT id, cantidad_lados, relleno, color FROM poligono WHERE fichero_id = ?";
        List<PAINT.Figura> figuras = new ArrayList<>();

        try (Connection con = getConnection(); PreparedStatement stmtPoligonos = con.prepareStatement(queryPoligonos)) {
            stmtPoligonos.setInt(1, ficheroId);
            try (ResultSet rsPoligonos = stmtPoligonos.executeQuery()) {
                while (rsPoligonos.next()) {
                    int poligonoId = rsPoligonos.getInt("id");
                    int cantidadLados = rsPoligonos.getInt("cantidad_lados");
                    boolean relleno = rsPoligonos.getBoolean("relleno");
                    String color = rsPoligonos.getString("color");

                    String queryPuntos = "SELECT coordenada_x, coordenada_y FROM poligono_puntos WHERE poligono_id = ?";
                    List<Integer> puntos = new ArrayList<>();
                    try (PreparedStatement stmtPuntos = con.prepareStatement(queryPuntos)) {
                        stmtPuntos.setInt(1, poligonoId);
                        try (ResultSet rsPuntos = stmtPuntos.executeQuery()) {
                            while (rsPuntos.next()) {
                                puntos.add(rsPuntos.getBigDecimal("coordenada_x").intValue());
                                puntos.add(rsPuntos.getBigDecimal("coordenada_y").intValue());
                            }
                        }
                    }

                    int[] puntosArray = puntos.stream().mapToInt(i -> i).toArray();
                    figuras.add(new PAINT.Figura(puntosArray, relleno, color, cantidadLados));
                }
            }
        }
        return figuras;
    }
}
