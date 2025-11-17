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
   29,   31,   30,   10,   10,   33,   10,   10,   10,   10,
   10,   10,   10,   10,   34,   10,   12,   12,   12,   32,
   32,   32,   32,   35,   35,   36,   36,   36,   36,   36,
   36,   15,   15,   16,   16,   24,   24,   23,   23,   19,
   19,   37,   37,   37,   37,   37,   37,   40,   11,   41,
   13,   42,   13,   43,   13,   44,   13,   45,   13,   38,
   39,   46,   46,   47,   47,   47,   47,   47,   47,   47,
   22,   22,   22,   22,   22,   22,   22,   48,   48,   48,
   48,   48,   48,   48,   48,   49,   49,   49,   49,   49,
   49,   25,   25,   25,   25,   25,   25,
};
final static short yylen[] = {                            2,
    0,    0,    5,    2,    3,    2,    2,    2,    1,    2,
    1,    2,    2,    1,    1,    3,    2,    2,    1,    1,
    1,    1,    1,    1,    1,    2,    0,    0,    0,   11,
    1,    4,    3,    3,    3,    3,    2,    2,    0,    2,
    1,    0,    2,    6,    4,    0,    5,    5,    3,    5,
    5,    3,    4,    3,    0,    4,    4,    4,    3,    3,
    2,    2,    1,    3,    1,    1,    1,    1,    1,    1,
    1,    1,    1,    3,    1,    3,    1,    1,    3,    3,
    1,    3,    2,    2,    1,    2,    1,    0,    4,    0,
    4,    0,    4,    0,    5,    0,    7,    0,    8,    2,
    1,    3,    1,    3,    9,    8,    8,    7,    8,    2,
    3,    3,    3,    3,    3,    3,    1,    3,    3,    3,
    3,    3,    3,    3,    1,    1,    2,    1,    1,    4,
    5,    3,    4,    1,    2,    2,    3,
};
final static short yydefred[] = {                         0,
    0,    0,   72,    0,    0,    0,   73,    0,    0,    0,
    0,   11,    0,    0,   20,   21,   22,   23,   24,   25,
    0,    0,    0,   39,    0,    0,    0,   78,  126,    0,
    0,    0,  129,    0,    0,    0,    0,    0,    0,  125,
    0,    0,    0,    6,    0,    4,    8,   10,   12,   13,
    0,    0,   27,    0,  100,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,  103,    2,
    0,    0,    0,    0,    0,    0,  127,   68,   69,   70,
   71,   66,   67,    0,    0,    0,    0,   52,    0,   61,
    0,    0,    0,   33,    0,   54,    0,   59,    0,    5,
    0,    0,    0,   79,  134,    0,    0,    0,    0,   55,
    0,    0,   40,   92,    0,    0,    0,    0,   90,   88,
    0,    0,    0,    0,    0,  120,  122,    0,    0,   60,
    0,    0,    0,    0,    0,   53,    0,   39,    0,   45,
  124,  121,  118,  123,  119,   32,   58,   57,   74,    0,
  135,  136,    0,    0,   46,   56,   38,    0,    0,   37,
   93,    0,    0,    0,   94,  104,   91,   89,  102,    3,
  130,    0,   51,   43,   50,    0,   87,    0,    0,    0,
   81,  132,    0,  137,   47,   17,   36,    0,    0,    0,
    0,   95,  131,   44,   86,    0,   83,    0,    0,  133,
   16,   96,    0,    0,    0,   82,   80,   29,   97,   98,
    0,    0,    0,    0,   99,    0,    0,  108,    0,    0,
  106,    0,  107,    0,  105,   30,
};
final static short yydgoto[] = {                          9,
   10,   26,  122,   11,   12,   13,   14,  159,   15,   16,
   33,   18,   19,   20,   21,   52,   53,  102,  180,  199,
  214,   34,   35,   23,  107,  113,   59,   60,   24,  137,
  138,   36,  185,  156,   37,   86,  181,   38,   67,  168,
  167,  161,  192,  209,  215,   68,   69,   39,   40,
};
final static short yysindex[] = {                       385,
    0,  -34,    0,   -2, -260,   11,    0,  638,    0, -201,
  662,    0,   39,   45,    0,    0,    0,    0,    0,    0,
 -199,  -28,  -30,    0,  131,  429,   36,    0,    0,   66,
  207, -148,    0,  444,   31,  -99,   77,  165,  -21,    0,
  185,  -34,  173,    0,  677,    0,    0,    0,    0,    0,
    0,   81,    0,  207,    0, -125,   -5, -121,  124,  602,
   97,  100, -219, -116,  -38,   34,  103,  104,    0,    0,
  211,  211,  109,  109,   17,  110,    0,    0,    0,    0,
    0,    0,    0,  215,  218,  207, -109,    0,  -96,    0,
  109,  238,  241,    0,   58,    0,  116,    0,   83,    0,
  -98,  128,   16,    0,    0,  -88,  -43,  127,  -34,    0,
  679,  118,    0,    0,   20,  -82,  137,  -78,    0,    0,
  165,  -95,  -14,  -21,  -21,    0,    0,  140,  -73,    0,
  -14,  -21,  -14,  -21,   16,    0,  -75,    0,  -72,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0, -221,
    0,    0,   24,  -69,    0,    0,    0,  133,  763,    0,
    0,  152,  -58,  161,    0,    0,    0,    0,    0,    0,
    0,  163,    0,    0,    0,  -56,    0, -237,  -50,  164,
    0,    0,  -49,    0,    0,    0,    0,  154,  170,  191,
  623,    0,    0,    0,    0,  -17,    0, -221,  216,    0,
    0,    0,  221,  502,  431,    0,    0,    0,    0,    0,
  733,   22,   13,  174,    0,   41,   30,    0,   48,  770,
    0,   51,    0,  694,    0,    0,
};
final static short yyrindex[] = {                         0,
  151,    0,    0,    0,  644,    0,    0,    0,    0,    0,
   43,    0,  541,  617,    0,    0,    0,    0,    0,    0,
    0,   -1,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,  260,  -18,  714,  455,    0,    4,    0,
    0,    0,    0,    0,   53,    0,    0,    0,    0,    0,
  -40,  596,    0,    0,    0,    0,    0,    0,    0,    0,
  141,    0,    0,    0,    0,  156,    0,  266,    0,    0,
    0,    0,    0,    0,    0,  488,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,  716,    0,  400,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,  509,    0,    0,    0,  523,   12,    0,    0,
    0,    0,    0,    0,    0,    0,  307,   47,    0,    0,
    0,    0,    0,   27,   49,    0,    0,    0,    0,    0,
   71,   96,  333,  355,  282,    0,    0,    0,  716,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  735,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,  555,    0,    0,   52,  283,
    0,    0,    0,    0,    0,    0,    0,  753,  390,    0,
    0,    0,    0,    0,    0,   76,    0,    0,    0,    0,
    0,    0,  407,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  105,    0,    0,    0,    0,
};
final static short yygindex[] = {                         0,
  301,    0,    0,    5,   -8,  -44,    0, -182,    0,    0,
  625,    0,    0,    0,  -55,    0,    0,    0,    0,    0,
    0,   59,  515,    0,    0,    0,  -19,    0,    0,  190,
    0,  -27,    0,    0,  311,    0,  147,  630,    0,    0,
    0,    0,    0,    0,    0,    0,  225,   50,  -39,
};
final static int YYTABLESIZE=1046;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         31,
  153,  154,   48,   75,   85,   31,   84,  116,  205,   42,
   32,   55,   45,   58,   96,  112,   89,   56,   75,  195,
   92,  211,  128,  128,  128,   93,  128,   73,  128,    3,
   57,  110,   74,  126,  127,  177,   48,   41,    7,  106,
  128,  128,   77,  128,  117,    3,  117,    3,  117,  178,
   43,  141,  143,  145,    7,   76,    7,   51,   85,   77,
   84,  129,  117,  117,  163,  117,  158,  115,  183,  115,
   55,  115,   76,   55,  119,   46,   56,   73,   72,   56,
   71,  155,   74,   65,   75,  115,  115,  110,  115,  113,
  110,  113,   85,  113,  179,   85,   65,   49,  146,   95,
   85,   99,   84,   50,  128,   75,  128,  113,  113,   77,
  113,  116,  103,  116,  188,  116,   84,   90,  174,   84,
  124,  125,  196,  148,  101,   85,  117,   84,  117,  116,
  116,  104,  116,  132,  134,   28,  112,  114,  112,  115,
  112,  117,  179,  120,  135,  109,  158,  121,  109,  115,
  130,  115,  136,   32,  112,  112,  147,  112,  149,  158,
  188,   87,   88,   31,  139,  140,  188,  150,   32,  151,
   63,  113,   56,  113,  164,   64,  160,  165,  166,   65,
  171,  170,  126,  126,  172,  126,  173,  126,  184,  175,
   78,  186,  189,  116,   78,  116,   78,  128,  128,  190,
  128,  191,  128,  193,   63,  194,  197,  198,  200,   32,
  202,   78,  201,   98,  152,   48,   75,   32,  112,   75,
  112,   27,   28,   29,  224,   94,   75,   75,   75,   32,
   54,  203,   75,  118,   91,   75,   75,  128,  128,  206,
   30,  128,  128,  128,  128,  128,  128,  128,  128,  128,
  128,   32,  105,  128,  128,   32,  208,  128,  128,   32,
  117,  210,   32,  117,  117,  117,  117,  117,  117,  117,
  117,  117,  117,    1,  128,  117,  117,  162,  218,  117,
  117,  182,   32,  115,  219,   32,  115,  115,  115,  115,
  115,  115,  115,  115,  115,  115,  220,  221,  115,  115,
   65,  222,  115,  115,  223,  113,  101,  225,  113,  113,
  113,  113,  113,  113,  113,  113,  113,  113,   65,    9,
  113,  113,   64,   28,  113,  113,   70,  116,  176,    7,
  116,  116,  116,  116,  116,  116,  116,  116,  116,  116,
   64,   76,  116,  116,  207,  169,  116,  116,  127,  127,
    0,  127,  112,  127,    0,  112,  112,  112,  112,  112,
  112,  112,  112,  112,  112,   28,   29,  112,  112,    0,
    0,  112,  112,  114,    0,  114,    0,  114,    0,   27,
   28,   29,   65,   30,   65,    0,   27,   28,   61,    0,
    0,  114,  114,  109,  114,  111,  126,  111,   30,  111,
    0,    0,    0,    0,   64,   62,   64,    1,    0,   78,
    1,  128,  126,  111,  111,    0,  111,    1,    1,    1,
   27,   28,   29,    1,    0,    0,    1,  128,   27,   28,
   29,  130,  130,    0,  130,    0,  130,    0,    0,   30,
   27,   28,   29,    0,    0,    0,   97,   30,  131,  131,
    0,  131,    0,  131,    0,  114,    0,  114,   49,   30,
    0,    0,   27,   28,   29,    0,  123,   28,   29,    0,
  131,   28,   29,  133,   28,   29,    0,  111,    0,  111,
    0,   30,    0,    0,    0,   30,   85,    0,   84,   30,
    0,    0,   30,  142,   28,   29,  144,   28,   29,    0,
    0,    0,    0,   83,    0,   82,    0,    8,    0,    0,
    0,    0,   30,   63,   22,   30,   65,    0,    0,   65,
   65,   65,   22,    0,   49,   22,   65,   65,   65,    0,
    0,   65,   65,    0,    0,   65,   65,    0,   64,   66,
   22,   64,   64,   64,    0,    0,   62,    0,   64,   64,
   64,    8,    0,   64,   64,  213,    0,   64,   64,   22,
    0,    0,  127,    0,    0,    0,    0,   34,    0,    0,
    0,    0,  108,    0,   22,    0,    0,   63,  127,   63,
    0,   35,    0,    0,    0,    0,    0,    0,    0,  114,
    0,    0,  114,  114,  114,  114,  114,  114,  114,  114,
  114,  114,    0,    0,  114,  114,    0,    0,  114,  114,
   62,  111,   62,   48,  111,  111,  111,  111,  111,  111,
  111,  111,  111,  111,   17,   22,  111,  111,    0,   25,
  111,  111,   17,   34,    0,   17,    0,   25,    0,    0,
   25,    1,    0,    0,    2,  130,    0,   35,    0,    0,
   17,    3,    4,    5,   26,   25,   49,    6,    0,   49,
    7,  130,  131,    0,    0,   14,   49,   49,   49,   17,
    0,   49,   49,   22,   25,   49,   49,    0,  131,   48,
    0,    0,    0,    0,   17,   28,    0,   28,    2,   25,
    2,    0,    0,    0,    0,    3,    4,    5,    4,    5,
    0,    6,  212,    6,    7,   22,   78,   79,   80,   81,
    0,   63,    0,    0,   63,   63,   63,    0,   22,   22,
   26,   63,   63,   63,  111,   22,   63,   63,    0,    0,
   63,   63,    0,    0,   22,   17,    0,    0,   22,    0,
   25,   15,    0,    0,   62,  204,    0,   62,   62,   62,
    0,    0,    0,    0,   62,   62,   62,    0,   28,   62,
   62,    2,   44,   62,   62,   34,   41,    0,   34,    4,
    5,    0,    0,    0,    6,   34,   34,   34,    0,   35,
   34,   34,   35,   17,   34,   34,   47,    0,   25,   35,
   35,   35,    0,    0,   35,   35,    0,   14,   35,   35,
   14,  100,    0,  157,    0,    0,    0,   14,   14,   14,
    0,   48,    0,   14,   48,   17,   14,   14,  226,    0,
   25,   48,   48,   48,    0,    0,   48,   48,   17,   17,
   48,   48,    0,   25,   25,   17,   39,    0,   42,    0,
   25,    0,    0,    0,   17,    0,    0,    0,   17,   25,
    0,    0,   26,   25,    0,   26,    0,  217,   28,   19,
    0,    2,   26,   26,   26,    0,    0,    0,   26,    4,
    5,   26,   26,   15,    6,    0,   15,   18,    0,   28,
    0,    0,    2,   15,   15,   15,    0,  187,    0,   15,
    4,    5,   15,   15,   28,    6,    0,    2,    0,    0,
   41,    0,    0,   41,    3,    4,    5,    0,    0,    0,
    6,   41,   41,    7,    0,    0,   41,    0,   28,    0,
    0,    2,    0,    0,    0,    0,    0,    0,    3,    4,
    5,    0,    0,   28,    6,   28,    2,    7,    2,    0,
    0,    0,    0,    3,    4,    5,    4,    5,    0,    6,
   28,    6,    7,    2,    0,    0,    0,    0,    0,    0,
    3,    4,    5,    0,    0,    0,    6,    0,    0,    7,
   39,    0,   42,   39,    0,   42,    0,    0,    0,    0,
    0,   39,   39,   42,   42,    0,   39,    0,   42,   28,
    0,   19,    2,    0,   19,    0,    0,    0,    0,    0,
    4,    5,   19,   19,  216,    6,   19,   19,    0,   18,
    0,    0,   18,    0,    0,    0,    0,    0,    0,   28,
   18,   18,    2,    0,   18,   18,   28,    0,    0,    2,
    4,    5,    0,    0,    0,    6,    3,    4,    5,    0,
    0,    0,    6,    0,    0,    7,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         40,
   44,   45,   11,   44,   43,   40,   45,   63,  191,  270,
   45,   40,    8,   44,   42,   60,   36,   46,   59,  257,
   42,  204,   41,   42,   43,   47,   45,   42,   47,  267,
   61,   59,   47,   73,   74,  257,   45,   40,  276,   45,
   59,   60,   44,   62,   41,  267,   43,  267,   45,  271,
   40,   91,   92,   93,  276,   44,  276,  257,   43,   61,
   45,   45,   59,   60,   45,   62,  111,   41,   45,   43,
   40,   45,   61,   40,   41,  277,   46,   42,   43,   46,
   45,  109,   47,   25,  125,   59,   60,   41,   62,   41,
   44,   43,   41,   45,  150,   44,   38,   59,   41,   41,
   43,   43,   45,   59,  123,   40,  125,   59,   60,  258,
   62,   41,   54,   43,  159,   45,   41,   41,  138,   44,
   71,   72,  178,   41,   44,   43,  123,   45,  125,   59,
   60,  257,   62,   84,   85,  257,   41,   41,   43,   40,
   45,  258,  198,   41,   86,   41,  191,   44,   44,  123,
   41,  125,  262,   45,   59,   60,   41,   62,  257,  204,
  205,  261,  262,   40,  261,  262,  211,   40,   45,  258,
   40,  123,   46,  125,  257,   45,   59,   41,  257,  121,
   41,  277,   42,   43,  258,   45,  262,   47,  258,  262,
   40,   59,   41,  123,   44,  125,   46,   42,   43,  258,
   45,   41,   47,   41,   40,  262,  257,   44,  258,   45,
   41,   61,   59,   41,  258,  224,  257,   45,  123,  260,
  125,  256,  257,  258,  220,   41,  267,  268,  269,   45,
  259,   41,  273,  272,  256,  276,  277,  256,  257,  257,
  275,  260,  261,  262,  263,  264,  265,  266,  267,  268,
  269,   45,  258,  272,  273,   45,   41,  276,  277,   45,
  257,   41,   45,  260,  261,  262,  263,  264,  265,  266,
  267,  268,  269,  123,  258,  272,  273,  258,  257,  276,
  277,  258,   45,  257,  272,   45,  260,  261,  262,  263,
  264,  265,  266,  267,  268,  269,  123,  257,  272,  273,
   41,  272,  276,  277,  257,  257,   41,  257,  260,  261,
  262,  263,  264,  265,  266,  267,  268,  269,   59,  277,
  272,  273,   41,   41,  276,  277,   26,  257,  139,  277,
  260,  261,  262,  263,  264,  265,  266,  267,  268,  269,
   59,   31,  272,  273,  198,  121,  276,  277,   42,   43,
   -1,   45,  257,   47,   -1,  260,  261,  262,  263,  264,
  265,  266,  267,  268,  269,  257,  258,  272,  273,   -1,
   -1,  276,  277,   41,   -1,   43,   -1,   45,   -1,  256,
  257,  258,  123,  275,  125,   -1,  256,  257,  258,   -1,
   -1,   59,   60,  270,   62,   41,  256,   43,  275,   45,
   -1,   -1,   -1,   -1,  123,  275,  125,  257,   -1,  259,
  260,  256,  272,   59,   60,   -1,   62,  267,  268,  269,
  256,  257,  258,  273,   -1,   -1,  276,  272,  256,  257,
  258,   42,   43,   -1,   45,   -1,   47,   -1,   -1,  275,
  256,  257,  258,   -1,   -1,   -1,  274,  275,   42,   43,
   -1,   45,   -1,   47,   -1,  123,   -1,  125,   59,  275,
   -1,   -1,  256,  257,  258,   -1,  256,  257,  258,   -1,
  256,  257,  258,  256,  257,  258,   -1,  123,   -1,  125,
   -1,  275,   -1,   -1,   -1,  275,   43,   -1,   45,  275,
   -1,   -1,  275,  256,  257,  258,  256,  257,  258,   -1,
   -1,   -1,   -1,   60,   -1,   62,   -1,  123,   -1,   -1,
   -1,   -1,  275,   59,    0,  275,  257,   -1,   -1,  260,
  261,  262,    8,   -1,  125,   11,  267,  268,  269,   -1,
   -1,  272,  273,   -1,   -1,  276,  277,   -1,  257,   25,
   26,  260,  261,  262,   -1,   -1,   59,   -1,  267,  268,
  269,  123,   -1,  272,  273,  125,   -1,  276,  277,   45,
   -1,   -1,  256,   -1,   -1,   -1,   -1,   59,   -1,   -1,
   -1,   -1,   58,   -1,   60,   -1,   -1,  123,  272,  125,
   -1,   59,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  257,
   -1,   -1,  260,  261,  262,  263,  264,  265,  266,  267,
  268,  269,   -1,   -1,  272,  273,   -1,   -1,  276,  277,
  123,  257,  125,   59,  260,  261,  262,  263,  264,  265,
  266,  267,  268,  269,    0,  111,  272,  273,   -1,    0,
  276,  277,    8,  125,   -1,   11,   -1,    8,   -1,   -1,
   11,  257,   -1,   -1,  260,  256,   -1,  125,   -1,   -1,
   26,  267,  268,  269,   59,   26,  257,  273,   -1,  260,
  276,  272,  256,   -1,   -1,  125,  267,  268,  269,   45,
   -1,  272,  273,  159,   45,  276,  277,   -1,  272,  125,
   -1,   -1,   -1,   -1,   60,  257,   -1,  257,  260,   60,
  260,   -1,   -1,   -1,   -1,  267,  268,  269,  268,  269,
   -1,  273,  272,  273,  276,  191,  263,  264,  265,  266,
   -1,  257,   -1,   -1,  260,  261,  262,   -1,  204,  205,
  125,  267,  268,  269,  123,  211,  272,  273,   -1,   -1,
  276,  277,   -1,   -1,  220,  111,   -1,   -1,  224,   -1,
  111,  125,   -1,   -1,  257,  123,   -1,  260,  261,  262,
   -1,   -1,   -1,   -1,  267,  268,  269,   -1,  257,  272,
  273,  260,  125,  276,  277,  257,  123,   -1,  260,  268,
  269,   -1,   -1,   -1,  273,  267,  268,  269,   -1,  257,
  272,  273,  260,  159,  276,  277,  125,   -1,  159,  267,
  268,  269,   -1,   -1,  272,  273,   -1,  257,  276,  277,
  260,  125,   -1,  125,   -1,   -1,   -1,  267,  268,  269,
   -1,  257,   -1,  273,  260,  191,  276,  277,  125,   -1,
  191,  267,  268,  269,   -1,   -1,  272,  273,  204,  205,
  276,  277,   -1,  204,  205,  211,  123,   -1,  123,   -1,
  211,   -1,   -1,   -1,  220,   -1,   -1,   -1,  224,  220,
   -1,   -1,  257,  224,   -1,  260,   -1,  125,  257,  125,
   -1,  260,  267,  268,  269,   -1,   -1,   -1,  273,  268,
  269,  276,  277,  257,  273,   -1,  260,  125,   -1,  257,
   -1,   -1,  260,  267,  268,  269,   -1,  125,   -1,  273,
  268,  269,  276,  277,  257,  273,   -1,  260,   -1,   -1,
  257,   -1,   -1,  260,  267,  268,  269,   -1,   -1,   -1,
  273,  268,  269,  276,   -1,   -1,  273,   -1,  257,   -1,
   -1,  260,   -1,   -1,   -1,   -1,   -1,   -1,  267,  268,
  269,   -1,   -1,  257,  273,  257,  260,  276,  260,   -1,
   -1,   -1,   -1,  267,  268,  269,  268,  269,   -1,  273,
  257,  273,  276,  260,   -1,   -1,   -1,   -1,   -1,   -1,
  267,  268,  269,   -1,   -1,   -1,  273,   -1,   -1,  276,
  257,   -1,  257,  260,   -1,  260,   -1,   -1,   -1,   -1,
   -1,  268,  269,  268,  269,   -1,  273,   -1,  273,  257,
   -1,  257,  260,   -1,  260,   -1,   -1,   -1,   -1,   -1,
  268,  269,  268,  269,  272,  273,  272,  273,   -1,  257,
   -1,   -1,  260,   -1,   -1,   -1,   -1,   -1,   -1,  257,
  268,  269,  260,   -1,  272,  273,  257,   -1,   -1,  260,
  268,  269,   -1,   -1,   -1,  273,  267,  268,  269,   -1,
   -1,   -1,  273,   -1,   -1,  276,
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
"control : IF condicion bloque_control ELSE bloque_control_else ENDIF",
"control : IF condicion bloque_control ENDIF",
"$$8 :",
"control : inicio_do bloque_control UNTIL condicion $$8",
"control : IF condicion bloque_control ELSE bloque_control_else",
"control : IF condicion bloque_control",
"control : IF condicion bloque_control ELSE ENDIF",
"control : IF condicion ELSE bloque_control_else ENDIF",
"control : IF condicion ENDIF",
"control : IF condicion ELSE ENDIF",
"control : DO UNTIL condicion",
"$$9 :",
"control : inicio_do bloque_control condicion $$9",
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
"listaparametros : listaparametros ',' parametro",
"listaparametros : parametro",
"parametro : CV tipo ID",
"parametro : tipo ID",
"parametro : CV tipo",
"parametro : tipo",
"parametro : CV ID",
"parametro : ID",
"$$10 :",
"funcion : cabecera_funcion argumentos_funcion ')' $$10",
"$$11 :",
"funcion_lambda : cabecera_funcion identificador ')' $$11",
"$$12 :",
"funcion_lambda : cabecera_funcion CTE ')' $$12",
"$$13 :",
"funcion_lambda : cabecera_funcion '-' CTE ')' $$13",
"$$14 :",
"funcion_lambda : cabecera_funcion TRUNC '(' CTE ')' ')' $$14",
"$$15 :",
"funcion_lambda : cabecera_funcion TRUNC '(' '-' CTE ')' ')' $$15",
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

