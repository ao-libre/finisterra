En esta carpeta o paquete (game.loaders) se declaran las clases que cargan recursos de forma custom mediante AssetManager.

> ¿Pero como? ¿No era el AssetManager el que se encargaba de cargar los recursos del juego?
- Si, AssetManager esta programado para cargar algunos tipos de recursos como Atlases de Texturas, los archivos que usamos para el soporte multilenguaje los graficos del juego, las partículas, los sonidos/música, las skins, las fuentes, etc...
Sin embargo, hay algunos recursos especificos de este proyecto cuya carga no esta implementada en el AssetManager que nos brinda LibGDX para eso usamos los ***loaders***