# Impresión Rápida 0.2 — versión de prueba

Aplicación Android 8.0 o superior para la HP OfficeJet Pro 9020 en el Wi-Fi interno del local. Sin PC, nube ni HP Print Service Plugin. Nombre inicial: **HPE802D0**. Usa la misma firma de prueba que la APK 0.1, por lo que puede instalarse como actualización.

## Cambios de esta versión

- Interfaz a color con pasos numerados, tarjetas y estados visibles.
- Vista previa de **todas** las páginas, con anterior/siguiente, desplazador de página, zoom con dos dedos y movimiento. Alterna entre el documento original y las caras preparadas para imprimir. Solo mantiene una imagen en memoria.
- Límite de **5 hojas físicas por trabajo**, incluyendo todas las copias. Se comprueba antes de contactar con la HP y nuevamente antes de preparar el envío.
- Conexión por nombre de host HPE802D0, editable. DNS local y búsqueda mDNS del nombre exacto terminado en `.local`. Una caché de resolución dura 60 segundos.
- Orientación vertical u horizontal para una y doble cara. Doble cara vertical usa borde largo; horizontal y folleto usan **borde corto**.
- Folleto siempre horizontal, también en PWG Raster. Eliminada la rotación automática a vertical de la versión anterior y su cambio a borde largo.
- Consulta IPP del trabajo: en cola, retenido, imprimiendo, detenido, cancelado, abortado y terminado. No se marca terminado simplemente por haber enviado el archivo.
- Botón de cancelación. Cancela la preparación local o solicita `Cancel-Job` a la impresora para el ID del trabajo. Espera la confirmación del estado.
- Guarda el ID y un nombre único del trabajo activo y reanuda su consulta al volver a abrir la app. Antes de cancelar verifica ese nombre para no afectar un ID reutilizado por la impresora.

## Límite de hojas

| Formato | Cálculo por copia | Máximo con una copia |
| --- | --- | --- |
| Una cara | páginas | 5 páginas |
| Doble cara | redondear hacia arriba páginas / 2 | 10 páginas |
| Folleto | redondear hacia arriba páginas / 4 | 20 páginas |

El cálculo se multiplica por las copias. Por ejemplo: 6 páginas a doble cara son 3 hojas; con dos copias son 6 hojas y la app lo bloquea. El límite es por trabajo; no es una cuota diaria ni un límite impuesto al resto de aplicaciones del celular.

## Primera prueba

1. Instala `Impresion-Rapida-v0.2.apk`, actualizando la versión anterior. Si Android lo solicita, permite temporalmente instalar desde la app con que abriste el archivo.
2. Conecta el celular al Wi-Fi interno. La red debe permitir comunicación entre dispositivos y resolución del nombre de la impresora. El nombre correcto es el que aparece en la información de red de la HP; no es necesariamente el nombre comercial.
3. Pulsa **Comprobar conexión**. No imprime. Si falla, prueba `HPE802D0.local` y comprueba el nombre en la HP. También se acepta una IP como alternativa de diagnóstico, sin cambiar el nombre inicial.
4. Selecciona un PDF y abre **Ver todas las páginas**. Recorre el original y la salida preparada.
5. Prueba un PDF de 4 páginas numeradas, una sola copia, en folleto. En la vista previa, frente: 4 y 1; reverso: 2 y 3. Debe salir horizontal, a doble cara con borde corto. Dóblalo para verificar el resultado físico.
6. Prueba la cancelación con un trabajo pequeño. La hoja que ya está saliendo puede terminar de imprimirse.
7. Mantén la app abierta durante la impresión para ver los cambios de estado.

## Seguimiento y cancelación: límites reales

La impresora debe devolver un `job-id` y admitir `Get-Job-Attributes` y `Cancel-Job`. Si admite `Create-Job` y `Send-Document`, se obtiene el ID antes de enviar el documento, permitiendo solicitar cancelación durante el envío. En el modo alternativo `Print-Job`, solo se conoce el ID al recibir la respuesta: una interrupción de conexión antes de esa respuesta no permite garantizar que el trabajo se canceló. Revisa la cola y la pantalla de la HP antes de repetir el envío.

