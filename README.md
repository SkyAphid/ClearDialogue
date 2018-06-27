# JDialogue
Java-based branching dialogue editor for video games. It supports basic Java serialization along with JSON-based exports/imports. The code is fully commented with various instructions how to easily expand the functionality as well as fix any problems you may run into.

![jdialogue](https://user-images.githubusercontent.com/6147299/41993374-e45260b2-7a10-11e8-8698-b03e248ba2fc.png)

## Features
JDialogue is built for video games specifically, and supports basic dialogue and multiple responses to that dialogue. Nodes are connected via connections between them, and each connection records the UUIDs for simple access. The software comes with serializers and serializers that can be easily converted into your preferred code of choice, or you can simply use the API of JDialogue to easily call those functions from it if you happen to be also using Java. 

## Dependencies
As long as you have [Java8 or above installed](https://java.com/en/), JDialogue should work right out of the box. 

## Implementing JDialogue in Your Game
JDialogue was designed primarily with Java-based games in mind, but it was also designed to work for any platform through the JSON functionality. If you don't want to write your own deserializer from scratch, I recommend checking out the `nokori.jdialogue.io` package of the source code.
