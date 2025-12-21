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
public final static short YYERRCODE=256;
final static short yylhs[] = {                           -1,
    2,    3,    0,    0,    1,    1,    1,    1,    1,    4,
    4,    5,    5,    5,    5,    5,    8,    8,    8,    8,
    6,    6,    6,    6,    6,    6,    7,   19,   20,    7,
   18,   18,   21,   21,   22,   22,   22,   22,   22,   22,
   17,   14,   14,   14,   14,   14,    9,    9,    9,    9,
   28,   28,   28,   30,   29,   31,   31,   31,   33,   32,
   10,   10,   10,   10,   10,   10,   10,   10,   10,   10,
   10,   10,   12,   12,   12,   12,   12,   35,   35,   34,
   34,   34,   34,   36,   36,   37,   37,   37,   37,   37,
   37,   15,   15,   16,   16,   25,   25,   24,   24,   11,
   11,   13,   13,   13,   13,   13,   40,   38,   39,   41,
   41,   42,   42,   42,   42,   42,   42,   42,   43,   43,
   43,   23,   23,   23,   23,   23,   23,   23,   44,   44,
   44,   44,   44,   44,   44,   44,   45,   45,   45,   45,
   45,   45,   45,   26,   26,   26,   26,   26,   26,   26,
   27,
};
final static short yylen[] = {                            2,
    0,    0,    5,    2,    3,    2,    2,    2,    1,    2,
    1,    2,    2,    1,    1,    2,    3,    2,    2,    1,
    1,    1,    1,    1,    1,    1,    2,    0,    0,    8,
    3,    2,    3,    1,    3,    2,    2,    1,    2,    1,
    1,    4,    3,    3,    2,    3,    3,    3,    3,    3,
    3,    2,    2,    0,    2,    4,    3,    3,    0,    2,
    4,    6,    3,    4,    5,    3,    5,    5,    3,    4,
    3,    2,    4,    3,    3,    3,    2,    1,    1,    3,
    2,    2,    1,    3,    1,    1,    1,    1,    1,    1,
    1,    1,    1,    3,    1,    3,    1,    1,    3,    3,
    2,    3,    3,    4,    6,    7,    1,    2,    1,    3,
    1,    3,    6,    5,    5,    4,    5,    2,    4,    3,
    3,    3,    3,    3,    3,    3,    3,    1,    3,    3,
    3,    3,    3,    3,    1,    2,    1,    2,    1,    1,
    1,    4,    5,    3,    4,    1,    2,    2,    3,    3,
    3,
};
final static short yydefred[] = {                         0,
    0,    0,    0,   92,    0,    0,    0,   93,    0,    0,
    0,    0,   11,    0,    0,   21,   22,   23,   24,   25,
   26,    0,    0,    0,    0,    0,   16,    0,    0,   98,
  137,    0,    0,    0,  140,  141,    0,    0,    0,    0,
    0,  135,    0,    0,    0,    0,    0,   79,    0,    0,
    0,    6,    0,    4,    8,   10,   12,   13,    0,    0,
   28,    0,  108,    0,    0,    0,    0,   72,    0,    0,
    0,  101,    0,    0,    0,    0,    0,    0,    0,  111,
    0,    2,    0,    0,    0,    0,    0,    0,  138,   88,
   89,   90,   91,   86,   87,    0,    0,    0,    0,   69,
    0,    0,   81,  136,    0,    0,   46,    0,   44,   71,
   57,    0,    0,    0,   58,   74,    0,   75,    5,    0,
    0,    0,   99,    0,  146,    0,    0,    0,    0,   50,
   63,  103,    0,    0,    0,    0,    0,  100,  102,    0,
    0,    0,    0,    0,    0,    0,  131,  133,    0,    0,
   80,    0,    0,    0,    0,    0,   70,    0,   54,    0,
   61,    0,    0,   55,  132,  129,  134,  130,   42,   18,
   56,    0,   64,   73,   94,    0,   29,  147,    0,  148,
    0,    0,    0,    0,    0,  104,  120,  112,  110,    0,
    0,    0,    3,  142,    0,   68,   60,   67,    0,   53,
    0,   52,   17,   40,    0,   32,    0,    0,   34,    0,
    0,  144,    0,    0,  149,    0,    0,  119,    0,    0,
  116,    0,  143,   62,   51,   39,    0,   36,   31,    0,
    0,  145,  105,    0,  114,    0,  115,   35,   33,    0,
  106,  113,   30,
};
final static short yydgoto[] = {                         10,
   11,   28,  143,   12,   13,   14,   15,  113,   16,   17,
   35,   19,   36,   21,   22,   60,   61,  177,  121,  210,
  208,  209,   37,   38,   24,  129,  130,  164,  101,  102,
   25,  158,  159,   39,   51,   40,   98,   26,   77,   78,
   79,   80,   81,   41,   42,
};
final static short yysindex[] = {                       596,
  -59,    0,  357,    0,  437,  187,  228,    0,  949,    0,
 -255,  963,    0,   -9,   26,    0,    0,    0,    0,    0,
    0, -197,  -22,   60,  264,  183,    0,  638,  158,    0,
    0,   33,  206, -168,    0,    0,  741,   15, -131,   54,
   -4,    0,  333,  116,  357, 1087,  -43,    0,  284,  -11,
   59,    0,  981,    0,    0,    0,    0,    0,    0,   64,
    0,  206,    0, -137, -116,  -37,  357,    0,   85,  110,
 -223,    0, -107,  -99,  -20,   15,  125,  129,  127,    0,
  931,    0,  287,  287,   -8,   -8,   23,  132,    0,    0,
    0,    0,    0,    0,    0,  467,  490,  206,  -77,    0,
 -123, 1005,    0,    0,  493,  525,    0,  317,    0,    0,
    0,  118, 1089,  357,    0,    0,  145,    0,    0,  -68,
  156,  -11,    0,  152,    0,  -51,  152,  166,  -25,    0,
    0,    0,   30,  -41,  173,  184,  -17,    0,    0,  -10,
 -120,  -85,  -24,   10,   -4,   -4,    0,    0,  214,   22,
    0,   10,   -4,   10,   -4,  -11,    0,   -2,    0,   17,
    0, 1104,  204,    0,    0,    0,    0,    0,    0,    0,
    0,  224,    0,    0,    0,   88,    0,    0,  -37,    0,
   19,   42,  243,   45,  281,    0,    0,    0,    0, 1046,
   69,   61,    0,    0,  308,    0,    0,    0,   95,    0,
 1118,    0,    0,    0, -231,    0,   96,   21,    0,  238,
  -25,    0,  105,  152,    0,  324,  325,    0,  111,   97,
    0,  115,    0,    0,    0,    0,  120,    0,    0, -174,
 1133,    0,    0,  326,    0,  136,    0,    0,    0,  999,
    0,    0,    0,
};
final static short yyrindex[] = {                         0,
    0,    2,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  117,    0,  910,  925,    0,    0,    0,    0,    0,
    0,    0,   62,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,  349,   29, 1019,  620,
   51,    0,    0,  564,    0,    0,    0,    0,    0,  223,
  680,    0,  121,    0,    0,    0,    0,    0,  -38,  884,
    0,    0,    0,    0,    0,    0,    0,    0,  170,    0,
    0,    0,    0,    0,    0,  482,    0,    0,  358,    0,
    0,    0,    0,    0,    0,    0,    0,  662,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0, 1025,    0,
  698,    0,    0,    0,    0,    0,    0,  721,    0,    0,
    0, 1064,    0,    0,    0,    0,  754,    0,    0,    0,
    0,  776,    0,  502,    0,    0,  542,  800,  826,    0,
    0,    0,    0,    0,  505,    0,   36,    0,    0,    0,
    0,    0,    0,    0,   74,  119,    0,    0,    0,    0,
    0,  149,  311,  386,  408,  430,    0,    0,    0, 1025,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0, 1070,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0, 1039,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,  844,    0,
    0,    0,    0,    0,    0,    0,   68,    0,    0,    0,
  862,    0,    0,  460,    0,  550,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,   83,    0,    0,    0,
    0,    0,    0,  586,    0,   91,    0,    0,    0,    0,
    0,    0,    0,
};
final static short yygindex[] = {                         0,
  372,    0,    0,   -5,   16, 1034,    0,  -40,    0,    0,
    3,    0,    5,    0,  612,    0,    0,    0,    0,    0,
    0,  171, 1345,    1,  337,  225,    0,    0,  248,    0,
    0,  260,    0,   14,  374,  391,    0,    0,    0,    0,
    0,  290,    0,   72,   -7,
};
final static int YYTABLESIZE=1485;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         27,
   23,   41,   18,   53,   20,   95,   23,  126,   18,   23,
   20,   18,   23,   20,   18,  115,   20,   63,  181,  182,
   95,   54,   97,   64,   96,  226,   76,   56,   23,   71,
   18,   97,   20,   96,   34,    4,   34,  105,   68,  192,
  142,   98,  106,    4,    8,   98,   23,   98,   18,   57,
   20,   85,    8,   23,   63,   18,   86,   20,  110,   59,
   64,  229,   98,  213,  230,  124,  127,  150,   56,  139,
  139,  139,   87,  139,  184,  139,  118,  147,  148,  118,
  131,   23,  204,   18,   58,   20,   95,  139,  139,   89,
  139,  128,    4,  128,  103,  128,  205,  166,  168,  118,
  190,    8,   23,   65,   18,   97,   20,  120,   38,  128,
  128,   38,  128,   23,  126,   18,  126,   20,  126,  123,
   66,  201,   97,   37,    1,  132,   37,  173,  206,   99,
  100,  117,  126,  126,  117,  126,   30,  160,  161,    3,
   30,   23,   23,   18,   18,   20,   20,    5,    6,  133,
  135,  139,    7,  139,  145,  146,  109,  136,   97,  124,
   96,  124,   23,  124,   18,  138,   20,  153,  155,  139,
  140,   30,  151,  128,    3,  128,  170,  124,  124,  124,
  124,  214,    5,    6,  157,  174,  191,    7,  175,  127,
   23,  127,   18,  127,   20,  176,  126,   64,  126,   85,
   84,   23,   83,   18,   86,   20,  178,  127,  127,  179,
  127,  137,  137,  186,  137,  185,  137,   95,   95,   30,
  125,   95,   71,   72,  187,  240,  114,   73,   95,   95,
   95,   23,  180,   18,   95,   20,   62,   95,   95,  188,
   23,  124,   18,  124,   20,   29,   30,   31,   30,   31,
   34,  137,  193,  104,  194,   56,    4,    1,    1,  196,
   98,    1,  202,   78,   32,    8,   32,   49,    1,    1,
    1,  127,   34,  127,    1,   30,  212,    1,  198,  195,
  149,   78,  203,  216,  139,  139,  139,  183,  139,  139,
  139,  139,  139,  139,  139,  139,  139,  139,  139,  215,
  139,  139,  217,   33,  139,  139,  128,  128,   34,   46,
  128,  128,  128,  128,  128,  128,  128,  128,  128,  128,
  128,  218,  128,  128,  116,  221,  128,  128,   34,  126,
  126,   34,  222,  126,  126,  126,  126,  126,  126,  126,
  126,  126,  126,  126,  204,  126,  126,   78,  223,  126,
  126,  123,  228,  123,    4,  123,  224,  169,  205,   97,
  231,   96,  232,    8,  233,  234,  241,  235,  236,  123,
  123,  237,  123,  107,  124,  124,  238,   34,  124,  124,
  124,  124,  124,  124,  124,  124,  124,  124,  124,   85,
  124,  124,  242,    9,  124,  124,   33,    7,  109,   82,
  239,   34,  128,  211,  127,  127,  197,   85,  127,  127,
  127,  127,  127,  127,  127,  127,  127,  127,  127,  199,
  127,  127,  117,   88,  127,  127,  125,  137,  125,  189,
  125,    0,    0,  123,    0,  123,    0,    0,   29,   30,
   69,  137,    0,   30,  125,  125,    3,  125,  122,    4,
  122,    0,  122,    0,    5,    6,   45,   70,    8,    7,
    0,   29,   30,   31,    0,    0,  122,  122,    0,  122,
   84,   85,    0,   85,    0,    0,   43,    0,   78,   78,
   32,   34,   78,   29,   30,   31,    0,    0,   84,   78,
   78,   78,   78,    0,   78,   78,    0,    0,   78,   78,
    0,   48,   32,  150,  150,    0,    0,    0,  125,    0,
  125,   34,    0,    0,    0,    0,    0,    0,  150,   29,
   30,   31,  107,  139,  139,    0,  139,    0,  139,    0,
  122,    0,  122,   67,   34,    0,    0,   34,   32,   29,
   30,   31,  144,   30,   31,   96,  138,  138,    0,  138,
    0,  138,   84,    0,   84,    0,    0,   48,   32,    0,
   96,   32,   96,    0,    0,    0,  123,  123,    0,   34,
  123,  123,  123,  123,  123,  123,  123,  123,  123,  123,
  123,    0,  123,  123,  150,   97,  123,  123,   29,   30,
   31,  142,  142,    0,  142,    0,  142,    0,    0,    0,
   97,    0,    0,    0,   85,   85,    0,   32,   85,   85,
   85,    0,   29,   30,   31,   85,   85,   85,   85,    0,
   85,   85,   45,    0,   85,   85,   96,  143,  143,    0,
  143,   32,  143,    0,    0,    0,    0,   74,    0,    0,
    0,  125,  125,    0,    0,  125,  125,  125,  125,  125,
  125,  125,  125,  125,  125,  125,    0,  125,  125,    0,
    0,  125,  125,  122,  122,    0,   97,  122,  122,  122,
  122,  122,  122,  122,  122,  122,  122,  122,   83,  122,
  122,    0,  134,  122,  122,   84,   84,    0,   45,   84,
   84,   84,   29,   30,   31,    0,   84,   84,   84,   84,
    0,   84,   84,    0,    0,   84,   84,    0,    0,    0,
    0,   32,    0,    0,    0,  150,  150,  150,    9,  150,
   82,    0,  152,   30,   31,    0,  150,  150,  150,  150,
    0,  150,  150,    0,    0,  150,  150,    0,   77,  139,
    0,   32,   83,    0,   83,  154,   30,   31,  165,   30,
   31,   74,    0,  139,    0,    0,   66,   96,   96,    0,
    9,   96,  138,    0,   32,    0,    0,   32,   96,   96,
   96,   96,    0,   96,   96,    0,  138,   96,   96,   43,
  167,   30,   31,   97,   82,   96,   82,  207,    0,    0,
    0,    0,    0,    0,    0,    0,    0,   97,   97,   32,
   95,   97,   94,    0,   77,    0,    0,  142,   97,   97,
   97,   97,   76,   97,   97,    0,  227,   97,   97,   45,
   45,  142,   66,   45,    0,    0,    0,    0,    0,    0,
   45,   45,   45,   45,   47,   45,   45,    0,    0,   45,
   45,  207,    0,  143,    0,   43,    0,    0,    0,    0,
    0,    1,    2,    0,    0,    3,    0,  143,   49,    0,
    0,    0,    4,    5,    6,    0,    0,    0,    7,    0,
    0,    8,    0,    0,    0,   83,   83,    0,   76,   83,
   83,   83,    0,    0,   48,    0,   83,   83,   83,   83,
    0,   83,   83,    1,   30,   83,   83,    3,    0,    0,
   47,    0,   65,    0,    4,    5,    6,    0,    0,    0,
    7,    0,    0,    8,    0,    0,    0,   82,   82,    0,
  151,   82,   82,   82,   49,    0,    0,    0,   82,   82,
   82,   82,    0,   82,   82,   77,   77,   82,   82,   77,
    0,    0,   27,    0,    0,    0,   77,   77,   77,   77,
   48,   77,   77,   66,   66,   77,   77,   66,    0,    0,
    0,    0,    0,    0,   66,   66,   66,   66,   65,   66,
   66,    0,    0,   66,   66,    0,   43,   43,    0,    0,
   43,    0,    0,    0,    0,    0,  151,   43,   43,   43,
   43,    0,   43,   43,    0,    0,   43,   43,    0,    0,
    0,    0,    0,   90,   91,   92,   93,    0,   27,   76,
   76,    0,    0,   76,    0,    0,    0,    0,    0,    0,
   76,   76,   76,   76,    0,   76,   76,    0,    0,   76,
   76,   47,   47,    0,   14,   47,    0,    0,    0,   47,
    0,    0,   47,   47,   47,   47,    0,   47,   47,   15,
    0,   47,   47,  141,    0,   49,   49,    0,    0,   49,
    0,    0,    0,    0,    0,    0,   49,   49,   49,   49,
    0,   49,   49,   52,    0,   49,   49,    0,    0,  112,
    0,   48,   48,    0,    0,   48,    0,   55,    0,    0,
    0,    0,   48,   48,   48,   48,    0,   48,   48,   65,
   65,   48,   48,   65,    0,  119,    0,    0,    0,    0,
   65,   65,   65,   65,  112,   65,   65,  151,  151,   65,
   65,  151,    0,  243,    0,    0,    0,  162,  151,  151,
  151,  151,    0,  151,  151,  163,    0,  151,  151,   27,
   27,   54,    0,   27,    0,    0,  172,   59,    0,    0,
   27,   27,   27,    0,    0,    0,   27,    0,    0,   27,
   27,  121,    0,    0,    0,   14,   14,    0,    0,   14,
  220,    0,    0,    0,  112,  172,   14,   14,   14,    0,
   15,   15,   14,    0,   15,   14,   14,   30,   20,    0,
    3,   15,   15,   15,   19,  112,    0,   15,    5,    6,
   15,   15,    0,    7,    1,   30,    0,    0,    3,    0,
    0,  111,    0,  171,    0,    4,    5,    6,    1,   30,
    0,    7,    3,  172,    8,    0,    0,    0,  200,    4,
    5,    6,    0,    0,  172,    7,    1,   30,    8,    0,
    3,    0,  225,    0,    0,    0,    0,    4,    5,    6,
    0,    0,    0,    7,    1,   30,    8,    0,    3,    0,
    0,   30,    0,    0,    3,    4,    5,    6,    0,    0,
    0,    7,    5,    6,    8,   54,    0,    7,   54,    0,
    0,   59,    0,    0,   59,    0,   54,   54,    0,    0,
    0,   54,   59,   59,    0,  121,    0,   59,  121,    0,
    0,    0,   30,    0,    0,    3,  121,  121,    0,    0,
    0,  121,    0,    5,    6,    0,    0,  219,    7,    0,
   20,    0,    0,   20,    0,    0,   19,    0,    0,   19,
    0,   20,   20,    0,    0,   20,   20,   19,   19,    0,
    0,   19,   19,   30,    0,   30,    3,    0,    3,   44,
    0,   50,    0,    0,    5,    6,    5,    6,    0,    7,
   30,    7,    0,    3,    0,    0,    0,    0,    0,    0,
   75,    5,    6,    0,   30,    0,    7,    3,    0,    0,
    0,    0,    0,    0,    0,    5,    6,  108,    1,   30,
    7,    0,    3,   50,    0,    0,    0,    0,    0,    4,
    5,    6,    0,    0,    0,    7,  122,    0,    8,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,  156,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,   75,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         59,
    0,   40,    0,    9,    0,   44,    6,   45,    6,    9,
    6,    9,   12,    9,   12,   59,   12,   40,   44,   45,
   59,  277,   43,   46,   45,  257,   26,   12,   28,   40,
   28,   43,   28,   45,   45,  267,   45,   42,   25,  125,
   81,   40,   47,  267,  276,   44,   46,   46,   46,   59,
   46,   42,  276,   53,   40,   53,   47,   53,   45,  257,
   46,   41,   61,   45,   44,   65,   66,   45,   53,   41,
   42,   43,   40,   45,   45,   47,   41,   85,   86,   44,
   67,   81,  257,   81,   59,   81,  125,   59,   60,  258,
   62,   41,  267,   43,   41,   45,  271,  105,  106,   41,
  141,  276,  102,   44,  102,   44,  102,   44,   41,   59,
   60,   44,   62,  113,   41,  113,   43,  113,   45,  257,
   61,  162,   61,   41,  123,   41,   44,  114,   41,  261,
  262,   41,   59,   60,   44,   62,  257,  261,  262,  260,
  257,  141,  142,  141,  142,  141,  142,  268,  269,   40,
  258,  123,  273,  125,   83,   84,   41,  257,   43,   41,
   45,   43,  162,   45,  162,   41,  162,   96,   97,   41,
   44,  257,   41,  123,  260,  125,   59,   59,   60,  179,
   62,  181,  268,  269,  262,   41,  272,  273,  257,   41,
  190,   43,  190,   45,  190,   40,  123,   46,  125,   42,
   43,  201,   45,  201,   47,  201,  258,   59,   60,   44,
   62,   42,   43,   41,   45,  257,   47,  256,  257,  257,
  258,  260,   40,   41,   41,  231,  270,   45,  267,  268,
  269,  231,  258,  231,  273,  231,  259,  276,  277,  257,
  240,  123,  240,  125,  240,  256,  257,  258,  257,  258,
   45,  272,  277,  258,   41,  240,  267,  256,  257,  262,
  259,  260,   59,   41,  275,  276,  275,   40,  267,  268,
  269,  123,   45,  125,  273,  257,  258,  276,  262,  258,
  258,   59,   59,   41,  256,  257,  258,  258,  260,  261,
  262,  263,  264,  265,  266,  267,  268,  269,  270,  258,
  272,  273,  258,   40,  276,  277,  256,  257,   45,  123,
  260,  261,  262,  263,  264,  265,  266,  267,  268,  269,
  270,   41,  272,  273,   41,  257,  276,  277,   45,  256,
  257,   45,  272,  260,  261,  262,  263,  264,  265,  266,
  267,  268,  269,  270,  257,  272,  273,  125,   41,  276,
  277,   41,  257,   43,  267,   45,  262,   41,  271,   43,
  123,   45,  258,  276,   41,   41,   41,  257,  272,   59,
   60,  257,   62,   41,  256,  257,  257,   45,  260,  261,
  262,  263,  264,  265,  266,  267,  268,  269,  270,   41,
  272,  273,  257,  277,  276,  277,   40,  277,   41,   28,
  230,   45,   66,  179,  256,  257,  159,   59,  260,  261,
  262,  263,  264,  265,  266,  267,  268,  269,  270,  160,
  272,  273,   49,   33,  276,  277,   41,  258,   43,  140,
   45,   -1,   -1,  123,   -1,  125,   -1,   -1,  256,  257,
  258,  272,   -1,  257,   59,   60,  260,   62,   41,  267,
   43,   -1,   45,   -1,  268,  269,  270,  275,  276,  273,
   -1,  256,  257,  258,   -1,   -1,   59,   60,   -1,   62,
   41,  123,   -1,  125,   -1,   -1,   40,   -1,  256,  257,
  275,   45,  260,  256,  257,  258,   -1,   -1,   59,  267,
  268,  269,  270,   -1,  272,  273,   -1,   -1,  276,  277,
   -1,  274,  275,   44,   45,   -1,   -1,   -1,  123,   -1,
  125,   45,   -1,   -1,   -1,   -1,   -1,   -1,   59,  256,
  257,  258,   41,   42,   43,   -1,   45,   -1,   47,   -1,
  123,   -1,  125,  270,   45,   -1,   -1,   45,  275,  256,
  257,  258,  256,  257,  258,   44,   42,   43,   -1,   45,
   -1,   47,  123,   -1,  125,   -1,   -1,  274,  275,   -1,
   59,  275,   61,   -1,   -1,   -1,  256,  257,   -1,   45,
  260,  261,  262,  263,  264,  265,  266,  267,  268,  269,
  270,   -1,  272,  273,  125,   44,  276,  277,  256,  257,
  258,   42,   43,   -1,   45,   -1,   47,   -1,   -1,   -1,
   59,   -1,   -1,   -1,  256,  257,   -1,  275,  260,  261,
  262,   -1,  256,  257,  258,  267,  268,  269,  270,   -1,
  272,  273,   59,   -1,  276,  277,  125,   42,   43,   -1,
   45,  275,   47,   -1,   -1,   -1,   -1,   26,   -1,   -1,
   -1,  256,  257,   -1,   -1,  260,  261,  262,  263,  264,
  265,  266,  267,  268,  269,  270,   -1,  272,  273,   -1,
   -1,  276,  277,  256,  257,   -1,  125,  260,  261,  262,
  263,  264,  265,  266,  267,  268,  269,  270,   59,  272,
  273,   -1,   71,  276,  277,  256,  257,   -1,  125,  260,
  261,  262,  256,  257,  258,   -1,  267,  268,  269,  270,
   -1,  272,  273,   -1,   -1,  276,  277,   -1,   -1,   -1,
   -1,  275,   -1,   -1,   -1,  256,  257,  258,  123,  260,
   59,   -1,  256,  257,  258,   -1,  267,  268,  269,  270,
   -1,  272,  273,   -1,   -1,  276,  277,   -1,   59,  258,
   -1,  275,  123,   -1,  125,  256,  257,  258,  256,  257,
  258,  140,   -1,  272,   -1,   -1,   59,  256,  257,   -1,
  123,  260,  258,   -1,  275,   -1,   -1,  275,  267,  268,
  269,  270,   -1,  272,  273,   -1,  272,  276,  277,   59,
  256,  257,  258,   43,  123,   45,  125,  176,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,  256,  257,  275,
   60,  260,   62,   -1,  125,   -1,   -1,  258,  267,  268,
  269,  270,   59,  272,  273,   -1,  205,  276,  277,  256,
  257,  272,  125,  260,   -1,   -1,   -1,   -1,   -1,   -1,
  267,  268,  269,  270,   59,  272,  273,   -1,   -1,  276,
  277,  230,   -1,  258,   -1,  125,   -1,   -1,   -1,   -1,
   -1,  256,  257,   -1,   -1,  260,   -1,  272,   59,   -1,
   -1,   -1,  267,  268,  269,   -1,   -1,   -1,  273,   -1,
   -1,  276,   -1,   -1,   -1,  256,  257,   -1,  125,  260,
  261,  262,   -1,   -1,   59,   -1,  267,  268,  269,  270,
   -1,  272,  273,  256,  257,  276,  277,  260,   -1,   -1,
  125,   -1,   59,   -1,  267,  268,  269,   -1,   -1,   -1,
  273,   -1,   -1,  276,   -1,   -1,   -1,  256,  257,   -1,
   59,  260,  261,  262,  125,   -1,   -1,   -1,  267,  268,
  269,  270,   -1,  272,  273,  256,  257,  276,  277,  260,
   -1,   -1,   59,   -1,   -1,   -1,  267,  268,  269,  270,
  125,  272,  273,  256,  257,  276,  277,  260,   -1,   -1,
   -1,   -1,   -1,   -1,  267,  268,  269,  270,  125,  272,
  273,   -1,   -1,  276,  277,   -1,  256,  257,   -1,   -1,
  260,   -1,   -1,   -1,   -1,   -1,  125,  267,  268,  269,
  270,   -1,  272,  273,   -1,   -1,  276,  277,   -1,   -1,
   -1,   -1,   -1,  263,  264,  265,  266,   -1,  125,  256,
  257,   -1,   -1,  260,   -1,   -1,   -1,   -1,   -1,   -1,
  267,  268,  269,  270,   -1,  272,  273,   -1,   -1,  276,
  277,  256,  257,   -1,  125,  260,   -1,   -1,   -1,    6,
   -1,   -1,  267,  268,  269,  270,   -1,  272,  273,  125,
   -1,  276,  277,  123,   -1,  256,  257,   -1,   -1,  260,
   -1,   -1,   -1,   -1,   -1,   -1,  267,  268,  269,  270,
   -1,  272,  273,  125,   -1,  276,  277,   -1,   -1,   46,
   -1,  256,  257,   -1,   -1,  260,   -1,  125,   -1,   -1,
   -1,   -1,  267,  268,  269,  270,   -1,  272,  273,  256,
  257,  276,  277,  260,   -1,  125,   -1,   -1,   -1,   -1,
  267,  268,  269,  270,   81,  272,  273,  256,  257,  276,
  277,  260,   -1,  125,   -1,   -1,   -1,  123,  267,  268,
  269,  270,   -1,  272,  273,  102,   -1,  276,  277,  256,
  257,  123,   -1,  260,   -1,   -1,  113,  123,   -1,   -1,
  267,  268,  269,   -1,   -1,   -1,  273,   -1,   -1,  276,
  277,  123,   -1,   -1,   -1,  256,  257,   -1,   -1,  260,
  125,   -1,   -1,   -1,  141,  142,  267,  268,  269,   -1,
  256,  257,  273,   -1,  260,  276,  277,  257,  125,   -1,
  260,  267,  268,  269,  125,  162,   -1,  273,  268,  269,
  276,  277,   -1,  273,  256,  257,   -1,   -1,  260,   -1,
   -1,  125,   -1,  125,   -1,  267,  268,  269,  256,  257,
   -1,  273,  260,  190,  276,   -1,   -1,   -1,  125,  267,
  268,  269,   -1,   -1,  201,  273,  256,  257,  276,   -1,
  260,   -1,  125,   -1,   -1,   -1,   -1,  267,  268,  269,
   -1,   -1,   -1,  273,  256,  257,  276,   -1,  260,   -1,
   -1,  257,   -1,   -1,  260,  267,  268,  269,   -1,   -1,
   -1,  273,  268,  269,  276,  257,   -1,  273,  260,   -1,
   -1,  257,   -1,   -1,  260,   -1,  268,  269,   -1,   -1,
   -1,  273,  268,  269,   -1,  257,   -1,  273,  260,   -1,
   -1,   -1,  257,   -1,   -1,  260,  268,  269,   -1,   -1,
   -1,  273,   -1,  268,  269,   -1,   -1,  272,  273,   -1,
  257,   -1,   -1,  260,   -1,   -1,  257,   -1,   -1,  260,
   -1,  268,  269,   -1,   -1,  272,  273,  268,  269,   -1,
   -1,  272,  273,  257,   -1,  257,  260,   -1,  260,    5,
   -1,    7,   -1,   -1,  268,  269,  268,  269,   -1,  273,
  257,  273,   -1,  260,   -1,   -1,   -1,   -1,   -1,   -1,
   26,  268,  269,   -1,  257,   -1,  273,  260,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,  268,  269,   43,  256,  257,
  273,   -1,  260,   49,   -1,   -1,   -1,   -1,   -1,  267,
  268,  269,   -1,   -1,   -1,  273,   62,   -1,  276,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   98,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,  140,
};
}
final static short YYFINAL=10;
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
"sentencia : error ';'",
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
"asignacion : listavariables '=' lista_expresiones",
"bloque_control_estructura : '{' listasentenciasejecutables '}'",
"bloque_control_estructura : sentenciaejecutable ';'",
"bloque_control_estructura : '{' '}'",
"$$5 :",
"bloque_control : $$5 bloque_control_estructura",
"inicio_do : DO '{' listasentenciasejecutables '}'",
"inicio_do : DO '{' '}'",
"inicio_do : DO sentenciaejecutable ';'",
"$$6 :",
"bloque_control_else : $$6 bloque_control",
"control : IF condicion bloque_control ENDIF",
"control : IF condicion bloque_control ELSE bloque_control_else ENDIF",
"control : inicio_do UNTIL condicion",
"control : DO sentenciaejecutable UNTIL condicion",
"control : IF condicion bloque_control ELSE bloque_control_else",
"control : IF condicion bloque_control",
"control : IF condicion bloque_control ELSE ENDIF",
"control : IF condicion ELSE bloque_control_else ENDIF",
"control : IF condicion ENDIF",
"control : IF condicion ELSE ENDIF",
"control : DO UNTIL condicion",
"control : inicio_do condicion",
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
"termino : termino CTE",
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
"lista_expresiones : listavariables ',' listaconstantes",
};

