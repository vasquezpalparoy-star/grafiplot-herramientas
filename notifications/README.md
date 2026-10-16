# Activar avisos de Grafiplot

Estado de esta entrega: interfaz, permiso Android, recepción FCM y código del servidor preparados. **Firebase conectado mediante consola web: proyecto `grafiplot-herramientas`, Android y web registrados, FCM HTTP v1 habilitado, Authentication correo/contraseña habilitado y Firestore Standard creado en nam5 con reglas de producción cerradas. El servidor y el administrador siguen pendientes; no se envían notificaciones remotas todavía.** El panel de vista previa está en `admin-avisos.html`; no permite enviar sin una cuenta autorizada.

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


## Chat privado (versión 1.2)

`chat.html` incluye acceso y creación de cuentas de clientes, conversación individual, mensajes de texto en tiempo real, historial de los últimos 100 mensajes, bandeja del administrador y cierre de sesión. Los clientes no pueden listar ni consultar conversaciones ajenas ni falsificar el rol de administrador. Los mensajes no se pueden editar o borrar desde el cliente. Firebase guarda el contenido; no es cifrado de extremo a extremo. La sesión se conserva solo durante la sesión del navegador.

El código está preparado y desactivado (`chatEnabled:false`) hasta autorizar al administrador, verificar las reglas y desplegarlas. `chatPushEnabled:false` mantiene desactivadas las notificaciones privadas hasta desplegar `registerChatDevice` y `notifyChatMessage`. Se envían a dispositivos individuales: nunca al tema público de avisos. El texto privado no se incluye en la notificación de bloqueo. Los avisos privados se descartan en la APK si la cuenta vinculada no coincide y se limpian al cerrar sesión.

1. Crea la cuenta del administrador en Firebase Authentication. El propietario debe introducir su contraseña personalmente; no incluirla en código ni archivos públicos.
2. Asigna `admin:true` con `set-admin.js` desde un entorno administrativo confiable.
3. Ejecuta las pruebas de reglas de `tests/` con el emulador. Despliega las reglas de `firestore.rules` en este proyecto dedicado. No cambia reglas de otros proyectos.
4. Activa `chatEnabled:true` en la configuración pública para habilitar el chat de texto. El chat abierto puede funcionar sin Cloud Functions, dentro de las cuotas de Firestore/Authentication.
5. Para notificaciones con la app cerrada, el propietario debe crear/vincular una cuenta de facturación y aprobar Blaze. Despliega las funciones y configura `chatDeviceUrl` con la URL HTTPS real de `registerChatDevice`; activa `chatPushEnabled:true`. Revisa alertas y límites de gasto.
6. Prueba con dos cuentas distintas y el administrador: el cliente A no debe ver a B; prueba ida y respuesta con la APK en segundo plano y después del cierre de sesión.

Esta versión prepara texto. Adjuntar imágenes o PDF requiere integrar Storage, permisos y límites de archivo; todavía no se ofrece como función activa del chat. No se realizaron envíos a clientes ni pruebas en un teléfono físico.
