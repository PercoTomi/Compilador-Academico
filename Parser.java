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






//#line 2 ".\gramatica.y"
    import java.io.*;
    import Lex.Lex;
//#line 20 "Parser.java"




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
public final static short THEN=261;
public final static short ELSE=262;
public final static short ENDIF=263;
public final static short MAYORIG=264;
public final static short MENORIG=265;
public final static short IGUAL=266;
public final static short DISTINTO=267;
public final static short LONG=268;
public final static short RETURN=269;
public final static short DO=270;
public final static short UNTIL=271;
public final static short CV=272;
public final static short FLECHA=273;
public final static short PRINT=274;
public final static short CADENAML=275;
public final static short TRUNC=276;
public final static short YYERRCODE=256;
final static short yylhs[] = {                           -1,
    0,    1,    1,    2,    2,    2,    2,    2,    4,    4,
    3,    3,    3,    5,    5,    5,    6,    6,   13,   14,
   14,   14,   14,   14,   14,    8,    9,    9,   10,   10,
   15,   15,   11,   11,   11,   12,   12,   16,   16,   16,
   17,   17,   17,    7,   18,   18,   19,   19,
};
final static short yylen[] = {                            2,
    4,    3,    2,    1,    1,    1,    1,    1,    2,   13,
    3,    5,    3,   13,    9,    8,    4,    4,    3,    1,
    1,    1,    1,    1,    1,    1,    3,    1,    3,    1,
    3,    2,    3,    3,    1,    3,    1,    3,    3,    1,
    1,    1,    1,    4,    3,    1,    3,    6,
};
final static short yydefred[] = {                         0,
    0,    0,    0,    0,    0,   26,    0,    0,    0,    0,
    4,    5,    6,    7,    8,    0,    0,    0,    0,    0,
    0,    0,    0,    1,    0,    3,    0,    0,    0,    0,
    0,   42,   43,    0,    0,   40,    0,    0,    0,   46,
    0,    0,    0,    0,    0,    0,    2,    0,    0,    0,
   27,    0,    0,    0,    0,    0,    0,   44,    0,    0,
   22,   23,   24,   25,   20,   21,    0,    0,    0,   18,
   17,    0,    0,    0,   30,    0,    0,    0,   38,   39,
    0,   47,   45,    0,    0,    0,    0,    0,   32,    0,
    0,    0,    0,    0,    0,   31,    0,   29,    0,    0,
    0,    0,   48,    0,   16,    0,    0,   15,    0,    0,
    0,    0,    0,    0,    0,   14,   10,
};
final static short yydgoto[] = {                          2,
    9,   10,   11,   12,   13,   14,   33,   16,   17,   74,
   38,   50,   43,   67,   75,   35,   36,   39,   40,
};
final static short yysindex[] = {                      -235,
  -61,    0, -170,   -1,   24,    0,  -50,   46, -114,   33,
    0,    0,    0,    0,    0, -163,   19, -138, -204, -155,
 -138, -170, -201,    0,   57,    0,   86,   85, -138, -127,
   95,    0,    0,   25,   -4,    0,   96,  -28,   73,    0,
 -119,  -18,  100, -109,  101,   50,    0, -165,   25,  106,
    0, -138, -138, -138, -138, -138, -113,    0, -204, -138,
    0,    0,    0,    0,    0,    0, -138, -116, -118,    0,
    0, -111, -105,   74,    0, -138,   -4,   -4,    0,    0,
   56,    0,    0,   25,   25,   32,  118,  -95,    0,   40,
 -165,   25, -107, -170, -138,    0, -170,    0,  -93,  -90,
  128, -191,    0, -141,    0,  132,   51,    0, -138, -170,
   65,  -89,  114,  -88,   52,    0,    0,
};
final static short yyrindex[] = {                         0,
    0,    0,    0,   27,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,   16,  117,    0,    0,
  -41,    0,    0,  123,  -36,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,   17,  124,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,  -31,  -11,    0,    0,
    0,    0,    0,  127,  146,    0,    0,    0,    0,    0,
    0,   23,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,
};
final static short yygindex[] = {                         0,
   15,   11,    0,    0,    0,    0,   37,   -7,  172,    0,
   29,    0,   94,    0,   99,   75,   78,    0,  133,
};
final static int YYTABLESIZE=262;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         41,
   41,   41,   41,   41,   35,   41,   35,   35,   35,   33,
   24,   33,   33,   33,   52,   69,   53,   41,   41,   25,
   41,    1,   35,   35,   52,   35,   53,   33,   33,   34,
   33,   34,   34,   34,  104,  114,   44,   54,   19,   15,
   73,   66,   55,   65,   20,   15,   34,   34,   34,   42,
   34,   46,   31,   32,   25,   31,   32,   49,   15,   28,
   37,    3,   30,   21,   88,    4,   36,   52,    5,   53,
   28,   37,   22,   45,   28,   37,    6,  106,    7,   29,
   15,   36,    8,   73,   81,   23,    4,   28,   84,    5,
   71,   26,   52,   27,   53,   85,   93,    6,   52,    7,
   53,   41,    6,    8,   92,  113,   72,   52,  100,   53,
   25,  102,   25,   58,   90,   47,   59,   91,   31,   32,
  107,  108,   25,   42,  112,   48,   77,   78,   30,   51,
   15,   79,   80,   15,   19,   56,   15,  111,   15,   60,
   68,   70,    4,   82,   86,    5,   15,    4,   15,   76,
    5,   89,   87,    6,   94,    7,    6,   95,    6,    8,
    7,   96,   97,  103,    8,   99,    4,    4,  105,    5,
    5,  109,  115,  110,  116,    9,  117,    6,    6,    7,
    7,   11,   13,    8,    8,   12,   19,   28,  101,   98,
    0,   83,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,   41,   41,   41,   41,    0,   35,   35,   35,
   35,   41,   33,   33,   33,   33,   35,    0,    0,    0,
    0,   33,    0,    0,   57,   61,   62,   63,   64,    0,
    0,    0,   34,   34,   34,   34,    0,   18,    0,    0,
    0,   34,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         41,
   42,   43,   44,   45,   41,   47,   43,   44,   45,   41,
  125,   43,   44,   45,   43,  125,   45,   59,   60,    9,
   62,  257,   59,   60,   43,   62,   45,   59,   60,   41,
   62,   43,   44,   45,  125,  125,   22,   42,   40,    3,
   48,   60,   47,   62,   46,    9,   18,   59,   60,   21,
   62,   23,  257,  258,   44,  257,  258,   29,   22,   44,
   44,  123,   44,   40,   72,  257,   44,   43,  260,   45,
   44,  276,  123,  275,   59,   59,  268,  269,  270,   61,
   44,   59,  274,   91,   56,   40,  257,   61,   60,  260,
   41,   59,   43,  257,   45,   67,   41,  268,   43,  270,
   45,  257,  268,  274,   76,   41,  272,   43,   94,   45,
  100,   97,  102,   41,   41,   59,   44,   44,  257,  258,
  262,  263,  112,   95,  110,   40,   52,   53,   44,  257,
   94,   54,   55,   97,   40,   40,  100,  109,  102,  259,
   41,   41,  257,  257,  261,  260,  110,  257,  112,   44,
  260,  257,  271,  268,  123,  270,  268,   40,  268,  274,
  270,  257,  123,  257,  274,  273,  257,  257,   41,  260,
  260,   40,   59,  123,  263,   59,  125,  268,  268,  270,
  270,   59,   59,  274,  274,   59,   41,   16,   95,   91,
   -1,   59,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,  264,  265,  266,  267,   -1,  264,  265,  266,
  267,  273,  264,  265,  266,  267,  273,   -1,   -1,   -1,
   -1,  273,   -1,   -1,  273,  264,  265,  266,  267,   -1,
   -1,   -1,  264,  265,  266,  267,   -1,  259,   -1,   -1,
   -1,  273,
};
}
final static short YYFINAL=2;
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
null,null,null,null,null,null,null,"ID","CTE","ASIG","IF","THEN","ELSE","ENDIF",
"MAYORIG","MENORIG","IGUAL","DISTINTO","LONG","RETURN","DO","UNTIL","CV",
"FLECHA","PRINT","CADENAML","TRUNC",
};
final static String yyrule[] = {
"$accept : prog",
"prog : ID '{' listasentencia '}'",
"listasentencia : listasentencia sentencia ';'",
"listasentencia : sentencia ';'",
"sentencia : asignacion",
"sentencia : declaracion",
"sentencia : control",
"sentencia : print",
"sentencia : funcion",
"declaracion : tipo listavariables",
"declaracion : tipo ID '(' listaparametros ')' '{' listasentencia RETURN '(' expresion ')' ';' '}'",
"asignacion : ID ASIG expresion",
"asignacion : ID '.' ID ASIG expresion",
"asignacion : listavariables '=' listaexpresiones",
"control : IF '(' condicion ')' THEN '{' listasentencia '}' ELSE '{' listasentencia '}' ENDIF",
"control : IF '(' condicion ')' THEN '{' listasentencia '}' ENDIF",
"control : DO '{' listasentencia '}' UNTIL '(' condicion ')'",
"print : PRINT '(' expresion ')'",
"print : PRINT '(' CADENAML ')'",
"condicion : expresion comparador expresion",
"comparador : '>'",
"comparador : '<'",
"comparador : MAYORIG",
"comparador : MENORIG",
"comparador : IGUAL",
"comparador : DISTINTO",
"tipo : LONG",
"listavariables : listavariables ',' ID",
"listavariables : ID",
"listaparametros : listaparametros ',' parametro",
"listaparametros : parametro",
"parametro : CV tipo ID",
"parametro : tipo ID",
"expresion : expresion '+' termino",
"expresion : expresion '-' termino",
"expresion : termino",
"listaexpresiones : listaexpresiones ',' expresion",
"listaexpresiones : expresion",
"termino : termino '*' factor",
"termino : termino '/' factor",
"termino : factor",
"factor : ID",
"factor : CTE",
"factor : funcion",
"funcion : ID '(' listapinvocacion ')'",
"listapinvocacion : listapinvocacion ',' pinvocacion",
"listapinvocacion : pinvocacion",
"pinvocacion : expresion FLECHA ID",
"pinvocacion : TRUNC '(' expresion ')' FLECHA ID",
};

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
