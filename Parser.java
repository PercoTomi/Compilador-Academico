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






//#line 2 ".\gramatica2.y"
    import Simbolos.*;
    import java.util.Map;
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
    0,    0,    0,    0,    0,    1,    1,    1,    1,    2,
    2,    2,    2,    2,    4,    4,    3,    3,   13,   13,
   13,   14,   14,   14,    5,    5,    5,    5,    5,    5,
    5,    5,    5,    5,    5,    5,    5,    5,    5,    5,
    5,    5,    5,    5,    6,    6,    6,    7,    7,    7,
    7,    7,    7,   15,   17,   17,   17,   17,   17,   17,
    8,    8,   12,   12,    9,    9,   10,   10,   18,   18,
   11,   11,   11,   11,   11,   11,   11,   19,   19,   19,
   19,   19,   19,   19,   20,   20,   20,   16,   16,   21,
   21,   21,
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
    3,    3,    3,    3,    1,    1,    1,    3,    1,    3,
    6,    7,
};
final static short yydefred[] = {                         0,
    0,    0,    0,    0,    0,    0,   61,    0,    0,   62,
    0,    0,    0,   10,   11,   12,   13,   14,    0,    0,
    0,    0,    0,    0,    0,   86,    0,   87,    0,    0,
    0,    0,   80,    0,    0,   23,    0,    0,    0,    4,
    0,    7,    0,    0,    0,    0,    0,    0,    0,    2,
    0,    0,    0,    0,    0,    0,   89,    0,    0,    0,
    0,    0,    0,    0,    0,   57,   58,   59,   60,   55,
   56,    0,    0,    0,    0,    0,    0,    0,    0,   24,
    0,    0,    0,    0,   47,    0,    1,    6,    0,   20,
    0,   63,    0,    0,   64,   53,    0,    0,    0,   52,
   51,    0,   50,   49,   48,    0,    0,    0,   83,   84,
    0,    0,    0,    0,    0,    0,    0,    0,    0,   33,
    0,   78,    0,   79,    0,   22,    0,    0,    0,   46,
   45,    0,    0,    0,   68,   21,    0,    0,    0,   90,
   88,    0,   38,    0,    0,   32,    0,   31,    0,   43,
    0,    0,   40,    0,   70,    0,    0,   19,    0,    0,
   39,    0,   35,    0,   26,    0,    0,   30,   44,   27,
   69,    0,   67,    0,    0,   37,   36,    0,   29,   28,
    0,   91,    0,   34,   25,    0,   92,    0,    0,    0,
    0,   16,
};
final static short yydgoto[] = {                          3,
   12,   36,   14,   15,   16,   17,   18,   19,   20,  134,
   29,   21,   91,   37,   31,   56,   74,  135,   32,   33,
   57,
};
final static short yysindex[] = {                      -120,
  852,  148,    0,   15,   19,   -7,    0,  318,   32,    0,
  148,  880,   30,    0,    0,    0,    0,    0, -242,  -37,
  -42,  894,  -30,  -27,   57,    0, -107,    0,  135,  -12,
  -35,   10,    0,   58,  909,    0, -229,   46,  923,    0,
   42,    0,   67,   91,  -12, -114, -101, -107,  -84,    0,
  134,  149, -209,  -29,   28,   -9,    0,  158,   74,   12,
  -93,  -93,  -90,  -90,  304,    0,    0,    0,    0,    0,
    0,  -87,  -75, -107,  866, -253,  -72,  -55, -107,    0,
  945,  150,  114,  167,    0,   35,    0,    0, -192,    0,
  -44,    0,  -12,   80,    0,    0, -107,  -48,  -47,    0,
    0,  -21,    0,    0,    0,   63,   10,   10,    0,    0,
  -69, -122,   63,   10,   63,   10,   80,  -56,  866,    0,
   15,    0,   15,    0,  170,    0, -107, -107,  171,    0,
    0, -209,  -38,   88,    0,    0,  -45,  102,  174,    0,
    0,  575,    0, -225,  866,    0,  866,    0,  -39,    0,
  179,  183,    0,  -24,    0,  121, -192,    0,  -26,  129,
    0,   13,    0,  621,    0,   14,   20,    0,    0,    0,
    0,  148,    0,   -4,  148,    0,    0, -251,    0,    0,
  968,    0,  950,    0,    0,  232,    0, -107,  116,  222,
  169,    0,
};
final static short yyrindex[] = {                         0,
    0,    0,    0,    0,    1,    0,    0,    0,    0,    0,
    0,  297,  239,    0,    0,    0,    0,    0,    0,    0,
  -32,    0,    0,    0,    0,    0,    0,    0,    0,   23,
    0,   93,    0,    0,    0,    0,    0,    0,  298,    0,
  278,    0,  465,  543,  525,    0,    0,    0,    0,    0,
  -25,    0,    0,    0,  -17,    0,    0,  -25,  -17,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
  593,    0,  497,  677,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,  117,  351,    0,    0,
    0,    0,  375,  397,  419,  443,  655,    0,    0,    0,
   49,    0,   71,    0,    0,    0,    0,    0,  701,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  731,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,
};
final static short yygindex[] = {                         0,
   84,  874,    0,    0,    0,    0, 1016,  -51,  281,    0,
  785,  989,    0,  408,   -6,  277,    0,  151,  -22,   64,
  205,
};
final static int YYTABLESIZE=1244;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                        137,
   63,   98,    2,   49,  184,   75,   47,  119,  120,   53,
  185,   66,   53,   72,   43,   73,   86,   86,   53,   86,
   65,   86,   85,   46,   85,   85,   82,   85,   66,   85,
  163,  101,   27,   49,  102,  164,  165,  133,  107,  108,
   83,   63,   63,   63,   63,   63,   63,   63,   81,  114,
  116,   77,  105,   35,   23,  102,   78,    7,   24,   63,
   63,   63,   63,   85,   85,   85,   10,   85,  100,   85,
   82,   38,  125,   49,    7,  131,  129,   72,  132,   73,
  154,   85,   85,   10,   85,   22,   85,   35,   42,   81,
   81,   81,   73,   81,   39,   81,   23,   79,   63,   61,
   88,   62,   23,   64,   63,  133,   89,   81,   81,   64,
   81,   82,   82,   82,  104,   82,   76,   82,   81,   49,
  151,  152,   72,   63,   73,   63,  109,  110,  156,   82,
   82,  157,   82,   73,   47,   73,    1,   73,  145,  146,
  122,  124,  159,   90,   72,   85,   73,   85,   25,    5,
   26,   73,   73,  128,   73,   92,  190,   76,   72,   76,
   73,   76,  106,    5,   26,    4,    5,   26,  113,    5,
   26,   81,   95,   81,   96,   76,   76,   72,   76,   73,
  115,    5,   26,  121,    5,   26,    4,    5,   97,  127,
    6,  142,  143,   82,   71,   82,   70,    7,  103,    8,
  123,    5,   26,    9,  147,  148,   10,  130,  139,  140,
  150,  153,  158,  136,  160,   73,   48,   73,  155,  169,
    4,    5,  168,  170,    6,   25,    5,   51,   25,    5,
   58,    7,  171,    8,   25,    5,   26,    9,    9,   76,
   10,   76,   99,  172,   52,  174,   86,   52,   25,    5,
   26,  175,  182,   52,   85,  181,   63,   63,  183,   63,
   63,   63,   63,   63,   63,   63,   63,   63,   63,   63,
   63,  188,   63,   63,  176,  179,   63,    8,   85,   85,
  191,  180,   85,   85,   85,   85,   85,   85,   85,   85,
   85,   85,   85,  192,   85,   85,    5,    3,   85,   44,
   60,   25,    5,   26,   81,   81,  141,  173,   81,   81,
   81,   81,   81,   81,   81,   81,   81,   81,   81,   84,
   81,   81,    0,    0,   81,    0,   82,   82,    0,    0,
   82,   82,   82,   82,   82,   82,   82,   82,   82,   82,
   82,    0,   82,   82,  111,    0,   82,    0,   73,   73,
   77,    0,   73,   73,   73,   73,   73,   73,   73,   73,
   73,   73,   73,    9,   73,   73,    0,    0,   73,   25,
    5,   26,   76,   76,   74,    0,   76,   76,   76,   76,
   76,   76,   76,   76,   76,   76,   76,    0,   76,   76,
    0,   77,   76,   77,    0,   77,   71,   66,   67,   68,
   69,    0,    8,    4,    5,    0,    0,    6,    0,   77,
   77,    0,   77,    0,    7,   74,    8,   74,   75,   74,
    9,    0,    0,   10,    0,    0,   35,    0,    0,    0,
    0,    0,    0,   74,   74,    0,   74,   71,   76,   71,
   35,   71,   72,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,   71,   71,    0,   71,   75,
    0,   75,    0,   75,   63,    0,    0,    0,    0,    0,
    0,    0,  112,   77,    0,   77,    0,   75,   75,    0,
   75,    0,  118,   72,    0,   72,    0,   72,    0,    0,
    0,    0,    0,    0,    9,    9,   65,   74,    9,   74,
    0,   72,   72,    0,   72,    9,    9,    9,   63,    0,
   63,    9,    0,    0,    9,    0,    0,    0,  144,   71,
    0,   71,    0,   63,   66,    0,  149,    0,    0,    0,
    0,    0,    0,    8,    8,    0,    0,    8,    0,    0,
   65,   75,   15,   75,    8,    8,    8,    0,    0,  162,
    8,    0,  166,    8,  167,   65,    0,   65,    0,    4,
    5,    0,    0,    6,    0,   72,    0,   72,   66,    0,
    7,  178,    8,    4,    5,    0,    9,    6,    0,   10,
    0,    0,    0,   66,    7,    0,    8,   34,    0,   63,
    9,    0,   18,   10,    0,    0,    0,    0,    0,    0,
    0,   15,    0,    0,    0,    0,   77,   77,    0,    0,
   77,   77,   77,   77,   77,   77,   77,   77,   77,   77,
   77,   65,   77,   77,    0,    0,   77,    0,    0,    0,
   74,   74,    0,    0,   74,   74,   74,   74,   74,   74,
   74,   74,   74,   74,   74,    0,   74,   74,    0,   66,
   74,   18,   71,   71,   54,    0,   71,   71,   71,   71,
   71,   71,   71,   71,   71,   71,   71,   15,   71,   71,
    0,    0,   71,    0,   75,   75,   17,    0,   75,   75,
   75,   75,   75,   75,   75,   75,   75,   75,   75,    0,
   75,   75,    0,    0,   75,   54,    0,   35,   72,   72,
   42,    0,   72,   72,   72,   72,   72,   72,   72,   72,
   72,   72,   72,   54,   72,   72,    0,   18,   72,    0,
   63,   63,    0,    0,   63,   63,   63,    0,    0,    0,
   41,   63,   63,   63,   63,   17,    0,   63,    0,    0,
   63,    0,    0,   35,    0,    0,    0,    0,    0,    0,
    0,    0,   65,   65,    0,    0,   65,   65,   65,   42,
    0,    0,    0,   65,   65,   65,   65,    0,    0,   65,
    0,    0,   65,    0,    0,    0,    0,   54,    0,   54,
   66,   66,    0,    0,   66,   66,   66,    0,    0,   41,
    0,   66,   66,   66,   66,    0,    0,   66,   15,   15,
   66,   17,   15,   15,   15,    0,    0,   54,   54,   15,
   15,   15,   15,    0,    0,   15,    0,    0,   15,    0,
    0,    0,   86,    0,    0,   42,    0,    0,    0,    0,
    4,    5,   94,    0,    6,    0,  161,    0,    0,    0,
    0,    7,    0,    8,    0,    0,    0,    9,   18,   18,
   10,    0,   18,   18,   18,   41,    0,    0,  117,   18,
   18,   18,   18,    0,    0,   18,    0,    0,   18,    0,
    0,    0,    0,    0,   13,   13,    4,    5,    0,    0,
    6,  138,  177,    0,   13,   41,   54,    7,    0,    8,
    0,    0,    0,    9,    0,   41,   10,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,   13,    0,
   54,   54,   41,    0,   54,   54,   54,    0,    0,    0,
    0,   54,   54,   54,   54,    0,    0,   54,    0,    0,
   54,    0,   17,   17,    0,    0,   17,   17,   17,    0,
    0,    0,    0,   17,   17,   17,   17,    0,    0,   17,
    0,    0,   17,    0,   41,    0,   42,   42,    0,    0,
   42,   42,   42,    0,    0,    0,    0,   42,   42,   42,
   42,    0,  189,   42,   11,    0,   42,    0,    0,    0,
    0,    0,    0,    0,    0,    0,   41,   41,   35,    0,
   41,   41,   41,    0,   30,    0,    0,   41,   41,   41,
   41,    0,    0,   41,   40,    0,   41,   45,    0,    0,
    0,   55,   59,    0,    0,   30,    0,    0,   50,    0,
    0,   28,    0,    0,    0,    0,   30,    0,    0,    0,
    0,    0,    0,   80,    0,   93,   30,    0,   28,   28,
    0,    0,   28,    0,    0,   13,    0,   87,   13,   30,
   30,   30,   30,   28,   41,    0,   41,    0,    0,    0,
   30,   30,   30,   28,    0,   30,   30,   30,    0,  126,
    0,   30,    0,    0,  187,    0,   28,   28,   28,   28,
    0,    0,    0,    0,    0,   30,    0,   28,   28,   28,
   30,    0,   28,   28,   28,    0,    0,    0,   28,    0,
    0,    0,    0,    0,    0,    0,    0,    4,    5,    0,
    0,    6,   28,    0,    0,   30,   30,   28,    7,    0,
    8,    4,    5,    0,    9,    6,    0,   10,    0,    0,
    0,    0,    7,    0,    8,    4,    5,    0,    9,    6,
    0,   10,   28,   28,    0,    0,    7,    0,    8,    4,
    5,    0,    9,    6,    0,   10,    0,    0,    0,    0,
    7,    0,    8,    0,    4,    5,    9,    0,    6,   10,
    0,    0,    0,    0,    0,    7,   30,    8,    4,    5,
    0,    9,    6,    0,   10,    0,    0,    0,    0,    7,
    0,    8,    0,    0,    0,    9,    0,    0,   10,    0,
    4,    5,    0,   28,    6,    4,    5,    0,    0,    6,
    0,    7,    0,    8,    0,    0,    7,    9,    8,    0,
   10,    0,    9,    4,    5,   10,    0,    6,    0,    0,
    0,    0,    0,    0,    7,  186,    8,    0,    0,    0,
    9,    0,    0,   10,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         44,
    0,   53,  123,   46,  256,   41,   44,  261,  262,   40,
  262,   44,   40,   43,  257,   45,   42,   43,   40,   45,
   27,   47,    0,   61,   42,   43,  256,   45,   61,   47,
  256,   41,   40,   46,   44,  261,  262,   89,   61,   62,
  270,   41,   42,   43,   44,   45,   46,   47,    0,   72,
   73,   42,   41,  123,   40,   44,   47,  267,   40,   59,
   60,   61,   62,   41,   42,   43,  276,   45,   41,   47,
    0,   40,   79,   46,  267,   41,   83,   43,  271,   45,
  132,   59,   60,  276,   62,    2,   41,  123,   59,   41,
   42,   43,    0,   45,   11,   47,   40,   40,   42,   43,
   59,   45,   40,   47,   42,  157,   40,   59,   60,   47,
   62,   41,   42,   43,   41,   45,    0,   47,   35,   46,
  127,  128,   43,  123,   45,  125,   63,   64,   41,   59,
   60,   44,   62,   41,   44,   43,  257,   45,  261,  262,
   77,   78,   41,  258,   43,  123,   45,  125,  256,  257,
  258,   59,   60,   40,   62,  257,   41,   41,   43,   43,
   45,   45,  256,  257,  258,  256,  257,  258,  256,  257,
  258,  123,  257,  125,   41,   59,   60,   43,   62,   45,
  256,  257,  258,  256,  257,  258,  256,  257,   40,   40,
  260,  261,  262,  123,   60,  125,   62,  267,   41,  269,
  256,  257,  258,  273,  261,  262,  276,   41,  257,  257,
   41,   41,  258,  258,   41,  123,  259,  125,  257,   41,
  256,  257,  262,   41,  260,  256,  257,  258,  256,  257,
  258,  267,  257,  269,  256,  257,  258,  273,    0,  123,
  276,  125,  272,  123,  275,  272,  272,  275,  256,  257,
  258,  123,  257,  275,  272,  172,  256,  257,  175,  259,
  260,  261,  262,  263,  264,  265,  266,  267,  268,  269,
  270,   40,  272,  273,  262,  262,  276,    0,  256,  257,
   59,  262,  260,  261,  262,  263,  264,  265,  266,  267,
  268,  269,  270,  125,  272,  273,    0,    0,  276,   19,
   24,  256,  257,  258,  256,  257,  102,  157,  260,  261,
  262,  263,  264,  265,  266,  267,  268,  269,  270,  274,
  272,  273,   -1,   -1,  276,   -1,  256,  257,   -1,   -1,
  260,  261,  262,  263,  264,  265,  266,  267,  268,  269,
  270,   -1,  272,  273,   41,   -1,  276,   -1,  256,  257,
    0,   -1,  260,  261,  262,  263,  264,  265,  266,  267,
  268,  269,  270,  125,  272,  273,   -1,   -1,  276,  256,
  257,  258,  256,  257,    0,   -1,  260,  261,  262,  263,
  264,  265,  266,  267,  268,  269,  270,   -1,  272,  273,
   -1,   41,  276,   43,   -1,   45,    0,  263,  264,  265,
  266,   -1,  125,  256,  257,   -1,   -1,  260,   -1,   59,
   60,   -1,   62,   -1,  267,   41,  269,   43,    0,   45,
  273,   -1,   -1,  276,   -1,   -1,  123,   -1,   -1,   -1,
   -1,   -1,   -1,   59,   60,   -1,   62,   41,   31,   43,
  123,   45,    0,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   59,   60,   -1,   62,   41,
   -1,   43,   -1,   45,    0,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   65,  123,   -1,  125,   -1,   59,   60,   -1,
   62,   -1,   75,   41,   -1,   43,   -1,   45,   -1,   -1,
   -1,   -1,   -1,   -1,  256,  257,    0,  123,  260,  125,
   -1,   59,   60,   -1,   62,  267,  268,  269,   44,   -1,
   46,  273,   -1,   -1,  276,   -1,   -1,   -1,  111,  123,
   -1,  125,   -1,   59,    0,   -1,  119,   -1,   -1,   -1,
   -1,   -1,   -1,  256,  257,   -1,   -1,  260,   -1,   -1,
   44,  123,    0,  125,  267,  268,  269,   -1,   -1,  142,
  273,   -1,  145,  276,  147,   59,   -1,   61,   -1,  256,
  257,   -1,   -1,  260,   -1,  123,   -1,  125,   44,   -1,
  267,  164,  269,  256,  257,   -1,  273,  260,   -1,  276,
   -1,   -1,   -1,   59,  267,   -1,  269,  270,   -1,  125,
  273,   -1,    0,  276,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   59,   -1,   -1,   -1,   -1,  256,  257,   -1,   -1,
  260,  261,  262,  263,  264,  265,  266,  267,  268,  269,
  270,  125,  272,  273,   -1,   -1,  276,   -1,   -1,   -1,
  256,  257,   -1,   -1,  260,  261,  262,  263,  264,  265,
  266,  267,  268,  269,  270,   -1,  272,  273,   -1,  125,
  276,   59,  256,  257,    0,   -1,  260,  261,  262,  263,
  264,  265,  266,  267,  268,  269,  270,  125,  272,  273,
   -1,   -1,  276,   -1,  256,  257,    0,   -1,  260,  261,
  262,  263,  264,  265,  266,  267,  268,  269,  270,   -1,
  272,  273,   -1,   -1,  276,   41,   -1,  123,  256,  257,
    0,   -1,  260,  261,  262,  263,  264,  265,  266,  267,
  268,  269,  270,   59,  272,  273,   -1,  125,  276,   -1,
  256,  257,   -1,   -1,  260,  261,  262,   -1,   -1,   -1,
    0,  267,  268,  269,  270,   59,   -1,  273,   -1,   -1,
  276,   -1,   -1,  123,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,  256,  257,   -1,   -1,  260,  261,  262,   59,
   -1,   -1,   -1,  267,  268,  269,  270,   -1,   -1,  273,
   -1,   -1,  276,   -1,   -1,   -1,   -1,  123,   -1,  125,
  256,  257,   -1,   -1,  260,  261,  262,   -1,   -1,   59,
   -1,  267,  268,  269,  270,   -1,   -1,  273,  256,  257,
  276,  125,  260,  261,  262,   -1,   -1,   23,   24,  267,
  268,  269,  270,   -1,   -1,  273,   -1,   -1,  276,   -1,
   -1,   -1,   38,   -1,   -1,  125,   -1,   -1,   -1,   -1,
  256,  257,   48,   -1,  260,   -1,  262,   -1,   -1,   -1,
   -1,  267,   -1,  269,   -1,   -1,   -1,  273,  256,  257,
  276,   -1,  260,  261,  262,  125,   -1,   -1,   74,  267,
  268,  269,  270,   -1,   -1,  273,   -1,   -1,  276,   -1,
   -1,   -1,   -1,   -1,    1,    2,  256,  257,   -1,   -1,
  260,   97,  262,   -1,   11,   12,  102,  267,   -1,  269,
   -1,   -1,   -1,  273,   -1,   22,  276,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   35,   -1,
  256,  257,   39,   -1,  260,  261,  262,   -1,   -1,   -1,
   -1,  267,  268,  269,  270,   -1,   -1,  273,   -1,   -1,
  276,   -1,  256,  257,   -1,   -1,  260,  261,  262,   -1,
   -1,   -1,   -1,  267,  268,  269,  270,   -1,   -1,  273,
   -1,   -1,  276,   -1,   81,   -1,  256,  257,   -1,   -1,
  260,  261,  262,   -1,   -1,   -1,   -1,  267,  268,  269,
  270,   -1,  188,  273,  123,   -1,  276,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,  256,  257,  123,   -1,
  260,  261,  262,   -1,    6,   -1,   -1,  267,  268,  269,
  270,   -1,   -1,  273,  125,   -1,  276,   19,   -1,   -1,
   -1,   23,   24,   -1,   -1,   27,   -1,   -1,  125,   -1,
   -1,    6,   -1,   -1,   -1,   -1,   38,   -1,   -1,   -1,
   -1,   -1,   -1,  125,   -1,   47,   48,   -1,   23,   24,
   -1,   -1,   27,   -1,   -1,  172,   -1,  125,  175,   61,
   62,   63,   64,   38,  181,   -1,  183,   -1,   -1,   -1,
   72,   73,   74,   48,   -1,   77,   78,   79,   -1,  125,
   -1,   83,   -1,   -1,  125,   -1,   61,   62,   63,   64,
   -1,   -1,   -1,   -1,   -1,   97,   -1,   72,   73,   74,
  102,   -1,   77,   78,   79,   -1,   -1,   -1,   83,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,  256,  257,   -1,
   -1,  260,   97,   -1,   -1,  127,  128,  102,  267,   -1,
  269,  256,  257,   -1,  273,  260,   -1,  276,   -1,   -1,
   -1,   -1,  267,   -1,  269,  256,  257,   -1,  273,  260,
   -1,  276,  127,  128,   -1,   -1,  267,   -1,  269,  256,
  257,   -1,  273,  260,   -1,  276,   -1,   -1,   -1,   -1,
  267,   -1,  269,   -1,  256,  257,  273,   -1,  260,  276,
   -1,   -1,   -1,   -1,   -1,  267,  188,  269,  256,  257,
   -1,  273,  260,   -1,  276,   -1,   -1,   -1,   -1,  267,
   -1,  269,   -1,   -1,   -1,  273,   -1,   -1,  276,   -1,
  256,  257,   -1,  188,  260,  256,  257,   -1,   -1,  260,
   -1,  267,   -1,  269,   -1,   -1,  267,  273,  269,   -1,
  276,   -1,  273,  256,  257,  276,   -1,  260,   -1,   -1,
   -1,   -1,   -1,   -1,  267,  268,  269,   -1,   -1,   -1,
  273,   -1,   -1,  276,
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
"termino : termino '*' error",
"termino : termino '/' error",
"termino : error '*' factor",
"termino : error '/' factor",
"factor : identificador",
"factor : CTE",
"factor : funcion",
"listapinvocacion : listapinvocacion ',' pinvocacion",
"listapinvocacion : pinvocacion",
"pinvocacion : expresion FLECHA ID",
"pinvocacion : TRUNC '(' expresion ')' FLECHA ID",
"pinvocacion : '(' tipo ID ')' '{' listasentencia '}'",
};

