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
import utils.MessageUtil;

public class TipoHabitacionDAO extends BaseDAO<TipoHabitacion> {
    
    @Override
    public void crear(TipoHabitacion t) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConectarBD.conectar();
            String sql = "INSERT INTO tipo_habitacion "
                       + "(nombre_tipo, descripcion, capacidad_maxima, created_at, updated_at) "
                       + "VALUES (?, ?, ?, NOW(), NOW())";

            ps = conn.prepareStatement(sql);
            ps.setString(1, t.getNombreTipo().name());
            ps.setString(2, t.getDescripcion());
            ps.setInt(3, t.getCapacidadMaxima());

            ps.executeUpdate();
            MessageUtil.createSuccess("tipo de habitación");
        } catch (SQLException e) {
            MessageUtil.createError("tipo de habitación");
            System.err.println("Error en TipoHabitacionDAO.crear: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps);
        }
    }

    @Override
    public TipoHabitacion buscar(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        TipoHabitacion tipo = null;

        try {
            conn = ConectarBD.conectar();
            String sql = "SELECT * FROM tipo_habitacion "
                       + "WHERE id_tipo_habitacion = ? AND deleted_at IS NULL";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                tipo = mapearResultSet(rs);
            }

        } catch (SQLException e) {
            MessageUtil.error("Error al buscar tipo de habitación: " + e.getMessage());
            System.err.println("Error en TipoHabitacionDAO.buscar: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }
        return tipo;
    }

    @Override
    public List<TipoHabitacion> listar() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<TipoHabitacion> lista = new ArrayList<>();

        try {
            conn = ConectarBD.conectar();
            String sql = "SELECT * FROM tipo_habitacion "
                       + "WHERE deleted_at IS NULL ORDER BY id_tipo_habitacion";

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapearResultSet(rs));
            }

        } catch (SQLException e) {
            MessageUtil.error("Error al listar tipos de habitación: " + e.getMessage());
            System.err.println("Error en TipoHabitacionDAO.listar: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }
        return lista;
    }

    @Override
    public void actualizar(TipoHabitacion t) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConectarBD.conectar();
            String sql = "UPDATE tipo_habitacion SET "
                       + "nombre_tipo = ?, descripcion = ?, capacidad_maxima = ?, "
                       + "updated_at = NOW() "
                       + "WHERE id_tipo_habitacion = ?";

            ps = conn.prepareStatement(sql);
            ps.setString(1, t.getNombreTipo().name());
            ps.setString(2, t.getDescripcion());
            ps.setInt(3, t.getCapacidadMaxima());
            ps.setInt(4, t.getIdTipoHabitacion());

            ps.executeUpdate();
            MessageUtil.updateSuccess("tipo de habitación");
        } catch (SQLException e) {
            MessageUtil.updateError("tipo de habitación");
            System.err.println("Error en TipoHabitacionDAO.actualizar: " + e.getMessage());
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
            String sql = "UPDATE tipo_habitacion "
                       + "SET deleted_at = NOW() "
                       + "WHERE id_tipo_habitacion = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);

            ps.executeUpdate();
            MessageUtil.deleteSuccess("tipo de habitación");
        } catch (SQLException e) {
            MessageUtil.deleteError("tipo de habitación");
            System.err.println("Error en TipoHabitacionDAO.eliminar: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps);
        }
    }
    
    @Override
    public List<TipoHabitacion> listarEliminados() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<TipoHabitacion> lista = new ArrayList<>();

        try {
            conn = ConectarBD.conectar();
            String sql = "SELECT * FROM tipo_habitacion "
                       + "WHERE deleted_at IS NOT NULL "
                       + "ORDER BY deleted_at DESC";

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapearResultSet(rs));
            }

        } catch (SQLException e) {
            MessageUtil.error("Error al listar tipos de habitación eliminados: " + e.getMessage());
            System.err.println("Error en TipoHabitacionDAO.listarEliminados: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }
        return lista;
    }

    @Override
    public void restaurar(int id) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConectarBD.conectar();
            String sql = "UPDATE tipo_habitacion "
                       + "SET deleted_at = NULL, updated_at = NOW() "
                       + "WHERE id_tipo_habitacion = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();

            MessageUtil.success("Tipo de habitación restaurado correctamente");

        } catch (SQLException e) {
            MessageUtil.error("Error al restaurar tipo de habitación: " + e.getMessage());
            System.err.println("Error en TipoHabitacionDAO.restaurar: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps);
        }
    }

    @Override
    public TipoHabitacion buscarConTrash(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        TipoHabitacion tipo = null;

        try {
            conn = ConectarBD.conectar();
            String sql = "SELECT * FROM tipo_habitacion "
                       + "WHERE id_tipo_habitacion = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                tipo = mapearResultSet(rs);
            }

        } catch (SQLException e) {
            MessageUtil.error("Error al buscar tipo de habitación (con papelera): " + e.getMessage());
            System.err.println("Error en TipoHabitacionDAO.buscarConTrash: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }
        return tipo;
    }

    public List<TipoHabitacion> obtenerPorCapacidadMinima(int capacidadMinima) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<TipoHabitacion> lista = new ArrayList<>();

        try {
            conn = ConectarBD.conectar();
            String sql = "SELECT * FROM tipo_habitacion " +
                         "WHERE capacidad_maxima >= ? AND deleted_at IS NULL " +
                         "ORDER BY capacidad_maxima";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, capacidadMinima);
            rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapearResultSet(rs));
            }

        } catch (SQLException e) {
            MessageUtil.error("Error al obtener tipos por capacidad: " + e.getMessage());
            System.err.println("Error en TipoHabitacionDAO.obtenerPorCapacidadMinima: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }
        return lista;
    }

    private TipoHabitacion mapearResultSet(ResultSet rs) throws SQLException {
        TipoHabitacion t = new TipoHabitacion();

        t.setIdTipoHabitacion(rs.getInt("id_tipo_habitacion"));
        
        String nombreStr = rs.getString("nombre_tipo");
        if (nombreStr != null) {
            try {
                t.setNombreTipo(NombreTipoHabitacion.fromString(nombreStr));
            } catch (IllegalArgumentException e) {
                System.err.println("Error convirtiendo nombre tipo habitación: " + nombreStr);
                t.setNombreTipo(NombreTipoHabitacion.BASICA);
            }
        }

        t.setDescripcion(rs.getString("descripcion"));
        t.setCapacidadMaxima(rs.getInt("capacidad_maxima"));

        // timestamps de ClaseBase
        Timestamp created = rs.getTimestamp("created_at");
        Timestamp updated = rs.getTimestamp("updated_at");
        Timestamp deleted = rs.getTimestamp("deleted_at");

        t.setCreatedAt(created != null ? created.toLocalDateTime() : null);
        t.setUpdatedAt(updated != null ? updated.toLocalDateTime() : null);
        t.setDeletedAt(deleted != null ? deleted.toLocalDateTime() : null);

        return t;
    }
    
    public boolean estaEnUso(int idTipoHabitacion) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConectarBD.conectar();
            String sql = "SELECT COUNT(*) as total FROM habitaciones " +
                         "WHERE id_tipo_habitacion = ? AND deleted_at IS NULL";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, idTipoHabitacion);
            rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("total") > 0;
            }

        } catch (SQLException e) {
            MessageUtil.error("Error al verificar uso del tipo de habitación: " + e.getMessage());
            System.err.println("Error en TipoHabitacionDAO.estaEnUso: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }
        return false;
    }
}