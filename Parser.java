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
    import java.util.Map;
    import java.util.HashMap;
    import java.nio.file.Path;
    import java.nio.file.Paths;
    import java.nio.file.Files;
    import java.nio.file.StandardOpenOption;
    import java.util.ArrayList;
    import java.util.Stack;

//#line 29 "Parser.java"




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
    1,    1,    2,    2,    2,    2,    5,    5,    5,    5,
    3,    3,    3,    3,    3,    3,    4,   15,    4,   11,
   11,    6,    6,   19,   19,   19,   21,   20,   22,   24,
   23,    7,    7,   26,    7,    7,    7,    7,    7,    7,
    7,    7,   27,    7,    9,    9,    9,   25,   25,   25,
   25,   28,   28,   29,   29,   29,   29,   29,   29,   12,
   12,   13,   13,   17,   17,    8,   10,   10,   10,   30,
   31,   31,   32,   32,   32,   32,   32,   32,   32,   32,
   32,   32,   32,   32,   14,   14,   33,   33,   33,   33,
   33,   33,   16,   16,   16,   16,   16,   16,   16,   34,
   34,   34,   34,   34,   34,   34,   34,   35,   35,   35,
   35,   18,   18,   18,   18,   18,   18,
};
final static short yylen[] = {                            2,
    5,    4,    4,    4,    4,    3,    3,    3,    2,    7,
    2,    1,    2,    2,    1,    1,    3,    2,    2,    1,
    1,    1,    1,    1,    1,    1,    2,    0,    9,    4,
    3,    3,    3,    3,    2,    2,    0,    2,    1,    0,
    2,    6,    4,    0,    5,    5,    3,    5,    5,    3,
    4,    3,    0,    4,    4,    4,    3,    3,    2,    2,
    1,    3,    1,    1,    1,    1,    1,    1,    1,    1,
    1,    3,    1,    1,    3,    4,    4,    4,    5,    1,
    3,    1,    3,    6,    5,    5,    4,    9,    8,    8,
    7,    8,    2,    5,    3,    1,    3,    2,    2,    1,
    2,    1,    3,    3,    3,    3,    3,    3,    1,    3,
    3,    3,    3,    3,    3,    3,    1,    1,    2,    1,
    1,    3,    4,    1,    2,    2,    3,
};
final static short yydefred[] = {                         0,
    0,    0,   70,    0,    0,    0,   71,    0,    0,    0,
   12,    0,    0,   21,   22,   23,   24,   25,   26,    0,
    0,    0,   37,    0,    0,    0,    0,    0,    0,  118,
    0,    0,  121,    0,    0,    0,    0,    0,  117,    0,
    0,    0,    0,    9,    0,   11,   13,   14,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,   82,    6,
    0,    0,    0,    0,    0,    0,    0,  119,   66,   67,
   68,   69,   64,   65,    0,    0,    0,    0,   50,    0,
   59,    0,    0,    0,   31,    0,   52,    0,   57,    0,
    7,    0,    8,    0,  124,    0,    0,   74,    0,    0,
   75,    0,   53,    0,    0,   38,    0,    2,    4,    0,
   78,    0,    0,    0,    0,    0,   77,   76,    0,    5,
    0,    0,    0,  112,  114,   58,    0,    0,    0,    0,
    0,   51,    0,   37,    0,   43,  116,  113,  110,  115,
  111,   30,   56,   55,    3,  102,    0,    0,    0,   96,
  125,  126,    0,    0,   44,   54,   36,    0,    0,   35,
    0,    1,    0,    0,    0,    0,   79,   83,   81,   49,
   41,   48,    0,  101,    0,   98,   28,    0,  122,    0,
  127,   45,   18,   34,    0,    0,    0,    0,   87,    0,
    0,   42,   97,    0,   95,  123,   17,   10,   85,    0,
   86,    0,    0,    0,   84,    0,    0,    0,    0,    0,
    0,   91,    0,   29,   89,    0,   90,   88,
};
final static short yydgoto[] = {                          9,
   10,   11,   12,   13,  169,   14,   15,   33,   17,   18,
   19,   20,   21,  159,  204,   34,   35,  107,  116,   56,
   57,   23,  143,  144,   36,  192,  166,   37,   87,   67,
   68,   69,  160,   38,   39,
};
final static short yysindex[] = {                       511,
  158,  122,    0,  -37, -263,    5,    0,  290,    0,  536,
    0,   -8,   53,    0,    0,    0,    0,    0,    0, -167,
  -11,  -29,    0,   85,  723,   98,  590,   31,   91,    0,
  207, -125,    0,  399,   99, -103,   96,  -22,    0,  281,
  122,  107,  666,    0, -138,    0,    0,    0,  110,  117,
   99,   15,  -92,  207,  -87,  166,  -66,  116, -105,  681,
  138,  304, -237,  -78,  -16,   34,  140,  139,    0,    0,
  -95,  424,  424,    4,    4,  101,  143,    0,    0,    0,
    0,    0,    0,    0,  477,  480,  207,  -77,    0,  -84,
    0,    4,  504,  508,    0,   54,    0,  155,    0,   55,
    0,  -80,    0, -199,    0,  -59,   -7,    0,   99,   36,
    0,  122,    0,  819,  141,    0,  290,    0,    0,  -76,
    0,  207,  -33,  -53,  164,  -49,    0,    0,  101,    0,
   45,  -22,  -22,    0,    0,    0,   45,  -22,   45,  -22,
   36,    0,  -50,    0,  -45,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0, -215,  -44,   61,    0,
    0,    0,   19,  -40,    0,    0,    0,  150,  826,    0,
  737,    0,  -32,  -43,   10,  192,    0,    0,    0,    0,
    0,    0,   23,    0,   27,    0,    0, -199,    0,   43,
    0,    0,    0,    0,  245,   29,   62,   56,    0,   72,
  768,    0,    0,  218,    0,    0,    0,    0,    0,   94,
    0, -113,  198,  290,    0,  564,  104,   81,  751,  111,
  103,    0,  114,    0,    0,  128,    0,    0,
};
final static short yyrindex[] = {                         0,
  -30,    0,    0,    0,  778,    0,    0,    0,    0,    0,
    0,  695,  709,    0,    0,    0,    0,    0,    0,    0,
    0,   22,    0,  -30,    0,    0,    0,    0,  -41,    0,
    0,    0,    0,  237,  -19,  785,  431,    3,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,  130,  652,
  215,    0,    0,    0,    0,    0,    0,    0,    0,    0,
  121,    0,    0,    0,    0,  145,    0,  342,    0,    0,
    0,    0,    0,    0,    0,    0,  620,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  795,    0,  453,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,  482,    0,  376,  550,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,  398,   73,    0,    0,    0,    0,
    0,   26,   48,    0,    0,    0,   70,  307,  329,  351,
  259,    0,    0,    0,  795,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,   75,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  802,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,  638,    0,   79,    0,    0,    0,    0,    0,
    0,    0,    0,    0,  812,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,   83,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,  113,    0,    0,
};
final static short yygindex[] = {                         0,
   93,  126,  639,    0, -142,    0,    0,  659,    0,    0,
    0,  -48,  366,    0,    0, 1051,  630,    0,    0,   -2,
    0,    0,  250,    0,   -9,    0,    0,  369,    0,    0,
    0,  273,  216,   49,  -39,
};
final static int YYTABLESIZE=1180;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         74,
   74,   74,   40,   74,   74,   74,   41,  175,  198,   86,
   86,   85,   85,   74,  124,   74,   55,   74,   74,   93,
   74,  120,  120,  120,   94,  120,   86,  120,   85,    3,
   74,   97,   53,   90,  134,  135,  163,  164,    7,  120,
  120,  184,  120,  109,   42,  109,  113,  109,   32,   52,
   47,    3,  147,  149,  151,  158,  114,  156,  213,  106,
    7,  109,  109,  190,  109,   73,  107,    3,  107,  216,
  107,  157,   74,   73,  127,   72,    7,   75,   86,   55,
   85,   74,   73,   74,  107,  107,   74,  107,  105,   49,
  105,   75,  105,   27,  152,  154,   86,   86,   85,   85,
   43,  187,  165,  120,  188,  120,  105,  105,  185,  105,
  108,   48,  108,   93,  108,  100,   93,   60,  100,   99,
  132,  133,   99,   94,   26,  109,   94,  109,  108,  108,
   76,  108,   78,  138,  140,   46,   91,   63,  103,  158,
   63,  181,   64,   24,   55,   32,    2,   99,  107,  104,
  107,   32,   46,   92,    4,    5,   92,   88,   89,    6,
   53,   31,  118,  118,  108,  118,   32,  118,   46,  111,
  105,  118,  105,   74,  117,   74,  145,  146,  121,  125,
  128,  130,  129,  136,  142,   46,  120,  120,   74,  120,
   24,  120,  108,    2,  108,  153,  155,   26,  161,  170,
  172,    4,    5,  176,  177,   31,    6,  178,  193,  171,
   32,  180,  186,  199,   74,   74,  182,  191,   74,   74,
   74,   74,   74,   74,   74,   74,   74,   74,   74,   54,
   74,   74,  201,   92,   74,   74,  120,  120,  174,  197,
  120,  120,  120,  120,  120,  120,  120,  120,  120,  120,
  162,   32,  120,  120,   74,  126,  120,  120,   73,  109,
   29,   30,  109,  109,  109,  109,  109,  109,  109,  109,
  109,  109,  105,   73,  109,  109,  189,   63,  109,  109,
   25,  200,  107,  203,  202,  107,  107,  107,  107,  107,
  107,  107,  107,  107,  107,   63,   46,  107,  107,   62,
  206,  107,  107,  207,  105,  208,  219,  105,  105,  105,
  105,  105,  105,  105,  105,  105,  105,   62,  209,  105,
  105,   95,  218,  105,  105,   32,  108,  210,  211,  108,
  108,  108,  108,  108,  108,  108,  108,  108,  108,   73,
  214,  108,  108,  122,   46,  108,  108,  104,   32,  104,
  215,  104,  223,   28,   29,   61,   28,   29,   30,   63,
  222,   63,   28,   29,   30,  104,  104,  225,  104,  106,
  227,  106,   62,  106,  226,   62,  118,   28,   29,   30,
   98,   62,   80,   62,  228,   50,   74,  106,  106,   74,
  106,  103,  118,  103,  183,  103,   74,   74,   74,   77,
  120,  179,   74,  205,    0,   74,   74,    0,    0,  103,
  103,    0,  103,    0,   24,    0,  120,    2,    0,   72,
    0,   28,   29,   30,    3,    4,    5,    0,    0,  104,
    6,  104,    0,    7,   72,  112,   72,    0,    0,  119,
  119,   86,  119,   85,  119,    0,    0,    0,    0,    0,
    0,  106,    0,  106,   24,    0,    0,    2,   84,    0,
   83,    0,   28,   29,   30,    4,    5,    0,   32,  217,
    6,   73,    0,  103,   73,  103,    0,    0,    0,    0,
    0,   73,   73,   73,    0,    0,    0,   73,    0,   61,
   73,   73,    0,   63,    0,    0,   63,   63,   63,    0,
   72,    0,    0,   63,   63,   63,    0,    0,   63,   63,
    0,   47,   63,   63,    0,   62,    0,    0,   62,   62,
   62,   32,    0,    0,   32,   62,   62,   62,    0,    0,
   62,   62,    0,    0,   62,   62,   28,   29,   30,    0,
   33,    0,    0,    0,    0,    0,   24,    0,   32,    2,
    0,    0,   32,   61,    0,   61,    3,    4,    5,   28,
   29,   30,    6,  104,    0,    7,  104,  104,  104,  104,
  104,  104,  104,  104,  104,  104,    0,   47,  104,  104,
    0,    0,  104,  104,    0,  106,    0,    0,  106,  106,
  106,  106,  106,  106,  106,  106,  106,  106,    0,    0,
  106,  106,    0,    0,  106,  106,   33,  103,   32,    0,
  103,  103,  103,  103,  103,  103,  103,  103,  103,  103,
    0,    0,  103,  103,    0,    0,  103,  103,    0,   22,
   22,    0,   72,    8,    0,   72,    0,   22,    0,   22,
    0,    0,   72,   72,   72,    0,    0,    0,   72,   51,
    0,   72,   72,  119,   22,   66,   22,    0,   16,   16,
   45,   79,   80,   81,   82,    0,   16,    0,   16,  119,
    0,    0,   22,    0,   32,    0,    0,    0,   60,  131,
   29,   30,  109,   16,    0,   16,   22,   61,  221,   22,
   61,   61,   61,    0,    0,  115,   46,   61,   61,   61,
    0,   16,   61,   61,    0,    0,   61,   61,    0,   47,
   27,    0,   47,    0,   71,   16,    0,    0,   16,   47,
   47,   47,    0,    0,   47,   47,    0,    0,   47,   47,
    0,    0,  137,   29,   30,  139,   29,   30,   33,    0,
    0,   33,   60,   22,   60,    0,   22,    0,   33,   33,
   33,    0,  168,   33,   33,    0,    0,   33,   33,  148,
   29,   30,   46,  150,   29,   30,    0,    1,    0,    0,
    2,    0,   16,    0,    0,   16,   27,    3,    4,    5,
    0,    0,    0,    6,    0,    0,    7,    0,    0,    0,
  102,    0,   24,    0,    0,    2,    0,    0,   22,    0,
   22,    0,    3,    4,    5,  120,   32,  195,    6,   32,
    0,    7,   44,    0,    0,    0,   32,   32,   32,   15,
   24,   32,   32,    2,    0,   32,   32,   16,    0,   16,
   22,    4,    5,   16,    0,  220,    6,    0,    0,  168,
    0,   22,   22,   22,    0,   22,   24,   59,   22,    2,
  168,  195,    0,    0,  195,    0,    3,    4,    5,   16,
    0,  196,    6,    0,    0,    7,   70,    0,    0,    0,
   16,   16,   16,    0,   16,  224,   60,   16,    0,   60,
   60,   60,    0,    0,    0,    0,   60,   60,   60,    0,
  212,   60,   60,    0,   46,   60,   60,   46,    0,    0,
   39,    0,    0,    0,   46,   46,   46,   37,   27,   46,
   46,   27,    0,   46,   46,    0,    0,   40,   27,   27,
   27,    0,   24,    0,   27,    2,   20,   27,   27,    0,
    0,    0,    3,    4,    5,    0,   19,   24,    6,    0,
    2,    7,  101,  167,    0,    0,    0,    3,    4,    5,
  194,   15,    0,    6,   15,    0,    7,  119,    0,    0,
    0,   15,   15,   15,    0,   16,    0,   15,   16,    0,
   15,   15,    0,    0,    0,   16,   16,   16,   58,   24,
    0,   16,    2,    0,   16,   16,    0,    0,    0,    3,
    4,    5,    0,   24,    0,    6,    2,    0,    7,    0,
    0,    0,    0,    3,    4,    5,    0,   24,    0,    6,
    2,    0,    7,    0,    0,    0,    0,    3,    4,    5,
    0,    0,    0,    6,   24,    0,    7,    2,    0,    0,
    0,    0,    0,    0,   39,    4,    5,   39,    0,    0,
    6,   37,    0,    0,   37,   39,   39,    0,    0,    0,
   39,   40,   37,   37,   40,    0,    0,   37,   20,    0,
    0,   20,   40,   40,    0,    0,    0,   40,   19,   20,
   20,   19,    0,   20,   20,   24,   65,    0,    2,   19,
   19,    0,   24,   19,   19,    2,    4,    5,    0,    0,
   96,    6,  100,    4,    5,    0,    0,    0,    6,    0,
    0,    0,    0,    0,  110,    0,    0,    0,    0,    0,
    0,    0,  123,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,   65,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  141,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,  173,    0,    0,    0,    0,    0,    0,   65,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         41,
   42,   43,   40,   45,   46,   47,  270,   41,   41,   43,
   43,   45,   45,   44,   63,   46,   46,   59,   60,   42,
   62,   41,   42,   43,   47,   45,   43,   47,   45,  267,
   61,   41,   44,   36,   74,   75,   44,   45,  276,   59,
   60,  257,   62,   41,   40,   43,   56,   45,   45,   61,
   59,  267,   92,   93,   94,  104,  123,  257,  201,   45,
  276,   59,   60,   45,   62,   44,   41,  267,   43,  212,
   45,  271,   42,   43,   41,   45,  276,   47,   43,   46,
   45,  123,   61,  125,   59,   60,   42,   62,   41,  257,
   43,   47,   45,    1,   41,   41,   43,   43,   45,   45,
    8,   41,  112,  123,   44,  125,   59,   60,  157,   62,
   41,   59,   43,   41,   45,   41,   44,   25,   44,   41,
   72,   73,   44,   41,   40,  123,   44,  125,   59,   60,
   40,   62,  258,   85,   86,   10,   41,   40,  277,  188,
   40,  144,   45,  257,   46,   45,  260,   41,  123,   40,
  125,   45,   27,   41,  268,  269,   44,  261,  262,  273,
   44,   40,   42,   43,  257,   45,   45,   47,   43,  257,
  123,  277,  125,   44,   59,   46,  261,  262,   41,  258,
   41,  277,   44,   41,  262,   60,   42,   43,   59,   45,
  257,   47,  123,  260,  125,   41,  277,   40,  258,   59,
  277,  268,  269,  257,   41,   40,  273,  257,   59,  117,
   45,  262,  257,  257,  256,  257,  262,  258,  260,  261,
  262,  263,  264,  265,  266,  267,  268,  269,  259,  259,
  272,  273,   41,  256,  276,  277,  256,  257,  272,  272,
  260,  261,  262,  263,  264,  265,  266,  267,  268,  269,
  258,   45,  272,  273,  125,  272,  276,  277,   44,  257,
  257,  258,  260,  261,  262,  263,  264,  265,  266,  267,
  268,  269,  258,   59,  272,  273,  258,   41,  276,  277,
  123,  272,  257,  257,  262,  260,  261,  262,  263,  264,
  265,  266,  267,  268,  269,   59,  171,  272,  273,   41,
  258,  276,  277,   59,  257,  277,  214,  260,  261,  262,
  263,  264,  265,  266,  267,  268,  269,   59,  257,  272,
  273,   41,  125,  276,  277,   45,  257,  272,  257,  260,
  261,  262,  263,  264,  265,  266,  267,  268,  269,  125,
  123,  272,  273,   40,  219,  276,  277,   41,   45,   43,
  257,   45,  272,  256,  257,  258,  256,  257,  258,  123,
  257,  125,  256,  257,  258,   59,   60,  257,   62,   41,
  257,   43,  275,   45,  272,  275,  256,  256,  257,  258,
  274,  123,   41,  125,  257,   20,  257,   59,   60,  260,
   62,   41,  272,   43,  145,   45,  267,  268,  269,   31,
  256,  129,  273,  188,   -1,  276,  277,   -1,   -1,   59,
   60,   -1,   62,   -1,  257,   -1,  272,  260,   -1,   44,
   -1,  256,  257,  258,  267,  268,  269,   -1,   -1,  123,
  273,  125,   -1,  276,   59,  270,   61,   -1,   -1,   42,
   43,   43,   45,   45,   47,   -1,   -1,   -1,   -1,   -1,
   -1,  123,   -1,  125,  257,   -1,   -1,  260,   60,   -1,
   62,   -1,  256,  257,  258,  268,  269,   -1,   45,  272,
  273,  257,   -1,  123,  260,  125,   -1,   -1,   -1,   -1,
   -1,  267,  268,  269,   -1,   -1,   -1,  273,   -1,   59,
  276,  277,   -1,  257,   -1,   -1,  260,  261,  262,   -1,
  125,   -1,   -1,  267,  268,  269,   -1,   -1,  272,  273,
   -1,   59,  276,  277,   -1,  257,   -1,   -1,  260,  261,
  262,   45,   -1,   -1,   45,  267,  268,  269,   -1,   -1,
  272,  273,   -1,   -1,  276,  277,  256,  257,  258,   -1,
   59,   -1,   -1,   -1,   -1,   -1,  257,   -1,   45,  260,
   -1,   -1,   45,  123,   -1,  125,  267,  268,  269,  256,
  257,  258,  273,  257,   -1,  276,  260,  261,  262,  263,
  264,  265,  266,  267,  268,  269,   -1,  125,  272,  273,
   -1,   -1,  276,  277,   -1,  257,   -1,   -1,  260,  261,
  262,  263,  264,  265,  266,  267,  268,  269,   -1,   -1,
  272,  273,   -1,   -1,  276,  277,  125,  257,   59,   -1,
  260,  261,  262,  263,  264,  265,  266,  267,  268,  269,
   -1,   -1,  272,  273,   -1,   -1,  276,  277,   -1,    0,
    1,   -1,  257,  123,   -1,  260,   -1,    8,   -1,   10,
   -1,   -1,  267,  268,  269,   -1,   -1,   -1,  273,   20,
   -1,  276,  277,  256,   25,   26,   27,   -1,    0,    1,
  125,  263,  264,  265,  266,   -1,    8,   -1,   10,  272,
   -1,   -1,   43,   -1,  125,   -1,   -1,   -1,   59,  256,
  257,  258,   53,   25,   -1,   27,   57,  257,  125,   60,
  260,  261,  262,   -1,   -1,   57,   59,  267,  268,  269,
   -1,   43,  272,  273,   -1,   -1,  276,  277,   -1,  257,
   59,   -1,  260,   -1,  125,   57,   -1,   -1,   60,  267,
  268,  269,   -1,   -1,  272,  273,   -1,   -1,  276,  277,
   -1,   -1,  256,  257,  258,  256,  257,  258,  257,   -1,
   -1,  260,  123,  114,  125,   -1,  117,   -1,  267,  268,
  269,   -1,  114,  272,  273,   -1,   -1,  276,  277,  256,
  257,  258,  125,  256,  257,  258,   -1,  257,   -1,   -1,
  260,   -1,  114,   -1,   -1,  117,  125,  267,  268,  269,
   -1,   -1,   -1,  273,   -1,   -1,  276,   -1,   -1,   -1,
  125,   -1,  257,   -1,   -1,  260,   -1,   -1,  169,   -1,
  171,   -1,  267,  268,  269,  125,  257,  169,  273,  260,
   -1,  276,  277,   -1,   -1,   -1,  267,  268,  269,  125,
  257,  272,  273,  260,   -1,  276,  277,  169,   -1,  171,
  201,  268,  269,  125,   -1,  272,  273,   -1,   -1,  201,
   -1,  212,  213,  214,   -1,  216,  257,  125,  219,  260,
  212,  213,   -1,   -1,  216,   -1,  267,  268,  269,  201,
   -1,  125,  273,   -1,   -1,  276,  277,   -1,   -1,   -1,
  212,  213,  214,   -1,  216,  125,  257,  219,   -1,  260,
  261,  262,   -1,   -1,   -1,   -1,  267,  268,  269,   -1,
  123,  272,  273,   -1,  257,  276,  277,  260,   -1,   -1,
  123,   -1,   -1,   -1,  267,  268,  269,  123,  257,  272,
  273,  260,   -1,  276,  277,   -1,   -1,  123,  267,  268,
  269,   -1,  257,   -1,  273,  260,  125,  276,  277,   -1,
   -1,   -1,  267,  268,  269,   -1,  125,  257,  273,   -1,
  260,  276,  277,  125,   -1,   -1,   -1,  267,  268,  269,
  125,  257,   -1,  273,  260,   -1,  276,  277,   -1,   -1,
   -1,  267,  268,  269,   -1,  257,   -1,  273,  260,   -1,
  276,  277,   -1,   -1,   -1,  267,  268,  269,  256,  257,
   -1,  273,  260,   -1,  276,  277,   -1,   -1,   -1,  267,
  268,  269,   -1,  257,   -1,  273,  260,   -1,  276,   -1,
   -1,   -1,   -1,  267,  268,  269,   -1,  257,   -1,  273,
  260,   -1,  276,   -1,   -1,   -1,   -1,  267,  268,  269,
   -1,   -1,   -1,  273,  257,   -1,  276,  260,   -1,   -1,
   -1,   -1,   -1,   -1,  257,  268,  269,  260,   -1,   -1,
  273,  257,   -1,   -1,  260,  268,  269,   -1,   -1,   -1,
  273,  257,  268,  269,  260,   -1,   -1,  273,  257,   -1,
   -1,  260,  268,  269,   -1,   -1,   -1,  273,  257,  268,
  269,  260,   -1,  272,  273,  257,   26,   -1,  260,  268,
  269,   -1,  257,  272,  273,  260,  268,  269,   -1,   -1,
   40,  273,   42,  268,  269,   -1,   -1,   -1,  273,   -1,
   -1,   -1,   -1,   -1,   54,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   62,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   76,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   87,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,  122,   -1,   -1,   -1,   -1,   -1,   -1,  129,
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
"prog : ID '{' listasentencias '}' EOF",
"prog : ID '{' '}' EOF",
"prog : '{' listasentencias '}' EOF",
"prog : ID '{' listasentencias EOF",
"prog : ID listasentencias '}' EOF",
"prog : ID listasentencias EOF",
"prog : '{' listasentencias EOF",
"prog : listasentencias '}' EOF",
"prog : listasentencias EOF",
"prog : ID '{' error ';' listasentencias '}' EOF",
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
"declaracion : tipo listavariables",
"$$1 :",
"declaracion : tipo ID '(' listaparametros ')' $$1 '{' listasentencias '}'",
"sentencia_return : RETURN '(' expresion ')'",
"sentencia_return : RETURN '(' ')'",
"asignacion : identificador ASIG expresion",
"asignacion : listavariables '=' listaconstantes",
"bloque_control_estructura : '{' listasentenciasejecutables '}'",
"bloque_control_estructura : sentenciaejecutable ';'",
"bloque_control_estructura : '{' '}'",
"$$2 :",
"bloque_control : $$2 bloque_control_estructura",
"inicio_do : DO",
"$$3 :",
"bloque_control_else : $$3 bloque_control",
"control : IF condicion bloque_control ELSE bloque_control_else ENDIF",
"control : IF condicion bloque_control ENDIF",
"$$4 :",
"control : inicio_do bloque_control UNTIL condicion $$4",
"control : IF condicion bloque_control ELSE bloque_control_else",
"control : IF condicion bloque_control",
"control : IF condicion bloque_control ELSE ENDIF",
"control : IF condicion ELSE bloque_control_else ENDIF",
"control : IF condicion ENDIF",
"control : IF condicion ELSE ENDIF",
"control : DO UNTIL condicion",
"$$5 :",
"control : inicio_do bloque_control condicion $$5",
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

