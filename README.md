# Grafiplot · Herramientas

Página de herramientas para clientes de Grafiplot. Incluye un editor del FUT de UNHEVAL y un creador de pósteres dividido en hojas A4.

## Funciones

- Completar los 12 recuadros del FUT.
- Vista previa en tiempo real y selección de campos desde el documento.
- Ajuste de líneas y tamaño de letra al espacio disponible.
- Firma con ratón o dedo.
- Descarga del PDF sobre el formulario original.
- Diseño adaptable a móviles y computadoras.

Los datos se procesan en el dispositivo del visitante. No se envían a un servidor ni se guardan entre sesiones.

## Crear pósteres

Abre `poster.html` desde el servidor o usa el enlace Crear póster en la página del FUT.

- Carga una imagen JPG, PNG o WEBP, o selecciona una página de un PDF.
- Elige 2, 4, 8 o 16 hojas A4, o una distribución de hasta 10 por 10.
- Pasa el cursor o toca las tarjetas de colores para ver hojas, distribución y medidas en centímetros en una ventana flotante. Se puede cerrar con la X, Escape o tocando fuera.
- Carga imágenes por un enlace directo cuando el sitio de origen permita la descarga desde el navegador.
- Mantén la proporción, recorta para llenar o estira la imagen.
- Gira el póster, incluye márgenes de 5 mm y guías de corte.
- Descarga un PDF con una pieza por página A4.
- Imprime al 100 %, recorta y arma según la numeración.

Las equivalencias A3, A2, A1 y A0 son referencias sin márgenes. A1 y A0 ensamblados con A4 difieren hasta 1 mm del estándar. La aplicación muestra el área real después de recortar los márgenes. Los PDF conservan el contenido original, incluida la rotación de página. Las imágenes grandes se limitan a 4096 píxeles en su lado mayor y 8 megapíxeles para facilitar el uso en móviles.

PDF.js 4.10.38 se distribuye bajo licencia Apache 2.0 (`PDFJS-LICENSE.txt`).

## Ordenar PDF

Abre `order.html`. Carga un PDF de hasta 50 MB y 200 páginas; mueve las miniaturas con el mouse o con el botón Arrastrar en pantallas táctiles. Al confirmar una posición con Enter o salir del campo, esa página intercambia su lugar con la que ocupaba dicha posición. También puedes pulsar Aplicar números. Las posiciones sirven para ordenar, no se imprimen. Incluye controles de movimiento con teclado y restauración del orden original. Las miniaturas se generan cuando se acercan a la pantalla. La vista previa permite recorrer el PDF final, elegir una página y descargar esa misma versión. La descarga copia las páginas originales conservando su contenido, tamaño y rotación. El archivo se procesa localmente.

## Publicar con GitHub Pages

En **Settings → Pages**, selecciona **Deploy from a branch**, rama **main** y carpeta **/(root)**.

## Ejecutar localmente

Sirve esta carpeta con cualquier servidor de archivos estáticos. Por ejemplo:

```bash
python -m http.server 8000
```

Abre `http://localhost:8000`. No requiere instalación de paquetes.

## Archivos

- `index.html`: interfaz en español.
- `style.css`: diseño adaptable.
- `app.js`: edición, ajuste de texto, firma y exportación.
- `fut-original.pdf`: formulario original de UNHEVAL.
- `fut-preview.png`: imagen del formulario para la vista previa.
- `pdf-lib.min.js`: PDF-Lib 1.17.1, bajo licencia MIT.

## Fuente del formulario

Universidad Nacional Hermilio Valdizán: https://webs.unheval.edu.pe/public/filemanager/files/DOCUMENTOS-GENERALES/FUT.pdf

Grafiplot no es una página oficial de UNHEVAL. Revisa los datos y requisitos de tu trámite antes de presentar el formulario.