//#line 252 "gramatica.y"

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
      simbolo =  new SimboloLong(258, "Long", "Constante", "-" + ((SimboloLong)valCte.obj).getLexema(), -((SimboloLong)valCte.obj).getValor());
      tablaSimbolos.put("-" +((SimboloLong)valCte.obj).getLexema(), simbolo);
    } else {
      simbolo = tablaSimbolos.get("-" + ((SimboloLong)valCte.obj).getLexema());
    }
    valCte.obj = simbolo;
  } else {
        Simbolo simbolo;
        if (!tablaSimbolos.containsKey("-" +((SimboloCteFloat)valCte.obj).getLexema())) {
          simbolo =  new SimboloCteFloat(258, "Float", "Constante", "-" + ((SimboloCteFloat)valCte.obj).getLexema(), -((SimboloCteFloat)valCte.obj).getValor());
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
        simbolo =  new SimboloLong(258, "Long", "Constante", "2147483647L", 2147483647L);
        tablaSimbolos.put("2147483647L", simbolo);
      } else {
        simbolo = tablaSimbolos.get("2147483647L");
      }
      valCte.obj = simbolo;
      System.out.println("||================ ANALIZADOR SINTÁCTICO: Warning en línea " + valCte.ival + ": el valor de la constante entera es mayor al máximo permitido, se asigna el valor 2147483647");
    }
}

