const assert=require('node:assert/strict'),fs=require('node:fs'),path=require('node:path'),vm=require('node:vm');
const {pathToFileURL}=require('node:url');const {JSDOM}=require('jsdom');
const root=path.resolve(__dirname,'../..');
(async()=>{
 const dom=new JSDOM('<div id="preview"></div><p id="imageStatus"></p>',{runScripts:'outside-only',url:'https://example.com'}),w=dom.window;
 vm.runInContext(fs.readFileSync(path.join(root,'avisos-common.js'),'utf8'),dom.getInternalVMContext());const a=w.GrafiplotAvisos;
 for(const style of ['aviso','promocion','importante','imagen']){const card=a.card({title:'Anuncio',body:'Texto',style,imageUrl:'https://encrypted-tbn0.gstatic.com/images?q=tbn:fixture&s=10+'});w.document.querySelector('#preview').innerHTML=card;assert.ok(w.document.querySelector('article.has-image img.advert-image'));}
 assert.equal(a.imageUrl('javascript:alert(1)'), '');assert.equal(a.imageUrl('https://user:pass@example.com/image.jpg'),'');assert.match(a.imageUrl('https://drive.google.com/file/d/abc_123/view?usp=sharing'),/thumbnail\?id=abc_123/);
 w.document.querySelector('img').dispatchEvent(new w.Event('error'));assert.equal(w.document.querySelector('article').classList.contains('has-image'),false);assert.match(w.document.querySelector('#imageStatus').textContent,/No se pudo cargar/);assert.match(w.document.querySelector('article').textContent,/Anuncio/);
 const {announcementMessage}=await import(pathToFileURL(path.join(root,'supabase/functions/grafiplot-chat/announcement-message.mjs')));
 const id='11111111-2222-4333-8444-555555555555';const input={requestId:id,title:'Importante',body:'Texto',style:'importante',target:'inicio',imageUrl:'https://example.com/image.jpg'};
 const payload=announcementMessage(input);assert.equal(payload.notification.title,input.title);assert.equal(payload.android.notification.channel_id,'grafiplot_importante');assert.equal(payload.data.announcement_id,id);assert.equal(payload.android.notification.image,input.imageUrl);
 const huge=announcementMessage({...input,title:'🙂'.repeat(40),body:'🙂'.repeat(500),imageUrl:'https://example.com/'+('a'.repeat(1400))+'.jpg'});assert.ok(Buffer.byteLength(JSON.stringify(huge))<=4000,'FCM payload must fit even with long URLs and multibyte text');
 const {deleteAdvertisement}=await import(pathToFileURL(path.join(root,'publicidad-utils.mjs')));let deleted=0;
 const svc={auth:{currentUser:{email:'grafiplotvasquez@gmail.com',emailVerified:true,getIdToken:async()=>{}}},fs:{doc:(db,col,id)=>({col,id}),deleteDoc:async ref=>{assert.equal(ref.id,id);deleted++}}};
 assert.equal((await deleteAdvertisement(svc,id)).deleted,true);assert.equal(deleted,1);
 svc.auth.currentUser.emailVerified=false;await assert.rejects(deleteAdvertisement(svc,id));assert.equal(deleted,1,'unverified account must not initiate deletion');
 const ui=new JSDOM('<section id="pushHome"><span data-notification-status></span><button data-enable-notifications></button></section><pre id="pushDiagnostics"></pre>',{runScripts:'outside-only'});let status={configured:true,initialized:true,permission:false,consent:false,subscribed:false},enabled=0;
 ui.window.AndroidPrint={notificationStatus:()=>JSON.stringify(status),enableNotifications:()=>enabled++};vm.runInContext(fs.readFileSync(path.join(root,'notification-ui.js'),'utf8'),ui.getInternalVMContext());ui.window.document.querySelector('button').click();assert.equal(enabled,1);assert.match(ui.window.document.querySelector('span').textContent,/Activa/);
 status={...status,permission:true,consent:true,subscribed:true,channelEnabled:true};ui.window.GrafiplotNotificationUI.refresh();assert.ok(ui.window.document.querySelector('button').hidden);assert.match(ui.window.document.querySelector('span').textContent,/activadas/);
 status.error='Registro rechazado';status.subscribed=false;ui.window.GrafiplotNotificationUI.refresh();assert.match(ui.window.document.querySelector('span').textContent,/Registro rechazado/);
 ui.window.close();dom.window.close();console.log('PASS: backgrounds on all styles, image failures, safe URLs, system notification payload, payload size, authorized deletion helper and native status UI.');
})().catch(e=>{console.error(e);process.exitCode=1});
