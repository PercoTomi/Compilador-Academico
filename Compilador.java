import Simbolos.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Compilador {
    public static void main(String[] args) throws Exception {
    Map<String, Simbolo> tablaSimbolos = new HashMap<String, Simbolo>();

    String path = "test.txt";

    // TRABAJO DE ARCHIVOS

    Path archivoLexico = null;
    Path archivoSintactico = null;

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

        // crear archivos "nombrebase-lexico.txt" y "nombrebase-sintactico.txt" en el directorio de salida
        archivoLexico = Paths.get(directorioSalida, nombreBaseArchivoFuente + "-lexico.txt");
         archivoSintactico = Paths.get(directorioSalida, nombreBaseArchivoFuente + "-sintactico.txt");

        // Crear los archivos o sobrescribir si ya existen
        Files.write(archivoLexico, new byte[0]);
        Files.write(archivoSintactico, new byte[0]);
        
        System.out.println("Archivos creados:");
        System.out.println("  - " + archivoLexico);
        System.out.println("  - " + archivoSintactico);            
    } catch (IOException e) {
        e.printStackTrace();
    }

    Parser parser = new Parser(tablaSimbolos, path, archivoLexico, archivoSintactico);
    System.out.println(parser.yyparse());

    System.out.println("\n" + "TABLA DE SÍMBOLOS:");

    //ELIMINACION DE SIMBOLOS CON RANGOS NO VALIDOS

    //tablaSimbolos.remove("2147483648L");

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