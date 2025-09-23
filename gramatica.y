%{
    import java.io.*;
    import Lex.Lex;
%}

%token ID CTE ASIG IF THEN ELSE ENDIF MAYORIG MENORIG IGUAL DISTINTO LONG RETURN DO UNTIL CV FLECHA PRINT CADENAML TRUNC

%%
prog                : ID '{' listasentencia '}'
                    ;

listasentencia      : listasentencia sentencia ';'
                    | sentencia ';'
                    ;

sentencia           : asignacion 
                    | declaracion
                    | control
                    | print
                    /*
                    | sentencia_matched
                    | sentencia_unmatched
                    */
                    | funcion
                    ;

declaracion         : tipo listavariables
                    | tipo ID '(' listaparametros ')' '{' listasentencia RETURN '(' expresion ')' ';' '}'
                    ;

asignacion          : ID ASIG expresion
                    | ID'.'ID ASIG expresion
                    | listavariables '=' listaexpresiones
                    ;

control             : IF '(' condicion ')' THEN '{' listasentencia '}' ELSE '{' listasentencia '}' ENDIF
                    | IF '(' condicion ')' THEN '{' listasentencia '}' ENDIF
                    | DO '{' listasentencia '}' UNTIL '(' condicion ')'
                    ;

/*
sentencia_matched   : IF '(' condicion ')' THEN '{' listasentencia '}' ELSE '{' listasentencia '}'
                    | otras_sentencias_matched
                    ;

sentencia_unmatched : IF '(' condicion ')' THEN '{' listasentencia '}'
                    | IF '(' condicion ')' THEN '{' listasentencia '}' ELSE '{' sentencia_unmatched '}'
                    | otras_sentencias_unmatched
                    ;
*/

print               : PRINT '(' expresion ')'
                    | PRINT '(' CADENAML ')'
                    ;   

condicion           : expresion comparador expresion
                    ;

comparador          : '>'
                    | '<'
                    | MAYORIG
                    | MENORIG
                    | IGUAL
                    | DISTINTO
                    ;

tipo                : LONG
                    ;

listavariables      : listavariables ',' ID
                    | ID
                    ;

listaparametros     : listaparametros ',' parametro
                    | parametro
                    ;

parametro           : CV tipo ID // es opcional la semantica
                    | tipo ID
                    ;

expresion           : expresion '+' termino
                    | expresion '-' termino
                    | termino
                    ;

listaexpresiones    : listaexpresiones ',' expresion
                    | expresion
                    ;   

termino             : termino '*' factor
                    | termino '/' factor
                    | factor
                    ;

factor              : ID
                    | CTE
                    | funcion
                    ;

funcion             : ID '(' listapinvocacion ')'
                    ;

listapinvocacion    : listapinvocacion ',' pinvocacion 
                    | pinvocacion
                    ;

pinvocacion         : expresion FLECHA ID
                    | TRUNC '(' expresion ')' FLECHA ID
                    ; 