package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import models.Mesa;
import models.enums.Estado;

public class MesaDAO {

    private final Connection conn = ConectarBD.conectar();
    private PreparedStatement ps;
    private ResultSet rs;

    private Estado mapEstado(int valorBD) {
        return valorBD == 1 ? Estado.ACTIVO : Estado.INACTIVO;
    }

    private int mapEstadoToInt(Estado estado) {
        return estado == Estado.ACTIVO ? 1 : 0;
    }

    public Mesa getMesaById(int id) {
        Mesa m = null;

        try {
            String sql = "SELECT * FROM mesas WHERE id_mesa = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                m = mapRow(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return m;
    }

    public List<Mesa> getMesasDisponibles() {
        List<Mesa> lista = new ArrayList<>();

        try {
            String sql = "SELECT * FROM mesas WHERE activo = 1";
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapRow(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public void createMesa(Mesa m) {
        try {
            String sql =
                "INSERT INTO mesas(numero_mesa, capacidad, zona, ubicacion_detalle, activo, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, NOW(), NOW())";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, m.getNumeroMesa());
            ps.setInt(2, m.getCapacidad());
            ps.setString(3, m.getZona());
            ps.setString(4, m.getUbicacionDetalle());
            ps.setInt(5, mapEstadoToInt(m.getEstado()));

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateMesa(Mesa m) {
        try {
            String sql =
                "UPDATE mesas SET numero_mesa = ?, capacidad = ?, zona = ?, ubicacion_detalle = ?, activo = ?, updated_at = NOW() " +
                "WHERE id_mesa = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, m.getNumeroMesa());
            ps.setInt(2, m.getCapacidad());
            ps.setString(3, m.getZona());
            ps.setString(4, m.getUbicacionDetalle());
            ps.setInt(5, mapEstadoToInt(m.getEstado()));
            ps.setInt(6, m.getIdMesa());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Mesa mapRow(ResultSet rs) throws SQLException {
        Mesa m = new Mesa();

        m.setIdMesa(rs.getInt("id_mesa"));
        m.setNumeroMesa(rs.getInt("numero_mesa"));
        m.setCapacidad(rs.getInt("capacidad"));
        m.setZona(rs.getString("zona"));
        m.setUbicacionDetalle(rs.getString("ubicacion_detalle"));

        m.setEstado(mapEstado(rs.getInt("activo")));

        m.setCreated_at(rs.getTimestamp("created_at"));
        m.setUpdated_at(rs.getTimestamp("updated_at"));

        return m;
    }

    Mesa getById(int aInt) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

