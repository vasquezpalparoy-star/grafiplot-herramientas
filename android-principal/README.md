# Grafiplot Herramientas para Android

## Actualización 1.7

- Inicio con ocho herramientas, nombres grandes y explicación antes del botón **Continuar**.
- **Mi Delivery** abre el seguimiento de pedidos de grafiplotvasquez.com dentro de la APK. El cliente puede mantener su sesión y cerrarla cuando lo necesite.
- **Libros e imágenes** integrado en la APK de clientes desde el proyecto Grifplotlibros recibido.
- Chat interno retirado de las dos APK y de la web. El contacto y los envíos por WhatsApp se mantienen.
- Administrador dedicado a Publicidad, con **Recuperar mi cuenta**, verificación del correo y comprobación de conexión.
- La publicación comprueba que el anuncio sea consultable por los clientes; los errores de lectura ya no se muestran como una bandeja vacía.
- Se conservan las opciones de notificaciones; la activación del envío en segundo plano sigue pendiente de conectar las credenciales privadas y el registro Android al proyecto Firebase correcto.

La cuenta autorizada sigue siendo **grafiplotvasquez@gmail.com**, con contraseña propia de la aplicación y correo verificado. Las actualizaciones instalables necesitan el respaldo de firma original de Grafiplot; la firma de Grifplotlibros no lo sustituye.

Las secciones siguientes documentan también versiones anteriores.

Aplicación principal con un inicio común para FUT, póster, ordenar PDF, imagen para pintar y carátulas. Incluye acceso a https://www.grafiplotvasquez.com/#hero dentro de una vista web separada y un botón para abrir la página en el navegador. El icono usa la imagen proporcionada por Grafiplot.

Las cinco herramientas se incluyen como recursos locales. La página comercial, WhatsApp y la carga de imágenes mediante URL necesitan conexión. La página comercial puede imponer restricciones de acceso propias.

## Compilar

Abre esta carpeta con Android Studio usando Java 17, Gradle 8.9 y SDK 35 con Build Tools 35.0.0. Ejecuta `gradle assembleDebug`. La tarea `bundleWebAssets` copia solo los archivos web del repositorio al APK. El resultado se encuentra en `app/build/outputs/apk/debug/app-debug.apk`.

## Guardar archivos

Las descargas abren el selector de documentos de Android. El usuario elige la carpeta y confirma el guardado. El botón de impresión de carátulas abre el chat de Grafiplot después de guardar el PDF; el usuario adjunta el archivo y envía su mensaje.

El APK de instalación manual usa una firma de prueba. La clave de firma no se publica. Para distribución formal, utiliza una clave propia y conserva esa clave para futuras actualizaciones.

## Verificación

Compilación Gradle, verificación de la firma, revisión del contenido del APK y comprobaciones de los documentos PDF y de las herramientas de procesamiento. Falta la prueba interactiva en un teléfono Android real.

## Versión 1.1: Impresión Rápida y avisos

Incluye el motor de `Impresion-Rapida-v0.2-completo` en el mismo APK, con nombre inicial HPE802D0. Los botones de FUT y Carátulas envían el PDF por un puente nativo a una copia privada y abren el módulo con el documento cargado. El envío físico solo se inicia cuando el usuario revisa y confirma en Impresión Rápida. Se mantienen las reglas de 5 hojas físicas por trabajo, 30 MB de PDF y 100 páginas de visualización, seguimiento y cancelación IPP.

Se incluyen pruebas de protocolo y una impresora simulada en `tests/`. No se ha accedido a la HP real ni ejecutado la app en un teléfono desde este entorno.

Los avisos FCM están preparados pero desactivados hasta conectar Firebase y publicar la configuración válida en `push-config.json`. El cliente decide si activar las notificaciones. La administración requiere una cuenta con la declaración `admin:true`; el servidor comprueba esa declaración en cada envío. Instrucciones en `../notifications/README.md`.

Para una APK de distribución firmada, usa `assembleRelease` y configura `GRAFIPLOT_KEYSTORE`, `GRAFIPLOT_STORE_PASSWORD` y `GRAFIPLOT_KEY_PASSWORD`; alias `grafiplot`. La clave privada y su contraseña no se publican en GitHub. Debe conservarse el respaldo privado para firmar actualizaciones futuras. Una firma de distribución no garantiza que Play Protect deje de mostrar un aviso.
