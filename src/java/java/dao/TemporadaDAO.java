package dao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import models.Temporada;
import models.enums.NombreTemporada;

public class TemporadaDAO {

    private final Connection conn = ConectarBD.conectar();
    private PreparedStatement ps;
    private ResultSet rs;

    // ------------------------------------------------------------
    // Obtener temporada actual según fechas
    // ------------------------------------------------------------
    public Temporada getTemporadaActual() {
        Temporada temporada = null;

        try {
            String sql = "SELECT * FROM temporadas "
                       + "WHERE NOW() BETWEEN fecha_inicio AND fecha_fin "
                       + "AND deleted_at IS NULL "
                       + "LIMIT 1";

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            if (rs.next()) {
                temporada = mapTemporada(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return temporada;
    }

    // ------------------------------------------------------------
    // Listar todas las temporadas activas
    // ------------------------------------------------------------
    public List<Temporada> getAllTemporadas() {
        List<Temporada> lista = new ArrayList<>();

        try {
            String sql = "SELECT * FROM temporadas WHERE deleted_at IS NULL";

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapTemporada(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // ------------------------------------------------------------
    // Crear temporada
    // ------------------------------------------------------------
    public void createTemporada(Temporada t) {
        try {
            String sql = "INSERT INTO temporadas "
                       + "(nombre, fecha_inicio, fecha_fin, modificador_precio, created_at, updated_at) "
                       + "VALUES (?, ?, ?, ?, NOW(), NOW())";

            ps = conn.prepareStatement(sql);
            ps.setString(1, t.getNombre().name());
            ps.setDate(2, java.sql.Date.valueOf(t.getFechaInicio()));
            ps.setDate(3, java.sql.Date.valueOf(t.getFechaFin()));
            ps.setDouble(4, t.getModificadorPrecio());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ------------------------------------------------------------
    // Actualizar temporada
    // ------------------------------------------------------------
    public void updateTemporada(Temporada t) {
        try {
            String sql = "UPDATE temporadas SET "
                       + "nombre = ?, fecha_inicio = ?, fecha_fin = ?, modificador_precio = ?, "
                       + "updated_at = NOW() "
                       + "WHERE id_temporada = ?";

            ps = conn.prepareStatement(sql);
            ps.setString(1, t.getNombre().name());
            ps.setDate(2, java.sql.Date.valueOf(t.getFechaInicio()));
            ps.setDate(3, java.sql.Date.valueOf(t.getFechaFin()));
            ps.setDouble(4, t.getModificadorPrecio());
            ps.setInt(5, t.getIdTemporada());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ------------------------------------------------------------
    // Mapeo ResultSet → Temporada
    // ------------------------------------------------------------
    private Temporada mapTemporada(ResultSet rs) throws SQLException {

        Temporada t = new Temporada();

        t.setIdTemporada(rs.getInt("id_temporada"));
        t.setNombre(NombreTemporada.valueOf(rs.getString("nombre")));
        t.setFechaInicio(rs.getDate("fecha_inicio").toLocalDate());
        t.setFechaFin(rs.getDate("fecha_fin").toLocalDate());
        t.setModificadorPrecio(rs.getDouble("modificador_precio"));

        // Fechas de ClaseBase
        Timestamp created = rs.getTimestamp("created_at");
        Timestamp updated = rs.getTimestamp("updated_at");
        Timestamp deleted = rs.getTimestamp("deleted_at");

        t.setCreatedAt(created != null ? created.toLocalDateTime() : null);
        t.setUpdatedAt(updated != null ? updated.toLocalDateTime() : null);
        t.setDeletedAt(deleted != null ? deleted.toLocalDateTime() : null);

        return t;
    }
}
