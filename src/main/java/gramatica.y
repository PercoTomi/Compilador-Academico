%{
    import java.io.*;
    import Simbolos.*;
    import Simbolos.Auxiliares.*;
    import java.util.Map;
    import java.util.HashMap;
    import java.nio.file.Path;
    import java.nio.file.Paths;
    import java.nio.file.Files;
    import java.nio.file.StandardOpenOption;
    import java.util.ArrayList;
    import java.util.Stack;

%}



%token ID CTE ASIG IF ELSE ENDIF MAYORIG MENORIG IGUAL DISTINTO LONG RETURN DO UNTIL CV FLECHA PRINT CADENAML TRUNC LAMBDA EOF

%%
prog                        : ID {stringAmbitos.append(":"+((Simbolo)$1.obj).getLexema());} bloque_sentencias {quitarAmbito();} EOF   {if (!error() && erroresSemanticos.isEmpty() && $3.obj != null && $3.obj != null) { arbolSintactico.adoptarNodoIntermedio((ArbolSintactico)$3.obj);}}
                            | bloque_sentencias EOF                     {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": falta el nombre del programa");} 
                            ;

bloque_sentencias           : '{' listasentencias '}'                   { $$ = $2; $$.ival = $3.ival; }          
                            | '{' '}'                                   { $$.obj = null; $$.ival = $2.ival; }     
                            | '{' listasentencias                       {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta llave de cierre del bloque de sentencias"); $$.obj = null; $$.ival = $2.ival; }
                            | listasentencias '}'                       {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": falta llave de apertura del bloque de sentencias"); $$.obj = null; $$.ival = $1.ival; }
                            | listasentencias                           {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": faltan ambas llaves del bloque de sentencias"); $$.obj = null; $$.ival = $1.ival; }
                            ;

listasentencias             : listasentencias sentencia                 { if (!error()) {
                                                                            if ($1.obj != null && $2.obj != null) { ((ArbolSintactico)$1.obj).setUltimoDerecho(new ArbolSintactico("Sentencia", ((ArbolSintactico)$2.obj) , null )); $$ = $1; } 
                                                                            else if ($1.obj != null) { $$= $1; $$.ival = $1.ival; }
                                                                            else if ($2.obj != null) { $$= new ParserVal(new ArbolSintactico("Sentencia", ((ArbolSintactico)$2.obj), null));} 
                                                                          }
                                                                          $$.ival = $2.ival; 
                                                                        }
                            | sentencia                                 { if (!error()) { if ($1.obj != null) { $$ = new ParserVal(new ArbolSintactico("Sentencia", ((ArbolSintactico)$1.obj), null)); }} $$.ival = $1.ival;}
                            ;

sentencia                   : sentenciaejecutable ';'                   { $$ = $1;}
                            | declaracion ';'                           { $$.obj = null; $$.ival = $2.ival; }
                            | sentenciaejecutable                       {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": falta ; al final de la sentencia ejecutable"); $$.ival = $1.ival;}
                            | declaracion                               {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": falta ; al final de la declaración"); $$.ival = $1.ival;}
                            | error ';'                                 {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": sentencia no válida"); $$.ival = $2.ival;}  
                            ;

listasentenciasejecutables  : listasentenciasejecutables sentenciaejecutable ';'  { if (!error()) {
                                                                                    ((ArbolSintactico)$1.obj).setUltimoDerecho(new ArbolSintactico("Sentencia", ((ArbolSintactico)$2.obj) , null ));
                                                                                    $$ = $1; 
                                                                                    }
                                                                                    $$.ival = $3.ival;
                                                                                  }
                            | sentenciaejecutable ';'                             { if (!error()) { $$ = new ParserVal(new ArbolSintactico("Sentencia", ((ArbolSintactico)$1.obj), null));} $$.ival = $2.ival;}                         
                            | listasentenciasejecutables sentenciaejecutable      {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta ; al final de la sentencia ejecutable"); $$.ival = $2.ival;}
                            | sentenciaejecutable                                 {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": falta ; al final de la sentencia ejecutable"); $$.ival = $1.ival;}
                            ;

sentenciaejecutable         : asignacion                                      { $$ = $1; }
                            | control                                         { $$ = $1; }  
                            | funcion                                         { erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + $1.ival + ": No está permitida la invocación de funciones sin utilizar su retorno"); }
                            | print                                           { if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $1.ival + ": Se detectó un print");
                                                                                $$ = $1;
                                                                              }
                            | funcion_lambda                                  { if(enFunc==0) {erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + $1.ival + ": No está permitida la invocación de función lambda fuera de una función");} 
                                                                                else { $$ = $1; }
                                                                              }
                            | sentencia_return                                {chequeoSentenciaReturn($1); $$ = $1;}
                            ;

declaracion                 : tipo listaIDs                                   { if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $2.ival + ": Se detectó una declaración de variables");}
                            | tipo id_funcion {String idFuncRaw = ((Simbolo)$2.obj).getLexema(); renombrarVariable($2); stringAmbitos.append(":"+idFuncRaw); stackTipoFuncActual.push($1.sval); enDeclaracionParam = true;} parametros_declaracion_f {enDeclaracionParam = false; stackParametrosActuales.push(parametrosActuales); cambiarASimboloFuncion((Simbolo)$2.obj); parametrosActuales = new ArrayList<>(); inicioFunc();} '{' listasentencias '}' {  
                                                                                                                                                                                                                                                                    finFunc($8); 
                                                                                                                                                                                                                                                                    quitarAmbito(); 
                                                                                                                                                                                                                                                                    if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $8.ival + ": Se detectó una declaración de función");
                                                                                                                                                                                                                                                                    if (!error()) {
                                                                                                                                                                                                                                                                      mapaFunciones.put(((Simbolo)$2.obj).getLexema(), (ArbolSintactico)$7.obj);
                                                                                                                                                                                                                                                                    }
                                                                                                                                                                                                                                                                    $$.ival = $8.ival;
                                                                                                                                                                                                                                                                  }
                            ;

parametros_declaracion_f    : '(' listaparametros ')'
                            | '(' ')'
                            ;

listaparametros             : listaparametros ',' parametro_formal
                            | parametro_formal
                            ;

parametro_formal            : CV tipo ID                             { ((Simbolo)$3.obj).setTipo($2.sval);
                                                                       renombrarVariable($3); 
                                                                       if ($2.sval.equals("Lambda")) {
                                                                          erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + $2.ival + ": el tipo 'Lambda' no puede utilizar la semántica de pasaje por Copia-Valor (CV)");
                                                                       }
                                                                       parametrosActuales.add(new Parametro(((Simbolo)$3.obj), "CV"));
                                                                     }                          
                            | tipo ID                                { ((Simbolo)$2.obj).setTipo($1.sval);
                                                                       renombrarVariable($2);
                                                                       if ($1.sval.equals("Lambda")) {
                                                                          parametrosActuales.add(new Parametro(((Simbolo)$2.obj), "CV"));
                                                                       } else parametrosActuales.add(new Parametro(((Simbolo)$2.obj), "CVR"));
                                                                     }
                            | CV tipo                                { erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": falta el nombre del parámetro");
                                                                     }
                            | tipo                                   { erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": falta el nombre del parámetro");
                                                                     }
                            | CV ID                                  { renombrarVariable($2); 
                                                                       parametrosActuales.add(new Parametro(((Simbolo)$2.obj), "CV")); 
                                                                       erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": falta el tipo del parámetro");
                                                                     }
                            | ID                                     { renombrarVariable($1); 
                                                                       parametrosActuales.add(new Parametro(((Simbolo)$1.obj), "CVR")); 
                                                                       erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": falta el tipo del parámetro");
                                                                     }
                            ;

