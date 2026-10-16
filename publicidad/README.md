# Grafiplot Publicidad

Proyecto Firebase exclusivo de publicidad: `grafiplot-publicidad`, propiedad de la cuenta `grafiplotvasquez@gmail.com`, plan Spark. La actualización propuesta conecta también el acceso del chat con este proyecto. Los mensajes se conservan en el servidor Supabase existente. Una vinculación que verifica la sesión anterior y la nueva permite conservar el historial sin reasignar sus mensajes. La activación del cambio está pendiente de aprobación.

La configuración pública de publicidad se encuentra en `push-config.json`, campo `advertisingFirebase`. El panel usa una instancia Firebase independiente, `grafiplot-advertising-admin`. El propietario registra su contraseña personalmente y verifica su correo. La contraseña de Google no se reutiliza automáticamente.

Los anuncios se guardan en Firestore, colección `advertisements`. Las reglas de `firestore.rules` permiten lectura pública con consultas limitadas a 20 anuncios y creación únicamente a `grafiplotvasquez@gmail.com` con correo verificado. Validan campos, tamaños, estilo, destino, fecha del servidor y autor. Otros datos no se exponen. No se incluyen claves privadas ni credenciales de servidor en las APK.

El inicio del cliente y la sección Avisos consultan los anuncios más recientes. El inicio actualiza cada minuto mientras está visible. Los cambios se aplican a las aplicaciones desde la versión 1.5. Las herramientas de documentos e impresión conservan su comportamiento.

Esta integración publica anuncios dentro de la aplicación. No envía notificaciones con la aplicación cerrada; ese servicio requiere una configuración adicional y permanece desactivado.
