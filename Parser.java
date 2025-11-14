//### This file created by BYACC 1.8(/Java extension  1.15)
//### Java capabilities added 7 Jan 97, Bob Jamison
//### Updated : 27 Nov 97  -- Bob Jamison, Joe Nieten
//###           01 Jan 98  -- Bob Jamison -- fixed generic semantic constructor
//###           01 Jun 99  -- Bob Jamison -- added Runnable support
//###           06 Aug 00  -- Bob Jamison -- made state variables class-global
//###           03 Jan 01  -- Bob Jamison -- improved flags, tracing
//###           16 May 01  -- Bob Jamison -- added custom stack sizing
//###           04 Mar 02  -- Yuval Oren  -- improved java performance, added options
//###           14 Mar 02  -- Tomas Hurka -- -d support, static initializer workaround
//### Please send bug reports to tom@hukatronic.cz
//### static char yysccsid[] = "@(#)yaccpar	1.8 (Berkeley) 01/20/90";






//#line 2 "gramatica.y"
    import java.io.*;
import java.lang.reflect.Array;

import Simbolos.*;
    import java.util.Map;
    import java.util.HashMap;
    import java.nio.file.Path;
    import java.nio.file.Paths;
    import java.nio.file.Files;
    import java.nio.file.StandardOpenOption;
    import java.util.ArrayList;
//#line 26 "Parser.java"