//#line 153 ".\gramatica2.y"

private void yyerror(String s) {
    System.out.println("Error: " + s);
}

public Parser(Map<String, Simbolo> tablaSimbolos, String path)
{
  this.tablaSimbolos = tablaSimbolos;
  this.lexer = new Lexico(tablaSimbolos);
  this.lexer.cargarBuffer(path);
}
//#line 606 "Parser.java"
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
        yychar = lexer.yylex(this.yylval);   //get next token
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
//#line 12 ".\gramatica2.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el nombre del programa");}
break;
case 3:
//#line 13 ".\gramatica2.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta llave de cierre del cuerpo del programa");}
break;
case 4:
//#line 14 ".\gramatica2.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta la llave de apertura del cuerpo del programa");}
break;
case 5:
//#line 15 ".\gramatica2.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": faltan llaves que encierren el cuerpo del programa");}
break;
case 6:
//#line 18 ".\gramatica2.y"
{System.out.println("Se detectó una lista de sentencias seguida de una sentencia");}
break;
case 7:
//#line 19 ".\gramatica2.y"
{System.out.println("Se detectó una sentencia");}
break;
case 8:
//#line 20 ".\gramatica2.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta punto y coma al final de la lista de sentencias con otra sentencia");}
break;
case 9:
//#line 21 ".\gramatica2.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta punto y coma al final de la sentencia");}
break;
case 13:
//#line 27 ".\gramatica2.y"
{System.out.println("Se detectó un print");}
break;
case 14:
//#line 28 ".\gramatica2.y"
{System.out.println("Se obtuvo una sentencia desde la invocación de una función");}
break;
case 15:
//#line 31 ".\gramatica2.y"
{System.out.println("Se detectó una declaración de variables");}
break;
case 16:
//#line 32 ".\gramatica2.y"
{System.out.println("Se detectó una declaración de función");}
break;
case 17:
//#line 35 ".\gramatica2.y"
{System.out.println("Se detectó una asignación simple");}
break;
case 18:
//#line 36 ".\gramatica2.y"
{System.out.println("Se detectó una asignación múltiple");}
break;
case 21:
//#line 41 ".\gramatica2.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta coma entre constantes");}
break;
case 25:
//#line 49 ".\gramatica2.y"
{System.out.println("Se detectó un bloque IF-ELSE");}
break;
case 26:
//#line 50 ".\gramatica2.y"
{System.out.println("Se detectó un bloque IF");}
break;
case 27:
//#line 51 ".\gramatica2.y"
{System.out.println("Se detectó un bloque DO UNTIL");}
break;
case 28:
//#line 53 ".\gramatica2.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta del parentesis de cierre de la condicion del bloque IF");}
break;
case 29:
//#line 54 ".\gramatica2.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta del parentesis de apertura de la condición del bloque IF");}
break;
case 30:
//#line 55 ".\gramatica2.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta de parentesis de la condicion del bloque IF");}
break;
case 31:
//#line 56 ".\gramatica2.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta del parentesis de apertura de la condicion del bloque IF");}
break;
case 32:
//#line 57 ".\gramatica2.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta del parentesis de cierre de la condición del bloque IF");}
break;
case 33:
//#line 58 ".\gramatica2.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta de ambos parentesis de la condicion del bloque IF");}
break;
case 34:
//#line 59 ".\gramatica2.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta la palabra ENDIF al final del bloque IF");}
break;
case 35:
//#line 60 ".\gramatica2.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta la palabra ENDIF al final del bloque IF");}
break;
case 36:
//#line 61 ".\gramatica2.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el cuerpo en el bloque ELSE");}
break;
case 37:
//#line 62 ".\gramatica2.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el cuerpo en el bloque IF");}
break;
case 38:
//#line 63 ".\gramatica2.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el cuerpo en el bloque IF");}
break;
case 39:
//#line 64 ".\gramatica2.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el cuerpo en los bloques IF y ELSE");}
break;
case 40:
//#line 65 ".\gramatica2.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el parentesis de apertura de la condición del bloque DO UNTIL");}
break;
case 41:
//#line 66 ".\gramatica2.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el parentesis de apertura de la condición del bloque DO UNTIL");}
break;
case 42:
//#line 67 ".\gramatica2.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta de ambos parentesis en la condición del bloque DO UNTIL");}
break;
case 43:
//#line 68 ".\gramatica2.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta de cuerpo en iteracion");}
break;
case 44:
//#line 69 ".\gramatica2.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta la palabra UNTIL en la iteracion");}
break;
case 47:
//#line 74 ".\gramatica2.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta expresión o cadena en el print");}
break;
case 48:
//#line 77 ".\gramatica2.y"
{System.out.println("Se detectó una invocación a función");}
break;
case 49:
//#line 78 ".\gramatica2.y"
{System.out.println("Se detectó una invocación a función");}
break;
case 50:
//#line 79 ".\gramatica2.y"
{System.out.println("Se detectó una invocación a función");}
break;
case 51:
//#line 80 ".\gramatica2.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el nombre de la funcion");}
break;
case 52:
//#line 81 ".\gramatica2.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el nombre de la funcion");}
break;
case 53:
//#line 82 ".\gramatica2.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el nombre de la funcion");}
break;
case 74:
//#line 122 ".\gramatica2.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando derecho de la suma");}
break;
case 75:
//#line 123 ".\gramatica2.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando derecho de la resta");}
break;
case 76:
//#line 124 ".\gramatica2.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando izquierdo de la suma");}
break;
case 77:
//#line 125 ".\gramatica2.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando izquierdo de la resta");}
break;
case 81:
//#line 132 ".\gramatica2.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando derecho de la multiplicación");}
break;
case 82:
//#line 133 ".\gramatica2.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando derecho de la división");}
break;
case 83:
//#line 134 ".\gramatica2.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando izquierdo de la multiplicación");}
break;
case 84:
//#line 135 ".\gramatica2.y"
{System.out.println("Error sintáctico en línea " + this.yylval.ival + ": falta el operando izquierdo de la división");}
break;
//#line 955 "Parser.java"
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
        yychar = lexer.yylex(this.yylval);       //get next character
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
