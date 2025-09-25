package Simbolos;

public class SimboloCteString extends Simbolo {
    private String valor;

    public SimboloCteString(String valor, int id, String tipo, String lexema) {
        super(id, tipo, lexema);
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

}
