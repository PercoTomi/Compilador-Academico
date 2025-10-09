%{
    import java.io.*;
    import Simbolos.*;
    import java.util.Map;
    import java.util.HashMap;
%}

%token ID CTE ASIG IF ELSE ENDIF MAYORIG MENORIG IGUAL DISTINTO LONG RETURN DO UNTIL CV FLECHA PRINT CADENAML TRUNC LAMBDA

%%
prog                : ID '{' listasentencia '}'
                    | '{' listasentencia '}'                {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el nombre del programa");} 
                    | ID '{' listasentencia                 {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta llave de cierre del cuerpo del programa");} 
                    | ID listasentencia '}'                 {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta la llave de apertura del cuerpo del programa");} 
                    | ID listasentencia                     {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": faltan llaves que encierren el cuerpo del programa");} 
                    ;

listasentencia      : listasentencia sentencia          {System.out.println("Se detectó una listasentencia sentencia ;");}
                    | sentencia                         {System.out.println("Se detectó una sentencia ;");}
                    |
                    //| listasentencia sentencia error             {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta punto y coma al final de la sentencia");} 
                    //| sentencia error                             {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta punto y coma al final de la sentencia");} 
                    //| error                                 {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta punto y coma al final de la sentencia");}
                    ;

sentencia           : asignacion ';'                               
                    | declaracion  ';'                         
                    | control ';'                               
                    | print ';'                                 {System.out.println("Se detectó un print");}
                    | funcion ';'                               {System.out.println("Se obtuvo una sentencia desde una invocación a función");}
                    //| sentencia_error   
                    ;

/*sentencia_error     : asignacion                        {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta punto y coma al final de la sentencia");}
                    | declaracion                       {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta punto y coma al final de la sentencia");}
                    | control                           {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta punto y coma al final de la sentencia");}
                    | print                             {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta punto y coma al final de la sentencia");}
                    | funcion                           {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta punto y coma al final de la sentencia");}                          
                    ;*/

declaracion         : tipo listavariables                   {System.out.println("Se detectó una declaración de variables");}  
                    | tipo ID '(' listaparametros ')' '{' listasentencia RETURN '(' expresion ')' ';' '}' {System.out.println("Se detectó una declaración de función");}
                    ;

asignacion          : identificador ASIG expresion          {System.out.println("Se detectó una asignación simple");}
                    | listavariables '=' listaconstantes    {System.out.println("Se detectó una asignación múltiple");}
                    ;

listaconstantes     : listaconstantes ',' CTE
                    | CTE
                    | listaconstantes CTE                   {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta coma entre constantes");}
                    ;

control             : IF '(' condicion ')' '{' listasentencia '}' ELSE '{' listasentencia '}' ENDIF {System.out.println("Se detectó un bloque IF-ELSE");}
                    | IF '(' condicion ')' '{' listasentencia '}' ENDIF                             {System.out.println("Se detectó un bloque IF");}
                    | DO '{' listasentencia '}' UNTIL '(' condicion ')'                             {System.out.println("Se detectó un bloque DO UNTIL");}  
                    | IF condicion ')' '{' listasentencia '}' ELSE '{' listasentencia '}' ENDIF     {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta del parentesis de cierre de la condicion del bloque IF");} 
                    | IF '(' condicion '{' listasentencia '}' ELSE '{' listasentencia '}' ENDIF     {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta del parentesis de apertura de la condición del bloque IF");} 
                    | IF condicion '{' listasentencia '}' ELSE '{' listasentencia '}' ENDIF         {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta de parentesis de la condicion del bloque IF");} 
                    | IF condicion ')' '{' listasentencia '}' ENDIF                                 {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta del parentesis de cierre de la condicion del bloque IF");}
                    | IF '(' condicion '{' listasentencia '}' ENDIF                                 {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta del parentesis de apertura de la condición del bloque IF");}
                    | IF condicion '{' listasentencia '}' ENDIF                                     {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta de parentesis de la condicion del bloque IF");}
                    | IF '(' condicion ')' '{' listasentencia '}' ELSE '{' listasentencia '}'       {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta la palabra ENDIF al final del bloque IF");}
                    | IF '(' condicion ')' '{' listasentencia '}'                                   {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta la palabra ENDIF al final del bloque IF");}
                    | IF '(' condicion ')' '{' listasentencia '}' ELSE '{''}' ENDIF                 {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el cuerpo en el bloque ELSE");}
                    | IF '(' condicion ')' '{''}' ELSE '{' listasentencia '}' ENDIF                 {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el cuerpo en el bloque IF");}
                    | IF '(' condicion ')' '{''}' ENDIF                                             {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el cuerpo en el bloque IF");}
                    | IF '(' condicion ')' '{''}' ELSE '{''}' ENDIF                                 {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el cuerpo en los bloques IF y ELSE");}
                    | DO '{' listasentencia '}' UNTIL condicion ')'                                 {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el parentesis de apertura de la condición del bloque DO UNTIL");}
                    | DO '{' listasentencia '}' UNTIL '(' condicion                                 {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el parentesis de apertura de la condición del bloque DO UNTIL");}
                    | DO '{' listasentencia '}' UNTIL condicion                                     {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta de parentesis en la condición del bloque DO UNTIL");}
                    | DO '{' '}' UNTIL '(' condicion ')'                                            {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta de cuerpo en iteracion");}
                    | DO '{' listasentencia '}' '(' condicion ')'                                   {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta la palabra UNTIL en la iteracion");}
                    ;

print               : PRINT '(' expresion ')'                  
                    | PRINT '(' CADENAML ')'
                    | PRINT '(' ')'                             {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta de argumento en sentencia print");}
                    ;   

condicion           : expresion comparador expresion
                    //| expresion expresion                       {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operador comparador en la condición");}
                    ;

comparador          : '>'
                    | '<'
                    | MAYORIG
                    | MENORIG
                    | IGUAL
                    | DISTINTO
                    ;

tipo                : LONG
                    | LAMBDA
                    ;

identificador       : ID                                        {System.out.println("Se detectó un IDENTIFICADOR");}
                    | identificador '.' ID                      {System.out.println("Se detectó un IDENTIFICADOR compuesto");}
                    ;

listavariables      : listavariables ',' identificador
                    | identificador
                    | listavariables ID                                         {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta coma entre variables");}
                    | listavariables identificador '.' ID                       {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta coma entre variables");}
                    ;

listaparametros     : listaparametros ',' parametro
                    | parametro
                    ;

parametro           : CV tipo ID // es opcional la semantica
                    | tipo ID
                    | CV tipo                                   {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el nombre del parámetro");}
                    | tipo                                      {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el nombre del parámetro");}
                    | CV ID                                     {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el tipo del parámetro");}
                    | ID                                        {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el tipo del parámetro");}
                    ;

expresion           : expresion '+' termino                     {System.out.println("Se detectó una expresión desde una suma");}  
                    | expresion '-' termino                     {System.out.println("Se detectó una expresión desde una resta");}
                    | termino                                   {System.out.println("Se detectó una expresión desde un término");}
                    | expresion '+' error                       {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando derecho de la suma");}
                    | expresion '-' error                       {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando derecho de la resta");}
                    | error '+' termino                         {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando izquierdo de la suma");}
                    | error '-' termino                         {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando izquierdo de la resta");}
                    //PREGUNTAR SI SE INCLUYE| '+'                                       {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta ambos operandos de la suma");}
                    //PREGUNTAR SI SE INCLUYE| '-'                                       {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta ambos operandos de la resta");}
                    //| expresion error termino                   {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta de operador");}
                    ;

termino             : termino '*' factor 
                    | termino '/' factor
                    | factor                                    {System.out.println("Se detecto un TERMINO desde un FACTOR");}
                    | termino '*' error                         {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando derecho de la multiplicación");}
                    | termino '/' error                         {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando derecho de la división");} 
                    | error '*' factor                          {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando izquierdo de la multiplicación");}
                    | error '/' factor                          {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando izquierdo de la división");}
                    //PREGUNTAR SI SE INCLUYE| '*'                                       {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta ambos operandos de la multiplicación");}
                    //PREGUNTAR SI SE INCLUYE| '/'                                       {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta ambos operandos de la división");}
                    //PREGUNTAR SI ES NECESARIO (Lo contemplamos en expresion?)| termino factor                            {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta de operador");}              
                    ;

factor              : identificador                             {System.out.println("Se detectó un FACTOR IDENTIFICADOR");}
                    | CTE                                       {System.out.println("Se detectó un FACTOR CTE");}
                    | funcion                                   {System.out.println("Se detectó un FACTOR FUNCIÓN");} 
                    //| identificador identificador              {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operador entre factores");}
                    ;

funcion             : ID '(' listapinvocacion ')'                {System.out.println("Se detectó una función invocada correctamente");}
                    | ID '(' identificador ')'                   {System.out.println("Se detectó una función invocada correctamente");}
                    | ID '(' CTE ')'                             {System.out.println("Se detectó una función invocada correctamente");}
                    | error '(' listapinvocacion ')'             {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el nombre de la funcion");}
                    | error '(' identificador ')'                {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el nombre de la funcion");}
                    | error '(' CTE ')'                          {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el nombre de la funcion");}
                    ;

listapinvocacion    : listapinvocacion ',' pinvocacion 
                    | pinvocacion
                    ;

pinvocacion         : expresion FLECHA ID                       
                    | TRUNC '(' expresion ')' FLECHA ID
                    | TRUNC '(' expresion FLECHA ID             {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el paréntesis de cierre de la función TRUNC");}
                    | TRUNC expresion ')' FLECHA ID             {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el paréntesis de apertura de la función TRUNC");}
                    | TRUNC expresion FLECHA ID                 {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": faltan ambos paréntesis de la función TRUNC");}
                    | '(' tipo ID ')' '{' listasentencia '}'
                    | '(' tipo ID ')' '{' listasentencia        {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta llave de cierre del cuerpo de la función Lambda");}
                    | '(' tipo ID ')' listasentencia '}'        {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta llave de apertura del cuerpo de la función Lambda");}
                    | '(' tipo ID ')' listasentencia            {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": faltan ambas llaves del cuerpo de la función Lambda");}
                    | expresion FLECHA                          {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": No se especifica el parametro formal al que corresponde el parámetro real");}
                    | TRUNC '(' expresion ')' FLECHA            {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": No se especifica el parametro formal al que corresponde el parámetro real");}
                    ;


%%

private void yyerror(String s) {
    System.out.println("Error: " + s);
}

public Parser(Map<String, Simbolo> tablaSimbolos, String path)
{
  this.tablaSimbolos = tablaSimbolos;
  this.lexer = new Lexico(tablaSimbolos);
  this.lexer.cargarBuffer(path);
}