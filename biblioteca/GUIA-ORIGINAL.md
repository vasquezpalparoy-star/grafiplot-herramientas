# Grifplotlibros · Guía de uso

Aplicación Android para buscar PDF y previsualizarlos en una tablet del local.
**No solicita registro, inicio de sesión, PIN, contraseñas ni credenciales. La administración también es de acceso libre**, según tu última indicación.

## Actualizar desde la versión anterior

Instala este APK sobre la aplicación anterior, sin desinstalarla. Tiene el mismo nombre de paquete y la misma firma, con versión 1.5.0. Así se conservan las carpetas, categorías, imágenes y PDF descargados. Los libros anteriores no necesitan precios para enviarse a impresión. Puedes añadirlos solo a los libros que quieras.

## Instalar

1. Usa una tablet con **Android 8.0 o posterior**.
2. Copia `Grifplotlibros.apk` a la tablet y ábrelo desde Archivos.
3. Si Android lo pide, permite a esa aplicación instalar este APK. Este es un permiso de Android, no una cuenta de Grifplotlibros.
4. Pulsa Instalar y abre **Grifplotlibros**.

La aplicación oculta las barras del sistema y mantiene la pantalla encendida mientras está abierta. Android permite volver a mostrar temporalmente sus barras con un gesto desde el borde. Para fijarla en pantalla, puedes usar la opción de Administración; la fijación depende de los ajustes del dispositivo. El modo inmersivo por sí solo no bloquea los botones del sistema.

## Cargar los libros

1. Copia tus PDF a una carpeta de la tablet, por ejemplo `Documentos/Grifplotlibros/Libros` y otra para los exámenes.
2. En la app, toca **Administración → Seleccionar carpeta de PDF**.
3. Elige la categoría inicial: **Libros**, **Exámenes UNHEVAL** o Sin categoría.
4. En el selector de Android, abre la carpeta y pulsa **Usar esta carpeta / Permitir**.
5. Espera el aviso de catálogo actualizado. Se incluyen también los PDF de sus subcarpetas.
6. Repite para las demás carpetas.

No hace falta conceder acceso a toda la memoria: solo a las carpetas que eliges. Android restringe algunas ubicaciones, como la raíz de almacenamiento, Android/data y la raíz de Descargas; usa una subcarpeta dentro de Documentos o Descargas.

Al añadir archivos, toca **Actualizar catálogo**. Mientras la pantalla de inicio está abierta, la app vuelve a revisar las carpetas aproximadamente cada minuto. Los documentos permanecen en su ubicación original: no se borran ni se duplican para catalogarlos. Si los mueves o cambias los permisos, vuelve a seleccionar la carpeta.

Los metadatos PDF comunes de título, autor y palabras clave se leen automáticamente. Los metadatos cifrados o guardados dentro de objetos comprimidos pueden no recuperarse; en ese caso se usa el nombre del archivo. Puedes nombrarlos `Título - Autor.pdf` y también corregir sus datos desde **Administración → Asignar archivos y editar datos → Editar**. No se busca en el texto completo del PDF.

## Categorías

Las categorías iniciales son **Exámenes UNHEVAL** y **Libros**.

- **Crear:** Administración → Crear categoría.
- **Cambiar nombre:** toca la categoría → Cambiar nombre.
- **Asignar archivos:** toca la categoría → Asignar archivos → Asignar junto a cada PDF.
- **Editar título, autor, palabras clave o categoría:** Asignar archivos y editar datos → Editar.
- **Eliminar:** toca la categoría → Eliminar categoría. Sus PDF pasan a Sin categoría y no se borran.

Cada archivo se asigna a una categoría o a Sin categoría. El buscador de inicio busca en todas; al entrar en una categoría, la búsqueda se limita a ella. **Ver todos los archivos** también incluye Sin categoría.

## Imágenes del lobito

El logo Grifplot PDF y archivos que enviaste se usa en el icono de Android y en la cabecera.

Ya vienen incluidas tus cinco imágenes transparentes para bienvenida, búsqueda, resultados, alternativas y agradecimiento. Se conserva su proporción y el lobito no aparece sobre la página durante la lectura.

