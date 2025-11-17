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
prog                        : ID {stringAmbitos.append(":"+((Simbolo)$1.obj).getLexema());} bloque_sentencias {quitarAmbito();} EOF
                            | bloque_sentencias EOF               {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": falta el nombre del programa");} 
                            //| ID '{' error ';' listasentencias '}' EOF  {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico: el programa no es válido");}
                            //| ID '{' listasentencias error '}' EOF      {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico: el programa no es válido");}
                            //| ID '{' listasentencias error ';' listasentencias '}' EOF      {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico: el programa no es válido");}
                            ;

bloque_sentencias           : '{' listasentencias '}'
                            | '{' '}'
                            | '{' listasentencias                      {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta llave de cierre del bloque de sentencias");}
                            | listasentencias '}'                      {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": falta llave de apertura del bloque de sentencias");}
                            | listasentencias                          {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": faltan ambas llaves del bloque de sentencias");}
                            ;

listasentencias             : listasentencias sentencia
                            | sentencia
                            ;

sentencia                   : sentenciaejecutable ';'                                 
                            | declaracion ';'                           
                            | sentenciaejecutable                       {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": falta ; al final de la sentencia ejecutable");}
                            | declaracion                               {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": falta ; al final de la declaración");}
                            ;                           

listasentenciasejecutables  : listasentenciasejecutables sentenciaejecutable ';'  
                            | sentenciaejecutable ';'
                            | listasentenciasejecutables sentenciaejecutable  {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta ; al final de la sentencia ejecutable");}
                            | sentenciaejecutable                             {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": falta ; al final de la sentencia ejecutable");}
                            ;

sentenciaejecutable         : asignacion                                      
                            | control
                            | funcion
                            | print                                           {System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $1.ival + ": Se detectó un print");}
                            | funcion_lambda                                  {if(enFunc==0) erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": Invocacion de funcion lambda fuera de una función");}
                            | sentencia_return                                {chequeoSentenciaReturn($1);}
                            ;

declaracion                 :  tipo listaIDs                             {System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $2.ival + ": Se detectó una declaración de variables");}
                            |  tipo ID {stringAmbitos.append(":"+((Simbolo)$2.obj).getLexema());} '(' listaparametros {stackParametrosActuales.push(parametrosActuales); parametrosActuales = new ArrayList<>();} ')' {enFunc++;} '{' listasentencias '}' {finFunc($11, ((Simbolo)$2.obj)); quitarAmbito(); renombrarVariable($2); System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $11.ival + ": Se detectó una declaración de función");}
                            ;

sentencia_return            : RETURN '(' expresion ')'                       {System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $3.ival + ": Se detectó una sentencia return");}
                            | RETURN '(' ')'                                 {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $3.ival + ": falta de argumento en sentencia return");}
                            ;

asignacion                  : identificador ASIG expresion                                 {System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $3.ival + ": Se detectó una asignación simple");}
                            | listavariables '=' listaconstantes                           {System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $3.ival + ": Se detectó una asignación múltiple");}
                            ;

bloque_control_estructura   : '{' listasentenciasejecutables '}'
                            | sentenciaejecutable ';'
                            | '{''}'
                            ;

bloque_control              : {inicioIf();} bloque_control_estructura {finIf($2);} //ANTES HABIA UN $1
                            ;

inicio_do                   : DO {enDo=true;}
                            ;

bloque_control_else         : {sinRetIf.push(sinRet);} bloque_control {if(sinRetIf.pop()) {sinRet=true;};}
                            ;

