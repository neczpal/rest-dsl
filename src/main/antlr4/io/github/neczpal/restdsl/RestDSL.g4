grammar RestDSL;

// --- PARSER RULES (The structure of your language, starts with lowercase) ---

// The root of our file
file: definition* EOF;

definition
    : apiDefinition
    | modelDefinition
    ;

apiDefinition: 'api' ID '{' apiProperties* '}';

apiProperties:
    'title:' STRING           # TitleProp
    | 'version:' STRING        # VersionProp
    | 'base:' STRING         # BaseProp
    ;

modelDefinition: 'model' ID '{' field* '}';

field: ID ':' type;

type
    : 'Int'
    | 'String'
    | 'Boolean'
    | 'Double'
    ;

// --- LEXER RULES (The vocabulary of your language, starts with UPPERCASE) ---

ID: [a-zA-Z_][a-zA-Z0-9_]*;
STRING: '"' ~'"'* '"';

// Ignore whitespace and newlines
WS: [ \t\r\n]+ -> skip;