private void chequeoSentenciaReturn(ParserVal yyval) {
  if (enFunc == 0) {
    erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + yyval.ival + ": sentencia return fuera de una función");
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
      erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + yyval.ival + ": Se han detectado multiples sentencias de retorno en el IF");
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
    erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + yyval.ival + ": La función no tiene sentencia return en todos sus caminos de ejecución");
  } else if (returnsFunc < 0) {
    erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + yyval.ival + ": Se han detectado multiples sentencias de retorno en la función");
  }
  enFunc--;
  returnsFunc = 1;
  huboIfEnFunc = false;

  // Creación del símbolo de función
  tablaSimbolos.remove(simboloID.getLexema());
  Simbolo nuevoSimbolo = new SimboloFuncion(257, stackTipoFuncActual.pop(), "Funcion", simboloID.getLexema(), stackParametrosActuales.pop());
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
    erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + yyval.ival + ": Se ha intentado invocar una función lambda perteneciente a otro ámbito");
  }
}
//#line 759 "Parser.java"
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
case 4:
//#line 22 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el nombre del programa");}
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
case 14:
//#line 41 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta ; al final de la sentencia ejecutable");}
break;
case 15:
//#line 42 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta ; al final de la declaración");}
break;
case 18:
//#line 47 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta ; al final de la sentencia ejecutable");}
break;
case 19:
//#line 48 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta ; al final de la sentencia ejecutable");}
break;
case 23:
//#line 54 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un print");}
break;
case 24:
//#line 55 "gramatica.y"
{if(enFunc==0) erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": Invocacion de funcion lambda fuera de una función");}
break;
case 25:
//#line 56 "gramatica.y"
{chequeoSentenciaReturn(val_peek(0));}
break;
case 26:
//#line 59 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó una declaración de variables");}
break;
case 27:
//#line 60 "gramatica.y"
{stringAmbitos.append(":"+((Simbolo)val_peek(0).obj).getLexema());}
break;
case 28:
//#line 60 "gramatica.y"
{stackParametrosActuales.push(parametrosActuales); parametrosActuales = new ArrayList<>();}
break;
case 29:
//#line 60 "gramatica.y"
{enFunc++;}
break;
case 30:
//#line 60 "gramatica.y"
{finFunc( val_peek(0), ((Simbolo)val_peek(9).obj) ); quitarAmbito(); renombrarVariable(val_peek(9)); System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó una declaración de función");}
break;
case 31:
//#line 63 "gramatica.y"
{yyval = new ParserVal((SimboloLong)val_peek(0).obj);}
break;
case 32:
//#line 66 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(1).ival + ": Se detectó una sentencia return");}
break;
case 33:
//#line 67 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta de argumento en sentencia return");}
break;
case 34:
//#line 70 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó una asignación simple");}
break;
case 35:
//#line 71 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó una asignación múltiple");}
break;
case 39:
//#line 79 "gramatica.y"
{inicioIf();}
break;
case 40:
//#line 79 "gramatica.y"
{finIf(val_peek(0));}
break;
case 41:
//#line 82 "gramatica.y"
{enDo=true;}
break;
case 42:
//#line 85 "gramatica.y"
{sinRetIf.push(sinRet);}
break;
case 43:
//#line 85 "gramatica.y"
{if(sinRetIf.pop()) {sinRet=true;};}
break;
case 44:
//#line 88 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un bloque IF-ELSE");}
break;
case 45:
//#line 89 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un bloque IF");}
break;
case 46:
//#line 90 "gramatica.y"
{enDo=false;}
break;
case 47:
//#line 90 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(1).ival + ": Se detectó un bloque DO UNTIL");}
break;
case 48:
//#line 92 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta la palabra ENDIF al final del bloque IF-ELSE");}
break;
case 49:
//#line 93 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta la palabra ENDIF al final del bloque IF");}
break;
case 50:
//#line 94 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el cuerpo en el bloque ELSE");}
break;
case 51:
//#line 95 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(3).ival + ": falta el cuerpo en el bloque IF");}
break;
case 52:
//#line 96 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el cuerpo en el bloque IF");}
break;
case 53:
//#line 97 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el cuerpo en los bloques IF y ELSE");}
break;
case 54:
//#line 99 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(2).ival + ": falta de cuerpo en iteracion");}
break;
case 55:
//#line 100 "gramatica.y"
{enDo=false;}
break;
case 56:
//#line 100 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(2).ival + ": falta la palabra UNTIL en la iteracion");}
break;
case 59:
//#line 105 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta de argumento en sentencia print");}
break;
case 61:
//#line 109 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta el paréntesis de apertura de la condición");}
break;
case 62:
//#line 110 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta el paréntesis de cierre de la condición");}
break;
case 63:
//#line 111 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": faltan ambos paréntesis de la condición");}
break;
case 65:
//#line 115 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": La condición es incompleta");}
break;
case 72:
//#line 126 "gramatica.y"
{stackTipoFuncActual.push("Long");}
break;
case 73:
//#line 127 "gramatica.y"
{stackTipoFuncActual.push("Lambda");}
break;
case 74:
//#line 130 "gramatica.y"
{renombrarVariable(val_peek(0));}
break;
case 75:
//#line 131 "gramatica.y"
{renombrarVariable(val_peek(0));}
break;
case 78:
//#line 140 "gramatica.y"
{yyval = new ParserVal( ((SimboloLong)val_peek(0).obj).getLexema() );}
break;
case 79:
//#line 141 "gramatica.y"
{yyval = new ParserVal( val_peek(2).sval + "." + ((SimboloLong)val_peek(0).obj).getLexema() );}
break;
case 82:
//#line 148 "gramatica.y"
{renombrarVariable(val_peek(0)); parametrosActuales.add(new Parametro(((Simbolo)val_peek(0).obj), "CV"));}
break;
case 83:
//#line 149 "gramatica.y"
{renombrarVariable(val_peek(0)); parametrosActuales.add(new Parametro(((Simbolo)val_peek(0).obj), "CVR"));}
break;
case 84:
//#line 150 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el nombre del parámetro");}
break;
case 85:
//#line 151 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta el nombre del parámetro");}
break;
case 86:
//#line 152 "gramatica.y"
{renombrarVariable(val_peek(0)); parametrosActuales.add(new Parametro(((Simbolo)val_peek(0).obj), "CV")); erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el tipo del parámetro");}
break;
case 87:
//#line 153 "gramatica.y"
{renombrarVariable(val_peek(0)); parametrosActuales.add(new Parametro(((Simbolo)val_peek(0).obj), "CVR")); erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta el tipo del parámetro");}
break;
case 88:
//#line 156 "gramatica.y"
{enParamInvocacion = false;}
break;
case 89:
//#line 156 "gramatica.y"
{ System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(1).ival + ": Se detectó la invocación de una función");}
break;
case 90:
//#line 159 "gramatica.y"
{enParamInvocacion = false;}
break;
case 91:
//#line 159 "gramatica.y"
{ chequeo_anid_lambda(val_peek(3).sval, val_peek(1)); System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(1).ival + ": Se detectó la invocación de una función lambda");}
break;
case 92:
//#line 160 "gramatica.y"
{enParamInvocacion = false;}
break;
case 93:
//#line 160 "gramatica.y"
{ chequeo_anid_lambda(val_peek(3).sval, val_peek(1)) ; constantePositiva(val_peek(2)); System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(1).ival + ": Se detectó la invocación de una función lambda");}
break;
case 94:
//#line 161 "gramatica.y"
{enParamInvocacion = false;}
break;
case 95:
//#line 161 "gramatica.y"
{ chequeo_anid_lambda(val_peek(4).sval, val_peek(1)) ; constanteNegativa(val_peek(2)); System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(1).ival + ": Se detectó la invocación de una función lambda");}
break;
case 96:
//#line 162 "gramatica.y"
{enParamInvocacion = false;}
break;
case 97:
//#line 162 "gramatica.y"
{ constantePositiva(val_peek(3)); System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(1).ival + ": Se detectó la invocación de una función lambda");}
break;
case 98:
//#line 163 "gramatica.y"
{enParamInvocacion = false;}
break;
case 99:
//#line 163 "gramatica.y"
{ constanteNegativa(val_peek(3)); System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(1).ival + ": Se detectó la invocación de una función lambda");}
break;
case 100:
//#line 166 "gramatica.y"
{enParamInvocacion = true; yyval = new ParserVal( val_peek(1).sval );}
break;
case 106:
//#line 182 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(2).ival + ": falta llave de cierre del cuerpo de la función Lambda");}
break;
case 107:
//#line 183 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(4).ival + ": falta llave de apertura del cuerpo de la función Lambda");}
break;
case 108:
//#line 184 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(3).ival + ": faltan ambas llaves del cuerpo de la función Lambda");}
break;
case 109:
//#line 185 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": No se especifica el parametro formal al que corresponde el parámetro real");}
break;
case 110:
//#line 186 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": No se especifica el parametro formal al que corresponde el parámetro real");}
break;
case 111:
//#line 190 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(1).ival + ": Se detectó una expresión desde una suma");}
break;
case 112:
//#line 191 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(1).ival + ": Se detectó una expresión desde una resta");}
break;
case 113:
//#line 192 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la izquierda de '+'"); }
break;
case 114:
//#line 193 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la derecha de '+'"); }
break;
case 115:
//#line 194 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la izquierda de '-'"); }
break;
case 116:
//#line 195 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la derecha de '-'"); }
break;
case 117:
//#line 196 "gramatica.y"
{
                                                                      System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó una expresión desde un término");    
                                                                      yyval = val_peek(0);
                                                                      System.out.println("LE ESTOY PASANDO A EXPRESIÓN LA REFERENCIA DE MI NO TERMINAL TERMINO. CATEGORIA: " + ((ArbolSintactico)val_peek(0).obj).getValor());
                                                                    }
