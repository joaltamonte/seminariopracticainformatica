package sisgeinli;
/*
Clase encargada de validar si el usuario y contraseña son válidos, caso contrario
no deja ingresar al menú
*/
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Autenticador {

    public Usuario login(String username, String password) throws InsumoExcepcion {
        String sql = "SELECT username, rol FROM usuarios WHERE username = ? AND password = ?";
        
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, username);
            ps.setString(2, password); 
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(rs.getString("username"), rs.getString("rol"));
                } else {
                    throw new InsumoExcepcion("Acceso denegado: Usuario o contraseña incorrectos.");
                }
            }
        } catch (SQLException e) {
            throw new InsumoExcepcion("Error de conexión al validar usuario: " + e.getMessage());
        }
    }
}