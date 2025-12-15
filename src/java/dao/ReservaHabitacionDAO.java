package dao;

import models.ReservaHabitacion;
import models.Cliente;
import models.DetallesReservaHabitacion;
import models.Habitacion;
import utils.MessageUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List; 
import java.util.Date;

public class ReservaHabitacionDAO extends BaseDAO<ReservaHabitacion> {

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final DetallesReservaHabitacionDAO detalleDAO = new DetallesReservaHabitacionDAO();

    @Override
    public void crear(ReservaHabitacion r) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConectarBD.conectar();
            String sql =
                "INSERT INTO reserva_habitacion " +
                "(id_cliente, id_detalle_reserva_hab, created_at, updated_at) " +
                "VALUES (?, ?, NOW(), NOW())";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, r.getCliente().getIdCliente());
            ps.setInt(2, r.getDetalleReservaHabitacion().getIdDetalleReservaHab());

            ps.executeUpdate();
            MessageUtil.createSuccess("reserva de habitación");
        } catch (SQLException e) {
            MessageUtil.createError("reserva de habitación");
            System.err.println("Error en ReservaHabitacionDAO.crear: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps);
        }
    }

    @Override
    public ReservaHabitacion buscar(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ReservaHabitacion r = null;

        try {
            conn = ConectarBD.conectar();
            String sql =
                "SELECT * FROM reserva_habitacion " +
                "WHERE id_reserva_habitacion = ? AND deleted_at IS NULL";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                r = mapearResultSet(rs);
            }

        } catch (SQLException e) {
            MessageUtil.error("Error al buscar reserva de habitación: " + e.getMessage());
            System.err.println("Error en ReservaHabitacionDAO.buscar: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }

        return r;
    }

    @Override
    public List<ReservaHabitacion> listar() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<ReservaHabitacion> lista = new ArrayList<>();

        try {
            conn = ConectarBD.conectar();
            String sql =
                "SELECT * FROM reserva_habitacion " +
                "WHERE deleted_at IS NULL " +
                "ORDER BY id_reserva_habitacion DESC";

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapearResultSet(rs));
            }

        } catch (SQLException e) {
            MessageUtil.error("Error al listar reservas de habitación: " + e.getMessage());
            System.err.println("Error en ReservaHabitacionDAO.listar: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }

        return lista;
    }
    //contar reservas 
 public int contarReservasActivas() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConectarBD.conectar();
            if (conn == null) return 0;

            String sql = "SELECT COUNT(*) FROM reserva_habitacion WHERE estado = 'ACTIVA'";
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
 //reservas por mes 
 public List<Object[]> reservasPorMes() {
    List<Object[]> lista = new ArrayList<>();
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
        conn = ConectarBD.conectar();
        if (conn == null) return lista;

        String sql =
            "SELECT MONTH(fecha_reserva) AS mes, COUNT(*) AS total " +
            "FROM reservas_habitacion " +
            "WHERE deleted_at IS NULL " +
            "GROUP BY MONTH(fecha_reserva) " +
            "ORDER BY mes";

        ps = conn.prepareStatement(sql);
        rs = ps.executeQuery();

        while (rs.next()) {
            lista.add(new Object[]{
                rs.getInt("mes"),
                rs.getInt("total")
            });
        }

    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        ConectarBD.cerrarConexion(conn, ps, rs);
    }

    return lista;
}

    @Override
    public void actualizar(ReservaHabitacion r) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConectarBD.conectar();
            String sql =
                "UPDATE reserva_habitacion SET " +
                "id_cliente = ?, id_detalle_reserva_hab = ?, " +
                "updated_at = NOW() " +
                "WHERE id_reserva_habitacion = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, r.getCliente().getIdCliente());
            ps.setInt(2, r.getDetalleReservaHabitacion().getIdDetalleReservaHab());
            ps.setInt(3, r.getIdReservaHabitacion());

            ps.executeUpdate();
            MessageUtil.updateSuccess("reserva de habitación");
        } catch (SQLException e) {
            MessageUtil.updateError("reserva de habitación");
            System.err.println("Error en ReservaHabitacionDAO.actualizar: " + e.getMessage());
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
                "UPDATE reserva_habitacion SET deleted_at = NOW() " +
                "WHERE id_reserva_habitacion = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);

            ps.executeUpdate();
            MessageUtil.deleteSuccess("reserva de habitación");
        } catch (SQLException e) {
            MessageUtil.deleteError("reserva de habitación");
            System.err.println("Error en ReservaHabitacionDAO.eliminar: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps);
        }
    }
    
    @Override
    public List<ReservaHabitacion> listarEliminados() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<ReservaHabitacion> lista = new ArrayList<>();

        try {
            conn = ConectarBD.conectar();
            String sql = "SELECT * FROM reserva_habitacion " +
                         "WHERE deleted_at IS NOT NULL " +
                         "ORDER BY deleted_at DESC";

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapearResultSet(rs));
            }

        } catch (SQLException e) {
            MessageUtil.error("Error al listar reservas eliminadas: " + e.getMessage());
            System.err.println("Error en ReservaHabitacionDAO.listarEliminados: " + e.getMessage());
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
            String sql = "UPDATE reserva_habitacion SET deleted_at = NULL, updated_at = NOW() " +
                         "WHERE id_reserva_habitacion = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
            MessageUtil.success("Reserva de habitación restaurada correctamente");
        } catch (SQLException e) {
            MessageUtil.error("Error al restaurar reserva: " + e.getMessage());
            System.err.println("Error en ReservaHabitacionDAO.restaurar: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps);
        }
    }

    @Override
    public ReservaHabitacion buscarConTrash(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ReservaHabitacion r = null;

        try {
            conn = ConectarBD.conectar();
            String sql = "SELECT * FROM reserva_habitacion WHERE id_reserva_habitacion = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                r = mapearResultSet(rs);
            }

        } catch (SQLException e) {
            MessageUtil.error("Error al buscar reserva (con papelera): " + e.getMessage());
            System.err.println("Error en ReservaHabitacionDAO.buscarConTrash: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }
        return r;
    }
    
    public int crearReservaInvitado(int idCliente, int idHabitacion, Date fechaInicio, Date fechaFin, int personas, String observacion) {
        Connection conn = null;
        PreparedStatement psDetalle = null;
        PreparedStatement psReserva = null;
        ResultSet rs = null;

        try {
            conn = ConectarBD.conectar();
            conn.setAutoCommit(false); // Iniciar transacción

            // 1. Calcular número de noches
            long diff = fechaFin.getTime() - fechaInicio.getTime();
            int noches = (int) (diff / (1000 * 60 * 60 * 24));

            if (noches <= 0) {
                MessageUtil.error("La fecha de fin debe ser posterior a la fecha de inicio");
                return -1;
            }

            // 2. Obtener precio de la habitación
            HabitacionDAO habitacionDAO = new HabitacionDAO();
            Habitacion hab = habitacionDAO.buscar(idHabitacion);
            double precioNoche = 0.0;

            if (hab != null && hab.getTipoHabitacion() != null) {
                // Aquí necesitarías agregar precio_noche a TipoHabitacion
                // Por ahora usaremos un valor fijo
                precioNoche = 100.0; // Cambiar por precio real
            }

            double precioTotal = precioNoche * noches;

            // 3. Insertar detalles
            String sqlDetalle = 
                "INSERT INTO detalles_reserva_habitacion " +
                "(id_habitacion, cantidad_personas, cantidad_noches, precio_noche, " +
                "precio_reserva, observacion, fecha_inicio, fecha_fin, " +
                "created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";

            psDetalle = conn.prepareStatement(sqlDetalle, PreparedStatement.RETURN_GENERATED_KEYS);
            psDetalle.setInt(1, idHabitacion);
            psDetalle.setInt(2, personas);
            psDetalle.setInt(3, noches);
            psDetalle.setDouble(4, precioNoche);
            psDetalle.setDouble(5, precioTotal);
            psDetalle.setString(6, observacion);
            psDetalle.setDate(7, new java.sql.Date(fechaInicio.getTime()));
            psDetalle.setDate(8, new java.sql.Date(fechaFin.getTime()));

            psDetalle.executeUpdate();

            // 4. Obtener ID del detalle insertado
            rs = psDetalle.getGeneratedKeys();
            int idDetalle = -1;
            if (rs.next()) {
                idDetalle = rs.getInt(1);
            }

            if (idDetalle == -1) {
                conn.rollback();
                return -1;
            }

            // 5. Insertar reserva (cabecera)
            String sqlReserva = 
                "INSERT INTO reserva_habitacion " +
                "(id_cliente, id_detalle_reserva_hab, created_at, updated_at) " +
                "VALUES (?, ?, NOW(), NOW())";

            psReserva = conn.prepareStatement(sqlReserva, PreparedStatement.RETURN_GENERATED_KEYS);
            psReserva.setInt(1, idCliente);
            psReserva.setInt(2, idDetalle);

            psReserva.executeUpdate();

            // 6. Obtener ID de la reserva
            rs = psReserva.getGeneratedKeys();
            int idReserva = -1;
            if (rs.next()) {
                idReserva = rs.getInt(1);
            }

            conn.commit(); // Confirmar transacción
            MessageUtil.createSuccess("reserva de habitación para invitado");
            return idReserva;

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                System.err.println("Error en rollback: " + ex.getMessage());
            }

            MessageUtil.createError("reserva de habitación");
            System.err.println("Error en ReservaHabitacionDAO.crearReservaInvitado: " + e.getMessage());
            return -1;

        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Error restaurando auto-commit: " + e.getMessage());
            }
            cerrarRecursos(conn, psDetalle);
            cerrarRecursos(null, psReserva);
            cerrarRecursos(null, null, rs);
        }
    }

    private ReservaHabitacion mapearResultSet(ResultSet rs) throws SQLException {
        ReservaHabitacion r = new ReservaHabitacion();
        r.setIdReservaHabitacion(rs.getInt("id_reserva_habitacion"));

        if (existeColumna(rs, "id_cliente")) {
            Cliente cli = new Cliente();
            cli.setIdCliente(rs.getInt("id_cliente"));

            if (existeColumna(rs, "cliente_nombre")) {
                cli.setNombre(rs.getString("cliente_nombre"));
            }
            if (existeColumna(rs, "cliente_email")) {
                cli.setEmail(rs.getString("cliente_email"));
            }

            r.setCliente(cli);
        }

        if (existeColumna(rs, "id_detalle_reserva_hab")) {
            DetallesReservaHabitacion det = new DetallesReservaHabitacion();
            det.setIdDetalleReservaHab(rs.getInt("id_detalle_reserva_hab"));

            if (existeColumna(rs, "id_habitacion")) {
                // Aquí podrías crear una Habitacion básica
                // O usar JOINs anidados
            }

            r.setDetalleReservaHabitacion(det);
        }

        // Timestamps
        Timestamp created = rs.getTimestamp("created_at");
        Timestamp updated = rs.getTimestamp("updated_at");
        Timestamp deleted = rs.getTimestamp("deleted_at");

        r.setCreatedAt(created != null ? created.toLocalDateTime() : null);
        r.setUpdatedAt(updated != null ? updated.toLocalDateTime() : null);
        r.setDeletedAt(deleted != null ? deleted.toLocalDateTime() : null);

        return r;
    }
    
    
    public boolean existeConflictoReserva(int idHabitacion, Date fechaInicio, Date fechaFin) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConectarBD.conectar();
            String sql = 
                "SELECT COUNT(*) as conflictos " +
                "FROM reserva_habitacion rh " +
                "JOIN detalles_reserva_habitacion drh ON rh.id_detalle_reserva_hab = drh.id_detalle_reserva_hab " +
                "WHERE drh.id_habitacion = ? " +
                "AND rh.deleted_at IS NULL " +
                "AND drh.deleted_at IS NULL " +
                "AND ( " +
                "    (drh.fecha_inicio <= ? AND drh.fecha_fin >= ?) " + // Superposición total
                "    OR (drh.fecha_inicio BETWEEN ? AND ?) " + // Inicio dentro del rango
                "    OR (drh.fecha_fin BETWEEN ? AND ?) " + // Fin dentro del rango
                ")";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, idHabitacion);

            java.sql.Date sqlFechaInicio = new java.sql.Date(fechaInicio.getTime());
            java.sql.Date sqlFechaFin = new java.sql.Date(fechaFin.getTime());

            ps.setDate(2, sqlFechaFin);
            ps.setDate(3, sqlFechaInicio);
            ps.setDate(4, sqlFechaInicio);
            ps.setDate(5, sqlFechaFin);
            ps.setDate(6, sqlFechaInicio);
            ps.setDate(7, sqlFechaFin);

            rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("conflictos") > 0;
            }

        } catch (SQLException e) {
            MessageUtil.error("Error al verificar conflictos de reserva: " + e.getMessage());
            System.err.println("Error en ReservaHabitacionDAO.existeConflictoReserva: " + e.getMessage());
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