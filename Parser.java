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
boolean enDeclaracionParam = false;
StringBuilder stringAmbitos = new StringBuilder();
ArrayList<Parametro> parametrosActuales = new ArrayList<>();
Stack<ArrayList<Parametro>> stackParametrosActuales = new Stack<>();
Stack<String> stackTipoFuncActual = new Stack<>();
ArbolSintactico arbolSintactico;
Map<String, ArbolSintactico> mapaFunciones = new HashMap<>();
SimboloFuncion funcionInvocadaActual = null;

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
    6,    6,    6,    6,    6,    7,   19,   20,    7,   18,
   18,   21,   21,   22,   22,   22,   22,   22,   22,   17,
   14,   14,    9,    9,   27,   27,   27,   29,   28,   30,
   32,   31,   10,   10,   10,   10,   10,   10,   10,   10,
   10,   10,   10,   12,   12,   12,   33,   33,   33,   33,
   34,   34,   35,   35,   35,   35,   35,   35,   15,   15,
   16,   16,   25,   25,   24,   24,   11,   13,   13,   13,
   13,   13,   38,   36,   37,   39,   39,   40,   40,   40,
   40,   40,   40,   40,   41,   23,   23,   23,   23,   23,
   23,   23,   42,   42,   42,   42,   42,   42,   42,   42,
   43,   43,   43,   43,   43,   43,   26,   26,   26,   26,
   26,   26,
};
final static short yylen[] = {                            2,
    0,    0,    5,    2,    3,    2,    2,    2,    1,    2,
    1,    2,    2,    1,    1,    3,    2,    2,    1,    1,
    1,    1,    1,    1,    1,    2,    0,    0,    8,    3,
    2,    3,    1,    3,    2,    2,    1,    2,    1,    1,
    4,    3,    3,    3,    3,    2,    2,    0,    2,    1,
    0,    2,    4,    6,    4,    5,    3,    5,    5,    3,
    4,    3,    3,    4,    4,    3,    3,    2,    2,    1,
    3,    1,    1,    1,    1,    1,    1,    1,    1,    1,
    3,    1,    3,    1,    1,    3,    3,    3,    3,    4,
    6,    7,    1,    2,    1,    3,    1,    3,    6,    5,
    5,    4,    5,    2,    4,    3,    3,    3,    3,    3,
    3,    1,    3,    3,    3,    3,    3,    3,    3,    1,
    1,    2,    1,    1,    4,    5,    3,    4,    1,    2,
    2,    3,
};
final static short yydefred[] = {                         0,
    0,    0,   79,    0,    0,    0,   80,    0,    0,    0,
    0,   11,    0,    0,   20,   21,   22,   23,   24,   25,
    0,    0,    0,   48,    0,    0,    0,   85,  121,    0,
    0,    0,  124,    0,    0,    0,    0,    0,    0,  120,
    0,    0,    0,    6,    0,    4,    8,   10,   12,   13,
    0,    0,   27,    0,   94,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,   97,
    0,    2,    0,    0,    0,    0,    0,    0,  122,   75,
   76,   77,   78,   73,   74,    0,    0,    0,    0,   60,
    0,   68,    0,    0,    0,   42,    0,   62,    0,   66,
    0,    5,    0,    0,    0,   86,    0,  129,    0,    0,
    0,   63,    0,    0,   49,   89,    0,    0,    0,    0,
   87,   88,    0,    0,    0,    0,    0,    0,    0,    0,
  115,  117,    0,    0,   67,    0,    0,    0,    0,    0,
   61,    0,   48,    0,   53,  119,  116,  113,  118,  114,
   41,   65,   64,   81,    0,   28,  130,  131,    0,    0,
   55,   47,    0,   46,    0,    0,    0,   90,   98,   96,
    0,   17,    0,    0,    0,    3,  125,    0,   59,   52,
   58,    0,   39,    0,   31,    0,    0,   33,    0,  127,
    0,  132,   45,    0,    0,  105,    0,    0,  102,    0,
   16,  126,   54,   38,    0,   35,   30,    0,    0,  128,
   91,    0,  100,    0,  101,   34,   32,    0,   92,   99,
   29,
};
final static short yydgoto[] = {                          9,
   10,   26,  127,   11,   12,   13,   14,  126,   15,   16,
   33,   18,   19,   20,   21,   52,   53,  156,  104,  189,
  187,  188,   34,   35,   23,  110,  115,   59,   60,   24,
  142,  143,   36,   37,   88,   38,   67,   68,   69,   70,
   71,   39,   40,
};
final static short yysindex[] = {                       616,
    0,  -34,    0,  -22, -218,   40,    0,  660,    0, -189,
  677,    0,   45,   64,    0,    0,    0,    0,    0,    0,
 -133,   -7,  -23,    0,  131,  631,   55,    0,    0,   85,
  -43, -126,    0,  419,  -12, -245,   93,  165,  -16,    0,
  190,  -34,  185,    0,  694,    0,    0,    0,    0,    0,
    0,   92,    0,  -43,    0, -119, -109,  -42,  124,  537,
  110,  113, -254, -101,  -38,  -12,  119,  120,  118,    0,
  572,    0,  211,  211,  109,  109,  -33,  122,    0,    0,
    0,    0,    0,    0,    0,  215,  218,  -43,  -97,    0,
 -143,    0,  109,  238,  241,    0,   36,    0,  125,    0,
   58,    0,  -90,  128,    8,    0,  127,    0,  -88,   17,
  -34,    0,  246,  116,    0,    0,   -5,  -80,  137,  -78,
    0,    0,  165, -147,  121,  752,  -96,   31,  -16,  -16,
    0,    0,  144,  -71,    0,   31,  -16,   31,  -16,    8,
    0,  -73,    0,  -72,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,  159,    0,    0,    0,   20,  -66,
    0,    0,  783,    0,  152,  -56,  163,    0,    0,    0,
  762,    0,  -51,  -65,  150,    0,    0,  177,    0,    0,
    0,   23,    0, -207,    0,   22,   76,    0,  134,    0,
   24,    0,    0,  221,  256,    0,   41,   33,    0,   50,
    0,    0,    0,    0,   51,    0,    0, -200,  797,    0,
    0,  261,    0,   63,    0,    0,    0,  711,    0,    0,
    0,
};
final static short yyrindex[] = {                         0,
  151,    0,    0,    0,  725,    0,    0,    0,    0,    0,
   47,    0,  300,  645,    0,    0,    0,    0,    0,    0,
    0,   14,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,  260,  -18,  731,  441,    0,    4,    0,
    0,    0,    0,    0,   52,    0,    0,    0,    0,    0,
  -40,  566,    0,    0,    0,    0,    0,    0,    0,    0,
  156,    0,    0,    0,    0,  141,    0,    0,  286,    0,
    0,    0,    0,    0,    0,    0,    0,  459,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,  745,    0,
  489,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,  511,    0,   30,    0,    0,  544,
    0,    0,    0,    0,    0,    0,    0,    0,  307,   99,
    0,    0,    0,    0,  769,    0,    0,    0,   27,   49,
    0,    0,    0,    0,    0,   71,   96,  333,  355,  282,
    0,    0,    0,  745,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,  776,    0,    0,    0,    0,    0,
    0,  598,    0,    0,    0,  101,    0,    0,    0,    0,
    0,    0,    0,  389,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,  103,    0,    0,    0,    0,    0,
    0,  407,    0,  105,    0,    0,    0,    0,    0,    0,
    0,
};
final static short yygindex[] = {                         0,
  304,    0,    0,    2,  -10,  937,    0, -104,    0,    0,
  581,    0,    0,    0,  -49,    0,    0,    0,    0,    0,
    0,  138,    5,  629,    0,    0,    0,  -28,    0,    0,
  198,    0,  -27,  314,    0,  651,    0,    0,    0,  228,
    0,    9,  -39,
};
final static int YYTABLESIZE=1108;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         40,
   48,   32,  109,   82,   87,   31,   86,   91,  163,   45,
   32,  134,    3,  118,   98,   89,   90,   41,   82,  171,
   57,    7,  123,  123,  123,   94,  123,   55,  123,   65,
   95,  112,   55,   56,   48,  131,  132,   58,   56,  166,
  123,  123,   65,  123,  112,   97,  112,  101,  112,  204,
   87,   42,   86,  146,  148,  150,  183,   84,  105,    3,
  159,  160,  112,  112,  191,  112,    3,  110,    7,  110,
  184,  110,   75,   83,   84,    7,  151,   76,   87,   43,
   86,  129,  130,  161,   82,  110,  110,   46,  110,  108,
   83,  108,  140,  108,  137,  139,   75,   74,  153,   73,
   87,   76,   86,   49,  123,  186,  123,  108,  108,   28,
  108,  111,    2,  111,  180,  111,  207,  144,  145,  208,
    4,    5,   50,   51,   77,    6,  112,   65,  112,  111,
  111,   79,  111,   92,  205,  103,  107,  106,  107,  104,
  107,   37,  104,   36,   37,  103,   36,   28,  103,  110,
  116,  110,  117,   32,  107,  107,  119,  107,  186,  121,
  122,  123,  135,   31,  141,  152,  154,  155,   32,  157,
   63,  108,   56,  108,  164,   64,  167,  168,  169,  172,
  176,   93,  123,  123,  177,  123,  178,  123,  179,  181,
   85,  192,  194,  111,   85,  111,   85,  121,  121,  185,
  121,  195,  121,  196,   63,  199,  200,   48,  201,   32,
  218,   85,   27,   28,   29,  108,   82,  202,  107,   82,
  107,   27,   28,   29,  133,  100,   82,   82,   82,   32,
   96,   30,   82,  120,   32,   82,   82,  123,  123,   93,
   30,  123,  123,  123,  123,  123,  123,  123,  123,  123,
  123,   54,  165,  123,  123,   32,  209,  123,  123,   32,
  112,  211,   32,  112,  112,  112,  112,  112,  112,  112,
  112,  112,  112,    1,  158,  112,  112,  190,  206,  112,
  112,  210,   32,  110,  203,   32,  110,  110,  110,  110,
  110,  110,  110,  110,  110,  110,  212,  213,  110,  110,
   72,  219,  110,  110,  214,  108,  215,  216,  108,  108,
  108,  108,  108,  108,  108,  108,  108,  108,   72,  220,
  108,  108,   71,    9,  108,  108,   95,  111,    7,   72,
  111,  111,  111,  111,  111,  111,  111,  111,  111,  111,
   71,  182,  111,  111,   78,  217,  111,  111,  122,  122,
  170,  122,  107,  122,    0,  107,  107,  107,  107,  107,
  107,  107,  107,  107,  107,   28,   29,  107,  107,    0,
  162,  107,  107,  109,    0,  109,    0,  109,    0,   27,
   28,   29,   72,   30,   72,    0,   27,   28,   61,    0,
    0,  109,  109,  111,  109,  106,  123,  106,   30,  106,
    0,    0,    0,    0,   71,   62,   71,    1,    0,   85,
    1,  121,  123,  106,  106,  183,  106,    1,    1,    1,
   27,   28,   29,    1,   14,    3,    1,  121,    0,  184,
  125,  125,    0,  125,    7,  125,    0,    0,    0,   30,
   27,   28,   29,    0,    0,   27,   28,   29,  126,  126,
    0,  126,    0,  126,    0,  109,    0,  109,   99,   30,
    0,   87,    0,   86,   30,    0,  128,   28,   29,    0,
  136,   28,   29,  138,   28,   29,    0,  106,   85,  106,
   84,    0,    0,    0,    0,   30,    0,    0,    0,   30,
    0,    0,   30,  147,   28,   29,  149,   28,   29,   70,
    0,    0,   28,    0,    0,    2,    0,    0,    0,    0,
    0,    0,   30,    4,    5,   30,   72,   69,    6,   72,
   72,   72,    0,    0,    0,    0,   72,   72,   72,    0,
    0,   72,   72,    0,    0,   72,   72,    0,   71,    0,
    0,   71,   71,   71,    0,    0,    0,   57,   71,   71,
   71,    0,    0,   71,   71,    0,   14,   71,   71,   14,
    0,    0,  122,   70,    0,   70,   14,   14,   14,   43,
    0,    0,   14,    0,    0,   14,   14,    0,  122,    0,
   17,   69,    0,   69,    0,    0,    0,    0,   17,  109,
    0,   17,  109,  109,  109,  109,  109,  109,  109,  109,
  109,  109,   44,    0,  109,  109,   17,    0,  109,  109,
    0,  106,    0,   57,  106,  106,  106,  106,  106,  106,
  106,  106,  106,  106,   26,   17,  106,  106,   22,    0,
  106,  106,    0,    0,    0,   43,   22,    0,    0,   22,
   17,    0,    0,    0,  125,    0,    0,    0,    0,    0,
   25,   17,    0,   66,   22,    0,   56,    0,   25,  113,
  125,   25,  126,    0,    0,    0,    0,    0,   44,    0,
    0,    0,    0,   22,    0,    0,   25,    0,  126,    0,
    0,   80,   81,   82,   83,  107,    0,    0,   22,    0,
   26,    0,    0,   17,  124,   25,    0,   70,    0,   22,
   70,   70,   70,    0,   17,    0,   17,   70,   70,   70,
   25,    0,   70,   70,    0,   69,   70,   70,   69,   69,
   69,   25,   56,    0,    0,   69,   69,   69,    0,    0,
   69,   69,    0,    0,   69,   69,    0,    0,    8,    0,
    0,   22,    0,   17,    0,   57,    0,    0,   57,    0,
    0,   17,   22,    8,   22,   57,   57,   57,    0,    0,
   57,   57,    0,   25,   57,   57,    0,   43,    0,   15,
   43,    0,    0,    0,   25,    0,   25,   43,   43,   43,
    0,    0,   43,   43,   44,    0,   43,   43,    0,   17,
    0,   22,    0,   28,    0,    0,    2,    0,   17,   22,
   44,   47,    0,   44,    4,    5,    0,    0,    0,    6,
   44,   44,   44,   25,    0,   44,   44,    0,  102,   44,
   44,   25,   26,    0,    0,   26,    0,    0,   28,    0,
    0,    2,   26,   26,   26,  221,    0,   22,   26,    4,
    5,   26,   26,    0,    6,    0,   22,   50,    0,    0,
    0,    0,    0,   48,   56,    0,    0,   56,    0,   25,
    0,    0,    0,    0,   56,   56,   56,   51,   25,   56,
   56,    0,    1,   56,   56,    2,  174,    0,    0,    0,
    0,    0,    3,    4,    5,    0,  198,   28,    6,    0,
    2,    7,    0,   19,    0,    0,    0,    3,    4,    5,
   18,   15,    0,    6,   15,    0,    7,  193,    0,    0,
    0,   15,   15,   15,    0,    0,   28,   15,    0,    2,
   15,   15,    0,    0,    0,    0,    3,    4,    5,    0,
    0,    0,    6,   28,    0,    7,    2,    0,    0,    0,
    0,    0,    0,    3,    4,    5,    0,    0,    0,    6,
   28,    0,    7,    2,    0,    0,    0,    0,    0,    0,
    3,    4,    5,    0,    0,    0,    6,   28,    0,    7,
    2,    0,    0,    0,    0,    0,    0,    3,    4,    5,
    0,   50,    0,    6,   50,    0,    7,   48,    0,    0,
   48,    0,   50,   50,    0,    0,  114,   50,   48,   48,
    0,   51,    0,   48,   51,    0,    0,  125,   28,    0,
    0,    2,   51,   51,    0,    0,    0,   51,   28,    4,
    5,    2,    0,  173,    6,   19,    0,    0,   19,    4,
    5,    0,   18,  197,    6,   18,   19,   19,    0,   28,
   19,   19,    2,   18,   18,    0,    0,   18,   18,  125,
    4,    5,    0,   28,    0,    6,    2,    0,    0,    0,
  125,    0,  175,    3,    4,    5,    0,    0,    0,    6,
    0,    0,    7,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,  175,
    0,    0,    0,    0,    0,    0,    0,  175,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         40,
   11,   45,   45,   44,   43,   40,   45,   36,  113,    8,
   45,   45,  267,   63,   42,  261,  262,   40,   59,  124,
   44,  276,   41,   42,   43,   42,   45,   40,   47,   25,
   47,   59,   40,   46,   45,   75,   76,   61,   46,   45,
   59,   60,   38,   62,   41,   41,   43,   43,   45,  257,
   43,  270,   45,   93,   94,   95,  257,   44,   54,  267,
   44,   45,   59,   60,   45,   62,  267,   41,  276,   43,
  271,   45,   42,   44,   61,  276,   41,   47,   43,   40,
   45,   73,   74,  111,  125,   59,   60,  277,   62,   41,
   61,   43,   88,   45,   86,   87,   42,   43,   41,   45,
   43,   47,   45,   59,  123,  155,  125,   59,   60,  257,
   62,   41,  260,   43,  143,   45,   41,  261,  262,   44,
  268,  269,   59,  257,   40,  273,  123,  123,  125,   59,
   60,  258,   62,   41,  184,   44,   41,  257,   43,   41,
   45,   41,   44,   41,   44,   41,   44,  257,   44,  123,
   41,  125,   40,   45,   59,   60,  258,   62,  208,   41,
   41,   44,   41,   40,  262,   41,  257,   40,   45,  258,
   40,  123,   46,  125,   59,   45,  257,   41,  257,   59,
  277,   41,   42,   43,   41,   45,  258,   47,  262,  262,
   40,  258,   41,  123,   44,  125,   46,   42,   43,   41,
   45,  258,   47,   41,   40,  257,  272,  218,   59,   45,
  209,   61,  256,  257,  258,  258,  257,   41,  123,  260,
  125,  256,  257,  258,  258,   41,  267,  268,  269,   45,
   41,  275,  273,  272,   45,  276,  277,  256,  257,  256,
  275,  260,  261,  262,  263,  264,  265,  266,  267,  268,
  269,  259,  258,  272,  273,   45,  123,  276,  277,   45,
  257,   41,   45,  260,  261,  262,  263,  264,  265,  266,
  267,  268,  269,  123,  258,  272,  273,  258,  257,  276,
  277,  258,   45,  257,  262,   45,  260,  261,  262,  263,
  264,  265,  266,  267,  268,  269,   41,  257,  272,  273,
   41,   41,  276,  277,  272,  257,  257,  257,  260,  261,
  262,  263,  264,  265,  266,  267,  268,  269,   59,  257,
  272,  273,   41,  277,  276,  277,   41,  257,  277,   26,
  260,  261,  262,  263,  264,  265,  266,  267,  268,  269,
   59,  144,  272,  273,   31,  208,  276,  277,   42,   43,
  123,   45,  257,   47,   -1,  260,  261,  262,  263,  264,
  265,  266,  267,  268,  269,  257,  258,  272,  273,   -1,
  125,  276,  277,   41,   -1,   43,   -1,   45,   -1,  256,
  257,  258,  123,  275,  125,   -1,  256,  257,  258,   -1,
   -1,   59,   60,  270,   62,   41,  256,   43,  275,   45,
   -1,   -1,   -1,   -1,  123,  275,  125,  257,   -1,  259,
  260,  256,  272,   59,   60,  257,   62,  267,  268,  269,
  256,  257,  258,  273,  125,  267,  276,  272,   -1,  271,
   42,   43,   -1,   45,  276,   47,   -1,   -1,   -1,  275,
  256,  257,  258,   -1,   -1,  256,  257,  258,   42,   43,
   -1,   45,   -1,   47,   -1,  123,   -1,  125,  274,  275,
   -1,   43,   -1,   45,  275,   -1,  256,  257,  258,   -1,
  256,  257,  258,  256,  257,  258,   -1,  123,   60,  125,
   62,   -1,   -1,   -1,   -1,  275,   -1,   -1,   -1,  275,
   -1,   -1,  275,  256,  257,  258,  256,  257,  258,   59,
   -1,   -1,  257,   -1,   -1,  260,   -1,   -1,   -1,   -1,
   -1,   -1,  275,  268,  269,  275,  257,   59,  273,  260,
  261,  262,   -1,   -1,   -1,   -1,  267,  268,  269,   -1,
   -1,  272,  273,   -1,   -1,  276,  277,   -1,  257,   -1,
   -1,  260,  261,  262,   -1,   -1,   -1,   59,  267,  268,
  269,   -1,   -1,  272,  273,   -1,  257,  276,  277,  260,
   -1,   -1,  256,  123,   -1,  125,  267,  268,  269,   59,
   -1,   -1,  273,   -1,   -1,  276,  277,   -1,  272,   -1,
    0,  123,   -1,  125,   -1,   -1,   -1,   -1,    8,  257,
   -1,   11,  260,  261,  262,  263,  264,  265,  266,  267,
  268,  269,   59,   -1,  272,  273,   26,   -1,  276,  277,
   -1,  257,   -1,  125,  260,  261,  262,  263,  264,  265,
  266,  267,  268,  269,   59,   45,  272,  273,    0,   -1,
  276,  277,   -1,   -1,   -1,  125,    8,   -1,   -1,   11,
   60,   -1,   -1,   -1,  256,   -1,   -1,   -1,   -1,   -1,
    0,   71,   -1,   25,   26,   -1,   59,   -1,    8,  123,
  272,   11,  256,   -1,   -1,   -1,   -1,   -1,  125,   -1,
   -1,   -1,   -1,   45,   -1,   -1,   26,   -1,  272,   -1,
   -1,  263,  264,  265,  266,   57,   -1,   -1,   60,   -1,
  125,   -1,   -1,  113,  123,   45,   -1,  257,   -1,   71,
  260,  261,  262,   -1,  124,   -1,  126,  267,  268,  269,
   60,   -1,  272,  273,   -1,  257,  276,  277,  260,  261,
  262,   71,  125,   -1,   -1,  267,  268,  269,   -1,   -1,
  272,  273,   -1,   -1,  276,  277,   -1,   -1,  123,   -1,
   -1,  113,   -1,  163,   -1,  257,   -1,   -1,  260,   -1,
   -1,  171,  124,  123,  126,  267,  268,  269,   -1,   -1,
  272,  273,   -1,  113,  276,  277,   -1,  257,   -1,  125,
  260,   -1,   -1,   -1,  124,   -1,  126,  267,  268,  269,
   -1,   -1,  272,  273,  125,   -1,  276,  277,   -1,  209,
   -1,  163,   -1,  257,   -1,   -1,  260,   -1,  218,  171,
  257,  125,   -1,  260,  268,  269,   -1,   -1,   -1,  273,
  267,  268,  269,  163,   -1,  272,  273,   -1,  125,  276,
  277,  171,  257,   -1,   -1,  260,   -1,   -1,  257,   -1,
   -1,  260,  267,  268,  269,  125,   -1,  209,  273,  268,
  269,  276,  277,   -1,  273,   -1,  218,  123,   -1,   -1,
   -1,   -1,   -1,  123,  257,   -1,   -1,  260,   -1,  209,
   -1,   -1,   -1,   -1,  267,  268,  269,  123,  218,  272,
  273,   -1,  257,  276,  277,  260,  125,   -1,   -1,   -1,
   -1,   -1,  267,  268,  269,   -1,  125,  257,  273,   -1,
  260,  276,   -1,  125,   -1,   -1,   -1,  267,  268,  269,
  125,  257,   -1,  273,  260,   -1,  276,  125,   -1,   -1,
   -1,  267,  268,  269,   -1,   -1,  257,  273,   -1,  260,
  276,  277,   -1,   -1,   -1,   -1,  267,  268,  269,   -1,
   -1,   -1,  273,  257,   -1,  276,  260,   -1,   -1,   -1,
   -1,   -1,   -1,  267,  268,  269,   -1,   -1,   -1,  273,
  257,   -1,  276,  260,   -1,   -1,   -1,   -1,   -1,   -1,
  267,  268,  269,   -1,   -1,   -1,  273,  257,   -1,  276,
  260,   -1,   -1,   -1,   -1,   -1,   -1,  267,  268,  269,
   -1,  257,   -1,  273,  260,   -1,  276,  257,   -1,   -1,
  260,   -1,  268,  269,   -1,   -1,   60,  273,  268,  269,
   -1,  257,   -1,  273,  260,   -1,   -1,   71,  257,   -1,
   -1,  260,  268,  269,   -1,   -1,   -1,  273,  257,  268,
  269,  260,   -1,  272,  273,  257,   -1,   -1,  260,  268,
  269,   -1,  257,  272,  273,  260,  268,  269,   -1,  257,
  272,  273,  260,  268,  269,   -1,   -1,  272,  273,  113,
  268,  269,   -1,  257,   -1,  273,  260,   -1,   -1,   -1,
  124,   -1,  126,  267,  268,  269,   -1,   -1,   -1,  273,
   -1,   -1,  276,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  163,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,  171,
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
"declaracion : tipo id_funcion $$3 parametros_declaracion_f $$4 '{' listasentencias '}'",
"parametros_declaracion_f : '(' listaparametros ')'",
"parametros_declaracion_f : '(' ')'",
"listaparametros : listaparametros ',' parametro_formal",
"listaparametros : parametro_formal",
"parametro_formal : CV tipo ID",
"parametro_formal : tipo ID",
"parametro_formal : CV tipo",
"parametro_formal : tipo",
"parametro_formal : CV ID",
"parametro_formal : ID",
"id_funcion : ID",
"sentencia_return : RETURN '(' expresion ')'",
"sentencia_return : RETURN '(' ')'",
"asignacion : identificador ASIG expresion",
"asignacion : listavariables '=' listaconstantes",
"bloque_control_estructura : '{' listasentenciasejecutables '}'",
"bloque_control_estructura : sentenciaejecutable ';'",
"bloque_control_estructura : '{' '}'",
"$$5 :",
"bloque_control : $$5 bloque_control_estructura",
"inicio_do : DO",
"$$6 :",
"bloque_control_else : $$6 bloque_control",
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
"pinvocacion : cabecera_declaracion_lambda '{' listasentenciasejecutables '}' FLECHA ID",
"pinvocacion : cabecera_declaracion_lambda '{' listasentenciasejecutables FLECHA ID",
"pinvocacion : cabecera_declaracion_lambda listasentenciasejecutables '}' FLECHA ID",
"pinvocacion : cabecera_declaracion_lambda listasentenciasejecutables FLECHA ID",
"pinvocacion : cabecera_declaracion_lambda '{' listasentenciasejecutables '}' FLECHA",
"pinvocacion : expresion FLECHA",
"cabecera_declaracion_lambda : '(' tipo ID ')'",
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

