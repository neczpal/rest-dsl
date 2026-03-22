grammar w5_new_restDSL;

// --- PARSER RULES ---

file: apiDefinition* EOF;

apiDefinition
    : 'api' CAPITAL_ID '{' (separator? apiElement)* separator? '}'
    ;

apiElement
    : metaDefinition
    | pathsDefinition
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
    : httpMethod anyId? endpointParams? endpointSignature?
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

// --- MODEL RULES ---
modelsDefinition
    : MODELS '{' (separator? modelDefinition)* separator? '}'
    ;

modelDefinition
    : CAPITAL_ID modelBlock
    ;

modelBlock
    : '{' (separator? field)* separator? '}'
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
    ;

separator
    : ','
    ;

// Safely allow keywords to be used as field names
anyId
    : ID
    | httpMethod
    | ERROR | RESPONSE | META | PATHS | MODELS
    ;

httpMethod
    : GET | POST | PUT | DELETE | PATCH | HEAD | OPTIONS
    ;

// --- LEXER RULES ---

// Keywords
META     : 'meta' ;
PATHS    : 'paths' ;
MODELS   : 'models' ;
ERROR    : 'error' ;
RESPONSE : 'response' ;

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

// Primitive type
STRING_TYPE   : 'String';
INT_TYPE      : 'Int';
BOOLEAN_TYPE  : 'Boolean';

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