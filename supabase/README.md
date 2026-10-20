# Grafiplot · Publicidad y notificaciones

El chat interno está retirado. Las operaciones `inbox`, `messages`, `send` y `device` devuelven HTTP 410 y no consultan ni modifican conversaciones. El estado público informa `chatEnabled:false`.

La función conserva el nombre técnico `grafiplot-chat` para mantener las URL configuradas en las APK. Continúan disponibles los anuncios, su consulta y las notificaciones de publicidad. El contacto externo por WhatsApp para imprimir permanece disponible.

La publicación de anuncios exige un JWT Firebase válido del proyecto `grafiplot-publicidad`, correo `grafiplotvasquez@gmail.com` y correo verificado. La configuración del gateway permanece como estaba: `verify_jwt=false`; la función verifica la firma RS256, emisor, audiencia y caducidad de Firebase. Las credenciales privadas se guardan exclusivamente en secretos del servidor.

No se borran tablas ni mensajes históricos. `schema.sql` conserva el esquema histórico y no debe ejecutarse de nuevo sobre producción.

El código local incluye una mejora de transporte FCM (`announcement-message.mjs`) cuya publicación queda pendiente de autorización independiente. La retirada del chat en producción se aplica sobre la versión existente y no incluye ese cambio pendiente.
