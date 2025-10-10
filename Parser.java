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
    5,    6,    6,    6,   14,   14,   14,   14,   15,   15,
   16,   16,   16,   16,   16,   16,    8,    8,   12,   12,
    9,    9,    9,    9,   10,   10,   17,   17,   17,   17,
   17,   17,   11,   11,   11,   11,   11,   11,   11,   11,
   18,   18,   18,   18,   18,   18,   18,   19,   19,   19,
    7,    7,    7,    7,    7,    7,   20,   20,   21,   21,
   21,   21,   21,   21,   21,   21,   21,   21,   21,
};
final static short yylen[] = {                            2,
    4,    3,    3,    3,    2,    3,    2,    1,    1,    1,
    1,    1,    1,    2,   13,    3,    3,    3,    1,    2,
   10,    6,    6,    9,    5,    9,    9,    5,    8,    5,
    5,    4,    4,    3,    3,    2,    2,    1,    3,    2,
    1,    1,    1,    1,    1,    1,    1,    1,    1,    3,
    3,    1,    2,    4,    3,    1,    3,    2,    2,    1,
    2,    1,    3,    3,    1,    3,    3,    3,    3,    3,
    3,    3,    1,    3,    3,    3,    3,    1,    1,    1,
    4,    4,    4,    4,    4,    4,    3,    1,    3,    6,
    5,    5,    4,    7,    6,    6,    5,    2,    5,
};
final static short yydefred[] = {                         0,
    0,    0,    0,    0,    0,    0,   47,    0,    0,   48,
    0,    0,    0,    9,   10,   11,   12,   13,    0,    0,
    0,    0,    0,    0,    0,   79,    0,   80,    0,    0,
    0,    0,    0,   73,    0,    0,    0,    0,    4,    0,
    7,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    2,    0,    0,    0,    0,    0,    0,   88,    0,    0,
    0,    0,    0,    0,    0,    0,    0,   43,   44,   45,
   46,   41,   42,    0,    0,    0,    0,    0,   36,    0,
    0,    0,    0,    0,   34,    0,    1,    6,    0,   19,
    0,   49,    0,    0,    0,   50,   86,    0,    0,    0,
    0,    0,   85,   84,    0,   83,   82,   81,    0,    0,
    0,   76,   77,   35,    0,    0,    0,    0,    0,    0,
    0,    0,    0,   71,    0,   72,    0,    0,   33,   32,
   62,    0,    0,    0,   56,   20,    0,    0,    0,    0,
    0,    0,   89,   87,    0,   28,    0,   30,    0,   31,
   61,    0,   58,    0,    0,   18,    0,    0,   93,    0,
    0,    0,    0,   22,   23,   57,    0,   55,   91,    0,
   92,    0,    0,    0,    0,    0,    0,   90,    0,   96,
   29,    0,    0,    0,    0,   94,   27,   26,    0,    0,
   21,    0,    0,    0,   15,
};
final static short yydgoto[] = {                          3,
   12,   13,   14,   15,   16,   17,   28,   19,   20,  134,
   29,   30,   91,   31,   32,   77,  135,   33,   34,   57,
   58,
};
final static short yysindex[] = {                      -118,
  255,  549,    0,  -32,   -4,  144,    0,  -95,   30,    0,
  549,  384,    7,    0,    0,    0,    0,    0, -174,  -17,
   -3,  402,  -40,  -37,  504,    0, -245,    0,  364,   46,
  -25,   62,  -33,    0,  407,  -31,  425,  -32,    0,   50,
    0,   77,    2,   46,    0, -123,  -88,  146, -245,  -67,
    0,  121,  157, -260,  -41,   18,   88,    0,  160,   49,
   99,  109,  109,  178,  178,  166,  313,    0,    0,    0,
    0,    0,    0,  190,  210,    9, -245,  430,    0,  228,
  233,  -59,  448,  172,    0,   10,    0,    0,  226,    0,
  -29,    0,   46,  -27,    9,    0,    0, -245,  -24,  -15,
  109,   -2,    0,    0,  -34,    0,    0,    0,  151,  -33,
  -33,    0,    0,    0,  -33,  151,  -33,  151,  -33,    9,
 -107,  453,  -32,    0,  -32,    0,  144,   94,    0,    0,
    0, -120,    5,  122,    0,    0,   17,    0,  -23,   29,
  -38,  247,    0,    0,  173,    0,  149,    0,  144,    0,
    0,   40,    0,  196,  226,    0,   64,   70,    0,  100,
  285,  471,  236,    0,    0,    0,  549,    0,    0,  119,
    0,  549,  476,  125,  498,  513,  308,    0,  527,    0,
    0,  132,  156,  541,  355,    0,    0,    0,  159, -245,
    0,   20,  363,  302,    0,
};
final static short yyrindex[] = {                         0,
    0,    0,    0,    1,  -12,    0,    0,    0,    0,    0,
    0,  428,    0,    0,    0,    0,    0,    0,    0,    0,
   92,    0,    0,    0,    0,    0,    0,    0,    0,   15,
    0,  -22,   48,    0,    0,    0,  431,    0,    0,    0,
    0,   69,  366,   28,  -21,    0,    0,    0,    0,    0,
    0,  161,    0,    0,    0,  167,    0,    0,  161,  167,
    0,    0,    0,    0,    0,   22,    0,    0,    0,    0,
    0,    0,    0,    0,    0,   35,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
  373,    0,   61,    0,  390,    0,    0,    0,    0,    0,
    0,  133,    0,    0,    0,    0,    0,    0,    0,   59,
   71,    0,    0,    0,   82,  105,  116,  127,  140,   65,
    0,    0,   26,    0,   37,    0,    0,    0,    0,    0,
    0,    0,  223,    0,    0,    0,    0,  -20,    0,    0,
    0,    0,    0,    0,    0,    0,  395,    0,    0,    0,
    0,  266,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,  289,
    0,    0,  300,    0,    0,    0,    0,    0,  345,    0,
    0,    0,    0,    0,    0,    0,    0,    0,  397,    0,
    0,    0,    0,    0,    0,
};
final static short yygindex[] = {                         0,
  297,  440,    0,    0,    0,    0,  470,  362,  424,    0,
  421,  418,    0,   24,  433,    0,  323,  413,  115,  439,
  374,
};
final static int YYTABLESIZE=825;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         54,
    8,   74,   54,   75,    2,   54,    7,   23,   80,   85,
   25,    5,   26,   81,  137,   10,  141,  158,   74,   74,
   75,   75,   53,   54,   49,   50,   47,   35,   49,   49,
   49,   49,   49,   49,   49,   24,   38,   53,   54,   53,
   54,    8,   50,   46,    8,   47,   49,   49,   49,   49,
  130,   74,   74,   75,   75,   78,   78,   78,  103,   78,
  193,   78,   74,   50,   75,   41,   74,   74,   74,   36,
   74,   52,   74,   78,   78,   40,   78,   75,   75,   75,
   37,   75,   42,   75,   74,   74,   52,   74,   65,  107,
   65,   50,   65,   40,   50,   75,   75,   78,   75,   68,
   38,   68,   79,   68,   51,   39,   65,   65,   88,   65,
   49,   69,   49,   69,   49,   69,   89,   68,   68,   51,
   68,   51,   70,   39,   70,    8,   70,   49,  104,   69,
   69,  105,   69,   27,   90,   52,  151,   78,    1,  108,
   70,   70,  105,   70,   37,   66,    7,   66,   74,   66,
  148,  150,   52,  145,  146,   10,   63,   40,   63,   75,
   63,   97,  154,   66,   66,  155,   66,   67,   92,   67,
   65,   67,  165,   98,   63,   63,   98,   63,  112,  113,
   64,   68,   64,   27,   64,   67,   67,   39,   67,   96,
   23,   94,   64,   69,  124,  126,   98,   65,   64,   64,
  106,   64,   79,   79,   70,   79,  114,   79,   78,   78,
  127,   78,  129,   78,  101,   25,    5,   52,   25,    5,
   59,   25,    5,   26,   25,    5,   26,   66,  136,  138,
  102,  101,  101,  160,   53,   53,   54,   53,   63,   45,
   53,  142,   84,   49,   49,   49,   49,  140,  157,   67,
   49,   49,   49,   49,  143,   49,    8,    8,   45,   49,
    8,  153,   64,   60,  101,  101,   60,    8,    8,    8,
   78,   78,   78,    8,  156,  101,    8,   78,   78,   78,
   78,   74,   74,   74,   52,  159,   78,  161,   74,   74,
   74,   74,   75,   75,   75,  162,  166,   74,   22,   75,
   75,   75,   75,   65,   65,   65,   59,   37,   75,   59,
   65,   65,   65,   65,   68,   68,   68,   51,  167,   65,
  169,   68,   68,   68,   68,   49,   69,   69,   69,   99,
   68,   83,   99,   69,   69,   69,   69,   70,   70,   70,
   97,  170,   69,   97,   70,   70,   70,   70,   52,   25,
    5,   26,   23,   70,   64,   62,  171,   63,  176,   65,
   66,   66,   66,  149,  109,    5,   26,   66,   66,   66,
   66,   63,   63,   63,  122,  178,   66,   11,   63,   63,
   63,   63,   67,   67,   67,   95,  181,   63,   95,   67,
   67,   67,   67,  187,  190,   64,   64,   64,   67,   25,
    5,   26,   64,   64,   64,   64,   74,  172,   75,  163,
  164,   64,   25,    5,   26,  100,   79,  188,   21,   21,
  191,  194,   78,   73,   14,   72,  195,    5,   21,   21,
    3,   17,   79,   38,    5,   26,   44,   48,   78,   21,
   56,   60,   43,   55,   55,  116,    5,   26,   16,   76,
  133,   40,   21,   25,   21,   24,   86,  173,  175,   66,
   48,   40,   61,  177,   93,  118,    5,   26,  179,   95,
   18,   18,  184,   99,  110,  111,   40,  168,  144,  115,
   18,   18,  131,  123,    5,   26,  117,  119,  125,    5,
   26,   18,    7,  152,    0,   21,  132,  120,    0,    0,
   21,   10,    0,    0,   18,    0,   18,    0,   39,    0,
    4,    5,    0,  115,    6,    0,  133,    0,  139,    0,
    0,    7,   40,    8,    0,   55,   51,    9,    0,    0,
   10,   82,    0,    0,    0,    0,    0,    0,    0,   21,
    4,    5,    0,   23,    6,   64,   62,   18,   63,   87,
   65,    7,   18,    8,  121,    0,    0,    9,    0,    0,
   10,   40,    0,   38,    5,    0,    0,    6,  109,    5,
   26,    0,  128,    0,    7,  185,    8,  147,   21,   21,
    9,    0,    0,   10,   21,    0,    0,    0,    0,   21,
   21,   18,   21,   21,   21,  174,   21,    0,    0,    0,
  180,   21,    0,    0,    0,    0,    0,    0,    0,    0,
  192,    0,   40,    0,   40,    0,   40,    0,   40,   67,
    5,   26,  182,   40,    0,    0,   68,   69,   70,   71,
   18,   18,    0,    0,    0,    0,   18,  183,    0,   38,
    5,   18,   18,    6,   18,   18,   18,    0,   18,    0,
    7,  186,    8,   18,    0,    0,    9,   38,    5,   10,
    0,    6,    4,    5,    0,  189,    6,    0,    7,    0,
    8,    0,    0,    7,    9,    8,    0,   10,    0,    9,
   38,    5,   10,    0,    6,    4,    5,    0,    0,    6,
    0,    7,    0,    8,    0,    0,    7,    9,    8,    0,
   10,    0,    9,   38,    5,   10,    0,    6,   38,    5,
    0,    0,    6,    0,    7,    0,    8,    0,    0,    7,
    9,    8,    0,   10,    0,    9,    4,    5,   10,    0,
    6,   38,    5,    0,    0,    6,    0,    7,    0,    8,
    0,    0,    7,    9,    8,    0,   10,    0,    9,    0,
    0,   10,    0,   38,    5,    0,    0,    6,    0,    0,
    0,    0,    0,    0,    7,    0,    8,    0,    4,    5,
    9,    0,    6,   10,    0,    0,    0,    0,    0,    7,
    0,    8,   38,    5,    0,    9,    6,    0,   10,    0,
    0,    0,    0,    7,    0,    8,   38,    5,    0,    9,
    6,    0,   10,    0,    4,    5,    0,    7,    6,    8,
    0,    0,    0,    9,    0,    7,   10,    8,    0,    0,
    0,    9,    0,    0,   10,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         40,
    0,   43,   40,   45,  123,   40,  267,   40,   42,   41,
  256,  257,  258,   47,   44,  276,   41,   41,   43,   43,
   45,   45,   44,   44,   46,   46,   44,  123,   41,   42,
   43,   44,   45,   46,   47,   40,   59,   59,   59,   61,
   61,   41,   46,   61,   44,   44,   59,   60,   61,   62,
   41,   43,   43,   45,   45,   41,   42,   43,   41,   45,
   41,   47,   43,   46,   45,   59,   41,   42,   43,   40,
   45,   44,   47,   59,   60,   41,   62,   41,   42,   43,
   59,   45,  257,   47,   59,   60,   59,   62,   41,   41,
   43,   46,   45,   59,   46,   59,   60,  123,   62,   41,
  123,   43,   41,   45,   44,   41,   59,   60,   59,   62,
  123,   41,   44,   43,   46,   45,   40,   59,   60,   59,
   62,   61,   41,   59,   43,  125,   45,   59,   41,   59,
   60,   44,   62,   40,  258,   44,  257,  123,  257,   41,
   59,   60,   44,   62,  123,   41,  267,   43,  123,   45,
  127,  128,   61,  261,  262,  276,   41,  123,   43,  123,
   45,   41,   41,   59,   60,   44,   62,   41,  257,   43,
  123,   45,  149,   41,   59,   60,   44,   62,   64,   65,
   41,  123,   43,   40,   45,   59,   60,  123,   62,  257,
   40,   46,   42,  123,   80,   81,   40,   47,   59,   60,
   41,   62,   42,   43,  123,   45,   41,   47,   42,   43,
  270,   45,   41,   47,  256,  256,  257,  258,  256,  257,
  258,  256,  257,  258,  256,  257,  258,  123,  258,  257,
  272,  256,  256,  272,  275,  257,  257,  275,  123,  257,
  275,  257,  274,  256,  257,  258,  259,  272,  272,  123,
  263,  264,  265,  266,  257,  259,  256,  257,  257,  272,
  260,  257,  123,   41,  256,  256,   44,  267,  268,  269,
  256,  257,  258,  273,  258,  256,  276,  263,  264,  265,
  266,  256,  257,  258,  257,  257,  272,   41,  263,  264,
  265,  266,  256,  257,  258,  123,  257,  272,    2,  263,
  264,  265,  266,  256,  257,  258,   41,   11,  272,   44,
  263,  264,  265,  266,  256,  257,  258,  257,  123,  272,
  257,  263,  264,  265,  266,  257,  256,  257,  258,   41,
  272,   35,   44,  263,  264,  265,  266,  256,  257,  258,
   41,  272,  272,   44,  263,  264,  265,  266,  257,  256,
  257,  258,   40,  272,   42,   43,  257,   45,  123,   47,
  256,  257,  258,  270,  256,  257,  258,  263,  264,  265,
  266,  256,  257,  258,   78,  257,  272,  123,  263,  264,
  265,  266,  256,  257,  258,   41,  262,  272,   44,  263,
  264,  265,  266,  262,   40,  256,  257,  258,  272,  256,
  257,  258,  263,  264,  265,  266,   43,  123,   45,  261,
  262,  272,  256,  257,  258,   54,  256,  262,    1,    2,
  262,   59,  256,   60,   59,   62,  125,    0,   11,   12,
    0,   59,  272,  256,  257,  258,   19,   20,  272,   22,
   23,   24,   19,   23,   24,  256,  257,  258,   59,   29,
   89,   12,   35,   59,   37,   59,   36,  161,  162,   27,
   43,   22,   24,  167,   47,  256,  257,  258,  172,   49,
    1,    2,  176,   53,   62,   63,   37,  155,  105,   67,
   11,   12,  257,  256,  257,  258,   74,   75,  256,  257,
  258,   22,  267,  132,   -1,   78,  271,   77,   -1,   -1,
   83,  276,   -1,   -1,   35,   -1,   37,   -1,  125,   -1,
  256,  257,   -1,  101,  260,   -1,  155,   -1,   98,   -1,
   -1,  267,   83,  269,   -1,  105,  125,  273,   -1,   -1,
  276,  125,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  122,
  256,  257,   -1,   40,  260,   42,   43,   78,   45,  125,
   47,  267,   83,  269,  125,   -1,   -1,  273,   -1,   -1,
  276,  122,   -1,  256,  257,   -1,   -1,  260,  256,  257,
  258,   -1,  125,   -1,  267,  268,  269,  125,  161,  162,
  273,   -1,   -1,  276,  167,   -1,   -1,   -1,   -1,  172,
  173,  122,  175,  176,  177,  125,  179,   -1,   -1,   -1,
  125,  184,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
  190,   -1,  173,   -1,  175,   -1,  177,   -1,  179,  256,
  257,  258,  125,  184,   -1,   -1,  263,  264,  265,  266,
  161,  162,   -1,   -1,   -1,   -1,  167,  125,   -1,  256,
  257,  172,  173,  260,  175,  176,  177,   -1,  179,   -1,
  267,  125,  269,  184,   -1,   -1,  273,  256,  257,  276,
   -1,  260,  256,  257,   -1,  125,  260,   -1,  267,   -1,
  269,   -1,   -1,  267,  273,  269,   -1,  276,   -1,  273,
  256,  257,  276,   -1,  260,  256,  257,   -1,   -1,  260,
   -1,  267,   -1,  269,   -1,   -1,  267,  273,  269,   -1,
  276,   -1,  273,  256,  257,  276,   -1,  260,  256,  257,
   -1,   -1,  260,   -1,  267,   -1,  269,   -1,   -1,  267,
  273,  269,   -1,  276,   -1,  273,  256,  257,  276,   -1,
  260,  256,  257,   -1,   -1,  260,   -1,  267,   -1,  269,
   -1,   -1,  267,  273,  269,   -1,  276,   -1,  273,   -1,
   -1,  276,   -1,  256,  257,   -1,   -1,  260,   -1,   -1,
   -1,   -1,   -1,   -1,  267,   -1,  269,   -1,  256,  257,
  273,   -1,  260,  276,   -1,   -1,   -1,   -1,   -1,  267,
   -1,  269,  256,  257,   -1,  273,  260,   -1,  276,   -1,
   -1,   -1,   -1,  267,   -1,  269,  256,  257,   -1,  273,
  260,   -1,  276,   -1,  256,  257,   -1,  267,  260,  269,
   -1,   -1,   -1,  273,   -1,  267,  276,  269,   -1,   -1,
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
"control : IF condicion '{' listasentencia '}' ELSE '{' listasentencia '}' ENDIF",
"control : IF condicion '{' listasentencia '}' ENDIF",
"control : DO '{' listasentencia '}' UNTIL condicion",
"control : IF condicion '{' listasentencia '}' ELSE '{' listasentencia '}'",
"control : IF condicion '{' listasentencia '}'",
"control : IF condicion '{' listasentencia '}' ELSE '{' '}' ENDIF",
"control : IF condicion '{' '}' ELSE '{' listasentencia '}' ENDIF",
"control : IF condicion '{' '}' ENDIF",
"control : IF condicion '{' '}' ELSE '{' '}' ENDIF",
"control : DO '{' '}' UNTIL condicion",
"control : DO '{' listasentencia '}' condicion",
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
"pinvocacion : '(' tipo ID ')' '{' listasentencia '}'",
"pinvocacion : '(' tipo ID ')' '{' listasentencia",
"pinvocacion : '(' tipo ID ')' listasentencia '}'",
"pinvocacion : '(' tipo ID ')' listasentencia",
"pinvocacion : expresion FLECHA",
"pinvocacion : TRUNC '(' expresion ')' FLECHA",
};

