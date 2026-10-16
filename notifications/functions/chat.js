const {onRequest}=require('firebase-functions/v2/https');
const {onDocumentCreated}=require('firebase-functions/v2/firestore');
const {getAuth}=require('firebase-admin/auth');
const {getFirestore,FieldValue}=require('firebase-admin/firestore');
const {getMessaging}=require('firebase-admin/messaging');
const {createHash}=require('node:crypto');
const options={region:'us-central1',maxInstances:3,cors:['https://vasquezpalparoy-star.github.io','https://appassets.androidplatform.net']};
exports.registerChatDevice=onRequest(options,async(req,res)=>{
 if(req.method!=='POST')return res.status(405).json({error:'Usa POST'});
 let user;try{user=await getAuth().verifyIdToken((req.headers.authorization||'').replace(/^Bearer /,''),true)}catch(e){return res.status(401).json({error:'Inicia sesión de nuevo'})}
 const {token,remove}=req.body||{};if(typeof token!=='string'||token.length<30||token.length>4096)return res.status(400).json({error:'Dispositivo no válido'});
 const ref=getFirestore().collection('chatDevices').doc(createHash('sha256').update(token).digest('hex'));
 try{if(remove===true){await getFirestore().runTransaction(async tx=>{const old=await tx.get(ref);if(old.exists&&old.data().uid===user.uid)tx.delete(ref)});return res.json({removed:true})}
 await ref.set({uid:user.uid,isAdmin:user.admin===true,token,updatedAt:FieldValue.serverTimestamp()});res.json({registered:true,uid:user.uid});}catch(e){res.status(500).json({error:'No se pudo vincular el dispositivo'})}
});
exports.notifyChatMessage=onDocumentCreated({region:'us-central1',document:'conversations/{conversationId}/messages/{messageId}',maxInstances:3,retry:false},async event=>{
 const m=event.data?.data();if(!m||!['admin','client'].includes(m.role))return;
 const db=getFirestore(),conversationId=event.params.conversationId;
 const query=m.role==='client'?db.collection('chatDevices').where('isAdmin','==',true):db.collection('chatDevices').where('uid','==',conversationId);
 const devices=await query.get();let recipients=devices.docs;
 if(m.role==='client'){const allowed=new Map();for(const d of recipients){const uid=d.data().uid;if(!allowed.has(uid)){try{const account=await getAuth().getUser(uid);allowed.set(uid,!account.disabled&&account.customClaims?.admin===true)}catch(e){allowed.set(uid,false)}}}recipients=recipients.filter(d=>allowed.get(d.data().uid));}
 // El texto privado nunca se incluye en la pantalla de bloqueo ni en el tema público de avisos.
 for(let i=0;i<recipients.length;i+=500){const part=recipients.slice(i,i+500),results=await getMessaging().sendEach(part.map(d=>({token:d.data().token,data:{announcement_id:'chat-'+event.params.messageId,target:'chat',title:'Chat de Grafiplot',body:m.role==='client'?'Tienes un nuevo mensaje de un cliente.':'Grafiplot respondió a tu conversación.',style:'aviso',recipientUid:d.data().uid},android:{priority:'high',ttl:24*60*60*1000}})));
 await Promise.all(results.responses.map((r,j)=>!r.success&&['messaging/registration-token-not-registered','messaging/invalid-registration-token'].includes(r.error?.code)?part[j].ref.delete():Promise.resolve()));}
});