id_funcion                  : ID                                             {$$ = new ParserVal((SimboloLong)$1.obj);}
                            ;

sentencia_return            : RETURN '(' expresion ')'                      { if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $3.ival + ": Se detectó una sentencia return");
                                                                              if (!error()) { $$ = new ParserVal(new ArbolSintactico("Return", (ArbolSintactico)$3.obj,  null)); } $$.ival = $4.ival;
                                                                            }
                            | RETURN '(' expresion                          {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta el paréntesis de cierre en sentencia return"); $$.ival = $3.ival;}
                            | RETURN expresion ')'                          {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta el paréntesis de apertura en sentencia return"); $$.ival = $3.ival;}
                            | RETURN expresion                              {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": faltan ambos paréntesis en sentencia return"); $$.ival = $2.ival;}
                            | RETURN '(' ')'                                {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $3.ival + ": falta de argumento en sentencia return"); $$.ival = $3.ival;}
                            ;

asignacion                  : identificador ASIG expresion                                { if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $3.ival + ": Se detectó una asignación simple");
                                                                                            Simbolo simboloAAsignar = checkearAlcance($1.sval, $1.ival);
                                                                                            if( simboloAAsignar != null && (!simboloAAsignar.getCategoria().equals("Identificador") || simboloAAsignar.getTipo().equals("Lambda"))) {
                                                                                              erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + $1.ival + ": El identificador '" + $1.sval + "' no es una variable");
                                                                                            }
                                                                                            if (!error()) { 
                                                                                              $$ = new ParserVal( new ArbolSintactico("Asig", new ArbolSintactico(simboloAAsignar), (ArbolSintactico)$3.obj) );
                                                                                            }
                                                                                          }
                            | listavariables '=' listaconstantes                          { if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $3.ival + ": Se detectó una asignación múltiple");
                                                                                            if (((ArrayList<Simbolo>)$1.obj).size() >= ((ArrayList<Simbolo>)$3.obj).size()) {
                                                                                              if(!error()) {  
                                                                                                $$ = new ParserVal(generarAsigMultiple((ArrayList<Simbolo>)$1.obj, (ArrayList<Simbolo>)$3.obj, $2.ival));
                                                                                              }
                                                                                            }
                                                                                            else erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + $3.ival + ": La cantidad de constantes en la asignación múltiple es mayor a la cantidad de variables");
                                                                                          }
                            | listavariables '=' listavariables                           {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": No se permite la asignación múltiple utilizando variables en el lado derecho");}
                            | listavariables '=' lista_expresiones                        { erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": No se permite la asignación múltiple utilizando variables en el lado derecho");
                                                                                            if (((ArrayList<Simbolo>)$1.obj).size() >= ((ArrayList<Simbolo>)$3.obj).size()) {
                                                                                              if(!error()) {  
                                                                                                $$ = new ParserVal(generarAsigMultiple((ArrayList<Simbolo>)$1.obj, (ArrayList<Simbolo>)$3.obj, $2.ival));
                                                                                              }
                                                                                            }
                                                                                            else erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + $3.ival + ": La cantidad de elementos en la derecha de la asignación múltiple es mayor a la cantidad de variables del lado izquierdo");
                                                                                          }
                            ;

bloque_control_estructura   : '{' listasentenciasejecutables '}'              { $$ = $2; $$.ival = $3.ival; }
                            | sentenciaejecutable ';'                         { if (!error()) { $$ = new ParserVal(new ArbolSintactico("Sentencia", ((ArbolSintactico)$1.obj), null)); } $$.ival = $2.ival; }
                            | '{''}'                                          { $$.obj = null; $$.ival = $2.ival; }
                            ;

bloque_control              : {inicioIf();} bloque_control_estructura {finIf($2); $$ = $2; }
                            ;

inicio_do                   : DO {enDo=true;}
                            ;

bloque_control_else         : {sinRetIf.push(sinRet);} bloque_control {if(sinRetIf.pop()) {sinRet=true;} $$ = $2; }
                            ;

control                     : IF condicion bloque_control ENDIF                                 { if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $4.ival + ": Se detectó un bloque IF");
                                                                                                  if (!error()) { $$ = new ParserVal(new ArbolSintactico("If", (ArbolSintactico)$2.obj, new ArbolSintactico("CuerpoIf",(ArbolSintactico)$3.obj, null)) ); }
                                                                                                  $$.ival = $4.ival;
                                                                                                  sinRet = true;
                                                                                                }
                            | IF condicion bloque_control ELSE bloque_control_else ENDIF        { if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $6.ival + ": Se detectó un bloque IF-ELSE");
                                                                                                  if (!error()) { $$ = new ParserVal(new ArbolSintactico("IfElse", (ArbolSintactico)$2.obj, new ArbolSintactico("CuerpoIfElse",(ArbolSintactico)$3.obj, (ArbolSintactico)$5.obj)) );}  $$.ival = $6.ival;
                                                                                                  if (enFunc > 0 && !sinRet && nivel > -1) {
                                                                                                      // El IF-ELSE completo actúa como un return
                                                                                                      huboRetStack.pop();
                                                                                                      huboRetStack.push(true);
                                                                                                  }
                                                                                                } 
                            | inicio_do bloque_control UNTIL condicion                          { enDo=false;
                                                                                                  if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $4.ival + ": Se detectó un bloque DO UNTIL");
                                                                                                  if (!error()) { $$ = new ParserVal(new ArbolSintactico("DoUntil", (ArbolSintactico)$4.obj, (ArbolSintactico)$2.obj));  }  $$.ival = $4.ival;
                                                                                                }

                            | IF condicion bloque_control ELSE bloque_control_else              {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $5.ival + ": falta la palabra ENDIF al final del bloque IF-ELSE"); $$.ival = $5.ival;}
                            | IF condicion bloque_control                                       {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta la palabra ENDIF al final del bloque IF"); $$.ival = $3.ival;}
                            | IF condicion bloque_control ELSE ENDIF                            {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $5.ival + ": falta el cuerpo en el bloque ELSE"); $$.ival = $5.ival;}
                            | IF condicion ELSE bloque_control_else ENDIF                       {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $5.ival + ": falta el cuerpo en el bloque IF"); $$.ival = $5.ival;}
                            | IF condicion ENDIF                                                {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $3.ival + ": falta el cuerpo en el bloque IF"); $$.ival = $3.ival;}
                            | IF condicion ELSE ENDIF                                           {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $4.ival + ": falta el cuerpo en los bloques IF y ELSE"); $$.ival = $4.ival;}
                            
                            | DO UNTIL condicion                                                {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": falta de cuerpo en iteracion"); $$.ival = $1.ival;}
                            | inicio_do bloque_control condicion                                { enDo=false;
                                                                                                  erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $3.ival + ": falta la palabra UNTIL en la iteracion"); $$.ival = $3.ival;
                                                                                                }
                            ;
                            

