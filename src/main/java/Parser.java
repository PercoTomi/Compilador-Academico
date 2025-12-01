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
    import Simbolos.Auxiliares.*;
    import java.util.Map;
    import java.util.HashMap;
    import java.nio.file.Path;
    import java.nio.file.Paths;
    import java.nio.file.Files;
    import java.nio.file.StandardOpenOption;
    import java.util.ArrayList;
    import java.util.Stack;

//#line 30 "Parser.java"




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
public final static short LOWER_THAN_ID=278;
public final static short YYERRCODE=256;
final static short yylhs[] = {                           -1,
    2,    3,    0,    0,    1,    1,    1,    1,    1,    4,
    4,    5,    5,    5,    5,    8,    8,    8,    8,    6,
    6,    6,    6,    6,    6,    7,   19,   20,    7,   18,
   18,   21,   21,   22,   22,   22,   22,   22,   22,   17,
   14,   14,   14,   14,   14,    9,    9,    9,   27,   27,
   27,   29,   28,   30,   32,   31,   10,   10,   10,   10,
   10,   10,   10,   10,   10,   10,   10,   12,   12,   12,
   12,   12,   34,   34,   33,   33,   33,   33,   35,   35,
   36,   36,   36,   36,   36,   36,   15,   15,   16,   16,
   25,   25,   24,   24,   11,   11,   13,   13,   13,   13,
   13,   39,   37,   38,   40,   40,   41,   41,   41,   41,
   41,   41,   41,   42,   42,   42,   23,   23,   23,   23,
   23,   23,   23,   43,   43,   43,   43,   43,   43,   43,
   43,   44,   44,   44,   44,   44,   44,   44,   26,   26,
   26,   26,   26,   26,   26,
};
final static short yylen[] = {                            2,
    0,    0,    5,    2,    3,    2,    2,    2,    1,    2,
    1,    2,    2,    1,    1,    3,    2,    2,    1,    1,
    1,    1,    1,    1,    1,    2,    0,    0,    8,    3,
    2,    3,    1,    3,    2,    2,    1,    2,    1,    1,
    4,    3,    3,    2,    3,    3,    3,    3,    3,    2,
    2,    0,    2,    1,    0,    2,    4,    6,    4,    5,
    3,    5,    5,    3,    4,    3,    3,    4,    3,    3,
    3,    2,    1,    1,    3,    2,    2,    1,    3,    1,
    1,    1,    1,    1,    1,    1,    1,    1,    3,    1,
    3,    1,    1,    3,    3,    2,    3,    3,    4,    6,
    7,    1,    2,    1,    3,    1,    3,    6,    5,    5,
    4,    5,    2,    4,    3,    3,    3,    3,    3,    3,
    3,    3,    1,    3,    3,    3,    3,    3,    3,    1,
    3,    1,    2,    1,    1,    1,    4,    5,    3,    4,
    1,    2,    2,    3,    3,
};
final static short yydefred[] = {                         0,
    0,    0,   87,    0,    0,    0,   88,    0,    0,    0,
    0,   11,    0,    0,   20,   21,   22,   23,   24,   25,
    0,    0,    0,   52,    0,    0,    0,   93,  132,    0,
    0,    0,  135,  136,    0,    0,    0,    0,    0,  130,
    0,    0,    0,   74,    0,    0,    0,    6,    0,    4,
    8,   10,   12,   13,    0,    0,   27,    0,  103,    0,
    0,    0,    0,    0,    0,    0,    0,    0,   96,    0,
    0,    0,    0,    0,    0,  106,    0,    2,    0,    0,
    0,    0,    0,  133,    0,   83,   84,   85,   86,    0,
    0,   81,   82,    0,    0,   64,    0,   76,    0,    0,
    0,   45,    0,   43,   66,   69,    0,   70,    5,    0,
    0,    0,   94,    0,  141,    0,    0,    0,    0,    0,
   67,    0,    0,   53,   98,    0,    0,    0,    0,    0,
   95,   97,    0,    0,    0,    0,    0,    0,    0,    0,
  126,  128,    0,    0,   75,    0,    0,    0,    0,    0,
   65,    0,   52,    0,   57,  131,  127,  124,  129,  125,
   41,   68,   89,    0,   28,  142,  143,    0,    0,   59,
   51,    0,   50,    0,    0,   99,    0,  115,  107,  105,
    0,   17,    0,    0,    0,    3,  137,    0,   63,   56,
   62,    0,   39,    0,   31,    0,    0,   33,    0,  144,
  139,    0,    0,   49,    0,    0,  114,    0,    0,  111,
    0,   16,  138,   58,   38,    0,   35,   30,    0,    0,
  140,  100,    0,  109,    0,  110,   34,   32,    0,  101,
  108,   29,
};
final static short yydgoto[] = {                          9,
   10,   26,  137,   11,   12,   13,   14,  136,   15,   16,
   33,   18,   34,   20,   21,   56,   57,  165,  111,  199,
  197,  198,   35,   36,   23,  119,  124,   63,   64,   24,
  152,  153,   37,   47,   38,   94,   25,   73,   74,   75,
   76,   77,   39,   40,
};
final static short yysindex[] = {                      -106,
    0,  131,    0,  305, -262,  -34,    0,  556,    0, -241,
  589,    0,   -7,    6,    0,    0,    0,    0,    0,    0,
 -175,  -33,  -26,    0,  212,  -68,   54,    0,    0,   48,
 -165,  309,    0,    0,  525,  -31, -138,   63,  -21,    0,
  241,   36,  131,    0,  215,   15,   74,    0,  865,    0,
    0,    0,    0,    0,    0,   84,    0,  309,    0, -123,
 -117,   50,  124,  507,  101,  105, -112, -228,    0, -110,
  -42,  -31,  112,  116,  121,    0,  570,    0,  383,  383,
  -43,  -43,   91,    0,  119,    0,    0,    0,    0,  466,
  470,    0,    0,  309,  -96,    0, -136,    0,  -43,  478,
  490,    0,   57,    0,    0,    0,  127,    0,    0,  -84,
  135,   15,    0,  132,    0,  -81,  132,  136,   17,  131,
    0,  984,  122,    0,    0,   93,  141,  -74,  143,  -72,
    0,    0,  238, -125,  128,  936,  -89,   -9,  -21,  -21,
    0,    0,  149,  -65,    0,   -9,  -21,   -9,  -21,   15,
    0,  -64,    0,  -60,    0,    0,    0,    0,    0,    0,
    0,    0,    0,  145,    0,    0,    0,  -55,   72,    0,
    0,  991,    0,  165,  -51,    0,  168,    0,    0,    0,
  946,    0,  -47,  -61,  157,    0,    0,  177,    0,    0,
    0,  -37,    0, -245,    0,   22,    2,    0,  140,    0,
    0,   40,  132,    0,  244,  261,    0,   67,   33,    0,
   70,    0,    0,    0,    0,   85,    0,    0, -198,  998,
    0,    0,  279,    0,   89,    0,    0,    0,  882,    0,
    0,    0,
};
final static short yyrindex[] = {                         0,
  151,    0,    0,    0,  756,    0,    0,    0,    0,    0,
   75,    0,  837,  851,    0,    0,    0,    0,    0,    0,
    0,  -24,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,  260,  -18,  899,  638,    4,    0,
    0,  674,    0,    0,    0,  172,  710,    0,   90,    0,
    0,    0,    0,    0,  -40,  660,    0,    0,    0,    0,
    0,    0,    0,    0,  388,    0,    0,    0,    0,    0,
    0,  412,    0,    0,  314,    0,    0,    0,    0,    0,
    0,    0,    0,    0,  696,    0,    0,    0,    0,    0,
    0,    0,    0,    0,  905,    0,  728,    0,    0,    0,
    0,    0,  742,    0,    0,    0,  763,    0,    0,    0,
    0,  777,    0,  406,    0,    0,  432,  791,  805,    0,
    0,    0,    0,    0,    0,    0,  439,    0,    0,   39,
    0,    0,    0,    0,  960,    0,    0,    0,   27,   49,
    0,    0,    0,    0,    0,   71,   96,  333,  355,  282,
    0,    0,    0,  905,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,  919,    0,    0,    0,
    0,    0,    0,    0,  974,    0,    0,    0,    0,    0,
    0,  819,    0,    0,    0,   62,    0,    0,    0,    0,
    0,    0,  378,    0,  459,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,   69,    0,    0,    0,    0,
    0,    0,  465,    0,   78,    0,    0,    0,    0,    0,
    0,    0,
};
final static short yygindex[] = {                         0,
  340,    0,    0,   42,    5, 1089,    0,  -94,    0,    0,
  661,    0,  751,    0,  -15,    0,    0,    0,    0,    0,
    0,  152,   26,  732,  308,    0,    0,  -32,    0,    0,
  221,    0,  -29,  332,  347,    0,    0,    0,    0,    0,
  251,    0,  372,  -25,
};
final static int YYTABLESIZE=1274;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         40,
   90,   31,   91,   90,   97,   45,   59,   43,   59,   70,
   31,  215,   60,  105,   60,   52,    8,   61,   90,   92,
  100,    3,  134,  134,  134,  101,  134,  172,  134,   42,
    7,   46,   81,  121,   62,   50,   92,   82,    3,  181,
  134,  134,  218,  134,  123,  219,  123,    7,  123,   49,
   71,   53,  128,   52,    8,  141,  142,   90,  193,   91,
  169,  168,  123,  123,   54,  123,  103,  119,    3,  119,
   46,  119,  194,  156,  158,  160,  104,    7,   90,  113,
   91,   55,  113,  112,   90,  119,  119,   83,  119,  121,
  170,  121,   84,  121,  116,   81,   79,  161,   80,   90,
   82,   91,   37,   98,  134,   37,  134,  121,  121,   36,
  121,  120,   36,  120,  108,  120,  202,   70,  112,  150,
  190,  112,   95,   96,  154,  155,  123,  110,  123,  120,
  120,   28,  120,  113,    2,  144,  117,  175,  117,   28,
  117,  125,    4,    5,  126,  127,  129,    6,  196,  119,
    1,  119,  131,    2,  117,  117,  132,  117,   71,  145,
    3,    4,    5,   32,  133,  151,    6,  162,   31,    7,
   32,  121,  163,  121,  164,   31,  166,   60,  216,   61,
  173,  176,  177,  178,  179,  195,  182,  186,   28,  187,
   93,    2,  188,  120,   93,  120,   93,  189,    3,    4,
    5,  191,  200,  196,    6,  205,  206,    7,  207,  210,
  211,   93,   73,   28,   29,  212,   90,  213,  117,   90,
  117,   27,   28,   29,  214,   58,   90,   90,   90,  130,
   73,   30,   90,   52,   99,   90,   90,  134,  134,   44,
   30,  134,  134,  134,  134,  134,  134,  134,  134,  134,
  134,   68,   69,  134,  134,  106,   67,  134,  134,   31,
  123,  229,  220,  123,  123,  123,  123,  123,  123,  123,
  123,  123,  123,    1,  167,  123,  123,   68,  217,  123,
  123,  102,   31,  119,  222,   31,  119,  119,  119,  119,
  119,  119,  119,  119,  119,  119,   73,  221,  119,  119,
   80,  223,  119,  119,  225,  121,   28,  115,  121,  121,
  121,  121,  121,  121,  121,  121,  121,  121,   80,  230,
  121,  121,   79,  224,  121,  121,  226,  120,   28,  201,
  120,  120,  120,  120,  120,  120,  120,  120,  120,  120,
   79,  227,  120,  120,   41,  231,  120,  120,  143,   31,
  174,    9,  117,   31,  104,  117,  117,  117,  117,  117,
  117,  117,  117,  117,  117,   78,    7,  117,  117,  118,
  228,  117,  117,  122,  192,  122,  107,  122,   85,   27,
   28,   29,   80,  180,   80,    0,   27,   28,   29,    0,
    0,  122,  122,  120,  122,  118,    0,  118,   30,  118,
    0,  193,    0,    0,   79,   30,   79,    1,    0,   93,
    1,    3,    0,  118,  118,  194,  118,    1,    1,    1,
    7,  145,  145,    1,    0,    0,    1,   31,   73,  132,
  132,   73,  132,    0,  132,    0,  145,    0,   73,   73,
   73,    0,    0,   73,   73,    0,    0,   73,   73,   91,
  139,  140,  102,  134,  134,  122,  134,  122,  134,    0,
    0,  147,  149,    0,   91,    0,   91,   27,   28,   65,
   27,   28,   29,    0,    0,   92,    0,  118,    3,  118,
  133,  133,    0,  133,    0,  133,   66,    7,   44,   30,
   92,    0,    0,   27,   28,   29,   27,   28,   29,    0,
  137,  137,  145,  137,    3,  137,  138,  138,    0,  138,
   31,  138,   30,    7,   31,   30,   80,    0,    0,   80,
   80,   80,   31,    0,    0,    0,   80,   80,   80,    0,
   91,   80,   80,    0,   31,   80,   80,    0,   79,    0,
    0,   79,   79,   79,    0,    0,    0,    0,   79,   79,
   79,    0,    0,   79,   79,    0,   92,   79,   79,    0,
   27,   28,   29,    0,   27,   28,   29,   90,    0,   91,
    0,    0,    0,    0,    0,    0,    0,    0,    0,   30,
    0,    0,    0,   30,   93,    0,   92,    0,    0,  122,
    0,    0,  122,  122,  122,  122,  122,  122,  122,  122,
  122,  122,    0,    0,  122,  122,    0,    0,  122,  122,
    0,  118,    0,    0,  118,  118,  118,  118,  118,  118,
  118,  118,  118,  118,    0,    0,  118,  118,    0,  122,
  118,  118,    0,    0,  145,  145,    0,  145,  138,   28,
   29,    0,    0,  132,  145,  145,  145,    0,    0,  145,
  145,    0,    0,  145,  145,    0,    0,   30,    0,  132,
   17,    0,   91,    0,    0,   91,    0,  134,   17,    0,
    0,   17,   91,   91,   91,    0,    0,   91,   91,    0,
   48,   91,   91,  134,    0,    0,   17,    0,   92,    0,
    0,   92,  134,    0,  133,    0,   78,    0,   92,   92,
   92,    0,    0,   92,   92,    0,    0,   92,   92,   17,
  133,    0,    0,   51,  137,    0,    0,    0,   26,    0,
  138,  146,   28,   29,   17,  148,   28,   29,    0,    0,
  137,   22,   44,  157,   28,   29,  138,   17,    0,   22,
   30,    0,   22,    0,   30,  159,   28,   29,    0,    0,
   19,    0,   30,    0,   77,    0,   72,   22,   19,    0,
   78,   19,   78,   28,   30,    0,    2,    0,   72,    0,
    0,    0,    0,    0,    4,    5,   19,    0,    0,    6,
   22,    0,   17,    0,   26,    0,   61,   86,   87,   88,
   89,    0,  114,  117,   17,   22,   17,    0,   44,   19,
   42,    0,    0,    0,    0,    0,    0,    0,   22,    0,
    0,    0,   28,    0,   19,    2,    0,    0,   77,    0,
   77,   71,    3,    4,    5,    0,   28,   19,    6,    2,
    0,    7,   17,    0,   72,   46,    0,    4,    5,    0,
    0,   17,    6,    0,    0,   28,    0,    0,    2,   48,
    0,    0,   61,   22,    0,    3,    4,    5,    0,    0,
    0,    6,    0,   47,    7,   22,   42,   22,    0,    0,
    0,    0,   19,    0,    0,    0,    0,   60,   54,    0,
   17,    0,    0,    0,   19,    0,   19,   71,    0,   17,
    0,    0,    0,    0,   78,    0,    0,   78,   78,   78,
  203,   46,    0,   22,   78,   78,   78,    0,    0,   78,
   78,    0,   22,   78,   78,   48,   26,    0,    0,   26,
    0,    0,   19,    0,    0,    0,   26,   26,   26,   47,
   44,   19,   26,   44,    0,   26,   26,    0,    0,    0,
   44,   44,   44,   60,    0,   44,   44,    0,    0,   44,
   44,   22,   77,    0,    0,   77,   77,   77,    0,    0,
   22,   14,   77,   77,   77,    0,   72,   77,   77,   72,
   19,   77,   77,    0,    0,   15,   72,   72,   72,   19,
    0,   72,   72,    0,   61,   72,   72,   61,    0,  109,
    0,    0,    0,    0,   61,   61,   61,    0,   42,   61,
   61,   42,    0,   61,   61,    0,  232,    0,   42,   42,
   42,    0,   54,   42,   42,   54,    0,   42,   42,   71,
    0,   52,   71,   54,   54,    0,    0,   55,   54,   71,
   71,   71,    0,   46,   71,   71,   46,    0,   71,   71,
    0,  116,    0,   46,   46,   46,    0,   48,   46,   46,
   48,    0,   46,   46,    0,    0,    0,   48,   48,   48,
  184,   47,   48,   48,   47,    0,   48,   48,    0,    0,
  209,   47,   47,   47,    0,   60,   47,   47,   60,    0,
   47,   47,    0,    0,   19,   60,   60,   60,    0,    0,
   60,   60,    0,   14,   60,   60,   14,    0,   18,    0,
    0,    0,    0,   14,   14,   14,    0,   15,  171,   14,
   15,    0,   14,   14,    0,  204,    0,   15,   15,   15,
    0,   28,    0,   15,    2,    0,   15,   15,    0,    0,
    0,    3,    4,    5,    0,    0,    0,    6,   28,    0,
    7,    2,    0,    0,    0,    0,    0,    0,    3,    4,
    5,    0,  123,    0,    6,   52,    0,    7,   52,    0,
    0,   55,    0,    0,   55,  135,   52,   52,    0,    0,
    0,   52,   55,   55,    0,  116,    0,   55,  116,    0,
    0,    0,    0,    0,    0,    0,  116,  116,    0,    0,
    0,  116,   28,    0,    0,    2,    0,    0,    0,    0,
    0,    0,   28,    4,    5,    2,    0,  183,    6,    0,
  135,    0,    0,    4,    5,    0,   19,  208,    6,   19,
    0,    0,  135,    0,  185,    0,    0,   19,   19,    0,
   18,   19,   19,   18,    0,    0,    0,    0,    0,    0,
   28,   18,   18,    2,    0,   18,   18,   28,    0,    0,
    2,    4,    5,    0,   28,    0,    6,    2,    4,    5,
  185,    0,    0,    6,    3,    4,    5,    0,    0,  185,
    6,    0,    0,    7,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         40,
   43,   45,   45,   44,   37,   40,   40,  270,   40,   25,
   45,  257,   46,   43,   46,   11,  123,   44,   59,   44,
   42,  267,   41,   42,   43,   47,   45,  122,   47,    4,
  276,    6,   42,   63,   61,  277,   61,   47,  267,  134,
   59,   60,   41,   62,   41,   44,   43,  276,   45,    8,
   25,   59,   68,   49,  123,   81,   82,   43,  257,   45,
   44,   45,   59,   60,   59,   62,   41,   41,  267,   43,
   45,   45,  271,   99,  100,  101,   41,  276,   43,   41,
   45,  257,   44,   58,  125,   59,   60,   40,   62,   41,
  120,   43,  258,   45,   45,   42,   43,   41,   45,   43,
   47,   45,   41,   41,  123,   44,  125,   59,   60,   41,
   62,   41,   44,   43,   41,   45,   45,  133,   41,   94,
  153,   44,  261,  262,  261,  262,  123,   44,  125,   59,
   60,  257,   62,  257,  260,   45,   41,   45,   43,  257,
   45,   41,  268,  269,   40,  258,  257,  273,  164,  123,
  257,  125,   41,  260,   59,   60,   41,   62,  133,   41,
  267,  268,  269,   40,   44,  262,  273,   41,   45,  276,
   40,  123,  257,  125,   40,   45,  258,   46,  194,   44,
   59,   41,  257,   41,  257,   41,   59,  277,  257,   41,
   40,  260,  258,  123,   44,  125,   46,  262,  267,  268,
  269,  262,  258,  219,  273,   41,  258,  276,   41,  257,
  272,   61,   41,  257,  258,   59,  257,   41,  123,  260,
  125,  256,  257,  258,  262,  259,  267,  268,  269,  272,
   59,  275,  273,  229,  256,  276,  277,  256,  257,  274,
  275,  260,  261,  262,  263,  264,  265,  266,  267,  268,
  269,   40,   41,  272,  273,   41,   45,  276,  277,   45,
  257,  220,  123,  260,  261,  262,  263,  264,  265,  266,
  267,  268,  269,  123,  258,  272,  273,   40,  257,  276,
  277,   41,   45,  257,   41,   45,  260,  261,  262,  263,
  264,  265,  266,  267,  268,  269,  125,  258,  272,  273,
   41,   41,  276,  277,  272,  257,  257,  258,  260,  261,
  262,  263,  264,  265,  266,  267,  268,  269,   59,   41,
  272,  273,   41,  257,  276,  277,  257,  257,  257,  258,
  260,  261,  262,  263,  264,  265,  266,  267,  268,  269,
   59,  257,  272,  273,   40,  257,  276,  277,  258,   45,
  258,  277,  257,   45,   41,  260,  261,  262,  263,  264,
  265,  266,  267,  268,  269,   26,  277,  272,  273,   62,
  219,  276,  277,   41,  154,   43,   45,   45,   32,  256,
  257,  258,  123,  133,  125,   -1,  256,  257,  258,   -1,
   -1,   59,   60,  270,   62,   41,   -1,   43,  275,   45,
   -1,  257,   -1,   -1,  123,  275,  125,  257,   -1,  259,
  260,  267,   -1,   59,   60,  271,   62,  267,  268,  269,
  276,   44,   45,  273,   -1,   -1,  276,   45,  257,   42,
   43,  260,   45,   -1,   47,   -1,   59,   -1,  267,  268,
  269,   -1,   -1,  272,  273,   -1,   -1,  276,  277,   44,
   79,   80,   41,   42,   43,  123,   45,  125,   47,   -1,
   -1,   90,   91,   -1,   59,   -1,   61,  256,  257,  258,
  256,  257,  258,   -1,   -1,   44,   -1,  123,  267,  125,
   42,   43,   -1,   45,   -1,   47,  275,  276,  274,  275,
   59,   -1,   -1,  256,  257,  258,  256,  257,  258,   -1,
   42,   43,  125,   45,  267,   47,   42,   43,   -1,   45,
   45,   47,  275,  276,   45,  275,  257,   -1,   -1,  260,
  261,  262,   45,   -1,   -1,   -1,  267,  268,  269,   -1,
  125,  272,  273,   -1,   45,  276,  277,   -1,  257,   -1,
   -1,  260,  261,  262,   -1,   -1,   -1,   -1,  267,  268,
  269,   -1,   -1,  272,  273,   -1,  125,  276,  277,   -1,
  256,  257,  258,   -1,  256,  257,  258,   43,   -1,   45,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  275,
   -1,   -1,   -1,  275,   60,   -1,   62,   -1,   -1,  257,
   -1,   -1,  260,  261,  262,  263,  264,  265,  266,  267,
  268,  269,   -1,   -1,  272,  273,   -1,   -1,  276,  277,
   -1,  257,   -1,   -1,  260,  261,  262,  263,  264,  265,
  266,  267,  268,  269,   -1,   -1,  272,  273,   -1,  123,
  276,  277,   -1,   -1,  257,  258,   -1,  260,  256,  257,
  258,   -1,   -1,  256,  267,  268,  269,   -1,   -1,  272,
  273,   -1,   -1,  276,  277,   -1,   -1,  275,   -1,  272,
    0,   -1,  257,   -1,   -1,  260,   -1,  256,    8,   -1,
   -1,   11,  267,  268,  269,   -1,   -1,  272,  273,   -1,
  125,  276,  277,  272,   -1,   -1,   26,   -1,  257,   -1,
   -1,  260,  123,   -1,  256,   -1,   59,   -1,  267,  268,
  269,   -1,   -1,  272,  273,   -1,   -1,  276,  277,   49,
  272,   -1,   -1,  125,  256,   -1,   -1,   -1,   59,   -1,
  256,  256,  257,  258,   64,  256,  257,  258,   -1,   -1,
  272,    0,   59,  256,  257,  258,  272,   77,   -1,    8,
  275,   -1,   11,   -1,  275,  256,  257,  258,   -1,   -1,
    0,   -1,  275,   -1,   59,   -1,   25,   26,    8,   -1,
  123,   11,  125,  257,  275,   -1,  260,   -1,   59,   -1,
   -1,   -1,   -1,   -1,  268,  269,   26,   -1,   -1,  273,
   49,   -1,  122,   -1,  125,   -1,   59,  263,  264,  265,
  266,   -1,   61,   62,  134,   64,  136,   -1,  125,   49,
   59,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   77,   -1,
   -1,   -1,  257,   -1,   64,  260,   -1,   -1,  123,   -1,
  125,   59,  267,  268,  269,   -1,  257,   77,  273,  260,
   -1,  276,  172,   -1,  125,   59,   -1,  268,  269,   -1,
   -1,  181,  273,   -1,   -1,  257,   -1,   -1,  260,   59,
   -1,   -1,  125,  122,   -1,  267,  268,  269,   -1,   -1,
   -1,  273,   -1,   59,  276,  134,  125,  136,   -1,   -1,
   -1,   -1,  122,   -1,   -1,   -1,   -1,   59,  123,   -1,
  220,   -1,   -1,   -1,  134,   -1,  136,  125,   -1,  229,
   -1,   -1,   -1,   -1,  257,   -1,   -1,  260,  261,  262,
  169,  125,   -1,  172,  267,  268,  269,   -1,   -1,  272,
  273,   -1,  181,  276,  277,  125,  257,   -1,   -1,  260,
   -1,   -1,  172,   -1,   -1,   -1,  267,  268,  269,  125,
  257,  181,  273,  260,   -1,  276,  277,   -1,   -1,   -1,
  267,  268,  269,  125,   -1,  272,  273,   -1,   -1,  276,
  277,  220,  257,   -1,   -1,  260,  261,  262,   -1,   -1,
  229,  125,  267,  268,  269,   -1,  257,  272,  273,  260,
  220,  276,  277,   -1,   -1,  125,  267,  268,  269,  229,
   -1,  272,  273,   -1,  257,  276,  277,  260,   -1,  125,
   -1,   -1,   -1,   -1,  267,  268,  269,   -1,  257,  272,
  273,  260,   -1,  276,  277,   -1,  125,   -1,  267,  268,
  269,   -1,  257,  272,  273,  260,   -1,  276,  277,  257,
   -1,  123,  260,  268,  269,   -1,   -1,  123,  273,  267,
  268,  269,   -1,  257,  272,  273,  260,   -1,  276,  277,
   -1,  123,   -1,  267,  268,  269,   -1,  257,  272,  273,
  260,   -1,  276,  277,   -1,   -1,   -1,  267,  268,  269,
  125,  257,  272,  273,  260,   -1,  276,  277,   -1,   -1,
  125,  267,  268,  269,   -1,  257,  272,  273,  260,   -1,
  276,  277,   -1,   -1,  125,  267,  268,  269,   -1,   -1,
  272,  273,   -1,  257,  276,  277,  260,   -1,  125,   -1,
   -1,   -1,   -1,  267,  268,  269,   -1,  257,  125,  273,
  260,   -1,  276,  277,   -1,  125,   -1,  267,  268,  269,
   -1,  257,   -1,  273,  260,   -1,  276,  277,   -1,   -1,
   -1,  267,  268,  269,   -1,   -1,   -1,  273,  257,   -1,
  276,  260,   -1,   -1,   -1,   -1,   -1,   -1,  267,  268,
  269,   -1,   64,   -1,  273,  257,   -1,  276,  260,   -1,
   -1,  257,   -1,   -1,  260,   77,  268,  269,   -1,   -1,
   -1,  273,  268,  269,   -1,  257,   -1,  273,  260,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,  268,  269,   -1,   -1,
   -1,  273,  257,   -1,   -1,  260,   -1,   -1,   -1,   -1,
   -1,   -1,  257,  268,  269,  260,   -1,  272,  273,   -1,
  122,   -1,   -1,  268,  269,   -1,  257,  272,  273,  260,
   -1,   -1,  134,   -1,  136,   -1,   -1,  268,  269,   -1,
  257,  272,  273,  260,   -1,   -1,   -1,   -1,   -1,   -1,
  257,  268,  269,  260,   -1,  272,  273,  257,   -1,   -1,
  260,  268,  269,   -1,  257,   -1,  273,  260,  268,  269,
  172,   -1,   -1,  273,  267,  268,  269,   -1,   -1,  181,
  273,   -1,   -1,  276,
};
}
final static short YYFINAL=9;
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
"FLECHA","PRINT","CADENAML","TRUNC","LAMBDA","EOF","LOWER_THAN_ID",
};
final static String yyrule[] = {
"$accept : prog",
"$$1 :",
"$$2 :",
"prog : ID $$1 bloque_sentencias $$2 EOF",
"prog : bloque_sentencias EOF",
"bloque_sentencias : '{' listasentencias '}'",
"bloque_sentencias : '{' '}'",
"bloque_sentencias : '{' listasentencias",
"bloque_sentencias : listasentencias '}'",
"bloque_sentencias : listasentencias",
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
"declaracion : tipo listaIDs",
"$$3 :",
"$$4 :",
"declaracion : tipo id_funcion $$3 parametros_declaracion_f $$4 '{' listasentencias '}'",
"parametros_declaracion_f : '(' listaparametros ')'",
"parametros_declaracion_f : '(' ')'",
"listaparametros : listaparametros ',' parametro_formal",
"listaparametros : parametro_formal",
"parametro_formal : CV tipo ID",
"parametro_formal : tipo ID",
"parametro_formal : CV tipo",
"parametro_formal : tipo",
"parametro_formal : CV ID",
"parametro_formal : ID",
"id_funcion : ID",
"sentencia_return : RETURN '(' expresion ')'",
"sentencia_return : RETURN '(' expresion",
"sentencia_return : RETURN expresion ')'",
"sentencia_return : RETURN expresion",
"sentencia_return : RETURN '(' ')'",
"asignacion : identificador ASIG expresion",
"asignacion : listavariables '=' listaconstantes",
"asignacion : listavariables '=' listavariables",
"bloque_control_estructura : '{' listasentenciasejecutables '}'",
"bloque_control_estructura : sentenciaejecutable ';'",
"bloque_control_estructura : '{' '}'",
"$$5 :",
"bloque_control : $$5 bloque_control_estructura",
"inicio_do : DO",
"$$6 :",
"bloque_control_else : $$6 bloque_control",
"control : IF condicion bloque_control ENDIF",
"control : IF condicion bloque_control ELSE bloque_control_else ENDIF",
"control : inicio_do bloque_control UNTIL condicion",
"control : IF condicion bloque_control ELSE bloque_control_else",
"control : IF condicion bloque_control",
"control : IF condicion bloque_control ELSE ENDIF",
"control : IF condicion ELSE bloque_control_else ENDIF",
"control : IF condicion ENDIF",
"control : IF condicion ELSE ENDIF",
"control : DO UNTIL condicion",
"control : inicio_do bloque_control condicion",
"print : PRINT '(' argumento_print ')'",
"print : PRINT '(' ')'",
"print : PRINT argumento_print ')'",
"print : PRINT '(' argumento_print",
"print : PRINT argumento_print",
"argumento_print : expresion",
"argumento_print : CADENAML",
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
"listaIDs : listaIDs ',' ID",
"listaIDs : ID",
"listavariables : listavariables ',' identificador",
"listavariables : identificador",
"identificador : ID",
"identificador : identificador '.' ID",
"funcion : cabecera_funcion argumentos_funcion ')'",
"funcion : cabecera_funcion ')'",
"funcion_lambda : cabecera_funcion argumento_id_lambda ')'",
"funcion_lambda : cabecera_funcion CTE ')'",
"funcion_lambda : cabecera_funcion '-' CTE ')'",
"funcion_lambda : cabecera_funcion TRUNC '(' CTE ')' ')'",
"funcion_lambda : cabecera_funcion TRUNC '(' '-' CTE ')' ')'",
"argumento_id_lambda : identificador",
"cabecera_funcion : identificador '('",
"argumentos_funcion : listapinvocacion",
"listapinvocacion : listapinvocacion ',' pinvocacion",
"listapinvocacion : pinvocacion",
"pinvocacion : expresion FLECHA ID",
"pinvocacion : cabecera_declaracion_lambda '{' listasentenciasejecutables '}' FLECHA ID",
"pinvocacion : cabecera_declaracion_lambda '{' listasentenciasejecutables FLECHA ID",
"pinvocacion : cabecera_declaracion_lambda listasentenciasejecutables '}' FLECHA ID",
"pinvocacion : cabecera_declaracion_lambda listasentenciasejecutables FLECHA ID",
"pinvocacion : cabecera_declaracion_lambda '{' listasentenciasejecutables '}' FLECHA",
"pinvocacion : expresion FLECHA",
"cabecera_declaracion_lambda : '(' tipo ID ')'",
"cabecera_declaracion_lambda : tipo ID ')'",
"cabecera_declaracion_lambda : '(' tipo ID",
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
"termino : factor",
"termino : termino error factor",
"factor : CTE",
"factor : '-' CTE",
"factor : identificador",
"factor : funcion",
"factor : funcion_lambda",
"factor : TRUNC '(' CTE ')'",
"factor : TRUNC '(' '-' CTE ')'",
"listaconstantes : listaconstantes ',' CTE",
"listaconstantes : listaconstantes ',' '-' CTE",
"listaconstantes : CTE",
"listaconstantes : '-' CTE",
"listaconstantes : listaconstantes CTE",
"listaconstantes : listaconstantes '-' CTE",
"listaconstantes : listaconstantes ',' identificador",
};

