package dao;

import models.Habitacion;
import models.TipoHabitacion;
import models.enums.EstadoHabitacion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HabitacionDAO extends BaseDAO<Habitacion> {

    private final TipoHabitacionDAO tipoDAO = new TipoHabitacionDAO();

    @Override
    public void crear(Habitacion h) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConectarBD.conectar();
            String sql =
                "INSERT INTO habitaciones " +
                "(id_tipo_habitacion, numero_habitacion, estado_habitacion, created_at, updated_at) " +
                "VALUES (?, ?, ?, NOW(), NOW())";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, h.getTipoHabitacion().getIdTipoHabitacion());
            ps.setInt(2, h.getNumeroHabitacion());
            ps.setString(3, h.getEstadoHabitacion().name().toLowerCase());

            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error en HabitacionDAO.crear: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps);
        }
    }

    @Override
    public Habitacion buscar(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Habitacion h = null;

        try {
            conn = ConectarBD.conectar();
            String sql =
                "SELECT * FROM habitaciones " +
                "WHERE id_habitacion = ? AND deleted_at IS NULL";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);

            rs = ps.executeQuery();

            if (rs.next()) {
                h = mapearResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error en HabitacionDAO.buscar: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }

        return h;
    }

    @Override
    public List<Habitacion> listar() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Habitacion> habitaciones = new ArrayList<>();

        try {
            conn = ConectarBD.conectar();
            String sql = "SELECT * FROM habitaciones WHERE deleted_at IS NULL ORDER BY numero_habitacion";

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                habitaciones.add(mapearResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error en HabitacionDAO.listar: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }

        return habitaciones;
    }

    @Override
    public void actualizar(Habitacion h) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConectarBD.conectar();
            String sql =
                "UPDATE habitaciones SET " +
                "id_tipo_habitacion = ?, numero_habitacion = ?, estado_habitacion = ?, " +
                "updated_at = NOW() " +
                "WHERE id_habitacion = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, h.getTipoHabitacion().getIdTipoHabitacion());
            ps.setInt(2, h.getNumeroHabitacion());
            ps.setString(3, h.getEstadoHabitacion().name().toLowerCase());
            ps.setInt(4, h.getIdHabitacion());

            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error en HabitacionDAO.actualizar: " + e.getMessage());
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
            String sql = "UPDATE habitaciones SET deleted_at = NOW() WHERE id_habitacion = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);

            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error en HabitacionDAO.eliminar: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps);
        }
    }

    private Habitacion mapearResultSet(ResultSet rs) throws SQLException {

        Habitacion h = new Habitacion();

        h.setIdHabitacion(rs.getInt("id_habitacion"));
        h.setNumeroHabitacion(rs.getInt("numero_habitacion"));
        h.setEstadoHabitacion(
            EstadoHabitacion.valueOf(rs.getString("estado_habitacion").toUpperCase())
        );

        int idTipo = rs.getInt("id_tipo_habitacion");
        TipoHabitacion tipo = tipoDAO.buscar(idTipo);
        h.setTipoHabitacion(tipo);

        Timestamp created = rs.getTimestamp("created_at");
        Timestamp updated = rs.getTimestamp("updated_at");
        Timestamp deleted = rs.getTimestamp("deleted_at");

        h.setCreatedAt(created != null ? created.toLocalDateTime() : null);
        h.setUpdatedAt(updated != null ? updated.toLocalDateTime() : null);
        h.setDeletedAt(deleted != null ? deleted.toLocalDateTime() : null);

        return h;
    }
}