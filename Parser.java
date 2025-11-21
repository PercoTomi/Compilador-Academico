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

//#line 30 "Parser.java"




public class Parser
{

boolean yydebug;        //do I want debug output?
int yynerrs;            //number of errors so far
int yyerrflag;          //was there an error?
int yychar;             //the current working character
Lexico lexer;
Map<String, Simbolo> tablaSimbolos;
ArrayList<String> erroresSintacticos;
ArrayList<String> erroresLexicos;
ArrayList<String> erroresSemanticos;
ArrayList<String> warnings;
int enFunc = 0;
int nivel = -1;
int returnsIf = 0;
int returnsFunc = 1;
Stack<Boolean> huboRetStack = new Stack<>();
Stack<Boolean> sinRetIf = new Stack<>();
boolean enDo = false;
boolean sinRet = false;
boolean huboIfEnFunc = false;
boolean enParamInvocacion = false;
StringBuilder stringAmbitos = new StringBuilder();
ArrayList<Parametro> parametrosActuales = new ArrayList<>();
Stack<ArrayList<Parametro>> stackParametrosActuales = new Stack<>();
Stack<String> stackTipoFuncActual = new Stack<>();
ArbolSintactico arbolSintactico;
Map<String, ArbolSintactico> mapaFunciones = new HashMap<>();
String lexemaFuncionInvocadaActual = "";

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
    2,    3,    0,    0,    1,    1,    1,    1,    1,    4,
    4,    5,    5,    5,    5,    8,    8,    8,    8,    6,
    6,    6,    6,    6,    6,    7,   18,   20,   21,    7,
   17,   14,   14,    9,    9,   26,   26,   26,   28,   27,
   29,   31,   30,   10,   10,   10,   10,   10,   10,   10,
   10,   10,   10,   10,   12,   12,   12,   32,   32,   32,
   32,   33,   33,   34,   34,   34,   34,   34,   34,   15,
   15,   16,   16,   24,   24,   23,   23,   19,   19,   35,
   35,   35,   35,   35,   35,   11,   13,   13,   13,   13,
   13,   38,   36,   37,   39,   39,   40,   40,   40,   40,
   40,   40,   40,   22,   22,   22,   22,   22,   22,   22,
   41,   41,   41,   41,   41,   41,   41,   41,   42,   42,
   42,   42,   42,   42,   25,   25,   25,   25,   25,   25,
};
final static short yylen[] = {                            2,
    0,    0,    5,    2,    3,    2,    2,    2,    1,    2,
    1,    2,    2,    1,    1,    3,    2,    2,    1,    1,
    1,    1,    1,    1,    1,    2,    0,    0,    0,   11,
    1,    4,    3,    3,    3,    3,    2,    2,    0,    2,
    1,    0,    2,    4,    6,    4,    5,    3,    5,    5,
    3,    4,    3,    3,    4,    4,    3,    3,    2,    2,
    1,    3,    1,    1,    1,    1,    1,    1,    1,    1,
    1,    3,    1,    3,    1,    1,    3,    3,    1,    3,
    2,    2,    1,    2,    1,    3,    3,    3,    4,    6,
    7,    1,    2,    1,    3,    1,    3,    9,    8,    8,
    7,    8,    2,    3,    3,    3,    3,    3,    3,    1,
    3,    3,    3,    3,    3,    3,    3,    1,    1,    2,
    1,    1,    4,    5,    3,    4,    1,    2,    2,    3,
};
final static short yydefred[] = {                         0,
    0,    0,   70,    0,    0,    0,   71,    0,    0,    0,
    0,   11,    0,    0,   20,   21,   22,   23,   24,   25,
    0,    0,    0,   39,    0,    0,    0,   76,  119,    0,
    0,    0,  122,    0,    0,    0,    0,    0,    0,  118,
    0,    0,    0,    6,    0,    4,    8,   10,   12,   13,
    0,    0,   27,    0,   93,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,   96,
    2,    0,    0,    0,    0,    0,    0,  120,   66,   67,
   68,   69,   64,   65,    0,    0,    0,    0,   51,    0,
   59,    0,    0,    0,   33,    0,   53,    0,   57,    0,
    5,    0,    0,    0,   77,  127,    0,    0,    0,    0,
   54,    0,    0,   40,   88,    0,    0,    0,    0,   86,
   87,    0,    0,    0,    0,    0,  113,  115,    0,    0,
   58,    0,    0,    0,    0,    0,   52,    0,   39,    0,
   44,  117,  114,  111,  116,  112,   32,   56,   55,   72,
    0,  128,  129,    0,    0,   46,   38,    0,    0,   37,
    0,    0,    0,   89,   97,   95,    3,  123,    0,   50,
   43,   49,    0,   85,    0,    0,    0,   79,  125,    0,
  130,   17,   36,    0,    0,    0,    0,  124,   45,   84,
    0,   81,    0,    0,  126,   16,   90,    0,    0,    0,
   80,   78,   29,   91,    0,    0,    0,    0,    0,    0,
  101,    0,    0,   99,    0,  100,    0,   98,   30,
};
final static short yydgoto[] = {                          9,
   10,   26,  123,   11,   12,   13,   14,  159,   15,   16,
   33,   18,   19,   20,   21,   52,   53,  103,  177,  194,
  208,   34,   35,   23,  108,  114,   59,   60,   24,  138,
  139,   36,   37,   87,  178,   38,   67,   68,   69,   70,
   39,   40,
};
final static short yysindex[] = {                       369,
    0,  -34,    0,  -31, -253,   43,    0,  580,    0, -220,
  637,    0,   29,   38,    0,    0,    0,    0,    0,    0,
 -155,  -28,  -22,    0,  131,  392,   56,    0,    0,   64,
  207, -152,    0,  391,   -3, -119,   76,  165,  -21,    0,
  185,  -34,  173,    0,  658,    0,    0,    0,    0,    0,
    0,   81,    0,  207,    0, -129,  -42, -125,  124,  488,
   93,  100, -196, -122,  -38,   -3,  105,  106,  104,    0,
    0,  211,  211,  109,  109,   -5,  108,    0,    0,    0,
    0,    0,    0,    0,  215,  218,  207, -111,    0, -117,
    0,  109,  238,  241,    0,   34,    0,  112,    0,   50,
    0, -100,  119,   39,    0,    0,  -98,  -43,  115,  -34,
    0,  761,  103,    0,    0,   17,  -94,  125,  -92,    0,
    0,  165, -110,   11,  -21,  -21,    0,    0,  127,  -88,
    0,   11,  -21,   11,  -21,   39,    0,  -89,    0,  -87,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
 -221,    0,    0,   20,  -81,    0,    0,  120,  782,    0,
  137,  -78,  140,    0,    0,    0,    0,    0,  144,    0,
    0,    0,  -75,    0, -198,  -68,  146,    0,    0,  -66,
    0,    0,    0,  134,  159,  161,  601,    0,    0,    0,
  -53,    0, -221,  166,    0,    0,    0,  167,  318,  300,
    0,    0,    0,    0,  413,  -51,  -63,   88,  -44,  -32,
    0,  -25,  780,    0,    5,    0,  675,    0,    0,
};
final static short yyrindex[] = {                         0,
  151,    0,    0,    0,  643,    0,    0,    0,    0,    0,
  -20,    0,  608,  622,    0,    0,    0,    0,    0,    0,
    0,  -13,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,  260,  -18,  701,  441,    0,    4,    0,
    0,    0,    0,    0,    2,    0,    0,    0,    0,    0,
  -40,  544,    0,    0,    0,    0,    0,    0,    0,    0,
  156,    0,    0,    0,    0,  141,    0,    0,  244,    0,
    0,    0,    0,    0,    0,    0,  459,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  703,    0,  420,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,  482,    0,    0,    0,  508,   -9,    0,
    0,    0,    0,    0,    0,    0,    0,  307,   74,    0,
    0,    0,    0,    0,   27,   49,    0,    0,    0,    0,
    0,   71,   96,  333,  355,  282,    0,    0,    0,  703,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  681,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,  522,    0,    0,   78,  256,    0,    0,    0,
    0,    0,    0,  720,  390,    0,    0,    0,    0,    0,
   80,    0,    0,    0,    0,    0,    0,  407,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,   82,    0,    0,    0,    0,
};
final static short yygindex[] = {                         0,
  272,    0,    0,   12,   65,  873,    0, -171,    0,    0,
  807,    0,    0,    0,  -55,    0,    0,    0,    0,    0,
    0,   13,  797,    0,    0,    0,  -26,    0,    0,  162,
    0,  -29,  274,    0,  114,  828,    0,    0,    0,  186,
  -12,  -60,
};
final static int YYTABLESIZE=1078;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         31,
  154,  155,  107,   73,   86,   31,   85,  117,   41,   90,
   32,   55,   97,  127,  128,  200,   42,   56,   73,   45,
   93,   58,  121,  121,  121,   94,  121,  205,  121,  111,
   75,  142,  144,  146,   74,  174,   55,   65,   57,  130,
  121,  121,   56,  121,  110,    3,  110,   75,  110,  175,
   65,   74,   74,   96,    7,  100,   46,   75,  190,  125,
  126,  162,  110,  110,  180,  110,  104,  108,    3,  108,
    3,  108,  133,  135,  147,   48,   86,    7,   85,    7,
  156,   86,   43,   85,   73,  108,  108,   49,  108,  106,
  149,  106,   86,  106,   85,  176,   50,   74,   73,  136,
   72,   51,   75,   76,  121,   78,  121,  106,  106,   48,
  106,  109,  171,  109,  103,  109,   91,  103,   83,  191,
   82,   83,  102,   82,  102,  102,  110,  105,  110,  109,
  109,   28,  109,  115,   65,  118,  105,  176,  105,  116,
  105,   88,   89,  140,  141,  120,  121,  122,  131,  108,
  137,  108,  148,   32,  105,  105,  150,  105,  151,  152,
   56,  160,  163,   31,  165,  164,  167,  168,   32,  169,
   63,  106,  170,  106,  172,   64,  181,  185,  182,  186,
  187,   92,  121,  121,  188,  121,  189,  121,  192,  193,
   76,  195,  196,  109,   76,  109,   76,  119,  119,  197,
  119,  198,  119,  201,   63,  211,  203,  204,  212,   32,
  213,   76,  214,   99,  153,  106,   73,   32,  105,   73,
  105,   27,   28,   29,  217,   95,   73,   73,   73,   32,
   54,  216,   73,  119,   92,   73,   73,  121,  121,  215,
   30,  121,  121,  121,  121,  121,  121,  121,  121,  121,
  121,   32,  129,  121,  121,   32,    9,  121,  121,   32,
  110,  218,   32,  110,  110,  110,  110,  110,  110,  110,
  110,  110,  110,    1,  161,  110,  110,  179,    7,  110,
  110,   48,   32,  108,   94,   32,  108,  108,  108,  108,
  108,  108,  108,  108,  108,  108,   28,   71,  108,  108,
   63,  173,  108,  108,   77,  106,  202,  166,  106,  106,
  106,  106,  106,  106,  106,  106,  106,  106,   63,    0,
  106,  106,   62,    0,  106,  106,    0,  109,    0,    0,
  109,  109,  109,  109,  109,  109,  109,  109,  109,  109,
   62,    0,  109,  109,    0,    0,  109,  109,  120,  120,
    0,  120,  105,  120,    0,  105,  105,  105,  105,  105,
  105,  105,  105,  105,  105,   28,   29,  105,  105,    0,
    0,  105,  105,  107,    0,  107,    0,  107,    0,   27,
   28,   29,   63,   30,   63,    0,   27,   28,   61,    0,
    0,  107,  107,  110,  107,  104,  121,  104,   30,  104,
    0,    0,    0,    0,   62,   62,   62,    1,    0,   76,
    1,  119,  121,  104,  104,    0,  104,    1,    1,    1,
   27,   28,   29,    1,  207,    0,    1,  119,   27,   28,
   29,  123,  123,   86,  123,   85,  123,    0,    0,   30,
   27,   28,   29,    0,    0,    0,   98,   30,  124,  124,
   84,  124,   83,  124,    0,  107,    0,  107,    0,   30,
    0,    0,   27,   28,   29,    0,  124,   28,   29,    0,
  132,   28,   29,  134,   28,   29,    0,  104,   48,  104,
    0,   30,    0,    0,    0,   30,    0,    0,    0,   30,
    0,    8,   30,  143,   28,   29,  145,   28,   29,   61,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,   30,    0,    8,   30,   63,   60,    0,   63,
   63,   63,    0,    0,    0,    0,   63,   63,   63,    0,
    0,   63,   63,    0,    0,   63,   63,  210,   62,    0,
   34,   62,   62,   62,   48,    0,    0,    0,   62,   62,
   62,    0,    0,   62,   62,    0,   28,   62,   62,    2,
    0,    0,  120,   61,    0,   61,   35,    4,    5,    0,
    0,  206,    6,    0,   28,    0,    0,    2,  120,    0,
   47,   60,    0,   60,    0,    4,    5,    0,    0,  107,
    6,    0,  107,  107,  107,  107,  107,  107,  107,  107,
  107,  107,   26,    0,  107,  107,   34,    0,  107,  107,
  112,  104,    0,    0,  104,  104,  104,  104,  104,  104,
  104,  104,  104,  104,    0,    1,  104,  104,    2,    0,
  104,  104,   35,    0,    0,    3,    4,    5,    0,    0,
    0,    6,    0,    0,    7,  123,   47,    0,   28,    0,
    0,    2,    0,   79,   80,   81,   82,    0,    3,    4,
    5,  123,  124,    0,    6,    0,    0,    7,   26,   28,
    0,    0,    2,    0,    0,    0,   48,    0,  124,   48,
    4,    5,    0,    0,  209,    6,   48,   48,   48,    0,
    0,   48,   48,    0,    0,   48,   48,   61,    0,    0,
   61,   61,   61,    0,   44,    0,    0,   61,   61,   61,
    0,    0,   61,   61,    0,   60,   61,   61,   60,   60,
   60,    0,    0,  199,    0,   60,   60,   60,    0,    0,
   60,   60,   14,    0,   60,   60,    0,    0,   34,    0,
    0,   34,    0,    0,   28,    0,   15,    2,   34,   34,
   34,    0,    0,   34,   34,    4,    5,   34,   34,    0,
    6,   47,    0,    0,   35,   41,    0,   35,    0,    0,
    0,    0,    0,    0,   35,   35,   35,    0,   47,   35,
   35,   47,  101,   35,   35,    0,    0,    0,   47,   47,
   47,    0,    0,   47,   47,    0,   22,   47,   47,  219,
   26,    0,    0,   26,   22,   19,   17,   22,    0,    0,
   26,   26,   26,    0,   17,    0,   26,   17,    0,   26,
   26,   66,   22,   39,    0,   42,    0,   25,    0,    0,
    0,    0,   17,    0,    0,   25,   28,    0,   25,    2,
    0,   22,    0,    0,   18,    0,    3,    4,    5,    0,
    0,   17,    6,   25,  109,    7,   22,   28,    0,    0,
    2,    0,    0,    0,   14,    0,   17,   14,    4,    5,
    0,    0,   25,    6,   14,   14,   14,    0,   15,    0,
   14,   15,    0,   14,   14,  157,    0,   25,   15,   15,
   15,    0,    0,   28,   15,    0,    2,   15,   15,   41,
    0,    0,   41,    3,    4,    5,  183,    0,   22,    6,
   41,   41,    7,    0,   28,   41,    0,    2,   17,    0,
    0,    0,    0,    0,    3,    4,    5,    0,    0,    0,
    6,   28,  113,    7,    2,    0,    0,   19,    0,   25,
   19,    3,    4,    5,    0,    0,    0,    6,   19,   19,
    7,    0,   19,   19,    0,   22,    0,   39,    0,   42,
   39,    0,   42,    0,    0,   17,    0,    0,   39,   39,
   42,   42,    0,   39,    0,   42,   18,    0,    0,   18,
    0,    0,    0,   22,  158,    0,   25,   18,   18,    0,
    0,   18,   18,   17,    0,   22,   22,    0,    0,    0,
    0,   22,    0,    0,    0,   17,   17,    0,    0,   22,
    0,   17,    0,   22,   25,    0,    0,   28,    0,   17,
    2,    0,    0,   17,    0,    0,   25,   25,    4,    5,
    0,  184,   25,    6,    0,    0,   28,    0,   28,    2,
   25,    2,    0,    0,   25,    0,    3,    4,    5,    4,
    5,    0,    6,    0,    6,    7,    0,    0,    0,  158,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  158,  184,    0,    0,    0,    0,  184,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         40,
   44,   45,   45,   44,   43,   40,   45,   63,   40,   36,
   45,   40,   42,   74,   75,  187,  270,   46,   59,    8,
   42,   44,   41,   42,   43,   47,   45,  199,   47,   59,
   44,   92,   93,   94,   44,  257,   40,   25,   61,   45,
   59,   60,   46,   62,   41,  267,   43,   61,   45,  271,
   38,   61,   42,   41,  276,   43,  277,   47,  257,   72,
   73,   45,   59,   60,   45,   62,   54,   41,  267,   43,
  267,   45,   85,   86,   41,   11,   43,  276,   45,  276,
  110,   43,   40,   45,  125,   59,   60,   59,   62,   41,
   41,   43,   43,   45,   45,  151,   59,   42,   43,   87,
   45,  257,   47,   40,  123,  258,  125,   59,   60,   45,
   62,   41,  139,   43,   41,   45,   41,   44,   41,  175,
   41,   44,   41,   44,   44,   44,  123,  257,  125,   59,
   60,  257,   62,   41,  122,  258,   41,  193,   43,   40,
   45,  261,  262,  261,  262,   41,   41,   44,   41,  123,
  262,  125,   41,   45,   59,   60,  257,   62,   40,  258,
   46,   59,  257,   40,  257,   41,  277,   41,   45,  258,
   40,  123,  262,  125,  262,   45,  258,   41,   59,  258,
   41,   41,   42,   43,   41,   45,  262,   47,  257,   44,
   40,  258,   59,  123,   44,  125,   46,   42,   43,   41,
   45,   41,   47,  257,   40,  257,   41,   41,  272,   45,
  123,   61,  257,   41,  258,  258,  257,   45,  123,  260,
  125,  256,  257,  258,  213,   41,  267,  268,  269,   45,
  259,  257,  273,  272,  256,  276,  277,  256,  257,  272,
  275,  260,  261,  262,  263,  264,  265,  266,  267,  268,
  269,   45,  258,  272,  273,   45,  277,  276,  277,   45,
  257,  257,   45,  260,  261,  262,  263,  264,  265,  266,
  267,  268,  269,  123,  258,  272,  273,  258,  277,  276,
  277,  217,   45,  257,   41,   45,  260,  261,  262,  263,
  264,  265,  266,  267,  268,  269,   41,   26,  272,  273,
   41,  140,  276,  277,   31,  257,  193,  122,  260,  261,
  262,  263,  264,  265,  266,  267,  268,  269,   59,   -1,
  272,  273,   41,   -1,  276,  277,   -1,  257,   -1,   -1,
  260,  261,  262,  263,  264,  265,  266,  267,  268,  269,
   59,   -1,  272,  273,   -1,   -1,  276,  277,   42,   43,
   -1,   45,  257,   47,   -1,  260,  261,  262,  263,  264,
  265,  266,  267,  268,  269,  257,  258,  272,  273,   -1,
   -1,  276,  277,   41,   -1,   43,   -1,   45,   -1,  256,
  257,  258,  123,  275,  125,   -1,  256,  257,  258,   -1,
   -1,   59,   60,  270,   62,   41,  256,   43,  275,   45,
   -1,   -1,   -1,   -1,  123,  275,  125,  257,   -1,  259,
  260,  256,  272,   59,   60,   -1,   62,  267,  268,  269,
  256,  257,  258,  273,  125,   -1,  276,  272,  256,  257,
  258,   42,   43,   43,   45,   45,   47,   -1,   -1,  275,
  256,  257,  258,   -1,   -1,   -1,  274,  275,   42,   43,
   60,   45,   62,   47,   -1,  123,   -1,  125,   -1,  275,
   -1,   -1,  256,  257,  258,   -1,  256,  257,  258,   -1,
  256,  257,  258,  256,  257,  258,   -1,  123,   59,  125,
   -1,  275,   -1,   -1,   -1,  275,   -1,   -1,   -1,  275,
   -1,  123,  275,  256,  257,  258,  256,  257,  258,   59,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,  275,   -1,  123,  275,  257,   59,   -1,  260,
  261,  262,   -1,   -1,   -1,   -1,  267,  268,  269,   -1,
   -1,  272,  273,   -1,   -1,  276,  277,  125,  257,   -1,
   59,  260,  261,  262,  125,   -1,   -1,   -1,  267,  268,
  269,   -1,   -1,  272,  273,   -1,  257,  276,  277,  260,
   -1,   -1,  256,  123,   -1,  125,   59,  268,  269,   -1,
   -1,  272,  273,   -1,  257,   -1,   -1,  260,  272,   -1,
   59,  123,   -1,  125,   -1,  268,  269,   -1,   -1,  257,
  273,   -1,  260,  261,  262,  263,  264,  265,  266,  267,
  268,  269,   59,   -1,  272,  273,  125,   -1,  276,  277,
  123,  257,   -1,   -1,  260,  261,  262,  263,  264,  265,
  266,  267,  268,  269,   -1,  257,  272,  273,  260,   -1,
  276,  277,  125,   -1,   -1,  267,  268,  269,   -1,   -1,
   -1,  273,   -1,   -1,  276,  256,  125,   -1,  257,   -1,
   -1,  260,   -1,  263,  264,  265,  266,   -1,  267,  268,
  269,  272,  256,   -1,  273,   -1,   -1,  276,  125,  257,
   -1,   -1,  260,   -1,   -1,   -1,  257,   -1,  272,  260,
  268,  269,   -1,   -1,  272,  273,  267,  268,  269,   -1,
   -1,  272,  273,   -1,   -1,  276,  277,  257,   -1,   -1,
  260,  261,  262,   -1,  125,   -1,   -1,  267,  268,  269,
   -1,   -1,  272,  273,   -1,  257,  276,  277,  260,  261,
  262,   -1,   -1,  123,   -1,  267,  268,  269,   -1,   -1,
  272,  273,  125,   -1,  276,  277,   -1,   -1,  257,   -1,
   -1,  260,   -1,   -1,  257,   -1,  125,  260,  267,  268,
  269,   -1,   -1,  272,  273,  268,  269,  276,  277,   -1,
  273,  125,   -1,   -1,  257,  123,   -1,  260,   -1,   -1,
   -1,   -1,   -1,   -1,  267,  268,  269,   -1,  257,  272,
  273,  260,  125,  276,  277,   -1,   -1,   -1,  267,  268,
  269,   -1,   -1,  272,  273,   -1,    0,  276,  277,  125,
  257,   -1,   -1,  260,    8,  125,    0,   11,   -1,   -1,
  267,  268,  269,   -1,    8,   -1,  273,   11,   -1,  276,
  277,   25,   26,  123,   -1,  123,   -1,    0,   -1,   -1,
   -1,   -1,   26,   -1,   -1,    8,  257,   -1,   11,  260,
   -1,   45,   -1,   -1,  125,   -1,  267,  268,  269,   -1,
   -1,   45,  273,   26,   58,  276,   60,  257,   -1,   -1,
  260,   -1,   -1,   -1,  257,   -1,   60,  260,  268,  269,
   -1,   -1,   45,  273,  267,  268,  269,   -1,  257,   -1,
  273,  260,   -1,  276,  277,  125,   -1,   60,  267,  268,
  269,   -1,   -1,  257,  273,   -1,  260,  276,  277,  257,
   -1,   -1,  260,  267,  268,  269,  125,   -1,  112,  273,
  268,  269,  276,   -1,  257,  273,   -1,  260,  112,   -1,
   -1,   -1,   -1,   -1,  267,  268,  269,   -1,   -1,   -1,
  273,  257,   60,  276,  260,   -1,   -1,  257,   -1,  112,
  260,  267,  268,  269,   -1,   -1,   -1,  273,  268,  269,
  276,   -1,  272,  273,   -1,  159,   -1,  257,   -1,  257,
  260,   -1,  260,   -1,   -1,  159,   -1,   -1,  268,  269,
  268,  269,   -1,  273,   -1,  273,  257,   -1,   -1,  260,
   -1,   -1,   -1,  187,  112,   -1,  159,  268,  269,   -1,
   -1,  272,  273,  187,   -1,  199,  200,   -1,   -1,   -1,
   -1,  205,   -1,   -1,   -1,  199,  200,   -1,   -1,  213,
   -1,  205,   -1,  217,  187,   -1,   -1,  257,   -1,  213,
  260,   -1,   -1,  217,   -1,   -1,  199,  200,  268,  269,
   -1,  159,  205,  273,   -1,   -1,  257,   -1,  257,  260,
  213,  260,   -1,   -1,  217,   -1,  267,  268,  269,  268,
  269,   -1,  273,   -1,  273,  276,   -1,   -1,   -1,  187,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,  199,  200,   -1,   -1,   -1,   -1,  205,
};
}
final static short YYFINAL=9;
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
"$$1 :",
"$$2 :",
"prog : ID $$1 bloque_sentencias $$2 EOF",
"prog : bloque_sentencias EOF",
"bloque_sentencias : '{' listasentencias '}'",
"bloque_sentencias : '{' '}'",
"bloque_sentencias : '{' listasentencias",
"bloque_sentencias : listasentencias '}'",
"bloque_sentencias : listasentencias",
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
"sentenciaejecutable : funcion_lambda",
"sentenciaejecutable : sentencia_return",
"declaracion : tipo listaIDs",
"$$3 :",
"$$4 :",
"$$5 :",
"declaracion : tipo id_funcion $$3 '(' listaparametros $$4 ')' $$5 '{' listasentencias '}'",
"id_funcion : ID",
"sentencia_return : RETURN '(' expresion ')'",
"sentencia_return : RETURN '(' ')'",
"asignacion : identificador ASIG expresion",
"asignacion : listavariables '=' listaconstantes",
"bloque_control_estructura : '{' listasentenciasejecutables '}'",
"bloque_control_estructura : sentenciaejecutable ';'",
"bloque_control_estructura : '{' '}'",
"$$6 :",
"bloque_control : $$6 bloque_control_estructura",
"inicio_do : DO",
"$$7 :",
"bloque_control_else : $$7 bloque_control",
"control : IF condicion bloque_control ENDIF",
"control : IF condicion bloque_control ELSE bloque_control_else ENDIF",
"control : inicio_do bloque_control UNTIL condicion",
"control : IF condicion bloque_control ELSE bloque_control_else",
"control : IF condicion bloque_control",
"control : IF condicion bloque_control ELSE ENDIF",
"control : IF condicion ELSE bloque_control_else ENDIF",
"control : IF condicion ENDIF",
"control : IF condicion ELSE ENDIF",
"control : DO UNTIL condicion",
"control : inicio_do bloque_control condicion",
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
"listaIDs : listaIDs ',' ID",
"listaIDs : ID",
"listavariables : listavariables ',' identificador",
"listavariables : identificador",
"identificador : ID",
"identificador : identificador '.' ID",
"listaparametros : listaparametros ',' parametro_formal",
"listaparametros : parametro_formal",
"parametro_formal : CV tipo ID",
"parametro_formal : tipo ID",
"parametro_formal : CV tipo",
"parametro_formal : tipo",
"parametro_formal : CV ID",
"parametro_formal : ID",
"funcion : cabecera_funcion argumentos_funcion ')'",
"funcion_lambda : cabecera_funcion argumento_id_lambda ')'",
"funcion_lambda : cabecera_funcion CTE ')'",
"funcion_lambda : cabecera_funcion '-' CTE ')'",
"funcion_lambda : cabecera_funcion TRUNC '(' CTE ')' ')'",
"funcion_lambda : cabecera_funcion TRUNC '(' '-' CTE ')' ')'",
"argumento_id_lambda : identificador",
"cabecera_funcion : identificador '('",
"argumentos_funcion : listapinvocacion",
"listapinvocacion : listapinvocacion ',' pinvocacion",
"listapinvocacion : pinvocacion",
"pinvocacion : expresion FLECHA ID",
"pinvocacion : '(' tipo ID ')' '{' listasentenciasejecutables '}' FLECHA ID",
"pinvocacion : '(' tipo ID ')' '{' listasentenciasejecutables FLECHA ID",
"pinvocacion : '(' tipo ID ')' listasentenciasejecutables '}' FLECHA ID",
"pinvocacion : '(' tipo ID ')' listasentenciasejecutables FLECHA ID",
"pinvocacion : '(' tipo ID ')' '{' listasentenciasejecutables '}' FLECHA",
"pinvocacion : expresion FLECHA",
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
"factor : TRUNC '(' CTE ')'",
"factor : TRUNC '(' '-' CTE ')'",
"listaconstantes : listaconstantes ',' CTE",
"listaconstantes : listaconstantes ',' '-' CTE",
"listaconstantes : CTE",
"listaconstantes : '-' CTE",
"listaconstantes : listaconstantes CTE",
"listaconstantes : listaconstantes '-' CTE",
};

