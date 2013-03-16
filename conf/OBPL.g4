grammar OBPL;

compilationUnit
	: EOL*
	  packageDeclaration
	 (importDeclaration)*
	 (typeDeclaration)* EOF
;

packageDeclaration
	: APP qualifiedName EOL
;

importDeclaration
	: (IMPORT qualifiedName)? EOL
;

typeDeclaration
	: boDeclaration
	| bpDeclaration
;

boDeclaration
	: BO ID (EXTENDS ID)? EOL+
	  boBody
	  END EOL+
;

bpDeclaration
	: BP ID (COLONCOLON ID)? EOL+
	  bpBody
	  END EOL+
;

boBody
	: (fieldDeclaration
	|  keyDeclaration
	|  refDeclaration)+
;

fieldDeclaration
	: ID (fieldDefinition1 | fieldDefinition2 | fieldInfer)? EOL+
;

keyDeclaration
	: (PK|UK|DK) ID (COMMA ID)* EOL+
;

refDeclaration
	: ID COLON ID (IF boolExpression)? EOL+
;

bpBody
	: (statement)*
;

statement
	: expression (IF boolExpression)? (FOR idEnumeration)? EOL+
	| caseStatement
	| loopStatement
	| BREAK EOL+
	| RETURN EOL+
;

caseStatement
	: IF boolExpression EOL+
	 (statement)*
	 (ELSE IF boolExpression EOL+
	 (statement)*)*
	 (ELSE EOL+
	 (statement)*)?
	  END EOL+
;

loopStatement
	: LOOP (idAbsPath)? (IF boolExpression)? (FOR idEnumeration)? EOL+
	 (statement)*
	  END EOL+
;

qualifiedName
	: ID (DOT ID)*
;

fieldDefinition1
	: NULL
;

fieldDefinition2
	: DEFAULT expression
;

fieldInfer
	: ASSIGN boolExpression (IF boolExpression)? (FOR idEnumeration)?
;

expression
	: boolExpression (ASSIGN expression)?
; 

idEnumeration
	: ENUMBEGIN ID (COMMA ID)* ENUMEND
;

boolExpression
	: orExpression (ASK expression COLON boolExpression)?
;

orExpression
	: andExpression (OR andExpression)*
;

andExpression
	: notExpression (AND notExpression)*
;

notExpression
	: NOT? compareExpression
;

compareExpression
	: additiveExpression (compareOp additiveExpression)*
	| idAbsPath IS NOT? NULL
	| EXISTS idAbsPath
;

compareOp
	: LE | GE | LT | GT | EQ | NE
;

additiveExpression
	: multiplicativeExpression (additiveOp multiplicativeExpression)*
;

additiveOp
	: ADD | MINUS
;
	
multiplicativeExpression
	: unaryExpression (multiplicativeOp unaryExpression)*
;

multiplicativeOp
	: MULTI | DIV | MOD
;

unaryExpression
	: MINUS? primary
;

primary 
	:  PARBEGIN boolExpression PAREND           
	|  idAbsPath (arguments)?
	|  TUPLEBEGIN boolExpression? (COMMA boolExpression)* TUPLEEND
	|  literal
;

idAbsPath
	: idWithIdentifier (DOT idWithIdentifier)*
;

idWithIdentifier
	: ID (identifierSuffix)?
;

identifierSuffix 
	: ENUMBEGIN expression ENUMEND
;

arguments
	: PARBEGIN (expressionList)? PAREND
;

expressionList 
	: expressionWithAssign (COMMA EOL? expressionWithAssign)*
;

expressionWithAssign
	: ID ASSIGN expression
;

literal 
	: INT | FLOAT | STRING
;

ID
	: (UNICODE_ESC|'a'..'z'|'A'..'Z'|'_') (UNICODE_ESC|'a'..'z'|'A'..'Z'|'0'..'9'|'_')*
;

INT
	: '0'..'9'+
;

FLOAT
	: ('0'..'9')+ '.' ('0'..'9')* EXPONENT?
	| '.' ('0'..'9')+ EXPONENT?
	| ('0'..'9')+ EXPONENT
;
    
COMMENT
	: '//' ~('\n'|'\r')* {skip();}
;

WS
	: (' '|'\t') {skip();}
;

EOL
	: ('\r')? '\n'
;

STRING
	: '"' ( ESC_SEQ | ~('\\'|'"') )* '"'
;

APP
	: 'app'
;

IMPORT
	: 'import'
;

BO
	: 'bo'
;

BP
	: 'bp'
;

EXTENDS
	: 'extends'
;

COLON
	: ':'
;

COLONCOLON
	: '::'
;

IF
	: 'if'
;

ELSE
	: 'else'
;

END
	: 'end'
;

PK
	: 'pk'
;

UK
	: 'uk'
;

DK
	: 'dk'
;

COMMA
	: ','
;

FOR
	: 'for'
;

BREAK
	: 'break'
;

RETURN
	: 'return'
;

LOOP
	: 'loop'
;

DOT
	: '.'
;

ASSIGN
	: '='
;

DEFAULT
	: 'default'
;

NULL
	: 'null'
;

EQ
	: '=='
;

LT
	: '<'
;

LE
	: '<='
;

GT
	: '>'
;

GE
	: '>='
;

NE
	: '!='
;

OR
	: 'or'
;

AND
	: 'and'
;

NOT
	: 'not'
;

ASK
	: '?'
;

ENUMBEGIN
	: '['
;

ENUMEND
	: ']'
;

TUPLEBEGIN
	: '{'
;

TUPLEEND
	: '}'
;

ADD
	: '+'
;

MINUS
	: '-'
;

MULTI
	: '*'
;

DIV
	: '/'
;

MOD
	: '%'
;

PARBEGIN
	: '('
;

PAREND
	: ')'
;

IS
	: 'is'
;

EXISTS
	: 'exists'
;

fragment
EXPONENT
	: ('e'|'E') ('+'|'-')? ('0'..'9')+
;

fragment
HEX_DIGIT
	: ('0'..'9'|'a'..'f'|'A'..'F')
;

fragment
ESC_SEQ
	: '\\' ('b'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\')
	| UNICODE_ESC
	| OCTAL_ESC
;

fragment
OCTAL_ESC
	: '\\' ('0'..'3') ('0'..'7') ('0'..'7')
	| '\\' ('0'..'7') ('0'..'7')
	| '\\' ('0'..'7')
;

fragment
UNICODE_ESC
	: '\\' 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
;