package Simbolos;

public class Simbolo {
    private String tipo;
    private String categoria;
    private String lexema;
    
    public Simbolo(String tipo, String categoria, String lexema) {
        this.tipo = tipo;
        this.categoria = categoria;
        this.lexema = lexema;
    }


    public String getTipo() {
        return tipo;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getLexema() {
        return lexema;
    }

    public void setLexema(String lexema) {
        this.lexema = lexema;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
}