//#line 332 "gramatica.y"

public Parser(Map<String, Simbolo> tablaSimbolos, String path, ArrayList<String> erroresLexicos, ArrayList<String> erroresSintacticos, ArrayList<String> erroresSemanticos, ArrayList<String> warnings, ArbolSintactico arbolSintactico){
  this.tablaSimbolos = tablaSimbolos;
  this.erroresLexicos = erroresLexicos;
  this.erroresSintacticos = erroresSintacticos;
  this.erroresSemanticos = erroresSemanticos;
  this.warnings = warnings;
  this.lexer = new Lexico(tablaSimbolos,path, erroresLexicos, warnings);
  this.arbolSintactico = arbolSintactico;
}

private void yyerror(String s) {
    erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error: " + s);
}

private boolean error() {
  return (!erroresLexicos.isEmpty() || !erroresSintacticos.isEmpty() || !erroresSemanticos.isEmpty());
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

private void finFunc(ParserVal yyval, Simbolo simboloID) {
  if (returnsFunc > 0 && (!huboIfEnFunc || sinRet)) { 
    erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + yyval.ival + ": La función no tiene sentencia return en todos sus caminos de ejecución");
  } else if (returnsFunc < 0) {
    erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + yyval.ival + ": Se han detectado multiples sentencias de retorno en la función");
  }
  enFunc--;
  returnsFunc = 1;
  huboIfEnFunc = false;

  // Creación del símbolo de función
  tablaSimbolos.remove(simboloID.getLexema());
  Simbolo nuevoSimbolo = new SimboloFuncion(stackTipoFuncActual.pop(), "Funcion", simboloID.getLexema(), stackParametrosActuales.pop());
  tablaSimbolos.put(simboloID.getLexema(), nuevoSimbolo);
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
  tablaSimbolos.put(lexemaNuevo, ((Simbolo)yyval.obj));
  tablaSimbolos.remove(lexemaViejo);
}

