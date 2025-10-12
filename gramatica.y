%{
    import java.io.*;
    import Simbolos.*;
    import java.util.Map;
    import java.util.HashMap;
%}

%token ID CTE ASIG IF ELSE ENDIF MAYORIG MENORIG IGUAL DISTINTO LONG RETURN DO UNTIL CV FLECHA PRINT CADENAML TRUNC LAMBDA EOF

%%
prog                : ID '{' listasentencias '}' EOF
                    | '{' listasentencias '}' EOF               {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el nombre del programa");} 
                    | ID '{' listasentencias EOF                {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta llave de cierre del cuerpo del programa");} 
                    | ID listasentencias '}' EOF                {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta la llave de apertura del cuerpo del programa");} 
                    | ID listasentencias EOF                    {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta la llave de apertura y la llave de cierre del cuerpo del programa");}
                    | ID '{' listasentencias error  EOF         {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": el programa terminó de forma inválida");}'}'
                    | ID '{' error EOF                          {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": no hay una sentencia válida dentro del cuerpo del programa");}'}'
                    | ID '{' listasentencias error '}' EOF      {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": el programa terminó de forma inválida");}
                    | ID '{' error '}' EOF                      {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": no hay una sentencia válida dentro del cuerpo del programa");}
                    ;

