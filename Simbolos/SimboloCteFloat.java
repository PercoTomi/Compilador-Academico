package Simbolos;
public class SimboloCteFloat extends Simbolo {
    private float valor;

    public SimboloCteFloat(int id, String tipo, String categoria, String lexema, float valor) {
        super(id, tipo, categoria, lexema);
        this.valor = valor;
    }

    public float getValor() {
        return valor;
    }

    public void setValor(float valor) {
        this.valor = valor;
    }
}