## How to run Game Design Center

## Processing of files in the game design center
El app se encarga de importar la información e imágenes que utiliza el juego para ofrecer una herramientas de diseño visual.
Cuando modifica algún elemento en el centro de diseño y lo guarda, la aplicación crea un nuevo archivo con toda la información (no agregua los valores dentro del archivo original, sino que crea un archivo nuevo completo en un directorio temporal).
Para ver los cambios dentro del juego, debe guardar los cambios en el centro de diseño y luego reemplazar los archivos originales en el proyecto Desktop/Server/Shared según corresponda.

This app import data and image reference from desktop. 
When you modify some element in the design center and save it, the application creates a new file with all the information (do not add the values inside the original file, but create a new file stored in a temporary directory).
To see the changes within the game, you must export from the design center and then replace the original files on the desktop/server/shared.

### Directories of Game Design Center
#### Import
* /shared/resources/spells
* /desktop/assets/data/descriptors
* /desktop/assets/data/graficos2x
* (incoming) /shared/resources/lang (It allows you to modify all the text that is displayed in the game, the interface, the screens, etc.)

#### Save
* Descriptors ```/design/resources/output```
* Images ```/design/resources/data/graficos2x```
* Maps ```/design/resources/output/maps```
## How to test an in-game design
Move archives in save to directory of Import (CAUTION)

* Maps
* Animation
* Images
* Spells