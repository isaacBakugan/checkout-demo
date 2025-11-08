const $ = id => document.getElementById(id);

const api = async (path, method = 'GET', body) => {
  const key = $('key').value.trim();
  const headers = { 'X-API-KEY': key, 'Content-Type': 'application/json' };
  const res = await fetch(path, {
    method,
    headers,
    body: body ? JSON.stringify(body) : undefined
  });
  const text = await res.text();
  let data;
  try { data = JSON.parse(text); } catch { data = text; }
  return { status: res.status, data };
};

// Ping
$('btnPing').onclick = async () => {
  $('outPing').textContent = 'Consultando /ping...';
  try {
    const r = await api('/ping');
    $('outPing').textContent = `HTTP ${r.status}\n${typeof r.data==='string'? r.data : JSON.stringify(r.data,null,2)}`;
  } catch (e) {
    $('outPing').textContent = String(e);
  }
};

// Registro
$('btnRegister').onclick = async () => {
  $('outRegister').textContent = 'Registrando...';
  const body = {
    name: $('r_name').value.trim(),
    email: $('r_email').value.trim(),
    phone: $('r_phone').value.trim(),
    address: $('r_address').value.trim(),
    password: $('r_password').value
  };
  try {
    const r = await api('/customers', 'POST', body);
    $('outRegister').textContent = `HTTP ${r.status}\n${JSON.stringify(r.data, null, 2)}`;
  } catch (e) {
    $('outRegister').textContent = String(e);
  }
};

// Login
$('btnLogin').onclick = async () => {
  $('outLogin').textContent = 'Autenticando...';
  const body = {
    email: $('l_email').value.trim(),
    password: $('l_password').value
  };
  try {
    const r = await api('/auth/login', 'POST', body);
    $('outLogin').textContent = `HTTP ${r.status}\n${JSON.stringify(r.data, null, 2)}`;
  } catch (e) {
    $('outLogin').textContent = String(e);
  }
};