print                       : PRINT '(' argumento_print ')'                { if (!error()) { $$ = new ParserVal (new ArbolSintactico("Print", (ArbolSintactico)$3.obj, null)); } $$.ival = $4.ival; }
                            | PRINT '(' ')'                                {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $3.ival + ": falta de argumento en sentencia print");  $$.ival = $3.ival; }
                            | PRINT argumento_print ')'                    {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta el paréntesis de apertura en sentencia print");  $$.ival = $3.ival; }
                            | PRINT '(' argumento_print                    {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta el paréntesis de cierre en sentencia print");  $$.ival = $2.ival; }
                            | PRINT argumento_print                        {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": faltan ambos paréntesis en sentencia print");  $$.ival = $2.ival; }
                            ;

argumento_print             : expresion                             { $$ = $1; }
                            | CADENAML                              { $$ = new ParserVal(new ArbolSintactico((Simbolo)$1.obj)); }

condicion                   : '(' cuerpocondicion ')'                { $$ = $2; $$.ival = $3.ival; }
                            | cuerpocondicion ')'                    {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta el paréntesis de apertura de la condición");}
                            | '(' cuerpocondicion                    {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta el paréntesis de cierre de la condición");}
                            | cuerpocondicion                        {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": faltan ambos paréntesis de la condición");}
                            ;

cuerpocondicion             : expresion comparador expresion         { if (!error()) { $$ = new ParserVal (new ArbolSintactico($2.sval, (ArbolSintactico)$1.obj, (ArbolSintactico)$3.obj)); } }
                            | expresion                              { erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": La condición es incompleta");}
                            ;

comparador                  : '>'                    { $$ = new ParserVal("Mayor"); }
                            | '<'                    { $$ = new ParserVal("Menor"); }
                            | MAYORIG                { $$ = new ParserVal("MayorIgual"); }
                            | MENORIG                { $$ = new ParserVal("MenorIgual"); }
                            | IGUAL                  { $$ = new ParserVal("Igual"); }
                            | DISTINTO               { $$ = new ParserVal("Distinto"); }
                            ;

tipo                        : LONG                  { $$ = new ParserVal("Long"); }
                            | LAMBDA                { $$ = new ParserVal("Lambda");
                                                      if (!enDeclaracionParam) {
                                                        erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": el tipo 'Lambda' solo puede utilizarse en la declaración de parámetros formales de funciones estándar");
                                                      }
                                                    }
                            ;

listaIDs                    : listaIDs ',' ID        {renombrarVariable($3);}
                            | ID                     {renombrarVariable($1);}
                            ;

listavariables              : listavariables ',' identificador        { Simbolo simboloActual = checkearAlcance($3.sval, $3.ival);
                                                                        if (simboloActual != null && (!(simboloActual).getCategoria().equals("Identificador") || simboloActual.getTipo().equals("Lambda"))) {
                                                                          erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + $3.ival + ": El identificador '" + $3.sval + "' no es una variable");
                                                                        }
                                                                        $$ = $1;
                                                                        ((ArrayList<Simbolo>)$$.obj).add(simboloActual);  
                                                                      }
                            | identificador                           { Simbolo simboloActual = checkearAlcance($1.sval, $1.ival);
                                                                        if (simboloActual != null && (!(simboloActual).getCategoria().equals("Identificador") || simboloActual.getTipo().equals("Lambda"))) {
                                                                          erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + $1.ival + ": El identificador '" + $1.sval + "' no es una variable");
                                                                        }
                                                                        $$ = new ParserVal( new ArrayList<Simbolo>() );
                                                                        ((ArrayList<Simbolo>)$$.obj).add(simboloActual);
                                                                      }
                            ;

identificador               : ID                                      { $$ = new ParserVal( ((SimboloLong)$1.obj).getLexema() );
                                                                        $$.ival = $1.ival; 
                                                                      }
                            | identificador '.' ID                    { $$ = new ParserVal( $1.sval + "." + ((SimboloLong)$3.obj).getLexema() );
                                                                        $$.ival = $3.ival;
                                                                      }                       
                            ;


funcion                     : cabecera_funcion argumentos_funcion ')'                                         { enParamInvocacion = false;
                                                                                                                if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $3.ival + ": Se detectó la invocación de una función");
                                                                                                                if (!error() && $1.obj != null) {
                                                                                                                  ((ArbolSintactico)$1.obj).setUltimoDerecho((ArbolSintactico)$2.obj); 
                                                                                                                  $$ = $1;
                                                                                                                }
                                                                                                                $$.ival = $3.ival;
                                                                                                              }
                            | cabecera_funcion ')'                                                            { erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": faltan los argumentos en la invocación de función"); 
                                                                                                                enParamInvocacion = false;
                                                                                                                $$.ival = $2.ival; 
                                                                                                              }
                            ;

