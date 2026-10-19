import {advertisingServices,advertisingAdmin,accessError} from './publicidad-auth.js?v=1.7';
const $=s=>document.querySelector(s);
let service,busy=false;
const ownerEmail='grafiplotvasquez@gmail.com';
function say(text){$('#authStatus').textContent=text}
async function session(user){
 $('#authForm').hidden=!!user;
 $('#pendingAccess').hidden=!user;
 if(!user){say('Entra con tu contraseña de Grafiplot. Si es tu primera vez, completa los datos y pulsa Crear mi acceso.');return;}
 const owner=user.email?.toLowerCase()===ownerEmail;
 $('#email').value=user.email||ownerEmail;
 $('#pendingEmail').textContent='Sesión abierta: '+(user.email||'cuenta sin correo');
 $('#resendVerification').hidden=!owner||user.emailVerified;
 $('#checkVerification').hidden=!owner;
 if(advertisingAdmin(user)){say('Correo verificado. Abriendo publicidad…');location.replace('admin-avisos.html');return;}
 say(owner?'Tu cuenta ya está creada y la sesión está abierta. Falta verificar el correo: pulsa Enviar enlace de verificación, abre el enlace recibido y vuelve para pulsar Ya verifiqué mi correo.':'Esta cuenta no está autorizada. Pulsa Usar otra cuenta y entra con '+ownerEmail+'.');
}
async function run(task,message){
 if(busy)return;
 if(!service){say('La conexión de acceso aún no está lista. Revisa el mensaje de arriba y pulsa Reintentar conexión.');return;}
 busy=true;
 const buttons=[...document.querySelectorAll('#authPanel button')];
 buttons.forEach(b=>b.disabled=true);
 say(message||'Comprobando…');
 try{await task()}catch(e){say(accessError(e))}
 finally{busy=false;buttons.forEach(b=>b.disabled=false)}
}
$('#authForm').onsubmit=e=>{e.preventDefault();run(async()=>{
 try{const c=await service.sdk.signInWithEmailAndPassword(service.auth,$('#email').value.trim(),$('#password').value);await c.user.reload();await c.user.getIdToken(true);await session(c.user)}
 finally{$('#password').value=''}
},'Entrando a tu cuenta…')};
$('#signupButton').onclick=()=>{
 if(!$('#authForm').reportValidity())return;
 run(async()=>{
  if($('#email').value.trim().toLowerCase()!==ownerEmail)throw Error('El acceso de publicidad está reservado a '+ownerEmail+'.');
  if($('#password').value.length<8)throw Error('Elige una contraseña de al menos 8 caracteres.');
  let c;
  try{c=await service.sdk.createUserWithEmailAndPassword(service.auth,$('#email').value.trim(),$('#password').value)}finally{$('#password').value=''}
  await session(c.user);
  try{await service.sdk.sendEmailVerification(c.user);say('Cuenta creada. Firebase aceptó el envío del enlace de verificación. Revisa Recibidos y Spam de '+ownerEmail+'. Abre el enlace y pulsa Ya verifiqué mi correo.')}catch(e){say('Cuenta creada, pero no se confirmó el envío del correo: '+accessError(e)+' Pulsa Enviar enlace de verificación para reintentar.');}
 },'Creando tu acceso…');
};
$('#resetPassword').onclick=()=>{
 if(!$('#email').reportValidity())return;
 run(async()=>{
  const email=$('#email').value.trim().toLowerCase();
  if(email!==ownerEmail)throw Error('Para recuperar tu acceso usa '+ownerEmail+'.');
  await service.sdk.sendPasswordResetEmail(service.auth,email);
  say('Firebase aceptó la solicitud de recuperación para '+email+'. Si la cuenta está registrada, recibirás un enlace para cambiar la contraseña. Revisa Recibidos y Spam. Si nunca creaste el acceso, pulsa Crear mi acceso.');
 },'Solicitando el correo de recuperación…');
};
$('#resendVerification').onclick=()=>run(async()=>{
 const user=service.auth.currentUser;if(!user)throw Error('La sesión terminó. Vuelve a entrar.');
 await service.sdk.sendEmailVerification(user);
 say('Firebase aceptó el envío del enlace a '+user.email+'. Revisa Recibidos y Spam. Abre el enlace y vuelve para pulsar Ya verifiqué mi correo.');
},'Solicitando el enlace de verificación…');
$('#checkVerification').onclick=()=>run(async()=>{
 const user=service.auth.currentUser;if(!user)throw Error('La sesión terminó. Vuelve a entrar.');
 await user.reload();await user.getIdToken(true);await session(user);
},'Comprobando la verificación del correo…');
$('#pendingLogout').onclick=()=>run(async()=>{await service.sdk.signOut(service.auth);await session(null)},'Cerrando sesión…');
async function connect(){
 $('#retryConnection').hidden=true;$('#serviceStatus').textContent='Conectando con Publicidad…';
 try{service=await advertisingServices();$('#serviceStatus').textContent='Conexión de acceso lista.';service.sdk.onAuthStateChanged(service.auth,user=>session(user).catch(e=>say(accessError(e))))}
 catch(e){service=null;$('#serviceStatus').textContent=accessError(e);say('No se pudo iniciar la conexión. Pulsa Reintentar conexión.');$('#retryConnection').hidden=false;}
}
$('#retryConnection').onclick=connect;
connect();
