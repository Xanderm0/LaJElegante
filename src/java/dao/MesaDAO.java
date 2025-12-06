package dao;

import models.Mesa;
import models.enums.Estado;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MesaDAO extends BaseDAO<Mesa> {

    @Override
    public void crear(Mesa m) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConectarBD.conectar();
            String sql =
                    "INSERT INTO mesas " +
                    "(numero_mesa, capacidad, zona, ubicacion_detalle, estado, created_at, updated_at) " +
                    "VALUES (?, ?, ?, ?, ?, NOW(), NOW())";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, m.getNumeroMesa());
            ps.setInt(2, m.getCapacidad());
            ps.setString(3, m.getZona());
            ps.setString(4, m.getUbicacionDetalle());
            ps.setString(5, m.getEstado().name());

            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error en MesaDAO.crear: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps);
        }
    }

    @Override
    public Mesa buscar(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Mesa mesa = null;

        try {
            conn = ConectarBD.conectar();
            String sql =
                    "SELECT * FROM mesas " +
                    "WHERE id_mesa = ? AND deleted_at IS NULL";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);

            rs = ps.executeQuery();

            if (rs.next()) {
                mesa = mapearResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error en MesaDAO.buscar: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }

        return mesa;
    }

    @Override
    public List<Mesa> listar() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Mesa> mesas = new ArrayList<>();

        try {
            conn = ConectarBD.conectar();
            String sql =
                    "SELECT * FROM mesas " +
                    "WHERE deleted_at IS NULL " +
                    "ORDER BY numero_mesa";

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                mesas.add(mapearResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error en MesaDAO.listar: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }

        return mesas;
    }

    @Override
    public void actualizar(Mesa m) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConectarBD.conectar();
            String sql =
                    "UPDATE mesas SET " +
                    "numero_mesa = ?, capacidad = ?, zona = ?, ubicacion_detalle = ?, estado = ?, " +
                    "updated_at = NOW() " +
                    "WHERE id_mesa = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, m.getNumeroMesa());
            ps.setInt(2, m.getCapacidad());
            ps.setString(3, m.getZona());
            ps.setString(4, m.getUbicacionDetalle());
            ps.setString(5, m.getEstado().name());
            ps.setInt(6, m.getIdMesa());

            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error en MesaDAO.actualizar: " + e.getMessage());
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
            String sql =
                    "UPDATE mesas SET deleted_at = NOW() WHERE id_mesa = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);

            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error en MesaDAO.eliminar: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps);
        }
    }

    private Mesa mapearResultSet(ResultSet rs) throws SQLException {

        Mesa m = new Mesa();

        m.setIdMesa(rs.getInt("id_mesa"));
        m.setNumeroMesa(rs.getInt("numero_mesa"));
        m.setCapacidad(rs.getInt("capacidad"));
        m.setZona(rs.getString("zona"));
        m.setUbicacionDetalle(rs.getString("ubicacion_detalle"));

        m.setEstado(Estado.valueOf(rs.getString("estado")));

        // Fechas ClaseBase
        Timestamp created = rs.getTimestamp("created_at");
        Timestamp updated = rs.getTimestamp("updated_at");
        Timestamp deleted = rs.getTimestamp("deleted_at");

        m.setCreatedAt(created != null ? created.toLocalDateTime() : null);
        m.setUpdatedAt(updated != null ? updated.toLocalDateTime() : null);
        m.setDeletedAt(deleted != null ? deleted.toLocalDateTime() : null);

        return m;
    }
}
