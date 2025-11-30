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
prog                        : ID {stringAmbitos.append(":"+((Simbolo)$1.obj).getLexema());} bloque_sentencias {quitarAmbito();} EOF   {if (!error() && $3.obj != null && $3.obj instanceof ArbolSintactico) { arbolSintactico.adoptarNodoIntermedio((ArbolSintactico)$3.obj);}}
                            | bloque_sentencias EOF                     {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": falta el nombre del programa");} 
                            //| ID '{' error ';' listasentencias '}' EOF  {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico: el programa no es válido");}
                            //| ID '{' listasentencias error '}' EOF      {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico: el programa no es válido");}
                            //| ID '{' listasentencias error ';' listasentencias '}' EOF      {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico: el programa no es válido");}
                            ;

bloque_sentencias           : '{' listasentencias '}'                   { $$ = $2; }          
                            | '{' '}'
                            | '{' listasentencias                       {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta llave de cierre del bloque de sentencias");}
                            | listasentencias '}'                       {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": falta llave de apertura del bloque de sentencias");}
                            | listasentencias                           {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": faltan ambas llaves del bloque de sentencias");}
                            ;

listasentencias             : listasentencias sentencia                 { if (!error()) {
                                                                            if ($1.obj != null && $2.obj != null) { ((ArbolSintactico)$1.obj).setUltimoDerecho(new ArbolSintactico("Sentencia", ((ArbolSintactico)$2.obj) , null )); $$ = $1; }
                                                                            else if ($2.obj != null) { $$= new ParserVal(new ArbolSintactico("Sentencia", ((ArbolSintactico)$2.obj), null)); } 
                                                                          } 
                                                                        }
                            | sentencia                                 { if (!error()) { if ($1.obj != null) { $$ = new ParserVal(new ArbolSintactico("Sentencia", ((ArbolSintactico)$1.obj), null)); }} }
                            ;

sentencia                   : sentenciaejecutable ';'                   { $$ = $1;}
                            | declaracion ';'                           { $$.obj = null;}
                            | sentenciaejecutable                       {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": falta ; al final de la sentencia ejecutable");}
                            | declaracion                               {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": falta ; al final de la declaración");}
                            ;

listasentenciasejecutables  : listasentenciasejecutables sentenciaejecutable ';'  { if (!error()) {
                                                                                    ((ArbolSintactico)$1.obj).setUltimoDerecho(new ArbolSintactico("Sentencia", ((ArbolSintactico)$2.obj) , null ));
                                                                                    $$ = $1;
                                                                                    }
                                                                                  }
                            | sentenciaejecutable ';'                             { if (!error()) { $$ = new ParserVal(new ArbolSintactico("Sentencia", ((ArbolSintactico)$1.obj), null)); } }                         
                            | listasentenciasejecutables sentenciaejecutable      {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta ; al final de la sentencia ejecutable");}
                            | sentenciaejecutable                                 {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": falta ; al final de la sentencia ejecutable");}
                            ;

sentenciaejecutable         : asignacion                                      { $$ = $1; }
                            | control                                         { $$ = $1; }  
                            | funcion                                         { erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + $1.ival + ": No está permitida la invocación de funciones sin utilizar su retorno"); }
                            | print                                           { System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $1.ival + ": Se detectó un print");
                                                                                $$ = $1;
                                                                              }
                            | funcion_lambda                                  { if(enFunc==0) {erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + $1.ival + ": No está permitida la invocación de función lambda sin utilizar un retorno ni fuera de una función");} 
                                                                                else { erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + $1.ival + ": No está permitida la invocación de funciones sin utilizar su retorno"); } 
                                                                              }
                            | sentencia_return                                {chequeoSentenciaReturn($1); $$ = $1;}
                            ;

declaracion                 : tipo listaIDs                                   { System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $2.ival + ": Se detectó una declaración de variables");}
                            | tipo id_funcion {String idFuncRaw = ((Simbolo)$2.obj).getLexema(); renombrarVariable($2); stringAmbitos.append(":"+idFuncRaw); stackTipoFuncActual.push($1.sval); enDeclaracionParam = true;} parametros_declaracion_f {enDeclaracionParam = false; stackParametrosActuales.push(parametrosActuales); cambiarASimboloFuncion((Simbolo)$2.obj); parametrosActuales = new ArrayList<>(); enFunc++;} '{' listasentencias '}' {  
                                                                                                                                                                                                                                                                    finFunc($8); 
                                                                                                                                                                                                                                                                    quitarAmbito(); 
                                                                                                                                                                                                                                                                    System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $8.ival + ": Se detectó una declaración de función");
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

