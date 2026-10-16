# Grafiplot · Chat sin Blaze

Proyecto Supabase `intsmwiivbdaversvlky`, organización `tiendasroy´s`, región us-east-2. Costo cotizado al crear: US$0/mes, dentro de las cuotas gratuitas. Firebase permanece en Spark; no se activó facturación.

## Ruta activa

- Firebase Authentication autentica correo y contraseña. El navegador usa persistencia de sesión; el usuario puede cerrar sesión.
- La función Supabase `grafiplot-chat` verifica firma RS256, emisor, audiencia, caducidad y sujeto del JWT de Firebase. No depende de Cloud Functions ni del plan Blaze.
- PostgreSQL guarda conversaciones, mensajes, dispositivos y avisos. Todas las tablas tienen RLS y acceso directo denegado a `anon` y `authenticated`. Solo el servidor con su credencial interna puede consultarlas, después de autorizar al usuario.
- Un cliente solo accede a su propia conversación. La bandeja y las respuestas administrativas exigen un correo verificado incluido en `grafiplot_admins` y activo. Inicialmente: `vasquezpalparoy@gmail.com`, la cuenta del propietario usada para configurar Firebase. No se autoriza por nombre ni por metadatos editables del cliente.
- Los mensajes son de texto, hasta 4000 caracteres. El cliente consulta actualizaciones cada 7 segundos mientras la pestaña está visible, y al enviar o regresar a la pestaña. No se utiliza WebSocket en esta versión. Se muestran los últimos 100 mensajes y hasta 100 conversaciones recientes.
- Un UUID conserva la identidad del intento en los reintentos; un bloqueo de transacción evita duplicados concurrentes. Límite de 30 mensajes/minuto por remitente. El botón indica guardado cuando el servidor confirma la transacción.
- El chat no tiene cifrado de extremo a extremo. Los PDF de las herramientas siguen procesándose en el dispositivo. Adjuntos de imágenes/PDF en el chat todavía no implementados.

## Administrador

Abre `chat.html`, crea o inicia sesión con el correo del propietario y pulsa **Verificar mi correo** si es necesario. Tras verificarlo, pulsa **Actualizar acceso**. El servidor comprobará de nuevo la condición de administrador. La contraseña se introduce personalmente en la aplicación; no se incluye en el código ni en la APK.

Para retirar el acceso, cambia `active=false` en `grafiplot_admins`; el servidor revisa esta tabla en cada solicitud. Para añadir otro administrador debe autorizarlo el propietario. No existe PIN público.

## Notificaciones Android pendientes de credencial

Firebase Cloud Messaging permite entregar notificaciones sin Blaze. El servidor de Supabase necesita una credencial de una cuenta de servicio de Google Cloud dedicada al envío de FCM en `grafiplot-herramientas`. Debe tener únicamente el rol que permite enviar mensajes FCM. La creación de esa credencial y su transferencia como secreto requieren autorización del propietario.

Guardar su JSON únicamente como secreto `GRAFIPLOT_FIREBASE_SERVICE_ACCOUNT` en Supabase. No incluirlo en GitHub, `push-config.json`, la APK ni archivos entregados a clientes. La función usa OAuth con el alcance `firebase.messaging`, conserva el token temporal en memoria y comprueba el ID del proyecto. No registra la credencial.

Sin ese secreto el chat funciona abierto, pero la función informa `pushEnabled:false`. `chatPushEnabled` y `announcementsEnabled` permanecen desactivados en la configuración pública. Tras configurar y probar el secreto, activa ambos. Los avisos masivos se envían al tema `grafiplot_clientes`; los mensajes del chat se envían a los dispositivos del destinatario. Las notificaciones privadas contienen un aviso genérico y nunca el texto de la conversación.

La APK vincula cada dispositivo a la cuenta autenticada, comprueba el destinatario antes de mostrar un aviso privado y limpia su vinculación local al cerrar sesión. Al conceder el permiso de Android por primera vez hay que volver a pulsar Activar para completar la vinculación. Sin una prueba en un teléfono físico no se afirma entrega real en segundo plano.

## Despliegue y revisión

`schema.sql` documenta el esquema final; las migraciones remotas registradas son `grafiplot_private_chat` y `grafiplot_deny_direct_client_access`. No ejecutar el archivo completo sobre un proyecto existente sin revisar las tablas primero. `functions/grafiplot-chat/index.ts` es el servidor desplegado. `verify_jwt=false` desactiva únicamente la comprobación automática de JWT Supabase en el gateway: la función aplica su propia autenticación Firebase antes de todas las operaciones privadas.

Las comprobaciones SQL confirmaron RLS en las cinco tablas y ausencia de SELECT para los roles de cliente. El asesor de seguridad devolvió cero avisos tras aplicar las políticas. No se configuraron cuentas de pago ni cargos automáticos.

Referencias: https://supabase.com/pricing ; https://supabase.com/docs/guides/functions/function-configuration ; https://firebase.google.com/docs/auth/admin/verify-id-tokens ; https://firebase.google.com/docs/cloud-messaging/auth-server
