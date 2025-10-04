%{
    import java.io.*;
    import Lex.Lex;
%}

%token ID CTE ASIG IF ELSE ENDIF MAYORIG MENORIG IGUAL DISTINTO LONG RETURN DO UNTIL CV FLECHA PRINT CADENAML TRUNC LAMBDA

%%
prog                : ID '{' listasentencia '}'
                    | '{' listasentencia '}'                    {System.out.println("Error sintáctico: falta el nombre del programa");}
                    | ID '{' listasentencia                     {System.out.println("Error sintáctico: falta llave de cierre del cuerpo del programa");}
                    | ID listasentencia '}'                     {System.out.println("Error sintáctico: falta la llave de apertura del cuerpo del programa");}
                    | ID listasentencia                         {System.out.println("Error sintáctico: faltan llaves que encierren el cuerpo del programa");}
                    ;

listasentencia      : listasentencia sentencia ';'
                    | sentencia ';'
                    | listasentencia                            {System.out.println("Error sintáctico: falta punto y coma al final de la sentencia");}
                    | sentencia                                 {System.out.println("Error sintáctico: falta punto y coma al final de la sentencia");}
                    ;

sentencia           : asignacion 
                    | declaracion
                    | control
                    | print
                    | funcion
                    ;

declaracion         : tipo listavariables
                    | tipo ID '(' listaparametros ')' '{' listasentencia RETURN '(' expresion ')' ';' '}'
                    ;

asignacion          : ID ASIG expresion
                    | ID'.'ID ASIG expresion
                    | listavariables '=' listaexpresiones
                    ;

control             : IF '(' condicion ')' '{' listasentencia '}' ELSE '{' listasentencia '}' ENDIF
                    | IF '(' condicion ')' '{' listasentencia '}' ENDIF
                    | DO '{' listasentencia '}' UNTIL '(' condicion ')'
                    | IF condicion ')' '{' listasentencia '}' ELSE '{' listasentencia '}' ENDIF     {System.out.println("Error sintáctico: falta del parentesis de cierre de la condicion del bloque IF");}
                    | IF '(' condicion '{' listasentencia '}' ELSE '{' listasentencia '}' ENDIF     {System.out.println("Error sintáctico: falta del parentesis de apertura de la condición del bloque IF");}
                    | IF condicion '{' listasentencia '}' ELSE '{' listasentencia '}' ENDIF         {System.out.println("Error sintáctico: falta de parentesis de la condicion del bloque IF");}
                    | IF condicion ')' '{' listasentencia '}' ENDIF                                 {System.out.println("Error sintáctico: falta del parentesis de cierre de la condicion del bloque IF");}
                    | IF '(' condicion '{' listasentencia '}' ENDIF                                 {System.out.println("Error sintáctico: falta del parentesis de apertura de la condición del bloque IF");}
                    | IF condicion '{' listasentencia '}' ENDIF                                     {System.out.println("Error sintáctico: falta de parentesis de la condicion del bloque IF");}
                    | IF '(' condicion ')' '{' listasentencia '}' ELSE '{' listasentencia '}'       {System.out.println("Error sintáctico: falta la palabra ENDIF al final del bloque IF");}
                    | IF '(' condicion ')' '{' listasentencia '}'                                   {System.out.println("Error sintáctico: falta la palabra ENDIF al final del bloque IF");} 
                    | IF '(' condicion ')' '{' listasentencia '}' ELSE '{''}' ENDIF                 {System.out.println("Error sintáctico: falta el cuerpo en el bloque ELSE");}
                    | IF '(' condicion ')' '{''}' ELSE '{' listasentencia '}' ENDIF                 {System.out.println("Error sintáctico: falta el cuerpo en el bloque IF");}    
                    | IF '(' condicion ')' '{''}' ENDIF                                             {System.out.println("Error sintáctico: falta el cuerpo en el bloque IF");}   
                    | IF '(' condicion ')' '{''}' ELSE '{''}' ENDIF                                 {System.out.println("Error sintáctico: falta el cuerpo en los bloques IF y ELSE");}
                    | DO '{' listasentencia '}' UNTIL condicion ')'                                 {System.out.println("Error sintáctico: falta el parentesis de apertura de la condición del bloque DO UNTIL");}
                    | DO '{' listasentencia '}' UNTIL '(' condicion                                 {System.out.println("Error sintáctico: falta el parentesis de apertura de la condición del bloque DO UNTIL");}
                    | DO '{' listasentencia '}' UNTIL condicion                                     {System.out.println("Error sintáctico: falta de parentesis en la condición del bloque DO UNTIL");}
                    | DO '{' '}' UNTIL '(' condicion ')'                                            {System.out.println("Error sintáctico: falta de cuerpo en iteracion");}
                    | DO '{' listasentencia '}' '(' condicion ')'                                   {System.out.println("Error sintáctico: falta la palabra UNTIL en la iteracion");}
                    ;

