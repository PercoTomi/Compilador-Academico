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
//#line 22 "Parser.java"




public class Parser
{

boolean yydebug;        //do I want debug output?
int yynerrs;            //number of errors so far
int yyerrflag;          //was there an error?
int yychar;             //the current working character

Lexico lexer;
Map<String, Simbolo> tablaSimbolos;

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
public final static short YYERRCODE=256;
final static short yylhs[] = {                           -1,
    0,    0,    0,    0,    0,    1,    1,    1,    2,    2,
    2,    2,    2,    4,    4,    3,    3,   13,   13,   13,
    5,    5,    5,    5,    5,    5,    5,    5,    5,    5,
    5,    5,    5,    5,    5,    5,    5,    5,    5,    5,
    6,    6,    6,   14,   14,   15,   15,   15,   15,   15,
   15,    8,    8,   12,   12,    9,    9,    9,    9,   10,
   10,   16,   16,   16,   16,   16,   16,   11,   11,   11,
   11,   11,   11,   11,   11,   17,   17,   17,   17,   17,
   17,   17,   18,   18,   18,    7,    7,    7,    7,   19,
   19,   20,   20,   20,   20,   20,   20,   20,   20,   20,
   20,   20,
};
final static short yylen[] = {                            2,
    4,    3,    3,    3,    2,    3,    2,    1,    1,    1,
    1,    1,    1,    2,   13,    3,    3,    3,    1,    2,
   12,    8,    8,   11,   11,   10,    7,    7,    6,   11,
    7,   11,   11,    7,   10,    7,    7,    6,    7,    7,
    4,    4,    3,    3,    2,    1,    1,    1,    1,    1,
    1,    1,    1,    1,    3,    3,    1,    2,    4,    3,
    1,    3,    2,    2,    1,    2,    1,    3,    3,    1,
    3,    3,    3,    3,    3,    3,    3,    1,    3,    3,
    3,    3,    1,    1,    1,    4,    4,    4,    4,    3,
    1,    3,    6,    5,    5,    4,    7,    6,    6,    5,
    2,    5,
};
final static short yydefred[] = {                         0,
    0,    0,    0,    0,    0,    0,   52,    0,    0,   53,
    0,    0,    0,    9,   10,   11,   12,   13,    0,    0,
    0,    0,    0,    0,    0,   84,    0,   85,    0,    0,
    0,    0,   78,    0,    0,    0,    0,    4,    0,    7,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    2,
    0,    0,    0,    0,   91,    0,    0,    0,    0,    0,
    0,    0,    0,    0,   48,   49,   50,   51,   46,   47,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,   43,    0,    1,    6,    0,   19,    0,   54,    0,
    0,    0,   55,    0,    0,    0,    0,    0,   89,    0,
   88,   87,   86,    0,    0,    0,   81,   82,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,   76,
    0,   77,    0,    0,   42,   41,   67,    0,    0,    0,
   61,   20,    0,    0,    0,    0,    0,    0,   92,   90,
    0,    0,    0,    0,    0,    0,    0,   66,    0,   63,
    0,    0,   18,    0,    0,   96,    0,    0,    0,    0,
    0,    0,   29,    0,    0,    0,    0,    0,   62,    0,
   60,   94,    0,   95,    0,    0,    0,   28,    0,   34,
    0,    0,    0,   27,   39,    0,   36,   40,    0,   93,
    0,   99,    0,    0,    0,   22,    0,    0,   23,    0,
   97,    0,    0,    0,    0,    0,    0,    0,    0,   35,
    0,    0,    0,   26,    0,    0,   25,   33,   32,    0,
   24,    0,   21,    0,   15,
};
final static short yydgoto[] = {                          3,
   12,   13,   14,   15,   16,   17,   18,   19,   20,  130,
   29,   21,   88,   31,   74,  131,   32,   33,   54,   55,
};
final static short yysindex[] = {                      -117,
  269,  659,    0,  -36,  -16,   94,    0, -115,  -14,    0,
  659,  397,  -49,    0,    0,    0,    0,    0, -220,    9,
  -29,  412,  -40,  -37,  504,    0, -104,    0,  167,   17,
   14,  -20,    0,  434,  -34,  448,  -36,    0,  -15,    0,
   21,   10,   17,    0, -182, -176,   56, -104, -174,    0,
   99, -262,   48,  -32,    0,   66,   18,   88,  155,  155,
  203,  203,   63,  161,    0,    0,    0,    0,    0,    0,
  212,  227,  113, -104,  659,  -28,  261,  283, -153,  456,
   80,    0,  -22,    0,    0,  139,    0,  -19,    0,   17,
 -129,  113,    0, -104,  -30, -121,  155,  -90,    0,  -40,
    0,    0,    0,  160,  -20,  -20,    0,    0,  659,   52,
  -20,  160,  -20,  160,  -20,  113,  471,  659,  -36,    0,
  -36,    0,  131,  -38,    0,    0,    0,  -78,  -80,  170,
    0,    0,  -77,    0,  -25,  -65,  -63,  172,    0,    0,
  485,  493,   -6,  508, -104,  106, -104,    0,  -42,    0,
  105,  139,    0,  -21,  -31,    0,    5,  379,  176,  180,
  516,  152,    0,  188,  235, -104,  245,  247,    0,  659,
    0,    0,   40,    0,  659,  534,  187,    0,  219,    0,
  242,  659,  226,    0,    0,  344,    0,    0,  645,    0,
  542,    0,  659,  557,  275,    0,  571,  659,    0,  359,
    0,  586,  154,  594,  608,  159,  623, -104,  182,    0,
  184,  190,  631,    0,  192,   -2,    0,    0,    0,  201,
    0,  355,    0,  309,    0,
};
final static short yyrindex[] = {                         0,
    0,    0,    0,    1,  -13,    0,    0,    0,    0,    0,
    0,  466,    0,    0,    0,    0,    0,    0,    0,    0,
   74,    0,    0,    0,    0,    0,    0,    0,    0,   15,
    0,   49,    0,    0,    0,  474,    0,    0,    0,    0,
    6,  422,    7,   54,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,   -7,  148,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,   65,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  427,    0,   28,
    0,  430,    0,    0,    0,    0,    0,  255,    0,    0,
    0,    0,    0,    0,   60,   71,    0,    0,    0,    0,
   82,  102,  114,  125,  137,  110,    0,    0,   26,    0,
   37,    0,    0,    0,    0,    0,    0,    0,  278,    0,
    0,    0,    0,  104,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,  289,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,  433,    0,    0,    0,
    0,    0,  300,    0,    0,  332,    0,    0,    0,    0,
  436,    0,    0,    0,    0,  439,    0,    0,    0,    0,
  364,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,  447,
    0,    0,    0,    0,    0,
};
final static short yygindex[] = {                         0,
  373,  443,    0,    0,    0,    0,  529,  301,  452,    0,
  476,  416,    0,  281,    0,  360,  437,  395,  483,  414,
};
final static int YYTABLESIZE=935;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         52,
    8,  147,   52,   23,    7,    2,   82,   34,   99,   40,
  137,  100,   71,   10,   72,  155,   49,   71,  126,   72,
   71,   77,   72,   24,  133,   35,   78,   54,   54,   54,
   54,   54,   54,   54,   84,   84,   41,   84,  222,   84,
   71,    8,   72,   85,    8,   54,   54,   54,   54,   54,
   57,   54,   46,   46,   76,   83,   83,   83,  102,   83,
   86,   83,   49,   49,   54,   57,   79,   79,   79,   45,
   79,   56,   79,   83,   83,   87,   83,   80,   80,   80,
   89,   80,   93,   80,   79,   79,   56,   79,   56,   70,
   71,   70,   72,   70,  118,   80,   80,   58,   80,   54,
   73,   91,   73,  110,   73,   45,  101,   70,   70,   54,
   70,   74,   58,   74,   58,   74,  123,   57,   73,   73,
  125,   73,   75,   45,   75,    8,   75,  134,  103,   74,
   74,  100,   74,   27,   57,  138,   75,   83,   94,    1,
   75,   75,   71,   75,   71,  166,   71,   59,   79,   55,
   44,   25,    5,   26,   68,   71,   68,   72,   68,   80,
   71,   71,   59,   71,   59,   72,  139,   72,   44,   72,
  145,   70,   68,   68,  142,   68,  150,   69,  148,   69,
  153,   69,   73,   72,   72,  109,   72,   45,    7,   83,
   83,  156,   83,   74,   83,   69,   69,   10,   69,   23,
   23,   61,   61,   59,   75,   60,   62,   62,  157,   71,
  151,   72,  158,  152,  169,   25,    5,   26,   25,    5,
   56,   25,    5,   26,   71,   97,   70,  170,   69,   48,
   97,  146,   44,   97,   51,  172,   68,   51,  132,   81,
  173,  136,   54,   54,   54,   54,  154,   72,   84,   54,
   54,   54,   54,   97,  162,  163,    8,    8,   54,   69,
    8,  174,   54,   57,   84,   44,   44,    8,    8,    8,
   83,   83,   83,    8,  182,  185,    8,   83,   83,   83,
   83,   79,   79,   79,   56,  187,   83,  188,   79,   79,
   79,   79,   80,   80,   80,  101,  190,   79,  101,   80,
   80,   80,   80,   97,   70,   70,   70,   63,   80,  193,
   58,   70,   70,   70,   70,   73,   73,   73,   65,   98,
   70,   65,   73,   73,   73,   73,   74,   74,   74,   64,
   57,   73,   64,   74,   74,   74,   74,   75,   75,   75,
  102,  194,   74,  102,   75,   75,   75,   75,  198,   25,
    5,   26,   96,   75,   25,    5,   26,   71,   71,   71,
   59,   25,    5,   26,   71,   71,   71,   71,   97,   68,
   68,   68,  100,   71,   22,  100,   68,   68,   68,   68,
   72,   72,   72,   36,  199,   68,  129,   72,   72,   72,
   72,   11,   69,   69,   69,  127,   72,  205,  208,   69,
   69,   69,   69,   83,   98,    7,   80,   98,   69,  128,
  104,    5,   26,  224,   10,  210,  104,    5,   26,   83,
  214,   30,   64,    5,   26,  165,  167,  168,  149,   65,
   66,   67,   68,  225,   43,   47,  177,  178,   30,   57,
  179,  180,   30,  217,   30,  218,  186,  117,  183,  184,
   30,  219,  129,  221,   39,  107,  108,   47,   37,    5,
   26,   90,  223,   30,   39,    5,   30,  112,    5,   26,
   42,  120,  122,    3,   30,   30,   30,   30,   39,   30,
   14,  141,  114,    5,   26,   17,   30,   30,   16,   30,
  144,   38,   30,   30,   31,  105,  106,   37,   53,   53,
  111,  175,  195,  196,   73,   30,   58,  113,  115,   30,
   83,  171,   30,  140,  161,   30,  119,    5,   26,    0,
    0,   38,   39,   92,    4,    5,   95,    0,    6,    0,
  176,    0,    0,  111,   28,    7,   50,    8,  121,    5,
   26,    9,  189,   23,   10,   61,   59,  191,   60,  116,
   62,   28,   28,    0,  197,   28,    0,   28,   79,   39,
   30,   30,   30,   28,    0,  202,  204,    0,    0,  135,
  207,    0,   84,    0,    0,   53,   28,  213,    0,   28,
  124,   30,    0,   39,    0,    0,   39,   28,   28,   28,
   28,    0,   28,    0,    0,  143,    0,    0,    0,   28,
   28,    0,   28,   39,    0,   28,   28,    0,    0,  159,
    0,    0,    0,    0,    0,    0,    0,  160,   39,    0,
    0,    0,   28,   30,    0,   28,    0,    0,   28,    0,
    0,   39,  164,   39,    4,    5,    0,    0,    6,   39,
  181,    0,    0,    0,   39,    7,   39,    8,    0,   39,
    0,    9,   37,    5,   10,   39,    6,    0,  192,    0,
    0,    0,    0,    7,    0,    8,  201,   37,    5,    9,
    0,    6,   10,   28,   28,   28,    0,    0,    7,    0,
    8,  203,    0,  216,    9,    0,    0,   10,    0,    4,
    5,    0,    0,    6,   28,  206,    0,    0,    0,    0,
    7,    0,    8,   37,    5,    0,    9,    6,    0,   10,
  209,   37,    5,    0,    7,    6,    8,    0,  211,    0,
    9,    0,    7,   10,    8,    0,   37,    5,    9,    0,
    6,   10,  212,    0,    0,    0,   28,    7,    0,    8,
   37,    5,    0,    9,    6,    0,   10,  215,    4,    5,
    0,    7,    6,    8,    0,  220,    0,    9,    0,    7,
   10,    8,    0,   37,    5,    9,    0,    6,   10,    0,
    0,   37,    5,    0,    7,    6,    8,    0,    0,    0,
    9,    0,    7,   10,    8,    0,    0,    0,    9,   37,
    5,   10,    0,    6,    0,    0,    0,   37,    5,    0,
    7,    6,    8,    0,    0,    0,    9,    0,    7,   10,
    8,    0,    4,    5,    9,    0,    6,   10,    0,    0,
    0,    0,    0,    7,    0,    8,   37,    5,    0,    9,
    6,    0,   10,    0,    0,    0,    0,    7,    0,    8,
    0,   37,    5,    9,    0,    6,   10,    0,    0,   37,
    5,    0,    7,    6,    8,    0,    0,    0,    9,    0,
    7,   10,    8,    4,    5,    0,    9,    6,    0,   10,
    0,    0,    0,    0,    7,    0,    8,    0,   37,    5,
    9,    0,    6,   10,    0,    0,   37,    5,    0,    7,
    6,    8,    0,    0,    0,    9,    0,    7,   10,    8,
   37,    5,    0,    9,    6,    0,   10,    0,    0,    0,
    0,    7,  200,    8,    4,    5,    0,    9,    6,    0,
   10,    0,    0,    0,    0,    7,    0,    8,    0,    0,
    0,    9,    0,    0,   10,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         40,
    0,   40,   40,   40,  267,  123,   41,  123,   41,   59,
   41,   44,   43,  276,   45,   41,   46,   43,   41,   45,
   43,   42,   45,   40,   44,   40,   47,   41,   42,   43,
   44,   45,   46,   47,   42,   43,  257,   45,   41,   47,
   43,   41,   45,   59,   44,   59,   60,   61,   62,   44,
   44,   46,   44,   44,   41,   41,   42,   43,   41,   45,
   40,   47,   46,   46,   59,   59,   41,   42,   43,   61,
   45,   44,   47,   59,   60,  258,   62,   41,   42,   43,
  257,   45,  257,   47,   59,   60,   59,   62,   61,   41,
   43,   43,   45,   45,  123,   59,   60,   44,   62,   46,
   41,   46,   43,   41,   45,   41,   41,   59,   60,  123,
   62,   41,   59,   43,   61,   45,  270,   44,   59,   60,
   41,   62,   41,   59,   43,  125,   45,  257,   41,   59,
   60,   44,   62,   40,   61,  257,  123,  123,   40,  257,
   59,   60,   41,   62,   43,   40,   45,   44,  123,   46,
   41,  256,  257,  258,   41,   43,   43,   45,   45,  123,
   59,   60,   59,   62,   61,   41,  257,   43,   59,   45,
   40,  123,   59,   60,  123,   62,  257,   41,  257,   43,
  258,   45,  123,   59,   60,  123,   62,  123,  267,   42,
   43,  257,   45,  123,   47,   59,   60,  276,   62,   40,
   40,   42,   42,   43,  123,   45,   47,   47,  272,   43,
   41,   45,   41,   44,  257,  256,  257,  258,  256,  257,
  258,  256,  257,  258,  123,  256,   60,  123,   62,  259,
  256,  270,  123,  256,  275,  257,  123,  275,  258,  274,
  272,  272,  256,  257,  258,  259,  272,  123,  256,  263,
  264,  265,  266,  256,  261,  262,  256,  257,  272,  123,
  260,  257,  257,  257,  272,  257,  257,  267,  268,  269,
  256,  257,  258,  273,  123,   41,  276,  263,  264,  265,
  266,  256,  257,  258,  257,   41,  272,   41,  263,  264,
  265,  266,  256,  257,  258,   41,  257,  272,   44,  263,
  264,  265,  266,  256,  256,  257,  258,   27,  272,  123,
  257,  263,  264,  265,  266,  256,  257,  258,   41,  272,
  272,   44,  263,  264,  265,  266,  256,  257,  258,   41,
  257,  272,   44,  263,  264,  265,  266,  256,  257,  258,
   41,  123,  272,   44,  263,  264,  265,  266,  123,  256,
  257,  258,   52,  272,  256,  257,  258,  256,  257,  258,
  257,  256,  257,  258,  263,  264,  265,  266,  256,  256,
  257,  258,   41,  272,    2,   44,  263,  264,  265,  266,
  256,  257,  258,   11,   41,  272,   86,  263,  264,  265,
  266,  123,  256,  257,  258,  257,  272,  123,   40,  263,
  264,  265,  266,  256,   41,  267,   34,   44,  272,  271,
  256,  257,  258,   59,  276,  262,  256,  257,  258,  272,
  262,    6,  256,  257,  258,  145,  146,  147,  128,  263,
  264,  265,  266,  125,   19,   20,  261,  262,   23,   24,
  261,  262,   27,  262,   29,  262,  166,   75,  261,  262,
   35,  262,  152,  262,   12,   61,   62,   42,  256,  257,
  258,   46,  262,   48,   22,    0,   51,  256,  257,  258,
   19,   77,   78,    0,   59,   60,   61,   62,   36,   64,
   59,  109,  256,  257,  258,   59,   71,   72,   59,   74,
  118,   59,   77,   78,   59,   59,   60,   59,   23,   24,
   64,  123,  261,  262,   29,   59,   24,   71,   72,   94,
   35,  152,   97,  100,  142,  100,  256,  257,  258,   -1,
   -1,  125,   80,   48,  256,  257,   51,   -1,  260,   -1,
  158,   -1,   -1,   97,    6,  267,  125,  269,  256,  257,
  258,  273,  170,   40,  276,   42,   43,  175,   45,   74,
   47,   23,   24,   -1,  182,   27,   -1,   29,  125,  117,
  145,  146,  147,   35,   -1,  193,  194,   -1,   -1,   94,
  198,   -1,  125,   -1,   -1,  100,   48,  205,   -1,   51,
  125,  166,   -1,  141,   -1,   -1,  144,   59,   60,   61,
   62,   -1,   64,   -1,   -1,  125,   -1,   -1,   -1,   71,
   72,   -1,   74,  161,   -1,   77,   78,   -1,   -1,  125,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,  125,  176,   -1,
   -1,   -1,   94,  208,   -1,   97,   -1,   -1,  100,   -1,
   -1,  189,  125,  191,  256,  257,   -1,   -1,  260,  197,
  125,   -1,   -1,   -1,  202,  267,  204,  269,   -1,  207,
   -1,  273,  256,  257,  276,  213,  260,   -1,  125,   -1,
   -1,   -1,   -1,  267,   -1,  269,  125,  256,  257,  273,
   -1,  260,  276,  145,  146,  147,   -1,   -1,  267,   -1,
  269,  125,   -1,  208,  273,   -1,   -1,  276,   -1,  256,
  257,   -1,   -1,  260,  166,  125,   -1,   -1,   -1,   -1,
  267,   -1,  269,  256,  257,   -1,  273,  260,   -1,  276,
  125,  256,  257,   -1,  267,  260,  269,   -1,  125,   -1,
  273,   -1,  267,  276,  269,   -1,  256,  257,  273,   -1,
  260,  276,  125,   -1,   -1,   -1,  208,  267,   -1,  269,
  256,  257,   -1,  273,  260,   -1,  276,  125,  256,  257,
   -1,  267,  260,  269,   -1,  125,   -1,  273,   -1,  267,
  276,  269,   -1,  256,  257,  273,   -1,  260,  276,   -1,
   -1,  256,  257,   -1,  267,  260,  269,   -1,   -1,   -1,
  273,   -1,  267,  276,  269,   -1,   -1,   -1,  273,  256,
  257,  276,   -1,  260,   -1,   -1,   -1,  256,  257,   -1,
  267,  260,  269,   -1,   -1,   -1,  273,   -1,  267,  276,
  269,   -1,  256,  257,  273,   -1,  260,  276,   -1,   -1,
   -1,   -1,   -1,  267,   -1,  269,  256,  257,   -1,  273,
  260,   -1,  276,   -1,   -1,   -1,   -1,  267,   -1,  269,
   -1,  256,  257,  273,   -1,  260,  276,   -1,   -1,  256,
  257,   -1,  267,  260,  269,   -1,   -1,   -1,  273,   -1,
  267,  276,  269,  256,  257,   -1,  273,  260,   -1,  276,
   -1,   -1,   -1,   -1,  267,   -1,  269,   -1,  256,  257,
  273,   -1,  260,  276,   -1,   -1,  256,  257,   -1,  267,
  260,  269,   -1,   -1,   -1,  273,   -1,  267,  276,  269,
  256,  257,   -1,  273,  260,   -1,  276,   -1,   -1,   -1,
   -1,  267,  268,  269,  256,  257,   -1,  273,  260,   -1,
  276,   -1,   -1,   -1,   -1,  267,   -1,  269,   -1,   -1,
   -1,  273,   -1,   -1,  276,
};
}
final static short YYFINAL=3;
final static short YYMAXTOKEN=276;
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
"FLECHA","PRINT","CADENAML","TRUNC","LAMBDA",
};
final static String yyrule[] = {
"$accept : prog",
"prog : ID '{' listasentencia '}'",
"prog : '{' listasentencia '}'",
"prog : ID '{' listasentencia",
"prog : ID listasentencia '}'",
"prog : ID listasentencia",
"listasentencia : listasentencia sentencia ';'",
"listasentencia : sentencia ';'",
"listasentencia : error",
"sentencia : asignacion",
"sentencia : declaracion",
"sentencia : control",
"sentencia : print",
"sentencia : funcion",
"declaracion : tipo listavariables",
"declaracion : tipo ID '(' listaparametros ')' '{' listasentencia RETURN '(' expresion ')' ';' '}'",
"asignacion : identificador ASIG expresion",
"asignacion : listavariables '=' listaconstantes",
"listaconstantes : listaconstantes ',' CTE",
"listaconstantes : CTE",
"listaconstantes : listaconstantes CTE",
"control : IF '(' condicion ')' '{' listasentencia '}' ELSE '{' listasentencia '}' ENDIF",
"control : IF '(' condicion ')' '{' listasentencia '}' ENDIF",
"control : DO '{' listasentencia '}' UNTIL '(' condicion ')'",
"control : IF condicion ')' '{' listasentencia '}' ELSE '{' listasentencia '}' ENDIF",
"control : IF '(' condicion '{' listasentencia '}' ELSE '{' listasentencia '}' ENDIF",
"control : IF condicion '{' listasentencia '}' ELSE '{' listasentencia '}' ENDIF",
"control : IF condicion ')' '{' listasentencia '}' ENDIF",
"control : IF '(' condicion '{' listasentencia '}' ENDIF",
"control : IF condicion '{' listasentencia '}' ENDIF",
"control : IF '(' condicion ')' '{' listasentencia '}' ELSE '{' listasentencia '}'",
"control : IF '(' condicion ')' '{' listasentencia '}'",
"control : IF '(' condicion ')' '{' listasentencia '}' ELSE '{' '}' ENDIF",
"control : IF '(' condicion ')' '{' '}' ELSE '{' listasentencia '}' ENDIF",
"control : IF '(' condicion ')' '{' '}' ENDIF",
"control : IF '(' condicion ')' '{' '}' ELSE '{' '}' ENDIF",
"control : DO '{' listasentencia '}' UNTIL condicion ')'",
"control : DO '{' listasentencia '}' UNTIL '(' condicion",
"control : DO '{' listasentencia '}' UNTIL condicion",
"control : DO '{' '}' UNTIL '(' condicion ')'",
"control : DO '{' listasentencia '}' '(' condicion ')'",
"print : PRINT '(' expresion ')'",
"print : PRINT '(' CADENAML ')'",
"print : PRINT '(' ')'",
"condicion : expresion comparador expresion",
"condicion : expresion expresion",
"comparador : '>'",
"comparador : '<'",
"comparador : MAYORIG",
"comparador : MENORIG",
"comparador : IGUAL",
"comparador : DISTINTO",
"tipo : LONG",
"tipo : LAMBDA",
"identificador : ID",
"identificador : identificador '.' ID",
"listavariables : listavariables ',' identificador",
"listavariables : identificador",
"listavariables : listavariables ID",
"listavariables : listavariables identificador '.' ID",
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
"expresion : termino",
"expresion : expresion '+' error",
"expresion : expresion '-' error",
"expresion : error '+' termino",
"expresion : error '-' termino",
"expresion : expresion error termino",
"termino : termino '*' factor",
"termino : termino '/' factor",
"termino : factor",
"termino : termino '*' error",
"termino : termino '/' error",
"termino : error '*' factor",
"termino : error '/' factor",
"factor : identificador",
"factor : CTE",
"factor : funcion",
"funcion : ID '(' listapinvocacion ')'",
"funcion : ID '(' identificador ')'",
"funcion : ID '(' CTE ')'",
"funcion : error '(' listapinvocacion ')'",
"listapinvocacion : listapinvocacion ',' pinvocacion",
"listapinvocacion : pinvocacion",
"pinvocacion : expresion FLECHA ID",
"pinvocacion : TRUNC '(' expresion ')' FLECHA ID",
"pinvocacion : TRUNC '(' expresion FLECHA ID",
"pinvocacion : TRUNC expresion ')' FLECHA ID",
"pinvocacion : TRUNC expresion FLECHA ID",
"pinvocacion : '(' tipo ID ')' '{' listasentencia '}'",
"pinvocacion : '(' tipo ID ')' '{' listasentencia",
"pinvocacion : '(' tipo ID ')' listasentencia '}'",
"pinvocacion : '(' tipo ID ')' listasentencia",
"pinvocacion : expresion FLECHA",
"pinvocacion : TRUNC '(' expresion ')' FLECHA",
};

