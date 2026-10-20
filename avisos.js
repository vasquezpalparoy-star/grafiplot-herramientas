const A=GrafiplotAvisos,$=s=>document.querySelector(s);let configured=false,busy=false;
async function refresh(){
 if(busy)return;busy=true;$('#refresh').disabled=true;$('#feedStatus').textContent='Actualizando publicidad…';
 try{
  const list=[],config=await A.config();configured=!!config.enabled;
  if(window.AndroidPrint&&typeof AndroidPrint.notificationMessages==='function')try{list.push(...JSON.parse(AndroidPrint.notificationMessages()))}catch{}
  window.GrafiplotNotificationUI?.refresh();
  let feedFailed=false;
  try{if(config.announcementsEnabled&&(config.advertisingFirebase?.projectId||/^https:\/\//.test(config.feedUrl||''))){const fresh=await A.feed(config);list.length=0;list.push(...fresh)}}catch(e){feedFailed=true;$('#feedStatus').textContent='No se pudo consultar la publicidad. Revisa Internet y pulsa Actualizar. Esto no significa que no existan anuncios.'}
  const seen=new Set(),messages=list.filter(m=>{const id=m.announcement_id||m.id;if(seen.has(id))return false;seen.add(id);return true});
  if(!feedFailed||messages.length)$('#messages').innerHTML=messages.length?messages.map(m=>A.card(m)).join(''):'<p class="inbox-empty">Todavía no hay anuncios publicados.</p>';
  if(!feedFailed)$('#feedStatus').textContent=messages.length?'Publicidad actualizada: '+messages.length+' anuncio(s).':'Conexión correcta. Todavía no hay anuncios publicados en Grafiplot.';
 }catch(e){$('#feedStatus').textContent='No se pudo cargar la configuración de publicidad. Pulsa Actualizar para volver a intentarlo.'}
 finally{busy=false;$('#refresh').disabled=false}
}
$('#refresh').onclick=refresh;document.addEventListener('visibilitychange',()=>{if(!document.hidden)refresh()});refresh();
