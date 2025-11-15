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
import java.util.Stack;
import java.util.HashMap;
    import java.nio.file.Path;
    import java.nio.file.Paths;
    import java.nio.file.Files;
    import java.nio.file.StandardOpenOption;
    import java.util.ArrayList;

//#line 28 "Parser.java"




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
int returnFunc = 1;
Stack<Boolean> huboRetStack = new Stack<>();
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
    3,    3,    3,    3,    3,    4,   14,    4,    6,    6,
   18,   18,   18,    7,    7,    7,    7,    7,    7,    7,
    7,    7,    7,    7,    9,    9,    9,   19,   19,   19,
   19,   20,   20,   21,   21,   21,   21,   21,   21,   11,
   11,   12,   12,   16,   16,    8,   10,   10,   10,   22,
   23,   23,   24,   24,   24,   24,   24,   24,   24,   24,
   24,   24,   24,   24,   13,   13,   25,   25,   25,   25,
   25,   25,   15,   15,   15,   15,   15,   15,   15,   26,
   26,   26,   26,   26,   26,   26,   26,   27,   27,   27,
   27,   17,   17,   17,   17,   17,   17,
};
final static short yylen[] = {                            2,
    5,    4,    4,    4,    4,    3,    3,    3,    2,    7,
    2,    1,    2,    2,    1,    1,    3,    2,    2,    1,
    1,    1,    1,    1,    1,    2,    0,   14,    3,    3,
    3,    2,    2,    6,    4,    4,    5,    3,    5,    5,
    3,    4,    3,    3,    4,    4,    3,    3,    2,    2,
    1,    3,    1,    1,    1,    1,    1,    1,    1,    1,
    1,    3,    1,    1,    3,    4,    4,    4,    5,    1,
    3,    1,    3,    6,    5,    5,    4,    9,    8,    8,
    7,    8,    2,    5,    3,    1,    3,    2,    2,    1,
    2,    1,    3,    3,    3,    3,    3,    3,    1,    3,
    3,    3,    3,    3,    3,    3,    1,    1,    2,    1,
    1,    3,    4,    1,    2,    2,    3,
};
final static short yydefred[] = {                         0,
    0,    0,   60,    0,    0,   61,    0,    0,    0,   12,
    0,    0,   21,   22,   23,   24,   25,    0,    0,    0,
    0,    0,    0,    0,    0,    0,  108,    0,    0,  111,
    0,    0,    0,    0,    0,  107,    0,    0,    0,    0,
    0,    0,    9,    0,   11,   13,   14,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,   72,    6,    0,    0,    0,
    0,    0,    0,    0,  109,   56,   57,   58,   59,   54,
   55,    0,    0,    0,    0,   41,    0,   49,    0,    0,
    0,   43,   33,    0,    0,   32,    0,   44,    0,   47,
    0,    7,    0,    8,    0,  114,    0,    0,   64,    0,
    0,   65,    0,    2,    4,    0,   68,    0,    0,    0,
    0,    0,   67,   66,    0,    5,    0,    0,    0,  102,
  104,   48,    0,    0,    0,    0,    0,   42,    0,    0,
   35,  106,  103,  100,  105,  101,   18,   31,    0,   36,
   46,   45,    3,   92,    0,    0,    0,   86,  115,  116,
    0,    0,    0,    1,    0,    0,    0,    0,   69,   73,
   71,   40,   39,    0,   17,   91,    0,   88,   27,    0,
  112,    0,  117,    0,    0,    0,   77,    0,    0,   34,
   87,    0,   85,  113,   10,   75,    0,   76,    0,    0,
    0,   74,    0,    0,    0,    0,    0,    0,   81,    0,
    0,   79,    0,   80,    0,   78,    0,    0,    0,   28,
};
final static short yydgoto[] = {                          8,
    9,   10,   11,   12,   95,   13,   14,   30,   16,   17,
   18,   19,  157,  192,   31,   20,  108,   40,   33,   34,
   84,   64,   65,   66,  158,   35,   36,
};
final static short yysindex[] = {                       542,
  158,  122,    0,  693,  -37,    0, -177,    0,  714,    0,
  -52,  -45,    0,    0,    0,    0,    0, -226,   11,  -26,
   38,  664,   98,  725,   34,   47,    0,  284, -192,    0,
  393,   13,  594,   60,   -5,    0,  122,  485,   46,  163,
  107,  739,    0, -153,    0,    0,    0,  102,   92,   13,
   15, -110,  284,  -97,  106, -102,  750,  136,  189, -231,
  -80,  -16,   54,  138,  137,    0,    0,  -95,  296,  296,
    4,    4,  101,  139,    0,    0,    0,    0,    0,    0,
    0,  299,  390,  284,  632,    0, -106,    0,    4,  404,
  411,    0,    0,  127,  511,    0,  122,    0,  153,    0,
   77,    0,  -78,    0, -155,    0,  -58,   -6,    0,   13,
   74,    0, -177,    0,    0,  -76,    0,  284,  -33,  -55,
  164,  -50,    0,    0,  101,    0,   56,   -5,   -5,    0,
    0,    0,   56,   -5,   56,   -5,   74,    0,  -49,  772,
    0,    0,    0,    0,    0,    0,    0,    0,  159,    0,
    0,    0,    0,    0, -123,  -40,    6,    0,    0,    0,
   19,   24,  220,    0,  -32,   27,   32,  244,    0,    0,
    0,    0,    0,   44,    0,    0,   50,    0,    0, -155,
    0,   61,    0,   45,   66,   79,    0,   69,  289,    0,
    0,  205,    0,    0,    0,    0,  104,    0, -199,  771,
 -177,    0,  779,  111,   81,  191,  118,   99,    0,  126,
  309,    0,  129,    0,  284,    0,   90,  336,  275,    0,
};
final static short yyrindex[] = {                         0,
  -29,    0,    0,    0,    0,    0,    0,    0,    0,    0,
  642,  688,    0,    0,    0,    0,    0,    0,    0,   12,
  -29,    0,    0,    0,    0,  -41,    0,    0,    0,    0,
  237,  -19,    0,  414,    3,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  130,  567,  215,
    0,    0,    0,    0,    0,    0,    0,  121,    0,    0,
    0,    0,  145,    0,  361,    0,    0,    0,    0,    0,
    0,    0,    0,  436,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,  463,    0,    0,    0,
    0,    0,    0,  798,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  484,    0,  365,
  506,    0,    0,    0,    0,    0,    0,    0,    0,    0,
  167,   53,    0,    0,    0,    0,    0,   26,   48,    0,
    0,    0,   70,  307,  329,  351,  259,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,  803,    0,
    0,    0,    0,    0,    0,   96,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,  528,    0,    0,  113,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,  117,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,  128,    0,    0,    0,    0,    0,    0,    0,
};
final static short yygindex[] = {                         0,
  184,  509,  743,    0, -164,    0,    0,  884,    0,    0,
  -30,  386,    0,    0,   86,  789,    0,  -17,   17,  377,
    0,    0,    0,  283,  234,  114,  -38,
};
final static int YYTABLESIZE=1090;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         64,
   64,   64,   41,   64,   64,   64,   46,  167,  186,   83,
   83,   82,   82,   47,   64,   87,   64,   64,   64,   54,
   64,  110,  110,  110,  200,  110,   83,  110,   82,  120,
   48,   64,  130,  131,  203,    3,   90,  161,  162,  110,
  110,   91,  110,   99,    6,   99,  179,   99,   29,  180,
  142,  144,  146,   92,   52,   63,   98,   21,   54,  107,
    2,   99,   99,  182,   99,   75,   97,  139,   97,    4,
   97,   51,   63,    5,  156,   71,   70,   23,   69,   21,
   72,   64,    2,   64,   97,   97,   73,   97,   95,    3,
   95,    4,   95,   83,  123,    5,   83,   71,    6,   54,
   88,  154,   72,  110,   96,  110,   95,   95,   62,   95,
   98,    3,   98,  150,   98,  155,   83,  152,   82,   83,
    6,   82,  174,  104,  177,   99,  101,   99,   98,   98,
  218,   98,   83,  176,   82,   52,   90,   60,  111,   90,
   60,  105,   61,    3,  119,   29,  109,  100,   97,  156,
   97,   29,    6,   89,  140,  141,   89,   84,   62,  112,
   84,   28,  108,  108,  113,  108,   29,  108,   82,  137,
   95,   82,   95,   64,  114,   64,  117,  121,  124,  132,
  125,  126,  128,  129,   24,  147,  110,  110,   64,  110,
   42,  110,   98,  151,   98,  134,  136,   23,  153,  159,
  164,  168,   28,  165,  169,   57,  170,   29,  109,  109,
   62,  109,  172,  109,   64,   64,  178,  175,   64,   64,
   64,   64,   64,   64,   64,   64,   64,   64,  118,   64,
   64,   64,   53,   29,   64,   64,  110,  110,  166,  185,
  110,  110,  110,  110,  110,  110,  110,  110,  110,  110,
   89,  160,  110,  110,   64,  122,  110,  110,   63,   99,
   26,   27,   99,   99,   99,   99,   99,   99,   99,   99,
   99,   99,  106,   63,   99,   99,  181,   53,   99,   99,
   22,  183,   97,  187,  189,   97,   97,   97,   97,   97,
   97,   97,   97,   97,   97,   53,  163,   97,   97,   52,
  217,   97,   97,  188,   95,  190,  191,   95,   95,   95,
   95,   95,   95,   95,   95,   95,   95,   52,  194,   95,
   95,  195,  196,   95,   95,  198,   98,  201,   29,   98,
   98,   98,   98,   98,   98,   98,   98,   98,   98,   63,
   29,   98,   98,   29,  184,   98,   98,   94,  215,   94,
  197,   94,  210,   25,   26,   58,   25,   26,   27,   53,
  202,   53,   25,   26,   27,   94,   94,  209,   94,   96,
  213,   96,   59,   96,  212,   59,  108,   25,   26,   27,
   99,   52,  214,   52,  206,  216,   64,   96,   96,   64,
   96,   93,  108,   93,  219,   93,   64,   64,   64,  220,
  110,   70,   64,   49,   74,   64,   64,  171,   62,   93,
   93,  199,   93,  193,   21,    0,  110,    2,   25,   26,
   27,    0,  109,   62,    3,   62,    4,    0,    0,   94,
    5,   94,   97,    6,   29,   83,    0,   82,  109,    0,
    0,    0,    0,    0,   25,   26,   27,   21,   29,    0,
    2,   96,   81,   96,   80,   29,    0,    3,  211,    4,
    0,    0,    0,    5,    0,    0,    6,    0,    0,    0,
    0,   63,   51,   93,   63,   93,   21,    0,    0,    2,
    0,   63,   63,   63,    0,    0,    3,   63,    4,   62,
   63,   63,    5,   53,   50,    6,   53,   53,   53,    0,
    0,    0,    0,   53,   53,   53,    0,    0,   53,   53,
    0,    0,   53,   53,    0,   52,    0,   45,   52,   52,
   52,   38,    0,    0,    0,   52,   52,   52,    0,    0,
   52,   52,   45,    0,   52,   52,   51,    0,   51,   25,
   26,   27,   30,    0,    0,   21,    0,    0,    2,    0,
   45,  127,   26,   27,  133,   26,   27,    4,   50,    0,
   50,    5,    0,   94,   29,   45,   94,   94,   94,   94,
   94,   94,   94,   94,   94,   94,    0,    0,   94,   94,
    0,    0,   94,   94,    0,   96,   37,   38,   96,   96,
   96,   96,   96,   96,   96,   96,   96,   96,    0,    0,
   96,   96,    0,    0,   96,   96,    0,   93,   30,   93,
   93,   93,   93,   93,   93,   93,   93,   93,   93,   93,
    0,   62,   93,   93,   62,   26,   93,   93,    0,    0,
   29,   62,   62,   62,    0,  148,    0,   62,    0,    0,
   62,   62,    0,    0,    0,  135,   26,   27,    0,    0,
    0,    0,   37,    0,    0,   76,   77,   78,   79,  143,
   26,   27,    0,    0,    7,    0,  145,   26,   27,    0,
   51,   45,    0,   51,   51,   51,    0,    0,    0,    0,
   51,   51,   51,    0,    0,   51,   51,    0,    0,   51,
   51,   26,   50,    0,    0,   50,   50,   50,    0,    0,
    0,    0,   50,   50,   50,    0,    0,   50,   50,    0,
    0,   50,   50,    0,   45,    0,   38,    0,    0,   38,
    0,    0,   38,    0,    0,    0,    0,    0,    0,   38,
   38,   38,    0,    0,   38,   38,    0,    0,   38,   38,
   30,   21,    0,   30,    2,    0,   39,    0,    0,    0,
   30,   30,   30,    4,   38,   30,   30,    5,    0,   30,
   30,    0,   29,    0,    0,   29,   15,   21,    0,    0,
    2,    0,   29,   29,   29,   39,    0,   29,   29,    4,
   94,   29,   29,    5,   37,    0,    0,   37,   56,    0,
   32,    0,    0,    0,   37,   37,   37,    0,    1,   37,
   37,    2,    0,   37,   37,    0,   50,    0,    3,    0,
    4,   63,   16,    0,    5,   38,   32,    6,    0,    0,
    0,    0,    0,   26,    0,   32,   26,   39,   32,   32,
    0,    0,    0,   26,   26,   26,    0,  149,   44,   26,
  110,   32,   26,   26,    0,    0,    0,   32,    0,   68,
   21,    0,    0,    2,   85,   86,    0,   32,   32,   32,
   32,   32,    4,  103,    0,    0,    5,    0,    0,    0,
   32,   32,   32,    0,  116,    0,    0,   32,   32,   32,
    0,    0,   39,   15,   15,   32,    0,   15,   21,    0,
   15,    2,   15,  138,   38,  205,    0,    0,   15,    0,
    4,   15,    0,  208,    5,   15,   32,   15,   15,   15,
   15,    0,    0,   32,   15,    0,   15,   15,   15,   55,
   21,   15,   20,    2,    0,   15,    0,   19,    0,    0,
    3,   94,    4,    0,    0,    0,    5,    0,    0,    6,
   15,   94,  149,    0,   16,  149,    0,   16,    0,   21,
    0,    0,    2,    0,   16,   16,   16,    0,    0,    0,
   16,    4,   37,   16,   16,    5,    0,    0,   15,    0,
   21,    0,    0,    2,    0,    0,    0,    0,   15,    0,
    3,   21,    4,    0,    2,    0,    5,    0,    0,    6,
   43,    3,    0,    4,    0,   21,   15,    5,    2,    0,
    6,   67,    0,   32,    0,    3,   21,    4,    0,    2,
    0,    5,    0,    0,    6,  102,    3,    0,    4,    0,
    0,    0,    5,   15,    0,    6,  115,   21,   21,    0,
    2,    2,    0,  173,    0,   21,    0,    0,    2,    4,
    4,    0,  204,    5,    5,    0,   15,    4,    0,    0,
  207,    5,    0,    0,   20,    0,    0,   20,    0,   19,
    0,    0,   19,    0,    0,    0,   20,    0,    0,   20,
   20,   19,   15,    0,   19,   19,    0,    0,    0,    0,
    0,    0,   15,   15,   15,    0,   15,    0,    0,   15,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         41,
   42,   43,   40,   45,   46,   47,   59,   41,   41,   43,
   43,   45,   45,   59,   44,   33,   46,   59,   60,   46,
   62,   41,   42,   43,  189,   45,   43,   47,   45,   60,
  257,   61,   71,   72,  199,  267,   42,   44,   45,   59,
   60,   47,   62,   41,  276,   43,   41,   45,   45,   44,
   89,   90,   91,   37,   44,   44,   40,  257,   46,   45,
  260,   59,   60,   45,   62,  258,   41,   85,   43,  269,
   45,   61,   61,  273,  105,   42,   43,   40,   45,  257,
   47,  123,  260,  125,   59,   60,   40,   62,   41,  267,
   43,  269,   45,   41,   41,  273,   44,   42,  276,   46,
   41,  257,   47,  123,   59,  125,   59,   60,   23,   62,
   41,  267,   43,   97,   45,  271,   43,   41,   45,   43,
  276,   45,  140,  277,  155,  123,   41,  125,   59,   60,
   41,   62,   43,  257,   45,   44,   41,   40,   53,   44,
   40,   40,   45,  267,   59,   45,  257,   41,  123,  180,
  125,   45,  276,   41,  261,  262,   44,   41,   73,  257,
   44,   40,   42,   43,   59,   45,   45,   47,   41,   84,
  123,   44,  125,   44,  277,   46,   41,  258,   41,   41,
   44,  277,   69,   70,    1,   59,   42,   43,   59,   45,
    7,   47,  123,   41,  125,   82,   83,   40,  277,  258,
  277,  257,   40,  118,   41,   22,  257,   45,   42,   43,
  125,   45,  262,   47,  256,  257,  257,   59,  260,  261,
  262,  263,  264,  265,  266,  267,  268,  269,   40,  259,
  272,  273,  259,   45,  276,  277,  256,  257,  272,  272,
  260,  261,  262,  263,  264,  265,  266,  267,  268,  269,
  256,  258,  272,  273,  125,  272,  276,  277,   44,  257,
  257,  258,  260,  261,  262,  263,  264,  265,  266,  267,
  268,  269,  258,   59,  272,  273,  258,   41,  276,  277,
  123,  258,  257,  257,   41,  260,  261,  262,  263,  264,
  265,  266,  267,  268,  269,   59,  113,  272,  273,   41,
  215,  276,  277,  272,  257,  262,  257,  260,  261,  262,
  263,  264,  265,  266,  267,  268,  269,   59,  258,  272,
  273,  277,  257,  276,  277,  257,  257,  123,   45,  260,
  261,  262,  263,  264,  265,  266,  267,  268,  269,  125,
   45,  272,  273,   45,  125,  276,  277,   41,   40,   43,
  272,   45,  272,  256,  257,  258,  256,  257,  258,  123,
  257,  125,  256,  257,  258,   59,   60,  257,   62,   41,
  272,   43,  275,   45,  257,  275,  256,  256,  257,  258,
  274,  123,  257,  125,  201,  257,  257,   59,   60,  260,
   62,   41,  272,   43,   59,   45,  267,  268,  269,  125,
  256,   41,  273,   18,   28,  276,  277,  125,   44,   59,
   60,  123,   62,  180,  257,   -1,  272,  260,  256,  257,
  258,   -1,  256,   59,  267,   61,  269,   -1,   -1,  123,
  273,  125,  270,  276,   45,   43,   -1,   45,  272,   -1,
   -1,   -1,   -1,   -1,  256,  257,  258,  257,   45,   -1,
  260,  123,   60,  125,   62,   45,   -1,  267,  268,  269,
   -1,   -1,   -1,  273,   -1,   -1,  276,   -1,   -1,   -1,
   -1,  257,   59,  123,  260,  125,  257,   -1,   -1,  260,
   -1,  267,  268,  269,   -1,   -1,  267,  273,  269,  125,
  276,  277,  273,  257,   59,  276,  260,  261,  262,   -1,
   -1,   -1,   -1,  267,  268,  269,   -1,   -1,  272,  273,
   -1,   -1,  276,  277,   -1,  257,   -1,    9,  260,  261,
  262,   59,   -1,   -1,   -1,  267,  268,  269,   -1,   -1,
  272,  273,   24,   -1,  276,  277,  123,   -1,  125,  256,
  257,  258,   59,   -1,   -1,  257,   -1,   -1,  260,   -1,
   42,  256,  257,  258,  256,  257,  258,  269,  123,   -1,
  125,  273,   -1,  257,   59,   57,  260,  261,  262,  263,
  264,  265,  266,  267,  268,  269,   -1,   -1,  272,  273,
   -1,   -1,  276,  277,   -1,  257,   59,  125,  260,  261,
  262,  263,  264,  265,  266,  267,  268,  269,   -1,   -1,
  272,  273,   -1,   -1,  276,  277,   -1,  257,  125,  125,
  260,  261,  262,  263,  264,  265,  266,  267,  268,  269,
   -1,  257,  272,  273,  260,   59,  276,  277,   -1,   -1,
  125,  267,  268,  269,   -1,  125,   -1,  273,   -1,   -1,
  276,  277,   -1,   -1,   -1,  256,  257,  258,   -1,   -1,
   -1,   -1,  125,   -1,   -1,  263,  264,  265,  266,  256,
  257,  258,   -1,   -1,  123,   -1,  256,  257,  258,   -1,
  257,  163,   -1,  260,  261,  262,   -1,   -1,   -1,   -1,
  267,  268,  269,   -1,   -1,  272,  273,   -1,   -1,  276,
  277,  125,  257,   -1,   -1,  260,  261,  262,   -1,   -1,
   -1,   -1,  267,  268,  269,   -1,   -1,  272,  273,   -1,
   -1,  276,  277,   -1,  206,   -1,  123,   -1,   -1,  257,
   -1,   -1,  260,   -1,   -1,   -1,   -1,   -1,   -1,  267,
  268,  269,   -1,   -1,  272,  273,   -1,   -1,  276,  277,
  257,  257,   -1,  260,  260,   -1,    4,   -1,   -1,   -1,
  267,  268,  269,  269,  123,  272,  273,  273,   -1,  276,
  277,   -1,  257,   -1,   -1,  260,  125,  257,   -1,   -1,
  260,   -1,  267,  268,  269,   33,   -1,  272,  273,  269,
   38,  276,  277,  273,  257,   -1,   -1,  260,  125,   -1,
    2,   -1,   -1,   -1,  267,  268,  269,   -1,  257,  272,
  273,  260,   -1,  276,  277,   -1,   18,   -1,  267,   -1,
  269,   23,  125,   -1,  273,  123,   28,  276,   -1,   -1,
   -1,   -1,   -1,  257,   -1,   37,  260,   85,   40,   41,
   -1,   -1,   -1,  267,  268,  269,   -1,   95,  125,  273,
   52,   53,  276,  277,   -1,   -1,   -1,   59,   -1,  125,
  257,   -1,   -1,  260,  261,  262,   -1,   69,   70,   71,
   72,   73,  269,  125,   -1,   -1,  273,   -1,   -1,   -1,
   82,   83,   84,   -1,  125,   -1,   -1,   89,   90,   91,
   -1,   -1,  140,    0,    1,   97,   -1,    4,  257,   -1,
    7,  260,    9,  262,  123,  125,   -1,   -1,  257,   -1,
  269,  260,   -1,  125,  273,   22,  118,   24,  267,  268,
  269,   -1,   -1,  125,  273,   -1,   33,  276,  277,  256,
  257,   38,  125,  260,   -1,   42,   -1,  125,   -1,   -1,
  267,  189,  269,   -1,   -1,   -1,  273,   -1,   -1,  276,
   57,  199,  200,   -1,  257,  203,   -1,  260,   -1,  257,
   -1,   -1,  260,   -1,  267,  268,  269,   -1,   -1,   -1,
  273,  269,  270,  276,  277,  273,   -1,   -1,   85,   -1,
  257,   -1,   -1,  260,   -1,   -1,   -1,   -1,   95,   -1,
  267,  257,  269,   -1,  260,   -1,  273,   -1,   -1,  276,
  277,  267,   -1,  269,   -1,  257,  113,  273,  260,   -1,
  276,  277,   -1,  215,   -1,  267,  257,  269,   -1,  260,
   -1,  273,   -1,   -1,  276,  277,  267,   -1,  269,   -1,
   -1,   -1,  273,  140,   -1,  276,  277,  257,  257,   -1,
  260,  260,   -1,  262,   -1,  257,   -1,   -1,  260,  269,
  269,   -1,  272,  273,  273,   -1,  163,  269,   -1,   -1,
  272,  273,   -1,   -1,  257,   -1,   -1,  260,   -1,  257,
   -1,   -1,  260,   -1,   -1,   -1,  269,   -1,   -1,  272,
  273,  269,  189,   -1,  272,  273,   -1,   -1,   -1,   -1,
   -1,   -1,  199,  200,  201,   -1,  203,   -1,   -1,  206,
};
}
final static short YYFINAL=8;
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
"declaracion : tipo listavariables",
"$$1 :",
"declaracion : tipo ID '(' listaparametros ')' $$1 '{' listasentencias RETURN '(' expresion ')' ';' '}'",
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

