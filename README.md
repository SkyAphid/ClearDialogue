# JDialogue
Java-based branching dialogue editor for video games. It supports basic Java serialization along with JSON-based exports/imports. The code is fully commented with various instructions how to easily expand the functionality as well as fix any problems you may run into.

![jdialogue](https://user-images.githubusercontent.com/6147299/41993374-e45260b2-7a10-11e8-8698-b03e248ba2fc.png)

## Features
- Supports both normal dialogue and dialogue responses
- Use the node-based system to connect these and make complex branching dialogue paths
- Supports Java serialization/deserialization
- Supports JSON exporting/importing
- Includes test applications in the source code showing how to implement it into your games
- Lightweight and simple to use
- Code-base is highly customizable, fully commented, and easy to modify

## Dependencies
As long as you have [Java8 or above installed](https://java.com/en/), JDialogue should work right out of the box. 

## Implementing JDialogue in Your Game
JDialogue was designed primarily with Java-based games in mind, but it was also designed to work for any platform through the JSON functionality. If you don't want to write your own deserializer from scratch, I recommend checking out the `nokori.jdialogue.io` package of the source code. Also, for examples on how it would be used in your game code, check out `nokori.jdialogue.test`.