private void chequeo_anid_lambda(String nombreLambda, ParserVal yyval) {
  if (nombreLambda.contains(".")) {
    erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + yyval.ival + ": Se ha intentado invocar una función lambda perteneciente a otro ámbito");
  }
}

private Simbolo checkearAlcance(String lexema, int nroLinea) {
  String auxSA = stringAmbitos.toString();
  if (!lexema.contains(".")) {
    String auxLexema = lexema + auxSA;

    while (!tablaSimbolos.containsKey(auxLexema) && auxSA.length() > 0) {
      auxSA = auxSA.substring(0, auxSA.lastIndexOf(":"));
      auxLexema = lexema + auxSA;
    }

    if (auxLexema.equals(lexema)) {
      erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + nroLinea + ": La variable '" + lexema + "' no ha sido declarada en el ámbito actual y tampoco está al alcance");
      tablaSimbolos.remove(lexema);
      return null;
    } else {
      tablaSimbolos.remove(lexema);
      return tablaSimbolos.get(auxLexema);
    }
      
  } else {
    String varActual = lexema.substring(lexema.lastIndexOf(".") + 1);
    String ambitoVarActual = (":" + lexema.substring(0, lexema.lastIndexOf("."))).replace(".", ":");

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

private void checkeoParametroDeInvocacion(String lexemaParametro, int nroLinea) {
  if (!error()) {
    SimboloFuncion funcionActual = (SimboloFuncion)tablaSimbolos.get(lexemaFuncionInvocadaActual);
    if (!funcionActual.existeParametro(lexemaParametro)) {
      erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + nroLinea + ": el parámetro formal '" + lexemaParametro + "' no existe en la función '" + lexemaFuncionInvocadaActual.substring(0, lexemaFuncionInvocadaActual.indexOf(':')) + "'");    
    }
  }
  tablaSimbolos.remove(lexemaParametro);
}