//#line 391 "gramatica.y"

public Parser(Map<String, Simbolo> tablaSimbolos, String path, ArrayList<String> erroresLexicos, ArrayList<String> erroresSintacticos, ArrayList<String> erroresSemanticos, ArrayList<String> warnings, ArbolSintactico arbolSintactico, Map<String, ArbolSintactico> mapaFunciones) {
  this.tablaSimbolos = tablaSimbolos;
  this.erroresLexicos = erroresLexicos;
  this.erroresSintacticos = erroresSintacticos;
  this.erroresSemanticos = erroresSemanticos;
  this.warnings = warnings;
  this.lexer = new Lexico(tablaSimbolos,path, erroresLexicos, warnings);
  this.arbolSintactico = arbolSintactico;
  this.mapaFunciones = mapaFunciones;
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
  if (simboloID != null) {
    tablaSimbolos.remove(simboloID.getLexema());
    Simbolo nuevoSimbolo = new SimboloFuncion(stackTipoFuncActual.pop(), "Funcion", simboloID.getLexema(), stackParametrosActuales.pop());
    tablaSimbolos.put(simboloID.getLexema(), nuevoSimbolo);
  }
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

// Esta función toma el Lexema de un identificado con la forma escrita por el programador (puede contener "."), busca y retorna de la tabla de símbolos el símbolo correspondiente, teniendo en cuenta los ámbitos.
private Simbolo checkearAlcance(String lexema, int nroLinea) {

  // AuxSA se vuelve una copia de StringAmbitos para ir recortándola
  String auxSA = stringAmbitos.toString();

  // Cuando el Lexema es una variable no anidada:
  if (!lexema.contains(".")) {
    String auxLexema = lexema + auxSA;

    while (!tablaSimbolos.containsKey(auxLexema) && auxSA.length() > 0) {
      auxSA = auxSA.substring(0, auxSA.lastIndexOf(":"));
      auxLexema = lexema + auxSA;
    }

    // Tira error si no se encuentra en el ámbito actual o los superiores (A no ser que sea una variable de función lambda, que se guarda sin ámbito por decisión de diseño)
    if (auxLexema.equals(lexema) && !tablaSimbolos.containsKey(auxLexema)) {
      erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + nroLinea + ": La variable '" + lexema + "' no ha sido declarada en el ámbito actual y tampoco está al alcance");
      tablaSimbolos.remove(lexema);
      return null;
    } else {
      tablaSimbolos.remove(lexema);
      return tablaSimbolos.get(auxLexema);
    }

  // Cuando el Lexema es una variable anidada:    
  } else { 
    String varActual = lexema.substring(lexema.lastIndexOf(".") + 1);
    String ambitoVarActual = (":" + lexema.substring(0, lexema.lastIndexOf("."))).replace(".", ":");

    // Dado que estaba anidada, se va a buscar el ámbito a partir de stringAmbitos seguido del ambito escrito en la variable
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
    String lexemaFuncion = funcionInvocadaActual.getLexema();
    if (!funcionInvocadaActual.existeParametro(lexemaParametro)) {
      erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + nroLinea + ": el parámetro formal '" + lexemaParametro + "' no existe en la función '" + lexemaFuncion.substring(0, lexemaFuncion.indexOf(':')) + "'");    
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

private ArbolSintactico generarAsigMultiple(ArrayList<Simbolo> listaVariables, ArrayList<Simbolo> listaConstantes) {
  int i = 1; 
  ArbolSintactico arbolRetorno = new ArbolSintactico("Sentencia", new ArbolSintactico("Asig", new ArbolSintactico(listaVariables.get(0)), new ArbolSintactico(listaConstantes.get(0))), null );                                                                                            
  while (i < listaConstantes.size()) {
    ArbolSintactico nuevaAsignacion = new ArbolSintactico( "Sentencia", new ArbolSintactico("Asig", new ArbolSintactico(listaVariables.get(i)), new ArbolSintactico(listaConstantes.get(i))), null);
    arbolRetorno.setUltimoDerecho(nuevaAsignacion);
    i++;
  }
  SimboloLong simboloCero = (SimboloLong)tablaSimbolos.get("0L");
  if (simboloCero == null) {
    simboloCero = new SimboloLong("Long", "Constante", "0L", 0L);
    tablaSimbolos.put("0L", simboloCero);
  }
  while (i < listaVariables.size()) {
    ArbolSintactico nuevaAsignacion = new ArbolSintactico( "Sentencia", new ArbolSintactico("Asig", new ArbolSintactico(listaVariables.get(i)), new ArbolSintactico(simboloCero)), null);
    arbolRetorno.setUltimoDerecho(nuevaAsignacion);
    i++;
  } 

  return arbolRetorno;
}
//#line 865 "Parser.java"
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
{if (!error() && val_peek(2).obj != null && val_peek(2).obj instanceof ArbolSintactico) { arbolSintactico.adoptarNodoIntermedio((ArbolSintactico)val_peek(2).obj);}}
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
                                                                                    }
                                                                                  }
