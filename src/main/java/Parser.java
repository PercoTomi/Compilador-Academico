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
   28,   28,   28,   30,   29,   31,   33,   32,   10,   10,
   10,   10,   10,   10,   10,   10,   10,   10,   10,   12,
   12,   12,   12,   12,   35,   35,   34,   34,   34,   34,
   36,   36,   37,   37,   37,   37,   37,   37,   15,   15,
   16,   16,   25,   25,   24,   24,   11,   11,   13,   13,
   13,   13,   13,   40,   38,   39,   41,   41,   42,   42,
   42,   42,   42,   42,   42,   43,   43,   43,   23,   23,
   23,   23,   23,   23,   23,   44,   44,   44,   44,   44,
   44,   44,   44,   45,   45,   45,   45,   45,   45,   45,
   26,   26,   26,   26,   26,   26,   26,   27,
};
final static short yylen[] = {                            2,
    0,    0,    5,    2,    3,    2,    2,    2,    1,    2,
    1,    2,    2,    1,    1,    2,    3,    2,    2,    1,
    1,    1,    1,    1,    1,    1,    2,    0,    0,    8,
    3,    2,    3,    1,    3,    2,    2,    1,    2,    1,
    1,    4,    3,    3,    2,    3,    3,    3,    3,    3,
    3,    2,    2,    0,    2,    1,    0,    2,    4,    6,
    4,    5,    3,    5,    5,    3,    4,    3,    3,    4,
    3,    3,    3,    2,    1,    1,    3,    2,    2,    1,
    3,    1,    1,    1,    1,    1,    1,    1,    1,    1,
    3,    1,    3,    1,    1,    3,    3,    2,    3,    3,
    4,    6,    7,    1,    2,    1,    3,    1,    3,    6,
    5,    5,    4,    5,    2,    4,    3,    3,    3,    3,
    3,    3,    3,    3,    1,    3,    3,    3,    3,    3,
    3,    1,    2,    1,    2,    1,    1,    1,    4,    5,
    3,    4,    1,    2,    2,    3,    3,    3,
};
final static short yydefred[] = {                         0,
    0,    0,    0,   89,    0,    0,    0,   90,    0,    0,
    0,    0,   11,    0,    0,   21,   22,   23,   24,   25,
   26,    0,    0,    0,   54,    0,   16,    0,    0,   95,
  134,    0,    0,    0,  137,  138,    0,    0,    0,    0,
    0,  132,    0,    0,    0,   76,    0,    0,    0,    6,
    0,    4,    8,   10,   12,   13,    0,    0,   28,    0,
  105,    0,    0,    0,    0,    0,    0,    0,    0,   98,
    0,    0,    0,    0,    0,    0,    0,  108,    0,    2,
    0,    0,    0,    0,    0,    0,  135,   85,   86,   87,
   88,   83,   84,    0,    0,    0,    0,   66,    0,   78,
  133,    0,    0,   46,    0,   44,   68,   71,    0,   72,
    5,    0,    0,    0,   96,    0,  143,    0,    0,    0,
    0,   50,    0,   69,    0,    0,   55,  100,    0,    0,
    0,    0,    0,   97,   99,    0,    0,    0,    0,    0,
    0,    0,    0,  128,  130,    0,    0,   77,    0,    0,
    0,    0,    0,   67,    0,   54,    0,   59,  129,  126,
  131,  127,   42,   70,   91,    0,   29,  144,    0,  145,
    0,    0,   61,   53,    0,   52,    0,    0,    0,  101,
  117,  109,  107,    0,   18,    0,    0,    0,    3,  139,
    0,   65,   58,   64,    0,   40,    0,   32,    0,    0,
   34,    0,    0,  141,    0,    0,  146,   51,    0,    0,
  116,    0,    0,  113,    0,   17,  140,   60,   39,    0,
   36,   31,    0,    0,  142,  102,    0,  111,    0,  112,
   35,   33,    0,  103,  110,   30,
};
final static short yydgoto[] = {                         10,
   11,   28,  140,   12,   13,   14,   15,  139,   16,   17,
   35,   19,   36,   21,   22,   58,   59,  167,  113,  202,
  200,  201,   37,   38,   24,  121,  122,  127,   65,   66,
   25,  155,  156,   39,   49,   40,   96,   26,   75,   76,
   77,   78,   79,   41,   42,
};
final static short yysindex[] = {                       -69,
  -23,    0,  131,    0,  145, -214,  212,    0,  954,    0,
 -226,  968,    0,   16,   24,    0,    0,    0,    0,    0,
    0, -164,   -3,  -28,    0,  169,    0,  903,   35,    0,
    0,   58,  237, -156,    0,    0,  609,   13, -248,   69,
  -32,    0,  233,   28,  131,    0,  190,   79,   78,    0,
  982,    0,    0,    0,    0,    0,    0,   77,    0,  237,
    0, -129, -123,    5,  124, -106,   95,   98, -200,    0,
 -118, -115,  -38,   13,  104,  105,  103,    0,  554,    0,
  417,  417,  133,  133,  -43,  112,    0,    0,    0,    0,
    0,    0,    0,  422,  461,  237, -103,    0, -136,    0,
    0,  496,  511,    0,   56,    0,    0,    0,  116,    0,
    0,  -97,  121,   79,    0,  119,    0,  -92,  119,  126,
   17,    0,  131,    0,  524,  109,    0,    0,  -17,  -84,
  134,  136,  -78,    0,    0,  185, -125,  138,  742,  -94,
   -8,  -32,  -32,    0,    0,  151,  -65,    0,   -8,  -32,
   -8,  -32,   79,    0,  -67,    0,  -61,    0,    0,    0,
    0,    0,    0,    0,    0,  183,    0,    0,    5,    0,
   50,  -55,    0,    0, 1042,    0,  164,  -47,  171,    0,
    0,    0,    0,  896,    0,  -44,    7,  173,    0,    0,
  245,    0,    0,    0,   -9,    0, -202,    0,   41,   40,
    0,  174,   17,    0,   44,  119,    0,    0,  279,  283,
    0,   72,   70,    0,   73,    0,    0,    0,    0,   88,
    0,    0, -219, 1056,    0,    0,  305,    0,   93,    0,
    0,    0,  996,    0,    0,    0,
};
final static short yyrindex[] = {                         0,
    0,  162,    0,    0,    0,  579,    0,    0,    0,    0,
    0,   89,    0,  917,  931,    0,    0,    0,    0,    0,
    0,    0,  -26,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,  260,  -18,  655,  623,
    4,    0,    0,  690,    0,    0,    0,  308,  704,    0,
   94,    0,    0,    0,    0,    0,  -40,  881,    0,    0,
    0,    0,    0,    0,    0,    0,  429,    0,    0,    0,
    0,    0,    0,  139,    0,    0,  310,    0,    0,    0,
    0,    0,    0,    0,    0,  665,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,  808,    0,  718,    0,
    0,    0,    0,    0,  736,    0,    0,    0,  762,    0,
    0,    0,    0,  776,    0,  390,    0,    0,  466,  794,
  818,    0,    0,    0,    0,    0,    0,    0,    0,    0,
  458,    0,   47,    0,    0,    0,    0, 1019,    0,    0,
    0,   27,   49,    0,    0,    0,    0,    0,   71,   96,
  333,  355,  282,    0,    0,    0,  808,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0, 1013,    0,
    0,    0,    0,    0,    0,    0,    0, 1036,    0,    0,
    0,    0,    0,    0,  843,    0,    0,    0,   62,    0,
    0,    0,  857,    0,    0,  438,    0,    0,  527,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,   74,
    0,    0,    0,    0,    0,    0,  596,    0,   76,    0,
    0,    0,    0,    0,    0,    0,
};
final static short yygindex[] = {                         0,
  326,    0,    0,   -6,  -11, 1123,    0, -105,    0,    0,
  633,    0,  681,    0,  534,    0,    0,    0,    0,    0,
    0,  132,   53,  625,  306,  206,    0,    0,  -33,    0,
    0,  220,    0,  -19,  332,  351,    0,    0,    0,    0,
    0,  250,    0,  -73,  -72,
};
final static int YYTABLESIZE=1332;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         41,
   54,  147,   51,   92,   95,   99,   94,  142,  143,  102,
  144,  145,   97,   98,  103,   63,  125,   94,   92,  175,
  150,  152,  136,  136,  136,  107,  136,  178,  136,  160,
  162,  184,   64,   83,   94,   27,   61,  196,   84,   54,
  136,  136,   62,  136,  125,  124,  125,    4,  125,  118,
   52,  197,   61,    9,  219,   45,    8,   44,   62,   48,
  171,  172,  125,  125,    4,  125,    4,  123,  106,  123,
   95,  123,   94,    8,   55,    8,   83,   82,   73,   81,
  222,   84,   56,  223,   92,  123,  123,  115,  123,  121,
  115,  121,   57,  121,  205,  105,  163,   85,   95,   48,
   94,   87,   38,  173,  136,   38,  136,  121,  121,  100,
  121,  124,  114,  124,   37,  124,  114,   37,  110,  114,
  112,   95,  193,   94,  157,  158,  125,  115,  125,  124,
  124,   30,  124,   30,    3,  128,  120,  129,  120,  131,
  120,  132,    5,    6,  134,  135,  136,    7,  153,  123,
   30,  123,  148,    3,  120,  120,  164,  120,  154,  165,
  166,    5,    6,   33,   62,  168,    7,  176,   34,  169,
   33,  121,  179,  121,  180,   34,  181,   34,  182,  104,
  136,  136,  189,  136,   43,  136,    1,    2,   73,   34,
    3,  190,  191,  124,  192,  124,  185,    4,    5,    6,
  194,   95,  207,    7,  209,   95,    8,   95,   69,   70,
  210,  211,  214,   71,  146,   92,   92,  233,  120,   92,
  120,   54,   95,  198,   69,  101,   92,   92,   92,   34,
  108,  216,   92,  133,   34,   92,   92,  136,  136,  136,
  177,  136,  136,  136,  136,  136,  136,  136,  136,  136,
  136,   47,  218,  136,  136,   60,   34,  136,  136,  125,
  125,   30,  117,  125,  125,  125,  125,  125,  125,  125,
  125,  125,  125,  104,  170,  125,  125,   34,  215,  125,
  125,   34,  123,  123,    1,  217,  123,  123,  123,  123,
  123,  123,  123,  123,  123,  123,  224,  221,  123,  123,
   82,  225,  123,  123,  121,  121,   30,  204,  121,  121,
  121,  121,  121,  121,  121,  121,  121,  121,   82,  226,
  121,  121,   81,  227,  121,  121,  124,  124,  228,  230,
  124,  124,  124,  124,  124,  124,  124,  124,  124,  124,
   81,  229,  124,  124,  231,  234,  124,  124,   75,  235,
  106,  120,  120,   80,  232,  120,  120,  120,  120,  120,
  120,  120,  120,  120,  120,    9,   75,  120,  120,  120,
    7,  120,  120,  122,  203,  122,  195,  122,  109,   29,
   30,   31,   82,   86,   82,  183,   29,   30,   31,   30,
   31,  122,  122,  123,  122,  119,  136,  119,   32,  119,
   29,   30,   31,    0,   81,   32,   81,   32,    0,    0,
  136,    0,    0,  119,  119,    0,  119,    1,    1,   32,
   95,    1,    0,    0,   29,   30,   67,    0,    1,    1,
    1,    0,   75,   93,    1,    4,    0,    1,    0,  196,
   29,   30,   31,   68,    8,   29,   30,   31,   93,    4,
   93,    4,    0,  197,    0,  122,    0,  122,    8,   32,
    8,   34,    0,   46,   32,    0,   34,   29,   30,   31,
  134,  134,    0,  134,    0,  134,    0,  119,    0,  119,
    0,  147,  147,    0,    0,   46,   32,    0,   29,   30,
   31,    0,   29,   30,   31,    0,  147,    0,    0,  135,
  135,    0,  135,    0,  135,   34,    0,   32,    0,   94,
    0,   32,    0,    0,   93,   82,   82,    0,    0,   82,
   82,   82,    0,    0,   94,    0,   82,   82,   82,    0,
    0,   82,   82,    0,    0,   82,   82,   81,   81,    0,
   34,   81,   81,   81,    0,    0,    0,    0,   81,   81,
   81,    0,    0,   81,   81,   34,    0,   81,   81,   72,
    0,    0,  147,   75,   75,    0,    0,   75,  139,  139,
    0,  139,    0,  139,   75,   75,   75,    0,    0,   75,
   75,    0,    0,   75,   75,    0,    0,    0,  122,  122,
   94,    0,  122,  122,  122,  122,  122,  122,  122,  122,
  122,  122,  130,    0,  122,  122,    0,    0,  122,  122,
  119,  119,    0,    0,  119,  119,  119,  119,  119,  119,
  119,  119,  119,  119,   23,    0,  119,  119,    0,    0,
  119,  119,   18,   23,    0,    0,   23,  140,  140,    0,
  140,   18,  140,    0,   18,   93,   93,    0,  174,   93,
   74,   95,   23,   94,    0,    0,   93,   93,   93,    0,
   18,   93,   93,    0,    0,   93,   93,    0,   93,   72,
   92,    0,  141,   30,   31,   23,  137,  149,   30,   31,
   20,   80,    0,   18,    0,    0,  134,  116,  119,   20,
   23,   32,   20,  147,  147,  147,   32,  147,   18,  199,
  134,   56,    0,   23,  147,  147,  147,    0,   20,  147,
  147,   18,    0,  147,  147,  135,  151,   30,   31,    0,
    0,   94,   94,   79,    0,   94,    0,    0,    0,  135,
  220,   20,   94,   94,   94,   32,    0,   94,   94,    0,
    0,   94,   94,    0,    0,   80,   20,   80,   45,   23,
    0,  159,   30,   31,    0,    0,  199,   18,    0,   20,
    0,   23,   74,   23,    0,    0,  161,   30,   31,   18,
   32,   18,    0,    0,    0,    0,   63,   54,    0,    0,
   30,    0,    0,    3,  139,   32,    0,   79,    0,   79,
    0,    5,    6,  116,   43,  206,    7,    0,  139,   23,
    0,    0,    0,    0,    0,   20,    0,   18,   23,    0,
   30,    0,    0,    3,   45,    0,   18,   20,    0,   20,
   73,    5,    6,    0,    0,    0,    7,    0,   74,    0,
    0,    0,    0,    0,   47,   56,    0,    0,   56,    0,
    0,    0,   63,    0,    0,    0,   56,   56,   23,    0,
    0,   56,   49,  140,    0,   20,   18,   23,    0,    0,
   43,    0,    0,    0,   20,   18,  187,  140,    0,    0,
    0,   88,   89,   90,   91,    0,   48,    0,   80,   80,
    0,    0,   80,   80,   80,    0,   73,    0,    0,   80,
   80,   80,    0,    0,   80,   80,    0,    0,   80,   80,
   47,   62,    0,    0,   20,    0,    0,    0,    0,    0,
    0,   54,    0,   20,   54,  148,    0,    0,   49,    0,
   79,   79,   54,   54,   79,   79,   79,   54,    0,    0,
   57,   79,   79,   79,    0,    0,   79,   79,    0,   27,
   79,   79,   48,    0,    0,   45,   45,    0,    0,   45,
    0,    0,    0,    0,    0,    0,   45,   45,   45,   74,
   74,   45,   45,   74,    0,   45,   45,   62,    0,    0,
   74,   74,   74,   63,   63,   74,   74,   63,    0,   74,
   74,  148,    0,    0,   63,   63,   63,    0,    0,   63,
   63,   43,   43,   63,   63,   43,    0,    0,   30,    0,
    0,    3,   43,   43,   43,   27,    0,   43,   43,    5,
    6,   43,   43,  186,    7,    0,    0,   73,   73,    0,
  213,   73,    0,    0,    0,    9,    0,    0,   73,   73,
   73,   47,   47,   73,   73,   47,    0,   73,   73,    0,
    0,   14,   47,   47,   47,    0,    0,   47,   47,   49,
   49,   47,   47,   49,    0,   15,    0,    0,    0,    0,
   49,   49,   49,    0,   57,   49,   49,   57,    0,   49,
   49,    0,    0,   48,   48,   57,   57,   48,   50,    0,
   57,    0,    0,    0,   48,   48,   48,    0,    0,   48,
   48,    0,   53,   48,   48,    0,    0,    0,   62,   62,
    0,    0,   62,    0,    0,    0,  111,    0,    0,   62,
   62,   62,  148,  148,   62,   62,  148,    0,   62,   62,
  236,    0,    0,  148,  148,  148,    0,    0,  148,  148,
    0,    0,  148,  148,    0,  118,   27,   27,    0,    0,
   27,    0,    0,   20,    0,    0,    0,   27,   27,   27,
    0,    0,   30,   27,    0,    3,   27,   27,    1,   30,
   19,    0,    3,    5,    6,    0,  208,  212,    7,    4,
    5,    6,   14,   14,    0,    7,   14,    0,    8,    0,
    0,    0,    0,   14,   14,   14,   15,   15,  126,   14,
   15,    0,   14,   14,    0,    0,    0,   15,   15,   15,
    0,  138,    0,   15,    0,    0,   15,   15,    0,    1,
   30,    0,    0,    3,    0,    0,    0,    0,    0,    0,
    4,    5,    6,    1,   30,    0,    7,    3,    0,    8,
    0,    0,    0,    0,    4,    5,    6,    1,   30,    0,
    7,    3,    0,    8,    0,    0,    0,  138,    4,    5,
    6,    1,   30,    0,    7,    3,    0,    8,    0,  138,
    0,  188,    4,    5,    6,    0,    0,    0,    7,  118,
    0,    8,  118,    0,    0,   20,    0,    0,   20,    0,
  118,  118,    0,    0,    0,  118,   20,   20,    0,    0,
   20,   20,   19,    0,    0,   19,    0,  188,   30,    0,
    0,    3,    0,   19,   19,    0,  188,   19,   19,    5,
    6,    1,   30,    0,    7,    3,    0,    0,    0,    0,
    0,    0,    4,    5,    6,    0,    0,    0,    7,    0,
    0,    8,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         40,
   12,   45,    9,   44,   43,   39,   45,   81,   82,   42,
   83,   84,  261,  262,   47,   44,  123,   44,   59,  125,
   94,   95,   41,   42,   43,   45,   45,   45,   47,  102,
  103,  137,   61,   42,   61,   59,   40,  257,   47,   51,
   59,   60,   46,   62,   41,   65,   43,  267,   45,   45,
  277,  271,   40,  123,  257,  270,  276,    5,   46,    7,
   44,   45,   59,   60,  267,   62,  267,   41,   41,   43,
   43,   45,   45,  276,   59,  276,   42,   43,   26,   45,
   41,   47,   59,   44,  125,   59,   60,   41,   62,   41,
   44,   43,  257,   45,   45,   43,   41,   40,   43,   47,
   45,  258,   41,  123,  123,   44,  125,   59,   60,   41,
   62,   41,   60,   43,   41,   45,   41,   44,   41,   44,
   44,   43,  156,   45,  261,  262,  123,  257,  125,   59,
   60,  257,   62,  257,  260,   41,   41,   40,   43,  258,
   45,  257,  268,  269,   41,   41,   44,  273,   96,  123,
  257,  125,   41,  260,   59,   60,   41,   62,  262,  257,
   40,  268,  269,   40,   46,  258,  273,   59,   45,   44,
   40,  123,  257,  125,   41,   45,   41,   45,  257,   41,
   42,   43,  277,   45,   40,   47,  256,  257,  136,   45,
  260,   41,  258,  123,  262,  125,   59,  267,  268,  269,
  262,   40,  258,  273,   41,   44,  276,   46,   40,   41,
  258,   41,  257,   45,  258,  256,  257,  224,  123,  260,
  125,  233,   61,   41,   40,  258,  267,  268,  269,   45,
   41,   59,  273,  272,   45,  276,  277,  256,  257,  258,
  258,  260,  261,  262,  263,  264,  265,  266,  267,  268,
  269,   40,  262,  272,  273,  259,   45,  276,  277,  256,
  257,  257,  258,  260,  261,  262,  263,  264,  265,  266,
  267,  268,  269,   41,  258,  272,  273,   45,  272,  276,
  277,   45,  256,  257,  123,   41,  260,  261,  262,  263,
  264,  265,  266,  267,  268,  269,  123,  257,  272,  273,
   41,  258,  276,  277,  256,  257,  257,  258,  260,  261,
  262,  263,  264,  265,  266,  267,  268,  269,   59,   41,
  272,  273,   41,   41,  276,  277,  256,  257,  257,  257,
  260,  261,  262,  263,  264,  265,  266,  267,  268,  269,
   59,  272,  272,  273,  257,   41,  276,  277,   41,  257,
   41,  256,  257,   28,  223,  260,  261,  262,  263,  264,
  265,  266,  267,  268,  269,  277,   59,  272,  273,   64,
  277,  276,  277,   41,  169,   43,  157,   45,   47,  256,
  257,  258,  123,   33,  125,  136,  256,  257,  258,  257,
  258,   59,   60,  270,   62,   41,  258,   43,  275,   45,
  256,  257,  258,   -1,  123,  275,  125,  275,   -1,   -1,
  272,   -1,   -1,   59,   60,   -1,   62,  256,  257,  275,
  259,  260,   -1,   -1,  256,  257,  258,   -1,  267,  268,
  269,   -1,  125,   44,  273,  267,   -1,  276,   -1,  257,
  256,  257,  258,  275,  276,  256,  257,  258,   59,  267,
   61,  267,   -1,  271,   -1,  123,   -1,  125,  276,  275,
  276,   45,   -1,  274,  275,   -1,   45,  256,  257,  258,
   42,   43,   -1,   45,   -1,   47,   -1,  123,   -1,  125,
   -1,   44,   45,   -1,   -1,  274,  275,   -1,  256,  257,
  258,   -1,  256,  257,  258,   -1,   59,   -1,   -1,   42,
   43,   -1,   45,   -1,   47,   45,   -1,  275,   -1,   44,
   -1,  275,   -1,   -1,  125,  256,  257,   -1,   -1,  260,
  261,  262,   -1,   -1,   59,   -1,  267,  268,  269,   -1,
   -1,  272,  273,   -1,   -1,  276,  277,  256,  257,   -1,
   45,  260,  261,  262,   -1,   -1,   -1,   -1,  267,  268,
  269,   -1,   -1,  272,  273,   45,   -1,  276,  277,   26,
   -1,   -1,  125,  256,  257,   -1,   -1,  260,   42,   43,
   -1,   45,   -1,   47,  267,  268,  269,   -1,   -1,  272,
  273,   -1,   -1,  276,  277,   -1,   -1,   -1,  256,  257,
  125,   -1,  260,  261,  262,  263,  264,  265,  266,  267,
  268,  269,   69,   -1,  272,  273,   -1,   -1,  276,  277,
  256,  257,   -1,   -1,  260,  261,  262,  263,  264,  265,
  266,  267,  268,  269,    0,   -1,  272,  273,   -1,   -1,
  276,  277,    0,    9,   -1,   -1,   12,   42,   43,   -1,
   45,    9,   47,   -1,   12,  256,  257,   -1,  125,  260,
   26,   43,   28,   45,   -1,   -1,  267,  268,  269,   -1,
   28,  272,  273,   -1,   -1,  276,  277,   -1,   60,  136,
   62,   -1,  256,  257,  258,   51,  123,  256,  257,  258,
    0,   59,   -1,   51,   -1,   -1,  258,   63,   64,    9,
   66,  275,   12,  256,  257,  258,  275,  260,   66,  166,
  272,  123,   -1,   79,  267,  268,  269,   -1,   28,  272,
  273,   79,   -1,  276,  277,  258,  256,  257,  258,   -1,
   -1,  256,  257,   59,   -1,  260,   -1,   -1,   -1,  272,
  197,   51,  267,  268,  269,  275,   -1,  272,  273,   -1,
   -1,  276,  277,   -1,   -1,  123,   66,  125,   59,  125,
   -1,  256,  257,  258,   -1,   -1,  223,  125,   -1,   79,
   -1,  137,   59,  139,   -1,   -1,  256,  257,  258,  137,
  275,  139,   -1,   -1,   -1,   -1,   59,  123,   -1,   -1,
  257,   -1,   -1,  260,  258,  275,   -1,  123,   -1,  125,
   -1,  268,  269,  169,   59,  171,  273,   -1,  272,  175,
   -1,   -1,   -1,   -1,   -1,  125,   -1,  175,  184,   -1,
  257,   -1,   -1,  260,  125,   -1,  184,  137,   -1,  139,
   59,  268,  269,   -1,   -1,   -1,  273,   -1,  125,   -1,
   -1,   -1,   -1,   -1,   59,  257,   -1,   -1,  260,   -1,
   -1,   -1,  125,   -1,   -1,   -1,  268,  269,  224,   -1,
   -1,  273,   59,  258,   -1,  175,  224,  233,   -1,   -1,
  125,   -1,   -1,   -1,  184,  233,  125,  272,   -1,   -1,
   -1,  263,  264,  265,  266,   -1,   59,   -1,  256,  257,
   -1,   -1,  260,  261,  262,   -1,  125,   -1,   -1,  267,
  268,  269,   -1,   -1,  272,  273,   -1,   -1,  276,  277,
  125,   59,   -1,   -1,  224,   -1,   -1,   -1,   -1,   -1,
   -1,  257,   -1,  233,  260,   59,   -1,   -1,  125,   -1,
  256,  257,  268,  269,  260,  261,  262,  273,   -1,   -1,
  123,  267,  268,  269,   -1,   -1,  272,  273,   -1,   59,
  276,  277,  125,   -1,   -1,  256,  257,   -1,   -1,  260,
   -1,   -1,   -1,   -1,   -1,   -1,  267,  268,  269,  256,
  257,  272,  273,  260,   -1,  276,  277,  125,   -1,   -1,
  267,  268,  269,  256,  257,  272,  273,  260,   -1,  276,
  277,  125,   -1,   -1,  267,  268,  269,   -1,   -1,  272,
  273,  256,  257,  276,  277,  260,   -1,   -1,  257,   -1,
   -1,  260,  267,  268,  269,  125,   -1,  272,  273,  268,
  269,  276,  277,  272,  273,   -1,   -1,  256,  257,   -1,
  125,  260,   -1,   -1,   -1,  123,   -1,   -1,  267,  268,
  269,  256,  257,  272,  273,  260,   -1,  276,  277,   -1,
   -1,  125,  267,  268,  269,   -1,   -1,  272,  273,  256,
  257,  276,  277,  260,   -1,  125,   -1,   -1,   -1,   -1,
  267,  268,  269,   -1,  257,  272,  273,  260,   -1,  276,
  277,   -1,   -1,  256,  257,  268,  269,  260,  125,   -1,
  273,   -1,   -1,   -1,  267,  268,  269,   -1,   -1,  272,
  273,   -1,  125,  276,  277,   -1,   -1,   -1,  256,  257,
   -1,   -1,  260,   -1,   -1,   -1,  125,   -1,   -1,  267,
  268,  269,  256,  257,  272,  273,  260,   -1,  276,  277,
  125,   -1,   -1,  267,  268,  269,   -1,   -1,  272,  273,
   -1,   -1,  276,  277,   -1,  123,  256,  257,   -1,   -1,
  260,   -1,   -1,  125,   -1,   -1,   -1,  267,  268,  269,
   -1,   -1,  257,  273,   -1,  260,  276,  277,  256,  257,
  125,   -1,  260,  268,  269,   -1,  125,  272,  273,  267,
  268,  269,  256,  257,   -1,  273,  260,   -1,  276,   -1,
   -1,   -1,   -1,  267,  268,  269,  256,  257,   66,  273,
  260,   -1,  276,  277,   -1,   -1,   -1,  267,  268,  269,
   -1,   79,   -1,  273,   -1,   -1,  276,  277,   -1,  256,
  257,   -1,   -1,  260,   -1,   -1,   -1,   -1,   -1,   -1,
  267,  268,  269,  256,  257,   -1,  273,  260,   -1,  276,
   -1,   -1,   -1,   -1,  267,  268,  269,  256,  257,   -1,
  273,  260,   -1,  276,   -1,   -1,   -1,  125,  267,  268,
  269,  256,  257,   -1,  273,  260,   -1,  276,   -1,  137,
   -1,  139,  267,  268,  269,   -1,   -1,   -1,  273,  257,
   -1,  276,  260,   -1,   -1,  257,   -1,   -1,  260,   -1,
  268,  269,   -1,   -1,   -1,  273,  268,  269,   -1,   -1,
  272,  273,  257,   -1,   -1,  260,   -1,  175,  257,   -1,
   -1,  260,   -1,  268,  269,   -1,  184,  272,  273,  268,
  269,  256,  257,   -1,  273,  260,   -1,   -1,   -1,   -1,
   -1,   -1,  267,  268,  269,   -1,   -1,   -1,  273,   -1,
   -1,  276,
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

