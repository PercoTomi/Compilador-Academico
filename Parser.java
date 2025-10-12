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
public final static short EOF=277;
public final static short YYERRCODE=256;
final static short yylhs[] = {                           -1,
    0,    0,    0,    0,    0,    2,    0,    3,    0,    0,
    0,    1,    1,    1,    1,    4,    4,    7,    7,    5,
    5,    5,    6,    6,    8,    8,   16,   16,   16,   17,
   17,    9,    9,    9,    9,    9,    9,    9,    9,    9,
    9,    9,   10,   10,   10,   18,   18,   18,   18,   19,
   19,   20,   20,   20,   20,   20,   20,   11,   11,   15,
   15,   12,   12,   12,   12,   13,   13,   21,   21,   21,
   21,   21,   21,   14,   14,   14,   14,   14,   14,   14,
   14,   22,   22,   22,   22,   22,   22,   22,   23,   23,
   23,   24,   24,   24,   24,   24,   24,   25,   25,   26,
   26,   26,   26,   26,   26,   26,   26,   26,   26,   26,
   26,
};
final static short yylen[] = {                            2,
    5,    4,    4,    4,    3,    0,    7,    0,    6,    6,
    5,    2,    1,    3,    2,    2,    2,    3,    2,    1,
    1,    1,    2,   13,    3,    3,    3,    1,    2,    3,
    2,    6,    4,    4,    5,    3,    5,    5,    3,    4,
    3,    3,    4,    4,    3,    3,    2,    2,    1,    3,
    2,    1,    1,    1,    1,    1,    1,    1,    1,    1,
    3,    3,    1,    2,    4,    3,    1,    3,    2,    2,
    1,    2,    1,    3,    3,    1,    3,    3,    3,    3,
    3,    3,    3,    1,    3,    3,    3,    3,    1,    1,
    1,    4,    4,    4,    4,    4,    4,    3,    1,    3,
    6,    5,    5,    4,    9,    8,    8,    7,    8,    2,
    5,
};
final static short yydefred[] = {                         0,
    0,    0,    0,    0,   60,    0,   58,    0,    0,   59,
    0,    0,   13,    0,    0,   20,   21,   22,    0,    0,
    0,    0,   15,    0,    0,   90,    0,    0,    0,    0,
    0,    0,   84,   91,    0,    0,    0,    0,    0,    0,
    0,    5,    0,   12,   16,   17,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,   54,   55,   56,   57,   52,   53,
    0,    0,    0,    0,    0,   39,    0,   47,    0,    0,
   41,   31,    0,    0,    0,   42,    0,   45,    0,    8,
    0,    0,    3,    0,   14,    4,    0,   28,    0,    0,
    0,    0,   61,    2,    0,    0,    0,    0,    0,    0,
   99,    0,    0,    0,    0,   87,   88,    0,    0,    0,
   46,    0,    0,    0,    0,    0,    0,    0,   40,    0,
    0,   33,    0,   82,    0,   83,   19,   30,    0,   34,
   44,   43,    0,   11,    6,    0,    1,   73,    0,    0,
    0,   67,   29,    0,    0,   97,    0,    0,    0,    0,
   96,   95,    0,   94,   93,   92,   38,   37,    0,   18,
    9,    0,   10,   72,    0,   69,    0,    0,   27,    0,
    0,    0,    0,  100,   98,   32,    7,   68,    0,   66,
    0,    0,  104,    0,    0,    0,  102,    0,  103,    0,
    0,    0,  101,    0,    0,    0,    0,    0,    0,  108,
    0,    0,  106,    0,  107,    0,  105,    0,   24,
};
final static short yydgoto[] = {                          3,
   12,  172,  143,   13,   14,   15,   84,   16,   17,   18,
   19,   20,  151,   28,   29,   99,   37,   30,   31,   74,
  152,   32,   33,   34,  110,  111,
};
final static short yysindex[] = {                       -96,
  277,  412,    0,  -51,    0,   44,    0, -110,   -8,    0,
  426,  213,    0,  -44,  -21,    0,    0,    0, -197,   24,
    3,  313,    0,  468,   22,    0, -153,   21,   41,  -98,
   48,   -3,    0,    0,   44,  303,  -28,  -31,  -19,  298,
   34,    0, -162,    0,    0,    0,   77,   46,   41,    0,
 -121,  -95,  120, -153,  -89,  -99,  -40,  210,  210,  260,
  260,  -37,  141,  102,    0,    0,    0,    0,    0,    0,
  264,  273,   43, -153, -119,    0,  -63,    0,  284,  341,
    0,    0,  129,  327,   44,    0,  159,    0,  -25,    0,
  -70,  -18,    0,  -68,    0,    0, -148,    0,  -22,   41,
  -45,   43,    0,    0,  192,  108, -230,   35,   30,  -39,
    0,   16,   -3,   -3,  197,    0,    0,  220,   89,    1,
    0,   -3,   16,   -3,   16,   -3,  210,   43,    0,    8,
 -116,    0,  197,    0,  197,    0,    0,    0,  206,    0,
    0,    0,  158,    0,    0,   15,    0,    0, -136,   52,
  386,    0,    0,   56,    0,    0, -153,  -24,   65,   70,
    0,    0,  -34,    0,    0,    0,    0,    0,   72,    0,
    0,  221,    0,    0,   93,    0,  238, -148,    0,  -17,
  105,  121,  353,    0,    0,    0,    0,    0,  412,    0,
  144,  133,    0,  172,  182,  394,    0,  184,    0,  234,
  266,  407,    0,  321,  191,  185, -153,  226,  215,    0,
  236,   57,    0,  244,    0,  443,    0,  380,    0,
};
final static short yyrindex[] = {                         0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
  114,    0,    0,    0,  -12,    0,    0,    0,   10,    0,
  -58,   67,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,  262,  -51,   23,  113,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,  117,    0,    0,    0,    0,    0,    0,    0,
    0,    0,   -5,    0,    0,    0,  447,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,  453,  376,
    0,  465,    0,    0,  150,    0,    0,    0,  168,    0,
    0,    0,   79,   91,    0,    0,    0,  150,  168,    0,
    0,  111,  124,  146,  354,  374,    0,    2,    0,    0,
    0,    0,   32,    0,   54,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,  415,
    0,    0,    0,    0,  135,    0,    0,    0,    0,  417,
    0,    0,    0,    0,    0,    0,    0,    0,  466,    0,
    0,    0,    0,    0,  421,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  437,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,  454,    0,    0,    0,    0,    0,
};
final static short yygindex[] = {                         0,
   12,    0,    0,  409,  348,    0,  -15,    0,    0,    0,
  378,  513,    0,  422,  452,    0,  368,  -26,  518,    0,
  369,  550,  365,    0,  489,  396,
};
final static int YYTABLESIZE=702;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                        107,
   49,  162,  107,   36,  163,  107,   36,   23,   81,   88,
   86,   27,   36,   22,   45,  142,  182,   71,   71,   72,
   72,  154,   40,  192,   36,   71,    2,   72,   60,   60,
   60,   38,   60,   60,   60,   51,    7,   46,   79,   23,
   95,  166,   50,   80,  163,   10,   60,   60,   55,   60,
   89,   89,   89,   51,   89,   57,   89,   60,  140,   47,
   50,   62,   61,   71,   49,   72,   63,   52,   89,   89,
  161,   89,   85,   85,   85,   55,   85,   71,   85,   72,
   70,   63,   69,   27,   51,   71,   55,   72,   78,   52,
   85,   85,   95,   85,   86,   86,   86,  216,   86,   71,
   86,   72,   24,   25,   26,   91,  146,   76,  148,   76,
   60,   76,   86,   86,   96,   86,   97,   51,    7,   79,
  174,   79,  149,   79,   50,   76,   76,   10,   76,  165,
    7,   80,   89,   80,   55,   80,   98,   79,   79,   10,
   79,   57,  129,   60,   58,  168,   59,  157,   61,   80,
   80,   81,   80,   81,   85,   81,   64,   63,   60,   35,
    1,    5,   75,   76,   77,  101,   77,  103,   77,   81,
   81,   64,   81,   64,   63,   48,   86,  104,   65,  201,
   61,  121,   77,   77,  204,   77,   74,  137,   74,   76,
   74,   90,   90,   65,   90,   65,   90,  131,  132,  141,
  196,   79,   49,   49,   74,   74,  144,   74,  147,   89,
   89,  155,   89,   80,   89,   24,   25,  105,   24,   25,
  118,   24,   25,   26,   24,   25,   26,   24,   25,   26,
  127,  127,  156,   81,  106,  153,   57,  106,  127,   48,
  106,   85,   87,   60,   60,   60,   77,  181,   60,   60,
   60,   60,   60,   60,  191,   51,   51,   90,  145,   60,
  164,   54,   50,   50,  170,   89,   89,   89,   74,  167,
   89,   89,   89,   89,   89,   89,   64,   25,   26,   63,
   50,   89,  171,   65,   66,   67,   68,   85,   85,   85,
  127,  173,   85,   85,   85,   85,   85,   85,  127,   24,
   25,   26,   50,   85,  200,   60,  160,   60,  176,   86,
   86,   86,  127,  179,   86,   86,   86,   86,   86,   86,
   60,  183,   76,   76,   76,   86,  184,   76,   76,   76,
   76,   76,   76,  186,   79,   79,   79,   43,   76,   79,
   79,   79,   79,   79,   79,  187,   80,   80,   80,  188,
   79,   80,   80,   80,   80,   80,   80,  112,   25,   26,
  189,  193,   80,   24,   25,   26,   81,   81,   81,   64,
   63,   81,   81,   81,   81,   81,   81,   48,   48,   77,
   77,   77,   81,   83,   77,   77,   77,   77,   77,   77,
  206,   65,  194,  195,   78,   77,   78,   77,   78,   11,
  197,   74,   74,   74,  198,   90,   74,   74,   74,   74,
   74,   74,   78,   78,   75,   78,   75,   74,   75,   62,
   44,   90,   94,   89,  116,  117,  177,   82,  199,  178,
   44,  139,   75,   75,   62,   75,   62,   56,    5,   89,
  203,    6,  130,  134,  136,  209,  207,  210,   44,   73,
    8,  138,   21,   21,    9,   71,  211,  110,   71,   89,
  110,   70,   21,   21,   70,  112,   25,   26,   41,    5,
   49,   53,    6,   21,  150,  102,   78,  111,  108,    7,
  111,    8,  213,  108,  159,    9,  214,   21,   10,   42,
    5,   21,  215,    6,  109,  128,   75,  109,  169,   53,
  217,  218,    8,  100,  219,   36,    9,   57,  109,   60,
   58,   26,   59,  119,   61,  115,   25,   26,   60,  123,
   25,   26,    5,   25,   35,    6,  175,  158,  125,   25,
   26,   48,    4,    5,    8,   21,    6,  205,    9,  133,
   25,   26,   83,    7,   63,    8,  190,   83,  139,    9,
  120,  139,   10,   92,    5,  150,    0,    6,  185,    5,
    0,    0,    6,    0,    7,    0,    8,    0,   41,    5,
    9,    8,    6,   10,   93,    9,    0,    5,  180,    7,
    6,    8,    0,    5,  108,    9,    6,    0,   10,    8,
    0,    0,  208,    9,    0,    8,  135,   25,   26,    9,
    0,    0,    0,    0,   44,    0,    0,  113,  114,   78,
   78,   78,    0,  122,   78,   78,   78,   78,   78,   78,
  124,  126,    0,    0,    0,   78,    0,    0,  212,   75,
   75,   75,   62,    0,   75,   75,   75,   75,   75,   75,
   21,    0,    0,    0,    0,   75,   21,   21,    0,   41,
    5,   21,   21,    6,    0,   21,    0,    0,    0,    0,
    7,  202,    8,    0,    0,    0,    9,    4,    5,   10,
    0,    6,    0,    0,    0,    0,  122,    0,    7,    0,
    8,   39,    5,    0,    9,    6,    0,   10,    0,    0,
    0,    0,    7,    0,    8,    0,    0,    0,    9,    0,
    0,   10,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         40,
   59,   41,   40,  123,   44,   40,  123,   59,   35,   41,
   37,   40,  123,    2,   59,   41,   41,   43,   43,   45,
   45,   44,   11,   41,  123,   43,  123,   45,   41,   42,
   43,   40,   45,   46,   47,   41,  267,   59,   42,   59,
   59,   41,   41,   47,   44,  276,   59,   60,   46,   62,
   41,   42,   43,   59,   45,   40,   47,   42,   85,  257,
   59,   40,   47,   43,  123,   45,   44,   44,   59,   60,
   41,   62,   41,   42,   43,   46,   45,   43,   47,   45,
   60,   59,   62,   40,   61,   43,   46,   45,   41,   44,
   59,   60,   59,   62,   41,   42,   43,   41,   45,   43,
   47,   45,  256,  257,  258,  125,  125,   41,  257,   43,
  123,   45,   59,   60,  277,   62,   40,  123,  267,   41,
  257,   43,  271,   45,  123,   59,   60,  276,   62,   41,
  267,   41,  123,   43,   46,   45,  258,   59,   60,  276,
   62,   40,  262,   42,   43,  262,   45,   40,   47,   59,
   60,   41,   62,   43,  123,   45,   44,   44,   46,  270,
  257,  257,  261,  262,   41,   46,   43,  257,   45,   59,
   60,   59,   62,   61,   61,   59,  123,  277,   44,  195,
   46,   41,   59,   60,  200,   62,   41,   59,   43,  123,
   45,   42,   43,   59,   45,   61,   47,  261,  262,   41,
  189,  123,  261,  262,   59,   60,  277,   62,  277,   42,
   43,  257,   45,  123,   47,  256,  257,  258,  256,  257,
  258,  256,  257,  258,  256,  257,  258,  256,  257,  258,
  256,  256,   41,  123,  275,  258,   40,  275,  256,  123,
  275,  270,  274,  256,  257,  258,  123,  272,  261,  262,
  263,  264,  265,  266,  272,  261,  262,  277,  277,  272,
   41,  259,  261,  262,   59,  256,  257,  258,  123,  262,
  261,  262,  263,  264,  265,  266,  256,  257,  258,  257,
  257,  272,  125,  263,  264,  265,  266,  256,  257,  258,
  256,  277,  261,  262,  263,  264,  265,  266,  256,  256,
  257,  258,  257,  272,  123,   44,  272,   46,  257,  256,
  257,  258,  256,  258,  261,  262,  263,  264,  265,  266,
   59,  257,  256,  257,  258,  272,  257,  261,  262,  263,
  264,  265,  266,  262,  256,  257,  258,  125,  272,  261,
  262,  263,  264,  265,  266,  125,  256,  257,  258,  257,
  272,  261,  262,  263,  264,  265,  266,  256,  257,  258,
  123,  257,  272,  256,  257,  258,  256,  257,  258,  257,
  257,  261,  262,  263,  264,  265,  266,  261,  262,  256,
  257,  258,  272,   36,  261,  262,  263,  264,  265,  266,
  125,  257,  272,   41,   41,  272,   43,   30,   45,  123,
  257,  256,  257,  258,  272,  256,  261,  262,  263,  264,
  265,  266,   59,   60,   41,   62,   43,  272,   45,   44,
   12,  272,  125,  256,   60,   61,   41,  125,  257,   44,
   22,   84,   59,   60,   59,   62,   61,  125,  257,  272,
  257,  260,   75,   79,   80,  125,   40,  257,   40,   28,
  269,  125,    1,    2,  273,   41,  272,   41,   44,   38,
   44,   41,   11,   12,   44,  256,  257,  258,  256,  257,
   19,   20,  260,   22,   97,   54,  123,   41,   57,  267,
   44,  269,  257,   62,  107,  273,  272,   36,  276,  277,
  257,   40,  257,  260,   41,   74,  123,   44,  131,   48,
  257,   59,  269,   52,  125,   59,  273,   40,   57,   42,
   43,   59,   45,   62,   47,  256,  257,  258,  257,  256,
  257,  258,  257,   59,   59,  260,  149,  106,  256,  257,
  258,   19,  256,  257,  269,   84,  260,  272,  273,  256,
  257,  258,  195,  267,   27,  269,  178,  200,  201,  273,
   62,  204,  276,  256,  257,  178,   -1,  260,  163,  257,
   -1,   -1,  260,   -1,  267,   -1,  269,   -1,  256,  257,
  273,  269,  260,  276,  277,  273,   -1,  257,  157,  267,
  260,  269,   -1,  257,  163,  273,  260,   -1,  276,  269,
   -1,   -1,  272,  273,   -1,  269,  256,  257,  258,  273,
   -1,   -1,   -1,   -1,  196,   -1,   -1,   58,   59,  256,
  257,  258,   -1,   64,  261,  262,  263,  264,  265,  266,
   71,   72,   -1,   -1,   -1,  272,   -1,   -1,  207,  256,
  257,  258,  257,   -1,  261,  262,  263,  264,  265,  266,
  189,   -1,   -1,   -1,   -1,  272,  195,  196,   -1,  256,
  257,  200,  201,  260,   -1,  204,   -1,   -1,   -1,   -1,
  267,  268,  269,   -1,   -1,   -1,  273,  256,  257,  276,
   -1,  260,   -1,   -1,   -1,   -1,  127,   -1,  267,   -1,
  269,  256,  257,   -1,  273,  260,   -1,  276,   -1,   -1,
   -1,   -1,  267,   -1,  269,   -1,   -1,   -1,  273,   -1,
   -1,  276,
};
}
final static short YYFINAL=3;
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
"prog : '{' listasentencias '}' EOF",
"prog : ID '{' listasentencias EOF",
"prog : ID listasentencias '}' EOF",
"prog : ID listasentencias EOF",
"$$1 :",
"prog : ID '{' listasentencias error EOF $$1 '}'",
"$$2 :",
"prog : ID '{' error EOF $$2 '}'",
"prog : ID '{' listasentencias error '}' EOF",
"prog : ID '{' error '}' EOF",
"listasentencias : listasentencias sentencia",
"listasentencias : sentencia",
"listasentencias : listasentencias error ';'",
"listasentencias : error ';'",
"sentencia : sentenciaejecutable ';'",
"sentencia : declaracion ';'",
"listasentenciasejecutables : listasentenciasejecutables sentenciaejecutable ';'",
"listasentenciasejecutables : sentenciaejecutable ';'",
"sentenciaejecutable : asignacion",
"sentenciaejecutable : control",
"sentenciaejecutable : print",
"declaracion : tipo listavariables",
"declaracion : tipo ID '(' listaparametros ')' '{' listasentencias RETURN '(' expresion ')' ';' '}'",
"asignacion : identificador ASIG expresion",
"asignacion : listavariables '=' listaconstantes",
"listaconstantes : listaconstantes ',' CTE",
"listaconstantes : CTE",
"listaconstantes : listaconstantes CTE",
"cuerpo_control : '{' listasentenciasejecutables '}'",
"cuerpo_control : '{' '}'",
"control : IF condicion cuerpo_control ELSE cuerpo_control ENDIF",
"control : IF condicion cuerpo_control ENDIF",
"control : DO cuerpo_control UNTIL condicion",
"control : IF condicion cuerpo_control ELSE cuerpo_control",
"control : IF condicion cuerpo_control",
"control : IF condicion cuerpo_control ELSE ENDIF",
"control : IF condicion ELSE cuerpo_control ENDIF",
"control : IF condicion ENDIF",
"control : IF condicion ELSE ENDIF",
"control : DO UNTIL condicion",
"control : DO cuerpo_control condicion",
"print : PRINT '(' expresion ')'",
"print : PRINT '(' CADENAML ')'",
"print : PRINT '(' ')'",
"condicion : '(' cuerpocondicion ')'",
"condicion : cuerpocondicion ')'",
"condicion : '(' cuerpocondicion",
"condicion : cuerpocondicion",
"cuerpocondicion : expresion comparador expresion",
"cuerpocondicion : expresion expresion",
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
"funcion : error '(' identificador ')'",
"funcion : error '(' CTE ')'",
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
};