public class Parser
{

boolean yydebug;        //do I want debug output?
int yynerrs;            //number of errors so far
int yyerrflag;          //was there an error?
int yychar;             //the current working character
Lexico lexer;
Map<String, Simbolo> tablaSimbolos;
ArrayList<String> erroresSintacticos;

//########## MESSAGES ##########
//###############################################################
// method: debug
//###############################################################
void debug(String msg)
{
  if (yydebug)
    System.out.println(msg);
}

//########## STATE STACK ##########
final static int YYSTACKSIZE = 500;  //maximum stack size
int statestk[] = new int[YYSTACKSIZE]; //state stack
int stateptr;
int stateptrmax;                     //highest index of stackptr
int statemax;                        //state when highest index reached
//###############################################################
// methods: state stack push,pop,drop,peek
//###############################################################
final void state_push(int state)
{
  try {
		stateptr++;
		statestk[stateptr]=state;
	 }
	 catch (ArrayIndexOutOfBoundsException e) {
     int oldsize = statestk.length;
     int newsize = oldsize * 2;
     int[] newstack = new int[newsize];
     System.arraycopy(statestk,0,newstack,0,oldsize);
     statestk = newstack;
     statestk[stateptr]=state;
  }
}
final int state_pop()
{
  return statestk[stateptr--];
}
final void state_drop(int cnt)
{
  stateptr -= cnt; 
}
final int state_peek(int relative)
{
  return statestk[stateptr-relative];
}
//###############################################################
// method: init_stacks : allocate and prepare stacks
//###############################################################
final boolean init_stacks()
{
  stateptr = -1;
  val_init();
  return true;
}
//###############################################################
// method: dump_stacks : show n levels of the stacks
//###############################################################
void dump_stacks(int count)
{
int i;
  System.out.println("=index==state====value=     s:"+stateptr+"  v:"+valptr);
  for (i=0;i<count;i++)
    System.out.println(" "+i+"    "+statestk[i]+"      "+valstk[i]);
  System.out.println("======================");
}


//########## SEMANTIC VALUES ##########
//public class ParserVal is defined in ParserVal.java


String   yytext;//user variable to return contextual strings
ParserVal yyval; //used to return semantic vals from action routines
ParserVal yylval;//the 'lval' (result) I got from yylex()
ParserVal valstk[];
int valptr;
//###############################################################
// methods: value stack push,pop,drop,peek.
//###############################################################
void val_init()
{
  valstk=new ParserVal[YYSTACKSIZE];
  yyval=new ParserVal();
  yylval=new ParserVal();
  valptr=-1;
}
void val_push(ParserVal val)
{
  if (valptr>=YYSTACKSIZE)
    return;
  valstk[++valptr]=val;
}
ParserVal val_pop()
{
  if (valptr<0)
    return new ParserVal();
  return valstk[valptr--];
}
void val_drop(int cnt)
{
int ptr;
  ptr=valptr-cnt;
  if (ptr<0)
    return;
  valptr = ptr;
}
ParserVal val_peek(int relative)
{
int ptr;
  ptr=valptr-relative;
  if (ptr<0)
    return new ParserVal();
  return valstk[ptr];
}
final ParserVal dup_yyval(ParserVal val)
{
  ParserVal dup = new ParserVal();
  dup.ival = val.ival;
  dup.dval = val.dval;
  dup.sval = val.sval;
  dup.obj = val.obj;
  return dup;
}
//#### end semantic value section ####
public final static short ID=257;
public final static short CTE=258;
public final static short ASIG=259;
public final static short IF=260;
public final static short ELSE=261;
public final static short ENDIF=262;
public final static short MAYORIG=263;
public final static short MENORIG=264;
public final static short IGUAL=265;
public final static short DISTINTO=266;
public final static short LONG=267;
public final static short RETURN=268;
public final static short DO=269;
public final static short UNTIL=270;
public final static short CV=271;
public final static short FLECHA=272;
public final static short PRINT=273;
public final static short CADENAML=274;
public final static short TRUNC=275;
public final static short LAMBDA=276;
public final static short EOF=277;
public final static short YYERRCODE=256;
final static short yylhs[] = {                           -1,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    1,    1,    1,    2,    2,    4,    4,    4,    4,    7,
    7,    7,    7,    5,    5,    5,    5,    3,    3,    3,
    6,    6,    6,    6,    8,    8,   19,   19,   19,   19,
   19,    9,    9,    9,    9,    9,    9,    9,    9,    9,
    9,    9,   11,   11,   11,   20,   20,   20,   20,   21,
   21,   22,   22,   22,   22,   22,   22,   13,   13,   14,
   14,   17,   17,   10,   12,   12,   12,   23,   24,   24,
   25,   25,   25,   25,   25,   25,   25,   25,   25,   25,
   25,   25,   15,   15,   26,   26,   26,   26,   26,   26,
   16,   16,   16,   16,   16,   16,   16,   27,   27,   27,
   27,   27,   27,   27,   27,   28,   28,   28,   28,   18,
   18,   18,   18,   18,   18,
};
final static short yylen[] = {                            2,
    5,    4,    4,    4,    4,    3,    3,    3,    2,    7,
    1,    1,    2,    2,    1,    2,    2,    1,    1,    3,
    2,    2,    1,    1,    1,    1,    1,    2,    3,    2,
    2,   13,   13,   14,    3,    3,    3,    2,    2,    4,
    3,    6,    4,    4,    5,    3,    5,    5,    3,    4,
    3,    3,    4,    4,    3,    3,    2,    2,    1,    3,
    1,    1,    1,    1,    1,    1,    1,    1,    1,    3,
    1,    1,    3,    4,    4,    4,    5,    1,    3,    1,
    3,    6,    5,    5,    4,    9,    8,    8,    7,    8,
    2,    5,    3,    1,    3,    2,    2,    1,    2,    1,
    3,    3,    3,    3,    3,    3,    1,    3,    3,    3,
    3,    3,    3,    3,    1,    1,    2,    1,    1,    3,
    4,    1,    2,    2,    3,
};
final static short yydefred[] = {                         0,
    0,    0,   68,    0,    0,   69,    0,    0,    0,    0,
    0,   15,    0,    0,   24,   25,   26,   27,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,  116,    0,
    0,  119,    0,    0,    0,    0,    0,  115,    0,    0,
    0,    0,    0,    0,    9,    0,    0,   14,   28,    0,
   16,   17,   30,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,   80,    6,    0,    0,    0,    0,    0,    0,    0,
  117,   64,   65,   66,   67,   62,   63,    0,    0,    0,
    0,   49,    0,   57,    0,    0,    0,   51,   39,    0,
    0,    0,   38,    0,   52,    0,   55,    0,    7,    0,
    8,   29,    0,  122,    0,    0,   72,    0,    0,   73,
    0,    2,    4,    0,   76,    0,    0,    0,    0,    0,
   75,   74,    0,    5,    0,    0,    0,  110,  112,   56,
    0,    0,    0,    0,    0,   50,    0,    0,   43,  114,
  111,  108,  113,  109,   41,   21,   37,    0,    0,   44,
   54,   53,    3,  100,    0,    0,    0,   94,  123,  124,
    0,    0,    0,    1,    0,    0,    0,    0,   77,   81,
   79,   48,   47,    0,   40,   20,   99,    0,   96,    0,
    0,  120,    0,  125,    0,    0,    0,   85,    0,    0,
   42,   95,    0,   93,  121,   10,   83,    0,   84,    0,
    0,    0,    0,   82,    0,    0,    0,    0,    0,    0,
    0,    0,   89,    0,    0,    0,    0,   87,    0,   88,
    0,    0,    0,   86,    0,    0,    0,    0,    0,    0,
   32,    0,   33,   34,
};
final static short yydgoto[] = {                          8,
    9,   10,   11,   12,   13,   14,  102,   15,   16,   32,
   18,   19,   20,   21,  167,   33,   22,  116,   42,   35,
   36,   90,   70,   71,   72,  168,   37,   38,
};
final static short yysindex[] = {                       442,
  158,  122,    0,  642,  -24,    0, -155,    0, -118, -155,
 -155,    0,  -12,   15,    0,    0,    0,    0,   22, -198,
   -9,   14,   63,  706,   98, -100,   77,   69,    0,  281,
 -141,    0,  398,   79,  746,  109,   -5,    0,  122,  457,
   97,  163,  107,  -95,    0, -117, -155,    0,    0,  106,
    0,    0,    0,  139,  136,   79,   19,  -76,  281,  -73,
  132,  -83,  -94,  155,  189, -175,  -59,  -16,   20,  160,
  161,    0,    0,  -71,  296,  296,    4,    4,  101,  166,
    0,    0,    0,    0,    0,    0,    0,  299,  304,  281,
  754,    0, -164,    0,    4,  411,  416,    0,    0,  220,
  152,  567,    0,  122,    0,  172,    0,  112,    0,  -60,
    0,    0, -177,    0,  -25,   -6,    0,   79,   10,    0,
 -155,    0,    0,    5,    0,  281,  -33,   40,  243,   44,
    0,    0,  101,    0,   53,   -5,   -5,    0,    0,    0,
   53,   -5,   53,   -5,   10,    0,   42,  764,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  729,  247,    0,
    0,    0,    0,    0, -189,   50,   90,    0,    0,    0,
   27,   61,  197,    0,  -32,   66,   56,  288,    0,    0,
    0,    0,    0,   89,    0,    0,    0,  104,    0,  230,
 -177,    0,  110,    0,   94,  118,  111,    0,  129,  724,
    0,    0, -155,    0,    0,    0,    0,  138,    0, -115,
  778,  809,  823,    0,  783,  148,  113,  368,  837,  382,
  171,  157,    0,  181,  281,  400,  281,    0,  185,    0,
  159,  281,  359,    0,  385,  371,  391,  326,  394,  330,
    0,  332,    0,    0,
};
final static short yyrindex[] = {                         0,
  -29,    0,    0,    0,    0,    0,    0,    0,    0,  -92,
  -91,    0,  575,  688,    0,    0,    0,    0,    0,    0,
    0,   35,  -29,    0,    0,    0,    0,  -41,    0,    0,
    0,    0,  237,  -19,    0,  529,    3,    0,    0,    0,
    0,    0,    0,    0,    0,    0,  -80,    0,    0,    0,
    0,    0,    0,  130,  663,  215,    0,    0,    0,    0,
    0,    0,    0,  121,    0,    0,    0,    0,  145,    0,
  418,    0,    0,    0,    0,    0,    0,    0,    0,  606,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,  482,    0,    0,    0,    0,    0,    0,    0,
  785,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,  504,    0,  365,  628,    0,
    0,    0,    0,    0,    0,    0,    0,    0,  167,   95,
    0,    0,    0,    0,    0,   26,   48,    0,    0,    0,
   70,  307,  329,  351,  259,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,  802,    0,
    0,    0,    0,    0,    0,   96,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,  649,    0,    0,    0,  103,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  128,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,  134,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,
};
final static short yygindex[] = {                         0,
   49,  260,  427,  722,  743,    0,  -77,    0,    0,  646,
    0,  500,  -30,  444,    0,  760,  601,    0,  -21,   12,
  432,    0,    0,    0,  333,  274,  360,  -20,
};
final static int YYTABLESIZE=1113;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         72,
   72,   72,   72,   72,   72,   72,   46,  177,  197,   89,
   89,   88,   88,   93,   72,   43,   72,   72,   72,   72,
   72,  118,  118,  118,   74,  118,   89,  118,   88,  110,
  124,   72,   11,   12,   58,  128,   96,  171,  172,  118,
  118,   97,  118,  107,   13,  107,   51,  107,   31,   26,
   98,   57,   89,  105,   88,   44,  138,  139,   54,   60,
  131,  107,  107,  115,  107,   60,  105,  187,  105,  147,
  105,  193,   63,   52,  150,  152,  154,    3,   71,  164,
   53,   72,  166,   72,  105,  105,    6,  105,  103,    3,
  103,    3,  103,  165,   77,   71,  148,  149,    6,   78,
    6,   23,   25,  118,    2,  118,  103,  103,   79,  103,
  106,    3,  106,    4,  106,  160,   81,    5,   77,   76,
    6,   75,  211,   78,   60,  107,  184,  107,  106,  106,
  190,  106,  215,  191,  188,   91,   98,   66,   91,   98,
   66,   28,   67,   97,    2,   31,   97,  107,  105,   94,
  105,   31,  162,    4,   89,  103,   88,    5,   45,  111,
  166,   30,  116,  116,  112,  116,   31,  116,   92,  173,
  103,   92,  103,   72,   90,   72,   73,   90,  113,   58,
  117,  109,  123,  120,   11,   12,  118,  118,   72,  118,
  121,  118,  106,  122,  106,  125,   13,   25,  129,  235,
  132,   89,   30,   88,  133,  134,  140,   31,  117,  117,
  156,  117,  161,  117,   72,   72,  163,   72,   72,   72,
   72,   72,   72,   72,   72,   72,   72,   72,  126,   72,
   72,   72,  169,   31,   72,   72,  118,  118,  176,  196,
  118,  118,  118,  118,  118,  118,  118,  118,  118,  118,
   95,  170,  118,  118,   72,  130,  118,  118,   71,  107,
   28,   29,  107,  107,  107,  107,  107,  107,  107,  107,
  107,  107,   59,   71,  107,  107,  114,   61,  107,  107,
   24,  174,  105,  179,  192,  105,  105,  105,  105,  105,
  105,  105,  105,  105,  105,   61,  178,  105,  105,   60,
  180,  105,  105,  182,  103,  186,  189,  103,  103,  103,
  103,  103,  103,  103,  103,  103,  103,   60,  194,  103,
  103,  195,  198,  103,  103,   31,  106,  199,  200,  106,
  106,  106,  106,  106,  106,  106,  106,  106,  106,   71,
   31,  106,  106,   31,  155,  106,  106,  102,   31,  102,
  201,  102,  203,   27,   28,   64,   27,   28,   29,   61,
  202,   61,   27,   28,   29,  102,  102,  205,  102,  104,
  206,  104,   65,  104,  207,   65,  116,   27,   28,   29,
  106,   60,  208,   60,  224,  209,   72,  104,  104,   72,
  104,  101,  116,  101,  214,  101,   72,   72,   72,  237,
  118,   89,   72,   88,  223,   72,   72,  225,   70,  101,
  101,  239,  101,   89,   23,   88,  118,    2,   27,   28,
   29,  227,  117,   70,    3,   70,    4,  228,  229,  102,
    5,  102,  104,    6,  136,  137,   47,  230,  117,  232,
   89,  234,   88,  238,   27,   28,   29,  142,  144,  240,
  241,  104,  242,  104,  243,   31,  244,   87,   78,   86,
   31,   80,  212,   55,  204,  181,  100,    0,    0,    0,
    0,   71,    0,  101,   71,  101,   23,    0,    0,    2,
    0,   71,   71,   71,    0,    0,    3,   71,    4,   70,
   71,   71,    5,   61,    0,    6,   61,   61,   61,    0,
    0,    0,    0,   61,   61,   61,    0,    0,   61,   61,
   50,    0,   61,   61,    0,   60,    0,    0,   60,   60,
   60,    0,    0,    0,    0,   60,   60,   60,  158,    0,
   60,   60,    0,    0,   60,   60,   27,   28,   29,    0,
   46,    0,    0,    0,    0,    0,   50,    0,    0,    0,
    0,  135,   28,   29,  141,   28,   29,    0,    0,  143,
   28,   29,   36,  102,    7,    0,  102,  102,  102,  102,
  102,  102,  102,  102,  102,  102,    0,    0,  102,  102,
    0,   99,  102,  102,    0,  104,    0,   59,  104,  104,
  104,  104,  104,  104,  104,  104,  104,  104,    0,   50,
  104,  104,   34,    0,  104,  104,   46,  101,    0,    0,
  101,  101,  101,  101,  101,  101,  101,  101,  101,  101,
   56,   70,  101,  101,   70,   69,  101,  101,   36,  213,
   34,   70,   70,   70,    0,    0,    0,   70,  219,   34,
   70,   70,   34,   34,    0,   17,   17,    0,    0,   17,
    0,   59,   17,   59,    0,   17,   17,   50,  118,   34,
   82,   83,   84,   85,   58,   34,  151,   28,   29,   17,
    0,  153,   28,   29,    0,   34,   34,   34,   34,   34,
   17,    0,    0,    0,    0,   17,   35,    0,   34,   34,
   34,  157,   17,    0,    0,   34,   34,   34,    1,   18,
    0,    2,    0,    0,   34,    0,    0,   45,    3,    0,
    4,    0,   50,   23,    5,    0,    2,    6,   50,    0,
    0,   31,    0,    0,    0,    4,   34,    0,   58,    5,
   58,   48,   49,   34,    0,    0,   17,    0,   46,    0,
    0,   46,    0,    0,    0,   17,   41,   17,   46,   46,
   46,    0,   35,   46,   46,    0,    0,   46,   46,    0,
   36,    0,    0,   36,   40,    0,   17,    0,   49,    0,
   36,   36,   36,   45,    0,   36,   36,   41,    0,   36,
   36,    0,  101,    0,   68,   59,    0,   31,   59,   59,
   59,    0,    0,   17,    0,   59,   59,   59,    0,    0,
   59,   59,  108,   17,   59,   59,    0,    0,    0,    0,
    0,    0,   19,    0,    0,    0,    0,    0,  119,    0,
    0,   49,    0,   23,  127,   34,    2,   34,    0,    0,
   62,   18,   34,   41,   18,    4,    0,    0,   68,    5,
    0,   18,   18,   18,  159,   17,  210,   18,   17,  145,
   18,   18,    0,  185,    0,   17,   17,   17,   17,    0,
   17,    0,   58,    0,   17,   58,   58,   58,   40,    0,
    0,    0,   58,   58,   58,    0,   40,   58,   58,   49,
    0,   58,   58,    0,   35,  175,   40,   35,    0,    0,
   41,    0,   68,    0,   35,   35,   35,    0,   28,   35,
   35,    2,  217,   35,   35,   45,    0,  222,   45,   23,
    4,   39,    0,    0,    5,   45,   45,   45,    0,   31,
   45,   45,   31,    0,   45,   45,   22,    0,    0,   31,
   31,   31,    0,   48,   49,   31,    0,    0,   31,   31,
   49,    0,  101,    0,   19,    0,    0,   19,    0,    0,
    0,    0,  101,  159,   19,   19,   19,  159,    0,    0,
   19,   61,   23,   19,   19,    2,    0,    0,    0,    0,
    0,    0,    3,    0,    4,    0,    0,    0,    5,    0,
   28,    6,    0,    2,  231,   23,  233,    0,    2,    0,
    0,  236,    4,    0,    0,    3,    5,    4,    0,    0,
    0,    5,   28,    0,    6,    2,   91,   92,    0,    0,
   28,    0,    0,    2,    4,  146,    0,    0,    5,    0,
   28,    0,    4,    2,    0,  183,    5,    0,    0,    0,
    0,    0,    4,    0,   28,    0,    5,    2,    0,   28,
    0,   23,    2,    0,   23,    0,    4,    0,    0,  216,
    5,    4,    0,   23,  221,    5,   23,   23,   22,    0,
    0,   22,    0,    0,    0,   23,    0,    0,    2,    0,
   22,    0,    0,   22,   22,    3,  218,    4,    0,   23,
    0,    5,    2,    0,    6,    0,    0,    0,    0,    3,
  220,    4,    0,   23,    0,    5,    2,    0,    6,    0,
    0,    0,    0,    3,  226,    4,    0,    0,    0,    5,
    0,    0,    6,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         41,
   42,   43,   44,   45,   46,   47,  125,   41,   41,   43,
   43,   45,   45,   35,   44,   40,   46,   59,   60,   61,
   62,   41,   42,   43,  125,   45,   43,   47,   45,  125,
  125,   61,  125,  125,   44,   66,   42,   44,   45,   59,
   60,   47,   62,   41,  125,   43,   59,   45,   45,    1,
   39,   61,   43,   42,   45,    7,   77,   78,  257,   46,
   41,   59,   60,   45,   62,   46,   41,  257,   43,   91,
   45,   45,   24,   59,   95,   96,   97,  267,   44,  257,
   59,  123,  113,  125,   59,   60,  276,   62,   41,  267,
   43,  267,   45,  271,   42,   61,  261,  262,  276,   47,
  276,  257,   40,  123,  260,  125,   59,   60,   40,   62,
   41,  267,   43,  269,   45,  104,  258,  273,   42,   43,
  276,   45,  200,   47,   46,  123,  148,  125,   59,   60,
   41,   62,  210,   44,  165,   41,   41,   40,   44,   44,
   40,  257,   45,   41,  260,   45,   44,   41,  123,   41,
  125,   45,   41,  269,   43,   59,   45,  273,  277,  277,
  191,   40,   42,   43,   59,   45,   45,   47,   41,  121,
  123,   44,  125,   44,   41,   46,  277,   44,   40,   44,
  257,  277,  277,  257,  277,  277,   42,   43,   59,   45,
   59,   47,  123,  277,  125,   41,  277,   40,  258,   41,
   41,   43,   40,   45,   44,  277,   41,   45,   42,   43,
   59,   45,   41,   47,  256,  257,  277,  259,  260,  261,
  262,  263,  264,  265,  266,  267,  268,  269,   40,  259,
  272,  273,  258,   45,  276,  277,  256,  257,  272,  272,
  260,  261,  262,  263,  264,  265,  266,  267,  268,  269,
  256,  258,  272,  273,  125,  272,  276,  277,   44,  257,
  257,  258,  260,  261,  262,  263,  264,  265,  266,  267,
  268,  269,  259,   59,  272,  273,  258,   41,  276,  277,
  123,  277,  257,   41,  258,  260,  261,  262,  263,  264,
  265,  266,  267,  268,  269,   59,  257,  272,  273,   41,
  257,  276,  277,  262,  257,   59,  257,  260,  261,  262,
  263,  264,  265,  266,  267,  268,  269,   59,  258,  272,
  273,  125,  257,  276,  277,   45,  257,  272,   41,  260,
  261,  262,  263,  264,  265,  266,  267,  268,  269,  125,
   45,  272,  273,   45,  125,  276,  277,   41,   45,   43,
  262,   45,  123,  256,  257,  258,  256,  257,  258,  123,
  257,  125,  256,  257,  258,   59,   60,  258,   62,   41,
  277,   43,  275,   45,  257,  275,  256,  256,  257,  258,
  274,  123,  272,  125,  272,  257,  257,   59,   60,  260,
   62,   41,  272,   43,  257,   45,  267,  268,  269,   41,
  256,   43,  273,   45,  257,  276,  277,   40,   44,   59,
   60,   41,   62,   43,  257,   45,  272,  260,  256,  257,
  258,   40,  256,   59,  267,   61,  269,  257,  272,  123,
  273,  125,  270,  276,   75,   76,   10,  257,  272,   40,
   43,  257,   45,   59,  256,  257,  258,   88,   89,   59,
  125,  123,   59,  125,  125,   45,  125,   60,   41,   62,
   45,   30,  203,   20,  191,  133,   40,   -1,   -1,   -1,
   -1,  257,   -1,  123,  260,  125,  257,   -1,   -1,  260,
   -1,  267,  268,  269,   -1,   -1,  267,  273,  269,  125,
  276,  277,  273,  257,   -1,  276,  260,  261,  262,   -1,
   -1,   -1,   -1,  267,  268,  269,   -1,   -1,  272,  273,
   11,   -1,  276,  277,   -1,  257,   -1,   -1,  260,  261,
  262,   -1,   -1,   -1,   -1,  267,  268,  269,  102,   -1,
  272,  273,   -1,   -1,  276,  277,  256,  257,  258,   -1,
   59,   -1,   -1,   -1,   -1,   -1,   47,   -1,   -1,   -1,
   -1,  256,  257,  258,  256,  257,  258,   -1,   -1,  256,
  257,  258,   59,  257,  123,   -1,  260,  261,  262,  263,
  264,  265,  266,  267,  268,  269,   -1,   -1,  272,  273,
   -1,  125,  276,  277,   -1,  257,   -1,   59,  260,  261,
  262,  263,  264,  265,  266,  267,  268,  269,   -1,  100,
  272,  273,    2,   -1,  276,  277,  125,  257,   -1,   -1,
  260,  261,  262,  263,  264,  265,  266,  267,  268,  269,
   20,  257,  272,  273,  260,   25,  276,  277,  125,  203,
   30,  267,  268,  269,   -1,   -1,   -1,  273,  212,   39,
  276,  277,   42,   43,   -1,    0,    1,   -1,   -1,    4,
   -1,  123,    7,  125,   -1,   10,   11,  158,   58,   59,
  263,  264,  265,  266,   59,   65,  256,  257,  258,   24,
   -1,  256,  257,  258,   -1,   75,   76,   77,   78,   79,
   35,   -1,   -1,   -1,   -1,   40,   59,   -1,   88,   89,
   90,  125,   47,   -1,   -1,   95,   96,   97,  257,  125,
   -1,  260,   -1,   -1,  104,   -1,   -1,   59,  267,   -1,
  269,   -1,  213,  257,  273,   -1,  260,  276,  219,   -1,
   -1,   59,   -1,   -1,   -1,  269,  126,   -1,  123,  273,
  125,   10,   11,  133,   -1,   -1,   91,   -1,  257,   -1,
   -1,  260,   -1,   -1,   -1,  100,    4,  102,  267,  268,
  269,   -1,  125,  272,  273,   -1,   -1,  276,  277,   -1,
  257,   -1,   -1,  260,  123,   -1,  121,   -1,   47,   -1,
  267,  268,  269,  125,   -1,  272,  273,   35,   -1,  276,
  277,   -1,   40,   -1,   25,  257,   -1,  125,  260,  261,
  262,   -1,   -1,  148,   -1,  267,  268,  269,   -1,   -1,
  272,  273,   43,  158,  276,  277,   -1,   -1,   -1,   -1,
   -1,   -1,  125,   -1,   -1,   -1,   -1,   -1,   59,   -1,
   -1,  100,   -1,  257,   65,  225,  260,  227,   -1,   -1,
  125,  257,  232,   91,  260,  269,   -1,   -1,   79,  273,
   -1,  267,  268,  269,  102,  200,  123,  273,  203,   90,
  276,  277,   -1,  125,   -1,  210,  211,  212,  213,   -1,
  215,   -1,  257,   -1,  219,  260,  261,  262,  123,   -1,
   -1,   -1,  267,  268,  269,   -1,  123,  272,  273,  158,
   -1,  276,  277,   -1,  257,  126,  123,  260,   -1,   -1,
  148,   -1,  133,   -1,  267,  268,  269,   -1,  257,  272,
  273,  260,  125,  276,  277,  257,   -1,  125,  260,  125,
  269,  270,   -1,   -1,  273,  267,  268,  269,   -1,  257,
  272,  273,  260,   -1,  276,  277,  125,   -1,   -1,  267,
  268,  269,   -1,  212,  213,  273,   -1,   -1,  276,  277,
  219,   -1,  200,   -1,  257,   -1,   -1,  260,   -1,   -1,
   -1,   -1,  210,  211,  267,  268,  269,  215,   -1,   -1,
  273,  256,  257,  276,  277,  260,   -1,   -1,   -1,   -1,
   -1,   -1,  267,   -1,  269,   -1,   -1,   -1,  273,   -1,
  257,  276,   -1,  260,  225,  257,  227,   -1,  260,   -1,
   -1,  232,  269,   -1,   -1,  267,  273,  269,   -1,   -1,
   -1,  273,  257,   -1,  276,  260,  261,  262,   -1,   -1,
  257,   -1,   -1,  260,  269,  262,   -1,   -1,  273,   -1,
  257,   -1,  269,  260,   -1,  262,  273,   -1,   -1,   -1,
   -1,   -1,  269,   -1,  257,   -1,  273,  260,   -1,  257,
   -1,  257,  260,   -1,  260,   -1,  269,   -1,   -1,  272,
  273,  269,   -1,  269,  272,  273,  272,  273,  257,   -1,
   -1,  260,   -1,   -1,   -1,  257,   -1,   -1,  260,   -1,
  269,   -1,   -1,  272,  273,  267,  268,  269,   -1,  257,
   -1,  273,  260,   -1,  276,   -1,   -1,   -1,   -1,  267,
  268,  269,   -1,  257,   -1,  273,  260,   -1,  276,   -1,
   -1,   -1,   -1,  267,  268,  269,   -1,   -1,   -1,  273,
   -1,   -1,  276,
};
}
final static short YYFINAL=8;
final static short YYMAXTOKEN=277;
final static String yyname[] = {
"end-of-file",null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,"'('","')'","'*'","'+'","','",
"'-'","'.'","'/'",null,null,null,null,null,null,null,null,null,null,null,"';'",
"'<'","'='","'>'",null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
"'{'",null,"'}'",null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,"ID","CTE","ASIG","IF","ELSE","ENDIF",
"MAYORIG","MENORIG","IGUAL","DISTINTO","LONG","RETURN","DO","UNTIL","CV",
"FLECHA","PRINT","CADENAML","TRUNC","LAMBDA","EOF",
};
final static String yyrule[] = {
"$accept : prog",
"prog : ID '{' bloque_programa '}' EOF",
"prog : ID '{' '}' EOF",
"prog : '{' bloque_programa '}' EOF",
"prog : ID '{' bloque_programa EOF",
"prog : ID bloque_programa '}' EOF",
"prog : ID bloque_programa EOF",
"prog : '{' bloque_programa EOF",
"prog : bloque_programa '}' EOF",
"prog : bloque_programa EOF",
"prog : ID '{' error ';' bloque_programa '}' EOF",
"bloque_programa : listasentencias",
"bloque_programa : listasentencias_conLambda",
"bloque_programa : listasentencias listasentencias_conLambda",
"listasentencias : listasentencias sentencia",
"listasentencias : sentencia",
"sentencia : sentenciaejecutable ';'",
"sentencia : declaracion ';'",
"sentencia : sentenciaejecutable",
"sentencia : declaracion",
"listasentenciasejecutables : listasentenciasejecutables sentenciaejecutable ';'",
"listasentenciasejecutables : sentenciaejecutable ';'",
"listasentenciasejecutables : listasentenciasejecutables sentenciaejecutable",
"listasentenciasejecutables : sentenciaejecutable",
"sentenciaejecutable : asignacion",
"sentenciaejecutable : control",
"sentenciaejecutable : funcion",
"sentenciaejecutable : print",
"listasentencias_conLambda : listasentencias_conLambda sentencia",
"listasentencias_conLambda : listasentencias_conLambda funcion_lambda ';'",
"listasentencias_conLambda : funcion_lambda ';'",
"declaracion : tipo listavariables",
"declaracion : tipo ID '(' listaparametros ')' '{' listasentencias RETURN '(' expresion ')' ';' '}'",
"declaracion : tipo ID '(' listaparametros ')' '{' listasentencias_conLambda RETURN '(' expresion ')' ';' '}'",
"declaracion : tipo ID '(' listaparametros ')' '{' listasentencias listasentencias_conLambda RETURN '(' expresion ')' ';' '}'",
"asignacion : identificador ASIG expresion",
"asignacion : listavariables '=' listaconstantes",
"cuerpo_bloque : '{' listasentenciasejecutables '}'",
"cuerpo_bloque : sentenciaejecutable ';'",
"cuerpo_bloque : '{' '}'",
"cuerpo_bloque : '{' listasentenciasejecutables listasentencias_conLambda '}'",
"cuerpo_bloque : '{' listasentencias_conLambda '}'",
"control : IF condicion cuerpo_bloque ELSE cuerpo_bloque ENDIF",
"control : IF condicion cuerpo_bloque ENDIF",
"control : DO cuerpo_bloque UNTIL condicion",
"control : IF condicion cuerpo_bloque ELSE cuerpo_bloque",
"control : IF condicion cuerpo_bloque",
"control : IF condicion cuerpo_bloque ELSE ENDIF",
"control : IF condicion ELSE cuerpo_bloque ENDIF",
"control : IF condicion ENDIF",
"control : IF condicion ELSE ENDIF",
"control : DO UNTIL condicion",
"control : DO cuerpo_bloque condicion",
"print : PRINT '(' expresion ')'",
"print : PRINT '(' CADENAML ')'",
"print : PRINT '(' ')'",
"condicion : '(' cuerpocondicion ')'",
"condicion : cuerpocondicion ')'",
"condicion : '(' cuerpocondicion",
"condicion : cuerpocondicion",
"cuerpocondicion : expresion comparador expresion",
"cuerpocondicion : expresion",
"comparador : '>'",
"comparador : '<'",
"comparador : MAYORIG",
"comparador : MENORIG",
"comparador : IGUAL",
"comparador : DISTINTO",
"tipo : LONG",
"tipo : LAMBDA",
"listavariables : listavariables ',' identificador",
"listavariables : identificador",
"identificador : ID",
"identificador : identificador '.' ID",
"funcion : ID '(' argumentos_funcion ')'",
"funcion_lambda : ID '(' identificador ')'",
"funcion_lambda : ID '(' CTE ')'",
"funcion_lambda : ID '(' '-' CTE ')'",
"argumentos_funcion : listapinvocacion",
"listapinvocacion : listapinvocacion ',' pinvocacion",
"listapinvocacion : pinvocacion",
"pinvocacion : expresion FLECHA ID",
"pinvocacion : TRUNC '(' expresion ')' FLECHA ID",
"pinvocacion : TRUNC '(' expresion FLECHA ID",
"pinvocacion : TRUNC expresion ')' FLECHA ID",
"pinvocacion : TRUNC expresion FLECHA ID",
"pinvocacion : '(' tipo ID ')' '{' listasentenciasejecutables '}' FLECHA ID",
"pinvocacion : '(' tipo ID ')' '{' listasentenciasejecutables FLECHA ID",
"pinvocacion : '(' tipo ID ')' listasentenciasejecutables '}' FLECHA ID",
"pinvocacion : '(' tipo ID ')' listasentenciasejecutables FLECHA ID",
"pinvocacion : '(' tipo ID ')' '{' listasentenciasejecutables '}' FLECHA",
"pinvocacion : expresion FLECHA",
"pinvocacion : TRUNC '(' expresion ')' FLECHA",
"listaparametros : listaparametros ',' parametro",
"listaparametros : parametro",
"parametro : CV tipo ID",
"parametro : tipo ID",
"parametro : CV tipo",
"parametro : tipo",
"parametro : CV ID",
"parametro : ID",
"expresion : expresion '+' termino",
"expresion : expresion '-' termino",
"expresion : error '+' termino",
"expresion : expresion '+' error",
"expresion : error '-' termino",
"expresion : expresion '-' error",
"expresion : termino",
"termino : termino '*' factor",
"termino : termino '/' factor",
"termino : error '*' factor",
"termino : termino '*' error",
"termino : error '/' factor",
"termino : termino '/' error",
"termino : termino error factor",
"termino : factor",
"factor : CTE",
"factor : '-' CTE",
"factor : identificador",
"factor : funcion",
"listaconstantes : listaconstantes ',' CTE",
"listaconstantes : listaconstantes ',' '-' CTE",
"listaconstantes : CTE",
"listaconstantes : '-' CTE",
"listaconstantes : listaconstantes CTE",
"listaconstantes : listaconstantes '-' CTE",
};

