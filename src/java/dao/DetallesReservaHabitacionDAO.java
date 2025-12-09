package dao;

import models.DetallesReservaHabitacion;
import models.Habitacion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DetallesReservaHabitacionDAO extends BaseDAO<DetallesReservaHabitacion> {

    @Override
    public void crear(DetallesReservaHabitacion d) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConectarBD.conectar();

            String sql = "INSERT INTO detalles_reserva_habitacion ("
                    + "id_habitacion, cantidad_personas, cantidad_noches, precio_noche, "
                    + "descuento_aplicado, recargo_aplicado, precio_reserva, observacion, "
                    + "fecha_inicio, fecha_fin, created_at, updated_at"
                    + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";

            ps = conn.prepareStatement(sql);

            ps.setInt(1, d.getHabitacion().getIdHabitacion());
            ps.setInt(2, d.getCantidadPersonas());
            ps.setInt(3, d.getCantidadNoches());
            ps.setDouble(4, d.getPrecioNoche());
            ps.setDouble(5, d.getDescuentoAplicado());
            ps.setDouble(6, d.getRecargoAplicado());
            ps.setDouble(7, d.getPrecioReserva());
            ps.setString(8, d.getObservacion());

            ps.setDate(9, new java.sql.Date(d.getFechaInicio().getTime()));
            ps.setDate(10, new java.sql.Date(d.getFechaFin().getTime()));

            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error en DetallesReservaHabitacionDAO.crear: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps);
        }
    }

    @Override
    public DetallesReservaHabitacion buscar(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        DetallesReservaHabitacion d = null;

        try {
            conn = ConectarBD.conectar();
            String sql = "SELECT * FROM detalles_reserva_habitacion "
                       + "WHERE id_detalle_reserva_hab = ? AND deleted_at IS NULL";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                d = mapearResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error en DetallesReservaHabitacionDAO.buscar: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }

        return d;
    }

    @Override
    public List<DetallesReservaHabitacion> listar() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<DetallesReservaHabitacion> lista = new ArrayList<>();

        try {
            conn = ConectarBD.conectar();
            String sql = "SELECT * FROM detalles_reserva_habitacion "
                       + "WHERE deleted_at IS NULL "
                       + "ORDER BY fecha_inicio";

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapearResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error en DetallesReservaHabitacionDAO.listar: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }

        return lista;
    }

    @Override
    public void actualizar(DetallesReservaHabitacion d) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConectarBD.conectar();

            String sql = "UPDATE detalles_reserva_habitacion SET "
                       + "id_habitacion = ?, cantidad_personas = ?, cantidad_noches = ?, "
                       + "precio_noche = ?, descuento_aplicado = ?, recargo_aplicado = ?, "
                       + "precio_reserva = ?, observacion = ?, fecha_inicio = ?, fecha_fin = ?, "
                       + "updated_at = NOW() "
                       + "WHERE id_detalle_reserva_hab = ? AND deleted_at IS NULL";

            ps = conn.prepareStatement(sql);

            ps.setInt(1, d.getHabitacion().getIdHabitacion());
            ps.setInt(2, d.getCantidadPersonas());
            ps.setInt(3, d.getCantidadNoches());
            ps.setDouble(4, d.getPrecioNoche());
            ps.setDouble(5, d.getDescuentoAplicado());
            ps.setDouble(6, d.getRecargoAplicado());
            ps.setDouble(7, d.getPrecioReserva());
            ps.setString(8, d.getObservacion());

            ps.setDate(9, new java.sql.Date(d.getFechaInicio().getTime()));
            ps.setDate(10, new java.sql.Date(d.getFechaFin().getTime()));

            ps.setInt(11, d.getIdDetalleReservaHab());

            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error en DetallesReservaHabitacionDAO.actualizar: " + e.getMessage());
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
            String sql = "UPDATE detalles_reserva_habitacion "
                       + "SET deleted_at = NOW() "
                       + "WHERE id_detalle_reserva_hab = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);

            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error en DetallesReservaHabitacionDAO.eliminar: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps);
        }
    }

    private DetallesReservaHabitacion mapearResultSet(ResultSet rs) throws SQLException {

        DetallesReservaHabitacion d = new DetallesReservaHabitacion();

        d.setIdDetalleReservaHab(rs.getInt("id_detalle_reserva_hab"));

        Habitacion h = new Habitacion();
        h.setIdHabitacion(rs.getInt("id_habitacion"));
        d.setHabitacion(h);

        d.setCantidadPersonas(rs.getInt("cantidad_personas"));
        d.setCantidadNoches(rs.getInt("cantidad_noches"));
        d.setPrecioNoche(rs.getDouble("precio_noche"));
        d.setDescuentoAplicado(rs.getDouble("descuento_aplicado"));
        d.setRecargoAplicado(rs.getDouble("recargo_aplicado"));
        d.setPrecioReserva(rs.getDouble("precio_reserva"));
        d.setObservacion(rs.getString("observacion"));
        d.setFechaInicio(rs.getDate("fecha_inicio"));
        d.setFechaFin(rs.getDate("fecha_fin"));

        Timestamp created = rs.getTimestamp("created_at");
        Timestamp updated = rs.getTimestamp("updated_at");
        Timestamp deleted = rs.getTimestamp("deleted_at");

        d.setCreatedAt(created != null ? created.toLocalDateTime() : null);
        d.setUpdatedAt(updated != null ? updated.toLocalDateTime() : null);
        d.setDeletedAt(deleted != null ? deleted.toLocalDateTime() : null);

        return d;
    }
}
