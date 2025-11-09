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


// Estado del usuario
const updateUserUI = () => {
  const user = JSON.parse(sessionStorage.getItem('user') || 'null');
  if (user) {
    $('userInfo').textContent = `Sesión: ${user.name} (${user.email})`;
    $('btnLogout').style.display = 'inline-block';
  } else {
    $('userInfo').textContent = 'No autenticado';
    $('btnLogout').style.display = 'none';
  }
};

$('btnLogout').onclick = () => {
  sessionStorage.removeItem('user');
  updateUserUI();
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
    if (r.status === 200 && r.data.authenticated) {
    // Guardamos info básica
    sessionStorage.setItem('user', JSON.stringify({
      name: r.data.name ?? body.email,
      email: body.email,
      token: r.data.token
    }));
    updateUserUI();}
  } catch (e) {
    $('outLogin').textContent = String(e);
  }
};

// --- Tokenización ---
$('btnTokenize').onclick = async () => {
  $('outToken').textContent = 'Tokenizando tarjeta...';

  const body = {
    cardNumber: $('t_card').value.replace(/\s+/g, ''),
    cvv: $('t_cvv').value.trim(),
    expMonth: parseInt($('t_expMonth').value, 10),
    expYear: parseInt($('t_expYear').value, 10),
    cardholderName: $('t_name').value.trim(),
    customerEmail: $('t_email').value.trim() || null
  };

  try {
    const r = await api('/tokenize', 'POST', body);
    if (r.status === 200) {
      $('outToken').textContent = `✅ Token creado: ${r.data.token} — ${r.data.brand} ****${r.data.last4}`;
      // Guardamos el token para el flujo de pago
      sessionStorage.setItem('paymentToken', r.data.token);
    } else {
      $('outToken').textContent = `⚠️ Error ${r.status}: ${JSON.stringify(r.data)}`;
    }
  } catch (e) {
    $('outToken').textContent = `💥 Error de red: ${e}`;
  }
};


// Inicializa UI
updateUserUI();
