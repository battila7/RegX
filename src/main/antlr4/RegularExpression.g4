grammar RegularExpression;

start
  : regex EOF
  ;

regex
  : concatenation (UNION concatenation)*
  ;

concatenation
  : term*
  ;

term
  : atom      #literalTerm
  | atom STAR #closureTerm
  ;

atom
  : group
  | any
  | regexCharacter
  ;

group
  : '(' regex ')'
  ;

any
  : '.'
  ;

regexCharacter
  : RegexChar
  ;

RegexChar
  : UnescapedRegexChar
  | EscapedRegexChar
  ;

fragment
UnescapedRegexChar
  : ~[ "'\\\r\n/\|\+*()]
  ;

fragment
EscapedRegexChar
  : '\\' [\\/'"|+*()]
  ;

UNION : '+';
STAR  : '*';