//#line 498 "gramatica.y"

private Lexico lexer;
private Map<String, Simbolo> tablaSimbolos;
private ArrayList<String> erroresSintacticos = new ArrayList<>();
private ArrayList<String> erroresLexicos = new ArrayList<>();
private ArrayList<String> erroresSemanticos = new ArrayList<>();
private ArrayList<String> warnings = new ArrayList<>();
private int enFunc = 0;
private int nivel = -1;
private int returnsIf = 0;
private int returnsFunc = 1;
private Stack<Boolean> huboRetStack = new Stack<>();
private Stack<Boolean> sinRetIf = new Stack<>();
private boolean enDo = false;
private boolean sinRet = false;
private boolean huboIfEnFunc = false;
private boolean enParamInvocacion = false;
private boolean enDeclaracionParam = false;
private StringBuilder stringAmbitos = new StringBuilder();
private ArrayList<Parametro> parametrosActuales = new ArrayList<>();
private Stack<ArrayList<Parametro>> stackParametrosActuales = new Stack<>();
private Stack<String> stackTipoFuncActual = new Stack<>();
private ArbolSintactico arbolSintactico;
private Map<String, ArbolSintactico> mapaFunciones = new HashMap<>();
private Simbolo funcionInvocadaActual = null;
private int funcLambdaDeclaradas = 0;
private boolean verbose = false;  

