package models.enums;

public enum EstadoReserva {
    PENDIENTE("pendiente"),
    CONFIRMADA("confirmada"),
    CANCELADA("cancelada");
    
    private final String valor;
    
    private EstadoReserva(String valor) {
        this.valor = valor;
    }
    
    public String getValor() {
        return valor;
    }
    
    public static EstadoReserva fromString(String valor) {
        if (valor == null) return null;
        
        valor = valor.trim().toLowerCase();
        
        for (EstadoReserva estadoReserva : EstadoReserva.values()) {
            if (estadoReserva.valor.equals(valor)) {
                return estadoReserva;
            }
        }
        
        throw new IllegalArgumentException("Estado de Reserva no válido: " + valor);
    }
}