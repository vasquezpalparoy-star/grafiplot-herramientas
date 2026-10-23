# Publicidad en texto o solo imagen

El administrador puede elegir «Texto (con fondo opcional)» o «Solo imagen desde un enlace». El modo imagen pide un enlace HTTPS público y exige que la vista previa cargue antes de publicar. Permite una descripción opcional para notificaciones y lectores de pantalla. No superpone títulos, mensajes, animaciones ni botones sobre la imagen.

El cliente muestra la imagen completa con `object-fit:contain` en la tarjeta de media pantalla. Si su proporción es distinta, se conserva completa con márgenes. Al tocarla se abre el destino configurado. Si deja de estar disponible, se muestra un aviso de error.

Se mantiene el esquema vigente: `style:imagen`, `imageUrl`, título de accesibilidad y mensaje de notificación no vacíos. No se cambian reglas ni permisos de Firebase. Las publicaciones existentes del estilo imagen pasan a mostrarse sin texto superpuesto en clientes 1.11; clientes anteriores conservan su presentación anterior.

Validación: publicación con texto, modo imagen sin título ni cuerpo manuales, bloqueo antes de carga y ante error de imagen, publicación después de carga, destino personalizado, presentación sin superposición y errores posteriores de imagen. No se publicaron anuncios reales durante las pruebas.
