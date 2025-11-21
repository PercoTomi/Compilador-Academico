import Simbolos.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Compilador {
    public static void main(String[] args) throws Exception {
        /*if (args.length == 0) {
            System.err.println("Error: Debes proporcionar la ruta del archivo como argumento.");
            System.err.println("Uso: java -jar Compilador.jar <ruta_archivo>");
            System.exit(1);
        }*/

        Map<String, Simbolo> tablaSimbolos = new HashMap<>();
        ArbolSintactico arbolSintactico = null;

        String path = "test.txt"; // QUITAR "COMPILADORES/"
        
        if (Files.size(Paths.get(path)) == 0) {
            System.err.println("Error: El archivo está vacío");
            System.exit(1);
        }
        
        // TRABAJO DE ARCHIVOS

        // Inicialización de los archivos de errores
        Path archivoLexico = null;
        Path archivoSintactico = null;
        Path archivoSemantico = null;
        Path archivoWarnings = null;

        ArrayList<String> erroresLexicos = new ArrayList<>();
        ArrayList<String> erroresSintacticos = new ArrayList<>();
        ArrayList<String> erroresSemanticos = new ArrayList<>();
        ArrayList<String> warnings = new ArrayList<>();

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

            // Crear los archivos o sobrescribir si ya existen
            Files.write(archivoLexico, new byte[0]);
            Files.write(archivoSintactico, new byte[0]);
            Files.write(archivoSemantico, new byte[0]);
            Files.write(archivoWarnings, new byte[0]);
            
            System.out.println("Archivos creados:");
            System.out.println("  - " + archivoLexico);
            System.out.println("  - " + archivoSintactico);
            System.out.println("  - " + archivoSemantico);
            System.out.println("  - " + archivoWarnings);

        } catch (IOException e) {
            e.printStackTrace();
        }
        
        Parser parser = new Parser(tablaSimbolos, path, erroresLexicos, erroresSintacticos, erroresSemanticos, warnings, arbolSintactico);
        System.out.println(parser.yyparse());

        // Imprimir todos los errores léxicos, luego los sintácticos y por último cargar ambos en los archivos correspondientes
        for (String error : erroresLexicos) {
            System.out.println("Error léxico: " + error);
        }
        for (String error : erroresSintacticos) {
            System.out.println("Error sintáctico: " + error);
        }
        for (String error : erroresSemanticos) {
            System.out.println("Error semántico: " + error);
        }
        for (String warning : warnings) {
            System.out.println("Warning: " + warning);
        }
        Files.write(archivoLexico, erroresLexicos);
        Files.write(archivoSintactico, erroresSintacticos);
        Files.write(archivoSemantico, erroresSemanticos);
        Files.write(archivoWarnings, warnings);

        System.out.println("\n" + "TABLA DE SÍMBOLOS:");

        //ELIMINACION DE SIMBOLOS CON RANGOS NO VALIDOS

        //tablaSimbolos.remove("2147483648L");

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

        System.out.println("\n" + "ÁRBOL SINTÁCTICO:");

        if (parser.arbolSintactico != null) {
            parser.arbolSintactico.imprimirArbol();
        } else if (!erroresLexicos.isEmpty() || !erroresSintacticos.isEmpty() || !erroresSemanticos.isEmpty()) {
            System.out.println("No se pudo construir el árbol sintáctico debido a errores.");
        } else {
            System.out.println("El árbol sintáctico está vacío.");
        }

        // IMPRESION DE TODAS LAS FUNCIONES EN mapaFunciones

        System.out.println("\n" + "MAPA DE FUNCIONES [RECORDAR BORRAR EN ENTREGA]:");

        for (String key : parser.mapaFunciones.keySet()) {
            System.out.println("Función: " + key);
            System.out.println("Árbol Sintáctico de la función:");
            // imprime el arbol de esa key
            parser.mapaFunciones.get(key).imprimirArbol();
        }
    }
}