//#line 195 "gramatica.y"

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
//#line 652 "Parser.java"
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
//#line 19 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(3).ival + ": falta el nombre del programa");}
break;
case 4:
//#line 20 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta la llave de cierre del cuerpo del programa");}
break;
case 5:
//#line 21 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(3).ival + ": falta la llave de apertura del cuerpo del programa");}
break;
case 6:
//#line 22 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": faltan ambas llaves en cuerpo del programa");}
break;
case 7:
//#line 23 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el nombre del programa y la llave de cierre del cuerpo del programa");}
break;
case 8:
//#line 24 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(2).ival + ": falta el nombre del programa y la llave de apertura del cuerpo del programa");}
break;
case 9:
//#line 25 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el nombre del programa y ambas llaves en cuerpo del programa");}
break;
case 10:
//#line 26 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico: el programa no es válido");}
break;
case 15:
//#line 37 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta ; al final de la sentencia ejecutable");}
break;
case 16:
//#line 38 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta ; al final de la declaración");}
break;
case 19:
//#line 43 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta ; al final de la sentencia ejecutable");}
break;
case 20:
//#line 44 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta ; al final de la sentencia ejecutable");}
break;
case 24:
//#line 50 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un print");}
break;
case 25:
//#line 51 "gramatica.y"
{if(funcionActiva==0) erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": Invocacion de funcion lambda fuera de una función");}
break;
case 26:
//#line 55 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó una declaración de variables");}
break;
case 27:
//#line 56 "gramatica.y"
{funcionActiva++;}
break;
case 28:
//#line 56 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(1).ival + ": Se detectó una declaración de función"); funcionActiva--;}
break;
case 29:
//#line 59 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó una asignación simple");}
break;
case 30:
//#line 60 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó una asignación múltiple");}
break;
case 34:
//#line 68 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un bloque IF-ELSE");}
break;
case 35:
//#line 69 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un bloque IF");}
break;
case 36:
//#line 70 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un bloque DO UNTIL");}
break;
case 37:
//#line 72 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta la palabra ENDIF al final del bloque IF-ELSE");}
break;
case 38:
//#line 73 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta la palabra ENDIF al final del bloque IF");}
break;
case 39:
//#line 74 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el cuerpo en el bloque ELSE");}
break;
case 40:
//#line 75 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(3).ival + ": falta el cuerpo en el bloque IF");}
break;
case 41:
//#line 76 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el cuerpo en el bloque IF");}
break;
case 42:
//#line 77 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el cuerpo en los bloques IF y ELSE");}
break;
case 43:
//#line 79 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(2).ival + ": falta de cuerpo en iteracion");}
break;
case 44:
//#line 80 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta la palabra UNTIL en la iteracion");}
break;
case 47:
//#line 85 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta de argumento en sentencia print");}
break;
case 49:
//#line 89 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta el paréntesis de apertura de la condición");}
break;
case 50:
//#line 90 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta el paréntesis de cierre de la condición");}
break;
case 51:
//#line 91 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": faltan ambos paréntesis de la condición");}
break;
case 53:
//#line 95 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": La condición es incompleta");}
break;
case 66:
//#line 119 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó una función invocada correctamente");}
break;
case 68:
//#line 123 "gramatica.y"
{ constantePositiva(val_peek(1)); }
break;
case 69:
//#line 124 "gramatica.y"
{ constanteNegativa(val_peek(1)); }
break;
case 75:
//#line 136 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(2).ival + ": falta el paréntesis de cierre de la función TRUNC");}
break;
case 76:
//#line 137 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(4).ival + ": falta el paréntesis de apertura de la función TRUNC");}
break;
case 77:
//#line 138 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(2).ival + ": faltan ambos paréntesis de la función TRUNC");}
break;
case 79:
//#line 140 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(2).ival + ": falta llave de cierre del cuerpo de la función Lambda");}
break;
case 80:
//#line 141 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(4).ival + ": falta llave de apertura del cuerpo de la función Lambda");}
break;
case 81:
//#line 142 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(3).ival + ": faltan ambas llaves del cuerpo de la función Lambda");}
break;
case 82:
//#line 143 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": No se especifica el parametro formal al que corresponde el parámetro real");}
break;
case 83:
//#line 144 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": No se especifica el parametro formal al que corresponde el parámetro real");}
break;
case 84:
//#line 145 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": No se especifica el parametro formal al que corresponde el parámetro real");}
break;
case 89:
//#line 154 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el nombre del parámetro");}
break;
case 90:
//#line 155 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta el nombre del parámetro");}
break;
case 91:
//#line 156 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el tipo del parámetro");}
break;
case 92:
//#line 157 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta el tipo del parámetro");}
break;
case 95:
//#line 162 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la izquierda de '+'"); }
break;
case 96:
//#line 163 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la derecha de '+'"); }
break;
case 97:
//#line 164 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la izquierda de '-'"); }
break;
case 98:
//#line 165 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la derecha de '-'"); }
break;
case 102:
//#line 171 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la izquierda de '*'"); }
break;
case 103:
//#line 172 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la derecha de '*'"); }
break;
case 104:
//#line 173 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la izquierda de '/'"); }
break;
case 105:
//#line 174 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la derecha de '/'"); }
break;
case 106:
//#line 175 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(2).ival + ": falta operador entre operandos o falta de comparador en condición"); }
break;
case 108:
//#line 179 "gramatica.y"
{ constantePositiva(val_peek(0)); }
break;
case 109:
//#line 180 "gramatica.y"
{ constanteNegativa(val_peek(0)); }
break;
case 112:
//#line 185 "gramatica.y"
{ constantePositiva(val_peek(0)); }
break;
case 113:
//#line 186 "gramatica.y"
{ constanteNegativa(val_peek(0)); }
break;
case 114:
//#line 187 "gramatica.y"
{ constantePositiva(val_peek(0)); }
break;
case 115:
//#line 188 "gramatica.y"
{ constanteNegativa(val_peek(0)); }
break;
case 116:
//#line 189 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta ',' entre constantes"); constantePositiva(val_peek(0)); }
break;
case 117:
//#line 190 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(2).ival + ": falta ',' entre constantes"); constanteNegativa(val_peek(0)); }
break;
//#line 1073 "Parser.java"
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