break;
case 17:
//#line 50 "gramatica.y"
{ if (!error()) { yyval = new ParserVal(new ArbolSintactico("Sentencia", ((ArbolSintactico)val_peek(1).obj), null)); } }
break;
case 18:
//#line 51 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta ; al final de la sentencia ejecutable");}
break;
case 19:
//#line 52 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta ; al final de la sentencia ejecutable");}
break;
case 20:
//#line 55 "gramatica.y"
{ yyval = val_peek(0); }
break;
case 21:
//#line 56 "gramatica.y"
{ yyval = val_peek(0); }
break;
case 22:
//#line 57 "gramatica.y"
{ yyval = val_peek(0); }
break;
case 23:
//#line 58 "gramatica.y"
{ System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un print");
                                                                                yyval = val_peek(0);
                                                                              }
break;
case 24:
//#line 61 "gramatica.y"
{if(enFunc==0) {erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + val_peek(0).ival + ": Invocacion de funcion lambda fuera de una función");} else {yyval = val_peek(0);}}
break;
case 25:
//#line 62 "gramatica.y"
{chequeoSentenciaReturn(val_peek(0)); yyval = val_peek(0);}
break;
case 26:
//#line 65 "gramatica.y"
{ System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó una declaración de variables");}
break;
case 27:
//#line 66 "gramatica.y"
{stringAmbitos.append(":"+((Simbolo)val_peek(0).obj).getLexema()); stackTipoFuncActual.push(val_peek(1).sval); enDeclaracionParam = true;}
break;
case 28:
//#line 66 "gramatica.y"
{enDeclaracionParam = false; stackParametrosActuales.push(parametrosActuales); parametrosActuales = new ArrayList<>(); enFunc++;}
break;
case 29:
//#line 66 "gramatica.y"
{  finFunc( val_peek(0), ((Simbolo)val_peek(6).obj) ); 
                                                                                                                                                                                                                                                                    quitarAmbito(); 
                                                                                                                                                                                                                                                                    renombrarVariable(val_peek(6)); 
                                                                                                                                                                                                                                                                    System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó una declaración de función");
                                                                                                                                                                                                                                                                    mapaFunciones.put(((Simbolo)val_peek(6).obj).getLexema(), (ArbolSintactico)val_peek(1).obj);
                                                                                                                                                                                                                                                                    yyval.ival = val_peek(0).ival;
                                                                                                                                                                                                                                                                  }
break;
case 34:
//#line 83 "gramatica.y"
{ ((Simbolo)val_peek(0).obj).setTipo(val_peek(1).sval);
                                                                       renombrarVariable(val_peek(0)); 
                                                                       parametrosActuales.add(new Parametro(((Simbolo)val_peek(0).obj), "CV"));
                                                                     }
break;
case 35:
//#line 87 "gramatica.y"
{ ((Simbolo)val_peek(0).obj).setTipo(val_peek(1).sval);
                                                                       renombrarVariable(val_peek(0));
                                                                       parametrosActuales.add(new Parametro(((Simbolo)val_peek(0).obj), "CVR"));
                                                                     }
break;
case 36:
//#line 91 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el nombre del parámetro");
                                                                     }
