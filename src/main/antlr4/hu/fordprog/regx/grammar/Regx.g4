grammar Regx;

program
  : declarationSequence? EOF
  ;

declarationSequence
  : declaration
  | declarationSequence declaration
  ;

declaration
  : variableDeclaration
  | functionDeclaration
  ;

variableDeclaration
  : stringDeclaration
  | listDeclaration
  | regexDeclaration
  ;

stringDeclaration
  : STRING identifier declarationInitializer? ';'
  ;

listDeclaration
  : LIST identifier declarationInitializer? ';'
  ;

stringListLiteral
  : '[' stringLiteralList? ']'
  ;

stringLiteralList
  : StringLiteral (',' StringLiteral)*
  ;

regexDeclaration
  : REGEX identifier declarationInitializer? ';'
  ;

declarationInitializer
  : '=' expression
  ;

functionDeclaration
  : 'function' returnType identifier '(' formalParameterList? ')' block
  ;

returnType
  : 'void'
  | STRING
  | LIST
  | REGEX
  ;

formalParameterList
  : formalParameter (',' formalParameter)*
  ;

formalParameter
  : typeName identifier
  ;

block
  : statement
  | '{' statement* '}'
  ;

statement
  : variableDeclaration
  | expression ';'
  | forLoop
  | returnStatement ';'
  ;

expression
  : identifier    #identifierExpression
  | literal       #literalExpression
  | functionCall  #functionCallExpression
  | assignment    #assignmentExpression
  ;

literal
  : stringLiteral
  | regexLiteral
  | stringListLiteral
  ;

functionCall
  : identifier '(' argumentList? ')';

argumentList
  : argument (',' argument)*
  ;

argument
  : expression
  ;

assignment
  : <assoc=right> identifier '=' expression
  ;

forLoop
  : FOR '(' identifier ':' expression ')' block
  ;

returnStatement
  : RETURN expression;

identifier
  : Identifier
  ;

typeName
  : STRING
  | LIST
  | REGEX
  ;

stringLiteral
  : StringLiteral
  ;

regexLiteral
  : RegexLiteral
  ;

StringLiteral
  : '"' StringCharacters? '"'
  ;

RegexLiteral
  : '/' StringCharacters? '/'
  ;

fragment
StringCharacters
  : StringCharacter+
  ;

fragment
StringCharacter
  : UnescapedCharacter
  | EscapedCharacter
  ;

fragment
UnescapedCharacter
  : ~[\\"'\n\r\t]
  ;

fragment
EscapedCharacter
  : '\\' [btnfr"'\\]
  ;

STRING        : 'string';
LIST          : 'list';
REGEX         : 'regex';
FOR           : 'for';
RETURN        : 'return';
FUNCTION      : 'function';

Identifier: Letter LetterOrDigit*;

fragment
Letter: [a-zA-Z$_];

fragment
LetterOrDigit: [a-zA-Z$_0-9];

WS
  :  [ \t\r\n\u000C]+ -> channel(HIDDEN)
  ;