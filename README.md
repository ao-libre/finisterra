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
git clone https://github.com/guidotamb/ao-java.git
```

### Run
* Go to folder and use following commands to run client ```./gradlew desktop:run``` or server ```./gradlew server:run```
* Or generate distribution jars ``` ./gradlew desktop:dist ``` ``` ./gradlew server:dist ```

### Import in IntelliJ
* Select 'Import Project'
* Find and select ```build.gradle``` file
* Create run configurations using gradle tasks (desktop:run/dist, server:run/dist)

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
