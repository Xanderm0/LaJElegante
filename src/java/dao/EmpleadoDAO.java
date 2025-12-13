package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import utils.MessageUtil;
import models.Empleado;
import models.enums.Rol;

public class EmpleadoDAO extends BaseDAO<Empleado> {

    @Override
    public void crear(Empleado emp) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConectarBD.conectar();
            String sql = "INSERT INTO empleados "
                       + "(nombre, email, contrasena, rol, estado, created_at, updated_at) "
                       + "VALUES (?, ?, ?, ?, 'activo', NOW(), NOW())";

            ps = conn.prepareStatement(sql);
            ps.setString(1, emp.getName());
            ps.setString(2, emp.getEmail());
            ps.setString(3, emp.getPassword());
            ps.setString(4, emp.getRol().getValor());

            ps.executeUpdate();
            MessageUtil.createSuccess("empleado");
        } catch (SQLException e) {
            MessageUtil.createError("empleado");
            System.err.println("Error en EmpleadoDAO.crear: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps);
        }
    }

    @Override
    public Empleado buscar(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Empleado emp = null;

        try {
            conn = ConectarBD.conectar();
            String sql = "SELECT * FROM empleados WHERE id_empleado = ? AND deleted_at IS NULL";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                emp = mapearResultSet(rs);
            }

        } catch (SQLException e) {
            MessageUtil.error("Error al buscar empleado: " + e.getMessage());
            System.err.println("Error en EmpleadoDAO.buscar: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }

        return emp;
    }

    @Override
    public List<Empleado> listar() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Empleado> empleados = new ArrayList<>();

        try {
            conn = ConectarBD.conectar();
            String sql = "SELECT * FROM empleados WHERE deleted_at IS NULL ORDER BY nombre";

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                empleados.add(mapearResultSet(rs));
            }

        } catch (SQLException e) {
            MessageUtil.error("Error al listar empleados: " + e.getMessage());
            System.err.println("Error en EmpleadoDAO.listar: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }

        return empleados;
    }

    @Override
    public void actualizar(Empleado emp) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConectarBD.conectar();
        String sql = "UPDATE empleados SET "
                   + "nombre = ?, email = ?, contrasena = ?, rol = ?, "
                   + "updated_at = NOW() "
                   + "WHERE id_empleado = ?";

            ps = conn.prepareStatement(sql);
            ps.setString(1, emp.getName());
            ps.setString(2, emp.getEmail());
            ps.setString(3, emp.getPassword());
            ps.setString(4, emp.getRol().getValor());
            ps.setInt(5, emp.getId());

            ps.executeUpdate();
            MessageUtil.updateSuccess("empleado");
        } catch (SQLException e) {
            MessageUtil.updateError("empleado");
            System.err.println("Error en EmpleadoDAO.actualizar: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps);
        }
    }

    @Override
    public void eliminar(int id) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConectarBD.conectar();
            String sql = "UPDATE empleados SET deleted_at = NOW() WHERE id_empleado = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);

            ps.executeUpdate();
            MessageUtil.deleteSuccess("empleado");
        } catch (SQLException e) {
            MessageUtil.deleteError("empleado");
            System.err.println("Error en EmpleadoDAO.eliminar: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps);
        }
    }
    
    @Override
    public List<Empleado> listarEliminados() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Empleado> empleados = new ArrayList<>();
        
        try {
            conn = ConectarBD.conectar();
            String sql = "SELECT * FROM empleados WHERE deleted_at IS NOT NULL ORDER BY deleted_at DESC";
            
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                empleados.add(mapearResultSet(rs));
            }
            
        } catch (SQLException e) {
            MessageUtil.error("Error al listar empleados eliminados: " + e.getMessage());
            System.err.println("Error en EmpleadoDAO.listarEliminados: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }
        return empleados;
    }
    
    @Override
    public void restaurar(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = ConectarBD.conectar();
            String sql = "UPDATE empleados SET deleted_at = NULL, updated_at = NOW() WHERE id_empleado = ?";
            
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
            MessageUtil.success("Empleado restaurado correctamente");
        } catch (SQLException e) {
            MessageUtil.error("Error al restaurar empleado: " + e.getMessage());
            System.err.println("Error en EmpleadoDAO.restaurar: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps);
        }
    }
    
    @Override
    public Empleado buscarConTrash(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Empleado emp = null;
        
        try {
            conn = ConectarBD.conectar();
            String sql = "SELECT * FROM empleados WHERE id_empleado = ?";
            
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                emp = mapearResultSet(rs);
            }
            
        } catch (SQLException e) {
            MessageUtil.error("Error al buscar empleado (con papelera): " + e.getMessage());
            System.err.println("Error en EmpleadoDAO.buscarConTrash: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }
        return emp;
    }
    
    public Empleado buscarPorEmail(String email) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Empleado emp = null;

        try {
            conn = ConectarBD.conectar();
            String sql = "SELECT * FROM empleados WHERE email = ? AND deleted_at IS NULL";

            ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            rs = ps.executeQuery();

            if (rs.next()) {
                emp = mapearResultSet(rs);
            }

        } catch (SQLException e) {
            MessageUtil.error("Error al buscar empleado por email: " + e.getMessage());
            System.err.println("Error en EmpleadoDAO.buscarPorEmail: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }
        return emp;
    }
    
    public boolean existeEmail(String email, int idExcluir) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = ConectarBD.conectar();
            String sql = "SELECT COUNT(*) FROM empleados "
                       + "WHERE email = ? "
                       + "AND id_empleado != ? "
                       + "AND deleted_at IS NULL";
            
            ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            ps.setInt(2, idExcluir);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            MessageUtil.error("Error al verificar email de empleado: " + e.getMessage());
            System.err.println("Error en EmpleadoDAO.existeEmail: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }
        return false;
    }
    
    public List<Empleado> listarPorRol(Rol rol) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Empleado> empleados = new ArrayList<>();
        
        try {
            conn = ConectarBD.conectar();
            String sql = "SELECT * FROM empleados WHERE rol = ? AND deleted_at IS NULL ORDER BY nombre";
            
            ps = conn.prepareStatement(sql);
            ps.setString(1, rol.getValor());
            rs = ps.executeQuery();
            
            while (rs.next()) {
                empleados.add(mapearResultSet(rs));
            }
            
        } catch (SQLException e) {
            MessageUtil.error("Error al listar empleados por rol: " + e.getMessage());
            System.err.println("Error en EmpleadoDAO.listarPorRol: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }
        return empleados;
    }

    private Rol stringARol(String rolStr) {
        if (rolStr == null) return null;
        
        rolStr = rolStr.trim().toLowerCase();
        
        for (Rol rol : Rol.values()) {
            if (rol.getValor().equals(rolStr)) {
                return rol;
            }
        }
        MessageUtil.error("Rol no reconocido: " + rolStr);
        System.err.println("Rol no reconocido: " + rolStr);
        return Rol.RECEPCIONISTA;
    }

    private Empleado mapearResultSet(ResultSet rs) throws SQLException {
        Empleado emp = new Empleado();

        emp.setId(rs.getInt("id_empleado"));
        emp.setName(rs.getString("nombre"));
        emp.setEmail(rs.getString("email"));
        emp.setPassword(rs.getString("contrasena"));
        
        String rolStr = rs.getString("rol");
        emp.setRol(stringARol(rolStr));

        Timestamp created = rs.getTimestamp("created_at");
        Timestamp updated = rs.getTimestamp("updated_at");
        Timestamp deleted = rs.getTimestamp("deleted_at");

        emp.setCreatedAt(created != null ? created.toLocalDateTime() : null);
        emp.setUpdatedAt(updated != null ? updated.toLocalDateTime() : null);
        emp.setDeletedAt(deleted != null ? deleted.toLocalDateTime() : null);

        return emp;
    }
}