import java.util.Map;
import Simbolos.*;
import java.nio.file.Path;

public class GeneradorDeCodigo {
    private Map<String, Simbolo> tablaSimbolos;
    private Map<String, ArbolSintactico> mapaFunciones;
    private ArbolSintactico arbolSintactico;
    private StringBuilder codigoAssembler;
    private String identacion = "  ";
    private int contadorControl = 0;

    public GeneradorDeCodigo(Map<String, Simbolo> tablaSimbolos, Map<String, ArbolSintactico> mapaFunciones, ArbolSintactico arbolSintactico, Path archivoAssembler) {
        this.tablaSimbolos = tablaSimbolos;
        this.mapaFunciones = mapaFunciones;
        this.arbolSintactico = arbolSintactico;
        this.codigoAssembler = new StringBuilder();
    }

    public String generarCodigo() {
        // Encabezado del módulo webAssembly
        codigoAssembler.append("(module\n");

        // Por cada variable en la tabla de Simbolos, generar código de declaración en webAssembly
        boolean variableProgInsertada = true;
        for (String key : tablaSimbolos.keySet()) {
            SimboloLong simbolo = (SimboloLong)tablaSimbolos.get(key);
            
            if (variableProgInsertada)  
                variableProgInsertada = false; 
            else if (simbolo.getCategoria().equals("Identificador")) 
                codigoAssembler.append(identacion + "(global $" + simbolo.getLexema() + " (mut i64) (i64.const 0))\n");                
        }
        // [ACORDARSE DE IMPLEMENTAR DESPUÉS] Por cada función en el mapa de funciones, generar código de declaración de función en webAssembly
            
        codigoAssembler.append(identacion + "(func $main (export \"main\") \n"); // Inicio de la función main
        
        // Recorrer el árbol sintáctico para generar el código de las instrucciones
        identacion += "  ";
        this.ejecutar(arbolSintactico);
        identacion = identacion.substring(2);

        codigoAssembler.append(identacion + ")\n"); // Cierre de la función main
        codigoAssembler.append(")\n"); // Cierre del módulo

        return codigoAssembler.toString();
    }

