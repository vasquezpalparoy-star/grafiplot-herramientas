const A=GrafiplotAvisos,$=s=>document.querySelector(s);let configured=false,busy=false;
async function refresh(){
 if(busy)return;busy=true;$('#refresh').disabled=true;$('#feedStatus').textContent='Actualizando publicidad…';
 try{
  const list=[],config=await A.config();configured=!!config.enabled;
  const native=window.AndroidPrint&&typeof AndroidPrint.notificationStatus==='function';
  if(native){
   try{const status=JSON.parse(AndroidPrint.notificationStatus());$('#pushStatus').textContent=!config.notificationsEnabled?'La publicidad se muestra dentro de la aplicación. Las notificaciones con la app cerrada están pendientes de activación.':!status.configured?'Las notificaciones del teléfono no están configuradas.':status.subscribed&&status.permission?'Notificaciones activadas para este teléfono.':'Puedes activar las notificaciones de Grafiplot en este teléfono.';$('#pushStatus').classList.toggle('good',status.configured&&status.subscribed&&status.permission);configured=status.configured&&config.notificationsEnabled;list.push(...JSON.parse(AndroidPrint.notificationMessages()))}catch(e){$('#pushStatus').textContent='Las notificaciones del teléfono no están disponibles. Puedes consultar la publicidad aquí.';configured=false}
  }else{$('#pushStatus').textContent='La publicidad se consulta dentro de la aplicación o en esta página. Las notificaciones con la app cerrada todavía no están activadas.'}
  $('#enablePush').disabled=!native||!configured;$('#disablePush').disabled=!native;
  let feedFailed=false;
  try{if(config.announcementsEnabled&&(config.advertisingFirebase?.projectId||/^https:\/\//.test(config.feedUrl||'')))list.unshift(...await A.feed(config))}catch(e){feedFailed=true;$('#feedStatus').textContent='No se pudo consultar la publicidad. Revisa Internet y pulsa Actualizar. Esto no significa que no existan anuncios.'}
  const seen=new Set(),messages=list.filter(m=>{const id=m.announcement_id||m.id;if(seen.has(id))return false;seen.add(id);return true});
  if(!feedFailed||messages.length)$('#messages').innerHTML=messages.length?messages.map(m=>A.card(m)).join(''):'<p class="inbox-empty">Todavía no hay anuncios publicados.</p>';
  if(!feedFailed)$('#feedStatus').textContent=messages.length?'Publicidad actualizada: '+messages.length+' anuncio(s).':'Conexión correcta. Todavía no hay anuncios publicados en Grafiplot.';
 }catch(e){$('#feedStatus').textContent='No se pudo cargar la configuración de publicidad. Pulsa Actualizar para volver a intentarlo.'}
 finally{busy=false;$('#refresh').disabled=false}
}
$('#enablePush').onclick=()=>{AndroidPrint.enableNotifications();setTimeout(refresh,1500)};$('#disablePush').onclick=()=>{AndroidPrint.disableNotifications();setTimeout(refresh,300)};$('#refresh').onclick=refresh;document.addEventListener('visibilitychange',()=>{if(!document.hidden)refresh()});refresh();