funcion_lambda              : cabecera_funcion argumento_id_lambda ')'                                        { enParamInvocacion = false;
                                                                                                                if ($1.obj != null && ((Simbolo)((ArbolSintactico)$1.obj).getLeft().getRefSimbolo()).getTipo() != "Lambda") {
                                                                                                                  erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + $3.ival + ": La función '" + $1.sval + "' no es una función Lambda");
                                                                                                                }
                                                                                                                if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $3.ival + ": Se detectó la invocación de una función lambda");
                                                                                                                chequeo_anid_lambda($1.sval, $3);
                                                                                                                if (!error() && $1.obj != null) {
                                                                                                                  ((ArbolSintactico)$1.obj).setUltimoDerecho( new ArbolSintactico((Simbolo)$2.obj) );
                                                                                                                  $$ = $1;
                                                                                                                  ((ArbolSintactico)$$.obj).setCategoria("CallLambda");
                                                                                                                }
                                                                                                                $$.ival = $3.ival;
                                                                                                              }
                            | cabecera_funcion CTE ')'                                                        { enParamInvocacion = false;
                                                                                                                if ($1.obj != null && ((Simbolo)((ArbolSintactico)$1.obj).getLeft().getRefSimbolo()).getTipo() != "Lambda") {
                                                                                                                  erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + $3.ival + ": La función '" + $1.sval + "' no es una función Lambda");
                                                                                                                }
                                                                                                                restriccionCteFloat($2);
                                                                                                                chequeo_anid_lambda($1.sval, $3); 
                                                                                                                constantePositiva($2); 
                                                                                                                if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $3.ival + ": Se detectó la invocación de una función lambda");
                                                                                                                if (!error() && $1.obj != null) {
                                                                                                                  ((ArbolSintactico)$1.obj).setUltimoDerecho( new ArbolSintactico((Simbolo)$2.obj) );
                                                                                                                  ((ArbolSintactico)$$.obj).setCategoria("CallLambda");
                                                                                                                }
                                                                                                                $$ = $1; $$.ival = $3.ival;
                                                                                                              }
                            | cabecera_funcion '-' CTE ')'                                                    { enParamInvocacion = false;
                                                                                                                if ($1.obj != null && ((Simbolo)((ArbolSintactico)$1.obj).getLeft().getRefSimbolo()).getTipo() != "Lambda") {
                                                                                                                  erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + $4.ival + ": La función '" + $1.sval + "' no es una función Lambda");
                                                                                                                }
                                                                                                                restriccionCteFloat($3);
                                                                                                                chequeo_anid_lambda($1.sval, $4); 
                                                                                                                constanteNegativa($3); 
                                                                                                                if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $4.ival + ": Se detectó la invocación de una función lambda");
                                                                                                                if (!error() && $1.obj != null) {
                                                                                                                  ((ArbolSintactico)$1.obj).setUltimoDerecho( new ArbolSintactico((Simbolo)$3.obj) );
                                                                                                                  $$ = $1;
                                                                                                                  ((ArbolSintactico)$$.obj).setCategoria("CallLambda");
                                                                                                                }
                                                                                                                $$.ival = $4.ival;
                                                                                                              }
                            | cabecera_funcion TRUNC '(' CTE ')' ')'                                          { enParamInvocacion = false;
                                                                                                                if ($1.obj != null && ((Simbolo)((ArbolSintactico)$1.obj).getLeft().getRefSimbolo()).getTipo() != "Lambda") {
                                                                                                                  erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + $6.ival + ": La función '" + $1.sval + "' no es una función Lambda");
                                                                                                                }
                                                                                                                constantePositiva($4);
                                                                                                                chequeo_anid_lambda($1.sval, $6);
                                                                                                                if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $6.ival + ": Se detectó la invocación de una función lambda");
                                                                                                                if (!error() && $1.obj != null) {
                                                                                                                  ((ArbolSintactico)$1.obj).setUltimoDerecho( new ArbolSintactico("Trunc", new ArbolSintactico((Simbolo)$4.obj), null) );
                                                                                                                  $$ = $1;
                                                                                                                  ((ArbolSintactico)$$.obj).setCategoria("CallLambda");
                                                                                                                }
                                                                                                                $$.ival = $6.ival;
                                                                                                              }
                            | cabecera_funcion TRUNC '(' '-' CTE ')' ')'                                      { enParamInvocacion = false;
                                                                                                                if ($1.obj != null && ((Simbolo)((ArbolSintactico)$1.obj).getLeft().getRefSimbolo()).getTipo() != "Lambda") {
                                                                                                                  erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + $7.ival + ": La función '" + $1.sval + "' no es una función Lambda");
                                                                                                                }
                                                                                                                constanteNegativa($5);
                                                                                                                chequeo_anid_lambda($1.sval, $7);
                                                                                                                if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $7.ival + ": Se detectó la invocación de una función lambda");
                                                                                                                if (!error() && $1.obj != null) {
                                                                                                                  ((ArbolSintactico)$1.obj).setUltimoDerecho( new ArbolSintactico("Trunc", new ArbolSintactico((Simbolo)$5.obj), null) );
                                                                                                                  $$ = $1;
                                                                                                                  ((ArbolSintactico)$$.obj).setCategoria("CallLambda");
                                                                                                                }
                                                                                                                $$.ival = $7.ival;
                                                                                                              }
                            ;

argumento_id_lambda         : identificador                                                     { Simbolo simboloActual = checkearAlcance($1.sval, $1.ival);
                                                                                                  $$ = new ParserVal(simboloActual);
                                                                                                  if (simboloActual != null && (!(simboloActual.getCategoria().equals("Identificador")) || simboloActual.getTipo().equals("Lambda"))) {
                                                                                                    erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + $1.ival + ": al usar un identificador como argumento en la invocación de una función Lambda, este debe ser una variable");
                                                                                                  }
                                                                                                }
                            ;

cabecera_funcion            : identificador '('                                                 { enParamInvocacion = true;
                                                                                                  String lexemaOriginal = $1.sval;
                                                                                                  $1.obj = checkearAlcance($1.sval, $2.ival);
                                                                                                  funcionInvocadaActual = (Simbolo) $1.obj;
                                                                                                  if ($1.obj != null && !error()) {
                                                                                                      $$ = new ParserVal(new ArbolSintactico("Call", new ArbolSintactico((Simbolo)$1.obj), null));
                                                                                                      $$.sval = lexemaOriginal;
                                                                                                  }
                                                                                                }
                            ;

argumentos_funcion          : listapinvocacion                              { if (!error()) {
                                                                                $$ = new ParserVal(new ArbolSintactico("ListaParametros", (ArbolSintactico)((ArbolSintactico[])$1.obj)[0], null) ); 
                                                                                for (int i = 1; i < ((ArbolSintactico[])$1.obj).length; i++) {
                                                                                  ((ArbolSintactico)$$.obj).setUltimoDerecho( new ArbolSintactico("ListaParametros", ((ArbolSintactico)((ArbolSintactico[])$1.obj)[i]), null) );
                                                                                }
                                                                              }
                                                                              if (contadorParametrosActual != ((SimboloFuncion)funcionInvocadaActual).getCantidadParametros()) {
                                                                                erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + $1.ival + ": La cantidad de parámetros en la invocación de la función '" + funcionInvocadaActual.getLexema() + "' no coincide con la cantidad de parámetros formales declarados");
                                                                              }
                                                                              contadorParametrosActual = 0;
                                                                              $$.ival = $1.ival;
                                                                            }
                            ;

listapinvocacion            : listapinvocacion ',' pinvocacion              { $$ = $1;
                                                                              int posParametro = ((SimboloFuncion)funcionInvocadaActual).getPosParametro($3.sval);
                                                                              if (posParametro != -1) {
                                                                                ((ArbolSintactico[])$$.obj)[((SimboloFuncion)funcionInvocadaActual).getPosParametro($3.sval)] = (ArbolSintactico)$3.obj; 
                                                                              }
                                                                              contadorParametrosActual++;
                                                                              $$.ival = $3.ival;
                                                                            }
                            | pinvocacion                                   { $$ = new ParserVal(new ArbolSintactico[((SimboloFuncion)funcionInvocadaActual).getCantidadParametros()]);
                                                                              int posParametro = ((SimboloFuncion)funcionInvocadaActual).getPosParametro($1.sval);
                                                                              if (posParametro != -1) {
                                                                                ((ArbolSintactico[])$$.obj)[((SimboloFuncion)funcionInvocadaActual).getPosParametro($1.sval)] = (ArbolSintactico)$1.obj;
                                                                              }
                                                                              contadorParametrosActual++;
                                                                              $$.ival = $1.ival;
                                                                            } 
                            ;

