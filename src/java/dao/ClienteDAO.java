package dao;

import java.time.LocalDate;
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
import utils.MessageUtil;

public class ClienteDAO extends BaseDAO<Cliente> {

    /* ===================== CRUD ===================== */

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
            MessageUtil.createSuccess("cliente");

        } catch (SQLException e) {
            MessageUtil.createError("cliente");
            System.err.println("ClienteDAO.crear: " + e.getMessage());
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

            String sql = "SELECT c.*, t.nombre_tipo AS tipo_cliente_nombre "
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
            MessageUtil.error("Error al buscar cliente");
            System.err.println("ClienteDAO.buscar: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }

        return c;
    }
    //conatr clientes activos 
public int contarClientes() {
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    int total = 0;

    try {
        conn = ConectarBD.conectar();
        if (conn == null) return 0;

        String sql = "SELECT COUNT(*) FROM clientes WHERE estado = 'activo'";
        ps = conn.prepareStatement(sql);
        rs = ps.executeQuery();

        if (rs.next()) {
            total = rs.getInt(1);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    } finally {
        ConectarBD.cerrarConexion(conn, ps, rs);
    }

    return total;
}


    /* ===================== BUSQUEDA POR EMAIL ===================== */

    public Cliente buscarPorEmail(String email) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Cliente cliente = null;

        try {
            conn = ConectarBD.conectar();

            String sql = "SELECT c.*, t.nombre_tipo AS tipo_cliente_nombre "
                       + "FROM clientes c "
                       + "JOIN tipo_cliente t ON c.id_tipo_cliente = t.id_tipo_cliente "
                       + "WHERE c.email_info = ? "
                       + "AND c.deleted_at IS NULL";

            ps = conn.prepareStatement(sql);
            ps.setString(1, email);

            rs = ps.executeQuery();

            if (rs.next()) {
                cliente = mapearResultSet(rs);
            }

        } catch (SQLException e) {
            MessageUtil.error("Error al buscar cliente por email");
            System.err.println("ClienteDAO.buscarPorEmail: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }

        return cliente;
    }

    @Override
    public List<Cliente> listar() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Cliente> lista = new ArrayList<>();

        try {
            conn = ConectarBD.conectar();

            String sql = "SELECT c.*, t.nombre_tipo AS tipo_cliente_nombre "
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
            MessageUtil.error("Error al listar clientes");
            System.err.println("ClienteDAO.listar: " + e.getMessage());
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
            MessageUtil.updateSuccess("cliente");

        } catch (SQLException e) {
            MessageUtil.updateError("cliente");
            System.err.println("ClienteDAO.actualizar: " + e.getMessage());
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

            MessageUtil.deleteSuccess("cliente");

        } catch (SQLException e) {
            MessageUtil.deleteError("cliente");
            System.err.println("ClienteDAO.eliminar: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps);
        }
    }

    /* ===================== VALIDACIONES ===================== */

    public boolean existeEmail(String email, int idExcluir) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean existe = false;

        try {
            conn = ConectarBD.conectar();

            String sql = "SELECT COUNT(*) "
                    + "FROM clientes "
                    + "WHERE email_info = ? "
                    + "AND deleted_at IS NULL ";

            if (idExcluir > 0) {
                sql += "AND id_cliente <> ?";
            }

            ps = conn.prepareStatement(sql);
            ps.setString(1, email);

            if (idExcluir > 0) {
                ps.setInt(2, idExcluir);
            }

            rs = ps.executeQuery();

            if (rs.next()) {
                existe = rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            MessageUtil.error("Error al validar email");
            System.err.println("ClienteDAO.existeEmail: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }

        return existe;
    }

    /* ===================== PAPELERA ===================== */

    @Override
    public List<Cliente> listarEliminados() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Cliente> lista = new ArrayList<>();

        try {
            conn = ConectarBD.conectar();

            String sql = "SELECT c.*, t.nombre_tipo AS tipo_cliente_nombre "
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
            MessageUtil.error("Error al listar clientes eliminados");
            System.err.println("ClienteDAO.listarEliminados: " + e.getMessage());
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

            MessageUtil.success("Cliente restaurado correctamente");

        } catch (SQLException e) {
            MessageUtil.error("Error al restaurar cliente");
            System.err.println("ClienteDAO.restaurar: " + e.getMessage());
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

            String sql = "SELECT c.*, t.nombre_tipo AS tipo_cliente_nombre "
                    + "FROM clientes c "
                    + "JOIN tipo_cliente t ON c.id_tipo_cliente = t.id_tipo_cliente "
                    + "WHERE c.id_cliente = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                c = mapearResultSet(rs);
            }

        } catch (SQLException e) {
            MessageUtil.error("Error al buscar cliente (con papelera)");
            System.err.println("ClienteDAO.buscarConTrash: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }

        return c;
    }

    /* ===================== REPORTE ===================== */

    public List<Cliente> listarReporte(
            Estado estado,
            Integer idTipoCliente,
            LocalDate desde,
            LocalDate hasta
    ) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Cliente> lista = new ArrayList<>();

        try {
            conn = ConectarBD.conectar();

            StringBuilder sql = new StringBuilder(
                    "SELECT c.*, t.nombre_tipo AS tipo_cliente_nombre "
                    + "FROM clientes c "
                    + "JOIN tipo_cliente t ON c.id_tipo_cliente = t.id_tipo_cliente "
                    + "WHERE c.deleted_at IS NULL "
            );

            if (estado != null) sql.append("AND c.estado = ? ");
            if (idTipoCliente != null) sql.append("AND c.id_tipo_cliente = ? ");
            if (desde != null) sql.append("AND DATE(c.created_at) >= ? ");
            if (hasta != null) sql.append("AND DATE(c.created_at) <= ? ");

            ps = conn.prepareStatement(sql.toString());

            int index = 1;
            if (estado != null) ps.setString(index++, estado.name());
            if (idTipoCliente != null) ps.setInt(index++, idTipoCliente);
            if (desde != null) ps.setDate(index++, java.sql.Date.valueOf(desde));
            if (hasta != null) ps.setDate(index++, java.sql.Date.valueOf(hasta));

            rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapearResultSet(rs));
            }

        } catch (SQLException e) {
            MessageUtil.error("Error al generar reporte de clientes");
            System.err.println("ClienteDAO.listarReporte: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, ps, rs);
        }

        return lista;
    }
    
    public int contarClientesNuevosUltimos30Dias() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int total = 0;

        try {
            conn = ConectarBD.conectar();
            if (conn == null) return 0;

            String sql = "SELECT COUNT(*) FROM clientes " +
                        "WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL 30 DAY) " +
                        "AND deleted_at IS NULL " +
                        "AND estado = 'activo'";

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            if (rs.next()) {
                total = rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConectarBD.cerrarConexion(conn, ps, rs);
        }

        return total;
    }
        /* ===================== MAPPER ===================== */
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

        c.setEstado(convertirStringAEstado(rs.getString("estado")));

        Timestamp created = rs.getTimestamp("created_at");
        Timestamp updated = rs.getTimestamp("updated_at");
        Timestamp deleted = rs.getTimestamp("deleted_at");

        c.setCreatedAt(created != null ? created.toLocalDateTime() : null);
        c.setUpdatedAt(updated != null ? updated.toLocalDateTime() : null);
        c.setDeletedAt(deleted != null ? deleted.toLocalDateTime() : null);

        return c;
    }

    private Estado convertirStringAEstado(String estadoStr) {
        if (estadoStr == null) return null;

        try {
            return Estado.valueOf(estadoStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            for (Estado estado : Estado.values()) {
                if (estado.getValor().equalsIgnoreCase(estadoStr)) {
                    return estado;
                }
            }
            throw e;
        }
    }
}