private void restriccionCteFloat(ParserVal valCte) {
  if (valCte.obj instanceof SimboloCteFloat) {
    erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + valCte.ival + ": Uso incorrecto de constante Float por incompatibilidad de tipos");
    tablaSimbolos.remove(((SimboloCteFloat)valCte.obj).getLexema());
  }
}

private void truncarConstate(ParserVal valCte) {
  if (valCte.obj instanceof SimboloCteFloat) {
    double valorOriginal = ((SimboloCteFloat)valCte.obj).getValor();
    long valorTruncado = (long) valorOriginal;
    String lexemaTruncado = Long.toString(valorTruncado) + "L";
    SimboloLong simbolo;
    tablaSimbolos.remove(((SimboloCteFloat)valCte.obj).getLexema());
    if (!tablaSimbolos.containsKey(lexemaTruncado)) {
      simbolo =  new SimboloLong("Long", "Constante", lexemaTruncado, valorTruncado);
      tablaSimbolos.put(lexemaTruncado, simbolo);
    } else {
      simbolo = (SimboloLong)tablaSimbolos.get(lexemaTruncado);
    }
    valCte.obj = simbolo;
  }
}
//#line 836 "Parser.java"
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
//#line 21 "gramatica.y"
{stringAmbitos.append(":"+((Simbolo)val_peek(0).obj).getLexema());}
break;
case 2:
//#line 21 "gramatica.y"
{quitarAmbito();}
break;
case 3:
//#line 21 "gramatica.y"
{if (!error() && val_peek(2).obj != null && val_peek(2).obj instanceof ArbolSintactico) { arbolSintactico = ((ArbolSintactico)val_peek(2).obj);}}
break;
case 4:
//#line 22 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el nombre del programa");}
break;
case 5:
//#line 28 "gramatica.y"
{ yyval = val_peek(1); }
break;
case 7:
//#line 30 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta llave de cierre del bloque de sentencias");}
break;
case 8:
//#line 31 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta llave de apertura del bloque de sentencias");}
break;
case 9:
//#line 32 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": faltan ambas llaves del bloque de sentencias");}
break;
case 10:
//#line 35 "gramatica.y"
{ if (!error()) { if (val_peek(1).obj != null) { ((ArbolSintactico)val_peek(1).obj).setUltimoDerecho(new ArbolSintactico("Sentencia", ((ArbolSintactico)val_peek(0).obj) , null )); yyval = val_peek(1); }
                                                                                                                                    else if (val_peek(0).obj != null) { yyval= new ParserVal(new ArbolSintactico("Sentencia", ((ArbolSintactico)val_peek(0).obj), null)); } } }
