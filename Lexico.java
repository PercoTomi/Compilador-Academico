import Simbolos.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Lexico {
    private ArrayList<String> buffer; //buffer de entrada que contiene el código fuente
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

    public Lexico(Map<String, Simbolo> tablaDeSimbolos) {
        this.buffer = new ArrayList<>();
        this.posicionActual = 0;
        this.tablaDeSimbolos = tablaDeSimbolos;
        cargarMatrices();
        cargarMapas();
        this.lineaActual = 1; // comenzamos en la línea 1
    }

    // a continuación el método cargarBuffer recibe un archivo txt como parámetro, lo convierte en array de strigs y lo asigna al buffer
    public boolean cargarBuffer(String path) {
        try {
            String contenido = new String(Files.readAllBytes(Paths.get(path)));
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
    
    public int yylex(ParserVal yylval) {
        this.yylval = yylval; // actualizar la referencia de yylval
        int estadoActual = 0; // resetear al estado inicial
        StringBuilder cadena = new StringBuilder("") ;
        idToken = 0; // en caso de que no se lea implica EOF
        
        // RESETEA LOS VALORES DE YYLVAL
        yylval.dval = 0.0;
        yylval.sval = null;
        yylval.obj = null;

        while (estadoActual >= 0 && posicionActual < buffer.size()) {
            String charLeido = buffer.get(posicionActual); // leer el caracter actual
            posicionActual++;

            // IDENTIFICACIÓN DE LA COLUMNA //
            int columna = mapaColumnas.getOrDefault(charLeido, -1); // obtener la columna correspondiente
            if (charLeido.equals("\n")) { // salto de línea
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
            mapaAS.get(matrizIndicesAS[estadoActual][columna]).ejecutar(cadena, charLeido);
            estadoActual = matrizDeTransiciones[estadoActual][columna];
        }


        if (idToken == 0 & !cadena.isEmpty()) {
            idToken = 256;
        }
        if (posicionActual >= buffer.size() && idToken == 0) {
            if (eof) {
                idToken = 0; // EOF
            }
            else {
                idToken = 277;
                eof = true;
            }
        }
        return idToken;
    }

    private void cargarMatrices() {

        // Matriz de transiciones
        this.matrizDeTransiciones = new int[][] {
            { 1, 13, 7, -1, -1, -1, 6, 3, 4, 5, 0, 0, 0, 2, -1, -1, 8, 12, -1, 1, 1, -1 },
            { 1, -1, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1, 1, 1, -1 },
            { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
            { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
            { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
            { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
            { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
            { -1, -1, 7, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 8, -1, -1, -1, -1, -1 },
            { -1, -1, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 9, -1 },
            { -1, -1, -1, -1, -1, 10, 10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
            { -1, -1, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
            { -1, -1, 11, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 },
            { 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12, -1, 12, 12, 12, 12 },
            { -1, 13, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 }
        };

        this.matrizIndicesAS = new int[][] {
            {0, 0, 0, 3, 3, 3, 0, 0, 0, 0, 11, 11, 11, 0, 100, 3, 0, 11, 100, 0, 0, 100},
            {0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1},
            {101, 101, 101, 101, 101, 101, 101, 6, 101, 101, 101, 101, 101, 101, 101, 101, 101, 101, 101, 101, 101, 101},
            {4, 4, 4, 4, 4, 4, 4, 6, 4, 4, 4, 4, 4, 4, 6, 4, 4, 4, 4, 4, 4, 4},
            {4, 4, 4, 4, 4, 4, 4, 6, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4},
            {4, 4, 4, 4, 4, 4, 4, 6, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4},
            {4, 4, 4, 4, 4, 4, 4, 4, 4, 6, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4},
            {102, 102, 0, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 102, 0, 102, 102, 2, 102, 102},
            {8, 8, 0, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 10, 8},
            {103, 103, 103, 103, 103, 0, 0, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103, 103},
            {104, 104, 0, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104, 104},
            {8, 8, 0, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8},
            {5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 9, 5, 5, 5, 5},
            {7, 0, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7}
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
        mapaColumnas.put(" ", 10);  // espacio
        mapaColumnas.put("\r", 11); // cr: retorno de carro en Windows
        mapaColumnas.put("\n", 11); // nl: salto de línea en Windows
        mapaColumnas.put("\t", 12);  // tabulador
        mapaColumnas.put(":", 13);
        mapaColumnas.put("!", 14);
        mapaColumnas.put(".", 16);
        mapaColumnas.put("&", 17);
        mapaColumnas.put("%", 18);
        mapaColumnas.put("L", 19);
        mapaColumnas.put("F", 20);

        // Mapa de Literales
        this.mapaTokens = new HashMap<>();
        mapaTokens.put("\n", 10);  // nueva línea
        mapaTokens.put("\t", 11);  // tabulador
        mapaTokens.put(" ", 32);   // espacio
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
                cadena.setLength(20);
            }
            posicionActual--;
            // Verificar si el lexema no está en la tabla de símbolos
            String lexema = cadena.toString();
            if(!tablaDeSimbolos.containsKey(lexema)) {
                // Si no está, crear un nuevo símbolo long y agregarlo a la tabla
                Simbolo nuevoSimbolo = new SimboloLong(257, "Identificador", lexema, 0L);
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

    // Verifica que no se exceda el rango definido para Long, y si es así, devuleve el id de Token correspondiente
    public class AS2 implements AccionSemantica {
        @Override
        public void ejecutar(StringBuilder cadena, String charLeido) {
            String lexema = cadena.toString();
            Long cte = Long.parseLong(lexema);
            lexema+= charLeido;
            
            if (cte > 32678) {
                mapaAS.get(105).ejecutar(cadena, charLeido);
            }
            else{
                // Verificar si el lexema no está en la tabla de símbolos
                if(!tablaDeSimbolos.containsKey(lexema)) {
                    // Si no está, crear un nuevo símbolo long y agregarlo a la tabla
                    Simbolo nuevoSimbolo = new SimboloLong(258, "Constante", lexema, cte);
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
    }

    // Reconocer caracter y entregar id de Token correspondiente
    public class AS3 implements AccionSemantica {
        @Override
        public void ejecutar(StringBuilder cadena, String charLeido) {
            idToken = mapaTokens.get(charLeido);
            yylval.sval = charLeido; // SE USA PARA ESTO?
            yylval.ival = lineaActual; // CHUSMEAR POR QUE EN EL CODIGO ACTUAL NO SE LEE LINEA 2 LUEGO DE ESTA ACTUALIZACION DEL YYLVAL
        }
    }

    // Retornar el último carácter leído. Reconocer caracter y entregar id de Token correspondiente
    public class AS4 implements AccionSemantica {
        @Override
        public void ejecutar(StringBuilder cadena, String charLeido) {
            posicionActual--;
            mapaAS.get(3).ejecutar(cadena, cadena.toString()); // reutiliza AS3
        }
    }

    // Si el caracter es new line, ignorarlo, sino, concatenarlo al string
    public class AS5 implements AccionSemantica {
        @Override
        public void ejecutar(StringBuilder cadena, String charLeido) {
            if (!charLeido.equals("\n")) {
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
            yylval.sval = cadena.toString(); // SE USA PARA ESTO?
            yylval.ival = lineaActual;
        }
    }

    // Retornar el último caracter leido. Revisar si el string matchea con alguna palabra reservada. Si lo hace, devolver id correspondiente.
    public class AS7 implements AccionSemantica {
        @Override
        public void ejecutar(StringBuilder cadena, String charLeido) {
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
            Float cte = Float.parseFloat(lexema);
            lexema+= charLeido;
            posicionActual--;

            if (lexema != ".") {
                // sin cte es mayor a 3.40282347E38 o menor a 1.17549435E-38 error
                if (cte > 3.40282347E+38 || cte < 1.17549435E-38) {
                    mapaAS.get(105).ejecutar(cadena, charLeido);
                }
                else{
                    // Verificar si el lexema no está en la tabla de símbolos
                    if(!tablaDeSimbolos.containsKey(lexema)) {
                        // Si no está, crear un nuevo símbolo float y agregarlo a la tabla
                        Simbolo nuevoSimbolo = new SimboloCteFloat(258, "Constante", lexema, cte);
                        tablaDeSimbolos.put(lexema, nuevoSimbolo);
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
            String lexema = cadena.toString();
            // Verificar si el lexema no está en la tabla de símbolos
            if(!tablaDeSimbolos.containsKey(lexema)) {
                    // Si no está, crear un nuevo símbolo float y agregarlo a la tabla
                    Simbolo nuevoSimbolo = new SimboloCteString(275, "CadenaML", lexema, lexema);
                    tablaDeSimbolos.put(lexema, nuevoSimbolo);
                    yylval.obj = nuevoSimbolo; // asignar el símbolo creado a yylval
                } else {
                    // Si ya está, simplemente asignarlo a yylval
                    yylval.obj = tablaDeSimbolos.get(lexema);
                }
                yylval.ival = lineaActual;
                idToken=275;
        }
    }

    // Añade una "E" a la cadena
    public class AS10 implements AccionSemantica {
        @Override
        public void ejecutar(StringBuilder cadena, String charLeido) {
            cadena.append("E");
        }
    }

    // No hace nada
    public class AS11 implements AccionSemantica {
        @Override
        public void ejecutar(StringBuilder cadena, String charLeido) {
        }
    }

    // AS100: Error: caractér no válido
    public class AS100 implements AccionSemantica {
        @Override
        public void ejecutar(StringBuilder cadena, String charLeido) {
            System.out.println("Error en línea " + lineaActual + ": caractér no valido");
            idToken = 256;
        }
    }

    //AS101: Error: Se esperaba '='
    public class AS101 implements AccionSemantica {
        @Override
        public void ejecutar(StringBuilder cadena, String charLeido) {
            System.out.println("Error en línea " + lineaActual + ": Se esperaba un '='");
            idToken = 256;
            posicionActual--;
        }
    }

    //AS102: Error: Se esperaba dígito, "." ó "L"
    public class AS102 implements AccionSemantica {
        @Override
        public void ejecutar(StringBuilder cadena, String charLeido) {
            posicionActual--;
            System.out.println("Error en línea " + lineaActual + ": Se esperaba dígito, \".\" ó \"L\"");
            idToken = 256;
        }
    }

    //AS103: Error: Se esperaba "+" ó "-"
    public class AS103 implements AccionSemantica {
        @Override
        public void ejecutar(StringBuilder cadena, String charLeido) {
            System.out.println("Error en línea " + lineaActual + ": Se esperaba \"+\" ó \"-\"");
            idToken = 256;
            posicionActual--;
        }
    }

    //AS104: Error: Se esperaba un dígito
    public class AS104 implements AccionSemantica {
        @Override
        public void ejecutar(StringBuilder cadena, String charLeido) {
            System.out.println("Error en línea " + lineaActual + ": Se esperaba un dígito");
            idToken = 256;
            posicionActual--;
        }
    }

    // AS105: Error: Constante fuera de rango
    public class AS105 implements AccionSemantica {
        @Override
        public void ejecutar(StringBuilder cadena, String charLeido) {
            System.out.println("Error en línea " + lineaActual + ": Constante fuera de rango");
            idToken = 256;
        }
    }

    // AS106: Error: Palabra reservada no válida
    public class AS106 implements AccionSemantica {
        @Override
        public void ejecutar(StringBuilder cadena, String charLeido) {
            System.out.println("Error en línea " + lineaActual + ": Palabra reservada no válida");
            idToken = 256;
        }
    }
}
