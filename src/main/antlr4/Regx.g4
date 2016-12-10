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
  : STRING Identifier stringInitializer? ';'
  ;

stringInitializer
  : '=' StringLiteral
  ;

listDeclaration
  : LIST Identifier listInitializer? ';'
  ;

listInitializer
  : '=' stringListLiteral
  ;

stringListLiteral
  : '[' stringLiteralList? ']'
  ;

stringLiteralList
  : StringLiteral (',' StringLiteral)*
  ;

regexDeclaration
  : REGEX Identifier regexInitializer? ';'
  ;

regexInitializer
  : '=' RegexLiteral
  ;

functionDeclaration
  : 'function' returnType Identifier '(' formalParameterList? ')' block
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
  : typeName Identifier
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
  : qualifiedName
  | literal
  | functionCall
  | assignment
  ;

qualifiedName
  : Identifier ('.' Identifier)*
  ;

literal
  : stringLiteral
  | regexLiteral
  | stringListLiteral
  ;

functionCall
  : qualifiedName '(' argumentList? ')';

argumentList
  : argument (',' argument)*
  ;

argument
  : expression
  ;

assignment
  : <assoc=right> qualifiedName '=' expression
  ;

forLoop
  : FOR '(' Identifier ':' expression ')' block
  ;

returnStatement
  : RETURN expression;

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
  :  [ \t\r\n\u000C]+ -> skip
  ;