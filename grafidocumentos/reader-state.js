/* Local reading session. Document data stays inside this device. */
(function(){'use strict';
const routeKey='grafi.reader.route';
function mark(kind){try{localStorage.setItem(routeKey,kind)}catch(e){}}
window.ReaderRoute={mark};
try{const first=!sessionStorage.getItem('grafi.reader.started');sessionStorage.setItem('grafi.reader.started','1');if(first&&location.pathname.endsWith('/index.html')&&!location.search&&localStorage.getItem(routeKey)==='pdf'){window.readerRedirecting=true;location.replace('pdf.html?resume=1')}}catch(e){}
document.addEventListener('click',function(e){const el=e.target.closest('a,button');if(!el)return;const target=(el.getAttribute('href')||'')+' '+(el.getAttribute('onclick')||'');if(target.includes('pdf.html'))mark('pdf');else if(target.includes('index.html'))mark('word');else if(target.includes('suite.html'))mark('suite')},true);
})();
