package io.github.neczpal.restdsl.parser.antlr;

import io.github.neczpal.restdsl.RestDSLLexer;
import io.github.neczpal.restdsl.RestDSLParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AntlrTraitParserTest {

    @Test
    public void testTraitDefinition() {
        String input = """
                api Test {
                    traits {
                        Auditable {
                            createdAt: String
                            updatedAt: String
                        }
                        Contactable {
                            + Auditable
                            phoneNumber: String
                        }
                    }
                }
                """;

        RestDSLLexer lexer = new RestDSLLexer(CharStreams.fromString(input));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        RestDSLParser parser = new RestDSLParser(tokens);

        RestDSLParser.FileContext fileContext = parser.file();

        RestDSLParser.ApiDefinitionContext apiContext = fileContext.apiDefinition(0);
        RestDSLParser.TraitsDefinitionContext traitsContext = null;
        for (RestDSLParser.ApiElementContext elem : apiContext.apiElement()) {
            if (elem.traitsDefinition() != null) {
                traitsContext = elem.traitsDefinition();
            }
        }

        assertNotNull(traitsContext);
        assertEquals(2, traitsContext.traitDefinition().size());

        // Test Auditable trait
        RestDSLParser.TraitDefinitionContext auditableContext = traitsContext.traitDefinition(0);
        assertEquals("Auditable", auditableContext.CAPITAL_ID().getText());
        assertEquals(2, auditableContext.traitBlock().traitField().size());
        assertEquals("createdAt", auditableContext.traitBlock().traitField(0).field().anyId().getText());
        assertEquals("String", auditableContext.traitBlock().traitField(0).field().type().getText());
        assertEquals("updatedAt", auditableContext.traitBlock().traitField(1).field().anyId().getText());
        assertEquals("String", auditableContext.traitBlock().traitField(1).field().type().getText());

        // Test Contactable trait
        RestDSLParser.TraitDefinitionContext contactableContext = traitsContext.traitDefinition(1);
        assertEquals("Contactable", contactableContext.CAPITAL_ID().getText());
        assertEquals(2, contactableContext.traitBlock().traitField().size());
        assertEquals("Auditable", contactableContext.traitBlock().traitField(0).CAPITAL_ID().getText());
        assertEquals("phoneNumber", contactableContext.traitBlock().traitField(1).field().anyId().getText());
        assertEquals("String", contactableContext.traitBlock().traitField(1).field().type().getText());
    }
}
