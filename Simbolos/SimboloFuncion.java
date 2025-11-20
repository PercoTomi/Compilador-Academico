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

    public boolean existeParametro(String nombreParametro) {
        for (Parametro parametro : listaParametros) {
            // Obtiene el nombre del parametro (primer campo antes del primer ':') y lo compara con nombreParametro
            String lexemaCompleto = parametro.getSimbolo().getLexema();
            String nombreParamActual = lexemaCompleto.substring(0, lexemaCompleto.indexOf(":"));
            if (nombreParamActual.equals(nombreParametro)) {
                return true;
            }
        }
        return false;
    }
}