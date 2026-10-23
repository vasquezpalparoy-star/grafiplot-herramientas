const assert=require('node:assert/strict'),fs=require('node:fs'),path=require('node:path'),vm=require('node:vm');
const {JSDOM}=require('jsdom');const root=path.resolve(__dirname,'../..'),suite=path.join(root,'grafidocumentos');
const adapter=fs.readFileSync(path.join(suite,'native-adapter.js'),'utf8');
const web=new JSDOM('',{runScripts:'outside-only'});vm.runInContext(adapter,web.getInternalVMContext());assert.equal(web.window.AndroidBridge,undefined,'browser fallback must not pretend native support');web.window.close();
const dom=new JSDOM('',{runScripts:'outside-only'}),w=dom.window;w.TextEncoder=TextEncoder;
const saves=[],shares=[];let printed=0;w.AndroidPrint={saveFile:(...args)=>saves.push(args),shareDocument:(...args)=>shares.push(args),print:()=>printed++};
vm.runInContext(adapter,dom.getInternalVMContext());
assert.ok(w.document.documentElement.classList.contains('android-documents'));
w.AndroidBridge.saveFile('informe.txt','text/plain','Impresión: niño 📄');assert.equal(Buffer.from(saves[0][0],'base64').toString(),'Impresión: niño 📄');assert.equal(saves[0][2],'informe.txt');
w.AndroidBridge.saveBase64('libro.xlsx','application/test','AQID');assert.deepEqual(saves[1],['AQID','application/test','libro.xlsx']);
w.AndroidBridge.shareBase64('hoja.pdf','application/pdf','AQID','51952628844');assert.deepEqual(shares[0],['hoja.pdf','application/pdf','AQID','51952628844']);w.AndroidBridge.printDocument();assert.equal(printed,1);
for(const page of ['index.html','suite.html','pdf.html']){
 const html=fs.readFileSync(path.join(suite,page),'utf8');const doc=new JSDOM(html).window.document;
 assert.equal(doc.querySelector('.grafiplot-home').getAttribute('href'),'../index.html');
 assert.ok(doc.querySelector('script[src^="native-adapter.js"]'));
 assert.equal(doc.querySelector('header'),null);assert.equal(doc.querySelector('.brandMark'),null);assert.ok(doc.querySelector('.document-actions #save,.document-actions #saveLocal'));assert.ok(doc.querySelector('.document-actions button'));
 for(const el of doc.querySelectorAll('script[src],link[href]')){const ref=el.getAttribute('src')||el.getAttribute('href');if(!/^https?:/.test(ref))assert.ok(fs.existsSync(path.join(suite,ref.split('?')[0])),page+' missing '+ref);}
}
const send=new JSDOM('',{runScripts:'outside-only'});vm.runInContext(fs.readFileSync(path.join(suite,'print-send.js'),'utf8'),send.getInternalVMContext());assert.equal(send.window.PrintSend.normalize('+51','952 628 844'),'51952628844');assert.throws(()=>send.window.PrintSend.normalize('0','123'));
const wasm=fs.readFileSync(path.join(suite,'vendor/pdfium.wasm'));assert.equal(wasm.subarray(0,4).toString('hex'),'0061736d');
dom.window.close();send.window.close();console.log('PASS: document navigation, local dependencies, native save/share/print adapter, Unicode, recipient validation and WASM resource.');
