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






//#line 2 "gramatica2.y"
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
public final static short YYERRCODE=256;
final static short yylhs[] = {                           -1,
    0,    0,    0,    0,    0,    1,    1,    1,    1,    2,
    2,    2,    2,    2,    4,    4,    3,    3,   13,   13,
   13,   14,   14,   14,    5,    5,    5,    5,    5,    5,
    5,    5,    5,    5,    5,    5,    5,    5,    5,    5,
    5,    5,    5,    5,    6,    6,    6,    7,    7,    7,
    7,    7,    7,   15,   17,   17,   17,   17,   17,   17,
    8,    8,   12,   12,    9,    9,   10,   10,   18,   18,
   11,   11,   11,   11,   11,   11,   11,   19,   19,   19,
   20,   20,   20,   16,   16,   21,   21,   21,
};
final static short yylen[] = {                            2,
    4,    3,    3,    3,    2,    3,    2,    2,    1,    1,
    1,    1,    1,    1,    2,   13,    3,    3,    3,    1,
    2,    3,    1,    2,    8,    6,    6,    7,    7,    6,
    5,    5,    4,    8,    6,    7,    7,    5,    6,    5,
    5,    4,    5,    6,    4,    4,    3,    4,    4,    4,
    4,    4,    4,    3,    1,    1,    1,    1,    1,    1,
    1,    1,    1,    3,    3,    1,    3,    1,    3,    2,
    3,    3,    1,    3,    3,    3,    3,    3,    3,    1,
    1,    1,    1,    3,    1,    3,    6,    7,
};
final static short yydefred[] = {                         0,
    0,    0,    0,    0,    0,    0,   61,    0,    0,   62,
    0,    0,    0,   10,   11,   12,   13,   14,    0,    0,
    0,    0,    0,    0,    0,   82,    0,   83,    0,    0,
    0,    0,   80,    0,    0,   23,    0,    0,    0,    4,
    0,    7,    0,    0,    0,    0,    0,    0,    0,    2,
    0,    0,    0,    0,    0,    0,   85,    0,    0,    0,
    0,    0,    0,   57,   58,   59,   60,   55,   56,    0,
    0,    0,    0,    0,    0,    0,    0,   24,    0,    0,
    0,    0,   47,    0,    1,    6,    0,   20,    0,   63,
    0,    0,   64,   53,    0,    0,    0,   52,   51,    0,
   50,   49,   48,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,   33,   78,   79,    0,   22,    0,
    0,    0,   46,   45,    0,    0,    0,   68,   21,    0,
    0,    0,   86,   84,    0,   38,    0,    0,   32,    0,
   31,    0,   43,    0,    0,   40,    0,   70,    0,    0,
   19,    0,    0,   39,    0,   35,    0,   26,    0,    0,
   30,   44,   27,   69,    0,   67,    0,    0,   37,   36,
    0,   29,   28,    0,   87,    0,   34,   25,    0,   88,
    0,    0,    0,    0,   16,
};
final static short yydgoto[] = {                          3,
   12,   36,   14,   15,   16,   17,   18,   19,   20,  127,
   29,   21,   89,   37,   31,   56,   72,  128,   32,   33,
   57,
};
final static short yysindex[] = {                      -120,
  603,  774,    0,   -2,   15,   -7,    0,  -89,   27,    0,
  774,  650,  -40,    0,    0,    0,    0,    0, -179,  -39,
  -42,  746,  -30,  142,   36,    0, -107,    0,  687,   34,
  -35,   11,    0,   51,  789,    0, -244,  -27,  803,    0,
   39,    0,   66,   69,   34, -148, -142, -107, -115,    0,
  103,  114, -246,  -28,   13,   -9,    0,  118,   28,   56,
  -53,  -53,  -21,    0,    0,    0,    0,    0,    0,  -46,
  166, -107,  760, -210,  -53,  -53, -107,    0,  811,  123,
  151,  124,    0,   60,    0,    0, -258,    0,  -44,    0,
   34,   -4,    0,    0, -107,  -82,  -72,    0,    0,  146,
    0,    0,    0,   11,   11,  720, -143,   -2,   11,   -2,
   11,   -4, -133,  760,    0,    0,    0,  147,    0, -107,
 -107,  157,    0,    0, -246,  -58,   63,    0,    0,  -57,
   82,  167,    0,    0,  -67,    0, -225,  760,    0,  760,
    0,  -55,    0,  172,  174,    0,  -38,    0,  101, -258,
    0,  -52,  110,    0,  -25,    0,  738,    0,  -19,   -6,
    0,    0,    0,    0,  774,    0,    2,  774,    0,    0,
 -254,    0,    0,  256,    0,  832,    0,    0,  213,    0,
 -107,   98,  216,  169,    0,
};
final static short yyrindex[] = {                         0,
    0,    0,    0,    0,    1,    0,    0,    0,    0,    0,
    0,  272,  281,    0,    0,    0,    0,    0,    0,    0,
  -33,    0,    0,    0,    0,    0,    0,    0,    0,   23,
    0,   49,    0,    0,    0,    0,    0,    0,  276,    0,
  303,    0,  419,  223,  467,    0,    0,    0,    0,    0,
  -18,    0,    0,    0,   30,    0,    0,  -18,   30,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,  497,    0,
  443,  521,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,   71,   93,    0,    0,  117,  351,  375,
  397,  329,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  547,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,  569,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,
};
final static short yygindex[] = {                         0,
    5,  953,    0,    0,    0,    0,  863,   -3,  278,    0,
   97,  827,    0,   26,  349,  274,    0,  150,   25,   81,
  201,
};
final static int YYTABLESIZE=1129;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                        130,
   63,  177,    2,   49,   47,   73,   22,  178,    7,   53,
   66,   80,  125,   83,   70,   39,   71,   10,   42,  106,
    7,   46,   81,   82,   82,   81,   82,   66,   82,   10,
  156,   99,   27,   35,  100,  157,  158,   23,   70,   79,
   71,   63,   63,   63,   63,   63,   63,   63,   73,   96,
  114,  115,   75,   98,   24,   35,   74,   76,   49,   63,
   63,   63,   63,   81,   81,   81,   38,   81,  102,   81,
   76,   81,   81,   49,   81,   23,   81,   43,   61,   49,
   62,   81,   81,  126,   81,  104,  105,   35,  107,   73,
   77,   73,   77,   73,  109,  111,  103,   86,  113,  100,
  124,   35,   70,  149,   71,   87,  150,   73,   73,   88,
   73,   76,   47,   76,   90,   76,   74,  138,  139,   54,
   54,  147,  152,   63,   70,   63,   71,  140,  141,   76,
   76,  137,   76,   77,   84,   77,    1,   77,  183,  142,
   70,   93,   71,   94,   92,   81,  126,   81,   25,    5,
   26,   77,   77,   95,   77,  116,  117,   74,  101,   74,
  155,   74,  120,  159,  123,  160,    4,    5,  112,  174,
    6,   73,  176,   73,  132,   74,   74,    7,   74,    8,
   34,   53,  171,    9,  133,   53,   10,  143,    4,    5,
  121,  131,    6,   76,  154,   76,   54,  146,  148,    7,
  151,    8,    4,    5,   26,    9,  161,  153,   10,  108,
    5,   26,  162,  129,  163,   77,   48,   77,  164,  167,
    4,    5,   15,  165,    6,   25,    5,   51,   25,    5,
   26,    7,  168,    8,    4,    5,  169,    9,    6,   74,
   10,   74,  172,   97,   52,    7,   82,    8,   25,    5,
   26,    9,  181,   82,   10,  173,   63,   63,  175,   63,
   63,   63,   63,   63,   63,   63,   63,   63,   63,   63,
   63,    5,   63,   63,  184,    3,   63,  182,   81,   81,
    9,   15,   81,   81,   81,   81,   81,   81,   81,   81,
   81,   81,   81,  185,   81,   81,   44,   60,   81,  166,
  134,   81,    8,    0,   73,   73,    0,    0,   73,   73,
   73,   73,   73,   73,   73,   73,   73,   73,   73,    0,
   73,   73,    0,    0,   73,    0,   76,   76,   54,    0,
   76,   76,   76,   76,   76,   76,   76,   76,   76,   76,
   76,    0,   76,   76,    0,    0,   76,   15,   77,   77,
   71,    0,   77,   77,   77,   77,   77,   77,   77,   77,
   77,   77,   77,    0,   77,   77,    0,    0,   77,   54,
    0,    0,   74,   74,   75,   63,   74,   74,   74,   74,
   74,   74,   74,   74,   74,   74,   74,   54,   74,   74,
    0,   71,   74,   71,    0,   71,   72,   25,    5,   58,
    0,   25,    5,   26,    0,    9,   25,    5,   26,   71,
   71,    0,   71,    0,    0,   75,   52,   75,   63,   75,
   52,  110,    5,   26,    0,  118,    0,    8,    0,  122,
    0,    0,    0,   75,   75,    0,   75,   72,    0,   72,
    0,   72,   65,    0,    0,    0,    0,    0,    0,    0,
    0,   54,    0,   54,    0,   72,   72,    0,   72,    0,
    0,    0,   63,    0,   63,    0,   66,    0,  144,  145,
    0,    0,    0,   71,    0,   71,    0,   63,   15,   15,
    0,    0,   15,   15,   15,    0,   65,    0,    0,   15,
   15,   15,   15,    0,    0,   15,   18,   75,   15,   75,
    0,   65,    0,   65,    0,    0,    0,    0,    0,    0,
   66,    4,    5,    0,    0,    6,    0,    0,    0,   72,
   17,   72,    7,  179,    8,   66,    0,    0,    9,    0,
    0,   10,    0,    0,    0,    0,    9,    9,    0,    0,
    9,    0,    0,   63,    0,    0,   42,    9,    9,    9,
    0,    0,    0,    9,    0,   18,    9,    0,    8,    8,
    0,    0,    8,    0,    0,    0,    0,   65,   41,    8,
    8,    8,    0,    0,    0,    8,    0,    0,    8,   17,
    0,    0,    0,    0,   54,   54,    0,    0,   54,   54,
   54,   66,    0,    0,    0,   54,   54,   54,   54,    0,
    0,   54,    0,    0,   54,   42,   71,   71,    0,    0,
   71,   71,   71,   71,   71,   71,   71,   71,   71,   71,
   71,   18,   71,   71,    0,    0,   71,   41,    0,    0,
   75,   75,    0,    0,   75,   75,   75,   75,   75,   75,
   75,   75,   75,   75,   75,   17,   75,   75,    0,    0,
   75,    0,   72,   72,    0,    0,   72,   72,   72,   72,
   72,   72,   72,   72,   72,   72,   72,    0,   72,   72,
    0,   42,   72,    0,   63,   63,    0,    0,   63,   63,
   63,    0,    0,    0,    0,   63,   63,   63,   63,    0,
    0,   63,    0,   41,   63,    0,    0,    0,   65,   65,
    0,    0,   65,   65,   65,    0,    0,    0,    0,   65,
   65,   65,   65,    0,    0,   65,    0,    0,   65,    0,
    0,    0,   66,   66,    0,   11,   66,   66,   66,   70,
    0,   71,    0,   66,   66,   66,   66,    0,    0,   66,
    0,    0,   66,    0,    0,    0,   69,    0,   68,    0,
    0,    0,   18,   18,    0,    0,   18,   18,   18,    0,
    0,    0,    0,   18,   18,   18,   18,    0,    0,   18,
    0,    0,   18,    0,   40,    0,   17,   17,    0,    0,
   17,   17,   17,    0,    0,    0,    0,   17,   17,   17,
   17,    0,    0,   17,    0,    0,   17,    0,    0,    0,
    0,    0,   42,   42,    0,    0,   42,   42,   42,    0,
    0,    0,    0,   42,   42,   42,   42,    0,    0,   42,
    0,    0,   42,    0,   41,   41,    0,    0,   41,   41,
   41,    0,   30,    0,    0,   41,   41,   41,   41,    0,
    0,   41,   35,    0,   41,   45,    0,    0,    0,   55,
   59,    0,    0,   30,    0,    0,    0,    0,    4,    5,
   35,    0,    6,    0,   30,    0,    0,    0,   28,    7,
   50,    8,    0,   91,   30,    9,    0,    0,   10,    0,
    0,    0,   35,    0,    0,   28,   28,   30,   30,   28,
    0,    0,    0,    0,    0,    0,   30,   30,   30,    0,
   28,   30,   30,   30,    0,    4,    5,   30,    0,    6,
   28,    0,    0,   78,    0,    0,    7,    0,    8,    0,
    0,   30,    9,   28,   28,   10,   30,   85,    0,    0,
    0,    0,   28,   28,   28,  119,    0,   28,   28,   28,
    0,    0,    0,   28,    0,    0,   30,   30,    0,   64,
   65,   66,   67,   13,   13,    0,  180,   28,    0,    0,
    0,    0,   28,   13,   41,    0,    0,    0,    0,    0,
    0,    0,    0,    0,   41,    4,    5,    0,    0,    6,
  135,  136,   28,   28,    0,    0,    7,   13,    8,    0,
    0,   41,    9,    4,    5,   10,    0,    6,    0,  170,
    0,    4,    5,    0,    7,    6,    8,   30,    0,    0,
    9,    0,    7,   10,    8,    4,    5,    0,    9,    6,
    0,   10,    0,    0,    0,    0,    7,    0,    8,    4,
    5,   41,    9,    6,    0,   10,    0,    0,    0,    0,
    7,    0,    8,   28,    4,    5,    9,    0,    6,   10,
    0,    0,    0,    0,    0,    7,    0,    8,    4,    5,
    0,    9,    6,    0,   10,    0,    4,    5,    0,    7,
    6,    8,    0,    0,    0,    9,    0,    7,   10,    8,
    0,    0,    0,    9,    0,    0,   10,    4,    5,    0,
    0,    6,    0,    0,    0,    0,    0,    0,    7,    0,
    8,    0,    0,    0,    9,    0,    0,   10,    0,    0,
    0,    0,    0,    0,    0,    0,    0,   13,    0,    0,
   13,    0,    0,    0,    0,    0,   41,    0,   41,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         44,
    0,  256,  123,   46,   44,   41,    2,  262,  267,   40,
   44,  256,  271,   41,   43,   11,   45,  276,   59,   41,
  267,   61,    0,   42,   43,  270,   45,   61,   47,  276,
  256,   41,   40,  123,   44,  261,  262,   40,   43,   35,
   45,   41,   42,   43,   44,   45,   46,   47,    0,   53,
  261,  262,   42,   41,   40,  123,   31,   47,   46,   59,
   60,   61,   62,   41,   42,   43,   40,   45,   41,   47,
    0,   42,   43,   46,   45,   40,   47,  257,   43,   46,
   45,   59,   60,   87,   62,   61,   62,  123,   63,   41,
   40,   43,    0,   45,   70,   71,   41,   59,   73,   44,
   41,  123,   43,   41,   45,   40,   44,   59,   60,  258,
   62,   41,   44,   43,  257,   45,    0,  261,  262,   23,
   24,  125,   41,  123,   43,  125,   45,  261,  262,   59,
   60,  106,   62,   41,   38,   43,  257,   45,   41,  114,
   43,  257,   45,   41,   48,  123,  150,  125,  256,  257,
  258,   59,   60,   40,   62,   75,   76,   41,   41,   43,
  135,   45,   40,  138,   41,  140,  256,  257,   72,  165,
  260,  123,  168,  125,  257,   59,   60,  267,   62,  269,
  270,   40,  157,  273,  257,   40,  276,   41,  256,  257,
   40,   95,  260,  123,  262,  125,  100,   41,  257,  267,
  258,  269,  256,  257,  258,  273,  262,   41,  276,  256,
  257,  258,   41,  258,   41,  123,  259,  125,  257,  272,
  256,  257,    0,  123,  260,  256,  257,  258,  256,  257,
  258,  267,  123,  269,  256,  257,  262,  273,  260,  123,
  276,  125,  262,  272,  275,  267,  274,  269,  256,  257,
  258,  273,   40,  272,  276,  262,  256,  257,  257,  259,
  260,  261,  262,  263,  264,  265,  266,  267,  268,  269,
  270,    0,  272,  273,   59,    0,  276,  181,  256,  257,
    0,   59,  260,  261,  262,  263,  264,  265,  266,  267,
  268,  269,  270,  125,  272,  273,   19,   24,  276,  150,
  100,  272,    0,   -1,  256,  257,   -1,   -1,  260,  261,
  262,  263,  264,  265,  266,  267,  268,  269,  270,   -1,
  272,  273,   -1,   -1,  276,   -1,  256,  257,    0,   -1,
  260,  261,  262,  263,  264,  265,  266,  267,  268,  269,
  270,   -1,  272,  273,   -1,   -1,  276,  125,  256,  257,
    0,   -1,  260,  261,  262,  263,  264,  265,  266,  267,
  268,  269,  270,   -1,  272,  273,   -1,   -1,  276,   41,
   -1,   -1,  256,  257,    0,   27,  260,  261,  262,  263,
  264,  265,  266,  267,  268,  269,  270,   59,  272,  273,
   -1,   41,  276,   43,   -1,   45,    0,  256,  257,  258,
   -1,  256,  257,  258,   -1,  125,  256,  257,  258,   59,
   60,   -1,   62,   -1,   -1,   41,  275,   43,    0,   45,
  275,  256,  257,  258,   -1,   77,   -1,  125,   -1,   81,
   -1,   -1,   -1,   59,   60,   -1,   62,   41,   -1,   43,
   -1,   45,    0,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,  123,   -1,  125,   -1,   59,   60,   -1,   62,   -1,
   -1,   -1,   44,   -1,   46,   -1,    0,   -1,  120,  121,
   -1,   -1,   -1,  123,   -1,  125,   -1,   59,  256,  257,
   -1,   -1,  260,  261,  262,   -1,   44,   -1,   -1,  267,
  268,  269,  270,   -1,   -1,  273,    0,  123,  276,  125,
   -1,   59,   -1,   61,   -1,   -1,   -1,   -1,   -1,   -1,
   44,  256,  257,   -1,   -1,  260,   -1,   -1,   -1,  123,
    0,  125,  267,  268,  269,   59,   -1,   -1,  273,   -1,
   -1,  276,   -1,   -1,   -1,   -1,  256,  257,   -1,   -1,
  260,   -1,   -1,  125,   -1,   -1,    0,  267,  268,  269,
   -1,   -1,   -1,  273,   -1,   59,  276,   -1,  256,  257,
   -1,   -1,  260,   -1,   -1,   -1,   -1,  125,    0,  267,
  268,  269,   -1,   -1,   -1,  273,   -1,   -1,  276,   59,
   -1,   -1,   -1,   -1,  256,  257,   -1,   -1,  260,  261,
  262,  125,   -1,   -1,   -1,  267,  268,  269,  270,   -1,
   -1,  273,   -1,   -1,  276,   59,  256,  257,   -1,   -1,
  260,  261,  262,  263,  264,  265,  266,  267,  268,  269,
  270,  125,  272,  273,   -1,   -1,  276,   59,   -1,   -1,
  256,  257,   -1,   -1,  260,  261,  262,  263,  264,  265,
  266,  267,  268,  269,  270,  125,  272,  273,   -1,   -1,
  276,   -1,  256,  257,   -1,   -1,  260,  261,  262,  263,
  264,  265,  266,  267,  268,  269,  270,   -1,  272,  273,
   -1,  125,  276,   -1,  256,  257,   -1,   -1,  260,  261,
  262,   -1,   -1,   -1,   -1,  267,  268,  269,  270,   -1,
   -1,  273,   -1,  125,  276,   -1,   -1,   -1,  256,  257,
   -1,   -1,  260,  261,  262,   -1,   -1,   -1,   -1,  267,
  268,  269,  270,   -1,   -1,  273,   -1,   -1,  276,   -1,
   -1,   -1,  256,  257,   -1,  123,  260,  261,  262,   43,
   -1,   45,   -1,  267,  268,  269,  270,   -1,   -1,  273,
   -1,   -1,  276,   -1,   -1,   -1,   60,   -1,   62,   -1,
   -1,   -1,  256,  257,   -1,   -1,  260,  261,  262,   -1,
   -1,   -1,   -1,  267,  268,  269,  270,   -1,   -1,  273,
   -1,   -1,  276,   -1,  125,   -1,  256,  257,   -1,   -1,
  260,  261,  262,   -1,   -1,   -1,   -1,  267,  268,  269,
  270,   -1,   -1,  273,   -1,   -1,  276,   -1,   -1,   -1,
   -1,   -1,  256,  257,   -1,   -1,  260,  261,  262,   -1,
   -1,   -1,   -1,  267,  268,  269,  270,   -1,   -1,  273,
   -1,   -1,  276,   -1,  256,  257,   -1,   -1,  260,  261,
  262,   -1,    6,   -1,   -1,  267,  268,  269,  270,   -1,
   -1,  273,  123,   -1,  276,   19,   -1,   -1,   -1,   23,
   24,   -1,   -1,   27,   -1,   -1,   -1,   -1,  256,  257,
  123,   -1,  260,   -1,   38,   -1,   -1,   -1,    6,  267,
  125,  269,   -1,   47,   48,  273,   -1,   -1,  276,   -1,
   -1,   -1,  123,   -1,   -1,   23,   24,   61,   62,   27,
   -1,   -1,   -1,   -1,   -1,   -1,   70,   71,   72,   -1,
   38,   75,   76,   77,   -1,  256,  257,   81,   -1,  260,
   48,   -1,   -1,  125,   -1,   -1,  267,   -1,  269,   -1,
   -1,   95,  273,   61,   62,  276,  100,  125,   -1,   -1,
   -1,   -1,   70,   71,   72,  125,   -1,   75,   76,   77,
   -1,   -1,   -1,   81,   -1,   -1,  120,  121,   -1,  263,
  264,  265,  266,    1,    2,   -1,  125,   95,   -1,   -1,
   -1,   -1,  100,   11,   12,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   22,  256,  257,   -1,   -1,  260,
  261,  262,  120,  121,   -1,   -1,  267,   35,  269,   -1,
   -1,   39,  273,  256,  257,  276,   -1,  260,   -1,  262,
   -1,  256,  257,   -1,  267,  260,  269,  181,   -1,   -1,
  273,   -1,  267,  276,  269,  256,  257,   -1,  273,  260,
   -1,  276,   -1,   -1,   -1,   -1,  267,   -1,  269,  256,
  257,   79,  273,  260,   -1,  276,   -1,   -1,   -1,   -1,
  267,   -1,  269,  181,  256,  257,  273,   -1,  260,  276,
   -1,   -1,   -1,   -1,   -1,  267,   -1,  269,  256,  257,
   -1,  273,  260,   -1,  276,   -1,  256,  257,   -1,  267,
  260,  269,   -1,   -1,   -1,  273,   -1,  267,  276,  269,
   -1,   -1,   -1,  273,   -1,   -1,  276,  256,  257,   -1,
   -1,  260,   -1,   -1,   -1,   -1,   -1,   -1,  267,   -1,
  269,   -1,   -1,   -1,  273,   -1,   -1,  276,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,  165,   -1,   -1,
  168,   -1,   -1,   -1,   -1,   -1,  174,   -1,  176,
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
"listasentencia : listasentencia sentencia",
"listasentencia : sentencia",
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
"cuerpo_bloque : '{' listasentencia '}'",
"cuerpo_bloque : sentencia",
"cuerpo_bloque : '{' '}'",
"control : IF '(' condicion ')' cuerpo_bloque ELSE cuerpo_bloque ENDIF",
"control : IF '(' condicion ')' cuerpo_bloque ENDIF",
"control : DO cuerpo_bloque UNTIL '(' condicion ')'",
"control : IF condicion ')' cuerpo_bloque ELSE cuerpo_bloque ENDIF",
"control : IF '(' condicion cuerpo_bloque ELSE cuerpo_bloque ENDIF",
"control : IF condicion cuerpo_bloque ELSE cuerpo_bloque ENDIF",
"control : IF condicion ')' cuerpo_bloque ENDIF",
"control : IF '(' condicion cuerpo_bloque ENDIF",
"control : IF condicion cuerpo_bloque ENDIF",
"control : IF '(' condicion ')' cuerpo_bloque ELSE cuerpo_bloque error",
"control : IF '(' condicion ')' cuerpo_bloque error",
"control : IF '(' condicion ')' cuerpo_bloque ELSE ENDIF",
"control : IF '(' condicion ')' ELSE cuerpo_bloque ENDIF",
"control : IF '(' condicion ')' ENDIF",
"control : IF '(' condicion ')' ELSE ENDIF",
"control : DO cuerpo_bloque UNTIL condicion ')'",
"control : DO cuerpo_bloque UNTIL '(' condicion",
"control : DO cuerpo_bloque UNTIL condicion",
"control : DO UNTIL '(' condicion ')'",
"control : DO cuerpo_bloque error '(' condicion ')'",
"print : PRINT '(' expresion ')'",
"print : PRINT '(' CADENAML ')'",
"print : PRINT '(' ')'",
"funcion : ID '(' listapinvocacion ')'",
"funcion : ID '(' identificador ')'",
"funcion : ID '(' CTE ')'",
"funcion : error '(' listapinvocacion ')'",
"funcion : error '(' identificador ')'",
"funcion : error '(' CTE ')'",
"condicion : expresion comparador expresion",
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
"listaparametros : listaparametros ',' parametro",
"listaparametros : parametro",
"parametro : CV tipo ID",
"parametro : tipo ID",
"expresion : expresion '+' termino",
"expresion : expresion '-' termino",
"expresion : termino",
"expresion : expresion '+' error",
"expresion : expresion '-' error",
"expresion : error '+' termino",
"expresion : error '-' termino",
"termino : termino '*' factor",
"termino : termino '/' factor",
"termino : factor",
"factor : identificador",
"factor : CTE",
"factor : funcion",
"listapinvocacion : listapinvocacion ',' pinvocacion",
"listapinvocacion : pinvocacion",
"pinvocacion : expresion FLECHA ID",
"pinvocacion : TRUNC '(' expresion ')' FLECHA ID",
"pinvocacion : '(' tipo ID ')' '{' listasentencia '}'",
};