Una cancelación solicitada no se presenta como cancelación confirmada hasta que la HP informe ese estado. Puede requerir permisos en la impresora. Si no responde, se indica que no pudo confirmarse y puedes cancelar desde la pantalla de la HP.

El estado «Terminado» depende de lo que reporte la HP mediante IPP. Una pérdida de conexión se muestra como «Estado sin confirmar» y se reintenta; no equivale a éxito. Si la HP elimina un trabajo de su historial antes de que la app lo consulte, el estado podría quedar sin confirmar. Mantener el seguimiento cuando Android suspende o cierra el proceso no está garantizado; al reabrir se vuelve a consultar el ID guardado. No se incluye un servicio permanente en segundo plano.

## Formatos y otras limitaciones

- PDFs de hasta 30 MB y de 1 a 100 páginas para visualizar. Solo pueden imprimirse combinaciones de páginas/formato/copias que quepan en 5 hojas. Sin selección de rangos en esta versión.
- No se admiten PDF cifrados, Word, Excel ni imágenes sueltas.
- A4 con margen; preparación rasterizada a aproximadamente 144 dpi. Puede reducir la nitidez de texto pequeño; no conserva vectores ni elementos interactivos.
- Envío IPP/1.1 por HTTP al puerto 631. Rutas `/ipp/print` o `/ipp/printer`.
- PDF directo si se anuncia; en caso contrario, PWG Raster `sgray_8` o `srgb_8`, a resolución cuadrada anunciada entre 150 y 300 dpi. Los tamaños raster del folleto ahora permanecen horizontales. Su compatibilidad y orientación física deben verificarse con la HP real.
- Otros formatos, como URF o PCLm, no se implementan. Un trabajo PWG está limitado a un estimado de 250 MB; divide archivos grandes.
- Caché de documentos privada en el teléfono. No sube archivos a servidores.
- Red local de confianza: HTTP local sin cifrado ni autenticación por cliente. No exponer el puerto de la HP a internet.
- Sin cobros, autorización de operador, historial de pedidos ni trabajos persistentes de reimpresión.

## Validación realizada

- Compilación Java, generación DEX y firma APK v2/v3.
- Orden de folleto para 1 a 100 páginas y casos conocidos de 8 páginas.
- 1500 combinaciones de páginas, modos y copias para el conteo y límite de hojas.
- Reglas de borde corto/largo y cabecera PWG horizontal con Tumble activado.
- Estados terminales IPP, validación de host y respuesta mDNS con nombre comprimido, nombre no coincidente y respuesta truncada.
- Integración HTTP contra una impresora simulada exclusivamente en loopback: Create-Job, Send-Document, estado Imprimiendo, Cancel-Job, estado Cancelado, Print-Job alternativo y estado Terminado. Verificación de atributos e IDs transmitidos.
- Archivo PWG de muestra decodificado independientemente mediante libcups.

No se ejecutó en un teléfono o emulador Android ni se pudo acceder a la impresora real desde el entorno de desarrollo. Las correcciones están implementadas y comprobadas al nivel indicado; la interfaz, conexión por nombre, orientación y comportamiento físico de la cancelación siguen pendientes de la prueba en tu Android y HP.

## Proyecto

Abre la carpeta `impresion-rapida` en Android Studio con JDK 17 y SDK 35. Android Gradle Plugin 8.9.2; Gradle 8.11.1 o compatible. Genera una APK desde Build. Sin Gradle Wrapper ni clave privada de publicación en el proyecto. La APK entregada usa una clave de prueba; una edición de producción necesita su propia clave y validación en dispositivos.

Pruebas de consola: `tests/ProtocolTest.java`, `tests/JobClientTest.java` y `tests/mock_printer.py`. Requieren las clases compiladas y `android.jar` en el classpath. El mock solo usa loopback.

Referencias: https://www.pwg.org/ipp/ippguide.html ; https://developer.android.com/reference/android/graphics/pdf/PdfRenderer ; https://github.com/OpenPrinting/cups/blob/master/cups/raster.h