break;
case 11:
//#line 37 "gramatica.y"
{ if (!error()) { if (val_peek(0).obj != null) { yyval = new ParserVal(new ArbolSintactico("Sentencia", ((ArbolSintactico)val_peek(0).obj), null)); }} }
break;
case 12:
//#line 40 "gramatica.y"
{ yyval = val_peek(1);}
break;
case 13:
//#line 41 "gramatica.y"
{ yyval.obj = null;}
break;
case 14:
//#line 42 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta ; al final de la sentencia ejecutable");}
break;
case 15:
//#line 43 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta ; al final de la declaración");}
break;
case 16:
//#line 46 "gramatica.y"
{ if (!error()) { ((ArbolSintactico)val_peek(2).obj).setUltimoDerecho(new ArbolSintactico("Sentencia", ((ArbolSintactico)val_peek(1).obj) , null ));
                                                                                    yyval = val_peek(2);
                                                                                  } }
break;
case 17:
//#line 49 "gramatica.y"
{ if (!error()) { yyval = new ParserVal(new ArbolSintactico("Sentencia", ((ArbolSintactico)val_peek(1).obj), null)); } }
break;
case 18:
//#line 50 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta ; al final de la sentencia ejecutable");}
break;
case 19:
//#line 51 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta ; al final de la sentencia ejecutable");}
break;
case 20:
//#line 54 "gramatica.y"
{ yyval = val_peek(0); }
break;
case 21:
//#line 55 "gramatica.y"
{ yyval = val_peek(0); }
break;
case 22:
//#line 56 "gramatica.y"
{ yyval = val_peek(0); }
break;
case 23:
//#line 57 "gramatica.y"
{ System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un print");
                                                                                yyval = val_peek(0);
                                                                              }