//#line 581 "gramatica.y"

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
private int contadorParametrosActual = 0;
private Stack<Boolean> huboRetStack = new Stack<>();
private Stack<Boolean> sinRetIf = new Stack<>();
private boolean enDo = false;
private boolean sinRet = false;
private boolean huboIfEnFunc = false;
private boolean enParamInvocacion = false;
private boolean enDeclaracionParam = false;
private boolean enDeclaracionLambda = false;
private StringBuilder stringAmbitos = new StringBuilder(":PROGRAMA");
private ArrayList<Parametro> parametrosActuales = new ArrayList<>();
private Stack<ArrayList<Parametro>> stackParametrosActuales = new Stack<>();
private Stack<String> stackTipoFuncActual = new Stack<>();
private ArbolSintactico arbolSintactico;
private Map<String, ArbolSintactico> mapaFunciones = new HashMap<>();
private Simbolo funcionInvocadaActual = null;
private int funcLambdaDeclaradas = 0;
private boolean verbose = false;

// Stacks para guardar el contexto en funciones anidadas
private Stack<Integer> stackNivel = new Stack<>();
private Stack<Integer> stackReturnsIf = new Stack<>();
private Stack<Integer> stackReturnsFunc = new Stack<>();
private Stack<Boolean> stackSinRet = new Stack<>();
private Stack<Boolean> stackHuboIfEnFunc = new Stack<>();
private Stack<Stack<Boolean>> stackHuboRetStack = new Stack<>();

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
}

