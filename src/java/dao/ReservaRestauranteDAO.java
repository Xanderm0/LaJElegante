package dao;

import models.ReservaRestaurante;
import models.Cliente;
import models.Mesa;
import models.enums.Estado;
import models.enums.EstadoReserva;
import utils.MessageUtil;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ReservaRestauranteDAO extends BaseDAO<ReservaRestaurante> {

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final MesaDAO mesaDAO = new MesaDAO();

    @Override
    public void crear(ReservaRestaurante r) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConectarBD.conectar();
            String sql = "INSERT INTO reservas_restaurante "
                       + "(id_cliente, id_mesa, fecha_reserva, hora_reserva, "
                       + "numero_personas, estado_reserva, estado, created_at, updated_at) "
                       + "VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, r.getCliente().getIdCliente());
            ps.setInt(2, r.getMesa().getIdMesa());
            ps.setDate(3, Date.valueOf(r.getFechaReserva()));
            ps.setTime(4, Time.valueOf(r.getHoraReserva()));
            ps.setInt(5, r.getNumeroPersonas());
            ps.setString(6, r.getEstadoReserva().getValor());
            ps.setString(7, r.getEstado().getValor());

            ps.executeUpdate();
            MessageUtil.createSuccess("reserva de restaurante");
        } catch (SQLException e) {
            MessageUtil.createError("reserva de restaurante");
            System.err.println("Error en ReservaRestauranteDAO.crear: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps);
        }
    }

    @Override
    public ReservaRestaurante buscar(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ReservaRestaurante r = null;

        try {
            conn = ConectarBD.conectar();
            String sql = "SELECT * FROM reservas_restaurante WHERE id_reserva = ? AND deleted_at IS NULL";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                r = mapRow(rs);
            }
        } catch (SQLException e) {
            MessageUtil.error("Error al buscar reserva: " + e.getMessage());
            System.err.println("Error en ReservaRestauranteDAO.buscar: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }

        return r;
    }

    @Override
    public List<ReservaRestaurante> listar() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<ReservaRestaurante> lista = new ArrayList<>();

        try {
            conn = ConectarBD.conectar();
            String sql = "SELECT * FROM reservas_restaurante WHERE deleted_at IS NULL ORDER BY fecha_reserva DESC, hora_reserva DESC";

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        } catch (SQLException e) {
            MessageUtil.error("Error al listar reservas: " + e.getMessage());
            System.err.println("Error en ReservaRestauranteDAO.listar: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }

        return lista;
    }

    @Override
    public void actualizar(ReservaRestaurante r) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConectarBD.conectar();
            String sql = "UPDATE reservas_restaurante SET "
                       + "id_cliente = ?, id_mesa = ?, fecha_reserva = ?, hora_reserva = ?, "
                       + "numero_personas = ?, estado_reserva = ?, estado = ?, updated_at = NOW() "
                       + "WHERE id_reserva = ? AND deleted_at IS NULL";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, r.getCliente().getIdCliente());
            ps.setInt(2, r.getMesa().getIdMesa());
            ps.setDate(3, Date.valueOf(r.getFechaReserva()));
            ps.setTime(4, Time.valueOf(r.getHoraReserva()));
            ps.setInt(5, r.getNumeroPersonas());
            ps.setString(6, r.getEstadoReserva().getValor());
            ps.setString(7, r.getEstado().getValor());
            ps.setInt(8, r.getIdReserva());

            ps.executeUpdate();
            MessageUtil.updateSuccess("reserva de restaurante");
        } catch (SQLException e) {
            MessageUtil.updateError("reserva de restaurante");
            System.err.println("Error en ReservaRestauranteDAO.actualizar: " + e.getMessage());
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
            String sql = "UPDATE reservas_restaurante SET deleted_at = NOW() WHERE id_reserva = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);

            ps.executeUpdate();
            MessageUtil.deleteSuccess("reserva de restaurante");
        } catch (SQLException e) {
            MessageUtil.deleteError("reserva de restaurante");
            System.err.println("Error en ReservaRestauranteDAO.eliminar: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps);
        }
    }

    @Override
    public List<ReservaRestaurante> listarEliminados() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<ReservaRestaurante> lista = new ArrayList<>();

        try {
            conn = ConectarBD.conectar();
            String sql = "SELECT * FROM reservas_restaurante WHERE deleted_at IS NOT NULL ORDER BY deleted_at DESC";

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        } catch (SQLException e) {
            MessageUtil.error("Error al listar reservas eliminadas: " + e.getMessage());
            System.err.println("Error en ReservaRestauranteDAO.listarEliminados: " + e.getMessage());
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
            String sql = "UPDATE reservas_restaurante SET deleted_at = NULL, estado = ?, updated_at = NOW() WHERE id_reserva = ?";

            ps = conn.prepareStatement(sql);
            ps.setString(1, Estado.ACTIVO.getValor());
            ps.setInt(2, id);
            
            ps.executeUpdate();
            MessageUtil.success("Reserva de restaurante restaurada correctamente");
        } catch (SQLException e) {
            MessageUtil.error("Error al restaurar reserva: " + e.getMessage());
            System.err.println("Error en ReservaRestauranteDAO.restaurar: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps);
        }
    }

    @Override
    public ReservaRestaurante buscarConTrash(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ReservaRestaurante r = null;

        try {
            conn = ConectarBD.conectar();
            String sql = "SELECT * FROM reservas_restaurante WHERE id_reserva = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                r = mapRow(rs);
            }
        } catch (SQLException e) {
            MessageUtil.error("Error al buscar reserva (con papelera): " + e.getMessage());
            System.err.println("Error en ReservaRestauranteDAO.buscarConTrash: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }

        return r;
    }

    // MÉTODOS ADICIONALES DEL CONTROLADOR PHP

    // 1. Crear reserva con verificación de disponibilidad
    public int crearReservaConVerificacion(ReservaRestaurante r) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConectarBD.conectar();
            conn.setAutoCommit(false); // Iniciar transacción

            // Verificar si la mesa está disponible
            if (!mesaDisponible(r.getMesa().getIdMesa(), r.getFechaReserva(), r.getHoraReserva())) {
                MessageUtil.error("La mesa ya está reservada para esa fecha y hora");
                return -1;
            }

            // Verificar que la mesa esté activa
            Mesa mesa = mesaDAO.buscar(r.getMesa().getIdMesa());
            if (mesa == null || mesa.getEstado() != Estado.ACTIVO) {
                MessageUtil.error("La mesa seleccionada no está disponible");
                return -1;
            }

            // Insertar reserva
            String sql = "INSERT INTO reservas_restaurante "
                       + "(id_cliente, id_mesa, fecha_reserva, hora_reserva, "
                       + "numero_personas, estado_reserva, estado, created_at, updated_at) "
                       + "VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";

            ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, r.getCliente().getIdCliente());
            ps.setInt(2, r.getMesa().getIdMesa());
            ps.setDate(3, Date.valueOf(r.getFechaReserva()));
            ps.setTime(4, Time.valueOf(r.getHoraReserva()));
            ps.setInt(5, r.getNumeroPersonas());
            ps.setString(6, r.getEstadoReserva().getValor());
            ps.setString(7, r.getEstado().getValor());

            ps.executeUpdate();

            // Obtener ID generado
            ResultSet rs = ps.getGeneratedKeys();
            int idReserva = -1;
            if (rs.next()) {
                idReserva = rs.getInt(1);
            }

            conn.commit();
            MessageUtil.createSuccess("reserva de restaurante");
            return idReserva;

        } catch (SQLException e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) {}
            MessageUtil.createError("reserva de restaurante");
            System.err.println("Error en ReservaRestauranteDAO.crearReservaConVerificacion: " + e.getMessage());
            return -1;
        } finally {
            try { if (conn != null) conn.setAutoCommit(true); } catch (SQLException e) {}
            cerrarRecursos(conn, ps);
        }
    }

    // 2. Cambiar estado de reserva
    public boolean cambiarEstadoReserva(int idReserva, EstadoReserva nuevoEstado) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConectarBD.conectar();
            String sql = "UPDATE reservas_restaurante SET estado_reserva = ?, updated_at = NOW() WHERE id_reserva = ?";

            ps = conn.prepareStatement(sql);
            ps.setString(1, nuevoEstado.getValor());
            ps.setInt(2, idReserva);

            int filas = ps.executeUpdate();
            if (filas > 0) {
                MessageUtil.success("Estado de reserva actualizado a: " + nuevoEstado.getValor());
                return true;
            }
            return false;

        } catch (SQLException e) {
            MessageUtil.error("Error al cambiar estado de reserva: " + e.getMessage());
            System.err.println("Error en ReservaRestauranteDAO.cambiarEstadoReserva: " + e.getMessage());
            return false;
        } finally {
            cerrarRecursos(conn, ps);
        }
    }

    // 3. Cambiar estado (activo/inactivo)
    public boolean cambiarEstado(int idReserva, Estado nuevoEstado) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConectarBD.conectar();
            String sql = "UPDATE reservas_restaurante SET estado = ?, updated_at = NOW() WHERE id_reserva = ?";

            ps = conn.prepareStatement(sql);
            ps.setString(1, nuevoEstado.getValor());
            ps.setInt(2, idReserva);

            int filas = ps.executeUpdate();
            if (filas > 0) {
                MessageUtil.success("Reserva " + nuevoEstado.getValor());
                return true;
            }
            return false;

        } catch (SQLException e) {
            MessageUtil.error("Error al cambiar estado: " + e.getMessage());
            System.err.println("Error en ReservaRestauranteDAO.cambiarEstado: " + e.getMessage());
            return false;
        } finally {
            cerrarRecursos(conn, ps);
        }
    }

    // 4. Verificar disponibilidad de mesa
    public boolean mesaDisponible(int idMesa, LocalDate fecha, LocalTime hora) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConectarBD.conectar();
            String sql = "SELECT COUNT(*) as ocupada FROM reservas_restaurante " +
                        "WHERE id_mesa = ? " +
                        "AND fecha_reserva = ? " +
                        "AND hora_reserva = ? " +
                        "AND estado_reserva != ? " + // No considerar canceladas
                        "AND estado = ? " + // Solo activas
                        "AND deleted_at IS NULL";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, idMesa);
            ps.setDate(2, Date.valueOf(fecha));
            ps.setTime(3, Time.valueOf(hora));
            ps.setString(4, EstadoReserva.CANCELADA.getValor());
            ps.setString(5, Estado.ACTIVO.getValor());

            rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("ocupada") == 0;
            }

        } catch (SQLException e) {
            MessageUtil.error("Error al verificar disponibilidad: " + e.getMessage());
            System.err.println("Error en ReservaRestauranteDAO.mesaDisponible: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }
        return false;
    }

    // 5. Filtrar reservas por estado
    public List<ReservaRestaurante> filtrarPorEstadoReserva(EstadoReserva estado) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<ReservaRestaurante> lista = new ArrayList<>();

        try {
            conn = ConectarBD.conectar();
            String sql = "SELECT * FROM reservas_restaurante " +
                        "WHERE estado_reserva = ? AND deleted_at IS NULL " +
                        "ORDER BY fecha_reserva DESC, hora_reserva DESC";

            ps = conn.prepareStatement(sql);
            ps.setString(1, estado.getValor());
            rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        } catch (SQLException e) {
            MessageUtil.error("Error al filtrar reservas: " + e.getMessage());
            System.err.println("Error en ReservaRestauranteDAO.filtrarPorEstadoReserva: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }
        return lista;
    }

    // 6. Filtrar por fecha
    public List<ReservaRestaurante> filtrarPorFecha(LocalDate fecha) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<ReservaRestaurante> lista = new ArrayList<>();

        try {
            conn = ConectarBD.conectar();
            String sql = "SELECT * FROM reservas_restaurante " +
                        "WHERE fecha_reserva = ? AND deleted_at IS NULL " +
                        "ORDER BY hora_reserva";

            ps = conn.prepareStatement(sql);
            ps.setDate(1, Date.valueOf(fecha));
            rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        } catch (SQLException e) {
            MessageUtil.error("Error al filtrar por fecha: " + e.getMessage());
            System.err.println("Error en ReservaRestauranteDAO.filtrarPorFecha: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }
        return lista;
    }

    // 7. Listar reservas con filtros (como en PHP)
    public List<ReservaRestaurante> listarConFiltros(EstadoReserva estadoReserva, LocalDate fecha, boolean mostrarTodas) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<ReservaRestaurante> lista = new ArrayList<>();

        try {
            conn = ConectarBD.conectar();
            StringBuilder sql = new StringBuilder("SELECT * FROM reservas_restaurante WHERE 1=1 ");
            List<Object> parametros = new ArrayList<>();

            if (!mostrarTodas) {
                sql.append("AND estado = ? ");
                parametros.add(Estado.ACTIVO.getValor());
            }

            if (estadoReserva != null) {
                sql.append("AND estado_reserva = ? ");
                parametros.add(estadoReserva.getValor());
            }

            if (fecha != null) {
                sql.append("AND fecha_reserva = ? ");
                parametros.add(Date.valueOf(fecha));
            }

            sql.append("ORDER BY fecha_reserva DESC, hora_reserva DESC");

            ps = conn.prepareStatement(sql.toString());
            
            // Establecer parámetros
            for (int i = 0; i < parametros.size(); i++) {
                Object param = parametros.get(i);
                if (param instanceof String) {
                    ps.setString(i + 1, (String) param);
                } else if (param instanceof Date) {
                    ps.setDate(i + 1, (Date) param);
                }
            }

            rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        } catch (SQLException e) {
            MessageUtil.error("Error al listar con filtros: " + e.getMessage());
            System.err.println("Error en ReservaRestauranteDAO.listarConFiltros: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }
        return lista;
    }

    // 8. Obtener reservas por cliente
    public List<ReservaRestaurante> obtenerReservasPorCliente(int idCliente) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<ReservaRestaurante> lista = new ArrayList<>();

        try {
            conn = ConectarBD.conectar();
            String sql = "SELECT * FROM reservas_restaurante " +
                        "WHERE id_cliente = ? AND estado = ? AND deleted_at IS NULL " +
                        "ORDER BY fecha_reserva DESC, hora_reserva DESC";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, idCliente);
            ps.setString(2, Estado.ACTIVO.getValor());
            rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        } catch (SQLException e) {
            MessageUtil.error("Error al obtener reservas del cliente: " + e.getMessage());
            System.err.println("Error en ReservaRestauranteDAO.obtenerReservasPorCliente: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }
        return lista;
    }

    // 9. Obtener reservas activas de hoy
    public List<ReservaRestaurante> obtenerReservasHoy() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<ReservaRestaurante> lista = new ArrayList<>();

        try {
            conn = ConectarBD.conectar();
            String sql = "SELECT * FROM reservas_restaurante " +
                        "WHERE fecha_reserva = CURDATE() " +
                        "AND estado = ? AND deleted_at IS NULL " +
                        "ORDER BY hora_reserva";

            ps = conn.prepareStatement(sql);
            ps.setString(1, Estado.ACTIVO.getValor());
            rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        } catch (SQLException e) {
            MessageUtil.error("Error al obtener reservas de hoy: " + e.getMessage());
            System.err.println("Error en ReservaRestauranteDAO.obtenerReservasHoy: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }
        return lista;
    }

    private ReservaRestaurante mapRow(ResultSet rs) throws SQLException {
        ReservaRestaurante r = new ReservaRestaurante();

        r.setIdReserva(rs.getInt("id_reserva"));

        // Cargar cliente y mesa
        Cliente cli = clienteDAO.buscar(rs.getInt("id_cliente"));
        Mesa mesa = mesaDAO.buscar(rs.getInt("id_mesa"));

        r.setCliente(cli);
        r.setMesa(mesa);

        r.setFechaReserva(rs.getDate("fecha_reserva").toLocalDate());
        r.setHoraReserva(rs.getTime("hora_reserva").toLocalTime());

        r.setNumeroPersonas(rs.getInt("numero_personas"));
        
        // CORRECCIÓN: Usar fromString() para enums
        String estadoReservaStr = rs.getString("estado_reserva");
        if (estadoReservaStr != null) {
            try {
                r.setEstadoReserva(EstadoReserva.fromString(estadoReservaStr));
            } catch (IllegalArgumentException e) {
                System.err.println("Error convirtiendo estado reserva: " + estadoReservaStr);
                r.setEstadoReserva(EstadoReserva.PENDIENTE); // Valor por defecto
            }
        }
        
        String estadoStr = rs.getString("estado");
        if (estadoStr != null) {
            try {
                r.setEstado(Estado.fromString(estadoStr));
            } catch (IllegalArgumentException e) {
                System.err.println("Error convirtiendo estado: " + estadoStr);
                r.setEstado(Estado.ACTIVO); // Valor por defecto
            }
        }

        Timestamp created = rs.getTimestamp("created_at");
        Timestamp updated = rs.getTimestamp("updated_at");
        Timestamp deleted = rs.getTimestamp("deleted_at");

        r.setCreatedAt(created != null ? created.toLocalDateTime() : null);
        r.setUpdatedAt(updated != null ? updated.toLocalDateTime() : null);
        r.setDeletedAt(deleted != null ? deleted.toLocalDateTime() : null);

        return r;
    }
}