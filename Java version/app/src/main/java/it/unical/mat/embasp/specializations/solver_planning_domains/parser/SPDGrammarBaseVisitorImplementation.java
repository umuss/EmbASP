package it.unical.mat.embasp.specializations.solver_planning_domains.parser;

import java.util.List;
import it.unical.mat.embasp.languages.pddl.Action;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

public class SPDGrammarBaseVisitorImplementation extends SPDGrammarBaseVisitor <Void> {
	private static final int OK_STATUS = 1;
    private static final int ERROR_STATUS = -1;
    private int status;
    private final List <Action> actions;
    private String errors = "";

    private SPDGrammarBaseVisitorImplementation(final List <Action> actions) {
        this.actions = actions;
    }

    @Override
    public Void visitPair(SPDGrammarParser.PairContext ctx) {
        final SPDGrammarParser.ValueContext valueContext = ctx.value();
        final String string = ctx.STRING().getText();

        if(status == 0 && string.equalsIgnoreCase("\"status\""))
            status = valueContext.getText().equalsIgnoreCase("\"ok\"") ? OK_STATUS : ERROR_STATUS;
        else if(status == ERROR_STATUS) {
            if(string.equalsIgnoreCase("\"result\"")) {
                if(valueContext instanceof SPDGrammarParser.ArrayValueContext || valueContext instanceof SPDGrammarParser.ObjectValueContext)
                    return visitChildren(ctx);
                else
                    errors += trim(valueContext.getText());
            } else if(string.equalsIgnoreCase("\"error\""))
                errors += trim(valueContext.getText());
        } else if(status == OK_STATUS) {
            if(string.equalsIgnoreCase("\"name\""))
                actions.add(new Action(trim(valueContext.getText())));
            else if(string.equalsIgnoreCase("\"plan\"") || string.equalsIgnoreCase("\"result\""))
                return visitChildren(ctx);
        }

        return null;
    }

    private static String trim(final String string) {
        final int stringLength = string.length();

        return (string.charAt(0) == '"' && string.charAt(stringLength - 1) == '"') ? string.substring(1, stringLength - 1) : string;
    }

    public static String parse(final List <Action> actions, final String spdOutput) {
        final SPDGrammarBaseVisitorImplementation parser = new SPDGrammarBaseVisitorImplementation(actions);

        parser.visit(new SPDGrammarParser(new CommonTokenStream(new SPDGrammarLexer(CharStreams.fromString(spdOutput)))).json());

        return parser.errors;
    }
}