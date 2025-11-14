%{
    import java.io.*;
    import Simbolos.*;
    import java.util.Map;
    import java.util.HashMap;
    import java.nio.file.Path;
    import java.nio.file.Paths;
    import java.nio.file.Files;
    import java.nio.file.StandardOpenOption;
    import java.util.ArrayList;

%}

%token ID CTE ASIG IF ELSE ENDIF MAYORIG MENORIG IGUAL DISTINTO LONG RETURN DO UNTIL CV FLECHA PRINT CADENAML TRUNC LAMBDA EOF

%%
prog                        : ID '{' bloque_programa '}' EOF           
                            | ID '{' '}' EOF
                            | '{' bloque_programa '}' EOF               {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": falta el nombre del programa");} 
                            | ID '{' bloque_programa EOF                {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $3.ival + ": falta la llave de cierre del cuerpo del programa");} 
                            | ID bloque_programa '}' EOF                {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": falta la llave de apertura del cuerpo del programa");} 
                            | ID bloque_programa EOF                    {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": faltan ambas llaves en cuerpo del programa");}
                            | '{' bloque_programa EOF                   {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta el nombre del programa y la llave de cierre del cuerpo del programa");}
                            | bloque_programa '}' EOF                   {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": falta el nombre del programa y la llave de apertura del cuerpo del programa");}
                            | bloque_programa EOF                       {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": falta el nombre del programa y ambas llaves en cuerpo del programa");}
                            | ID '{' error ';' bloque_programa '}' EOF  {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico: el programa no es válido");}
                            //| ID '{' bloque_programa error '}' EOF      {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico: el programa no es válido");}
                            //| ID '{' bloque_programa error ';' bloque_programa '}' EOF      {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico: el programa no es válido");}
                            ;

bloque_programa             : listasentencias
                            | listasentencias_conLambda                 {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": se intentó invocar una función Lambda fuera de una función");}
                            | listasentencias listasentencias_conLambda {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": se intentó invocar una función Lambda fuera de una función");}
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
                            ;

listasentencias_conLambda   : listasentencias_conLambda sentencia
                            | listasentencias_conLambda funcion_lambda ';'
                            | funcion_lambda ';'
                            ;

declaracion                 :  tipo listavariables                             {System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $2.ival + ": Se detectó una declaración de variables");}
                            |  tipo ID '(' listaparametros ')' '{' listasentencias RETURN '(' expresion ')' ';' '}'  {System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $13.ival + ": Se detectó una declaración de función");}
                            |  tipo ID '(' listaparametros ')' '{' listasentencias_conLambda RETURN '(' expresion ')' ';' '}'  {System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $13.ival + ": Se detectó una declaración de función");}
                            |  tipo ID '(' listaparametros ')' '{' listasentencias listasentencias_conLambda RETURN '(' expresion ')' ';' '}'  {System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $13.ival + ": Se detectó una declaración de función");}                            
                            ;

asignacion                  : identificador ASIG expresion                                 {System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $3.ival + ": Se detectó una asignación simple");}
                            | listavariables '=' listaconstantes                           {System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $3.ival + ": Se detectó una asignación múltiple");}
                            ;

cuerpo_bloque               : '{' listasentenciasejecutables '}'
                            | sentenciaejecutable ';'
                            | '{''}'
                            | '{' listasentenciasejecutables listasentencias_conLambda '}' {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": se intentó invocar una función Lambda fuera de una función");}
                            | '{' listasentencias_conLambda '}'                            {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": se intentó invocar una función Lambda fuera de una función");}
                            ;

control                     : IF condicion cuerpo_bloque ELSE cuerpo_bloque ENDIF          {System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $6.ival + ": Se detectó un bloque IF-ELSE");}
                            | IF condicion cuerpo_bloque ENDIF                             {System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $4.ival + ": Se detectó un bloque IF");}
                            | DO cuerpo_bloque UNTIL condicion                             {System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $4.ival + ": Se detectó un bloque DO UNTIL");}

                            | IF condicion cuerpo_bloque ELSE cuerpo_bloque                {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $5.ival + ": falta la palabra ENDIF al final del bloque IF-ELSE");}
                            | IF condicion cuerpo_bloque                                   {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $3.ival + ": falta la palabra ENDIF al final del bloque IF");}
                            | IF condicion cuerpo_bloque ELSE ENDIF                        {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $4.ival + ": falta el cuerpo en el bloque ELSE");}
                            | IF condicion ELSE cuerpo_bloque ENDIF                        {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta el cuerpo en el bloque IF");}
                            | IF condicion ENDIF                                           {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta el cuerpo en el bloque IF");}
                            | IF condicion ELSE ENDIF                                      {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $3.ival + ": falta el cuerpo en los bloques IF y ELSE");}
                            
                            | DO UNTIL condicion                                           {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": falta de cuerpo en iteracion");}
                            | DO cuerpo_bloque condicion                                   {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta la palabra UNTIL en la iteracion");}
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

tipo                        : LONG
                            | LAMBDA
                            ;

listavariables              : listavariables ',' identificador
                            | identificador
                            //| listavariables error identificador    {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": falta ',' entre variables"); }
                            ;

identificador               : ID
                            | identificador '.' ID
                            ;

funcion                     : ID '(' argumentos_funcion ')'        {System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $4.ival + ": Se detectó una función invocada correctamente");}
                            ;

