## How to run Game Design Center

## Processing of files in the game design center
El app se encarga de importar la informació e imágenes que utiliza el juego y ofrece herramientas para la edición desde un editor Front End.
Cuando modifica algún elemento en el centro de diseño y lo guarda, la aplicación crea un nuevo archivo con toda la información (no agregue los valores dentro del archivo original, pero cree un nuevo archivo almacenado en un directorio temporal).
Para ver los cambios dentro del juego, debe exportar desde el centro de diseño y luego reemplazar los archivos originales en el escritorio.

This app import data and image reference from desktop. 
When you modify some element in the design center and save it, the application creates a new file with all the information (do not add the values inside the original file, but create a new file stored in a temporary directory).
To see the changes within the game, you must export from the design center and then replace the original files on the desktop.

### Directories of Game Design Center
#### Import
/shared/resources/spells
/home/carlos/Juegos/ao-java/ao-java/desktop/assets/data/descriptors
/home/carlos/Juegos/ao-java/ao-java/desktop/assets/data/graficos2x

#### Save
* Json (descriptors)```/design/resources/output```
* Images ```/home/carlos/Juegos/ao-java/ao-java/design/resources/data/graficos2x```

## How to test an in-game design
Move archives in save to directory of Import (CAUTION)

* Maps
* Animation
* Images
* Spells