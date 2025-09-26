package Simbolos;
public class SimboloCteFloat extends Simbolo {
    private float valor;

    public SimboloCteFloat(int id, String tipo, String lexema, float valor) {
        super(id, tipo, lexema);
        this.valor = valor;
    }

    public float getValor() {
        return valor;
    }
}