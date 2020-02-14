# Finisterra [![Build Status](https://travis-ci.org/ao-libre/finisterra.svg?branch=master)](https://travis-ci.org/ao-libre/finisterra)  [![GitHub release (latest by date including pre-releases)](https://img.shields.io/github/v/release/ao-libre/finisterra?include_prereleases)](https://github.com/ao-libre/finisterra/releases) ![Language](https://img.shields.io/badge/lang-espa%C3%B1ol%20%2F%20english-yellow) [![Discord](https://img.shields.io/discord/479056868707270657?color=blue&label=discord)](https://discord.gg/qCJPGbY)

MMORPG Java Open Source based on Argentum Online. Written in Java, using Artemis, Kryonet and libGDX.

## Colaboradores
![game preview image](https://cdn.discordapp.com/attachments/580487031197794313/636899837354442755/readme-repo.png)

### Fugaz Get Started
basado en [OpenJDK versión 13](https://jdk.java.net/13/):

```
git clone https://github.com/ao-libre/finisterra.git
cd finisterra
./gradlew desktop:run -x :shared:fluid  
```
Puede expandir visitando [Compilar o probar](https://docu-amigable-finisterra.000webhostapp.com/index/Espa%C3%B1ol/Comenzar/1_Compilar_para_probar_o_testear.html)

## Documentación Oficial
- [Docu-Wiki](https://docu-amigable-finisterra.000webhostapp.com/) 

Puede aportar a esta Documentación editando su [Código Fuente](https://github.com/ao-libre/finisterra/tree/master/docs)

## Java
Compatible con Eclipse, Netbeans y IntelliJ

#### ECS (entity-component-system)
To understand ECS, I recommend to read [this](https://github.com/junkdog/artemis-odb/wiki/Introduction-to-Entity-Systems).
Using [artemis](https://github.com/junkdog/artemis-odb) framework has been really useful to start rewriting this game.

#### Kryonet
Avoid creating huge game protocol since we can use Requests and Responses, which can be processed easily with 'visitor' design pattern. 
Read more [here](https://github.com/EsotericSoftware/kryonet)

#### libGDX
It allows us to create application game logic, create screens and render all that we need using OpenGL.

## ChangeLog 
[Historial de cambios](https://github.com/ao-libre/finisterra/blob/master/docs/index/Espa%C3%B1ol/ChangeLog.txt)

## Networks
[Web](https://finisterra.argentumonline.org/) -  [Discord](https://discord.gg/qCJPGbY) - 