pinvocacion                 : expresion FLECHA ID                                                           { checkeoParametroDeInvocacion(((Simbolo)$3.obj).getLexema(), $3.ival, (ArbolSintactico)$1.obj);
                                                                                                              if (!error()) {
                                                                                                                $$ = new ParserVal( (ArbolSintactico)$1.obj );
                                                                                                                $$.sval = ((Simbolo)$3.obj).getLexema();
                                                                                                              }
                                                                                                              $$.ival = $3.ival;
                                                                                                            }                                                                     
                            | cabecera_declaracion_lambda '{'  listasentenciasejecutables '}' FLECHA ID     { checkeoParametroDeInvocacion(((Simbolo)$6.obj).getLexema(), $6.ival, (ArbolSintactico)$3.obj); 
                                                                                                              if (!error()) {
                                                                                                                funcLambdaDeclaradas++;
                                                                                                                tablaSimbolos.remove($1.sval);
                                                                                                                $$ = new ParserVal(new ArbolSintactico( "DeclaracionLambda", (ArbolSintactico)$3.obj , null));
                                                                                                                $$.sval = ((Simbolo)$6.obj).getLexema();
                                                                                                                enDeclaracionLambda = false;
                                                                                                              }
                                                                                                              $$.ival = $6.ival;
                                                                                                            }
                            | cabecera_declaracion_lambda '{' listasentenciasejecutables FLECHA ID          {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $4.ival + ": falta llave de cierre del cuerpo de la función Lambda"); enDeclaracionLambda = false; $$.ival = $4.ival;}
                            | cabecera_declaracion_lambda listasentenciasejecutables '}' FLECHA ID          {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $3.ival + ": falta llave de apertura del cuerpo de la función Lambda"); enDeclaracionLambda = false; $$.ival = $3.ival;}
                            | cabecera_declaracion_lambda listasentenciasejecutables FLECHA ID              {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $3.ival + ": faltan ambas llaves del cuerpo de la función Lambda"); enDeclaracionLambda = false; $$.ival = $3.ival;}
                            | cabecera_declaracion_lambda '{' listasentenciasejecutables '}' FLECHA         {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $5.ival + ": No se especifica el parametro formal al que corresponde el parámetro real"); enDeclaracionLambda = false; $$.ival = $5.ival;}
                            | expresion FLECHA                                                              {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": No se especifica el parametro formal al que corresponde el parámetro real"); $$.ival = $2.ival;}
                            ;

cabecera_declaracion_lambda : '(' tipo ID ')' { enDeclaracionLambda = true;
                                                if ($2.sval.equals("Lambda")) {
                                                  erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + $4.ival + ": el tipo 'Lambda' no puede ser utilizado como el tipo del parámetro en la declaración de funciones Lambda");
                                                }
                                                String lexema = ((SimboloLong)$3.obj).getLexema();
                                                if (tablaSimbolos.containsKey("lambda:" + lexema)) {
                                                  erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + $3.ival + ": la variable '" + lexema + "' ya ha sido declarada");
                                                }
                                                tablaSimbolos.remove(lexema);
                                                tablaSimbolos.put("lambda:" + lexema,new SimboloLong("Long", "Identificador", "lambda:" + lexema, 0L));
                                                $$ = new ParserVal("lambda:" + lexema);
                                              }
                            | tipo ID ')'     { enDeclaracionLambda = true;
                                                erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta el paréntesis de apertura en la declaración del argumento de la función Lambda");
                                                if ($1.sval.equals("Lambda")) {
                                                  erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + $3.ival + ": el tipo 'Lambda' no puede ser utilizado como el tipo del parámetro en la declaración de funciones Lambda");
                                                }
                                                String lexema = ((SimboloLong)$2.obj).getLexema();
                                                if (tablaSimbolos.containsKey("lambda:" + lexema)) {
                                                  erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + $2.ival + ": la variable '" + lexema + "' ya ha sido declarada");
                                                }
                                                tablaSimbolos.remove(lexema);
                                                tablaSimbolos.put("lambda:" + lexema,new SimboloLong("Long", "Identificador", "lambda:" + lexema, 0L));
                                                $$ = new ParserVal("lambda:" + lexema);
                                              }
                            | '(' tipo ID   { enDeclaracionLambda = true;
                                              erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $3.ival + ": falta el paréntesis de cierre en la declaración del argumento de la función Lambda");
                                              if ($2.sval.equals("Lambda")) {
                                                erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + $3.ival + ": el tipo 'Lambda' no puede ser utilizado como el tipo del parámetro en la declaración de funciones Lambda");
                                              }
                                              String lexema = ((SimboloLong)$2.obj).getLexema();
                                              if (tablaSimbolos.containsKey("lambda:" + lexema)) {
                                                erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + $2.ival + ": la variable '" + lexema + "' ya ha sido declarada");
                                              }
                                              tablaSimbolos.remove(lexema);
                                              tablaSimbolos.put("lambda:" + lexema,new SimboloLong("Long", "Identificador", "lambda:" + lexema, 0L));
                                              $$ = new ParserVal("lambda:" + lexema);
                                            }
                            ;                           

expresion                   : expresion '+' termino                 { if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $2.ival + ": Se detectó una expresión desde una suma");
                                                                      if (!error()) {
                                                                        $$ = new ParserVal(new ArbolSintactico("Suma", (ArbolSintactico)$1.obj, (ArbolSintactico)$3.obj));
                                                                      }
                                                                      $$.ival = $2.ival;
                                                                    }      
                            | expresion '-' termino                 { if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $2.ival + ": Se detectó una expresión desde una resta");
                                                                      if (!error()) { $$ = new ParserVal(new ArbolSintactico("Resta", (ArbolSintactico)$1.obj, (ArbolSintactico)$3.obj));} $$.ival = $2.ival; 
                                                                    }
                            | error '+' termino                     { erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta operando a la izquierda de '+'"); }
                            | expresion '+' error                   { erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta operando a la derecha de '+'"); }
                            | error '-' termino                     { erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta operando a la izquierda de '-'"); }
                            | expresion '-' error                   { erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta operando a la derecha de '-'"); }
                            | termino                               { if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $1.ival + ": Se detectó una expresión desde un término");    
                                                                      $$ = $1;
                                                                    }
                            ;

termino                     : termino '*' factor                    { if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $2.ival + ": Se detectó un término desde una multiplicación");
                                                                      if (!error()) { $$ = new ParserVal(new ArbolSintactico("Multiplicacion", (ArbolSintactico)$1.obj, (ArbolSintactico)$3.obj));} $$.ival = $2.ival;
                                                                    }
                            | termino '/' factor                    { if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $2.ival + ": Se detectó un término desde una división");
                                                                      if (!error()) { $$ = new ParserVal(new ArbolSintactico("Division", (ArbolSintactico)$1.obj, (ArbolSintactico)$3.obj)); } $$.ival = $2.ival; 
                                                                    }
                            | error '*' factor                      { erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta operando a la izquierda de '*'"); }
                            | termino '*' error                     { erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta operando a la derecha de '*'"); }
                            | error '/' factor                      { erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta operando a la izquierda de '/'"); }
                            | termino '/' error                     { erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta operando a la derecha de '/'"); }
                            | factor                                { if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $1.ival + ": Se detectó un término desde un factor");
                                                                      $$ = $1; 
                                                                    }
                            | termino CTE                           { constantePositiva($2); restriccionCteFloat($2); erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta operador/comparador entre término y constante"); }
                            ;

factor                      : CTE                                   { constantePositiva($1); if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $1.ival + ": Se detectó un factor desde una constante");
                                                                      restriccionCteFloat($1);
                                                                      if (!error()) { $$ = new ParserVal(new ArbolSintactico((Simbolo)$1.obj));} $$.ival = $1.ival;
                                                                    }
                            | '-' CTE                               { constanteNegativa($2); if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $2.ival + ": Se detectó un factor desde una constante negativa");
                                                                      restriccionCteFloat($2);
                                                                      if (!error()) { $$ = new ParserVal(new ArbolSintactico((Simbolo)$2.obj));} $$.ival = $2.ival;
                                                                    }
                            | identificador                         { if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $1.ival + ": Se detectó un factor desde un identificador");
                                                                      Simbolo simboloActual = checkearAlcance($1.sval, $1.ival);
                                                                      if (!error()) {
                                                                        $$ = new ParserVal(new ArbolSintactico(simboloActual));
                                                                        $$.ival = $1.ival;
                                                                      }
                                                                      if ( simboloActual != null && (!simboloActual.getCategoria().equals("Identificador") || simboloActual.getTipo().equals("Lambda"))) {
                                                                        erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + $1.ival + ": el identificador '" + $1.sval + "' no es una variable");
                                                                      }
                                                                    }
                            | funcion                               { if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $1.ival + ": Se detectó un factor desde una función");
                                                                      $$ = $1;
                                                                    }
                            | funcion_lambda                        { erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + $1.ival + ": Las funciones Lambda no devuelven valor y no pueden usarse en expresiones"); }
                            | TRUNC '(' CTE ')'                     { constantePositiva($3);
                                                                      if (enParamInvocacion) {
                                                                        if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $1.ival + ": Se detectó la función Trunc en uno de los parámetros reales de la invocación");
                                                                        if (!error()) {
                                                                          $$ = new ParserVal(new ArbolSintactico("Trunc", new ArbolSintactico((Simbolo)$3.obj), null));
                                                                        }
                                                                        $$.ival = $1.ival;
                                                                      }
                                                                      else
                                                                        erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + $1.ival + ": la función TRUNC solo puede utilizarse en parámetros reales de invocación de funciones");
                                                                    }
                            | TRUNC '(' '-' CTE ')'                 { constanteNegativa($4); 
                                                                      if (enParamInvocacion) {
                                                                        if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $1.ival + ": Se detectó la función Trunc en uno de los parámetros reales de la invocación");
                                                                        if (!error()) {
                                                                          $$ = new ParserVal(new ArbolSintactico("Trunc", new ArbolSintactico((Simbolo)$4.obj), null));
                                                                        }
                                                                        $$.ival = $1.ival;
                                                                      }
                                                                      else  
                                                                        erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + $1.ival + ": la función TRUNC solo puede utilizarse en parámetros reales de invocación de funciones");
                                                                    }
                            ;

