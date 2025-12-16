import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Simbolos.Simbolo;
import Simbolos.SimboloCteFloat;
import Simbolos.SimboloCteString;
import Simbolos.SimboloFuncion;
import Simbolos.SimboloLong;

public class Compilador {
    // Rango de enteros de 32 bits con signo
    public static final long INT32_MAX = 2147483647L;
    public static final long INT32_MIN = -2147483648L;
    
    // Rango de punto flotante de precisión simple (float)
    public static final float FLOAT_MAX = 3.40282347E+38f;
    public static final float FLOAT_MIN = 1.17549435E-38f;
    
    // Límite de caracteres para identificadores
    public static final int MAX_ID_LENGTH = 20;

    public static void main(String[] args) throws Exception {
        /*if (args.length == 0) {
            System.err.println("Error: Debes proporcionar la ruta del archivo como argumento.");
            System.err.println("Uso: java -jar Compilador.jar <ruta_archivo> [--verbose|-v]");
            System.exit(1);
        }

        String path = args[0];
        
        // Verificar si el usuario quiere modo verbose
        boolean verbose = false;
        if (args.length >= 2) {
            if (args[1].equals("--verbose") || args[1].equals("-v")) {
                verbose = true;
            } else {
                System.err.println("Error: Argumento no reconocido: " + args[1]);
                System.err.println("Uso: java -jar Compilador.jar <ruta_archivo> [--verbose|-v]");
                System.exit(1);
            }
        }*/
       String path = "test.txt";

        Map<String, Simbolo> tablaSimbolos = new HashMap<>();
        ArbolSintactico arbolSintactico = new ArbolSintactico("Vacio", null, null);
        Map<String, ArbolSintactico> mapaFunciones = new HashMap<>();
        int funcLambdaDeclaradas = 0;
        
        if (Files.size(Paths.get(path)) == 0) {
            System.out.println("El archivo está vacío");
            System.exit(1);
        }
        
        // TRABAJO DE ARCHIVOS

        // Inicializacion del archivo que contendrá el código assembler
        Path archivoAssembler = null;

        // Inicialización de los archivos de errores
        Path archivoLexico = null;
        Path archivoSintactico = null;
        Path archivoSemantico = null;
        Path archivoWarnings = null;

        ArrayList<String> erroresLexicos;
        ArrayList<String> erroresSintacticos;
        ArrayList<String> erroresSemanticos;
        ArrayList<String> warnings;

        try {
            // Convertir a Path absoluto
            Path pathAbsoluto = Paths.get(path).toAbsolutePath().normalize();
            
            // Conseguir el directorio de salida y también el nombre del archivo
            String directorioSalida = pathAbsoluto.getParent().toString();
            String nombreArchivo = pathAbsoluto.getFileName().toString();
            
            // conseguir nombre sin extensión del archivo
            int lastDot = nombreArchivo.lastIndexOf('.');
            String nombreBaseArchivoFuente = (lastDot > 0) ? nombreArchivo.substring(0, lastDot) : nombreArchivo;
            
            System.out.println("Archivo cargado desde: " + pathAbsoluto);
            System.out.println("Archivos de salida se guardarán en: " + directorioSalida);

            // definir rutas de salida para los archivos de análisis léxico y sintáctico
            archivoLexico = Paths.get(directorioSalida, nombreBaseArchivoFuente + "-lexico.txt");
            archivoSintactico = Paths.get(directorioSalida, nombreBaseArchivoFuente + "-sintactico.txt");
            archivoSemantico = Paths.get(directorioSalida, nombreBaseArchivoFuente + "-semantico.txt");
            archivoWarnings = Paths.get(directorioSalida, nombreBaseArchivoFuente + "-warnings.txt");
            archivoAssembler = Paths.get(directorioSalida, "salida.wat");

            // Crear los archivos o sobrescribir si ya existen
            Files.write(archivoLexico, new byte[0]);
            Files.write(archivoSintactico, new byte[0]);
            Files.write(archivoSemantico, new byte[0]);
            Files.write(archivoWarnings, new byte[0]);
            Files.write(archivoAssembler, new byte[0]);
            
            System.out.println("Archivos creados:");
            System.out.println("  - " + archivoLexico);
            System.out.println("  - " + archivoSintactico);
            System.out.println("  - " + archivoSemantico);
            System.out.println("  - " + archivoWarnings);
            System.out.println("  - " + archivoAssembler);

        } catch (IOException e) {
            e.printStackTrace();
        }
        
        Parser parser = new Parser(tablaSimbolos, path, arbolSintactico, mapaFunciones);
        boolean verbose = true;
        if (verbose) {
            parser.setVerbose(true);
        }
        

        System.out.println(parser.yyparse());

        erroresLexicos = parser.getErroresLexicos();
        erroresSintacticos = parser.getErroresSintacticos();
        erroresSemanticos = parser.getErroresSemanticos();
        warnings = parser.getWarnings();
        funcLambdaDeclaradas = parser.getFuncLambdaDeclaradas();

        // Imprimir todos los errores léxicos, luego los sintácticos y por último cargar ambos en los archivos correspondientes
        for (String error : erroresLexicos) {
            System.out.print(error);
        }
        for (String error : erroresSintacticos) {
            System.out.println(error);
        }
        for (String error : erroresSemanticos) {
            System.out.println(error);
        }
        for (String warning : warnings) {
            System.out.println(warning);
        }
        Files.write(archivoLexico, erroresLexicos);
        Files.write(archivoSintactico, erroresSintacticos);
        Files.write(archivoSemantico, erroresSemanticos);
        Files.write(archivoWarnings, warnings);

        System.out.println(System.lineSeparator() + "TABLA DE SÍMBOLOS:");

        for (String key : tablaSimbolos.keySet()) {
            Simbolo simbolo = tablaSimbolos.get(key);
            if (simbolo instanceof SimboloCteFloat)
                System.out.println("Lexema: " + key + ", Valor: " + ((SimboloCteFloat)simbolo).getValor() + ", Tipo: " + simbolo.getTipo() + ", Categoria: " + simbolo.getCategoria());
            else if (simbolo instanceof SimboloCteString)
                System.out.println("Lexema: " + key + ", Valor: " + ((SimboloCteString)simbolo).getValor() + ", Tipo: " + simbolo.getTipo() + ", Categoria: " + simbolo.getCategoria());
            else if (simbolo instanceof SimboloLong)
                System.out.println("Lexema: " + key + ", Valor: " + ((SimboloLong)simbolo).getValor() + ", Tipo: " + simbolo.getTipo() + ", Categoria: " + simbolo.getCategoria());
            else if (simbolo instanceof SimboloFuncion)
                System.out.println("Lexema: " + key + ", Tipo de retorno: " + simbolo.getTipo() + ", Categoria: " + simbolo.getCategoria());
            }
            
        // IMPRESION DEL ARBOL SINTACTICO

        System.out.println(System.lineSeparator() + "ÁRBOL SINTÁCTICO:");

        if (!arbolSintactico.getCategoria().equals("Vacio")) {
            arbolSintactico.imprimirArbol();
        } else if (!erroresLexicos.isEmpty() || !erroresSintacticos.isEmpty() || !erroresSemanticos.isEmpty()) {
            System.out.println("No se pudo construir el árbol sintáctico debido a errores.");
        } else {
            System.out.println("El árbol sintáctico está vacío.");
        }

        // IMPRESION DE TODAS LAS FUNCIONES EN mapaFunciones

        if (verbose) {
            System.out.println(System.lineSeparator() + "MAPA DE FUNCIONES:");
    
            for (String key : mapaFunciones.keySet()) {
                System.out.println("Función: " + key);
                System.out.println("Árbol Sintáctico de la función:");
                // imprime el arbol de esa key
                mapaFunciones.get(key).imprimirArbol();
            }
        }


        // GENERACION DE CÓDIGO ASSEMBLER
        
        GeneradorDeCodigo generador = new GeneradorDeCodigo(tablaSimbolos, mapaFunciones, arbolSintactico, archivoAssembler, funcLambdaDeclaradas);
        String codigoAssembler;
        
        if (!arbolSintactico.getCategoria().equals("Vacio")) {
            codigoAssembler = generador.generarCodigo();
            //Crea el archivo
            Files.write(archivoAssembler, codigoAssembler.getBytes());
        } else {
            // Limpiar el archivo si el árbol está vacío (sobrescribir con contenido vacío)
            System.out.println("No se generó código assembler porque el árbol sintáctico está vacío.");
        }
    }
}