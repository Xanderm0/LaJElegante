package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import utils.MessageUtil;
import models.Temporada;
import models.enums.NombreTemporada;

public class TemporadaDAO extends BaseDAO<Temporada> {
    
    @Override
    public void crear(Temporada t){
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = ConectarBD.conectar();
            String sql = "INSERT INTO temporadas "
                       + "(nombre, fecha_inicio, fecha_fin, modificador_precio, created_at, updated_at) "
                       + "VALUES (?, ?, ?, ?, NOW(), NOW())";
            
            ps = conn.prepareStatement(sql);
            ps.setString(1, t.getNombre().name());
            ps.setDate(2, java.sql.Date.valueOf(t.getFechaInicio()));
            ps.setDate(3, java.sql.Date.valueOf(t.getFechaFin()));
            ps.setDouble(4, t.getModificadorPrecio());
            
            ps.executeUpdate();
            MessageUtil.createSuccess("temporada");
        } catch (SQLException e) {
            MessageUtil.createError("temporada");
            System.err.println("Error en TemporadaDAO.crear: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps);
        }
    }
    
    @Override
    public Temporada buscar(int id){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Temporada temporada = null;
        
        try {
            conn = ConectarBD.conectar();
            String sql = "SELECT * FROM temporadas WHERE id_temporada = ? AND deleted_at IS NULL";
            
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            
            if(rs.next()){
                temporada = mapearResultSet(rs);
            }
            
        } catch (SQLException e) {
            MessageUtil.error("Error al buscar temporada: " + e.getMessage());
            System.err.println("Error en TemporadaDAO.buscar: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }
        return temporada;
    }
    
    @Override
    public List<Temporada> listar(){
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Temporada> temporadas = new ArrayList<>();
        
        try {
            conn = ConectarBD.conectar();
            String sql = "SELECT * FROM temporadas WHERE deleted_at IS NULL ORDER BY fecha_inicio";
            
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            
            while(rs.next()){
                temporadas.add(mapearResultSet(rs));
            }
            
        } catch (SQLException e) {
            MessageUtil.error("Error al listar temporadas: " + e.getMessage());
            System.err.println("Error en TemporadaDAO.listar: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }
        return temporadas;
    }
    
    @Override
    public void actualizar(Temporada t){
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = ConectarBD.conectar();
            String sql = "UPDATE temporadas SET "
                       + "nombre = ?, fecha_inicio = ?, fecha_fin = ?, modificador_precio = ?, "
                       + "updated_at = NOW() "
                       + "WHERE id_temporada = ?";
            
            ps = conn.prepareStatement(sql);
            ps.setString(1, t.getNombre().name());
            ps.setDate(2, java.sql.Date.valueOf(t.getFechaInicio()));
            ps.setDate(3, java.sql.Date.valueOf(t.getFechaFin()));
            ps.setDouble(4, t.getModificadorPrecio());
            ps.setInt(5, t.getIdTemporada());
            
            ps.executeUpdate();
            MessageUtil.updateSuccess("temporada");
        } catch (SQLException e) {
            MessageUtil.updateError("temporada");
            System.err.println("Error en TemporadaDAO.actualizar: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps);    
        }
    }
    
    @Override
    public void eliminar(int id){
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = ConectarBD.conectar();
            String sql = "UPDATE temporadas SET deleted_at = NOW() WHERE id_temporada = ?";
            
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            
            ps.executeUpdate();
            MessageUtil.deleteSuccess("temporada");
            
        } catch (SQLException e) {
            MessageUtil.deleteError("temporada");
            System.err.println("Error en TemporadaDAO.eliminar: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps);
        }
    }
    
    @Override
    public List<Temporada> listarEliminados() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Temporada> temporadas = new ArrayList<>();

        try {
            conn = ConectarBD.conectar();
            String sql = "SELECT * FROM temporadas WHERE deleted_at IS NOT NULL ORDER BY deleted_at DESC";

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while(rs.next()){
                temporadas.add(mapearResultSet(rs));
            }

        } catch (SQLException e) {
            MessageUtil.error("Error al listar temporadas eliminadas: " + e.getMessage());
            System.err.println("Error en TemporadaDAO.listarEliminados: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }
        return temporadas;
}

    @Override
    public void restaurar(int id) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConectarBD.conectar();
            String sql = "UPDATE temporadas SET deleted_at = NULL, updated_at = NOW() WHERE id_temporada = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
            MessageUtil.success("Temporada restaurada correctamente");
        } catch (SQLException e) {
            MessageUtil.error("Error al restaurar temporada: " + e.getMessage());
            System.err.println("Error en TemporadaDAO.restaurar: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps);
        }
    }

    @Override
    public Temporada buscarConTrash(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Temporada temporada = null;

        try {
            conn = ConectarBD.conectar();
            String sql = "SELECT * FROM temporadas WHERE id_temporada = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if(rs.next()){
                temporada = mapearResultSet(rs);
            }

        } catch (SQLException e) {
            MessageUtil.error("Error al buscar temporada (con papelera): " + e.getMessage());
            System.err.println("Error en TemporadaDAO.buscarConTrash: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }
        return temporada;
    }
    
    public Temporada getTemporadaActual() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Temporada temporada = null;
        
        try {
            conn = ConectarBD.conectar();
            String sql = "SELECT * FROM temporadas "
                       + "WHERE NOW() BETWEEN fecha_inicio AND fecha_fin "
                       + "AND deleted_at IS NULL "
                       + "LIMIT 1";
            
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                temporada = mapearResultSet(rs);
            }
            
        } catch (SQLException e) {
            MessageUtil.error("Error al obtener temporada actual: " + e.getMessage());
            System.err.println("Error en TemporadaDAO.getTemporadaActual: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }
        return temporada;
    }
    
    public Temporada obtenerTemporadaPorFecha(Date fecha) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Temporada temporada = null;

        try {
            conn = ConectarBD.conectar();
            String sql = "SELECT * FROM temporadas "
                       + "WHERE ? BETWEEN fecha_inicio AND fecha_fin "
                       + "AND deleted_at IS NULL "
                       + "LIMIT 1";

            ps = conn.prepareStatement(sql);
            ps.setDate(1, new java.sql.Date(fecha.getTime()));
            rs = ps.executeQuery();

            if (rs.next()) {
                temporada = mapearResultSet(rs);
            }

        } catch (SQLException e) {
            MessageUtil.error("Error al obtener temporada por fecha: " + e.getMessage());
            System.err.println("Error en TemporadaDAO.obtenerTemporadaPorFecha: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }
        return temporada;
    }

    private Temporada mapearResultSet(ResultSet rs) throws SQLException {
        Temporada t = new Temporada();

        t.setIdTemporada(rs.getInt("id_temporada"));

        String nombreStr = rs.getString("nombre");
        if (nombreStr != null) {
            try {
                t.setNombre(NombreTemporada.valueOf(nombreStr));
            } catch (IllegalArgumentException e) {
                System.err.println("Error convirtiendo nombre temporada: " + nombreStr);
                t.setNombre(NombreTemporada.BAJA);
            }
        }

        t.setFechaInicio(rs.getDate("fecha_inicio").toLocalDate());
        t.setFechaFin(rs.getDate("fecha_fin").toLocalDate());
        t.setModificadorPrecio(rs.getDouble("modificador_precio"));

        // Fechas de ClaseBase
        Timestamp created = rs.getTimestamp("created_at");
        Timestamp updated = rs.getTimestamp("updated_at");
        Timestamp deleted = rs.getTimestamp("deleted_at");

        t.setCreatedAt(created != null ? created.toLocalDateTime() : null);
        t.setUpdatedAt(updated != null ? updated.toLocalDateTime() : null);
        t.setDeletedAt(deleted != null ? deleted.toLocalDateTime() : null);

        return t;
    }
}