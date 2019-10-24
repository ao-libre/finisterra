## Getting Started

### Clone repo
```
git clone https://github.com/ao-libre/ao-java.git
```
## Requirements

1. Important: you need to use [OpenJDK 12](http://jdk.java.net/12/) to make it run o SE edition https://www.oracle.com/technetwork/java/javase/downloads/jdk12-downloads-5295953.html
2. Intel IJ versión comunidad https://www.jetbrains.com/idea/download/index.html

### Import in IntelliJ
* Select 'Import Project'
* Find and select ```build.gradle``` file
* Create run configurations using gradle tasks (desktop:run, server:run)

Here is an image showing how it should look like:
#### Server
![imageserverconfig](https://cdn.discordapp.com/attachments/573645939663699988/620037001122414606/Captura_de_pantalla_de_2019-09-07_20-23-53.png)
#### Client
![imageclientconfig](https://cdn.discordapp.com/attachments/573645939663699988/620037006545649669/Captura_de_pantalla_de_2019-09-07_20-23-37.png)
#### Game Design Center
![imageclientconfig](https://cdn.discordapp.com/attachments/573645939663699988/620037009829789698/Captura_de_pantalla_de_2019-09-07_20-23-08.png)

#### How to change Java Version?
If you have another version of Java already installed, please change:
https://kodejava.org/how-do-i-set-the-default-java-jdk-version-on-mac-os-x/

If you use IntelliJ, use the JDK that comes with the project if is not working change it by yourself like in the picture below:

`IntelliJ IDEA -> Preferences -> Build, Execution, Deployment -> Build Tools -> Gradle `

![imagenintellij](https://cdn.discordapp.com/attachments/580487031197794313/627486688574308392/Captura_de_pantalla_de_2019-09-28_09-39-58.png)


## Modules

### components
Contains all components (ECS related)
### core
Game client logic, screens, GUI, client systems (ECS): on runtime should have a replicated World.
### desktop
Contains assets and desktop game launcher.
### server
Server logic and systems (ECS), database connection (TBD), etc.
### shared
All stuff shared between client and server, like network requests and responses, objects, maps, etc.

