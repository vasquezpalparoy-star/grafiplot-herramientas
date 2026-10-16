# Activar avisos de Grafiplot

Estado de esta entrega: interfaz, permiso Android, recepción FCM y código del servidor preparados. **Firebase no está conectado; no se envían notificaciones remotas todavía.** El panel de vista previa está en `admin-avisos.html`; no permite enviar sin una cuenta autorizada.

## Configuración pendiente en tu cuenta

1. Conecta el plugin oficial Firebase en ChatGPT o crea un proyecto dedicado a estos avisos en la consola de Firebase. Si decides usar un proyecto que ya tiene otras aplicaciones, fusiona sus reglas de Firestore antes de desplegar; no sobrescribas sus reglas existentes.
2. Registra la app Android `pe.grafiplot.herramientas`. Habilita Cloud Messaging HTTP v1 y Firestore.
3. Habilita Authentication con correo y contraseña. Crea tu usuario administrador. Nunca publiques su contraseña ni una clave de cuenta de servicio.
4. Desde un entorno de confianza con credenciales de administrador de Firebase, ejecuta `node functions/set-admin.js tu-correo`. Esto asigna la declaración `admin:true`. No existe un PIN administrativo dentro de la APK.
5. Instala las dependencias en `functions/` con `npm install`, prueba con `npm test` y despliega con Firebase CLI: `firebase deploy --only functions,firestore:rules --project TU_PROYECTO`. Desplegar Cloud Functions requiere el plan Blaze y una cuenta de facturación; revisa y aprueba sus condiciones en tu cuenta antes de activarlo.
6. Completa el archivo público `push-config.json` de la raíz del sitio. Contiene solo configuración del cliente: `enabled`, `projectId`, `apiKey`, `androidAppId`, `senderId`, `sendUrl` (función sendAnnouncement) y `feedUrl` (listAnnouncements). Los valores de Android se obtienen del registro de la aplicación en Firebase. La clave pública del cliente no autoriza el envío; el servidor exige la sesión de un administrador.
7. Publica el JSON en GitHub Pages. La APK consulta esa configuración por HTTPS al abrirse. Cierra y vuelve a abrirla, entra en Avisos y pulsa Activar notificaciones. En Android 13 o superior acepta el permiso. Se requieren servicios compatibles de Google Play.
8. Inicia sesión en Administración de avisos, revisa una plantilla y envía un aviso de prueba a un teléfono propio. Una respuesta «aceptado» significa aceptación de FCM, no entrega garantizada al teléfono.

## Plantillas y privacidad

Aviso, promoción, importante e imagen. El título, texto, imagen HTTPS y destino se pueden variar. Android controla la tipografía, disposición y sonido final de las notificaciones. Los clientes pueden desactivar los avisos. La página comercial no tiene acceso al puente nativo que administra notificaciones o impresiones.

Los mensajes van al tema `grafiplot_clientes`, para todos los usuarios que activaron las notificaciones. No hay segmentación ni mensajes privados por cliente en esta versión. El listado público solo expone los avisos aceptados; no expone correos de administradores, credenciales ni identificadores de dispositivos. Las reglas impiden el acceso directo a Firestore; las funciones validan y publican los datos autorizados.

El formulario solicita confirmación al enviar; cambiar de plantilla no envía nada. Cada intento usa un UUID para impedir duplicaciones por reintentos. Un error de red o una respuesta ambigua se mantiene como «no confirmado» y requiere revisar el servidor antes de volver a enviar.

Referencias: https://firebase.google.com/docs/cloud-messaging/android/get-started ; https://firebase.google.com/docs/cloud-messaging/android/receive-messages ; https://firebase.google.com/docs/functions/get-started ; https://developers.google.com/android/play-protect/warning-dev-guidance