break;
case 37:
//#line 93 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta el nombre del parámetro");
                                                                     }
break;
case 38:
//#line 95 "gramatica.y"
{ renombrarVariable(val_peek(0)); 
                                                                       parametrosActuales.add(new Parametro(((Simbolo)val_peek(0).obj), "CV")); 
                                                                       erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el tipo del parámetro");
                                                                     }
break;
case 39:
//#line 99 "gramatica.y"
{ renombrarVariable(val_peek(0)); 
                                                                       parametrosActuales.add(new Parametro(((Simbolo)val_peek(0).obj), "CVR")); 
                                                                       erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta el tipo del parámetro");
                                                                     }
break;
case 40:
//#line 105 "gramatica.y"
{yyval = new ParserVal((SimboloLong)val_peek(0).obj);}
break;
case 41:
//#line 108 "gramatica.y"
{ System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(1).ival + ": Se detectó una sentencia return");
                                                                              if (!error()) { yyval = new ParserVal(new ArbolSintactico("Return", (ArbolSintactico)val_peek(1).obj,  null)); }
                                                                            }
break;
case 42:
//#line 111 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta de argumento en sentencia return");}
break;
case 43:
//#line 114 "gramatica.y"
{ System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó una asignación simple");
                                                                                            if (!error()) { yyval = new ParserVal( new ArbolSintactico("Asig", new ArbolSintactico(checkearAlcance(val_peek(2).sval, val_peek(2).ival)), (ArbolSintactico)val_peek(0).obj) ); }
                                                                                          }
