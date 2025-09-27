import Simbolos.*;
import java.util.HashMap;
import java.util.Map;

public class Compilador {
    public static void main(String[] args) throws Exception {
    Map<String, Simbolo> tablaSimbolos = new HashMap<String, Simbolo>();
    String path = "test.txt";
    Parser parser = new Parser(tablaSimbolos, path);
    System.out.println(parser.yyparse());
    }
}