package models;

import java.util.Objects;
import models.enums.Estado; // Asegúrate de tener tu enum Estado
import java.time.LocalDateTime;

public class TipoCliente extends ClaseBase {

    private int idTipoCliente;
    private String nombreTipo;
    private Estado estado;        // <--- NUEVO ATRIBUTO
    private LocalDateTime createdAt; // Para tus filtros por fecha

    public int getIdTipoCliente() { return idTipoCliente; }
    public void setIdTipoCliente(int idTipoCliente) { this.idTipoCliente = idTipoCliente; }

    public String getNombreTipo() { return nombreTipo; }
    public void setNombreTipo(String nombreTipo) { this.nombreTipo = nombreTipo; }

    public Estado getEstado() { return estado; }       // <--- GETTER
    public void setEstado(Estado estado) { this.estado = estado; } // <--- SETTER

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

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
