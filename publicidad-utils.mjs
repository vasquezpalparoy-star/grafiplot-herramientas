export function publicationId(cryptoApi = globalThis.crypto) {
  if (typeof cryptoApi?.randomUUID === 'function') return cryptoApi.randomUUID();
  if (typeof cryptoApi?.getRandomValues !== 'function') throw new Error('Actualiza Android System WebView para publicar desde este teléfono.');
  const bytes = cryptoApi.getRandomValues(new Uint8Array(16));
  bytes[6] = (bytes[6] & 15) | 64;
  bytes[8] = (bytes[8] & 63) | 128;
  const hex = Array.from(bytes, b => b.toString(16).padStart(2, '0')).join('');
  return [hex.slice(0,8),hex.slice(8,12),hex.slice(12,16),hex.slice(16,20),hex.slice(20)].join('-');
}

export async function notifyAdvertisement(service, requestId, message, fetchImpl = globalThis.fetch) {
  if (!service.config.notificationsEnabled) throw Error('Las notificaciones del teléfono todavía no están conectadas. El anuncio se conserva dentro de la aplicación.');
  const endpoint = 'https://intsmwiivbdaversvlky.supabase.co/functions/v1/grafiplot-chat';
  if (service.config.sendUrl !== endpoint) throw Error('La dirección del servicio de notificaciones no es válida.');
  const token = await service.auth.currentUser.getIdToken();
  const response = await waitForPublication(fetchImpl(endpoint, {
    method:'POST', headers:{'Content-Type':'application/json', Authorization:'Bearer '+token},
    body:JSON.stringify({...message, action:'announcement', requestId, notify:true})
  }));
  const result = await response.json();
  if (!response.ok || !result.accepted) throw Error(result.error || 'El anuncio se guardó, pero no se confirmó la notificación.');
  return result;
}

export async function waitForPublication(operation, ms = 20000) {
  let timer;
  try {
    return await Promise.race([operation, new Promise((_, reject) => {
      timer = setTimeout(() => {
        const error = new Error('No se confirmó la publicación a tiempo. El anuncio podría haberse guardado; reintenta sin cambiarlo para comprobar el mismo envío.');
        error.code = 'publication-timeout';
        reject(error);
      }, ms);
    })]);
  } finally { clearTimeout(timer); }
}