//#line 147 "gramatica2.y"

private void yyerror(String s) {
    System.out.println("Error: " + s);
}

public Parser(Map<String, Simbolo> tablaSimbolos, String path)
{
  this.tablaSimbolos = tablaSimbolos;
  this.lexer = new Lexico(tablaSimbolos);
  this.lexer.cargarBuffer(path);
}
//#line 573 "Parser.java"
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
//#line 12 "gramatica2.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el nombre del programa");}
break;
case 3:
//#line 13 "gramatica2.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta llave de cierre del cuerpo del programa");}
break;
case 4:
//#line 14 "gramatica2.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta la llave de apertura del cuerpo del programa");}
break;
case 5:
//#line 15 "gramatica2.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": faltan llaves que encierren el cuerpo del programa");}
break;
case 6:
//#line 18 "gramatica2.y"
{System.out.println("Se detectó una lista de sentencias seguida de una sentencia");}
break;
case 7:
//#line 19 "gramatica2.y"
{System.out.println("Se detectó una sentencia");}
break;
case 8:
//#line 20 "gramatica2.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta punto y coma al final de la lista de sentencias con otra sentencia");}
break;
case 9:
//#line 21 "gramatica2.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta punto y coma al final de la sentencia");}
break;
case 13:
//#line 27 "gramatica2.y"
{System.out.println("Se detectó un print");}
break;
case 14:
//#line 28 "gramatica2.y"
{System.out.println("Se obtuvo una sentencia desde la invocación de una función");}
break;
case 15:
//#line 31 "gramatica2.y"
{System.out.println("Se detectó una declaración de variables");}
break;
case 16:
//#line 32 "gramatica2.y"
{System.out.println("Se detectó una declaración de función");}
break;
case 17:
//#line 35 "gramatica2.y"
{System.out.println("Se detectó una asignación simple");}
break;
case 18:
//#line 36 "gramatica2.y"
{System.out.println("Se detectó una asignación múltiple");}
break;
case 21:
//#line 41 "gramatica2.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta coma entre constantes");}
break;
case 25:
//#line 49 "gramatica2.y"
{System.out.println("Se detectó un bloque IF-ELSE");}
break;
case 26:
//#line 50 "gramatica2.y"
{System.out.println("Se detectó un bloque IF");}
break;
case 27:
//#line 51 "gramatica2.y"
{System.out.println("Se detectó un bloque DO UNTIL");}
break;
case 28:
//#line 53 "gramatica2.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta del parentesis de cierre de la condicion del bloque IF");}
break;
case 29:
//#line 54 "gramatica2.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta del parentesis de apertura de la condición del bloque IF");}
break;
case 30:
//#line 55 "gramatica2.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta de parentesis de la condicion del bloque IF");}
break;
case 31:
//#line 56 "gramatica2.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta del parentesis de apertura de la condicion del bloque IF");}
break;
case 32:
//#line 57 "gramatica2.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta del parentesis de cierre de la condición del bloque IF");}
break;
case 33:
//#line 58 "gramatica2.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta de ambos parentesis de la condicion del bloque IF");}
break;
case 34:
//#line 59 "gramatica2.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta la palabra ENDIF al final del bloque IF");}
break;
case 35:
//#line 60 "gramatica2.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta la palabra ENDIF al final del bloque IF");}
break;
case 36:
//#line 61 "gramatica2.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el cuerpo en el bloque ELSE");}
break;
case 37:
//#line 62 "gramatica2.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el cuerpo en el bloque IF");}
break;
case 38:
//#line 63 "gramatica2.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el cuerpo en el bloque IF");}
break;
case 39:
//#line 64 "gramatica2.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el cuerpo en los bloques IF y ELSE");}
break;
case 40:
//#line 65 "gramatica2.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el parentesis de apertura de la condición del bloque DO UNTIL");}
break;
case 41:
//#line 66 "gramatica2.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el parentesis de apertura de la condición del bloque DO UNTIL");}
break;
case 42:
//#line 67 "gramatica2.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta de ambos parentesis en la condición del bloque DO UNTIL");}
break;
case 43:
//#line 68 "gramatica2.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta de cuerpo en iteracion");}
break;
case 44:
//#line 69 "gramatica2.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta la palabra UNTIL en la iteracion");}
break;
case 47:
//#line 74 "gramatica2.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta expresión o cadena en el print");}
break;
case 48:
//#line 77 "gramatica2.y"
{System.out.println("Se detectó una invocación a función");}
break;
case 49:
//#line 78 "gramatica2.y"
{System.out.println("Se detectó una invocación a función");}
break;
case 50:
//#line 79 "gramatica2.y"
{System.out.println("Se detectó una invocación a función");}
break;
case 51:
//#line 80 "gramatica2.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el nombre de la funcion");}
break;
case 52:
//#line 81 "gramatica2.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el nombre de la funcion");}
break;
case 53:
//#line 82 "gramatica2.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el nombre de la funcion");}
break;
case 74:
//#line 120 "gramatica2.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando derecho de la suma");}
break;
case 75:
//#line 121 "gramatica2.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando derecho de la resta");}
break;
case 76:
//#line 122 "gramatica2.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando izquierdo de la suma");}
break;
case 77:
//#line 123 "gramatica2.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando izquierdo de la resta");}
break;
//#line 906 "Parser.java"
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
