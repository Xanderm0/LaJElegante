package entities.enums;

public enum Rol {
    RECEPCIONISTA("recepcionista"),
    ASISTENTE_ADMINISTRATIVO("asistente_administrativo"),
    GERENTE_ALIMENTOS("gerente_alimentos"),
    GERENTE_HABITACIONES("gerente_habitaciones"),
    GERENTE_GENERAL("gerente_general"),
    ADMINISTRADOR("administrador");
    
    private final String valor;
    
    private Rol(String valor) {
        this.valor = valor;
    }
    
    public String getValor() {
        return valor;
    }
}