public Parser(Map<String, Simbolo> tablaSimbolos, String path, ArbolSintactico arbolSintactico, Map<String, ArbolSintactico> mapaFunciones) {
  this.tablaSimbolos = tablaSimbolos;
  this.lexer = new Lexico(tablaSimbolos,path, erroresLexicos, warnings);
  this.arbolSintactico = arbolSintactico;
  this.mapaFunciones = mapaFunciones;
}

public void setVerbose(boolean verbose) {
  this.verbose = verbose;
  this.lexer.setVerbose(verbose);
}

private void yyerror(String s) {
    erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error: " + s);
}

private boolean error() {
  return (!erroresLexicos.isEmpty() || !erroresSintacticos.isEmpty() || !erroresSemanticos.isEmpty());
}


public ArrayList<String> getErroresSintacticos(){
  return erroresSintacticos;
}

public ArrayList<String> getErroresSemanticos(){
  return erroresSemanticos;
}

public ArrayList<String> getErroresLexicos(){
  return erroresLexicos;
}

public ArrayList<String> getWarnings(){
  return warnings;
}

public int getFuncLambdaDeclaradas(){
  return funcLambdaDeclaradas;
}

private void constanteNegativa(ParserVal valCte) {
  if (valCte.obj instanceof SimboloLong) {
    Simbolo simbolo;
    if (!tablaSimbolos.containsKey("-" +((SimboloLong)valCte.obj).getLexema())) {
      simbolo =  new SimboloLong("Long", "Constante", "-" + ((SimboloLong)valCte.obj).getLexema(), -((SimboloLong)valCte.obj).getValor());
      tablaSimbolos.put("-" +((SimboloLong)valCte.obj).getLexema(), simbolo);
    } else {
      simbolo = tablaSimbolos.get("-" + ((SimboloLong)valCte.obj).getLexema());
    }
    valCte.obj = simbolo;
  } else {
        Simbolo simbolo;
        if (!tablaSimbolos.containsKey("-" +((SimboloCteFloat)valCte.obj).getLexema())) {
          simbolo =  new SimboloCteFloat("Float", "Constante", "-" + ((SimboloCteFloat)valCte.obj).getLexema(), -((SimboloCteFloat)valCte.obj).getValor());
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
        simbolo =  new SimboloLong("Long", "Constante", "2147483647L", 2147483647L);
        tablaSimbolos.put("2147483647L", simbolo);
      } else {
        simbolo = tablaSimbolos.get("2147483647L");
      }
      valCte.obj = simbolo;
      warnings.add("||================ ANALIZADOR SINTÁCTICO: Warning en línea " + valCte.ival + ": el valor de la constante entera es mayor al máximo permitido, se asigna el valor 2147483647");
    }
}

