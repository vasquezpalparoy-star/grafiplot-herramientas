# Transición propuesta al nuevo Firebase

Firebase principal de acceso: `grafiplot-publicidad`. Administrador autorizado: `grafiplotvasquez@gmail.com`, con correo verificado. Los clientes siguen entrando únicamente con nombre mediante Authentication anónimo. Publicidad y chat del administrador comparten la sesión de ese proyecto. Las herramientas FUT, Carátulas, PDF e impresión no cambian.

Los mensajes continúan en el servidor Supabase existente. La tabla de vinculación propuesta asocia la identidad nueva con la conversación anterior solo después de comprobar ambas firmas JWT. No copia, reasigna ni elimina mensajes. Los tokens antiguos siguen siendo compatibles durante la transición. Un nuevo usuario sin sesión anterior conserva una conversación independiente.

## Validación

- SQL ejecutado en PostgreSQL local con PGlite: preserva historial, idempotencia, denegación de una segunda vinculación a otro usuario y aislamiento de roles públicos.
- 15 comprobaciones del controlador con JWT RS256 locales de prueba: firma, emisor, audiencia, correo verificado, acceso por proyecto, conversaciones ajenas y vinculación de historial.
- Compilación de ambas variantes Android.

Pruebas reproducibles: `npm install --prefix supabase/tests` y `npm test --prefix supabase/tests`.

## Activación pendiente

La revisión automática rechazó sustituir inmediatamente el servicio de producción al faltar pruebas de compatibilidad. Esta propuesta incorpora esas pruebas y queda en una rama de revisión, sin activar el cambio en la página publicada. Las llamadas de escritura a Supabase devolvieron `Invalid or expired requestState`; una consulta posterior confirmó que la tabla de vinculación aún no existe.

Para activar: aprobar el cambio, aplicar `migrate-chat-identity.sql`, comprobar permisos, desplegar la función actualizada, probar con cuentas sintéticas de ambos proyectos y publicar web y APK. La firma privada no se incluye. Las notificaciones con la app cerrada siguen pendientes.
