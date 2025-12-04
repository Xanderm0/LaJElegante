package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class BaseDAO<T> {

    protected final Connection conn = ConectarBD.conectar();
    protected PreparedStatement ps;

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

