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






//#line 2 "gramatica3.y"
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
public final static short UMINUS=278;
public final static short YYERRCODE=256;
final static short yylhs[] = {                           -1,
    0,    0,    0,    0,    0,    0,    0,    0,    1,    1,
    2,    2,    2,    2,    5,    5,    5,    5,    3,    3,
    3,    3,    4,    4,    6,    6,   16,   16,   16,    7,
    7,    7,    7,    7,    7,    7,    7,    7,    7,    7,
    9,    9,    9,   17,   17,   17,   17,   18,   18,   19,
   19,   19,   19,   19,   19,   10,   10,   11,   11,   11,
   14,   14,    8,   20,   20,   20,   21,   21,   22,   22,
   22,   22,   22,   22,   22,   22,   22,   22,   22,   22,
   12,   12,   23,   23,   23,   23,   23,   23,   13,   13,
   13,   13,   13,   13,   13,   24,   24,   24,   24,   24,
   24,   24,   24,   25,   25,   25,   25,   15,   15,   15,
   15,   15,   15,
};
final static short yylen[] = {                            2,
    5,    4,    4,    4,    3,    3,    3,    2,    2,    1,
    2,    2,    1,    1,    3,    2,    2,    1,    1,    1,
    1,    1,    2,   13,    3,    3,    3,    2,    2,    6,
    4,    4,    5,    3,    5,    5,    3,    4,    3,    3,
    4,    4,    3,    3,    2,    2,    1,    3,    1,    1,
    1,    1,    1,    1,    1,    1,    1,    3,    1,    3,
    1,    3,    4,    1,    1,    1,    3,    1,    3,    6,
    5,    5,    4,    9,    8,    8,    7,    8,    2,    5,
    3,    1,    3,    2,    2,    1,    2,    1,    3,    3,
    3,    3,    3,    3,    1,    3,    3,    3,    3,    3,
    3,    3,    1,    1,    2,    1,    1,    3,    4,    1,
    2,    2,    3,
};
final static short yydefred[] = {                         0,
    0,    0,   56,    0,    0,   57,    0,    0,    0,   10,
    0,    0,   19,   20,   21,   22,    0,    0,    0,    0,
    0,    0,    0,    0,  104,    0,    0,  107,    0,    0,
    0,    0,    0,  103,    0,    0,    0,    0,    0,    0,
    8,    0,    9,   11,   12,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,   68,    5,    0,    0,    0,    0,    0,  105,    0,
   52,   53,   54,   55,    0,    0,   50,   51,    0,    0,
   37,    0,   45,    0,    0,    0,   39,   29,    0,    0,
   28,    0,   40,    0,   43,    0,    6,    0,    7,    0,
   61,    0,  110,    0,    0,    0,    0,   62,    3,    0,
    0,    0,    0,    0,   63,    0,    4,    0,    0,    0,
   98,  100,   44,    0,    0,    0,    0,    0,   38,    0,
    0,   31,  102,   99,   96,  101,   97,   16,   27,    0,
   32,   42,   41,    2,   88,    0,    0,    0,   82,  111,
  112,    0,    0,    1,    0,    0,    0,    0,   69,   67,
   36,   35,    0,   15,   87,    0,   84,    0,    0,  113,
  108,    0,    0,    0,   73,    0,    0,   30,   83,    0,
   81,  109,   71,    0,   72,    0,    0,    0,   70,    0,
    0,    0,    0,    0,    0,   77,    0,    0,   75,    0,
   76,    0,   74,    0,    0,   24,
};
final static short yydgoto[] = {                          8,
    9,   10,   11,   12,   90,   13,   14,   28,   16,   17,
   18,  148,   29,   30,  105,   38,   31,   32,   79,   60,
   61,   62,  149,   33,   34,
};
final static short yysindex[] = {                       624,
  158,  189,    0,  -88,   11,    0, -177,    0,  692,    0,
  -52,   15,    0,    0,    0,    0, -198,   51,   14,   57,
 -177,   98,  710,   58,    0, -160,  122,    0,  417,   68,
  285,   83,   -5,    0,  189,  666,   78,  163,  107,  738,
    0, -135,    0,    0,    0,  105,   50,   68, -103,   19,
 -103,  122, -102,  763,    0,  283, -222,  -16,   68,  118,
  116,    0,    0, -116,  207,  207,    4,    4,    0,  124,
    0,    0,    0,    0,  299,  304,    0,    0,  122,  507,
    0, -247,    0,    4,  399,  414,    0,    0,  111,  786,
    0,  189,    0,  138,    0,   75,    0,  -94,    0, -210,
    0,   68,    0,  -74,  -28,   68,   36,    0,    0,  -86,
  122,  -33,  -63,  -58,    0,  101,    0,   -9,   -5,   -5,
    0,    0,    0,   -9,   -5,   -9,   -5,   36,    0,  -62,
  648,    0,    0,    0,    0,    0,    0,    0,    0,  142,
    0,    0,    0,    0,    0, -199,  -55,  -10,    0,    0,
    0,  -53,   27,    0,  -32,  -47,  -61,  171,    0,    0,
    0,    0,  -49,    0,    0,  -43,    0,   94, -210,    0,
    0,   24,  -24,   12,    0,   40,  406,    0,    0, -177,
    0,    0,    0,   44,    0, -138,  577,  724,    0,  617,
   47,   54,  279,   65,   69,    0,   88,  122,    0,   96,
    0,   82,    0,  292,  236,    0,
};
final static short yyrindex[] = {                         0,
  395,    0,    0,    0,    0,    0,    0,    0,    0,    0,
  665,  687,    0,    0,    0,    0,    0,    0,   73,  -41,
    0,    0,    0,    0,    0,    0,    0,    0,  237,  -19,
    0,  427,    3,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,  130,  551,  215,    0,    0,
    0,    0,    0,    0,  121,    0,    0,    0,  145,    0,
  327,    0,    0,    0,    0,    0,    0,    0,    0,  484,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  441,    0,    0,    0,    0,    0,    0,  716,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  365,    0,    0,  463,  392,  506,    0,    0,    0,
    0,    0,    0,   32,    0,    0,    0,    0,   26,   48,
    0,    0,    0,   70,  307,  329,  351,  259,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,  769,
    0,    0,    0,    0,    0,    0,   92,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,  528,    0,    0,  106,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,  134,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,  136,
    0,    0,    0,    0,    0,    0,
};
final static short yygindex[] = {                         0,
   29,   16,  785,    0,  -99,    0,    0,  847,    0,  -25,
  354,    0,  803,  822,    0,   22,   17,  348,    0,    0,
    0,  267,  216,  131,   72,
};
final static int YYTABLESIZE=1059;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         61,
   61,   61,   61,   61,   61,   61,   44,  157,  174,   75,
   75,   76,   76,  131,  132,  153,  152,   61,   61,   61,
   61,  106,  106,  106,   43,  106,   75,  106,   76,   23,
  168,  113,   67,  169,   36,   40,   85,   68,   43,  106,
  106,   86,  106,   95,    3,   95,  145,   95,   26,   54,
   39,   87,   82,    6,   93,   43,    3,  165,   46,   53,
  146,   95,   95,  104,   95,    6,   91,    3,   91,   43,
   91,  172,   79,   45,  147,   79,    6,  187,   75,   20,
   76,   61,    2,   61,   91,   91,  190,   91,   93,    3,
   93,    4,   93,   51,   51,    5,   22,   69,    6,   67,
   65,  130,   66,  106,   68,  106,   93,   93,  141,   93,
   92,   50,   92,   53,   92,  143,   59,   75,   20,   76,
  166,    2,  204,   83,   75,   95,   76,   95,   92,   92,
    4,   92,   86,   59,    5,   86,   91,   57,  121,  122,
   57,   99,   26,  147,  100,   26,   85,   95,   91,   85,
   91,   26,  163,  101,  108,  133,  135,  137,  115,  116,
  117,   66,  104,  104,  123,  104,   26,  104,   20,  138,
   93,    2,   93,   61,   80,   61,   78,   80,  142,   78,
    4,   35,  144,  150,    5,   65,  106,  106,   61,  106,
  154,  106,   92,  158,   92,  119,  120,   22,  159,  161,
  164,  167,   27,   43,  170,  125,  127,   26,  188,  175,
  176,  177,  178,  179,   61,   61,  180,   61,   61,   61,
   61,   61,   61,   61,   61,   61,   61,   61,   27,  151,
   61,   61,  183,   26,   61,   61,  106,  106,  156,  173,
  106,  106,  106,  106,  106,  106,  106,  106,  106,  106,
   84,   26,  106,  106,   61,  114,  106,  106,   59,   95,
   20,   25,   95,   95,   95,   95,   95,   95,   95,   95,
   95,   95,   52,   59,   95,   95,  103,   49,   95,   95,
   21,  182,   91,  184,  171,   91,   91,   91,   91,   91,
   91,   91,   91,   91,   91,   49,  185,   91,   91,   48,
  189,   91,   91,  196,   93,   49,   49,   93,   93,   93,
   93,   93,   93,   93,   93,   93,   93,   48,  198,   93,
   93,  199,  111,   93,   93,  197,   92,   26,   59,   92,
   92,   92,   92,   92,   92,   92,   92,   92,   92,   59,
  200,   92,   92,   26,  201,   92,   92,   89,   26,   89,
  205,   89,  203,   24,   20,   55,   24,   20,   25,   49,
  206,   49,   24,   20,   25,   89,   89,   64,   89,   94,
   47,   94,   56,   94,   70,   56,  104,   24,   20,   25,
   94,   48,  160,   48,  181,   61,   61,   94,   94,   61,
   94,   90,  104,   90,    0,   90,   61,   61,   61,    0,
  106,    0,   61,    0,    0,   61,   61,   36,   60,   90,
   90,    0,   90,    0,   20,    0,  106,    2,   24,   20,
   25,    0,    0,   60,    3,   60,    4,    0,    0,   89,
    5,   89,   92,    6,    0,   58,    0,    0,   61,    0,
   61,    0,    0,   26,   24,   20,   25,    0,    0,    0,
   58,   94,   58,   94,    0,   61,    0,    0,   26,   75,
    0,   76,  118,   20,   25,    0,    0,    0,    0,    0,
   59,   59,    0,   90,   59,   90,   78,    0,   77,    0,
    0,   59,   59,   59,    0,   47,    0,   59,    0,   60,
   59,   59,    0,   49,    0,    0,   49,   49,   49,   34,
    0,    0,    0,   49,   49,   49,    0,    0,   49,   49,
    0,    0,   49,   49,    0,   48,   58,    0,   48,   48,
   48,   26,    0,    0,    0,   48,   48,   48,  186,    0,
   48,   48,    0,    0,   48,   48,    0,    0,   24,   20,
   25,   20,   46,    0,    2,   80,   81,    0,    0,   47,
    0,   47,    0,    4,  124,   20,   25,    5,    0,  126,
   20,   25,    0,   89,   25,   34,   89,   89,   89,   89,
   89,   89,   89,   89,   89,   89,    0,    0,   89,   89,
    0,    0,   89,   89,    0,   94,   33,   26,   94,   94,
   94,   94,   94,   94,   94,   94,   94,   94,    0,    0,
   94,   94,    0,    0,   94,   94,   46,   90,   46,   23,
   90,   90,   90,   90,   90,   90,   90,   90,   90,   90,
   60,   60,   90,   90,   60,    0,   90,   90,    0,   36,
   25,   60,   60,   60,    0,    0,    0,   60,    0,    0,
   60,   60,    0,    0,    0,    0,    0,   58,   58,    0,
   61,   58,   33,   61,  134,   20,   25,    0,   58,   58,
   58,    0,   20,    0,   58,    2,    0,   58,   58,  136,
   20,   25,    0,    0,    4,   23,    0,    0,    5,   71,
   72,   73,   74,   47,    0,    0,   47,   47,   47,    0,
    0,    0,    0,   47,   47,   47,    0,   34,   47,   47,
   34,  192,   47,   47,    0,    0,    0,   34,   34,   34,
    0,    0,   34,   34,    0,    0,   34,   34,    0,   26,
    0,    0,   26,    0,    0,    0,    0,    0,    0,   26,
   26,   26,    0,    0,   26,   26,    0,    0,   26,   26,
   46,  195,    0,   46,   46,   46,    7,    0,    0,    0,
   46,   46,   46,    0,    0,   46,   46,    0,    0,   46,
   46,    0,   25,   20,    0,   25,    2,    0,  129,    0,
   36,    0,   25,   25,   25,    4,    0,   25,   25,    5,
    0,   25,   25,    0,   33,    0,    0,   33,   37,   13,
   88,    0,    0,    0,   33,   33,   33,    0,    0,   33,
   33,    0,    0,   33,   33,    0,    0,   23,    0,    0,
   23,   14,    0,    0,    0,   37,   42,   23,   23,   23,
   89,   19,   19,   23,   58,   19,   23,   23,   19,    0,
   19,    0,    0,   20,   64,    0,    2,    0,   48,    0,
   18,   96,   19,   59,   19,    4,   15,   15,  191,    5,
   15,    0,   19,   15,  107,   15,    0,   19,  112,    0,
    0,   19,   98,    0,   37,    0,    0,   15,    0,   15,
  102,    0,  106,   20,  140,   19,    2,   15,    0,    0,
    1,  128,   15,    2,    0,    4,   15,  110,  194,    5,
    3,    0,    4,   17,    0,    0,    5,    0,    0,    6,
   15,   19,    0,    0,   20,    0,    0,    2,    0,  162,
  139,   19,    0,  155,    0,   37,    4,    0,   58,    0,
    5,   13,   20,    0,   13,    2,   15,    0,    0,    0,
    0,   13,   13,   13,    4,    0,   15,   13,    5,    0,
   13,   13,    0,   14,    0,    0,   14,    0,   20,    0,
    0,    2,   19,   14,   14,   14,    0,    0,    3,   14,
    4,   89,   14,   14,    5,    0,   20,    6,   41,    2,
   89,  140,   18,    0,  140,   18,    3,   15,    4,    0,
   20,    0,    5,    2,   18,    6,   63,   18,   18,    0,
    3,  193,    4,    0,   20,    0,    5,    2,   19,    6,
  202,   19,    0,    0,    3,    0,    4,   19,   19,   19,
    5,   19,    0,    6,   97,    0,    0,    0,    0,   20,
    0,    0,    2,   15,    0,   17,   15,    0,   17,    3,
    0,    4,   15,   15,   15,    5,   15,   17,    6,  109,
   17,   17,   20,    0,    0,    2,    0,    0,    0,    0,
    0,    0,    0,    0,    4,    0,    0,    0,    5,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         41,
   42,   43,   44,   45,   46,   47,   59,   41,   41,   43,
   43,   45,   45,  261,  262,   44,   45,   59,   60,   61,
   62,   41,   42,   43,    9,   45,   43,   47,   45,    1,
   41,   57,   42,   44,  123,    7,   42,   47,   23,   59,
   60,   47,   62,   41,  267,   43,  257,   45,   45,   21,
   40,   35,   31,  276,   38,   40,  267,  257,  257,   46,
  271,   59,   60,   45,   62,  276,   41,  267,   43,   54,
   45,   45,   41,   59,  100,   44,  276,  177,   43,  257,
   45,  123,  260,  125,   59,   60,  186,   62,   41,  267,
   43,  269,   45,   44,   44,  273,   40,  258,  276,   42,
   43,   80,   45,  123,   47,  125,   59,   60,   92,   62,
   41,   61,   43,   46,   45,   41,   44,   43,  257,   45,
  146,  260,   41,   41,   43,  123,   45,  125,   59,   60,
  269,   62,   41,   61,  273,   44,   59,   40,   67,   68,
   40,  277,   45,  169,   40,   45,   41,   41,  123,   44,
  125,   45,  131,  257,  257,   84,   85,   86,   41,   44,
  277,   41,   42,   43,   41,   45,   45,   47,  257,   59,
  123,  260,  125,   44,   41,   46,   41,   44,   41,   44,
  269,  270,  277,  258,  273,   41,   42,   43,   59,   45,
  277,   47,  123,  257,  125,   65,   66,   40,  257,  262,
   59,  257,   40,  188,  258,   75,   76,   45,  180,  257,
  272,   41,  262,  257,  256,  257,  123,  259,  260,  261,
  262,  263,  264,  265,  266,  267,  268,  269,   40,  258,
  272,  273,  257,   45,  276,  277,  256,  257,  272,  272,
  260,  261,  262,  263,  264,  265,  266,  267,  268,  269,
  256,   45,  272,  273,  125,  272,  276,  277,   44,  257,
  257,  258,  260,  261,  262,  263,  264,  265,  266,  267,
  268,  269,  259,   59,  272,  273,  258,   41,  276,  277,
  123,  258,  257,  272,  258,  260,  261,  262,  263,  264,
  265,  266,  267,  268,  269,   59,  257,  272,  273,   41,
  257,  276,  277,  257,  257,  256,  256,  260,  261,  262,
  263,  264,  265,  266,  267,  268,  269,   59,   40,  272,
  273,  257,   40,  276,  277,  272,  257,   45,  256,  260,
  261,  262,  263,  264,  265,  266,  267,  268,  269,  125,
  272,  272,  273,   45,  257,  276,  277,   41,   45,   43,
   59,   45,  257,  256,  257,  258,  256,  257,  258,  123,
  125,  125,  256,  257,  258,   59,   60,   41,   62,   41,
   17,   43,  275,   45,   27,  275,  256,  256,  257,  258,
  274,  123,  116,  125,  169,  256,  257,   59,   60,  260,
   62,   41,  272,   43,   -1,   45,  267,  268,  269,   -1,
  256,   -1,  273,   -1,   -1,  276,  277,  123,   44,   59,
   60,   -1,   62,   -1,  257,   -1,  272,  260,  256,  257,
  258,   -1,   -1,   59,  267,   61,  269,   -1,   -1,  123,
  273,  125,  270,  276,   -1,   44,   -1,   -1,   44,   -1,
   46,   -1,   -1,   45,  256,  257,  258,   -1,   -1,   -1,
   59,  123,   61,  125,   -1,   61,   -1,   -1,   45,   43,
   -1,   45,  256,  257,  258,   -1,   -1,   -1,   -1,   -1,
  256,  257,   -1,  123,  260,  125,   60,   -1,   62,   -1,
   -1,  267,  268,  269,   -1,   59,   -1,  273,   -1,  125,
  276,  277,   -1,  257,   -1,   -1,  260,  261,  262,   59,
   -1,   -1,   -1,  267,  268,  269,   -1,   -1,  272,  273,
   -1,   -1,  276,  277,   -1,  257,  125,   -1,  260,  261,
  262,   59,   -1,   -1,   -1,  267,  268,  269,  123,   -1,
  272,  273,   -1,   -1,  276,  277,   -1,   -1,  256,  257,
  258,  257,   59,   -1,  260,  261,  262,   -1,   -1,  123,
   -1,  125,   -1,  269,  256,  257,  258,  273,   -1,  256,
  257,  258,   -1,  257,   59,  125,  260,  261,  262,  263,
  264,  265,  266,  267,  268,  269,   -1,   -1,  272,  273,
   -1,   -1,  276,  277,   -1,  257,   59,  125,  260,  261,
  262,  263,  264,  265,  266,  267,  268,  269,   -1,   -1,
  272,  273,   -1,   -1,  276,  277,  123,  257,  125,   59,
  260,  261,  262,  263,  264,  265,  266,  267,  268,  269,
  256,  257,  272,  273,  260,   -1,  276,  277,   -1,  123,
  125,  267,  268,  269,   -1,   -1,   -1,  273,   -1,   -1,
  276,  277,   -1,   -1,   -1,   -1,   -1,  256,  257,   -1,
  256,  260,  125,  259,  256,  257,  258,   -1,  267,  268,
  269,   -1,  257,   -1,  273,  260,   -1,  276,  277,  256,
  257,  258,   -1,   -1,  269,  125,   -1,   -1,  273,  263,
  264,  265,  266,  257,   -1,   -1,  260,  261,  262,   -1,
   -1,   -1,   -1,  267,  268,  269,   -1,  257,  272,  273,
  260,  125,  276,  277,   -1,   -1,   -1,  267,  268,  269,
   -1,   -1,  272,  273,   -1,   -1,  276,  277,   -1,  257,
   -1,   -1,  260,   -1,   -1,   -1,   -1,   -1,   -1,  267,
  268,  269,   -1,   -1,  272,  273,   -1,   -1,  276,  277,
  257,  125,   -1,  260,  261,  262,  123,   -1,   -1,   -1,
  267,  268,  269,   -1,   -1,  272,  273,   -1,   -1,  276,
  277,   -1,  257,  257,   -1,  260,  260,   -1,  262,   -1,
  123,   -1,  267,  268,  269,  269,   -1,  272,  273,  273,
   -1,  276,  277,   -1,  257,   -1,   -1,  260,    4,  125,
  125,   -1,   -1,   -1,  267,  268,  269,   -1,   -1,  272,
  273,   -1,   -1,  276,  277,   -1,   -1,  257,   -1,   -1,
  260,  125,   -1,   -1,   -1,   31,  125,  267,  268,  269,
   36,    0,    1,  273,   22,    4,  276,  277,    7,   -1,
    9,   -1,   -1,  257,  125,   -1,  260,   -1,   17,   -1,
  125,   39,   21,   22,   23,  269,    0,    1,  272,  273,
    4,   -1,   31,    7,   52,    9,   -1,   36,   56,   -1,
   -1,   40,  125,   -1,   80,   -1,   -1,   21,   -1,   23,
   49,   -1,   51,  257,   90,   54,  260,   31,   -1,   -1,
  257,   79,   36,  260,   -1,  269,   40,  125,  272,  273,
  267,   -1,  269,  125,   -1,   -1,  273,   -1,   -1,  276,
   54,   80,   -1,   -1,  257,   -1,   -1,  260,   -1,  262,
  125,   90,   -1,  111,   -1,  131,  269,   -1,  116,   -1,
  273,  257,  257,   -1,  260,  260,   80,   -1,   -1,   -1,
   -1,  267,  268,  269,  269,   -1,   90,  273,  273,   -1,
  276,  277,   -1,  257,   -1,   -1,  260,   -1,  257,   -1,
   -1,  260,  131,  267,  268,  269,   -1,   -1,  267,  273,
  269,  177,  276,  277,  273,   -1,  257,  276,  277,  260,
  186,  187,  257,   -1,  190,  260,  267,  131,  269,   -1,
  257,   -1,  273,  260,  269,  276,  277,  272,  273,   -1,
  267,  268,  269,   -1,  257,   -1,  273,  260,  177,  276,
  198,  180,   -1,   -1,  267,   -1,  269,  186,  187,  188,
  273,  190,   -1,  276,  277,   -1,   -1,   -1,   -1,  257,
   -1,   -1,  260,  177,   -1,  257,  180,   -1,  260,  267,
   -1,  269,  186,  187,  188,  273,  190,  269,  276,  277,
  272,  273,  257,   -1,   -1,  260,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,  269,   -1,   -1,   -1,  273,
};
}
final static short YYFINAL=8;
final static short YYMAXTOKEN=278;
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
"FLECHA","PRINT","CADENAML","TRUNC","LAMBDA","EOF","UMINUS",
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