sentencia_return            : RETURN '(' expresion ')'                      { System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $3.ival + ": Se detectó una sentencia return");
                                                                              if (!error()) { $$ = new ParserVal(new ArbolSintactico("Return", (ArbolSintactico)$3.obj,  null)); }
                                                                            }
                            | RETURN '(' ')'                                 {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $3.ival + ": falta de argumento en sentencia return");}
                            ;

asignacion                  : identificador ASIG expresion                                { System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $3.ival + ": Se detectó una asignación simple");
                                                                                            if (!error()) { $$ = new ParserVal( new ArbolSintactico("Asig", new ArbolSintactico(checkearAlcance($1.sval, $1.ival)), (ArbolSintactico)$3.obj) ); }
                                                                                          }
                            | listavariables '=' listaconstantes                          { System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $3.ival + ": Se detectó una asignación múltiple");
                                                                                            if(((ArrayList<Simbolo>)$1.obj).size() >= ((ArrayList<Simbolo>)$3.obj).size() && !error()) {  
                                                                                              $$ = new ParserVal(generarAsigMultiple((ArrayList<Simbolo>)$1.obj, (ArrayList<Simbolo>)$3.obj));
                                                                                            }
                                                                                            else erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + $3.ival + ": La cantidad de constantes en la asignación múltiple es mayor a la cantidad de variables");
                                                                                          }
                            ;

bloque_control_estructura   : '{' listasentenciasejecutables '}'              { $$ = $2; }
                            | sentenciaejecutable ';'                         { { if (!error()) { $$ = new ParserVal(new ArbolSintactico("Sentencia", ((ArbolSintactico)$1.obj), null)); } }  }
                            | '{''}'                                          { $$.obj = null; }
                            ;

bloque_control              : {inicioIf();} bloque_control_estructura {finIf($2); $$ = $2; }
                            ;

inicio_do                   : DO {enDo=true;}
                            ;

bloque_control_else         : {sinRetIf.push(sinRet);} bloque_control {if(sinRetIf.pop()) {sinRet=true;}; $$ = $2; }
                            ;

control                     : IF condicion bloque_control ENDIF                                 { System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $4.ival + ": Se detectó un bloque IF");
                                                                                                  if (!error()) { $$ = new ParserVal(new ArbolSintactico("If", (ArbolSintactico)$2.obj, new ArbolSintactico("CuerpoIf",(ArbolSintactico)$3.obj, null)) ); }
                                                                                                }
                            | IF condicion bloque_control ELSE bloque_control_else ENDIF        { System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $6.ival + ": Se detectó un bloque IF-ELSE");
                                                                                                  if (!error()) { $$ = new ParserVal(new ArbolSintactico("IfElse", (ArbolSintactico)$2.obj, new ArbolSintactico("CuerpoIfElse",(ArbolSintactico)$3.obj, (ArbolSintactico)$5.obj)) ); }  
                                                                                                } 
                            | inicio_do bloque_control UNTIL condicion                          { enDo=false;
                                                                                                  System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $4.ival + ": Se detectó un bloque DO UNTIL");
                                                                                                  if (!error()) { $$ = new ParserVal(new ArbolSintactico("DoUntil", (ArbolSintactico)$4.obj, (ArbolSintactico)$2.obj)); }  
                                                                                                }

                            | IF condicion bloque_control ELSE bloque_control_else              {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $5.ival + ": falta la palabra ENDIF al final del bloque IF-ELSE");}
                            | IF condicion bloque_control                                       {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $3.ival + ": falta la palabra ENDIF al final del bloque IF");}
                            | IF condicion bloque_control ELSE ENDIF                            {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $4.ival + ": falta el cuerpo en el bloque ELSE");}
                            | IF condicion ELSE bloque_control_else ENDIF                       {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta el cuerpo en el bloque IF");}
                            | IF condicion ENDIF                                                {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta el cuerpo en el bloque IF");}
                            | IF condicion ELSE ENDIF                                           {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $3.ival + ": falta el cuerpo en los bloques IF y ELSE");}
                            
                            | DO UNTIL condicion                                                {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": falta de cuerpo en iteracion");}
                            | inicio_do bloque_control condicion                                { enDo=false;
                                                                                                  erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta la palabra UNTIL en la iteracion");
                                                                                                }
                            ;
                            

