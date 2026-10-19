# Validación 1.7 · 19 de octubre de 2026

- Pruebas de interacción del inicio: ocho herramientas; primer toque abre explicación; Continuar conserva el destino; Volver devuelve el foco a la herramienta.
- Biblioteca: apertura mediante el puente nativo, prevención de doble apertura y explicación alternativa en la web.
- Publicidad (servicios simulados): recuperación de cuenta, comprobación de correo verificado, publicación confirmada en el listado público, doble toque y reintento sin duplicación. UUID compatible con WebView sin `crypto.randomUUID`; tiempo de espera con mensaje explícito.
- Cliente (servicio simulado): un fallo de lectura muestra Reintentar y no una falsa ausencia de anuncios; la recuperación vuelve a mostrar la tarjeta de publicidad.
- Notificaciones (servicio simulado): estado pendiente impide prometer un envío; configuración activa conserva el envío autenticado a la función existente y requiere confirmación del servidor.
- Pruebas Java del módulo recibido: búsqueda (10), navegador y precios (13), paquetes de imágenes (18). JavaScript: selección de imágenes y recuperación de su URL original (6).
- Comprobación pública real: los tres módulos Firebase JS 13.0.0 respondieron HTTP 200; Firestore del proyecto grafiplot-publicidad respondió HTTP 200 y cero anuncios en la consulta del cliente. No se publicó publicidad de prueba a los clientes.
- Estado real del servicio de notificaciones: `pushEnabled:false`, proyecto grafiplot-publicidad. No se han creado credenciales privadas ni activado un nuevo proyecto Android durante esta actualización.

El acceso de la cuenta propietaria y una publicación real están pendientes de iniciar sesión. Tampoco se han probado la entrega de notificaciones en un teléfono físico ni la impresora real. La compilación sin firma permite comprobar el paquete, pero para entregar actualizaciones instalables se necesita el respaldo de firma original de Grafiplot.

Compilación final: **BUILD SUCCESSFUL**, 93 tareas, 28 segundos. Comprobado en ambos paquetes: recursos web idénticos al código final, ausencia del chat y presencia de la biblioteca exclusivamente en clientes. Los dos paquetes están sin firma de distribución, pendientes del respaldo original.

Mi Delivery: acceso nativo al sitio original, almacenamiento de sesión mediante Firebase Auth local para clientes que lo eligen dentro de la APK, y cierre de sesión disponible. La cuenta de administración de delivery conserva persistencia de sesión. Pruebas del sitio de delivery: 29 (incluyen selección de persistencia, privacidad de pedidos y recuperación de cuentas). No se han introducido ni probado credenciales reales de clientes.