control                     : IF condicion bloque_control ELSE bloque_control_else ENDIF       {System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $6.ival + ": Se detectó un bloque IF-ELSE");}
                            | IF condicion bloque_control ENDIF                                                              {System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $4.ival + ": Se detectó un bloque IF");}
                            | inicio_do bloque_control UNTIL condicion {enDo=false;}                                                                          {System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $4.ival + ": Se detectó un bloque DO UNTIL");}

                            | IF condicion bloque_control ELSE bloque_control_else             {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $5.ival + ": falta la palabra ENDIF al final del bloque IF-ELSE");}
                            | IF condicion bloque_control                                                          {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $3.ival + ": falta la palabra ENDIF al final del bloque IF");}
                            | IF condicion bloque_control ELSE ENDIF                                               {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $4.ival + ": falta el cuerpo en el bloque ELSE");}
                            | IF condicion ELSE bloque_control_else ENDIF                                               {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta el cuerpo en el bloque IF");}
                            | IF condicion ENDIF                                                                                          {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta el cuerpo en el bloque IF");}
                            | IF condicion ELSE ENDIF                                                                                     {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $3.ival + ": falta el cuerpo en los bloques IF y ELSE");}
                            
                            | DO UNTIL condicion                                                                                          {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": falta de cuerpo en iteracion");}
                            | inicio_do bloque_control condicion {enDo=false;}                                                                                {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta la palabra UNTIL en la iteracion");}
                            ;
                            
print                       : PRINT '(' expresion ')'
                            | PRINT '(' CADENAML ')'
                            | PRINT '(' ')'                          {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $3.ival + ": falta de argumento en sentencia print");}
                            ;

condicion                   : '(' cuerpocondicion ')'                
                            | cuerpocondicion ')'                    {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta el paréntesis de apertura de la condición");}
                            | '(' cuerpocondicion                    {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta el paréntesis de cierre de la condición");}
                            | cuerpocondicion                        {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": faltan ambos paréntesis de la condición");}
                            ;

cuerpocondicion             : expresion comparador expresion
                            | expresion                              {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": La condición es incompleta");}
                            ;

comparador                  : '>'
                            | '<'
                            | MAYORIG
                            | MENORIG
                            | IGUAL
                            | DISTINTO
                            ;  

tipo                        : LONG                   {stackTipoFuncActual.push("Long");}
                            | LAMBDA                 {stackTipoFuncActual.push("Lambda");} 
                            ;

listaIDs                    : listaIDs ',' ID        {renombrarVariable($3);}
                            | ID                     {renombrarVariable($1);}
                            // REDUCE PREMATURAMENTE | listaIDs error ID    {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": falta ',' entre variables"); }
                            ;

listavariables              : listavariables ',' identificador
                            | identificador
                            // REDUCE PREMATURAMENTE | listavariables error identificador    {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": falta ',' entre variables"); }
                            ;

identificador               : ID                                      //{$$.sval = ((SimboloLong)$1.obj).getLexema();}
                            | identificador '.' ID                    //{$$.sval = $1.sval + "." + ((SimboloLong)$3.obj).getLexema();}                       
                            ;

listaparametros             : listaparametros ',' parametro
                            | parametro
                            ;                    

parametro                   : CV tipo ID                             {renombrarVariable($3); parametrosActuales.add(new Parametro(((Simbolo)$3.obj), "CV"));}                          
                            | tipo ID                                {renombrarVariable($2); parametrosActuales.add(new Parametro(((Simbolo)$2.obj), "CVR"));}
                            | CV tipo                                {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": falta el nombre del parámetro");}
                            | tipo                                   {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": falta el nombre del parámetro");}
                            | CV ID                                  {renombrarVariable($2); parametrosActuales.add(new Parametro(((Simbolo)$2.obj), "CV")); erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": falta el tipo del parámetro");}
                            | ID                                     {renombrarVariable($1); parametrosActuales.add(new Parametro(((Simbolo)$1.obj), "CVR")); erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": falta el tipo del parámetro");}
                            ;

funcion                     : cabecera_funcion argumentos_funcion ')' {enParamInvocacion = false;}        { System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $3.ival + ": Se detectó la invocación de una función");}
                            ;

funcion_lambda              : cabecera_funcion identificador ')' {enParamInvocacion = false;}                 { /*chequeo_anid_lambda($1.sval, $3);*/ System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $3.ival + ": Se detectó la invocación de una función lambda");}
                            | cabecera_funcion CTE ')' {enParamInvocacion = false;}                           { /*chequeo_anid_lambda($1.sval, $3)*/ ; constantePositiva($2); System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $3.ival + ": Se detectó la invocación de una función lambda");}
                            | cabecera_funcion '-' CTE ')' {enParamInvocacion = false;}                       { /*chequeo_anid_lambda($1.sval, $4)*/ ; constanteNegativa($3); System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $4.ival + ": Se detectó la invocación de una función lambda");}
                            | cabecera_funcion TRUNC '(' CTE ')' ')' {enParamInvocacion = false;}             { constantePositiva($4); System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $6.ival + ": Se detectó la invocación de una función lambda");}
                            | cabecera_funcion TRUNC '(' '-' CTE ')' ')' {enParamInvocacion = false;}         { constanteNegativa($5); System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $7.ival + ": Se detectó la invocación de una función lambda");}
                            ;

