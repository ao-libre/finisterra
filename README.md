# ao-java [![Build Status](https://travis-ci.org/ao-libre/ao-java.svg?branch=master)](https://travis-ci.org/ao-libre/ao-java)
Rewriting Argentum Online in Java, using Artemis, Kryonet and libGDX.

## ECS (entity-component-system)
To understand ECS, I recommend to read [this](https://github.com/junkdog/artemis-odb/wiki/Introduction-to-Entity-Systems).
Using [artemis](https://github.com/junkdog/artemis-odb) framework has been really useful to start rewriting this game.

## Kryonet
Avoid creating huge game protocol since we can use Requests and Responses, which can be processed easily with 'visitor' design pattern. 
Read more [here](https://github.com/EsotericSoftware/kryonet)

## libGDX
It allows us to create application game logic, create screens and render all that we need using OpenGL.

## Getting Started
### Clone repo
```
git clone https://github.com/ao-libre/ao-java.git
```
## Requirements

Important: you need to use Java SE Development Kit (JDK) 8 (Java 1.8) to make it run

Download link: https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html

### Run
* Go to folder and use following commands to run client ```./gradlew desktop:run``` or server ```./gradlew server:run```
* Or generate distribution jars ``` ./gradlew desktop:dist ``` ``` ./gradlew server:dist ```

### Import in IntelliJ
* Select 'Import Project'
* Find and select ```build.gradle``` file
* Create run configurations using gradle tasks (desktop:run, server:run)

Here is an image showing how it should look like:
![imagen](https://media.discordapp.net/attachments/573645939663699988/585399360037322777/Screen_Shot_2019-06-04_at_9.25.56_PM.png)


#### How to change Java Version?
If you have another version of Java already installed, please change: 
https://kodejava.org/how-do-i-set-the-default-java-jdk-version-on-mac-os-x/

If you use IntelliJ, use the JDK that comes with the project if is not working change it by yourself like in the picture below:

`IntelliJ IDEA -> Preferences -> Build, Execution, Deployment -> Build Tools -> Gradle `

![imagenintellij](https://media.discordapp.net/attachments/519531620064296971/543934316233883669/Screen_Shot_2019-02-10_at_12.20.01_PM.png)


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

## Images 
![alt text](https://github.com/guidotamb/ao-java/blob/master/src/main/resources/readme-example.png)

# Key Config to play
## Default

### Combat
ATTACK_1 = CONTROL_LEFT
ATTACK_2 = CONTROL_RIGHT
MEDITATE = M
USE = U
HIDE = O
INVENTORY = I
SPELLS = K
TALK = ENTER
DROP = T
TAKE = A
EQUIP = E

### Move
MOVE_LEFT = LEFT
MOVE_RIGHT = RIGHT
MOVE_UP = UP
MOVE_DOWN = DOWN

## Alternative wasd
### Combat
ATTACK_1 = SPACE
ATTACK_2 = SHIFT_RIGHT
MEDITATE = M
USE = SHIFT_LEFT
HIDE = O
INVENTORY = I
SPELLS = K
TALK = ENTER
DROP = T
TAKE = L
EQUIP = E
### MOVE
MOVE_LEFT = A
MOVE_RIGHT = D
MOVE_UP = W
MOVE_DOWN = S
