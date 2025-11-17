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
    6,    6,    6,    6,    6,    7,   17,   19,   20,    7,
   14,   14,    9,    9,   25,   25,   25,   27,   26,   28,
   30,   29,   10,   10,   32,   10,   10,   10,   10,   10,
   10,   10,   10,   33,   10,   12,   12,   12,   31,   31,
   31,   31,   34,   34,   35,   35,   35,   35,   35,   35,
   15,   15,   16,   16,   23,   23,   22,   22,   18,   18,
   36,   36,   36,   36,   36,   36,   39,   11,   40,   13,
   41,   13,   42,   13,   43,   13,   44,   13,   37,   38,
   45,   45,   46,   46,   46,   46,   46,   46,   46,   21,
   21,   21,   21,   21,   21,   21,   47,   47,   47,   47,
   47,   47,   47,   47,   48,   48,   48,   48,   48,   48,
   24,   24,   24,   24,   24,   24,
};
final static short yylen[] = {                            2,
    0,    0,    5,    2,    3,    2,    2,    2,    1,    2,
    1,    2,    2,    1,    1,    3,    2,    2,    1,    1,
    1,    1,    1,    1,    1,    2,    0,    0,    0,   11,
    4,    3,    3,    3,    3,    2,    2,    0,    2,    1,
    0,    2,    6,    4,    0,    5,    5,    3,    5,    5,
    3,    4,    3,    0,    4,    4,    4,    3,    3,    2,
    2,    1,    3,    1,    1,    1,    1,    1,    1,    1,
    1,    1,    3,    1,    3,    1,    1,    3,    3,    1,
    3,    2,    2,    1,    2,    1,    0,    4,    0,    4,
    0,    4,    0,    5,    0,    7,    0,    8,    2,    1,
    3,    1,    3,    9,    8,    8,    7,    8,    2,    3,
    3,    3,    3,    3,    3,    1,    3,    3,    3,    3,
    3,    3,    3,    1,    1,    2,    1,    1,    4,    5,
    3,    4,    1,    2,    2,    3,
};
final static short yydefred[] = {                         0,
    0,    0,   71,    0,    0,    0,   72,    0,    0,    0,
    0,   11,    0,    0,   20,   21,   22,   23,   24,   25,
    0,    0,    0,   38,    0,   99,    0,    0,    0,  125,
    0,    0,    0,  128,    0,    0,    0,    0,    0,    0,
  124,    0,    0,    0,    6,    0,    4,    8,   10,   12,
   13,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  102,    2,    0,
    0,    0,    0,    0,    0,  126,   67,   68,   69,   70,
   65,   66,    0,    0,    0,    0,   51,    0,   60,    0,
    0,    0,   32,    0,   53,    0,   58,    0,    5,    0,
    0,    0,   78,  133,    0,    0,   77,    0,    0,   54,
    0,    0,   39,   91,    0,    0,    0,    0,   89,   87,
    0,    0,    0,    0,    0,  119,  121,    0,    0,   59,
    0,    0,    0,    0,    0,   52,    0,   38,    0,   44,
  123,  120,  117,  122,  118,   31,   57,   56,    0,   73,
  134,  135,    0,    0,   45,   55,   37,    0,    0,   36,
   92,    0,    0,    0,   93,  103,   90,   88,  101,    3,
  129,    0,   50,   42,   49,    0,   86,    0,    0,    0,
   80,  131,    0,  136,   46,   17,   35,    0,    0,    0,
    0,   94,  130,   43,   85,    0,   82,    0,    0,  132,
   16,   95,    0,    0,    0,   81,   79,   29,   96,   97,
    0,    0,    0,    0,   98,    0,    0,  107,    0,    0,
  105,    0,  106,    0,  104,   30,
};
final static short yydgoto[] = {                          9,
   10,   27,  122,   11,   12,   13,   14,  159,   15,   16,
   34,   18,   19,   20,   21,   53,  100,  180,  199,  214,
   35,   36,   23,  106,  113,   58,   59,   24,  137,  138,
   37,  185,  156,   38,   85,  181,   39,   66,  168,  167,
  161,  192,  209,  215,   67,   68,   40,   41,
};
final static short yysindex[] = {                       425,
   20,  131,    0,   34, -263,   63,    0,  700,    0, -246,
  721,    0,  -24,   23,    0,    0,    0,    0,    0,    0,
 -144,  -34,   -8,    0,  146,    0,  601,   59,   20,    0,
   85,  177, -126,    0,  439,   90, -166,  103,  152,  -33,
    0,  173,  131,  167,    0,  735,    0,    0,    0,    0,
    0,    0,  115,  177,  -94,   -5,  -92,  124,  197,  127,
  130, -237,  -83,  -42,   42,  137,  140,    0,    0,  211,
  211,  -23,  -23,    5,  141,    0,    0,    0,    0,    0,
    0,    0,  217,  233,  177,  -77,    0, -143,    0,  -23,
  237,  240,    0,   83,    0,  149,    0,  138,    0,  147,
  -62,  -11,    0,    0,  -60,  -27,    0,   90,  131,    0,
  250,  142,    0,    0,   16,  -58,  159,  -53,    0,    0,
  152,  -71,   68,  -33,  -33,    0,    0,  168,  -48,    0,
   68,  -33,   68,  -33,  -11,    0,  -51,    0,  -49,    0,
    0,    0,    0,    0,    0,    0,    0,    0, -192,    0,
    0,    0,   17,   -1,    0,    0,    0,  157,  804,    0,
    0,  185,   21,  242,    0,    0,    0,    0,    0,    0,
    0,  245,    0,    0,    0,   35,    0, -176,   41,  258,
    0,    0,   47,    0,    0,    0,    0,  248,  267,  283,
  564,    0,    0,    0,    0,   70,    0, -192,  288,    0,
    0,    0,  289,  -80,  755,    0,    0,    0,    0,    0,
  776,   88,   74,  219,    0,   93,   80,    0,   97, -122,
    0,   98,    0,  753,    0,    0,
};
final static short yyrindex[] = {                         0,
  305,    0,    0,    0,  636,    0,    0,    0,    0,    0,
   94,    0,  665,  686,    0,    0,    0,    0,    0,    0,
    0,   10,    0,    0,    0,    0,    0,    0,  -18,    0,
    0,    0,    0,    0,  260,    4,  643,  454,    0,   27,
    0,    0,    0,    0,    0,  100,    0,    0,    0,    0,
    0,  -40,  650,    0,    0,    0,    0,    0,    0,  -32,
    0,    0,    0,    0,  160,    0,  326,    0,    0,    0,
    0,    0,    0,    0,  488,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,  671,    0,  510,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  532,    0,    0,    0,  571,    0,   36,    0,    0,
    0,    0,    0,    0,    0,    0,  402,  -36,    0,    0,
    0,    0,    0,   49,   71,    0,    0,    0,    0,    0,
   96,  333,  355,  377,  282,    0,    0,    0,  671,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  783,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,  622,    0,    0,   79,  329,
    0,    0,    0,    0,    0,    0,    0,  790,  408,    0,
    0,    0,    0,    0,    0,   99,    0,    0,    0,    0,
    0,    0,  417,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  116,    0,    0,    0,    0,
};
final static short yygindex[] = {                         0,
  352,    0,    0,   12,   -9,  -38,    0, -135,    0,    0,
  625,    0,    0,    0,  -56,    0,    0,    0,    0,    0,
   13,  651,    0,    0,    0,  -21,    0,    0,  247,    0,
  -10,    0,    0,  358,    0,  186,  661,    0,    0,    0,
    0,    0,    0,    0,    0,  270,   78,  -14,
};
final static int YYTABLESIZE=1077;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         27,
   84,   49,   83,   74,  109,  116,   43,  109,   91,  125,
  125,   55,  125,   92,  125,   88,  153,  154,   74,   46,
  112,   33,   77,   77,   77,   77,   77,   77,   77,    3,
   47,   84,   95,   83,   50,   57,   49,   64,    7,  105,
   77,   77,   77,   77,  127,  127,  127,  110,  127,  129,
  127,   64,   56,   76,   94,  205,   98,  126,  127,   26,
  163,  183,  127,  127,  177,  127,  102,  116,  211,  116,
   76,  116,  158,   42,    3,  141,  143,  145,  178,   75,
  195,   51,  119,    7,   74,  116,  116,   55,  116,  114,
    3,  114,  179,  114,   86,   87,   75,  135,  155,    7,
   72,   71,   44,   70,   77,   73,   77,  114,  114,   72,
  114,  112,   52,  112,   73,  112,  174,  139,  140,   84,
  188,  196,   84,  146,   74,   84,  127,   83,  127,  112,
  112,   76,  112,   64,   29,   55,  115,    2,  115,   83,
  115,  179,   83,   89,    3,    4,    5,  124,  125,  116,
    6,  116,  158,    7,  115,  115,  108,  115,  101,  108,
  132,  134,  103,   32,  107,  158,  188,  114,   33,  115,
   32,  114,  188,  114,  117,   33,   29,  120,  148,    2,
   84,  130,   83,  121,  136,   62,  149,    4,    5,  147,
   63,   62,    6,  112,  150,  112,   33,  151,  164,  165,
  160,  127,  127,  166,  127,  170,  127,   97,  171,  172,
  173,   33,  175,   93,   49,  186,   74,   33,  115,   74,
  115,   33,   90,  125,   54,  189,   74,   74,   74,  118,
  152,  224,   74,   29,   30,   74,   74,   77,   77,  125,
   77,   77,   77,   77,   77,   77,   77,   77,   77,   77,
   77,   31,  104,   77,   77,   33,  184,   77,   77,  127,
  127,   33,  128,  127,  127,  127,  127,  127,  127,  127,
  127,  127,  127,  162,  182,  127,  127,   33,  190,  127,
  127,   33,  191,  116,   33,  193,  116,  116,  116,  116,
  116,  116,  116,  116,  116,  116,  194,  197,  116,  116,
   64,  198,  116,  116,  200,  114,  201,  202,  114,  114,
  114,  114,  114,  114,  114,  114,  114,  114,   64,  111,
  114,  114,   63,  203,  114,  114,  206,  112,  208,  210,
  112,  112,  112,  112,  112,  112,  112,  112,  112,  112,
   63,  220,  112,  112,  218,  219,  112,  112,   77,  221,
   77,  222,  115,  223,  225,  115,  115,  115,  115,  115,
  115,  115,  115,  115,  115,   77,  100,  115,  115,   28,
    9,  115,  115,  111,  157,  111,    7,  111,   69,   28,
   29,   30,   64,  207,   64,  176,   28,   29,   30,   75,
  169,  111,  111,  109,  111,  113,    0,  113,   31,  113,
    0,   28,   29,   60,   63,   31,   63,   28,   29,   30,
    0,    0,    0,  113,  113,  127,  113,  110,    0,  110,
   61,  110,   28,   29,   30,    0,   31,    1,   28,   29,
   30,  127,   28,   29,   30,  110,  110,    0,  110,    0,
   96,   31,    0,  126,  126,    0,  126,   31,  126,  129,
  129,   31,  129,   29,  129,  111,    2,  111,  130,  130,
    0,  130,    0,  130,    4,    5,  123,   29,   30,    6,
    0,    0,  131,   29,   30,    0,    0,  113,    0,  113,
    0,   84,    0,   83,    0,   31,    0,    0,  133,   29,
   30,   31,  142,   29,   30,  144,   29,   30,   82,  110,
   81,  110,    0,    0,    0,    0,   29,   31,    0,    2,
    0,   31,   62,    0,   31,    0,   64,    4,    5,   64,
   64,   64,    6,    0,    0,    0,   64,   64,   64,    0,
    0,   64,   64,    0,    0,   64,   64,    0,   63,    0,
    0,   63,   63,   63,    0,    0,   61,    8,   63,   63,
   63,    0,    0,   63,   63,    0,    0,   63,   63,    0,
    0,    1,    0,   77,    1,    0,    0,    0,   48,    0,
    0,    1,    1,    1,    0,    0,   62,    1,   62,    0,
    1,    0,    0,    0,    0,    0,    0,    0,    0,  111,
   33,    0,  111,  111,  111,  111,  111,  111,  111,  111,
  111,  111,    0,    0,  111,  111,    0,    0,  111,  111,
   61,  113,   61,    0,  113,  113,  113,  113,  113,  113,
  113,  113,  113,  113,   17,    0,  113,  113,    0,   34,
  113,  113,   17,  110,   48,   17,  110,  110,  110,  110,
  110,  110,  110,  110,  110,  110,    0,    0,  110,  110,
   22,   17,  110,  110,    0,    0,   33,  126,   22,    0,
   25,   22,    0,  129,    0,    0,    0,    0,   25,    0,
   17,   25,  130,  126,    0,   65,    0,   22,    0,  129,
   47,    1,    0,   17,    2,    0,  204,   25,  130,    0,
    0,    3,    4,    5,    0,   34,   22,    6,    0,    0,
    7,   77,   78,   79,   80,    0,   25,  108,   26,   22,
   62,    0,    0,   62,   62,   62,    0,    0,    0,   25,
   62,   62,   62,    8,    0,   62,   62,    0,    0,   62,
   62,    0,    0,    0,    0,   17,    0,    0,    0,    0,
    0,    0,    0,    0,   61,    0,   47,   61,   61,   61,
    0,    0,    0,    0,   61,   61,   61,    0,   40,   61,
   61,   22,    0,   61,   61,   38,   48,    0,    0,   48,
    0,   25,    0,    0,   26,    0,   48,   48,   48,    0,
    0,   48,   48,   17,    0,   48,   48,    0,   33,   14,
    0,   33,    0,   41,    0,    0,    0,    0,   33,   33,
   33,    0,    0,   33,   33,    0,    0,   33,   33,   22,
   15,    0,    0,    0,    0,   17,    0,    0,    0,   25,
   29,    0,    0,    2,   45,    0,    0,   34,   17,   17,
   34,    4,    5,    0,    0,   17,    6,   34,   34,   34,
    0,   22,   34,   34,   17,   48,   34,   34,   17,    0,
    0,   25,    0,    0,   22,   22,    0,   29,    0,   99,
    2,   22,    0,    0,   25,   25,    0,    3,    4,    5,
   22,   25,    0,    6,   22,    0,    7,  226,   47,  213,
   25,   47,    0,    0,   25,    0,    0,    0,   47,   47,
   47,    0,   40,   47,   47,   40,    0,   47,   47,   38,
  217,    0,   38,   40,   40,    0,   26,   19,   40,   26,
   38,   38,    0,    0,   18,   38,   26,   26,   26,    0,
    0,   14,   26,    0,   14,   26,   26,   41,  187,    0,
   41,   14,   14,   14,    0,    0,    0,   14,   41,   41,
   14,   14,   15,   41,    0,   15,    0,    0,    0,    0,
    0,    0,   15,   15,   15,    0,   29,    0,   15,    2,
    0,   15,   15,    0,    0,    0,    3,    4,    5,    0,
    0,    0,    6,    0,    0,    7,    0,   29,    0,    0,
    2,    0,    0,    0,    0,    0,    0,    3,    4,    5,
    0,   29,    0,    6,    2,    0,    7,    0,    0,    0,
    0,    3,    4,    5,    0,    0,    0,    6,    0,   29,
    7,   29,    2,    0,    2,    0,    0,    0,    0,    3,
    4,    5,    4,    5,    0,    6,  212,    6,    7,    0,
    0,    0,   29,    0,    0,    2,    0,    0,    0,   19,
    0,    0,   19,    4,    5,    0,   18,  216,    6,   18,
   19,   19,    0,    0,   19,   19,    0,   18,   18,    0,
   29,   18,   18,    2,    0,    0,    0,    0,    0,    0,
    0,    4,    5,    0,    0,    0,    6,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         40,
   43,   11,   45,   44,   41,   62,  270,   44,   42,   42,
   43,   46,   45,   47,   47,   37,   44,   45,   59,    8,
   59,   45,   41,   42,   43,   44,   45,   46,   47,  267,
  277,   43,   43,   45,   59,   44,   46,   25,  276,   45,
   59,   60,   61,   62,   41,   42,   43,   58,   45,   45,
   47,   39,   61,   44,   42,  191,   44,   72,   73,   40,
   45,   45,   59,   60,  257,   62,   54,   41,  204,   43,
   61,   45,  111,   40,  267,   90,   91,   92,  271,   44,
  257,   59,   41,  276,  125,   59,   60,   46,   62,   41,
  267,   43,  149,   45,  261,  262,   61,   85,  109,  276,
   42,   43,   40,   45,  123,   47,  125,   59,   60,   42,
   62,   41,  257,   43,   47,   45,  138,  261,  262,   41,
  159,  178,   44,   41,   40,   43,  123,   45,  125,   59,
   60,  258,   62,  121,  257,   46,   41,  260,   43,   41,
   45,  198,   44,   41,  267,  268,  269,   70,   71,  123,
  273,  125,  191,  276,   59,   60,   41,   62,   44,   44,
   83,   84,  257,   40,  257,  204,  205,   41,   45,   40,
   40,  123,  211,  125,  258,   45,  257,   41,   41,  260,
   43,   41,   45,   44,  262,   40,   40,  268,  269,   41,
   45,   40,  273,  123,  257,  125,   45,  258,  257,   41,
   59,   42,   43,  257,   45,  277,   47,   41,   41,  258,
  262,   45,  262,   41,  224,   59,  257,   45,  123,  260,
  125,   45,  256,  256,  259,   41,  267,  268,  269,  272,
  258,  220,  273,  257,  258,  276,  277,  256,  257,  272,
  259,  260,  261,  262,  263,  264,  265,  266,  267,  268,
  269,  275,  258,  272,  273,   45,  258,  276,  277,  256,
  257,   45,  258,  260,  261,  262,  263,  264,  265,  266,
  267,  268,  269,  258,  258,  272,  273,   45,  258,  276,
  277,   45,   41,  257,   45,   41,  260,  261,  262,  263,
  264,  265,  266,  267,  268,  269,  262,  257,  272,  273,
   41,   44,  276,  277,  258,  257,   59,   41,  260,  261,
  262,  263,  264,  265,  266,  267,  268,  269,   59,  123,
  272,  273,   41,   41,  276,  277,  257,  257,   41,   41,
  260,  261,  262,  263,  264,  265,  266,  267,  268,  269,
   59,  123,  272,  273,  257,  272,  276,  277,   44,  257,
   46,  272,  257,  257,  257,  260,  261,  262,  263,  264,
  265,  266,  267,  268,  269,   61,   41,  272,  273,   41,
  277,  276,  277,   41,  125,   43,  277,   45,   27,  256,
  257,  258,  123,  198,  125,  139,  256,  257,  258,   32,
  121,   59,   60,  270,   62,   41,   -1,   43,  275,   45,
   -1,  256,  257,  258,  123,  275,  125,  256,  257,  258,
   -1,   -1,   -1,   59,   60,  256,   62,   41,   -1,   43,
  275,   45,  256,  257,  258,   -1,  275,  123,  256,  257,
  258,  272,  256,  257,  258,   59,   60,   -1,   62,   -1,
  274,  275,   -1,   42,   43,   -1,   45,  275,   47,   42,
   43,  275,   45,  257,   47,  123,  260,  125,   42,   43,
   -1,   45,   -1,   47,  268,  269,  256,  257,  258,  273,
   -1,   -1,  256,  257,  258,   -1,   -1,  123,   -1,  125,
   -1,   43,   -1,   45,   -1,  275,   -1,   -1,  256,  257,
  258,  275,  256,  257,  258,  256,  257,  258,   60,  123,
   62,  125,   -1,   -1,   -1,   -1,  257,  275,   -1,  260,
   -1,  275,   59,   -1,  275,   -1,  257,  268,  269,  260,
  261,  262,  273,   -1,   -1,   -1,  267,  268,  269,   -1,
   -1,  272,  273,   -1,   -1,  276,  277,   -1,  257,   -1,
   -1,  260,  261,  262,   -1,   -1,   59,  123,  267,  268,
  269,   -1,   -1,  272,  273,   -1,   -1,  276,  277,   -1,
   -1,  257,   -1,  259,  260,   -1,   -1,   -1,   59,   -1,
   -1,  267,  268,  269,   -1,   -1,  123,  273,  125,   -1,
  276,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  257,
   59,   -1,  260,  261,  262,  263,  264,  265,  266,  267,
  268,  269,   -1,   -1,  272,  273,   -1,   -1,  276,  277,
  123,  257,  125,   -1,  260,  261,  262,  263,  264,  265,
  266,  267,  268,  269,    0,   -1,  272,  273,   -1,   59,
  276,  277,    8,  257,  125,   11,  260,  261,  262,  263,
  264,  265,  266,  267,  268,  269,   -1,   -1,  272,  273,
    0,   27,  276,  277,   -1,   -1,  125,  256,    8,   -1,
    0,   11,   -1,  256,   -1,   -1,   -1,   -1,    8,   -1,
   46,   11,  256,  272,   -1,   25,   -1,   27,   -1,  272,
   59,  257,   -1,   59,  260,   -1,  123,   27,  272,   -1,
   -1,  267,  268,  269,   -1,  125,   46,  273,   -1,   -1,
  276,  263,  264,  265,  266,   -1,   46,   57,   59,   59,
  257,   -1,   -1,  260,  261,  262,   -1,   -1,   -1,   59,
  267,  268,  269,  123,   -1,  272,  273,   -1,   -1,  276,
  277,   -1,   -1,   -1,   -1,  111,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,  257,   -1,  125,  260,  261,  262,
   -1,   -1,   -1,   -1,  267,  268,  269,   -1,  123,  272,
  273,  111,   -1,  276,  277,  123,  257,   -1,   -1,  260,
   -1,  111,   -1,   -1,  125,   -1,  267,  268,  269,   -1,
   -1,  272,  273,  159,   -1,  276,  277,   -1,  257,  125,
   -1,  260,   -1,  123,   -1,   -1,   -1,   -1,  267,  268,
  269,   -1,   -1,  272,  273,   -1,   -1,  276,  277,  159,
  125,   -1,   -1,   -1,   -1,  191,   -1,   -1,   -1,  159,
  257,   -1,   -1,  260,  125,   -1,   -1,  257,  204,  205,
  260,  268,  269,   -1,   -1,  211,  273,  267,  268,  269,
   -1,  191,  272,  273,  220,  125,  276,  277,  224,   -1,
   -1,  191,   -1,   -1,  204,  205,   -1,  257,   -1,  125,
  260,  211,   -1,   -1,  204,  205,   -1,  267,  268,  269,
  220,  211,   -1,  273,  224,   -1,  276,  125,  257,  125,
  220,  260,   -1,   -1,  224,   -1,   -1,   -1,  267,  268,
  269,   -1,  257,  272,  273,  260,   -1,  276,  277,  257,
  125,   -1,  260,  268,  269,   -1,  257,  125,  273,  260,
  268,  269,   -1,   -1,  125,  273,  267,  268,  269,   -1,
   -1,  257,  273,   -1,  260,  276,  277,  257,  125,   -1,
  260,  267,  268,  269,   -1,   -1,   -1,  273,  268,  269,
  276,  277,  257,  273,   -1,  260,   -1,   -1,   -1,   -1,
   -1,   -1,  267,  268,  269,   -1,  257,   -1,  273,  260,
   -1,  276,  277,   -1,   -1,   -1,  267,  268,  269,   -1,
   -1,   -1,  273,   -1,   -1,  276,   -1,  257,   -1,   -1,
  260,   -1,   -1,   -1,   -1,   -1,   -1,  267,  268,  269,
   -1,  257,   -1,  273,  260,   -1,  276,   -1,   -1,   -1,
   -1,  267,  268,  269,   -1,   -1,   -1,  273,   -1,  257,
  276,  257,  260,   -1,  260,   -1,   -1,   -1,   -1,  267,
  268,  269,  268,  269,   -1,  273,  272,  273,  276,   -1,
   -1,   -1,  257,   -1,   -1,  260,   -1,   -1,   -1,  257,
   -1,   -1,  260,  268,  269,   -1,  257,  272,  273,  260,
  268,  269,   -1,   -1,  272,  273,   -1,  268,  269,   -1,
  257,  272,  273,  260,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,  268,  269,   -1,   -1,   -1,  273,
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
"declaracion : tipo ID $$3 '(' listaparametros $$4 ')' $$5 '{' listasentencias '}'",
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
"cabecera_funcion : ID '('",
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

