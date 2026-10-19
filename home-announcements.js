(()=>{
 const section=document.querySelector('#featuredAnnouncements');if(!section)return;
 const area=section.querySelector('.featured-content'),count=section.querySelector('.featured-count'),prev=section.querySelector('[data-previous]'),next=section.querySelector('[data-next]'),status=document.querySelector('#announcementStatus'),refreshButton=document.querySelector('#refreshAnnouncements');
 let messages=[],index=0,config,busy=false;
 function render(){section.hidden=!messages.length;if(!messages.length)return;area.innerHTML=GrafiplotAvisos.card(messages[index]);area.firstElementChild.classList.add('featured-card');count.textContent=(index+1)+' de '+messages.length;prev.hidden=next.hidden=count.hidden=messages.length<2;prev.disabled=index===0;next.disabled=index===messages.length-1}
 prev.onclick=()=>{if(index>0){index--;render()}};next.onclick=()=>{if(index<messages.length-1){index++;render()}};
 async function refresh(){
  if(busy||document.hidden)return;busy=true;if(refreshButton)refreshButton.disabled=true;
  try{
   config=config||await GrafiplotAvisos.config();
   if(!config.announcementsEnabled||(!config.advertisingFirebase?.projectId&&!/^https:\/\//.test(config.feedUrl||'')))return;
   const data=await GrafiplotAvisos.feed(config),active=messages[index]?.announcement_id;
   messages=data.slice(0,20);index=Math.max(0,messages.findIndex(m=>m.announcement_id===active));render();if(status)status.hidden=true;
  }catch(e){if(status){status.hidden=false;status.textContent='No se pudo actualizar la publicidad. Revisa tu conexión. ';const retry=document.createElement('button');retry.textContent='Reintentar';retry.className='contact';retry.onclick=refresh;status.append(retry)}}
  finally{busy=false;if(refreshButton)refreshButton.disabled=false}
 }
 if(refreshButton)refreshButton.onclick=refresh;
 document.addEventListener('visibilitychange',()=>{if(!document.hidden)refresh()});refresh();setInterval(refresh,60000);
})();
