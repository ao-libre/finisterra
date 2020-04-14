# Finisterra ![CI](https://github.com/ao-libre/finisterra/workflows/CI/badge.svg) ![Build Status](https://github.com/ao-libre/finisterra/workflows/Release/badge.svg)  [![GitHub release (latest by date including pre-releases)](https://img.shields.io/github/v/release/ao-libre/finisterra?include_prereleases)](https://github.com/ao-libre/finisterra/releases) [![Language](https://img.shields.io/badge/lang-espa%C3%B1ol%20%2F%20english-yellow)](#)

MMORPG de código abierto basado en Argentum Online. Escrito en Java usando Artemis, Kryonet y libGDX.

*Open Source MMORPG based on Argentum Online. Written in Java, using Artemis, Kryonet and libGDX.*

#### Redes [![Website](https://img.shields.io/website?down_color=lightgrey&down_message=offline&up_color=blue&up_message=online&url=https%3A%2F%2Ffinisterra.argentumonline.org%2F)](https://finisterra.argentumonline.org/) [![Discord](https://img.shields.io/discord/479056868707270657?color=blueviolet&label=discord)](https://discord.gg/qCJPGbY) 

## Colaboradores
![game preview image](https://cdn.discordapp.com/attachments/580487031197794313/636899837354442755/readme-repo.png)

### Fugaz Get Started
basado en [OpenJDK versión 14](https://jdk.java.net/14/):

```
git clone https://github.com/ao-libre/finisterra.git
cd finisterra
./gradlew desktop:run -x :shared:fluid  
```

## Java
Compatible con Eclipse, Netbeans y IntelliJ

#### ECS (entity-component-system)
To understand ECS, I recommend to read [this](https://github.com/junkdog/artemis-odb/wiki/Introduction-to-Entity-Systems).
Using [artemis](https://github.com/junkdog/artemis-odb) framework has been really useful to start rewriting this game.

#### Kryonet
Avoid creating huge game protocol since we can use Requests and Responses, which can be processed easily with 'visitor' design pattern. 
Read more [here](https://github.com/EsotericSoftware/kryonet)

#### LibGDX
It allows us to create application game logic, create screens and render all that we need using OpenGL.

## Networks
[Web](https://finisterra.argentumonline.org/) -  [Discord](https://discord.gg/qCJPGbY)

