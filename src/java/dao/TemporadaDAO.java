package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

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
            ps.setDate(2, Date.valueOf(t.getFechaInicio()));
            ps.setDate(3, Date.valueOf(t.getFechaFin()));
            ps.setDouble(4, t.getModificadorPrecio());
            
            ps.executeUpdate();
        } catch (SQLException e) {
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
            ps.setDate(2, Date.valueOf(t.getFechaInicio()));
            ps.setDate(3, Date.valueOf(t.getFechaFin()));
            ps.setDouble(4, t.getModificadorPrecio());
            ps.setInt(5, t.getIdTemporada());
            
            int filas = ps.executeUpdate();
            System.out.println(filas + "temporadas(s) actualizada(s)");
        } catch (SQLException e) {
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
            
            int filas = ps.executeUpdate();
            System.out.println(filas + " temporada(s) eliminada(s)");
            
        } catch (SQLException e) {
            System.err.println("Error en TemporadaDAO.eliminar: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps);
        }
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
            System.err.println("Error en TemporadaDAO.getTemporadaActual: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }
        return temporada;
    }

    private Temporada mapearResultSet(ResultSet rs) throws SQLException {

        Temporada t = new Temporada();

        t.setIdTemporada(rs.getInt("id_temporada"));
        t.setNombre(NombreTemporada.valueOf(rs.getString("nombre")));
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
