(() => {
  const button = document.querySelector('#openLibrary');
  const status = document.querySelector('#libraryStatus');
  if (typeof window.AndroidPrint?.openLibrary !== 'function') return;
  button.hidden = false;
  status.textContent = 'Tu biblioteca se abre dentro de Grafiplot. Usa «Volver a herramientas» para regresar al menú principal.';
  let opening = false;
  const open = () => {
    if (opening) return;
    opening = true;
    try { window.AndroidPrint.openLibrary(); }
    catch { status.textContent = 'No se pudo abrir la biblioteca. Comprueba que instalaste la última APK de clientes.'; }
    window.setTimeout(() => { opening = false; }, 800);
  };
  button.addEventListener('click', open);
  open();
})();