print               : PRINT '(' expresion ')'
                    | PRINT '(' CADENAML ')'
                    | PRINT '(' ')'                             {System.out.println("Error sintáctico: falta de argumento en sentencia print");}
                    ;   

condicion           : expresion comparador expresion
                    | expresion expresion                       {System.out.println("Error sintáctico: falta el operador comparador en la condición");}
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

listavariables      : listavariables ',' ID
                    | listavariables ',' ID '.' ID
                    | ID
                    | ID '.' ID
                    | listavariables ID                         {System.out.println("Error sintáctico: falta coma entre variables");}
                    | listavariables ID '.' ID                  {System.out.println("Error sintáctico: falta coma entre variables");}
                    ;

listaparametros     : listaparametros ',' parametro
                    | parametro
                    ;

parametro           : CV tipo ID // es opcional la semantica
                    | tipo ID
                    | CV tipo                                   {System.out.println("Error sintáctico: falta el nombre del parámetro");}
                    | tipo                                      {System.out.println("Error sintáctico: falta el nombre del parámetro");}
                    | CV ID                                     {System.out.println("Error sintáctico: falta el tipo del parámetro");}
                    | ID                                        {System.out.println("Error sintáctico: falta el tipo del parámetro");}
                    ;

expresion           : expresion '+' termino
                    | expresion '-' termino
                    | termino
                    | expresion '+'                             {System.out.println("Error sintáctico: falta el operando derecho de la suma");}
                    | expresion '-'                             {System.out.println("Error sintáctico: falta el operando derecho de la resta");}
                    | '+' termino                               {System.out.println("Error sintáctico: falta el operando izquierdo de la suma");}
                    | '-' termino                               {System.out.println("Error sintáctico: falta el operando izquierdo de la resta");}
                    | '+'                                       {System.out.println("Error sintáctico: falta ambos operandos de la suma");}
                    | '-'                                       {System.out.println("Error sintáctico: falta ambos operandos de la resta");}
                    | expresion termino                         {System.out.println("Error sintáctico: falta de operador");}
                    ;

listaexpresiones    : listaexpresiones ',' expresion
                    | expresion
                    | listaexpresiones expresion                {System.out.println("Error sintáctico: falta coma entre expresiones");}
                    ;   

termino             : termino '*' factor
                    | termino '/' factor
                    | factor
                    | termino '*'                               {System.out.println("Error sintáctico: falta el operando derecho de la multiplicación");}
                    | termino '/'                               {System.out.println("Error sintáctico: falta el operando derecho de la división");}
                    | '*' factor                                {System.out.println("Error sintáctico: falta el operando izquierdo de la multiplicación");}
                    | '/' factor                                {System.out.println("Error sintáctico: falta el operando izquierdo de la división");}
                    | '*'                                       {System.out.println("Error sintáctico: falta ambos operandos de la multiplicación");}
                    | '/'                                       {System.out.println("Error sintáctico: falta ambos operandos de la división");}
                    | termino factor                            {System.out.println("Error sintáctico: falta de operador");}
                    ;

factor              : ID
                    | ID'.'ID
                    | CTE
                    | funcion
                    ;

funcion             : ID '(' listapinvocacion ')'
                    //| ID '(' ID ')'         // hay que verificar que se trate de una función Lambda (preguntar estos dos casos)
                    //| ID '(' CTE ')'        // hay que verificar que se trate de una función Lambda
                    | '(' listapinvocacion ')'                  {System.out.println("Error sintáctico: falta el nombre de la funcion");}
                    ;

listapinvocacion    : listapinvocacion ',' pinvocacion 
                    | pinvocacion
                    ;

pinvocacion         : expresion FLECHA ID
                    | TRUNC '(' expresion ')' FLECHA ID
                    | TRUNC '(' expresion FLECHA ID             {System.out.println("Error sintáctico: falta el paréntesis de cierre de la función TRUNC");}
                    | TRUNC expresion ')' FLECHA ID             {System.out.println("Error sintáctico: falta el paréntesis de apertura de la función TRUNC");}
                    | TRUNC expresion FLECHA ID                 {System.out.println("Error sintáctico: faltan ambos paréntesis de la función TRUNC");}
                    | '(' tipo ID ')' '{' listasentencia '}'
                    | '(' tipo ID ')' '{' listasentencia        {System.out.println("Error sintáctico: falta llave de cierre del cuerpo de la función Lambda");}
                    | '(' tipo ID ')' listasentencia '}'        {System.out.println("Error sintáctico: falta llave de apertura del cuerpo de la función Lambda");}
                    | '(' tipo ID ')' listasentencia            {System.out.println("Error sintáctico: faltan ambas llaves del cuerpo de la función Lambda");}
                    | expresion FLECHA                          {System.out.println("Error sintáctico: No se especifica el parametro formal al que corresponde el parámetro real");}
                    | TRUNC '(' expresion ')' FLECHA            {System.out.println("Error sintáctico: No se especifica el parametro formal al que corresponde el parámetro real");}
                    ; 