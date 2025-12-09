package dao;

import com.mysql.cj.jdbc.Driver;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConectarBD {
    public static Connection conectar(){
        Connection conn = null;
        
        try {
            Driver drv = new Driver();
            DriverManager.registerDriver(drv);
            
            String cad = "jdbc:mysql://localhost:3306/hotelLJE";
            conn = DriverManager.getConnection(cad, "root", "");
        } catch (SQLException e) {
            System.out.println("Error en Conexión a Base de Datos");
        }
        
        return conn;
    }
    
    public static void cerrarConexion(Connection conn, PreparedStatement ps, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            System.out.println("Error cerrando conexión: " + e.getMessage());
        }
    }
}
