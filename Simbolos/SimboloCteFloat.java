package Simbolos;

public class SimboloCteFloat extends Simbolo {
    private float valor;

    public SimboloCteFloat(String tipo, String categoria, String lexema, float valor) {
        super(tipo, categoria, lexema);
        this.valor = valor;
    }

    public float getValor() {
        return valor;
    }

    public void setValor(float valor) {
        this.valor = valor;
    }
}