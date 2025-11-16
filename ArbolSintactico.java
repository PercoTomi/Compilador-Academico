import Simbolos.*;

public class ArbolSintactico {
    private Simbolo refSimbolo;
    private String valor; 
    private ArbolSintactico left;
    private ArbolSintactico right;

    // Constructor para nodos hoja
    public ArbolSintactico(Simbolo refSimbolo) {
        this.refSimbolo = refSimbolo;
        this.valor = null;
        this.left = null;
        this.right = null;
    }

    // Constructor para nodos intermedios
    public ArbolSintactico(String valor, ArbolSintactico left, ArbolSintactico right) {
        this.refSimbolo = null;
        this.valor = valor;
        this.left = left;
        this.right = right;
    }

    public Simbolo getRefSimbolo() {
        return refSimbolo;
    }

    public String getValor() {
        return valor;
    }

    public ArbolSintactico getLeft() {
        return left;
    }

    public ArbolSintactico getRight() {
        return right;
    }
}



