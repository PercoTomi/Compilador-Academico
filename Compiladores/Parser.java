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
  System.out.println("=====================");
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
    0,    0,    0,    0,    0,    0,    0,    0,    0,    1,
    1,    2,    2,    2,    2,    5,    5,    5,    5,    3,
    3,    3,    3,    4,    4,    6,    6,   16,   16,   16,
    7,    7,    7,    7,    7,    7,    7,    7,    7,    7,
    7,    9,    9,    9,   17,   17,   17,   17,   18,   18,
   19,   19,   19,   19,   19,   19,   10,   10,   11,   11,
   11,   14,   14,    8,   20,   20,   20,   20,   21,   21,
   22,   22,   22,   22,   22,   22,   22,   22,   22,   22,
   22,   22,   12,   12,   23,   23,   23,   23,   23,   23,
   13,   13,   13,   13,   13,   13,   13,   24,   24,   24,
   24,   24,   24,   24,   24,   25,   25,   25,   25,   15,
   15,   15,   15,   15,   15,
};
final static short yylen[] = {                            2,
    5,    4,    4,    4,    3,    3,    3,    2,    1,    2,
    1,    2,    2,    1,    1,    3,    2,    2,    1,    1,
    1,    1,    1,    2,   13,    3,    3,    3,    2,    2,
    6,    4,    4,    5,    3,    5,    5,    3,    4,    3,
    3,    4,    4,    3,    3,    2,    2,    1,    3,    1,
    1,    1,    1,    1,    1,    1,    1,    1,    3,    1,
    3,    1,    3,    4,    1,    1,    1,    2,    3,    1,
    3,    6,    5,    5,    4,    9,    8,    8,    7,    8,
    2,    5,    3,    1,    3,    2,    2,    1,    2,    1,
    3,    3,    3,    3,    3,    3,    1,    3,    3,    3,
    3,    3,    3,    3,    1,    1,    2,    1,    1,    3,
    4,    1,    2,    2,    3,
};
final static short yydefred[] = {                         0,
    9,    0,    0,   57,    0,    0,   58,    0,    0,    0,
   11,    0,    0,   20,   21,   22,   23,    0,    0,    0,
    0,    0,    0,    0,    0,  106,    0,    0,  109,    0,
    0,    0,    0,    0,  105,    0,    0,    0,    0,    0,
    0,    8,    0,   10,   12,   13,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,   70,    5,    0,    0,    0,    0,    0,
    0,  107,   53,   54,   55,   56,   51,   52,    0,    0,
    0,    0,   38,    0,   46,    0,    0,    0,   40,   30,
    0,    0,   29,    0,   41,    0,   44,    0,    6,    0,
    7,    0,   62,    0,  112,    0,    0,    0,    0,   63,
    3,    0,    0,    0,    0,    0,    0,   64,    0,    4,
    0,    0,    0,  100,  102,   45,    0,    0,    0,    0,
    0,   39,    0,    0,   32,  104,  101,   98,  103,   99,
   17,   28,    0,   33,   43,   42,    2,   90,    0,    0,
    0,   84,  113,  114,    0,    0,    1,    0,    0,    0,
    0,   71,   69,   37,   36,    0,   16,   89,    0,   86,
    0,    0,  110,    0,  115,    0,    0,   75,    0,    0,
   31,   85,    0,   83,  111,   73,    0,   74,    0,    0,
    0,   72,    0,    0,    0,    0,    0,    0,   79,    0,
    0,   77,    0,   78,    0,   76,    0,    0,   25,
};
final static short yydgoto[] = {                          9,
   10,   11,   12,   13,   92,   14,   15,   29,   17,   18,
   19,  151,   30,   31,  107,   39,   32,   33,   81,   62,
   63,   64,  152,   34,   35,
};
final static short yysindex[] = {                       610,
    0,  158,  283,    0,  -88,   39,    0, -199,    0,  698,
    0,   33,   43,    0,    0,    0,    0, -170,   51,   14,
   56, -199,   98,  716,   58,    0,  122, -149,    0,  417,
   94,  507,  112,   -5,    0,  283,  553,  100,  189,  107,
  751,    0, -123,    0,    0,    0,  120,   50,   94,  -96,
   19,  -96,  122,  -92,  769,    0,  304, -229,  -80,  -16,
   94,  129,  140,    0,    0,  -78,  164,  164,    4,    4,
  156,    0,    0,    0,    0,    0,    0,    0,  207,  300,
  122,  -66,    0, -137,    0,    4,  388,  399,    0,    0,
  141,  722,    0,  283,    0,  160,    0,   75,    0,  -75,
    0, -134,    0,   94,    0,  -46,  -28,   94,   10,    0,
    0,  -64,  122,  -33,  -43,    0,  -40,    0,  101,    0,
   -8,   -5,   -5,    0,    0,    0,   -8,   -5,   -8,   -5,
   10,    0,   22,  636,    0,    0,    0,    0,    0,    0,
    0,    0,  223,    0,    0,    0,    0,    0, -177,   40,
   32,    0,    0,    0,   27,   46,    0,  -32,   44,   47,
  281,    0,    0,    0,    0,   64,    0,    0,   84,    0,
  228, -134,    0,  103,    0,   96,   99,    0,  111,  406,
    0,    0, -199,    0,    0,    0,  118,    0, -138,  783,
  811,    0,  790,  126,  113,  355,  143,  132,    0,  148,
  122,    0,  151,    0,  134,    0,  343,  287,    0,
};
final static short yyrindex[] = {                         0,
    0,  395,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  659,  677,    0,    0,    0,    0,    0,    0,   73,
  -41,    0,    0,    0,    0,    0,    0,    0,    0,  237,
  -19,    0,  427,    3,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,  130,  551,  215,    0,
    0,    0,    0,    0,    0,  121,    0,    0,    0,    0,
  145,    0,  373,    0,    0,    0,    0,    0,    0,    0,
  484,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,  441,    0,    0,    0,    0,    0,    0,
  797,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,  365,    0,    0,  463,  392,  506,    0,
    0,    0,    0,    0,    0,  163,   37,    0,    0,    0,
    0,   26,   48,    0,    0,    0,   70,  307,  329,  351,
  259,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,  804,    0,    0,    0,    0,    0,    0,   95,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,  528,    0,    0,  106,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,  114,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,  139,    0,    0,    0,    0,    0,    0,
};
final static short yygindex[] = {                         0,
   28,   42,  711,    0, -135,    0,    0,  832,    0,  -51,
  398,    0,  793,  807,    0,   -7,   20,  396,    0,    0,
    0,  309,  257,   77,  -55,
};
final static int YYTABLESIZE=1087;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         62,
   62,   62,   62,   62,   62,   62,  115,  160,  177,   80,
   80,   79,   79,  124,  125,  155,  156,   62,   62,   62,
   62,  108,  108,  108,   84,  108,   80,  108,   79,   24,
  136,  138,  140,   69,   37,   41,   87,    4,   70,  108,
  108,   88,  108,   97,  190,   97,    7,   97,   28,   55,
  150,   44,   80,  193,   79,   89,   37,   21,   95,   54,
    3,   97,   97,  106,   97,   44,   95,    4,   95,    5,
   95,  174,  171,    6,  133,  172,    7,   81,   40,  168,
   81,   62,   44,   62,   95,   95,   47,   95,   93,    4,
   93,   45,   93,   52,   52,   23,   44,  169,    7,   69,
   68,   46,   67,  108,   70,  108,   93,   93,   72,   93,
   96,   51,   96,  144,   96,  146,   60,   80,   21,   79,
  150,    3,  148,  134,  135,   97,  166,   97,   96,   96,
    5,   96,    4,   60,    6,   88,  149,   58,   88,   54,
   58,    7,   59,  122,  123,   28,   87,   97,   95,   87,
   95,   28,   85,  101,   82,  128,  130,   82,   93,  102,
  103,   67,  106,  106,  110,  106,   28,  106,   21,  118,
   93,    3,   93,   62,  207,   62,   80,  116,   79,   80,
    5,   36,   80,  119,    6,   66,  108,  108,   62,  108,
   21,  108,   96,    3,   96,  132,  126,   23,  120,  141,
  145,  147,    5,   68,  107,  107,    6,  107,   28,  107,
  191,  153,  157,  161,   62,   62,  162,   62,   62,   62,
   62,   62,   62,   62,   62,   62,   62,   62,   27,  154,
   62,   62,   44,   28,   62,   62,  108,  108,  159,  176,
  108,  108,  108,  108,  108,  108,  108,  108,  108,  108,
   86,   28,  108,  108,   62,  117,  108,  108,   60,   97,
   21,   26,   97,   97,   97,   97,   97,   97,   97,   97,
   97,   97,   53,   60,   97,   97,  105,   50,   97,   97,
   22,  167,   95,  164,  173,   95,   95,   95,   95,   95,
   95,   95,   95,   95,   95,   50,  170,   95,   95,   49,
  178,   95,   95,  175,   93,   50,   50,   93,   93,   93,
   93,   93,   93,   93,   93,   93,   93,   49,  179,   93,
   93,  180,   27,   93,   93,  181,   96,   28,   60,   96,
   96,   96,   96,   96,   96,   96,   96,   96,   96,   60,
  182,   96,   96,  113,   28,   96,   96,   92,   28,   92,
  183,   92,  186,   25,   21,   56,   25,   21,   26,   50,
  185,   50,   25,   21,   26,   92,   92,  188,   92,   94,
  187,   94,   57,   94,  192,   57,  106,   25,   21,   26,
   96,   49,  199,   49,  200,   62,   62,   94,   94,   62,
   94,   91,  106,   91,  201,   91,   62,   62,   62,  202,
  108,  208,   62,  203,  204,   62,   62,  206,   61,   91,
   91,  209,   91,   65,   21,   48,  108,    3,  107,  121,
   21,   26,   71,   61,    4,   61,    5,  163,  184,   92,
    6,   92,   28,    7,  107,   59,    0,    0,   62,    0,
   62,    0,    0,   28,   25,   21,   26,    0,    0,    0,
   59,   94,   59,   94,    0,   62,    0,    0,   94,   80,
    0,   79,  127,   21,   26,    0,    0,    0,    0,    0,
   60,   60,    0,   91,   60,   91,   78,    0,   77,    0,
    0,   60,   60,   60,    0,   48,    0,   60,    0,   61,
   60,   60,    0,   50,    0,    0,   50,   50,   50,   35,
    0,    0,    0,   50,   50,   50,    0,    0,   50,   50,
    0,    0,   50,   50,    0,   49,   59,    0,   49,   49,
   49,   27,    0,    0,    0,   49,   49,   49,  189,    0,
   49,   49,    0,    0,   49,   49,    0,    0,   25,   21,
   26,    0,   47,    0,    0,    0,    0,    0,    0,   48,
    0,   48,    0,    0,    0,  129,   21,   26,    0,   25,
   21,   26,    0,   92,   26,   35,   92,   92,   92,   92,
   92,   92,   92,   92,   92,   92,    0,    0,   92,   92,
    0,    0,   92,   92,    0,   94,   34,   27,   94,   94,
   94,   94,   94,   94,   94,   94,   94,   94,    0,    0,
   94,   94,    0,    0,   94,   94,   47,   91,   47,   24,
   91,   91,   91,   91,   91,   91,   91,   91,   91,   91,
   61,   61,   91,   91,   61,    0,   91,   91,    0,   37,
   26,   61,   61,   61,    0,    0,    0,   61,    0,    0,
   61,   61,    0,  137,   21,   26,    0,   59,   59,    0,
   62,   59,   34,   62,  139,   21,   26,    0,   59,   59,
   59,    0,   21,    0,   59,    3,    0,   59,   59,    0,
    0,    0,    0,    0,    5,   24,    0,   90,    6,   73,
   74,   75,   76,   48,    0,    0,   48,   48,   48,    0,
    0,    0,    0,   48,   48,   48,    0,   35,   48,   48,
   35,    0,   48,   48,    0,    0,    0,   35,   35,   35,
    0,    0,   35,   35,    0,   38,   35,   35,    0,   27,
    0,    0,   27,    0,    0,    0,    0,    0,    0,   27,
   27,   27,    8,    0,   27,   27,    0,    0,   27,   27,
   47,    0,   38,   47,   47,   47,    0,   91,    0,    0,
   47,   47,   47,    0,    0,   47,   47,    0,   37,   47,
   47,    0,   26,   21,    0,   26,    3,   82,   83,    0,
    0,    0,   26,   26,   26,    5,    0,   26,   26,    6,
    0,   26,   26,   14,   34,    0,    0,   34,    0,    0,
    0,    0,   38,    0,   34,   34,   34,    0,    0,   34,
   34,   15,  143,   34,   34,    0,   20,   24,   20,   21,
   24,   20,    3,    0,   20,   60,   20,   24,   24,   24,
    0,    5,   43,   24,   49,    6,   24,   24,   20,   61,
   20,   16,   98,   16,    0,    0,   16,    0,   20,   16,
   66,   16,    0,   20,   38,  109,  142,   20,    0,  114,
    0,    0,    0,   16,    0,   16,  104,    0,  108,    0,
    0,   20,    0,   16,    0,    1,    2,    0,   16,    3,
    0,    0,   16,  131,    0,  100,    4,    0,    5,    0,
    0,    0,    6,    0,    0,    7,   16,    0,   20,    0,
   91,    0,   21,  112,    0,    3,    0,  165,   20,   91,
  143,    0,    0,  143,    5,  158,    0,  195,    6,    0,
    0,   60,    0,   16,  198,   14,    0,    0,   14,    0,
    0,   19,    0,   16,    0,   14,   14,   14,   18,    0,
    0,   14,    0,   15,   14,   14,   15,    0,    0,    0,
   20,    0,    0,   15,   15,   15,    0,    0,    0,   15,
    0,    0,   15,   15,   21,    0,    0,    3,    0,    0,
    0,    0,    0,    0,    4,   16,    5,    0,    0,    0,
    6,    0,   21,    7,   42,    3,    0,    0,   21,    0,
    0,    3,    4,    0,    5,    0,   20,    0,    6,   20,
    5,    7,   65,  205,    6,   20,   20,   20,    0,   20,
    0,    0,    0,    0,    0,    0,    0,   21,    0,    0,
    3,   16,    0,    0,   16,    0,    0,    4,    0,    5,
   16,   16,   16,    6,   16,   21,    7,   99,    3,    0,
    0,    0,    0,    0,    0,    4,    0,    5,    0,   21,
    0,    6,    3,    0,    7,  111,   21,    0,    0,    3,
    0,    5,    0,   19,  194,    6,   19,    0,    5,    0,
   18,  197,    6,   18,    0,   19,    0,   21,   19,   19,
    3,    0,   18,    0,    0,   18,   18,    4,  196,    5,
    0,    0,    0,    6,    0,    0,    7,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         41,
   42,   43,   44,   45,   46,   47,   58,   41,   41,   43,
   43,   45,   45,   69,   70,   44,   45,   59,   60,   61,
   62,   41,   42,   43,   32,   45,   43,   47,   45,    2,
   86,   87,   88,   42,  123,    8,   42,  267,   47,   59,
   60,   47,   62,   41,  180,   43,  276,   45,   45,   22,
  102,   10,   43,  189,   45,   36,  123,  257,   39,   46,
  260,   59,   60,   45,   62,   24,   41,  267,   43,  269,
   45,   45,   41,  273,   82,   44,  276,   41,   40,  257,
   44,  123,   41,  125,   59,   60,  257,   62,   41,  267,
   43,   59,   45,   44,   44,   40,   55,  149,  276,   42,
   43,   59,   45,  123,   47,  125,   59,   60,  258,   62,
   41,   61,   43,   94,   45,   41,   44,   43,  257,   45,
  172,  260,  257,  261,  262,  123,  134,  125,   59,   60,
  269,   62,  267,   61,  273,   41,  271,   40,   44,   46,
   40,  276,   45,   67,   68,   45,   41,   41,  123,   44,
  125,   45,   41,  277,   41,   79,   80,   44,   59,   40,
  257,   41,   42,   43,  257,   45,   45,   47,  257,   41,
  123,  260,  125,   44,   41,   46,   43,  258,   45,   41,
  269,  270,   44,   44,  273,   41,   42,   43,   59,   45,
  257,   47,  123,  260,  125,  262,   41,   40,  277,   59,
   41,  277,  269,   41,   42,   43,  273,   45,   45,   47,
  183,  258,  277,  257,  256,  257,  257,  259,  260,  261,
  262,  263,  264,  265,  266,  267,  268,  269,   40,  258,
  272,  273,  191,   45,  276,  277,  256,  257,  272,  272,
  260,  261,  262,  263,  264,  265,  266,  267,  268,  269,
  256,   45,  272,  273,  125,  272,  276,  277,   44,  257,
  257,  258,  260,  261,  262,  263,  264,  265,  266,  267,
  268,  269,  259,   59,  272,  273,  258,   41,  276,  277,
  123,   59,  257,  262,  258,  260,  261,  262,  263,  264,
  265,  266,  267,  268,  269,   59,  257,  272,  273,   41,
  257,  276,  277,  258,  257,  256,  256,  260,  261,  262,
  263,  264,  265,  266,  267,  268,  269,   59,  272,  272,
  273,   41,   40,  276,  277,  262,  257,   45,  256,  260,
  261,  262,  263,  264,  265,  266,  267,  268,  269,  125,
  257,  272,  273,   40,   45,  276,  277,   41,   45,   43,
  123,   45,  257,  256,  257,  258,  256,  257,  258,  123,
  258,  125,  256,  257,  258,   59,   60,  257,   62,   41,
  272,   43,  275,   45,  257,  275,  256,  256,  257,  258,
  274,  123,  257,  125,  272,  256,  257,   59,   60,  260,
   62,   41,  272,   43,   40,   45,  267,  268,  269,  257,
  256,   59,  273,  272,  257,  276,  277,  257,   44,   59,
   60,  125,   62,   41,  257,   18,  272,  260,  256,  256,
  257,  258,   27,   59,  267,   61,  269,  119,  172,  123,
  273,  125,   45,  276,  272,   44,   -1,   -1,   44,   -1,
   46,   -1,   -1,   45,  256,  257,  258,   -1,   -1,   -1,
   59,  123,   61,  125,   -1,   61,   -1,   -1,  270,   43,
   -1,   45,  256,  257,  258,   -1,   -1,   -1,   -1,   -1,
  256,  257,   -1,  123,  260,  125,   60,   -1,   62,   -1,
   -1,  267,  268,  269,   -1,   59,   -1,  273,   -1,  125,
  276,  277,   -1,  257,   -1,   -1,  260,  261,  262,   59,
   -1,   -1,   -1,  267,  268,  269,   -1,   -1,  272,  273,
   -1,   -1,  276,  277,   -1,  257,  125,   -1,  260,  261,
  262,   59,   -1,   -1,   -1,  267,  268,  269,  123,   -1,
  272,  273,   -1,   -1,  276,  277,   -1,   -1,  256,  257,
  258,   -1,   59,   -1,   -1,   -1,   -1,   -1,   -1,  123,
   -1,  125,   -1,   -1,   -1,  256,  257,  258,   -1,  256,
  257,  258,   -1,  257,   59,  125,  260,  261,  262,  263,
  264,  265,  266,  267,  268,  269,   -1,   -1,  272,  273,
   -1,   -1,  276,  277,   -1,  257,   59,  125,  260,  261,
  262,  263,  264,  265,  266,  267,  268,  269,   -1,   -1,
  272,  273,   -1,   -1,  276,  277,  123,  257,  125,   59,
  260,  261,  262,  263,  264,  265,  266,  267,  268,  269,
  256,  257,  272,  273,  260,   -1,  276,  277,   -1,  123,
  125,  267,  268,  269,   -1,   -1,   -1,  273,   -1,   -1,
  276,  277,   -1,  256,  257,  258,   -1,  256,  257,   -1,
  256,  260,  125,  259,  256,  257,  258,   -1,  267,  268,
  269,   -1,  257,   -1,  273,  260,   -1,  276,  277,   -1,
   -1,   -1,   -1,   -1,  269,  125,   -1,  125,  273,  263,
  264,  265,  266,  257,   -1,   -1,  260,  261,  262,   -1,
   -1,   -1,   -1,  267,  268,  269,   -1,  257,  272,  273,
  260,   -1,  276,  277,   -1,   -1,   -1,  267,  268,  269,
   -1,   -1,  272,  273,   -1,    5,  276,  277,   -1,  257,
   -1,   -1,  260,   -1,   -1,   -1,   -1,   -1,   -1,  267,
  268,  269,  123,   -1,  272,  273,   -1,   -1,  276,  277,
  257,   -1,   32,  260,  261,  262,   -1,   37,   -1,   -1,
  267,  268,  269,   -1,   -1,  272,  273,   -1,  123,  276,
  277,   -1,  257,  257,   -1,  260,  260,  261,  262,   -1,
   -1,   -1,  267,  268,  269,  269,   -1,  272,  273,  273,
   -1,  276,  277,  125,  257,   -1,   -1,  260,   -1,   -1,
   -1,   -1,   82,   -1,  267,  268,  269,   -1,   -1,  272,
  273,  125,   92,  276,  277,   -1,    0,  257,    2,  257,
  260,    5,  260,   -1,    8,   23,   10,  267,  268,  269,
   -1,  269,  125,  273,   18,  273,  276,  277,   22,   23,
   24,    0,   40,    2,   -1,   -1,    5,   -1,   32,    8,
  125,   10,   -1,   37,  134,   53,  125,   41,   -1,   57,
   -1,   -1,   -1,   22,   -1,   24,   50,   -1,   52,   -1,
   -1,   55,   -1,   32,   -1,  256,  257,   -1,   37,  260,
   -1,   -1,   41,   81,   -1,  125,  267,   -1,  269,   -1,
   -1,   -1,  273,   -1,   -1,  276,   55,   -1,   82,   -1,
  180,   -1,  257,  125,   -1,  260,   -1,  262,   92,  189,
  190,   -1,   -1,  193,  269,  113,   -1,  125,  273,   -1,
   -1,  119,   -1,   82,  125,  257,   -1,   -1,  260,   -1,
   -1,  125,   -1,   92,   -1,  267,  268,  269,  125,   -1,
   -1,  273,   -1,  257,  276,  277,  260,   -1,   -1,   -1,
  134,   -1,   -1,  267,  268,  269,   -1,   -1,   -1,  273,
   -1,   -1,  276,  277,  257,   -1,   -1,  260,   -1,   -1,
   -1,   -1,   -1,   -1,  267,  134,  269,   -1,   -1,   -1,
  273,   -1,  257,  276,  277,  260,   -1,   -1,  257,   -1,
   -1,  260,  267,   -1,  269,   -1,  180,   -1,  273,  183,
  269,  276,  277,  201,  273,  189,  190,  191,   -1,  193,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,  257,   -1,   -1,
  260,  180,   -1,   -1,  183,   -1,   -1,  267,   -1,  269,
  189,  190,  191,  273,  193,  257,  276,  277,  260,   -1,
   -1,   -1,   -1,   -1,   -1,  267,   -1,  269,   -1,  257,
   -1,  273,  260,   -1,  276,  277,  257,   -1,   -1,  260,
   -1,  269,   -1,  257,  272,  273,  260,   -1,  269,   -1,
  257,  272,  273,  260,   -1,  269,   -1,  257,  272,  273,
  260,   -1,  269,   -1,   -1,  272,  273,  267,  268,  269,
   -1,   -1,   -1,  273,   -1,   -1,  276,
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
"prog : '{' listasentencias '}' EOF",
"prog : ID '{' listasentencias EOF",
"prog : ID listasentencias '}' EOF",
"prog : ID listasentencias EOF",
"prog : '{' listasentencias EOF",
"prog : listasentencias '}' EOF",
"prog : listasentencias EOF",
"prog : error",
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
"declaracion : tipo listavariables",
"declaracion : tipo ID '(' listaparametros ')' '{' listasentencias RETURN '(' expresion ')' ';' '}'",
"asignacion : identificador ASIG expresion",
"asignacion : listavariables '=' listaconstantes",
"cuerpo_bloque : '{' listasentenciasejecutables '}'",
"cuerpo_bloque : sentenciaejecutable ';'",
"cuerpo_bloque : '{' '}'",
"control : IF condicion cuerpo_bloque ELSE cuerpo_bloque ENDIF",
"control : IF condicion cuerpo_bloque ENDIF",
"control : DO cuerpo_bloque UNTIL condicion",
"control : IF condicion cuerpo_bloque ELSE cuerpo_bloque",
"control : IF condicion cuerpo_bloque",
"control : IF condicion cuerpo_bloque ELSE ENDIF",
"control : IF condicion ELSE cuerpo_bloque ENDIF",
"control : IF condicion ENDIF",
"control : IF condicion ELSE ENDIF",
"control : DO UNTIL condicion",
"control : DO cuerpo_bloque condicion",
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
"listavariables : listavariables error identificador",
"identificador : ID",
"identificador : identificador '.' ID",
"funcion : ID '(' argumentos_funcion ')'",
"argumentos_funcion : listapinvocacion",
"argumentos_funcion : identificador",
"argumentos_funcion : CTE",
"argumentos_funcion : '-' CTE",
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

