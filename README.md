# JDialogue
JDialogue is a branching dialogue editor for video games designed to be extremely flexible and work with any type of game engine. 

![jdialogue](https://user-images.githubusercontent.com/6147299/52175570-5aced880-276b-11e9-8879-924d437652af.png)

[This program was written by NOKORI•WARE. If you're satisfied with JDialogue, please visit our website and consider buying some of our products to help support the development of free software like this!](https://www.nokoriware.com/)

## Features

#### General 
- Should be compatible with any language (however the UI itself is only in English, if you'd like a translated version, please let us know and we'll see about adding support for it)
- Supports both normal dialogue and dialogue responses
- Use the node-based system to connect these and make complex branching dialogue paths
- Has a game API that can be referenced directly from your Java game for even further simplicity of use
- Lightweight and simple to use

#### IO
- Supports JSON exporting/importing
- Is heavily documented with instructions on how to add support for your own filetypes

#### Tools
- Supports quickly merging projects together into single files in the case of dialogue restructuring
- Has basic replace/find tools that support refactoring within the current project as well as multiple projects spanning multiple files
- Edit/Move multiple nodes at once
- Add/Remove tags from multiple nodes at once
- Supports implementing custom syntax highlighting (I.E. highlighting text commands from your particular engine)

#### Documentation
- Includes test applications in the source code showing how to implement JDialogue into your games
- Code-base is highly customizable and easy to modify
- Code-base has extremely detailed documentation that walks you through adding new functionality

## Dependencies
For simply running JDialogue as a jar, all you need is to have [Java installed](https://java.com/en/).

For development, JDialogue uses JDK11 and JavaFX11. All other used APIs are included with JDialogue in its lib folder and are [credited in the license.](https://github.com/SkyAphid/JDialogue/blob/master/LICENSE)

## Implementing JDialogue in Your Game
- For examples of importing/exporting JDialogue files, check [`nokori.jdialogue.io`](https://github.com/SkyAphid/JDialogue/tree/master/JDialogue/src/nokori/jdialogue/io)
- For examples of implementing JDialogue into your game, see [`nokori.jdialogue.test`](https://github.com/SkyAphid/JDialogue/tree/master/JDialogue/src/nokori/jdialogue/test)

## Download
[Get a runnable version of JDialogue on the releases page.](https://github.com/SkyAphid/JDialogue/releases)

## Also check out:
Do you work on projects that have thousands of cells in Google Sheets? Check out my open source table of contents generator to help you stay organized:

[Google Sheets Table of Contents Generator](https://github.com/SkyAphid/GoogleSheetsTableOfContents)

Also check out my software company's website to see more of our projects:

[NOKORI•WARE Website](https://www.nokoriware.com)

## Special Thanks
This project was inspired by these programs:
- [Yarn](https://github.com/InfiniteAmmoInc/Yarn)
- [Monologue](https://github.com/nospoone/monologue)