private boolean error() {
  return (!erroresLexicos.isEmpty() || !erroresSintacticos.isEmpty() );
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
  if (((Simbolo)valCte.obj).getTipo().equals("Long")) {
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
    if ( ((Simbolo)valCte.obj).getTipo().equals("Long") && ( ((SimboloLong) valCte.obj).getValor() == Compilador.INT32_MAX + 1) ) {
      Simbolo simbolo;
      if (!tablaSimbolos.containsKey(Compilador.INT32_MAX + "L")) {
        simbolo =  new SimboloLong("Long", "Constante", Compilador.INT32_MAX + "L", Compilador.INT32_MAX);
        tablaSimbolos.put(Compilador.INT32_MAX + "L", simbolo);
      } else {
        simbolo = tablaSimbolos.get(Compilador.INT32_MAX + "L");
      }
      valCte.obj = simbolo;
      warnings.add("||================ ANALIZADOR SINTÁCTICO: Warning en línea " + valCte.ival + ": el valor de la constante entera es mayor al máximo permitido, se asigna el valor " + Compilador.INT32_MAX);
    }
}

private void chequeoSentenciaReturn(ParserVal yyval) {
  if (enFunc == 0 || enDeclaracionLambda ) {
    erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + yyval.ival + ": Uso de sentencia return fuera de una función estándar");
  } else if (nivel > -1) {
    returnsIf--;
    huboRetStack.pop();
    huboRetStack.push(true);
  } else {
    returnsFunc --;
  }
}