//#line 570 "gramatica.y"

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
    warnings.add("||================ ANALIZADOR SINTÁCTICO: Warning en línea " + nroLinea+ ": hay más variables que constantes en la asignación múltiple. Las variables sobrantes se inicializan en 0L");
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
//#line 1019 "Parser.java"
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
{stringAmbitos.append(":"+((Simbolo)val_peek(0).obj).getLexema());}
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
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el nombre del programa");}
break;
case 5:
//#line 28 "gramatica.y"
{ yyval = val_peek(1); yyval.ival = val_peek(0).ival; }
break;
case 6:
//#line 29 "gramatica.y"
{ yyval.obj = null; yyval.ival = val_peek(0).ival; }
break;
case 7:
//#line 30 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta llave de cierre del bloque de sentencias"); yyval.obj = null; yyval.ival = val_peek(0).ival; }
break;
case 8:
//#line 31 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta llave de apertura del bloque de sentencias"); yyval.obj = null; yyval.ival = val_peek(1).ival; }
break;
case 9:
//#line 32 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": faltan ambas llaves del bloque de sentencias"); yyval.obj = null; yyval.ival = val_peek(0).ival; }
break;
case 10:
//#line 35 "gramatica.y"
{ if (!error()) {
                                                                            if (val_peek(1).obj != null && val_peek(0).obj != null) { ((ArbolSintactico)val_peek(1).obj).setUltimoDerecho(new ArbolSintactico("Sentencia", ((ArbolSintactico)val_peek(0).obj) , null )); yyval = val_peek(1); } 
                                                                            else if (val_peek(1).obj != null) { yyval= val_peek(1); yyval.ival = val_peek(1).ival; }
                                                                            else if (val_peek(0).obj != null) { yyval= new ParserVal(new ArbolSintactico("Sentencia", ((ArbolSintactico)val_peek(0).obj), null));} 
                                                                          }
                                                                          yyval.ival = val_peek(0).ival; 
                                                                        }
