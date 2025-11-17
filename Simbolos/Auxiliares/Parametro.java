package Simbolos.Auxiliares;
import Simbolos.*;
    
public class Parametro {
    private Simbolo simbolo;
    private String pasaje;

    public Parametro(Simbolo simbolo, String pasaje) {
        this.simbolo = simbolo;
        this.pasaje = pasaje;
    }

    public Simbolo getSimbolo() {
        return simbolo;
    }

    public String getPasaje() {
        return pasaje;
    }
}