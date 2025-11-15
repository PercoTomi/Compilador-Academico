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
    3,    3,    3,    3,    3,    3,    4,   15,    4,   11,
   11,    6,    6,   19,   19,   19,   21,   20,    7,    7,
    7,    7,    7,    7,    7,    7,    7,    7,    7,    9,
    9,    9,   22,   22,   22,   22,   23,   23,   24,   24,
   24,   24,   24,   24,   12,   12,   13,   13,   17,   17,
    8,   10,   10,   10,   25,   26,   26,   27,   27,   27,
   27,   27,   27,   27,   27,   27,   27,   27,   27,   14,
   14,   28,   28,   28,   28,   28,   28,   16,   16,   16,
   16,   16,   16,   16,   29,   29,   29,   29,   29,   29,
   29,   29,   30,   30,   30,   30,   18,   18,   18,   18,
   18,   18,
};
final static short yylen[] = {                            2,
    5,    4,    4,    4,    4,    3,    3,    3,    2,    7,
    2,    1,    2,    2,    1,    1,    3,    2,    2,    1,
    1,    1,    1,    1,    1,    1,    2,    0,    9,    5,
    4,    3,    3,    3,    2,    2,    0,    2,    6,    4,
    4,    5,    3,    5,    5,    3,    4,    3,    3,    4,
    4,    3,    3,    2,    2,    1,    3,    1,    1,    1,
    1,    1,    1,    1,    1,    1,    3,    1,    1,    3,
    4,    4,    4,    5,    1,    3,    1,    3,    6,    5,
    5,    4,    9,    8,    8,    7,    8,    2,    5,    3,
    1,    3,    2,    2,    1,    2,    1,    3,    3,    3,
    3,    3,    3,    1,    3,    3,    3,    3,    3,    3,
    3,    1,    1,    2,    1,    1,    3,    4,    1,    2,
    2,    3,
};
final static short yydefred[] = {                         0,
    0,    0,   65,    0,    0,    0,   66,    0,    0,    0,
   12,    0,    0,   21,   22,   23,   24,   25,   26,    0,
    0,    0,    0,    0,    0,    0,    0,    0,  113,    0,
    0,  116,    0,    0,    0,    0,    0,  112,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    9,    0,   11,
   13,   14,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
   77,    6,    0,    0,    0,    0,    0,    0,    0,  114,
   61,   62,   63,   64,   59,   60,    0,    0,    0,    0,
   46,    0,   54,    0,    0,    0,    0,    0,   48,   36,
    0,    0,   35,   49,    0,   38,    0,   52,    0,    7,
    0,    8,    0,  119,    0,    0,   69,    0,    0,   70,
    0,    2,    4,    0,   73,    0,    0,    0,    0,    0,
   72,   71,    0,    5,    0,    0,    0,  107,  109,   53,
    0,    0,    0,    0,    0,   47,    0,    0,   40,  111,
  108,  105,  110,  106,   31,    0,   18,   34,    0,   41,
   51,   50,    3,   97,    0,    0,    0,   91,  120,  121,
    0,    0,    0,    1,    0,    0,    0,    0,   74,   78,
   76,   45,   44,    0,   30,   17,   96,    0,   93,   28,
    0,  117,    0,  122,    0,    0,    0,   82,    0,    0,
   39,   92,    0,   90,  118,   10,   80,    0,   81,    0,
    0,    0,   79,    0,    0,    0,    0,    0,    0,   86,
    0,   29,   84,    0,   85,   83,
};
final static short yydgoto[] = {                          9,
   10,   11,   12,   13,  102,   14,   15,   32,   17,   18,
   19,   20,   21,  167,  203,   33,   34,  116,   43,   44,
   45,   35,   36,   89,   69,   70,   71,  168,   37,   38,
};
final static short yysindex[] = {                       -66,
  158,  122,    0,  -37,  477,    7,    0,  878,    0,  594,
    0,   21,   31,    0,    0,    0,    0,    0,    0, -198,
   11,  -26,   34,  743,   98,  629,   52,   56,    0,  284,
 -156,    0,  396,   71, -211,   93,   -5,    0,  300,  122,
  856,   83,  122, -125,  679,  107,  643,    0, -123,    0,
    0,    0,  115,  117,   71,   15,  -98,  284,  -92,  111,
 -102,  661,  137,  189, -251,  -75,  -16,   35,  143,  141,
    0,    0,  -91,  383,  383,    4,    4,  101,  156,    0,
    0,    0,    0,    0,    0,    0,  398,  410,  284,  -63,
    0, -122,    0,    4,  421,  444,  146,   60,    0,    0,
  147,  863,    0,    0,  122,    0,  159,    0,   73,    0,
  -73,    0, -201,    0,  -46,   -6,    0,   71,   90,    0,
  878,    0,    0,  -60,    0,  284,  -33,  -43,  241,   27,
    0,    0,  101,    0,   45,   -5,   -5,    0,    0,    0,
   45,   -5,   45,   -5,   90,    0,   23,   39,    0,    0,
    0,    0,    0,    0,    0,  238,    0,    0,  245,    0,
    0,    0,    0,    0, -199,   62,   78,    0,    0,    0,
   19,   64,  758,    0,  -32,   66,   54,  265,    0,    0,
    0,    0,    0,   82,    0,    0,    0,   92,    0,    0,
 -201,    0,   95,    0,   51,  104,   96,    0,  114,  703,
    0,    0,  252,    0,    0,    0,    0,  126,    0, -148,
  378,  878,    0,  817,  129,  113,  789,  148,  123,    0,
  151,    0,    0,  152,    0,    0,
};
final static short yyrindex[] = {                         0,
  -29,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  689,  710,    0,    0,    0,    0,    0,    0,    0,
    0,   17,  -29,    0,    0,    0,    0,  -41,    0,    0,
    0,    0,  237,  -19,  803,  414,    3,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,  130,  563,  215,    0,    0,    0,    0,    0,
    0,    0,  121,    0,    0,    0,    0,  145,    0,  359,
    0,    0,    0,    0,    0,    0,    0,    0,  436,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,  803,
    0,  463,    0,    0,    0,    0,    0,    0,    0,    0,
  834,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,  484,    0,  376,  506,    0,
    0,    0,    0,    0,    0,    0,    0,    0,  166,  106,
    0,    0,    0,    0,    0,   26,   48,    0,    0,    0,
   70,  307,  329,  351,  259,    0,    0,  803,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,  836,    0,
    0,    0,    0,    0,    0,  112,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,  540,    0,    0,    0,  128,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  136,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,  138,    0,    0,
};
final static short yygindex[] = {                         0,
    6,  134,  665,    0, -127,    0,    0,  780,    0,    0,
    0,  -34,  382,    0,    0, 1075,  833,    0,  367,   10,
    0,   -7,  374,    0,    0,    0,  281,  225,   49,  -42,
};
final static int YYTABLESIZE=1208;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         69,
   69,   69,   39,   69,   69,   69,   26,  177,  197,   88,
   88,   87,   87,   47,   69,    3,   69,   69,   69,   59,
   69,  115,  115,  115,    7,  115,   88,  115,   87,   62,
  128,   69,   99,  138,  139,  104,   95,  171,  172,  115,
  115,   96,  115,  104,   92,  104,   46,  104,   31,   90,
   91,  150,  152,  154,   57,  164,    8,  187,   53,  115,
   68,  104,  104,  193,  104,    3,  102,    3,  102,  165,
  102,   56,  211,   25,    7,  131,    7,   68,  166,   51,
   59,   69,  214,   69,  102,  102,   76,  102,  100,   52,
  100,   77,  100,   76,   75,   78,   74,  160,   77,  147,
  156,   80,   88,  115,   87,  115,  100,  100,   23,  100,
  103,    2,  103,  162,  103,   88,   59,   87,  190,    4,
    5,  191,  136,  137,    6,  104,  173,  104,  103,  103,
  188,  103,   88,   93,   87,  142,  144,   65,  148,  149,
   65,  103,   66,   50,  105,   31,   88,  108,  102,   88,
  102,   31,   95,  112,  113,   95,  166,  184,  117,   50,
   57,   30,  113,  113,  120,  113,   31,  113,   94,  121,
  100,   94,  100,   69,  122,   69,   89,  125,   87,   89,
   50,   87,  129,  132,  133,  134,  115,  115,   69,  115,
    1,  115,  103,    2,  103,   50,  140,   25,  146,  161,
    3,    4,    5,  163,  155,  157,    6,  114,  114,    7,
  114,  169,  114,  178,   69,   69,  174,  217,   69,   69,
   69,   69,   69,   69,   69,   69,   69,   69,  126,   69,
   69,   69,   58,   31,   69,   69,  115,  115,  176,  196,
  115,  115,  115,  115,  115,  115,  115,  115,  115,  115,
   94,  170,  115,  115,   69,  130,  115,  115,   68,  104,
   28,   29,  104,  104,  104,  104,  104,  104,  104,  104,
  104,  104,  114,   68,  104,  104,  192,   58,  104,  104,
   24,  179,  102,  180,  182,  102,  102,  102,  102,  102,
  102,  102,  102,  102,  102,   58,  185,  102,  102,   57,
  183,  102,  102,  186,  100,  200,   50,  100,  100,  100,
  100,  100,  100,  100,  100,  100,  100,   57,  189,  100,
  100,  194,  198,  100,  100,  199,  103,  206,   31,  103,
  103,  103,  103,  103,  103,  103,  103,  103,  103,   68,
   97,  103,  103,  201,   31,  103,  103,   99,  202,   99,
   50,   99,  205,   27,   28,   63,   27,   28,   29,   58,
  207,   58,   27,   28,   29,   99,   99,  208,   99,  101,
  209,  101,   64,  101,  212,   64,  113,   27,   28,   29,
  107,   57,  213,   57,  221,  220,   69,  101,  101,   69,
  101,   98,  113,   98,  224,   98,   69,   69,   69,   75,
  115,   54,   69,   79,  223,   69,   69,  225,  226,   98,
   98,  106,   98,  181,   23,  204,  115,    2,    0,   67,
    0,  114,    0,    0,    3,    4,    5,   31,    0,   99,
    6,   99,    0,    7,   67,    0,   67,  114,   88,    0,
   87,    0,   31,    0,   27,   28,   29,    0,    0,    0,
    0,  101,    0,  101,   31,   86,    0,   85,    0,    0,
    0,    0,    0,    0,    0,   31,    0,    0,    0,    0,
    0,   68,   56,   98,   68,   98,    0,    0,    0,    0,
    0,   68,   68,   68,    0,    0,    0,   68,   31,    0,
   68,   68,    0,   58,   55,    0,   58,   58,   58,    0,
   67,    0,  216,   58,   58,   58,    0,    0,   58,   58,
    0,    0,   58,   58,    0,   57,    0,    0,   57,   57,
   57,   43,    0,    0,    0,   57,   57,   57,    0,    0,
   57,   57,    0,    0,   57,   57,   56,    0,   56,   27,
   28,   29,   33,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,   27,   28,   29,   55,    0,
   55,    0,    0,   99,   32,    0,   99,   99,   99,   99,
   99,   99,   99,   99,   99,   99,    0,    0,   99,   99,
    0,    0,   99,   99,    0,  101,    0,   43,  101,  101,
  101,  101,  101,  101,  101,  101,  101,  101,   42,   41,
  101,  101,    0,    0,  101,  101,    0,   98,   33,    0,
   98,   98,   98,   98,   98,   98,   98,   98,   98,   98,
    0,   27,   98,   98,    0,    0,   98,   98,    0,    0,
   32,    0,   67,    0,   23,   67,    0,    2,  135,   28,
   29,    0,   67,   67,   67,    4,    5,    0,   67,  215,
    6,   67,   67,  141,   28,   29,    0,    0,   81,   82,
   83,   84,    0,    0,   42,  143,   28,   29,    0,   42,
   56,    0,    0,   56,   56,   56,  151,   28,   29,    0,
   56,   56,   56,    0,    0,   56,   56,   27,    0,   56,
   56,    0,   55,    0,    0,   55,   55,   55,    0,  153,
   28,   29,   55,   55,   55,  101,    0,   55,   55,   42,
    0,   55,   55,    0,    0,    0,    0,    0,   49,   43,
    0,    0,   43,    0,    0,    0,    0,    0,    0,   43,
   43,   43,    0,   23,   43,   43,    2,    0,   43,   43,
   33,    0,    0,   33,    4,    5,   40,    0,    0,    6,
   33,   33,   33,   73,    0,   33,   33,    0,    0,   33,
   33,    0,   32,    0,    0,   32,  159,  111,    0,    0,
    0,    0,   32,   32,   32,    0,    0,   32,   32,   16,
   16,   32,   32,    0,   16,  124,    0,   16,    0,   16,
    0,    0,    0,    0,    0,    0,   42,    0,    0,   42,
    0,   41,    0,   16,    0,   16,   42,   42,   42,    0,
    0,   42,   42,   15,    0,   42,   42,    0,    0,   27,
   16,    0,   27,    0,   16,  210,   16,    0,    0,   27,
   27,   27,   22,   22,   16,   27,    0,   22,   27,   27,
   22,   16,   22,    0,    0,    0,    0,    0,    0,    0,
   23,    0,   55,    2,    0,    0,   22,   68,   22,    0,
    3,    4,    5,    0,  101,    0,    6,   61,    0,    7,
   48,    0,    0,   22,  101,  159,    0,   22,  159,   22,
    0,   16,  195,    0,    0,   23,    0,    0,    2,  118,
    0,    0,    0,    0,   22,    3,    4,    5,    0,   23,
   16,    6,    2,    0,    7,   72,    0,    0,    0,    3,
    4,    5,    0,  222,    0,    6,    0,   23,    7,  110,
    2,    0,    0,    0,    0,   37,    0,    3,    4,    5,
    0,    0,    0,    6,   22,   23,    7,  123,    2,    0,
    0,  219,    0,    0,    0,   15,    4,    5,   15,    0,
    0,    6,   16,   22,    0,   15,   15,   15,   20,   23,
   19,   15,    2,    0,   15,   15,   16,    0,    0,   16,
    4,    5,    0,    0,    0,    6,   16,   16,   16,   16,
  100,    0,   16,    0,    0,   16,   16,  158,    0,   16,
   16,   16,    0,   16,    0,    0,   16,    0,   60,   23,
    0,    0,    2,    0,    0,   22,    0,    0,    0,    3,
    4,    5,    0,    0,   23,    6,    0,    2,    7,    0,
    0,    0,    0,    0,    3,    4,    5,    0,    0,    0,
    6,    0,   22,    7,    0,    0,    0,    0,    0,    0,
    0,    0,   22,   22,   22,   23,   22,    0,    2,   22,
    0,    0,    0,    0,    0,    3,    4,    5,    0,   37,
    0,    6,   37,    0,    7,    0,    0,    0,    0,    0,
   37,   37,    0,   23,    0,   37,    2,    0,    0,    0,
    0,    0,    0,    0,    4,    5,    0,    0,  218,    6,
   20,    0,   19,   20,    0,   19,    0,    0,    0,   67,
    0,   20,   20,   19,   19,   20,   20,   19,   19,    0,
    0,    0,   23,   98,    0,    2,    0,    0,    0,   23,
  109,    0,    2,    4,    5,    0,    0,    0,    6,    0,
    4,    5,  119,    0,   23,    6,    0,    2,  127,    0,
    0,    0,    0,    0,    3,    4,    5,    0,    0,    0,
    6,    0,   67,    7,    0,    0,    0,    0,    0,    0,
    0,    0,    0,  145,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
  175,    0,    0,    0,    0,    0,    0,   67,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         41,
   42,   43,   40,   45,   46,   47,    1,   41,   41,   43,
   43,   45,   45,    8,   44,  267,   46,   59,   60,   46,
   62,   41,   42,   43,  276,   45,   43,   47,   45,   24,
   65,   61,   40,   76,   77,   43,   42,   44,   45,   59,
   60,   47,   62,   41,   35,   43,   40,   45,   45,  261,
  262,   94,   95,   96,   44,  257,  123,  257,  257,   45,
   44,   59,   60,   45,   62,  267,   41,  267,   43,  271,
   45,   61,  200,   40,  276,   41,  276,   61,  113,   59,
   46,  123,  210,  125,   59,   60,   42,   62,   41,   59,
   43,   47,   45,   42,   43,   40,   45,  105,   47,   90,
   41,  258,   43,  123,   45,  125,   59,   60,  257,   62,
   41,  260,   43,   41,   45,   43,   46,   45,   41,  268,
  269,   44,   74,   75,  273,  123,  121,  125,   59,   60,
  165,   62,   43,   41,   45,   87,   88,   40,  261,  262,
   40,   59,   45,   10,  270,   45,   41,   41,  123,   44,
  125,   45,   41,  277,   40,   44,  191,  148,  257,   26,
   44,   40,   42,   43,  257,   45,   45,   47,   41,   59,
  123,   44,  125,   44,  277,   46,   41,   41,   41,   44,
   47,   44,  258,   41,   44,  277,   42,   43,   59,   45,
  257,   47,  123,  260,  125,   62,   41,   40,  262,   41,
  267,  268,  269,  277,   59,   59,  273,   42,   43,  276,
   45,  258,   47,  257,  256,  257,  277,  212,  260,  261,
  262,  263,  264,  265,  266,  267,  268,  269,   40,  259,
  272,  273,  259,   45,  276,  277,  256,  257,  272,  272,
  260,  261,  262,  263,  264,  265,  266,  267,  268,  269,
  256,  258,  272,  273,  125,  272,  276,  277,   44,  257,
  257,  258,  260,  261,  262,  263,  264,  265,  266,  267,
  268,  269,  258,   59,  272,  273,  258,   41,  276,  277,
  123,   41,  257,  257,  262,  260,  261,  262,  263,  264,
  265,  266,  267,  268,  269,   59,   59,  272,  273,   41,
  262,  276,  277,   59,  257,   41,  173,  260,  261,  262,
  263,  264,  265,  266,  267,  268,  269,   59,  257,  272,
  273,  258,  257,  276,  277,  272,  257,  277,   45,  260,
  261,  262,  263,  264,  265,  266,  267,  268,  269,  125,
   41,  272,  273,  262,   45,  276,  277,   41,  257,   43,
  217,   45,  258,  256,  257,  258,  256,  257,  258,  123,
  257,  125,  256,  257,  258,   59,   60,  272,   62,   41,
  257,   43,  275,   45,  123,  275,  256,  256,  257,  258,
  274,  123,  257,  125,  272,  257,  257,   59,   60,  260,
   62,   41,  272,   43,  272,   45,  267,  268,  269,   41,
  256,   20,  273,   30,  257,  276,  277,  257,  257,   59,
   60,   45,   62,  133,  257,  191,  272,  260,   -1,   44,
   -1,  256,   -1,   -1,  267,  268,  269,   45,   -1,  123,
  273,  125,   -1,  276,   59,   -1,   61,  272,   43,   -1,
   45,   -1,   45,   -1,  256,  257,  258,   -1,   -1,   -1,
   -1,  123,   -1,  125,   45,   60,   -1,   62,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   45,   -1,   -1,   -1,   -1,
   -1,  257,   59,  123,  260,  125,   -1,   -1,   -1,   -1,
   -1,  267,  268,  269,   -1,   -1,   -1,  273,   45,   -1,
  276,  277,   -1,  257,   59,   -1,  260,  261,  262,   -1,
  125,   -1,  125,  267,  268,  269,   -1,   -1,  272,  273,
   -1,   -1,  276,  277,   -1,  257,   -1,   -1,  260,  261,
  262,   59,   -1,   -1,   -1,  267,  268,  269,   -1,   -1,
  272,  273,   -1,   -1,  276,  277,  123,   -1,  125,  256,
  257,  258,   59,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,  256,  257,  258,  123,   -1,
  125,   -1,   -1,  257,   59,   -1,  260,  261,  262,  263,
  264,  265,  266,  267,  268,  269,   -1,   -1,  272,  273,
   -1,   -1,  276,  277,   -1,  257,   -1,  125,  260,  261,
  262,  263,  264,  265,  266,  267,  268,  269,   59,  123,
  272,  273,   -1,   -1,  276,  277,   -1,  257,  125,   -1,
  260,  261,  262,  263,  264,  265,  266,  267,  268,  269,
   -1,   59,  272,  273,   -1,   -1,  276,  277,   -1,   -1,
  125,   -1,  257,   -1,  257,  260,   -1,  260,  256,  257,
  258,   -1,  267,  268,  269,  268,  269,   -1,  273,  272,
  273,  276,  277,  256,  257,  258,   -1,   -1,  263,  264,
  265,  266,   -1,   -1,  125,  256,  257,  258,   -1,    5,
  257,   -1,   -1,  260,  261,  262,  256,  257,  258,   -1,
  267,  268,  269,   -1,   -1,  272,  273,  125,   -1,  276,
  277,   -1,  257,   -1,   -1,  260,  261,  262,   -1,  256,
  257,  258,  267,  268,  269,   41,   -1,  272,  273,   45,
   -1,  276,  277,   -1,   -1,   -1,   -1,   -1,  125,  257,
   -1,   -1,  260,   -1,   -1,   -1,   -1,   -1,   -1,  267,
  268,  269,   -1,  257,  272,  273,  260,   -1,  276,  277,
  257,   -1,   -1,  260,  268,  269,  270,   -1,   -1,  273,
  267,  268,  269,  125,   -1,  272,  273,   -1,   -1,  276,
  277,   -1,  257,   -1,   -1,  260,  102,  125,   -1,   -1,
   -1,   -1,  267,  268,  269,   -1,   -1,  272,  273,    0,
    1,  276,  277,   -1,    5,  125,   -1,    8,   -1,   10,
   -1,   -1,   -1,   -1,   -1,   -1,  257,   -1,   -1,  260,
   -1,  123,   -1,   24,   -1,   26,  267,  268,  269,   -1,
   -1,  272,  273,  125,   -1,  276,  277,   -1,   -1,  257,
   41,   -1,  260,   -1,   45,  123,   47,   -1,   -1,  267,
  268,  269,    0,    1,  125,  273,   -1,    5,  276,  277,
    8,   62,   10,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
  257,   -1,   20,  260,   -1,   -1,   24,   25,   26,   -1,
  267,  268,  269,   -1,  200,   -1,  273,  125,   -1,  276,
  277,   -1,   -1,   41,  210,  211,   -1,   45,  214,   47,
   -1,  102,  125,   -1,   -1,  257,   -1,   -1,  260,   57,
   -1,   -1,   -1,   -1,   62,  267,  268,  269,   -1,  257,
  121,  273,  260,   -1,  276,  277,   -1,   -1,   -1,  267,
  268,  269,   -1,  125,   -1,  273,   -1,  257,  276,  277,
  260,   -1,   -1,   -1,   -1,  123,   -1,  267,  268,  269,
   -1,   -1,   -1,  273,  102,  257,  276,  277,  260,   -1,
   -1,  125,   -1,   -1,   -1,  257,  268,  269,  260,   -1,
   -1,  273,  173,  121,   -1,  267,  268,  269,  125,  257,
  125,  273,  260,   -1,  276,  277,  257,   -1,   -1,  260,
  268,  269,   -1,   -1,   -1,  273,  267,  268,  269,  200,
  125,   -1,  273,   -1,   -1,  276,  277,  125,   -1,  210,
  211,  212,   -1,  214,   -1,   -1,  217,   -1,  256,  257,
   -1,   -1,  260,   -1,   -1,  173,   -1,   -1,   -1,  267,
  268,  269,   -1,   -1,  257,  273,   -1,  260,  276,   -1,
   -1,   -1,   -1,   -1,  267,  268,  269,   -1,   -1,   -1,
  273,   -1,  200,  276,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,  210,  211,  212,  257,  214,   -1,  260,  217,
   -1,   -1,   -1,   -1,   -1,  267,  268,  269,   -1,  257,
   -1,  273,  260,   -1,  276,   -1,   -1,   -1,   -1,   -1,
  268,  269,   -1,  257,   -1,  273,  260,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,  268,  269,   -1,   -1,  272,  273,
  257,   -1,  257,  260,   -1,  260,   -1,   -1,   -1,   25,
   -1,  268,  269,  268,  269,  272,  273,  272,  273,   -1,
   -1,   -1,  257,   39,   -1,  260,   -1,   -1,   -1,  257,
   46,   -1,  260,  268,  269,   -1,   -1,   -1,  273,   -1,
  268,  269,   58,   -1,  257,  273,   -1,  260,   64,   -1,
   -1,   -1,   -1,   -1,  267,  268,  269,   -1,   -1,   -1,
  273,   -1,   78,  276,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   89,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
  126,   -1,   -1,   -1,   -1,   -1,   -1,  133,
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
"sentenciaejecutable : sentencia_return",
"declaracion : tipo listavariables",
"$$1 :",
"declaracion : tipo ID '(' listaparametros ')' $$1 '{' listasentencias '}'",
"sentencia_return : RETURN '(' expresion ')' ';'",
"sentencia_return : RETURN '(' ')' ';'",
"asignacion : identificador ASIG expresion",
"asignacion : listavariables '=' listaconstantes",
"estructura_bloque_control : '{' listasentenciasejecutables '}'",
"estructura_bloque_control : sentenciaejecutable ';'",
"estructura_bloque_control : '{' '}'",
"$$2 :",
"bloque_control_if : $$2 estructura_bloque_control",
"control : IF condicion bloque_control_if ELSE bloque_control_if ENDIF",
"control : IF condicion bloque_control_if ENDIF",
"control : DO bloque_control_if UNTIL condicion",
"control : IF condicion bloque_control_if ELSE bloque_control_if",
"control : IF condicion bloque_control_if",
"control : IF condicion bloque_control_if ELSE ENDIF",
"control : IF condicion ELSE bloque_control_if ENDIF",
"control : IF condicion ENDIF",
"control : IF condicion ELSE ENDIF",
"control : DO UNTIL condicion",
"control : DO estructura_bloque_control condicion",
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

