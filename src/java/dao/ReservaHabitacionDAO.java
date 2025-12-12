package dao;

import models.ReservaHabitacion;
import models.Cliente;
import models.DetallesReservaHabitacion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservaHabitacionDAO extends BaseDAO<ReservaHabitacion> {

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final DetallesReservaHabitacionDAO detalleDAO = new DetallesReservaHabitacionDAO();

    @Override
    public void crear(ReservaHabitacion r) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConectarBD.conectar();
            String sql =
                "INSERT INTO reserva_habitacion " +
                "(id_cliente, id_detalle_reserva_hab, created_at, updated_at) " +
                "VALUES (?, ?, NOW(), NOW())";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, r.getCliente().getIdCliente());
            ps.setInt(2, r.getDetalleReservaHabitacion().getIdDetalleReservaHab());

            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error en ReservaHabitacionDAO.crear: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps);
        }
    }

    @Override
    public ReservaHabitacion buscar(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ReservaHabitacion r = null;

        try {
            conn = ConectarBD.conectar();
            String sql =
                "SELECT * FROM reserva_habitacion " +
                "WHERE id_reserva_habitacion = ? AND deleted_at IS NULL";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                r = mapearResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error en ReservaHabitacionDAO.buscar: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }

        return r;
    }

    @Override
    public List<ReservaHabitacion> listar() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<ReservaHabitacion> lista = new ArrayList<>();

        try {
            conn = ConectarBD.conectar();
            String sql =
                "SELECT * FROM reserva_habitacion " +
                "WHERE deleted_at IS NULL " +
                "ORDER BY id_reserva_habitacion DESC";

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapearResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error en ReservaHabitacionDAO.listar: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }

        return lista;
    }

    @Override
    public void actualizar(ReservaHabitacion r) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConectarBD.conectar();
            String sql =
                "UPDATE reserva_habitacion SET " +
                "id_cliente = ?, id_detalle_reserva_hab = ?, " +
                "updated_at = NOW() " +
                "WHERE id_reserva_habitacion = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, r.getCliente().getIdCliente());
            ps.setInt(2, r.getDetalleReservaHabitacion().getIdDetalleReservaHab());
            ps.setInt(3, r.getIdReservaHabitacion());

            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error en ReservaHabitacionDAO.actualizar: " + e.getMessage());
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
            String sql =
                "UPDATE reserva_habitacion SET deleted_at = NOW() " +
                "WHERE id_reserva_habitacion = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);

            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error en ReservaHabitacionDAO.eliminar: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps);
        }
    }

    private ReservaHabitacion mapearResultSet(ResultSet rs) throws SQLException {

        ReservaHabitacion r = new ReservaHabitacion();

        r.setIdReservaHabitacion(rs.getInt("id_reserva_habitacion"));

        int idCliente = rs.getInt("id_cliente");
        Cliente cli = clienteDAO.buscar(idCliente);
        r.setCliente(cli);

        int idDetalle = rs.getInt("id_detalle_reserva_hab");
        DetallesReservaHabitacion det = detalleDAO.buscar(idDetalle);
        r.setDetalleReservaHabitacion(det);

        Timestamp created = rs.getTimestamp("created_at");
        Timestamp updated = rs.getTimestamp("updated_at");
        Timestamp deleted = rs.getTimestamp("deleted_at");

        r.setCreatedAt(created != null ? created.toLocalDateTime() : null);
        r.setUpdatedAt(updated != null ? updated.toLocalDateTime() : null);
        r.setDeletedAt(deleted != null ? deleted.toLocalDateTime() : null);

        return r;
    }
}