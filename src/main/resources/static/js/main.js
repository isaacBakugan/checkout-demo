/**
 * main.js
 * 
 * Script de cliente para probar el endpoint /ping desde el front.
 * 
 * - Toma la API Key del input.
 * - Realiza una solicitud GET al backend en /ping.
 * - Muestra el resultado (código de estado y respuesta) en la página.
 */

// Helper para acceder rápido a elementos por ID
const $ = id => document.getElementById(id);

/**
 * Manejador del botón "Ping".
 * Envía una solicitud al endpoint /ping usando la API Key provista.
 */
$('btn').onclick = async () => {
  const key = $('key').value.trim();
  $('out').textContent = 'Consultando...';
  try {
    const res = await fetch('/ping', { headers: { 'X-API-KEY': key } });
    const text = await res.text();
    $('out').textContent = `HTTP ${res.status}\n${text}`;
  } catch (e) {
    $('out').textContent = String(e);
  }
};
