/* Save PDF bytes on open/edit; save lightweight reading position on scroll. */
(function(){'use strict';
const key='grafi.pdf.position';let queue=Promise.resolve();
function db(){return new Promise((resolve,reject)=>{const r=indexedDB.open('grafi-reader',1);r.onupgradeneeded=()=>r.result.createObjectStore('sessions');r.onsuccess=()=>resolve(r.result);r.onerror=()=>reject(r.error)})}
window.PdfSession={
position(){try{return JSON.parse(localStorage.getItem(key)||'null')}catch(e){return null}},
writePosition(value){try{localStorage.setItem(key,JSON.stringify(value))}catch(e){}},
writeDocument(value){const next=queue.catch(()=>{}).then(async()=>{const d=await db();try{await new Promise((resolve,reject)=>{const tx=d.transaction('sessions','readwrite');tx.objectStore('sessions').put(value,'pdf');tx.oncomplete=resolve;tx.onabort=tx.onerror=()=>reject(tx.error||Error('Sin espacio local'))})}finally{d.close()}});queue=next;return next},
async readDocument(){await queue.catch(()=>{});const d=await db();try{return await new Promise((resolve,reject)=>{const r=d.transaction('sessions').objectStore('sessions').get('pdf');r.onsuccess=()=>resolve(r.result);r.onerror=()=>reject(r.error)})}finally{d.close()}}
};
window.acceptNativePdf=async function(meta){const r=await fetch(meta.url);if(!r.ok)throw Error('No se pudo leer el PDF compartido');const bytes=new Uint8Array(await r.arrayBuffer());if(bytes.length>50*1024*1024)throw Error('El límite es 50 MB');const id=Date.now().toString(36)+Math.random().toString(36).slice(2);await PdfSession.writeDocument({id,bytes,name:meta.name.replace(/\.pdf$/i,''),dirty:false});PdfSession.writePosition({id,num:1,fit:true,left:0,top:0,thumbs:false});window.ReaderRoute?.mark('pdf');location.href='pdf.html'};
})();
