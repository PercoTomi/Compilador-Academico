package Simbolos;
public class SimboloCteFloat extends Simbolo {
    private float valor;

    public SimboloCteFloat(float valor, int id, String tipo, String lexema) {
        super(id, tipo, lexema);
        this.valor = valor;
    }

    public float getValor() {
        return valor;
    }
}