import java.util.Map;
import Simbolos.*;
import Simbolos.Auxiliares.Parametro;
import java.nio.file.Path;

public class GeneradorDeCodigo {
    private Map<String, Simbolo> tablaSimbolos;
    private Map<String, ArbolSintactico> mapaFunciones;
    private ArbolSintactico arbolSintactico;
    private StringBuilder codigoAssembler;
    private String identacion = "  ";
    private int contadorControl = 0;
    private StringBuilder stringAuxCVR = new StringBuilder();
    private int contadorPosParametro = -1;
    private String lexemaFuncionInvocadaActual = null;

    private StringBuilder dataSection = new StringBuilder();
    private int memoryOffset = 0;

    private int overflowErrorOffset = 0;
    private int overflowErrorLen = 0;

    private int truncErrorOffset = 0;
    private int truncErrorLen = 0;

    private int recursiveCallErrorOffset = 0;
    private int recursiveCallErrorLen = 0;

    public GeneradorDeCodigo(Map<String, Simbolo> tablaSimbolos, Map<String, ArbolSintactico> mapaFunciones, ArbolSintactico arbolSintactico, Path archivoAssembler) {
        this.tablaSimbolos = tablaSimbolos;
        this.mapaFunciones = mapaFunciones;
        this.arbolSintactico = arbolSintactico;
        this.codigoAssembler = new StringBuilder();
    }

