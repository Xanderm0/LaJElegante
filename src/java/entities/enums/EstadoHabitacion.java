package entities.enums;

public enum EstadoHabitacion {
    EN_SERVICIO("en servicio"),
    MANTENIMIENTO("mantenimiento"),
    OCUPADA("ocupada");
    
    private final String valor;
    
    private EstadoHabitacion(String valor) {
        this.valor = valor;
    }
    
    public String getValor() {
        return valor;
    }
}
