%{
    import java.io.*;
    import Simbolos.*;
    import java.util.Map;
    import java.util.HashMap;
%}

%token ID CTE ASIG IF ELSE ENDIF MAYORIG MENORIG IGUAL DISTINTO LONG RETURN DO UNTIL CV FLECHA PRINT CADENAML TRUNC LAMBDA EOF

%%
prog                        : ID '{' listasentencias '}' EOF
                            | '{' listasentencias '}' EOF               {System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": falta el nombre del programa");} 
                            | ID '{' listasentencias EOF                {System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $3.ival + ": falta la llave de cierre del cuerpo del programa");} 
                            | ID listasentencias '}' EOF                {System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": falta la llave de apertura del cuerpo del programa");} 
                            | ID listasentencias EOF                    {System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": faltaN ambas llaves en cuerpo del programa");}
                            | '{' listasentencias EOF                   {System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta el nombre del programa y la llave de cierre del cuerpo del programa");}
                            | listasentencias '}' EOF                   {System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": falta el nombre del programa y la llave de apertura del cuerpo del programa");}
                            | listasentencias EOF                       {System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": falta el nombre del programa y ambas llaves en cuerpo del programa");}
                            | error                                     {System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico: el programa no es válido");}
                            ;

listasentencias             : listasentencias sentencia
                            | sentencia
                            ;

sentencia                   : sentenciaejecutable ';'                                 
                            | declaracion ';'                           
                            | sentenciaejecutable                       {System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": falta ; al final de la sentencia ejecutable");}
                            | declaracion                               {System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": falta ; al final de la declaración");}
                            ;                           

listasentenciasejecutables  : listasentenciasejecutables sentenciaejecutable ';'  
                            | sentenciaejecutable ';'
                            | listasentenciasejecutables sentenciaejecutable  {System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta ; al final de la sentencia ejecutable");}
                            | sentenciaejecutable                             {System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": falta ; al final de la sentencia ejecutable");}
                            ;

sentenciaejecutable         : asignacion                                      
                            | control
                            | funcion
                            | print                                           {System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $1.ival + ": Se detectó un print");}
                            ;

declaracion                 : tipo listavariables                             {System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $2.ival + ": Se detectó una declaración de variables");}
                            | tipo ID '(' listaparametros ')' '{' listasentencias RETURN '(' expresion ')' ';' '}' {System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $13.ival + ": Se detectó una declaración de función");}
                            ;

asignacion                  : identificador ASIG expresion                                 {System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $3.ival + ": Se detectó una asignación simple");}
                            | listavariables '=' listaconstantes                           {System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $3.ival + ": Se detectó una asignación múltiple");}
                            ;

cuerpo_bloque               : '{' listasentenciasejecutables '}'
                            | sentenciaejecutable ';'
                            | '{''}'
                            ;

control                     : IF condicion cuerpo_bloque ELSE cuerpo_bloque ENDIF          {System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $6.ival + ": Se detectó un bloque IF-ELSE");}
                            | IF condicion cuerpo_bloque ENDIF                             {System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $4.ival + ": Se detectó un bloque IF");}
                            | DO cuerpo_bloque UNTIL condicion                             {System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $4.ival + ": Se detectó un bloque DO UNTIL");}

                            | IF condicion cuerpo_bloque ELSE cuerpo_bloque                {System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $5.ival + ": falta la palabra ENDIF al final del bloque IF");}
                            | IF condicion cuerpo_bloque                                   {System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $3.ival + ": falta la palabra ENDIF al final del bloque IF");}
                            | IF condicion cuerpo_bloque ELSE ENDIF                        {System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $4.ival + ": falta el cuerpo en el bloque ELSE");}
                            | IF condicion ELSE cuerpo_bloque ENDIF                        {System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta el cuerpo en el bloque IF");}
                            | IF condicion ENDIF                                           {System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta el cuerpo en el bloque IF");}
                            | IF condicion ELSE ENDIF                                      {System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $3.ival + ": falta el cuerpo en los bloques IF y ELSE");}
                            
                            | DO UNTIL condicion                                           {System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": falta de cuerpo en iteracion");}
                            | DO cuerpo_bloque condicion                                   {System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta la palabra UNTIL en la iteracion");}
                            ;
                            
print                       : PRINT '(' expresion ')'
                            | PRINT '(' CADENAML ')'
                            | PRINT '(' ')'                          {System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $3.ival + ": falta de argumento en sentencia print");}
                            ;

condicion                   : '(' cuerpocondicion ')'                
                            | cuerpocondicion ')'                    {System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta el paréntesis de apertura de la condición");}
                            | '(' cuerpocondicion                    {System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta el paréntesis de cierre de la condición");}
                            | cuerpocondicion                        {System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": faltan ambos paréntesis de la condición");}
                            ;

cuerpocondicion             : expresion comparador expresion
                            | expresion                              {System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": La condición es incompleta");}
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
                            | listavariables error identificador    {System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": falta ',' entre variables"); }
                            ;

identificador               : ID
                            | identificador '.' ID
                            ;

