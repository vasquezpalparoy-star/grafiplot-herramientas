/* Bridge for the embedded document suite; browser downloads keep their own fallback. */
(()=>{'use strict';
 if(!window.AndroidPrint||typeof AndroidPrint.shareDocument!=='function')return;
 window.AndroidBridge={
  saveBase64(name,mime,data){AndroidPrint.saveFile(data,mime,name)},
  saveFile(name,mime,text){const bytes=new TextEncoder().encode(String(text));let raw='';for(let i=0;i<bytes.length;i+=8192)raw+=String.fromCharCode(...bytes.subarray(i,i+8192));AndroidPrint.saveFile(btoa(raw),mime,name)},
  printDocument(){AndroidPrint.print()},
  shareBase64(name,mime,data,phone){AndroidPrint.shareDocument(name,mime,data,phone)}
 };
})();
