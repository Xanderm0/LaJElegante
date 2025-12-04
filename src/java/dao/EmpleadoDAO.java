package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import models.Empleado;
import models.enums.Rol;

public class EmpleadoDAO {

    private final Connection conn = ConectarBD.conectar();
    private PreparedStatement ps;
    private ResultSet rs;

    // LISTAR TODOS LOS EMPLEADOS ACTIVOS
    public List<Empleado> getAll() {
        List<Empleado> lista = new ArrayList<>();

        try {
            String sql = "SELECT * FROM users WHERE deleted_at IS NULL";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapRow(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // BUSCAR POR ID
    public Empleado getById(int id) {
        Empleado emp = null;

        try {
            String sql = "SELECT * FROM users WHERE id = ? AND deleted_at IS NULL";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                emp = mapRow(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return emp;
    }

    // BUSCAR POR EMAIL
    public Empleado getByEmail(String email) {
        Empleado emp = null;

        try {
            String sql = "SELECT * FROM users WHERE email = ? AND deleted_at IS NULL";
            ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            rs = ps.executeQuery();

            if (rs.next()) {
                emp = mapRow(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return emp;
    }

    // CREAR EMPLEADO
    public void create(Empleado emp) {
        try {
            String sql = "INSERT INTO users (name, email, password, role, created_at, updated_at) "
                       + "VALUES (?, ?, ?, ?, NOW(), NOW())";

            ps = conn.prepareStatement(sql);
            ps.setString(1, emp.getName());
            ps.setString(2, emp.getEmail());
            ps.setString(3, emp.getPassword());
            ps.setString(4, emp.getRol().name());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ACTUALIZAR EMPLEADO
    public void update(Empleado emp) {
        try {
            String sql = "UPDATE users SET name = ?, email = ?, password = ?, role = ?, "
                       + "updated_at = NOW() WHERE id = ?";

            ps = conn.prepareStatement(sql);
            ps.setString(1, emp.getName());
            ps.setString(2, emp.getEmail());
            ps.setString(3, emp.getPassword());
            ps.setString(4, emp.getRol().name());
            ps.setInt(5, emp.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ELIMINACIÓN LÓGICA (SOFT DELETE)
    public void delete(int id) {
        try {
            String sql = "UPDATE users SET deleted_at = NOW() WHERE id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // MAPEAR REGISTRO A OBJETO EMPLEADO
    private Empleado mapRow(ResultSet rs) throws SQLException {
        Empleado emp = new Empleado();

        emp.setId(rs.getInt("id"));
        emp.setName(rs.getString("name"));
        emp.setEmail(rs.getString("email"));
        emp.setPassword(rs.getString("password"));

        emp.setRol(
                Rol.valueOf(rs.getString("role"))
        );

        // atributos de ClaseBase
        emp.setCreatedAt(rs.getTimestamp("created_at"));
        emp.setUpdatedAt(rs.getTimestamp("updated_at"));
        emp.setDeletedAt(rs.getTimestamp("deleted_at"));

        return emp;
    }
}
