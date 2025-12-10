package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import models.Cliente;
import models.TipoCliente;
import models.enums.Estado;
import models.enums.Saludo;

public class ClienteDAO extends BaseDAO<Cliente> {

    @Override
    public void crear(Cliente c) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConectarBD.conectar();

            String sql = "INSERT INTO clientes "
                       + "(id_tipo_cliente, email_info, contrasena, nombre, apellido, saludo, "
                       + "numero_telefono, prefijo_telefono, estado, created_at, updated_at) "
                       + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";

            ps = conn.prepareStatement(sql);

            ps.setInt(1, c.getTipoCliente().getIdTipoCliente());
            ps.setString(2, c.getEmail());
            ps.setString(3, c.getContraseña());
            ps.setString(4, c.getNombre());
            ps.setString(5, c.getApellido());
            ps.setString(6, c.getSaludo() != null ? c.getSaludo().name() : null);
            ps.setString(7, c.getNumTel());
            ps.setString(8, c.getPrefijo());
            ps.setString(9, c.getEstado().name());

            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error en ClienteDAO.crear: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps);
        }
    }

    @Override
    public Cliente buscar(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Cliente c = null;

        try {
            conn = ConectarBD.conectar();

            String sql = "SELECT c.*, t.nombre AS tipo_cliente_nombre "
                       + "FROM clientes c "
                       + "JOIN tipo_cliente t ON c.id_tipo_cliente = t.id_tipo_cliente "
                       + "WHERE c.id_cliente = ? AND c.deleted_at IS NULL";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                c = mapearResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error en ClienteDAO.buscar: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }

        return c;
    }

    @Override
    public List<Cliente> listar() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Cliente> lista = new ArrayList<>();

        try {
            conn = ConectarBD.conectar();

            String sql = "SELECT c.*, t.nombre AS tipo_cliente_nombre "
                       + "FROM clientes c "
                       + "JOIN tipo_cliente t ON c.id_tipo_cliente = t.id_tipo_cliente "
                       + "WHERE c.deleted_at IS NULL "
                       + "ORDER BY c.nombre";

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapearResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error en ClienteDAO.listar: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }

        return lista;
    }

    @Override
    public void actualizar(Cliente c) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = ConectarBD.conectar();

            String sql = "UPDATE clientes SET "
                       + "id_tipo_cliente = ?, email_info = ?, contrasena = ?, nombre = ?, apellido = ?, "
                       + "saludo = ?, numero_telefono = ?, prefijo_telefono = ?, estado = ?, "
                       + "updated_at = NOW() "
                       + "WHERE id_cliente = ?";

            ps = conn.prepareStatement(sql);

            ps.setInt(1, c.getTipoCliente().getIdTipoCliente());
            ps.setString(2, c.getEmail());
            ps.setString(3, c.getContraseña());
            ps.setString(4, c.getNombre());
            ps.setString(5, c.getApellido());
            ps.setString(6, c.getSaludo() != null ? c.getSaludo().name() : null);
            ps.setString(7, c.getNumTel());
            ps.setString(8, c.getPrefijo());
            ps.setString(9, c.getEstado().name());
            ps.setInt(10, c.getIdCliente());

            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error en ClienteDAO.actualizar: " + e.getMessage());
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

            String sql = "UPDATE clientes SET estado = 'INACTIVO', deleted_at = NOW() "
                       + "WHERE id_cliente = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);

            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error en ClienteDAO.eliminar: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps);
        }
    }
    
    @Override
    public List<Cliente> listarEliminados() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Cliente> lista = new ArrayList<>();
        
        try {
            conn = ConectarBD.conectar();
            String sql = "SELECT c.*, t.nombre AS tipo_cliente_nombre "
                       + "FROM clientes c "
                       + "JOIN tipo_cliente t ON c.id_tipo_cliente = t.id_tipo_cliente "
                       + "WHERE c.deleted_at IS NOT NULL "
                       + "ORDER BY c.deleted_at DESC";
            
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                lista.add(mapearResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error en ClienteDAO.listarEliminados: " + e.getMessage());
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
            String sql = "UPDATE clientes SET deleted_at = NULL, updated_at = NOW() "
                       + "WHERE id_cliente = ?";
            
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Error en ClienteDAO.restaurar: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps);
        }
    }
    
    @Override
    public Cliente buscarConTrash(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Cliente c = null;
        
        try {
            conn = ConectarBD.conectar();
            String sql = "SELECT c.*, t.nombre AS tipo_cliente_nombre "
                       + "FROM clientes c "
                       + "JOIN tipo_cliente t ON c.id_tipo_cliente = t.id_tipo_cliente "
                       + "WHERE c.id_cliente = ?";  // ← SIN deleted_at IS NULL
            
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                c = mapearResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error en ClienteDAO.buscarConTrash: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }
        return c;
    }
    
    public boolean existeEmail(String email, int idExcluir) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = ConectarBD.conectar();
            String sql = "SELECT COUNT(*) FROM clientes "
                       + "WHERE email_info = ? "
                       + "AND id_cliente != ? "
                       + "AND deleted_at IS NULL";
            
            ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            ps.setInt(2, idExcluir);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error en ClienteDAO.existeEmail: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }
        return false;
    }
    
    private Cliente mapearResultSet(ResultSet rs) throws SQLException {

        Cliente c = new Cliente();

        c.setIdCliente(rs.getInt("id_cliente"));

        TipoCliente tipo = new TipoCliente();
        tipo.setIdTipoCliente(rs.getInt("id_tipo_cliente"));
        tipo.setNombreTipo(rs.getString("tipo_cliente_nombre"));
        c.setTipoCliente(tipo);

        c.setEmail(rs.getString("email_info"));
        c.setContraseña(rs.getString("contrasena"));
        c.setNombre(rs.getString("nombre"));
        c.setApellido(rs.getString("apellido"));

        String saludoStr = rs.getString("saludo");
        c.setSaludo(saludoStr != null ? Saludo.valueOf(saludoStr) : null);

        c.setNumTel(rs.getString("numero_telefono"));
        c.setPrefijo(rs.getString("prefijo_telefono"));

        c.setEstado(Estado.valueOf(rs.getString("estado")));

        Timestamp created = rs.getTimestamp("created_at");
        Timestamp updated = rs.getTimestamp("updated_at");
        Timestamp deleted = rs.getTimestamp("deleted_at");

        c.setCreatedAt(created != null ? created.toLocalDateTime() : null);
        c.setUpdatedAt(updated != null ? updated.toLocalDateTime() : null);
        c.setDeletedAt(deleted != null ? deleted.toLocalDateTime() : null);

        return c;
    }
}
