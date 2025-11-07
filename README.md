# Xynon Client

### Visual Studio Code Setup
To setup this project in visual studio code you need a few things
- Java 8
- The java extension pack
- The repository cloned

Move the native folder in your .minecraft
```mv ./test_natives /home/your_user/.minecraft/```
Go to launch.json and change the cwd to your .minecraft folder
Then change the vmArgs path to your test_natives folder
Go to 'Run and Debug' or press CTRL+SHIFT+D
Select the 'Start MCP' run configuration
Click run
