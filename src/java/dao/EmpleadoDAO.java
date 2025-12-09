package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import models.Empleado;
import models.enums.Rol;

public class EmpleadoDAO extends BaseDAO<Empleado> {

    @Override
    public void crear(Empleado emp) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConectarBD.conectar();
            String sql = "INSERT INTO users "
                       + "(name, email, password, role, created_at, updated_at) "
                       + "VALUES (?, ?, ?, ?, NOW(), NOW())";

            ps = conn.prepareStatement(sql);
            ps.setString(1, emp.getName());
            ps.setString(2, emp.getEmail());
            ps.setString(3, emp.getPassword());
            ps.setString(4, emp.getRol().name());

            ps.executeUpdate();

        } catch (SQLException e) {
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
            String sql = "SELECT * FROM users WHERE id = ? AND deleted_at IS NULL";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                emp = mapearResultSet(rs);
            }

        } catch (SQLException e) {
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
            String sql = "SELECT * FROM users WHERE deleted_at IS NULL ORDER BY name";

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                empleados.add(mapearResultSet(rs));
            }

        } catch (SQLException e) {
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
            String sql = "UPDATE users SET "
                       + "name = ?, email = ?, password = ?, role = ?, "
                       + "updated_at = NOW() "
                       + "WHERE id = ?";

            ps = conn.prepareStatement(sql);
            ps.setString(1, emp.getName());
            ps.setString(2, emp.getEmail());
            ps.setString(3, emp.getPassword());
            ps.setString(4, emp.getRol().name());
            ps.setInt(5, emp.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
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
            String sql = "UPDATE users SET deleted_at = NOW() WHERE id = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);

            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error en EmpleadoDAO.eliminar: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps);
        }
    }

    private Empleado mapearResultSet(ResultSet rs) throws SQLException {
        Empleado emp = new Empleado();

        emp.setId(rs.getInt("id"));
        emp.setName(rs.getString("name"));
        emp.setEmail(rs.getString("email"));
        emp.setPassword(rs.getString("password"));
        emp.setRol(Rol.valueOf(rs.getString("role")));

        Timestamp created = rs.getTimestamp("created_at");
        Timestamp updated = rs.getTimestamp("updated_at");
        Timestamp deleted = rs.getTimestamp("deleted_at");

        emp.setCreatedAt(created != null ? created.toLocalDateTime() : null);
        emp.setUpdatedAt(updated != null ? updated.toLocalDateTime() : null);
        emp.setDeletedAt(deleted != null ? deleted.toLocalDateTime() : null);

        return emp;
    }
}
