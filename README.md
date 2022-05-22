# Finisterra ![CI](https://github.com/ao-libre/finisterra/workflows/CI/badge.svg) ![Build Status](https://github.com/ao-libre/finisterra/workflows/Release/badge.svg)  [![GitHub release (latest by date including pre-releases)](https://img.shields.io/github/v/release/ao-libre/finisterra?include_prereleases)](https://github.com/ao-libre/finisterra/releases) [![Language](https://img.shields.io/badge/lang-espa%C3%B1ol%20%2F%20english-yellow)](#)

MMORPG de código abierto basado en Argentum Online. Escrito en Java usando Artemis, Kryonet y libGDX.

*Open Source MMORPG based on Argentum Online. Written in Java, using Artemis, Kryonet and libGDX.*

#### Redes [![Website](https://img.shields.io/website?down_color=lightgrey&down_message=offline&up_color=blue&up_message=online&url=https%3A%2F%2Ffinisterra.argentumonline.org%2F)](https://finisterra.argentumonline.org/) [![Discord](https://img.shields.io/discord/479056868707270657?color=blueviolet&label=discord)](https://discord.gg/4Wd4EMwnFm)


## Colaboradores
![game preview image](https://cdn.discordapp.com/attachments/580487031197794313/636899837354442755/readme-repo.png)

### Fugaz Get Started
basado en [OpenJDK versión 15](https://jdk.java.net/15/):

```
git clone https://github.com/ao-libre/finisterra.git
cd finisterra
./gradlew desktop:run -x :shared:fluid  
```

## Abrir Cliente/Servidor

Windows:
Abrir CMD y posicionarse en la carpeta del proyecto.

Cliente: Ejecutar el comando
`gradlew.bat desktop:run -x shared:fluid
`

![cliente preview](https://i.imgur.com/3P2L0K4.png)

Server: Abrir otro CMD posicionado en la carpeta del proyecto y ejecutar el comando `gradlew.bat server:run -x shared:fluid`

![server preview](https://i.imgur.com/mPl3pXy.png)

## Comenzar a programar (IntelliJ)
Requerido JDK 15

- Clonar el repositorio e importarlo, para eso lo haremos desde el propio IntelliJ:

![IntelliJ preview](https://i.imgur.com/pAhfyuZ.png)

![IntelliJ preview 2](https://i.imgur.com/AskF398.png)

Tras pulsar completar esperaremos unos minutos a que el proceso termine, cuando IntelliJ se abra con el codigo deberemos esperar unos minutos a que el IDE indexe todos los archivos y dependencias. Se mostrara mediante una barra de progreso abajo a la derecha.

- Configurar el inicio del Cliente y Servidor desde el IDE. Para ello vamos al desplegable de arriba derecha y selecciona "Edit Configurations".
  Se abrira una ventana, pulsa en el '+' y selecciona 'Gradle':

![Configuracion](https://i.imgur.com/fgOF9cf.png)

Asegurate de que en run tengas
`desktop:run -x shared fluid` para el caso del cliente y 'server:run -x shared fluid' para el server.
Una vez configurado, pulsas OK y la ventana se cerrara. Ahora en el desplegable de antes podrás seleccionar Server o Cliente, pulsa el botón 'Play' o Mayús + F10 para iniciar.

- Ramas: Por defecto estaras en la rama master, para cambiar de rama podrás hacerlo desde con la herramienta de git o desde el propio IDE abajo a la derecha.

## ¿Dudas? ¿Preguntas?
Si tienes dudas, preguntas y/o quieres colaborar en el proyecto te esperamos en Discord!
https://discord.gg/Uy8AnEv9YG

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


