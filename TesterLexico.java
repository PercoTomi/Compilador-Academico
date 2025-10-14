import java.util.HashMap;
import java.util.Map;
import Simbolos.*;


public class TesterLexico {
    public static void main(String[] args) {
        ParserVal parserVal = new ParserVal(0);
        Map<String,Simbolo> tablaSimbolos = new HashMap<>();

        Lexico lexico = new Lexico(tablaSimbolos);
        // pasar archivo de entrada "test.txt" a String y pasarselo a lexico en cargarBuffer
        String test = "test.txt";

        if (lexico.cargarBuffer(test)) {
            System.out.println("ÉTAPA DE ANÁLISIS LÉXICO:");
            int token = 10000; // valor inicial cualquiera distinto de 0
            while (token != 0) {
                token = lexico.yylex(parserVal);
                if (parserVal.obj != null)
                    System.out.println("||------------------------ TOKEN IDENTIFICADO - ID: " + token + ", Lexema: " + ((Simbolo)parserVal.obj).getLexema() + ", Línea: " + parserVal.ival + " ------------------------||");
                else
                    System.out.println("||------------------------ TOKEN IDENTIFICADO - ID: " + token + ", Línea: " + parserVal.ival + " ------------------------||");
            }
        }
        else
            System.out.println("Error al cargar el buffer");
    }
}