    public String generarCodigo() {
        // Encabezado del módulo webAssembly
        codigoAssembler.append("(module\n");

        // Imports necesarios para interactuar con JS al usar prints
        // Import de la memoria compartida
        codigoAssembler.append(identacion + "(import \"env\" \"mem\" (memory 1))\n");
        // Import de la función para imprimir enteros de 64 bits
        codigoAssembler.append(identacion + "(import \"env\" \"print_i64\" (func $print_i64 (param i64)))\n");
        // Import de la función para imprimir strings (recibe offset y length)
        codigoAssembler.append(identacion + "(import \"env\" \"print_string\" (func $print_string (param i32 i32)))\n");

        // Variable global auxiliar para chequear overflow sin perder el valor de la pila
        codigoAssembler.append(identacion + "(global $aux_overflow (mut i64) (i64.const 0))\n");
        // Variable global auxiliar para chequear decimales en Trunc
        codigoAssembler.append(identacion + "(global $aux_float (mut f64) (f64.const 0.0))\n");

        // Mensaje de error para overflow
        String mensajeError = "Error: Overflow en suma.\n";
        overflowErrorLen = mensajeError.getBytes().length;
        overflowErrorOffset = memoryOffset;
        dataSection.append(identacion + "(data (i32.const " + overflowErrorOffset + ") \"" + mensajeError.replace("\n", "\\n") + "\")\n");
        memoryOffset += overflowErrorLen;
        // Mensaje de error para truncamiento con decimales
        String mensajeErrorTrunc = "Error: Perdida de precision al truncar la constante flotante.\n";
        truncErrorLen = mensajeErrorTrunc.getBytes().length;
        truncErrorOffset = memoryOffset;
        dataSection.append(identacion + "(data (i32.const " + truncErrorOffset + ") \"" + mensajeErrorTrunc.replace("\n", "\\n") + "\")\n");
        memoryOffset += truncErrorLen;
        // Mensaje de error para una función llamada desde si misma
        String mensajeErrorRecursion = "Error: No se puede llamar una función desde si misma.\n";
        recursiveCallErrorLen = mensajeErrorRecursion.getBytes().length;
        recursiveCallErrorOffset = memoryOffset;
        dataSection.append(identacion + "(data (i32.const " + recursiveCallErrorOffset + ") \"" + mensajeErrorRecursion.replace("\n", "\\n") + "\")\n");
        memoryOffset += recursiveCallErrorLen;

        // Por cada variable en la tabla de Simbolos, generar código de declaración en webAssembly
        boolean variableProgInsertada = true;
        for (String key : tablaSimbolos.keySet()) {
            Simbolo simbolo = tablaSimbolos.get(key);
            
            if (variableProgInsertada)  
                variableProgInsertada = false; 
            else if (simbolo.getCategoria().equals("Identificador")) 
                codigoAssembler.append(identacion + "(global $" + simbolo.getLexema() + " (mut i64) (i64.const 0))\n");                
        }

        // Por cada función en el mapa de funciones, generar código de declaración de función en webAssembly
        for (String key : mapaFunciones.keySet()) {
            ArbolSintactico arbolFuncion = mapaFunciones.get(key);
            SimboloFuncion simboloFuncion = (SimboloFuncion) tablaSimbolos.get(key);
            if (!simboloFuncion.contieneLambda()) {
                codigoAssembler.append(identacion + "(func $" + key + " ");
                // Parámetros
                for (int i = 1; i <= simboloFuncion.getCantidadParametros(); i++) {
                    codigoAssembler.append("(param i64) ");
                }
                codigoAssembler.append(" (result i64) \n");
                identacion += "  ";
                // Cuerpo de la función
                ejecutar(arbolFuncion);
                identacion = identacion.substring(2);

                codigoAssembler.append(identacion + ")\n"); // Cierre de la función
                codigoAssembler.append(identacion + "(global $flag_funcion_activa_" + key + " (mut i32) (i32.const 0))\n");
            }
        }
            
        codigoAssembler.append(identacion + "(func $main (export \"main\") \n"); // Inicio de la función main
        
        // Recorrer el árbol sintáctico para generar el código de las instrucciones
        identacion += "  ";
        this.ejecutar(arbolSintactico);
        identacion = identacion.substring(2);

        codigoAssembler.append(identacion + ")\n"); // Cierre de la función main

        // Sección de datos al final del módulo
        codigoAssembler.append(dataSection.toString());

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
                // --- INICIO CHEQUEO OVERFLOW ---
                // 1. Guarda el resultado en la variable auxiliar para no perderlo de la pila
                codigoAssembler.append(identacion + "global.set $aux_overflow\n");

                // 2. Chequo del Limite Superior (> 2147483647)
                codigoAssembler.append(identacion + "global.get $aux_overflow\n");
                codigoAssembler.append(identacion + "i64.const 2147483647\n");
                codigoAssembler.append(identacion + "i64.gt_s\n"); // ¿Es mayor con signo?
                codigoAssembler.append(identacion + "(if\n");
                codigoAssembler.append(identacion + "  (then\n");
                codigoAssembler.append(identacion + "    i32.const " + overflowErrorOffset + "\n");
                codigoAssembler.append(identacion + "    i32.const " + overflowErrorLen + "\n");
                codigoAssembler.append(identacion + "    call $print_string\n");
                codigoAssembler.append(identacion + "    unreachable\n");
                codigoAssembler.append(identacion + "  )\n");
                codigoAssembler.append(identacion + ")\n");

                // 3. Chequeo del Limite Inferior (< -2147483648)
                codigoAssembler.append(identacion + "global.get $aux_overflow\n");
                codigoAssembler.append(identacion + "i64.const -2147483648\n");
                codigoAssembler.append(identacion + "i64.lt_s\n"); // ¿Es menor con signo?
                codigoAssembler.append(identacion + "(if\n");
                codigoAssembler.append(identacion + "  (then\n");
                codigoAssembler.append(identacion + "    i32.const " + overflowErrorOffset + "\n");
                codigoAssembler.append(identacion + "    i32.const " + overflowErrorLen + "\n");
                codigoAssembler.append(identacion + "    call $print_string\n");
                codigoAssembler.append(identacion + "    unreachable\n");
                codigoAssembler.append(identacion + "  )\n");
                codigoAssembler.append(identacion + ")\n");

                // 4. Se devuelve el resultado a la pila para que el programa continúe
                codigoAssembler.append(identacion + "global.get $aux_overflow\n");
                // --- FIN CHEQUEO OVERFLOW ---
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
                lexemaFuncionInvocadaActual = nodo.getLeft().getRefSimbolo().getLexema();
                codigoAssembler.append(identacion + "global.get $flag_funcion_activa_" + lexemaFuncionInvocadaActual + "\n");
                codigoAssembler.append(identacion + "(if\n");
                codigoAssembler.append(identacion + "  (then\n");
                codigoAssembler.append(identacion + "    i32.const " + recursiveCallErrorOffset + "\n");
                codigoAssembler.append(identacion + "    i32.const " + recursiveCallErrorLen + "\n");
                codigoAssembler.append(identacion + "    call $print_string\n");
                codigoAssembler.append(identacion + "    unreachable\n");
                codigoAssembler.append(identacion + "  )\n");
                codigoAssembler.append(identacion + ")\n");
                codigoAssembler.append(identacion + "i32.const 1\n");
                codigoAssembler.append(identacion + "global.set $flag_funcion_activa_" + lexemaFuncionInvocadaActual + "\n");
                ejecutar(nodo.getRight()); // parámetros
                codigoAssembler.append(identacion + "call $" + nodo.getLeft().getRefSimbolo().getLexema() + "\n");
                if (stringAuxCVR.length() > 0) {
                    codigoAssembler.append(stringAuxCVR.toString());
                    stringAuxCVR.setLength(0); // Resetear la variable auxiliar después de usarla
                }
                codigoAssembler.append(identacion + "i32.const 0\n");
                codigoAssembler.append(identacion + "global.set $flag_funcion_activa_" + lexemaFuncionInvocadaActual + "\n");
                break;
            case "ListaParametros":
                contadorPosParametro++;
                ejecutar(nodo.getLeft());
                SimboloFuncion funcionInvocada = (SimboloFuncion) tablaSimbolos.get(lexemaFuncionInvocadaActual);
                Parametro parametroFormal = funcionInvocada.getParametroEnPos(contadorPosParametro);
                if (parametroFormal.getPasaje().equals("CVR")) {
                    stringAuxCVR.append(identacion + "global.get $" + parametroFormal.getLexema() + "\n");
                    stringAuxCVR.append(identacion + "global.set $" + nodo.getLeft().getRefSimbolo().getLexema() + "\n");
                }
                if (nodo.getRight() != null) {
                    ejecutar(nodo.getRight());
                }
                contadorPosParametro--;
                break;
            case "Return":
                ejecutar(nodo.getLeft());
                break;
            case "Trunc":
                // 1. Cargar la constante float en la pila
                SimboloCteFloat simboloCte = (SimboloCteFloat) nodo.getLeft().getRefSimbolo();
                codigoAssembler.append(identacion + "f64.const " + simboloCte.getValor() + "\n");
                
                // 2. Guardar en variable auxiliar para no perder el valor original
                codigoAssembler.append(identacion + "global.set $aux_float\n");

                // 3. Verificar si tiene parte decimal: (original != trunc(original))
                codigoAssembler.append(identacion + "global.get $aux_float\n"); // Valor original
                codigoAssembler.append(identacion + "global.get $aux_float\n"); // Copia para truncar
                codigoAssembler.append(identacion + "f64.trunc\n");             // Truncar la copia
                codigoAssembler.append(identacion + "f64.ne\n");                // Comparar: ¿Son diferentes?

                // 4. Si son diferentes (tiene decimales), imprimir error y detener ejecución
                codigoAssembler.append(identacion + "(if\n");
                codigoAssembler.append(identacion + "  (then\n");
                codigoAssembler.append(identacion + "    i32.const " + truncErrorOffset + "\n");
                codigoAssembler.append(identacion + "    i32.const " + truncErrorLen + "\n");
                codigoAssembler.append(identacion + "    call $print_string\n");
                codigoAssembler.append(identacion + "    unreachable\n");
                codigoAssembler.append(identacion + "  )\n");
                codigoAssembler.append(identacion + ")\n");

                // 5. Si no hubo error, recuperar el valor

                // --- INICIO CHEQUEO OVERFLOW TRUNC ---
                // Chequeo del Limite Superior (> 2147483647.0)
                codigoAssembler.append(identacion + "global.get $aux_float\n");
                codigoAssembler.append(identacion + "f64.const 2147483647.0\n");
                codigoAssembler.append(identacion + "f64.gt\n"); 
                codigoAssembler.append(identacion + "(if\n");
                codigoAssembler.append(identacion + "  (then\n");
                codigoAssembler.append(identacion + "    i32.const " + truncErrorOffset + "\n");
                codigoAssembler.append(identacion + "    i32.const " + truncErrorLen + "\n");
                codigoAssembler.append(identacion + "    call $print_string\n");
                codigoAssembler.append(identacion + "    unreachable\n");
                codigoAssembler.append(identacion + "  )\n");
                codigoAssembler.append(identacion + ")\n");

                // Chequeo del Limite Inferior (< -2147483648.0)
                codigoAssembler.append(identacion + "global.get $aux_float\n");
                codigoAssembler.append(identacion + "f64.const -2147483648.0\n");
                codigoAssembler.append(identacion + "f64.lt\n"); 
                codigoAssembler.append(identacion + "(if\n");
                codigoAssembler.append(identacion + "  (then\n");
                codigoAssembler.append(identacion + "    i32.const " + truncErrorOffset + "\n");
                codigoAssembler.append(identacion + "    i32.const " + truncErrorLen + "\n");
                codigoAssembler.append(identacion + "    call $print_string\n");
                codigoAssembler.append(identacion + "    unreachable\n");
                codigoAssembler.append(identacion + "  )\n");
                codigoAssembler.append(identacion + ")\n");
                // --- FIN CHEQUEO OVERFLOW TRUNC ---
                codigoAssembler.append(identacion + "global.get $aux_float\n");
                codigoAssembler.append(identacion + "i64.trunc_f64_s\n"); // Conversión final a entero
                break;
            case "Print":
                ArbolSintactico hijo = nodo.getLeft();
                Simbolo simboloHijo = hijo.getRefSimbolo();
                
                if (simboloHijo.getCategoria().equals("CadenaML")) {
                    String textoRaw = simboloHijo.getLexema();
                    String textoEscapado = textoRaw
                        .replace("\\", "\\\\")
                        .replace("\"", "\\\"")
                        .replace("\n", "\\n")
                        .replace("\t", "\\t")
                        .replace("\r", "\\r");
                    
                    dataSection.append("  (data (i32.const " + memoryOffset + ") \"" + textoEscapado + "\")\n");

                    int largo = textoEscapado.getBytes().length;
                    // Llamado al print de string
                    codigoAssembler.append(identacion + "i32.const " + memoryOffset + "\n"); 
                    codigoAssembler.append(identacion + "i32.const " + largo + "\n");       
                    codigoAssembler.append(identacion + "call $print_string\n");

                    // Actualización del offset para el siguiente string
                    memoryOffset += largo;
                } else {
                    // Es una variable o constante
                    ejecutar(hijo); // Esto pone el valor del hijo en el stack
                    codigoAssembler.append(identacion + "call $print_i64\n");
                }
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