print                       : PRINT '(' expresion ')'                { if (!error()) { $$ = new ParserVal (new ArbolSintactico("Print", (ArbolSintactico)$3.obj, null)); } }
                            | PRINT '(' CADENAML ')'                 { if (!error()) { $$ = new ParserVal (new ArbolSintactico("Print", new ArbolSintactico((Simbolo)$3.obj), null)); } }
                            | PRINT '(' ')'                          {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $3.ival + ": falta de argumento en sentencia print");}
                            ;

condicion                   : '(' cuerpocondicion ')'                { $$ = $2; }
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
                            // REDUCE PREMATURAMENTE | listaIDs error ID    {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": falta ',' entre variables"); }
                            ;

listavariables              : listavariables ',' identificador        { if (!error()) {
                                                                          $$ = $1; 
                                                                          ((ArrayList<Simbolo>)$$.obj).add( checkearAlcance($3.sval, $3.ival) );
                                                                        }
                                                                      }
                            | identificador                           { if (!error()) {
                                                                          $$ = new ParserVal( new ArrayList<Simbolo>() );
                                                                          ((ArrayList<Simbolo>)$$.obj).add( checkearAlcance($1.sval, $1.ival) );
                                                                        }
                                                                      }
                            // REDUCE PREMATURAMENTE | listavariables error identificador    {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": falta ',' entre variables"); }
                            ;

identificador               : ID                                      { $$ = new ParserVal( ((SimboloLong)$1.obj).getLexema() );
                                                                        $$.ival = $1.ival; 
                                                                      }
                            | identificador '.' ID                    { $$ = new ParserVal( $1.sval + "." + ((SimboloLong)$3.obj).getLexema() );
                                                                        $$.ival = $3.ival;
                                                                      }                       
                            ;


funcion                     : cabecera_funcion argumentos_funcion ')'                                         { enParamInvocacion = false;
                                                                                                                System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $3.ival + ": Se detectó la invocación de una función");
                                                                                                                if (!error()) {
                                                                                                                  ((ArbolSintactico)$1.obj).setUltimoDerecho((ArbolSintactico)$2.obj); 
                                                                                                                  $$ = $1;
                                                                                                                }
                                                                                                              }
                            ;

funcion_lambda              : cabecera_funcion argumento_id_lambda ')'                                        { enParamInvocacion = false;
                                                                                                                System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $3.ival + ": Se detectó la invocación de una función lambda");
                                                                                                                chequeo_anid_lambda($1.sval, $3);
                                                                                                                if (!error()) {
                                                                                                                  ((ArbolSintactico)$1.obj).setUltimoDerecho( new ArbolSintactico((Simbolo)$2.obj) );
                                                                                                                  $$ = $1;
                                                                                                                  ((ArbolSintactico)$$.obj).setCategoria("CallLambda");
                                                                                                                }
                                                                                                              }
                            | cabecera_funcion CTE ')'                                                        { enParamInvocacion = false;
                                                                                                                restriccionCteFloat($2);
                                                                                                                chequeo_anid_lambda($1.sval, $3); 
                                                                                                                constantePositiva($2); 
                                                                                                                System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $3.ival + ": Se detectó la invocación de una función lambda");
                                                                                                                if (!error()) {
                                                                                                                  ((ArbolSintactico)$1.obj).setUltimoDerecho( new ArbolSintactico((Simbolo)$2.obj) );
                                                                                                                  $$ = $1;
                                                                                                                  ((ArbolSintactico)$$.obj).setCategoria("CallLambda");
                                                                                                                }
                                                                                                              }
                            | cabecera_funcion '-' CTE ')'                                                    { enParamInvocacion = false;
                                                                                                                restriccionCteFloat($3);
                                                                                                                chequeo_anid_lambda($1.sval, $4); 
                                                                                                                constanteNegativa($3); 
                                                                                                                System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $4.ival + ": Se detectó la invocación de una función lambda");
                                                                                                                if (!error()) {
                                                                                                                  ((ArbolSintactico)$1.obj).setUltimoDerecho( new ArbolSintactico((Simbolo)$3.obj) );
                                                                                                                  $$ = $1;
                                                                                                                  ((ArbolSintactico)$$.obj).setCategoria("CallLambda");
                                                                                                                }
                                                                                                              }
                            | cabecera_funcion TRUNC '(' CTE ')' ')'                                          { enParamInvocacion = false;
                                                                                                                constantePositiva($4);
                                                                                                                chequeo_anid_lambda($1.sval, $6); 
                                                                                                                System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $6.ival + ": Se detectó la invocación de una función lambda");
                                                                                                                if (!error()) {
                                                                                                                  ((ArbolSintactico)$1.obj).setUltimoDerecho( new ArbolSintactico("Trunc", new ArbolSintactico((Simbolo)$4.obj), null) );
                                                                                                                  $$ = $1;
                                                                                                                  ((ArbolSintactico)$$.obj).setCategoria("CallLambda");
                                                                                                                }
                                                                                                              }
                            | cabecera_funcion TRUNC '(' '-' CTE ')' ')'                                      { enParamInvocacion = false;
                                                                                                                constanteNegativa($5); 
                                                                                                                chequeo_anid_lambda($1.sval, $7);
                                                                                                                System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $7.ival + ": Se detectó la invocación de una función lambda");
                                                                                                                if (!error()) {
                                                                                                                  ((ArbolSintactico)$1.obj).setUltimoDerecho( new ArbolSintactico("Trunc", new ArbolSintactico((Simbolo)$5.obj), null) );
                                                                                                                  $$ = $1;
                                                                                                                  ((ArbolSintactico)$$.obj).setCategoria("CallLambda");
                                                                                                                }
                                                                                                              }
                            ;

