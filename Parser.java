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
   17,   17,   18,   18,   18,    7,    7,    7,    7,    7,
    7,   19,   19,   20,   20,   20,   20,   20,   20,   20,
   20,   20,   20,   20,
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
    3,    3,    1,    1,    1,    4,    4,    4,    4,    4,
    4,    3,    1,    3,    6,    5,    5,    4,    7,    6,
    6,    5,    2,    5,
};
final static short yydefred[] = {                         0,
    0,    0,    0,    0,    0,    0,   52,    0,    0,   53,
    0,    0,    0,    9,   10,   11,   12,   13,    0,    0,
    0,    0,    0,    0,    0,   84,    0,   85,    0,    0,
    0,    0,   78,    0,    0,    0,    0,    4,    0,    7,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    2,
    0,    0,    0,    0,    0,    0,   93,    0,    0,    0,
    0,    0,    0,    0,    0,    0,   48,   49,   50,   51,
   46,   47,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,   43,    0,    1,    6,    0,   19,    0,
   54,    0,    0,    0,   55,   91,    0,    0,    0,    0,
    0,   90,   89,    0,   88,   87,   86,    0,    0,    0,
   81,   82,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,   76,    0,   77,    0,    0,   42,   41,
   67,    0,    0,    0,   61,   20,    0,    0,    0,    0,
    0,    0,   94,   92,    0,    0,    0,    0,    0,    0,
    0,   66,    0,   63,    0,    0,   18,    0,    0,   98,
    0,    0,    0,    0,    0,    0,   29,    0,    0,    0,
    0,    0,   62,    0,   60,   96,    0,   97,    0,    0,
    0,   28,    0,   34,    0,    0,    0,   27,   39,    0,
   36,   40,    0,   95,    0,  101,    0,    0,    0,   22,
    0,    0,   23,    0,   99,    0,    0,    0,    0,    0,
    0,    0,    0,   35,    0,    0,    0,   26,    0,    0,
   25,   33,   32,    0,   24,    0,   21,    0,   15,
};
final static short yydgoto[] = {                          3,
   12,   13,   14,   15,   16,   17,   18,   19,   20,  134,
   29,   21,   90,   31,   76,  135,   32,   33,   56,   57,
};
final static short yysindex[] = {                      -114,
  345,  214,    0,  -15,    6,  -27,    0,  -57,   36,    0,
  214,  401,   22,    0,    0,    0,    0,    0, -174,  -17,
  -44,  419,  -40,  -37,  763,    0, -218,    0,   94,   55,
  -25,  -35,    0,  424,  -31,  442,  -15,    0,   44,    0,
   69,   -1,   55,    0, -153, -121,  101, -218, -117,    0,
  121,  144, -239,   70,   -5,  -30,    0,  128,   18,   51,
  109,  109,  188,  188,   29,  368,    0,    0,    0,    0,
    0,    0,  192,  235,    9, -218,  214,   32,  245,  296,
  -79,  447,  155,    0,   10,    0,    0,  180,    0,  -21,
    0,   55,  -50,    9,    0,    0, -218,  -24,   -2,  109,
   31,    0,    0,  -34,    0,    0,    0,  111,  -35,  -35,
    0,    0,  214,   90,  -35,  111,  -35,  111,  -35,    9,
  470,  214,  -15,    0,  -15,    0,  196,  -36,    0,    0,
    0, -252,   42,  122,    0,    0,  -16,    0,  -23,   53,
    3,  277,    0,    0,  475,  497,  -82,  502, -218,  171,
 -218,    0,   64,    0,  163,  180,    0,   75,   72,    0,
   92,  383,   35,   46,  520,  230,    0,  114,  315, -218,
  337,  348,    0,  214,    0,    0,  129,    0,  214,  528,
  271,    0,  272,    0,  164,  214,  291,    0,    0,  377,
    0,    0,  652,    0,  542,    0,  214,  557,  293,    0,
  565,  214,    0,  380,    0,  583,  169,  588,  609,  170,
  624, -218,  173,    0,  176,  190,  638,    0,  197,   20,
    0,    0,    0,  198,    0,  405,    0,  342,    0,
};
final static short yyrindex[] = {                         0,
    0,    0,    0,    1,  -12,    0,    0,    0,    0,    0,
    0,  478,    0,    0,    0,    0,    0,    0,    0,    0,
   84,    0,    0,    0,    0,    0,    0,    0,    0,   15,
    0,   48,    0,    0,    0,  494,    0,    0,    0,    0,
   76,  450,   28,  363,    0,    0,    0,    0,    0,    0,
  161,    0,    0,    0,  167,    0,    0,  161,  167,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,  -33,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,  453,
    0,   73,    0,  459,    0,    0,    0,    0,    0,    0,
  133,    0,    0,    0,    0,    0,    0,    0,   59,   71,
    0,    0,    0,    0,   82,  105,  116,  127,  140,   65,
    0,    0,   26,    0,   37,    0,    0,    0,    0,    0,
    0,    0,  149,    0,    0,    0,    0,  375,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,  151,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
  461,    0,    0,    0,    0,    0,  157,    0,    0,  218,
    0,    0,    0,    0,  462,    0,    0,    0,    0,  464,
    0,    0,    0,    0,  223,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,  465,    0,    0,    0,    0,    0,
};
final static short yygindex[] = {                         0,
  353,  443,    0,    0,    0,    0,  513,   41,  510,    0,
  493,  434,    0,  292,    0,  374,  411,  425,  509,  431,
};
final static int YYTABLESIZE=928;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         53,
    8,   49,   53,  151,  152,   53,   79,   45,    2,   84,
  103,   80,   27,  104,    7,   78,  141,  159,   73,   73,
   74,   74,  137,   10,   23,   45,   46,    7,   54,   54,
   54,   54,   54,   54,   54,  102,   10,   25,    5,   26,
   49,    8,   46,   45,    8,   24,   54,   54,   54,   54,
  130,   73,   73,   74,   74,   83,   83,   83,  106,   83,
  226,   83,   73,   49,   74,   34,   79,   79,   79,  114,
   79,   57,   79,   83,   83,   35,   83,   80,   80,   80,
   40,   80,   41,   80,   79,   79,   57,   79,   70,   45,
   70,  107,   70,   99,  104,   80,   80,   77,   80,   73,
   49,   73,   87,   73,   89,   44,   70,   70,   88,   70,
   54,   74,   73,   74,   74,   74,   56,   73,   73,   54,
   73,   54,   75,   44,   75,    8,   75,   57,  133,   74,
   74,   56,   74,   56,   54,   91,   73,   83,   74,   95,
   75,   75,    1,   75,   57,   71,   93,   71,   79,   71,
   23,  113,   63,   72,  122,   71,   68,   64,   68,   80,
   68,   96,  155,   71,   71,  156,   71,   72,  105,   72,
   70,   72,  153,  103,   68,   68,  103,   68,  166,  167,
   69,   73,   69,   97,   69,   72,   72,   44,   72,   65,
  127,   64,   65,   74,   64,  129,  133,  104,   69,   69,
  104,   69,   84,   84,   75,   84,  138,   84,   83,   83,
  170,   83,  146,   83,   48,   25,    5,   51,   25,    5,
   58,   25,    5,   26,   25,    5,   26,   71,   25,    5,
   26,  100,  100,  150,   52,  149,  136,   52,   68,   44,
   52,  157,   83,   54,   54,   54,   54,  140,  158,   72,
   54,   54,   54,   54,  142,   44,    8,    8,  102,   54,
    8,  102,   69,  100,  100,  100,  100,    8,    8,    8,
   83,   83,   83,    8,  161,  100,    8,   83,   83,   83,
   83,   79,   79,   79,   57,  174,   83,  143,   79,   79,
   79,   79,   80,   80,   80,  181,  182,   79,  154,   80,
   80,   80,   80,   70,   70,   70,  183,  184,   80,  160,
   70,   70,   70,   70,   73,   73,   73,  162,   65,   70,
  173,   73,   73,   73,   73,  100,   74,   74,   74,   56,
   73,  176,   54,   74,   74,   74,   74,   75,   75,   75,
   57,  101,   74,  177,   75,   75,   75,   75,  178,   66,
    5,   26,  186,   75,   22,  189,   67,   68,   69,   70,
   71,   71,   71,   36,  108,    5,   26,   71,   71,   71,
   71,   68,   68,   68,  187,  188,   71,  191,   68,   68,
   68,   68,   72,   72,   72,  194,   82,   68,  192,   72,
   72,   72,   72,  197,  198,   69,   69,   69,   72,   25,
    5,   26,   69,   69,   69,   69,   58,   23,   54,   63,
   61,   69,   62,  202,   64,  209,   84,  203,   59,  212,
   55,   58,   83,   58,  199,  200,   25,    5,   26,  121,
  214,  218,   84,   59,  221,   59,  131,  222,   83,   30,
  169,  171,  172,   37,    5,   26,    7,  116,    5,   26,
  132,  223,   43,   47,   39,   10,   55,   59,  225,  227,
   30,  190,   30,  228,   39,  145,  229,   11,   30,    4,
    5,  109,  110,    6,  148,   47,  115,    5,   39,   92,
    7,   30,    8,  117,  119,   30,    9,  111,  112,   10,
  118,    5,   26,    3,   30,   30,   30,   30,  165,   30,
  123,    5,   26,  124,  126,  179,   30,   30,   14,   30,
  115,   17,   30,   30,  180,   54,   54,   16,   28,   38,
   31,   75,   37,   30,   39,   38,  193,   85,   42,  175,
   30,  195,   60,   30,  144,   28,   28,   30,  201,   28,
   94,   28,    0,   50,   98,    0,    0,   28,   81,  206,
  208,  125,    5,   26,  211,    0,    0,    0,    0,    0,
   28,  217,    0,   39,   28,    0,   86,    0,  120,    0,
    0,  128,    0,   28,   28,   28,   28,    0,   28,    0,
    0,    0,   30,   30,   30,   28,   28,   39,   28,  139,
   39,   28,   28,    0,  147,    0,   54,    0,    0,  163,
    4,    5,    0,   30,    6,    0,    0,   39,    0,   28,
    0,    7,   28,    8,    0,    0,   28,    9,    0,   58,
   10,  164,   39,  108,    5,   26,  168,    0,    0,    0,
    0,   59,    0,    0,    0,   39,    0,   39,    4,    5,
    0,    0,    6,   39,  185,   30,    0,    0,   39,    7,
   39,    8,  196,   39,    0,    9,   37,    5,   10,   39,
    6,   28,   28,   28,    0,    0,  205,    7,    0,    8,
    0,    0,    0,    9,   37,    5,   10,    0,    6,    4,
    5,  207,   28,    6,    0,    7,    0,    8,    0,  210,
    7,    9,    8,    0,   10,    0,    9,   37,    5,   10,
    0,    6,   37,    5,  220,    0,    6,  213,    7,    0,
    8,    0,  215,    7,    9,    8,    0,   10,    0,    9,
    0,    0,   10,    0,   28,   37,    5,    0,    0,    6,
   37,    5,    0,  216,    6,    0,    7,    0,    8,    0,
    0,    7,    9,    8,    0,   10,    0,    9,  219,    0,
   10,    0,    4,    5,    0,    0,    6,   37,    5,    0,
    0,    6,  224,    7,    0,    8,    0,    0,    7,    9,
    8,    0,   10,    0,    9,   37,    5,   10,    0,    6,
    0,    0,    0,   37,    5,    0,    7,    6,    8,    0,
    0,    0,    9,    0,    7,   10,    8,   37,    5,    0,
    9,    6,   23,   10,   63,   61,    0,   62,    7,   64,
    8,    0,    4,    5,    9,    0,    6,   10,    0,    0,
   37,    5,    0,    7,    6,    8,    0,    0,    0,    9,
    0,    7,   10,    8,    0,    0,    0,    9,   37,    5,
   10,    0,    6,   37,    5,    0,    0,    6,    0,    7,
    0,    8,    0,    0,    7,    9,    8,    0,   10,    0,
    9,    0,    0,   10,    4,    5,    0,    0,    6,    0,
    0,    0,    0,    0,    0,    7,    0,    8,    0,   37,
    5,    9,    0,    6,   10,    0,    0,    0,    0,    0,
    7,    0,    8,   37,    5,    0,    9,    6,    0,   10,
    0,    0,    0,    0,    7,    0,    8,   37,    5,    0,
    9,    6,    0,   10,    0,    0,    0,    0,    7,  204,
    8,    0,    0,    0,    9,    0,    0,   10,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         40,
    0,   46,   40,   40,  257,   40,   42,   41,  123,   41,
   41,   47,   40,   44,  267,   41,   41,   41,   43,   43,
   45,   45,   44,  276,   40,   59,   44,  267,   41,   42,
   43,   44,   45,   46,   47,   41,  276,  256,  257,  258,
   46,   41,   44,   61,   44,   40,   59,   60,   61,   62,
   41,   43,   43,   45,   45,   41,   42,   43,   41,   45,
   41,   47,   43,   46,   45,  123,   41,   42,   43,   41,
   45,   44,   47,   59,   60,   40,   62,   41,   42,   43,
   59,   45,  257,   47,   59,   60,   59,   62,   41,  123,
   43,   41,   45,   53,   44,   59,   60,  123,   62,   41,
   46,   43,   59,   45,  258,   41,   59,   60,   40,   62,
  123,   41,   43,   43,   45,   45,   44,   59,   60,   44,
   62,   46,   41,   59,   43,  125,   45,   44,   88,   59,
   60,   59,   62,   61,   59,  257,   43,  123,   45,  257,
   59,   60,  257,   62,   61,   41,   46,   43,  123,   45,
   40,  123,   42,   60,  123,   62,   41,   47,   43,  123,
   45,   41,   41,   59,   60,   44,   62,   41,   41,   43,
  123,   45,  132,   41,   59,   60,   44,   62,  261,  262,
   41,  123,   43,   40,   45,   59,   60,  123,   62,   41,
  270,   41,   44,  123,   44,   41,  156,   41,   59,   60,
   44,   62,   42,   43,  123,   45,  257,   47,   42,   43,
   40,   45,  123,   47,  259,  256,  257,  258,  256,  257,
  258,  256,  257,  258,  256,  257,  258,  123,  256,  257,
  258,  256,  256,  270,  275,   40,  258,  275,  123,  257,
  275,  258,  274,  256,  257,  258,  259,  272,  272,  123,
  263,  264,  265,  266,  257,  257,  256,  257,   41,  272,
  260,   44,  123,   41,  256,  256,   44,  267,  268,  269,
  256,  257,  258,  273,  272,  256,  276,  263,  264,  265,
  266,  256,  257,  258,  257,  123,  272,  257,  263,  264,
  265,  266,  256,  257,  258,  261,  262,  272,  257,  263,
  264,  265,  266,  256,  257,  258,  261,  262,  272,  257,
  263,  264,  265,  266,  256,  257,  258,   41,   27,  272,
  257,  263,  264,  265,  266,  256,  256,  257,  258,  257,
  272,  257,  257,  263,  264,  265,  266,  256,  257,  258,
  257,  272,  272,  272,  263,  264,  265,  266,  257,  256,
  257,  258,  123,  272,    2,   41,  263,  264,  265,  266,
  256,  257,  258,   11,  256,  257,  258,  263,  264,  265,
  266,  256,  257,  258,  261,  262,  272,   41,  263,  264,
  265,  266,  256,  257,  258,  257,   34,  272,   41,  263,
  264,  265,  266,  123,  123,  256,  257,  258,  272,  256,
  257,  258,  263,  264,  265,  266,   44,   40,   46,   42,
   43,  272,   45,  123,   47,  123,  256,   41,   44,   40,
   46,   59,  256,   61,  261,  262,  256,  257,  258,   77,
  262,  262,  272,   59,  262,   61,  257,  262,  272,    6,
  149,  150,  151,  256,  257,  258,  267,  256,  257,  258,
  271,  262,   19,   20,   12,  276,   23,   24,  262,  262,
   27,  170,   29,   59,   22,  113,  125,  123,   35,  256,
  257,   61,   62,  260,  122,   42,   66,    0,   36,   46,
  267,   48,  269,   73,   74,   52,  273,   63,   64,  276,
  256,  257,  258,    0,   61,   62,   63,   64,  146,   66,
  256,  257,  258,   79,   80,  123,   73,   74,   59,   76,
  100,   59,   79,   80,  162,   23,   24,   59,    6,   59,
   59,   29,   59,   59,   82,  125,  174,   35,   19,  156,
   97,  179,   24,  100,  104,   23,   24,  104,  186,   27,
   48,   29,   -1,  125,   52,   -1,   -1,   35,  125,  197,
  198,  256,  257,  258,  202,   -1,   -1,   -1,   -1,   -1,
   48,  209,   -1,  121,   52,   -1,  125,   -1,   76,   -1,
   -1,  125,   -1,   61,   62,   63,   64,   -1,   66,   -1,
   -1,   -1,  149,  150,  151,   73,   74,  145,   76,   97,
  148,   79,   80,   -1,  125,   -1,  104,   -1,   -1,  125,
  256,  257,   -1,  170,  260,   -1,   -1,  165,   -1,   97,
   -1,  267,  100,  269,   -1,   -1,  104,  273,   -1,  257,
  276,  125,  180,  256,  257,  258,  125,   -1,   -1,   -1,
   -1,  257,   -1,   -1,   -1,  193,   -1,  195,  256,  257,
   -1,   -1,  260,  201,  125,  212,   -1,   -1,  206,  267,
  208,  269,  125,  211,   -1,  273,  256,  257,  276,  217,
  260,  149,  150,  151,   -1,   -1,  125,  267,   -1,  269,
   -1,   -1,   -1,  273,  256,  257,  276,   -1,  260,  256,
  257,  125,  170,  260,   -1,  267,   -1,  269,   -1,  125,
  267,  273,  269,   -1,  276,   -1,  273,  256,  257,  276,
   -1,  260,  256,  257,  212,   -1,  260,  125,  267,   -1,
  269,   -1,  125,  267,  273,  269,   -1,  276,   -1,  273,
   -1,   -1,  276,   -1,  212,  256,  257,   -1,   -1,  260,
  256,  257,   -1,  125,  260,   -1,  267,   -1,  269,   -1,
   -1,  267,  273,  269,   -1,  276,   -1,  273,  125,   -1,
  276,   -1,  256,  257,   -1,   -1,  260,  256,  257,   -1,
   -1,  260,  125,  267,   -1,  269,   -1,   -1,  267,  273,
  269,   -1,  276,   -1,  273,  256,  257,  276,   -1,  260,
   -1,   -1,   -1,  256,  257,   -1,  267,  260,  269,   -1,
   -1,   -1,  273,   -1,  267,  276,  269,  256,  257,   -1,
  273,  260,   40,  276,   42,   43,   -1,   45,  267,   47,
  269,   -1,  256,  257,  273,   -1,  260,  276,   -1,   -1,
  256,  257,   -1,  267,  260,  269,   -1,   -1,   -1,  273,
   -1,  267,  276,  269,   -1,   -1,   -1,  273,  256,  257,
  276,   -1,  260,  256,  257,   -1,   -1,  260,   -1,  267,
   -1,  269,   -1,   -1,  267,  273,  269,   -1,  276,   -1,
  273,   -1,   -1,  276,  256,  257,   -1,   -1,  260,   -1,
   -1,   -1,   -1,   -1,   -1,  267,   -1,  269,   -1,  256,
  257,  273,   -1,  260,  276,   -1,   -1,   -1,   -1,   -1,
  267,   -1,  269,  256,  257,   -1,  273,  260,   -1,  276,
   -1,   -1,   -1,   -1,  267,   -1,  269,  256,  257,   -1,
  273,  260,   -1,  276,   -1,   -1,   -1,   -1,  267,  268,
  269,   -1,   -1,   -1,  273,   -1,   -1,  276,
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