break;
case 24:
//#line 60 "gramatica.y"
{if(enFunc==0) {erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + val_peek(0).ival + ": Invocacion de funcion lambda fuera de una función");} else {yyval = val_peek(0);}}
break;
case 25:
//#line 61 "gramatica.y"
{chequeoSentenciaReturn(val_peek(0)); yyval = val_peek(0);}
break;
case 26:
//#line 64 "gramatica.y"
{ System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó una declaración de variables");}
break;
case 27:
//#line 65 "gramatica.y"
{stringAmbitos.append(":"+((Simbolo)val_peek(0).obj).getLexema());}
break;
case 28:
//#line 65 "gramatica.y"
{stackParametrosActuales.push(parametrosActuales); parametrosActuales = new ArrayList<>();}
break;
case 29:
//#line 65 "gramatica.y"
{enFunc++;}
break;
case 30:
//#line 65 "gramatica.y"
{  finFunc( val_peek(0), ((Simbolo)val_peek(9).obj) ); 
                                                                                                                                                                                                                                                                    quitarAmbito(); 
                                                                                                                                                                                                                                                                    renombrarVariable(val_peek(9)); 
                                                                                                                                                                                                                                                                    System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó una declaración de función");
                                                                                                                                                                                                                                                                    mapaFunciones.put(((Simbolo)val_peek(9).obj).getLexema(), (ArbolSintactico)val_peek(1).obj);
                                                                                                                                                                                                                                                                    yyval.ival = val_peek(0).ival;
                                                                                                                                                                                                                                                                  }
break;
case 31:
//#line 74 "gramatica.y"
{yyval = new ParserVal((SimboloLong)val_peek(0).obj);}
break;
case 32:
//#line 77 "gramatica.y"
{ System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(1).ival + ": Se detectó una sentencia return");
                                                                              if (!error()) { yyval = new ParserVal(new ArbolSintactico("Return", (ArbolSintactico)val_peek(1).obj,  null)); }
                                                                            }
break;
case 33:
//#line 80 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta de argumento en sentencia return");}
break;
case 34:
//#line 83 "gramatica.y"
{ System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó una asignación simple");
                                                                                            if (!error()) { yyval = new ParserVal( new ArbolSintactico("ASIG", new ArbolSintactico(checkearAlcance(val_peek(2).sval, val_peek(2).ival)), (ArbolSintactico)val_peek(0).obj) ); }
                                                                                          }
break;
case 35:
//#line 86 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó una asignación múltiple");}
break;
case 36:
//#line 89 "gramatica.y"
{ yyval = val_peek(1); }
break;
case 37:
//#line 90 "gramatica.y"
{ yyval = val_peek(1); }
break;
case 38:
//#line 91 "gramatica.y"
{ yyval.obj = null; }
break;
case 39:
//#line 94 "gramatica.y"
{inicioIf();}
break;
case 40:
//#line 94 "gramatica.y"
{finIf(val_peek(0)); yyval = val_peek(0); }
break;
case 41:
//#line 97 "gramatica.y"
{enDo=true;}
break;
case 42:
//#line 100 "gramatica.y"
{sinRetIf.push(sinRet);}
break;
case 43:
//#line 100 "gramatica.y"
{if(sinRetIf.pop()) {sinRet=true;}; yyval = val_peek(0); }
break;
case 44:
//#line 103 "gramatica.y"
{ System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un bloque IF");
                                                                                                  if (!error()) { yyval = new ParserVal(new ArbolSintactico("If", (ArbolSintactico)val_peek(2).obj, new ArbolSintactico("Cuerpo",(ArbolSintactico)val_peek(1).obj, null)) ); }
                                                                                                }
break;
case 45:
//#line 106 "gramatica.y"
{ System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un bloque IF-ELSE");
                                                                                                  if (!error()) { yyval = new ParserVal(new ArbolSintactico("If", (ArbolSintactico)val_peek(4).obj, new ArbolSintactico("Cuerpo",(ArbolSintactico)val_peek(3).obj, (ArbolSintactico)val_peek(1).obj)) ); }  
                                                                                                }
break;
case 46:
//#line 109 "gramatica.y"
{  enDo=false;
                                                                                                  System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un bloque DO UNTIL");
                                                                                                }
