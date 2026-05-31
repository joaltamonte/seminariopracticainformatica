package sisgeinli;

import java.math.BigDecimal;
import java.sql.*;
// import java.util.ArrayList;

/**
 * Clase de control responsable de consultar precios.
 * Obtiene el margen de la tabla utilidad y calcula el precio final
 * delegando en ProductoLibreria.calcularPrecioFinal().
 */
public class ConsultadorPrecios {

    // ── Obtiene el margen vigente desde la tabla utilidad ─────────
    private BigDecimal obtenerMargen() throws InsumoExcepcion {
        String sql = "SELECT porcentaje FROM utilidad "
                   + "ORDER BY fecha_actualizacion DESC LIMIT 1";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getBigDecimal("porcentaje");
            throw new InsumoExcepcion("No hay margen de utilidad configurado. "
                    + "Configure uno antes de consultar precios.");
        } catch (SQLException e) {
            throw new InsumoExcepcion("Error al obtener margen: " + e.getMessage());
        }
    }

    // ── Busca por código de barras → devuelve el más caro ─────────
    // Si el mismo producto viene de varios proveedores, el más caro
    // es el que tiene mayor importe_sin_iva
    public void consultarPorCodigo(String codigoBarras) throws InsumoExcepcion {
        BigDecimal margen = obtenerMargen();

        String sql = "SELECT p.descripcion_producto, p.importe_sin_iva, "
                   + "pr.nombre_proveedor "
                   + "FROM productos p "
                   + "JOIN proveedores pr ON p.id_proveedor = pr.id "
                   + "WHERE p.codigo_barras = ? "
                   + "ORDER BY p.importe_sin_iva DESC "
                   + "LIMIT 1";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, codigoBarras);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    mostrarResultado(rs, margen);
                } else {
                    System.out.println("❌ No se encontró ningún producto con ese código de barras.");
                }
            }
        } catch (SQLException e) {
            throw new InsumoExcepcion("Error al consultar por código: " + e.getMessage());
        }
    }

    // ── Busca por descripción → devuelve todos los que coincidan ──
    public void consultarPorDescripcion(String descripcion) throws InsumoExcepcion {
        BigDecimal margen = obtenerMargen();

        String sql = "SELECT p.descripcion_producto, p.importe_sin_iva, "
                   + "pr.nombre_proveedor "
                   + "FROM productos p "
                   + "JOIN proveedores pr ON p.id_proveedor = pr.id "
                   + "WHERE p.descripcion_producto LIKE ? "
                   + "ORDER BY p.descripcion_producto ASC";

        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + descripcion + "%");
            try (ResultSet rs = ps.executeQuery()) {
                int encontrados = 0;
                while (rs.next()) {
                    mostrarResultado(rs, margen);
                    encontrados++;
                }
                if (encontrados == 0) {
                    System.out.println("❌ No se encontraron productos con esa descripción.");
                } else {
                    System.out.println("\n📊 Total encontrados: " + encontrados);
                }
            }
        } catch (SQLException e) {
            throw new InsumoExcepcion("Error al consultar por descripción: " + e.getMessage());
        }
    }

    // ── Muestra el resultado formateado y calcula el precio final ──
    private void mostrarResultado(ResultSet rs, BigDecimal margen) throws SQLException {
        String descripcion   = rs.getString("descripcion_producto");
        BigDecimal costo     = rs.getBigDecimal("importe_sin_iva");
        String proveedor     = rs.getString("nombre_proveedor");

        // Delega el cálculo en ProductoLibreria (polimorfismo)
        GestorMargen mock = new GestorMargen("", "", descripcion, costo, 1, 0);
        BigDecimal pvp = mock.calcularPrecioFinal(margen);

        System.out.println("\n📦 " + descripcion);
        System.out.println("   Proveedor      : " + proveedor);
        System.out.println("   Costo sin IVA  : $" + String.format("%.2f", costo));
        System.out.println("   Margen aplicado: " + margen + "%");
        System.out.println("   Precio de venta: $" + String.format("%.2f", pvp));
    }
}
