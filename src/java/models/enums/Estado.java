package models.enums;

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
    
    public static Estado fromString(String valor) {
        if (valor == null) return null;
        
        valor = valor.trim().toLowerCase();
        
        for (Estado estado : Estado.values()) {
            if (estado.valor.equals(valor)) {
                return estado;
            }
        }
        
        throw new IllegalArgumentException("Estado no válido: " + valor);
    }
}