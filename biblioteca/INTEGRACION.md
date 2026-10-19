# Libros e imágenes · Grafiplot Clientes 1.7

El módulo nativo del ZIP `Grifplotlibros-completo (1).zip` se incorpora en la misma APK de clientes. No requiere instalar una segunda aplicación. En el inicio, toca **Libros e imágenes**, revisa la explicación y pulsa **Continuar**.

Incluye catálogo de PDF por carpetas y categorías, exámenes UNHEVAL, lector con zoom y miniaturas, búsqueda de PDF en Google, selección de imágenes, preparación de ZIP y solicitud de impresión por WhatsApp al +51 952 628 844. El usuario confirma el envío en WhatsApp. No se ha añadido impresión automática de libros ni cambiado los límites de Impresión Rápida.

En **Configurar**, el usuario elige las carpetas autorizadas y puede editar categorías y precios opcionales. Estos datos se guardan en cada dispositivo. No se transfieren automáticamente los datos privados de la app independiente Grifplotlibros. Para usar los mismos PDF, selecciona sus carpetas desde la nueva biblioteca. No se requieren correo ni contraseña para la biblioteca.

**Volver a herramientas** regresa al menú principal. El botón Atrás desde la bienvenida de biblioteca también vuelve al menú. La APK de administrador conserva únicamente sus accesos de Publicidad y recuperación de cuenta; el código, los recursos, la actividad y el proveedor de biblioteca se incluyen solo en la variante `cliente`.

En la página web se explica que el lector nativo y el catálogo local necesitan la APK Android. Las demás herramientas web conservan sus rutas.

## Mantenimiento

- Java y assets: `android-principal/app/src/cliente/`.
- Actividad interna: `com.grifplot.libros.MainActivity`, sin exportación pública.
- Compartición: proveedor privado `${applicationId}.libros.share`, con permisos temporales de lectura. Así puede coexistir con la app independiente.
- El icono principal de Grafiplot se conserva. El logo de Grifplotlibros y los lobitos se usan dentro del módulo.
- La clave incluida en el ZIP de Grifplotlibros **no** se usa para firmar Grafiplot Clientes. Para actualizar Grafiplot se necesita su respaldo de firma original.
- Versión propuesta: 1.7, código 11. Compilación: Java 17, SDK 35, Build Tools 35.0.0 y Gradle 8.9.

La guía original se conserva como referencia en `GUIA-ORIGINAL.md`; sus referencias a la instalación independiente y a su clave corresponden al ZIP original, no a esta integración.
