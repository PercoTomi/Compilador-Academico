package Simbolos;

public class Simbolo {
    private int id;
    private String tipo;
    private String lexema;
    
    public Simbolo(int id, String tipo, String lexema) {
        this.id = id;
        this.tipo = tipo;
        this.lexema = lexema;
    }

    public int getId() {
        return id;
    }

    public String getTipo() {
        return tipo;
    }

    public String getLexema() {
        return lexema;
    }

    public void setLexema(String lexema) {
        this.lexema = lexema;
    }
}