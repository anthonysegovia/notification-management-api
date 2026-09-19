import { useCallback, useState } from 'react'
import './App.css'
import Login from './components/Login'
import Register from './components/Register'
import Dashboard from './components/Dashboard'

function App() {
  const [showRegister, setShowRegister] = useState(false)

  const [token, setToken] = useState(
    () => localStorage.getItem('accessToken')
  )

  const [theme, setTheme] = useState(
    () => localStorage.getItem('theme') || 'light'
  )

  const handleLogin = (accessToken) => {
    localStorage.setItem('accessToken', accessToken)
    setToken(accessToken)
  }

  const handleLogout = useCallback(() => {
    localStorage.removeItem('accessToken')
    setToken(null)
  }, [])

  const toggleTheme = () => {
    const newTheme = theme === 'light' ? 'dark' : 'light'

    localStorage.setItem('theme', newTheme)
    setTheme(newTheme)
  }

  if (token) {
    return (
<main className={`app ${theme}`}>
  <button
    className="theme-toggle"
    onClick={toggleTheme}
    aria-label="Toggle theme"
    title={
      theme === 'light'
        ? 'Switch to dark mode'
        : 'Switch to light mode'
    }
  >
    {theme === 'light' ? '☾' : '☀'}
  </button>

  <Dashboard
    token={token}
    onLogout={handleLogout}
  />
</main>
    )
  }

  return (
    <main className={`auth-page ${theme}`}>
      <button
        className="theme-toggle"
        onClick={toggleTheme}
        aria-label="Toggle theme"
      >
        {theme === 'light' ? '☾' : '☀'}
      </button>

      <section className="auth-container">
        <div className="brand">
          <div className="brand-icon">N</div>

          <h1>Notification Manager</h1>

          <p>
            Manage and send your notifications from one place.
          </p>
        </div>

        <div className="auth-card">
          {showRegister ? (
            <>
              <Register
                onRegistrationSuccess={() =>
                  setShowRegister(false)
                }
              />

              <p className="auth-switch">
                Already have an account?{' '}
                <button
                  className="link-button"
                  onClick={() => setShowRegister(false)}
                >
                  Sign in
                </button>
              </p>
            </>
          ) : (
            <>
              <Login onLoginSuccess={handleLogin} />

              <p className="auth-switch">
                Don't have an account?{' '}
                <button
                  className="link-button"
                  onClick={() => setShowRegister(true)}
                >
                  Create account
                </button>
              </p>
            </>
          )}
        </div>
      </section>
    </main>
  )
}

export default App