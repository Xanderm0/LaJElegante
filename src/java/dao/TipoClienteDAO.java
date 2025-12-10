package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import models.TipoCliente;

public class TipoClienteDAO extends BaseDAO<TipoCliente> {

    @Override
    public void crear(TipoCliente t) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConectarBD.conectar();
            String sql = "INSERT INTO tipo_cliente "
                       + "(nombre_tipo, created_at, updated_at) "
                       + "VALUES (?, NOW(), NOW())";

            ps = conn.prepareStatement(sql);
            ps.setString(1, t.getNombreTipo());

            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error en TipoClienteDAO.crear: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps);
        }
    }

    @Override
    public TipoCliente buscar(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        TipoCliente tipo = null;

        try {
            conn = ConectarBD.conectar();
            String sql = "SELECT * FROM tipo_cliente "
                       + "WHERE id_tipo_cliente = ? AND deleted_at IS NULL";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                tipo = mapearResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error en TipoClienteDAO.buscar: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }

        return tipo;
    }

    @Override
    public List<TipoCliente> listar() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<TipoCliente> lista = new ArrayList<>();

        try {
            conn = ConectarBD.conectar();
            String sql = "SELECT * FROM tipo_cliente "
                       + "WHERE deleted_at IS NULL ORDER BY id_tipo_cliente";

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapearResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error en TipoClienteDAO.listar: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }

        return lista;
    }

    @Override
    public void actualizar(TipoCliente t) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConectarBD.conectar();
            String sql = "UPDATE tipo_cliente SET "
                       + "nombre_tipo = ?, updated_at = NOW() "
                       + "WHERE id_tipo_cliente = ?";

            ps = conn.prepareStatement(sql);
            ps.setString(1, t.getNombreTipo());
            ps.setInt(2, t.getIdTipoCliente());

            int filas = ps.executeUpdate();
            System.out.println(filas + " tipo_cliente actualizado(s)");

        } catch (SQLException e) {
            System.err.println("Error en TipoClienteDAO.actualizar: " + e.getMessage());
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
            String sql = "UPDATE tipo_cliente "
                       + "SET deleted_at = NOW() "
                       + "WHERE id_tipo_cliente = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);

            int filas = ps.executeUpdate();
            System.out.println(filas + " tipo_cliente eliminado(s)");

        } catch (SQLException e) {
            System.err.println("Error en TipoClienteDAO.eliminar: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps);
        }
    }
    
        @Override
    public List<TipoCliente> listarEliminados() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<TipoCliente> lista = new ArrayList<>();
        
        try {
            conn = ConectarBD.conectar();
            String sql = "SELECT * FROM tipo_cliente "
                       + "WHERE deleted_at IS NOT NULL "
                       + "ORDER BY deleted_at DESC";
            
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                lista.add(mapearResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error en TipoClienteDAO.listarEliminados: " + e.getMessage());
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
            String sql = "UPDATE tipo_cliente SET deleted_at = NULL, updated_at = NOW() "
                       + "WHERE id_tipo_cliente = ?";
            
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Error en TipoClienteDAO.restaurar: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps);
        }
    }
    
    @Override
    public TipoCliente buscarConTrash(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        TipoCliente tc = null;
        
        try {
            conn = ConectarBD.conectar();
            String sql = "SELECT * FROM tipo_cliente "
                       + "WHERE id_tipo_cliente = ?";  // ← SIN deleted_at
            
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                tc = mapearResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error en TipoClienteDAO.buscarConTrash: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }
        return tc;
    }
    
    private TipoCliente mapearResultSet(ResultSet rs) throws SQLException {

        TipoCliente t = new TipoCliente();

        t.setIdTipoCliente(rs.getInt("id_tipo_cliente"));
        t.setNombreTipo(rs.getString("nombre_tipo"));

        Timestamp created = rs.getTimestamp("created_at");
        Timestamp updated = rs.getTimestamp("updated_at");
        Timestamp deleted = rs.getTimestamp("deleted_at");

        t.setCreatedAt(created != null ? created.toLocalDateTime() : null);
        t.setUpdatedAt(updated != null ? updated.toLocalDateTime() : null);
        t.setDeletedAt(deleted != null ? deleted.toLocalDateTime() : null);

        return t;
    }
}
