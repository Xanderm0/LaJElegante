package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class BaseDAO<T> {

    protected final Connection conn = ConectarBD.conectar();
    protected PreparedStatement ps;

    /**
     * Marca created_at y updated_at con NOW() al crear registros.
     */
    protected void applyCreationTimestamps(String table, int id) {
        try {
            String sql = "UPDATE " + table +
                " SET created_at = NOW(), updated_at = NOW() " +
                " WHERE id_" + table.substring(0, table.length() - 1) + " = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Actualiza updated_at con NOW() en una tabla específica.
     */
    protected void applyUpdateTimestamp(String table, int id) {
        try {
            String sql =
                "UPDATE " + table +
                " SET updated_at = NOW() WHERE id_" +
                table.substring(0, table.length() - 1) + " = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Soft delete (marca deleted_at con NOW()).
     */
    protected void applySoftDelete(String table, int id) {
        try {
            String sql =
                "UPDATE " + table +
                " SET deleted_at = NOW() WHERE id_" +
                table.substring(0, table.length() - 1) + " = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Restaurar soft delete (pone deleted_at en NULL).
     */
    protected void restoreDeleted(String table, int id) {
        try {
            String sql =
                "UPDATE " + table +
                " SET deleted_at = NULL WHERE id_" +
                table.substring(0, table.length() - 1) + " = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