break;
case 118:
//#line 203 "gramatica.y"
{
                                                                      System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(1).ival + ": Se detectó un término desde una multiplicación");
                                                                      yyval = new ParserVal(new ArbolSintactico("MULT", (ArbolSintactico)val_peek(2).obj, (ArbolSintactico)val_peek(0).obj));
                                                                      System.out.println("EL VALOR DE MI PESOS PESOS ACTUAL ES : " + ((ArbolSintactico)yyval.obj).getValor());
                                                                      System.out.println("ESTOY CREANDO UNA OPERACIÓN DE MULTIPLICACIÓN. A DERECHA EL FACTOR TIENE EL VALOR : " + ((ArbolSintactico)val_peek(0).obj).getRefSimbolo().getLexema());
                                                                    }
break;
case 119:
//#line 209 "gramatica.y"
{
                                                                      System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(1).ival + ": Se detectó un término desde una división");
                                                                      yyval = new ParserVal(new ArbolSintactico("DIV", (ArbolSintactico)val_peek(2).obj, (ArbolSintactico)val_peek(0).obj));
                                                                      System.out.println("ESTOY CREANDO UNA OPERACIÓN DE DIVISIÓN. A DERECHA EL FACTOR TIENE EL VALOR : " + ((ArbolSintactico)val_peek(0).obj).getRefSimbolo().getLexema());
                                                                    }