argumento_id_lambda         : identificador                                                     {$$ = new ParserVal(checkearAlcance($1.sval, $1.ival).getLexema() );} 
                            ;

cabecera_funcion            : identificador '('                                                 { enParamInvocacion = true; 
                                                                                                  $1.obj = checkearAlcance($1.sval, $2.ival);
                                                                                                  if ($1.obj != null) {
                                                                                                    if (!error()) {
                                                                                                      $$ = new ParserVal(new ArbolSintactico("Call", new ArbolSintactico((Simbolo)$1.obj), null)); 
                                                                                                      $$.sval = ((Simbolo)$1.obj).getLexema();
                                                                                                    }
                                                                                                    funcionInvocadaActual = (Simbolo) $1.obj;
                                                                                                  }
                                                                                                }
                            ;

argumentos_funcion          : listapinvocacion                              { if (!error()) {
                                                                                $$ = new ParserVal(new ArbolSintactico("ListaParametros", (ArbolSintactico)((ArbolSintactico[])$1.obj)[0], null) ); 
                                                                                for (int i = 1; i < ((ArbolSintactico[])$1.obj).length; i++) {
                                                                                  ((ArbolSintactico)$$.obj).setUltimoDerecho( new ArbolSintactico("ListaParametros", ((ArbolSintactico)((ArbolSintactico[])$1.obj)[i]), null) );
                                                                                }
                                                                              }
                                                                            }  
                            ;

listapinvocacion            : listapinvocacion ',' pinvocacion              { if (!error()) { $$ = $1; ((ArbolSintactico[])$$.obj)[((SimboloFuncion)funcionInvocadaActual).getPosParametro($3.sval)] = (ArbolSintactico)$3.obj; } }
                            | pinvocacion                                   { if (!error()) {
                                                                                $$ = new ParserVal(new ArbolSintactico[((SimboloFuncion)funcionInvocadaActual).getCantidadParametros()]);
                                                                                ((ArbolSintactico[])$$.obj)[((SimboloFuncion)funcionInvocadaActual).getPosParametro($1.sval)] = (ArbolSintactico)$1.obj;
                                                                              }
                                                                            } 
                            ;

