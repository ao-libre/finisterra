## Fecha:
## Duración: desde las 19hs hasta las 24hs
## Objetivos:
### Rol
Fuertes vientos y devastadoras olas desde los mares azotaron nuestro mundo. Nuestras tierras han quedado desbastadas. Millones de desastres ha ocasionado, suelos que perdieron su forma, criaturas que cambiaron su aspecto. Algo peculiar sucedió también. Desde las profundidades han brotado las reliquias de nuestros antepasados. Ayudanos a recuperar el aspecto original del mundo y a recuperar estos objetos preciados. Los reyes del pasado sabrán recompensarte. 
### Técnico
Ayudanos a encontrar los errores de mapeo y graficación. Estamos intentando reorientar perspectiva gráfica, por eso te recomendamos la lectura de [dimetrica] para que entiendas el concepto.
### Puedes aportar de 3 formas:
‣ Indicando errores visuales que veas en npc o mapas. 
‣ Sugiriendo cambios de gráficos o reemplazo por otros que se adapten a la perspectiva [dimétrica]
‣ Recorriendo todo el mundo en búsqueda de los tesoros perdidos.
∘ Para que sea eficiente te pediremos al finalizar que completes un issue en github [enlace] o simplemente nos envíes un email a testing@finisterra.online con tus anotaciones del bloc de notas.
## Información General:
• Es posible que el juego simplemente se tilde, en se caso agendalo en tu bloc de notas con una descripción de como sucedio el error, en el mapa que estabas y si fue al relacionarte con algún objeto.
• Es posible que el servidor se cierre por una falla en la programación, en ese caso revisa en el Canal de Discord>Ao-Java>Testing

## Enlaces para feedback:
- Encuesta para recompensas.
- Enlace a github Issues con plantilla.
## Recompensas 
Si quieres recibir tu recompensa, te recomendamos que utilizes un email real para que podamos gratificarte por tu aporte.
- 1 mención en nuestro Papiro de agradecimientos acompañado de un breve comentario que quieras ofrecer!
## Lecturas adicionales para optimizar tu colaboración:
Mapa del mundo
## Descarga del cliente:
• Encontrarás un enlace para tu sistema operativo en el listado de relases de github, en este caso el realease se llamará BetaTesting-1
## Enlaces para difundir:
### Ubicaciones fijas de difución:
- Facebook Argentum Online
- Discord en Importantes
- Reddit
- Emails
- Gs-zone
- Servidores y fansites comunidad Argentum
- Facebook Grupo Argentum online (una semana antes para que se suscriban, y el mismo día).

## Implementaciones necesarias:
- Un campo Email al crear el personaje.
- Se implementa un objeto que cuando la persona lo agarra, guarda en un archivo el email con el que creo su personaje. Este registro será utilizado para reenviar un agradecimiento y un enlace a una encuesta para que complete la Ficha y agregarlo en el Papiro de agradecimientos.
- Plantilla de Issue para este caso:
  - Contiene la referencia al markdown - [ ] y el formato Ves a ó Tipo de gráfico, Mapa, Descripción adicional.
- Acelerar la velocidad de movimiento de los usuarios para que puedan llegar mas rápido y explorar más.
- #69 Efecto de transición cuando un usuario muere y respawnea en ulla.
- Un formulario para mandar un reporte de bug.
  - Mapa
  - coordenada X Y
  - Comentarios adicionales
  - Solo se pueden enviar hasta 3 formularios por minuto
- Posible Featured: Una herramienta para informar bug en pantalla que funciona como Un botón que cuando lo apretás se convierte en target y si lo cliqueas sobre la pantalla levanta la información de la ubicación, mapa x y, lo que hay allí, si es un grafico que grafico es, si es una criatura su nombre e index. Luego aparece un mesaje que ¿ pregunta si es correcta la información del reporte y muestra lo la info que levanto del target. Y luego lo envía al servidor para ser almacenado y utilizado para luego recopilar todos los datos enviados. Se pueden enviar hasta 5 por minuto.
- Mensajes del servidor:
  - El usuario X ha recuperado una reliquia.
  - Aún quedan X reliquias por recuperar.
  - Todas las reliquias han sido recuperadas! Se ha cumplido con el objetivo! Muchas gracias por participar.