Para cambiarlas: **Administración → Imágenes PNG del lobito → [estado] → Cargar PNG**. Elige un PNG válido, preferiblemente con fondo transparente. Se copia al almacenamiento de la aplicación; luego puedes retirar el archivo de origen. También puedes cargar un sexto PNG para Sin resultados. Por defecto ese estado usa el lobito de bienvenida junto al mensaje de que no hubo resultados.

El botón **Restaurar lobitos incluidos** recupera las imágenes originales. Los mensajes se presentan como texto legible en la interfaz aunque las imágenes también lleven bocadillos.

## Recorrido del cliente y lector rápido

1. En el inicio, toca **Buscar libro**. Escribe por título, autor o palabra clave, o elige una categoría.
2. Toca el recuadro para abrir el teclado grande de la aplicación. Pulsa **Buscar**. Se muestran primero los PDF locales y, debajo, los resultados de Google automáticamente. Abre el archivo que quieres.
3. Desliza lateralmente para pasar de una página a la siguiente o a la anterior, con transición horizontal de libro.
4. La página aparece completa, sin recortar y conservando sus proporciones. Por defecto la tira de miniaturas está **apagada**; solo queda una franja inferior discreta para el número de página y los botones.
5. Pulsa **Miniaturas** para desplegar la tira de **todas** las páginas. Puedes desplazarla a lo largo del libro y tocar cualquier página. Pulsa **Ocultar miniaturas** para recuperar ese espacio.
6. Toca una vez la página para mostrar Volver, Finalizar visita y los botones Anterior/Siguiente. Estos controles vuelven a ocultarse a los cinco segundos para dar más espacio al libro.
7. Usa dos dedos o un doble toque para ampliar. La imagen se refina después del zoom. Toca el indicador **Página X de Y** para saltar a una página concreta.
8. Pulsa **Finalizar visita** en la franja inferior para elegir entre enviar a impresión o volver al inicio.

La lectura usa imágenes ajustadas al tamaño real disponible, caché limitada en memoria y precarga de las páginas vecinas. Las miniaturas tienen menor resolución, se generan solo cuando la función está encendida y se prioriza siempre la página que el cliente quiere leer. Esto reduce el trabajo de renderizado y las esperas repetidas; el tiempo concreto depende del PDF y de la tablet. **No se comprime ni modifica el PDF original**, para conservar el archivo que se enviará a impresión.

## Google y navegador dentro de la aplicación

Cada búsqueda no vacía muestra primero el catálogo local y, a continuación, **Google dentro de Grifplotlibros**, usando el mismo texto escrito, incluidas tildes y signos, con `filetype:pdf`. No hace falta pedir otra búsqueda en internet. Los listados locales y el navegador se muestran en ese orden y se pueden deslizar dentro de su recuadro. Toca **Ampliar ›** en la cabecera de Google para darle toda la pantalla; usa Biblioteca para volver al listado combinado. Sin conexión aparece un aviso y siguen disponibles los libros locales.

Para cambiar la consulta usa el buscador grande de la aplicación. Los enlaces PDF habituales y las respuestas con tipo PDF se detectan y se abren en el lector nativo de la aplicación después de descargar y validar el archivo. Cuando una fuente muestra primero una página, toca su enlace **Descargar PDF**. 

Se muestra el progreso y puedes cancelar. Un enlace que requiere acceso, devuelve HTML, falla o no contiene un PDF válido genera un mensaje. Los PDF descargados quedan en el catálogo y se pueden reutilizar sin descargar de nuevo al abrir la misma dirección. **Volver** desde el lector regresa a los resultados locales y de Google, conservando el navegador.

Google puede mostrar documentos de todo tipo de fuentes. El resultado de una búsqueda no demuestra que un PDF tenga permiso de impresión: usa documentos de dominio público, con licencia abierta o cuya descarga e impresión estén autorizadas por su autor o editorial. Esta versión ya no limita los resultados a Internet Archive ni a doce candidatos. No garantiza que cualquier libro exista en PDF.

