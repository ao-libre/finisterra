# ao-java [![Build Status](https://travis-ci.org/ao-libre/ao-java.svg?branch=master)](https://travis-ci.org/ao-libre/ao-java) [![Chat](https://img.shields.io/badge/chat-on%20discord-7289da.svg)](https://discord.gg/GpX3zzZ)

Rewriting Argentum Online in Java, using Artemis, Kryonet and libGDX.

## ECS (entity-component-system)
To understand ECS, I recommend to read [this](https://github.com/junkdog/artemis-odb/wiki/Introduction-to-Entity-Systems).
Using [artemis](https://github.com/junkdog/artemis-odb) framework has been really useful to start rewriting this game.

## Kryonet
Avoid creating huge game protocol since we can use Requests and Responses, which can be processed easily with 'visitor' design pattern. 
Read more [here](https://github.com/EsotericSoftware/kryonet)

## libGDX
It allows us to create application game logic, create screens and render all that we need using OpenGL.

## Images 
![game preview image](https://cdn.discordapp.com/attachments/580487031197794313/636899837354442755/readme-repo.png)

## Get Started
* [Download Stable Versión App (incoming)](https://github.com/ao-libre/ao-java/releases)
* [Run on console](docs/get-started/run-on-console.md)
* [Development Environment](docs/get-started/development-environment.md)
* [Workflow to collaborating on Github](docs/get-started/workflow-github.md)
* [Key Config to Play](docs/get-started/key-config-to-play.md)

## Networks
[Web](https://finisterra.argentumonline.org/) [Youtube](https://www.youtube.com/channel/UCftJ6hBfoovJY6nfmXTBD0g)
