import java.io.IOException;

/* The aim of this class is to analyse the tokens translated by the Lexical Analyser
 * The tokens are then taken and tested against the languages grammar rules
 * The grammar rules are outlined in the specificaiton sheet in page 5
 * There are 15 grammar rules each which corresponds to a different set of steps
 * Each one of them will have it's own method to computer whether the tokens are correct
 */
public class SyntaxAnalyser extends AbstractSyntaxAnalyser {
    String fileName;
    public SyntaxAnalyser(String fileName) throws IOException{
        this.fileName = fileName;
        // create an instance of the lexical analyser
        // Allowing us to decode the raw data into tokens
        lex = new LexicalAnalyser(fileName);
    }
   

    /* Accept Terminal intakes the symbol of a token
     * It then checks if this token is valid
     * If the token is valid it moves to the next token processed by the lexical analyser. 
     * If the token is nvalid it returns an error
     */
    @Override
    public void acceptTerminal(int symbol) throws IOException, CompilationException {
        // Check if the symbol retrieved from the lex analyser matches the expected symbol
        // if it does 
        if(nextToken.symbol == symbol){
            myGenerate.insertTerminal(nextToken);
            nextToken = lex.getNextToken();
        }else{
            // 
            myGenerate.reportError(nextToken, "Expected " + Token.getName(symbol) + ", but retrieved " + Token.getName(nextToken.symbol));
        }
    }

     /* The syntax analyser is a top down recursive parser
     * This statementPart method is the first block to the syntax analyser's recursive process
     * <StatementPart> ::= begin <StatementList> end
     */
    @Override
    public void _statementPart_() throws IOException, CompilationException {
        // start the non terminal
        myGenerate.commenceNonterminal("<StatementPart>");
        // Follow grammar rules and output begin
        acceptTerminal(Token.beginSymbol);
        // call statement list based of the grammar above
        StatementList();
        // Follow grammar rules and output end
        acceptTerminal(Token.endSymbol);
        // end the non terminal
        myGenerate.finishNonterminal("<StatementPart>");
    }

    /*
     * <StatementList> ::= <Statement> | <StatementList> ; <Statement>
     */
    public void StatementList() throws CompilationException, IOException{
        myGenerate.commenceNonterminal("<StatementList>");
        // call the statement method
        Statement();
        // Checks if the next symbol is a semicolon, if it is then that means there are multiple statements in this list
        // | <StatementList> ; <Statement> , this signals that statementlist is called again which essentially just calls another statement
        while(nextToken.symbol == Token.semicolonSymbol){
            acceptTerminal(Token.semicolonSymbol);
            Statement();
        }
        myGenerate.finishNonterminal("<StatementList>");
    }
    /*
     * <Statement> ::= <AssignmentStatement> | <IfStatement> | <WhileStatement> | <ProcedureStatement> | <UntilStatement> | <ForStatement>
     * As shown in the equation above as statement can be one of many therefore the use of switch case allows us to navigate to the desired method
     */
    public void Statement() throws CompilationException, IOException{
        myGenerate.commenceNonterminal("<Statement>");
        switch (nextToken.symbol) {
            case Token.identifier:
                assignmentStatement();    
                break;
            case Token.ifSymbol:
                ifStatement();
                break;
            case Token.whileSymbol:
                whileStatement();
                break;
            case Token.callSymbol:
                procedureStatement();
                break;
            case Token.untilSymbol:
                untilStatement();
                break;
            case Token.forSymbol:
                forStatement();
                break;
            default:
                myGenerate.reportError(nextToken, "Invalid statement input");
            break;
        }
        myGenerate.finishNonterminal("<Statement>");
    }

    /*
     * <AssignmentStatement> ::= identifier := <Expression> | identifier := stringConstant
     */
    public void assignmentStatement() throws IOException, CompilationException{
        myGenerate.commenceNonterminal("<AssignmentStatement>");
            acceptTerminal(Token.identifier);
            // assigns the assignment operator :=
            acceptTerminal(Token.becomesSymbol);
        if (nextToken.symbol == Token.stringConstant) {
            acceptTerminal(Token.stringConstant);
        }else if  (nextToken.symbol == Token.identifier ||
        nextToken.symbol == Token.numberConstant ||
        nextToken.symbol == Token.leftParenthesis) {
            expression();
        } else{
            myGenerate.reportError(nextToken, "Expected a string constant or an expression after ':='");
        }
        myGenerate.finishNonterminal("<AssignmentStatement>");
    }

