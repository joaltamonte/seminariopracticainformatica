package sisgeinli;
/*
clase encargada de realizar la conexion a la base de datos 
*/
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {
    private static final String URL = "jdbc:mysql://localhost:3306/insumos_db";
    private static final String USER = "root";
    private static final String PASSWORD = "password_mysql"; 

    public static Connection obtenerConexion() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("⚠️ Driver de MySQL no encontrado.");
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}