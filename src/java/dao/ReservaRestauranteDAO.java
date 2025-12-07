package dao;

import models.ReservaRestaurante;
import models.Cliente;
import models.Mesa;
import models.enums.Estado;
import models.enums.EstadoReserva;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservaRestauranteDAO extends BaseDAO<ReservaRestaurante> {

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final MesaDAO mesaDAO = new MesaDAO();

    @Override
    public void crear(ReservaRestaurante r) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConectarBD.conectar();
            String sql = "INSERT INTO reservas_restaurante "
                       + "(id_cliente, id_mesa, fecha_reserva, hora_reserva, "
                       + "numero_personas, estado_reserva, activo, created_at, updated_at) "
                       + "VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, r.getCliente().getIdCliente());
            ps.setInt(2, r.getMesa().getIdMesa());
            ps.setDate(3, Date.valueOf(r.getFechaReserva()));
            ps.setTime(4, Time.valueOf(r.getHoraReserva()));
            ps.setInt(5, r.getNumeroPersonas());
            ps.setString(6, r.getEstadoReserva().name());
            ps.setString(7, r.getEstado().name());

            int filas = ps.executeUpdate();
            System.out.println(filas + " reserva(s) creada(s)");
        } catch (SQLException e) {
            System.err.println("Error en ReservaRestauranteDAO.crear: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps);
        }
    }

    @Override
    public ReservaRestaurante buscar(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ReservaRestaurante r = null;

        try {
            conn = ConectarBD.conectar();
            String sql = "SELECT * FROM reservas_restaurante WHERE id_reserva = ? AND deleted_at IS NULL";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                r = mapRow(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error en ReservaRestauranteDAO.buscar: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }

        return r;
    }

    @Override
    public List<ReservaRestaurante> listar() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<ReservaRestaurante> lista = new ArrayList<>();

        try {
            conn = ConectarBD.conectar();
            String sql = "SELECT * FROM reservas_restaurante WHERE deleted_at IS NULL ORDER BY fecha_reserva, hora_reserva";

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error en ReservaRestauranteDAO.listar: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }

        return lista;
    }

    @Override
    public void actualizar(ReservaRestaurante r) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConectarBD.conectar();
            String sql = "UPDATE reservas_restaurante SET "
                       + "id_cliente = ?, id_mesa = ?, fecha_reserva = ?, hora_reserva = ?, "
                       + "numero_personas = ?, estado_reserva = ?, activo = ?, updated_at = NOW() "
                       + "WHERE id_reserva = ? AND deleted_at IS NULL";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, r.getCliente().getIdCliente());
            ps.setInt(2, r.getMesa().getIdMesa());
            ps.setDate(3, Date.valueOf(r.getFechaReserva()));
            ps.setTime(4, Time.valueOf(r.getHoraReserva()));
            ps.setInt(5, r.getNumeroPersonas());
            ps.setString(6, r.getEstadoReserva().name());
            ps.setString(7, r.getEstado().name());
            ps.setInt(8, r.getIdReserva());

            int filas = ps.executeUpdate();
            System.out.println(filas + " reserva(s) actualizada(s)");
        } catch (SQLException e) {
            System.err.println("Error en ReservaRestauranteDAO.actualizar: " + e.getMessage());
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
            String sql = "UPDATE reservas_restaurante SET deleted_at = NOW() WHERE id_reserva = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);

            int filas = ps.executeUpdate();
            System.out.println(filas + " reserva(s) eliminada(s)");
        } catch (SQLException e) {
            System.err.println("Error en ReservaRestauranteDAO.eliminar: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps);
        }
    }

    private ReservaRestaurante mapRow(ResultSet rs) throws SQLException {
        ReservaRestaurante r = new ReservaRestaurante();

        r.setIdReserva(rs.getInt("id_reserva"));

        Cliente cli = clienteDAO.buscar(rs.getInt("id_cliente"));
        Mesa mesa = mesaDAO.buscar(rs.getInt("id_mesa"));

        r.setCliente(cli);
        r.setMesa(mesa);

        r.setFechaReserva(rs.getDate("fecha_reserva").toLocalDate());
        r.setHoraReserva(rs.getTime("hora_reserva").toLocalTime());

        r.setNumeroPersonas(rs.getInt("numero_personas"));
        r.setEstadoReserva(EstadoReserva.valueOf(rs.getString("estado_reserva")));
        r.setEstado(Estado.valueOf(rs.getString("activo")));

        Timestamp created = rs.getTimestamp("created_at");
        Timestamp updated = rs.getTimestamp("updated_at");
        Timestamp deleted = rs.getTimestamp("deleted_at");

        r.setCreatedAt(created != null ? created.toLocalDateTime() : null);
        r.setUpdatedAt(updated != null ? updated.toLocalDateTime() : null);
        r.setDeletedAt(deleted != null ? deleted.toLocalDateTime() : null);

        return r;
    }
}
