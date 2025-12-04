package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import models.Cliente;
import models.TipoCliente;
import models.enums.Estado;
import models.enums.Saludo;

public class ClienteDAO {

    private final Connection conn = ConectarBD.conectar();
    private PreparedStatement ps;
    private ResultSet rs;

    // Obtener todos los clientes activos
    public List<Cliente> getClientesActivos() {
        List<Cliente> listaClientes = new ArrayList<>();

        try {
            String sql = 
                "SELECT c.*, t.nombre AS tipo_nombre " +
                "FROM clientes c " +
                "JOIN tipo_cliente t ON c.id_tipo_cliente = t.id_tipo_cliente " +
                "WHERE c.estado = 'ACTIVO'";

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Cliente c = mapCliente(rs);
                listaClientes.add(c);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listaClientes;
    }

    // Obtener cliente por ID
    public Cliente getClienteById(long id) {
        Cliente c = null;

        try {
            String sql =
                "SELECT c.*, t.nombre AS tipo_nombre " +
                "FROM clientes c " +
                "JOIN tipo_cliente t ON c.id_tipo_cliente = t.id_tipo_cliente " +
                "WHERE c.id_cliente = ?";

            ps = conn.prepareStatement(sql);
            ps.setLong(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                c = mapCliente(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return c;
    }

    // Crear nuevo cliente
    public void createCliente(Cliente c) {
        try {
            String sql =
                "INSERT INTO clientes(nombre, apellido, email_info, contrasena, numero_telefono, prefijo_telefono, id_tipo_cliente, saludo, estado, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";

            ps = conn.prepareStatement(sql);
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getApellido());
            ps.setString(3, c.getEmail());
            ps.setString(4, c.getContraseña());
            ps.setString(5, c.getNumTel());
            ps.setString(6, c.getPrefijo());
            ps.setInt(7, c.getTipoCliente().getIdTipoCliente());
            ps.setString(8, c.getSaludo().name());
            ps.setString(9, c.getEstado().name());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Actualizar cliente
    public void updateCliente(Cliente c) {
        try {
            String sql =
                "UPDATE clientes SET nombre = ?, apellido = ?, email_info = ?, contrasena = ?, numero_telefono = ?, " +
                "prefijo_telefono = ?, id_tipo_cliente = ?, saludo = ?, estado = ?, updated_at = CURRENT_TIMESTAMP " +
                "WHERE id_cliente = ?";

            ps = conn.prepareStatement(sql);

            ps.setString(1, c.getNombre());
            ps.setString(2, c.getApellido());
            ps.setString(3, c.getEmail());
            ps.setString(4, c.getContraseña());
            ps.setString(5, c.getNumTel());
            ps.setString(6, c.getPrefijo());
            ps.setInt(7, c.getTipoCliente().getIdTipoCliente());
            ps.setString(8, c.getSaludo().name());
            ps.setString(9, c.getEstado().name());
            ps.setInt(10, c.getIdCliente());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Eliminar cliente (marcar inactivo)
    public void deleteCliente(long id) {
        try {
            String sql =
                "UPDATE clientes SET estado = 'INACTIVO', deleted_at = CURRENT_TIMESTAMP WHERE id_cliente = ?";

            ps = conn.prepareStatement(sql);
            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // --------------------------
    // Método para mapear objeto
    // --------------------------
    private Cliente mapCliente(ResultSet rs) throws SQLException {
        Cliente c = new Cliente();

        c.setIdCliente(rs.getInt("id_cliente"));
        c.setNombre(rs.getString("nombre"));
        c.setApellido(rs.getString("apellido"));
        c.setEmail(rs.getString("email_info"));
        c.setContraseña(rs.getString("contrasena"));
        c.setNumTel(rs.getString("numero_telefono"));
        c.setPrefijo(rs.getString("prefijo_telefono"));

        // Enums
        c.setSaludo(Saludo.valueOf(rs.getString("saludo")));
        c.setEstado(Estado.valueOf(rs.getString("estado")));

        // TipoCliente (solo ID y nombre)
        TipoCliente tipo = new TipoCliente();
        tipo.setIdTipoCliente(rs.getInt("id_tipo_cliente"));
        tipo.setNombre(rs.getString("tipo_nombre"));
        c.setTipoCliente(tipo);

        return c;
    }

    Cliente getById(int idCliente) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
