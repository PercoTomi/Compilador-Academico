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
    5,    5,    5,    6,    6,    8,    8,   17,   17,   17,
   18,   18,    9,    9,    9,    9,    9,    9,    9,    9,
    9,    9,    9,   10,   10,   10,   19,   19,   19,   19,
   20,   20,   21,   21,   21,   21,   21,   21,   12,   12,
   16,   16,   13,   13,   13,   13,   14,   14,   22,   22,
   22,   22,   22,   22,   15,   15,   15,   15,   15,   15,
   15,   15,   23,   23,   23,   23,   23,   23,   23,   24,
   24,   24,   11,   11,   11,   11,   11,   11,   25,   25,
   26,   26,   26,   26,   26,   26,   26,   26,   26,   26,
   26,
};
final static short yylen[] = {                            2,
    5,    4,    4,    4,    3,    0,    7,    0,    6,    6,
    5,    2,    1,    3,    2,    2,    2,    3,    2,    1,
    1,    1,    1,    2,   13,    3,    3,    3,    1,    2,
    3,    2,    6,    4,    4,    5,    3,    5,    5,    3,
    4,    3,    3,    4,    4,    3,    3,    2,    2,    1,
    3,    2,    1,    1,    1,    1,    1,    1,    1,    1,
    1,    3,    3,    1,    2,    4,    3,    1,    3,    2,
    2,    1,    2,    1,    3,    3,    1,    3,    3,    3,
    3,    3,    3,    3,    1,    3,    3,    3,    3,    1,
    1,    1,    4,    4,    4,    4,    4,    4,    3,    1,
    3,    6,    5,    5,    4,    7,    6,    6,    5,    2,
    5,
};
final static short yydefred[] = {                         0,
    0,    0,    0,    0,    0,    0,   59,    0,    0,   60,
    0,    0,   13,    0,    0,   20,   21,   22,   23,    0,
    0,    0,    0,   15,    0,    0,    0,   91,    0,   92,
    0,    0,    0,    0,    0,   85,    0,    0,    0,    0,
    0,    0,    0,    5,    0,   12,   16,   17,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,  100,    0,    0,    0,    0,    0,
    0,    0,    0,    0,   55,   56,   57,   58,   53,   54,
    0,    0,    0,    0,    0,   40,    0,   48,    0,    0,
   42,    0,   32,    0,    0,    0,   43,    0,   46,    0,
    8,    0,    0,    3,    0,   14,    4,    0,   29,    0,
   61,    0,    0,    0,   62,    2,   98,    0,    0,    0,
    0,    0,   97,   96,    0,   95,   94,   93,    0,    0,
    0,   88,   89,   47,    0,    0,    0,    0,    0,    0,
   41,    0,    0,   34,    0,   83,    0,   84,   19,   31,
    0,   35,   45,   44,    0,   11,    6,    0,    1,   74,
    0,    0,    0,   68,   30,    0,    0,    0,    0,    0,
    0,  101,   99,   39,   38,    0,   18,    9,    0,   10,
   73,    0,   70,    0,    0,   28,    0,    0,  105,    0,
    0,   33,    7,   69,    0,   67,  103,    0,  104,    0,
    0,    0,  102,    0,  108,    0,  106,    0,    0,    0,
    0,   25,
};
final static short yydgoto[] = {                          3,
   12,  179,  155,   13,   14,   15,   95,   16,   17,   18,
   30,   20,   21,  163,   31,   32,  110,   39,   33,   34,
   84,  164,   35,   36,   64,   65,
};
final static short yysindex[] = {                      -110,
  279,  445,    0,  -18,    2,    4,    0, -106,    8,    0,
  450,  287,    0,  -32,   -9,    0,    0,    0,    0, -211,
   50,  -15,  336,    0,  -36,  -33,  462,    0,  266,    0,
   47,   25,  -94,   34,   22,    0,    4,  293,  -24,  -27,
  -40,  318,  -16,    0, -214,    0,    0,    0,   51,   24,
   25,    0, -169, -123,  115,  266,  -88,  -77,  169,  159,
 -258,  -37,   26,   91,    0,  174,   56,  154,  284,  284,
  301,  301,  228,  106,    0,    0,    0,    0,    0,    0,
  324,  332,   82,  266, -118,    0, -139,    0,  350,  359,
    0,  234,    0,  225,  393,    4,    0,  247,    0,  -20,
    0,   19,  -39,    0,   41,    0,    0,  -64,    0,  -42,
    0,   25,   68,   82,    0,    0,    0,  266,  -13,   69,
  284,   74,    0,    0,  -30,    0,    0,    0,  330,   22,
   22,    0,    0,    0,   22,  330,   22,  330,   22,   82,
    0,   80, -112,    0,  234,    0,  234,    0,    0,    0,
  291,    0,    0,    0,  244,    0,    0,   94,    0,    0,
  178,  139,  173,    0,    0,  152,    0,   17,  140,  147,
  379,    0,    0,    0,    0,  162,    0,    0,  319,    0,
    0,  198,    0,  303,  -64,    0,  203,  193,    0,  217,
  341,    0,    0,    0,  445,    0,    0,  221,    0,  190,
  407,  418,    0,  440,    0,  391,    0,  266,   39,  426,
  370,    0,
};
final static short yyrindex[] = {                         0,
    0,    0,    0,    0,   -8,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  131,    0,    0,    0,    0,    0,    0,    0,    0,
    0,   14,    0,  306,   71,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,  127,  434,
   97,   60,    0,    0,    0,    0,    0,    0,  166,    0,
    0,    0,  240,    0,    0,  166,  240,    0,    0,    0,
    0,    0,  309,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    6,    0,    0,    0,  438,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,  449,
    0,   49,    0,  455,    0,    0,    0,    0,    0,    0,
    0,  222,    0,    0,    0,    0,    0,    0,    0,   83,
   95,    0,    0,    0,  117,  129,  142,  368,  380,   29,
    0,    0,    0,    0,   36,    0,   58,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  412,    0,    0,    0,    0,  119,    0,    0,    0,
    0,    0,    0,    0,    0,  460,    0,    0,    0,    0,
    0,  425,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  429,    0,    0,
  439,    0,    0,  472,    0,    0,    0,    0,    0,    0,
    0,    0,
};
final static short yygindex[] = {                         0,
    1,    0,    0,    3,  271,    0, -151,    0,    0,    0,
  488,    5,  464,    0,  461,  456,    0,   54,  145,  486,
    0,  335,  367,  362,  499,  402,
};
final static int YYTABLESIZE=726;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         25,
   25,  166,   23,   61,   38,   81,   61,   82,    7,   61,
   38,   42,    2,   99,   46,   29,   38,   10,   24,  106,
  154,   25,   81,   25,   82,   46,   47,  170,   38,   81,
   57,   82,   61,   61,   61,   61,   61,   61,   61,  201,
   24,   26,  106,   29,   46,   49,   52,   40,  204,   48,
   61,   61,   61,   61,   90,   90,   90,  188,   90,   81,
   90,   82,  107,   89,   52,  120,  123,   54,   90,   51,
   57,   57,   90,   90,   88,   90,   86,   86,   86,  210,
   86,   81,   86,   82,  102,  158,   87,   51,  109,   81,
  108,   82,   63,   54,   86,   86,  127,   86,   87,   87,
   87,   57,   87,   65,   87,   61,   80,   63,   79,   63,
   53,   77,  162,   77,   61,   77,   87,   87,   65,   87,
   65,  143,  144,   80,   81,   80,   82,   80,   52,   77,
   77,  124,   77,  111,  125,   81,   90,   81,  142,   81,
   64,   80,   80,  141,   80,   25,    1,   71,   69,  175,
   70,   51,   72,   81,   81,   64,   81,   82,   86,   82,
  113,   82,   66,   37,   62,  182,   85,   86,  115,   78,
   61,   78,   61,   78,   64,   82,   82,   66,   82,   66,
   87,   91,   75,   97,   75,   61,   75,   78,   78,  162,
   78,   64,  160,   77,  128,  202,  176,  125,  118,  116,
   75,   75,    7,   75,   46,   80,  161,   91,   91,  117,
   91,   10,   91,  184,  126,  165,  185,   81,  121,   27,
    5,   59,   27,    5,   66,   27,    5,   28,   27,    5,
   28,   27,    5,   28,  122,  121,  101,  157,   60,   82,
  152,   60,  121,   56,   60,   96,   98,   61,   61,   61,
   61,   78,   61,   61,   61,   61,   61,   61,  169,   27,
    5,   28,  110,   61,   75,  110,   52,   52,  134,   90,
   90,   90,  121,   25,   90,   90,   90,   90,   90,   90,
   52,   90,   90,  149,   90,   90,   90,  153,  187,   51,
   51,   86,   86,   86,  121,  156,   86,   86,   86,   86,
   86,   86,   74,    5,   28,   63,   52,   86,   94,   75,
   76,   77,   78,   87,   87,   87,   65,  159,   87,   87,
   87,   87,   87,   87,  167,  171,   77,   77,   77,   87,
  172,   77,   77,   77,   77,   77,   77,  121,   80,   80,
   80,  174,   77,   80,   80,   80,   80,   80,   80,  177,
   81,   81,   81,   64,   80,   81,   81,   81,   81,   81,
   81,  129,    5,   28,   50,  151,   81,   49,  178,   25,
  180,   71,   82,   82,   82,   66,   72,   82,   82,   82,
   82,   82,   82,   61,   78,   78,   78,   64,   82,   78,
   78,   78,   78,   78,   78,  183,  189,   75,   75,   75,
   78,   11,   75,   75,   75,   75,   75,   75,   79,  186,
   79,   45,   79,   75,   27,    5,   28,   93,  190,  191,
   76,   91,   76,  192,   76,  195,   79,   79,   50,   79,
  208,   49,  132,  133,  181,  130,  131,   91,   76,   76,
  135,   76,  105,  193,    7,   92,    5,  137,  139,    6,
  146,  148,   72,   10,  194,   72,   22,   22,    8,  197,
   58,   94,    9,  200,  198,   71,   22,   22,   71,  111,
   94,  151,  111,  199,  151,   51,   55,  203,   22,  109,
   63,   67,  109,   50,  211,   62,   62,  135,   19,   19,
   79,   83,   24,   22,  212,   90,   37,   22,   19,   19,
  100,   25,   76,   71,   69,   55,   70,   27,   72,  112,
   19,   90,  107,   26,   73,  107,  114,  150,   36,  196,
  119,   27,    5,   28,   68,   19,  173,    0,    0,   19,
    0,  205,    0,    0,    4,    5,    0,    0,    6,  129,
    5,   28,   43,    5,  140,    7,    6,    8,   92,    5,
   22,    9,    6,    7,   10,    8,   92,    5,   28,    9,
    0,    8,   10,   44,  207,    9,   50,   50,    0,   49,
   49,    0,    0,  103,    5,    0,    0,    6,  168,  136,
    5,   28,   19,    0,    7,   62,    8,  138,    5,   28,
    9,   43,    5,   10,  104,    6,   92,    5,    0,    0,
    6,    0,    7,    0,    8,  145,    5,   28,    9,    8,
    0,   10,    0,    9,  147,    5,   28,    0,    0,    0,
    0,    0,    0,   79,   79,   79,    0,    0,   79,   79,
   79,   79,   79,   79,    0,   76,   76,   76,    0,   79,
   76,   76,   76,   76,   76,   76,   22,    0,   92,    5,
   22,   76,    6,    0,    0,   22,   22,   22,    0,   22,
    0,    8,   92,    5,    0,    9,    6,    0,  209,    0,
    0,    0,    0,   43,    5,    8,    0,    6,   19,    9,
    0,    0,   19,    0,    7,  206,    8,   19,   19,   19,
    9,   19,    0,   10,    0,   92,    5,    0,    0,    6,
    4,    5,    0,    0,    6,   41,    5,    0,    8,    6,
    0,    7,    9,    8,    0,    0,    7,    9,    8,    0,
   10,    0,    9,    0,    0,   10,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         40,
   40,   44,    2,   40,  123,   43,   40,   45,  267,   40,
  123,   11,  123,   41,   12,   40,  123,  276,   59,   59,
   41,   40,   43,   40,   45,   23,   59,   41,  123,   43,
   46,   45,   41,   42,   43,   44,   45,   46,   47,  191,
   59,   40,   59,   40,   42,  257,   41,   40,  200,   59,
   59,   60,   61,   62,   41,   42,   43,   41,   45,   43,
   47,   45,  277,   42,   59,   61,   41,   44,   47,   41,
   46,   46,   59,   60,   41,   62,   41,   42,   43,   41,
   45,   43,   47,   45,  125,  125,   33,   59,  258,   43,
   40,   45,   44,   44,   59,   60,   41,   62,   41,   42,
   43,   46,   45,   44,   47,   46,   60,   59,   62,   61,
   61,   41,  108,   43,  123,   45,   59,   60,   59,   62,
   61,  261,  262,   41,   43,   43,   45,   45,  123,   59,
   60,   41,   62,  257,   44,   41,  123,   43,   85,   45,
   44,   59,   60,  262,   62,   40,  257,   42,   43,  262,
   45,  123,   47,   59,   60,   59,   62,   41,  123,   43,
   46,   45,   44,  270,   46,  161,  261,  262,  257,   41,
   44,   43,   46,   45,   44,   59,   60,   59,   62,   61,
  123,   37,   41,   39,   43,   59,   45,   59,   60,  185,
   62,   61,  257,  123,   41,  195,  143,   44,   40,  277,
   59,   60,  267,   62,  202,  123,  271,   42,   43,   41,
   45,  276,   47,   41,   41,  258,   44,  123,  256,  256,
  257,  258,  256,  257,  258,  256,  257,  258,  256,  257,
  258,  256,  257,  258,  272,  256,  277,  277,  275,  123,
   96,  275,  256,  259,  275,  270,  274,  256,  257,  258,
  259,  123,  261,  262,  263,  264,  265,  266,  272,  256,
  257,  258,   41,  272,  123,   44,  261,  262,   41,  256,
  257,  258,  256,   40,  261,  262,  263,  264,  265,  266,
  257,   42,   43,   59,   45,  272,   47,   41,  272,  261,
  262,  256,  257,  258,  256,  277,  261,  262,  263,  264,
  265,  266,  256,  257,  258,  257,  257,  272,   38,  263,
  264,  265,  266,  256,  257,  258,  257,  277,  261,  262,
  263,  264,  265,  266,  257,  257,  256,  257,  258,  272,
  257,  261,  262,  263,  264,  265,  266,  256,  256,  257,
  258,  262,  272,  261,  262,  263,  264,  265,  266,   59,
  256,  257,  258,  257,  272,  261,  262,  263,  264,  265,
  266,  256,  257,  258,   59,   95,  272,   59,  125,   40,
  277,   42,  256,  257,  258,  257,   47,  261,  262,  263,
  264,  265,  266,  257,  256,  257,  258,  257,  272,  261,
  262,  263,  264,  265,  266,  257,  257,  256,  257,  258,
  272,  123,  261,  262,  263,  264,  265,  266,   41,  258,
   43,  125,   45,  272,  256,  257,  258,  125,  272,   41,
   41,  256,   43,  262,   45,  123,   59,   60,  123,   62,
   40,  123,   71,   72,  257,   69,   70,  272,   59,   60,
   74,   62,  125,  125,  267,  256,  257,   81,   82,  260,
   89,   90,   41,  276,  257,   44,    1,    2,  269,  257,
  125,  191,  273,  123,  272,   41,   11,   12,   44,   41,
  200,  201,   44,  257,  204,   20,   21,  257,   23,   41,
   25,   26,   44,   20,   59,   25,   26,  121,    1,    2,
  123,   31,   59,   38,  125,  256,   59,   42,   11,   12,
   40,   40,  123,   42,   43,   50,   45,   59,   47,   54,
   23,  272,   41,   59,   29,   44,   56,  125,   59,  185,
   60,  256,  257,  258,   26,   38,  125,   -1,   -1,   42,
   -1,  125,   -1,   -1,  256,  257,   -1,   -1,  260,  256,
  257,  258,  256,  257,   84,  267,  260,  269,  256,  257,
   95,  273,  260,  267,  276,  269,  256,  257,  258,  273,
   -1,  269,  276,  277,  125,  273,  261,  262,   -1,  261,
  262,   -1,   -1,  256,  257,   -1,   -1,  260,  118,  256,
  257,  258,   95,   -1,  267,  125,  269,  256,  257,  258,
  273,  256,  257,  276,  277,  260,  256,  257,   -1,   -1,
  260,   -1,  267,   -1,  269,  256,  257,  258,  273,  269,
   -1,  276,   -1,  273,  256,  257,  258,   -1,   -1,   -1,
   -1,   -1,   -1,  256,  257,  258,   -1,   -1,  261,  262,
  263,  264,  265,  266,   -1,  256,  257,  258,   -1,  272,
  261,  262,  263,  264,  265,  266,  191,   -1,  256,  257,
  195,  272,  260,   -1,   -1,  200,  201,  202,   -1,  204,
   -1,  269,  256,  257,   -1,  273,  260,   -1,  208,   -1,
   -1,   -1,   -1,  256,  257,  269,   -1,  260,  191,  273,
   -1,   -1,  195,   -1,  267,  268,  269,  200,  201,  202,
  273,  204,   -1,  276,   -1,  256,  257,   -1,   -1,  260,
  256,  257,   -1,   -1,  260,  256,  257,   -1,  269,  260,
   -1,  267,  273,  269,   -1,   -1,  267,  273,  269,   -1,
  276,   -1,  273,   -1,   -1,  276,
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

