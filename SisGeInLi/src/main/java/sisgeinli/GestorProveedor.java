package sisgeinli;
/*
Clase encargada de guardar en base de datos al proveedor 
*/
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class GestorProveedor {
    private ArrayList<Proveedor> listaProveedoresMemoria;

    public GestorProveedor() {
        this.listaProveedoresMemoria = new ArrayList<>();
    }

    public void agregarProveedor(Proveedor p) {
        listaProveedoresMemoria.add(p);
        System.out.println("✔ Proveedor '" + p.getNombreProveedor() + "' listo en memoria.");
    }

    public void guardarProveedoresEnBD() {
        String sql = "INSERT INTO proveedores (nombre_proveedor, columna_id_lista_proveedor, columna_codigo_barras, columna_descripcion_producto, columna_importe_sin_iva, columna_cantidad_x_presentacion, incluye_iva) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            for (Proveedor p : listaProveedoresMemoria) {
                ps.setString(1, p.getNombreProveedor());
                ps.setString(2, p.getColumnaIdListaProveedor());
                ps.setString(3, p.getColumnaCodigoBarras());
                ps.setString(4, p.getcolumnaDescripcionProducto());
                ps.setString(5, p.getColumnaImporteSinIva());
                ps.setString(6, p.getColumnaCantidadXPresentacion());
                ps.setBoolean(7, p.getIncluyeIva());
                ps.executeUpdate();
                System.out.println("💾 [MySQL] Proveedor '" + p.getNombreProveedor() + "' guardado.");
            }
            listaProveedoresMemoria.clear(); // Limpiamos la memoria tras guardar
            
        } catch (SQLException e) {
            System.out.println("❌ Error al guardar proveedores: " + e.getMessage());
        }
    }

    public ArrayList<Proveedor> getListaProveedoresMemoria() { return listaProveedoresMemoria; }
}