break;
case 11:
//#line 42 "gramatica.y"
{ if (!error()) { if (val_peek(0).obj != null) { yyval = new ParserVal(new ArbolSintactico("Sentencia", ((ArbolSintactico)val_peek(0).obj), null)); }} yyval.ival = val_peek(0).ival;}
break;
case 12:
//#line 45 "gramatica.y"
{ yyval = val_peek(1);}
break;
case 13:
//#line 46 "gramatica.y"
{ yyval.obj = null; yyval.ival = val_peek(0).ival; }
break;
case 14:
//#line 47 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta ; al final de la sentencia ejecutable"); yyval.ival = val_peek(0).ival;}
break;
case 15:
//#line 48 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta ; al final de la declaración"); yyval.ival = val_peek(0).ival;}
break;
case 16:
//#line 49 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": sentencia no válida"); yyval.ival = val_peek(0).ival;}
break;
case 17:
//#line 52 "gramatica.y"
{ if (!error()) {
                                                                                    ((ArbolSintactico)val_peek(2).obj).setUltimoDerecho(new ArbolSintactico("Sentencia", ((ArbolSintactico)val_peek(1).obj) , null ));
                                                                                    yyval = val_peek(2); 
                                                                                    }
                                                                                    yyval.ival = val_peek(0).ival;
                                                                                  }