//#line 194 "gramatica3.y"

private void yyerror(String s) {
    System.out.println("Error: " + s);
}

public Parser(Map<String, Simbolo> tablaSimbolos, String path)
{
  this.tablaSimbolos = tablaSimbolos;
  this.lexer = new Lexico(tablaSimbolos);
  this.lexer.cargarBuffer(path);
}
//#line 598 "Parser.java"
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
        yychar = yylex();  //get next token
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
//#line 19 "gramatica3.y"
{System.out.println("Error sintáctico en línea " + val_peek(3).ival + ": falta el nombre del programa");}
break;
case 3:
//#line 20 "gramatica3.y"
{System.out.println("Error sintáctico en línea " + val_peek(1).ival + ": falta la llave de cierre del cuerpo del programa");}
break;
case 4:
//#line 21 "gramatica3.y"
{System.out.println("Error sintáctico en línea " + val_peek(3).ival + ": falta la llave de apertura del cuerpo del programa");}
break;
case 5:
//#line 22 "gramatica3.y"
{System.out.println("Error sintáctico en línea " + val_peek(1).ival + ": faltaN ambas llaves en cuerpo del programa");}
break;
case 6:
//#line 23 "gramatica3.y"
{System.out.println("Error sintáctico en línea " + val_peek(1).ival + ": falta el nombre del programa y la llave de cierre del cuerpo del programa");}
break;
case 7:
//#line 24 "gramatica3.y"
{System.out.println("Error sintáctico en línea " + val_peek(2).ival + ": falta el nombre del programa y la llave de apertura del cuerpo del programa");}
break;
case 8:
//#line 25 "gramatica3.y"
{System.out.println("Error sintáctico en línea " + val_peek(1).ival + ": falta el nombre del programa y ambas llaves en cuerpo del programa");}
break;
case 11:
//#line 32 "gramatica3.y"
{System.out.println("Se detectó una sentenciaejecutable ;");}
break;
case 12:
//#line 33 "gramatica3.y"
{System.out.println("Se detectó una declaración ;");}
break;
case 13:
//#line 34 "gramatica3.y"
{System.out.println("Error sintáctico en línea " + val_peek(0).ival + ": falta ; al final de la sentencia ejecutable");}
break;
case 14:
//#line 35 "gramatica3.y"
{System.out.println("Error sintáctico en línea " + val_peek(0).ival + ": falta ; al final de la declaración");}
break;
case 17:
//#line 40 "gramatica3.y"
{System.out.println("Error sintáctico en línea " + val_peek(0).ival + ": falta ; al final de la sentencia ejecutable");}
break;
case 18:
//#line 41 "gramatica3.y"
{System.out.println("Error sintáctico en línea " + val_peek(0).ival + ": falta ; al final de la sentencia ejecutable");}
break;
case 22:
//#line 48 "gramatica3.y"
{System.out.println("Se detectó un print");}
break;
case 23:
//#line 51 "gramatica3.y"
{System.out.println("Se detectó una declaración de variables");}
break;
case 24:
//#line 52 "gramatica3.y"
{System.out.println("Se detectó una declaración de función");}
break;
case 25:
//#line 55 "gramatica3.y"
{System.out.println("Se detectó una asignación simple");}
break;
case 26:
//#line 56 "gramatica3.y"
{System.out.println("Se detectó una asignación múltiple");}
break;
case 30:
//#line 64 "gramatica3.y"
{System.out.println("Se detectó un bloque IF-ELSE");}
break;
case 31:
//#line 65 "gramatica3.y"
{System.out.println("Se detectó un bloque IF");}
break;
case 32:
//#line 66 "gramatica3.y"
{System.out.println("Se detectó un bloque DO UNTIL");}
break;
case 33:
//#line 68 "gramatica3.y"
{System.out.println("Error sintáctico en línea " + val_peek(0).ival + ": falta la palabra ENDIF al final del bloque IF");}
break;
case 34:
//#line 69 "gramatica3.y"
{System.out.println("Error sintáctico en línea " + val_peek(0).ival + ": falta la palabra ENDIF al final del bloque IF");}
break;
case 35:
//#line 70 "gramatica3.y"
{System.out.println("Error sintáctico en línea " + val_peek(1).ival + ": falta el cuerpo en el bloque ELSE");}
break;
case 36:
//#line 71 "gramatica3.y"
{System.out.println("Error sintáctico en línea " + val_peek(3).ival + ": falta el cuerpo en el bloque IF");}
break;
case 37:
//#line 72 "gramatica3.y"
{System.out.println("Error sintáctico en línea " + val_peek(1).ival + ": falta el cuerpo en el bloque IF");}
break;
case 38:
//#line 73 "gramatica3.y"
{System.out.println("Error sintáctico en línea " + val_peek(1).ival + ": falta el cuerpo en los bloques IF y ELSE");}
break;
case 39:
//#line 75 "gramatica3.y"
{System.out.println("Error sintáctico en línea " + val_peek(2).ival + ": falta de cuerpo en iteracion");}
break;
case 40:
//#line 76 "gramatica3.y"
{System.out.println("Error sintáctico en línea " + val_peek(1).ival + ": falta la palabra UNTIL en la iteracion");}
break;
case 43:
//#line 81 "gramatica3.y"
{System.out.println("Error sintáctico en línea " + val_peek(0).ival + ": falta de argumento en sentencia print");}
break;
case 44:
//#line 84 "gramatica3.y"
{System.out.println("Se detectó una condición entre paréntesis");}
break;
case 45:
//#line 85 "gramatica3.y"
{System.out.println("Error sintáctico en línea " + val_peek(0).ival + ": falta el paréntesis de apertura de la condición");}
break;
case 46:
//#line 86 "gramatica3.y"
{System.out.println("Error sintáctico en línea " + val_peek(0).ival + ": falta el paréntesis de cierre de la condición");}
break;
case 47:
//#line 87 "gramatica3.y"
{System.out.println("Error sintáctico en línea " + val_peek(0).ival + ": faltan ambos paréntesis de la condición");}
break;
case 49:
//#line 91 "gramatica3.y"
{System.out.println("Error sintáctico en línea " + val_peek(0).ival + ": La condición es incompleta");}
break;
case 60:
//#line 108 "gramatica3.y"
{ System.out.println("Error sintáctico en línea " + val_peek(2).ival + ": falta ',' entre variables"); }
break;
case 63:
//#line 115 "gramatica3.y"
{System.out.println("Se detectó una función invocada correctamente");}
break;
case 71:
//#line 133 "gramatica3.y"
{System.out.println("Error sintáctico en línea " + val_peek(2).ival + ": falta el paréntesis de cierre de la función TRUNC");}
break;
case 72:
//#line 134 "gramatica3.y"
{System.out.println("Error sintáctico en línea " + val_peek(4).ival + ": falta el paréntesis de apertura de la función TRUNC");}
break;
case 73:
//#line 135 "gramatica3.y"
{System.out.println("Error sintáctico en línea " + val_peek(2).ival + ": faltan ambos paréntesis de la función TRUNC");}
break;
case 75:
//#line 137 "gramatica3.y"
{System.out.println("Error sintáctico en línea " + val_peek(2).ival + ": falta llave de cierre del cuerpo de la función Lambda");}
break;
case 76:
//#line 138 "gramatica3.y"
{System.out.println("Error sintáctico en línea " + val_peek(4).ival + ": falta llave de apertura del cuerpo de la función Lambda");}
break;
case 77:
//#line 139 "gramatica3.y"
{System.out.println("Error sintáctico en línea " + val_peek(3).ival + ": faltan ambas llaves del cuerpo de la función Lambda");}
break;
case 78:
//#line 140 "gramatica3.y"
{System.out.println("Error sintáctico en línea " + val_peek(0).ival + ": No se especifica el parametro formal al que corresponde el parámetro real");}
break;
case 79:
//#line 141 "gramatica3.y"
{System.out.println("Error sintáctico en línea " + val_peek(0).ival + ": No se especifica el parametro formal al que corresponde el parámetro real");}
break;
case 80:
//#line 142 "gramatica3.y"
{System.out.println("Error sintáctico en línea " + val_peek(0).ival + ": No se especifica el parametro formal al que corresponde el parámetro real");}
break;
case 85:
//#line 151 "gramatica3.y"
{System.out.println("Error sintáctico en línea " + val_peek(1).ival + ": falta el nombre del parámetro");}
break;
case 86:
//#line 152 "gramatica3.y"
{System.out.println("Error sintáctico en línea " + val_peek(0).ival + ": falta el nombre del parámetro");}
break;
case 87:
//#line 153 "gramatica3.y"
{System.out.println("Error sintáctico en línea " + val_peek(1).ival + ": falta el tipo del parámetro");}
break;
case 88:
//#line 154 "gramatica3.y"
{System.out.println("Error sintáctico en línea " + val_peek(0).ival + ": falta el tipo del parámetro");}
break;
case 91:
//#line 161 "gramatica3.y"
{ System.out.println("Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la izquierda de '+'"); }
break;
case 92:
//#line 162 "gramatica3.y"
{ System.out.println("Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la derecha de '+'"); }
break;
case 93:
//#line 163 "gramatica3.y"
{ System.out.println("Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la izquierda de '-'"); }
break;
case 94:
//#line 164 "gramatica3.y"
{ System.out.println("Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la derecha de '-'"); }
break;
case 98:
//#line 170 "gramatica3.y"
{ System.out.println("Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la izquierda de '*'"); }
break;
case 99:
//#line 171 "gramatica3.y"
{ System.out.println("Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la derecha de '*'"); }
break;
case 100:
//#line 172 "gramatica3.y"
{ System.out.println("Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la izquierda de '/'"); }
break;
case 101:
//#line 173 "gramatica3.y"
{ System.out.println("Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la derecha de '/'"); }
break;
case 102:
//#line 174 "gramatica3.y"
{ System.out.println("Error sintáctico en línea " + val_peek(2).ival + ": falta operador entre operandos o falta de comparador en condición"); }
break;
case 112:
//#line 188 "gramatica3.y"
{ System.out.println("Error sintáctico en línea " + val_peek(1).ival + ": falta ',' entre constantes"); }
break;
case 113:
//#line 189 "gramatica3.y"
{ System.out.println("Error sintáctico en línea " + val_peek(2).ival + ": falta ',' entre constantes"); }
break;
//#line 991 "Parser.java"
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
        yychar = yylex();        //get next character
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
