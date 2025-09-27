





//#line 2 ".\gramatica.y"
    import java.io.*;
    //import Lex.Lex;

    import java.util.HashMap;
    import java.util.Map;
    import Simbolos.*;

//#line 20 "Parser.java"




public class Parser
{

boolean yydebug;        //do I want debug output?
int yynerrs;            //number of errors so far
int yyerrflag;          //was there an error?
int yychar;             //the current working character

Lexico lexer;
Map<String, Simbolo> tablaSimbolos;
String path;

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
public final static short LAMBDA=277;
public final static short YYERRCODE=256;
final static short yylhs[] = {                           -1,
    0,    1,    1,    2,    2,    2,    2,    2,    4,    4,
    3,    3,    3,    5,    5,    5,    6,    6,   13,   14,
   14,   14,   14,   14,   14,    8,    8,    9,    9,   10,
   10,   15,   15,   11,   11,   11,   12,   12,   16,   16,
   16,   17,   17,   17,    7,    7,    7,   18,   18,   19,
   19,   19,
};
final static short yylen[] = {                            2,
    4,    3,    2,    1,    1,    1,    1,    1,    2,   13,
    3,    5,    3,   13,    9,    8,    4,    4,    3,    1,
    1,    1,    1,    1,    1,    1,    1,    3,    1,    3,
    1,    3,    2,    3,    3,    1,    3,    1,    3,    3,
    1,    1,    1,    1,    4,    4,    4,    3,    1,    3,
    6,    7,
};
final static short yydefred[] = {                         0,
    0,    0,    0,    0,    0,   26,    0,    0,   27,    0,
    0,    4,    5,    6,    7,    8,    0,    0,    0,    0,
    0,    0,    0,    0,    1,    0,    3,    0,    0,    0,
    0,    0,   43,   44,    0,    0,   41,    0,    0,    0,
    0,    0,    0,   49,    0,    0,    0,    0,    0,    0,
    2,    0,    0,    0,   28,    0,    0,    0,    0,   46,
   47,    0,    0,    0,   45,    0,    0,   22,   23,   24,
   25,   20,   21,    0,    0,    0,   18,   17,    0,    0,
    0,   31,    0,    0,    0,   39,   40,    0,    0,   50,
   48,    0,    0,    0,    0,    0,   33,    0,    0,    0,
    0,    0,    0,    0,   32,    0,   30,    0,    0,    0,
    0,    0,   51,    0,    0,   16,    0,   52,    0,   15,
    0,    0,    0,    0,    0,    0,    0,   14,   10,
};
final static short yydgoto[] = {                          2,
   10,   11,   12,   13,   14,   15,   34,   17,   18,   81,
   42,   54,   47,   74,   82,   36,   37,   43,   44,
};
final static short yysindex[] = {                      -253,
 -109,    0, -186,   20,  -12,    0,  -84,   39,    0, -124,
   36,    0,    0,    0,    0,    0, -146,   11, -225,  -40,
 -143, -225, -186, -181,    0,   68,    0,   89,   97, -225,
 -122,  105,    0,    0,   78,   63,    0,   91,  107,  112,
 -165,    8,   23,    0, -101,   18,  115, -103,  118,   74,
    0, -171,   78,  116,    0, -225, -225, -225, -225,    0,
    0, -225,  -95,  -94,    0,  -38, -225,    0,    0,    0,
    0,    0,    0, -225,  -97, -105,    0,    0, -165,  -89,
   24,    0, -225,   63,   63,    0,    0,   79,  128,    0,
    0,   78,   78,   47,  133,  -80,    0,   57, -171,   78,
  -92,   59, -186, -225,    0, -186,    0,  -74, -186,  -82,
  143, -170,    0,  -81, -125,    0,  145,    0,   67,    0,
 -225, -186,   85,  -63,  132,  -65,   75,    0,    0,
};
final static short yyrindex[] = {                         0,
    0,    0,    0,   48,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,  -34,  140,    0,
    0,  -36,    0,    0,  144,  -25,    0,  -30,    7,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,  -23,  147,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,  -14,   -3,    0,    0,    0,    0,    0,
    0,  150,  161,    0,    0,    0,    0,    0,    0,   49,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,
};
final static short yygindex[] = {                         0,
  -20,   37,    0,    0,    0,    0,  198,   17,  187,    0,
   51,    0,  106,    0,  113,   83,   84,    0,  149,
};
final static int YYTABLESIZE=322;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         41,
   25,   41,   48,    1,   42,   42,   42,   42,   42,   29,
   42,   42,   42,    3,   42,   36,   42,   36,   36,   36,
   38,   76,   42,   42,   29,   42,   34,   22,   34,   34,
   34,   32,   33,   36,   36,   38,   36,   35,   23,   35,
   35,   35,  115,  118,   34,   34,   26,   34,   43,   43,
   56,   43,   57,   43,   31,   35,   35,   63,   35,   20,
   56,  126,   57,   65,   98,   21,   66,   99,   80,   35,
    4,   30,   46,    5,   50,   32,   33,   73,   24,   72,
   53,    6,  110,    7,   26,  112,    4,    8,  114,    5,
    9,   29,   37,   49,   27,   96,    6,    6,  117,    7,
   79,  124,    6,    8,   58,    9,    9,   37,   29,   59,
   28,    9,   88,   45,   78,   80,   56,   92,   57,  101,
   56,   56,   57,   57,   93,  125,   51,   56,   52,   57,
   20,   60,    4,  100,   55,    5,  119,  120,   84,   85,
   31,   86,   87,    6,   20,    7,   26,   61,   26,    8,
   26,   62,    9,    4,   46,   75,    5,   67,   77,   83,
   26,   89,   90,   94,    6,   95,    7,   97,  102,  103,
    8,  123,  104,    9,    4,    4,  105,    5,    5,  106,
  108,  109,  113,  116,  121,    6,    6,    7,    7,  122,
  127,    8,    8,    4,    9,    9,    5,  128,    9,  129,
   16,   19,   11,   29,    6,   13,    7,   16,   12,  111,
    8,  107,    0,    9,   91,    0,   38,   39,   32,   33,
   16,    0,    0,    0,    0,    0,    0,   42,   42,   42,
   42,    0,    0,    0,    0,   40,   42,   40,   36,   36,
   36,   36,   42,    0,    0,   16,    0,   36,    0,   34,
   34,   34,   34,    0,    0,    0,    0,    0,   34,    0,
   35,   35,   35,   35,    0,    0,    0,    0,    0,   35,
    0,    0,    0,    0,    0,    0,    0,    0,   19,   43,
   64,   68,   69,   70,   71,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
   16,    0,    0,   16,    0,    0,   16,   16,    0,   16,
    0,   16,    0,    0,    0,    0,    0,    0,    0,   16,
    0,   16,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         40,
  125,   40,   23,  257,   41,   42,   43,   44,   45,   44,
   47,   42,   43,  123,   45,   41,   47,   43,   44,   45,
   44,  125,   59,   60,   59,   62,   41,   40,   43,   44,
   45,  257,  258,   59,   60,   59,   62,   41,  123,   43,
   44,   45,  125,  125,   59,   60,   10,   62,   42,   43,
   43,   45,   45,   47,   44,   59,   60,   41,   62,   40,
   43,  125,   45,   41,   41,   46,   44,   44,   52,   19,
  257,   61,   22,  260,   24,  257,  258,   60,   40,   62,
   30,  268,  103,  270,   48,  106,  257,  274,  109,  260,
  277,   44,   44,  275,   59,   79,  268,  268,  269,  270,
  272,  122,  268,  274,   42,  277,  277,   59,   61,   47,
  257,  277,   62,  257,   41,   99,   43,   67,   45,   41,
   43,   43,   45,   45,   74,   41,   59,   43,   40,   45,
   40,   41,  257,   83,  257,  260,  262,  263,   56,   57,
   44,   58,   59,  268,   40,  270,  110,   41,  112,  274,
  114,   40,  277,  257,  104,   41,  260,  259,   41,   44,
  124,  257,  257,  261,  268,  271,  270,  257,   41,  123,
  274,  121,   40,  277,  257,  257,  257,  260,  260,  123,
  273,  123,  257,   41,   40,  268,  268,  270,  270,  123,
   59,  274,  274,  257,  277,  277,  260,  263,   59,  125,
    3,   41,   59,   17,  268,   59,  270,   10,   59,  104,
  274,   99,   -1,  277,   66,   -1,  257,  258,  257,  258,
   23,   -1,   -1,   -1,   -1,   -1,   -1,  264,  265,  266,
  267,   -1,   -1,   -1,   -1,  276,  273,  276,  264,  265,
  266,  267,  273,   -1,   -1,   48,   -1,  273,   -1,  264,
  265,  266,  267,   -1,   -1,   -1,   -1,   -1,  273,   -1,
  264,  265,  266,  267,   -1,   -1,   -1,   -1,   -1,  273,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  259,  273,
  273,  264,  265,  266,  267,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
  103,   -1,   -1,  106,   -1,   -1,  109,  110,   -1,  112,
   -1,  114,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  122,
   -1,  124,
};
}
final static short YYFINAL=2;
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
null,null,null,null,null,null,null,"ID","CTE","ASIG","IF","THEN","ELSE","ENDIF",
"MAYORIG","MENORIG","IGUAL","DISTINTO","LONG","RETURN","DO","UNTIL","CV",
"FLECHA","PRINT","CADENAML","TRUNC","LAMBDA",
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
"tipo : LAMBDA",
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
"funcion : ID '(' ID ')'",
"funcion : ID '(' CTE ')'",
"listapinvocacion : listapinvocacion ',' pinvocacion",
"listapinvocacion : pinvocacion",
"pinvocacion : expresion FLECHA ID",
"pinvocacion : TRUNC '(' expresion ')' FLECHA ID",
"pinvocacion : '(' tipo ID ')' '{' listasentencia '}'",
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

  // INICIALIZACIÓN DEL LÉXICO Y CARGA DEL CÓDIGO //
  this.lexer = new Lexico(this.tablaSimbolos, yylval);
  lexer.cargarBuffer(path);
  // FIN //
  
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
        yychar = lexer.yylex();  //get next token
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
        yychar = lexer.yylex();        //get next character
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

// DECLARACIÓN DE YYERROR //
void yyerror(String mensaje) {
    System.err.println("Error sintáctico: " + mensaje);
}
// FIN DECLARACION DE YYERROR //

//## Constructors ###############################################
/**
 * Default constructor.  Turn off with -Jnoconstruct .

 */
public Parser(Map<String, Simbolo> tablaSimbolos, String path)
{
  this.tablaSimbolos = tablaSimbolos;
  this.path = path;
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

//################### END OF CLASS ##############################

}