Los archivos se guardan en `downloads`, dentro del almacenamiento de la aplicación. Se conserva su enlace de origen. Desinstalar o borrar los datos de la aplicación elimina las descargas y la configuración, pero no los PDF originales de las carpetas seleccionadas. Los enlaces `blob:`, archivos generados exclusivamente dentro de una página, algunos botones que requieren sesión y los PDF protegidos pueden no abrirse; busca un enlace directo público compatible. Las descargas requieren HTTPS y aceptan hasta 1 GB.

## Teclado grande propio

Al entrar en **Buscar libro** o al pulsar **Comencemos ahora** en Buscar imágenes, el teclado aparece automáticamente. También puedes abrirlo de nuevo tocando el recuadro. Al tocar un dato de administración aparece el teclado de Grifplotlibros casi a pantalla completa (98 % del ancho y 96 % del alto). Las letras son **más grandes, en negrita y en teclas cuadradas**. Las letras, la ñ, las vocales acentuadas y los números **0–9 están visibles juntos en una sola pantalla**. No hay botones para cambiar entre ABC y 123, ni panel de símbolos. Espacio, Borrar, Cancelar y Buscar/Listo son controles más pequeños. Los campos numéricos de administración conservan una tecla Decimal para introducir precios. En vertical se distribuyen las letras en menos columnas para mantener su tamaño; en horizontal se mantiene la distribución QWERTY. Tiene letras, ñ, vocales con tilde, números, Espacio, borrar y Buscar/Listo. El recuadro muestra lo escrito y permite colocar el cursor. El teclado de Android queda desactivado dentro de la aplicación. El selector de carpetas o archivos pertenece a Android y conserva sus propios controles.

## Buscar imágenes y enviarlas

1. En el inicio, toca **Buscar imágenes**. Aparece una bienvenida grande: **“¡Bienvenido! Solo toca la imagen que quieres imprimir y listo.”** Pulsa el botón **Comencemos ahora** para abrir el buscador.
2. Toca el recuadro, escribe con el teclado grande y pulsa **Buscar**. Se abre Google Imágenes dentro de la aplicación.
3. Toca cada imagen para seleccionarla: queda marcada en rojo. Vuelve a tocarla para quitarla. La selección se conserva si cambias la consulta; puedes seleccionar hasta 100 imágenes por paquete.
4. **Ver selección** muestra los nombres y enlaces; toca una entrada para quitarla.
5. Pulsa **Enviar por WhatsApp**, o **Finalizar visita → Enviar imágenes por WhatsApp**. Se muestran el progreso y la opción de cancelar.
6. Se intenta descargar primero el **original de la fuente, sin reducir ni recomprimir**. Si no está accesible, se usa la imagen de Google como respaldo. Se crea un **ZIP compatible con WinRAR**, con las imágenes que pudieron descargarse, `enlaces-todas.txt`, `calidad-imagenes.txt` con el tipo y la resolución de cada archivo y, cuando hay fallos o respaldos, `enlaces-pendientes.txt`. Si ninguna se puede descargar, se prepara solo el TXT de enlaces.
7. Se muestra cuántas imágenes se descargaron y cuántas quedaron pendientes. Toca **Abrir WhatsApp** y confirma allí el archivo y el destinatario **+51 952 628 844**. También puedes finalizar con **Luego lo imprimiré, gracias**.

El ZIP es un archivo comprimido que WinRAR abre; no es un archivo `.rar`. No necesita claves de Google ni inicio de sesión en Grifplotlibros. WhatsApp sí debe estar disponible y configurado en la tablet, y el envío se confirma en esa aplicación.

Google cambia su página y no siempre muestra el enlace original. La app intenta recuperar el original y, cuando solo está disponible el enlace de una miniatura, usa esa imagen de menor resolución. Si Google no ofrece ningún enlace utilizable para una imagen, se avisa al tocarla. Algunas fuentes bloquean la descarga, requieren acceso o devuelven una página en vez de una imagen: sus enlaces quedan en el TXT para revisarlos en la PC. No se garantiza descargar todas las imágenes ni obtener siempre su resolución original. Usa imágenes con permiso para el uso y la impresión previstos.

