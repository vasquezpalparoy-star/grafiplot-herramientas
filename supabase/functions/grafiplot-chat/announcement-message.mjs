export function announcementMessage({requestId,title,body,style,target,imageUrl=''}) {
 const titleText=title.trim(),bodyText=body.trim();
 const androidNotification={channel_id:style==='importante'?'grafiplot_importante':'grafiplot_avisos',icon:'ic_notification',tag:requestId,default_sound:true};
 try{const image=new URL(imageUrl);if(image.protocol==='https:')androidNotification.image=image.href}catch{}
 const message={topic:'grafiplot_clientes',notification:{title:titleText,body:Array.from(bodyText).slice(0,160).join('')},data:{announcement_id:requestId,title:titleText,body:bodyText,style,target,imageUrl},android:{priority:'high',ttl:'86400s',notification:androidNotification}};
 const size=()=>new TextEncoder().encode(JSON.stringify(message)).length;
 if(size()>4000)delete message.data.imageUrl;
 while(size()>4000&&message.data.body.length)message.data.body=Array.from(message.data.body).slice(0,-24).join('');
 return message;
}