break;
case 44:
//#line 117 "gramatica.y"
{ System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó una asignación múltiple");
                                                                                            if(((ArrayList<Simbolo>)val_peek(2).obj).size() >= ((ArrayList<Simbolo>)val_peek(0).obj).size() && !error()) {  
                                                                                              yyval = new ParserVal(generarAsigMultiple((ArrayList<Simbolo>)val_peek(2).obj, (ArrayList<Simbolo>)val_peek(0).obj));
                                                                                            }
                                                                                            else erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + val_peek(0).ival + ": La cantidad de constantes en la asignación múltiple es mayor a la cantidad de variables");
                                                                                          }
break;
case 45:
//#line 125 "gramatica.y"
{ yyval = val_peek(1); }
break;
case 46:
//#line 126 "gramatica.y"
{ { if (!error()) { yyval = new ParserVal(new ArbolSintactico("Sentencia", ((ArbolSintactico)val_peek(1).obj), null)); } }  }
break;
case 47:
//#line 127 "gramatica.y"
{ yyval.obj = null; }
break;
case 48:
//#line 130 "gramatica.y"
{inicioIf();}
break;
case 49:
//#line 130 "gramatica.y"
{finIf(val_peek(0)); yyval = val_peek(0); }
break;
case 50:
//#line 133 "gramatica.y"
{enDo=true;}
break;
case 51:
//#line 136 "gramatica.y"
{sinRetIf.push(sinRet);}
break;
case 52:
//#line 136 "gramatica.y"
{if(sinRetIf.pop()) {sinRet=true;}; yyval = val_peek(0); }
break;
case 53:
//#line 139 "gramatica.y"
{ System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un bloque IF");
                                                                                                  if (!error()) { yyval = new ParserVal(new ArbolSintactico("If", (ArbolSintactico)val_peek(2).obj, new ArbolSintactico("CuerpoIf",(ArbolSintactico)val_peek(1).obj, null)) ); }
                                                                                                }
