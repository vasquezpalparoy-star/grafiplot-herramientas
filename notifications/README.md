# Avisos y chat de Grafiplot

La ruta actual usa Supabase como servidor y base de datos, Firebase Authentication para el acceso y Firebase Cloud Messaging para las notificaciones Android. No requiere el plan Blaze. Consulta `../supabase/README.md`.

El chat de texto se activa con `chatEnabled:true`. Las notificaciones en segundo plano y el envío de avisos desde el panel permanecen desactivados hasta guardar la credencial privada FCM en Supabase y realizar una prueba de entrega.

`functions/`, `firebase.json` y `firestore.rules` son el código de la alternativa inicial con Cloud Functions/Firestore. No se desplegó esa alternativa. Firestore sigue cerrado en modo producción y no guarda las conversaciones del chat actual. No es necesario activar Blaze ni desplegar estos archivos.

El panel `admin-avisos.html` permite diseñar y revisar plantillas. El envío requiere el correo verificado del propietario autorizado por el servidor Supabase; no hay contraseña ni clave administrativa en la APK. Las credenciales privadas nunca se incluyen en GitHub.
