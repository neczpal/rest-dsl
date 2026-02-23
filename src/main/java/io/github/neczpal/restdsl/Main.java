package io.github.neczpal.restdsl;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class Main {
    static void main() {
        String dslCode = "api Petstore { version: \"1.0.0\" base: \"/api/v3\" }";

        CharStream input = CharStreams.fromString(dslCode);
        RestDSLLexer lexer = new RestDSLLexer(input);

        CommonTokenStream tokens = new CommonTokenStream(lexer);
        RestDSLParser parser = new RestDSLParser(tokens);

        ParseTree tree = parser.file();

        System.out.println(tree.toStringTree(parser));
    }
}