break;
case 54:
//#line 142 "gramatica.y"
{ System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un bloque IF-ELSE");
                                                                                                  if (!error()) { yyval = new ParserVal(new ArbolSintactico("IfElse", (ArbolSintactico)val_peek(4).obj, new ArbolSintactico("CuerpoIfElse",(ArbolSintactico)val_peek(3).obj, (ArbolSintactico)val_peek(1).obj)) ); }  
                                                                                                }
break;
case 55:
//#line 145 "gramatica.y"
{ enDo=false;
                                                                                                  System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un bloque DO UNTIL");
                                                                                                  if (!error()) { yyval = new ParserVal(new ArbolSintactico("DoUntil", (ArbolSintactico)val_peek(0).obj, (ArbolSintactico)val_peek(2).obj)); }  
                                                                                                }
break;
case 56:
//#line 150 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta la palabra ENDIF al final del bloque IF-ELSE");}
break;
case 57:
//#line 151 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta la palabra ENDIF al final del bloque IF");}
break;
case 58:
//#line 152 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el cuerpo en el bloque ELSE");}
break;
case 59:
//#line 153 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(3).ival + ": falta el cuerpo en el bloque IF");}
break;
case 60:
//#line 154 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el cuerpo en el bloque IF");}
break;
case 61:
//#line 155 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el cuerpo en los bloques IF y ELSE");}
break;
case 62:
//#line 157 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(2).ival + ": falta de cuerpo en iteracion");}
break;
case 63:
//#line 158 "gramatica.y"
{ enDo=false;
                                                                                                  erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta la palabra UNTIL en la iteracion");
                                                                                                }
break;
case 64:
//#line 164 "gramatica.y"
{ if (!error()) { yyval = new ParserVal (new ArbolSintactico("Print", (ArbolSintactico)val_peek(1).obj, null)); } }
break;
case 65:
//#line 165 "gramatica.y"
{ if (!error()) { yyval = new ParserVal (new ArbolSintactico("Print", new ArbolSintactico((Simbolo)val_peek(1).obj), null)); } }
break;
case 66:
//#line 166 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta de argumento en sentencia print");}
break;
case 67:
//#line 169 "gramatica.y"
{ yyval = val_peek(1); }
break;
case 68:
//#line 170 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta el paréntesis de apertura de la condición");}
break;
case 69:
//#line 171 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta el paréntesis de cierre de la condición");}
break;
case 70:
//#line 172 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": faltan ambos paréntesis de la condición");}
break;
case 71:
//#line 175 "gramatica.y"
{ if (!error()) { yyval = new ParserVal (new ArbolSintactico(val_peek(1).sval, (ArbolSintactico)val_peek(2).obj, (ArbolSintactico)val_peek(0).obj)); } }
break;
case 72:
//#line 176 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": La condición es incompleta");}
break;
case 73:
//#line 179 "gramatica.y"
{ yyval = new ParserVal("Mayor"); }
break;
case 74:
//#line 180 "gramatica.y"
{ yyval = new ParserVal("Menor"); }
break;
case 75:
//#line 181 "gramatica.y"
{ yyval = new ParserVal("MayorIgual"); }
break;
case 76:
//#line 182 "gramatica.y"
{ yyval = new ParserVal("MenorIgual"); }
break;
case 77:
//#line 183 "gramatica.y"
{ yyval = new ParserVal("Igual"); }
break;
case 78:
//#line 184 "gramatica.y"
{ yyval = new ParserVal("Distinto"); }
break;
case 79:
//#line 187 "gramatica.y"
{ yyval = new ParserVal("Long"); }
break;
case 80:
//#line 188 "gramatica.y"
{ yyval = new ParserVal("Lambda");
                                                      if (!enDeclaracionParam) {
                                                        erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": el tipo 'Lambda' solo puede utilizarse en la declaración de parámetros formales de funciones estándar");
                                                      }
                                                    }
break;
case 81:
//#line 195 "gramatica.y"
{renombrarVariable(val_peek(0));}
break;
case 82:
//#line 196 "gramatica.y"
{  renombrarVariable(val_peek(0));}
break;
case 83:
//#line 200 "gramatica.y"
{ if (!error()) {
                                                                          yyval = val_peek(2); 
                                                                          ((ArrayList<Simbolo>)yyval.obj).add( checkearAlcance(val_peek(0).sval, val_peek(0).ival) );
                                                                        }
                                                                      }
break;
case 84:
//#line 205 "gramatica.y"
{ if (!error()) {
                                                                          yyval = new ParserVal( new ArrayList<Simbolo>() );
                                                                          ((ArrayList<Simbolo>)yyval.obj).add( checkearAlcance(val_peek(0).sval, val_peek(0).ival) );
                                                                        }
                                                                      }