private void chequeoSentenciaReturn(ParserVal yyval) {
  if (enFunc == 0) {
    erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + yyval.ival + ": Uso de sentencia return fuera de una función");
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
    huboIfEnFunc = true;
    huboRetStack.push(false);
    nivel++;
    returnsIf++;    
  }
}

private void finIf(ParserVal yyval) {
  if (enFunc > 0 && !enDo) {
    
    //Detección de multiples retornos
    if (returnsIf < nivel) {
      erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + yyval.ival + ": Se han detectado multiples sentencias de retorno en el IF");
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
  if (returnsFunc > 0 && (!huboIfEnFunc || sinRet)) { 
    erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + yyval.ival + ": La función no tiene sentencia return en todos sus caminos de ejecución");
  } else if (returnsFunc < 0) {
    erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + yyval.ival + ": Se han detectado multiples sentencias de retorno en la función");
  }
  enFunc--;
  returnsFunc = 1;
  huboIfEnFunc = false;
}

private void cambiarASimboloFuncion (Simbolo simboloID) {
  if (simboloID != null) {
    tablaSimbolos.remove(simboloID.getLexema());
    // Creación del símbolo de función
    Simbolo nuevoSimbolo = new SimboloFuncion(stackTipoFuncActual.pop(), "Funcion", simboloID.getLexema(), stackParametrosActuales.pop());
    tablaSimbolos.put(simboloID.getLexema(), nuevoSimbolo);
  }
}

private void quitarAmbito() {
    int lastIndex = stringAmbitos.lastIndexOf(":");
    if (lastIndex > 0) {
        stringAmbitos.setLength(lastIndex);
    } else {
        stringAmbitos.setLength(0);
    }
}

private void renombrarVariable(ParserVal yyval) {
  String lexemaViejo = ((Simbolo)yyval.obj).getLexema();
  yyval.obj = tablaSimbolos.get(lexemaViejo);
  String lexemaNuevo = lexemaViejo + stringAmbitos.toString();
  ((Simbolo)yyval.obj).setLexema(lexemaNuevo);
  if (tablaSimbolos.containsKey(lexemaNuevo)) {
    erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + yyval.ival + ": La variable '" + ((Simbolo)yyval.obj).getLexema().substring(0, ((Simbolo)yyval.obj).getLexema().indexOf(":")) + "' ya ha sido declarada en el ámbito actual");
    yyval.obj = tablaSimbolos.get(lexemaNuevo);
  } else {
    tablaSimbolos.put(lexemaNuevo, ((Simbolo)yyval.obj));
  }
  tablaSimbolos.remove(lexemaViejo);
}

private void chequeo_anid_lambda(String nombreLambda, ParserVal yyval) {
  if (nombreLambda.contains(".")) {
    erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + yyval.ival + ": Se ha intentado invocar una función lambda perteneciente a otro ámbito");
  }
}

