# GDD AO·JAVA
# Información básica
- **Nombre:** Finisterra.
- **Plataforma:** 2D
- **Género:** MMO
- **OS:** Windows, Linux, Mac, Android
- **Idiomas:** Multi Idioma - Español Nativo.
- **Engine Licence:** Codigo Abierto
- **Content Licence:** Codigo Abierto
- **Clasificación:** TODOS 10+. El contenido es generalmente adecuado para mayores de 10 años. Puede contener más caricaturas, fantasía o violencia leve, lenguaje moderado y / o temas sugerentes mínimos.
- **Orientación Arte Gráfica:** 2D model pixel art.
- **Principios:** Comunidad - Diversión - Respeto - Open Source - Aprendizaje - Cooperación.
(basados en la [Encuesta](https://docs.google.com/forms/d/e/1FAIpQLSemp4axFF6y5mitrH0bZjeKE64xMC0pIvhT7UELsmuTIqfXpg/viewform?usp=sf_link) incial del proyecto).

# Manejo de Idiomas en la documentación y/o información del proyecto
- **Diseño del juego:** Español
- **Programación:** Inglés
- **Documentación de programación:** Inglés
- **Documentación para usuarios:** Español - Inglés

# Breve descripción del Diseño del Juego
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
- Fecha y hora pautadas, alternan entre fin de semana y media semana.
- Los test publicos disponen objetivos, son publicados junto con la Convocotaria.
- Finalizado el test, se convoca al feedback mediante una invitación.
## Frecuencia estimada para los testeos
Mínimo 1 vez al mes.
Máximo 4 veces al mes.
## Canales de suscripción
Por listas de email.
## Canales de difusión
Email Principal, Web, Discord, Redes Sociales.
## Metajuego de los testeos
Los jugadores realizan objetivos dentro de diferentes mundos que ofrecen variantes.
### 1 Modo Hogar
**Descripción.**
Es una zona segura. Existen refugios para recibir a los nuevos ingresados y a los visitantes frencuentes.
- Primer logueo: El jugador ingresa por primera vez a una zona segura donde lo recibe un Npc Guía que le enseña a jugar lo guía hasta un objetivo.
- Siguientes visitas: En función del avanze que desarrolla el usuario, un npc lo sigue guiando.
### 2 Modo pvp
**Descripción.**
Es una zona insegura. Compienza siendo una sala de combate en donde se encuentra un npc hostil con mecánicas de combate que van evolucionando y se permite el combate usuarios contra usuarios.
Se encuentra Deshabilitado hasta que el usuario pueda superar el modo hogar, a partir de allí se desbloquea para nuevos ingresos.
Al morir en el pvp, el usuario vuelve a salvo al modo hogar.

**A lo largo de las versiones los modos evolucionan, ofrece nuevos conocimientos, misiones, mecánicas de combate.**
## Repercusiones de los testeos
- Oportunidad para los que quieran apoyar al proyecto.
- FeedBack fomentado de jugadores casuales y experimentados.
- Desarrollo de inteligencia artificial que puede ser utilizada para modo learning, modo combate, modo mascotas.
- Oportunidad para probar mecánicas de combates, controles de mando, balance.

# Npc 
## Asistentes
### Mario

**Descripción** Mario debería guiar al nuevo a usuario a jugar en menos de 120 segundos.
#### Primer encuentro
- Le muestra cómo moverse.
- Le regala equipo y el usuario aprende a levantarlos.
- Luego lo golpea y le muestra los indicadores en pantalla de Estadisticas Primarias.
- Le enseña a usar las pociones.
- Lo guía a un objetivo.
### Segundo encuentro
- Si el usuario vuelve vivo sin haber cumplido el objetivo que le guío, vuelve al indicarle que debe superar el objetivo.
- Si vuelve muerto, Mario le enseña a usar hechizos y le ofrece un duelo.
    Si gana el Usuario, aparece un GM y le da un Toque de Magia. Resucita a Mario y se va.
    Si gana Mario, Mario resucita a Usuario y le dice que su equipo no se ha caido porque esta en Zona Segura.
        Mario le indica el próximo objetivo.