funcion                     : ID '(' argumentos_funcion ')'        {System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + $4.ival + ": Se detectó una función invocada correctamente");}
                            ;

argumentos_funcion          : listapinvocacion
                            | identificador  
                            | CTE                                       { constantePositiva($1); }
                            | '-' CTE                                   { constanteNegativa($2); }
                            ;

listapinvocacion            : listapinvocacion ',' pinvocacion 
                            | pinvocacion
                            ;

pinvocacion                 : expresion FLECHA ID                       
                            | TRUNC '(' expresion ')' FLECHA ID
                            | TRUNC '(' expresion FLECHA ID             {System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $3.ival + ": falta el paréntesis de cierre de la función TRUNC");}
                            | TRUNC expresion ')' FLECHA ID             {System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": falta el paréntesis de apertura de la función TRUNC");}
                            | TRUNC expresion FLECHA ID                 {System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": faltan ambos paréntesis de la función TRUNC");}
                            | '(' tipo ID ')' '{' listasentenciasejecutables '}' FLECHA ID
                            | '(' tipo ID ')' '{' listasentenciasejecutables FLECHA ID       {System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $6.ival + ": falta llave de cierre del cuerpo de la función Lambda");}
                            | '(' tipo ID ')' listasentenciasejecutables '}' FLECHA ID       {System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $4.ival + ": falta llave de apertura del cuerpo de la función Lambda");}
                            | '(' tipo ID ')' listasentenciasejecutables FLECHA ID           {System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $4.ival + ": faltan ambas llaves del cuerpo de la función Lambda");}
                            | '(' tipo ID ')' '{' listasentenciasejecutables '}' FLECHA      {System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $8.ival + ": No se especifica el parametro formal al que corresponde el parámetro real");}
                            | expresion FLECHA                          {System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": No se especifica el parametro formal al que corresponde el parámetro real");}
                            | TRUNC '(' expresion ')' FLECHA            {System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $5.ival + ": No se especifica el parametro formal al que corresponde el parámetro real");}
                            ;                                                        

listaparametros             : listaparametros ',' parametro
                            | parametro
                            ;                    

parametro                   : CV tipo ID
                            | tipo ID
                            | CV tipo                                {System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": falta el nombre del parámetro");}
                            | tipo                                   {System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": falta el nombre del parámetro");}
                            | CV ID                                  {System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": falta el tipo del parámetro");}
                            | ID                                     {System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": falta el tipo del parámetro");}
                            ;

expresion                   : expresion '+' termino
                            | expresion '-' termino
                            | error '+' termino                     { System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta operando a la izquierda de '+'"); }
                            | expresion '+' error                   { System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta operando a la derecha de '+'"); }
                            | error '-' termino                     { System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta operando a la izquierda de '-'"); }
                            | expresion '-' error                   { System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta operando a la derecha de '-'"); }
                            | termino
                            ;

termino                     : termino '*' factor
                            | termino '/' factor
                            | error '*' factor                      { System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta operando a la izquierda de '*'"); }
                            | termino '*' error                     { System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta operando a la derecha de '*'"); }
                            | error '/' factor                      { System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta operando a la izquierda de '/'"); }
                            | termino '/' error                     { System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $2.ival + ": falta operando a la derecha de '/'"); }
                            | termino error factor                  { System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": falta operador entre operandos o falta de comparador en condición"); }
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
                            | listaconstantes CTE                   { System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": falta ',' entre constantes"); constantePositiva($2); }
                            | listaconstantes '-' CTE               { System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + $1.ival + ": falta ',' entre constantes"); constanteNegativa($3); }
                            ;


%%

private void yyerror(String s) {
    System.out.println("||================ ANALIZADOR SINTÁCTICO: Error: " + s);
}

public Parser(Map<String, Simbolo> tablaSimbolos, String path)
{
  this.tablaSimbolos = tablaSimbolos;
  this.lexer = new Lexico(tablaSimbolos);
  this.lexer.cargarBuffer(path);
}

private void constanteNegativa(ParserVal valCte) {
    if (valCte.obj instanceof SimboloLong) {
      ((SimboloLong) valCte.obj).setValor(-(((SimboloLong) valCte.obj).getValor()));
      ((SimboloLong) valCte.obj).setLexema("-" + ((SimboloLong) valCte.obj).getLexema());
    } else {
      ((SimboloCteFloat) valCte.obj).setValor(-(((SimboloCteFloat) valCte.obj).getValor()));
      ((SimboloCteFloat) valCte.obj).setLexema("-" + ((SimboloCteFloat) valCte.obj).getLexema());
    }
}

private void constantePositiva(ParserVal valCte) {
    if ( (valCte.obj instanceof SimboloLong) && ( ((SimboloLong) valCte.obj).getValor() == 2147483648L) ) {
      ((SimboloLong) valCte.obj).setValor(2147483647L);
      ((SimboloLong) valCte.obj).setLexema("2147483647L");
      System.out.println("||================ ANALIZADOR SINTÁCTICO: Warning en línea " + valCte.ival + ": el valor de la constante entera es mayor al máximo permitido, se asigna el valor 2147483647");
    }
}