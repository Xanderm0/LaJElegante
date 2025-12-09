package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public abstract class BaseDAO<T> {
    
    protected void cerrarRecursos(Connection conn, PreparedStatement ps, ResultSet rs) {
        ConectarBD.cerrarConexion(conn, ps, rs);
    }
    
    protected void cerrarRecursos(Connection conn, PreparedStatement ps) {
        ConectarBD.cerrarConexion(conn, ps, null);
    }
    
    public abstract void crear(T entidad);
    public abstract T buscar(int id);
    public abstract List<T> listar();
    public abstract void actualizar(T entidad);
    public abstract void eliminar(int id);
}
