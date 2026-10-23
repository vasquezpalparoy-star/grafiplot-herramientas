# Grafidocumentos en Grafiplot Herramientas 1.10

Se integraron los recursos de `Grafidocumentos-WhatsApp (1).apk`, adjuntado por el propietario, como novena herramienta. Conserva los editores de Word, Excel, PowerPoint y PDF, sus bibliotecas locales y sus licencias. No requiere instalar otra aplicación para abrir el editor desde Grafiplot.

La adaptación `native-adapter.js` conecta el guardado y la impresión con Grafiplot. El puente nativo adjunta copias de hasta 50 MB a WhatsApp o al selector de aplicaciones. El usuario revisa el destinatario y confirma el envío en WhatsApp. Un proveedor privado concede acceso de solo lectura al archivo adjunto, no a carpetas ni a documentos ajenos. Los archivos temporales de más de siete días se limpian al preparar otro envío.

Los archivos y sesiones permanecen en el dispositivo. El menú de clientes conserva los anuncios y las notificaciones. El administrador conserva su panel de publicidad.

Las pruebas verifican navegación, recursos locales, guardado de Unicode, adaptación de exportación/impresión, validación del número y formato WASM. La apertura de documentos complejos y el envío con WhatsApp deben comprobarse en un teléfono; la integración no promete conservar todos los formatos avanzados de Microsoft Office.
