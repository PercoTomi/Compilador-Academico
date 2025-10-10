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
    0,    0,    0,    0,    0,    0,    0,    0,    1,    1,
    1,    1,    2,    2,    5,    5,    3,    3,    3,    3,
    4,    4,    6,    6,   15,   15,   15,   16,   16,    7,
    7,    7,    7,    7,    7,    7,    7,    7,    7,    7,
    8,    8,    8,   17,   17,   17,   17,   18,   18,   19,
   19,   19,   19,   19,   19,   10,   10,   14,   14,   11,
   11,   11,   11,   12,   12,   20,   20,   20,   20,   20,
   20,   13,   13,   13,   13,   13,   13,   13,   13,   21,
   21,   21,   21,   21,   21,   21,   22,   22,   22,    9,
    9,    9,    9,    9,    9,   23,   23,   24,   24,   24,
   24,   24,   24,   24,   24,   24,   24,   24,
};
final static short yylen[] = {                            2,
    4,    3,    3,    3,    5,    4,    4,    3,    2,    1,
    3,    2,    2,    2,    3,    2,    1,    1,    1,    1,
    2,   13,    3,    3,    3,    1,    2,    3,    2,    6,
    4,    4,    5,    3,    5,    5,    3,    4,    3,    3,
    4,    4,    3,    3,    2,    2,    1,    3,    2,    1,
    1,    1,    1,    1,    1,    1,    1,    1,    3,    3,
    1,    2,    4,    3,    1,    3,    2,    2,    1,    2,
    1,    3,    3,    1,    3,    3,    3,    3,    3,    3,
    3,    1,    3,    3,    3,    3,    1,    1,    1,    4,
    4,    4,    4,    4,    4,    3,    1,    3,    6,    5,
    5,    4,    7,    6,    6,    5,    2,    5,
};
final static short yydefred[] = {                         0,
    0,    0,    0,    0,    0,    0,   56,    0,    0,   57,
    0,    0,   10,    0,    0,   17,   18,   19,   20,    0,
    0,    0,    0,   12,    0,    0,    0,   88,    0,   89,
    0,    0,    0,    0,    0,   82,    0,    0,    0,    0,
    0,    0,    0,    4,    9,   13,   14,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    2,    0,    0,    0,
    0,    0,    0,   97,    0,    0,    0,    0,    0,    0,
    0,    0,    0,   52,   53,   54,   55,   50,   51,    0,
    0,    0,    0,    0,   37,    0,   45,    0,    0,   39,
    0,   29,    0,    0,    0,   40,    0,   43,    0,    6,
    0,    1,   11,    0,   26,    0,   58,    0,    0,    0,
   59,   95,    0,    0,    0,    0,    0,   94,   93,    0,
   92,   91,   90,    0,    0,    0,   85,   86,   44,    0,
    0,    0,    0,    0,    0,   38,    0,    0,   31,    0,
   80,    0,   81,   16,   28,    0,   32,   42,   41,    5,
   71,    0,    0,    0,   65,   27,    0,    0,    0,    0,
    0,    0,   98,   96,   36,   35,    0,   15,   70,    0,
   67,    0,    0,   25,    0,    0,  102,    0,    0,   30,
   66,    0,   64,  100,    0,  101,    0,    0,    0,   99,
    0,  105,    0,  103,    0,    0,    0,    0,   22,
};
final static short yydgoto[] = {                          3,
   12,   13,   14,   15,   94,   16,   17,   18,   30,   20,
   21,  154,   31,   32,  106,   39,   33,   34,   83,  155,
   35,   36,   63,   64,
};
final static short yysindex[] = {                      -112,
  243,  427,    0,   -3,  -33,    1,    0, -121,  -27,    0,
  445,  333,    0,  -50,  -36,    0,    0,    0,    0, -219,
   24,  -19,  338,    0,  -40,  -37,  664,    0,  257,    0,
   21,   -6, -119,   52,  154,    0,    1,  236,  -28,  -31,
    6,  400,   58,    0,    0,    0,    0,   22,   23,   -6,
    0, -155, -120,   89,  257, -113,    0,  118,  128, -231,
   35,  158,   -2,    0,  124,  260,   77,  289,  289,  292,
  292,  125,  383,    0,    0,    0,    0,    0,    0,  330,
  364,   44,  257, -122,    0, -156,    0,  391,  459,    0,
  160,    0,  127,  297,    1,    0,  157,    0,  -25,    0,
  393,    0,    0, -213,    0,  -22,    0,   -6,    4,   44,
    0,    0,  257,  -24,    5,  289,   26,    0,    0,  -34,
    0,    0,    0,   62,  154,  154,    0,    0,    0,  154,
   62,  154,   62,  154,   44,    0,  -29, -115,    0,  160,
    0,  160,    0,    0,    0,  153,    0,    0,    0,    0,
    0,  184,   56,  131,    0,    0,   34,    0,  -17,   57,
   37,  286,    0,    0,    0,    0,   76,    0,    0,   93,
    0,  223, -213,    0,  101,   98,    0,  108,  283,    0,
    0,  427,    0,    0,  143,    0,  305,  402,  422,    0,
  408,    0,  342,    0,  257,   43,  329,  295,    0,
};
final static short yyrindex[] = {                         0,
    0,    0,    0,    0,  -12,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  136,    0,    0,    0,    0,    0,    0,    0,    0,
    0,   10,    0,   60,   67,    0,    0,    0,    0,    0,
  421,  439,    0,    0,    0,    0,    0,  135,  368,  144,
  102,    0,    0,    0,    0,    0,    0,  115,    0,    0,
    0,  168,    0,    0,  115,  168,    0,    0,    0,    0,
    0,  211,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    2,    0,    0,    0,  384,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
  467,    0,    0,    0,    0,  386,    0,  385,    0,  409,
    0,    0,    0,    0,    0,    0,  264,    0,    0,    0,
    0,    0,    0,    0,   79,   91,    0,    0,    0,  111,
  133,  146,  319,  372,   41,    0,    0,    0,    0,   32,
    0,   54,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,  375,    0,    0,    0,    0,  123,    0,    0,
    0,    0,    0,    0,    0,    0,  410,    0,    0,  440,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,  447,    0,    0,  453,    0,    0,
  457,    0,    0,    0,    0,    0,    0,    0,    0,
};
final static short yygindex[] = {                         0,
    3,   48,  376,    0,  -64,    0,    0,    0,  464,  -45,
  451,    0,  424,  436,    0,   -8,  170,  448,    0,  307,
  455,  365,  456,  366,
};
final static int YYTABLESIZE=721;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         60,
   38,   38,   60,   38,   23,   60,   26,   38,   46,   98,
    2,   29,   40,   42,  115,  149,  161,   80,   80,   81,
   81,  157,   47,  176,   86,   80,   56,   81,   58,   58,
   58,   58,   58,   58,   58,    7,   25,   48,  119,   56,
   29,  120,   49,  151,   10,   25,   58,   58,   58,   58,
   87,   87,   87,    7,   87,   24,   87,  152,  153,   45,
   49,  104,   10,   80,   24,   81,   53,   53,   87,   87,
   45,   87,   83,   83,   83,  137,   83,   80,   83,   81,
   79,   48,   78,  197,   52,   80,   80,   81,   81,   45,
   83,   83,   87,   83,   84,   84,   84,   25,   84,   48,
   84,   25,  105,   70,  138,  139,  170,   74,   71,   74,
   58,   74,   84,   84,  188,   84,  103,  123,   47,   77,
  120,   77,  191,   77,   49,   74,   74,  153,   74,  167,
  100,   78,   87,   78,  109,   78,  107,   77,   77,  136,
   77,   84,   85,  111,    1,   62,  166,   58,   37,   78,
   78,   79,   78,   79,   83,   79,   88,   88,  112,   88,
   62,   88,   62,   48,  121,  129,   63,  113,   59,   79,
   79,  172,   79,   75,  173,   75,   84,   75,   58,   61,
   58,   63,   47,   63,  189,  144,   72,   61,   72,   74,
   72,   75,   75,   58,   75,   88,   61,  148,  118,   25,
   89,   77,   61,   56,   72,   72,   90,   72,   96,   87,
   87,  168,   87,   78,   87,   27,    5,   58,   27,    5,
   65,   27,    5,   28,   27,    5,   28,   27,    5,   28,
  116,  116,  165,   79,   59,  156,   45,   59,  116,   55,
   59,   95,   97,   58,   58,   58,   58,  160,   58,   58,
   58,   58,   58,   58,  175,   75,   27,    5,   28,   58,
  158,  162,   49,   49,  147,   87,   87,   87,   72,   46,
   87,   87,   87,   87,   87,   87,   73,    5,   28,   51,
   51,   87,  163,   74,   75,   76,   77,   83,   83,   83,
  116,  174,   83,   83,   83,   83,   83,   83,  116,  116,
  122,   48,   48,   83,  107,   56,  117,  107,  178,   84,
   84,   84,  171,  177,   84,   84,   84,   84,   84,   84,
   47,   47,   74,   74,   74,   84,  179,   74,   74,   74,
   74,   74,   74,   46,   77,   77,   77,  180,   74,   77,
   77,   77,   77,   77,   77,  182,   78,   78,   78,  181,
   77,   78,   78,   78,   78,   78,   78,  184,   62,   76,
   92,   76,   78,   76,  186,   11,   79,   79,   79,  185,
   88,   79,   79,   79,   79,   79,   79,   76,   76,   63,
   76,  195,   79,   27,    5,   28,   88,  198,   75,   75,
   75,   58,   61,   75,   75,   75,   75,   75,   75,  190,
   61,   72,   72,   72,   75,  187,   72,   72,   72,   72,
   72,   72,   73,   93,   73,   69,   73,   72,   69,  199,
    8,  145,   25,   87,   70,   68,   21,   69,   60,   71,
   73,   73,   25,   73,  127,  128,   22,   22,    3,   87,
  169,   76,   34,   60,   24,   60,   22,   22,   61,   61,
    7,  103,  141,  143,   82,   50,   54,   44,   22,   10,
   62,   66,   57,   99,   19,   19,    7,   23,   33,  146,
   49,   46,   46,   22,   19,   19,   72,   22,  110,  183,
   68,   67,  114,   68,   54,  164,   19,  108,  108,    0,
  108,   91,    5,  106,   73,    6,  106,  104,    4,    5,
  104,   19,    6,    0,    8,   19,  135,    0,    9,    7,
    0,    8,   27,    5,   28,    9,    0,  150,   10,    0,
    0,    0,  125,  126,  102,    0,  192,  130,    0,   22,
    0,    0,  194,    0,  132,  134,  159,    0,   91,    5,
    0,    0,    6,   61,  124,    5,   28,   91,    5,   28,
    0,    8,   91,    5,   93,    9,    6,   19,    0,    0,
   91,    5,   93,  146,    6,    8,  146,    0,    0,    9,
  130,    0,    0,    8,   76,   76,   76,    9,    0,   76,
   76,   76,   76,   76,   76,  131,    5,   28,   43,    5,
   76,    0,    6,   43,    5,    0,    0,    6,    0,    7,
    0,    8,    0,    0,    7,    9,    8,    0,   10,    0,
    9,    0,    0,   10,   22,    0,    0,   22,  196,  133,
    5,   28,   22,   22,   22,    0,   22,   73,   73,   73,
    0,    0,   73,   73,   73,   73,   73,   73,  124,    5,
   28,   60,   19,   73,    0,   19,  140,    5,   28,    0,
   19,   19,   19,    0,   19,  101,    5,   91,    5,    6,
    0,    6,    0,   91,    5,    0,    7,    6,    8,    0,
    8,    0,    9,    0,    9,   10,    8,   43,    5,    0,
    9,    6,    4,    5,    0,    0,    6,    0,    7,  193,
    8,    0,    0,    7,    9,    8,    0,   10,    0,    9,
   41,    5,   10,   25,    6,   70,   68,    0,   69,    0,
   71,    7,    0,    8,  142,    5,   28,    9,    0,    0,
   10,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         40,
  123,  123,   40,  123,    2,   40,   40,  123,   59,   41,
  123,   40,   40,   11,   60,   41,   41,   43,   43,   45,
   45,   44,   59,   41,   33,   43,   46,   45,   41,   42,
   43,   44,   45,   46,   47,  267,   40,  257,   41,   46,
   40,   44,   41,  257,  276,   40,   59,   60,   61,   62,
   41,   42,   43,  267,   45,   59,   47,  271,  104,   12,
   59,   40,  276,   43,   59,   45,   44,   44,   59,   60,
   23,   62,   41,   42,   43,   84,   45,   43,   47,   45,
   60,   41,   62,   41,   61,   43,   43,   45,   45,   42,
   59,   60,   41,   62,   41,   42,   43,   40,   45,   59,
   47,   40,  258,   42,  261,  262,  152,   41,   47,   43,
  123,   45,   59,   60,  179,   62,   59,   41,   59,   41,
   44,   43,  187,   45,  123,   59,   60,  173,   62,  138,
  125,   41,  123,   43,   46,   45,  257,   59,   60,  262,
   62,  261,  262,  257,  257,   44,  262,   46,  270,   59,
   60,   41,   62,   43,  123,   45,   42,   43,   41,   45,
   59,   47,   61,  123,   41,   41,   44,   40,   46,   59,
   60,   41,   62,   41,   44,   43,  123,   45,   44,   44,
   46,   59,  123,   61,  182,   59,   41,   44,   43,  123,
   45,   59,   60,   59,   62,   42,   61,   41,   41,   40,
   47,  123,   59,   46,   59,   60,   37,   62,   39,   42,
   43,   59,   45,  123,   47,  256,  257,  258,  256,  257,
  258,  256,  257,  258,  256,  257,  258,  256,  257,  258,
  256,  256,  262,  123,  275,  258,  189,  275,  256,  259,
  275,  270,  274,  256,  257,  258,  259,  272,  261,  262,
  263,  264,  265,  266,  272,  123,  256,  257,  258,  272,
  257,  257,  261,  262,   95,  256,  257,  258,  123,   59,
  261,  262,  263,  264,  265,  266,  256,  257,  258,  257,
  257,  272,  257,  263,  264,  265,  266,  256,  257,  258,
  256,  258,  261,  262,  263,  264,  265,  266,  256,  256,
   41,  261,  262,  272,   41,   46,  272,   44,  272,  256,
  257,  258,  257,  257,  261,  262,  263,  264,  265,  266,
  261,  262,  256,  257,  258,  272,   41,  261,  262,  263,
  264,  265,  266,  123,  256,  257,  258,  262,  272,  261,
  262,  263,  264,  265,  266,  123,  256,  257,  258,  257,
  272,  261,  262,  263,  264,  265,  266,  257,  257,   41,
  125,   43,  272,   45,  257,  123,  256,  257,  258,  272,
  256,  261,  262,  263,  264,  265,  266,   59,   60,  257,
   62,   40,  272,  256,  257,  258,  272,   59,  256,  257,
  258,  257,  257,  261,  262,  263,  264,  265,  266,  257,
  257,  256,  257,  258,  272,  123,  261,  262,  263,  264,
  265,  266,   41,   38,   43,   41,   45,  272,   44,  125,
    0,  125,   40,  256,   42,   43,   59,   45,   44,   47,
   59,   60,   40,   62,   70,   71,    1,    2,    0,  272,
  257,  123,   59,   59,   59,   61,   11,   12,   25,   26,
  267,   59,   88,   89,   31,   20,   21,  125,   23,  276,
   25,   26,  125,   40,    1,    2,    0,   59,   59,   94,
   20,  261,  262,   38,   11,   12,   29,   42,   55,  173,
   41,   26,   59,   44,   49,  120,   23,   41,   53,   -1,
   44,  256,  257,   41,  123,  260,   44,   41,  256,  257,
   44,   38,  260,   -1,  269,   42,   83,   -1,  273,  267,
   -1,  269,  256,  257,  258,  273,   -1,  125,  276,   -1,
   -1,   -1,   68,   69,  125,   -1,  125,   73,   -1,   94,
   -1,   -1,  125,   -1,   80,   81,  113,   -1,  256,  257,
   -1,   -1,  260,  120,  256,  257,  258,  256,  257,  258,
   -1,  269,  256,  257,  179,  273,  260,   94,   -1,   -1,
  256,  257,  187,  188,  260,  269,  191,   -1,   -1,  273,
  116,   -1,   -1,  269,  256,  257,  258,  273,   -1,  261,
  262,  263,  264,  265,  266,  256,  257,  258,  256,  257,
  272,   -1,  260,  256,  257,   -1,   -1,  260,   -1,  267,
   -1,  269,   -1,   -1,  267,  273,  269,   -1,  276,   -1,
  273,   -1,   -1,  276,  179,   -1,   -1,  182,  195,  256,
  257,  258,  187,  188,  189,   -1,  191,  256,  257,  258,
   -1,   -1,  261,  262,  263,  264,  265,  266,  256,  257,
  258,  257,  179,  272,   -1,  182,  256,  257,  258,   -1,
  187,  188,  189,   -1,  191,  256,  257,  256,  257,  260,
   -1,  260,   -1,  256,  257,   -1,  267,  260,  269,   -1,
  269,   -1,  273,   -1,  273,  276,  269,  256,  257,   -1,
  273,  260,  256,  257,   -1,   -1,  260,   -1,  267,  268,
  269,   -1,   -1,  267,  273,  269,   -1,  276,   -1,  273,
  256,  257,  276,   40,  260,   42,   43,   -1,   45,   -1,
   47,  267,   -1,  269,  256,  257,  258,  273,   -1,   -1,
  276,
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
"prog : ID '{' listasentencias '}'",
"prog : '{' listasentencias '}'",
"prog : ID '{' listasentencias",
"prog : ID listasentencias '}'",
"prog : ID '{' listasentencias error '}'",
"prog : ID '{' error '}'",
"prog : ID '{' listasentencias error",
"prog : ID '{' error",
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
"sentenciaejecutable : funcion",
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
"pinvocacion : '(' tipo ID ')' '{' listasentenciasejecutables '}'",
"pinvocacion : '(' tipo ID ')' '{' listasentenciasejecutables",
"pinvocacion : '(' tipo ID ')' listasentenciasejecutables '}'",
"pinvocacion : '(' tipo ID ')' listasentenciasejecutables",
"pinvocacion : expresion FLECHA",
"pinvocacion : TRUNC '(' expresion ')' FLECHA",
};

