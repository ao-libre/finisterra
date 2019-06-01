                   
# GDD AO·JAVA
# Información básica
            
- **Nombre:** Finisterra.
- **Plataforma:** 2D
- **Género:** MMO
- **OS:** Windows, Linux, Mac, Android
- **Idiomas:** Multi Idioma - Español Nativo.
- **Engine Licence:** La lincencia se definirá en función de la historia que tiene el Argentum en relación sus reperciciones en término de licencia.    
- **Content Licence:** La lincencia se definirá en función de la historia que tiene el Argentum en relación sus reperciciones en término de licencia.    
- **Clasificación:** EVERYONE 10+. Content is generally suitable for ages 10 and up. May contain more cartoon, fantasy or mild violence, mild language and/or minimal suggestive themes. 
- **Orientación Arte Gráfica:** 2D model pixel art
- **Principios:** Comunidad - Diversión - Respeto - Open Source - Aprendizaje - Cooperación
(definidos por la [Encuesta](https://docs.google.com/forms/d/e/1FAIpQLSemp4axFF6y5mitrH0bZjeKE64xMC0pIvhT7UELsmuTIqfXpg/viewform?usp=sf_link) incial del proyecto) 


# Breve descripción del Game Design
    Los usuarios adoptan un rol.
    Existen clases y razas
    Modo de juego:
        Se mueve con las flechas y se apunta con el mouse.
        Los ataques cuerpo a cuerpo se realizan presionando o manteniendo apretada una tecla.
        La habilidades se usan con selección o macros.
        Rangos ¿?
    Los usuarios pueden agruparse.
    Existe el modo PVP
    Existen Hechizos
    Existen Habilidades
    Existe un mundo.
    Existen Npc pacíficos y hostiles.
## Animaciones
Assets gráficos frame per frame con o sin esqueletos.
## Estilo gráfico
Medieval. Colores vivos, iluminaciones.

# Testeos Públicos
## Definiciones básicas.
- Tienen definida fecha y hora. Van alternando entre fin de semana y media semana.
- Cada test público tiene objetivos. Estos son definidos y publicados en la convocatoria.
- El usuario tester recibe una invitación con fecha, hora pautada. (mail, discord, facebook, etc)
- El usuario luego de completar la fase del test tiene a disposición un sistema de recompensas y unas encuestas para responder.

## Frecuencia estimada para los testeos
Mínimo 1 vez al mes.
Máximo 4 veces al mes.

## Canales de suscripción
Un embudo de las redes: discord, redes sociales, web TO> EMAIL.

## Método de implementación de nuevas features para los testeos.
Con el fin de sumar nuevos tester/jugadores podemos implementar un modo tutorial.

Launcher>Conect to> list of server:
### Modo Hogar
El jugador ingresa por primera vez a una zona segura donde lo recibe un Npc Guía Mario. Mario lo despide y el usuario busca un teleport que lleva al usuario al modo PVP
### Modo pvp
 Se encuentra Deshabilitado hasta que el usuario pueda superar el modo hogar, a partir de allí se desbloquea para nuevos ingresos.
    Al morir en el pvp, el usuario vuelve a salvo al modo hogar.

A lo largo de las versiones los modos evolucionan, ofrece nuevos conocimientos, misiones, mecánicas de combate.

## Generosidades del método
- Oportunidad para los que quieran apoyar al proyecto.
- FeedBack fomentado de jugadores casuales y experimentados.
- Desarrollo de inteligencia artificial que puede ser utilizada para modo learning, modo combate, modo mascotas.
- Oportunidad para probar mecánicas de combates, controles de mando, balance.
- Los testeos tienen una base fundada de varios jugadores en simultáneo realizando un objetivo.


# Npc 
## Asistentes
### Mario

- Mario tiene la función ser un Asistente inicial en términos vulgares “a los bifes”
- Su función principal es enseñarle las 5 acciones principales al usuario en menos de 120 segundos.
- Le muestra cómo moverse.
- Le regala equipo y el usuario aprende a levantarlos.
- Luego lo golpea y le quita vida, le muestra sus barras.
- Le enseña a usar las pociones  
- Lo manda al modo pvp, el usuario seguramente muera, vuelve a mario y mario le enseña a usar hechizos y le ofrece un duelo.