pinvocacion                 : expresion FLECHA ID                                                           { if (!error()) {
                                                                                                                checkeoParametroDeInvocacion(((Simbolo)$3.obj).getLexema(), $3.ival, (ArbolSintactico)$1.obj); 
                                                                                                                $$ = new ParserVal( (ArbolSintactico)$1.obj );
                                                                                                                $$.sval = ((Simbolo)$3.obj).getLexema();
                                                                                                              }
                                                                                                            }                                                                     
                            | cabecera_declaracion_lambda '{'  listasentenciasejecutables '}' FLECHA ID     { if (!error()) {
                                                                                                                funcLambdaDeclaradas++;
                                                                                                                finFunc($6);
                                                                                                                tablaSimbolos.remove($1.sval);
                                                                                                                checkeoParametroDeInvocacion(((Simbolo)$6.obj).getLexema(), $6.ival, (ArbolSintactico)$3.obj);  
                                                                                                                $$ = new ParserVal(new ArbolSintactico( "DeclaracionLambda", (ArbolSintactico)$3.obj , null));
                                                                                                                $$.sval = ((Simbolo)$6.obj).getLexema();
                                                                                                              }
                                                                                                            }
                            | cabecera_declaracion_lambda '{' listasentenciasejecutables FLECHA ID          {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $4.ival + ": falta llave de cierre del cuerpo de la función Lambda");}
                            | cabecera_declaracion_lambda listasentenciasejecutables '}' FLECHA ID          {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $3.ival + ": falta llave de apertura del cuerpo de la función Lambda");}
                            | cabecera_declaracion_lambda listasentenciasejecutables FLECHA ID              {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $3.ival + ": faltan ambas llaves del cuerpo de la función Lambda");}
                            | cabecera_declaracion_lambda '{' listasentenciasejecutables '}' FLECHA         {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $5.ival + ": No se especifica el parametro formal al que corresponde el parámetro real");}
                            | expresion FLECHA                                                              {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": No se especifica el parametro formal al que corresponde el parámetro real");}
                            ;