cabecera_funcion            : ID '(' {enParamInvocacion = true;}
                            ; 

argumentos_funcion          : listapinvocacion
                            ;

listapinvocacion            : listapinvocacion ',' pinvocacion 
                            | pinvocacion
                            ;

pinvocacion                 : expresion FLECHA ID                       
                            //| TRUNC '(' expresion ')' FLECHA ID
                            //| TRUNC '(' expresion FLECHA ID                                     {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $3.ival + ": falta el paréntesis de cierre de la función TRUNC");}
                            //| TRUNC expresion ')' FLECHA ID                                     {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": falta el paréntesis de apertura de la función TRUNC");}
                            //| TRUNC expresion FLECHA ID                                         {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": faltan ambos paréntesis de la función TRUNC");}
                            | '(' tipo ID ')' '{' listasentenciasejecutables '}' FLECHA ID
                            | '(' tipo ID ')' '{' listasentenciasejecutables FLECHA ID          {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $6.ival + ": falta llave de cierre del cuerpo de la función Lambda");}
                            | '(' tipo ID ')' listasentenciasejecutables '}' FLECHA ID          {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $4.ival + ": falta llave de apertura del cuerpo de la función Lambda");}
                            | '(' tipo ID ')' listasentenciasejecutables FLECHA ID              {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $4.ival + ": faltan ambas llaves del cuerpo de la función Lambda");}
                            | '(' tipo ID ')' '{' listasentenciasejecutables '}' FLECHA         {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $8.ival + ": No se especifica el parametro formal al que corresponde el parámetro real");}
                            | expresion FLECHA                                                  {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": No se especifica el parametro formal al que corresponde el parámetro real");}
                            //| TRUNC '(' expresion ')' FLECHA                                    {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $5.ival + ": No se especifica el parametro formal al que corresponde el parámetro real");}
                            ;                                                        

expresion                   : expresion '+' termino                 {System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $2.ival + ": Se detectó una expresión desde una suma");}      
                            | expresion '-' termino                 {System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $2.ival + ": Se detectó una expresión desde una resta");}
                            | error '+' termino                     { erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta operando a la izquierda de '+'"); }
                            | expresion '+' error                   { erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta operando a la derecha de '+'"); }
                            | error '-' termino                     { erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta operando a la izquierda de '-'"); }
                            | expresion '-' error                   { erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta operando a la derecha de '-'"); }
                            | termino                               {System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $1.ival + ": Se detectó una expresión desde un término");}         
                            ;

termino                     : termino '*' factor                    {System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $2.ival + ": Se detectó un término desde una multiplicación");}
                            | termino '/' factor                    {System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $2.ival + ": Se detectó un término desde una división");}
                            | error '*' factor                      { erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta operando a la izquierda de '*'"); }
                            | termino '*' error                     { erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta operando a la derecha de '*'"); }
                            | error '/' factor                      { erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta operando a la izquierda de '/'"); }
                            | termino '/' error                     { erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta operando a la derecha de '/'"); }
                            | termino error factor                  { erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": falta operador entre operandos o falta de comparador en condición"); }
                            | factor                                {System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $1.ival + ": Se detectó un término desde un factor");}
                            ;

factor                      : CTE                                   { constantePositiva($1); System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $1.ival + ": Se detectó un factor desde una constante");}
                            | '-' CTE                               { constanteNegativa($2); System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $2.ival + ": Se detectó un factor desde una constante negativa");}
                            | identificador                         {System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $1.ival + ": Se detectó un factor desde un identificador");}
                            | funcion                               {System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $1.ival + ": Se detectó un factor desde una función");}
                            | TRUNC '(' CTE ')'                     { constantePositiva($3); if (enParamInvocacion) {System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $1.ival + ": Se detectó la función Trunc en uno de los parámetros reales de la invocación");} else erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": la función TRUNC solo puede utilizarse en parámetros reales de invocación de funciones");}
                            | TRUNC '(' '-' CTE ')'                 { constanteNegativa($4); if (enParamInvocacion) {System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $1.ival + ": Se detectó la función Trunc en uno de los parámetros reales de la invocación");} else erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": la función TRUNC solo puede utilizarse en parámetros reales de invocación de funciones");}
                            ;

