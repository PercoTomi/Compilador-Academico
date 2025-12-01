import Simbolos.*;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

public class Lexico {
    private String buffer; //buffer de entrada que contiene el código fuente
    private int posicionActual; // posición actual en el buffer
    private int idToken;
    private Map<String,Simbolo> tablaDeSimbolos; // referencia a la tabla de simbolos en la clase Compilador
    private int[][] matrizDeTransiciones; // matriz que indica a qué estado ir en cada transición
    private int[][] matrizIndicesAS; // matriz que indica qué acción semántica ejecutar en cada transición
    private Map<Integer, AccionSemantica> mapaAS; // mapa que asocia cada número de acción semántica con su implementación
    private int lineaActual; // contador de línea
    private Map<String,Integer> mapaTokens; // mapa que asocia literales y sus simbolos compuestos con sus IDs de token
    private Map<String,Integer> mapaColumnas; // mapa que guarda el indice de las columnas segun su caracter
    private ParserVal yylval; // simbolo referenciado que se devuelve al parser
    private boolean eof = false; // indica si se ha llegado al final del archivo
    private ArrayList<String> erroresLexicos;
    private ArrayList<String> warnings;
    private boolean verbose = false; // Variable para controlar los prints


    public Lexico(Map<String, Simbolo> tablaDeSimbolos, String path, ArrayList<String> erroresLexicos, ArrayList<String> warnings) {
        this.buffer = new String("");
        this.posicionActual = 0;
        this.tablaDeSimbolos = tablaDeSimbolos;
        this.erroresLexicos = erroresLexicos;
        this.warnings = warnings;
        cargarMatrices();
        cargarMapas();
        this.lineaActual = 1; // comenzamos en la línea 1
        this.cargarBuffer(path);
    }

    // a continuación el método cargarBuffer recibe un archivo txt como parámetro, lo convierte en array de strigs y lo asigna al buffer
    private boolean cargarBuffer(String path) {
        try {
            this.buffer = new String(Files.readAllBytes(Paths.get(path)));
            this.posicionActual = 0;
        } catch (IOException e) {
            e.printStackTrace();
            return false; // si hubo un error al cargar el archivo
        }
        return true; // si se cargó correctamente
    }
    
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public int yylex(ParserVal yylval) {
        this.yylval = yylval; // actualizar la referencia de yylval
        int estadoActual = 0; // resetear al estado inicial
        StringBuilder cadena = new StringBuilder("") ;
        idToken = 0; // en caso de que no se lea implica EOF

        while (estadoActual >= 0 && posicionActual < buffer.length()) {
            char c = buffer.charAt(posicionActual); // leer el caracter actual directamente
            String charLeido = String.valueOf(c);
            posicionActual++;

            // IDENTIFICACIÓN DE LA COLUMNA //
            int columna = mapaColumnas.getOrDefault(charLeido, -1); // obtener la columna correspondiente
            if (columna == -1) {
                if(Character.isUpperCase(c)) { // mayúsculas
                    columna = 0;
                } else if(Character.isLowerCase(c)) { // minúsculas
                    columna = 1;
                } else if(Character.isDigit(c)) { // digito
                    columna = 2;
                } else if ("(){}_,;".contains(charLeido)) { // Caracter especial
                    columna = 15;
                } else {
                    columna = 21; // otros
                }
            }

            // FIN IDENTIFICACIÓN DE LA COLUMNA //
            mapaAS.get(matrizIndicesAS[estadoActual][columna]).ejecutar(cadena, charLeido);
            estadoActual = matrizDeTransiciones[estadoActual][columna];
        }


        if (idToken == 0 & cadena.length() != 0) { //aca tengo que cambiar el isEmpty
            idToken = 256; 
        }
        if (posicionActual >= buffer.length() && idToken == 0) {
            if (eof) {
                idToken = 0; // EOF
            }
            else {
                idToken = 277;
                eof = true;
            }
        }
        if (idToken == 257) 
            if (verbose) System.out.println("||---------------- ANALIZADOR LÉXICO: TOKEN IDENTIFICADO - idToken: " + idToken + ", Categoria: ID, Lexema: " + cadena.toString() + " , Línea: " + lineaActual);
        else if (idToken == 258) 
            if (verbose) System.out.println("||---------------- ANALIZADOR LÉXICO: TOKEN IDENTIFICADO - idToken: " + idToken + ", Categoria: CTE, Lexema: " + cadena.toString() + " , Línea: " + lineaActual);
        else if (idToken == 0)
            if (verbose) System.out.println("||---------------- ANALIZADOR LÉXICO: FIN DE ARCHIVO ALCANZADO");
        else if (idToken != 277)
            if (verbose) System.out.println("||---------------- ANALIZADOR LÉXICO: TOKEN IDENTIFICADO - idToken: " + idToken + ", Categoria: " + cadena.toString() + " , Línea: " + lineaActual);
        return idToken;
    }

