(()=>{
 const target='https://www.grafiplotvasquez.com/delivery.html';
 const open=()=>{
  if(typeof window.AndroidPrint?.openDelivery==='function'){
   window.AndroidPrint.openDelivery();
   document.querySelector('#deliveryStatus').textContent='Mi Delivery se abre dentro de Grafiplot. Usa Herramientas para regresar al inicio.';
  }else window.location.replace(target);
 };
 document.querySelector('#deliveryOpen').addEventListener('click',event=>{if(typeof window.AndroidPrint?.openDelivery==='function'){event.preventDefault();open()}});
 open();
})();
