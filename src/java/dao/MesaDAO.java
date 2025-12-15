package dao;

import models.Mesa;
import models.enums.Estado;
import utils.MessageUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

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
            ps.setString(5, m.getEstado().getValor());

            ps.executeUpdate();
            MessageUtil.createSuccess("mesa");
        } catch (SQLException e) {
            MessageUtil.createError("mesa");
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
            MessageUtil.error("Error al buscar mesa: " + e.getMessage());
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
            MessageUtil.error("Error al listar mesas: " + e.getMessage());
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
            ps.setString(5, m.getEstado().getValor());
            ps.setInt(6, m.getIdMesa());

            ps.executeUpdate();
            MessageUtil.updateSuccess("mesa");
        } catch (SQLException e) {
            MessageUtil.updateError("mesa");
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
            MessageUtil.deleteSuccess("mesa");
        } catch (SQLException e) {
            MessageUtil.deleteError("mesa");
            System.err.println("Error en MesaDAO.eliminar: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps);
        }
    }
    
    @Override
    public List<Mesa> listarEliminados() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Mesa> mesas = new ArrayList<>();

        try {
            conn = ConectarBD.conectar();
            String sql = "SELECT * FROM mesas WHERE deleted_at IS NOT NULL ORDER BY deleted_at DESC";

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                mesas.add(mapearResultSet(rs));
            }

        } catch (SQLException e) {
            MessageUtil.error("Error al listar mesas eliminadas: " + e.getMessage());
            System.err.println("Error en MesaDAO.listarEliminados: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }
        return mesas;
    }

    @Override
    public void restaurar(int id) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConectarBD.conectar();
            String sql = "UPDATE mesas SET deleted_at = NULL, updated_at = NOW() WHERE id_mesa = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
            MessageUtil.success("Mesa restaurada correctamente");
        } catch (SQLException e) {
            MessageUtil.error("Error al restaurar mesa: " + e.getMessage());
            System.err.println("Error en MesaDAO.restaurar: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps);
        }
    }

    @Override
    public Mesa buscarConTrash(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Mesa mesa = null;

        try {
            conn = ConectarBD.conectar();
            String sql = "SELECT * FROM mesas WHERE id_mesa = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                mesa = mapearResultSet(rs);
            }

        } catch (SQLException e) {
            MessageUtil.error("Error al buscar mesa (con papelera): " + e.getMessage());
            System.err.println("Error en MesaDAO.buscarConTrash: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }
        return mesa;
    }
    
    public List<Mesa> obtenerMesasDisponibles(Date fecha, String turno, int capacidadMinima) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Mesa> mesas = new ArrayList<>();

        try {
            conn = ConectarBD.conectar();
            String sql = 
                "SELECT m.* " +
                "FROM mesas m " +
                "WHERE m.estado = ? " +
                "AND m.deleted_at IS NULL " +
                "AND m.capacidad >= ? " +
                "AND m.id_mesa NOT IN ( " +
                "    SELECT rm.id_mesa FROM reservas_mesas rm " +
                "    WHERE DATE(rm.fecha_reserva) = DATE(?) " +
                "    AND rm.turno = ? " +
                "    AND rm.estado_reserva != 'CANCELADA' " +
                "    AND rm.deleted_at IS NULL " +
                ") " +
                "ORDER BY m.capacidad, m.numero_mesa";

            ps = conn.prepareStatement(sql);
            ps.setString(1, Estado.ACTIVO.getValor());
            ps.setInt(2, capacidadMinima);
            ps.setDate(3, new java.sql.Date(fecha.getTime()));
            ps.setString(4, turno);

            rs = ps.executeQuery();

            while (rs.next()) {
                mesas.add(mapearResultSet(rs));
            }

        } catch (SQLException e) {
            MessageUtil.error("Error al buscar mesas disponibles: " + e.getMessage());
            System.err.println("Error en MesaDAO.obtenerMesasDisponibles: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }
        return mesas;
    }

    public boolean estaDisponible(int idMesa, Date fecha, String turno) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConectarBD.conectar();
            String sql = 
                "SELECT COUNT(*) as disponible " +
                "FROM mesas m " +
                "WHERE m.id_mesa = ? " +
                "AND m.estado = ? " +
                "AND m.deleted_at IS NULL " +
                "AND NOT EXISTS ( " +
                "    SELECT 1 FROM reservas_mesas rm " +
                "    WHERE rm.id_mesa = m.id_mesa " +
                "    AND DATE(rm.fecha_reserva) = DATE(?) " +
                "    AND rm.turno = ? " +
                "    AND rm.estado_reserva != 'CANCELADA' " +
                "    AND rm.deleted_at IS NULL " +
                ")";


            ps = conn.prepareStatement(sql);
            ps.setInt(1, idMesa);
            ps.setString(2, Estado.ACTIVO.getValor());
            ps.setDate(3, new java.sql.Date(fecha.getTime()));
            ps.setString(4, turno);

            rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("disponible") > 0;
            }

        } catch (SQLException e) {
            MessageUtil.error("Error al verificar disponibilidad de mesa: " + e.getMessage());
            System.err.println("Error en MesaDAO.estaDisponible: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }
        return false;
    }

    public List<Mesa> obtenerMesasPorZona(String zona) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Mesa> mesas = new ArrayList<>();

        try {
            conn = ConectarBD.conectar();
            String sql = "SELECT * FROM mesas " +
                         "WHERE zona = ? AND deleted_at IS NULL " +
                         "ORDER BY numero_mesa";

            ps = conn.prepareStatement(sql);
            ps.setString(1, zona);
            rs = ps.executeQuery();

            while (rs.next()) {
                mesas.add(mapearResultSet(rs));
            }

        } catch (SQLException e) {
            MessageUtil.error("Error al buscar mesas por zona: " + e.getMessage());
            System.err.println("Error en MesaDAO.obtenerMesasPorZona: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }
        return mesas;
    }

    public List<Mesa> obtenerMesasPorCapacidad(int capacidadMin, int capacidadMax) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Mesa> mesas = new ArrayList<>();

        try {
            conn = ConectarBD.conectar();
            String sql = "SELECT * FROM mesas " +
                         "WHERE capacidad BETWEEN ? AND ? " +
                         "AND deleted_at IS NULL " +
                         "ORDER BY capacidad, numero_mesa";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, capacidadMin);
            ps.setInt(2, capacidadMax);
            rs = ps.executeQuery();

            while (rs.next()) {
                mesas.add(mapearResultSet(rs));
            }

        } catch (SQLException e) {
            MessageUtil.error("Error al buscar mesas por capacidad: " + e.getMessage());
            System.err.println("Error en MesaDAO.obtenerMesasPorCapacidad: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }
        return mesas;
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
    
    public void cambiarEstadoMesa(int idMesa, Estado nuevoEstado) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConectarBD.conectar();
            String sql = "UPDATE mesas SET estado = ?, updated_at = NOW() WHERE id_mesa = ?";
            
            ps = conn.prepareStatement(sql);
            ps.setString(1, nuevoEstado.getValor());
            ps.setInt(2, idMesa);
            
            int filas = ps.executeUpdate();
            if (filas > 0) {
                MessageUtil.success("Estado de mesa actualizado correctamente");
            } else {
                MessageUtil.warn("No se encontró la mesa con ID: " + idMesa);
            }
            
        } catch (SQLException e) {
            MessageUtil.error("Error al cambiar estado de mesa: " + e.getMessage());
            System.err.println("Error en MesaDAO.cambiarEstadoMesa: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps);
        }
    }
}