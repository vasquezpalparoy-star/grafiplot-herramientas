(()=>{
 const native=window.AndroidPrint&&typeof AndroidPrint.notificationStatus==='function';
 function refresh(){
  const home=document.querySelector('#pushHome'),labels=document.querySelectorAll('[data-notification-status]');
  if(!native){if(home)home.hidden=true;labels.forEach(el=>el.textContent='Activa los avisos desde la APK de Grafiplot para recibir notificaciones.');document.querySelectorAll('[data-enable-notifications],[data-disable-notifications],[data-test-notification],[data-notification-settings]').forEach(b=>b.hidden=true);return;}
  let s;try{s=JSON.parse(AndroidPrint.notificationStatus())}catch{return;}
  const ready=s.permission&&s.consent&&s.subscribed&&s.channelEnabled!==false;
  if(home){home.hidden=false;home.classList.toggle('ready',!!ready)}
  const text=s.registering?'Conectando este celular con Firebase…':s.error?s.error:ready?'Notificaciones activadas en este celular.':!s.configured||s.initialized===false?'Preparando la conexión de notificaciones…':s.consent&&!s.permission?'Activa el permiso en Ajustes de Android.':s.channelEnabled===false?'El canal de avisos está bloqueado en Android.':'Activa los avisos para recibir novedades en tu celular.';
  labels.forEach(el=>{el.textContent=text;el.classList.toggle('good',!!ready)});
  document.querySelectorAll('[data-enable-notifications]').forEach(b=>{b.disabled=!s.configured||s.initialized===false||s.registering;b.textContent=s.registering?'Conectando…':s.error?'Reintentar activación':'Activar avisos';b.hidden=!!ready});
  const d=document.querySelector('#pushDiagnostics');if(d)d.textContent=['Proyecto: '+(s.project||'sin configurar'),'APK: '+(s.package||''),'Permiso Android: '+(s.permission?'sí':'no'),'Registro Firebase: '+(s.hasToken?'obtenido':'pendiente'),'Suscripción: '+(s.subscribed?'confirmada':'pendiente'),'Canal Android: '+(s.channelEnabled===false?'bloqueado':'disponible'),s.error?'Error: '+s.error:''].filter(Boolean).join('\n');
 }
 document.querySelectorAll('[data-enable-notifications]').forEach(b=>b.onclick=()=>{if(native)AndroidPrint.enableNotifications();setTimeout(refresh,300)});
 document.querySelectorAll('[data-disable-notifications]').forEach(b=>b.onclick=()=>{if(native)AndroidPrint.disableNotifications();refresh()});
 document.querySelectorAll('[data-test-notification]').forEach(b=>b.onclick=()=>{if(native&&typeof AndroidPrint.testNotification==='function')AndroidPrint.testNotification()});
 document.querySelectorAll('[data-notification-settings]').forEach(b=>b.onclick=()=>{if(native&&typeof AndroidPrint.notificationSettings==='function')AndroidPrint.notificationSettings()});
 window.GrafiplotNotificationUI={refresh};window.addEventListener('grafiplot-push-state',refresh);document.addEventListener('visibilitychange',()=>{if(!document.hidden)refresh()});refresh();setTimeout(refresh,1200);setTimeout(refresh,5000);
})();