//#line 224 "gramatica.y"

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
  tablaSimbolos.put(simboloID.getLexema(), new SimboloFuncion(257, stackTipoFuncActual.pop(), "Funcion", simboloID.getLexema(), stackParametrosActuales.pop()));
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
  String lexemaNuevo = lexemaViejo + stringAmbitos.toString();
  ((Simbolo)yyval.obj).setLexema(lexemaNuevo);
  tablaSimbolos.put(lexemaNuevo, ((Simbolo)yyval.obj));
  tablaSimbolos.remove(lexemaViejo);
}

/*private void chequeo_anid_lambda(String nombreLambda, ParserVal yyval) {
  if (nombreLambda.contains(".")) {
    erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + yyval.ival + ": Se ha intentado invocar una función lambda perteneciente a otro ámbito");
  }
}*/
//#line 762 "Parser.java"
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
{finFunc(val_peek(0), ((Simbolo)val_peek(9).obj)); quitarAmbito(); renombrarVariable(val_peek(9)); System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó una declaración de función");}
break;
case 31:
//#line 63 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(1).ival + ": Se detectó una sentencia return");}
break;
case 32:
//#line 64 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta de argumento en sentencia return");}
break;
case 33:
//#line 67 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó una asignación simple");}
break;
case 34:
//#line 68 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó una asignación múltiple");}
break;
case 38:
//#line 76 "gramatica.y"
{inicioIf();}
break;
case 39:
//#line 76 "gramatica.y"
{finIf(val_peek(0));}
break;
case 40:
//#line 79 "gramatica.y"
{enDo=true;}
break;
case 41:
//#line 82 "gramatica.y"
{sinRetIf.push(sinRet);}
break;
case 42:
//#line 82 "gramatica.y"
{if(sinRetIf.pop()) {sinRet=true;};}
break;
case 43:
//#line 85 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un bloque IF-ELSE");}
break;
case 44:
//#line 86 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un bloque IF");}
break;
case 45:
//#line 87 "gramatica.y"
{enDo=false;}
break;
case 46:
//#line 87 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(1).ival + ": Se detectó un bloque DO UNTIL");}
break;
case 47:
//#line 89 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta la palabra ENDIF al final del bloque IF-ELSE");}
break;
case 48:
//#line 90 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta la palabra ENDIF al final del bloque IF");}
break;
case 49:
//#line 91 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el cuerpo en el bloque ELSE");}
break;
case 50:
//#line 92 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(3).ival + ": falta el cuerpo en el bloque IF");}
break;
case 51:
//#line 93 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el cuerpo en el bloque IF");}
break;
case 52:
//#line 94 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el cuerpo en los bloques IF y ELSE");}
break;
case 53:
//#line 96 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(2).ival + ": falta de cuerpo en iteracion");}
break;
case 54:
//#line 97 "gramatica.y"
{enDo=false;}
break;
case 55:
//#line 97 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(2).ival + ": falta la palabra UNTIL en la iteracion");}
break;
case 58:
//#line 102 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta de argumento en sentencia print");}
break;
case 60:
//#line 106 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta el paréntesis de apertura de la condición");}
break;
case 61:
//#line 107 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta el paréntesis de cierre de la condición");}
break;
case 62:
//#line 108 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": faltan ambos paréntesis de la condición");}
break;
case 64:
//#line 112 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": La condición es incompleta");}
break;
case 71:
//#line 123 "gramatica.y"
{stackTipoFuncActual.push("Long");}
break;
case 72:
//#line 124 "gramatica.y"
{stackTipoFuncActual.push("Lambda");}
break;
case 73:
//#line 127 "gramatica.y"
{renombrarVariable(val_peek(0));}
break;
case 74:
//#line 128 "gramatica.y"
{renombrarVariable(val_peek(0));}
break;
case 81:
//#line 145 "gramatica.y"
{renombrarVariable(val_peek(0)); parametrosActuales.add(new Parametro(((Simbolo)val_peek(0).obj), "CV"));}
break;
case 82:
//#line 146 "gramatica.y"
{renombrarVariable(val_peek(0)); parametrosActuales.add(new Parametro(((Simbolo)val_peek(0).obj), "CVR"));}
break;
case 83:
//#line 147 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el nombre del parámetro");}
break;
case 84:
//#line 148 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta el nombre del parámetro");}
break;
case 85:
//#line 149 "gramatica.y"
{renombrarVariable(val_peek(0)); parametrosActuales.add(new Parametro(((Simbolo)val_peek(0).obj), "CV")); erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el tipo del parámetro");}
break;
case 86:
//#line 150 "gramatica.y"
{renombrarVariable(val_peek(0)); parametrosActuales.add(new Parametro(((Simbolo)val_peek(0).obj), "CVR")); erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta el tipo del parámetro");}
break;
case 87:
//#line 153 "gramatica.y"
{enParamInvocacion = false;}
break;
case 88:
//#line 153 "gramatica.y"
{ System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(1).ival + ": Se detectó la invocación de una función");}
break;
case 89:
//#line 156 "gramatica.y"
{enParamInvocacion = false;}
break;
case 90:
//#line 156 "gramatica.y"
{ /*chequeo_anid_lambda($1.sval, $3);*/ System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(1).ival + ": Se detectó la invocación de una función lambda");}
break;
case 91:
//#line 157 "gramatica.y"
{enParamInvocacion = false;}
break;
case 92:
//#line 157 "gramatica.y"
{ /*chequeo_anid_lambda($1.sval, $3)*/ ; constantePositiva(val_peek(2)); System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(1).ival + ": Se detectó la invocación de una función lambda");}
break;
case 93:
//#line 158 "gramatica.y"
{enParamInvocacion = false;}
break;
case 94:
//#line 158 "gramatica.y"
{ /*chequeo_anid_lambda($1.sval, $4)*/ ; constanteNegativa(val_peek(2)); System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(1).ival + ": Se detectó la invocación de una función lambda");}
break;
case 95:
//#line 159 "gramatica.y"
{enParamInvocacion = false;}
break;
case 96:
//#line 159 "gramatica.y"
{ constantePositiva(val_peek(3)); System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(1).ival + ": Se detectó la invocación de una función lambda");}
break;
case 97:
//#line 160 "gramatica.y"
{enParamInvocacion = false;}
break;
case 98:
//#line 160 "gramatica.y"
{ constanteNegativa(val_peek(3)); System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(1).ival + ": Se detectó la invocación de una función lambda");}
break;
case 99:
//#line 163 "gramatica.y"
{enParamInvocacion = true;}
break;
case 105:
//#line 179 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(2).ival + ": falta llave de cierre del cuerpo de la función Lambda");}
break;
case 106:
//#line 180 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(4).ival + ": falta llave de apertura del cuerpo de la función Lambda");}
break;
case 107:
//#line 181 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(3).ival + ": faltan ambas llaves del cuerpo de la función Lambda");}
break;
case 108:
//#line 182 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": No se especifica el parametro formal al que corresponde el parámetro real");}
break;
case 109:
//#line 183 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": No se especifica el parametro formal al que corresponde el parámetro real");}
break;
case 110:
//#line 187 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(1).ival + ": Se detectó una expresión desde una suma");}
break;
case 111:
//#line 188 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(1).ival + ": Se detectó una expresión desde una resta");}
break;
case 112:
//#line 189 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la izquierda de '+'"); }
break;
case 113:
//#line 190 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la derecha de '+'"); }
break;
case 114:
//#line 191 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la izquierda de '-'"); }
break;
case 115:
//#line 192 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la derecha de '-'"); }
break;
case 116:
//#line 193 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó una expresión desde un término");}
break;
case 117:
//#line 196 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(1).ival + ": Se detectó un término desde una multiplicación");}
break;
case 118:
//#line 197 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(1).ival + ": Se detectó un término desde una división");}
break;
case 119:
//#line 198 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la izquierda de '*'"); }
break;
case 120:
//#line 199 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la derecha de '*'"); }
break;
case 121:
//#line 200 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la izquierda de '/'"); }
break;
case 122:
//#line 201 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la derecha de '/'"); }
break;
case 123:
//#line 202 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(2).ival + ": falta operador entre operandos o falta de comparador en condición"); }
break;
case 124:
//#line 203 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un término desde un factor");}
break;
case 125:
//#line 206 "gramatica.y"
{ constantePositiva(val_peek(0)); System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un factor desde una constante");}
break;
case 126:
//#line 207 "gramatica.y"
{ constanteNegativa(val_peek(0)); System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un factor desde una constante negativa");}
break;
case 127:
//#line 208 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un factor desde un identificador");}
break;
case 128:
//#line 209 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un factor desde una función");}
break;
case 129:
//#line 210 "gramatica.y"
{ constantePositiva(val_peek(1)); if (enParamInvocacion) {System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(3).ival + ": Se detectó la función Trunc en uno de los parámetros reales de la invocación");} else erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(3).ival + ": la función TRUNC solo puede utilizarse en parámetros reales de invocación de funciones");}
break;
case 130:
//#line 211 "gramatica.y"
{ constanteNegativa(val_peek(1)); if (enParamInvocacion) {System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(4).ival + ": Se detectó la función Trunc en uno de los parámetros reales de la invocación");} else erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(4).ival + ": la función TRUNC solo puede utilizarse en parámetros reales de invocación de funciones");}
break;
case 131:
//#line 214 "gramatica.y"
{ constantePositiva(val_peek(0)); }
break;
case 132:
//#line 215 "gramatica.y"
{ constanteNegativa(val_peek(0)); }
break;
case 133:
//#line 216 "gramatica.y"
{ constantePositiva(val_peek(0)); }
break;
case 134:
//#line 217 "gramatica.y"
{ constanteNegativa(val_peek(0)); }
break;
case 135:
//#line 218 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta ',' entre constantes"); constantePositiva(val_peek(0)); }
break;
case 136:
//#line 219 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(2).ival + ": falta ',' entre constantes"); constanteNegativa(val_peek(0)); }
break;
//#line 1311 "Parser.java"
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