private void inicioFunc() {
  // Guardar estado actual en los stacks
  stackNivel.push(nivel);
  stackReturnsIf.push(returnsIf);
  stackReturnsFunc.push(returnsFunc);
  stackSinRet.push(sinRet);
  stackHuboIfEnFunc.push(huboIfEnFunc);
  stackHuboRetStack.push(huboRetStack);
  
  // Reiniciar variables para la nueva función
  nivel = -1;
  returnsIf = 0;
  returnsFunc = 1;
  sinRet = false;
  huboIfEnFunc = false;
  huboRetStack = new Stack<>();
  
  enFunc++;
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
  
  // Restaurar estado previo desde los stacks
  if (!stackNivel.isEmpty()) {
    nivel = stackNivel.pop();
    returnsIf = stackReturnsIf.pop();
    returnsFunc = stackReturnsFunc.pop();
    sinRet = stackSinRet.pop();
    huboIfEnFunc = stackHuboIfEnFunc.pop();
    huboRetStack = stackHuboRetStack.pop();
  } else {
    // Reseteo por defecto si no hay contexto previo
    nivel = -1;
    returnsIf = 0;
    returnsFunc = 1;
    sinRet = false;
    huboIfEnFunc = false;
    huboRetStack = new Stack<>();
  }
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
    //Si el ámbito de la lambda no es padre del ámbito actual, tira error
    String ambitoVarActual = (":" + nombreLambda.substring(0, nombreLambda.lastIndexOf("."))).replace(".", ":");
    // P.F.F2.L() ->  ambitoVarActual = :P:F:F2
    if (!stringAmbitos.toString().contains(ambitoVarActual)) {
    erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + yyval.ival + ": Se ha intentado invocar una función lambda perteneciente a un ámbito no accesible");
    }
  }
}

// Esta función toma el Lexema de un identificador con la forma escrita por el programador (puede contener "."), 
// busca y retorna de la tabla de símbolos el símbolo correspondiente, teniendo en cuenta los ámbitos.
private Simbolo checkearAlcance(String lexema, int nroLinea) {
  // AuxSA se vuelve una copia de StringAmbitos para ir recortándola
  String auxSA;
  if (enDeclaracionLambda) {
    if (funcionInvocadaActual == null) { // En caso de que la función invocada actual no exista o no esté al alcance, no reviso los alcances de las variables en las sentencias de la lambda
      return null;
    }
    // En caso de estar dentro de una función lambda, el ámbito actual se toma desde la función invocada actual, moviendo el lexema de la del inicio al final.
    String lexemaFunc = funcionInvocadaActual.getLexema();
    String nombreFunc = lexemaFunc.substring(0, lexemaFunc.indexOf(":"));
    String ambitoFunc = lexemaFunc.substring(lexemaFunc.indexOf(":"));
    auxSA = ambitoFunc + ":" + nombreFunc;
  } else
    auxSA = stringAmbitos.toString();

  // Cuando el Lexema es una variable no anidada:
  if (!lexema.contains(".")) {
    String auxLexema = lexema + auxSA;

    while (!tablaSimbolos.containsKey(auxLexema) && auxSA.length() > 0) {
      auxSA = auxSA.substring(0, auxSA.lastIndexOf(":"));
      auxLexema = lexema + auxSA;
    }

    // Tira error si no se encuentra en el ámbito actual o los superiores (A no ser que sea una variable de función lambda, que se guarda sin ámbito por decisión de diseño)
    if (auxLexema.equals(lexema) && !tablaSimbolos.containsKey("lambda:" + auxLexema)) {
      erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + nroLinea + ": '" + lexema + "' no ha sido declarada en el ámbito actual y tampoco está al alcance");
      tablaSimbolos.remove(lexema);
      return null;
    } else {
      tablaSimbolos.remove(lexema);
      if (auxLexema.equals(lexema)) { 
        return tablaSimbolos.get("lambda:" + auxLexema);
      } else
        return tablaSimbolos.get(auxLexema);
    }

  // Cuando el Lexema es una variable anidada:    
  } else { 
    String varActual = lexema.substring(lexema.lastIndexOf(".") + 1);
    String ambitoVarActual = (":" + lexema.substring(0, lexema.lastIndexOf("."))).replace(".", ":");
    // P.F.F2.X -> varActual = X ; ambitoVarActual = :P:F:F2

    // Dado que estaba anidada, se va a buscar el ámbito a partir de stringAmbitos seguido del ambito escrito en la variable
    while (!tablaSimbolos.containsKey(varActual + auxSA + ambitoVarActual) && auxSA.length() > 0) {
      auxSA = auxSA.substring(0, auxSA.lastIndexOf(":"));
    }

    if (!tablaSimbolos.containsKey(varActual + auxSA + ambitoVarActual)) {
      erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + nroLinea + ": '" + lexema + "' no ha sido declarada en el ámbito actual y tampoco está al alcance");
      for (String ambitoPart : lexema.split("\\.")) {
        tablaSimbolos.remove(ambitoPart);
      }
      return null;
    } else {
      for (String ambitoPart : lexema.split("\\.")) {
        tablaSimbolos.remove(ambitoPart);
      }
      if (!stringAmbitos.toString().contains(auxSA + ambitoVarActual)) { // Si el ámbito donde se encontró la variable no está dentro del ámbito actual, tira error
        erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + nroLinea + ": '" + lexema + "' no está al alcance desde el ámbito actual");
      } 
      return tablaSimbolos.get(varActual + auxSA + ambitoVarActual);
    }
  }
}

// Función encargada de dos cosas: en base a conocer el parámetro real y el parámetro formal intencionado, chequea si ese parámetro formal verdaderamente existe en la función invocada,
// y, si existe, verifica también que, en caso de ser CVR, el parámetro real no sea una constante ni una expresión.
private void checkeoParametroDeInvocacion(String lexemaParametroFormal, int nroLinea, ArbolSintactico arbolParametroReal) {
  if (funcionInvocadaActual != null && funcionInvocadaActual.getCategoria().equals("Funcion")) {
    String lexemaFuncion = ((SimboloFuncion)funcionInvocadaActual).getLexema();
    Parametro parametroFormal = ((SimboloFuncion)funcionInvocadaActual).getRefParametro(lexemaParametroFormal);
    if (parametroFormal == null) {
      erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + nroLinea + ": el parámetro formal '" + lexemaParametroFormal + "' no existe en la función '" + lexemaFuncion.substring(0, lexemaFuncion.indexOf(':')) + "'");    
    } else if (arbolParametroReal != null) {
      // Solo chequeamos CVR si el árbol existe
      if ((arbolParametroReal.getCategoria() != null || arbolParametroReal.getRefSimbolo().getCategoria().equals("Constante")) && parametroFormal.getPasaje().equals("CVR")) {
        erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + nroLinea + ": el parámetro real asociado al parámetro formal '" + lexemaParametroFormal + "' de la función '" + lexemaFuncion.substring(0, lexemaFuncion.indexOf(':')) + "' es una constante u expresión y no puede pasarse por Copia-Valor-Resultado (CVR)");
      }
    }
  }
  tablaSimbolos.remove(lexemaParametroFormal);
}
private void restriccionCteFloat(ParserVal valCte) {
  if (((Simbolo)valCte.obj).getTipo().equals("Float")) {
    erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + valCte.ival + ": Uso incorrecto de constante Float por incompatibilidad de tipos");
    tablaSimbolos.remove(((SimboloCteFloat)valCte.obj).getLexema());
  }
}

private ArbolSintactico generarAsigMultiple(ArrayList<Simbolo> listaVariables, ArrayList<Simbolo> listaConstantes, int nroLinea) {
  int i = 1; 
  ArbolSintactico arbolRetorno = new ArbolSintactico("Sentencia", new ArbolSintactico("Asig", new ArbolSintactico(listaVariables.get(0)), new ArbolSintactico(listaConstantes.get(0))), null );                                                                                            

  while (i < listaConstantes.size()) {
    ArbolSintactico nuevaAsignacion = new ArbolSintactico( "Sentencia", new ArbolSintactico("Asig", new ArbolSintactico(listaVariables.get(i)), new ArbolSintactico(listaConstantes.get(i))), null);
    arbolRetorno.setUltimoDerecho(nuevaAsignacion);
    i++;
  }
  if (i < listaVariables.size()) {
    warnings.add("||================ ANALIZADOR SINTÁCTICO: Warning en línea " + nroLinea+ ": hay más variables que constantes en la asignación múltiple. A las variables sobrantes se les asigna el valor 0L");
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
  } 
  return arbolRetorno;
}
//#line 1104 "Parser.java"
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
//#line 21 "gramatica.y"
{stringAmbitos = new StringBuilder(":"+((Simbolo)val_peek(0).obj).getLexema());}
break;
case 2:
//#line 21 "gramatica.y"
{quitarAmbito();}
break;
case 3:
//#line 21 "gramatica.y"
{if (!error() && erroresSemanticos.isEmpty() && val_peek(2).obj != null && val_peek(2).obj != null) { arbolSintactico.adoptarNodoIntermedio((ArbolSintactico)val_peek(2).obj);}}
break;
case 4:
//#line 22 "gramatica.y"
{erroresSintacticos.add("ERROR SINTÁCTICO EN LÍNEA " + val_peek(1).ival + ": falta el nombre del programa");}
break;
case 5:
//#line 25 "gramatica.y"
{ yyval = val_peek(1); yyval.ival = val_peek(0).ival; }
break;
case 6:
//#line 26 "gramatica.y"
{ yyval.obj = null; yyval.ival = val_peek(0).ival; }
break;
case 7:
//#line 27 "gramatica.y"
{erroresSintacticos.add("ERROR SINTÁCTICO EN LÍNEA " + val_peek(0).ival + ": falta llave de cierre del bloque de sentencias"); yyval.obj = null; yyval.ival = val_peek(0).ival; }
break;
case 8:
//#line 28 "gramatica.y"
{erroresSintacticos.add("ERROR SINTÁCTICO EN LÍNEA " + val_peek(1).ival + ": falta llave de apertura del bloque de sentencias"); yyval.obj = null; yyval.ival = val_peek(1).ival; }
break;
case 9:
//#line 29 "gramatica.y"
{erroresSintacticos.add("ERROR SINTÁCTICO EN LÍNEA " + val_peek(0).ival + ": faltan ambas llaves del bloque de sentencias"); yyval.obj = null; yyval.ival = val_peek(0).ival; }
break;
case 10:
//#line 32 "gramatica.y"
{ if (!error()) {
                                                                            if (val_peek(1).obj != null && val_peek(0).obj != null) { ((ArbolSintactico)val_peek(1).obj).setUltimoDerecho(new ArbolSintactico("Sentencia", ((ArbolSintactico)val_peek(0).obj) , null )); yyval = val_peek(1); } 
                                                                            else if (val_peek(1).obj != null) { yyval= val_peek(1); yyval.ival = val_peek(1).ival; }
                                                                            else if (val_peek(0).obj != null) { yyval= new ParserVal(new ArbolSintactico("Sentencia", ((ArbolSintactico)val_peek(0).obj), null));} 
                                                                          }
                                                                          yyval.ival = val_peek(0).ival; 
                                                                        }
