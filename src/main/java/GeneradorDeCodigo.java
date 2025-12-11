import Simbolos.*;
import Simbolos.Auxiliares.Parametro;
import java.nio.file.Path;
import java.util.Map;
import java.util.Stack;

public class GeneradorDeCodigo {
    private Map<String, Simbolo> tablaSimbolos;
    private Map<String, ArbolSintactico> mapaFunciones;
    private ArbolSintactico arbolSintactico;
    private StringBuilder codigoAssembler;
    private String identacion = "  ";
    private int contadorControl = 0;
    private Stack<StringBuilder> stackStringAuxCVR = new Stack<>();
    private int contadorPosParametro = -1;
    private String lexemaFuncionInvocadaActual = null;
    private int funcLambdaDeclaradas = 0;
    private int contadorLambda = 0;
    private int marcadorSeccionLambdas = 0;

    private StringBuilder dataSection = new StringBuilder();
    private int memoryOffset = 0;

    private int overflowErrorOffset = 0;
    private int overflowErrorLen = 0;

    private int truncErrorOffset = 0;
    private int truncErrorLen = 0;

    private int recursiveCallErrorOffset = 0;
    private int recursiveCallErrorLen = 0;

    public GeneradorDeCodigo(Map<String, Simbolo> tablaSimbolos, Map<String, ArbolSintactico> mapaFunciones, ArbolSintactico arbolSintactico, Path archivoAssembler, int funcLambdaDeclaradas) {
        this.tablaSimbolos = tablaSimbolos;
        this.mapaFunciones = mapaFunciones;
        this.arbolSintactico = arbolSintactico;
        this.codigoAssembler = new StringBuilder();
        this.funcLambdaDeclaradas = funcLambdaDeclaradas;
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

        codigoAssembler.append(identacion + ";; variables auxiliares para chequeo de errores \n");
        // Variable global auxiliar para chequear overflow sin perder el valor de la pila
        codigoAssembler.append(identacion + "(global $aux_overflow (mut i64) (i64.const 0))\n");
        // Variable global auxiliar para chequear decimales en Trunc
        codigoAssembler.append(identacion + "(global $aux_float (mut f64) (f64.const 0.0))\n");

        codigoAssembler.append(identacion + ";; Declaraciones de tipo de función lambda y de su tabla \n");
        // Declaración del tipo funcLambda 
        codigoAssembler.append(identacion + "(type $funcLambda (func (param i64)))\n");
        // Tabla para funciones lambda
        codigoAssembler.append(identacion + "(table " + funcLambdaDeclaradas + " funcref)\n");

        // Mensaje de error para overflow
        String mensajeError = "Error: Overflow en la operación.\n";
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
        codigoAssembler.append(identacion + ";; Declaración de variables globales \n");
        for (String key : tablaSimbolos.keySet()) {
            Simbolo simbolo = tablaSimbolos.get(key);
            if (simbolo.getCategoria().equals("Identificador")) 
                codigoAssembler.append(identacion + "(global $" + simbolo.getLexema() + " (mut i64) (i64.const 0))\n");                
        }
        
        codigoAssembler.append(identacion+ ";; Espacio reservado para la declaración de funciones Lambda\n");
        marcadorSeccionLambdas = codigoAssembler.length();

        // Por cada función en el mapa de funciones, generar código de declaración de función en webAssembly
        codigoAssembler.append(identacion + ";; Declaración de funciones estándar\n");
        for (String key : mapaFunciones.keySet()) {
            ArbolSintactico arbolFuncion = mapaFunciones.get(key);
            SimboloFuncion simboloFuncion = (SimboloFuncion) tablaSimbolos.get(key);
            codigoAssembler.append(identacion + "(func $" + key + " (result i64) \n");
            identacion += "  ";
            // Cuerpo de la función
            ejecutar(arbolFuncion);
            // Se añade unreachable para que WebAssembly no tire error al existir funciones sin return general pero con return en todos sus caminos
            codigoAssembler.append(identacion + "unreachable\n");
            identacion = identacion.substring(2);

        
            codigoAssembler.append(identacion + ")\n"); // Cierre de la función
            codigoAssembler.append(identacion + "(global $flag_funcion_activa_" + key + " (mut i32) (i32.const 0))\n");

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
                if (nodo.getLeft().getRefSimbolo() != null)
                    codigoAssembler.append(identacion +"global.set $" + nodo.getLeft().getRefSimbolo().getLexema() + "\n");
                else 
                    codigoAssembler.append(identacion +"local.set 0\n");
                break;
            case "Suma":
                ejecutar(nodo.getLeft());
                ejecutar(nodo.getRight());
                codigoAssembler.append(identacion + "i64.add\n");
                // --- INICIO CHEQUEO OVERFLOW ---
                codigoAssembler.append(identacion + ";; INICIO CHEQUEO OVERFLOW \n");
                // 1. Guarda el resultado en la variable auxiliar para no perderlo de la pila
                codigoAssembler.append(identacion + "global.set $aux_overflow\n");

                // 2. Chequo del Limite Superior (> INT32_MAX)
                codigoAssembler.append(identacion + "global.get $aux_overflow\n");
                codigoAssembler.append(identacion + "i64.const " + Compilador.INT32_MAX + "\n");
                codigoAssembler.append(identacion + "i64.gt_s\n"); // ¿Es mayor con signo?
                codigoAssembler.append(identacion + "(if\n");
                codigoAssembler.append(identacion + "  (then\n");
                codigoAssembler.append(identacion + "    i32.const " + overflowErrorOffset + "\n");
                codigoAssembler.append(identacion + "    i32.const " + overflowErrorLen + "\n");
                codigoAssembler.append(identacion + "    call $print_string\n");
                codigoAssembler.append(identacion + "    unreachable\n");
                codigoAssembler.append(identacion + "  )\n");
                codigoAssembler.append(identacion + ")\n");

                // 3. Chequeo del Limite Inferior (< INT32_MIN)
                codigoAssembler.append(identacion + "global.get $aux_overflow\n");
                codigoAssembler.append(identacion + "i64.const " + Compilador.INT32_MIN + "\n");
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
                codigoAssembler.append(identacion + ";; FIN CHEQUEO OVERFLOW \n");

                codigoAssembler.append(identacion + "global.get $aux_overflow\n");
                break;
            case "Resta":
                ejecutar(nodo.getLeft());
                ejecutar(nodo.getRight());
                codigoAssembler.append(identacion + "i64.sub\n");
                // --- INICIO CHEQUEO OVERFLOW ---
                codigoAssembler.append(identacion + ";; INICIO CHEQUEO OVERFLOW \n");
                // 1. Guarda el resultado en la variable auxiliar para no perderlo de la pila
                codigoAssembler.append(identacion + "global.set $aux_overflow\n");

                // 2. Chequo del Limite Superior (> INT32_MAX)
                codigoAssembler.append(identacion + "global.get $aux_overflow\n");
                codigoAssembler.append(identacion + "i64.const " + Compilador.INT32_MAX + "\n");
                codigoAssembler.append(identacion + "i64.gt_s\n"); // ¿Es mayor con signo?
                codigoAssembler.append(identacion + "(if\n");
                codigoAssembler.append(identacion + "  (then\n");
                codigoAssembler.append(identacion + "    i32.const " + overflowErrorOffset + "\n");
                codigoAssembler.append(identacion + "    i32.const " + overflowErrorLen + "\n");
                codigoAssembler.append(identacion + "    call $print_string\n");
                codigoAssembler.append(identacion + "    unreachable\n");
                codigoAssembler.append(identacion + "  )\n");
                codigoAssembler.append(identacion + ")\n");

                // 3. Chequeo del Limite Inferior (< INT32_MIN)
                codigoAssembler.append(identacion + "global.get $aux_overflow\n");
                codigoAssembler.append(identacion + "i64.const " + Compilador.INT32_MIN + "\n");
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
                codigoAssembler.append(identacion + ";; FIN CHEQUEO OVERFLOW \n");

                codigoAssembler.append(identacion + "global.get $aux_overflow\n");
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
                ejecutar(nodo.getLeft()); // condición
                codigoAssembler.append(identacion + "(if \n");
                identacion += "  ";
                ejecutar(nodo.getRight()); // cuerpo
                identacion = identacion.substring(2);
                codigoAssembler.append(identacion + ") \n");
                break;
            case "IfElse":
                ejecutar(nodo.getLeft()); // condición
                codigoAssembler.append(identacion + "(if \n");
                identacion += "  ";
                ejecutar(nodo.getRight()); // cuerpo
                identacion = identacion.substring(2);
                codigoAssembler.append(identacion + ") \n");
                break;
            case "CuerpoIf":
                codigoAssembler.append(identacion + "(then \n");
                identacion += "  ";
                ejecutar(nodo.getLeft());
                identacion = identacion.substring(2);
                codigoAssembler.append(identacion + ") \n");
                break;
            case "CuerpoIfElse":
                codigoAssembler.append(identacion + "(then \n");
                identacion += "  ";
                ejecutar(nodo.getLeft()); // sentencias del then
                identacion = identacion.substring(2);
                codigoAssembler.append(identacion + ") \n");
                codigoAssembler.append(identacion + "(else \n");
                identacion += "  ";
                ejecutar(nodo.getRight()); // sentencias del else
                identacion = identacion.substring(2);
                codigoAssembler.append(identacion + ") \n");
                break;
            case "DoUntil":
                contadorControl++;
                int contadorActualDo = contadorControl;
                codigoAssembler.append(identacion + "(block $salidaDoUntil" + contadorControl + "\n");
                identacion += "  ";
                codigoAssembler.append(identacion + "(loop $loopDoUntil" + contadorControl + "\n");
                ejecutar(nodo.getRight()); // cuerpo
                ejecutar(nodo.getLeft()); // condición
                codigoAssembler.append(identacion + "br_if $salidaDoUntil" + contadorActualDo + "\n");
                codigoAssembler.append(identacion + "br $loopDoUntil" + contadorActualDo + "\n");
                codigoAssembler.append(identacion + ")\n"); // Cierre del bloque loop
                identacion = identacion.substring(2);
                codigoAssembler.append(identacion + ")\n"); // Cierre del bloque doUntil
                break;
            case "Call":
                stackStringAuxCVR.push(new StringBuilder());
                lexemaFuncionInvocadaActual = nodo.getLeft().getRefSimbolo().getLexema();

                codigoAssembler.append(identacion + ";; Inicio de chequeo de llamada recursiva\n");
                codigoAssembler.append(identacion + "global.get $flag_funcion_activa_" + lexemaFuncionInvocadaActual + "\n");
                codigoAssembler.append(identacion + "(if\n");
                codigoAssembler.append(identacion + "  (then\n");
                codigoAssembler.append(identacion + "    i32.const " + recursiveCallErrorOffset + "\n");
                codigoAssembler.append(identacion + "    i32.const " + recursiveCallErrorLen + "\n");
                codigoAssembler.append(identacion + "    call $print_string\n");
                codigoAssembler.append(identacion + "    unreachable\n");
                codigoAssembler.append(identacion + "  )\n");
                codigoAssembler.append(identacion + ")\n");
                codigoAssembler.append(identacion + ";; Fin de chequeo de llamada recursiva\n");
                codigoAssembler.append(identacion + "i32.const 1\n");
                codigoAssembler.append(identacion + "global.set $flag_funcion_activa_" + lexemaFuncionInvocadaActual + "\n");
                
                // Se guarda el contadorPosParametro actual y se resetea para la nueva función (en caso de llamados anidados por declaración de funciones Lambda)
                int contadorPosParametroActual = contadorPosParametro;
                contadorPosParametro = -1;
                ejecutar(nodo.getRight()); // parámetros
                codigoAssembler.append(identacion + "call $" + nodo.getLeft().getRefSimbolo().getLexema() + "\n");

                contadorPosParametro = contadorPosParametroActual;
                StringBuilder stringAuxCVR = stackStringAuxCVR.pop();
                if (stringAuxCVR.length() > 0) {
                    codigoAssembler.append(stringAuxCVR.toString());
                    stringAuxCVR.setLength(0); // Resetear la variable auxiliar después de usarla
                }
                codigoAssembler.append(identacion + "i32.const 0\n");
                codigoAssembler.append(identacion + "global.set $flag_funcion_activa_" + lexemaFuncionInvocadaActual + "\n");
                break;
            case "CallLambda":
                // Deja en la pila el argumento de la función lambda
                ejecutar(nodo.getRight());
                // Apila el índice de la función lambda en la tabla y lo pasa a i32
                codigoAssembler.append(identacion + "global.get $" + nodo.getLeft().getRefSimbolo().getLexema() + "\n");
                codigoAssembler.append(identacion + "i32.wrap_i64\n");

                // Invoca la función lambda indirectamente
                codigoAssembler.append(identacion + "call_indirect (type $funcLambda)\n");
                break;
            case "ListaParametros":
                contadorPosParametro++;
                ejecutar(nodo.getLeft());
                SimboloFuncion funcionInvocada = (SimboloFuncion) tablaSimbolos.get(lexemaFuncionInvocadaActual);
                Parametro parametroFormal = funcionInvocada.getParametroEnPos(contadorPosParametro);
                codigoAssembler.append(identacion + "global.set $" + parametroFormal.getLexema() + "\n");
                if (parametroFormal.getPasaje().equals("CVR")) {
                    stackStringAuxCVR.peek().append(identacion + "global.get $" + parametroFormal.getLexema() + "\n");
                    if (nodo.getLeft().getRefSimbolo() != null) 
                        stackStringAuxCVR.peek().append(identacion + "global.set $" + nodo.getLeft().getRefSimbolo().getLexema() + "\n");
                    else 
                        stackStringAuxCVR.peek().append(identacion + "local.set 0\n");
                }
                if (nodo.getRight() != null) {
                    ejecutar(nodo.getRight());
                }
                contadorPosParametro--;
                break;
            case "DeclaracionLambda":
                codigoAssembler.append("  (func $lambda_func_" + contadorLambda + " (param i64) \n");
                int contadorLambdaActual = contadorLambda;
                contadorLambda++;
                String identacionAux = identacion;
                identacion = "    ";
                ejecutar(nodo.getLeft());
                identacion = identacionAux;
                codigoAssembler.append("  )\n");
                codigoAssembler.append("  (elem (i32.const " + contadorLambdaActual + ") $lambda_func_" + contadorLambdaActual+ ")\n");
                int finMarcador = codigoAssembler.length();
                int inicioMarcador = codigoAssembler.indexOf("  (func $lambda_func_" + contadorLambdaActual);
                String funcionLambda = codigoAssembler.substring(inicioMarcador, finMarcador);
                codigoAssembler.delete(inicioMarcador, finMarcador);
                codigoAssembler.insert(marcadorSeccionLambdas, funcionLambda);
                marcadorSeccionLambdas += funcionLambda.length();
                
                codigoAssembler.append(identacion + "i64.const " + contadorLambdaActual + "   ;; Indice en la tabla a guardar en parámetro formal\n");                
                break;
            case "Return":
                ejecutar(nodo.getLeft());
                codigoAssembler.append(identacion + "return\n");
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
                // Chequeo del Limite Superior (> INT32_MAX.0)
                codigoAssembler.append(identacion + "global.get $aux_float\n");
                codigoAssembler.append(identacion + "f64.const " + Compilador.INT32_MAX + ".0\n");
                codigoAssembler.append(identacion + "f64.gt\n"); 
                codigoAssembler.append(identacion + "(if\n");
                codigoAssembler.append(identacion + "  (then\n");
                codigoAssembler.append(identacion + "    i32.const " + truncErrorOffset + "\n");
                codigoAssembler.append(identacion + "    i32.const " + truncErrorLen + "\n");
                codigoAssembler.append(identacion + "    call $print_string\n");
                codigoAssembler.append(identacion + "    unreachable\n");
                codigoAssembler.append(identacion + "  )\n");
                codigoAssembler.append(identacion + ")\n");

                // Chequeo del Limite Inferior (< INT32_MIN.0)
                codigoAssembler.append(identacion + "global.get $aux_float\n");
                codigoAssembler.append(identacion + "f64.const " + Compilador.INT32_MIN + ".0\n");
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
                
                if (simboloHijo != null && simboloHijo.getCategoria().equals("CadenaML")) {
                    String textoRaw = ((SimboloCteString)simboloHijo).getValor();
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
                codigoAssembler.append(identacion + "i64.gt_s\n");
                break;
            case "Menor":
                ejecutar(nodo.getLeft());
                ejecutar(nodo.getRight());
                codigoAssembler.append(identacion + "i64.lt_s\n");
                break;
            case "MayorIgual":
                ejecutar(nodo.getLeft());
                ejecutar(nodo.getRight());
                codigoAssembler.append(identacion + "i64.ge_s\n");
                break;
            case "MenorIgual":
                ejecutar(nodo.getLeft());
                ejecutar(nodo.getRight());
                codigoAssembler.append(identacion + "i64.le_s\n");
                break;
            case "Igual":
                ejecutar(nodo.getLeft());
                ejecutar(nodo.getRight());
                codigoAssembler.append(identacion + "i64.eq\n");
                break;
            case "Distinto":
                ejecutar(nodo.getLeft());
                ejecutar(nodo.getRight());
                codigoAssembler.append(identacion + "i64.ne\n");
                break;
            case "":    // Es un nodo hoja
                if (nodo.getRefSimbolo().getLexema().startsWith("lambda:")) {
                    codigoAssembler.append(identacion + "local.get 0\n");
                }
                else if (nodo.getRefSimbolo().getCategoria().equals("Constante")) { // Es una constante
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