break;
case 47:
//#line 113 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta la palabra ENDIF al final del bloque IF-ELSE");}
break;
case 48:
//#line 114 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta la palabra ENDIF al final del bloque IF");}
break;
case 49:
//#line 115 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el cuerpo en el bloque ELSE");}
break;
case 50:
//#line 116 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(3).ival + ": falta el cuerpo en el bloque IF");}
break;
case 51:
//#line 117 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el cuerpo en el bloque IF");}
break;
case 52:
//#line 118 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el cuerpo en los bloques IF y ELSE");}
break;
case 53:
//#line 120 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(2).ival + ": falta de cuerpo en iteracion");}
break;
case 54:
//#line 121 "gramatica.y"
{ enDo=false;
                                                                                                  erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta la palabra UNTIL en la iteracion");
                                                                                                }
break;
case 55:
//#line 127 "gramatica.y"
{ if (!error()) { yyval = new ParserVal (new ArbolSintactico("Print", (ArbolSintactico)val_peek(1).obj, null)); } }
break;
case 56:
//#line 128 "gramatica.y"
{ if (!error()) { yyval = new ParserVal (new ArbolSintactico("Print", new ArbolSintactico((Simbolo)val_peek(1).obj), null)); } }
break;
case 57:
//#line 129 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta de argumento en sentencia print");}
break;
case 58:
//#line 132 "gramatica.y"
{ yyval = val_peek(1); }
break;
case 59:
//#line 133 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta el paréntesis de apertura de la condición");}
break;
case 60:
//#line 134 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta el paréntesis de cierre de la condición");}
break;
case 61:
//#line 135 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": faltan ambos paréntesis de la condición");}
break;
case 62:
//#line 138 "gramatica.y"
{ if (!error()) { yyval = new ParserVal (new ArbolSintactico(val_peek(1).sval, (ArbolSintactico)val_peek(2).obj, (ArbolSintactico)val_peek(0).obj)); } }
break;
case 63:
//#line 139 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": La condición es incompleta");}
break;
case 64:
//#line 142 "gramatica.y"
{ yyval = new ParserVal("Mayor"); }
break;
case 65:
//#line 143 "gramatica.y"
{ yyval = new ParserVal("Menor"); }
break;
case 66:
//#line 144 "gramatica.y"
{ yyval = new ParserVal("MayorIgual"); }
break;
case 67:
//#line 145 "gramatica.y"
{ yyval = new ParserVal("MenorIgual"); }
break;
case 68:
//#line 146 "gramatica.y"
{ yyval = new ParserVal("Igual"); }
break;
case 69:
//#line 147 "gramatica.y"
{ yyval = new ParserVal("Distinto"); }
break;
case 70:
//#line 150 "gramatica.y"
{stackTipoFuncActual.push("Long");}
break;
case 71:
//#line 151 "gramatica.y"
{stackTipoFuncActual.push("Lambda");}
break;
case 72:
//#line 154 "gramatica.y"
{renombrarVariable(val_peek(0));}
break;
case 73:
//#line 155 "gramatica.y"
{  renombrarVariable(val_peek(0));}
break;
case 76:
//#line 164 "gramatica.y"
{ yyval = new ParserVal( ((SimboloLong)val_peek(0).obj).getLexema() );
                                                                        yyval.ival = val_peek(0).ival; 
                                                                      }
break;
case 77:
//#line 167 "gramatica.y"
{ yyval = new ParserVal( val_peek(2).sval + "." + ((SimboloLong)val_peek(0).obj).getLexema() );
                                                                        yyval.ival = val_peek(0).ival;
                                                                      }
break;
case 80:
//#line 176 "gramatica.y"
{ renombrarVariable(val_peek(0)); 
                                                                       parametrosActuales.add(new Parametro(((Simbolo)val_peek(0).obj), "CV"));
                                                                     }
break;
case 81:
//#line 179 "gramatica.y"
{ renombrarVariable(val_peek(0)); 
                                                                       parametrosActuales.add(new Parametro(((Simbolo)val_peek(0).obj), "CVR"));
                                                                     }
break;
case 82:
//#line 182 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el nombre del parámetro");
                                                                     }
break;
case 83:
//#line 184 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta el nombre del parámetro");
                                                                     }
break;
case 84:
//#line 186 "gramatica.y"
{ renombrarVariable(val_peek(0)); 
                                                                       parametrosActuales.add(new Parametro(((Simbolo)val_peek(0).obj), "CV")); 
                                                                       erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el tipo del parámetro");
                                                                     }
break;
case 85:
//#line 190 "gramatica.y"
{ renombrarVariable(val_peek(0)); 
                                                                       parametrosActuales.add(new Parametro(((Simbolo)val_peek(0).obj), "CVR")); 
                                                                       erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta el tipo del parámetro");
                                                                     }
break;
case 86:
//#line 196 "gramatica.y"
{ enParamInvocacion = false;
                                                                                                                System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó la invocación de una función");
                                                                                                                yyval = val_peek(2); 
                                                                                                              }
break;
case 87:
//#line 202 "gramatica.y"
{ enParamInvocacion = false;
                                                                                                                System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó la invocación de una función lambda");
                                                                                                                chequeo_anid_lambda(val_peek(2).sval, val_peek(0));
                                                                                                                yyval = val_peek(2);
                                                                                                              }
break;
case 88:
//#line 207 "gramatica.y"
{ enParamInvocacion = false;
                                                                                                                restriccionCteFloat(val_peek(1));
                                                                                                                chequeo_anid_lambda(val_peek(2).sval, val_peek(0)); 
                                                                                                                constantePositiva(val_peek(1)); 
                                                                                                                System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó la invocación de una función lambda");
                                                                                                              }
break;
case 89:
//#line 213 "gramatica.y"
{ enParamInvocacion = false;
                                                                                                                restriccionCteFloat(val_peek(1));
                                                                                                                chequeo_anid_lambda(val_peek(3).sval, val_peek(0)); 
                                                                                                                constanteNegativa(val_peek(1)); 
                                                                                                                System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó la invocación de una función lambda");
                                                                                                              }
break;
case 90:
//#line 219 "gramatica.y"
{ enParamInvocacion = false;
                                                                                                                truncarConstate(val_peek(2));
                                                                                                                constantePositiva(val_peek(2)); 
                                                                                                                System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó la invocación de una función lambda");
                                                                                                              }
break;
case 91:
//#line 224 "gramatica.y"
{ enParamInvocacion = false;
                                                                                                                truncarConstate(val_peek(2));
                                                                                                                constanteNegativa(val_peek(2)); 
                                                                                                                System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó la invocación de una función lambda");
                                                                                                              }