listaconstantes             : listaconstantes ',' CTE               { constantePositiva($3); }
                            | listaconstantes ',' '-' CTE           { constanteNegativa($4); }
                            | CTE                                   { constantePositiva($1); }
                            | '-' CTE                               { constanteNegativa($2); }
                            | listaconstantes CTE                   { erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": falta ',' entre constantes"); constantePositiva($2); }
                            | listaconstantes '-' CTE               { erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": falta ',' entre constantes"); constanteNegativa($3); }
                            ;


%%

private void yyerror(String s) {
    erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error: " + s);
}

public Parser(Map<String, Simbolo> tablaSimbolos, String path, ArrayList<String> erroresLexicos, ArrayList<String> erroresSintacticos)
{
  this.tablaSimbolos = tablaSimbolos;
  this.erroresSintacticos = erroresSintacticos;
  this.lexer = new Lexico(tablaSimbolos,path, erroresLexicos);
}

private void constanteNegativa(ParserVal valCte) {
  if (valCte.obj instanceof SimboloLong) {
    Simbolo simbolo;
    if (!tablaSimbolos.containsKey("-" +((SimboloLong)valCte.obj).getLexema())) {
      simbolo =  new SimboloLong(258, "Long", "Constante", "-" + ((SimboloLong)valCte.obj).getLexema(), -((SimboloLong)valCte.obj).getValor());
      tablaSimbolos.put("-" +((SimboloLong)valCte.obj).getLexema(), simbolo);
    } else {
      simbolo = tablaSimbolos.get("-" + ((SimboloLong)valCte.obj).getLexema());
    }
    valCte.obj = simbolo;
  } else {
        Simbolo simbolo;
        if (!tablaSimbolos.containsKey("-" +((SimboloCteFloat)valCte.obj).getLexema())) {
          simbolo =  new SimboloCteFloat(258, "Float", "Constante", "-" + ((SimboloCteFloat)valCte.obj).getLexema(), -((SimboloCteFloat)valCte.obj).getValor());
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
        simbolo =  new SimboloLong(258, "Long", "Constante", "2147483647L", 2147483647L);
        tablaSimbolos.put("2147483647L", simbolo);
      } else {
        simbolo = tablaSimbolos.get("2147483647L");
      }
      valCte.obj = simbolo;
      System.out.println("||================ ANALIZADOR SINTÁCTICO: Warning en línea " + valCte.ival + ": el valor de la constante entera es mayor al máximo permitido, se asigna el valor 2147483647");
    }
}

private void chequeoSentenciaReturn(ParserVal yyval) {
  if (enFunc == 0) {
    erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + yyval.ival + ": sentencia return fuera de una función");
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
      erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + yyval.ival + ": Se han detectado multiples sentencias de retorno en el IF");
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

private void finFunc(ParserVal yyval, Simbolo simboloID) {
  if (returnsFunc > 0 && (!huboIfEnFunc || sinRet)) { 
    erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + yyval.ival + ": La función no tiene sentencia return en todos sus caminos de ejecución");
  } else if (returnsFunc < 0) {
    erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + yyval.ival + ": Se han detectado multiples sentencias de retorno en la función");
  }
  enFunc--;
  returnsFunc = 1;
  huboIfEnFunc = false;

  // Creación del símbolo de función
  tablaSimbolos.remove(simboloID.getLexema());
  tablaSimbolos.put(simboloID.getLexema(), new SimboloFuncion(257, stackTipoFuncActual.pop(), "Funcion", simboloID.getLexema(), stackParametrosActuales.pop()));
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
  String lexemaNuevo = lexemaViejo + stringAmbitos.toString();
  ((Simbolo)yyval.obj).setLexema(lexemaNuevo);
  tablaSimbolos.put(lexemaNuevo, ((Simbolo)yyval.obj));
  tablaSimbolos.remove(lexemaViejo);
}

/*private void chequeo_anid_lambda(String nombreLambda, ParserVal yyval) {
  if (nombreLambda.contains(".")) {
    erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + yyval.ival + ": Se ha intentado invocar una función lambda perteneciente a otro ámbito");
  }
}*/