cabecera_declaracion_lambda : '(' tipo ID ')' { enFunc++;
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
                            ;                           

expresion                   : expresion '+' termino                 { System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $2.ival + ": Se detectó una expresión desde una suma");
                                                                      if (!error()) {
                                                                        $$ = new ParserVal(new ArbolSintactico("Suma", (ArbolSintactico)$1.obj, (ArbolSintactico)$3.obj));
                                                                      }
                                                                    }      
                            | expresion '-' termino                 { System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $2.ival + ": Se detectó una expresión desde una resta");
                                                                      if (!error()) { $$ = new ParserVal(new ArbolSintactico("Resta", (ArbolSintactico)$1.obj, (ArbolSintactico)$3.obj)); }
                                                                    }
                            | error '+' termino                     { erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta operando a la izquierda de '+'"); }
                            | expresion '+' error                   { erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta operando a la derecha de '+'"); }
                            | error '-' termino                     { erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta operando a la izquierda de '-'"); }
                            | expresion '-' error                   { erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta operando a la derecha de '-'"); }
                            | termino                               { System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $1.ival + ": Se detectó una expresión desde un término");    
                                                                      $$ = $1;
                                                                    }  
                            ;

termino                     : termino '*' factor                    { System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $2.ival + ": Se detectó un término desde una multiplicación");
                                                                      if (!error()) { $$ = new ParserVal(new ArbolSintactico("Multiplicacion", (ArbolSintactico)$1.obj, (ArbolSintactico)$3.obj)); }
                                                                    }
                            | termino '/' factor                    { System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $2.ival + ": Se detectó un término desde una división");
                                                                      if (!error()) { $$ = new ParserVal(new ArbolSintactico("Division", (ArbolSintactico)$1.obj, (ArbolSintactico)$3.obj)); }
                                                                    }
                            | error '*' factor                      { erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta operando a la izquierda de '*'"); }
                            | termino '*' error                     { erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta operando a la derecha de '*'"); }
                            | error '/' factor                      { erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta operando a la izquierda de '/'"); }
                            | termino '/' error                     { erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta operando a la derecha de '/'"); }
                            | termino error factor                  { erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": falta operador entre operandos o falta de comparador en condición"); }
                            | factor                                { System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $1.ival + ": Se detectó un término desde un factor");
                                                                      $$ = $1;
                                                                    }
                            ;

factor                      : CTE                                   { constantePositiva($1); System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $1.ival + ": Se detectó un factor desde una constante");
                                                                      restriccionCteFloat($1);
                                                                      if (!error()) { $$ = new ParserVal(new ArbolSintactico((Simbolo)$1.obj)); }
                                                                    }
                            | '-' CTE                               { constanteNegativa($2); System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $2.ival + ": Se detectó un factor desde una constante negativa");
                                                                      restriccionCteFloat($2);
                                                                      if (!error()) { $$ = new ParserVal(new ArbolSintactico((Simbolo)$2.obj)); }
                                                                    }
                            | identificador                         { System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $1.ival + ": Se detectó un factor desde un identificador");
                                                                      if (!error()) { $$ = new ParserVal(new ArbolSintactico(checkearAlcance($1.sval, $1.ival))); }
                                                                    }
                            | funcion                               { System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $1.ival + ": Se detectó un factor desde una función");
                                                                      $$ = $1;
                                                                    }
                            | funcion_lambda                        { if(enFunc==0) {erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + $1.ival + ": Invocacion de funcion lambda fuera de una función");} else {$$ = $1;}}
                            | TRUNC '(' CTE ')'                     { constantePositiva($3); 
                                                                      if (enParamInvocacion) {
                                                                        System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $1.ival + ": Se detectó la función Trunc en uno de los parámetros reales de la invocación");
                                                                        if (!error()) {
                                                                          $$ = new ParserVal(new ArbolSintactico("Trunc", new ArbolSintactico((Simbolo)$3.obj), null));
                                                                        }
                                                                      }
                                                                      else  
                                                                        erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + $1.ival + ": la función TRUNC solo puede utilizarse en parámetros reales de invocación de funciones");
                                                                    }
                            | TRUNC '(' '-' CTE ')'                 { constanteNegativa($4); 
                                                                      if (enParamInvocacion) {
                                                                        System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $1.ival + ": Se detectó la función Trunc en uno de los parámetros reales de la invocación");
                                                                        if (!error()) {
                                                                          $$ = new ParserVal(new ArbolSintactico("Trunc", new ArbolSintactico((Simbolo)$4.obj), null));
                                                                        }
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
                            ;


%%

public Parser(Map<String, Simbolo> tablaSimbolos, String path, ArbolSintactico arbolSintactico, Map<String, ArbolSintactico> mapaFunciones) {
  this.tablaSimbolos = tablaSimbolos;
  this.lexer = new Lexico(tablaSimbolos,path, erroresLexicos, warnings);
  this.arbolSintactico = arbolSintactico;
  this.mapaFunciones = mapaFunciones;
}

private void yyerror(String s) {
    erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error: " + s);
}

private boolean error() {
  return (!erroresLexicos.isEmpty() || !erroresSintacticos.isEmpty() || !erroresSemanticos.isEmpty());
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
  if (valCte.obj instanceof SimboloLong) {
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
    if ( (valCte.obj instanceof SimboloLong) && ( ((SimboloLong) valCte.obj).getValor() == 2147483648L) ) {
      Simbolo simbolo;
      if (!tablaSimbolos.containsKey("2147483647L")) {
        simbolo =  new SimboloLong("Long", "Constante", "2147483647L", 2147483647L);
        tablaSimbolos.put("2147483647L", simbolo);
      } else {
        simbolo = tablaSimbolos.get("2147483647L");
      }
      valCte.obj = simbolo;
      warnings.add("||================ ANALIZADOR SINTÁCTICO: Warning en línea " + valCte.ival + ": el valor de la constante entera es mayor al máximo permitido, se asigna el valor 2147483647");
    }
}

private void chequeoSentenciaReturn(ParserVal yyval) {
  if (enFunc == 0) {
    erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + yyval.ival + ": Uso de sentencia return fuera de una función");
  } else if (nivel > -1) {
    returnsIf--;
    huboRetStack.pop();
    huboRetStack.push(true);
  } else {
    returnsFunc --;
  }
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
  returnsFunc = 1;
  huboIfEnFunc = false;
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
    erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + yyval.ival + ": Se ha intentado invocar una función lambda perteneciente a otro ámbito");
  }
}

