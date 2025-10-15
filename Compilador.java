import Simbolos.*;
import java.util.HashMap;
import java.util.Map;

public class Compilador {
    public static void main(String[] args) throws Exception {
    Map<String, Simbolo> tablaSimbolos = new HashMap<String, Simbolo>();
    String path = "test.txt";
    Parser parser = new Parser(tablaSimbolos, path);
    System.out.println(parser.yyparse());

    System.out.println("\n" + "TABLA DE SÍMBOLOS:");

    //ELIMINACION DE SIMBOLOS CON RANGOS NO VALIDOS

    tablaSimbolos.remove("2147483648L");

    for (String key : tablaSimbolos.keySet()) {
        Simbolo simbolo = tablaSimbolos.get(key);
        if (simbolo instanceof SimboloCteFloat)
            System.out.println("Lexema: " + key + ", Valor: " + ((SimboloCteFloat)simbolo).getValor() + ", Tipo: " + simbolo.getTipo());
        else if (simbolo instanceof SimboloCteString)
            System.out.println("Lexema: " + key + ", Valor: " + ((SimboloCteString)simbolo).getValor() + ", Tipo: " + simbolo.getTipo());
        else if (simbolo instanceof SimboloLong)
            System.out.println("Lexema: " + key + ", Valor: " + ((SimboloLong)simbolo).getValor() + ", Tipo: " + simbolo.getTipo());
        }
    }

}