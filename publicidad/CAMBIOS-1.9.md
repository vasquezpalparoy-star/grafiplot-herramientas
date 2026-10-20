# Publicidad 1.9

- El anuncio destacado ocupa como mínimo la mitad de la altura visible del cliente. El texto largo se puede desplegar con «Leer completo».
- Cada diseño tiene título flotante, flecha animada y brillo suave. Se puede pausar el movimiento; se respeta la preferencia del sistema de reducir animaciones.
- En el administrador se puede seleccionar «Página personalizada» y configurar un enlace HTTPS de hasta 250 caracteres. Toda la tarjeta y su botón llevan al mismo destino; los controles de lectura y pausa mantienen su propia función.
- Las notificaciones recibidas por la APK 1.9 abren también ese destino personalizado. No se ha cambiado el transporte FCM pendiente del servidor.

## Compatibilidad

Se conserva el esquema y las reglas desplegadas de Firestore: `target` usa `pagina-web` y el enlace se guarda como pie `\n\nEnlace del anuncio: https://…` en `body`. La APK y la web 1.9 separan ese pie del texto visible. Las versiones anteriores pueden mostrarlo como texto y conservan su destino de página web anterior. El formulario reserva espacio dentro del máximo de 1000 caracteres del servidor. No se añaden campos ni se relajan permisos. Los anuncios anteriores siguen funcionando sin cambios.

## Validación

Pruebas de publicación, consulta del feed, enlace personalizado, rechazo de URL insegura, escape de HTML, pausa de animación, lectura completa y destino Android. Compilación de ambas ediciones y verificación criptográfica con la firma original. La entrega real de notificaciones y el comportamiento en un teléfono físico requieren una prueba en el dispositivo.
