package Simbolos;

public class SimboloLong extends Simbolo {
    private long valor;

    public SimboloLong(String tipo, String categoria, String lexema, long valor) {
        super(tipo, categoria, lexema);
        this.valor = valor;
    }

    public long getValor() {
        return valor;
    }

    public void setValor(long valor) {
        this.valor = valor;
    }
}