//#line 175 "gramatica.y"

private void yyerror(String s) {
    System.out.println("Error: " + s);
}

public Parser(Map<String, Simbolo> tablaSimbolos, String path)
{
  this.tablaSimbolos = tablaSimbolos;
  this.lexer = new Lexico(tablaSimbolos);
  this.lexer.cargarBuffer(path);
  this.yydebug = true;
}
//#line 533 "Parser.java"
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
//#line 39 "gramatica.y"
{System.out.println("Se obtuvo una sentencia desde una invocación a función");}
break;
case 24:
//#line 42 "gramatica.y"
{System.out.println("Se detectó una declaración de variables");}
break;
case 25:
//#line 43 "gramatica.y"
{System.out.println("Se detectó una declaración de función");}
break;
case 26:
//#line 46 "gramatica.y"
{System.out.println("Se detectó una asignación simple");}
break;
case 27:
//#line 48 "gramatica.y"
{System.out.println("Se detectó una asignación múltiple");}
break;
case 30:
//#line 53 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta coma entre constantes");}
break;
case 33:
//#line 60 "gramatica.y"
{System.out.println("Se detectó un bloque IF-ELSE");}
break;
case 34:
//#line 61 "gramatica.y"
{System.out.println("Se detectó un bloque IF");}
break;
case 35:
//#line 62 "gramatica.y"
{System.out.println("Se detectó un bloque DO UNTIL");}
break;
case 36:
//#line 64 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta la palabra ENDIF al final del bloque IF");}
break;
case 37:
//#line 65 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta la palabra ENDIF al final del bloque IF");}
break;
case 38:
//#line 66 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el cuerpo en el bloque ELSE");}
break;
case 39:
//#line 67 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el cuerpo en el bloque IF");}
break;
case 40:
//#line 68 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el cuerpo en el bloque IF");}
break;
case 41:
//#line 69 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el cuerpo en los bloques IF y ELSE");}
break;
case 42:
//#line 71 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta de cuerpo en iteracion");}
break;
case 43:
//#line 72 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta la palabra UNTIL en la iteracion");}
break;
case 46:
//#line 77 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta de argumento en sentencia print");}
break;
case 47:
//#line 80 "gramatica.y"
{System.out.println("Se detectó una condición entre paréntesis");}
break;
case 48:
//#line 81 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el paréntesis de apertura de la condición");}
break;
case 49:
//#line 82 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el paréntesis de cierre de la condición");}
break;
case 50:
//#line 83 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": faltan ambos paréntesis de la condición");}
break;
case 52:
//#line 87 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operador comparador en la condición");}
break;
case 61:
//#line 102 "gramatica.y"
{System.out.println("Se detectó un IDENTIFICADOR");}
break;
case 62:
//#line 103 "gramatica.y"
{System.out.println("Se detectó un IDENTIFICADOR compuesto");}
break;
case 65:
//#line 108 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta coma entre variables");}
break;
case 66:
//#line 109 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta coma entre variables");}
break;
case 71:
//#line 118 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el nombre del parámetro");}
break;
case 72:
//#line 119 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el nombre del parámetro");}
break;
case 73:
//#line 120 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el tipo del parámetro");}
break;
case 74:
//#line 121 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el tipo del parámetro");}
break;
case 75:
//#line 124 "gramatica.y"
{System.out.println("Se detectó una expresión desde una suma");}
break;
case 76:
//#line 125 "gramatica.y"
{System.out.println("Se detectó una expresión desde una resta");}
break;
case 77:
//#line 126 "gramatica.y"
{System.out.println("Se detectó una expresión desde un término");}
break;
case 78:
//#line 127 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando derecho de la suma");}
break;
case 79:
//#line 128 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando derecho de la resta");}
break;
case 80:
//#line 129 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando izquierdo de la suma");}
break;
case 81:
//#line 130 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando izquierdo de la resta");}
break;
case 82:
//#line 131 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta de operador");}
break;
case 85:
//#line 136 "gramatica.y"
{System.out.println("Se detecto un TERMINO desde un FACTOR");}
break;
case 86:
//#line 137 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando derecho de la multiplicación");}
break;
case 87:
//#line 138 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando derecho de la división");}
break;
case 88:
//#line 139 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando izquierdo de la multiplicación");}
break;
case 89:
//#line 140 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando izquierdo de la división");}
break;
case 90:
//#line 143 "gramatica.y"
{System.out.println("Se detectó un FACTOR IDENTIFICADOR");}
break;
case 91:
//#line 144 "gramatica.y"
{System.out.println("Se detectó un FACTOR CTE");}
break;
case 92:
//#line 145 "gramatica.y"
{System.out.println("Se detectó un FACTOR FUNCIÓN");}
break;
case 93:
//#line 148 "gramatica.y"
{System.out.println("Se detectó una función invocada correctamente");}
break;
case 94:
//#line 149 "gramatica.y"
{System.out.println("Se detectó una función invocada correctamente");}
break;
case 95:
//#line 150 "gramatica.y"
{System.out.println("Se detectó una función invocada correctamente");}
break;
case 96:
//#line 151 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el nombre de la funcion");}
break;
case 97:
//#line 152 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el nombre de la funcion");}
break;
case 98:
//#line 153 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el nombre de la funcion");}
break;
case 103:
//#line 162 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el paréntesis de cierre de la función TRUNC");}
break;
case 104:
//#line 163 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el paréntesis de apertura de la función TRUNC");}
break;
case 105:
//#line 164 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": faltan ambos paréntesis de la función TRUNC");}
break;
case 107:
//#line 166 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta llave de cierre del cuerpo de la función Lambda");}
break;
case 108:
//#line 167 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta llave de apertura del cuerpo de la función Lambda");}
break;
case 109:
//#line 168 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": faltan ambas llaves del cuerpo de la función Lambda");}
break;
case 110:
//#line 169 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": No se especifica el parametro formal al que corresponde el parámetro real");}
break;
case 111:
//#line 170 "gramatica.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": No se especifica el parametro formal al que corresponde el parámetro real");}
break;
//#line 986 "Parser.java"
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
