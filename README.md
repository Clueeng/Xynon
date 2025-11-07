# Xynon Client

### Visual Studio Code Setup
To setup this project in visual studio code you need a few things
- Java 8
- The java extension pack
- The repository cloned

Move the native folder in your .minecraft <br>
```mv ./test_natives /home/your_user/.minecraft/``` <br>
Go to launch.json and change the cwd to your .minecraft folder <br>
Then change the vmArgs path to your test_natives folder <br>
Go to 'Run and Debug' or press CTRL+SHIFT+D <br>
Select the 'Start MCP' run configuration <br>
Click run <br>
