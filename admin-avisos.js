import {advertisingServices,advertisingAdmin,accessError} from './publicidad-auth.js?v=1.7';
import {publicationId,waitForPublication,notifyAdvertisement,deleteAdvertisement} from './publicidad-utils.mjs?v=1.8';
const A=GrafiplotAvisos,$=s=>document.querySelector(s);
let service,allowed=false,requestId=null,busy=false;
function message(){return {title:$('#title').value.trim(),body:$('#body').value.trim(),style:$('#style').value,imageUrl:A.imageUrl($('#imageUrl').value),target:$('#target').value}}
function preview(){$('#imageStatus').textContent=A.imageUrl($('#imageUrl').value)?'Cargando vista previa…':'Usa un enlace HTTPS directo o un archivo de Google Drive compartido públicamente.';if(!busy)requestId=null;$('#preview').innerHTML=A.card(message());$('#preview').firstElementChild.classList.add('featured-card');$('#sendButton').disabled=!allowed||busy}
$('#messageForm').oninput=preview;$('#messageForm').onchange=preview;
async function session(user){
 allowed=advertisingAdmin(user);
 $('#sendButton').disabled=!allowed||busy;
 $('#logoutButton').classList.toggle('hidden',!user);
 if(allowed)loadPublished();else{$('#publishedList').replaceChildren();$('#publishedStatus').textContent='Inicia sesión con tu cuenta verificada para administrar tus publicaciones.';}
 const owner=user?.email?.toLowerCase()==='grafiplotvasquez@gmail.com';
 $('#sendVerification').hidden=!owner||user.emailVerified;
 $('#refreshAccess').hidden=!owner;
 $('#loginStatus').textContent=allowed?'Cuenta verificada y conectada. Ya puedes publicar.':user?owner?'Falta verificar tu correo. Pulsa Enviar verificación y revisa también Spam. Después pulsa Ya verifiqué mi correo.':'Este correo no tiene acceso. Usa grafiplotvasquez@gmail.com.':'Entra con grafiplotvasquez@gmail.com. Si aún no creaste tu cuenta en esta aplicación, pulsa Crear mi acceso.';
}
async function access(task){
 if(busy)return;
 if(!service){$('#accessNotice').textContent='La conexión aún no está lista. Revisa el aviso de configuración de arriba.';return;}
 busy=true;$('#loginButton').disabled=true;$('#sendButton').disabled=true;
 try{await task()}catch(e){$('#accessNotice').textContent=accessError(e)}
 finally{busy=false;$('#loginButton').disabled=false;$('#sendButton').disabled=!allowed}
}
$('#loginForm').onsubmit=e=>{e.preventDefault();access(async()=>{
 try{const c=await service.sdk.signInWithEmailAndPassword(service.auth,$('#email').value.trim(),$('#password').value);await c.user.reload();await c.user.getIdToken(true);await session(c.user);$('#accessNotice').textContent='';}
 finally{$('#password').value=''}
})};
$('#recoverAccount').onclick=()=>access(async()=>{
 if(!$('#email').reportValidity())return;
 const email=$('#email').value.trim().toLowerCase();
 if(email!=='grafiplotvasquez@gmail.com')throw Error('Para recuperar la cuenta del administrador, usa grafiplotvasquez@gmail.com.');
 await service.sdk.sendPasswordResetEmail(service.auth,email);
 $('#accessNotice').textContent='Si ya creaste tu cuenta en esta aplicación, recibirás un enlace para elegir una nueva contraseña. Revisa Recibidos y Spam. Si nunca la creaste, pulsa Crear mi acceso.';
});
$('#sendVerification').onclick=()=>access(async()=>{
 await service.sdk.sendEmailVerification(service.auth.currentUser);
 $('#accessNotice').textContent='Enlace enviado. Abre tu correo y revisa también Spam. Después pulsa Ya verifiqué mi correo.';
});
$('#refreshAccess').onclick=()=>access(async()=>{
 const user=service.auth.currentUser;if(!user)return;
 await user.reload();await user.getIdToken(true);await session(user);
 $('#accessNotice').textContent=allowed?'Acceso actualizado. Puedes publicar.':'El correo todavía aparece sin verificar.';
});
$('#checkConnection').onclick=()=>access(async()=>{
 $('#accessNotice').textContent='Comprobando el acceso y la lectura de publicidad…';
 const user=service.auth.currentUser;
 if(user){await user.reload();await user.getIdToken(true);await session(user)}
 const feed=await A.feed(service.config);
 $('#accessNotice').textContent='Lectura de publicidad correcta: '+feed.length+' anuncio(s). '+(allowed?'Cuenta verificada; revisa tu anuncio y pulsa Publicar anuncio.':user?'Completa la verificación de tu correo para publicar.':'Inicia sesión con tu cuenta de administrador para publicar.');
});
$('#logoutButton').onclick=()=>access(async()=>{await service.sdk.signOut(service.auth);$('#accessNotice').textContent='Sesión cerrada.'});
$('#messageForm').onsubmit=async e=>{
 e.preventDefault();if(busy)return;
 if(!service||!allowed||!service.auth.currentUser){$('#sendStatus').textContent='Primero inicia sesión y verifica tu correo de administrador.';return;}
 const msg=message(),notify=$('#notify').checked;
 if(!msg.title||!msg.body){$('#sendStatus').textContent='Completa el título y el mensaje.';return;}
 if($('#imageUrl').value.trim()&&!msg.imageUrl){$('#sendStatus').textContent='El enlace de imagen debe usar HTTPS y ser público.';return;}
 if(msg.style==='imagen'&&!/^https:\/\//.test(msg.imageUrl)){$('#sendStatus').textContent='Añade un enlace HTTPS para la imagen.';return;}
 if(!confirm(notify?'¿Publicar y enviar una notificación a los clientes que la tengan activada?':'¿Publicar solo dentro de la aplicación, sin notificación al teléfono?'))return;
 busy=true;for(const field of $('#messageForm').elements)field.disabled=true;
 $('#sendStatus').textContent='Publicando…';
 try{
  requestId=requestId||publicationId();
  const user=service.auth.currentUser;
  await waitForPublication(user.reload());await waitForPublication(user.getIdToken(true));
  if(!advertisingAdmin(user))throw Object.assign(Error('Verifica tu correo antes de publicar.'),{code:'permission-denied'});
  const f=service.fs,ref=f.doc(service.db,'advertisements',requestId);
  const existing=await waitForPublication(f.getDocFromServer(ref));
  if(!existing.exists())await waitForPublication(f.setDoc(ref,{...msg,createdAt:f.serverTimestamp(),authorUid:user.uid}));
  else if(existing.data().authorUid!==user.uid)throw Error('No se pudo confirmar este intento. Cambia el mensaje y vuelve a publicar.');
  $('#sendStatus').textContent='Anuncio guardado. Comprobando que esté disponible para los clientes…';
  try{
   const feed=await waitForPublication(A.feed(service.config));
   const visible=feed.some(ad=>ad.announcement_id===requestId);
   $('#sendStatus').textContent=visible?'Publicado y disponible para los clientes. Aparecerá en el inicio al abrir la aplicación o pulsar Actualizar anuncios. No se envió una notificación al teléfono.':'Anuncio guardado. Abre Avisos o actualiza el inicio del cliente para comprobarlo.';
  }catch(e){$('#sendStatus').textContent='Anuncio guardado, pero no se confirmó la consulta del cliente. Revisa tu conexión y abre Avisos antes de volver a publicar.';}
  if(notify){try{await notifyAdvertisement(service,requestId,msg);$('#sendStatus').textContent='Anuncio publicado. Firebase aceptó el envío de la notificación a los clientes que la tengan activada. La entrega depende de la conexión y de los permisos del teléfono.';}catch(e){$('#sendStatus').textContent='Anuncio guardado. Notificación sin confirmar: '+accessError(e)+' Revisa el estado antes de repetir el envío.';}}
  await loadPublished();
 }catch(e){$('#sendStatus').textContent=accessError(e)+' Reintenta sin modificar el anuncio para comprobar el mismo intento.';}
 finally{busy=false;for(const field of $('#messageForm').elements)field.disabled=false;$('#notify').disabled=!service.config.notificationsEnabled;$('#sendButton').disabled=!allowed;}
};
(async()=>{preview();try{
 service=await advertisingServices();service.sdk.onAuthStateChanged(service.auth,session);
 $('#configStatus').textContent='Grafiplot Publicidad conectado · versión 1.8. Los anuncios aparecen dentro de la app de clientes.';$('#configStatus').classList.add('good');$('#notify').disabled=!service.config.notificationsEnabled;$('#notify').checked=!!service.config.notificationsEnabled;$('#notifyLabel').textContent=service.config.notificationsEnabled?'Enviar también una notificación al teléfono':'Notificaciones al teléfono pendientes de conexión';
}catch(e){$('#configStatus').textContent=accessError(e)}})();

let deleting=false,publicationLoad=0;
async function loadPublished(){
 if(!service||!allowed)return;
 const sequence=++publicationLoad;$('#publishedStatus').textContent='Consultando publicaciones…';
 try{const ads=await A.feed(service.config);if(!allowed||sequence!==publicationLoad)return;$('#publishedList').replaceChildren();
  for(const ad of ads){const row=document.createElement('div');row.className='published-row';const text=document.createElement('span');const title=document.createElement('strong');title.textContent=ad.title;text.append(title);const date=document.createElement('small');date.textContent=ad.createdAt?new Date(ad.createdAt).toLocaleString('es-PE'):'Publicado';text.append(date);const button=document.createElement('button');button.type='button';button.className='secondary';button.textContent='Eliminar';button.onclick=()=>removePublished(ad,button);row.append(text,button);$('#publishedList').append(row)}
  $('#publishedStatus').textContent=ads.length?'Se muestran las 20 publicaciones más recientes. Al eliminar una, se retira del inicio y de Avisos cuando el cliente actualiza.':'No hay publicaciones.';
 }catch(e){$('#publishedStatus').textContent=accessError(e)}
}
async function removePublished(ad,button){
 if(deleting||!allowed||!service.auth.currentUser)return;
 if(!confirm('¿Eliminar «'+ad.title+'» del inicio y de Avisos? Las notificaciones que ya recibió Android no se pueden retirar desde este panel.'))return;
 deleting=true;publicationLoad++;button.disabled=true;$('#publishedStatus').textContent='Eliminando publicación…';
 try{await deleteAdvertisement(service,ad.announcement_id);await loadPublished();$('#publishedStatus').textContent='Publicación eliminada. El cliente la retirará al actualizar o volver al inicio.'}
 catch(e){$('#publishedStatus').textContent=accessError(e)+' Puedes reintentar el mismo anuncio.'}
 finally{deleting=false;button.disabled=false}
}
$('#refreshPublished').onclick=loadPublished;
