package models.enums;

public enum NombreTipoHabitacion {
    FAMILIAR("familiar"),
    PAREJA("pareja"),
    BASICA("basica"),
    ESPECIAL("especial");
    
    private final String valor;
    
    private NombreTipoHabitacion(String valor) {
        this.valor = valor;
    }
    
    public String getValor() {
        return valor;
    }
    
    public static NombreTipoHabitacion fromString(String valor) {
        if (valor == null) return null;
        
        valor = valor.trim().toLowerCase();
        
        for (NombreTipoHabitacion tipo : NombreTipoHabitacion.values()) {
            if (tipo.valor.equals(valor)) {
                return tipo;
            }
        }
        
        throw new IllegalArgumentException("Tipo de habitación no válido: " + valor);
    }
}