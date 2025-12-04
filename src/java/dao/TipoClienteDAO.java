package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import models.TipoCliente;

public class TipoClienteDAO {

    private final Connection conn = ConectarBD.conectar();
    private PreparedStatement ps;
    private ResultSet rs;

    // ==========================
    // LISTAR TODOS (ACTIVOS)
    // ==========================
    public List<TipoCliente> getAllTiposClientes() {
        List<TipoCliente> lista = new ArrayList<>();

        try {
            String sql = "SELECT * FROM tipo_cliente WHERE deleted_at IS NULL";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                TipoCliente t = new TipoCliente();

                t.setIdTipoCliente(rs.getInt("id_tipo_cliente"));
                t.setNombreTipo(rs.getString("nombre_tipo"));

                // Manejo de fechas como LocalDateTime
                Timestamp created = rs.getTimestamp("created_at");
                Timestamp updated = rs.getTimestamp("updated_at");
                Timestamp deleted = rs.getTimestamp("deleted_at");

                t.setCreatedAt(created != null ? created.toLocalDateTime() : null);
                t.setUpdatedAt(updated != null ? updated.toLocalDateTime() : null);
                t.setDeletedAt(deleted != null ? deleted.toLocalDateTime() : null);

                lista.add(t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // ==========================
    // BUSCAR POR ID
    // ==========================
    public TipoCliente getTipoClienteById(int id) {
        TipoCliente t = null;

        try {
            String sql = "SELECT * FROM tipo_cliente WHERE id_tipo_cliente = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                t = new TipoCliente();

                t.setIdTipoCliente(rs.getInt("id_tipo_cliente"));
                t.setNombreTipo(rs.getString("nombre_tipo"));

                Timestamp created = rs.getTimestamp("created_at");
                Timestamp updated = rs.getTimestamp("updated_at");
                Timestamp deleted = rs.getTimestamp("deleted_at");

                t.setCreatedAt(created != null ? created.toLocalDateTime() : null);
                t.setUpdatedAt(updated != null ? updated.toLocalDateTime() : null);
                t.setDeletedAt(deleted != null ? deleted.toLocalDateTime() : null);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return t;
    }

    // ==========================
    // CREAR
    // ==========================
    public void createTipoCliente(TipoCliente t) {
        try {
            String sql = "INSERT INTO tipo_cliente (nombre_tipo, created_at, updated_at) VALUES (?, NOW(), NOW())";
            ps = conn.prepareStatement(sql);
            ps.setString(1, t.getNombreTipo());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ==========================
    // ACTUALIZAR
    // ==========================
    public void updateTipoCliente(TipoCliente t) {
        try {
            String sql = "UPDATE tipo_cliente SET nombre_tipo = ?, updated_at = NOW() WHERE id_tipo_cliente = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, t.getNombreTipo());
            ps.setInt(2, t.getIdTipoCliente());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ==========================
    // BORRADO LÓGICO
    // ==========================
    public void deleteTipoCliente(int id) {
        try {
            String sql = "UPDATE tipo_cliente SET deleted_at = NOW() WHERE id_tipo_cliente = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