listasentencias     : listasentencias sentencia                 {System.out.println("Se detectó una listasentencias sentencia ;");}
                    | sentencia                                 {System.out.println("Se detectó una sentencia ;");}
                    | listasentencias error ';'                 {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta de punto y coma en una sentencia anterior o sentencia no válida antes del punto y coma.");}
                    | error ';'                                 {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta de punto y coma en una sentencia anterior o sentencia no válida antes del punto y coma.");}
                    ;

sentencia           : sentenciaejecutable ';'                   {System.out.println("Se detectó una sentenciaejecutable ;");}              
                    | declaracion ';'                           {System.out.println("Se detectó una declaración ;");}
                    ;                           

listasentenciasejecutables: listasentenciasejecutables sentenciaejecutable ';'  
                          | sentenciaejecutable  ';'
                          ;

sentenciaejecutable   : asignacion                                                       
                      | control                               
                      | print                                   {System.out.println("Se detectó un print");}
                      //| funcion                                 {System.out.println("Se obtuvo una sentencia desde una invocación a función");} 
                      ;

declaracion         : tipo listavariables                       {System.out.println("Se detectó una declaración de variables");}  
                    | tipo ID '(' listaparametros ')' '{' listasentencias RETURN '(' expresion ')' ';' '}' {System.out.println("Se detectó una declaración de función");}
                    ;

asignacion          : identificador ASIG expresion              {System.out.println("Se detectó una asignación simple");}
                    //| identificador ASIG expresion error         {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando derecho en la asignación");}
                    | listavariables '=' listaconstantes        {System.out.println("Se detectó una asignación múltiple");}
                    ;

listaconstantes     : listaconstantes ',' CTE
                    | CTE
                    | listaconstantes CTE                       {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta coma entre constantes");}
                    ;

cuerpo_control      : '{' listasentenciasejecutables '}' 
                    | '{' '}'
                    ; 

control             : IF condicion cuerpo_control ELSE cuerpo_control ENDIF {System.out.println("Se detectó un bloque IF-ELSE");}
                    | IF condicion cuerpo_control ENDIF                             {System.out.println("Se detectó un bloque IF");}
                    | DO cuerpo_control UNTIL condicion                             {System.out.println("Se detectó un bloque DO UNTIL");}  
                  
                    | IF condicion cuerpo_control ELSE cuerpo_control       {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta la palabra ENDIF al final del bloque IF");}
                    | IF condicion cuerpo_control                                   {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta la palabra ENDIF al final del bloque IF");}
                    | IF condicion cuerpo_control ELSE ENDIF                 {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el cuerpo en el bloque ELSE");}
                    | IF condicion ELSE cuerpo_control ENDIF                 {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el cuerpo en el bloque IF");}
                    | IF condicion ENDIF                                             {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el cuerpo en el bloque IF");}
                    | IF condicion ELSE ENDIF                                 {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el cuerpo en los bloques IF y ELSE");}
                    
                    | DO UNTIL condicion                                            {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta de cuerpo en iteracion");}
                    | DO cuerpo_control condicion                                   {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta la palabra UNTIL en la iteracion");}
                    ;

print               : PRINT '(' expresion ')'                  
                    | PRINT '(' CADENAML ')'
                    | PRINT '(' ')'                             {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta de argumento en sentencia print");}
                    ;   

condicion           : '(' cuerpocondicion ')'                   {System.out.println("Se detectó una condición entre paréntesis");}
                    | cuerpocondicion ')'                       {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el paréntesis de apertura de la condición");}
                    | '(' cuerpocondicion                       {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el paréntesis de cierre de la condición");}
                    | cuerpocondicion                           {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": faltan ambos paréntesis de la condición");}
                    ;

cuerpocondicion     : expresion comparador expresion
                    | expresion expresion                       {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el comparador en la condición");}
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
                    | listavariables ID                         {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta coma entre variables");}
                    | listavariables identificador '.' ID       {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta coma entre variables");}
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
                    | expresion error termino                   {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta de operador");}
                    ;

termino             : termino '*' factor 
                    | termino '/' factor
                    | factor                                    {System.out.println("Se detecto un TERMINO desde un FACTOR");}
                    | termino '*' error                         {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando derecho de la multiplicación");}
                    | termino '/' error                         {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando derecho de la división");} 
                    | error '*' factor                          {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando izquierdo de la multiplicación");}
                    | error '/' factor                          {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando izquierdo de la división");}              
                    ;

factor              : identificador                             {System.out.println("Se detectó un FACTOR IDENTIFICADOR");}
                    | CTE                                       {System.out.println("Se detectó un FACTOR CTE");}
                    | funcion                                   {System.out.println("Se detectó un FACTOR FUNCIÓN");}
                    ;

funcion             : ID '(' listapinvocacion ')'               {System.out.println("Se detectó una función invocada correctamente");}
                    | ID '(' identificador ')'                  {System.out.println("Se detectó una función invocada correctamente");}
                    | ID '(' CTE ')'                            {System.out.println("Se detectó una función invocada correctamente");}
                    | error '(' listapinvocacion ')'            {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el nombre de la funcion");}
                    | error '(' identificador ')'               {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el nombre de la funcion");}
                    | error '(' CTE ')'                         {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el nombre de la funcion");}
                    ;

listapinvocacion    : listapinvocacion ',' pinvocacion 
                    | pinvocacion
                    ;

pinvocacion         : expresion FLECHA ID                       
                    | TRUNC '(' expresion ')' FLECHA ID
                    | TRUNC '(' expresion FLECHA ID             {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el paréntesis de cierre de la función TRUNC");}
                    | TRUNC expresion ')' FLECHA ID             {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el paréntesis de apertura de la función TRUNC");}
                    | TRUNC expresion FLECHA ID                 {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": faltan ambos paréntesis de la función TRUNC");}
                    | '(' tipo ID ')' '{' listasentenciasejecutables '}' FLECHA ID
                    | '(' tipo ID ')' '{' listasentenciasejecutables FLECHA ID       {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta llave de cierre del cuerpo de la función Lambda");}
                    | '(' tipo ID ')' listasentenciasejecutables '}' FLECHA ID       {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta llave de apertura del cuerpo de la función Lambda");}
                    | '(' tipo ID ')' listasentenciasejecutables FLECHA ID           {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": faltan ambas llaves del cuerpo de la función Lambda");}
                    | '(' tipo ID ')' '{' listasentenciasejecutables '}' FLECHA   {System.out.println("Error sintáctico en línea " + this.yylval.ival + ": No se especifica el parametro formal al que corresponde el parámetro real");}
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