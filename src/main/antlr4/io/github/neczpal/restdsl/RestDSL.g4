grammar RestDSL;

// --- PARSER RULES (The structure of your language, starts with lowercase) ---

file: definition* EOF;

definition
    : apiDefinition
    | modelDefinition
    | serviceDefinition
    ;

apiDefinition: 'api' ID '{' apiProperties* '}';

apiProperties
    : 'title:' STRING        # TitleProp
    | 'version:' STRING      # VersionProp
    | 'base:' STRING         # BaseProp
    ;

modelDefinition: 'model' ID '{' field* '}';

field: ID ':' type;

serviceDefinition: 'service' ID '{' serviceElement* '}';

serviceElement
    : 'base:' STRING         # ServiceBaseProp
    | methodDefinition       # ServiceMethodProp
    ;

methodDefinition: verb ID '{' methodElement* '}';

verb: 'get' | 'post' | 'put' | 'delete' | 'patch';

methodElement
    : 'path:' STRING                    # MethodPathProp
    | 'body:' type                      # MethodBodyProp
    | 'pathParams:' '{' paramField* '}' # MethodPathParamsProp
    | 'queryParams:' '{' paramField* '}'# MethodQueryParamsProp
    | 'responses:' '{' responseField* '}' # MethodResponsesProp
    ;

paramField: ID ':' type;

responseField: INT ':' (type | STRING);

type
    : 'Int'
    | 'String'
    | 'Boolean'
    | 'Double'
    | ID
    | '[' type ']'
    ;

// --- LEXER RULES (The vocabulary of your language, starts with UPPERCASE) ---

ID: [a-zA-Z_][a-zA-Z0-9_]*;
STRING: '"' ~'"'* '"';
INT: [0-9]+;

// Ignore whitespace and newlines
WS: [ \t\r\n]+ -> skip;