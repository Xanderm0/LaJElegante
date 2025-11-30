package entities.enums;

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
}
