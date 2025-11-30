package entities.enums;

public enum Estado {
    ACTIVO("activo"),
    INACTIVO("inactivo"),
    VIGENTE("vigente");
    
    private final String valor;
    
    private Estado(String valor) {
        this.valor = valor;
    }
    
    public String getValor() {
        return valor;
    }
}