// Esta función toma el Lexema de un identificador con la forma escrita por el programador (puede contener "."), 
// busca y retorna de la tabla de símbolos el símbolo correspondiente, teniendo en cuenta los ámbitos.
private Simbolo checkearAlcance(String lexema, int nroLinea) {

  // AuxSA se vuelve una copia de StringAmbitos para ir recortándola
  String auxSA = stringAmbitos.toString();

  // Cuando el Lexema es una variable no anidada:
  if (!lexema.contains(".")) {
    String auxLexema = lexema + auxSA;

    while (!tablaSimbolos.containsKey(auxLexema) && auxSA.length() > 0) {
      auxSA = auxSA.substring(0, auxSA.lastIndexOf(":"));
      auxLexema = lexema + auxSA;
    }

    // Tira error si no se encuentra en el ámbito actual o los superiores (A no ser que sea una variable de función lambda, que se guarda sin ámbito por decisión de diseño)
    if (auxLexema.equals(lexema) && !tablaSimbolos.containsKey("lambda:" + auxLexema)) {
      erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + nroLinea + ": La variable '" + lexema + "' no ha sido declarada en el ámbito actual y tampoco está al alcance");
      tablaSimbolos.remove(lexema);
      return null;
    } else {
      tablaSimbolos.remove(lexema);
      return tablaSimbolos.get(auxLexema);
    }

  // Cuando el Lexema es una variable anidada:    
  } else { 
    String varActual = lexema.substring(lexema.lastIndexOf(".") + 1);
    String ambitoVarActual = (":" + lexema.substring(0, lexema.lastIndexOf("."))).replace(".", ":");

    // Dado que estaba anidada, se va a buscar el ámbito a partir de stringAmbitos seguido del ambito escrito en la variable
    while (!tablaSimbolos.containsKey(varActual + auxSA + ambitoVarActual) && auxSA.length() > 0) {
      auxSA = auxSA.substring(0, auxSA.lastIndexOf(":"));
    }

    if (!tablaSimbolos.containsKey(varActual + auxSA + ambitoVarActual)) {
      erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + nroLinea + ": La variable '" + lexema + "' no ha sido declarada en el ámbito actual y tampoco está al alcance");
      for (String ambitoPart : lexema.split("\\.")) {
        tablaSimbolos.remove(ambitoPart);
      }
      return null;
    } else {
      for (String ambitoPart : lexema.split("\\.")) {
        tablaSimbolos.remove(ambitoPart);
      }
      return tablaSimbolos.get(varActual + auxSA + ambitoVarActual);
    }
  }
}

// Función encargada de dos cosas: en base a conocer el parámetro real y el parámetro formal intencionado, chequea si ese parámetro formal verdaderamente existe en la función invocada,
// y, si existe, verifica también que, en caso de ser CVR, el parámetro real no sea una constante ni una expresión.
private void checkeoParametroDeInvocacion(String lexemaParametroFormal, int nroLinea, ArbolSintactico arbolParametroReal) {
  if (!error()) {
    String lexemaFuncion = ((SimboloFuncion)funcionInvocadaActual).getLexema();
    Parametro parametroFormal = ((SimboloFuncion)funcionInvocadaActual).getRefParametro(lexemaParametroFormal);
    if (parametroFormal == null) {
      erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + nroLinea + ": el parámetro formal '" + lexemaParametroFormal + "' no existe en la función '" + lexemaFuncion.substring(0, lexemaFuncion.indexOf(':')) + "'");    
    } else if ((arbolParametroReal.getCategoria() != null || arbolParametroReal.getRefSimbolo().getCategoria().equals("Constante")) && parametroFormal.getPasaje().equals("CVR") ) {
      erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + nroLinea + ": el parámetro real asociado al parámetro formal '" + lexemaParametroFormal + "' de la función '" + lexemaFuncion.substring(0, lexemaFuncion.indexOf(':')) + "' es una constante u expresión y no puede pasarse por Copia-Valor-Resultado (CVR)");
    }
  }
  tablaSimbolos.remove(lexemaParametroFormal);
}

private void restriccionCteFloat(ParserVal valCte) {
  if (valCte.obj instanceof SimboloCteFloat) {
    erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + valCte.ival + ": Uso incorrecto de constante Float por incompatibilidad de tipos");
    tablaSimbolos.remove(((SimboloCteFloat)valCte.obj).getLexema());
  }
}

private ArbolSintactico generarAsigMultiple(ArrayList<Simbolo> listaVariables, ArrayList<Simbolo> listaConstantes) {
  int i = 1; 
  ArbolSintactico arbolRetorno = new ArbolSintactico("Sentencia", new ArbolSintactico("Asig", new ArbolSintactico(listaVariables.get(0)), new ArbolSintactico(listaConstantes.get(0))), null );                                                                                            
  while (i < listaConstantes.size()) {
    ArbolSintactico nuevaAsignacion = new ArbolSintactico( "Sentencia", new ArbolSintactico("Asig", new ArbolSintactico(listaVariables.get(i)), new ArbolSintactico(listaConstantes.get(i))), null);
    arbolRetorno.setUltimoDerecho(nuevaAsignacion);
    i++;
  }
  SimboloLong simboloCero = (SimboloLong)tablaSimbolos.get("0L");
  if (simboloCero == null) {
    simboloCero = new SimboloLong("Long", "Constante", "0L", 0L);
    tablaSimbolos.put("0L", simboloCero);
  }
  while (i < listaVariables.size()) {
    ArbolSintactico nuevaAsignacion = new ArbolSintactico( "Sentencia", new ArbolSintactico("Asig", new ArbolSintactico(listaVariables.get(i)), new ArbolSintactico(simboloCero)), null);
    arbolRetorno.setUltimoDerecho(nuevaAsignacion);
    i++;
  } 

  return arbolRetorno;
}
//#line 980 "Parser.java"
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
case 1:
//#line 23 "gramatica.y"
{stringAmbitos.append(":"+((Simbolo)val_peek(0).obj).getLexema());}
break;
case 2:
//#line 23 "gramatica.y"
{quitarAmbito();}
break;
case 3:
//#line 23 "gramatica.y"
{if (!error() && val_peek(2).obj != null && val_peek(2).obj instanceof ArbolSintactico) { arbolSintactico.adoptarNodoIntermedio((ArbolSintactico)val_peek(2).obj);}}
break;
case 4:
//#line 24 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el nombre del programa");}
break;
case 5:
//#line 30 "gramatica.y"
{ yyval = val_peek(1); yyval.ival = val_peek(0).ival; }
break;
case 7:
//#line 32 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta llave de cierre del bloque de sentencias");}
break;
case 8:
//#line 33 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta llave de apertura del bloque de sentencias");}
break;
case 9:
//#line 34 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": faltan ambas llaves del bloque de sentencias");}
break;
case 10:
//#line 37 "gramatica.y"
{ if (!error()) {
                                                                            if (val_peek(1).obj != null && val_peek(0).obj != null) { ((ArbolSintactico)val_peek(1).obj).setUltimoDerecho(new ArbolSintactico("Sentencia", ((ArbolSintactico)val_peek(0).obj) , null )); yyval = val_peek(1); } 
                                                                            else if (val_peek(1).obj != null) { yyval= val_peek(1); yyval.ival = val_peek(1).ival; }
                                                                            else if (val_peek(0).obj != null) { yyval= new ParserVal(new ArbolSintactico("Sentencia", ((ArbolSintactico)val_peek(0).obj), null));} 
                                                                          }
                                                                          yyval.ival = val_peek(0).ival; 
                                                                        }
break;
case 11:
//#line 44 "gramatica.y"
{ if (!error()) { if (val_peek(0).obj != null) { yyval = new ParserVal(new ArbolSintactico("Sentencia", ((ArbolSintactico)val_peek(0).obj), null)); }} yyval.ival = val_peek(0).ival;}
break;
case 12:
//#line 47 "gramatica.y"
{ yyval = val_peek(1);}
break;
case 13:
//#line 48 "gramatica.y"
{ yyval.obj = null; yyval.ival = val_peek(0).ival; }
break;
case 14:
//#line 49 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta ; al final de la sentencia ejecutable"); yyval.ival = val_peek(0).ival;}
break;
case 15:
//#line 50 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta ; al final de la declaración"); yyval.ival = val_peek(0).ival;}
break;
case 16:
//#line 53 "gramatica.y"
{ if (!error()) {
                                                                                    ((ArbolSintactico)val_peek(2).obj).setUltimoDerecho(new ArbolSintactico("Sentencia", ((ArbolSintactico)val_peek(1).obj) , null ));
                                                                                    yyval = val_peek(2); 
                                                                                    }
                                                                                    yyval.ival = val_peek(0).ival;
                                                                                  }
break;
case 17:
//#line 59 "gramatica.y"
{ if (!error()) { yyval = new ParserVal(new ArbolSintactico("Sentencia", ((ArbolSintactico)val_peek(1).obj), null));} yyval.ival = val_peek(0).ival;}
break;
case 18:
//#line 60 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta ; al final de la sentencia ejecutable"); yyval.ival = val_peek(0).ival;}
break;
case 19:
//#line 61 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta ; al final de la sentencia ejecutable"); yyval.ival = val_peek(0).ival;}
break;
case 20:
//#line 64 "gramatica.y"
{ yyval = val_peek(0); }
break;
case 21:
//#line 65 "gramatica.y"
{ yyval = val_peek(0); }
break;
case 22:
//#line 66 "gramatica.y"
{ erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + val_peek(0).ival + ": No está permitida la invocación de funciones sin utilizar su retorno"); }
break;
case 23:
//#line 67 "gramatica.y"
{ if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un print");
                                                                                yyval = val_peek(0);
                                                                              }
break;
case 24:
//#line 70 "gramatica.y"
{ if(enFunc==0) {erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + val_peek(0).ival + ": No está permitida la invocación de función lambda sin utilizar un retorno ni fuera de una función");} 
                                                                                else { erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + val_peek(0).ival + ": No está permitida la invocación de funciones sin utilizar su retorno"); } 
                                                                              }
