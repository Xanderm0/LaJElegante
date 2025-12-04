package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import models.TipoHabitacion;
import models.enums.NombreTipoHabitacion;

public class TipoHabitacionDAO {

    private final Connection conn = ConectarBD.conectar();
    private PreparedStatement ps;
    private ResultSet rs;

    // ============================================
    // LISTAR ACTIVOS (deleted_at IS NULL)
    // ============================================
    public List<TipoHabitacion> getAllTiposHabitacion() {
        List<TipoHabitacion> lista = new ArrayList<>();

        try {
            String sql = "SELECT * FROM tipo_habitacion WHERE deleted_at IS NULL";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                TipoHabitacion tipo = new TipoHabitacion();

                tipo.setIdTipoHabitacion(rs.getInt("id_tipo_habitacion"));
                tipo.setNombreTipo(
                        NombreTipoHabitacion.valueOf(rs.getString("nombre_tipo"))
                );
                tipo.setDescripcion(rs.getString("descripcion"));
                tipo.setCapacidadMaxima(rs.getInt("capacidad_maxima"));

                Timestamp created = rs.getTimestamp("created_at");
                Timestamp updated = rs.getTimestamp("updated_at");
                Timestamp deleted = rs.getTimestamp("deleted_at");

                tipo.setCreatedAt(created != null ? created.toLocalDateTime() : null);
                tipo.setUpdatedAt(updated != null ? updated.toLocalDateTime() : null);
                tipo.setDeletedAt(deleted != null ? deleted.toLocalDateTime() : null);

                lista.add(tipo);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // ============================================
    // BUSCAR POR ID
    // ============================================
    public TipoHabitacion getTipoHabitacionById(int id) {
        TipoHabitacion tipo = null;

        try {
            String sql = "SELECT * FROM tipo_habitacion WHERE id_tipo_habitacion = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                tipo = new TipoHabitacion();

                tipo.setIdTipoHabitacion(rs.getInt("id_tipo_habitacion"));
                tipo.setNombreTipo(
                        NombreTipoHabitacion.valueOf(rs.getString("nombre_tipo"))
                );
                tipo.setDescripcion(rs.getString("descripcion"));
                tipo.setCapacidadMaxima(rs.getInt("capacidad_maxima"));

                Timestamp created = rs.getTimestamp("created_at");
                Timestamp updated = rs.getTimestamp("updated_at");
                Timestamp deleted = rs.getTimestamp("deleted_at");

                tipo.setCreatedAt(created != null ? created.toLocalDateTime() : null);
                tipo.setUpdatedAt(updated != null ? updated.toLocalDateTime() : null);
                tipo.setDeletedAt(deleted != null ? deleted.toLocalDateTime() : null);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tipo;
    }

    // ============================================
    // CREAR NUEVO TIPO DE HABITACIÓN
    // ============================================
    public void createTipoHabitacion(TipoHabitacion tipo) {
        try {
            String sql = "INSERT INTO tipo_habitacion "
                    + "(nombre_tipo, descripcion, capacidad_maxima, created_at, updated_at) "
                    + "VALUES (?, ?, ?, NOW(), NOW())";

            ps = conn.prepareStatement(sql);
            ps.setString(1, tipo.getNombreTipo().name());
            ps.setString(2, tipo.getDescripcion());
            ps.setInt(3, tipo.getCapacidadMaxima());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ============================================
    // ACTUALIZAR REGISTRO
    // ============================================
    public void updateTipoHabitacion(TipoHabitacion tipo) {
        try {
            String sql = "UPDATE tipo_habitacion SET "
                    + "nombre_tipo = ?, descripcion = ?, capacidad_maxima = ?, updated_at = NOW() "
                    + "WHERE id_tipo_habitacion = ?";

            ps = conn.prepareStatement(sql);
            ps.setString(1, tipo.getNombreTipo().name());
            ps.setString(2, tipo.getDescripcion());
            ps.setInt(3, tipo.getCapacidadMaxima());
            ps.setInt(4, tipo.getIdTipoHabitacion());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ============================================
    // BORRADO LÓGICO
    // ============================================
    public void deleteTipoHabitacion(int id) {
        try {
            String sql = "UPDATE tipo_habitacion SET deleted_at = NOW() WHERE id_tipo_habitacion = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    TipoHabitacion getById(int idTipoHab) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