break;
case 120:
//#line 214 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la izquierda de '*'"); }
break;
case 121:
//#line 215 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la derecha de '*'"); }
break;
case 122:
//#line 216 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la izquierda de '/'"); }
break;
case 123:
//#line 217 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la derecha de '/'"); }
break;
case 124:
//#line 218 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(2).ival + ": falta operador entre operandos o falta de comparador en condición"); }
break;
case 125:
//#line 219 "gramatica.y"
{
                                                                      System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un término desde un factor");
                                                                      yyval = val_peek(0);
                                                                      System.out.println("LE ESTOY PASANDO A TERMINO LA REFERENCIA DE MI NO TERMINAL FACTOR: " + ((ArbolSintactico)val_peek(0).obj).getRefSimbolo().getLexema());
                                                                    }
break;
case 126:
//#line 226 "gramatica.y"
{ 
                                                                      constantePositiva(val_peek(0)); System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un factor desde una constante");
                                                                      yyval = new ParserVal(new ArbolSintactico((Simbolo)val_peek(0).obj));
                                                                    }
break;
case 127:
//#line 231 "gramatica.y"
{ 
                                                                      constanteNegativa(val_peek(0)); System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un factor desde una constante negativa");
                                                                      yyval = new ParserVal(new ArbolSintactico((Simbolo)val_peek(0).obj));
                                                                    }