break;
case 25:
//#line 73 "gramatica.y"
{chequeoSentenciaReturn(val_peek(0)); yyval = val_peek(0);}
break;
case 26:
//#line 76 "gramatica.y"
{ if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó una declaración de variables");}
break;
case 27:
//#line 77 "gramatica.y"
{String idFuncRaw = ((Simbolo)val_peek(0).obj).getLexema(); renombrarVariable(val_peek(0)); stringAmbitos.append(":"+idFuncRaw); stackTipoFuncActual.push(val_peek(1).sval); enDeclaracionParam = true;}
break;
case 28:
//#line 77 "gramatica.y"
{enDeclaracionParam = false; stackParametrosActuales.push(parametrosActuales); cambiarASimboloFuncion((Simbolo)val_peek(2).obj); parametrosActuales = new ArrayList<>(); enFunc++;}
break;
case 29:
//#line 77 "gramatica.y"
{  
                                                                                                                                                                                                                                                                    finFunc(val_peek(0)); 
                                                                                                                                                                                                                                                                    quitarAmbito(); 
                                                                                                                                                                                                                                                                    if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó una declaración de función");
                                                                                                                                                                                                                                                                    if (!error()) {
                                                                                                                                                                                                                                                                      mapaFunciones.put(((Simbolo)val_peek(6).obj).getLexema(), (ArbolSintactico)val_peek(1).obj);
                                                                                                                                                                                                                                                                    }
                                                                                                                                                                                                                                                                    yyval.ival = val_peek(0).ival;
                                                                                                                                                                                                                                                                  }
break;
case 34:
//#line 96 "gramatica.y"
{ ((Simbolo)val_peek(0).obj).setTipo(val_peek(1).sval);
                                                                       renombrarVariable(val_peek(0)); 
                                                                       if (val_peek(1).sval.equals("Lambda")) {
                                                                          erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + val_peek(1).ival + ": el tipo 'Lambda' no puede utilizar la semántica de pasaje por Copia-Valor (CV)");
                                                                       }
                                                                       parametrosActuales.add(new Parametro(((Simbolo)val_peek(0).obj), "CV"));
                                                                     }
break;
case 35:
//#line 103 "gramatica.y"
{ ((Simbolo)val_peek(0).obj).setTipo(val_peek(1).sval);
                                                                       renombrarVariable(val_peek(0));
                                                                       if (val_peek(1).sval.equals("Lambda")) {
                                                                          parametrosActuales.add(new Parametro(((Simbolo)val_peek(0).obj), "CV"));
                                                                       } else parametrosActuales.add(new Parametro(((Simbolo)val_peek(0).obj), "CVR"));
                                                                     }
break;
case 36:
//#line 109 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el nombre del parámetro");
                                                                     }
break;
case 37:
//#line 111 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta el nombre del parámetro");
                                                                     }
break;
case 38:
//#line 113 "gramatica.y"
{ renombrarVariable(val_peek(0)); 
                                                                       parametrosActuales.add(new Parametro(((Simbolo)val_peek(0).obj), "CV")); 
                                                                       erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el tipo del parámetro");
                                                                     }
break;
case 39:
//#line 117 "gramatica.y"
{ renombrarVariable(val_peek(0)); 
                                                                       parametrosActuales.add(new Parametro(((Simbolo)val_peek(0).obj), "CVR")); 
                                                                       erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta el tipo del parámetro");
                                                                     }
break;
case 40:
//#line 123 "gramatica.y"
{yyval = new ParserVal((SimboloLong)val_peek(0).obj);}
break;
case 41:
//#line 126 "gramatica.y"
{ if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(1).ival + ": Se detectó una sentencia return");
                                                                              if (!error()) { yyval = new ParserVal(new ArbolSintactico("Return", (ArbolSintactico)val_peek(1).obj,  null)); } yyval.ival = val_peek(0).ival;
                                                                            }
break;
case 42:
//#line 129 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el paréntesis de cierre en sentencia return"); yyval.ival = val_peek(0).ival;}
break;
case 43:
//#line 130 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el paréntesis de apertura en sentencia return"); yyval.ival = val_peek(0).ival;}
break;
case 44:
//#line 131 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": faltan ambos paréntesis en sentencia return"); yyval.ival = val_peek(0).ival;}
break;
case 45:
//#line 132 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta de argumento en sentencia return"); yyval.ival = val_peek(0).ival;}
break;
case 46:
//#line 135 "gramatica.y"
{ if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó una asignación simple");
                                                                                            if (!error()) { yyval = new ParserVal( new ArbolSintactico("Asig", new ArbolSintactico(checkearAlcance(val_peek(2).sval, val_peek(2).ival)), (ArbolSintactico)val_peek(0).obj) ); }
                                                                                          }
break;
case 47:
//#line 138 "gramatica.y"
{ if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó una asignación múltiple");
                                                                                          if (!error()) {
                                                                                            if(((ArrayList<Simbolo>)val_peek(2).obj).size() >= ((ArrayList<Simbolo>)val_peek(0).obj).size()) {  
                                                                                              yyval = new ParserVal(generarAsigMultiple((ArrayList<Simbolo>)val_peek(2).obj, (ArrayList<Simbolo>)val_peek(0).obj));
                                                                                            }
                                                                                            else erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + val_peek(0).ival + ": La cantidad de constantes en la asignación múltiple es mayor a la cantidad de variables");
                                                                                          }
                                                                                          }
break;
case 48:
//#line 146 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": No se permite la asignación múltiple utilizando variables en el lado derecho");}
break;
case 49:
//#line 149 "gramatica.y"
{ yyval = val_peek(1); yyval.ival = val_peek(0).ival; }
break;
case 50:
//#line 150 "gramatica.y"
{ if (!error()) { yyval = new ParserVal(new ArbolSintactico("Sentencia", ((ArbolSintactico)val_peek(1).obj), null)); } yyval.ival = val_peek(0).ival; }
break;
case 51:
//#line 151 "gramatica.y"
{ yyval.obj = null; yyval.ival = val_peek(0).ival; }
break;
case 52:
//#line 154 "gramatica.y"
{inicioIf();}
break;
case 53:
//#line 154 "gramatica.y"
{finIf(val_peek(0)); yyval = val_peek(0); }
break;
case 54:
//#line 157 "gramatica.y"
{enDo=true;}
break;
case 55:
//#line 160 "gramatica.y"
{sinRetIf.push(sinRet);}
break;
case 56:
//#line 160 "gramatica.y"
{if(sinRetIf.pop()) {sinRet=true;} yyval = val_peek(0); }
break;
case 57:
//#line 163 "gramatica.y"
{ if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un bloque IF");
                                                                                                  if (!error()) { yyval = new ParserVal(new ArbolSintactico("If", (ArbolSintactico)val_peek(2).obj, new ArbolSintactico("CuerpoIf",(ArbolSintactico)val_peek(1).obj, null)) ); } yyval.ival = val_peek(0).ival;
                                                                                                }
break;
case 58:
//#line 166 "gramatica.y"
{ if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un bloque IF-ELSE");
                                                                                                  if (!error()) { yyval = new ParserVal(new ArbolSintactico("IfElse", (ArbolSintactico)val_peek(4).obj, new ArbolSintactico("CuerpoIfElse",(ArbolSintactico)val_peek(3).obj, (ArbolSintactico)val_peek(1).obj)) );}  yyval.ival = val_peek(0).ival;
                                                                                                }
break;
case 59:
//#line 169 "gramatica.y"
{ enDo=false;
                                                                                                  if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un bloque DO UNTIL");
                                                                                                  if (!error()) { yyval = new ParserVal(new ArbolSintactico("DoUntil", (ArbolSintactico)val_peek(0).obj, (ArbolSintactico)val_peek(2).obj));  }  yyval.ival = val_peek(0).ival;
                                                                                                }
break;
case 60:
//#line 174 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta la palabra ENDIF al final del bloque IF-ELSE"); yyval.ival = val_peek(0).ival;}
break;
case 61:
//#line 175 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta la palabra ENDIF al final del bloque IF"); yyval.ival = val_peek(0).ival;}
break;
case 62:
//#line 176 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta el cuerpo en el bloque ELSE"); yyval.ival = val_peek(0).ival;}
break;
case 63:
//#line 177 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta el cuerpo en el bloque IF"); yyval.ival = val_peek(0).ival;}
break;
case 64:
//#line 178 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta el cuerpo en el bloque IF"); yyval.ival = val_peek(0).ival;}
break;
case 65:
//#line 179 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta el cuerpo en los bloques IF y ELSE"); yyval.ival = val_peek(0).ival;}
break;
case 66:
//#line 181 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(2).ival + ": falta de cuerpo en iteracion"); yyval.ival = val_peek(2).ival;}
break;
case 67:
//#line 182 "gramatica.y"
{ enDo=false;
                                                                                                  erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta la palabra UNTIL en la iteracion"); yyval.ival = val_peek(0).ival;
                                                                                                }
break;
case 68:
//#line 188 "gramatica.y"
{ if (!error()) { yyval = new ParserVal (new ArbolSintactico("Print", (ArbolSintactico)val_peek(1).obj, null)); } yyval.ival = val_peek(0).ival; }
break;
case 69:
//#line 189 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta de argumento en sentencia print");  yyval.ival = val_peek(0).ival; }
break;
case 70:
//#line 190 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el paréntesis de apertura en sentencia print");  yyval.ival = val_peek(0).ival; }
break;
case 71:
//#line 191 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el paréntesis de cierre en sentencia print");  yyval.ival = val_peek(1).ival; }
break;
case 72:
//#line 192 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": faltan ambos paréntesis en sentencia print");  yyval.ival = val_peek(0).ival; }
break;
case 73:
//#line 195 "gramatica.y"
{ yyval = val_peek(0); }
break;
case 74:
//#line 196 "gramatica.y"
{ yyval = val_peek(0); }
break;
case 75:
//#line 198 "gramatica.y"
{ yyval = val_peek(1); yyval.ival = val_peek(0).ival; }
break;
case 76:
//#line 199 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta el paréntesis de apertura de la condición");}
break;
case 77:
//#line 200 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta el paréntesis de cierre de la condición");}
break;
case 78:
//#line 201 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": faltan ambos paréntesis de la condición");}
break;
case 79:
//#line 204 "gramatica.y"
{ if (!error()) { yyval = new ParserVal (new ArbolSintactico(val_peek(1).sval, (ArbolSintactico)val_peek(2).obj, (ArbolSintactico)val_peek(0).obj)); } }
break;
case 80:
//#line 205 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": La condición es incompleta");}
break;
case 81:
//#line 208 "gramatica.y"
{ yyval = new ParserVal("Mayor"); }
break;
case 82:
//#line 209 "gramatica.y"
{ yyval = new ParserVal("Menor"); }
break;
case 83:
//#line 210 "gramatica.y"
{ yyval = new ParserVal("MayorIgual"); }
break;
case 84:
//#line 211 "gramatica.y"
{ yyval = new ParserVal("MenorIgual"); }
break;
case 85:
//#line 212 "gramatica.y"
{ yyval = new ParserVal("Igual"); }
break;
case 86:
//#line 213 "gramatica.y"
{ yyval = new ParserVal("Distinto"); }
break;
case 87:
//#line 216 "gramatica.y"
{ yyval = new ParserVal("Long"); }
break;
case 88:
//#line 217 "gramatica.y"
{ yyval = new ParserVal("Lambda");
                                                      if (!enDeclaracionParam) {
                                                        erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": el tipo 'Lambda' solo puede utilizarse en la declaración de parámetros formales de funciones estándar");
                                                      }
                                                    }