break;
case 92:
//#line 231 "gramatica.y"
{yyval = new ParserVal(checkearAlcance(val_peek(0).sval, val_peek(0).ival).getLexema() );}
break;
case 93:
//#line 234 "gramatica.y"
{ enParamInvocacion = true; 
                                                                                                  val_peek(1).obj = checkearAlcance(val_peek(1).sval, val_peek(0).ival);
                                                                                                  if (val_peek(1).obj != null) {
                                                                                                    if (!error()) { yyval = new ParserVal(new ArbolSintactico("Call", new ArbolSintactico((Simbolo)val_peek(1).obj), null)); }
                                                                                                    lexemaFuncionInvocadaActual = ((Simbolo)val_peek(1).obj).getLexema();
                                                                                                  }
                                                                                                }
break;
case 97:
//#line 250 "gramatica.y"
{ checkeoParametroDeInvocacion(((Simbolo)val_peek(0).obj).getLexema(), val_peek(0).ival); }
break;
case 98:
//#line 251 "gramatica.y"
{ checkeoParametroDeInvocacion(((Simbolo)val_peek(1).obj).getLexema(), val_peek(1).ival); }
break;
case 99:
//#line 252 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(2).ival + ": falta llave de cierre del cuerpo de la función Lambda");}
break;
case 100:
//#line 253 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(4).ival + ": falta llave de apertura del cuerpo de la función Lambda");}
break;
case 101:
//#line 254 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(3).ival + ": faltan ambas llaves del cuerpo de la función Lambda");}
break;
case 102:
//#line 255 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": No se especifica el parametro formal al que corresponde el parámetro real");}
break;
case 103:
//#line 256 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": No se especifica el parametro formal al que corresponde el parámetro real");}
break;
case 104:
//#line 260 "gramatica.y"
{ System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(1).ival + ": Se detectó una expresión desde una suma");
                                                                      if (!error()) {
                                                                        yyval = new ParserVal(new ArbolSintactico("SUM", (ArbolSintactico)val_peek(2).obj, (ArbolSintactico)val_peek(0).obj));
                                                                        ((ArbolSintactico)yyval.obj).imprimirArbol();
                                                                      }
                                                                    }
break;
case 105:
//#line 266 "gramatica.y"
{ System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(1).ival + ": Se detectó una expresión desde una resta");
                                                                      if (!error()) { yyval = new ParserVal(new ArbolSintactico("RES", (ArbolSintactico)val_peek(2).obj, (ArbolSintactico)val_peek(0).obj)); }
                                                                    }
break;
case 106:
//#line 269 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la izquierda de '+'"); }
break;
case 107:
//#line 270 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la derecha de '+'"); }
break;
case 108:
//#line 271 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la izquierda de '-'"); }
break;
case 109:
//#line 272 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la derecha de '-'"); }
break;
case 110:
//#line 273 "gramatica.y"
{ System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó una expresión desde un término");    
                                                                      yyval = val_peek(0);
                                                                    }
break;
case 111:
//#line 278 "gramatica.y"
{ System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(1).ival + ": Se detectó un término desde una multiplicación");
                                                                      if (!error()) { yyval = new ParserVal(new ArbolSintactico("MULT", (ArbolSintactico)val_peek(2).obj, (ArbolSintactico)val_peek(0).obj)); }
                                                                    }
break;
case 112:
//#line 281 "gramatica.y"
{ System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(1).ival + ": Se detectó un término desde una división");
                                                                      if (!error()) { yyval = new ParserVal(new ArbolSintactico("DIV", (ArbolSintactico)val_peek(2).obj, (ArbolSintactico)val_peek(0).obj)); }
                                                                    }
break;
case 113:
//#line 284 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la izquierda de '*'"); }
break;
case 114:
//#line 285 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la derecha de '*'"); }
break;
case 115:
//#line 286 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la izquierda de '/'"); }
break;
case 116:
//#line 287 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la derecha de '/'"); }
break;
case 117:
//#line 288 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(2).ival + ": falta operador entre operandos o falta de comparador en condición"); }
break;
case 118:
//#line 289 "gramatica.y"
{ System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un término desde un factor");
                                                                      yyval = val_peek(0);
                                                                    }
break;
case 119:
//#line 294 "gramatica.y"
{ constantePositiva(val_peek(0)); System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un factor desde una constante");
                                                                      restriccionCteFloat(val_peek(0));
                                                                      if (!error()) { yyval = new ParserVal(new ArbolSintactico((Simbolo)val_peek(0).obj)); }
                                                                    }
break;
case 120:
//#line 298 "gramatica.y"
{ constanteNegativa(val_peek(0)); System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un factor desde una constante negativa");
                                                                      restriccionCteFloat(val_peek(0));
                                                                      if (!error()) { yyval = new ParserVal(new ArbolSintactico((Simbolo)val_peek(0).obj)); }
                                                                    }
break;
case 121:
//#line 302 "gramatica.y"
{ System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un factor desde un identificador");
                                                                      if (!error()) { yyval = new ParserVal(new ArbolSintactico(checkearAlcance(val_peek(0).sval, val_peek(0).ival))); }
                                                                    }
break;
case 122:
//#line 305 "gramatica.y"
{ System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un factor desde una función");}
break;
case 123:
//#line 306 "gramatica.y"
{ constantePositiva(val_peek(1)); 
                                                                      if (enParamInvocacion)
                                                                        System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(3).ival + ": Se detectó la función Trunc en uno de los parámetros reales de la invocación");
                                                                      else  
                                                                        erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + val_peek(3).ival + ": la función TRUNC solo puede utilizarse en parámetros reales de invocación de funciones");
                                                                      truncarConstate(val_peek(1));
                                                                    }
break;
case 124:
//#line 313 "gramatica.y"
{ constanteNegativa(val_peek(1)); 
                                                                      if (enParamInvocacion)
                                                                        System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(4).ival + ": Se detectó la función Trunc en uno de los parámetros reales de la invocación");
                                                                      else  
                                                                        erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + val_peek(4).ival + ": la función TRUNC solo puede utilizarse en parámetros reales de invocación de funciones");
                                                                      truncarConstate(val_peek(1));
                                                                    }
break;
case 125:
//#line 322 "gramatica.y"
{ constantePositiva(val_peek(0)); restriccionCteFloat(val_peek(0)); }
break;
case 126:
//#line 323 "gramatica.y"
{ constanteNegativa(val_peek(0)); restriccionCteFloat(val_peek(0)); }
break;
case 127:
//#line 324 "gramatica.y"
{ constantePositiva(val_peek(0)); restriccionCteFloat(val_peek(0)); }
break;
case 128:
//#line 325 "gramatica.y"
{ constanteNegativa(val_peek(0)); restriccionCteFloat(val_peek(0)); }
break;
case 129:
//#line 326 "gramatica.y"
{ constantePositiva(val_peek(0)); restriccionCteFloat(val_peek(0)); erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta ',' entre constantes"); constantePositiva(val_peek(0)); restriccionCteFloat(val_peek(0)); }
break;
case 130:
//#line 327 "gramatica.y"
{ constanteNegativa(val_peek(0)); restriccionCteFloat(val_peek(0)); erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(2).ival + ": falta ',' entre constantes"); constanteNegativa(val_peek(0)); restriccionCteFloat(val_peek(0)); }
break;
//#line 1578 "Parser.java"
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