funcion_lambda              : ID '(' identificador ')'
                            | ID '(' CTE ')'                            { constantePositiva($3); }
                            | ID '(' '-' CTE ')'                        { constanteNegativa($4); }
                            ;

argumentos_funcion          : listapinvocacion
                            ;

listapinvocacion            : listapinvocacion ',' pinvocacion 
                            | pinvocacion
                            ;

pinvocacion                 : expresion FLECHA ID                       
                            | TRUNC '(' expresion ')' FLECHA ID
                            | TRUNC '(' expresion FLECHA ID             {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $3.ival + ": falta el paréntesis de cierre de la función TRUNC");}
                            | TRUNC expresion ')' FLECHA ID             {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": falta el paréntesis de apertura de la función TRUNC");}
                            | TRUNC expresion FLECHA ID                 {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": faltan ambos paréntesis de la función TRUNC");}
                            | '(' tipo ID ')' '{' listasentenciasejecutables '}' FLECHA ID
                            | '(' tipo ID ')' '{' listasentenciasejecutables FLECHA ID       {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $6.ival + ": falta llave de cierre del cuerpo de la función Lambda");}
                            | '(' tipo ID ')' listasentenciasejecutables '}' FLECHA ID       {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $4.ival + ": falta llave de apertura del cuerpo de la función Lambda");}
                            | '(' tipo ID ')' listasentenciasejecutables FLECHA ID           {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $4.ival + ": faltan ambas llaves del cuerpo de la función Lambda");}
                            | '(' tipo ID ')' '{' listasentenciasejecutables '}' FLECHA      {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $8.ival + ": No se especifica el parametro formal al que corresponde el parámetro real");}
                            | expresion FLECHA                          {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": No se especifica el parametro formal al que corresponde el parámetro real");}
                            | TRUNC '(' expresion ')' FLECHA            {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $5.ival + ": No se especifica el parametro formal al que corresponde el parámetro real");}
                            ;                                                        

listaparametros             : listaparametros ',' parametro
                            | parametro
                            ;                    

parametro                   : CV tipo ID
                            | tipo ID
                            | CV tipo                                {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": falta el nombre del parámetro");}
                            | tipo                                   {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": falta el nombre del parámetro");}
                            | CV ID                                  {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": falta el tipo del parámetro");}
                            | ID                                     {erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": falta el tipo del parámetro");}
                            ;

expresion                   : expresion '+' termino
                            | expresion '-' termino
                            | error '+' termino                     { erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta operando a la izquierda de '+'"); }
                            | expresion '+' error                   { erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta operando a la derecha de '+'"); }
                            | error '-' termino                     { erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta operando a la izquierda de '-'"); }
                            | expresion '-' error                   { erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta operando a la derecha de '-'"); }
                            | termino
                            ;

termino                     : termino '*' factor
                            | termino '/' factor
                            | error '*' factor                      { erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta operando a la izquierda de '*'"); }
                            | termino '*' error                     { erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta operando a la derecha de '*'"); }
                            | error '/' factor                      { erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta operando a la izquierda de '/'"); }
                            | termino '/' error                     { erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta operando a la derecha de '/'"); }
                            | termino error factor                  { erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": falta operador entre operandos o falta de comparador en condición"); }
                            | factor
                            ;

factor                      : CTE                                   { constantePositiva($1); }
                            | '-' CTE                               { constanteNegativa($2); }
                            | identificador
                            | funcion
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
      simbolo =  new SimboloLong(258, "Constante", "-" + ((SimboloLong)valCte.obj).getLexema(), -((SimboloLong)valCte.obj).getValor());
      tablaSimbolos.put("-" +((SimboloLong)valCte.obj).getLexema(), simbolo);
    } else {
      simbolo = tablaSimbolos.get("-" + ((SimboloLong)valCte.obj).getLexema());
    }
    valCte.obj = simbolo;
  } else {
        Simbolo simbolo;
        if (!tablaSimbolos.containsKey("-" +((SimboloCteFloat)valCte.obj).getLexema())) {
          simbolo =  new SimboloCteFloat(258, "Constante", "-" + ((SimboloCteFloat)valCte.obj).getLexema(), -((SimboloCteFloat)valCte.obj).getValor());
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
        simbolo =  new SimboloLong(258, "Constante", "2147483647L", 2147483647L);
        tablaSimbolos.put("2147483647L", simbolo);
      } else {
        simbolo = tablaSimbolos.get("2147483647L");
      }
      valCte.obj = simbolo;
      System.out.println("||================ ANALIZADOR SINTÁCTICO: Warning en línea " + valCte.ival + ": el valor de la constante entera es mayor al máximo permitido, se asigna el valor 2147483647");
    }
}
