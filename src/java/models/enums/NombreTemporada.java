package models.enums;

public enum NombreTemporada {
    ALTA("alta"),
    MEDIA("media"),
    BAJA("baja");
    
    private final String valor;
    
    private NombreTemporada(String valor) {
        this.valor = valor;
    }
    
    public String getValor() {
        return valor;
    }
}