//#line 158 "gramatica.y"

private void yyerror(String s) {
    System.out.println("Error: " + s);
}

public Parser(Map<String, Simbolo> tablaSimbolos, String path)
{
  this.tablaSimbolos = tablaSimbolos;
  this.lexer = new Lexico(tablaSimbolos);
  this.lexer.cargarBuffer(path);
}
//#line 529 "Parser.java"
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
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": faltan llaves que encierren el cuerpo del programa");}
break;
case 6:
//#line 18 "gramatica.y"
{System.out.println("Se detectó una listasentencia sentencia ;");}
break;
case 7:
//#line 19 "gramatica.y"
{System.out.println("Se detectó una sentencia ;");}
break;
case 8:
//#line 20 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta de punto y coma ó sentencia inválida");}
break;
case 12:
//#line 26 "gramatica.y"
{System.out.println("Se detectó un print");}
break;
case 13:
//#line 27 "gramatica.y"
{System.out.println("Se obtuvo una sentencia desde una invocación a función");}
break;
case 14:
//#line 30 "gramatica.y"
{System.out.println("Se detectó una declaración de variables");}
break;
case 15:
//#line 31 "gramatica.y"
{System.out.println("Se detectó una declaración de función");}
break;
case 16:
//#line 34 "gramatica.y"
{System.out.println("Se detectó una asignación simple");}
break;
case 17:
//#line 35 "gramatica.y"
{System.out.println("Se detectó una asignación múltiple");}
break;
case 20:
//#line 40 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta coma entre constantes");}
break;
case 21:
//#line 43 "gramatica.y"
{System.out.println("Se detectó un bloque IF-ELSE");}
break;
case 22:
//#line 44 "gramatica.y"
{System.out.println("Se detectó un bloque IF");}
break;
case 23:
//#line 45 "gramatica.y"
{System.out.println("Se detectó un bloque DO UNTIL");}
break;
case 24:
//#line 47 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta la palabra ENDIF al final del bloque IF");}
break;
case 25:
//#line 48 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta la palabra ENDIF al final del bloque IF");}
break;
case 26:
//#line 49 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el cuerpo en el bloque ELSE");}
break;
case 27:
//#line 50 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el cuerpo en el bloque IF");}
break;
case 28:
//#line 51 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el cuerpo en el bloque IF");}
break;
case 29:
//#line 52 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el cuerpo en los bloques IF y ELSE");}
break;
case 30:
//#line 54 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta de cuerpo en iteracion");}
break;
case 31:
//#line 55 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta la palabra UNTIL en la iteracion");}
break;
case 34:
//#line 60 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta de argumento en sentencia print");}
break;
case 35:
//#line 63 "gramatica.y"
{System.out.println("Se detectó una condición entre paréntesis");}
break;
case 36:
//#line 64 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el paréntesis de apertura de la condición");}
break;
case 37:
//#line 65 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el paréntesis de cierre de la condición");}
break;
case 38:
//#line 66 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": faltan ambos paréntesis de la condición");}
break;
case 40:
//#line 70 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operador comparador en la condición");}
break;
case 49:
//#line 85 "gramatica.y"
{System.out.println("Se detectó un IDENTIFICADOR");}
break;
case 50:
//#line 86 "gramatica.y"
{System.out.println("Se detectó un IDENTIFICADOR compuesto");}
break;
case 53:
//#line 91 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta coma entre variables");}
break;
case 54:
//#line 92 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta coma entre variables");}
break;
case 59:
//#line 101 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el nombre del parámetro");}
break;
case 60:
//#line 102 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el nombre del parámetro");}
break;
case 61:
//#line 103 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el tipo del parámetro");}
break;
case 62:
//#line 104 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el tipo del parámetro");}
break;
case 63:
//#line 107 "gramatica.y"
{System.out.println("Se detectó una expresión desde una suma");}
break;
case 64:
//#line 108 "gramatica.y"
{System.out.println("Se detectó una expresión desde una resta");}
break;
case 65:
//#line 109 "gramatica.y"
{System.out.println("Se detectó una expresión desde un término");}
break;
case 66:
//#line 110 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando derecho de la suma");}
break;
case 67:
//#line 111 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando derecho de la resta");}
break;
case 68:
//#line 112 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando izquierdo de la suma");}
break;
case 69:
//#line 113 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando izquierdo de la resta");}
break;
case 70:
//#line 114 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta de operador");}
break;
case 73:
//#line 119 "gramatica.y"
{System.out.println("Se detecto un TERMINO desde un FACTOR");}
break;
case 74:
//#line 120 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando derecho de la multiplicación");}
break;
case 75:
//#line 121 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando derecho de la división");}
break;
case 76:
//#line 122 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando izquierdo de la multiplicación");}
break;
case 77:
//#line 123 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando izquierdo de la división");}
break;
case 78:
//#line 126 "gramatica.y"
{System.out.println("Se detectó un FACTOR IDENTIFICADOR");}
break;
case 79:
//#line 127 "gramatica.y"
{System.out.println("Se detectó un FACTOR CTE");}
break;
case 80:
//#line 128 "gramatica.y"
{System.out.println("Se detectó un FACTOR FUNCIÓN");}
break;
case 81:
//#line 131 "gramatica.y"
{System.out.println("Se detectó una función invocada correctamente");}
break;
case 82:
//#line 132 "gramatica.y"
{System.out.println("Se detectó una función invocada correctamente");}
break;
case 83:
//#line 133 "gramatica.y"
{System.out.println("Se detectó una función invocada correctamente");}
break;
case 84:
//#line 134 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el nombre de la funcion");}
break;
case 85:
//#line 135 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el nombre de la funcion");}
break;
case 86:
//#line 136 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el nombre de la funcion");}
break;
case 91:
//#line 145 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el paréntesis de cierre de la función TRUNC");}
break;
case 92:
//#line 146 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el paréntesis de apertura de la función TRUNC");}
break;
case 93:
//#line 147 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": faltan ambos paréntesis de la función TRUNC");}
break;
case 95:
//#line 149 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta llave de cierre del cuerpo de la función Lambda");}
break;
case 96:
//#line 150 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta llave de apertura del cuerpo de la función Lambda");}
break;
case 97:
//#line 151 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": faltan ambas llaves del cuerpo de la función Lambda");}
break;
case 98:
//#line 152 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": No se especifica el parametro formal al que corresponde el parámetro real");}
break;
case 99:
//#line 153 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": No se especifica el parametro formal al que corresponde el parámetro real");}
break;
//#line 954 "Parser.java"
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
