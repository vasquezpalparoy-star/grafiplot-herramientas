# Publicidad y cliente 1.8

## Cambios

- El inicio muestra las ocho herramientas en dos columnas en teléfonos y cuatro en pantallas amplias. Se redujeron el encabezado, la presentación, los espacios y la altura de las tarjetas.
- Una imagen HTTPS puede usarse como fondo en cualquiera de los cuatro diseños. La vista previa informa errores de carga y conserva el texto cuando la fuente rechaza la imagen. Se normalizan enlaces públicos de Google Drive, Dropbox e imgres de Google.
- El envío de una notificación queda marcado inicialmente en el panel cuando el servicio está configurado. La confirmación distingue publicar con aviso de publicar solo en la aplicación.
- Mis publicaciones permite eliminar un anuncio con confirmación. Se usa Firebase SDK con las reglas existentes: solo el propietario verificado puede borrar. No se eliminaron publicaciones reales durante las pruebas.
- Al obtener correctamente la lista de Firestore, Avisos utiliza esa lista como fuente actual y no vuelve a añadir anuncios antiguos desde la caché de notificaciones. La caché se usa solo si falla la consulta.
- Android inicia Firebase también cuando el proceso se abre por un servicio, vuelve a suscribir tras renovar el token, comprueba el proyecto de la suscripción y muestra permiso, registro, errores y canal de Android. Se añadió una prueba local y acceso a los ajustes del sistema.

## Cambio del servidor pendiente de autorización

El código preparado en `supabase/functions/grafiplot-chat/announcement-message.mjs` produce mensajes FCM con `notification` y `data`, de modo que Android pueda presentar el aviso en segundo plano. La validación de Firebase JWT y el propietario verificado siguen siendo obligatorios. No se cambia `verify_jwt`, que ya era false por utilizar autenticación Firebase en vez de JWT de Supabase.

La revisión automática rechazó desplegar esta actualización de producción sin una autorización explícita adicional. El servidor sigue con su versión anterior. No se intentó otra ruta de despliegue ni se enviaron notificaciones masivas de prueba.

## Verificación

Las pruebas cubren los ocho accesos y su explicación, recuperación/verificación de cuenta, publicación y reintentos, eliminación sin doble envío, permisos del cliente, fondos para todos los estilos, enlaces inseguros, fallo de imagen, formato de notificación y tamaño máximo del mensaje con texto Unicode y enlaces largos.

La ejecución de Chromium para revisar visualmente el diseño no estuvo disponible en este entorno. Falta validar el resultado y la recepción real en un teléfono Android. Una prueba local solo demuestra que Android puede presentar una notificación; no demuestra entrega desde Firebase.

La firma privada usada para la edición de prueba no sobrevivió al mantenimiento de archivos temporales. No se puede generar una actualización compatible sin el respaldo de la firma. El respaldo original identificado anteriormente es `Grafiplot-Firma-Privada.zip`; no se incluyeron claves privadas ni contraseñas en el repositorio.