break;
case 85:
//#line 213 "gramatica.y"
{ yyval = new ParserVal( ((SimboloLong)val_peek(0).obj).getLexema() );
                                                                        yyval.ival = val_peek(0).ival; 
                                                                      }
break;
case 86:
//#line 216 "gramatica.y"
{ yyval = new ParserVal( val_peek(2).sval + "." + ((SimboloLong)val_peek(0).obj).getLexema() );
                                                                        yyval.ival = val_peek(0).ival;
                                                                      }
break;
case 87:
//#line 222 "gramatica.y"
{ enParamInvocacion = false;
                                                                                                                System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó la invocación de una función");
                                                                                                                ((ArbolSintactico)val_peek(2).obj).setUltimoDerecho((ArbolSintactico)val_peek(1).obj); 
                                                                                                                yyval = val_peek(2);
                                                                                                              }
break;
case 88:
//#line 229 "gramatica.y"
{ enParamInvocacion = false;
                                                                                                                System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó la invocación de una función lambda");
                                                                                                                chequeo_anid_lambda(val_peek(2).sval, val_peek(0));
                                                                                                                ((ArbolSintactico)val_peek(2).obj).setUltimoDerecho( new ArbolSintactico((Simbolo)val_peek(1).obj) );
                                                                                                                yyval = val_peek(2);
                                                                                                              }
break;
case 89:
//#line 235 "gramatica.y"
{ enParamInvocacion = false;
                                                                                                                restriccionCteFloat(val_peek(1));
                                                                                                                chequeo_anid_lambda(val_peek(2).sval, val_peek(0)); 
                                                                                                                constantePositiva(val_peek(1)); 
                                                                                                                System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó la invocación de una función lambda");
                                                                                                                ((ArbolSintactico)val_peek(2).obj).setUltimoDerecho( new ArbolSintactico((Simbolo)val_peek(1).obj) );
                                                                                                                yyval = val_peek(2);
                                                                                                              }
break;
case 90:
//#line 243 "gramatica.y"
{ enParamInvocacion = false;
                                                                                                                restriccionCteFloat(val_peek(1));
                                                                                                                chequeo_anid_lambda(val_peek(3).sval, val_peek(0)); 
                                                                                                                constanteNegativa(val_peek(1)); 
                                                                                                                System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó la invocación de una función lambda");
                                                                                                                ((ArbolSintactico)val_peek(3).obj).setUltimoDerecho( new ArbolSintactico((Simbolo)val_peek(1).obj) );
                                                                                                                yyval = val_peek(3);
                                                                                                              }
break;
case 91:
//#line 251 "gramatica.y"
{ enParamInvocacion = false;
                                                                                                                constantePositiva(val_peek(2));
                                                                                                                chequeo_anid_lambda(val_peek(5).sval, val_peek(0)); 
                                                                                                                System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó la invocación de una función lambda");
                                                                                                                ((ArbolSintactico)val_peek(5).obj).setUltimoDerecho( new ArbolSintactico("Trunc", new ArbolSintactico((Simbolo)val_peek(2).obj), null) );
                                                                                                                yyval = val_peek(5);
                                                                                                              }
break;
case 92:
//#line 258 "gramatica.y"
{ enParamInvocacion = false;
                                                                                                                constanteNegativa(val_peek(2)); 
                                                                                                                chequeo_anid_lambda(val_peek(6).sval, val_peek(0));
                                                                                                                System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó la invocación de una función lambda");
                                                                                                                ((ArbolSintactico)val_peek(6).obj).setUltimoDerecho( new ArbolSintactico("Trunc", new ArbolSintactico((Simbolo)val_peek(2).obj), null) );
                                                                                                                yyval = val_peek(6);                                                                                                              
                                                                                                              }
break;
case 93:
//#line 267 "gramatica.y"
{yyval = new ParserVal(checkearAlcance(val_peek(0).sval, val_peek(0).ival).getLexema() );}
break;
case 94:
//#line 270 "gramatica.y"
{ enParamInvocacion = true; 
                                                                                                  val_peek(1).obj = checkearAlcance(val_peek(1).sval, val_peek(0).ival);
                                                                                                  if (val_peek(1).obj != null) {
                                                                                                    if (!error()) {
                                                                                                      yyval = new ParserVal(new ArbolSintactico("Call", new ArbolSintactico((Simbolo)val_peek(1).obj), null)); 
                                                                                                      yyval.sval = ((Simbolo)val_peek(1).obj).getLexema();
                                                                                                    }
                                                                                                    funcionInvocadaActual = (SimboloFuncion) val_peek(1).obj;
                                                                                                  }
                                                                                                }
break;
case 95:
//#line 282 "gramatica.y"
{ yyval = new ParserVal(new ArbolSintactico("ListaParametros", (ArbolSintactico)((ArbolSintactico[])val_peek(0).obj)[0], null) ); 
                                                                              for (int i = 1; i < ((ArbolSintactico[])val_peek(0).obj).length; i++) {
                                                                                ((ArbolSintactico)yyval.obj).setUltimoDerecho( new ArbolSintactico("ListaParametros", ((ArbolSintactico)((ArbolSintactico[])val_peek(0).obj)[i]), null) );
                                                                              }
                                                                            }
break;
case 96:
//#line 289 "gramatica.y"
{ yyval = val_peek(2); ((ArbolSintactico[])yyval.obj)[funcionInvocadaActual.getPosParametro(val_peek(0).sval)] = (ArbolSintactico)val_peek(0).obj; }
break;
case 97:
//#line 290 "gramatica.y"
{ yyval = new ParserVal(new ArbolSintactico[funcionInvocadaActual.getCantidadParametros()]);
                                                                              ((ArbolSintactico[])yyval.obj)[funcionInvocadaActual.getPosParametro(val_peek(0).sval)] = (ArbolSintactico)val_peek(0).obj;
                                                                            }
break;
case 98:
//#line 295 "gramatica.y"
{ checkeoParametroDeInvocacion(((Simbolo)val_peek(0).obj).getLexema(), val_peek(0).ival); 
                                                                                                              yyval = new ParserVal( (ArbolSintactico)val_peek(2).obj );
                                                                                                              yyval.sval = ((Simbolo)val_peek(0).obj).getLexema();
                                                                                                            }
break;
case 99:
//#line 299 "gramatica.y"
{ checkeoParametroDeInvocacion(((Simbolo)val_peek(0).obj).getLexema(), val_peek(0).ival);   
                                                                                                              finFunc( val_peek(0), null);
                                                                                                              mapaFunciones.put( ((Simbolo)val_peek(0).obj).getLexema() + ":" + funcionInvocadaActual.getLexema(), (ArbolSintactico)val_peek(3).obj);
                                                                                                              tablaSimbolos.remove(val_peek(5).sval);
                                                                                                            }
