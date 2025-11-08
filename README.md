# Xynon Client

## Visual Studio Code Setup (Linux)
### Requirements
To setup this project in visual studio code you need a few things
- Java 21
- The java extension pack
- The repository cloned

/!\ Move the native folder in your .minecraft <br>
``mv ./test_natives $HOME/.minecraft/`` <br>
### launch.json
Open launch.json (Ctrl+Shift+P -> launch.json)
Change the <strong>'cwd'</strong> property to a custom .minecraft if needed
Change the <strong>'javaExec'</strong> property to your Java 21 installation path
Change <strong>'vmArgs'</strong> path to your .minecraft/test_natives
## Running the client
Go to 'Run and Debug' or press CTRL+SHIFT+D
Select the 'Start MCP' run configuration
Click run <br>
Enjoy!
