package models.enums;

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
    
    public static EstadoHabitacion fromString(String valor) {
        if (valor == null) return null;
        
        valor = valor.trim().toLowerCase();
        
        for (EstadoHabitacion estadoHabitacion : EstadoHabitacion.values()) {
            if (estadoHabitacion.valor.equals(valor)) {
                return estadoHabitacion;
            }
        }
        
        throw new IllegalArgumentException("Estado de habitacion no válido: " + valor);
    }
}