    private void cargarMatrices() {

        // Matriz de transiciones
        this.matrizDeTransiciones = new int[][] {
            { 1, 13, 7, -1, -1, -1, 6, 3, 4, 5, 0, 0, 0, 2, -1, -1, 8, 12, -1, 1, 1, -1, 14 },
            { 1, 1, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1, 1, 1, -1, -1 },
            { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
            { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
            { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
            { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
            { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
            { -1, -1, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 8, -1, -1, -1, -1, -1, -1 },
            { -1, -1, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 9, -1, -1 },
            { -1, -1, -1, -1, -1, 10, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
            { -1, -1, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
            { -1, -1, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
            { 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, -1, 12, 12, 12, 12, 12 },
            { 13, 13, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 13, 13, -1, -1 },
            { 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 0, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14 }
        };

        this.matrizIndicesAS = new int[][] {
            {0, 0, 0, 3, 3, 3, 0, 0, 0, 0, 11, 11, 11, 0, 100, 3, 0, 0, 100, 0, 0, 100,11},
            {0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1,1},
            {101, 101, 101, 101, 101, 101, 101, 6, 101, 101, 101, 101, 101, 101, 101, 101, 101, 101, 101, 101, 101, 101,101},
            {4, 4, 4, 4, 4, 4, 4, 6, 4, 4, 4, 4, 4, 4, 6, 4, 4, 4, 4, 4, 4, 4, 4},
            {4, 4, 4, 4, 4, 4, 4, 6, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4},
            {4, 4, 4, 4, 4, 4, 4, 6, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4},
            {4, 4, 4, 4, 4, 4, 4, 4, 4, 6, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4},
            {2, 2, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 2, 2, 2, 102, 2, 2},
            {8, 8, 0, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 10, 8, 8},
            {103, 103, 103, 103, 103, 0, 0, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103},
            {104, 104, 0, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104},
            {8, 8, 0, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8},
            {5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 9, 5, 5, 5, 5, 5},
            {0, 0, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 0, 0, 7, 7},
            {11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11, 11}
        };
    }

    private void cargarMapas() {

        // Mapa de Columnas
        this.mapaColumnas = new HashMap<>();
        mapaColumnas.put("*", 3);
        mapaColumnas.put("/", 4);
        mapaColumnas.put("+", 5);
        mapaColumnas.put("-", 6);
        mapaColumnas.put("=", 7);
        mapaColumnas.put("<", 8);
        mapaColumnas.put(">", 9);
        mapaColumnas.put(" ", 10);
        mapaColumnas.put("\r", 11);
        mapaColumnas.put("\n", 11);
        mapaColumnas.put("\t", 12); 
        mapaColumnas.put(":", 13);
        mapaColumnas.put("!", 14);
        mapaColumnas.put(".", 16);
        mapaColumnas.put("&", 17);
        mapaColumnas.put("%", 18);
        mapaColumnas.put("L", 19);
        mapaColumnas.put("F", 20);
        mapaColumnas.put("@", 22);

        // Mapa de Literales
        this.mapaTokens = new HashMap<>();
        mapaTokens.put("\n", 10);
        mapaTokens.put("\t", 11);
        mapaTokens.put(" ", 32);
        mapaTokens.put("!", 33);
        mapaTokens.put("&", 38);
        mapaTokens.put("(", 40);
        mapaTokens.put(")", 41);
        mapaTokens.put("*", 42);
        mapaTokens.put("+", 43);
        mapaTokens.put(",", 44);
        mapaTokens.put("-", 45);
        mapaTokens.put(".", 46);
        mapaTokens.put("/", 47);
        mapaTokens.put(":", 58);
        mapaTokens.put(";", 59);
        mapaTokens.put("<", 60);
        mapaTokens.put("=", 61);
        mapaTokens.put(">", 62);
        mapaTokens.put("_", 95);
        mapaTokens.put("{", 123);
        mapaTokens.put("}", 125);
        mapaTokens.put(":=", 259);
        mapaTokens.put(">=", 263);
        mapaTokens.put("<=", 264);
        mapaTokens.put("==", 265);
        mapaTokens.put("=!", 266);
        mapaTokens.put("->", 272);
        mapaTokens.put("if",260);
        mapaTokens.put("else",261);
        mapaTokens.put("endif",262);
        mapaTokens.put("long",267);
        mapaTokens.put("return",268);
        mapaTokens.put("do",269);
        mapaTokens.put("until",270);
        mapaTokens.put("cv",271);
        mapaTokens.put("print",273);
        mapaTokens.put("trunc",275);
        mapaTokens.put("lambda",276);

        // Mapa de Acciones Semánticas
        mapaAS = new HashMap<>();
        mapaAS.put(0, new AS0());
        mapaAS.put(1, new AS1());
        mapaAS.put(2, new AS2());
        mapaAS.put(3, new AS3());
        mapaAS.put(4, new AS4());
        mapaAS.put(5, new AS5());
        mapaAS.put(6, new AS6());
        mapaAS.put(7, new AS7());
        mapaAS.put(8, new AS8());
        mapaAS.put(9, new AS9());
        mapaAS.put(10, new AS10());
        mapaAS.put(11, new AS11());
        mapaAS.put(100, new AS100());
        mapaAS.put(101, new AS101());
        mapaAS.put(102, new AS102());
        mapaAS.put(103, new AS103());
        mapaAS.put(104, new AS104());
        mapaAS.put(105, new AS105());
        mapaAS.put(106, new AS106());
    }

    //------------------------------------------------------------------------------------------------//
    //---------------------------------SECCIÓN DE ACCIONES SEMÁNTICAS---------------------------------//
    //------------------------------------------------------------------------------------------------//

    public interface AccionSemantica {
        void ejecutar(StringBuilder cadena, String charLeido);
    }

    // AS0: Concatenar el caracter leído al string
    public class AS0 implements AccionSemantica {
        @Override
        public void ejecutar(StringBuilder cadena, String charLeido) {
            cadena.append(charLeido);
        }
    }

    // Chequeo de tope de caracteres de un identificador, inserción en la TS y devolución de ID del token
    public class AS1 implements AccionSemantica {
        @Override
        public void ejecutar(StringBuilder cadena, String charLeido) {
            if (cadena.length() > 20) {
                String cadenaCompleta = cadena.toString();
                cadena.setLength(20);
                warnings.add("||---------------- ANALIZADOR LÉXICO: Warning en línea " + lineaActual + " - El identificador '" + cadenaCompleta + "' excede el límite de 20 caracteres. Se truncará a: '" + cadena.toString() + "'");
            }
            posicionActual--;

            String lexemaOriginal = cadena.toString();
            
            // Verificación y conversión de minúsculas en el ID
            String lexema = lexemaOriginal.toUpperCase();
            if (!lexema.equals(lexemaOriginal)) {
                warnings.add("||---------------- ANALIZADOR LÉXICO: Warning en línea " + lineaActual + " - El identificador '" + lexemaOriginal + "' contenía minúsculas y ha sido convertido a : '" + lexema + "'");
            }
            
            // Verificar si el lexema no está en la tabla de símbolos
            if(!tablaDeSimbolos.containsKey(lexema)) {
                // Si no está, crear un nuevo símbolo long y agregarlo a la tabla
                Simbolo nuevoSimbolo = new SimboloLong("Long", "Identificador", lexema, 0L);
                tablaDeSimbolos.put(lexema, nuevoSimbolo);
                yylval.obj = nuevoSimbolo; // asignar el símbolo creado a yylval
            } else {
                // Si ya está, simplemente asignarlo a yylval
                yylval.obj = tablaDeSimbolos.get(lexema);
            }
            yylval.ival = lineaActual;
            idToken=257;
        }
    }

    // Contempla error por falta de sufijo, corrigiendo para que no se propague a error sintáctico.
    // Verifica que no se exceda el rango definido para Long. Si lo hace, entrega error.
    public class AS2 implements AccionSemantica {
        @Override
        public void ejecutar(StringBuilder cadena, String charLeido) {
            String lexema = cadena.toString();

            // Intenta parsear el número como long. Si supera el rango de Java, guarda el valor positivo máximo posible.
            Long cte;
            try {
                cte = Long.parseLong(lexema);
            } catch (NumberFormatException e) {
                cte = 2147483647L;
                mapaAS.get(105).ejecutar(cadena, charLeido);
            }
            
            if (!charLeido.equals("L")) {
                posicionActual--;
                charLeido = "L";
                erroresLexicos.add("Error léxico en linea " + lineaActual + ": Sufijo 'L' faltante en constante long \n");
            }
            
            lexema+= charLeido;
            cadena.append(charLeido);

            if (cte > 2147483648L) {
                mapaAS.get(105).ejecutar(cadena, charLeido);
                cte = 2147483647L;
            }
            
            // Verificar si el lexema no está en la tabla de símbolos
            if(!tablaDeSimbolos.containsKey(lexema)) {
                // Si no está, crear un nuevo símbolo long y agregarlo a la tabla
                Simbolo nuevoSimbolo = new SimboloLong("Long", "Constante", lexema, cte);
                tablaDeSimbolos.put(lexema, nuevoSimbolo);
                yylval.obj = nuevoSimbolo; // asignar el símbolo creado a yylval
            } else {
                // Si ya está, simplemente asignarlo a yylval
                yylval.obj = tablaDeSimbolos.get(lexema);
            }
            yylval.ival = lineaActual;
            idToken=258;
        }
    }

    // Reconocer caracter y entregar id de Token correspondiente
    public class AS3 implements AccionSemantica {
        @Override
        public void ejecutar(StringBuilder cadena, String charLeido) {
            idToken = mapaTokens.get(charLeido);
            cadena.append(charLeido);
            yylval.sval = charLeido;
            yylval.ival = lineaActual;
        }
    }

    // Retornar el último carácter leído. Reconocer caracter y entregar id de Token correspondiente
    public class AS4 implements AccionSemantica {
        @Override
        public void ejecutar(StringBuilder cadena, String charLeido) {
            posicionActual--;
            idToken = mapaTokens.get(cadena.toString());
            yylval.sval = cadena.toString();
            yylval.ival = lineaActual;
        }
    }

    // Si el caracter es new line, sumar a linea actual, sino, concatenarlo al string
    public class AS5 implements AccionSemantica {
        @Override
        public void ejecutar(StringBuilder cadena, String charLeido) {
            if (charLeido.equals("\n") || charLeido.equals("\r")) {
                mapaAS.get(11).ejecutar(cadena, charLeido); // se ejecuta la acción semántica que aumenta el contador de línea
            } else {
                cadena.append(charLeido);
            }
        }
    }

    // Concatenar el carácter leído a la cadena y entregar el token.
    public class AS6 implements AccionSemantica {
        @Override
        public void ejecutar(StringBuilder cadena, String charLeido) {
            cadena.append(charLeido);
            idToken = mapaTokens.get(cadena.toString());
            yylval.sval = cadena.toString();
            yylval.ival = lineaActual;
        }
    }

    // Maneja las mayúsculas insertadas en palabras reservadas, tirando error Léxico y pasándola a minúsculas.
    // Retornar el último caracter leido. Revisar si el string matchea con alguna palabra reservada. Si lo hace, devolver id correspondiente.
    public class AS7 implements AccionSemantica {
        @Override
        public void ejecutar(StringBuilder cadena, String charLeido) {
            // Verificación y conversión de mayúsculas en la palabra reservada
            String lexema = cadena.toString();

            cadena.setLength(0);
            cadena.append(lexema.toLowerCase());
            
            if (!lexema.equals(cadena.toString())) {
                warnings.add("||---------------- ANALIZADOR LÉXICO: Warning en línea " + lineaActual + " - La palabra reservada '" + lexema + "' contenía mayúsculas y ha sido convertido a : '" + cadena.toString() + "'");
            }


            posicionActual--;
            idToken = mapaTokens.getOrDefault(cadena.toString(), 256); // si no se encuentra en el mapa entonces es un error
            if (idToken == 256) {
                mapaAS.get(106).ejecutar(cadena, cadena.toString()); // se ejecuta el error "palabra reservada no válida"
            }
            else {
                yylval.sval = cadena.toString();
                yylval.ival = lineaActual;
            }
        }
    }

    // Retornar el último caracter leido. Distingue si es un punto o una Cte. Verificar que no se supere el rango definido para punto flotante. Si no lo hace, devolver id corrrespondiente.
    public class AS8 implements AccionSemantica {
        @Override
        public void ejecutar(StringBuilder cadena, String charLeido) {
            String lexema = cadena.toString();
            Float cte = 0f;
            posicionActual--;
            if (!lexema.equals(".")) {
                cte = Float.parseFloat(lexema);
                // sin cte es mayor a 3.40282347E38 o menor a 1.17549435E-38 error
                if (cte > 3.40282347E+38 || cte < 1.17549435E-38) {
                    mapaAS.get(105).ejecutar(cadena, charLeido);
                }
                else{
                    // Verificar si el lexema no está en la tabla de símbolos
                    if(!tablaDeSimbolos.containsKey(lexema)) {
                        // Si no está, crear un nuevo símbolo float y agregarlo a la tabla
                        // Antes de ingresar el lexema a la tabla se crea una var auxiliar en la cual se reemplaza la E por la F
                        String lexemaAux = lexema.replace("E", "F");
                        Simbolo nuevoSimbolo = new SimboloCteFloat("Float", "Constante", lexemaAux, cte);
                        tablaDeSimbolos.put(lexemaAux, nuevoSimbolo);
                        yylval.obj = nuevoSimbolo; // asignar el símbolo creado a yylval
                    } else {
                        // Si ya está, simplemente asignarlo a yylval
                        yylval.obj = tablaDeSimbolos.get(lexema);
                    }
                    idToken=258; // El Token es una constante
                }
            } else idToken = 46; // El Token es un punto
            yylval.ival = lineaActual;
        }
    }

    // Verificar si la cadena se encuentra en la tabla de simbolos. Si no está, darlo de alta. Devolver id correspondiente.
    public class AS9 implements AccionSemantica {
        @Override
        public void ejecutar(StringBuilder cadena, String charLeido) {
            cadena.append(charLeido);
            String lexema = cadena.toString();
            // Verificar si el lexema no está en la tabla de símbolos
            if(!tablaDeSimbolos.containsKey(lexema)) {
                    String valorSinAmpersand = lexema.substring(1, lexema.length() - 1); // Remover el ampersand inicial y final
                    // Si no está, crear un nuevo símbolo String y agregarlo a la tabla
                    Simbolo nuevoSimbolo = new SimboloCteString("String", "CadenaML", lexema, valorSinAmpersand);
                    tablaDeSimbolos.put(lexema, nuevoSimbolo);
                    yylval.obj = nuevoSimbolo; // asignar el símbolo creado a yylval
                } else {
                    // Si ya está, simplemente asignarlo a yylval
                    yylval.obj = tablaDeSimbolos.get(lexema);
                }
                yylval.ival = lineaActual;
                idToken=274;
        }
    }

    // Añade una "E" a la cadena
    public class AS10 implements AccionSemantica {
        @Override
        public void ejecutar(StringBuilder cadena, String charLeido) {
            cadena.append("E");
        }
    }

    // Si es un salto de línea, incrementar el contador de línea, sino, no hace nada
    public class AS11 implements AccionSemantica {
        @Override
        public void ejecutar(StringBuilder cadena, String charLeido) {
            if (charLeido.equals("\n")) {
                lineaActual++;
            }
        }
    }

    // AS100: Error: caractér no válido
    public class AS100 implements AccionSemantica {
        @Override
        public void ejecutar(StringBuilder cadena, String charLeido) {
            erroresLexicos.add("Error léxico en linea " + lineaActual + ": caractér no valido\n");
            idToken = 256;
        }
    }

    //AS101: Error: Se esperaba '='
    public class AS101 implements AccionSemantica {
        @Override
        public void ejecutar(StringBuilder cadena, String charLeido) {
            erroresLexicos.add("Error léxico en linea " + lineaActual + ": Se esperaba un '='\n");
            idToken = 256;
            posicionActual--;
        }
    }

    //AS102: Error: Se esperaba dígito, "." ó "L"
    public class AS102 implements AccionSemantica {
        @Override
        public void ejecutar(StringBuilder cadena, String charLeido) {
            posicionActual--;
            erroresLexicos.add("Error léxico en linea " + lineaActual + ": Se esperaba dígito, \".\" ó \"L\"\n");
        }
    }

    //AS103: Error: Se esperaba "+" ó "-"
    public class AS103 implements AccionSemantica {
        @Override
        public void ejecutar(StringBuilder cadena, String charLeido) {
            erroresLexicos.add("Error léxico en linea " + lineaActual + ": Se esperaba \"+\" ó \"-\"\n");
            idToken = 256;
            posicionActual--;
        }
    }

    //AS104: Error: Se esperaba un dígito
    public class AS104 implements AccionSemantica {
        @Override
        public void ejecutar(StringBuilder cadena, String charLeido) {
            erroresLexicos.add("Error léxico en linea " + lineaActual + ": Se esperaba un dígito\n");
            idToken = 256;
            posicionActual--;
        }
    }

    // AS105: Error: Constante fuera de rango
    public class AS105 implements AccionSemantica {
        @Override
        public void ejecutar(StringBuilder cadena, String charLeido) {
             erroresLexicos.add("Error léxico en linea " + lineaActual + ": Constante fuera de rango\n");
        }
    }

    // AS106: Error: Palabra reservada no válida
    public class AS106 implements AccionSemantica {
        @Override
        public void ejecutar(StringBuilder cadena, String charLeido) {
            erroresLexicos.add("Error léxico en linea " + lineaActual + ": Palabra reservada no válida\n");
            idToken = 256;
        }
    }
}