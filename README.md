#  SCC312 – Recursive Descent Parser 

The project demonstrates understanding of compiler design principles, recursive parsing, and syntax validation — including informative error handling and grammar rule traversal.


# Overview

The parser is designed to:

- Validate the syntactic correctness of source programs
- Recognise constructs like:
  - Assignment statements
  - Conditional statements (`if`, `if-else`)
  - Loops (`while`, `for`, `until`)
  - Procedure calls
- Halt and report the **first syntax error** with contextual detail
- Output a trace of parsing steps using a custom `Generate` class

Implemented Components

# `SyntaxAnalyser.java`
- Recursive descent implementation based on the formal grammar
- Walks the syntax tree, reporting errors early and clearly
- Uses the supplied lexical analyser to retrieve tokens

## `Generate.java`
- Outputs structured trace of the parsing process
- Handles error messaging via `reportError()`
- Ensures compliance with the abstract methods in `AbstractGenerate`

---

#Testing & Output

- Valid programs produce detailed trace logs (output.txt)
- Invalid programs terminate early with clear error messages (res.txt)
- Tested on all provided programN test files


# Project Layout
- The university provided students with a number of classes, and abstract classes which students utilise to create their compiler
- These classes will be placed in their own stand alone file
- The classes I have worked on will be added in a file named '312_CW.zip', which will also include the output files
- The coursework specification is provided to assist in your understanding of the program and it's requirements
- 
