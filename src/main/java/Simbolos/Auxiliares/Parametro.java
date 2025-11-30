package Simbolos.Auxiliares;
import Simbolos.*;
    
public class Parametro {
    private Simbolo simbolo;
    private String pasaje;
    private String lexema;
    private String tipo;

    public Parametro(Simbolo simbolo, String pasaje) {
        this.simbolo = simbolo;
        this.pasaje = pasaje;
        this.lexema = simbolo.getLexema();
        this.tipo = simbolo.getTipo();
    }

    public Simbolo getSimbolo() {
        return simbolo;
    }

    public String getPasaje() {
        return pasaje;
    }

    public String getLexema() {
        return lexema;
    }

    public String getTipo() {
        return tipo;
    }
}