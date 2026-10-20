const fs=require('node:fs');
const path=require('node:path');
const vm=require('node:vm');
const {stripTypeScriptTypes}=require('node:module');
const assert=require('node:assert/strict');
const file=process.env.GRAFIPLOT_FUNCTION_SOURCE||path.resolve(__dirname,'../../supabase/functions/grafiplot-chat/index.ts');
const source=fs.readFileSync(file,'utf8').replace(/^import .*;\n/gm,'');
let handler;let calls=0;
const context={URL,Response,Request,TextEncoder,console,createRemoteJWKSet:()=>({}),jwtVerify:async()=>{throw Error('invalid token')},Deno:{env:{get:()=>''},serve:fn=>handler=fn},fetch:async()=>{calls++;throw Error('unexpected fetch')}};
vm.runInNewContext(stripTypeScriptTypes(source),context);
(async()=>{
 const base='https://example.test/functions/v1/grafiplot-chat';
 const status=await handler(new Request(base+'?action=status'));
 assert.equal((await status.json()).chatEnabled,false);
 for(const action of ['inbox','messages','send','device']){
  for(const req of [new Request(base+'?action='+action),new Request(base,{method:'POST',body:JSON.stringify({action})})]){
   const r=await handler(req);assert.equal(r.status,410,action);assert.equal((await r.json()).chatEnabled,false);
  }
 }
 const r=await handler(new Request(base,{method:'POST',body:JSON.stringify({action:'announcement'})}));
 assert.equal(r.status,401,'advertising still requires authenticated owner');
 assert.equal(calls,0,'retired routes must not access history or send notifications');
 console.log('Chat removal: status, eight retired routes and advertising authentication passed');
})().catch(e=>{console.error(e);process.exitCode=1});
