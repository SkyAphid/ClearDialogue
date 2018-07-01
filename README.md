# JDialogue
Java-based branching dialogue editor for video games that supports basic Java serialization along with JSON-based exports/imports. The code is fully commented with various instructions how to easily expand the functionality.

![jdialogue](https://user-images.githubusercontent.com/6147299/42131561-41b0f9e2-7cca-11e8-8491-1df63f9432c1.PNG)

## Features
- Supports both normal dialogue and dialogue responses
- Use the node-based system to connect these and make complex branching dialogue paths
- Supports Java serialization/deserialization
- Supports JSON exporting/importing
- Has a Refactor tool for find -> replace in multiple JDialogue Projects
- Supports implementing custom syntax highlighting (I.E. highlighting text commands from your particular engine)
- Includes test applications in the source code showing how to implement JDialogue into your games
- Lightweight and simple to use
- Code-base is highly customizable, fully commented, and easy to modify

## Dependencies
As long as you have [Java8 or above installed](https://java.com/en/), JDialogue should work right out of the box. 

## Implementing JDialogue in Your Game
- For examples of importing/exporting JDialogue files, check `nokori.jdialogue.io`
- For examples of implementing JDialogue into your game, see `nokori.jdialogue.test`
