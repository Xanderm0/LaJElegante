package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import models.DetallesReservaHabitacion;
import models.Habitacion;
import java.util.ArrayList;
import java.util.List;
import models.TipoHabitacion;
import models.enums.EstadoHabitacion;
import models.enums.NombreTipoHabitacion;

public class DetallesReservaHabitacionDAO extends BaseDAO<DetallesReservaHabitacion> {

    @Override
    public void crear(DetallesReservaHabitacion d) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConectarBD.conectar();
            
            String sql = "INSERT INTO detalles_reserva_habitacion ("
                    + "id_habitacion, cantidad_personas, cantidad_noches, precio_noche, "
                    + "descuento_aplicado, recargo_aplicado, precio_reserva, observacion, "
                    + "fecha_inicio, fecha_fin, created_at, updated_at"
                    + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";

            ps = conn.prepareStatement(sql);

            ps.setInt(1, d.getHabitacion().getIdHabitacion());
            ps.setInt(2, d.getCantidadPersonas());
            ps.setInt(3, d.getCantidadNoches());
            ps.setDouble(4, d.getPrecioNoche());
            ps.setDouble(5, d.getDescuentoAplicado());
            ps.setDouble(6, d.getRecargoAplicado());
            ps.setDouble(7, d.getPrecioReserva());
            ps.setString(8, d.getObservacion());

