package dao;

import models.Habitacion;
import models.TipoHabitacion;
import models.enums.EstadoHabitacion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HabitacionDAO {

    private final Connection conn = ConectarBD.conectar();
    private PreparedStatement ps;
    private ResultSet rs;

    private final TipoHabitacionDAO tipoDAO = new TipoHabitacionDAO();

    public List<Habitacion> getAll() {
        List<Habitacion> lista = new ArrayList<>();

        try {
            String sql = "SELECT * FROM habitaciones WHERE deleted_at IS NULL";
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
    
    public Habitacion getById(int id) {
        Habitacion h = null;

        try {
            String sql = "SELECT * FROM habitaciones WHERE id_habitacion = ? AND deleted_at IS NULL";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                h = mapRow(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return h;
    }
    
    public void create(Habitacion h) {
        try {
            String sql =
                    "INSERT INTO habitaciones (id_tipo_habitacion, numero_habitacion, estado_habitacion, created_at, updated_at) "
                            + "VALUES (?, ?, ?, NOW(), NOW())";

            ps = conn.prepareStatement(sql);

            ps.setInt(1, h.getTipoHabitacion().getIdTipoHabitacion());
            ps.setInt(2, h.getNumeroHabitacion());
            ps.setString(3, h.getEstadoHabitacion().name().toLowerCase());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Habitacion h) {
        try {
            String sql =
                    "UPDATE habitaciones SET id_tipo_habitacion = ?, numero_habitacion = ?, "
                            + "estado_habitacion = ?, updated_at = NOW() WHERE id_habitacion = ?";

            ps = conn.prepareStatement(sql);

            ps.setInt(1, h.getTipoHabitacion().getIdTipoHabitacion());
            ps.setInt(2, h.getNumeroHabitacion());
            ps.setString(3, h.getEstadoHabitacion().name().toLowerCase());
            ps.setInt(4, h.getIdHabitacion());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        try {
            String sql = "UPDATE habitaciones SET deleted_at = NOW() WHERE id_habitacion = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Habitacion mapRow(ResultSet rs) throws SQLException {
        Habitacion h = new Habitacion();

        h.setIdHabitacion(rs.getInt("id_habitacion"));
        h.setNumeroHabitacion(rs.getInt("numero_habitacion"));

        h.setEstadoHabitacion(
                EstadoHabitacion.valueOf(rs.getString("estado_habitacion").toUpperCase())
        );

        int idTipoHab = rs.getInt("id_tipo_habitacion");
        TipoHabitacion tipo = tipoDAO.getById(idTipoHab);
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