    private void ejecutar(ArbolSintactico nodo) {
        String categoria;
        if (nodo.getCategoria() == null) {
            categoria = "";
        } else {
            categoria = nodo.getCategoria();
        }
        switch (categoria) {
            case "Sentencia":
                ejecutar(nodo.getLeft());
                if (nodo.getRight() != null)
                    ejecutar(nodo.getRight());
                break;
            case "Asig":
                ejecutar(nodo.getRight());
                codigoAssembler.append(identacion +"global.set $" + nodo.getLeft().getRefSimbolo().getLexema() + "\n");
                break;
            case "Suma":
                ejecutar(nodo.getLeft());
                ejecutar(nodo.getRight());
                codigoAssembler.append(identacion + "i64.add\n");
                break;
            case "Resta":
                ejecutar(nodo.getLeft());
                ejecutar(nodo.getRight());
                codigoAssembler.append(identacion + "i64.sub\n");
                break;
            case "Multiplicacion":
                ejecutar(nodo.getLeft());
                ejecutar(nodo.getRight());
                codigoAssembler.append(identacion + "i64.mul\n");
                break;
            case "Division":
                ejecutar(nodo.getLeft());
                ejecutar(nodo.getRight());
                codigoAssembler.append(identacion + "i64.div_s\n");
                break;
            case "If":
                contadorControl++;
                codigoAssembler.append(identacion + "(block $then" + contadorControl + "\n");
                identacion += "  ";
                ejecutar(nodo.getLeft()); // condición
                codigoAssembler.append(identacion + "br_if $then" + contadorControl + "\n");
                ejecutar(nodo.getRight()); // cuerpo
                break;
            case "IfElse":
                contadorControl++;
                codigoAssembler.append(identacion + "(block $else" + contadorControl + "\n");
                identacion += "  ";
                codigoAssembler.append(identacion + "(block $then" + contadorControl + "\n");
                identacion += "  ";
                ejecutar(nodo.getLeft()); // condición
                codigoAssembler.append(identacion + "br_if $then" + contadorControl + "\n");
                ejecutar(nodo.getRight()); // cuerpo
                break;
            case "CuerpoIf":
                ejecutar(nodo.getLeft());
                identacion = identacion.substring(2);
                codigoAssembler.append(identacion + ")\n"); // Cierre del bloque then
                if (nodo.getRight() != null) {
                    ejecutar(nodo.getRight());
                    identacion = identacion.substring(2);
                    codigoAssembler.append(identacion + ")\n"); // Cierre del bloque else
                }
                break;
            case "CuerpoIfElse":
                int contadorActual = contadorControl;
                ejecutar(nodo.getLeft());
                codigoAssembler.append(identacion + "br $else" + contadorActual + "\n");
                identacion = identacion.substring(2);
                codigoAssembler.append(identacion + ")\n"); // Cierre del bloque then
                if (nodo.getRight() != null) {
                    ejecutar(nodo.getRight());
                    identacion = identacion.substring(2);
                    codigoAssembler.append(identacion + ")\n"); // Cierre del bloque else
                }
                break;
            case "DoUntil":
                contadorControl++;
                int contadorActualDo = contadorControl;
                codigoAssembler.append(identacion + "(block $salidaDoUntil" + contadorControl + "\n");
                identacion += "  ";
                codigoAssembler.append(identacion + "(loop $loopDoUntil" + contadorControl + "\n");
                ejecutar(nodo.getRight()); // cuerpo
                ejecutar(nodo.getLeft()); // condición
                codigoAssembler.append(identacion + "br_if $loopDoUntil" + contadorActualDo + "\n");
                codigoAssembler.append(identacion + "br $salidaDoUntil" + contadorActualDo + "\n");
                codigoAssembler.append(identacion + ")\n"); // Cierre del bloque loop
                identacion = identacion.substring(2);
                codigoAssembler.append(identacion + ")\n"); // Cierre del bloque doUntil
                break;
            case "Call":
                ejecutar(nodo.getRight()); // parámetros
                codigoAssembler.append(identacion + "call $" + nodo.getLeft().getRefSimbolo().getLexema() + "\n");
                break;
            case "Mayor":
                ejecutar(nodo.getLeft());
                ejecutar(nodo.getRight());
                codigoAssembler.append(identacion + "i64.le_s\n");
                break;
            case "Menor":
                ejecutar(nodo.getLeft());
                ejecutar(nodo.getRight());
                codigoAssembler.append(identacion + "i64.ge_s\n");
                break;
            case "MayorIgual":
                ejecutar(nodo.getLeft());
                ejecutar(nodo.getRight());
                codigoAssembler.append(identacion + "i64.lt_s\n");
                break;
            case "MenorIgual":
                ejecutar(nodo.getLeft());
                ejecutar(nodo.getRight());
                codigoAssembler.append(identacion + "i64.gt_s\n");
                break;
            case "Igual":
                ejecutar(nodo.getLeft());
                ejecutar(nodo.getRight());
                codigoAssembler.append(identacion + "i64.ne\n");
                break;
            case "Distinto":
                ejecutar(nodo.getLeft());
                ejecutar(nodo.getRight());
                codigoAssembler.append(identacion + "i64.eq\n");
                break;
            case "":    // Es un nodo hoja
                if (nodo.getRefSimbolo().getCategoria().equals("Constante")) { // Es una constante
                    codigoAssembler.append(identacion + "i64.const " + ((SimboloLong)nodo.getRefSimbolo()).getValor() + "\n");
                }
                else { // Es un identificador
                    codigoAssembler.append(identacion + "global.get $" + nodo.getRefSimbolo().getLexema() + "\n");
                }
                break;
            default:

                break;
        }
    }

}