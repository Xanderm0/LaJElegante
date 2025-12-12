package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import models.Tarifa;
import models.TipoHabitacion;
import models.Temporada;
import models.enums.Estado;

public class TarifaDAO extends BaseDAO<Tarifa> {

    @Override
    public void crear(Tarifa t) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConectarBD.conectar();
            t.prePersist();
            String sql = "INSERT INTO tarifas "
                       + "(tarifa_fija, precio_final, estado, id_tipo_habitacion, id_temporada, created_at, updated_at) "
                       + "VALUES (?, ?, ?, ?, ?, ?, ?)";

            ps = conn.prepareStatement(sql);
            ps.setFloat(1, t.getTarifaFija());
            ps.setFloat(2, t.getPrecioFinal());
            ps.setString(3, t.getEstado().name());
            ps.setInt(4, t.getTipoHabitacion().getIdTipoHabitacion());
            ps.setInt(5, t.getTemporada().getIdTemporada());
            ps.setTimestamp(6, Timestamp.valueOf(t.getCreatedAt()));
            ps.setTimestamp(7, Timestamp.valueOf(t.getUpdatedAt()));

            int filas = ps.executeUpdate();
            System.out.println(filas + " tarifa(s) creada(s)");

        } catch (SQLException e) {
            System.err.println("Error en TarifaDAO.crear: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps);
        }
    }

    @Override
    public Tarifa buscar(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Tarifa t = null;

        try {
            conn = ConectarBD.conectar();
            String sql = "SELECT * FROM tarifas WHERE id_tarifa = ? AND deleted_at IS NULL";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                t = mapTarifa(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error en TarifaDAO.buscar: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }
        return t;
    }

    @Override
    public List<Tarifa> listar() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Tarifa> lista = new ArrayList<>();

        try {
            conn = ConectarBD.conectar();
            String sql = "SELECT * FROM tarifas WHERE deleted_at IS NULL";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapTarifa(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error en TarifaDAO.listar: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }

        return lista;
    }

    @Override
    public void actualizar(Tarifa t) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConectarBD.conectar();
            t.preUpdate();
            String sql = "UPDATE tarifas SET tarifa_fija = ?, precio_final = ?, estado = ?, "
                       + "id_tipo_habitacion = ?, id_temporada = ?, updated_at = ? "
                       + "WHERE id_tarifa = ? AND deleted_at IS NULL";

            ps = conn.prepareStatement(sql);
            ps.setFloat(1, t.getTarifaFija());
            ps.setFloat(2, t.getPrecioFinal());
            ps.setString(3, t.getEstado().name());
            ps.setInt(4, t.getTipoHabitacion().getIdTipoHabitacion());
            ps.setInt(5, t.getTemporada().getIdTemporada());
            ps.setTimestamp(6, Timestamp.valueOf(t.getUpdatedAt()));
            ps.setInt(7, t.getIdTarifa());

            int filas = ps.executeUpdate();
            System.out.println(filas + " tarifa(s) actualizada(s)");

        } catch (SQLException e) {
            System.err.println("Error en TarifaDAO.actualizar: " + e.getMessage());
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
            String sql = "UPDATE tarifas SET deleted_at = NOW() WHERE id_tarifa = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            int filas = ps.executeUpdate();
            System.out.println(filas + " tarifa(s) eliminada(s)");

        } catch (SQLException e) {
            System.err.println("Error en TarifaDAO.eliminar: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps);
        }
    }

    private Tarifa mapTarifa(ResultSet rs) throws SQLException {
        Tarifa t = new Tarifa();

        t.setIdTarifa(rs.getInt("id_tarifa"));
        t.setTarifaFija(rs.getFloat("tarifa_fija"));
        t.setPrecioFinal(rs.getFloat("precio_final"));
        t.setEstado(Estado.valueOf(rs.getString("estado")));

        TipoHabitacion tipo = new TipoHabitacion();
        tipo.setIdTipoHabitacion(rs.getInt("id_tipo_habitacion"));
        t.setTipoHabitacion(tipo);

        Temporada temporada = new Temporada();
        temporada.setIdTemporada(rs.getInt("id_temporada"));
        t.setTemporada(temporada);

        Timestamp created = rs.getTimestamp("created_at");
        Timestamp updated = rs.getTimestamp("updated_at");
        Timestamp deleted = rs.getTimestamp("deleted_at");

        t.setCreatedAt(created != null ? created.toLocalDateTime() : null);
        t.setUpdatedAt(updated != null ? updated.toLocalDateTime() : null);
        t.setDeletedAt(deleted != null ? deleted.toLocalDateTime() : null);

        return t;
    }
}