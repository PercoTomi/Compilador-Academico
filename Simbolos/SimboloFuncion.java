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

    public Parametro getRefParametro(String nombreParametro) {
        for (Parametro parametro : listaParametros) {
            // Obtiene el nombre del parametro (primer campo antes del primer ':') y lo compara con nombreParametro
            String lexemaCompleto = parametro.getLexema();
            // X:P:F -> X
            String nombreParamActual = lexemaCompleto.substring(0, lexemaCompleto.indexOf(":"));
            if (nombreParamActual.equals(nombreParametro)) {
                return parametro;
            }
        }
        return null;
    }

    public int getCantidadParametros() {
        return listaParametros.size();
    }

    public int getPosParametro(String lexema) {
        for (int i = 0; i < listaParametros.size(); i++) {
            Parametro parametro = listaParametros.get(i);
            String lexemaBaseParam = parametro.getLexema().substring(0, parametro.getLexema().indexOf(":"));
            if (lexemaBaseParam.equals(lexema)) {
                return i;
            }
        }
        return -1; // No encontrado
    }
}