// Esta función toma el Lexema de un identificador con la forma escrita por el programador (puede contener "."), 
// busca y retorna de la tabla de símbolos el símbolo correspondiente, teniendo en cuenta los ámbitos.
private Simbolo checkearAlcance(String lexema, int nroLinea) {

  // AuxSA se vuelve una copia de StringAmbitos para ir recortándola
  String auxSA = stringAmbitos.toString();

  // Cuando el Lexema es una variable no anidada:
  if (!lexema.contains(".")) {
    String auxLexema = lexema + auxSA;

    while (!tablaSimbolos.containsKey(auxLexema) && auxSA.length() > 0) {
      auxSA = auxSA.substring(0, auxSA.lastIndexOf(":"));
      auxLexema = lexema + auxSA;
    }

    // Tira error si no se encuentra en el ámbito actual o los superiores (A no ser que sea una variable de función lambda, que se guarda sin ámbito por decisión de diseño)
    if (auxLexema.equals(lexema) && !tablaSimbolos.containsKey("lambda:" + auxLexema)) {
      erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + nroLinea + ": La variable '" + lexema + "' no ha sido declarada en el ámbito actual y tampoco está al alcance");
      tablaSimbolos.remove(lexema);
      return null;
    } else {
      tablaSimbolos.remove(lexema);
      return tablaSimbolos.get(auxLexema);
    }

  // Cuando el Lexema es una variable anidada:    
  } else { 
    String varActual = lexema.substring(lexema.lastIndexOf(".") + 1);
    String ambitoVarActual = (":" + lexema.substring(0, lexema.lastIndexOf("."))).replace(".", ":");

    // Dado que estaba anidada, se va a buscar el ámbito a partir de stringAmbitos seguido del ambito escrito en la variable
    while (!tablaSimbolos.containsKey(varActual + auxSA + ambitoVarActual) && auxSA.length() > 0) {
      auxSA = auxSA.substring(0, auxSA.lastIndexOf(":"));
    }

    if (!tablaSimbolos.containsKey(varActual + auxSA + ambitoVarActual)) {
      erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + nroLinea + ": La variable '" + lexema + "' no ha sido declarada en el ámbito actual y tampoco está al alcance");
      for (String ambitoPart : lexema.split("\\.")) {
        tablaSimbolos.remove(ambitoPart);
      }
      return null;
    } else {
      for (String ambitoPart : lexema.split("\\.")) {
        tablaSimbolos.remove(ambitoPart);
      }
      return tablaSimbolos.get(varActual + auxSA + ambitoVarActual);
    }
  }
}

// Función encargada de dos cosas: en base a conocer el parámetro real y el parámetro formal intencionado, chequea si ese parámetro formal verdaderamente existe en la función invocada,
// y, si existe, verifica también que, en caso de ser CVR, el parámetro real no sea una constante ni una expresión.
private void checkeoParametroDeInvocacion(String lexemaParametroFormal, int nroLinea, ArbolSintactico arbolParametroReal) {
  if (!error()) {
    String lexemaFuncion = ((SimboloFuncion)funcionInvocadaActual).getLexema();
    Parametro parametroFormal = ((SimboloFuncion)funcionInvocadaActual).getRefParametro(lexemaParametroFormal);
    if (parametroFormal == null) {
      erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + nroLinea + ": el parámetro formal '" + lexemaParametroFormal + "' no existe en la función '" + lexemaFuncion.substring(0, lexemaFuncion.indexOf(':')) + "'");    
    } else if ((arbolParametroReal.getCategoria() != null || arbolParametroReal.getRefSimbolo().getCategoria().equals("Constante")) && parametroFormal.getPasaje().equals("CVR") ) {
      erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + nroLinea + ": el parámetro real asociado al parámetro formal '" + lexemaParametroFormal + "' de la función '" + lexemaFuncion.substring(0, lexemaFuncion.indexOf(':')) + "' es una constante u expresión y no puede pasarse por Copia-Valor-Resultado (CVR)");
    }
  }
  tablaSimbolos.remove(lexemaParametroFormal);
}

private void restriccionCteFloat(ParserVal valCte) {
  if (valCte.obj instanceof SimboloCteFloat) {
    erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + valCte.ival + ": Uso incorrecto de constante Float por incompatibilidad de tipos");
    tablaSimbolos.remove(((SimboloCteFloat)valCte.obj).getLexema());
  }
}

private ArbolSintactico generarAsigMultiple(ArrayList<Simbolo> listaVariables, ArrayList<Simbolo> listaConstantes) {
  int i = 1; 
  ArbolSintactico arbolRetorno = new ArbolSintactico("Sentencia", new ArbolSintactico("Asig", new ArbolSintactico(listaVariables.get(0)), new ArbolSintactico(listaConstantes.get(0))), null );                                                                                            
  while (i < listaConstantes.size()) {
    ArbolSintactico nuevaAsignacion = new ArbolSintactico( "Sentencia", new ArbolSintactico("Asig", new ArbolSintactico(listaVariables.get(i)), new ArbolSintactico(listaConstantes.get(i))), null);
    arbolRetorno.setUltimoDerecho(nuevaAsignacion);
    i++;
  }
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

  return arbolRetorno;
}