listaconstantes             : listaconstantes ',' CTE               { constantePositiva($3); restriccionCteFloat($3); $$ = $1; ((ArrayList<Simbolo>)$$.obj).add((Simbolo)$3.obj); $$.ival = $3.ival; }
                            | listaconstantes ',' '-' CTE           { constanteNegativa($4); restriccionCteFloat($4); $$ = $1; ((ArrayList<Simbolo>)$$.obj).add((Simbolo)$4.obj); $$.ival = $4.ival; }
                            | CTE                                   { constantePositiva($1); restriccionCteFloat($1); $$ = new ParserVal(new ArrayList<Simbolo>()); ((ArrayList<Simbolo>)$$.obj).add((Simbolo)$1.obj); $$.ival = $1.ival; }
                            | '-' CTE                               { constanteNegativa($2); restriccionCteFloat($2); $$ = new ParserVal(new ArrayList<Simbolo>()); ((ArrayList<Simbolo>)$$.obj).add((Simbolo)$2.obj); $$.ival = $2.ival; }
                            | listaconstantes CTE                   { constantePositiva($2); restriccionCteFloat($2); erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": falta ',' entre constantes"); constantePositiva($2); restriccionCteFloat($2); }
                            | listaconstantes '-' CTE               { constanteNegativa($3); restriccionCteFloat($3); erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": falta ',' entre constantes"); constanteNegativa($3); restriccionCteFloat($3); }
                            | listaconstantes ',' identificador     {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": la variable '" + $3.sval + "' no puede estar en una lista de constantes");}
                            ;

lista_expresiones           : listavariables ',' listaconstantes    { $$ = $1; ((ArrayList<Simbolo>)$$.obj).addAll( ((ArrayList<Simbolo>)$3.obj) ); $$.ival = $3.ival; }
                            ;


%%

private Lexico lexer;
private Map<String, Simbolo> tablaSimbolos;
private ArrayList<String> erroresSintacticos = new ArrayList<>();
private ArrayList<String> erroresLexicos = new ArrayList<>();
private ArrayList<String> erroresSemanticos = new ArrayList<>();
private ArrayList<String> warnings = new ArrayList<>();
private int enFunc = 0;
private int nivel = -1;
private int returnsIf = 0;
private int returnsFunc = 1;
private int contadorParametrosActual = 0;
private Stack<Boolean> huboRetStack = new Stack<>();
private Stack<Boolean> sinRetIf = new Stack<>();
private boolean enDo = false;
private boolean sinRet = false;
private boolean huboIfEnFunc = false;
private boolean enParamInvocacion = false;
private boolean enDeclaracionParam = false;
private boolean enDeclaracionLambda = false;
private StringBuilder stringAmbitos = new StringBuilder();
private ArrayList<Parametro> parametrosActuales = new ArrayList<>();
private Stack<ArrayList<Parametro>> stackParametrosActuales = new Stack<>();
private Stack<String> stackTipoFuncActual = new Stack<>();
private ArbolSintactico arbolSintactico;
private Map<String, ArbolSintactico> mapaFunciones = new HashMap<>();
private Simbolo funcionInvocadaActual = null;
private int funcLambdaDeclaradas = 0;
private boolean verbose = false;  

// Stacks para guardar el contexto en funciones anidadas
private Stack<Integer> stackNivel = new Stack<>();
private Stack<Integer> stackReturnsIf = new Stack<>();
private Stack<Integer> stackReturnsFunc = new Stack<>();
private Stack<Boolean> stackSinRet = new Stack<>();
private Stack<Boolean> stackHuboIfEnFunc = new Stack<>();
private Stack<Stack<Boolean>> stackHuboRetStack = new Stack<>();

public Parser(Map<String, Simbolo> tablaSimbolos, String path, ArbolSintactico arbolSintactico, Map<String, ArbolSintactico> mapaFunciones) {
  this.tablaSimbolos = tablaSimbolos;
  this.lexer = new Lexico(tablaSimbolos,path, erroresLexicos, warnings);
  this.arbolSintactico = arbolSintactico;
  this.mapaFunciones = mapaFunciones;
}

public void setVerbose(boolean verbose) {
  this.verbose = verbose;
  this.lexer.setVerbose(verbose);
}

private void yyerror(String s) {
    erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error: " + s);
}

private boolean error() {
  return (!erroresLexicos.isEmpty() || !erroresSintacticos.isEmpty() );
}


public ArrayList<String> getErroresSintacticos(){
  return erroresSintacticos;
}

public ArrayList<String> getErroresSemanticos(){
  return erroresSemanticos;
}

public ArrayList<String> getErroresLexicos(){
  return erroresLexicos;
}

public ArrayList<String> getWarnings(){
  return warnings;
}

public int getFuncLambdaDeclaradas(){
  return funcLambdaDeclaradas;
}