break;
case 11:
//#line 39 "gramatica.y"
{ if (!error()) { if (val_peek(0).obj != null) { yyval = new ParserVal(new ArbolSintactico("Sentencia", ((ArbolSintactico)val_peek(0).obj), null)); }} yyval.ival = val_peek(0).ival;}
break;
case 12:
//#line 42 "gramatica.y"
{ yyval = val_peek(1);}
break;
case 13:
//#line 43 "gramatica.y"
{ yyval.obj = null; yyval.ival = val_peek(0).ival; }
break;
case 14:
//#line 44 "gramatica.y"
{erroresSintacticos.add("ERROR SINTÁCTICO EN LÍNEA " + val_peek(0).ival + ": falta ; al final de la sentencia ejecutable"); yyval.ival = val_peek(0).ival;}
break;
case 15:
//#line 45 "gramatica.y"
{erroresSintacticos.add("ERROR SINTÁCTICO EN LÍNEA " + val_peek(0).ival + ": falta ; al final de la declaración"); yyval.ival = val_peek(0).ival;}
break;
case 16:
//#line 46 "gramatica.y"
{erroresSintacticos.add("ERROR SINTÁCTICO EN LÍNEA " + val_peek(0).ival + ": sentencia no válida"); yyval.ival = val_peek(0).ival;}
break;
case 17:
//#line 49 "gramatica.y"
{ if (!error()) {
                                                                                    ((ArbolSintactico)val_peek(2).obj).setUltimoDerecho(new ArbolSintactico("Sentencia", ((ArbolSintactico)val_peek(1).obj) , null ));
                                                                                    yyval = val_peek(2); 
                                                                                    }
                                                                                    yyval.ival = val_peek(0).ival;
                                                                                  }
break;
case 18:
//#line 55 "gramatica.y"
{ if (!error()) { yyval = new ParserVal(new ArbolSintactico("Sentencia", ((ArbolSintactico)val_peek(1).obj), null));} yyval.ival = val_peek(0).ival;}
break;
case 19:
//#line 56 "gramatica.y"
{erroresSintacticos.add("ERROR SINTÁCTICO EN LÍNEA " + val_peek(0).ival + ": falta ; al final de la sentencia ejecutable"); yyval.ival = val_peek(0).ival;}
break;
case 20:
//#line 57 "gramatica.y"
{erroresSintacticos.add("ERROR SINTÁCTICO EN LÍNEA " + val_peek(0).ival + ": falta ; al final de la sentencia ejecutable"); yyval.ival = val_peek(0).ival;}
break;
case 21:
//#line 60 "gramatica.y"
{ yyval = val_peek(0); }
break;
case 22:
//#line 61 "gramatica.y"
{ yyval = val_peek(0); }
break;
case 23:
//#line 62 "gramatica.y"
{ erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + val_peek(0).ival + ": No está permitida la invocación de funciones sin utilizar su retorno"); yyval.ival = val_peek(0).ival; }
break;
case 24:
//#line 63 "gramatica.y"
{ if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un print");
                                                                                yyval = val_peek(0);
                                                                              }
break;
case 25:
//#line 66 "gramatica.y"
{ if(enFunc==0) {erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + val_peek(0).ival + ": No está permitida la invocación de función lambda fuera de una función"); yyval.ival = val_peek(0).ival; } 
                                                                                else { yyval = val_peek(0); }
                                                                              }
break;
case 26:
//#line 69 "gramatica.y"
{chequeoSentenciaReturn(val_peek(0)); yyval = val_peek(0);}
break;
case 27:
//#line 72 "gramatica.y"
{if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó una declaración de variables"); yyval.ival = val_peek(0).ival; }
break;
case 28:
//#line 73 "gramatica.y"
{ String idFuncRaw = ((Simbolo)val_peek(0).obj).getLexema(); renombrarVariable(val_peek(0)); stringAmbitos.append(":"+idFuncRaw); stackTipoFuncActual.push(val_peek(1).sval); enDeclaracionParam = true;}
break;
case 29:
//#line 73 "gramatica.y"
{enDeclaracionParam = false; stackParametrosActuales.push(parametrosActuales); cambiarASimboloFuncion((Simbolo)val_peek(2).obj); parametrosActuales = new ArrayList<>(); inicioFunc();}
break;
case 30:
//#line 73 "gramatica.y"
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
case 35:
//#line 92 "gramatica.y"
{ ((Simbolo)val_peek(0).obj).setTipo(val_peek(1).sval);
                                                                       renombrarVariable(val_peek(0)); 
                                                                       if (val_peek(1).sval.equals("Lambda")) {
                                                                          erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + val_peek(1).ival + ": el tipo 'Lambda' no puede utilizar la semántica de pasaje por Copia-Valor (CV)");
                                                                       }
                                                                       parametrosActuales.add(new Parametro(((Simbolo)val_peek(0).obj), "CV"));
                                                                     }
break;
case 36:
//#line 99 "gramatica.y"
{ ((Simbolo)val_peek(0).obj).setTipo(val_peek(1).sval);
                                                                       renombrarVariable(val_peek(0));
                                                                       if (val_peek(1).sval.equals("Lambda")) {
                                                                          parametrosActuales.add(new Parametro(((Simbolo)val_peek(0).obj), "CV"));
                                                                       } else parametrosActuales.add(new Parametro(((Simbolo)val_peek(0).obj), "CVR"));
                                                                     }
break;
case 37:
//#line 105 "gramatica.y"
{ erroresSintacticos.add("ERROR SINTÁCTICO EN LÍNEA " + val_peek(1).ival + ": falta el nombre del parámetro");
                                                                     }
break;
case 38:
//#line 107 "gramatica.y"
{ erroresSintacticos.add("ERROR SINTÁCTICO EN LÍNEA " + val_peek(0).ival + ": falta el nombre del parámetro");
                                                                     }
break;
case 39:
//#line 109 "gramatica.y"
{ renombrarVariable(val_peek(0)); 
                                                                       parametrosActuales.add(new Parametro(((Simbolo)val_peek(0).obj), "CV")); 
                                                                       erroresSintacticos.add("ERROR SINTÁCTICO EN LÍNEA " + val_peek(1).ival + ": falta el tipo del parámetro");
                                                                     }
break;
case 40:
//#line 113 "gramatica.y"
{ renombrarVariable(val_peek(0)); 
                                                                       parametrosActuales.add(new Parametro(((Simbolo)val_peek(0).obj), "CVR")); 
                                                                       erroresSintacticos.add("ERROR SINTÁCTICO EN LÍNEA " + val_peek(0).ival + ": falta el tipo del parámetro");
                                                                     }
break;
case 41:
//#line 119 "gramatica.y"
{yyval = new ParserVal((SimboloLong)val_peek(0).obj);}
break;
case 42:
//#line 122 "gramatica.y"
{ if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(1).ival + ": Se detectó una sentencia return");
                                                                              if (!error()) { yyval = new ParserVal(new ArbolSintactico("Return", (ArbolSintactico)val_peek(1).obj,  null)); } yyval.ival = val_peek(0).ival;
                                                                            }
break;
case 43:
//#line 125 "gramatica.y"
{erroresSintacticos.add("ERROR SINTÁCTICO EN LÍNEA " + val_peek(1).ival + ": falta el paréntesis de cierre en sentencia return"); yyval.ival = val_peek(0).ival;}
break;
case 44:
//#line 126 "gramatica.y"
{erroresSintacticos.add("ERROR SINTÁCTICO EN LÍNEA " + val_peek(1).ival + ": falta el paréntesis de apertura en sentencia return"); yyval.ival = val_peek(0).ival;}
break;
case 45:
//#line 127 "gramatica.y"
{erroresSintacticos.add("ERROR SINTÁCTICO EN LÍNEA " + val_peek(1).ival + ": faltan ambos paréntesis en sentencia return"); yyval.ival = val_peek(0).ival;}
break;
case 46:
//#line 128 "gramatica.y"
{erroresSintacticos.add("ERROR SINTÁCTICO EN LÍNEA " + val_peek(0).ival + ": falta de argumento en sentencia return"); yyval.ival = val_peek(0).ival;}
break;
case 47:
//#line 131 "gramatica.y"
{ if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó una asignación simple");
                                                                                            Simbolo simboloAAsignar = checkearAlcance(val_peek(2).sval, val_peek(2).ival);
                                                                                            if( simboloAAsignar != null && (!simboloAAsignar.getCategoria().equals("Identificador") || simboloAAsignar.getTipo().equals("Lambda"))) {
                                                                                              erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + val_peek(2).ival + ": El identificador '" + val_peek(2).sval + "' no es una variable");
                                                                                            }
                                                                                            if (!error()) { 
                                                                                              yyval = new ParserVal( new ArbolSintactico("Asig", new ArbolSintactico(simboloAAsignar), (ArbolSintactico)val_peek(0).obj) );
                                                                                            }
                                                                                            yyval.ival = val_peek(0).ival;
                                                                                          }
break;
case 48:
//#line 141 "gramatica.y"
{ if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó una asignación múltiple");
                                                                                            if (((ArrayList<Simbolo>)val_peek(2).obj).size() >= ((ArrayList<Simbolo>)val_peek(0).obj).size()) {
                                                                                              if(!error()) {  
                                                                                                yyval = new ParserVal(generarAsigMultiple((ArrayList<Simbolo>)val_peek(2).obj, (ArrayList<Simbolo>)val_peek(0).obj, val_peek(1).ival));
                                                                                              }
                                                                                            }
                                                                                            else { 
                                                                                              erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + val_peek(0).ival + ": La cantidad de constantes en la asignación múltiple es mayor a la cantidad de variables");
                                                                                              yyval = new ParserVal(new ArbolSintactico("ErrorAsigMultiple", null, null ));
                                                                                            }
                                                                                            yyval.ival = val_peek(0).ival;
                                                                                          }
break;
case 49:
//#line 153 "gramatica.y"
{erroresSintacticos.add("ERROR SINTÁCTICO EN LÍNEA " + val_peek(1).ival + ": No se permite la asignación múltiple utilizando variables en el lado derecho"); yyval.ival = val_peek(1).ival; }
break;
case 50:
//#line 154 "gramatica.y"
{ erroresSintacticos.add("ERROR SINTÁCTICO EN LÍNEA " + val_peek(1).ival + ": No se permite la asignación múltiple utilizando variables en el lado derecho");
                                                                                            if (((ArrayList<Simbolo>)val_peek(2).obj).size() >= ((ArrayList<Simbolo>)val_peek(0).obj).size()) {
                                                                                              if(!error()) {  
                                                                                                yyval = new ParserVal(generarAsigMultiple((ArrayList<Simbolo>)val_peek(2).obj, (ArrayList<Simbolo>)val_peek(0).obj, val_peek(1).ival));
                                                                                              }
                                                                                            }
                                                                                            else erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + val_peek(0).ival + ": La cantidad de elementos en la derecha de la asignación múltiple es mayor a la cantidad de variables del lado izquierdo");
                                                                                            yyval.ival = val_peek(1).ival;
                                                                                          }
break;
case 51:
//#line 165 "gramatica.y"
{ yyval = val_peek(1); yyval.ival = val_peek(0).ival; }
break;
case 52:
//#line 166 "gramatica.y"
{ yyval = val_peek(1); yyval.ival = val_peek(0).ival; }
break;
case 53:
//#line 167 "gramatica.y"
{ yyval.obj = null; yyval.ival = val_peek(0).ival; }
break;
case 54:
//#line 170 "gramatica.y"
{inicioIf();}
break;
case 55:
//#line 170 "gramatica.y"
{finIf(val_peek(0)); yyval = val_peek(0); }
break;
case 56:
//#line 173 "gramatica.y"
{ yyval = val_peek(1); yyval.ival = val_peek(0).ival; }
break;
case 57:
//#line 174 "gramatica.y"
{ yyval.obj = null; yyval.ival = val_peek(0).ival; }
break;
case 58:
//#line 175 "gramatica.y"
{ yyval = val_peek(1); yyval.ival = val_peek(0).ival; }
break;
case 59:
//#line 178 "gramatica.y"
{sinRetIf.push(sinRet);}
break;
case 60:
//#line 178 "gramatica.y"
{if(sinRetIf.pop()) {sinRet=true;} yyval = val_peek(0); }
break;
case 61:
//#line 181 "gramatica.y"
{ if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un bloque IF");
                                                                                                  if (!error()) { yyval = new ParserVal(new ArbolSintactico("If", (ArbolSintactico)val_peek(2).obj, new ArbolSintactico("CuerpoIf",(ArbolSintactico)val_peek(1).obj, null)) ); }
                                                                                                  yyval.ival = val_peek(0).ival;
                                                                                                  sinRet = true;
                                                                                                }
