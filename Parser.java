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
boolean enParamInvocacion = false;

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
   12,   13,   13,   17,   17,   14,   14,   30,   30,   30,
   30,   30,   30,   33,    8,   34,   10,   35,   10,   36,
   10,   37,   10,   38,   10,   31,   32,   39,   39,   40,
   40,   40,   40,   40,   40,   40,   16,   16,   16,   16,
   16,   16,   16,   41,   41,   41,   41,   41,   41,   41,
   41,   42,   42,   42,   42,   42,   42,   18,   18,   18,
   18,   18,   18,
};
final static short yylen[] = {                            2,
    5,    4,    4,    4,    4,    3,    3,    3,    2,    7,
    2,    1,    2,    2,    1,    1,    3,    2,    2,    1,
    1,    1,    1,    1,    1,    1,    2,    0,    9,    4,
    3,    3,    3,    3,    2,    2,    0,    2,    1,    0,
    2,    6,    4,    0,    5,    5,    3,    5,    5,    3,
    4,    3,    0,    4,    4,    4,    3,    3,    2,    2,
    1,    3,    1,    1,    1,    1,    1,    1,    1,    1,
    1,    3,    1,    1,    3,    3,    1,    3,    2,    2,
    1,    2,    1,    0,    4,    0,    4,    0,    4,    0,
    5,    0,    7,    0,    8,    2,    1,    3,    1,    3,
    9,    8,    8,    7,    8,    2,    3,    3,    3,    3,
    3,    3,    1,    3,    3,    3,    3,    3,    3,    3,
    1,    1,    2,    1,    1,    4,    5,    3,    4,    1,
    2,    2,    3,
};
final static short yydefred[] = {                         0,
    0,    0,   70,    0,    0,    0,   71,    0,    0,    0,
   12,    0,    0,   21,   22,   23,   24,   25,   26,    0,
    0,    0,   37,    0,    0,    0,   96,    0,    0,  122,
    0,    0,    0,  125,    0,    0,    0,    0,    0,    0,
  121,    0,    0,    0,    0,    9,    0,   11,   13,   14,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,   99,    0,    0,
    0,    6,    0,    0,    0,    0,    0,    0,    0,  123,
   66,   67,   68,   69,   64,   65,    0,    0,    0,    0,
   50,    0,   59,    0,    0,    0,   31,    0,   52,    0,
   57,    0,    7,    0,    8,    0,  130,    0,    0,   74,
    0,    0,   75,    0,   53,    0,    0,   38,   88,    0,
    0,    0,    0,   86,   84,    0,    0,    2,    4,    0,
    5,    0,    0,    0,  116,  118,    0,    0,   58,    0,
    0,    0,    0,    0,   51,    0,   37,    0,   43,  120,
  117,  114,  119,  115,   30,   56,   55,    3,   83,    0,
    0,    0,   77,  131,  132,    0,    0,   44,   54,   36,
    0,    0,   35,   89,    0,    0,    0,   90,  100,   87,
   85,   98,    0,    1,  126,    0,   49,   41,   48,    0,
   82,    0,   79,   28,    0,  128,    0,  133,   45,   18,
   34,    0,    0,    0,    0,   91,    0,  127,   42,   78,
    0,   76,  129,   17,   92,    0,    0,    0,   10,    0,
   93,   94,    0,    0,    0,    0,   95,    0,    0,  104,
    0,   29,  102,    0,  103,  101,
};
final static short yydgoto[] = {                          9,
   10,   11,   12,   13,  172,   14,   15,   34,   17,   18,
   19,   20,   21,  162,  211,   35,   36,  109,  118,   58,
   59,   23,  146,  147,   37,  199,  169,   38,   89,  163,
   39,   66,  181,  180,  174,  206,  221,  227,   67,   68,
   40,   41,
};
final static short yysindex[] = {                       640,
  158,  101,    0,  -27, -263,   -4,    0,  925,    0,  726,
    0,   22,   41,    0,    0,    0,    0,    0,    0, -207,
  -36,  -29,    0,  122,   50,  829,    0,  747,   56,    0,
   65,  206, -135,    0,  503,   79,  -62,   86,  129,   28,
    0,  145,  101,  142,  765,    0, -142,    0,    0,    0,
  102,  111,   79,  -18, -112,  206,  -98,   98,  -72,  127,
  132, -210,  -83,  -33,  -12,  137,  138,    0,  125,  -88,
  779,    0,  -85,  210,  210,  232,  232,    4,  153,    0,
    0,    0,    0,    0,    0,    0,  383,  410,  206,  -60,
    0,  -50,    0,  232,  416,  424,    0,   15,    0,  166,
    0,   73,    0,  -64,    0, -184,    0,  -44,   -6,    0,
   79,   31,    0,  101,    0,  903,  174,    0,    0,   27,
  -40,  193,   -1,    0,    0,  129,  925,    0,    0,    5,
    0,  -10,   28,   28,    0,    0,  256,   43,    0,  -10,
   28,  -10,   28,   31,    0,   42,    0,   44,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0, -145,
   62,   95,    0,    0,    0,   49,   64,    0,    0,    0,
  264,  918,    0,    0,  287,   71,  303,    0,    0,    0,
    0,    0,  843,    0,    0,  304,    0,    0,    0,   87,
    0,   94,    0,    0, -184,    0,  106,    0,    0,    0,
    0,  294,  322,  324,  607,    0,  104,    0,    0,    0,
  248,    0,    0,    0,    0,  334, -103,  284,    0,  925,
    0,    0,  505,  126,  121,  857,    0,  148,  123,    0,
  151,    0,    0,  155,    0,    0,
};
final static short yyrindex[] = {                         0,
  -30,    0,    0,    0,  740,    0,    0,    0,    0,    0,
    0,  801,  815,    0,    0,    0,    0,    0,    0,    0,
    0,   -2,    0,    0,  -41,    0,    0,    0,    0,    0,
    0,    0,    0,    0,  237,  -19,  794,  526,    0,    3,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
  215,  712,  282,    0,    0,    0,    0,    0,    0,  105,
    0,    0,    0,    0,  134,    0,  373,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,  593,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,  875,
    0,  475,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,  562,    0,
  377,  675,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  163,   96,    0,    0,    0,    0,    0,    0,    0,
    0,    0,   26,   48,    0,    0,    0,    0,    0,   70,
  307,  329,  351,  259,    0,    0,    0,  875,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
  112,    0,    0,    0,    0,    0,    0,    0,    0,    0,
  881,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,  698,
    0,  119,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  901,  399,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,  406,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,  120,    0,    0,
};
final static short yygindex[] = {                         0,
   53,  710,  -14,    0, -153,    0,    0,  701,    0,    0,
    0,  -51,  402,    0,    0,   -9,  669,    0,    0,  -28,
    0,    0,  275,    0,   10,    0,    0,  392,    0,  234,
  705,    0,    0,    0,    0,    0,    0,    0,    0,  311,
   46,    1,
};
final static int YYTABLESIZE=1201;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         74,
   74,   74,   74,   74,   74,   74,   43,   55,   92,   88,
  121,   87,   42,   74,   64,   74,   57,   74,   74,   74,
   74,  124,  124,  124,   54,  124,  108,  124,  124,   64,
   74,   76,   98,   57,  102,   44,   77,  166,  167,  124,
  124,   73,  124,  113,  117,  113,  112,  113,  138,   51,
  116,  218,   99,   28,  161,  155,    3,   88,   73,   87,
   45,  113,  113,  223,  113,    7,  111,  115,  111,   95,
  111,  176,  159,   88,   96,   87,  135,  136,   71,  144,
   49,   74,    3,   74,  111,  111,  160,  111,  109,   27,
  109,    7,  109,  197,  150,  152,  154,   76,   75,   50,
   74,  171,   77,  124,   78,  124,  109,  109,  192,  109,
  112,  191,  112,  157,  112,   88,   64,   87,  188,  133,
  134,    3,   80,  168,   57,  113,   93,  113,  112,  112,
    7,  112,  141,  143,  105,  194,  106,   32,  195,  106,
   32,  106,   33,  161,  110,   33,  122,  122,  111,  122,
  111,  122,   81,   25,   55,   81,    2,  202,  113,   80,
  105,   62,   80,  105,    4,    5,   63,  119,   62,    6,
  109,  120,  109,   33,  122,  124,  124,  125,  124,  183,
  124,  126,  101,  127,   25,   97,   33,    2,  128,   33,
  171,  131,  112,  139,  112,    4,    5,   27,   90,   91,
    6,  145,  171,  202,  123,  123,  156,  123,  202,  123,
  148,  149,  158,  164,   74,   74,  177,   74,   74,   74,
   74,   74,   74,   74,   74,   74,   74,   74,   74,   56,
   74,   74,  173,  178,   74,   74,  124,  124,  123,  107,
  124,  124,  124,  124,  124,  124,  124,  124,  124,  124,
   33,  165,  124,  124,   33,  179,  124,  124,   74,  113,
   74,  137,  113,  113,  113,  113,  113,  113,  113,  113,
  113,  113,  226,   74,  113,  113,   33,   63,  113,  113,
   26,  184,  111,   94,  175,  111,  111,  111,  111,  111,
  111,  111,  111,  111,  111,   63,  185,  111,  111,   62,
  186,  111,  111,  187,  109,  189,  196,  109,  109,  109,
  109,  109,  109,  109,  109,  109,  109,   62,  193,  109,
  109,  198,  200,  109,  109,   73,  112,  203,  204,  112,
  112,  112,  112,  112,  112,  112,  112,  112,  112,   74,
   73,  112,  112,  205,  208,  112,  112,  108,  209,  108,
  210,  108,  214,   29,   25,   30,   29,   25,   30,   63,
  122,   63,  215,  213,  216,  108,  108,  114,  108,  110,
  220,  110,   31,  110,  222,   31,  122,   29,   25,   60,
  219,   62,  230,   62,   29,   25,   30,  110,  110,  124,
  110,  107,  231,  107,  234,  107,   61,   29,   25,   30,
   29,   25,   30,   31,  233,  124,   73,  235,  225,  107,
  107,  236,  107,   97,   25,  100,   31,    2,  123,   31,
   72,   52,  190,   79,    3,    4,    5,   33,  212,  108,
    6,  108,    0,    7,  123,   72,  182,   72,    0,    0,
  126,  126,    0,  126,    0,  126,    0,  127,  127,    0,
  127,  110,  127,  110,   33,    0,    0,    0,    0,    0,
   33,   29,   25,   30,    0,  132,   25,   30,   33,    0,
    0,   74,    0,  107,   74,  107,    0,    0,    0,    0,
   31,   74,   74,   74,   31,    0,    0,   74,   25,   30,
   74,   74,    0,   63,    0,    0,   63,   63,   63,    0,
    0,   72,    0,   63,   63,   63,   31,    0,   63,   63,
    0,    0,   63,   63,    0,   62,    0,    0,   62,   62,
   62,    0,    0,    0,    0,   62,   62,   62,    0,    0,
   62,   62,    0,   47,   62,   62,    0,    0,   73,    0,
   25,   73,    0,    2,    0,   88,    0,   87,   73,   73,
   73,    4,    5,    0,   73,  224,    6,   73,   73,    0,
    0,    0,   86,  108,   85,    0,  108,  108,  108,  108,
  108,  108,  108,  108,  108,  108,    0,    0,  108,  108,
    0,    0,  108,  108,   61,  110,    0,    0,  110,  110,
  110,  110,  110,  110,  110,  110,  110,  110,    0,   47,
  110,  110,    0,    0,  110,  110,    0,  107,    0,    0,
  107,  107,  107,  107,  107,  107,  107,  107,  107,  107,
   33,    0,  107,  107,    0,    0,  107,  107,    0,  229,
    0,    0,    0,   72,    0,    0,   72,    0,  140,   25,
   30,    0,    0,   72,   72,   72,    0,    0,   61,   72,
   61,   60,   72,   72,  126,    0,    0,   31,    0,    0,
    0,  127,    0,    0,    0,  142,   25,   30,   22,   22,
  126,  151,   25,   30,    0,    0,   22,  127,   22,  153,
   25,   30,    0,    0,   31,    0,   33,    0,   53,    0,
   31,    0,   65,    0,   22,    0,   22,    0,   31,    0,
   16,   16,    0,    0,   24,   24,    0,    0,   16,    0,
   16,    0,   24,   22,   24,   60,    0,   60,    0,   48,
    0,    0,    0,  111,    0,    0,   16,   22,   16,  217,
   24,   47,   24,   32,   47,    0,    0,   48,    0,   22,
    0,   47,   47,   47,    0,   16,   47,   47,    0,   24,
   47,   47,    0,    0,   48,    0,   46,    0,    0,   16,
    0,   25,    8,   24,    2,   81,   82,   83,   84,    0,
   27,   16,    4,    5,    0,   24,  228,    6,    0,    0,
   48,    0,   61,    0,   22,   61,   61,   61,    0,    0,
    0,    0,   61,   61,   61,   22,    0,   61,   61,   32,
    0,   61,   61,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,   16,    0,   33,    0,
   24,   33,   46,    0,    0,    0,    0,   16,   33,   33,
   33,   24,    0,   33,   33,    0,   27,   33,   33,    0,
   22,    0,    0,    0,    0,    0,    0,    0,    0,   60,
   47,   22,   60,   60,   60,    0,    0,    0,    0,   60,
   60,   60,   39,   25,   60,   60,    2,    0,   60,   60,
    0,   73,   16,   22,    4,    5,   24,    0,    0,    6,
    0,    0,    0,   16,    0,   22,   22,   24,   22,  104,
    0,   22,   48,    0,   22,    0,    1,    0,    0,    2,
    0,    0,    0,  130,    0,   16,    3,    4,    5,   24,
    0,    0,    6,    0,    0,    7,   37,   16,   16,    0,
   16,   24,   24,   16,   24,   15,   16,   24,    0,    0,
   24,   32,    0,    0,   32,   48,    0,    0,    0,   16,
    0,   32,   32,   32,    0,    0,   32,   32,    0,    0,
   32,   32,    0,   70,   46,    0,    0,   46,    0,    0,
    0,    0,    0,    0,   46,   46,   46,  207,   27,   46,
   46,   27,    0,   46,   46,    0,    0,    0,   27,   27,
   27,  232,   25,    0,   27,    2,    0,   27,   27,    0,
    0,    0,    3,    4,    5,    0,   39,   40,    6,   39,
    0,    7,   46,   25,    0,   20,    2,   39,   39,    0,
    0,    0,   39,    3,    4,    5,    0,    0,    0,    6,
    0,   25,    7,   72,    2,   19,    0,  170,    0,    0,
    0,    3,    4,    5,    0,   25,    0,    6,    2,    0,
    7,  103,  201,    0,    0,    3,    4,    5,    0,    0,
   37,    6,    0,   37,    7,  129,    0,   15,    0,    0,
   15,   37,   37,    0,    0,    0,   37,   15,   15,   15,
    0,   16,    0,   15,   16,    0,   15,   15,    0,    0,
    0,   16,   16,   16,   69,   25,    0,   16,    2,    0,
   16,   16,    0,    0,    0,    3,    4,    5,    0,   25,
    0,    6,    2,    0,    7,    0,    0,    0,    0,    3,
    4,    5,    0,   25,    0,    6,    2,    0,    7,    0,
    0,    0,    0,    3,    4,    5,    0,    0,    0,    6,
    0,   40,    7,    0,   40,    0,    0,   20,    0,    0,
   20,    0,   40,   40,    0,    0,    0,   40,   20,   20,
    0,    0,   20,   20,    0,    0,    0,   19,    0,   25,
   19,    0,    2,    0,    0,    0,    0,    0,   19,   19,
    4,    5,   19,   19,   25,    6,    0,    2,    0,    0,
    0,   25,    0,    0,    2,    4,    5,    0,    0,    0,
    6,    3,    4,    5,    0,    0,    0,    6,    0,    0,
    7,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         41,
   42,   43,   44,   45,   46,   47,  270,   44,   37,   43,
   62,   45,   40,   44,   24,   46,   46,   59,   60,   61,
   62,   41,   42,   43,   61,   45,   45,   47,   41,   39,
   61,   42,   42,   46,   44,   40,   47,   44,   45,   59,
   60,   44,   62,   41,   59,   43,   56,   45,   45,  257,
  123,  205,   43,    1,  106,   41,  267,   43,   61,   45,
    8,   59,   60,  217,   62,  276,   41,   58,   43,   42,
   45,   45,  257,   43,   47,   45,   76,   77,   26,   89,
   59,  123,  267,  125,   59,   60,  271,   62,   41,   40,
   43,  276,   45,   45,   94,   95,   96,   42,   43,   59,
   45,  116,   47,  123,   40,  125,   59,   60,  160,   62,
   41,  257,   43,   41,   45,   43,  126,   45,  147,   74,
   75,  267,  258,  114,   46,  123,   41,  125,   59,   60,
  276,   62,   87,   88,  277,   41,   41,   40,   44,   44,
   40,   40,   45,  195,  257,   45,   42,   43,  123,   45,
  125,   47,   41,  257,   44,   44,  260,  172,  257,   41,
   41,   40,   44,   44,  268,  269,   45,   41,   40,  273,
  123,   40,  125,   45,  258,   42,   43,   41,   45,  127,
   47,   44,   41,   59,  257,   41,   45,  260,  277,   45,
  205,  277,  123,   41,  125,  268,  269,   40,  261,  262,
  273,  262,  217,  218,   42,   43,   41,   45,  223,   47,
  261,  262,  277,  258,  256,  257,  257,  259,  260,  261,
  262,  263,  264,  265,  266,  267,  268,  269,  259,  259,
  272,  273,   59,   41,  276,  277,  256,  257,  272,  258,
  260,  261,  262,  263,  264,  265,  266,  267,  268,  269,
   45,  258,  272,  273,   45,  257,  276,  277,   44,  257,
   46,  258,  260,  261,  262,  263,  264,  265,  266,  267,
  268,  269,  220,   59,  272,  273,   45,   41,  276,  277,
  123,  277,  257,  256,  258,  260,  261,  262,  263,  264,
  265,  266,  267,  268,  269,   59,   41,  272,  273,   41,
  258,  276,  277,  262,  257,  262,  258,  260,  261,  262,
  263,  264,  265,  266,  267,  268,  269,   59,  257,  272,
  273,  258,   59,  276,  277,   44,  257,   41,  258,  260,
  261,  262,  263,  264,  265,  266,  267,  268,  269,  125,
   59,  272,  273,   41,   41,  276,  277,   41,  262,   43,
  257,   45,   59,  256,  257,  258,  256,  257,  258,  123,
  256,  125,   41,  258,   41,   59,   60,  270,   62,   41,
  123,   43,  275,   45,   41,  275,  272,  256,  257,  258,
  277,  123,  257,  125,  256,  257,  258,   59,   60,  256,
   62,   41,  272,   43,  272,   45,  275,  256,  257,  258,
  256,  257,  258,  275,  257,  272,  125,  257,  125,   59,
   60,  257,   62,   41,  257,  274,  275,  260,  256,  275,
   44,   20,  148,   32,  267,  268,  269,   45,  195,  123,
  273,  125,   -1,  276,  272,   59,  126,   61,   -1,   -1,
   42,   43,   -1,   45,   -1,   47,   -1,   42,   43,   -1,
   45,  123,   47,  125,   45,   -1,   -1,   -1,   -1,   -1,
   45,  256,  257,  258,   -1,  256,  257,  258,   45,   -1,
   -1,  257,   -1,  123,  260,  125,   -1,   -1,   -1,   -1,
  275,  267,  268,  269,  275,   -1,   -1,  273,  257,  258,
  276,  277,   -1,  257,   -1,   -1,  260,  261,  262,   -1,
   -1,  125,   -1,  267,  268,  269,  275,   -1,  272,  273,
   -1,   -1,  276,  277,   -1,  257,   -1,   -1,  260,  261,
  262,   -1,   -1,   -1,   -1,  267,  268,  269,   -1,   -1,
  272,  273,   -1,   59,  276,  277,   -1,   -1,  257,   -1,
  257,  260,   -1,  260,   -1,   43,   -1,   45,  267,  268,
  269,  268,  269,   -1,  273,  272,  273,  276,  277,   -1,
   -1,   -1,   60,  257,   62,   -1,  260,  261,  262,  263,
  264,  265,  266,  267,  268,  269,   -1,   -1,  272,  273,
   -1,   -1,  276,  277,   59,  257,   -1,   -1,  260,  261,
  262,  263,  264,  265,  266,  267,  268,  269,   -1,  125,
  272,  273,   -1,   -1,  276,  277,   -1,  257,   -1,   -1,
  260,  261,  262,  263,  264,  265,  266,  267,  268,  269,
   59,   -1,  272,  273,   -1,   -1,  276,  277,   -1,  125,
   -1,   -1,   -1,  257,   -1,   -1,  260,   -1,  256,  257,
  258,   -1,   -1,  267,  268,  269,   -1,   -1,  123,  273,
  125,   59,  276,  277,  256,   -1,   -1,  275,   -1,   -1,
   -1,  256,   -1,   -1,   -1,  256,  257,  258,    0,    1,
  272,  256,  257,  258,   -1,   -1,    8,  272,   10,  256,
  257,  258,   -1,   -1,  275,   -1,  125,   -1,   20,   -1,
  275,   -1,   24,   -1,   26,   -1,   28,   -1,  275,   -1,
    0,    1,   -1,   -1,    0,    1,   -1,   -1,    8,   -1,
   10,   -1,    8,   45,   10,  123,   -1,  125,   -1,   10,
   -1,   -1,   -1,   55,   -1,   -1,   26,   59,   28,  123,
   26,  257,   28,   59,  260,   -1,   -1,   28,   -1,   71,
   -1,  267,  268,  269,   -1,   45,  272,  273,   -1,   45,
  276,  277,   -1,   -1,   45,   -1,   59,   -1,   -1,   59,
   -1,  257,  123,   59,  260,  263,  264,  265,  266,   -1,
   59,   71,  268,  269,   -1,   71,  272,  273,   -1,   -1,
   71,   -1,  257,   -1,  116,  260,  261,  262,   -1,   -1,
   -1,   -1,  267,  268,  269,  127,   -1,  272,  273,  125,
   -1,  276,  277,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,  116,   -1,  257,   -1,
  116,  260,  125,   -1,   -1,   -1,   -1,  127,  267,  268,
  269,  127,   -1,  272,  273,   -1,  125,  276,  277,   -1,
  172,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  257,
  125,  183,  260,  261,  262,   -1,   -1,   -1,   -1,  267,
  268,  269,  123,  257,  272,  273,  260,   -1,  276,  277,
   -1,  125,  172,  205,  268,  269,  172,   -1,   -1,  273,
   -1,   -1,   -1,  183,   -1,  217,  218,  183,  220,  125,
   -1,  223,  183,   -1,  226,   -1,  257,   -1,   -1,  260,
   -1,   -1,   -1,  125,   -1,  205,  267,  268,  269,  205,
   -1,   -1,  273,   -1,   -1,  276,  123,  217,  218,   -1,
  220,  217,  218,  223,  220,  125,  226,  223,   -1,   -1,
  226,  257,   -1,   -1,  260,  226,   -1,   -1,   -1,  125,
   -1,  267,  268,  269,   -1,   -1,  272,  273,   -1,   -1,
  276,  277,   -1,  125,  257,   -1,   -1,  260,   -1,   -1,
   -1,   -1,   -1,   -1,  267,  268,  269,  125,  257,  272,
  273,  260,   -1,  276,  277,   -1,   -1,   -1,  267,  268,
  269,  125,  257,   -1,  273,  260,   -1,  276,  277,   -1,
   -1,   -1,  267,  268,  269,   -1,  257,  123,  273,  260,
   -1,  276,  277,  257,   -1,  125,  260,  268,  269,   -1,
   -1,   -1,  273,  267,  268,  269,   -1,   -1,   -1,  273,
   -1,  257,  276,  277,  260,  125,   -1,  125,   -1,   -1,
   -1,  267,  268,  269,   -1,  257,   -1,  273,  260,   -1,
  276,  277,  125,   -1,   -1,  267,  268,  269,   -1,   -1,
  257,  273,   -1,  260,  276,  277,   -1,  257,   -1,   -1,
  260,  268,  269,   -1,   -1,   -1,  273,  267,  268,  269,
   -1,  257,   -1,  273,  260,   -1,  276,  277,   -1,   -1,
   -1,  267,  268,  269,  256,  257,   -1,  273,  260,   -1,
  276,  277,   -1,   -1,   -1,  267,  268,  269,   -1,  257,
   -1,  273,  260,   -1,  276,   -1,   -1,   -1,   -1,  267,
  268,  269,   -1,  257,   -1,  273,  260,   -1,  276,   -1,
   -1,   -1,   -1,  267,  268,  269,   -1,   -1,   -1,  273,
   -1,  257,  276,   -1,  260,   -1,   -1,  257,   -1,   -1,
  260,   -1,  268,  269,   -1,   -1,   -1,  273,  268,  269,
   -1,   -1,  272,  273,   -1,   -1,   -1,  257,   -1,  257,
  260,   -1,  260,   -1,   -1,   -1,   -1,   -1,  268,  269,
  268,  269,  272,  273,  257,  273,   -1,  260,   -1,   -1,
   -1,  257,   -1,   -1,  260,  268,  269,   -1,   -1,   -1,
  273,  267,  268,  269,   -1,   -1,   -1,  273,   -1,   -1,
  276,
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
"listaparametros : listaparametros ',' parametro",
"listaparametros : parametro",
"parametro : CV tipo ID",
"parametro : tipo ID",
"parametro : CV tipo",
"parametro : tipo",
"parametro : CV ID",
"parametro : ID",
"$$6 :",
"funcion : cabecera_funcion argumentos_funcion ')' $$6",
"$$7 :",
"funcion_lambda : cabecera_funcion identificador ')' $$7",
"$$8 :",
"funcion_lambda : cabecera_funcion CTE ')' $$8",
"$$9 :",
"funcion_lambda : cabecera_funcion '-' CTE ')' $$9",
"$$10 :",
"funcion_lambda : cabecera_funcion TRUNC '(' CTE ')' ')' $$10",
"$$11 :",
"funcion_lambda : cabecera_funcion TRUNC '(' '-' CTE ')' ')' $$11",
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

