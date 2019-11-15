# Nombre: Quetzalcoatl
## Definiciones
Será un npc guardia que irá evolucionando sus mecánicas de combate a lo largo de los parches. 
Tiene que estar preparado para tener animaciones de combate clásicas (golpear) y habilidades especiales (paralizar o quemar en área)
## Arte
El aspecto del guardián aún no esta definido. En tamaño es del doble que un personaje, aproximadamente 200px. 
### Animaciones (pueden implementarse versión a versión)
#### Principales
- Caminar en 4 direcciones
#### Secundarias
- Se enfurece y un fuego lo consume alrededor.
- Agita su arma y golpea a todos los que lo rodeen.
- Desvanecimiento
## Diálogos
### Al acercase al rango de visión:
- Si te atreves acercarte conocerás la irá del guardián.
### Cuando pierde un 50% de la vida
- Aún queda mucho por conocerme, ¡Huye ahora antes que despierte mi irá!
### Cuando regresa a su punto inicial porque se alejó demasiado
- Cuidaré la puerta a Finisterra con mi vida.
### Cuando pierde un 75% de la vida
- ¡Enciéndete en llamas! Finisterra es la puerta entre la vida y la muerte, solo los seres con valentía suprema podrán atravesarla.

# v0.1
## Funciones
-  Tiene su propia área. Si un usuario entra a su área se pone agresivo.
- Posee una posición inicial de guardia.
- Si lo atacas te sigue durante 3 segundos, sino estas en rango de ataque antes de los 3 seg vuelve a su zona.
- Puede
# v0.2
## Funciones
- Solo puede alejarse luchando x cantidad de distancia de la entrada, en el caso que se aleja de más regresa a punto inicial y recupera el 50% de la vida perdida.
- Cuando pierde el 75% de la vida, se enciende en llamas y quema a todos a su alrededor.

# v0.3
## Funciones
- Cuando llega a un 10% de vida restante, una furia lo invade y hace temblar la pantallaantalla.
