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

listasentencia      : listasentencia sentencia ';'                      {System.out.println("Se detectó una lista de sentencias seguida de una sentencia");}
                    | sentencia ';'                                     {System.out.println("Se detectó una sentencia");}
                    | listasentencia sentencia                          {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta punto y coma al final de la lista de sentencias con otra sentencia");}
                    | sentencia                                         {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta punto y coma al final de la sentencia");}
                    ;

sentencia           : asignacion 
                    | declaracion  
                    | control 
                    | print                                             {System.out.println("Se detectó un print");}
                    | funcion                                           {System.out.println("Se obtuvo una sentencia desde la invocación de una función");}
                    ;

declaracion         : tipo listavariables                                                                        {System.out.println("Se detectó una declaración de variables");}
                    | tipo ID '(' listaparametros ')' '{' listasentencia RETURN '(' expresion ')' ';' '}'        {System.out.println("Se detectó una declaración de función");}
                    ;

asignacion          : identificador ASIG expresion                       {System.out.println("Se detectó una asignación simple");}
                    | listavariables '=' listaconstantes                 {System.out.println("Se detectó una asignación múltiple");}
                    ;

listaconstantes     : listaconstantes ',' CTE
                    | CTE
                    | listaconstantes CTE                                {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta coma entre constantes");}
                    ;

cuerpo_bloque       : '{' listasentencia '}'
                    | sentencia
                    | '{''}'
                    ;

control             : IF '(' condicion ')' cuerpo_bloque ELSE cuerpo_bloque ENDIF           {System.out.println("Se detectó un bloque IF-ELSE");}
                    | IF '(' condicion ')' cuerpo_bloque ENDIF                              {System.out.println("Se detectó un bloque IF");}
                    | DO cuerpo_bloque UNTIL '(' condicion ')'                              {System.out.println("Se detectó un bloque DO UNTIL");}
                      
                    | IF condicion ')' cuerpo_bloque ELSE cuerpo_bloque ENDIF               {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta del parentesis de cierre de la condicion del bloque IF");} 
                    | IF '(' condicion cuerpo_bloque ELSE cuerpo_bloque ENDIF               {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta del parentesis de apertura de la condición del bloque IF");} 
                    | IF condicion cuerpo_bloque ELSE cuerpo_bloque ENDIF                   {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta de parentesis de la condicion del bloque IF");} 
                    | IF condicion ')' cuerpo_bloque ENDIF                                  {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta del parentesis de apertura de la condicion del bloque IF");}
                    | IF '(' condicion cuerpo_bloque ENDIF                                  {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta del parentesis de cierre de la condición del bloque IF");}
                    | IF condicion cuerpo_bloque ENDIF                                      {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta de ambos parentesis de la condicion del bloque IF");}
                    | IF '(' condicion ')' cuerpo_bloque ELSE cuerpo_bloque error           {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta la palabra ENDIF al final del bloque IF");}
                    | IF '(' condicion ')' cuerpo_bloque error                              {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta la palabra ENDIF al final del bloque IF");}
                    | IF '(' condicion ')' cuerpo_bloque ELSE ENDIF                         {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el cuerpo en el bloque ELSE");}
                    | IF '(' condicion ')'  ELSE cuerpo_bloque ENDIF                        {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el cuerpo en el bloque IF");}
                    | IF '(' condicion ')'  ENDIF                                           {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el cuerpo en el bloque IF");}
                    | IF '(' condicion ')'  ELSE  ENDIF                                     {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el cuerpo en los bloques IF y ELSE");}
                    | DO cuerpo_bloque UNTIL condicion ')'                                  {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el parentesis de apertura de la condición del bloque DO UNTIL");}
                    | DO cuerpo_bloque UNTIL '(' condicion                                  {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el parentesis de apertura de la condición del bloque DO UNTIL");}
                    | DO cuerpo_bloque UNTIL condicion                                      {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta de ambos parentesis en la condición del bloque DO UNTIL");}
                    | DO UNTIL '(' condicion ')'                                            {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta de cuerpo en iteracion");}
                    | DO cuerpo_bloque error '(' condicion ')'                              {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta la palabra UNTIL en la iteracion");}
                    ;

print               : PRINT '(' expresion ')'
                    | PRINT '(' CADENAML ')'
                    | PRINT '(' ')'                            {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta expresión o cadena en el print");}
                    ;

funcion             : ID '(' listapinvocacion ')'                {System.out.println("Se detectó una invocación a función");}
                    | ID '(' identificador ')'                   {System.out.println("Se detectó una invocación a función");}
                    | ID '(' CTE ')'                             {System.out.println("Se detectó una invocación a función");}
                    | error '(' listapinvocacion ')'             {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el nombre de la funcion");}
                    | error '(' identificador ')'                {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el nombre de la funcion");}
                    | error '(' CTE ')'                          {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el nombre de la funcion");}
                    ;

condicion           : expresion comparador expresion
                    //| expresion expresion                {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta un operador de comparación entre las expresiones");}
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

identificador       : ID
                    | identificador '.' ID
                    ;

listavariables      : listavariables ',' identificador
                    | identificador
                    //| listavariables ID                                         {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta coma entre variables");}
                    //| listavariables identificador '.' ID                       {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta coma entre variables");}
                    ;

listaparametros     : listaparametros ',' parametro
                    | parametro
                    ;

parametro           : CV tipo ID
                    | tipo ID
                    ;

expresion           : expresion '+' termino
                    | expresion '-' termino
                    | termino
                    | expresion '+' error                       {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando derecho de la suma");}
                    | expresion '-' error                       {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando derecho de la resta");}
                    | error '+' termino                         {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando izquierdo de la suma");}
                    | error '-' termino                         {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando izquierdo de la resta");}
                    //| expresion  termino                     {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operador entre términos");}
                    ;

termino             : termino '*' factor
                    | termino '/' factor
                    | factor
                    | termino '*' error                         {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando derecho de la multiplicación");}
                    | termino '/' error                         {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando derecho de la división");} 
                    | error '*' factor                          {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando izquierdo de la multiplicación");}
                    | error '/' factor                          {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando izquierdo de la división");}
                    ;

factor              : identificador
                    | CTE
                    | funcion
                    ;

listapinvocacion    : listapinvocacion ',' pinvocacion
                    | pinvocacion
                    ;

pinvocacion         : expresion FLECHA ID
                    | TRUNC '(' expresion ')' FLECHA ID
                    | '(' tipo ID ')' '{' listasentencia '}'
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