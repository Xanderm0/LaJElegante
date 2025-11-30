package entities.enums;

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
}
