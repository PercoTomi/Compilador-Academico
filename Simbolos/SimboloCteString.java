package Simbolos;

public class SimboloCteString extends Simbolo {
    private String valor;

    public SimboloCteString(int id, String tipo, String lexema, String valor) {
        super(id, tipo, lexema);
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

}
