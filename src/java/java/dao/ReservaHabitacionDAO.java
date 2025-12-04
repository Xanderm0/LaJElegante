package dao;

import models.ReservaHabitacion;
import models.Cliente;
import models.DetallesReservaHabitacion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservaHabitacionDAO {

    private final Connection conn = ConectarBD.conectar();
    private PreparedStatement ps;
    private ResultSet rs;

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final DetallesReservaHabitacionDAO detalleDAO = new DetallesReservaHabitacionDAO();

    // =====================================================
    //  BUSCAR POR ID
    // =====================================================
    public ReservaHabitacion getById(int id) {
        ReservaHabitacion r = null;

        try {
            String sql = "SELECT * FROM reserva_habitacion "
                       + "WHERE id_reserva_habitacion = ? AND deleted_at IS NULL";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                r = mapRow(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return r;
    }

    // =====================================================
    //  LISTAR POR CLIENTE
    // =====================================================
    public List<ReservaHabitacion> getByCliente(int idCliente) {
        List<ReservaHabitacion> lista = new ArrayList<>();

        try {
            String sql = "SELECT * FROM reserva_habitacion "
                       + "WHERE id_cliente = ? AND deleted_at IS NULL";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, idCliente);
            rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapRow(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // =====================================================
    //  CREAR RESERVA
    // =====================================================
    public void create(ReservaHabitacion r) {

        try {
            String sql = "INSERT INTO reserva_habitacion "
                       + "(id_cliente, id_detalle_reserva_hab, created_at, updated_at) "
                       + "VALUES (?, ?, NOW(), NOW())";

            ps = conn.prepareStatement(sql);

            ps.setInt(1, r.getCliente().getIdCliente());
            ps.setInt(2, r.getDetalleReservaHabitacion().getIdDetalleReservaHab());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // =====================================================
    //  ACTUALIZAR RESERVA
    // =====================================================
    public void update(ReservaHabitacion r) {

        try {
            String sql = "UPDATE reserva_habitacion SET "
                       + "id_cliente = ?, id_detalle_reserva_hab = ?, "
                       + "updated_at = NOW() "
                       + "WHERE id_reserva_habitacion = ?";

            ps = conn.prepareStatement(sql);

            ps.setInt(1, r.getCliente().getIdCliente());
            ps.setInt(2, r.getDetalleReservaHabitacion().getIdDetalleReservaHab());
            ps.setInt(3, r.getIdReservaHabitacion());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // =====================================================
    //  SOFT DELETE
    // =====================================================
    public void delete(int id) {

        try {
            String sql = "UPDATE reserva_habitacion "
                       + "SET deleted_at = NOW() "
                       + "WHERE id_reserva_habitacion = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // =====================================================
    //  MAPEO DE FILA → OBJETO
    // =====================================================
    private ReservaHabitacion mapRow(ResultSet rs) throws SQLException {

        ReservaHabitacion r = new ReservaHabitacion();

        r.setIdReservaHabitacion(rs.getInt("id_reserva_habitacion"));

        // Obtener cliente completo
        int idCliente = rs.getInt("id_cliente");
        Cliente cli = clienteDAO.getById(idCliente);
        r.setCliente(cli);

        // Obtener detalle completo
        int idDetalle = rs.getInt("id_detalle_reserva_hab");
        DetallesReservaHabitacion det = detalleDAO.getById(idDetalle);
        r.setDetalleReservaHabitacion(det);

        // Fechas heredadas de ClaseBase
        r.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        r.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

        Timestamp deleted = rs.getTimestamp("deleted_at");
        r.setDeletedAt(deleted != null ? deleted.toLocalDateTime() : null);

        return r;
    }
}
