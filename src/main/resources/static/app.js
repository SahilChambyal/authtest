import { useEffect, useState } from 'react';
import { registerUser, loginUser, fetchMe, logout, startGoogleLogin } from './api';

export default function App() {
  const [me, setMe] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  // Forms
  const [reg, setReg] = useState({ name: '', email: '', password: '' });
  const [login, setLogin] = useState({ email: '', password: '' });

  useEffect(() => {
    (async () => {
      setLoading(true);
      const m = await fetchMe();
      setMe(m);
      setLoading(false);
    })();
  }, []);

  const onRegister = async (e) => {
    e.preventDefault();
    setError('');
    try {
      await registerUser(reg);
      alert('Registered! Now login.');
    } catch (e) {
      setError(e.message);
    }
  };

  const onLogin = async (e) => {
    e.preventDefault();
    setError('');
    try {
      await loginUser(login);
      const m = await fetchMe();
      setMe(m);
    } catch (e) {
      setError(e.message);
    }
  };

  const onLogout = async () => {
    await logout();
    setMe(null);
  };

  if (loading) return <p>Loading...</p>;

  return (
    <div style={{maxWidth: 640, margin: '40px auto', fontFamily: 'sans-serif'}}>
      <h1>Auth Demo (React + Spring Boot)</h1>

      {me ? (
        <div>
          <h2>Welcome</h2>
          <pre>{JSON.stringify(me, null, 2)}</pre>
          <button onClick={onLogout}>Logout</button>
        </div>
      ) : (
        <>
          <section style={{display: 'flex', gap: 24}}>
            <form onSubmit={onRegister} style={{flex: 1}}>
              <h2>Sign Up (Email)</h2>
              <input placeholder="Name" value={reg.name} onChange={e=>setReg({...reg, name:e.target.value})} required />
              <br/>
              <input type="email" placeholder="Email" value={reg.email} onChange={e=>setReg({...reg, email:e.target.value})} required />
              <br/>
              <input type="password" placeholder="Password" value={reg.password} onChange={e=>setReg({...reg, password:e.target.value})} required />
              <br/>
              <button type="submit">Register</button>
            </form>

            <form onSubmit={onLogin} style={{flex: 1}}>
              <h2>Login (Email)</h2>
              <input type="email" placeholder="Email" value={login.email} onChange={e=>setLogin({...login, email:e.target.value})} required />
              <br/>
              <input type="password" placeholder="Password" value={login.password} onChange={e=>setLogin({...login, password:e.target.value})} required />
              <br/>
              <button type="submit">Login</button>
            </form>
          </section>

          <div style={{marginTop: 24}}>
            <h3>Or</h3>
            <button onClick={startGoogleLogin}>Continue with Google</button>
          </div>

          {error && <p style={{color: 'red'}}>{error}</p>}
        </>
      )}
    </div>
  );
}