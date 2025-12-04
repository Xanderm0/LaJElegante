package dao;

import models.ReservaRestaurante;
import models.Cliente;
import models.Mesa;
import models.enums.Estado;
import models.enums.EstadoReserva;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ReservaRestauranteDAO {

    private final Connection conn = ConectarBD.conectar();
    private PreparedStatement ps;
    private ResultSet rs;

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final MesaDAO mesaDAO = new MesaDAO();

    public ReservaRestaurante getById(int id) {

        ReservaRestaurante r = null;

        try {
            String sql = "SELECT * FROM reservas_restaurante "
                    + "WHERE id_reserva = ? AND deleted_at IS NULL";

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
    
    public List<ReservaRestaurante> getByCliente(int idCliente) {

        List<ReservaRestaurante> lista = new ArrayList<>();

        try {
            String sql = "SELECT * FROM reservas_restaurante "
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

    public void create(ReservaRestaurante r) {

        try {
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

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateEstado(ReservaRestaurante r) {

        try {
            String sql = "UPDATE reservas_restaurante SET "
                    + "estado_reserva = ?, activo = ?, updated_at = NOW() "
                    + "WHERE id_reserva = ?";

            ps = conn.prepareStatement(sql);

            ps.setString(1, r.getEstadoReserva().name());
            ps.setString(2, r.getEstado().name());
            ps.setInt(3, r.getIdReserva());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {

        try {
            String sql = "UPDATE reservas_restaurante "
                    + "SET deleted_at = NOW() "
                    + "WHERE id_reserva = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private ReservaRestaurante mapRow(ResultSet rs) throws SQLException {

        ReservaRestaurante r = new ReservaRestaurante();

        r.setIdReserva(rs.getInt("id_reserva"));

        Cliente cli = clienteDAO.getById(rs.getInt("id_cliente"));
        Mesa mesa = mesaDAO.getById(rs.getInt("id_mesa"));

        r.setCliente(cli);
        r.setMesa(mesa);

        r.setFechaReserva(rs.getDate("fecha_reserva").toLocalDate());
        r.setHoraReserva(rs.getTime("hora_reserva").toLocalTime());

        r.setNumeroPersonas(rs.getInt("numero_personas"));

        r.setEstadoReserva(EstadoReserva.valueOf(rs.getString("estado_reserva")));
        r.setEstado(Estado.valueOf(rs.getString("activo")));

        r.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        r.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

        Timestamp del = rs.getTimestamp("deleted_at");
        r.setDeletedAt(del != null ? del.toLocalDateTime() : null);

        return r;
    }
}

