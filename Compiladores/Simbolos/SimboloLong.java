package Simbolos;

public class SimboloLong extends Simbolo {
    private long valor;

    public SimboloLong(int id, String tipo, String lexema, long valor) {
        super(id, tipo, lexema);
        this.valor = valor;
    }

    public long getValor() {
        return valor;
    }

    public void setValor(long valor) {
        this.valor = valor;
    }
}