break;
case 18:
//#line 58 "gramatica.y"
{ if (!error()) { yyval = new ParserVal(new ArbolSintactico("Sentencia", ((ArbolSintactico)val_peek(1).obj), null));} yyval.ival = val_peek(0).ival;}
break;
case 19:
//#line 59 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta ; al final de la sentencia ejecutable"); yyval.ival = val_peek(0).ival;}
break;
case 20:
//#line 60 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta ; al final de la sentencia ejecutable"); yyval.ival = val_peek(0).ival;}
break;
case 21:
//#line 63 "gramatica.y"
{ yyval = val_peek(0); }
break;
case 22:
//#line 64 "gramatica.y"
{ yyval = val_peek(0); }
break;
case 23:
//#line 65 "gramatica.y"
{ erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + val_peek(0).ival + ": No está permitida la invocación de funciones sin utilizar su retorno"); }
break;
case 24:
//#line 66 "gramatica.y"
{ if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un print");
                                                                                yyval = val_peek(0);
                                                                              }
break;
case 25:
//#line 69 "gramatica.y"
{ if(enFunc==0) {erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + val_peek(0).ival + ": No está permitida la invocación de función lambda fuera de una función");} 
                                                                                else { yyval = val_peek(0); }
                                                                              }
break;
case 26:
//#line 72 "gramatica.y"
{chequeoSentenciaReturn(val_peek(0)); yyval = val_peek(0);}
break;
case 27:
//#line 75 "gramatica.y"
{ if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó una declaración de variables");}
break;
case 28:
//#line 76 "gramatica.y"
{String idFuncRaw = ((Simbolo)val_peek(0).obj).getLexema(); renombrarVariable(val_peek(0)); stringAmbitos.append(":"+idFuncRaw); stackTipoFuncActual.push(val_peek(1).sval); enDeclaracionParam = true;}
break;
case 29:
//#line 76 "gramatica.y"
{enDeclaracionParam = false; stackParametrosActuales.push(parametrosActuales); cambiarASimboloFuncion((Simbolo)val_peek(2).obj); parametrosActuales = new ArrayList<>(); enFunc++;}
break;
case 30:
//#line 76 "gramatica.y"
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
//#line 96 "gramatica.y"
{ ((Simbolo)val_peek(0).obj).setTipo(val_peek(1).sval);
                                                                       renombrarVariable(val_peek(0)); 
                                                                       if (val_peek(1).sval.equals("Lambda")) {
                                                                          erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + val_peek(1).ival + ": el tipo 'Lambda' no puede utilizar la semántica de pasaje por Copia-Valor (CV)");
                                                                       }
                                                                       parametrosActuales.add(new Parametro(((Simbolo)val_peek(0).obj), "CV"));
                                                                     }
