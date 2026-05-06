grammar RestDSL;

// --- PARSER RULES ---

file: apiDefinition* EOF;

apiDefinition
    : 'api' CAPITAL_ID '{' (separator? apiElement)* separator? '}'
    ;

apiElement
    : metaDefinition
    | pathsDefinition
    | traitsDefinition
    | modelsDefinition
    ;

// --- META RULES ---
metaDefinition
    : META '{' (separator? metaField)* separator? '}'
    ;

metaField
    : anyId ':' metaValue
    ;

metaValue
    : STRING
    | PATH_ID
    | INT
    ;

// --- PATHS RULES ---
pathsDefinition
    : PATHS '{' (separator? pathElement)* separator? '}'
    ;

pathBlock
    : '{' (separator? pathElement)* separator? '}'
    ;

pathElement
    : pathDefinition
    | endpointDefinition
    ;

pathDefinition
    : PATH_ID pathBlock
    ;

// --- ENDPOINT RULES ---
endpointDefinition
    : endpointAnnotations? httpMethod anyId? endpointParams? endpointSignature?
    ;

endpointAnnotations
    : endpointAnnotation+
    ;

endpointAnnotation
    : SUMMARY '(' STRING ')'
    | DESCRIPTION '(' STRING ')'
    ;

endpointParams
    : '(' (param (',' param)*)? ')'
    ;

param
    : anyId ':' type
    ;

endpointSignature
    : inlineSignature
    | blockSignature
    ;

inlineSignature
    : '->' responseType (','? ERROR ':' inlineErrorDef)?
    | ','? ERROR ':' inlineErrorDef
    ;

responseType
    : type
    | INT
    ;

inlineErrorDef
    : INT
    | '{' errorMapping (',' errorMapping)* '}'
    ;

errorMapping
    : INT ('->' type)?
    ;

blockSignature
    : '{' (separator? signatureBlockElement)* separator? '}'
    ;

signatureBlockElement
    : RESPONSE responseBlock
    | ERROR errorsBlock
    ;

responseBlock
    : '{' (separator? errorMapping)* separator? '}'
    ;

errorsBlock
    : '{' (separator? errorDetail)* separator? '}'
    ;

errorDetail
    : INT ('->' type | errorDetailBlock)?
    ;

errorDetailBlock
    : '{' (separator? field)* separator? '}'
    ;

// --- TRAIT RULES ---
traitsDefinition
    : TRAITS '{' (separator? traitDefinition)* separator? '}'
    ;

traitDefinition
    : CAPITAL_ID traitBlock
    ;

traitBlock
    : '{' (separator? traitField)* separator? '}'
    ;

traitField
    : '+' CAPITAL_ID
    | field
    ;

// --- MODEL RULES ---
modelsDefinition
    : MODELS '{' (separator? modelDefinition)* separator? '}'
    ;

modelDefinition
    : CAPITAL_ID (':' CAPITAL_ID)? modelBlock
    ;

modelBlock
    : '{' (separator? modelField)* separator? '}'
    ;

modelField
    : '+' CAPITAL_ID
    | field
    ;

field
    : anyId ':' type
    ;

type
    : CAPITAL_ID
    | primitiveType
    | '[' type ']'
    ;

primitiveType
    : STRING_TYPE
    | INT_TYPE
    | BOOLEAN_TYPE
    | DATETIME_TYPE
    | DATE_TYPE
    | TIME_TYPE
    | FLOAT_TYPE
    | LONG_TYPE
    | BINARY_TYPE
    ;

separator
    : ','
    ;

// Safely allow keywords to be used as field names
anyId
    : ID
    | httpMethod
    | ERROR | RESPONSE | META | PATHS | MODELS | TRAITS
    ;

httpMethod
    : GET | POST | PUT | DELETE | PATCH | HEAD | OPTIONS | TRACE | CONNECT
    ;

// --- LEXER RULES ---

// Keywords
META     : 'meta' ;
PATHS    : 'paths' ;
MODELS   : 'models' ;
TRAITS   : 'traits' ;
ERROR    : 'error' ;
RESPONSE : 'response' ;
SUMMARY  : '@summary' ;
DESCRIPTION : '@description' ;


// Symbols
ARROW    : '->' ;

// HTTP methods
GET     : 'get' ;
POST    : 'post' ;
PUT     : 'put' ;
DELETE  : 'delete' ;
PATCH   : 'patch' ;
HEAD    : 'head' ;
OPTIONS : 'options' ;
TRACE   : 'trace' ;
CONNECT : 'connect' ;

// Primitive type
STRING_TYPE   : 'String';
INT_TYPE      : 'Int';
BOOLEAN_TYPE  : 'Boolean';
DATETIME_TYPE : 'DateTime';
DATE_TYPE     : 'Date';
TIME_TYPE     : 'Time';
FLOAT_TYPE    : 'Float';
LONG_TYPE     : 'Long';
BINARY_TYPE   : 'Binary';

// Identifiers
CAPITAL_ID: [A-Z][a-zA-Z0-9_]*;
PATH_ID: '/' [a-zA-Z0-9_:/.-]*;
ID: [a-z_][a-zA-Z0-9_]*;

// Primitive values
STRING: '"' ~'"'* '"';
INT: [0-9]+;

// Ignore whitespace
WS: [ \t\r\n]+ -> skip;
COMMENT: '//' ~[\r\n]* -> skip;
BLOCK_COMMENT: '/*' .*? '*/' -> skip;