//#line 182 "gramatica.y"

private void yyerror(String s) {
    System.out.println("||================ ANALIZADOR SINTÁCTICO: Error: " + s);
}

public Parser(Map<String, Simbolo> tablaSimbolos, String path)
{
  this.tablaSimbolos = tablaSimbolos;
  this.lexer = new Lexico(tablaSimbolos);
  this.lexer.cargarBuffer(path);
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
//#line 623 "Parser.java"
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
        yychar = lexer.yylex(yylval);  //get next token
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
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(3).ival + ": falta el nombre del programa");}
break;
case 3:
//#line 13 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta la llave de cierre del cuerpo del programa");}
break;
case 4:
//#line 14 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(3).ival + ": falta la llave de apertura del cuerpo del programa");}
break;
case 5:
//#line 15 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": faltan ambas llaves en cuerpo del programa");}
break;
case 6:
//#line 16 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el nombre del programa y la llave de cierre del cuerpo del programa");}
break;
case 7:
//#line 17 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(2).ival + ": falta el nombre del programa y la llave de apertura del cuerpo del programa");}
break;
case 8:
//#line 18 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el nombre del programa y ambas llaves en cuerpo del programa");}
break;
case 9:
//#line 19 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico: el programa no es válido");}
break;
case 14:
//#line 28 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta ; al final de la sentencia ejecutable");}
break;
case 15:
//#line 29 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta ; al final de la declaración");}
break;
case 18:
//#line 34 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta ; al final de la sentencia ejecutable");}
break;
case 19:
//#line 35 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta ; al final de la sentencia ejecutable");}
break;
case 23:
//#line 41 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un print");}
break;
case 24:
//#line 44 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó una declaración de variables");}
break;
case 25:
//#line 45 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó una declaración de función");}
break;
case 26:
//#line 48 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó una asignación simple");}
break;
case 27:
//#line 49 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó una asignación múltiple");}
break;
case 31:
//#line 57 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un bloque IF-ELSE");}
break;
case 32:
//#line 58 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un bloque IF");}
break;
case 33:
//#line 59 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un bloque DO UNTIL");}
break;
case 34:
//#line 61 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta la palabra ENDIF al final del bloque IF-ELSE");}
break;
case 35:
//#line 62 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta la palabra ENDIF al final del bloque IF");}
break;
case 36:
//#line 63 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el cuerpo en el bloque ELSE");}
break;
case 37:
//#line 64 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(3).ival + ": falta el cuerpo en el bloque IF");}
break;
case 38:
//#line 65 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el cuerpo en el bloque IF");}
break;
case 39:
//#line 66 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el cuerpo en los bloques IF y ELSE");}
break;
case 40:
//#line 68 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(2).ival + ": falta de cuerpo en iteracion");}
break;
case 41:
//#line 69 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta la palabra UNTIL en la iteracion");}
break;
case 44:
//#line 74 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta de argumento en sentencia print");}
break;
case 46:
//#line 78 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta el paréntesis de apertura de la condición");}
break;
case 47:
//#line 79 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta el paréntesis de cierre de la condición");}
break;
case 48:
//#line 80 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": faltan ambos paréntesis de la condición");}
break;
case 50:
//#line 84 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": La condición es incompleta");}
break;
case 61:
//#line 101 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(2).ival + ": falta ',' entre variables"); }
break;
case 64:
//#line 108 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó una función invocada correctamente");}
break;
case 67:
//#line 113 "gramatica.y"
{ constantePositiva(val_peek(0)); }
break;
case 68:
//#line 114 "gramatica.y"
{ constanteNegativa(val_peek(0)); }
break;
case 73:
//#line 123 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(2).ival + ": falta el paréntesis de cierre de la función TRUNC");}
break;
case 74:
//#line 124 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(4).ival + ": falta el paréntesis de apertura de la función TRUNC");}
break;
case 75:
//#line 125 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(2).ival + ": faltan ambos paréntesis de la función TRUNC");}
break;
case 77:
//#line 127 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(2).ival + ": falta llave de cierre del cuerpo de la función Lambda");}
break;
case 78:
//#line 128 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(4).ival + ": falta llave de apertura del cuerpo de la función Lambda");}
break;
case 79:
//#line 129 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(3).ival + ": faltan ambas llaves del cuerpo de la función Lambda");}
break;
case 80:
//#line 130 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": No se especifica el parametro formal al que corresponde el parámetro real");}
break;
case 81:
//#line 131 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": No se especifica el parametro formal al que corresponde el parámetro real");}
break;
case 82:
//#line 132 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": No se especifica el parametro formal al que corresponde el parámetro real");}
break;
case 87:
//#line 141 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el nombre del parámetro");}
break;
case 88:
//#line 142 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta el nombre del parámetro");}
break;
case 89:
//#line 143 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el tipo del parámetro");}
break;
case 90:
//#line 144 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta el tipo del parámetro");}
break;
case 93:
//#line 149 "gramatica.y"
{ System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la izquierda de '+'"); }
break;
case 94:
//#line 150 "gramatica.y"
{ System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la derecha de '+'"); }
break;
case 95:
//#line 151 "gramatica.y"
{ System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la izquierda de '-'"); }
break;
case 96:
//#line 152 "gramatica.y"
{ System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la derecha de '-'"); }
break;
case 100:
//#line 158 "gramatica.y"
{ System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la izquierda de '*'"); }
break;
case 101:
//#line 159 "gramatica.y"
{ System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la derecha de '*'"); }
break;
case 102:
//#line 160 "gramatica.y"
{ System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la izquierda de '/'"); }
break;
case 103:
//#line 161 "gramatica.y"
{ System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la derecha de '/'"); }
break;
case 104:
//#line 162 "gramatica.y"
{ System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(2).ival + ": falta operador entre operandos o falta de comparador en condición"); }
break;
case 106:
//#line 166 "gramatica.y"
{ constantePositiva(val_peek(0)); }
break;
case 107:
//#line 167 "gramatica.y"
{ constanteNegativa(val_peek(0)); }
break;
case 110:
//#line 172 "gramatica.y"
{ constantePositiva(val_peek(0)); }
break;
case 111:
//#line 173 "gramatica.y"
{ constanteNegativa(val_peek(0)); }
break;
case 112:
//#line 174 "gramatica.y"
{ constantePositiva(val_peek(0)); }
break;
case 113:
//#line 175 "gramatica.y"
{ constanteNegativa(val_peek(0)); }
break;
case 114:
//#line 176 "gramatica.y"
{ System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta ',' entre constantes"); constantePositiva(val_peek(0)); }
break;
case 115:
//#line 177 "gramatica.y"
{ System.out.println("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(2).ival + ": falta ',' entre constantes"); constanteNegativa(val_peek(0)); }
break;
//#line 1040 "Parser.java"
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