//#line 174 "gramatica.y"

private void yyerror(String s) {
    System.out.println("Error: " + s);
}

public Parser(Map<String, Simbolo> tablaSimbolos, String path)
{
  this.tablaSimbolos = tablaSimbolos;
  this.lexer = new Lexico(tablaSimbolos);
  this.lexer.cargarBuffer(path);
}
//#line 520 "Parser.java"
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
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": la última sentencia del programa es inválida");}
break;
case 6:
//#line 16 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": no hay una sentencia válida dentro del cuerpo del programa");}
break;
case 7:
//#line 17 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": el programa terminó de forma inválida");}
break;
case 8:
//#line 18 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": no hay una sentencia válida dentro del cuerpo del programa");}
break;
case 9:
//#line 21 "gramatica.y"
{System.out.println("Se detectó una listasentencias sentencia ;");}
break;
case 10:
//#line 22 "gramatica.y"
{System.out.println("Se detectó una sentencia ;");}
break;
case 11:
//#line 23 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": no hay una sentencia válida antes del punto y coma");}
break;
case 12:
//#line 24 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": no hay una sentencia válida antes del punto y coma");}
break;
case 13:
//#line 28 "gramatica.y"
{System.out.println("Se detectó una sentenciaejecutable ;");}
break;
case 14:
//#line 29 "gramatica.y"
{System.out.println("Se detectó una declaración ;");}
break;
case 19:
//#line 38 "gramatica.y"
{System.out.println("Se detectó un print");}
break;
case 20:
//#line 39 "gramatica.y"
{System.out.println("Se obtuvo una sentencia desde una invocación a función");}
break;
case 21:
//#line 42 "gramatica.y"
{System.out.println("Se detectó una declaración de variables");}
break;
case 22:
//#line 43 "gramatica.y"
{System.out.println("Se detectó una declaración de función");}
break;
case 23:
//#line 46 "gramatica.y"
{System.out.println("Se detectó una asignación simple");}
break;
case 24:
//#line 47 "gramatica.y"
{System.out.println("Se detectó una asignación múltiple");}
break;
case 27:
//#line 52 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta coma entre constantes");}
break;
case 30:
//#line 59 "gramatica.y"
{System.out.println("Se detectó un bloque IF-ELSE");}
break;
case 31:
//#line 60 "gramatica.y"
{System.out.println("Se detectó un bloque IF");}
break;
case 32:
//#line 61 "gramatica.y"
{System.out.println("Se detectó un bloque DO UNTIL");}
break;
case 33:
//#line 63 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta la palabra ENDIF al final del bloque IF");}
break;
case 34:
//#line 64 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta la palabra ENDIF al final del bloque IF");}
break;
case 35:
//#line 65 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el cuerpo en el bloque ELSE");}
break;
case 36:
//#line 66 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el cuerpo en el bloque IF");}
break;
case 37:
//#line 67 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el cuerpo en el bloque IF");}
break;
case 38:
//#line 68 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el cuerpo en los bloques IF y ELSE");}
break;
case 39:
//#line 70 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta de cuerpo en iteracion");}
break;
case 40:
//#line 71 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta la palabra UNTIL en la iteracion");}
break;
case 43:
//#line 76 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta de argumento en sentencia print");}
break;
case 44:
//#line 79 "gramatica.y"
{System.out.println("Se detectó una condición entre paréntesis");}
break;
case 45:
//#line 80 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el paréntesis de apertura de la condición");}
break;
case 46:
//#line 81 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el paréntesis de cierre de la condición");}
break;
case 47:
//#line 82 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": faltan ambos paréntesis de la condición");}
break;
case 49:
//#line 86 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operador comparador en la condición");}
break;
case 58:
//#line 101 "gramatica.y"
{System.out.println("Se detectó un IDENTIFICADOR");}
break;
case 59:
//#line 102 "gramatica.y"
{System.out.println("Se detectó un IDENTIFICADOR compuesto");}
break;
case 62:
//#line 107 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta coma entre variables");}
break;
case 63:
//#line 108 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta coma entre variables");}
break;
case 68:
//#line 117 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el nombre del parámetro");}
break;
case 69:
//#line 118 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el nombre del parámetro");}
break;
case 70:
//#line 119 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el tipo del parámetro");}
break;
case 71:
//#line 120 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el tipo del parámetro");}
break;
case 72:
//#line 123 "gramatica.y"
{System.out.println("Se detectó una expresión desde una suma");}
break;
case 73:
//#line 124 "gramatica.y"
{System.out.println("Se detectó una expresión desde una resta");}
break;
case 74:
//#line 125 "gramatica.y"
{System.out.println("Se detectó una expresión desde un término");}
break;
case 75:
//#line 126 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando derecho de la suma");}
break;
case 76:
//#line 127 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando derecho de la resta");}
break;
case 77:
//#line 128 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando izquierdo de la suma");}
break;
case 78:
//#line 129 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando izquierdo de la resta");}
break;
case 79:
//#line 130 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta de operador");}
break;
case 82:
//#line 135 "gramatica.y"
{System.out.println("Se detecto un TERMINO desde un FACTOR");}
break;
case 83:
//#line 136 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando derecho de la multiplicación");}
break;
case 84:
//#line 137 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando derecho de la división");}
break;
case 85:
//#line 138 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando izquierdo de la multiplicación");}
break;
case 86:
//#line 139 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando izquierdo de la división");}
break;
case 87:
//#line 142 "gramatica.y"
{System.out.println("Se detectó un FACTOR IDENTIFICADOR");}
break;
case 88:
//#line 143 "gramatica.y"
{System.out.println("Se detectó un FACTOR CTE");}
break;
case 89:
//#line 144 "gramatica.y"
{System.out.println("Se detectó un FACTOR FUNCIÓN");}
break;
case 90:
//#line 147 "gramatica.y"
{System.out.println("Se detectó una función invocada correctamente");}
break;
case 91:
//#line 148 "gramatica.y"
{System.out.println("Se detectó una función invocada correctamente");}
break;
case 92:
//#line 149 "gramatica.y"
{System.out.println("Se detectó una función invocada correctamente");}
break;
case 93:
//#line 150 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el nombre de la funcion");}
break;
case 94:
//#line 151 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el nombre de la funcion");}
break;
case 95:
//#line 152 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el nombre de la funcion");}
break;
case 100:
//#line 161 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el paréntesis de cierre de la función TRUNC");}
break;
case 101:
//#line 162 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el paréntesis de apertura de la función TRUNC");}
break;
case 102:
//#line 163 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": faltan ambos paréntesis de la función TRUNC");}
break;
case 104:
//#line 165 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta llave de cierre del cuerpo de la función Lambda");}
break;
case 105:
//#line 166 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta llave de apertura del cuerpo de la función Lambda");}
break;
case 106:
//#line 167 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": faltan ambas llaves del cuerpo de la función Lambda");}
break;
case 107:
//#line 168 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": No se especifica el parametro formal al que corresponde el parámetro real");}
break;
case 108:
//#line 169 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": No se especifica el parametro formal al que corresponde el parámetro real");}
break;
//#line 969 "Parser.java"
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