private void constanteNegativa(ParserVal valCte) {
  if (((Simbolo)valCte.obj).getTipo().equals("Long")) {
    Simbolo simbolo;
    if (!tablaSimbolos.containsKey("-" +((SimboloLong)valCte.obj).getLexema())) {
      simbolo =  new SimboloLong("Long", "Constante", "-" + ((SimboloLong)valCte.obj).getLexema(), -((SimboloLong)valCte.obj).getValor());
      tablaSimbolos.put("-" +((SimboloLong)valCte.obj).getLexema(), simbolo);
    } else {
      simbolo = tablaSimbolos.get("-" + ((SimboloLong)valCte.obj).getLexema());
    }
    valCte.obj = simbolo;
  } else {
        Simbolo simbolo;
        if (!tablaSimbolos.containsKey("-" +((SimboloCteFloat)valCte.obj).getLexema())) {
          simbolo =  new SimboloCteFloat("Float", "Constante", "-" + ((SimboloCteFloat)valCte.obj).getLexema(), -((SimboloCteFloat)valCte.obj).getValor());
          tablaSimbolos.put("-" + ((SimboloCteFloat)valCte.obj).getLexema(), simbolo);
        } else {
          simbolo = tablaSimbolos.get("-" + ((SimboloCteFloat)valCte.obj).getLexema());
        }
        valCte.obj = simbolo;
    }
}

private void constantePositiva(ParserVal valCte) {
    if ( ((Simbolo)valCte.obj).getTipo().equals("Long") && ( ((SimboloLong) valCte.obj).getValor() == Compilador.INT32_MAX + 1) ) {
      Simbolo simbolo;
      if (!tablaSimbolos.containsKey(Compilador.INT32_MAX + "L")) {
        simbolo =  new SimboloLong("Long", "Constante", Compilador.INT32_MAX + "L", Compilador.INT32_MAX);
        tablaSimbolos.put(Compilador.INT32_MAX + "L", simbolo);
      } else {
        simbolo = tablaSimbolos.get(Compilador.INT32_MAX + "L");
      }
      valCte.obj = simbolo;
      warnings.add("||================ ANALIZADOR SINTÁCTICO: Warning en línea " + valCte.ival + ": el valor de la constante entera es mayor al máximo permitido, se asigna el valor " + Compilador.INT32_MAX);
    }
}

private void chequeoSentenciaReturn(ParserVal yyval) {
  if (enFunc == 0 || enDeclaracionLambda ) {
    erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + yyval.ival + ": Uso de sentencia return fuera de una función estándar");
  } else if (nivel > -1) {
    returnsIf--;
    huboRetStack.pop();
    huboRetStack.push(true);
  } else {
    returnsFunc --;
  }
}

private void inicioFunc() {
  // Guardar estado actual en los stacks
  stackNivel.push(nivel);
  stackReturnsIf.push(returnsIf);
  stackReturnsFunc.push(returnsFunc);
  stackSinRet.push(sinRet);
  stackHuboIfEnFunc.push(huboIfEnFunc);
  stackHuboRetStack.push(huboRetStack);
  
  // Reiniciar variables para la nueva función
  nivel = -1;
  returnsIf = 0;
  returnsFunc = 1;
  sinRet = false;
  huboIfEnFunc = false;
  huboRetStack = new Stack<>();
  
  enFunc++;
}

private void inicioIf() {
  if(enFunc > 0 && !enDo) {
    huboIfEnFunc = true;
    huboRetStack.push(false);
    nivel++;
    returnsIf++;    
  }
}

private void finIf(ParserVal yyval) {
  if (enFunc > 0 && !enDo) {
    
    //Detección de multiples retornos
    if (returnsIf < nivel) {
      erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + yyval.ival + ": Se han detectado multiples sentencias de retorno en el IF");
      returnsIf = nivel + 1; // Reseteo de returnsIf para evitar propagación erronea de errores
    }
    
    // Detección de ausencia de retorno
    if (!huboRetStack.pop()) {
      sinRet = true;
      returnsIf--;
    } else if (nivel==returnsIf) { // Ausencia de retorno válida por cobertura de nivel anterior
      sinRet = false;
    }

    nivel--;
  }
}

private void finFunc(ParserVal yyval) {
  if (returnsFunc > 0 && (!huboIfEnFunc || sinRet)) { 
    erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + yyval.ival + ": La función no tiene sentencia return en todos sus caminos de ejecución");
  } else if (returnsFunc < 0) {
    erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + yyval.ival + ": Se han detectado multiples sentencias de retorno en la función");
  }
  enFunc--;
  
  // Restaurar estado previo desde los stacks
  if (!stackNivel.isEmpty()) {
    nivel = stackNivel.pop();
    returnsIf = stackReturnsIf.pop();
    returnsFunc = stackReturnsFunc.pop();
    sinRet = stackSinRet.pop();
    huboIfEnFunc = stackHuboIfEnFunc.pop();
    huboRetStack = stackHuboRetStack.pop();
  } else {
    // Reseteo por defecto si no hay contexto previo
    nivel = -1;
    returnsIf = 0;
    returnsFunc = 1;
    sinRet = false;
    huboIfEnFunc = false;
    huboRetStack = new Stack<>();
  }
}

private void cambiarASimboloFuncion (Simbolo simboloID) {
  if (simboloID != null) {
    tablaSimbolos.remove(simboloID.getLexema());
    // Creación del símbolo de función
    Simbolo nuevoSimbolo = new SimboloFuncion(stackTipoFuncActual.pop(), "Funcion", simboloID.getLexema(), stackParametrosActuales.pop());
    tablaSimbolos.put(simboloID.getLexema(), nuevoSimbolo);
  }
}

private void quitarAmbito() {
    int lastIndex = stringAmbitos.lastIndexOf(":");
    if (lastIndex > 0) {
        stringAmbitos.setLength(lastIndex);
    } else {
        stringAmbitos.setLength(0);
    }
}

private void renombrarVariable(ParserVal yyval) {
  String lexemaViejo = ((Simbolo)yyval.obj).getLexema();
  yyval.obj = tablaSimbolos.get(lexemaViejo);
  String lexemaNuevo = lexemaViejo + stringAmbitos.toString();
  ((Simbolo)yyval.obj).setLexema(lexemaNuevo);
  if (tablaSimbolos.containsKey(lexemaNuevo)) {
    erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + yyval.ival + ": La variable '" + ((Simbolo)yyval.obj).getLexema().substring(0, ((Simbolo)yyval.obj).getLexema().indexOf(":")) + "' ya ha sido declarada en el ámbito actual");
    yyval.obj = tablaSimbolos.get(lexemaNuevo);
  } else {
    tablaSimbolos.put(lexemaNuevo, ((Simbolo)yyval.obj));
  }
  tablaSimbolos.remove(lexemaViejo);
}

private void chequeo_anid_lambda(String nombreLambda, ParserVal yyval) {
  if (nombreLambda.contains(".")) {
    //Si el ámbito de la lambda no es padre del ámbito actual, tira error
    String ambitoVarActual = (":" + nombreLambda.substring(0, nombreLambda.lastIndexOf("."))).replace(".", ":");
    // P.F.F2.L() ->  ambitoVarActual = :P:F:F2
    if (!stringAmbitos.toString().contains(ambitoVarActual)) {
    erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + yyval.ival + ": Se ha intentado invocar una función lambda perteneciente a un ámbito no accesible");
    }
  }
}

