package Simbolos;

public class SimboloCteString extends Simbolo {
    private String valor;

    public SimboloCteString(String tipo, String categoria, String lexema, String valor) {
        super(tipo, categoria, lexema);
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

}