//#line 211 "gramatica.y"

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

private void finFunc(ParserVal yyval) {
  if (returnsFunc > 0 && sinRet) {
    erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + yyval.ival + ": La función no tiene sentencia return en todos sus caminos de ejecución");
  } else if (returnsFunc < 0) {
    erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + yyval.ival + ": Se han detectado multiples sentencias de retorno en la función");
  }
  enFunc--;
  returnsFunc = 1;
}
//#line 739 "Parser.java"
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
case 3:
//#line 20 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(3).ival + ": falta el nombre del programa");}
break;
case 4:
//#line 21 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta la llave de cierre del cuerpo del programa");}
break;
case 5:
//#line 22 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(3).ival + ": falta la llave de apertura del cuerpo del programa");}
break;
case 6:
//#line 23 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": faltan ambas llaves en cuerpo del programa");}
break;
case 7:
//#line 24 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el nombre del programa y la llave de cierre del cuerpo del programa");}
break;
case 8:
//#line 25 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(2).ival + ": falta el nombre del programa y la llave de apertura del cuerpo del programa");}
break;
case 9:
//#line 26 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el nombre del programa y ambas llaves en cuerpo del programa");}
break;
case 10:
//#line 27 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico: el programa no es válido");}
break;
case 15:
//#line 38 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta ; al final de la sentencia ejecutable");}
break;
case 16:
//#line 39 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta ; al final de la declaración");}
break;
case 19:
//#line 44 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta ; al final de la sentencia ejecutable");}
break;
case 20:
//#line 45 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta ; al final de la sentencia ejecutable");}
break;
case 24:
//#line 51 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un print");}
break;
case 25:
//#line 52 "gramatica.y"
{if(enFunc==0) erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": Invocacion de funcion lambda fuera de una función");}
break;
case 26:
//#line 53 "gramatica.y"
{chequeoSentenciaReturn(val_peek(0));}
break;
case 27:
//#line 56 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó una declaración de variables");}
break;
case 28:
//#line 57 "gramatica.y"
{enFunc++;}
break;
case 29:
//#line 57 "gramatica.y"
{finFunc(val_peek(1)); System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(1).ival + ": Se detectó una declaración de función");}
break;
case 30:
//#line 60 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(1).ival + ": Se detectó una sentencia return");}
break;
case 31:
//#line 61 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta de argumento en sentencia return");}
break;
case 32:
//#line 64 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó una asignación simple");}
break;
case 33:
//#line 65 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó una asignación múltiple");}
break;
case 37:
//#line 73 "gramatica.y"
{inicioIf();}
break;
case 38:
//#line 73 "gramatica.y"
{finIf(val_peek(1));}
break;
case 39:
//#line 76 "gramatica.y"
{enDo=true;}
break;
case 40:
//#line 79 "gramatica.y"
{sinRetIf.push(sinRet);}
break;
case 41:
//#line 79 "gramatica.y"
{if(sinRetIf.pop()) {sinRet=true;};}
break;
case 42:
//#line 82 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un bloque IF-ELSE");}
break;
case 43:
//#line 83 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un bloque IF");}
break;
case 44:
//#line 84 "gramatica.y"
{enDo=false;}
break;
case 45:
//#line 84 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(1).ival + ": Se detectó un bloque DO UNTIL");}
break;
case 46:
//#line 86 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta la palabra ENDIF al final del bloque IF-ELSE");}
break;
case 47:
//#line 87 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta la palabra ENDIF al final del bloque IF");}
break;
case 48:
//#line 88 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el cuerpo en el bloque ELSE");}
break;
case 49:
//#line 89 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(3).ival + ": falta el cuerpo en el bloque IF");}
break;
case 50:
//#line 90 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el cuerpo en el bloque IF");}
break;
case 51:
//#line 91 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el cuerpo en los bloques IF y ELSE");}
break;
case 52:
//#line 93 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(2).ival + ": falta de cuerpo en iteracion");}
break;
case 53:
//#line 94 "gramatica.y"
{enDo=false;}
break;
case 54:
//#line 94 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(2).ival + ": falta la palabra UNTIL en la iteracion");}
break;
case 57:
//#line 101 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta de argumento en sentencia print");}
break;
case 59:
//#line 105 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta el paréntesis de apertura de la condición");}
break;
case 60:
//#line 106 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta el paréntesis de cierre de la condición");}
break;
case 61:
//#line 107 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": faltan ambos paréntesis de la condición");}
break;
case 63:
//#line 111 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": La condición es incompleta");}
break;
case 76:
//#line 135 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó una función invocada correctamente");}
break;
case 78:
//#line 139 "gramatica.y"
{ constantePositiva(val_peek(1)); }
break;
case 79:
//#line 140 "gramatica.y"
{ constanteNegativa(val_peek(1)); }
break;
case 85:
//#line 152 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(2).ival + ": falta el paréntesis de cierre de la función TRUNC");}
break;
case 86:
//#line 153 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(4).ival + ": falta el paréntesis de apertura de la función TRUNC");}
break;
case 87:
//#line 154 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(2).ival + ": faltan ambos paréntesis de la función TRUNC");}
break;
case 89:
//#line 156 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(2).ival + ": falta llave de cierre del cuerpo de la función Lambda");}
break;
case 90:
//#line 157 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(4).ival + ": falta llave de apertura del cuerpo de la función Lambda");}
break;
case 91:
//#line 158 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(3).ival + ": faltan ambas llaves del cuerpo de la función Lambda");}
break;
case 92:
//#line 159 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": No se especifica el parametro formal al que corresponde el parámetro real");}
break;
case 93:
//#line 160 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": No se especifica el parametro formal al que corresponde el parámetro real");}
break;
case 94:
//#line 161 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": No se especifica el parametro formal al que corresponde el parámetro real");}
break;
case 99:
//#line 170 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el nombre del parámetro");}
break;
case 100:
//#line 171 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta el nombre del parámetro");}
break;
case 101:
//#line 172 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el tipo del parámetro");}
break;
case 102:
//#line 173 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta el tipo del parámetro");}
break;
case 105:
//#line 178 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la izquierda de '+'"); }
break;
case 106:
//#line 179 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la derecha de '+'"); }
break;
case 107:
//#line 180 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la izquierda de '-'"); }
break;
case 108:
//#line 181 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la derecha de '-'"); }
break;
case 112:
//#line 187 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la izquierda de '*'"); }
break;
case 113:
//#line 188 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la derecha de '*'"); }
break;
case 114:
//#line 189 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la izquierda de '/'"); }
break;
case 115:
//#line 190 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la derecha de '/'"); }
break;
case 116:
//#line 191 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(2).ival + ": falta operador entre operandos o falta de comparador en condición"); }
break;
case 118:
//#line 195 "gramatica.y"
{ constantePositiva(val_peek(0)); }
break;
case 119:
//#line 196 "gramatica.y"
{ constanteNegativa(val_peek(0)); }
break;
case 122:
//#line 201 "gramatica.y"
{ constantePositiva(val_peek(0)); }
break;
case 123:
//#line 202 "gramatica.y"
{ constanteNegativa(val_peek(0)); }
break;
case 124:
//#line 203 "gramatica.y"
{ constantePositiva(val_peek(0)); }
break;
case 125:
//#line 204 "gramatica.y"
{ constanteNegativa(val_peek(0)); }
break;
case 126:
//#line 205 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta ',' entre constantes"); constantePositiva(val_peek(0)); }
break;
case 127:
//#line 206 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(2).ival + ": falta ',' entre constantes"); constanteNegativa(val_peek(0)); }
break;
//#line 1200 "Parser.java"
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