    /*
     * <IfStatement> ::= if <Condition> then <StatementList> end if | if <Condition> then <StatementList> else <StatementList> end if
     */
    public void ifStatement() throws IOException, CompilationException {
        myGenerate.commenceNonterminal("<IfStatement>");
        // start if statement
        acceptTerminal(Token.ifSymbol);
        // call conditional 
        conditionalStatement();
        // then keyword
        acceptTerminal(Token.thenSymbol);
        // call statement list
        StatementList();
        // if statement which allows us to check the else conditional 
        // allowing for a more streamlined programming approach
        if (nextToken.symbol == Token.elseSymbol) {
            acceptTerminal(Token.elseSymbol);
            StatementList();
        }
        // end/close if statement
        acceptTerminal(Token.endSymbol);
        acceptTerminal(Token.ifSymbol);
    
        myGenerate.finishNonterminal("<IfStatement>");
    }
    

    /*
     * <WhileStatement> ::= while <Condition> loop <StatementList> end loop
     */
    public void whileStatement() throws IOException, CompilationException{
        myGenerate.commenceNonterminal("<WhileStatement>");
        acceptTerminal(Token.whileSymbol);
        // call the conditional statement method
        conditionalStatement();
        acceptTerminal(Token.loopSymbol);
        // call the statement list method
        StatementList();
        acceptTerminal(Token.endSymbol);
        acceptTerminal(Token.loopSymbol);
        myGenerate.finishNonterminal("<WhileStatement>");
    }

    /*
     * <ProcedureStatement> ::= call identifier ( <ArgumentList> )
     */
    public void procedureStatement() throws IOException, CompilationException{
        myGenerate.commenceNonterminal("<Procedure Statement>");
        acceptTerminal(Token.callSymbol);
        acceptTerminal(Token.identifier);
        // parse the parenthesis in which the argument list is called in
        acceptTerminal(Token.leftParenthesis);
        // call the argument list method
        argumentList();
        // close the parenthesis
        acceptTerminal(Token.rightParenthesis);
        myGenerate.finishNonterminal("<Procedure Statement>");
    }

    /*
     * <UntilStatement> ::= do <StatementList> until <Condition>
     */
    public void untilStatement() throws IOException, CompilationException{
        myGenerate.commenceNonterminal("<UntilStatement>");

        acceptTerminal(Token.doSymbol);
        // call the statement list method
        StatementList();

        acceptTerminal(Token.untilSymbol);
        // call the conditional Statement method
        conditionalStatement();
        myGenerate.finishNonterminal("<UntilStatement>");
    }

    /*
     * <ForStatement> ::= for ( <AssignmentStatement> ; <Condition> ; <AssignmentStatement> ) do <StatementList> end loop
     */
    public void forStatement() throws IOException, CompilationException{
        myGenerate.commenceNonterminal("<ForStatement>");
        // create paranthesis to hold loop logic
        acceptTerminal(Token.forSymbol);
        acceptTerminal(Token.leftParenthesis);
        // call assignment statement
        assignmentStatement();
        acceptTerminal(Token.semicolonSymbol);
        // call conditional statement
        conditionalStatement();
        acceptTerminal(Token.semicolonSymbol);
        // call assignments statement
        assignmentStatement();
        acceptTerminal(Token.rightParenthesis);
        acceptTerminal(Token.doSymbol);
        // call the statement list method
        StatementList();
        acceptTerminal(Token.endSymbol);
        acceptTerminal(Token.loopSymbol);

        myGenerate.finishNonterminal("<ForStatement>");
    }
    
    /*
     * <ArgumentList> ::= identifier | <ArgumentList>, identifier
     */
    public void argumentList() throws IOException, CompilationException{
        myGenerate.commenceNonterminal("<ArgumentList>");
        acceptTerminal(Token.identifier);
       
        while (nextToken.symbol == Token.commaSymbol) {
            acceptTerminal(Token.commaSymbol);
            acceptTerminal(Token.identifier);
        }
        myGenerate.finishNonterminal("<ArgumentList>");
    }