break;
case 62:
//#line 186 "gramatica.y"
{ if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un bloque IF-ELSE");
                                                                                                  if (!error()) { yyval = new ParserVal(new ArbolSintactico("IfElse", (ArbolSintactico)val_peek(4).obj, new ArbolSintactico("CuerpoIfElse",(ArbolSintactico)val_peek(3).obj, (ArbolSintactico)val_peek(1).obj)) );}  yyval.ival = val_peek(0).ival;
                                                                                                  if (enFunc > 0 && !sinRet && nivel > -1) {
                                                                                                      /* El IF-ELSE completo actúa como un return*/
                                                                                                      huboRetStack.pop();
                                                                                                      huboRetStack.push(true);
                                                                                                  }
                                                                                                }
break;
case 63:
//#line 194 "gramatica.y"
{ if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un bloque DO UNTIL");
                                                                                                  if (!error()) { yyval = new ParserVal(new ArbolSintactico("DoUntil", (ArbolSintactico)val_peek(0).obj, (ArbolSintactico)val_peek(2).obj));  }  yyval.ival = val_peek(0).ival;
                                                                                                }
break;
case 64:
//#line 197 "gramatica.y"
{ if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un bloque DO UNTIL");
                                                                                                  if (!error()) { yyval = new ParserVal(new ArbolSintactico("DoUntil", (ArbolSintactico)val_peek(0).obj, (ArbolSintactico)val_peek(2).obj));  }  yyval.ival = val_peek(0).ival;
                                                                                                  erroresSintacticos.add("ERROR SINTÁCTICO EN LÍNEA " + val_peek(0).ival + ": falta ';' en la sentencia ejecutable dentro del bloque DO");
                                                                                                }
break;
case 65:
//#line 201 "gramatica.y"
{erroresSintacticos.add("ERROR SINTÁCTICO EN LÍNEA " + val_peek(0).ival + ": falta la palabra ENDIF al final del bloque IF-ELSE"); yyval.ival = val_peek(0).ival;}
break;
case 66:
//#line 202 "gramatica.y"
{erroresSintacticos.add("ERROR SINTÁCTICO EN LÍNEA " + val_peek(1).ival + ": falta la palabra ENDIF al final del bloque IF"); yyval.ival = val_peek(0).ival;}
break;
case 67:
//#line 203 "gramatica.y"
{erroresSintacticos.add("ERROR SINTÁCTICO EN LÍNEA " + val_peek(0).ival + ": falta el cuerpo en el bloque ELSE"); yyval.ival = val_peek(0).ival;}
break;
case 68:
//#line 204 "gramatica.y"
{erroresSintacticos.add("ERROR SINTÁCTICO EN LÍNEA " + val_peek(0).ival + ": falta el cuerpo en el bloque IF"); yyval.ival = val_peek(0).ival;}
break;
case 69:
//#line 205 "gramatica.y"
{erroresSintacticos.add("ERROR SINTÁCTICO EN LÍNEA " + val_peek(0).ival + ": falta el cuerpo en el bloque IF"); yyval.ival = val_peek(0).ival;}
break;
case 70:
//#line 206 "gramatica.y"
{erroresSintacticos.add("ERROR SINTÁCTICO EN LÍNEA " + val_peek(0).ival + ": falta el cuerpo en los bloques IF y ELSE"); yyval.ival = val_peek(0).ival;}
break;
case 71:
//#line 208 "gramatica.y"
{erroresSintacticos.add("ERROR SINTÁCTICO EN LÍNEA " + val_peek(0).ival + ": falta de cuerpo en iteracion"); yyval.ival = val_peek(0).ival;}
break;
case 72:
//#line 209 "gramatica.y"
{ erroresSintacticos.add("ERROR SINTÁCTICO EN LÍNEA " + val_peek(0).ival + ": falta la palabra UNTIL en la iteracion"); yyval.ival = val_peek(0).ival; }
break;
case 73:
//#line 212 "gramatica.y"
{ if (val_peek(1).obj instanceof ArbolSintactico) { yyval = new ParserVal (new ArbolSintactico("Print", (ArbolSintactico)val_peek(1).obj, null)); } yyval.ival = val_peek(0).ival; }
break;
case 74:
//#line 213 "gramatica.y"
{erroresSintacticos.add("ERROR SINTÁCTICO EN LÍNEA " + val_peek(0).ival + ": falta de argumento en sentencia print");  yyval.ival = val_peek(0).ival; }
break;
case 75:
//#line 214 "gramatica.y"
{erroresSintacticos.add("ERROR SINTÁCTICO EN LÍNEA " + val_peek(1).ival + ": falta el paréntesis de apertura en sentencia print");  yyval.ival = val_peek(0).ival; }
break;
case 76:
//#line 215 "gramatica.y"
{erroresSintacticos.add("ERROR SINTÁCTICO EN LÍNEA " + val_peek(1).ival + ": falta el paréntesis de cierre en sentencia print");  yyval.ival = val_peek(1).ival; }
break;
case 77:
//#line 216 "gramatica.y"
{erroresSintacticos.add("ERROR SINTÁCTICO EN LÍNEA " + val_peek(1).ival + ": faltan ambos paréntesis en sentencia print");  yyval.ival = val_peek(0).ival; }
break;
case 78:
//#line 219 "gramatica.y"
{ yyval = val_peek(0); }
break;
case 79:
//#line 220 "gramatica.y"
{ yyval = new ParserVal(new ArbolSintactico((Simbolo)val_peek(0).obj)); }
break;
case 80:
//#line 222 "gramatica.y"
{ yyval = val_peek(1); yyval.ival = val_peek(0).ival; }
break;
case 81:
//#line 223 "gramatica.y"
{erroresSintacticos.add("ERROR SINTÁCTICO EN LÍNEA " + val_peek(0).ival + ": falta el paréntesis de apertura de la condición");}
break;
case 82:
//#line 224 "gramatica.y"
{erroresSintacticos.add("ERROR SINTÁCTICO EN LÍNEA " + val_peek(0).ival + ": falta el paréntesis de cierre de la condición");}
break;
case 83:
//#line 225 "gramatica.y"
{erroresSintacticos.add("ERROR SINTÁCTICO EN LÍNEA " + val_peek(0).ival + ": faltan ambos paréntesis de la condición");}
break;
case 84:
//#line 228 "gramatica.y"
{ if (!error()) { yyval = new ParserVal (new ArbolSintactico(val_peek(1).sval, (ArbolSintactico)val_peek(2).obj, (ArbolSintactico)val_peek(0).obj)); } }
break;
case 85:
//#line 229 "gramatica.y"
{ erroresSintacticos.add("ERROR SINTÁCTICO EN LÍNEA " + val_peek(0).ival + ": La condición es incompleta");}
break;
case 86:
//#line 232 "gramatica.y"
{ yyval = new ParserVal("Mayor"); }
break;
case 87:
//#line 233 "gramatica.y"
{ yyval = new ParserVal("Menor"); }
break;
case 88:
//#line 234 "gramatica.y"
{ yyval = new ParserVal("MayorIgual"); }
break;
case 89:
//#line 235 "gramatica.y"
{ yyval = new ParserVal("MenorIgual"); }
break;
case 90:
//#line 236 "gramatica.y"
{ yyval = new ParserVal("Igual"); }
break;
case 91:
//#line 237 "gramatica.y"
{ yyval = new ParserVal("Distinto"); }
break;
case 92:
//#line 240 "gramatica.y"
{ yyval = new ParserVal("Long"); }
break;
case 93:
//#line 241 "gramatica.y"
{ yyval = new ParserVal("Lambda");
                                                      if (!enDeclaracionParam) {
                                                        erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + val_peek(0).ival + ": el tipo 'Lambda' solo puede utilizarse en la declaración de parámetros formales de funciones estándar");
                                                      }
                                                    }
break;
case 94:
//#line 248 "gramatica.y"
{renombrarVariable(val_peek(0));}
break;
case 95:
//#line 249 "gramatica.y"
{renombrarVariable(val_peek(0));}
break;
case 96:
//#line 252 "gramatica.y"
{ Simbolo simboloActual = checkearAlcance(val_peek(0).sval, val_peek(0).ival);
                                                                        if (simboloActual != null && (!(simboloActual).getCategoria().equals("Identificador") || simboloActual.getTipo().equals("Lambda"))) {
                                                                          erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + val_peek(0).ival + ": El identificador '" + val_peek(0).sval + "' no es una variable");
                                                                        }
                                                                        yyval = val_peek(2);
                                                                        ((ArrayList<Simbolo>)yyval.obj).add(simboloActual);  
                                                                      }
break;
case 97:
//#line 259 "gramatica.y"
{ Simbolo simboloActual = checkearAlcance(val_peek(0).sval, val_peek(0).ival);
                                                                        if (simboloActual != null && (!(simboloActual).getCategoria().equals("Identificador") || simboloActual.getTipo().equals("Lambda"))) {
                                                                          erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + val_peek(0).ival + ": El identificador '" + val_peek(0).sval + "' no es una variable");
                                                                        }
                                                                        yyval = new ParserVal( new ArrayList<Simbolo>() );
                                                                        ((ArrayList<Simbolo>)yyval.obj).add(simboloActual);
                                                                      }
break;
case 98:
//#line 268 "gramatica.y"
{ yyval = new ParserVal( ((SimboloLong)val_peek(0).obj).getLexema() );
                                                                        yyval.ival = val_peek(0).ival; 
                                                                      }
break;
case 99:
//#line 271 "gramatica.y"
{ yyval = new ParserVal( val_peek(2).sval + "." + ((SimboloLong)val_peek(0).obj).getLexema() );
                                                                        yyval.ival = val_peek(0).ival;
                                                                      }
break;
case 100:
//#line 277 "gramatica.y"
{ enParamInvocacion = false;
                                                                                                                if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó la invocación de una función");
                                                                                                                if (!error() && val_peek(2).obj != null) {
                                                                                                                  ((ArbolSintactico)val_peek(2).obj).setUltimoDerecho((ArbolSintactico)val_peek(1).obj); 
                                                                                                                  yyval = val_peek(2);
                                                                                                                }
                                                                                                                yyval.ival = val_peek(0).ival;
                                                                                                              }
break;
case 101:
//#line 285 "gramatica.y"
{ erroresSintacticos.add("ERROR SINTÁCTICO EN LÍNEA " + val_peek(0).ival + ": faltan los argumentos en la invocación de función"); 
                                                                                                                enParamInvocacion = false;
                                                                                                                yyval.ival = val_peek(0).ival; 
                                                                                                              }