//#line 203 "gramatica.y"

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

private void chequeoSentenciaReturn(ParserVal yyval) {
  if (enFunc == 0) {
    erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + yyval.ival + ": sentencia return fuera de una función");
  } else if (nivel > -1) {
    returnsIf--;
    huboRetStack.pop();
    huboRetStack.push(true);
  } else {
    returnsFunc --;
  }
}

private void inicioIf() {
  if(enFunc > 0){
    huboRetStack.push(false);
    nivel++;
    returnsIf++;    
  }
}

private void finIf(ParserVal yyval) {
  if (enFunc > 0) {
    
    //Detección de multiples retornos
    if (returnsIf < nivel) {
      erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + yyval.ival + ": Se han detectado multiples sentencias de retorno en el IF");
      returnsIf = nivel + 1; // Reseteo de returnsIf para evitar propagación erronea de errores
    }
    
    // Detección de ausencia de retorno
    if (!huboRetStack.pop()) {
      erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + yyval.ival + ": Se ha detectado ausencia de retorno");
      sinRet = true;
      returnsIf--;
    } else if (nivel==returnsIf) { // Ausencia de retorno válida por cobertura de nivel anterior
      sinRet = false;
    }

    nivel--;
  }
}

private void finFunc(ParserVal yyval) {
  if (returnsFunc > 0 && sinRet) {
    erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + yyval.ival + ": La función no tiene sentencia return en todos sus caminos de ejecución");
  } else if (returnsFunc < 0) {
    erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + yyval.ival + ": Se han detectado multiples sentencias de retorno en la función");
  }
  enFunc--;
  returnFunc = 1;
}
//#line 738 "Parser.java"
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
{if(enFunc==0) erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": Invocacion de funcion lambda fuera de una función");}
break;
case 26:
//#line 52 "gramatica.y"
{chequeoSentenciaReturn(val_peek(0));}
break;
case 27:
//#line 55 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó una declaración de variables");}
break;
case 28:
//#line 56 "gramatica.y"
{enFunc++;}
break;
case 29:
//#line 56 "gramatica.y"
{finFunc(val_peek(1)); System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(1).ival + ": Se detectó una declaración de función");}
break;
case 30:
//#line 59 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(2).ival + ": Se detectó una sentencia return");}
break;
case 31:
//#line 60 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta de argumento en sentencia return");}
break;
case 32:
//#line 63 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó una asignación simple");}
break;
case 33:
//#line 64 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó una asignación múltiple");}
break;
case 37:
//#line 72 "gramatica.y"
{inicioIf()}
break;
case 38:
//#line 72 "gramatica.y"
{finIf(val_peek(1))}
break;
case 39:
//#line 74 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un bloque IF-ELSE");}
break;
case 40:
//#line 75 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un bloque IF");}
break;
case 41:
//#line 76 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó un bloque DO UNTIL");}
break;
case 42:
//#line 78 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta la palabra ENDIF al final del bloque IF-ELSE");}
break;
case 43:
//#line 79 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta la palabra ENDIF al final del bloque IF");}
break;
case 44:
//#line 80 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el cuerpo en el bloque ELSE");}
break;
case 45:
//#line 81 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(3).ival + ": falta el cuerpo en el bloque IF");}
break;
case 46:
//#line 82 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el cuerpo en el bloque IF");}
break;
case 47:
//#line 83 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el cuerpo en los bloques IF y ELSE");}
break;
case 48:
//#line 85 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(2).ival + ": falta de cuerpo en iteracion");}
break;
case 49:
//#line 86 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta la palabra UNTIL en la iteracion");}
break;
case 52:
//#line 93 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta de argumento en sentencia print");}
break;
case 54:
//#line 97 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta el paréntesis de apertura de la condición");}
break;
case 55:
//#line 98 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta el paréntesis de cierre de la condición");}
break;
case 56:
//#line 99 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": faltan ambos paréntesis de la condición");}
break;
case 58:
//#line 103 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": La condición es incompleta");}
break;
case 71:
//#line 127 "gramatica.y"
{System.out.println("||================ ANALIZADOR SINTÁCTICO: Linea " + val_peek(0).ival + ": Se detectó una función invocada correctamente");}
break;
case 73:
//#line 131 "gramatica.y"
{ constantePositiva(val_peek(1)); }
break;
case 74:
//#line 132 "gramatica.y"
{ constanteNegativa(val_peek(1)); }
break;
case 80:
//#line 144 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(2).ival + ": falta el paréntesis de cierre de la función TRUNC");}
break;
case 81:
//#line 145 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(4).ival + ": falta el paréntesis de apertura de la función TRUNC");}
break;
case 82:
//#line 146 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(2).ival + ": faltan ambos paréntesis de la función TRUNC");}
break;
case 84:
//#line 148 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(2).ival + ": falta llave de cierre del cuerpo de la función Lambda");}
break;
case 85:
//#line 149 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(4).ival + ": falta llave de apertura del cuerpo de la función Lambda");}
break;
case 86:
//#line 150 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(3).ival + ": faltan ambas llaves del cuerpo de la función Lambda");}
break;
case 87:
//#line 151 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": No se especifica el parametro formal al que corresponde el parámetro real");}
break;
case 88:
//#line 152 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": No se especifica el parametro formal al que corresponde el parámetro real");}
break;
case 89:
//#line 153 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": No se especifica el parametro formal al que corresponde el parámetro real");}
break;
case 94:
//#line 162 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el nombre del parámetro");}
break;
case 95:
//#line 163 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta el nombre del parámetro");}
break;
case 96:
//#line 164 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta el tipo del parámetro");}
break;
case 97:
//#line 165 "gramatica.y"
{erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(0).ival + ": falta el tipo del parámetro");}
break;
case 100:
//#line 170 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la izquierda de '+'"); }
break;
case 101:
//#line 171 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la derecha de '+'"); }
break;
case 102:
//#line 172 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la izquierda de '-'"); }
break;
case 103:
//#line 173 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la derecha de '-'"); }
break;
case 107:
//#line 179 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la izquierda de '*'"); }
break;
case 108:
//#line 180 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la derecha de '*'"); }
break;
case 109:
//#line 181 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la izquierda de '/'"); }
break;
case 110:
//#line 182 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta operando a la derecha de '/'"); }
break;
case 111:
//#line 183 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(2).ival + ": falta operador entre operandos o falta de comparador en condición"); }
break;
case 113:
//#line 187 "gramatica.y"
{ constantePositiva(val_peek(0)); }
break;
case 114:
//#line 188 "gramatica.y"
{ constanteNegativa(val_peek(0)); }
break;
case 117:
//#line 193 "gramatica.y"
{ constantePositiva(val_peek(0)); }
break;
case 118:
//#line 194 "gramatica.y"
{ constanteNegativa(val_peek(0)); }
break;
case 119:
//#line 195 "gramatica.y"
{ constantePositiva(val_peek(0)); }
break;
case 120:
//#line 196 "gramatica.y"
{ constanteNegativa(val_peek(0)); }
break;
case 121:
//#line 197 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(1).ival + ": falta ',' entre constantes"); constantePositiva(val_peek(0)); }
break;
case 122:
//#line 198 "gramatica.y"
{ erroresSintacticos.add("||================ ANALIZADOR SINTÁCTICO: Error sintáctico en línea " + val_peek(2).ival + ": falta ',' entre constantes"); constanteNegativa(val_peek(0)); }
break;
//#line 1179 "Parser.java"
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