break;
case 36:
//#line 103 "gramatica.y"
{ ((Simbolo)val_peek(0).obj).setTipo(val_peek(1).sval);
                                                                       renombrarVariable(val_peek(0));
                                                                       if (val_peek(1).sval.equals("Lambda")) {
                                                                          parametrosActuales.add(new Parametro(((Simbolo)val_peek(0).obj), "CV"));
                                                                       } else parametrosActuales.add(new Parametro(((Simbolo)val_peek(0).obj), "CVR"));
                                                                     }
break;
case 37:
//#line 109 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el nombre del parámetro");
                                                                     }
break;
case 38:
//#line 111 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta el nombre del parámetro");
                                                                     }
break;
case 39:
//#line 113 "gramatica.y"
{ renombrarVariable(val_peek(0)); 
                                                                       parametrosActuales.add(new Parametro(((Simbolo)val_peek(0).obj), "CV")); 
                                                                       erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el tipo del parámetro");
                                                                     }
break;
case 40:
//#line 117 "gramatica.y"
{ renombrarVariable(val_peek(0)); 
                                                                       parametrosActuales.add(new Parametro(((Simbolo)val_peek(0).obj), "CVR")); 
                                                                       erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta el tipo del parámetro");
                                                                     }
break;
case 41:
//#line 123 "gramatica.y"
{yyval = new ParserVal((SimboloLong)val_peek(0).obj);}
break;
case 42:
//#line 126 "gramatica.y"
{ if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(1).ival + ": Se detectó una sentencia return");
                                                                              if (!error()) { yyval = new ParserVal(new ArbolSintactico("Return", (ArbolSintactico)val_peek(1).obj,  null)); } yyval.ival = val_peek(0).ival;
                                                                            }
break;
case 43:
//#line 129 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el paréntesis de cierre en sentencia return"); yyval.ival = val_peek(0).ival;}
break;
case 44:
//#line 130 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el paréntesis de apertura en sentencia return"); yyval.ival = val_peek(0).ival;}
break;
case 45:
//#line 131 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": faltan ambos paréntesis en sentencia return"); yyval.ival = val_peek(0).ival;}
break;
case 46:
//#line 132 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta de argumento en sentencia return"); yyval.ival = val_peek(0).ival;}
break;
case 47:
//#line 135 "gramatica.y"
{ if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó una asignación simple");
                                                                                            Simbolo simboloAAsignar = checkearAlcance(val_peek(2).sval, val_peek(2).ival);
                                                                                            if( simboloAAsignar != null && (!simboloAAsignar.getCategoria().equals("Identificador") || simboloAAsignar.getTipo().equals("Lambda"))) {
                                                                                              erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + val_peek(2).ival + ": El identificador '" + val_peek(2).sval + "' no es una variable");
                                                                                            }
                                                                                            if (!error()) { 
                                                                                              yyval = new ParserVal( new ArbolSintactico("Asig", new ArbolSintactico(simboloAAsignar), (ArbolSintactico)val_peek(0).obj) );
                                                                                            }
                                                                                          }
