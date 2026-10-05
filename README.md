# Grafiplot · Herramientas

Página de herramientas para clientes de Grafiplot. La primera herramienta permite completar el Formulario Único de Trámite Virtual (FUT) de UNHEVAL y descargarlo en PDF.

## Funciones

- Completar los 12 recuadros del FUT.
- Vista previa en tiempo real y selección de campos desde el documento.
- Ajuste de líneas y tamaño de letra al espacio disponible.
- Firma con ratón o dedo.
- Descarga del PDF sobre el formulario original.
- Diseño adaptable a móviles y computadoras.

Los datos se procesan en el dispositivo del visitante. No se envían a un servidor ni se guardan entre sesiones.

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