break;
case 89:
//#line 224 "gramatica.y"
{renombrarVariable(val_peek(0));}
break;
case 90:
//#line 225 "gramatica.y"
{renombrarVariable(val_peek(0));}
break;
case 91:
//#line 228 "gramatica.y"
{ if (!error()) {
                                                                          yyval = val_peek(2); 
                                                                          ((ArrayList<Simbolo>)yyval.obj).add( checkearAlcance(val_peek(0).sval, val_peek(0).ival) );
                                                                        }
                                                                      }
break;
case 92:
//#line 233 "gramatica.y"
{ if (!error()) {
                                                                          yyval = new ParserVal( new ArrayList<Simbolo>() );
                                                                          ((ArrayList<Simbolo>)yyval.obj).add( checkearAlcance(val_peek(0).sval, val_peek(0).ival) );
                                                                        }
                                                                      }
break;
case 93:
//#line 240 "gramatica.y"
{ yyval = new ParserVal( ((SimboloLong)val_peek(0).obj).getLexema() );
                                                                        yyval.ival = val_peek(0).ival; 
                                                                      }
break;
case 94:
//#line 243 "gramatica.y"
{ yyval = new ParserVal( val_peek(2).sval + "." + ((SimboloLong)val_peek(0).obj).getLexema() );
                                                                        yyval.ival = val_peek(0).ival;
                                                                      }
break;
case 95:
//#line 249 "gramatica.y"
{ enParamInvocacion = false;
                                                                                                                if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó la invocación de una función");
                                                                                                                if (!error()) {
                                                                                                                  ((ArbolSintactico)val_peek(2).obj).setUltimoDerecho((ArbolSintactico)val_peek(1).obj); 
                                                                                                                  yyval = val_peek(2);
                                                                                                                }
                                                                                                                yyval.ival = val_peek(0).ival;
                                                                                                              }
break;
case 96:
//#line 257 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": faltan los argumentos en la invocación de función"); 
                                                                                                                enParamInvocacion = false;
                                                                                                                yyval.ival = val_peek(0).ival; 
                                                                                                              }
break;
case 97:
//#line 263 "gramatica.y"
{ enParamInvocacion = false;
                                                                                                                if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó la invocación de una función lambda");
                                                                                                                chequeo_anid_lambda(val_peek(2).sval, val_peek(0));
                                                                                                                if (!error()) {
                                                                                                                  ((ArbolSintactico)val_peek(2).obj).setUltimoDerecho( new ArbolSintactico((Simbolo)val_peek(1).obj) );
                                                                                                                  yyval = val_peek(2);
                                                                                                                  ((ArbolSintactico)yyval.obj).setCategoria("CallLambda");
                                                                                                                }
                                                                                                                yyval.ival = val_peek(0).ival;
                                                                                                              }
break;
case 98:
//#line 273 "gramatica.y"
{ enParamInvocacion = false;
                                                                                                                restriccionCteFloat(val_peek(1));
                                                                                                                chequeo_anid_lambda(val_peek(2).sval, val_peek(0)); 
                                                                                                                constantePositiva(val_peek(1)); 
                                                                                                                if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó la invocación de una función lambda");
                                                                                                                if (!error()) {
                                                                                                                  ((ArbolSintactico)val_peek(2).obj).setUltimoDerecho( new ArbolSintactico((Simbolo)val_peek(1).obj) );
                                                                                                                  ((ArbolSintactico)yyval.obj).setCategoria("CallLambda");
                                                                                                                }
                                                                                                                yyval = val_peek(2); yyval.ival = val_peek(0).ival;
                                                                                                              }
break;
case 99:
//#line 284 "gramatica.y"
{ enParamInvocacion = false;
                                                                                                                restriccionCteFloat(val_peek(1));
                                                                                                                chequeo_anid_lambda(val_peek(3).sval, val_peek(0)); 
                                                                                                                constanteNegativa(val_peek(1)); 
                                                                                                                if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó la invocación de una función lambda");
                                                                                                                if (!error()) {
                                                                                                                  ((ArbolSintactico)val_peek(3).obj).setUltimoDerecho( new ArbolSintactico((Simbolo)val_peek(1).obj) );
                                                                                                                  yyval = val_peek(3);
                                                                                                                  ((ArbolSintactico)yyval.obj).setCategoria("CallLambda");
                                                                                                                }
                                                                                                                yyval.ival = val_peek(0).ival;
                                                                                                              }
break;
case 100:
//#line 296 "gramatica.y"
{ enParamInvocacion = false;
                                                                                                                constantePositiva(val_peek(2));
                                                                                                                chequeo_anid_lambda(val_peek(5).sval, val_peek(0)); 
                                                                                                                if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó la invocación de una función lambda");
                                                                                                                if (!error()) {
                                                                                                                  ((ArbolSintactico)val_peek(5).obj).setUltimoDerecho( new ArbolSintactico("Trunc", new ArbolSintactico((Simbolo)val_peek(2).obj), null) );
                                                                                                                  yyval = val_peek(5);
                                                                                                                  ((ArbolSintactico)yyval.obj).setCategoria("CallLambda");
                                                                                                                }
                                                                                                                yyval.ival = val_peek(0).ival;
                                                                                                              }
break;
case 101:
//#line 307 "gramatica.y"
{ enParamInvocacion = false;
                                                                                                                constanteNegativa(val_peek(2)); 
                                                                                                                chequeo_anid_lambda(val_peek(6).sval, val_peek(0));
                                                                                                                if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó la invocación de una función lambda");
                                                                                                                if (!error()) {
                                                                                                                  ((ArbolSintactico)val_peek(6).obj).setUltimoDerecho( new ArbolSintactico("Trunc", new ArbolSintactico((Simbolo)val_peek(2).obj), null) );
                                                                                                                  yyval = val_peek(6);
                                                                                                                  ((ArbolSintactico)yyval.obj).setCategoria("CallLambda");
                                                                                                                }
                                                                                                                yyval.ival = val_peek(0).ival; 
                                                                                                              }
break;
case 102:
//#line 320 "gramatica.y"
{yyval = new ParserVal(checkearAlcance(val_peek(0).sval, val_peek(0).ival) );}
break;
case 103:
//#line 323 "gramatica.y"
{ enParamInvocacion = true; 
                                                                                                  val_peek(1).obj = checkearAlcance(val_peek(1).sval, val_peek(0).ival);
                                                                                                  if (val_peek(1).obj != null) {
                                                                                                    if (!error()) {
                                                                                                      yyval = new ParserVal(new ArbolSintactico("Call", new ArbolSintactico((Simbolo)val_peek(1).obj), null)); 
                                                                                                      yyval.sval = ((Simbolo)val_peek(1).obj).getLexema();
                                                                                                    }
                                                                                                    funcionInvocadaActual = (Simbolo) val_peek(1).obj;
                                                                                                  }
                                                                                                }
break;
case 104:
//#line 335 "gramatica.y"
{ if (!error()) {
                                                                                yyval = new ParserVal(new ArbolSintactico("ListaParametros", (ArbolSintactico)((ArbolSintactico[])val_peek(0).obj)[0], null) ); 
                                                                                for (int i = 1; i < ((ArbolSintactico[])val_peek(0).obj).length; i++) {
                                                                                  ((ArbolSintactico)yyval.obj).setUltimoDerecho( new ArbolSintactico("ListaParametros", ((ArbolSintactico)((ArbolSintactico[])val_peek(0).obj)[i]), null) );
                                                                                }
                                                                              }
                                                                            }
break;
case 105:
//#line 344 "gramatica.y"
{ if (!error()) { yyval = val_peek(2); ((ArbolSintactico[])yyval.obj)[((SimboloFuncion)funcionInvocadaActual).getPosParametro(val_peek(0).sval)] = (ArbolSintactico)val_peek(0).obj; } }
break;
case 106:
//#line 345 "gramatica.y"
{ if (!error()) {
                                                                                yyval = new ParserVal(new ArbolSintactico[((SimboloFuncion)funcionInvocadaActual).getCantidadParametros()]);
                                                                                ((ArbolSintactico[])yyval.obj)[((SimboloFuncion)funcionInvocadaActual).getPosParametro(val_peek(0).sval)] = (ArbolSintactico)val_peek(0).obj;
                                                                              }
                                                                            }
break;
case 107:
//#line 352 "gramatica.y"
{ if (!error()) {
                                                                                                                checkeoParametroDeInvocacion(((Simbolo)val_peek(0).obj).getLexema(), val_peek(0).ival, (ArbolSintactico)val_peek(2).obj); 
                                                                                                                yyval = new ParserVal( (ArbolSintactico)val_peek(2).obj );
                                                                                                                yyval.sval = ((Simbolo)val_peek(0).obj).getLexema();
                                                                                                              }
                                                                                                            }
break;
case 108:
//#line 358 "gramatica.y"
{ if (!error()) {
                                                                                                                funcLambdaDeclaradas++;
                                                                                                                finFunc(val_peek(0));
                                                                                                                tablaSimbolos.remove(val_peek(5).sval);
                                                                                                                checkeoParametroDeInvocacion(((Simbolo)val_peek(0).obj).getLexema(), val_peek(0).ival, (ArbolSintactico)val_peek(3).obj);  
                                                                                                                yyval = new ParserVal(new ArbolSintactico( "DeclaracionLambda", (ArbolSintactico)val_peek(3).obj , null));
                                                                                                                yyval.sval = ((Simbolo)val_peek(0).obj).getLexema();
                                                                                                              }
                                                                                                            }
