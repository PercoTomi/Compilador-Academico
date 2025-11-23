package Simbolos.Auxiliares;
import Simbolos.*;
    
public class Parametro {
    private Simbolo simbolo;
    private String pasaje;
    private String lexema;

    public Parametro(Simbolo simbolo, String pasaje) {
        this.simbolo = simbolo;
        this.pasaje = pasaje;
        this.lexema = simbolo.getLexema();
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
}