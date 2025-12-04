package dao;

import models.DetallesReservaHabitacion;
import models.Habitacion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DetallesReservaHabitacionDAO {

    private final Connection conn = ConectarBD.conectar();
    private PreparedStatement ps;
    private ResultSet rs;

    public List<DetallesReservaHabitacion> getAll() {
        List<DetallesReservaHabitacion> lista = new ArrayList<>();

        try {
            String sql = "SELECT * FROM detalles_reserva_habitacion WHERE deleted_at IS NULL";
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

    public DetallesReservaHabitacion getById(int id) {
        DetallesReservaHabitacion d = null;

        try {
            String sql = "SELECT * FROM detalles_reserva_habitacion "
                       + "WHERE id_detalle_reserva_hab = ? AND deleted_at IS NULL";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                d = mapRow(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return d;
    }
    
    public void create(DetallesReservaHabitacion d) {
        try {
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
            e.printStackTrace();
        }
    }

    public void update(DetallesReservaHabitacion d) {
        try {
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
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        try {
            String sql = "UPDATE detalles_reserva_habitacion "
                       + "SET deleted_at = NOW() WHERE id_detalle_reserva_hab = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private DetallesReservaHabitacion mapRow(ResultSet rs) throws SQLException {

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

        d.setCreatedAt(rs.getTimestamp("created_at"));
        d.setUpdatedAt(rs.getTimestamp("updated_at"));
        d.setDeletedAt(rs.getTimestamp("deleted_at"));

        return d;
    }
}

