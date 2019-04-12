# ClearDialogue
ClearDialogue is a branching dialogue editor for video games designed to be extremely flexible and work with any type of game engine. It was built with the [Clear LWJGL3 API.](https://github.com/SkyAphid/Clear)

![ClearDialogue](https://user-images.githubusercontent.com/6147299/56028879-c6d73b00-5cde-11e9-9a56-8cea701cdc5a.png)

[This program was written by NOKORI•WARE. If you're satisfied with ClearDialogue, please visit our website and consider buying some of our products to help support the development of free software like this!](https://www.nokoriware.com/)

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
- Has basic replace/find tools that support refactoring within the current project
- Edit/Move multiple nodes at once
- Use grid snapping to keep nodes aligned neatly in the project
- Add/Remove tags from multiple nodes at once
- Supports implementing custom syntax highlighting (I.E. highlighting text commands from your particular engine)

#### Documentation
- Includes test applications in the source code showing how to implement ClearDialogue into your games
- Code-base is highly customizable and easy to modify
- Code-base has extremely detailed documentation that walks you through adding new functionality

## Dependencies
ClearDialogue is configured to use Maven, so most of the required APIs will be downloaded automatically. ClearDialogue [also has support for THJSON, and a JAR for that is included in the repository.](https://github.com/Puppygames/thjson) The only other dependency you need to make sure you have is [Clear.](https://github.com/SkyAphid/Clear)

## Download
[Get a runnable version of ClearDialogue on the releases page.](https://github.com/SkyAphid/ClearDialogue/releases)

## Also check out:
Do you work on projects that have thousands of cells in Google Sheets? Check out my open source table of contents generator to help you stay organized:

[Google Sheets Table of Contents Generator](https://github.com/SkyAphid/GoogleSheetsTableOfContents)

Also check out my software company's website to see more of our projects:

[NOKORI•WARE Website](https://www.nokoriware.com)

## Special Thanks
This project was inspired by these programs:
- [Yarn](https://github.com/InfiniteAmmoInc/Yarn)
- [Monologue](https://github.com/nospoone/monologue)
