import java.util.Stack;

public class Generate extends AbstractGenerate {
    // Create a stak to act as the stack trace
    private Stack<String> ruleStack = new Stack<>();

    @Override
    public void commenceNonterminal(String name) {
        // add item to the stack and start the translation process
        ruleStack.push(name);
        System.out.println("312BEGIN " + name);
    }

    @Override
    public void finishNonterminal(String name) {
        //remove the item from the stack and end 
        if (!ruleStack.isEmpty() && ruleStack.peek().equals(name)) {
            ruleStack.pop();
        }
        System.out.println("312END " + name);
    }

    @Override
    public void reportError(Token token, String explanatoryMessage) throws CompilationException {
        // create a string builder to act as the foundation for the 
        StringBuilder error = new StringBuilder();
        // Append the phrases in "" to create error messages which should work for all levels within the stack
        // E.g. Error location, "line", error type "enxpected token" + token name
        // Explanatory custom message to inform of the issue
        error.append("Line ").append(token.lineNumber).append(": ");
        error.append("Unexpected token '").append(token.text).append("' (").append(Token.getName(token.symbol)).append(")\n");
        // error.append("    ").append(explanatoryMessage).append("\n");
        // Add simulated stack trace 
        if (!ruleStack.isEmpty()) {
            // If the stack has items with in it identify the stack trace pathway
            error.append("    Parse path:\n");
            // Reverse-order stack trace (deepest rule first)
            Stack<String> reversed = new Stack<>();
            // Add all items within rule stack to the reverstack
            reversed.addAll(ruleStack);

            for (int i = reversed.size() - 1; i >= 0; i--) {
                error.append("        at ").append(reversed.get(i)).append("\n");
            }
        }
        // Print these statements, allows for the  error to be displayed in the output.txt file
        System.out.println("Compilation Exception");
        System.out.print(error.toString());

        throw new CompilationException(error.toString() + explanatoryMessage);
    }
}