//#line 166 "gramatica.y"

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
//#line 22 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta punto y coma al final de la sentencia");}
break;
case 12:
//#line 28 "gramatica.y"
{System.out.println("Se detectó un print");}
break;
case 14:
//#line 32 "gramatica.y"
{System.out.println("Se detectó una declaración de variables");}
break;
case 15:
//#line 33 "gramatica.y"
{System.out.println("Se detectó una declaración de función");}
break;
case 16:
//#line 36 "gramatica.y"
{System.out.println("Se detectó una asignación simple");}
break;
case 17:
//#line 37 "gramatica.y"
{System.out.println("Se detectó una asignación múltiple");}
break;
case 20:
//#line 42 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta coma entre constantes");}
break;
case 21:
//#line 45 "gramatica.y"
{System.out.println("Se detectó un bloque IF-ELSE");}
break;
case 22:
//#line 46 "gramatica.y"
{System.out.println("Se detectó un bloque IF");}
break;
case 23:
//#line 47 "gramatica.y"
{System.out.println("Se detectó un bloque DO UNTIL");}
break;
case 24:
//#line 48 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta del parentesis de cierre de la condicion del bloque IF");}
break;
case 25:
//#line 49 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta del parentesis de apertura de la condición del bloque IF");}
break;
case 26:
//#line 50 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta de parentesis de la condicion del bloque IF");}
break;
case 27:
//#line 51 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta del parentesis de cierre de la condicion del bloque IF");}
break;
case 28:
//#line 52 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta del parentesis de apertura de la condición del bloque IF");}
break;
case 29:
//#line 53 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta de parentesis de la condicion del bloque IF");}
break;
case 30:
//#line 54 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta la palabra ENDIF al final del bloque IF");}
break;
case 31:
//#line 55 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta la palabra ENDIF al final del bloque IF");}
break;
case 32:
//#line 56 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el cuerpo en el bloque ELSE");}
break;
case 33:
//#line 57 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el cuerpo en el bloque IF");}
break;
case 34:
//#line 58 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el cuerpo en el bloque IF");}
break;
case 35:
//#line 59 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el cuerpo en los bloques IF y ELSE");}
break;
case 36:
//#line 60 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el parentesis de apertura de la condición del bloque DO UNTIL");}
break;
case 37:
//#line 61 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el parentesis de apertura de la condición del bloque DO UNTIL");}
break;
case 38:
//#line 62 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta de parentesis en la condición del bloque DO UNTIL");}
break;
case 39:
//#line 63 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta de cuerpo en iteracion");}
break;
case 40:
//#line 64 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta la palabra UNTIL en la iteracion");}
break;
case 43:
//#line 69 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta de argumento en sentencia print");}
break;
case 45:
//#line 73 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operador comparador en la condición");}
break;
case 58:
//#line 94 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta coma entre variables");}
break;
case 59:
//#line 95 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta coma entre variables");}
break;
case 64:
//#line 104 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el nombre del parámetro");}
break;
case 65:
//#line 105 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el nombre del parámetro");}
break;
case 66:
//#line 106 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el tipo del parámetro");}
break;
case 67:
//#line 107 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el tipo del parámetro");}
break;
case 71:
//#line 113 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando derecho de la suma");}
break;
case 72:
//#line 114 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando derecho de la resta");}
break;
case 73:
//#line 115 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando izquierdo de la suma");}
break;
case 74:
//#line 116 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando izquierdo de la resta");}
break;
case 75:
//#line 119 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta de operador");}
break;
case 79:
//#line 125 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando derecho de la multiplicación");}
break;
case 80:
//#line 126 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando derecho de la división");}
break;
case 81:
//#line 127 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando izquierdo de la multiplicación");}
break;
case 82:
//#line 128 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando izquierdo de la división");}
break;
case 86:
//#line 139 "gramatica.y"
{System.out.println("Se detectó una función invocada correctamente");}
break;
case 87:
//#line 140 "gramatica.y"
{System.out.println("Se detectó una función invocada correctamente");}
break;
case 88:
//#line 141 "gramatica.y"
{System.out.println("Se detectó una función invocada correctamente");}
break;
case 89:
//#line 142 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el nombre de la funcion");}
break;
case 90:
//#line 143 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el nombre de la funcion");}
break;
case 91:
//#line 144 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el nombre de la funcion");}
break;
case 96:
//#line 153 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el paréntesis de cierre de la función TRUNC");}
break;
case 97:
//#line 154 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el paréntesis de apertura de la función TRUNC");}
break;
case 98:
//#line 155 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": faltan ambos paréntesis de la función TRUNC");}
break;
case 100:
//#line 157 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta llave de cierre del cuerpo de la función Lambda");}
break;
case 101:
//#line 158 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta llave de apertura del cuerpo de la función Lambda");}
break;
case 102:
//#line 159 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": faltan ambas llaves del cuerpo de la función Lambda");}
break;
case 103:
//#line 160 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": No se especifica el parametro formal al que corresponde el parámetro real");}
break;
case 104:
//#line 161 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": No se especifica el parametro formal al que corresponde el parámetro real");}
break;
//#line 968 "Parser.java"
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
