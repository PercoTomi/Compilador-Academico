package Simbolos;

import java.util.ArrayList;
import Simbolos.Auxiliares.Parametro;

public class SimboloFuncion extends Simbolo {
    private ArrayList<Parametro> listaParametros;

    public SimboloFuncion(String tipo, String categoria, String lexema, ArrayList<Parametro> parametros) {
        super(tipo, categoria, lexema);
        this.listaParametros = parametros;
    }

    public String getTipoRetorno() {
        return getTipo();
    }

}