// Esta función toma el Lexema de un identificador con la forma escrita por el programador (puede contener "."), 
// busca y retorna de la tabla de símbolos el símbolo correspondiente, teniendo en cuenta los ámbitos.
private Simbolo checkearAlcance(String lexema, int nroLinea) {

  // AuxSA se vuelve una copia de StringAmbitos para ir recortándola
  String auxSA;
  if (enDeclaracionLambda) {
    if (funcionInvocadaActual == null) { // En caso de que la función invocada actual no exista o no esté al alcance, no reviso los alcances de las variables en las sentencias de la lambda
      return null;
    }
    // En caso de estar dentro de una función lambda, el ámbito actual se toma desde la función invocada actual, moviendo el lexema de la del inicio al final.
    String lexemaFunc = funcionInvocadaActual.getLexema();
    String nombreFunc = lexemaFunc.substring(0, lexemaFunc.indexOf(":"));
    String ambitoFunc = lexemaFunc.substring(lexemaFunc.indexOf(":"));
    auxSA = ambitoFunc + ":" + nombreFunc;
  } else
    auxSA = stringAmbitos.toString();

  // Cuando el Lexema es una variable no anidada:
  if (!lexema.contains(".")) {
    String auxLexema = lexema + auxSA;

    while (!tablaSimbolos.containsKey(auxLexema) && auxSA.length() > 0) {
      auxSA = auxSA.substring(0, auxSA.lastIndexOf(":"));
      auxLexema = lexema + auxSA;
    }

    // Tira error si no se encuentra en el ámbito actual o los superiores (A no ser que sea una variable de función lambda, que se guarda sin ámbito por decisión de diseño)
    if (auxLexema.equals(lexema) && !tablaSimbolos.containsKey("lambda:" + auxLexema)) {
      erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + nroLinea + ": '" + lexema + "' no ha sido declarada en el ámbito actual y tampoco está al alcance");
      tablaSimbolos.remove(lexema);
      return null;
    } else {
      tablaSimbolos.remove(lexema);
      if (auxLexema.equals(lexema)) { 
        return tablaSimbolos.get("lambda:" + auxLexema);
      } else
        return tablaSimbolos.get(auxLexema);
    }

  // Cuando el Lexema es una variable anidada:    
  } else { 
    String varActual = lexema.substring(lexema.lastIndexOf(".") + 1);
    String ambitoVarActual = (":" + lexema.substring(0, lexema.lastIndexOf("."))).replace(".", ":");
    // P.F.F2.X -> varActual = X ; ambitoVarActual = :P:F:F2

    // Dado que estaba anidada, se va a buscar el ámbito a partir de stringAmbitos seguido del ambito escrito en la variable
    while (!tablaSimbolos.containsKey(varActual + auxSA + ambitoVarActual) && auxSA.length() > 0) {
      auxSA = auxSA.substring(0, auxSA.lastIndexOf(":"));
    }

    if (!tablaSimbolos.containsKey(varActual + auxSA + ambitoVarActual)) {
      erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + nroLinea + ": '" + lexema + "' no ha sido declarada en el ámbito actual y tampoco está al alcance");
      for (String ambitoPart : lexema.split("\\.")) {
        tablaSimbolos.remove(ambitoPart);
      }
      return null;
    } else {
      for (String ambitoPart : lexema.split("\\.")) {
        tablaSimbolos.remove(ambitoPart);
      }
      if (!stringAmbitos.toString().contains(auxSA + ambitoVarActual)) { // Si el ámbito donde se encontró la variable no está dentro del ámbito actual, tira error
        erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + nroLinea + ": '" + lexema + "' no está al alcance desde el ámbito actual");
      } 
      return tablaSimbolos.get(varActual + auxSA + ambitoVarActual);
    }
  }
}

// Función encargada de dos cosas: en base a conocer el parámetro real y el parámetro formal intencionado, chequea si ese parámetro formal verdaderamente existe en la función invocada,
// y, si existe, verifica también que, en caso de ser CVR, el parámetro real no sea una constante ni una expresión.
private void checkeoParametroDeInvocacion(String lexemaParametroFormal, int nroLinea, ArbolSintactico arbolParametroReal) {
  if (funcionInvocadaActual != null && funcionInvocadaActual.getCategoria().equals("Funcion")) {
    String lexemaFuncion = ((SimboloFuncion)funcionInvocadaActual).getLexema();
    Parametro parametroFormal = ((SimboloFuncion)funcionInvocadaActual).getRefParametro(lexemaParametroFormal);
    if (parametroFormal == null) {
      erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + nroLinea + ": el parámetro formal '" + lexemaParametroFormal + "' no existe en la función '" + lexemaFuncion.substring(0, lexemaFuncion.indexOf(':')) + "'");    
    } else if (arbolParametroReal != null) {
      // Solo chequeamos CVR si el árbol existe
      if ((arbolParametroReal.getCategoria() != null || arbolParametroReal.getRefSimbolo().getCategoria().equals("Constante")) && parametroFormal.getPasaje().equals("CVR")) {
        erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + nroLinea + ": el parámetro real asociado al parámetro formal '" + lexemaParametroFormal + "' de la función '" + lexemaFuncion.substring(0, lexemaFuncion.indexOf(':')) + "' es una constante u expresión y no puede pasarse por Copia-Valor-Resultado (CVR)");
      }
    }
  }
  tablaSimbolos.remove(lexemaParametroFormal);
}
private void restriccionCteFloat(ParserVal valCte) {
  if (((Simbolo)valCte.obj).getTipo().equals("Float")) {
    erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + valCte.ival + ": Uso incorrecto de constante Float por incompatibilidad de tipos");
    tablaSimbolos.remove(((SimboloCteFloat)valCte.obj).getLexema());
  }
}

private ArbolSintactico generarAsigMultiple(ArrayList<Simbolo> listaVariables, ArrayList<Simbolo> listaConstantes, int nroLinea) {
  int i = 1; 
  ArbolSintactico arbolRetorno = new ArbolSintactico("Sentencia", new ArbolSintactico("Asig", new ArbolSintactico(listaVariables.get(0)), new ArbolSintactico(listaConstantes.get(0))), null );                                                                                            

  while (i < listaConstantes.size()) {
    ArbolSintactico nuevaAsignacion = new ArbolSintactico( "Sentencia", new ArbolSintactico("Asig", new ArbolSintactico(listaVariables.get(i)), new ArbolSintactico(listaConstantes.get(i))), null);
    arbolRetorno.setUltimoDerecho(nuevaAsignacion);
    i++;
  }
  if (i < listaVariables.size()) {
    warnings.add("||================ ANALIZADOR SINTÁCTICO: Warning en línea " + nroLinea+ ": hay más variables que constantes en la asignación múltiple. Las variables sobrantes se inicializan en 0L");
    SimboloLong simboloCero = (SimboloLong)tablaSimbolos.get("0L");
    if (simboloCero == null) {
      simboloCero = new SimboloLong("Long", "Constante", "0L", 0L);
      tablaSimbolos.put("0L", simboloCero);
    }
    while (i < listaVariables.size()) {
      ArbolSintactico nuevaAsignacion = new ArbolSintactico( "Sentencia", new ArbolSintactico("Asig", new ArbolSintactico(listaVariables.get(i)), new ArbolSintactico(simboloCero)), null);
      arbolRetorno.setUltimoDerecho(nuevaAsignacion);
      i++;
    }
  } 
  return arbolRetorno;
}