//#line 164 "gramatica.y"

private void yyerror(String s) {
    System.out.println("Error: " + s);
}

public Parser(Map<String, Simbolo> tablaSimbolos, String path)
{
  this.tablaSimbolos = tablaSimbolos;
  this.lexer = new Lexico(tablaSimbolos);
  this.lexer.cargarBuffer(path);
}
//#line 563 "Parser.java"
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
        yychar = lexer.yylex(this.yylval);  //get next token
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
case 2:
//#line 12 "gramatica.y"
{System.out.println("Error sintáctico: falta el nombre del programa");}
break;
case 3:
//#line 13 "gramatica.y"
{System.out.println("Error sintáctico: falta llave de cierre del cuerpo del programa");}
break;
case 4:
//#line 14 "gramatica.y"
{System.out.println("Error sintáctico: falta la llave de apertura del cuerpo del programa");}
break;
case 5:
//#line 15 "gramatica.y"
{System.out.println("Error sintáctico: faltan llaves que encierren el cuerpo del programa");}
break;
case 8:
//#line 22 "gramatica.y"
{System.out.println("Error sintáctico: falta punto y coma al final de la sentencia");}
break;
case 20:
//#line 42 "gramatica.y"
{System.out.println("Error sintáctico: falta coma entre constantes");}
break;
case 24:
//#line 48 "gramatica.y"
{System.out.println("Error sintáctico: falta del parentesis de cierre de la condicion del bloque IF");}
break;
case 25:
//#line 49 "gramatica.y"
{System.out.println("Error sintáctico: falta del parentesis de apertura de la condición del bloque IF");}
break;
case 26:
//#line 50 "gramatica.y"
{System.out.println("Error sintáctico: falta de parentesis de la condicion del bloque IF");}
break;
case 27:
//#line 51 "gramatica.y"
{System.out.println("Error sintáctico: falta del parentesis de cierre de la condicion del bloque IF");}
break;
case 28:
//#line 52 "gramatica.y"
{System.out.println("Error sintáctico: falta del parentesis de apertura de la condición del bloque IF");}
break;
case 29:
//#line 53 "gramatica.y"
{System.out.println("Error sintáctico: falta de parentesis de la condicion del bloque IF");}
break;
case 30:
//#line 54 "gramatica.y"
{System.out.println("Error sintáctico: falta la palabra ENDIF al final del bloque IF");}
break;
case 31:
//#line 55 "gramatica.y"
{System.out.println("Error sintáctico: falta la palabra ENDIF al final del bloque IF");}
break;
case 32:
//#line 56 "gramatica.y"
{System.out.println("Error sintáctico: falta el cuerpo en el bloque ELSE");}
break;
case 33:
//#line 57 "gramatica.y"
{System.out.println("Error sintáctico: falta el cuerpo en el bloque IF");}
break;
case 34:
//#line 58 "gramatica.y"
{System.out.println("Error sintáctico: falta el cuerpo en el bloque IF");}
break;
case 35:
//#line 59 "gramatica.y"
{System.out.println("Error sintáctico: falta el cuerpo en los bloques IF y ELSE");}
break;
case 36:
//#line 60 "gramatica.y"
{System.out.println("Error sintáctico: falta el parentesis de apertura de la condición del bloque DO UNTIL");}
break;
case 37:
//#line 61 "gramatica.y"
{System.out.println("Error sintáctico: falta el parentesis de apertura de la condición del bloque DO UNTIL");}
break;
case 38:
//#line 62 "gramatica.y"
{System.out.println("Error sintáctico: falta de parentesis en la condición del bloque DO UNTIL");}
break;
case 39:
//#line 63 "gramatica.y"
{System.out.println("Error sintáctico: falta de cuerpo en iteracion");}
break;
case 40:
//#line 64 "gramatica.y"
{System.out.println("Error sintáctico: falta la palabra UNTIL en la iteracion");}
break;
case 43:
//#line 69 "gramatica.y"
{System.out.println("Error sintáctico: falta de argumento en sentencia print");}
break;
case 45:
//#line 73 "gramatica.y"
{System.out.println("Error sintáctico: falta el operador comparador en la condición");}
break;
case 58:
//#line 94 "gramatica.y"
{System.out.println("Error sintáctico: falta coma entre variables");}
break;
case 59:
//#line 95 "gramatica.y"
{System.out.println("Error sintáctico: falta coma entre variables");}
break;
case 64:
//#line 104 "gramatica.y"
{System.out.println("Error sintáctico: falta el nombre del parámetro");}
break;
case 65:
//#line 105 "gramatica.y"
{System.out.println("Error sintáctico: falta el nombre del parámetro");}
break;
case 66:
//#line 106 "gramatica.y"
{System.out.println("Error sintáctico: falta el tipo del parámetro");}
break;
case 67:
//#line 107 "gramatica.y"
{System.out.println("Error sintáctico: falta el tipo del parámetro");}
break;
case 71:
//#line 113 "gramatica.y"
{System.out.println("Error sintáctico: falta el operando derecho de la suma");}
break;
case 72:
//#line 114 "gramatica.y"
{System.out.println("Error sintáctico: falta el operando derecho de la resta");}
break;
case 73:
//#line 115 "gramatica.y"
{System.out.println("Error sintáctico: falta el operando izquierdo de la suma");}
break;
case 74:
//#line 116 "gramatica.y"
{System.out.println("Error sintáctico: falta el operando izquierdo de la resta");}
break;
case 75:
//#line 119 "gramatica.y"
{System.out.println("Error sintáctico: falta de operador");}
break;
case 79:
//#line 125 "gramatica.y"
{System.out.println("Error sintáctico: falta el operando derecho de la multiplicación");}
break;
case 80:
//#line 126 "gramatica.y"
{System.out.println("Error sintáctico: falta el operando derecho de la división");}
break;
case 81:
//#line 127 "gramatica.y"
{System.out.println("Error sintáctico: falta el operando izquierdo de la multiplicación");}
break;
case 82:
//#line 128 "gramatica.y"
{System.out.println("Error sintáctico: falta el operando izquierdo de la división");}
break;
case 89:
//#line 142 "gramatica.y"
{System.out.println("Error sintáctico: falta el nombre de la funcion");}
break;
case 94:
//#line 151 "gramatica.y"
{System.out.println("Error sintáctico: falta el paréntesis de cierre de la función TRUNC");}
break;
case 95:
//#line 152 "gramatica.y"
{System.out.println("Error sintáctico: falta el paréntesis de apertura de la función TRUNC");}
break;
case 96:
//#line 153 "gramatica.y"
{System.out.println("Error sintáctico: faltan ambos paréntesis de la función TRUNC");}
break;
case 98:
//#line 155 "gramatica.y"
{System.out.println("Error sintáctico: falta llave de cierre del cuerpo de la función Lambda");}
break;
case 99:
//#line 156 "gramatica.y"
{System.out.println("Error sintáctico: falta llave de apertura del cuerpo de la función Lambda");}
break;
case 100:
//#line 157 "gramatica.y"
{System.out.println("Error sintáctico: faltan ambas llaves del cuerpo de la función Lambda");}
break;
case 101:
//#line 158 "gramatica.y"
{System.out.println("Error sintáctico: No se especifica el parametro formal al que corresponde el parámetro real");}
break;
case 102:
//#line 159 "gramatica.y"
{System.out.println("Error sintáctico: No se especifica el parametro formal al que corresponde el parámetro real");}
break;
//#line 908 "Parser.java"
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
