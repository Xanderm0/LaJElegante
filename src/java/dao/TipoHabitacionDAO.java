package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import models.TipoHabitacion;
import models.enums.NombreTipoHabitacion;

public class TipoHabitacionDAO extends BaseDAO<TipoHabitacion> {
    
    @Override
    public void crear(TipoHabitacion t) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConectarBD.conectar();
            String sql = "INSERT INTO tipo_habitacion "
                       + "(nombre_tipo, descripcion, capacidad_maxima, created_at, updated_at) "
                       + "VALUES (?, ?, ?, NOW(), NOW())";

            ps = conn.prepareStatement(sql);
            ps.setString(1, t.getNombreTipo().name());
            ps.setString(2, t.getDescripcion());
            ps.setInt(3, t.getCapacidadMaxima());

            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error en TipoHabitacionDAO.crear: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps);
        }
    }

    @Override
    public TipoHabitacion buscar(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        TipoHabitacion tipo = null;

        try {
            conn = ConectarBD.conectar();
            String sql = "SELECT * FROM tipo_habitacion "
                       + "WHERE id_tipo_habitacion = ? AND deleted_at IS NULL";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                tipo = mapearResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error en TipoHabitacionDAO.buscar: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }
        return tipo;
    }

    @Override
    public List<TipoHabitacion> listar() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<TipoHabitacion> lista = new ArrayList<>();

        try {
            conn = ConectarBD.conectar();
            String sql = "SELECT * FROM tipo_habitacion "
                       + "WHERE deleted_at IS NULL ORDER BY id_tipo_habitacion";

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapearResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error en TipoHabitacionDAO.listar: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }
        return lista;
    }

    @Override
    public void actualizar(TipoHabitacion t) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConectarBD.conectar();
            String sql = "UPDATE tipo_habitacion SET "
                       + "nombre_tipo = ?, descripcion = ?, capacidad_maxima = ?, "
                       + "updated_at = NOW() "
                       + "WHERE id_tipo_habitacion = ?";

            ps = conn.prepareStatement(sql);
            ps.setString(1, t.getNombreTipo().name());
            ps.setString(2, t.getDescripcion());
            ps.setInt(3, t.getCapacidadMaxima());
            ps.setInt(4, t.getIdTipoHabitacion());

            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error en TipoHabitacionDAO.actualizar: " + e.getMessage());
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
            String sql = "UPDATE tipo_habitacion "
                       + "SET deleted_at = NOW() "
                       + "WHERE id_tipo_habitacion = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);

            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error en TipoHabitacionDAO.eliminar: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps);
        }
    }

    private TipoHabitacion mapearResultSet(ResultSet rs) throws SQLException {

        TipoHabitacion t = new TipoHabitacion();

        t.setIdTipoHabitacion(rs.getInt("id_tipo_habitacion"));
        t.setNombreTipo(NombreTipoHabitacion.valueOf(rs.getString("nombre_tipo")));
        t.setDescripcion(rs.getString("descripcion"));
        t.setCapacidadMaxima(rs.getInt("capacidad_maxima"));

        // timestamps de ClaseBase
        Timestamp created = rs.getTimestamp("created_at");
        Timestamp updated = rs.getTimestamp("updated_at");
        Timestamp deleted = rs.getTimestamp("deleted_at");

        t.setCreatedAt(created != null ? created.toLocalDateTime() : null);
        t.setUpdatedAt(updated != null ? updated.toLocalDateTime() : null);
        t.setDeletedAt(deleted != null ? deleted.toLocalDateTime() : null);

        return t;
    }
}