break;
case 102:
//#line 291 "gramatica.y"
{ enParamInvocacion = false;
                                                                                                                if (val_peek(2).obj != null && ((Simbolo)((ArbolSintactico)val_peek(2).obj).getLeft().getRefSimbolo()).getTipo() != "Lambda") {
                                                                                                                  erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + val_peek(0).ival + ": La función '" + val_peek(2).sval + "' no es una función Lambda");
                                                                                                                }
                                                                                                                if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó la invocación de una función lambda");
                                                                                                                chequeo_anid_lambda(val_peek(2).sval, val_peek(0));
                                                                                                                if (!error() && val_peek(2).obj != null) {
                                                                                                                  ((ArbolSintactico)val_peek(2).obj).setUltimoDerecho( new ArbolSintactico((Simbolo)val_peek(1).obj) );
                                                                                                                  yyval = val_peek(2);
                                                                                                                  ((ArbolSintactico)yyval.obj).setCategoria("CallLambda");
                                                                                                                }
                                                                                                                yyval.ival = val_peek(0).ival;
                                                                                                              }
break;
case 103:
//#line 304 "gramatica.y"
{ enParamInvocacion = false;
                                                                                                                if (val_peek(2).obj != null && ((Simbolo)((ArbolSintactico)val_peek(2).obj).getLeft().getRefSimbolo()).getTipo() != "Lambda") {
                                                                                                                  erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + val_peek(0).ival + ": La función '" + val_peek(2).sval + "' no es una función Lambda");
                                                                                                                }
                                                                                                                restriccionCteFloat(val_peek(1));
                                                                                                                chequeo_anid_lambda(val_peek(2).sval, val_peek(0)); 
                                                                                                                constantePositiva(val_peek(1)); 
                                                                                                                if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó la invocación de una función lambda");
                                                                                                                if (!error() && val_peek(2).obj != null) {
                                                                                                                  ((ArbolSintactico)val_peek(2).obj).setUltimoDerecho( new ArbolSintactico((Simbolo)val_peek(1).obj) );
                                                                                                                  ((ArbolSintactico)yyval.obj).setCategoria("CallLambda");
                                                                                                                }
                                                                                                                yyval = val_peek(2); yyval.ival = val_peek(0).ival;
                                                                                                              }
break;
case 104:
//#line 318 "gramatica.y"
{ enParamInvocacion = false;
                                                                                                                if (val_peek(3).obj != null && ((Simbolo)((ArbolSintactico)val_peek(3).obj).getLeft().getRefSimbolo()).getTipo() != "Lambda") {
                                                                                                                  erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + val_peek(0).ival + ": La función '" + val_peek(3).sval + "' no es una función Lambda");
                                                                                                                }
                                                                                                                restriccionCteFloat(val_peek(1));
                                                                                                                chequeo_anid_lambda(val_peek(3).sval, val_peek(0)); 
                                                                                                                constanteNegativa(val_peek(1)); 
                                                                                                                if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó la invocación de una función lambda");
                                                                                                                if (!error() && val_peek(3).obj != null) {
                                                                                                                  ((ArbolSintactico)val_peek(3).obj).setUltimoDerecho( new ArbolSintactico((Simbolo)val_peek(1).obj) );
                                                                                                                  yyval = val_peek(3);
                                                                                                                  ((ArbolSintactico)yyval.obj).setCategoria("CallLambda");
                                                                                                                }
                                                                                                                yyval.ival = val_peek(0).ival;
                                                                                                              }
break;
case 105:
//#line 333 "gramatica.y"
{ enParamInvocacion = false;
                                                                                                                if (val_peek(5).obj != null && ((Simbolo)((ArbolSintactico)val_peek(5).obj).getLeft().getRefSimbolo()).getTipo() != "Lambda") {
                                                                                                                  erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + val_peek(0).ival + ": La función '" + val_peek(5).sval + "' no es una función Lambda");
                                                                                                                }
                                                                                                                constantePositiva(val_peek(2));
                                                                                                                chequeo_anid_lambda(val_peek(5).sval, val_peek(0));
                                                                                                                if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó la invocación de una función lambda");
                                                                                                                if (!error() && val_peek(5).obj != null) {
                                                                                                                  ((ArbolSintactico)val_peek(5).obj).setUltimoDerecho( new ArbolSintactico("Trunc", new ArbolSintactico((Simbolo)val_peek(2).obj), null) );
                                                                                                                  yyval = val_peek(5);
                                                                                                                  ((ArbolSintactico)yyval.obj).setCategoria("CallLambda");
                                                                                                                }
                                                                                                                yyval.ival = val_peek(0).ival;
                                                                                                              }
break;
case 106:
//#line 347 "gramatica.y"
{ enParamInvocacion = false;
                                                                                                                if (val_peek(6).obj != null && ((Simbolo)((ArbolSintactico)val_peek(6).obj).getLeft().getRefSimbolo()).getTipo() != "Lambda") {
                                                                                                                  erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + val_peek(0).ival + ": La función '" + val_peek(6).sval + "' no es una función Lambda");
                                                                                                                }
                                                                                                                constanteNegativa(val_peek(2));
                                                                                                                chequeo_anid_lambda(val_peek(6).sval, val_peek(0));
                                                                                                                if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó la invocación de una función lambda");
                                                                                                                if (!error() && val_peek(6).obj != null) {
                                                                                                                  ((ArbolSintactico)val_peek(6).obj).setUltimoDerecho( new ArbolSintactico("Trunc", new ArbolSintactico((Simbolo)val_peek(2).obj), null) );
                                                                                                                  yyval = val_peek(6);
                                                                                                                  ((ArbolSintactico)yyval.obj).setCategoria("CallLambda");
                                                                                                                }
                                                                                                                yyval.ival = val_peek(0).ival;
                                                                                                              }
break;
case 107:
//#line 363 "gramatica.y"
{ Simbolo simboloActual = checkearAlcance(val_peek(0).sval, val_peek(0).ival);
                                                                                                  yyval = new ParserVal(simboloActual);
                                                                                                  if (simboloActual != null && (!(simboloActual.getCategoria().equals("Identificador")) || simboloActual.getTipo().equals("Lambda"))) {
                                                                                                    erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + val_peek(0).ival + ": al usar un identificador como argumento en la invocación de una función Lambda, este debe ser una variable");
                                                                                                  }
                                                                                                }
break;
case 108:
//#line 371 "gramatica.y"
{ enParamInvocacion = true;
                                                                                                  String lexemaOriginal = val_peek(1).sval;
                                                                                                  val_peek(1).obj = checkearAlcance(val_peek(1).sval, val_peek(0).ival);
                                                                                                  funcionInvocadaActual = (Simbolo) val_peek(1).obj;
                                                                                                  if (val_peek(1).obj != null && !error()) {
                                                                                                      yyval = new ParserVal(new ArbolSintactico("Call", new ArbolSintactico((Simbolo)val_peek(1).obj), null));
                                                                                                      yyval.sval = lexemaOriginal;
                                                                                                  }
                                                                                                }
break;
case 109:
//#line 382 "gramatica.y"
{ if (!error() && funcionInvocadaActual != null) {
                                                                                yyval = new ParserVal(new ArbolSintactico("ListaParametros", (ArbolSintactico)((ArbolSintactico[])val_peek(0).obj)[0], null) ); 
                                                                                for (int i = 1; i < ((ArbolSintactico[])val_peek(0).obj).length; i++) {
                                                                                  ((ArbolSintactico)yyval.obj).setUltimoDerecho( new ArbolSintactico("ListaParametros", ((ArbolSintactico)((ArbolSintactico[])val_peek(0).obj)[i]), null) );
                                                                                  if ((ArbolSintactico[])val_peek(0).obj == null) {
                                                                                    String lexemaParametroFaltante = ((SimboloFuncion)funcionInvocadaActual).getParametroEnPos(i).getLexema();
                                                                                    erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + val_peek(0).ival + ": Al parámetro formal '" + lexemaParametroFaltante + "' en la invocación de la función '" + funcionInvocadaActual.getLexema() + "' no se le ha asignado ningún parámetro real");
                                                                                  }
                                                                                }
                                                                                if (contadorParametrosActual != ((SimboloFuncion)funcionInvocadaActual).getCantidadParametros()) {
                                                                                  String lexemaFuncion = funcionInvocadaActual.getLexema().substring(0, funcionInvocadaActual.getLexema().indexOf(':'));
                                                                                  erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + val_peek(0).ival + ": La cantidad de parámetros en la invocación de la función '" + lexemaFuncion + "' no coincide con la cantidad de parámetros formales declarados");
                                                                                }
                                                                              }
                                                                              contadorParametrosActual = 0;
                                                                              yyval.ival = val_peek(0).ival;
                                                                            }
break;
case 110:
//#line 401 "gramatica.y"
{ yyval = val_peek(2);
                                                                              if (funcionInvocadaActual != null) {
                                                                                int posParametro = ((SimboloFuncion)funcionInvocadaActual).getPosParametro(val_peek(0).sval);
                                                                                if (posParametro != -1) {
                                                                                  ((ArbolSintactico[])yyval.obj)[((SimboloFuncion)funcionInvocadaActual).getPosParametro(val_peek(0).sval)] = (ArbolSintactico)val_peek(0).obj; 
                                                                                }
                                                                                contadorParametrosActual++;
                                                                              }
                                                                              yyval.ival = val_peek(0).ival;
                                                                            }
break;
case 111:
//#line 411 "gramatica.y"
{ if (funcionInvocadaActual != null) {
                                                                                yyval = new ParserVal(new ArbolSintactico[((SimboloFuncion)funcionInvocadaActual).getCantidadParametros()]);
                                                                                int posParametro = ((SimboloFuncion)funcionInvocadaActual).getPosParametro(val_peek(0).sval);
                                                                                if (posParametro != -1) {
                                                                                  ((ArbolSintactico[])yyval.obj)[((SimboloFuncion)funcionInvocadaActual).getPosParametro(val_peek(0).sval)] = (ArbolSintactico)val_peek(0).obj;
                                                                                }
                                                                                contadorParametrosActual++; 
                                                                              } else {
                                                                                yyval = new ParserVal(new ArbolSintactico[0]);
                                                                              }
                                                                              yyval.ival = val_peek(0).ival;
                                                                            }
break;
case 112:
//#line 425 "gramatica.y"
{ checkeoParametroDeInvocacion(((Simbolo)val_peek(0).obj).getLexema(), val_peek(0).ival, (ArbolSintactico)val_peek(2).obj);
                                                                                                              Parametro parametroActual = ((SimboloFuncion)funcionInvocadaActual).getRefParametro(((Simbolo)val_peek(0).obj).getLexema());
                                                                                                              if (parametroActual != null && parametroActual.getTipo().equals("Lambda")) {
                                                                                                                erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + val_peek(0).ival + ": No se puede pasar una expresión como parámetro real a un parámetro formal de tipo 'Lambda'");
                                                                                                              }
                                                                                                              if (!error()) {
                                                                                                                yyval = new ParserVal( (ArbolSintactico)val_peek(2).obj );
                                                                                                                yyval.sval = ((Simbolo)val_peek(0).obj).getLexema();
                                                                                                              }
                                                                                                              yyval.ival = val_peek(0).ival;
                                                                                                            }
break;
case 113:
//#line 436 "gramatica.y"
{ checkeoParametroDeInvocacion(((Simbolo)val_peek(0).obj).getLexema(), val_peek(0).ival, (ArbolSintactico)val_peek(3).obj); 
                                                                                                              if (!error()) {
                                                                                                                funcLambdaDeclaradas++;
                                                                                                                tablaSimbolos.remove(val_peek(5).sval);
                                                                                                                yyval = new ParserVal(new ArbolSintactico( "DeclaracionLambda", (ArbolSintactico)val_peek(3).obj , null));
                                                                                                                yyval.sval = ((Simbolo)val_peek(0).obj).getLexema();
                                                                                                                enDeclaracionLambda = false;
                                                                                                              }
                                                                                                              yyval.ival = val_peek(0).ival;
                                                                                                            }