    /*
     * Condition> ::= identifier <ConditionalOperator> identifier | identifier <ConditionalOperator> numberConstant | identifier <ConditionalOperator> stringConstant
     */
    public void conditionalStatement() throws IOException, CompilationException{
        myGenerate.commenceNonterminal("<Conditional Statement>");
        acceptTerminal(Token.identifier);
        conditionalOperators();
        switch (nextToken.symbol) {
            case Token.identifier:
                acceptTerminal(Token.identifier);
                break;
            case Token.numberConstant:
                acceptTerminal(Token.numberConstant);
                break;
            case Token.stringConstant:
                acceptTerminal(Token.stringConstant);
                break;
            default:
                myGenerate.reportError(nextToken, "Invalid Condition has been placed");
                break;
                
        }
        myGenerate.finishNonterminal("<Conditional Statement>");
    }

    /*
     * <ConditionalOperator> ::= > | >= | = | != | < | <=
     */
    public void conditionalOperators() throws IOException, CompilationException{
        myGenerate.commenceNonterminal("<Conditonal Operator>");
        switch (nextToken.symbol) {
            case Token.greaterThanSymbol:
                acceptTerminal(Token.greaterThanSymbol);
                break;
            case Token.greaterEqualSymbol:
                acceptTerminal(Token.greaterEqualSymbol);
                break;
            case Token.equalSymbol:
                acceptTerminal(Token.equalSymbol);
                break;
            case Token.notEqualSymbol:
                acceptTerminal(Token.notEqualSymbol);
                break;
            case Token.lessThanSymbol:
                acceptTerminal(Token.lessThanSymbol);
                break;
            case Token.lessEqualSymbol:
                acceptTerminal(Token.lessEqualSymbol);
                break;
            default:
            myGenerate.reportError(nextToken, "Expected a conditional operator.");
                break;
        }
        myGenerate.finishNonterminal("<Conditional Operator>");
    }

    /*
     *  <Expression> ::= <Term> | <Expression> + <Term> | <Expression> - <Term>
     */
    public void expression() throws IOException, CompilationException{
        myGenerate.commenceNonterminal("<Expression>");
        term();
        /*
         * if statement to compute whether addition or subtraction is taking place
         * thus allowing for the calculation of expression to take place
         * E.g. <Expression> + <Term> in essence equates to <Term> + <Term>
         * <Expression> ::= <Term>
         */
        while (nextToken.symbol == Token.plusSymbol || nextToken.symbol == Token.minusSymbol) {
        if(nextToken.symbol == Token.plusSymbol){
            acceptTerminal(Token.plusSymbol);
        } else if(nextToken.symbol == Token.minusSymbol){
            acceptTerminal(Token.minusSymbol);
        } else{
            myGenerate.reportError(nextToken, "Expected '+' or '-' in expression.");
        }
        term();
    }
        myGenerate.finishNonterminal("<Expression>");
    }

    /* 
     * <Term> ::= <Factor> | <Term> * <Factor> | <Term> / <Factor> | <Term> % <Factor>
    */
    public void term() throws IOException, CompilationException{
        myGenerate.commenceNonterminal("<Term>");
        factor();
        /* loop ensuring that the next token is equivalent to one of the expected symbols
         * if it is then the calculations can take place 
         */
        while (nextToken.symbol == Token.timesSymbol ||
        nextToken.symbol == Token.divideSymbol ||
        nextToken.symbol == Token.modSymbol) {
        switch (nextToken.symbol) {
            case Token.timesSymbol:
                acceptTerminal(Token.timesSymbol);
                factor();
                break;
            case Token.divideSymbol:
                acceptTerminal(Token.divideSymbol);
                factor();
                break;
            case Token.modSymbol:
                acceptTerminal(Token.modSymbol);
                factor();
                break;
            default:
                myGenerate.reportError(nextToken, "Expected the multiplication, division, or modulus operator in term");
                break;
            }
        }
        myGenerate.finishNonterminal("<Term>");
    }
    /* 
     * <Factor> ::= identifier | numberConstant | ( <Expression> )
     */
    public void factor() throws IOException, CompilationException{
        myGenerate.commenceNonterminal("<Factor>");
        switch (nextToken.symbol) {
            case Token.identifier:
                acceptTerminal(Token.identifier);
                break;
            case Token.numberConstant:
                acceptTerminal(Token.numberConstant);
                break;
            case Token.leftParenthesis:
                // call the expression method within brackets
                acceptTerminal(Token.leftParenthesis);
                expression();
                acceptTerminal(Token.rightParenthesis);
                break;    
            default:
            myGenerate.reportError(nextToken, "Expected identifier, a number, or left parenthesis");

            break;
        }
        myGenerate.finishNonterminal("<Factor>");
    }
}
