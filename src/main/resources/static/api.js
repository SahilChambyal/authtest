const API_BASE = import.meta.env.VITE_API_BASE || 'http://localhost:8080';

export async function registerUser({ name, email, password }) {
  const res = await fetch(`${API_BASE}/api/auth/register`, {
    method: 'POST',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify({ name, email, password }),
    credentials: 'include',
  });
  if (!res.ok) {
    const data = await res.json().catch(() => ({}));
    throw new Error(data.error || `Register failed (${res.status})`);
  }
  return true;
}

export async function loginUser({ email, password }) {
  const res = await fetch(`${API_BASE}/api/auth/login`, {
    method: 'POST',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify({ email, password }),
    credentials: 'include',
  });
  const data = await res.json().catch(() => ({}));
  if (!res.ok) throw new Error(data.error || `Login failed (${res.status})`);
  return data; // contains accessToken + expiresIn (also cookie is set)
}

export async function logout() {
  await fetch(`${API_BASE}/api/auth/logout`, {
    method: 'POST',
    credentials: 'include',
  });
}

export async function fetchMe() {
  const res = await fetch(`${API_BASE}/api/auth/me`, {
    credentials: 'include',
  });
  if (res.status === 401) return null;
  const data = await res.json();
  return data;
}

export function startGoogleLogin() {
  window.location.href = `${API_BASE}/oauth2/authorization/google`;
}