            ps.setDate(9, new java.sql.Date(d.getFechaInicio().getTime()));
            ps.setDate(10, new java.sql.Date(d.getFechaFin().getTime()));

            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error en DetallesReservaHabitacionDAO.crear: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps);
        }
    }

    @Override
    public DetallesReservaHabitacion buscar(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        DetallesReservaHabitacion d = null;

        try {
            conn = ConectarBD.conectar();
            
        String sql = "SELECT drh.*, "
                   + "h.numero_habitacion, "
                   + "h.id_tipo_habitacion, "
                   + "h.estado AS estado_habitacion, "
                   + "th.nombre AS nombre_tipo_habitacion "
                   + "FROM detalles_reserva_habitacion drh "
                   + "LEFT JOIN habitaciones h ON drh.id_habitacion = h.id_habitacion "
                   + "LEFT JOIN tipo_habitacion th ON h.id_tipo_habitacion = th.id_tipo_habitacion "
                   + "WHERE drh.id_detalle_reserva_hab = ? AND drh.deleted_at IS NULL";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                d = mapearResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error en DetallesReservaHabitacionDAO.buscar: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }

        return d;
    }

    @Override
    public List<DetallesReservaHabitacion> listar() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<DetallesReservaHabitacion> lista = new ArrayList<>();

        try {
            conn = ConectarBD.conectar();
            
        String sql = "SELECT drh.*, "
                   + "h.numero_habitacion, "
                   + "h.id_tipo_habitacion, "
                   + "h.estado AS estado_habitacion, "
                   + "th.nombre AS nombre_tipo_habitacion "
                   + "FROM detalles_reserva_habitacion drh "
                   + "LEFT JOIN habitaciones h ON drh.id_habitacion = h.id_habitacion "
                   + "LEFT JOIN tipo_habitacion th ON h.id_tipo_habitacion = th.id_tipo_habitacion "
                   + "WHERE drh.deleted_at IS NULL "
                   + "ORDER BY drh.fecha_inicio DESC";

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapearResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error en DetallesReservaHabitacionDAO.listar: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }

        return lista;
    }

    @Override
    public void actualizar(DetallesReservaHabitacion d) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConectarBD.conectar();

            String sql = "UPDATE detalles_reserva_habitacion SET "
                       + "id_habitacion = ?, cantidad_personas = ?, cantidad_noches = ?, "
                       + "precio_noche = ?, descuento_aplicado = ?, recargo_aplicado = ?, "
                       + "precio_reserva = ?, observacion = ?, fecha_inicio = ?, fecha_fin = ?, "
                       + "updated_at = NOW() "
                       + "WHERE id_detalle_reserva_hab = ? AND deleted_at IS NULL";

            ps = conn.prepareStatement(sql);

            ps.setInt(1, d.getHabitacion().getIdHabitacion());
            ps.setInt(2, d.getCantidadPersonas());
            ps.setInt(3, d.getCantidadNoches());
            ps.setDouble(4, d.getPrecioNoche());
            ps.setDouble(5, d.getDescuentoAplicado());
            ps.setDouble(6, d.getRecargoAplicado());
            ps.setDouble(7, d.getPrecioReserva());
            ps.setString(8, d.getObservacion());

            ps.setDate(9, new java.sql.Date(d.getFechaInicio().getTime()));
            ps.setDate(10, new java.sql.Date(d.getFechaFin().getTime()));

            ps.setInt(11, d.getIdDetalleReservaHab());

            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error en DetallesReservaHabitacionDAO.actualizar: " + e.getMessage());
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
            String sql = "UPDATE detalles_reserva_habitacion "
                       + "SET deleted_at = NOW() "
                       + "WHERE id_detalle_reserva_hab = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);

            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error en DetallesReservaHabitacionDAO.eliminar: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps);
        }
    }

    @Override
    public List<DetallesReservaHabitacion> listarEliminados() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<DetallesReservaHabitacion> lista = new ArrayList<>();
        
        try {
            conn = ConectarBD.conectar();
            
            String sql = "SELECT drh.*, "
               + "h.numero_habitacion, h.id_tipo_habitacion, h.estado AS estado_habitacion, "
               + "th.nombre AS nombre_tipo_habitacion "
               + "FROM detalles_reserva_habitacion drh "
               + "LEFT JOIN habitaciones h ON drh.id_habitacion = h.id_habitacion "
               + "LEFT JOIN tipo_habitacion th ON h.id_tipo_habitacion = th.id_tipo_habitacion "
               + "WHERE drh.deleted_at IS NOT NULL ORDER BY drh.deleted_at DESC";
            
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                lista.add(mapearResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error en DetallesReservaHabitacionDAO.listarEliminados: " + e.getMessage());
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
            String sql = "UPDATE detalles_reserva_habitacion SET deleted_at = NULL, updated_at = NOW() "
                       + "WHERE id_detalle_reserva_hab = ?";
            
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Error en DetallesReservaHabitacionDAO.restaurar: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps);
        }
    }
    
    @Override
    public DetallesReservaHabitacion buscarConTrash(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        DetallesReservaHabitacion d = null;
        
        try {
            conn = ConectarBD.conectar();
            
            String sql = "SELECT drh.*, "
                       + "h.numero_habitacion, h.id_tipo_habitacion, h.estado AS estado_habitacion, "
                       + "th.nombre AS nombre_tipo_habitacion "
                       + "FROM detalles_reserva_habitacion drh "
                       + "LEFT JOIN habitaciones h ON drh.id_habitacion = h.id_habitacion "
                       + "LEFT JOIN tipo_habitacion th ON h.id_tipo_habitacion = th.id_tipo_habitacion "
                       + "WHERE drh.id_detalle_reserva_hab = ?";
            
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                d = mapearResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error en DetallesReservaHabitacionDAO.buscarConTrash: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }
        return d;
    }
    
    public List<DetallesReservaHabitacion> listarPorReserva(int idReserva) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<DetallesReservaHabitacion> lista = new ArrayList<>();
        
        try {
            conn = ConectarBD.conectar();
            String sql = "SELECT drh.*, h.numero_habitacion, h.id_tipo_habitacion, h.estado AS estado_habitacion "
                       + "FROM detalles_reserva_habitacion drh "
                       + "LEFT JOIN habitaciones h ON drh.id_habitacion = h.id_habitacion "
                       + "WHERE drh.id_reserva = ? AND drh.deleted_at IS NULL "
                       + "ORDER BY drh.fecha_inicio";
            
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idReserva);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                lista.add(mapearResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error en DetallesReservaHabitacionDAO.listarPorReserva: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }
        return lista;
    }

    private DetallesReservaHabitacion mapearResultSet(ResultSet rs) throws SQLException {

        DetallesReservaHabitacion d = new DetallesReservaHabitacion();

        d.setIdDetalleReservaHab(rs.getInt("id_detalle_reserva_hab"));

        Habitacion h = new Habitacion();
        h.setIdHabitacion(rs.getInt("id_habitacion"));

        if (existeColumna(rs, "numero_habitacion")) {
            h.setNumeroHabitacion(rs.getInt("numero_habitacion"));
        }

        if (existeColumna(rs, "id_tipo_habitacion")) {
            TipoHabitacion tipo = new TipoHabitacion();
            tipo.setIdTipoHabitacion(rs.getInt("id_tipo_habitacion"));

            if (existeColumna(rs, "nombre_tipo_habitacion")) {
                String nombreStr = rs.getString("nombre_tipo_habitacion");
                if (nombreStr != null) {
                    try {
                        tipo.setNombreTipo(NombreTipoHabitacion.valueOf(nombreStr));
                    } catch (IllegalArgumentException e) {
                        System.err.println("Error convirtiendo nombre tipo habitación: " + nombreStr);
                    }
                }
            }

            h.setTipoHabitacion(tipo);
        }

        if (existeColumna(rs, "estado_habitacion")) {
            String estadoStr = rs.getString("estado_habitacion");
            if (estadoStr != null) {
                try {
                    h.setEstadoHabitacion(EstadoHabitacion.valueOf(estadoStr));
                } catch (IllegalArgumentException e) {
                    System.err.println("Error convirtiendo estado habitación: " + estadoStr);
                    h.setEstadoHabitacion(EstadoHabitacion.EN_SERVICIO);
                }
            }
        }

        d.setHabitacion(h);

        d.setCantidadPersonas(rs.getInt("cantidad_personas"));
        d.setCantidadNoches(rs.getInt("cantidad_noches"));
        d.setPrecioNoche(rs.getDouble("precio_noche"));
        d.setDescuentoAplicado(rs.getDouble("descuento_aplicado"));
        d.setRecargoAplicado(rs.getDouble("recargo_aplicado"));
        d.setPrecioReserva(rs.getDouble("precio_reserva"));
        d.setObservacion(rs.getString("observacion"));
        d.setFechaInicio(rs.getDate("fecha_inicio"));
        d.setFechaFin(rs.getDate("fecha_fin"));

        Timestamp created = rs.getTimestamp("created_at");
        Timestamp updated = rs.getTimestamp("updated_at");
        Timestamp deleted = rs.getTimestamp("deleted_at");

        d.setCreatedAt(created != null ? created.toLocalDateTime() : null);
        d.setUpdatedAt(updated != null ? updated.toLocalDateTime() : null);
        d.setDeletedAt(deleted != null ? deleted.toLocalDateTime() : null);

        return d;
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