grammar obp;

options {
    backtrack=true;
    memoize=true;
    output=AST;
}

@header {
package obp;
}

@members {

public String getErrorHeader(RecognitionException e) {
    return getSourceName()+":"+e.line+":"+(e.charPositionInLine+1)+":";
}
	
public String getErrorMessage(RecognitionException e, String[] tokenNames) {
    List stack = getRuleInvocationStack(e, this.getClass().getName());
    String msg = null;
    if (e instanceof NoViableAltException) {
        NoViableAltException nvae = (NoViableAltException) e;
        msg = " no viable alt; token="+e.token+
              " (decision="+nvae.decisionNumber+
              " state "+nvae.stateNumber+")"+
              " decision=<<"+nvae.grammarDecisionDescription+">>";
    }
    else {
        msg = super.getErrorMessage(e, tokenNames);
    }
    return stack+" "+msg;
}
	    
public String getTokenErrorDisplay(Token t) {
    return t.toString();
}

}

@lexer::header {
package bp;
}

@lexer::members {
public String getErrorHeader(RecognitionException e) {
    return getSourceName()+":"+e.line+":"+(e.charPositionInLine+1)+":";
}

}

compilationUnit
	: packageDeclaration
	 (importDeclaration)*
	 (typeDeclaration)? 
;

packageDeclaration
	: 'app' qualifiedName EOL
;

importDeclaration
	: 'import' qualifiedName EOL
;

qualifiedName
	: ID ('.' ID)*
;

typeDeclaration
	: boDeclaration
	| bpDeclaration
;

boDeclaration
	: 'bo' ID ('extends' ID)? EOL
	   boBody
	  'end'
;

bpDeclaration
	: 'bp' ID ('::' ID)? EOL
	   bpBody
	  'end'
;

boBody
	: (fieldDeclaration)*
;

bpBody
	: (expression)*
;

fieldDeclaration
	: ID (fieldDefinition1 | fieldDefinition2 | fieldInfer)? EOL
;

fieldDefinition1
	: 'null'
;

fieldDefinition2
	: 'default' expression
;

fieldInfer
	: '=' expression
;

expression
	: boolExpression ('=' expression)? ('if' boolExpression)? ('for' idEnumeration)?
; 

idEnumeration
	: '[' ID (',' ID)* ']'
;

boolExpression
	: orExpression ('?' expression ':' boolExpression)?
;

orExpression
	: andExpression ('or' andExpression)*
;

andExpression
	: compareExpression ('and' compareExpression)*
;

compareExpression
	: additiveExpression (compareOp additiveExpression)*
;

compareOp
	: '<='
	| '>='
	| '<'
	| '>'
	| '=='
	| '!='
;

additiveExpression
	: multiplicativeExpression (additiveOp multiplicativeExpression)*
;

additiveOp
	: '+'
	| '-'
;
	
multiplicativeExpression
	: unaryExpression (multiplicativeOp unaryExpression)*
;

multiplicativeOp
	: '*'
	| '/'
	| '%' 
;

unaryExpression
	: '-' unaryExpression
	| unaryExpressionNotPlusMinus
;
	
unaryExpressionNotPlusMinus
	: '!' unaryExpression
	| primary
;

primary 
    :  '(' expression ')'           
    |   idAbsPath (arguments)?
    |   literal
;

idAbsPath
	: idWithIdentifier ('.' idWithIdentifier)*
;

idWithIdentifier
	: ID (identifierSuffix)?
;

identifierSuffix 
    : '[' expression ']'
;

arguments
    : '(' (expressionList)? ')'
;

expressionList 
    : expressionWithAssign (',' expressionWithAssign)*
;

expressionWithAssign
	: ID '=' expression EOL
;

literal 
    : INT
    | FLOAT
    | STRING
;

ID	: (UNICODE_ESC|'a'..'z'|'A'..'Z'|'_') (UNICODE_ESC|'a'..'z'|'A'..'Z'|'0'..'9'|'_')*
;

INT	: '0'..'9'+
;

FLOAT
    : ('0'..'9')+ '.' ('0'..'9')* EXPONENT?
    | '.' ('0'..'9')+ EXPONENT?
    | ('0'..'9')+ EXPONENT
;
    
COMMENT
    : '//' ~('\n'|'\r')* '\r'? '\n' {$channel=HIDDEN;}
;

WS
    : ( ' ' | '\t' ) {$channel=HIDDEN;}
;

EOL	: ('\r')? '\n'
    ;

STRING
    : '"' ( ESC_SEQ | ~('\\'|'"') )* '"'
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