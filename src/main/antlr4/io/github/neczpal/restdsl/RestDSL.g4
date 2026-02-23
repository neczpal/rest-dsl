grammar RestDSL;

// --- PARSER RULES (The structure of your language, starts with lowercase) ---

// The root of our file
file: apiDefinition EOF;

apiDefinition: 'api' ID '{' properties* '}';

properties:
    'title:' STRING           # TitleProp
    | 'version:' STRING        # VersionProp
    | 'base:' STRING         # BaseProp
    ;

// --- LEXER RULES (The vocabulary of your language, starts with UPPERCASE) ---

ID: [a-zA-Z_][a-zA-Z0-9_]*;
STRING: '"' ~'"'* '"';

// Ignore whitespace and newlines
WS: [ \t\r\n]+ -> skip;