//#line 205 "gramatica.y"

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
//#line 675 "Parser.java"
//###############################################################
// method: yylexdebug : check lexer state
//###############################################################
void yylexdebug(int state,int ch)
{
String s=null;
  if (ch < 0) ch=0;
  if (ch <= YYMAXTOKEN) //check index bounds
     s = yyname[ch];    //now get it
  if (s==null)
    s = "illegal-symbol";
  debug("state "+state+", reading "+ch+" ("+s+")");
}





//The following are now global, to aid in error reporting
int yyn;       //next next thing to do
int yym;       //
int yystate;   //current parsing state from state table
String yys;    //current token string


//###############################################################
// method: yyparse : parse input and execute indicated items
//###############################################################
int yyparse()
{
boolean doaction;
  init_stacks();
  yynerrs = 0;
  yyerrflag = 0;
  yychar = -1;          //impossible char forces a read
  yystate=0;            //initial state
  state_push(yystate);  //save it
  val_push(yylval);     //save empty value
  while (true) //until parsing is done, either correctly, or w/error
    {
    doaction=true;
    if (yydebug) debug("loop"); 
    //#### NEXT ACTION (from reduction table)
    for (yyn=yydefred[yystate];yyn==0;yyn=yydefred[yystate])
      {
      if (yydebug) debug("yyn:"+yyn+"  state:"+yystate+"  yychar:"+yychar);
      if (yychar < 0)      //we want a char?
        {
        yychar = lexer.yylex(this.yylval);        //get next character
        if (yydebug) debug(" next yychar:"+yychar);
        //#### ERROR CHECK ####
        if (yychar < 0)    //it it didn't work/error
          {
          yychar = 0;      //change it to default string (no -1!)
          if (yydebug)
            yylexdebug(yystate,yychar);
          }
        }//yychar<0
      yyn = yysindex[yystate];  //get amount to shift by (shift index)
      if ((yyn != 0) && (yyn += yychar) >= 0 &&
          yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
        {
        if (yydebug)
          debug("state "+yystate+", shifting to state "+yytable[yyn]);
        //#### NEXT STATE ####
        yystate = yytable[yyn];//we are in a new state
        state_push(yystate);   //save it
        val_push(yylval);      //push our lval as the input for next rule
        yychar = -1;           //since we have 'eaten' a token, say we need another
        if (yyerrflag > 0)     //have we recovered an error?
           --yyerrflag;        //give ourselves credit
        doaction=false;        //but don't process yet
        break;   //quit the yyn=0 loop
        }

    yyn = yyrindex[yystate];  //reduce
    if ((yyn !=0 ) && (yyn += yychar) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
      {   //we reduced!
      if (yydebug) debug("reduce");
      yyn = yytable[yyn];
      doaction=true; //get ready to execute
      break;         //drop down to actions
      }
    else //ERROR RECOVERY
      {
      if (yyerrflag==0)
        {
        yyerror("syntax error");
        yynerrs++;
        }
      if (yyerrflag < 3) //low error count?
        {
        yyerrflag = 3;
        while (true)   //do until break
          {
          if (stateptr<0)   //check for under & overflow here
            {
            yyerror("stack underflow. aborting...");  //note lower case 's'
            return 1;
            }
          yyn = yysindex[state_peek(0)];
          if ((yyn != 0) && (yyn += YYERRCODE) >= 0 &&
                    yyn <= YYTABLESIZE && yycheck[yyn] == YYERRCODE)
            {
            if (yydebug)
              debug("state "+state_peek(0)+", error recovery shifting to state "+yytable[yyn]+" ");
            yystate = yytable[yyn];
            state_push(yystate);
            val_push(yylval);
            doaction=false;
            break;
            }
          else
            {
            if (yydebug)
              debug("error recovery discarding state "+state_peek(0)+" ");
            if (stateptr<0)   //check for under & overflow here
              {
              yyerror("Stack underflow. aborting...");  //capital 'S'
              return 1;
              }
            state_pop();
            val_pop();
            }
          }
        }
      else            //discard this token
        {
        if (yychar == 0)
          return 1; //yyabort
        if (yydebug)
          {
          yys = null;
          if (yychar <= YYMAXTOKEN) yys = yyname[yychar];
          if (yys == null) yys = "illegal-symbol";
          debug("state "+yystate+", error recovery discards token "+yychar+" ("+yys+")");
          }
        yychar = -1;  //read another
        }
      }//end error recovery
    }//yyn=0 loop
    if (!doaction)   //any reason not to proceed?
      continue;      //skip action
    yym = yylen[yyn];          //get count of terminals on rhs
    if (yydebug)
      debug("state "+yystate+", reducing "+yym+" by rule "+yyn+" ("+yyrule[yyn]+")");
    if (yym>0)                 //if count of rhs not 'nil'
      yyval = val_peek(yym-1); //get current semantic value
    yyval = dup_yyval(yyval); //duplicate yyval if ParserVal is used as semantic value
    switch(yyn)
      {
//########## USER-SUPPLIED ACTIONS ##########
case 1:
//#line 15 "gramatica.y"
{erroresSintacticos.add("A");}
break;
case 3:
//#line 17 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(3).ival + ": falta el nombre del programa");}
break;
case 4:
//#line 18 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta la llave de cierre del cuerpo del programa");}
break;
case 5:
//#line 19 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(3).ival + ": falta la llave de apertura del cuerpo del programa");}
break;
case 6:
//#line 20 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": faltan ambas llaves en cuerpo del programa");}
break;
case 7:
//#line 21 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el nombre del programa y la llave de cierre del cuerpo del programa");}
break;
case 8:
//#line 22 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(2).ival + ": falta el nombre del programa y la llave de apertura del cuerpo del programa");}
break;
case 9:
//#line 23 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el nombre del programa y ambas llaves en cuerpo del programa");}
break;
case 10:
//#line 24 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico: el programa no es válido");}
break;
case 12:
//#line 30 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": se intentó invocar una función Lambda fuera de una función");}
break;
case 13:
//#line 31 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": se intentó invocar una función Lambda fuera de una función");}
break;
case 18:
//#line 40 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta ; al final de la sentencia ejecutable");}
break;
case 19:
//#line 41 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta ; al final de la declaración");}
break;
case 22:
//#line 46 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta ; al final de la sentencia ejecutable");}
break;
case 23:
//#line 47 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta ; al final de la sentencia ejecutable");}
break;
case 27:
//#line 53 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un print");}
break;
case 31:
//#line 61 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó una declaración de variables");}
break;
case 32:
//#line 62 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó una declaración de función");}
break;
case 33:
//#line 63 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó una declaración de función");}
break;
case 34:
//#line 64 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(1).ival + ": Se detectó una declaración de función");}
break;
case 35:
//#line 67 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó una asignación simple");}
break;
case 36:
//#line 68 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó una asignación múltiple");}
break;
case 40:
//#line 74 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(3).ival + ": se intentó invocar una función Lambda fuera de una función");}
break;
case 41:
//#line 75 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(2).ival + ": se intentó invocar una función Lambda fuera de una función");}
break;
case 42:
//#line 78 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un bloque IF-ELSE");}
break;
case 43:
//#line 79 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un bloque IF");}
break;
case 44:
//#line 80 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un bloque DO UNTIL");}
break;
case 45:
//#line 82 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta la palabra ENDIF al final del bloque IF-ELSE");}
break;
case 46:
//#line 83 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta la palabra ENDIF al final del bloque IF");}
break;
case 47:
//#line 84 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el cuerpo en el bloque ELSE");}
break;
case 48:
//#line 85 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(3).ival + ": falta el cuerpo en el bloque IF");}
break;
case 49:
//#line 86 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el cuerpo en el bloque IF");}
break;
case 50:
//#line 87 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el cuerpo en los bloques IF y ELSE");}
break;
case 51:
//#line 89 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(2).ival + ": falta de cuerpo en iteracion");}
break;
case 52:
//#line 90 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta la palabra UNTIL en la iteracion");}
break;
case 55:
//#line 95 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta de argumento en sentencia print");}
break;
case 57:
//#line 99 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta el paréntesis de apertura de la condición");}
break;
case 58:
//#line 100 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta el paréntesis de cierre de la condición");}
break;
case 59:
//#line 101 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": faltan ambos paréntesis de la condición");}
break;
case 61:
//#line 105 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": La condición es incompleta");}
break;
case 74:
//#line 129 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó una función invocada correctamente");}
break;
case 76:
//#line 133 "gramatica.y"
{ constantePositiva(val_peek(1)); }
break;
case 77:
//#line 134 "gramatica.y"
{ constanteNegativa(val_peek(1)); }
break;
case 83:
//#line 146 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(2).ival + ": falta el paréntesis de cierre de la función TRUNC");}
break;
case 84:
//#line 147 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(4).ival + ": falta el paréntesis de apertura de la función TRUNC");}
break;
case 85:
//#line 148 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(2).ival + ": faltan ambos paréntesis de la función TRUNC");}
break;
case 87:
//#line 150 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(2).ival + ": falta llave de cierre del cuerpo de la función Lambda");}
break;
case 88:
//#line 151 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(4).ival + ": falta llave de apertura del cuerpo de la función Lambda");}
break;
case 89:
//#line 152 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(3).ival + ": faltan ambas llaves del cuerpo de la función Lambda");}
break;
case 90:
//#line 153 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": No se especifica el parametro formal al que corresponde el parámetro real");}
break;
case 91:
//#line 154 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": No se especifica el parametro formal al que corresponde el parámetro real");}
break;
case 92:
//#line 155 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": No se especifica el parametro formal al que corresponde el parámetro real");}
break;
case 97:
//#line 164 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el nombre del parámetro");}
break;
case 98:
//#line 165 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta el nombre del parámetro");}
break;
case 99:
//#line 166 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el tipo del parámetro");}
break;
case 100:
//#line 167 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta el tipo del parámetro");}
break;
case 103:
//#line 172 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la izquierda de '+'"); }
break;
case 104:
//#line 173 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la derecha de '+'"); }
break;
case 105:
//#line 174 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la izquierda de '-'"); }
break;
case 106:
//#line 175 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la derecha de '-'"); }
break;
case 110:
//#line 181 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la izquierda de '*'"); }
break;
case 111:
//#line 182 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la derecha de '*'"); }
break;
case 112:
//#line 183 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la izquierda de '/'"); }
break;
case 113:
//#line 184 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la derecha de '/'"); }
break;
case 114:
//#line 185 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(2).ival + ": falta operador entre operandos o falta de comparador en condición"); }
break;
case 116:
//#line 189 "gramatica.y"
{ constantePositiva(val_peek(0)); }
break;
case 117:
//#line 190 "gramatica.y"
{ constanteNegativa(val_peek(0)); }
break;
case 120:
//#line 195 "gramatica.y"
{ constantePositiva(val_peek(0)); }
break;
case 121:
//#line 196 "gramatica.y"
{ constanteNegativa(val_peek(0)); }
break;
case 122:
//#line 197 "gramatica.y"
{ constantePositiva(val_peek(0)); }
break;
case 123:
//#line 198 "gramatica.y"
{ constanteNegativa(val_peek(0)); }
break;
case 124:
//#line 199 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta ',' entre constantes"); constantePositiva(val_peek(0)); }
break;
case 125:
//#line 200 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(2).ival + ": falta ',' entre constantes"); constanteNegativa(val_peek(0)); }
break;
//#line 1116 "Parser.java"
//########## END OF USER-SUPPLIED ACTIONS ##########
    }//switch
    //#### Now let's reduce... ####
    if (yydebug) debug("reduce");
    state_drop(yym);             //we just reduced yylen states
    yystate = state_peek(0);     //get new state
    val_drop(yym);               //corresponding value drop
    yym = yylhs[yyn];            //select next TERMINAL(on lhs)
    if (yystate == 0 && yym == 0)//done? 'rest' state and at first TERMINAL
      {
      if (yydebug) debug("After reduction, shifting from state 0 to state "+YYFINAL+"");
      yystate = YYFINAL;         //explicitly say we're done
      state_push(YYFINAL);       //and save it
      val_push(yyval);           //also save the semantic value of parsing
      if (yychar < 0)            //we want another character?
        {
        yychar = lexer.yylex(this.yylval);        //get next character
        if (yychar<0) yychar=0;  //clean, if necessary
        if (yydebug)
          yylexdebug(yystate,yychar);
        }
      if (yychar == 0)          //Good exit (if lex returns 0 ;-)
         break;                 //quit the loop--all DONE
      }//if yystate
    else                        //else not done yet
      {                         //get next state and push, for next yydefred[]
      yyn = yygindex[yym];      //find out where to go
      if ((yyn != 0) && (yyn += yystate) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yystate)
        yystate = yytable[yyn]; //get new state
      else
        yystate = yydgoto[yym]; //else go to new defred
      if (yydebug) debug("after reduction, shifting from state "+state_peek(0)+" to state "+yystate+"");
      state_push(yystate);     //going again, so push state & val...
      val_push(yyval);         //for next action
      }
    }//main loop
  return 0;//yyaccept!!
}
//## end of method parse() ######################################



//## run() --- for Thread #######################################
/**
 * A default run method, used for operating this parser
 * object in the background.  It is intended for extending Thread
 * or implementing Runnable.  Turn off with -Jnorun .
 */
public void run()
{
  yyparse();
}
//## end of method run() ########################################



//## Constructors ###############################################
/**
 * Default constructor.  Turn off with -Jnoconstruct .

 */
public Parser()
{
  //nothing to do
}


/**
 * Create a parser, setting the debug to true or false.
 * @param debugMe true for debugging, false for no debug.
 */
public Parser(boolean debugMe)
{
  yydebug=debugMe;
}
//###############################################################



}
//################### END OF CLASS ##############################
