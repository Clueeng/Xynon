# Xynon Client
Xynon Client is an open-source Minecraft 1.8.9 hacked client, in early development
Feel free to make pull requests
The client is still in its very early stages, hence why no bypasses have been made yet

Want to support the project ? Don't hesitate to leave a star on the repo!

## F.A.Q
- How do I use the client ?
For now, the only way to use the client is to set it up and running it within an IDE, a launcher will be made later
- Will there be a scripting API ?
No plans for one as of now, but I do want to add one eventually
- Will the client ever be paid ?
Nope, free, open-source, forver
- Will the client always be maintained ?
It all depends on how well the client is received by the community
If I see no reason in maintaining a client, I might drop it, while occasionally updating it from time to time

# Setup
The client can be setup in many ways, but here is what I've used or currently using

## Visual Studio Code Setup (Linux)
### Requirements
To setup this project in visual studio code you need a few things
- Java 21
- The java extension pack
- The repository cloned


/!\ Move the native folder in your .minecraft <br>
``mv ./test_natives $HOME/.minecraft/`` <br>
Wait for maven to index everything <br>

### launch.json
Open launch.json (Ctrl+Shift+P -> launch.json)
Change the <strong>'cwd'</strong> property to a custom .minecraft if needed
Change the <strong>'javaExec'</strong> property to your Java 21 installation path
Change <strong>'vmArgs'</strong> path to your .minecraft/test_natives
### Running the client
Go to 'Run and Debug' or press CTRL+SHIFT+D
Select the 'Start MCP' run configuration
Click run <br>
Enjoy!

## IntelliJ IDEA
### Requirements
To setup this project in Intellij IDEA you need a few things
- Java 21
- The repository cloned

/!\ Move the native folder in your .minecraft <br>
``mv ./test_natives $HOME/.minecraft/`` <br>
Wait for maven to index everything <br>

### Run Configuration
Add a new Java run configuration
- Java version: Java 21
- Working directory: your minecraft folder
- Main class: Start.java