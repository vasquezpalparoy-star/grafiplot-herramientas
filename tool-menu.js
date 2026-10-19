(() => {
  const descriptions = {
    'fut.html': {
      features: ['Completar tus datos y la solicitud del FUT de UNHEVAL.', 'Ajustar el texto y revisar el documento.', 'Descargar el PDF o enviarlo a la impresora del local desde la APK.']
    },
    'poster.html': {
      features: ['Elegir una imagen o una página de un PDF.', 'Dividir el diseño en varias hojas A4.', 'Descargar las hojas para imprimirlas y unir tu póster.']
    },
    'order.html': {
      features: ['Agregar tus archivos PDF y ordenar sus páginas.', 'Elegir la numeración para tu documento.', 'Revisar el resultado y descargar el PDF preparado.']
    },
    'coloring.html': {
      features: ['Cargar una imagen desde tu dispositivo.', 'Convertirla en un dibujo de líneas para pintar o en escala de grises.', 'Ajustar el resultado y descargarlo.']
    },
    'caratulas/web/': {
      features: ['Elegir universidad, facultad, carrera y logos.', 'Completar un trabajo individual o grupal y personalizar su formato.', 'Descargar la carátula en PDF o imprimirla desde la APK.']
    },
    'impresion.html': {
      features: ['Conectar con la impresora HP del local.', 'Cargar un PDF y revisar las opciones de impresión.', 'Confirmar el envío y consultar el estado del trabajo.'],
      note: 'Necesitas la APK de clientes y estar en la misma red Wi-Fi que la impresora.'
    },
    'libros.html': {
      features: ['Buscar libros y exámenes de las carpetas que elijas en tu dispositivo.', 'Leer PDF con zoom y buscar PDF en Google.', 'Seleccionar imágenes y preparar un ZIP para WhatsApp.', 'Solicitar la impresión de un libro por WhatsApp.'],
      note: 'Esta herramienta funciona dentro de la APK de clientes. El catálogo y los precios se configuran en cada dispositivo; las búsquedas en internet requieren conexión.'
    }
  };
  const dialog = document.querySelector('#toolIntro');
  if (!dialog) return;
  let selected;
  const close = () => dialog.close();
  dialog.querySelector('.intro-close').addEventListener('click', close);
  dialog.querySelector('.intro-cancel').addEventListener('click', close);
  dialog.addEventListener('click', event => { if (event.target === dialog) {
    const rect = dialog.getBoundingClientRect();
    if (event.clientX < rect.left || event.clientX > rect.right || event.clientY < rect.top || event.clientY > rect.bottom) close();
  }});
  dialog.addEventListener('close', () => { document.body.classList.remove('intro-open'); selected?.focus(); });
  document.querySelectorAll('.grid > a.card').forEach(card => {
    const detail = descriptions[card.getAttribute('href')];
    if (!detail) return;
    card.setAttribute('aria-haspopup', 'dialog');
    card.addEventListener('click', event => {
      if (event.ctrlKey || event.metaKey || event.shiftKey || event.altKey) return;
      event.preventDefault();
      selected = card;
      document.querySelector('#introTitle').textContent = card.querySelector('h3').textContent;
      document.querySelector('#introDescription').textContent = card.querySelector('p').textContent;
      const features = document.querySelector('#introFeatures');
      features.replaceChildren(...detail.features.map(text => { const li = document.createElement('li'); li.textContent = text; return li; }));
      const note = document.querySelector('#introNote');
      note.hidden = !detail.note;
      note.textContent = detail.note || '';
      document.querySelector('#introContinue').href = card.href;
      document.body.classList.add('intro-open');
      dialog.showModal();
    });
  });
})();