//#line 176 "gramatica.y"

private void yyerror(String s) {
    System.out.println("Error: " + s);
}

public Parser(Map<String, Simbolo> tablaSimbolos, String path)
{
  this.tablaSimbolos = tablaSimbolos;
  this.lexer = new Lexico(tablaSimbolos);
  this.lexer.cargarBuffer(path);
}
//#line 528 "Parser.java"
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
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el nombre del programa");}
break;
case 3:
//#line 13 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta llave de cierre del cuerpo del programa");}
break;
case 4:
//#line 14 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta la llave de apertura del cuerpo del programa");}
break;
case 5:
//#line 15 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta la llave de apertura y la llave de cierre del cuerpo del programa");}
break;
case 6:
//#line 16 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": el programa terminó de forma inválida");}
break;
case 8:
//#line 17 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": no hay una sentencia válida dentro del cuerpo del programa");}
break;
case 10:
//#line 18 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": el programa terminó de forma inválida");}
break;
case 11:
//#line 19 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": no hay una sentencia válida dentro del cuerpo del programa");}
break;
case 12:
//#line 22 "gramatica.y"
{System.out.println("Se detectó una listasentencias sentencia ;");}
break;
case 13:
//#line 23 "gramatica.y"
{System.out.println("Se detectó una sentencia ;");}
break;
case 14:
//#line 24 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta de punto y coma en una sentencia anterior o sentencia no válida antes del punto y coma.");}
break;
case 15:
//#line 25 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta de punto y coma en una sentencia anterior o sentencia no válida antes del punto y coma.");}
break;
case 16:
//#line 28 "gramatica.y"
{System.out.println("Se detectó una sentenciaejecutable ;");}
break;
case 17:
//#line 29 "gramatica.y"
{System.out.println("Se detectó una declaración ;");}
break;
case 22:
//#line 38 "gramatica.y"
{System.out.println("Se detectó un print");}
break;
case 23:
//#line 42 "gramatica.y"
{System.out.println("Se detectó una declaración de variables");}
break;
case 24:
//#line 43 "gramatica.y"
{System.out.println("Se detectó una declaración de función");}
break;
case 25:
//#line 46 "gramatica.y"
{System.out.println("Se detectó una asignación simple");}
break;
case 26:
//#line 48 "gramatica.y"
{System.out.println("Se detectó una asignación múltiple");}
break;
case 29:
//#line 53 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta coma entre constantes");}
break;
case 32:
//#line 60 "gramatica.y"
{System.out.println("Se detectó un bloque IF-ELSE");}
break;
case 33:
//#line 61 "gramatica.y"
{System.out.println("Se detectó un bloque IF");}
break;
case 34:
//#line 62 "gramatica.y"
{System.out.println("Se detectó un bloque DO UNTIL");}
break;
case 35:
//#line 64 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta la palabra ENDIF al final del bloque IF");}
break;
case 36:
//#line 65 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta la palabra ENDIF al final del bloque IF");}
break;
case 37:
//#line 66 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el cuerpo en el bloque ELSE");}
break;
case 38:
//#line 67 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el cuerpo en el bloque IF");}
break;
case 39:
//#line 68 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el cuerpo en el bloque IF");}
break;
case 40:
//#line 69 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el cuerpo en los bloques IF y ELSE");}
break;
case 41:
//#line 71 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta de cuerpo en iteracion");}
break;
case 42:
//#line 72 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta la palabra UNTIL en la iteracion");}
break;
case 45:
//#line 77 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta de argumento en sentencia print");}
break;
case 46:
//#line 80 "gramatica.y"
{System.out.println("Se detectó una condición entre paréntesis");}
break;
case 47:
//#line 81 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el paréntesis de apertura de la condición");}
break;
case 48:
//#line 82 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el paréntesis de cierre de la condición");}
break;
case 49:
//#line 83 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": faltan ambos paréntesis de la condición");}
break;
case 51:
//#line 87 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el comparador en la condición");}
break;
case 60:
//#line 102 "gramatica.y"
{System.out.println("Se detectó un IDENTIFICADOR");}
break;
case 61:
//#line 103 "gramatica.y"
{System.out.println("Se detectó un IDENTIFICADOR compuesto");}
break;
case 64:
//#line 108 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta coma entre variables");}
break;
case 65:
//#line 109 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta coma entre variables");}
break;
case 70:
//#line 118 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el nombre del parámetro");}
break;
case 71:
//#line 119 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el nombre del parámetro");}
break;
case 72:
//#line 120 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el tipo del parámetro");}
break;
case 73:
//#line 121 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el tipo del parámetro");}
break;
case 74:
//#line 124 "gramatica.y"
{System.out.println("Se detectó una expresión desde una suma");}
break;
case 75:
//#line 125 "gramatica.y"
{System.out.println("Se detectó una expresión desde una resta");}
break;
case 76:
//#line 126 "gramatica.y"
{System.out.println("Se detectó una expresión desde un término");}
break;
case 77:
//#line 127 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando derecho de la suma");}
break;
case 78:
//#line 128 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando derecho de la resta");}
break;
case 79:
//#line 129 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando izquierdo de la suma");}
break;
case 80:
//#line 130 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando izquierdo de la resta");}
break;
case 81:
//#line 131 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta de operador");}
break;
case 84:
//#line 136 "gramatica.y"
{System.out.println("Se detecto un TERMINO desde un FACTOR");}
break;
case 85:
//#line 137 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando derecho de la multiplicación");}
break;
case 86:
//#line 138 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando derecho de la división");}
break;
case 87:
//#line 139 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando izquierdo de la multiplicación");}
break;
case 88:
//#line 140 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando izquierdo de la división");}
break;
case 89:
//#line 143 "gramatica.y"
{System.out.println("Se detectó un FACTOR IDENTIFICADOR");}
break;
case 90:
//#line 144 "gramatica.y"
{System.out.println("Se detectó un FACTOR CTE");}
break;
case 91:
//#line 145 "gramatica.y"
{System.out.println("Se detectó un FACTOR FUNCIÓN");}
break;
case 92:
//#line 148 "gramatica.y"
{System.out.println("Se detectó una función invocada correctamente");}
break;
case 93:
//#line 149 "gramatica.y"
{System.out.println("Se detectó una función invocada correctamente");}
break;
case 94:
//#line 150 "gramatica.y"
{System.out.println("Se detectó una función invocada correctamente");}
break;
case 95:
//#line 151 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el nombre de la funcion");}
break;
case 96:
//#line 152 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el nombre de la funcion");}
break;
case 97:
//#line 153 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el nombre de la funcion");}
break;
case 102:
//#line 162 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el paréntesis de cierre de la función TRUNC");}
break;
case 103:
//#line 163 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el paréntesis de apertura de la función TRUNC");}
break;
case 104:
//#line 164 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": faltan ambos paréntesis de la función TRUNC");}
break;
case 106:
//#line 166 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta llave de cierre del cuerpo de la función Lambda");}
break;
case 107:
//#line 167 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta llave de apertura del cuerpo de la función Lambda");}
break;
case 108:
//#line 168 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": faltan ambas llaves del cuerpo de la función Lambda");}
break;
case 109:
//#line 169 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": No se especifica el parametro formal al que corresponde el parámetro real");}
break;
case 110:
//#line 170 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": No se especifica el parametro formal al que corresponde el parámetro real");}
break;
case 111:
//#line 171 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": No se especifica el parametro formal al que corresponde el parámetro real");}
break;
//#line 981 "Parser.java"
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