Se admiten descargas HTTPS públicas de PNG, JPG, GIF y WebP, con hasta 50 MB por original, 20 MB por respaldo descargado y 200 MB por paquete. No se guardan páginas HTML como imágenes. Los paquetes se preparan en el almacenamiento temporal de la app para compartirlos. Al volver al inicio se limpia la selección del cliente.

## Precios opcionales por libro

Solo algunos libros necesitan tener precio. Cuando quieras asignarlo, son **precios fijos del libro completo**, sin multiplicar por páginas:

- A4 · Negro
- A4 · Color
- A5 · Negro
- A5 · Color

Para configurarlos: **Administración → Asignar archivos y editar datos → Editar → Precios opcionales del libro completo → Guardar**. Puedes escribir todos, algunos o ninguno de los importes, con hasta dos decimales. La moneda inicial es **S/**; puedes cambiarla en Administración. Un campo vacío significa Consultar precio; escribir `0` significa gratuito. Los libros descargados y los de versiones anteriores empiezan sin precios configurados. La app no inventa importes. **Todas las opciones se pueden enviar aunque no tengan precio**: el mensaje indicará Consultar precio.

## Finalizar visita y enviar a WhatsApp

**Finalizar visita** presenta dos opciones:

1. **Enviar al WhatsApp para impresión:** muestra el título y los cuatro precios. El cliente selecciona A4/A5 y Negro/Color y pulsa **Enviar PDF a WhatsApp**. Se prepara una copia del PDF y se abre WhatsApp o WhatsApp Business con el archivo adjunto y un mensaje que incluye libro, formato y precio fijo. El destino configurado es **+51 952 628 844** (Perú).
2. **Luego lo imprimiré, gracias:** muestra el agradecimiento y vuelve al inicio, limpiando la búsqueda y la visita.

Para enviar, debe haber un PDF abierto durante esa visita y WhatsApp instalado y configurado en la tablet. **Grifplotlibros no pide credenciales**; la configuración de WhatsApp corresponde a esa aplicación. El usuario confirma el envío en WhatsApp. Algunas versiones pueden pedir seleccionar el chat: comprueba que el destinatario sea **+51 952 628 844** antes de pulsar Enviar. Abrir el compositor no confirma que el mensaje haya sido enviado o entregado.

La app adjunta el PDF original junto con la opción de impresión; no recolorea, redimensiona ni envía trabajos directamente a una impresora. Tras abrir WhatsApp se cierra la visita en Grifplotlibros. La copia para compartir se conserva temporalmente para que WhatsApp pueda leerla y se limpia después de un día al preparar otro envío.

## Inactividad y errores

En Administración puedes configurar la inactividad entre 30 y 3600 segundos. El valor inicial es 180 segundos. Aparece un aviso con 20 segundos adicionales para tocar **Continuar**; si no hay respuesta, la visita finaliza y se limpia. También se limpia una búsqueda escrita en el inicio si el cliente la deja abandonada.

Sin internet siguen funcionando el catálogo y la lectura local. Si un PDF está dañado, protegido, no tiene páginas o perdió acceso, aparece un aviso y puedes volver a los resultados. Si una descarga falla, requiere acceso, no contiene PDF o no hay espacio, se informa del problema y no se incorpora el archivo incompleto.

## Proyecto y mantenimiento

El ZIP incluye el APK, esta guía, el proyecto Java nativo para Android Studio y pruebas de búsqueda, metadatos, precios opcionales, descargas PDF y preparación de paquetes de imágenes. No usa servicios de cuentas ni bibliotecas de autenticación.

Para compilar en Android Studio: abre la carpeta `Grifplotlibros`, instala SDK 35 y usa JDK 17. El proyecto usa Android Gradle Plugin 8.7.3; requiere Gradle 8.9 o compatible. También se incluye `build-apk.sh` para compilar con SDK 35, build-tools 35.0.0 y ECJ 3.38.0 sin Gradle.

El APK está firmado para instalación directa. Se incluye la clave de distribución local en `signing/local.jks` para que puedas recompilar actualizaciones compatibles con este APK. Es una clave del proyecto, no una contraseña solicitada al cliente. Conserva el proyecto para mantenimiento; para publicar en una tienda, configura tu propia firma de publicación.