break;
case 109:
//#line 367 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta llave de cierre del cuerpo de la función Lambda");}
break;
case 110:
//#line 368 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(2).ival + ": falta llave de apertura del cuerpo de la función Lambda");}
break;
case 111:
//#line 369 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": faltan ambas llaves del cuerpo de la función Lambda");}
break;
case 112:
//#line 370 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": No se especifica el parametro formal al que corresponde el parámetro real");}
break;
case 113:
//#line 371 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": No se especifica el parametro formal al que corresponde el parámetro real");}
break;
case 114:
//#line 374 "gramatica.y"
{ enFunc++;
                                                if (val_peek(2).sval.equals("Lambda")) {
                                                  erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + val_peek(0).ival + ": el tipo 'Lambda' no puede ser utilizado como el tipo del parámetro en la declaración de funciones Lambda");
                                                }
                                                String lexema = ((SimboloLong)val_peek(1).obj).getLexema();
                                                if (tablaSimbolos.containsKey("lambda:" + lexema)) {
                                                  erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + val_peek(1).ival + ": la variable '" + lexema + "' ya ha sido declarada");
                                                }
                                                tablaSimbolos.remove(lexema);
                                                tablaSimbolos.put("lambda:" + lexema,new SimboloLong("Long", "Identificador", "lambda:" + lexema, 0L));
                                                yyval = new ParserVal("lambda:" + lexema);
                                              }
break;
case 115:
//#line 386 "gramatica.y"
{ enFunc++;
                                                erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el paréntesis de apertura en la declaración del argumento de la función Lambda");
                                                if (val_peek(2).sval.equals("Lambda")) {
                                                  erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + val_peek(0).ival + ": el tipo 'Lambda' no puede ser utilizado como el tipo del parámetro en la declaración de funciones Lambda");
                                                }
                                                String lexema = ((SimboloLong)val_peek(1).obj).getLexema();
                                                if (tablaSimbolos.containsKey("lambda:" + lexema)) {
                                                  erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + val_peek(1).ival + ": la variable '" + lexema + "' ya ha sido declarada");
                                                }
                                                tablaSimbolos.remove(lexema);
                                                tablaSimbolos.put("lambda:" + lexema,new SimboloLong("Long", "Identificador", "lambda:" + lexema, 0L));
                                                yyval = new ParserVal("lambda:" + lexema);
                                              }
break;
case 116:
//#line 399 "gramatica.y"
{ enFunc++;
                                              erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta el paréntesis de cierre en la declaración del argumento de la función Lambda");
                                              if (val_peek(1).sval.equals("Lambda")) {
                                                erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + val_peek(0).ival + ": el tipo 'Lambda' no puede ser utilizado como el tipo del parámetro en la declaración de funciones Lambda");
                                              }
                                              String lexema = ((SimboloLong)val_peek(1).obj).getLexema();
                                                if (tablaSimbolos.containsKey("lambda:" + lexema)) {
                                                  erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + val_peek(1).ival + ": la variable '" + lexema + "' ya ha sido declarada");
                                                }
                                                tablaSimbolos.remove(lexema);
                                                tablaSimbolos.put("lambda:" + lexema,new SimboloLong("Long", "Identificador", "lambda:" + lexema, 0L));
                                                yyval = new ParserVal("lambda:" + lexema);
                                            }
break;
case 117:
//#line 414 "gramatica.y"
{ if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(1).ival + ": Se detectó una expresión desde una suma");
                                                                      if (!error()) {
                                                                        yyval = new ParserVal(new ArbolSintactico("Suma", (ArbolSintactico)val_peek(2).obj, (ArbolSintactico)val_peek(0).obj));
                                                                      }
                                                                      yyval.ival = val_peek(1).ival;
                                                                    }
break;
case 118:
//#line 420 "gramatica.y"
{ if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(1).ival + ": Se detectó una expresión desde una resta");
                                                                      if (!error()) { yyval = new ParserVal(new ArbolSintactico("Resta", (ArbolSintactico)val_peek(2).obj, (ArbolSintactico)val_peek(0).obj));} yyval.ival = val_peek(1).ival; 
                                                                    }
break;
case 119:
//#line 423 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la izquierda de '+'"); }
break;
case 120:
//#line 424 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la derecha de '+'"); }
break;
case 121:
//#line 425 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la izquierda de '-'"); }
break;
case 122:
//#line 426 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la derecha de '-'"); }
break;
case 123:
//#line 427 "gramatica.y"
{ if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó una expresión desde un término");    
                                                                      yyval = val_peek(0);
                                                                    }
break;
case 124:
//#line 432 "gramatica.y"
{ if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(1).ival + ": Se detectó un término desde una multiplicación");
                                                                      if (!error()) { yyval = new ParserVal(new ArbolSintactico("Multiplicacion", (ArbolSintactico)val_peek(2).obj, (ArbolSintactico)val_peek(0).obj));} yyval.ival = val_peek(1).ival;
                                                                    }
break;
case 125:
//#line 435 "gramatica.y"
{ if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(1).ival + ": Se detectó un término desde una división");
                                                                      if (!error()) { yyval = new ParserVal(new ArbolSintactico("Division", (ArbolSintactico)val_peek(2).obj, (ArbolSintactico)val_peek(0).obj)); } yyval.ival = val_peek(1).ival; 
                                                                    }
break;
case 126:
//#line 438 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la izquierda de '*'"); }
break;
case 127:
//#line 439 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la derecha de '*'"); }
break;
case 128:
//#line 440 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la izquierda de '/'"); }
break;
case 129:
//#line 441 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la derecha de '/'"); }
break;
case 130:
//#line 442 "gramatica.y"
{ if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un término desde un factor");
                                                                      yyval = val_peek(0); 
                                                                    }
break;
case 131:
//#line 445 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operador/comparador entre término y factor"); }
break;
case 132:
//#line 448 "gramatica.y"
{ constantePositiva(val_peek(0)); if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un factor desde una constante");
                                                                      restriccionCteFloat(val_peek(0));
                                                                      if (!error()) { yyval = new ParserVal(new ArbolSintactico((Simbolo)val_peek(0).obj));} yyval.ival = val_peek(0).ival;
                                                                    }
break;
case 133:
//#line 452 "gramatica.y"
{ constanteNegativa(val_peek(0)); if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un factor desde una constante negativa");
                                                                      restriccionCteFloat(val_peek(0));
                                                                      if (!error()) { yyval = new ParserVal(new ArbolSintactico((Simbolo)val_peek(0).obj));} yyval.ival = val_peek(0).ival;
                                                                    }
break;
case 134:
//#line 456 "gramatica.y"
{ if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un factor desde un identificador");
                                                                      if (!error()) { yyval = new ParserVal(new ArbolSintactico(checkearAlcance(val_peek(0).sval, val_peek(0).ival)));} yyval.ival = val_peek(0).ival;
                                                                    }
break;
case 135:
//#line 459 "gramatica.y"
{ if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un factor desde una función");
                                                                      yyval = val_peek(0); 
                                                                    }
break;
case 136:
//#line 462 "gramatica.y"
{ if(enFunc==0) {erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + val_peek(0).ival + ": Invocacion de funcion lambda fuera de una función");} else {yyval = val_peek(0);}}
break;
case 137:
//#line 463 "gramatica.y"
{ constantePositiva(val_peek(1)); 
                                                                      if (enParamInvocacion) {
                                                                        if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(3).ival + ": Se detectó la función Trunc en uno de los parámetros reales de la invocación");
                                                                        if (!error()) {
                                                                          yyval = new ParserVal(new ArbolSintactico("Trunc", new ArbolSintactico((Simbolo)val_peek(1).obj), null));
                                                                        }
                                                                        yyval.ival = val_peek(3).ival;
                                                                      }
                                                                      else  
                                                                        erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + val_peek(3).ival + ": la función TRUNC solo puede utilizarse en parámetros reales de invocación de funciones");
                                                                    }
break;
case 138:
//#line 474 "gramatica.y"
{ constanteNegativa(val_peek(1)); 
                                                                      if (enParamInvocacion) {
                                                                        if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(4).ival + ": Se detectó la función Trunc en uno de los parámetros reales de la invocación");
                                                                        if (!error()) {
                                                                          yyval = new ParserVal(new ArbolSintactico("Trunc", new ArbolSintactico((Simbolo)val_peek(1).obj), null));
                                                                        }
                                                                        yyval.ival = val_peek(4).ival;
                                                                      }
                                                                      else  
                                                                        erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + val_peek(4).ival + ": la función TRUNC solo puede utilizarse en parámetros reales de invocación de funciones");
                                                                    }
break;
case 139:
//#line 487 "gramatica.y"
{ constantePositiva(val_peek(0)); restriccionCteFloat(val_peek(0)); yyval = val_peek(2); ((ArrayList<Simbolo>)yyval.obj).add((Simbolo)val_peek(0).obj); yyval.ival = val_peek(0).ival; }
break;
case 140:
//#line 488 "gramatica.y"
{ constanteNegativa(val_peek(0)); restriccionCteFloat(val_peek(0)); yyval = val_peek(3); ((ArrayList<Simbolo>)yyval.obj).add((Simbolo)val_peek(0).obj); yyval.ival = val_peek(0).ival; }
break;
case 141:
//#line 489 "gramatica.y"
{ constantePositiva(val_peek(0)); restriccionCteFloat(val_peek(0)); yyval = new ParserVal(new ArrayList<Simbolo>()); ((ArrayList<Simbolo>)yyval.obj).add((Simbolo)val_peek(0).obj); yyval.ival = val_peek(0).ival; }
break;
case 142:
//#line 490 "gramatica.y"
{ constanteNegativa(val_peek(0)); restriccionCteFloat(val_peek(0)); yyval = new ParserVal(new ArrayList<Simbolo>()); ((ArrayList<Simbolo>)yyval.obj).add((Simbolo)val_peek(0).obj); yyval.ival = val_peek(0).ival; }
break;
case 143:
//#line 491 "gramatica.y"
{ constantePositiva(val_peek(0)); restriccionCteFloat(val_peek(0)); erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta ',' entre constantes"); constantePositiva(val_peek(0)); restriccionCteFloat(val_peek(0)); }
break;
case 144:
//#line 492 "gramatica.y"
{ constanteNegativa(val_peek(0)); restriccionCteFloat(val_peek(0)); erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(2).ival + ": falta ',' entre constantes"); constanteNegativa(val_peek(0)); restriccionCteFloat(val_peek(0)); }
break;
case 145:
//#line 493 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": la variable '" + val_peek(0).sval + "' no puede estar en una lista de constantes");}
break;
//#line 1939 "Parser.java"
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