break;
case 100:
//#line 304 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta llave de cierre del cuerpo de la función Lambda");}
break;
case 101:
//#line 305 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(2).ival + ": falta llave de apertura del cuerpo de la función Lambda");}
break;
case 102:
//#line 306 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": faltan ambas llaves del cuerpo de la función Lambda");}
break;
case 103:
//#line 307 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": No se especifica el parametro formal al que corresponde el parámetro real");}
break;
case 104:
//#line 308 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": No se especifica el parametro formal al que corresponde el parámetro real");}
break;
case 105:
//#line 311 "gramatica.y"
{ enFunc++; 
                                                String lexema = ((SimboloLong)val_peek(1).obj).getLexema();
                                                tablaSimbolos.put(lexema,new SimboloLong("Long", "Identificador", lexema, 0L));
                                                yyval = new ParserVal(lexema);
                                              }
break;
case 106:
//#line 318 "gramatica.y"
{ System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(1).ival + ": Se detectó una expresión desde una suma");
                                                                      if (!error()) {
                                                                        yyval = new ParserVal(new ArbolSintactico("Suma", (ArbolSintactico)val_peek(2).obj, (ArbolSintactico)val_peek(0).obj));
                                                                      }
                                                                    }
break;
case 107:
//#line 323 "gramatica.y"
{ System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(1).ival + ": Se detectó una expresión desde una resta");
                                                                      if (!error()) { yyval = new ParserVal(new ArbolSintactico("Resta", (ArbolSintactico)val_peek(2).obj, (ArbolSintactico)val_peek(0).obj)); }
                                                                    }
break;
case 108:
//#line 326 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la izquierda de '+'"); }
break;
case 109:
//#line 327 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la derecha de '+'"); }
break;
case 110:
//#line 328 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la izquierda de '-'"); }
break;
case 111:
//#line 329 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la derecha de '-'"); }
break;
case 112:
//#line 330 "gramatica.y"
{ System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó una expresión desde un término");    
                                                                      yyval = val_peek(0);
                                                                    }
break;
case 113:
//#line 335 "gramatica.y"
{ System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(1).ival + ": Se detectó un término desde una multiplicación");
                                                                      if (!error()) { yyval = new ParserVal(new ArbolSintactico("Multiplicacion", (ArbolSintactico)val_peek(2).obj, (ArbolSintactico)val_peek(0).obj)); }
                                                                    }
break;
case 114:
//#line 338 "gramatica.y"
{ System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(1).ival + ": Se detectó un término desde una división");
                                                                      if (!error()) { yyval = new ParserVal(new ArbolSintactico("Division", (ArbolSintactico)val_peek(2).obj, (ArbolSintactico)val_peek(0).obj)); }
                                                                    }
break;
case 115:
//#line 341 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la izquierda de '*'"); }
break;
case 116:
//#line 342 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la derecha de '*'"); }
break;
case 117:
//#line 343 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la izquierda de '/'"); }
break;
case 118:
//#line 344 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la derecha de '/'"); }
break;
case 119:
//#line 345 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(2).ival + ": falta operador entre operandos o falta de comparador en condición"); }
break;
case 120:
//#line 346 "gramatica.y"
{ System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un término desde un factor");
                                                                      yyval = val_peek(0);
                                                                    }
break;
case 121:
//#line 351 "gramatica.y"
{ constantePositiva(val_peek(0)); System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un factor desde una constante");
                                                                      restriccionCteFloat(val_peek(0));
                                                                      if (!error()) { yyval = new ParserVal(new ArbolSintactico((Simbolo)val_peek(0).obj)); }
                                                                    }
break;
case 122:
//#line 355 "gramatica.y"
{ constanteNegativa(val_peek(0)); System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un factor desde una constante negativa");
                                                                      restriccionCteFloat(val_peek(0));
                                                                      if (!error()) { yyval = new ParserVal(new ArbolSintactico((Simbolo)val_peek(0).obj)); }
                                                                    }
break;
case 123:
//#line 359 "gramatica.y"
{ System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un factor desde un identificador");
                                                                      if (!error()) { yyval = new ParserVal(new ArbolSintactico(checkearAlcance(val_peek(0).sval, val_peek(0).ival))); }
                                                                    }
break;
case 124:
//#line 362 "gramatica.y"
{ System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un factor desde una función");}
break;
case 125:
//#line 363 "gramatica.y"
{ constantePositiva(val_peek(1)); 
                                                                      if (enParamInvocacion) {
                                                                        System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(3).ival + ": Se detectó la función Trunc en uno de los parámetros reales de la invocación");
                                                                        yyval = new ParserVal(new ArbolSintactico("Trunc", new ArbolSintactico((Simbolo)val_peek(1).obj), null));
                                                                      }
                                                                      else  
                                                                        erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + val_peek(3).ival + ": la función TRUNC solo puede utilizarse en parámetros reales de invocación de funciones");
                                                                    }
break;
case 126:
//#line 371 "gramatica.y"
{ constanteNegativa(val_peek(1)); 
                                                                      if (enParamInvocacion) {
                                                                        System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(4).ival + ": Se detectó la función Trunc en uno de los parámetros reales de la invocación");
                                                                        yyval = new ParserVal(new ArbolSintactico("Trunc", new ArbolSintactico((Simbolo)val_peek(1).obj), null));
                                                                      }
                                                                      else  
                                                                        erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + val_peek(4).ival + ": la función TRUNC solo puede utilizarse en parámetros reales de invocación de funciones");
                                                                    }
break;
case 127:
//#line 381 "gramatica.y"
{ constantePositiva(val_peek(0)); restriccionCteFloat(val_peek(0)); yyval = val_peek(2); ((ArrayList<Simbolo>)yyval.obj).add((Simbolo)val_peek(0).obj); yyval.ival = val_peek(0).ival; }
break;
case 128:
//#line 382 "gramatica.y"
{ constanteNegativa(val_peek(0)); restriccionCteFloat(val_peek(0)); yyval = val_peek(3); ((ArrayList<Simbolo>)yyval.obj).add((Simbolo)val_peek(0).obj); yyval.ival = val_peek(0).ival; }
break;
case 129:
//#line 383 "gramatica.y"
{ constantePositiva(val_peek(0)); restriccionCteFloat(val_peek(0)); yyval = new ParserVal(new ArrayList<Simbolo>()); ((ArrayList<Simbolo>)yyval.obj).add((Simbolo)val_peek(0).obj); yyval.ival = val_peek(0).ival; }
break;
case 130:
//#line 384 "gramatica.y"
{ constanteNegativa(val_peek(0)); restriccionCteFloat(val_peek(0)); yyval = new ParserVal(new ArrayList<Simbolo>()); ((ArrayList<Simbolo>)yyval.obj).add((Simbolo)val_peek(0).obj); yyval.ival = val_peek(0).ival; }
break;
case 131:
//#line 385 "gramatica.y"
{ constantePositiva(val_peek(0)); restriccionCteFloat(val_peek(0)); erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta ',' entre constantes"); constantePositiva(val_peek(0)); restriccionCteFloat(val_peek(0)); }
break;
case 132:
//#line 386 "gramatica.y"
{ constanteNegativa(val_peek(0)); restriccionCteFloat(val_peek(0)); erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(2).ival + ": falta ',' entre constantes"); constanteNegativa(val_peek(0)); restriccionCteFloat(val_peek(0)); }
break;
//#line 1679 "Parser.java"
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
