grammar Sq2;

@header {
  package ro.pub.cpl.sq2.hello1;
}

@lexer::header {
  package ro.pub.cpl.sq2.hello1;
}
program returns [Program p]
	@init {
	  $p = new Program();
	  }
  :
	(function { $p.add($function.f); }) +
	;

function returns [Function f]
  @init { $f = new Function(); }
  :
	name=ID {
        $f.name = $name.text;
    } (decl_args {
        $f.declArgs = $decl_args.result;
    })? ('->' NUMERIC { $f.intType = true; })?
    ':' (var_decl { $f.declLocal = $var_decl.result; })?
    statements { $f.statements=$statements.b; } END ';'
	;

var_decl returns [DeclLocal result]
    @init { result = new DeclLocal(); }
    : LOCAL (decl { result.addLocal($decl.result); })* END ';';

decl returns [LocalVar result]
    : type ID { result = new LocalVar($type.result, $ID.text); } ';';

decl_args returns [DeclArgs result]
  @init { result = new DeclArgs(); }
  : a1=decl_arg { result.addDeclArg($a1.result); }
  (',' a2=decl_arg { result.addDeclArg($a2.result); })*;

decl_arg returns [DeclArg result]
  : type ID {
        result = new DeclArg();
        result.setName($ID.text);
        result.setType($type.result);
  };

type returns [SQ2Type result]
  : NUMERIC { result = new NumberType(); }
  | NUMERIC '[' INT ']' { result = new ArrayType(Integer.parseInt($INT.text)); }
  ;

statements returns [StatementList b]
  @init {
    $b = new StatementList();
    }
	:	(assignment { $b.add($assignment.s); } ';')+
	;

assignment returns [TreeNode s]
    : left=addition { s = $left.s; } ('=' right=addition {
        s = new Assignment($left.s, $right.s);
    })?
	;

addition returns [TreeNode s]
    @init {
        boolean moreTerms = false;
    }
    : left=mult { s = $left.s; }
    ((('+') => ('+' right_plus=mult) {
        if(moreTerms == false) {
            s = new Addition(Term.positive($left.s));
            moreTerms = true;
        }
        ((Addition)s).addTerm(Term.positive($right_plus.s));
    }) | (('-') => ('-' right_minus=mult) {
        if(moreTerms == false) {
            s = new Addition(Term.positive($left.s));
            moreTerms = true;
        }
        ((Addition)s).addTerm(Term.negative($right_minus.s));
    }))*;

mult returns [TreeNode s]
    @init {
        boolean moreFactors = false;
    }
    : left=negation { s = $left.s; }
    ((('*') => ('*' right_mul=negation) {
        if(moreFactors == false) {
            s = new Multiplication(Factor.positive($left.s));
            moreFactors = true;
        }
        ((Multiplication)s).addFactor(Factor.positive($right_mul.s));
    }) | (('/') => ('/' right_div=negation) {
        if(moreFactors == false) {
            s = new Multiplication(Factor.positive($left.s));
            moreFactors = true;
        }
        ((Multiplication)s).addFactor(Factor.negative($right_div.s));
    }))*;

negation returns [TreeNode s]
    : '-' atom { s = new Negation($atom.s); }
    | atom { s = $atom.s; };

atom returns [TreeNode s]
    : ID { s = new Instantiation($ID.text); }
        ('[' addition { ((Instantiation)s).index = $addition.s; } ']')?
    | INT { s = new IntConstant(Integer.parseInt($INT.text)); }
    | '(' addition ')' { s = $addition.s; }
    | '[' call_fun ']' { s = $call_fun.s; }
    | RETURN addition { s = new Return($addition.s); }
    | IF c=condition { s = new IfBranching($c.result); }
        ';' (sif=statements { ((IfBranching)s).if_statements = $sif.b; })?
        (ELSE (selse=statements { ((IfBranching)s).else_statements = $selse.b; } )?)? END
    | WHILE condition { s = new WhileCycle($condition.result); }
        ';' statements { ((WhileCycle)s).statements = $statements.b; } END
    | UNTIL condition { s = new UntilCycle($condition.result); }
        ';' statements { ((UntilCycle)s).statements = $statements.b; } END
    ;

condition returns [Condition result]
    @init { result = new Condition(); }
    :
    left=addition { result.left = $left.s; }
    (
        '<' { result.cType = ConditionType.LESS; } |
        '=' { result.cType = ConditionType.EQUAL; } |
        '>' { result.cType = ConditionType.GREATER; }
    )
    right=addition { result.right = $right.s; }
    ;

call_fun returns [TreeNode s]
    : PRINT STRING { s = new PrintStatement(LexerUtils.unquote($STRING.text)); }
    | name=ID {
            s = new FunctionCall();
            ((FunctionCall)s).setName($name.text);
        } (args=call_args {
            ((FunctionCall)s).setCallArgs($args.result);
        } )?
	;

call_args returns [CallArgs result]
  @init {
    result = new CallArgs();
  }
    : s1=addition {
            result.addArg($s1.s);
        } (',' s2=addition {
            result.addArg($s2.s);
        } )*;

UNTIL           :   'until';
ELSE            :   'else';
IF              :   'if';
WHILE           :   'while';
RETURN          :   'return';
NUMERIC         :   'number';
PRINT           :   'print';
LOCAL           :   'local';
END             :   'end';
ID              :   ('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'_')*;
INT             :   '0'..'9'+;
COMMENT         :   '#' ~('\n'|'\r')* '\r'? '\n' {$channel=HIDDEN;};
WS              :   (' ' | '\t' | '\r' | '\n') {$channel=HIDDEN;} ;
STRING          :  '"' ( ESC_RULE | ~('\\'|'"')  )* '"';

fragment
ESC_RULE        :	'\\' ('n' | '"' | '\\');