break;
case 128:
//#line 236 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un factor desde un identificador");}
break;
case 129:
//#line 237 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un factor desde una función");}
break;
case 130:
//#line 238 "gramatica.y"
{ constantePositiva(val_peek(1)); if (enParamInvocacion) {System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(3).ival + ": Se detectó la función Trunc en uno de los parámetros reales de la invocación");} else erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(3).ival + ": la función TRUNC solo puede utilizarse en parámetros reales de invocación de funciones");}
break;
case 131:
//#line 239 "gramatica.y"
{ constanteNegativa(val_peek(1)); if (enParamInvocacion) {System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(4).ival + ": Se detectó la función Trunc en uno de los parámetros reales de la invocación");} else erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(4).ival + ": la función TRUNC solo puede utilizarse en parámetros reales de invocación de funciones");}
break;
case 132:
//#line 242 "gramatica.y"
{ constantePositiva(val_peek(0)); }
break;
case 133:
//#line 243 "gramatica.y"
{ constanteNegativa(val_peek(0)); }
break;
case 134:
//#line 244 "gramatica.y"
{ constantePositiva(val_peek(0)); }
break;
case 135:
//#line 245 "gramatica.y"
{ constanteNegativa(val_peek(0)); }
break;
case 136:
//#line 246 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta ',' entre constantes"); constantePositiva(val_peek(0)); }
break;
case 137:
//#line 247 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(2).ival + ": falta ',' entre constantes"); constanteNegativa(val_peek(0)); }
break;
//#line 1343 "Parser.java"
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
