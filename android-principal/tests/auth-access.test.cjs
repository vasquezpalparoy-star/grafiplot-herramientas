const assert=require('node:assert/strict');
const fs=require('node:fs'),path=require('node:path'),vm=require('node:vm');
const {JSDOM}=require('jsdom');
const root=path.resolve(__dirname,'../..');
const settle=async()=>{for(let i=0;i<8;i++)await new Promise(setImmediate)};
async function fixture(currentUser,connectionError){
 const dom=new JSDOM(fs.readFileSync(path.join(root,'administrador.html'),'utf8'));
 const doc=dom.window.document;let redirected=null,verificationCount=0,resetCount=0;
 const service={auth:{currentUser},sdk:{
  onAuthStateChanged:(auth,callback)=>callback(auth.currentUser),
  sendEmailVerification:async user=>{assert.equal(user.email,'grafiplotvasquez@gmail.com');verificationCount++},
  sendPasswordResetEmail:async(auth,email)=>{assert.equal(email,'grafiplotvasquez@gmail.com');resetCount++},
  signOut:async auth=>{auth.currentUser=null},
  signInWithEmailAndPassword:async()=>{throw Error('Contraseña incorrecta de prueba')}
 }};
 let fail=connectionError;
 const context=vm.createContext({document:doc,location:{replace:url=>{redirected=url}},advertisingServices:async()=>{if(fail)throw Error('sin conexión');return service},advertisingAdmin:user=>!!user&&user.emailVerified&&user.email==='grafiplotvasquez@gmail.com',accessError:e=>e.message});
 vm.runInContext(fs.readFileSync(path.join(root,'publicidad-acceso.js'),'utf8').replace(/^import .*;\n/,''),context);
 await settle();return {dom,doc,service,get redirected(){return redirected},get verificationCount(){return verificationCount},get resetCount(){return resetCount},recoverConnection:()=>{fail=false}};
}
(async()=>{
 const user={email:'grafiplotvasquez@gmail.com',emailVerified:false,reload:async()=>{},getIdToken:async()=>{}};
 const f=await fixture(user);
 assert.equal(f.doc.querySelector('#authForm').hidden,true,'open session must not ask for its password again');
 assert.equal(f.doc.querySelector('#pendingAccess').hidden,false);
 assert.match(f.doc.querySelector('#authStatus').textContent,/Falta verificar/);
 f.doc.querySelector('#resendVerification').click();await settle();assert.equal(f.verificationCount,1);assert.match(f.doc.querySelector('#authStatus').textContent,/Spam/);
 f.doc.querySelector('#checkVerification').click();await settle();assert.equal(f.redirected,null,'unverified user cannot enter the publication panel');
 user.emailVerified=true;f.doc.querySelector('#checkVerification').click();await settle();assert.equal(f.redirected,'admin-avisos.html');
 const wrong=await fixture({...user,email:'otra@example.com'});assert.equal(wrong.redirected,null);assert.equal(wrong.doc.querySelector('#resendVerification').hidden,true);assert.match(wrong.doc.querySelector('#authStatus').textContent,/no está autorizada/);
 const loggedOut=await fixture(null);assert.equal(loggedOut.doc.querySelector('#pendingAccess').hidden,true);loggedOut.doc.querySelector('#resetPassword').click();await settle();assert.equal(loggedOut.resetCount,1,'recovery does not require a password');
 loggedOut.doc.querySelector('#password').value='incorrecta123';loggedOut.doc.querySelector('#authForm').dispatchEvent(new loggedOut.dom.window.Event('submit',{cancelable:true}));await settle();assert.match(loggedOut.doc.querySelector('#authStatus').textContent,/incorrecta/);assert.equal(loggedOut.doc.querySelector('#password').value,'');
 const offline=await fixture(null,true);assert.equal(offline.doc.querySelector('#retryConnection').hidden,false);offline.doc.querySelector('#resetPassword').click();assert.match(offline.doc.querySelector('#authStatus').textContent,/conexión/);offline.recoverConnection();offline.doc.querySelector('#retryConnection').click();await settle();assert.equal(offline.doc.querySelector('#retryConnection').hidden,true);
 for(const fixture of [f,wrong,loggedOut,offline])fixture.dom.window.close();
 console.log('PASS: pending owner session, email verification, unauthorized account, password-free recovery, login error and connection retry.');
})().catch(e=>{console.error(e);process.exitCode=1});
