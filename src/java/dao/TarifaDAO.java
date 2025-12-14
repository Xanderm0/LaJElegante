package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;
import models.Tarifa;
import models.TipoHabitacion;
import models.Temporada;
import models.enums.Estado;
import models.enums.NombreTipoHabitacion;
import utils.MessageUtil;

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
            ps.setString(3, t.getEstado().getValor());
            ps.setInt(4, t.getTipoHabitacion().getIdTipoHabitacion());
            ps.setInt(5, t.getTemporada().getIdTemporada());
            ps.setTimestamp(6, Timestamp.valueOf(t.getCreatedAt()));
            ps.setTimestamp(7, Timestamp.valueOf(t.getUpdatedAt()));

            ps.executeUpdate();
            MessageUtil.createSuccess("tarifa");
        } catch (SQLException e) {
            MessageUtil.createError("tarifa");
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
            MessageUtil.error("Error al buscar tarifa: " + e.getMessage());
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
            MessageUtil.error("Error al listar tarifas: " + e.getMessage());
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
            ps.setString(3, t.getEstado().getValor());
            ps.setInt(4, t.getTipoHabitacion().getIdTipoHabitacion());
            ps.setInt(5, t.getTemporada().getIdTemporada());
            ps.setTimestamp(6, Timestamp.valueOf(t.getUpdatedAt()));
            ps.setInt(7, t.getIdTarifa());

            ps.executeUpdate();
            MessageUtil.updateSuccess("tarifa");
        } catch (SQLException e) {
            MessageUtil.updateError("tarifa");
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
            ps.executeUpdate();
            MessageUtil.deleteSuccess("tarifa");
        } catch (SQLException e) {
            MessageUtil.deleteError("tarifa");
            System.err.println("Error en TarifaDAO.eliminar: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps);
        }
    }
    
    @Override
    public List<Tarifa> listarEliminados() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Tarifa> lista = new ArrayList<>();

        try {
            conn = ConectarBD.conectar();
            String sql = "SELECT * FROM tarifas WHERE deleted_at IS NOT NULL ORDER BY deleted_at DESC";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapTarifa(rs));
            }

        } catch (SQLException e) {
            MessageUtil.error("Error al listar tarifas eliminadas: " + e.getMessage());
            System.err.println("Error en TarifaDAO.listarEliminados: " + e.getMessage());
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
            String sql = "UPDATE tarifas SET deleted_at = NULL, updated_at = NOW() WHERE id_tarifa = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
            MessageUtil.success("Tarifa restaurada correctamente");
        } catch (SQLException e) {
            MessageUtil.error("Error al restaurar tarifa: " + e.getMessage());
            System.err.println("Error en TarifaDAO.restaurar: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps);
        }
    }

    @Override
    public Tarifa buscarConTrash(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Tarifa t = null;

        try {
            conn = ConectarBD.conectar();
            String sql = "SELECT * FROM tarifas WHERE id_tarifa = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                t = mapTarifa(rs);
            }

        } catch (SQLException e) {
            MessageUtil.error("Error al buscar tarifa (con papelera): " + e.getMessage());
            System.err.println("Error en TarifaDAO.buscarConTrash: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }
        return t;
    }
    
    public double obtenerPrecioPorFecha(int idTipoHabitacion, Date fecha) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConectarBD.conectar();
        String sql = 
            "SELECT " +
            "    CASE " +
            "        WHEN tf.id_temporada IS NOT NULL THEN " + 
            "            tf.tarifa_fija * tm.modificador_precio " +
            "        ELSE " +
            "            tf.tarifa_fija " +
            "    END as precio_calculado " +
            "FROM tarifas tf " +
            "LEFT JOIN temporadas tm ON tf.id_temporada = tm.id_temporada " +
            "WHERE tf.id_tipo_habitacion = ? " +
            "AND tf.estado = 'vigente' " +
            "AND tf.deleted_at IS NULL " +
            "AND ( " +
            "    tf.id_temporada IS NULL " + 
            "    OR ( " + 
            "        tm.fecha_inicio <= ? " +
            "        AND tm.fecha_fin >= ? " +
            "        AND tm.deleted_at IS NULL " +
            "    ) " +
            ") " +
            "ORDER BY " +
            "    CASE WHEN tf.id_temporada IS NOT NULL THEN 0 ELSE 1 END, " +
            "    tf.updated_at DESC " +
            "LIMIT 1";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, idTipoHabitacion);
            ps.setDate(2, new java.sql.Date(fecha.getTime()));
            ps.setDate(3, new java.sql.Date(fecha.getTime()));

            rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble("precio_calculado");
            } else {
                MessageUtil.warn("No hay tarifa configurada para el tipo de habitación " + idTipoHabitacion);
                return 0.0;
            }

        } catch (SQLException e) {
            MessageUtil.error("Error al calcular precio: " + e.getMessage());
            System.err.println("Error en TarifaDAO.obtenerPrecioPorFecha: " + e.getMessage());
            return 0.0;
        } finally {
            cerrarRecursos(conn, ps, rs);
        }
    }

    public double calcularPrecioTotalReserva(int idTipoHabitacion, Date fechaInicio, Date fechaFin) {
        double precioTotal = 0.0;

        if (fechaInicio.after(fechaFin)) {
            MessageUtil.error("La fecha de inicio no puede ser posterior a la fecha de fin");
            return 0.0;
        }

        if (fechaInicio.equals(fechaFin)) {
            MessageUtil.warn("Fechas iguales detectadas, se considerará 1 noche de estadía");
            Calendar cal = Calendar.getInstance();
            cal.setTime(fechaFin);
            cal.add(Calendar.DATE, 1);
            fechaFin = cal.getTime();
        }

        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(fechaInicio);

        while (!cal.getTime().after(fechaFin)) {
            Date fechaNoche = cal.getTime();
            double precioNoche = obtenerPrecioPorFecha(idTipoHabitacion, fechaNoche);

            if (precioNoche == 0.0) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                MessageUtil.error("No se pudo calcular precio para el " + sdf.format(fechaNoche));
                return 0.0;
            }

            precioTotal += precioNoche;
            cal.add(java.util.Calendar.DATE, 1);
        }

        return precioTotal;
    }

    public Tarifa obtenerTarifaVigente(int idTipoHabitacion, Date fecha) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Tarifa tarifa = null;

        try {
            conn = ConectarBD.conectar();
            String sql = 
                "SELECT tf.*, " +
                "    th.nombre_tipo, " +
                "    tm.nombre as nombre_temporada, tm.modificador_precio " +
                "FROM tarifas tf " +
                "LEFT JOIN tipo_habitacion th ON tf.id_tipo_habitacion = th.id_tipo_habitacion " +
                "LEFT JOIN temporadas tm ON tf.id_temporada = tm.id_temporada " +
                "WHERE tf.id_tipo_habitacion = ? " +
                "AND tf.estado = 'vigente' " +
                "AND tf.deleted_at IS NULL " +
                "AND ( " +
                "    tf.id_temporada IS NULL " +
                "    OR (tm.fecha_inicio <= ? AND tm.fecha_fin >= ? AND tm.deleted_at IS NULL) " +
                ") " +
                "ORDER BY tf.id_temporada DESC, tf.updated_at DESC " +
                "LIMIT 1";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, idTipoHabitacion);
            ps.setDate(2, new java.sql.Date(fecha.getTime()));
            ps.setDate(3, new java.sql.Date(fecha.getTime()));

            rs = ps.executeQuery();

            if (rs.next()) {
                tarifa = mapTarifaConJoins(rs);
            }

        } catch (SQLException e) {
            MessageUtil.error("Error al obtener tarifa vigente: " + e.getMessage());
            System.err.println("Error en TarifaDAO.obtenerTarifaVigente: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }

        return tarifa;
    }

    private Tarifa mapTarifaConJoins(ResultSet rs) throws SQLException {
        Tarifa t = mapTarifa(rs);

        if (existeColumna(rs, "nombre_tipo")) {
            String nombreStr = rs.getString("nombre_tipo");
            if (nombreStr != null) {
                try {
                    t.getTipoHabitacion().setNombreTipo(
                        NombreTipoHabitacion.fromString(nombreStr)
                    );
                } catch (IllegalArgumentException e) {
                    System.err.println("Error convirtiendo nombre tipo habitación: " + nombreStr);
                    t.getTipoHabitacion().setNombreTipo(NombreTipoHabitacion.BASICA);
                }
            }
        }

        if (existeColumna(rs, "nombre_temporada")) {
            String nombreTempStr = rs.getString("nombre_temporada");
            if (nombreTempStr != null) {
                try {
                    t.getTemporada().setNombre(
                        models.enums.NombreTemporada.valueOf(nombreTempStr)
                    );
                } catch (IllegalArgumentException e) {
                    System.err.println("Error convirtiendo nombre temporada: " + nombreTempStr);
                }
            }
        }

        return t;
    }
    
    private boolean existeColumna(ResultSet rs, String columna) {
        try {
            rs.findColumn(columna);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
    
    private Tarifa mapTarifa(ResultSet rs) throws SQLException {
        Tarifa t = new Tarifa();

        t.setIdTarifa(rs.getInt("id_tarifa"));
        t.setTarifaFija(rs.getFloat("tarifa_fija"));
        t.setPrecioFinal(rs.getFloat("precio_final"));

        String estadoStr = rs.getString("estado");
        if (estadoStr != null) {
            try {
                t.setEstado(Estado.valueOf(estadoStr.toUpperCase()));
            } catch (IllegalArgumentException e) {
                estadoStr = estadoStr.trim().toLowerCase();
                boolean encontrado = false;

                for (Estado estado : Estado.values()) {
                    if (estado.getValor().equals(estadoStr)) {
                        t.setEstado(estado);
                        encontrado = true;
                        break;
                    }
                }

                if (!encontrado) {
                    System.err.println("Error convirtiendo estado: " + estadoStr);
                    t.setEstado(Estado.VIGENTE);
                }
            }
        }

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