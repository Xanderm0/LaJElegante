package models;

import java.util.Objects;

public class TipoCliente extends ClaseBase {

    private int idTipoCliente;
    private String nombreTipo;

    public int getIdTipoCliente() {
        return idTipoCliente;
    }

    public void setIdTipoCliente(int idTipoCliente) {
        this.idTipoCliente = idTipoCliente;
    }

    public String getNombreTipo() {
        return nombreTipo;
    }

    public void setNombreTipo(String nombreTipo) {
        this.nombreTipo = nombreTipo;
    }

    // 🔐 CLAVE PARA JSF
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TipoCliente)) return false;
        TipoCliente that = (TipoCliente) o;
        return idTipoCliente == that.idTipoCliente;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idTipoCliente);
    }
}