break;
case 48:
//#line 144 "gramatica.y"
{ if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó una asignación múltiple");
                                                                                            if (((ArrayList<Simbolo>)val_peek(2).obj).size() >= ((ArrayList<Simbolo>)val_peek(0).obj).size()) {
                                                                                              if(!error()) {  
                                                                                                yyval = new ParserVal(generarAsigMultiple((ArrayList<Simbolo>)val_peek(2).obj, (ArrayList<Simbolo>)val_peek(0).obj, val_peek(1).ival));
                                                                                              }
                                                                                            }
                                                                                            else erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + val_peek(0).ival + ": La cantidad de constantes en la asignación múltiple es mayor a la cantidad de variables");
                                                                                          }
break;
case 49:
//#line 152 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": No se permite la asignación múltiple utilizando variables en el lado derecho");}
break;
case 50:
//#line 153 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": No se permite la asignación múltiple utilizando variables en el lado derecho");
                                                                                            if (((ArrayList<Simbolo>)val_peek(2).obj).size() >= ((ArrayList<Simbolo>)val_peek(0).obj).size()) {
                                                                                              if(!error()) {  
                                                                                                yyval = new ParserVal(generarAsigMultiple((ArrayList<Simbolo>)val_peek(2).obj, (ArrayList<Simbolo>)val_peek(0).obj, val_peek(1).ival));
                                                                                              }
                                                                                            }
                                                                                            else erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + val_peek(0).ival + ": La cantidad de elementos en la derecha de la asignación múltiple es mayor a la cantidad de variables del lado izquierdo");
                                                                                          }
break;
case 51:
//#line 163 "gramatica.y"
{ yyval = val_peek(1); yyval.ival = val_peek(0).ival; }
break;
case 52:
//#line 164 "gramatica.y"
{ if (!error()) { yyval = new ParserVal(new ArbolSintactico("Sentencia", ((ArbolSintactico)val_peek(1).obj), null)); } yyval.ival = val_peek(0).ival; }
break;
case 53:
//#line 165 "gramatica.y"
{ yyval.obj = null; yyval.ival = val_peek(0).ival; }
break;
case 54:
//#line 168 "gramatica.y"
{inicioIf();}
break;
case 55:
//#line 168 "gramatica.y"
{finIf(val_peek(0)); yyval = val_peek(0); }
break;
case 56:
//#line 171 "gramatica.y"
{enDo=true;}
break;
case 57:
//#line 174 "gramatica.y"
{sinRetIf.push(sinRet);}
break;
case 58:
//#line 174 "gramatica.y"
{if(sinRetIf.pop()) {sinRet=true;} yyval = val_peek(0); }
break;
case 59:
//#line 177 "gramatica.y"
{ if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un bloque IF");
                                                                                                  if (!error()) { yyval = new ParserVal(new ArbolSintactico("If", (ArbolSintactico)val_peek(2).obj, new ArbolSintactico("CuerpoIf",(ArbolSintactico)val_peek(1).obj, null)) ); }
                                                                                                  yyval.ival = val_peek(0).ival;
                                                                                                  sinRet = true;
                                                                                                }
break;
case 60:
//#line 182 "gramatica.y"
{ if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un bloque IF-ELSE");
                                                                                                  if (!error()) { yyval = new ParserVal(new ArbolSintactico("IfElse", (ArbolSintactico)val_peek(4).obj, new ArbolSintactico("CuerpoIfElse",(ArbolSintactico)val_peek(3).obj, (ArbolSintactico)val_peek(1).obj)) );}  yyval.ival = val_peek(0).ival;
                                                                                                  if (enFunc > 0 && !sinRet && nivel > -1) {
                                                                                                      /* El IF-ELSE completo actúa como un return*/
                                                                                                      huboRetStack.pop();
                                                                                                      huboRetStack.push(true);
                                                                                                  }
                                                                                                }
break;
case 61:
//#line 190 "gramatica.y"
{ enDo=false;
                                                                                                  if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un bloque DO UNTIL");
                                                                                                  if (!error()) { yyval = new ParserVal(new ArbolSintactico("DoUntil", (ArbolSintactico)val_peek(0).obj, (ArbolSintactico)val_peek(2).obj));  }  yyval.ival = val_peek(0).ival;
                                                                                                }
break;
case 62:
//#line 195 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta la palabra ENDIF al final del bloque IF-ELSE"); yyval.ival = val_peek(0).ival;}
break;
case 63:
//#line 196 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta la palabra ENDIF al final del bloque IF"); yyval.ival = val_peek(0).ival;}
break;
case 64:
//#line 197 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta el cuerpo en el bloque ELSE"); yyval.ival = val_peek(0).ival;}
break;
case 65:
//#line 198 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta el cuerpo en el bloque IF"); yyval.ival = val_peek(0).ival;}
break;
case 66:
//#line 199 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta el cuerpo en el bloque IF"); yyval.ival = val_peek(0).ival;}
break;
case 67:
//#line 200 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta el cuerpo en los bloques IF y ELSE"); yyval.ival = val_peek(0).ival;}
break;
case 68:
//#line 202 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(2).ival + ": falta de cuerpo en iteracion"); yyval.ival = val_peek(2).ival;}
break;
case 69:
//#line 203 "gramatica.y"
{ enDo=false;
                                                                                                  erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta la palabra UNTIL en la iteracion"); yyval.ival = val_peek(0).ival;
                                                                                                }
break;
case 70:
//#line 209 "gramatica.y"
{ if (!error()) { yyval = new ParserVal (new ArbolSintactico("Print", (ArbolSintactico)val_peek(1).obj, null)); } yyval.ival = val_peek(0).ival; }
break;
case 71:
//#line 210 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta de argumento en sentencia print");  yyval.ival = val_peek(0).ival; }
break;
case 72:
//#line 211 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el paréntesis de apertura en sentencia print");  yyval.ival = val_peek(0).ival; }
break;
case 73:
//#line 212 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el paréntesis de cierre en sentencia print");  yyval.ival = val_peek(1).ival; }
break;
case 74:
//#line 213 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": faltan ambos paréntesis en sentencia print");  yyval.ival = val_peek(0).ival; }
break;
case 75:
//#line 216 "gramatica.y"
{ yyval = val_peek(0); }
break;
case 76:
//#line 217 "gramatica.y"
{ yyval = new ParserVal(new ArbolSintactico((Simbolo)val_peek(0).obj)); }
break;
case 77:
//#line 219 "gramatica.y"
{ yyval = val_peek(1); yyval.ival = val_peek(0).ival; }
break;
case 78:
//#line 220 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta el paréntesis de apertura de la condición");}
break;
case 79:
//#line 221 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta el paréntesis de cierre de la condición");}
break;
case 80:
//#line 222 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": faltan ambos paréntesis de la condición");}
break;
case 81:
//#line 225 "gramatica.y"
{ if (!error()) { yyval = new ParserVal (new ArbolSintactico(val_peek(1).sval, (ArbolSintactico)val_peek(2).obj, (ArbolSintactico)val_peek(0).obj)); } }
break;
case 82:
//#line 226 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": La condición es incompleta");}
break;
case 83:
//#line 229 "gramatica.y"
{ yyval = new ParserVal("Mayor"); }
break;
case 84:
//#line 230 "gramatica.y"
{ yyval = new ParserVal("Menor"); }
break;
case 85:
//#line 231 "gramatica.y"
{ yyval = new ParserVal("MayorIgual"); }
break;
case 86:
//#line 232 "gramatica.y"
{ yyval = new ParserVal("MenorIgual"); }
break;
case 87:
//#line 233 "gramatica.y"
{ yyval = new ParserVal("Igual"); }
break;
case 88:
//#line 234 "gramatica.y"
{ yyval = new ParserVal("Distinto"); }
break;
case 89:
//#line 237 "gramatica.y"
{ yyval = new ParserVal("Long"); }
break;
case 90:
//#line 238 "gramatica.y"
{ yyval = new ParserVal("Lambda");
                                                      if (!enDeclaracionParam) {
                                                        erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": el tipo 'Lambda' solo puede utilizarse en la declaración de parámetros formales de funciones estándar");
                                                      }
                                                    }
