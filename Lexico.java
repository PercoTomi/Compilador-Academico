import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Lexico {
    private ArrayList<String> buffer; //buffer de entrada que contiene el código fuente
    private int posicionActual; // posición actual en el buffer
    private ArrayList<Token> listaDeTokens;
    private Map<String,Token> tablaDeSimbolos; // referencia a la tabla de simbolos en la clase Compilador
    private int[][] matrizDeTransiciones; // matriz que indica a qué estado ir en cada transición
    private int[][] matrizIndicesAS; // matriz que indica qué acción semántica ejecutar en cada transición
    private Map<Integer, AccionSemantica> mapaAS; // mapa que asocia cada número de acción semántica con su implementación
    private int lineaActual; // para el manejo de errores
    private Map<String,Integer> mapaLiterales;
    private Token yylval; // simbolo referenciado que se devuelve al parser

    public Lexico(Map<String, Token> tablaDeSimbolos) {
        this.buffer = new ArrayList<>();
        this.posicionActual = 0;
        this.listaDeTokens = new ArrayList<>();
        this.tablaDeSimbolos = tablaDeSimbolos;
        cargarListaDeTokens();
        cargarMatrices();
        cargarMapas();
        this.lineaActual = 1; // comenzamos en la línea 1
    }

    // a continuación el método cargarBuffer recibe un archivo txt como parámetro, lo convierte en array de strigs y lo asigna al buffer
    public boolean cargarBuffer(String archivo) {
        try {
            String contenido = new String(Files.readAllBytes(Paths.get(archivo)));
            for (char c : contenido.toCharArray()) {
                buffer.add(String.valueOf(c)); // cada caracter como String
            }
            this.posicionActual = 0;
        } catch (IOException e) {
            e.printStackTrace();
            return false; // si hubo un error al cargar el archivo
        }
        return true; // si se cargó correctamente
    }
    
    public int yylex() {
        int estadoActual = 0; // resetear al estado inicial
        StringBuilder cadena = new StringBuilder("") ;
        int idToken = -1; // id por defecto (error léxico)
        this.yylval=null;
        while (estadoActual >= 0 && posicionActual < buffer.size()) {
            String charLeido = buffer.get(posicionActual); // leer el caracter actual
            posicionActual++;

            // IDENTIFICACIÓN DE LA COLUMNA //
            int columna = mapaLiterales.getOrDefault(charLeido, -1); // obtener la columna correspondiente
            if (columna == 11) { // salto de línea
                lineaActual++;
            } else if (columna == -1) {
                if(Character.isUpperCase(charLeido.charAt(0))) { // mayúsculas
                    columna = 0;
                } else if(Character.isLowerCase(charLeido.charAt(0))) { // minúsculas
                    columna = 1;
                } else if(Character.isDigit(charLeido.charAt(0))) { // digito
                    columna = 2;
                } else if ("(){}_,;".contains(charLeido)) { // Caracter especial
                    columna = 15;
                } else {
                    columna = 21; // otros
                }
            }

            // FIN IDENTIFICACIÓN DE LA COLUMNA //
            
            mapaAS.get(matrizIndicesAS[estadoActual][columna]).ejecutar(cadena, charLeido, idToken);
            estadoActual = matrizDeTransiciones[estadoActual][columna];
        }
        return idToken;
    }

    private void cargarListaDeTokens() {
 
    }

    private void cargarMatrices() {

        // Matriz de transiciones
        this.matrizDeTransiciones = new int[][] {
            { 1, 13, 7, -1, -1, -1, 6, 3, 4, 5, 0, 0, 0, 2, -2, -1, 8, 12, -2, 1, 1, -2 },
            { 1, -1, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1, -1, -1, -1 },
            { -2, -2, -2, -2, -2, -2, -2, -1, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2 },
            { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
            { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
            { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
            { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
            { -2, -2, 7, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, 8, -2, -2, -1, -2, -2 },
            { -1, -1, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 9, -1 },
            { -2, -2, -2, -2, -2, 10, 10, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2 },
            { -2, -2, 11, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2 },
            { -1, -1, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
            { 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, -1, 12, 12, 12, 12 },
            { -1, 13, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }
        };

        this.matrizIndicesAS = new int[][] {
            {0, 0, 0, 3, 3, 3, 0, 0, 0, 0, 9, 9, 9, 0, 100, 4, 0, 0, 100, 0, 0, 100},
            {0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1},
            {101, 101, 101, 101, 101, 101, 101, 7, 101, 101, 101, 101, 101, 101, 101, 101, 101, 101, 101, 101, 101, 101},
            {4, 4, 4, 4, 4, 4, 4, 6, 4, 4, 4, 4, 4, 4, 6, 4, 4, 4, 4, 4, 4, 4},
            {4, 4, 4, 4, 4, 4, 4, 6, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4},
            {4, 4, 4, 4, 4, 4, 4, 6, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4},
            {4, 4, 4, 4, 4, 4, 4, 4, 4, 6, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4},
            {102, 102, 1, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 0, 102, 102, 2, 102, 102},
            {8, 8, 0, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 0, 8},
            {103, 103, 103, 103, 103, 1, 1, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103},
            {104, 104, 0, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104},
            {8, 8, 0, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8},
            {5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 6, 5, 5, 5, 5},
            {7, 0, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7}
        };

    }

    private void cargarMapas() {

        // Mapa de Literales
        this.mapaLiterales = new HashMap<>();
        mapaLiterales.put("*", 3);
        mapaLiterales.put("/", 4);
        mapaLiterales.put("+", 5);
        mapaLiterales.put("-", 6);
        mapaLiterales.put("=", 7);
        mapaLiterales.put("<", 8);
        mapaLiterales.put(">", 9);
        mapaLiterales.put("b", 10);
        mapaLiterales.put("\n", 11); // nl: salto de línea en Windows
        mapaLiterales.put("\t", 12);  // tabulador
        mapaLiterales.put(":", 13);
        mapaLiterales.put("!", 14);
        mapaLiterales.put(".", 16);
        mapaLiterales.put("&", 17);
        mapaLiterales.put("%", 18);
        mapaLiterales.put("L", 19);
        mapaLiterales.put("F", 20);

        // Mapa de Acciones Semánticas
    }

    //------------------------------------------------------------------------------------------------//
    //---------------------------------SECCIÓN DE ACCIONES SEMÁNTICAS---------------------------------//
    //------------------------------------------------------------------------------------------------//

    public interface AccionSemantica {
        void ejecutar(StringBuilder cadena, String charLeido, int idToken);
    }

    // AS0: Concatenar el caracter leído al string
    public class AS0 implements AccionSemantica {
        @Override
        public void ejecutar(StringBuilder cadena, String charLeido, int idToken) {
            cadena.append(charLeido);
        }
    }

    // Chequeo de tope de caracteres de un identificador, inserción en la TS y devolución de ID del token
    public class AS1 implements AccionSemantica {
        @Override
        public void ejecutar(StringBuilder cadena, String charLeido, int idToken) {
            // No hace nada
            
        }
    }

    // Verifica que no se exceda el rango definido para Long, y si es así, devuleve el id de Token correspondiente
    public class AS2 implements AccionSemantica {
        @Override
        public void ejecutar(StringBuilder cadena, String charLeido, int idToken) {
            // No hace nada 
            
        }
    }

    // Reconocer caracter y entregar id de Token correspondiente
    public class AS3 implements AccionSemantica {
        @Override
        public void ejecutar(StringBuilder cadena, String charLeido, int idToken) {
            // No hace nada
        }
    
    }

    // Retornar el último carácter leído. Reconocer caracter y entregar id de Token correspondiente
    public class AS4 implements AccionSemantica {
        @Override
        public void ejecutar(StringBuilder cadena, String charLeido, int idToken) {
            // No hace nada
        }
    }
    
    
    // Si el caracter es new line, ignorarlo, sino, concatenarlo al string
    public class AS5 implements AccionSemantica {
        @Override
        public void ejecutar(StringBuilder cadena, String charLeido, int idToken) {
        // No hace nada
        }
    }
    
    // Concatenar el carácter leído a la cadena y entregar el token.
    public class AS6 implements AccionSemantica {
        @Override
        public void ejecutar(StringBuilder cadena, String charLeido, int idToken) {
            // No hace nada
        }
    }

    // Retornar el último caracter leido. Revisar si el string matchea con alguna palabra reservada. Si lo hace, devolver id correspondiente.
    public class AS7 implements AccionSemantica {
        @Override
        public void ejecutar(StringBuilder cadena, String charLeido, int idToken) {
            // No hace nada
        }
    }

    // Retornar el último caracter leido. Verificar que no se supere el rango definido para punto flotante. Si no lo hace, devolver id corrrespondiente.
    public class AS8 implements AccionSemantica {
        @Override
        public void ejecutar(StringBuilder cadena, String charLeido, int idToken) {
            // No hace nada
        }
    }
     
    // No hace nada
    public class AS9 implements AccionSemantica {
        @Override
        public void ejecutar(StringBuilder cadena, String charLeido, int idToken) {
        }
    }

    // AS100: Error: caractér no válido
    public class AS100 implements AccionSemantica {
        @Override
        public void ejecutar(StringBuilder cadena, String charLeido, int idToken) {
            System.out.println("Error en línea " + lineaActual + ": caractér no valido");
        }
    }

    //AS101: Error: Se esperaba '='
    public class AS101 implements AccionSemantica {
        @Override
        public void ejecutar(StringBuilder cadena, String charLeido, int idToken) {
            System.out.println("Error en línea " + lineaActual + ": Se esperaba un '='");
        }
    }

    //AS102: Error: Se esperaba dígito, "." ó "L"
    public class AS102 implements AccionSemantica {
        @Override
        public void ejecutar(StringBuilder cadena, String charLeido, int idToken) {
            System.out.println("Error en línea " + lineaActual + ": Se esperaba dígito, \".\" ó \"L\"");
        }
    }

    //AS103: Error: Se esperaba "+" ó "-"
    public class AS103 implements AccionSemantica {
        @Override
        public void ejecutar(StringBuilder cadena, String charLeido, int idToken) {
            System.out.println("Error en línea " + lineaActual + ": Se esperaba \"+\" ó \"-\"");
        }
    }   

    //AS104: Error: Se esperaba un dígito
    public class AS104 implements AccionSemantica {
        @Override
        public void ejecutar(StringBuilder cadena, String charLeido, int idToken) {
            System.out.println("Error en línea " + lineaActual + ": Se esperaba un dígito");
        }
    }
}