break;
case 114:
//#line 446 "gramatica.y"
{erroresSintacticos.add("ERROR SINTÁCTICO EN LÍNEA " + val_peek(1).ival + ": falta llave de cierre del cuerpo de la función Lambda"); enDeclaracionLambda = false; yyval.ival = val_peek(1).ival;}
break;
case 115:
//#line 447 "gramatica.y"
{erroresSintacticos.add("ERROR SINTÁCTICO EN LÍNEA " + val_peek(2).ival + ": falta llave de apertura del cuerpo de la función Lambda"); enDeclaracionLambda = false; yyval.ival = val_peek(2).ival;}
break;
case 116:
//#line 448 "gramatica.y"
{erroresSintacticos.add("ERROR SINTÁCTICO EN LÍNEA " + val_peek(1).ival + ": faltan ambas llaves del cuerpo de la función Lambda"); enDeclaracionLambda = false; yyval.ival = val_peek(1).ival;}
break;
case 117:
//#line 449 "gramatica.y"
{erroresSintacticos.add("ERROR SINTÁCTICO EN LÍNEA " + val_peek(0).ival + ": No se especifica el parametro formal al que corresponde el parámetro real"); enDeclaracionLambda = false; yyval.ival = val_peek(0).ival;}
break;
case 118:
//#line 450 "gramatica.y"
{erroresSintacticos.add("ERROR SINTÁCTICO EN LÍNEA " + val_peek(0).ival + ": No se especifica el parametro formal al que corresponde el parámetro real"); yyval.ival = val_peek(0).ival;}
break;
case 119:
//#line 453 "gramatica.y"
{ enDeclaracionLambda = true;
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
case 120:
//#line 465 "gramatica.y"
{ enDeclaracionLambda = true;
                                                erroresSintacticos.add("ERROR SINTÁCTICO EN LÍNEA " + val_peek(1).ival + ": falta el paréntesis de apertura en la declaración del argumento de la función Lambda");
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
case 121:
//#line 478 "gramatica.y"
{ enDeclaracionLambda = true;
                                              erroresSintacticos.add("ERROR SINTÁCTICO EN LÍNEA " + val_peek(0).ival + ": falta el paréntesis de cierre en la declaración del argumento de la función Lambda");
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
case 122:
//#line 493 "gramatica.y"
{ if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(1).ival + ": Se detectó una expresión desde una suma");
                                                                      if (!error()) {
                                                                        yyval = new ParserVal(new ArbolSintactico("Suma", (ArbolSintactico)val_peek(2).obj, (ArbolSintactico)val_peek(0).obj));
                                                                      }
                                                                      yyval.ival = val_peek(1).ival;
                                                                    }
break;
case 123:
//#line 499 "gramatica.y"
{ if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(1).ival + ": Se detectó una expresión desde una resta");
                                                                      if (!error()) { yyval = new ParserVal(new ArbolSintactico("Resta", (ArbolSintactico)val_peek(2).obj, (ArbolSintactico)val_peek(0).obj));} yyval.ival = val_peek(1).ival; 
                                                                    }
break;
case 124:
//#line 502 "gramatica.y"
{ erroresSintacticos.add("ERROR SINTÁCTICO EN LÍNEA " + val_peek(1).ival + ": falta operando a la izquierda de '+'"); }
break;
case 125:
//#line 503 "gramatica.y"
{ erroresSintacticos.add("ERROR SINTÁCTICO EN LÍNEA " + val_peek(1).ival + ": falta operando a la derecha de '+'"); }
break;
case 126:
//#line 504 "gramatica.y"
{ erroresSintacticos.add("ERROR SINTÁCTICO EN LÍNEA " + val_peek(1).ival + ": falta operando a la izquierda de '-'"); }
break;
case 127:
//#line 505 "gramatica.y"
{ erroresSintacticos.add("ERROR SINTÁCTICO EN LÍNEA " + val_peek(1).ival + ": falta operando a la derecha de '-'"); }
break;
case 128:
//#line 506 "gramatica.y"
{ if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó una expresión desde un término");    
                                                                      yyval = val_peek(0);
                                                                    }
break;
case 129:
//#line 511 "gramatica.y"
{ if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(1).ival + ": Se detectó un término desde una multiplicación");
                                                                      yyval = new ParserVal(new ArbolSintactico("Multiplicacion", (ArbolSintactico)val_peek(2).obj, (ArbolSintactico)val_peek(0).obj)); yyval.ival = val_peek(1).ival;
                                                                    }
break;
case 130:
//#line 514 "gramatica.y"
{ if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(1).ival + ": Se detectó un término desde una división");
                                                                      yyval = new ParserVal(new ArbolSintactico("Division", (ArbolSintactico)val_peek(2).obj, (ArbolSintactico)val_peek(0).obj)); yyval.ival = val_peek(1).ival; 
                                                                    }
break;
case 131:
//#line 517 "gramatica.y"
{ erroresSintacticos.add("ERROR SINTÁCTICO EN LÍNEA " + val_peek(1).ival + ": falta operando a la izquierda de '*'"); }
break;
case 132:
//#line 518 "gramatica.y"
{ erroresSintacticos.add("ERROR SINTÁCTICO EN LÍNEA " + val_peek(1).ival + ": falta operando a la derecha de '*'"); }
break;
case 133:
//#line 519 "gramatica.y"
{ erroresSintacticos.add("ERROR SINTÁCTICO EN LÍNEA " + val_peek(1).ival + ": falta operando a la izquierda de '/'"); }
break;
case 134:
//#line 520 "gramatica.y"
{ erroresSintacticos.add("ERROR SINTÁCTICO EN LÍNEA " + val_peek(1).ival + ": falta operando a la derecha de '/'"); }
break;
case 135:
//#line 521 "gramatica.y"
{ if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un término desde un factor");
                                                                      yyval = val_peek(0); 
                                                                    }
break;
case 136:
//#line 524 "gramatica.y"
{ constantePositiva(val_peek(0)); restriccionCteFloat(val_peek(0)); erroresSintacticos.add("ERROR SINTÁCTICO EN LÍNEA " + val_peek(0).ival + ": falta operador/comparador entre término y constante"); }
break;
case 137:
//#line 527 "gramatica.y"
{ constantePositiva(val_peek(0)); if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un factor desde una constante");
                                                                      restriccionCteFloat(val_peek(0));
                                                                      yyval = new ParserVal(new ArbolSintactico((Simbolo)val_peek(0).obj)); yyval.ival = val_peek(0).ival;
                                                                    }
break;
case 138:
//#line 531 "gramatica.y"
{ constanteNegativa(val_peek(0)); if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un factor desde una constante negativa");
                                                                      restriccionCteFloat(val_peek(0));
                                                                      yyval = new ParserVal(new ArbolSintactico((Simbolo)val_peek(0).obj)); yyval.ival = val_peek(0).ival;
                                                                    }
break;
case 139:
//#line 535 "gramatica.y"
{ if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un factor desde un identificador");
                                                                      Simbolo simboloActual = checkearAlcance(val_peek(0).sval, val_peek(0).ival);
                                                                      yyval = new ParserVal(new ArbolSintactico(simboloActual));
                                                                      yyval.ival = val_peek(0).ival;
                                                                      if ( simboloActual != null && (!simboloActual.getCategoria().equals("Identificador") || simboloActual.getTipo().equals("Lambda"))) {
                                                                        erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + val_peek(0).ival + ": el identificador '" + val_peek(0).sval + "' no es una variable");
                                                                      }
                                                                    }
break;
case 140:
//#line 543 "gramatica.y"
{ if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un factor desde una función");
                                                                      yyval = val_peek(0);
                                                                    }
break;
case 141:
//#line 546 "gramatica.y"
{ erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + val_peek(0).ival + ": Las funciones Lambda no devuelven valor y no pueden usarse en expresiones"); }
break;
case 142:
//#line 547 "gramatica.y"
{ constantePositiva(val_peek(1));
                                                                      if (enParamInvocacion) {
                                                                        if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(3).ival + ": Se detectó la función Trunc en uno de los parámetros reales de la invocación");
                                                                      }
                                                                      else
                                                                        erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + val_peek(3).ival + ": la función TRUNC solo puede utilizarse en parámetros reales de invocación de funciones");
                                                                      yyval = new ParserVal(new ArbolSintactico("Trunc", new ArbolSintactico((Simbolo)val_peek(1).obj), null));
                                                                      yyval.ival = val_peek(3).ival;
                                                                    }
break;
case 143:
//#line 556 "gramatica.y"
{ constanteNegativa(val_peek(1)); 
                                                                      if (enParamInvocacion) {
                                                                        if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(4).ival + ": Se detectó la función Trunc en uno de los parámetros reales de la invocación");
                                                                      }
                                                                      else  
                                                                        erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + val_peek(4).ival + ": la función TRUNC solo puede utilizarse en parámetros reales de invocación de funciones");
                                                                      yyval = new ParserVal(new ArbolSintactico("Trunc", new ArbolSintactico((Simbolo)val_peek(1).obj), null));
                                                                      yyval.ival = val_peek(4).ival;
                                                                    }
break;
case 144:
//#line 567 "gramatica.y"
{ constantePositiva(val_peek(0)); restriccionCteFloat(val_peek(0)); yyval = val_peek(2); ((ArrayList<Simbolo>)yyval.obj).add((Simbolo)val_peek(0).obj); yyval.ival = val_peek(0).ival; }
break;
case 145:
//#line 568 "gramatica.y"
{ constanteNegativa(val_peek(0)); restriccionCteFloat(val_peek(0)); yyval = val_peek(3); ((ArrayList<Simbolo>)yyval.obj).add((Simbolo)val_peek(0).obj); yyval.ival = val_peek(0).ival; }
break;
case 146:
//#line 569 "gramatica.y"
{ constantePositiva(val_peek(0)); restriccionCteFloat(val_peek(0)); yyval = new ParserVal(new ArrayList<Simbolo>()); ((ArrayList<Simbolo>)yyval.obj).add((Simbolo)val_peek(0).obj); yyval.ival = val_peek(0).ival; }
break;
case 147:
//#line 570 "gramatica.y"
{ constanteNegativa(val_peek(0)); restriccionCteFloat(val_peek(0)); yyval = new ParserVal(new ArrayList<Simbolo>()); ((ArrayList<Simbolo>)yyval.obj).add((Simbolo)val_peek(0).obj); yyval.ival = val_peek(0).ival; }
break;
case 148:
//#line 571 "gramatica.y"
{ constantePositiva(val_peek(0)); restriccionCteFloat(val_peek(0)); erroresSintacticos.add("ERROR SINTÁCTICO EN LÍNEA " + val_peek(1).ival + ": falta ',' entre constantes"); constantePositiva(val_peek(0)); restriccionCteFloat(val_peek(0)); }
break;
case 149:
//#line 572 "gramatica.y"
{ constanteNegativa(val_peek(0)); restriccionCteFloat(val_peek(0)); erroresSintacticos.add("ERROR SINTÁCTICO EN LÍNEA " + val_peek(2).ival + ": falta ',' entre constantes"); constanteNegativa(val_peek(0)); restriccionCteFloat(val_peek(0)); }
break;
case 150:
//#line 573 "gramatica.y"
{erroresSintacticos.add("ERROR SINTÁCTICO EN LÍNEA " + val_peek(1).ival + ": la variable '" + val_peek(0).sval + "' no puede estar en una lista de constantes");}
break;
case 151:
//#line 576 "gramatica.y"
{ yyval = val_peek(2); ((ArrayList<Simbolo>)yyval.obj).addAll( ((ArrayList<Simbolo>)val_peek(0).obj) ); yyval.ival = val_peek(0).ival; }
break;
//#line 2173 "Parser.java"
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