break;
case 91:
//#line 245 "gramatica.y"
{renombrarVariable(val_peek(0));}
break;
case 92:
//#line 246 "gramatica.y"
{renombrarVariable(val_peek(0));}
break;
case 93:
//#line 249 "gramatica.y"
{ Simbolo simboloActual = checkearAlcance(val_peek(0).sval, val_peek(0).ival);
                                                                        if (simboloActual != null && (!(simboloActual).getCategoria().equals("Identificador") || simboloActual.getTipo().equals("Lambda"))) {
                                                                          erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + val_peek(0).ival + ": El identificador '" + val_peek(0).sval + "' no es una variable");
                                                                        }
                                                                        yyval = val_peek(2);
                                                                        ((ArrayList<Simbolo>)yyval.obj).add(simboloActual);  
                                                                      }
break;
case 94:
//#line 256 "gramatica.y"
{ Simbolo simboloActual = checkearAlcance(val_peek(0).sval, val_peek(0).ival);
                                                                        if (simboloActual != null && (!(simboloActual).getCategoria().equals("Identificador") || simboloActual.getTipo().equals("Lambda"))) {
                                                                          erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + val_peek(0).ival + ": El identificador '" + val_peek(0).sval + "' no es una variable");
                                                                        }
                                                                        yyval = new ParserVal( new ArrayList<Simbolo>() );
                                                                        ((ArrayList<Simbolo>)yyval.obj).add(simboloActual);
                                                                      }
break;
case 95:
//#line 265 "gramatica.y"
{ yyval = new ParserVal( ((SimboloLong)val_peek(0).obj).getLexema() );
                                                                        yyval.ival = val_peek(0).ival; 
                                                                      }
break;
case 96:
//#line 268 "gramatica.y"
{ yyval = new ParserVal( val_peek(2).sval + "." + ((SimboloLong)val_peek(0).obj).getLexema() );
                                                                        yyval.ival = val_peek(0).ival;
                                                                      }
break;
case 97:
//#line 274 "gramatica.y"
{ enParamInvocacion = false;
                                                                                                                if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó la invocación de una función");
                                                                                                                if (!error() && val_peek(2).obj != null) {
                                                                                                                  ((ArbolSintactico)val_peek(2).obj).setUltimoDerecho((ArbolSintactico)val_peek(1).obj); 
                                                                                                                  yyval = val_peek(2);
                                                                                                                }
                                                                                                                yyval.ival = val_peek(0).ival;
                                                                                                              }
break;
case 98:
//#line 282 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": faltan los argumentos en la invocación de función"); 
                                                                                                                enParamInvocacion = false;
                                                                                                                yyval.ival = val_peek(0).ival; 
                                                                                                              }
break;
case 99:
//#line 288 "gramatica.y"
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
case 100:
//#line 301 "gramatica.y"
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
case 101:
//#line 315 "gramatica.y"
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
case 102:
//#line 330 "gramatica.y"
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
case 103:
//#line 344 "gramatica.y"
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
case 104:
//#line 360 "gramatica.y"
{ Simbolo simboloActual = checkearAlcance(val_peek(0).sval, val_peek(0).ival);
                                                                                                  yyval = new ParserVal(simboloActual);
                                                                                                  if (simboloActual != null && (!(simboloActual.getCategoria().equals("Identificador")) || simboloActual.getTipo().equals("Lambda"))) {
                                                                                                    erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + val_peek(0).ival + ": al usar un identificador como argumento en la invocación de una función Lambda, este debe ser una variable");
                                                                                                  }
                                                                                                }
break;
case 105:
//#line 368 "gramatica.y"
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
case 106:
//#line 379 "gramatica.y"
{ if (!error()) {
                                                                                yyval = new ParserVal(new ArbolSintactico("ListaParametros", (ArbolSintactico)((ArbolSintactico[])val_peek(0).obj)[0], null) ); 
                                                                                for (int i = 1; i < ((ArbolSintactico[])val_peek(0).obj).length; i++) {
                                                                                  ((ArbolSintactico)yyval.obj).setUltimoDerecho( new ArbolSintactico("ListaParametros", ((ArbolSintactico)((ArbolSintactico[])val_peek(0).obj)[i]), null) );
                                                                                }
                                                                              }
                                                                              if (contadorParametrosActual != ((SimboloFuncion)funcionInvocadaActual).getCantidadParametros()) {
                                                                                erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + val_peek(0).ival + ": La cantidad de parámetros en la invocación de la función '" + funcionInvocadaActual.getLexema() + "' no coincide con la cantidad de parámetros formales declarados");
                                                                              }
                                                                              contadorParametrosActual = 0;
                                                                              yyval.ival = val_peek(0).ival;
                                                                            }
break;
case 107:
//#line 393 "gramatica.y"
{ yyval = val_peek(2);
                                                                              int posParametro = ((SimboloFuncion)funcionInvocadaActual).getPosParametro(val_peek(0).sval);
                                                                              if (posParametro != -1) {
                                                                                ((ArbolSintactico[])yyval.obj)[((SimboloFuncion)funcionInvocadaActual).getPosParametro(val_peek(0).sval)] = (ArbolSintactico)val_peek(0).obj; 
                                                                              }
                                                                              contadorParametrosActual++;
                                                                              yyval.ival = val_peek(0).ival;
                                                                            }
break;
case 108:
//#line 401 "gramatica.y"
{ yyval = new ParserVal(new ArbolSintactico[((SimboloFuncion)funcionInvocadaActual).getCantidadParametros()]);
                                                                              int posParametro = ((SimboloFuncion)funcionInvocadaActual).getPosParametro(val_peek(0).sval);
                                                                              if (posParametro != -1) {
                                                                                ((ArbolSintactico[])yyval.obj)[((SimboloFuncion)funcionInvocadaActual).getPosParametro(val_peek(0).sval)] = (ArbolSintactico)val_peek(0).obj;
                                                                              }
                                                                              contadorParametrosActual++;
                                                                              yyval.ival = val_peek(0).ival;
                                                                            }
break;
case 109:
//#line 411 "gramatica.y"
{ checkeoParametroDeInvocacion(((Simbolo)val_peek(0).obj).getLexema(), val_peek(0).ival, (ArbolSintactico)val_peek(2).obj);
                                                                                                              if (!error()) {
                                                                                                                yyval = new ParserVal( (ArbolSintactico)val_peek(2).obj );
                                                                                                                yyval.sval = ((Simbolo)val_peek(0).obj).getLexema();
                                                                                                              }
                                                                                                              yyval.ival = val_peek(0).ival;
                                                                                                            }