//#line 220 "gramatica.y"

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

/*private void chequeo_anid_lambda(String nombreLambda, ParserVal yyval) {
  if (nombreLambda.contains(".")) {
    erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + yyval.ival + ": Se ha intentado invocar una función lambda perteneciente a otro ámbito");
  }
}*/
//#line 764 "Parser.java"
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
//#line 22 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(3).ival + ": falta el nombre del programa");}
break;
case 4:
//#line 23 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta la llave de cierre del cuerpo del programa");}
break;
case 5:
//#line 24 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(3).ival + ": falta la llave de apertura del cuerpo del programa");}
break;
case 6:
//#line 25 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": faltan ambas llaves en cuerpo del programa");}
break;
case 7:
//#line 26 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el nombre del programa y la llave de cierre del cuerpo del programa");}
break;
case 8:
//#line 27 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(2).ival + ": falta el nombre del programa y la llave de apertura del cuerpo del programa");}
break;
case 9:
//#line 28 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el nombre del programa y ambas llaves en cuerpo del programa");}
break;
case 10:
//#line 29 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico: el programa no es válido");}
break;
case 15:
//#line 40 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta ; al final de la sentencia ejecutable");}
break;
case 16:
//#line 41 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta ; al final de la declaración");}
break;
case 19:
//#line 46 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta ; al final de la sentencia ejecutable");}
break;
case 20:
//#line 47 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta ; al final de la sentencia ejecutable");}
break;
case 24:
//#line 53 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un print");}
break;
case 25:
//#line 54 "gramatica.y"
{if(enFunc==0) erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": Invocacion de funcion lambda fuera de una función");}
break;
case 26:
//#line 55 "gramatica.y"
{chequeoSentenciaReturn(val_peek(0));}
break;
case 27:
//#line 58 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó una declaración de variables");}
break;
case 28:
//#line 59 "gramatica.y"
{enFunc++;}
break;
case 29:
//#line 59 "gramatica.y"
{finFunc(val_peek(1)); System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(1).ival + ": Se detectó una declaración de función");}
break;
case 30:
//#line 62 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(1).ival + ": Se detectó una sentencia return");}
break;
case 31:
//#line 63 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta de argumento en sentencia return");}
break;
case 32:
//#line 66 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó una asignación simple");}
break;
case 33:
//#line 67 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó una asignación múltiple");}
break;
case 37:
//#line 75 "gramatica.y"
{inicioIf();}
break;
case 38:
//#line 75 "gramatica.y"
{finIf(val_peek(1));}
break;
case 39:
//#line 78 "gramatica.y"
{enDo=true;}
break;
case 40:
//#line 81 "gramatica.y"
{sinRetIf.push(sinRet);}
break;
case 41:
//#line 81 "gramatica.y"
{if(sinRetIf.pop()) {sinRet=true;};}
break;
case 42:
//#line 84 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un bloque IF-ELSE");}
break;
case 43:
//#line 85 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un bloque IF");}
break;
case 44:
//#line 86 "gramatica.y"
{enDo=false;}
break;
case 45:
//#line 86 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(1).ival + ": Se detectó un bloque DO UNTIL");}
break;
case 46:
//#line 88 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta la palabra ENDIF al final del bloque IF-ELSE");}
break;
case 47:
//#line 89 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta la palabra ENDIF al final del bloque IF");}
break;
case 48:
//#line 90 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el cuerpo en el bloque ELSE");}
break;
case 49:
//#line 91 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(3).ival + ": falta el cuerpo en el bloque IF");}
break;
case 50:
//#line 92 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el cuerpo en el bloque IF");}
break;
case 51:
//#line 93 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el cuerpo en los bloques IF y ELSE");}
break;
case 52:
//#line 95 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(2).ival + ": falta de cuerpo en iteracion");}
break;
case 53:
//#line 96 "gramatica.y"
{enDo=false;}
break;
case 54:
//#line 96 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(2).ival + ": falta la palabra UNTIL en la iteracion");}
break;
case 57:
//#line 103 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta de argumento en sentencia print");}
break;
case 59:
//#line 107 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta el paréntesis de apertura de la condición");}
break;
case 60:
//#line 108 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta el paréntesis de cierre de la condición");}
break;
case 61:
//#line 109 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": faltan ambos paréntesis de la condición");}
break;
case 63:
//#line 113 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": La condición es incompleta");}
break;
case 80:
//#line 143 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el nombre del parámetro");}
break;
case 81:
//#line 144 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta el nombre del parámetro");}
break;
case 82:
//#line 145 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el tipo del parámetro");}
break;
case 83:
//#line 146 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta el tipo del parámetro");}
break;
case 84:
//#line 149 "gramatica.y"
{enParamInvocacion = false;}
break;
case 85:
//#line 149 "gramatica.y"
{ System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(1).ival + ": Se detectó la invocación de una función");}
break;
case 86:
//#line 152 "gramatica.y"
{enParamInvocacion = false;}
break;
case 87:
//#line 152 "gramatica.y"
{ /*chequeo_anid_lambda($1.sval, $3);*/ System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(1).ival + ": Se detectó la invocación de una función lambda");}
break;
case 88:
//#line 153 "gramatica.y"
{enParamInvocacion = false;}
break;
case 89:
//#line 153 "gramatica.y"
{ /*chequeo_anid_lambda($1.sval, $3)*/ ; constantePositiva(val_peek(2)); System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(1).ival + ": Se detectó la invocación de una función lambda");}
break;
case 90:
//#line 154 "gramatica.y"
{enParamInvocacion = false;}
break;
case 91:
//#line 154 "gramatica.y"
{ /*chequeo_anid_lambda($1.sval, $4)*/ ; constanteNegativa(val_peek(2)); System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(1).ival + ": Se detectó la invocación de una función lambda");}
break;
case 92:
//#line 155 "gramatica.y"
{enParamInvocacion = false;}
break;
case 93:
//#line 155 "gramatica.y"
{ constantePositiva(val_peek(3)); System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(1).ival + ": Se detectó la invocación de una función lambda");}
break;
case 94:
//#line 156 "gramatica.y"
{enParamInvocacion = false;}
break;
case 95:
//#line 156 "gramatica.y"
{ constanteNegativa(val_peek(3)); System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(1).ival + ": Se detectó la invocación de una función lambda");}
break;
case 96:
//#line 159 "gramatica.y"
{enParamInvocacion = true;}
break;
case 102:
//#line 175 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(2).ival + ": falta llave de cierre del cuerpo de la función Lambda");}
break;
case 103:
//#line 176 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(4).ival + ": falta llave de apertura del cuerpo de la función Lambda");}
break;
case 104:
//#line 177 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(3).ival + ": faltan ambas llaves del cuerpo de la función Lambda");}
break;
case 105:
//#line 178 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": No se especifica el parametro formal al que corresponde el parámetro real");}
break;
case 106:
//#line 179 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": No se especifica el parametro formal al que corresponde el parámetro real");}
break;
case 107:
//#line 183 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(1).ival + ": Se detectó una expresión desde una suma");}
break;
case 108:
//#line 184 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(1).ival + ": Se detectó una expresión desde una resta");}
break;
case 109:
//#line 185 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la izquierda de '+'"); }
break;
case 110:
//#line 186 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la derecha de '+'"); }
break;
case 111:
//#line 187 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la izquierda de '-'"); }
break;
case 112:
//#line 188 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la derecha de '-'"); }
break;
case 113:
//#line 189 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó una expresión desde un término");}
break;
case 114:
//#line 192 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(1).ival + ": Se detectó un término desde una multiplicación");}
break;
case 115:
//#line 193 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(1).ival + ": Se detectó un término desde una división");}
break;
case 116:
//#line 194 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la izquierda de '*'"); }
break;
case 117:
//#line 195 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la derecha de '*'"); }
break;
case 118:
//#line 196 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la izquierda de '/'"); }
break;
case 119:
//#line 197 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la derecha de '/'"); }
break;
case 120:
//#line 198 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(2).ival + ": falta operador entre operandos o falta de comparador en condición"); }
break;
case 121:
//#line 199 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un término desde un factor");}
break;
case 122:
//#line 202 "gramatica.y"
{ constantePositiva(val_peek(0)); System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un factor desde una constante");}
break;
case 123:
//#line 203 "gramatica.y"
{ constanteNegativa(val_peek(0)); System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un factor desde una constante negativa");}
break;
case 124:
//#line 204 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un factor desde un identificador");}
break;
case 125:
//#line 205 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un factor desde una función");}
break;
case 126:
//#line 206 "gramatica.y"
{ constantePositiva(val_peek(1)); if (enParamInvocacion) {System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(3).ival + ": Se detectó la función Trunc en uno de los parámetros reales de la invocación");} else erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(3).ival + ": la función TRUNC solo puede utilizarse en parámetros reales de invocación de funciones");}
break;
case 127:
//#line 207 "gramatica.y"
{ constanteNegativa(val_peek(1)); if (enParamInvocacion) {System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(4).ival + ": Se detectó la función Trunc en uno de los parámetros reales de la invocación");} else erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(4).ival + ": la función TRUNC solo puede utilizarse en parámetros reales de invocación de funciones");}
break;
case 128:
//#line 210 "gramatica.y"
{ constantePositiva(val_peek(0)); }
break;
case 129:
//#line 211 "gramatica.y"
{ constanteNegativa(val_peek(0)); }
break;
case 130:
//#line 212 "gramatica.y"
{ constantePositiva(val_peek(0)); }
break;
case 131:
//#line 213 "gramatica.y"
{ constanteNegativa(val_peek(0)); }
break;
case 132:
//#line 214 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta ',' entre constantes"); constantePositiva(val_peek(0)); }
break;
case 133:
//#line 215 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(2).ival + ": falta ',' entre constantes"); constanteNegativa(val_peek(0)); }
break;
//#line 1289 "Parser.java"
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
