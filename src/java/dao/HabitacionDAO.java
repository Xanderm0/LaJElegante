package dao;

import models.Habitacion;
import models.TipoHabitacion;
import models.enums.EstadoHabitacion;
import utils.MessageUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.enums.NombreTipoHabitacion;
import java.util.Date;

public class HabitacionDAO extends BaseDAO<Habitacion> {

    private final TipoHabitacionDAO tipoHabitacionDAO = new TipoHabitacionDAO();

    @Override
    public void crear(Habitacion h) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConectarBD.conectar();
            String sql =
                "INSERT INTO habitaciones " +
                "(id_tipo_habitacion, numero_habitacion, estado_habitacion, created_at, updated_at) " +
                "VALUES (?, ?, ?, NOW(), NOW())";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, h.getTipoHabitacion().getIdTipoHabitacion());
            ps.setInt(2, h.getNumeroHabitacion());
            ps.setString(3, h.getEstadoHabitacion().name().toLowerCase());

            ps.executeUpdate();
            MessageUtil.createSuccess("habitación");
        } catch (SQLException e) {
            MessageUtil.createError("habitación");
            System.err.println("Error en HabitacionDAO.crear: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps);
        }
    }

    @Override
    public Habitacion buscar(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Habitacion h = null;

        try {
            conn = ConectarBD.conectar();
        String sql = "SELECT h.*, th.nombre_tipo, th.descripcion, th.capacidad_maxima " +
                     "FROM habitaciones h " +
                     "LEFT JOIN tipo_habitacion th ON h.id_tipo_habitacion = th.id_tipo_habitacion " +
                     "WHERE h.id_habitacion = ? AND h.deleted_at IS NULL";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);

            rs = ps.executeQuery();

            if (rs.next()) {
                h = mapearResultSet(rs);
            }

        } catch (SQLException e) {
            MessageUtil.error("Error al buscar habitación: " + e.getMessage());
            System.err.println("Error en HabitacionDAO.buscar: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }

        return h;
    }

    @Override
    public List<Habitacion> listar() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Habitacion> habitaciones = new ArrayList<>();

        try {
            conn = ConectarBD.conectar();
                    String sql = "SELECT h.*, th.nombre_tipo, th.descripcion, th.capacidad_maxima " +
                     "FROM habitaciones h " +
                     "LEFT JOIN tipo_habitacion th ON h.id_tipo_habitacion = th.id_tipo_habitacion " +
                     "WHERE h.deleted_at IS NULL " +
                     "ORDER BY h.numero_habitacion";

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                habitaciones.add(mapearResultSet(rs));
            }

        } catch (SQLException e) {
            MessageUtil.error("Error al listar habitaciones: " + e.getMessage());
            System.err.println("Error en HabitacionDAO.listar: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }

        return habitaciones;
    }
//contar habitaciones
 public int contarHabitaciones() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConectarBD.conectar();
            if (conn == null) return 0;

            String sql = "SELECT COUNT(*) FROM habitaciones";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConectarBD.cerrarConexion(conn, ps, rs);
        }
        return 0;
    }
 //contar habitaciones ocupadas 
 public int contarHabitacionesOcupadas() {
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
        conn = ConectarBD.conectar();
        if (conn == null) return 0;

        String sql =
            "SELECT COUNT(*) FROM habitaciones " +
            "WHERE estado = 'OCUPADA' AND deleted_at IS NULL";

        ps = conn.prepareStatement(sql);
        rs = ps.executeQuery();

        if (rs.next()) {
            return rs.getInt(1);
        }

    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        ConectarBD.cerrarConexion(conn, ps, rs);
    }

    return 0;
}

    @Override
    public void actualizar(Habitacion h) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConectarBD.conectar();
            String sql =
                "UPDATE habitaciones SET " +
                "id_tipo_habitacion = ?, numero_habitacion = ?, estado_habitacion = ?, " +
                "updated_at = NOW() " +
                "WHERE id_habitacion = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, h.getTipoHabitacion().getIdTipoHabitacion());
            ps.setInt(2, h.getNumeroHabitacion());
            ps.setString(3, h.getEstadoHabitacion().name().toLowerCase());
            ps.setInt(4, h.getIdHabitacion());

            ps.executeUpdate();
            MessageUtil.updateSuccess("habitación");
        } catch (SQLException e) {
            MessageUtil.updateError("habitación");
            System.err.println("Error en HabitacionDAO.actualizar: " + e.getMessage());
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
            String sql = "UPDATE habitaciones SET deleted_at = NOW() WHERE id_habitacion = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);

            ps.executeUpdate();
            MessageUtil.deleteSuccess("habitación");
        } catch (SQLException e) {
            MessageUtil.deleteError("habitación");
            System.err.println("Error en HabitacionDAO.eliminar: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps);
        }
    }
    
    @Override
    public List<Habitacion> listarEliminados() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Habitacion> habitaciones = new ArrayList<>();

        try {
            conn = ConectarBD.conectar();
                    String sql = "SELECT h.*, th.nombre_tipo, th.descripcion, th.capacidad_maxima " +
                     "FROM habitaciones h " +
                     "LEFT JOIN tipo_habitacion th ON h.id_tipo_habitacion = th.id_tipo_habitacion " +
                     "WHERE h.deleted_at IS NOT NULL " +
                     "ORDER BY h.deleted_at DESC";

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                habitaciones.add(mapearResultSet(rs));
            }

        } catch (SQLException e) {
            MessageUtil.error("Error al listar habitaciones eliminadas: " + e.getMessage());
            System.err.println("Error en HabitacionDAO.listarEliminados: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }
        return habitaciones;
    }
    
    @Override
    public void restaurar(int id) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConectarBD.conectar();
            String sql = "UPDATE habitaciones SET deleted_at = NULL, updated_at = NOW() WHERE id_habitacion = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
            MessageUtil.success("Habitación restaurada correctamente");
        } catch (SQLException e) {
            MessageUtil.error("Error al restaurar habitación: " + e.getMessage());
            System.err.println("Error en HabitacionDAO.restaurar: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps);
        }
    }
    
    @Override
    public Habitacion buscarConTrash(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Habitacion h = null;

        try {
            conn = ConectarBD.conectar();
            
            String sql = "SELECT h.*, th.nombre_tipo, th.descripcion, th.capacidad_maxima " +
                    "FROM habitaciones h " +
                    "LEFT JOIN tipo_habitacion th ON h.id_tipo_habitacion = th.id_tipo_habitacion " +
                    "WHERE h.id_habitacion = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                h = mapearResultSet(rs);
            }

        } catch (SQLException e) {
            MessageUtil.error("Error al buscar habitación (con papelera): " + e.getMessage());
            System.err.println("Error en HabitacionDAO.buscarConTrash: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }
        return h;
    }
    
    public List<Habitacion> obtenerDisponibles(Date fechaInicio, Date fechaFin, int personas) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Habitacion> habitaciones = new ArrayList<>();

        try {
            conn = ConectarBD.conectar();
            String sql = 
                "SELECT h.*, th.nombre_tipo, th.descripcion, th.capacidad_maxima " +
                "FROM habitaciones h " +
                "JOIN tipo_habitacion th ON h.id_tipo_habitacion = th.id_tipo_habitacion " +
                "WHERE h.estado_habitacion = 'disponible' " +
                "AND h.deleted_at IS NULL " +
                "AND th.capacidad_maxima >= ? " +
                "AND NOT EXISTS ( " +
                "    SELECT 1 FROM reservas_habitaciones rh " +
                "    WHERE rh.id_habitacion = h.id_habitacion " +
                "    AND rh.estado_reserva != 'cancelada' " +
                "    AND rh.deleted_at IS NULL " +
                "    AND ( " +
                "        (rh.fecha_inicio <= ? AND rh.fecha_fin >= ?) " + // Superposición total
                "        OR (rh.fecha_inicio BETWEEN ? AND ?) " + // Inicio dentro del rango
                "        OR (rh.fecha_fin BETWEEN ? AND ?) " + // Fin dentro del rango
                "    ) " +
                ") " +
                "ORDER BY th.capacidad_maxima, h.numero_habitacion";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, personas);

            java.sql.Date sqlFechaInicio = new java.sql.Date(fechaInicio.getTime());
            java.sql.Date sqlFechaFin = new java.sql.Date(fechaFin.getTime());

            ps.setDate(2, sqlFechaFin);   // fechaFin para <= ?
            ps.setDate(3, sqlFechaInicio); // fechaInicio para >= ?
            ps.setDate(4, sqlFechaInicio); // fechaInicio para BETWEEN
            ps.setDate(5, sqlFechaFin);    // fechaFin para BETWEEN
            ps.setDate(6, sqlFechaInicio); // fechaInicio para BETWEEN (fin)
            ps.setDate(7, sqlFechaFin);    // fechaFin para BETWEEN (fin)

            rs = ps.executeQuery();

            while (rs.next()) {
                habitaciones.add(mapearResultSet(rs));
            }

        } catch (SQLException e) {
            MessageUtil.error("Error al buscar habitaciones disponibles: " + e.getMessage());
            System.err.println("Error en HabitacionDAO.obtenerDisponibles: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }

        return habitaciones;
    }
    
    public List<Habitacion> obtenerPorPiso(int piso) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Habitacion> habitaciones = new ArrayList<>();

        try {
            conn = ConectarBD.conectar();
            // La separacion de pisos (100-199 = piso 1 / 200-299 = piso 2 )
            String sql = "SELECT h.*, th.nombre_tipo, th.descripcion, th.capacidad_maxima " +
                         "FROM habitaciones h " +
                         "LEFT JOIN tipo_habitacion th ON h.id_tipo_habitacion = th.id_tipo_habitacion " +
                         "WHERE FLOOR(h.numero_habitacion / 100) = ? " +
                         "AND h.deleted_at IS NULL " +
                         "ORDER BY h.numero_habitacion";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, piso);
            rs = ps.executeQuery();

            while (rs.next()) {
                habitaciones.add(mapearResultSet(rs));
            }

        } catch (SQLException e) {
            MessageUtil.error("Error al buscar habitaciones por piso: " + e.getMessage());
            System.err.println("Error en HabitacionDAO.obtenerPorPiso: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }

        return habitaciones;
    }
    
    private Habitacion mapearResultSet(ResultSet rs) throws SQLException {
        Habitacion h = new Habitacion();

        h.setIdHabitacion(rs.getInt("id_habitacion"));
        h.setNumeroHabitacion(rs.getInt("numero_habitacion"));
        String estadoStr = rs.getString("estado_habitacion");
        
        if (estadoStr != null) {
            h.setEstadoHabitacion(EstadoHabitacion.valueOf(estadoStr.toUpperCase()));
        }

        if (existeColumna(rs, "id_tipo_habitacion")) {
            TipoHabitacion tipo = new TipoHabitacion();
            tipo.setIdTipoHabitacion(rs.getInt("id_tipo_habitacion"));

            if (existeColumna(rs, "nombre_tipo")) {
                String nombreTipoStr = rs.getString("nombre_tipo");
                if (nombreTipoStr != null) {
                    try {
                        tipo.setNombreTipo(NombreTipoHabitacion.fromString(nombreTipoStr));
                    } catch (IllegalArgumentException e) {
                        System.err.println("Error convirtiendo nombre tipo habitación: " + nombreTipoStr);
                        tipo.setNombreTipo(NombreTipoHabitacion.BASICA);
                    }
                }
            }

            if (existeColumna(rs, "descripcion")) {
                tipo.setDescripcion(rs.getString("descripcion"));
            }
            if (existeColumna(rs, "capacidad_maxima")) {
                tipo.setCapacidadMaxima(rs.getInt("capacidad_maxima"));
            }

            h.setTipoHabitacion(tipo);
        }

        Timestamp created = rs.getTimestamp("created_at");
        Timestamp updated = rs.getTimestamp("updated_at");
        Timestamp deleted = rs.getTimestamp("deleted_at");

        h.setCreatedAt(created != null ? created.toLocalDateTime() : null);
        h.setUpdatedAt(updated != null ? updated.toLocalDateTime() : null);
        h.setDeletedAt(deleted != null ? deleted.toLocalDateTime() : null);

        return h;
    }
    
    public boolean estaDisponible(int idHabitacion, Date fechaInicio, Date fechaFin, int personas) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConectarBD.conectar();
            String sql = 
                "SELECT COUNT(*) as disponible " +
                "FROM habitaciones h " +
                "JOIN tipo_habitacion th ON h.id_tipo_habitacion = th.id_tipo_habitacion " +
                "WHERE h.id_habitacion = ? " +
                "AND h.estado_habitacion = 'disponible' " +
                "AND h.deleted_at IS NULL " +
                "AND th.capacidad_maxima >= ? " +
                "AND NOT EXISTS ( " +
                "    SELECT 1 FROM reservas_habitaciones rh " +
                "    WHERE rh.id_habitacion = h.id_habitacion " +
                "    AND rh.estado_reserva != 'cancelada' " +
                "    AND rh.deleted_at IS NULL " +
                "    AND (rh.fecha_inicio <= ? AND rh.fecha_fin >= ?) " +
                ")";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, idHabitacion);
            ps.setInt(2, personas);
            ps.setDate(3, new java.sql.Date(fechaFin.getTime()));
            ps.setDate(4, new java.sql.Date(fechaInicio.getTime()));

            rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("disponible") > 0;
            }

        } catch (SQLException e) {
            MessageUtil.error("Error al verificar disponibilidad: " + e.getMessage());
            System.err.println("Error en HabitacionDAO.estaDisponible: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }
        return false;
    }

    private boolean existeColumna(ResultSet rs, String columna) {
        try {
            rs.findColumn(columna);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}