break;
case 110:
//#line 418 "gramatica.y"
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
case 111:
//#line 428 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta llave de cierre del cuerpo de la función Lambda"); enDeclaracionLambda = false; yyval.ival = val_peek(1).ival;}
break;
case 112:
//#line 429 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(2).ival + ": falta llave de apertura del cuerpo de la función Lambda"); enDeclaracionLambda = false; yyval.ival = val_peek(2).ival;}
break;
case 113:
//#line 430 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": faltan ambas llaves del cuerpo de la función Lambda"); enDeclaracionLambda = false; yyval.ival = val_peek(1).ival;}
break;
case 114:
//#line 431 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": No se especifica el parametro formal al que corresponde el parámetro real"); enDeclaracionLambda = false; yyval.ival = val_peek(0).ival;}
break;
case 115:
//#line 432 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": No se especifica el parametro formal al que corresponde el parámetro real"); yyval.ival = val_peek(0).ival;}
break;
case 116:
//#line 435 "gramatica.y"
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
case 117:
//#line 447 "gramatica.y"
{ enDeclaracionLambda = true;
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
case 118:
//#line 460 "gramatica.y"
{ enDeclaracionLambda = true;
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
case 119:
//#line 475 "gramatica.y"
{ if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(1).ival + ": Se detectó una expresión desde una suma");
                                                                      if (!error()) {
                                                                        yyval = new ParserVal(new ArbolSintactico("Suma", (ArbolSintactico)val_peek(2).obj, (ArbolSintactico)val_peek(0).obj));
                                                                      }
                                                                      yyval.ival = val_peek(1).ival;
                                                                    }
break;
case 120:
//#line 481 "gramatica.y"
{ if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(1).ival + ": Se detectó una expresión desde una resta");
                                                                      if (!error()) { yyval = new ParserVal(new ArbolSintactico("Resta", (ArbolSintactico)val_peek(2).obj, (ArbolSintactico)val_peek(0).obj));} yyval.ival = val_peek(1).ival; 
                                                                    }
break;
case 121:
//#line 484 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la izquierda de '+'"); }
break;
case 122:
//#line 485 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la derecha de '+'"); }
break;
case 123:
//#line 486 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la izquierda de '-'"); }
break;
case 124:
//#line 487 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la derecha de '-'"); }
break;
case 125:
//#line 488 "gramatica.y"
{ if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó una expresión desde un término");    
                                                                      yyval = val_peek(0);
                                                                    }
break;
case 126:
//#line 493 "gramatica.y"
{ if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(1).ival + ": Se detectó un término desde una multiplicación");
                                                                      if (!error()) { yyval = new ParserVal(new ArbolSintactico("Multiplicacion", (ArbolSintactico)val_peek(2).obj, (ArbolSintactico)val_peek(0).obj));} yyval.ival = val_peek(1).ival;
                                                                    }
break;
case 127:
//#line 496 "gramatica.y"
{ if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(1).ival + ": Se detectó un término desde una división");
                                                                      if (!error()) { yyval = new ParserVal(new ArbolSintactico("Division", (ArbolSintactico)val_peek(2).obj, (ArbolSintactico)val_peek(0).obj)); } yyval.ival = val_peek(1).ival; 
                                                                    }
break;
case 128:
//#line 499 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la izquierda de '*'"); }
break;
case 129:
//#line 500 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la derecha de '*'"); }
break;
case 130:
//#line 501 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la izquierda de '/'"); }
break;
case 131:
//#line 502 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la derecha de '/'"); }
break;
case 132:
//#line 503 "gramatica.y"
{ if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un término desde un factor");
                                                                      yyval = val_peek(0); 
                                                                    }
break;
case 133:
//#line 507 "gramatica.y"
{ constantePositiva(val_peek(0)); restriccionCteFloat(val_peek(0)); erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta operador/comparador entre término y constante"); }
break;
case 134:
//#line 510 "gramatica.y"
{ constantePositiva(val_peek(0)); if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un factor desde una constante");
                                                                      restriccionCteFloat(val_peek(0));
                                                                      if (!error()) { yyval = new ParserVal(new ArbolSintactico((Simbolo)val_peek(0).obj));} yyval.ival = val_peek(0).ival;
                                                                    }
break;
case 135:
//#line 514 "gramatica.y"
{ constanteNegativa(val_peek(0)); if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un factor desde una constante negativa");
                                                                      restriccionCteFloat(val_peek(0));
                                                                      if (!error()) { yyval = new ParserVal(new ArbolSintactico((Simbolo)val_peek(0).obj));} yyval.ival = val_peek(0).ival;
                                                                    }
break;
case 136:
//#line 518 "gramatica.y"
{ if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un factor desde un identificador");
                                                                      Simbolo simboloActual = checkearAlcance(val_peek(0).sval, val_peek(0).ival);
                                                                      if (!error()) {
                                                                        yyval = new ParserVal(new ArbolSintactico(simboloActual));
                                                                        yyval.ival = val_peek(0).ival;
                                                                      }
                                                                      if ( simboloActual != null && (!simboloActual.getCategoria().equals("Identificador") || simboloActual.getTipo().equals("Lambda"))) {
                                                                        erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + val_peek(0).ival + ": el identificador '" + val_peek(0).sval + "' no es una variable");
                                                                      }
                                                                    }
break;
case 137:
//#line 528 "gramatica.y"
{ if (verbose) System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un factor desde una función");
                                                                      yyval = val_peek(0);
                                                                    }
break;
case 138:
//#line 531 "gramatica.y"
{ erroresSemanticos.add("ERROR SEMÁNTICO EN LÍNEA " + val_peek(0).ival + ": Las funciones Lambda no devuelven valor y no pueden usarse en expresiones"); }
break;
case 139:
//#line 532 "gramatica.y"
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
case 140:
//#line 543 "gramatica.y"
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
case 141:
//#line 556 "gramatica.y"
{ constantePositiva(val_peek(0)); restriccionCteFloat(val_peek(0)); yyval = val_peek(2); ((ArrayList<Simbolo>)yyval.obj).add((Simbolo)val_peek(0).obj); yyval.ival = val_peek(0).ival; }
break;
case 142:
//#line 557 "gramatica.y"
{ constanteNegativa(val_peek(0)); restriccionCteFloat(val_peek(0)); yyval = val_peek(3); ((ArrayList<Simbolo>)yyval.obj).add((Simbolo)val_peek(0).obj); yyval.ival = val_peek(0).ival; }
break;
case 143:
//#line 558 "gramatica.y"
{ constantePositiva(val_peek(0)); restriccionCteFloat(val_peek(0)); yyval = new ParserVal(new ArrayList<Simbolo>()); ((ArrayList<Simbolo>)yyval.obj).add((Simbolo)val_peek(0).obj); yyval.ival = val_peek(0).ival; }
break;
case 144:
//#line 559 "gramatica.y"
{ constanteNegativa(val_peek(0)); restriccionCteFloat(val_peek(0)); yyval = new ParserVal(new ArrayList<Simbolo>()); ((ArrayList<Simbolo>)yyval.obj).add((Simbolo)val_peek(0).obj); yyval.ival = val_peek(0).ival; }
break;
case 145:
//#line 560 "gramatica.y"
{ constantePositiva(val_peek(0)); restriccionCteFloat(val_peek(0)); erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta ',' entre constantes"); constantePositiva(val_peek(0)); restriccionCteFloat(val_peek(0)); }
break;
case 146:
//#line 561 "gramatica.y"
{ constanteNegativa(val_peek(0)); restriccionCteFloat(val_peek(0)); erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(2).ival + ": falta ',' entre constantes"); constanteNegativa(val_peek(0)); restriccionCteFloat(val_peek(0)); }
break;
case 147:
//#line 562 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": la variable '" + val_peek(0).sval + "' no puede estar en una lista de constantes");}
break;
case 148:
//#line 565 "gramatica.y"
{ yyval = val_peek(2); ((ArrayList<Simbolo>)yyval.obj).addAll( ((ArrayList<Simbolo>)val_peek(0).obj) ); yyval.ival = val_peek(0).ival; }
break;
//#line 2061 "Parser.java"
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
