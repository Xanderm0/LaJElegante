package entities.enums;

public enum Saludo {
    MR("mr"),
    MS("ms"),
    MX("mx");
    
    private final String valor;
    
    private Saludo(String valor) {
        this.valor = valor;
    }
    
    public String getValor() {
        return valor;
    }
}