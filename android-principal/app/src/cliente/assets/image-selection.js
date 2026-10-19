(function(){
 if(window.grifplotInstalled)return;window.grifplotInstalled=true;window.grifplotKeys=[];
 function https(s){if(!s)return '';try{var u=new URL(s,location.href);if(u.protocol==='http:')u.protocol='https:';return u.protocol==='https:'?u.href:'';}catch(e){return '';}}
 function proxy(s){try{var h=new URL(s).hostname;return /(^|\.)gstatic\.com$/.test(h)||/(^|\.)googleusercontent\.com$/.test(h);}catch(e){return false;}}
 function decode(s){try{return JSON.parse('"'+s+'"');}catch(e){return '';}}
 var originals={},ids={},indexed=new WeakMap();
 function indexOriginals(){document.querySelectorAll('script').forEach(function(script){var text=script.textContent||'';if(indexed.get(script)===text.length)return;indexed.set(script,text.length);
  // Google's image tuples contain a thumbnail and a separate full-size URL, sometimes under an image ID.
  var pair=/\["((?:[^"\\]|\\.)*)",\s*\d+,\s*\d+\],\s*\["((?:[^"\\]|\\.)*)",\s*(\d+),\s*(\d+)\]/g,m;
  while((m=pair.exec(text))!==null){var thumb=decode(m[1]),full=https(decode(m[2]));if(full&&!proxy(full)){var item={url:full,width:+m[4],height:+m[3]};originals[thumb]=item;var prefix=text.slice(Math.max(0,m.index-240),m.index),identifier=/\["([^"\\]+)",\s*$/.exec(prefix);if(identifier)ids[identifier[1]]=item;}}
 });}
 function info(img){var original='',source='',title=img.alt||'Imagen',id='',width=0,height=0;var thumb=img.currentSrc||img.src||img.getAttribute('data-src')||'';if(!https(thumb)&&!/^data:image\/(png|jpeg|gif|webp);base64,/i.test(thumb))thumb=img.getAttribute('data-src')||'';if(thumb.length>350000)thumb='';var node=img;
  for(var depth=0;node&&depth<5;depth++,node=node.parentElement){
   if(!id)id=node.getAttribute('data-docid')||node.getAttribute('data-id')||node.getAttribute('data-iid')||'';
   if(!original){var attr=https(node.getAttribute('data-ou')||node.getAttribute('data-iurl')||node.getAttribute('data-original')||'');if(attr&&!proxy(attr))original=attr;}
   if(!source)source=https(node.getAttribute('data-ru')||node.getAttribute('data-lpage')||'');
   // Only use the clicked image's container, never a surrounding grid with other results.
   var anchors=node.tagName==='A'?[node]:node.querySelectorAll('a[href]');
   if(depth>1&&Array.prototype.filter.call(node.querySelectorAll('img'),candidate).length>1)break;
   for(var j=0;j<anchors.length&&j<8;j++)try{var link=new URL(anchors[j].href,location.href);var full=https(link.searchParams.get('imgurl'));if(full&&!proxy(full))original=full;var ref=https(link.searchParams.get('imgrefurl'));if(ref)source=ref;if(!source&&!/(^|\.)google\.[a-z.]+$/.test(link.hostname))source=https(link.href);}catch(e){}
  }
  var mapped=originals[img.getAttribute('data-src')||'']||originals[thumb]||ids[id];if(!original&&mapped){original=mapped.url;width=mapped.width;height=mapped.height;}
  if(!original&&https(thumb)&&!proxy(thumb))original=https(thumb);
  var key=id?'id:'+id:thumb||original||source;if(key.indexOf('data:')===0){var hash=0;for(var k=0;k<key.length;k++)hash=(hash*31+key.charCodeAt(k))|0;key='inline:'+hash+':'+key.length;}
  return {url:original||https(thumb),original:original,google:thumb,source:source||(thumb||original?location.href:''),title:title,key:key,width:width,height:height};
 }
 function candidate(img){var r=img.getBoundingClientRect();return r.width>=70&&r.height>=60&&img.closest('a,div');}
 window.grifplotPaint=function(){document.querySelectorAll('img').forEach(function(img){if(!candidate(img))return;var data=info(img),active=window.grifplotKeys.indexOf(data.key)>=0||window.grifplotKeys.indexOf(data.url)>=0;img.style.outline=active?'6px solid #c91c2e':'';img.style.outlineOffset='-6px';img.style.opacity=active?'0.7':'';img.setAttribute('aria-selected',active?'true':'false');});};
 window.grifplotSelectionInfo=function(keys){indexOriginals();var result=[];document.querySelectorAll('img').forEach(function(img){if(!candidate(img))return;var data=info(img);if(keys.indexOf(data.key)>=0)result.push(data);});return result;};
 window.addEventListener('click',function(e){var img=e.target.closest&&e.target.closest('img');if(!img||!candidate(img))return;e.preventDefault();e.stopImmediatePropagation();indexOriginals();location.href='grifplot-image://select?data='+encodeURIComponent(JSON.stringify(info(img)));},true);
 document.addEventListener('focusin',function(e){if(e.target.matches('input,textarea,[contenteditable=true]'))e.target.blur();},true);
 var timer;new MutationObserver(function(){clearTimeout(timer);timer=setTimeout(function(){indexOriginals();window.grifplotPaint();},150);}).observe(document.body,{childList:true,subtree:true});
 indexOriginals();window.grifplotPaint();
})();
