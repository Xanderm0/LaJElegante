package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import models.Tarifa;
import models.TipoHabitacion;
import models.Temporada;
import models.enums.Estado;

public class TarifaDAO {

    private final Connection conn = ConectarBD.conectar();
    private PreparedStatement ps;
    private ResultSet rs;

    // ------------------------------------------------------------
    // Obtener tarifa por tipoHabitacion + temporada
    // ------------------------------------------------------------
    public Tarifa getTarifa(int idTipoHabitacion, int idTemporada) {
        Tarifa tarifa = null;

        try {
            String sql = "SELECT * FROM tarifas "
                       + "WHERE id_tipo_habitacion = ? "
                       + "AND id_temporada = ? "
                       + "AND deleted_at IS NULL";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, idTipoHabitacion);
            ps.setInt(2, idTemporada);

            rs = ps.executeQuery();

            if (rs.next()) {
                tarifa = mapTarifa(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tarifa;
    }

    // ------------------------------------------------------------
    // Obtener todas las tarifas
    // ------------------------------------------------------------
    public List<Tarifa> getAllTarifas() {
        List<Tarifa> lista = new ArrayList<>();

        try {
            String sql = "SELECT * FROM tarifas WHERE deleted_at IS NULL";

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapTarifa(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // ------------------------------------------------------------
    // Crear tarifa
    // ------------------------------------------------------------
    public void createTarifa(Tarifa t) {
        try {
            String sql = "INSERT INTO tarifas "
                       + "(tarifa_fija, precio_final, estado, id_tipo_habitacion, id_temporada, created_at, updated_at) "
                       + "VALUES (?, ?, ?, ?, ?, NOW(), NOW())";

            ps = conn.prepareStatement(sql);
            ps.setFloat(1, t.getTarifaFija());
            ps.setFloat(2, t.getPrecioFinal());
            ps.setString(3, t.getEstado().name());
            ps.setInt(4, t.getTipoHabitacion().getIdTipoHabitacion());
            ps.setInt(5, t.getTemporada().getIdTemporada());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ------------------------------------------------------------
    // Actualizar tarifa
    // ------------------------------------------------------------
    public void updateTarifa(Tarifa t) {
        try {
            String sql = "UPDATE tarifas SET "
                       + "tarifa_fija = ?, precio_final = ?, estado = ?, "
                       + "id_tipo_habitacion = ?, id_temporada = ?, updated_at = NOW() "
                       + "WHERE id_tarifa = ?";

            ps = conn.prepareStatement(sql);
            ps.setFloat(1, t.getTarifaFija());
            ps.setFloat(2, t.getPrecioFinal());
            ps.setString(3, t.getEstado().name());
            ps.setInt(4, t.getTipoHabitacion().getIdTipoHabitacion());
            ps.setInt(5, t.getTemporada().getIdTemporada());
            ps.setInt(6, t.getIdTarifa());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ------------------------------------------------------------
    // Mapeo ResultSet → Tarifa
    // ------------------------------------------------------------
    private Tarifa mapTarifa(ResultSet rs) throws SQLException {

        Tarifa t = new Tarifa();

        t.setIdTarifa(rs.getInt("id_tarifa"));
        t.setTarifaFija(rs.getFloat("tarifa_fija"));
        t.setPrecioFinal(rs.getFloat("precio_final"));
        t.setEstado(Estado.valueOf(rs.getString("estado")));

        // Crear objetos referenciados
        TipoHabitacion tipo = new TipoHabitacion();
        tipo.setIdTipoHabitacion(rs.getInt("id_tipo_habitacion"));
        t.setTipoHabitacion(tipo);

        Temporada temporada = new Temporada();
        temporada.setIdTemporada(rs.getInt("id_temporada"));
        t.setTemporada(temporada);

        // Fechas de la clase base
        t.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        t.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        Timestamp deleted = rs.getTimestamp("deleted_at");
        t.setDeletedAt(deleted != null ? deleted.toLocalDateTime() : null);

        return t;
    }
}
