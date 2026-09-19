import { useState } from 'react'
import { API_URL } from '../api/api'

function Login({ onLoginSuccess }) {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')

  const handleSubmit = async (event) => {
    event.preventDefault()
    setError('')

    try {
      const response = await fetch(
        `${API_URL}/api/auth/login`,
        {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            email,
            password,
          }),
        }
      )

      if (!response.ok) {
        throw new Error('Invalid email or password')
      }

      const data = await response.json()

      onLoginSuccess(data.accessToken)
    } catch (error) {
      setError(error.message)
    }
  }

  return (
    <form className="auth-form" onSubmit={handleSubmit}>
      <div className="form-heading">
        <h2>Welcome back</h2>
        <p>Sign in to continue to your dashboard.</p>
      </div>

      <div className="form-group">
        <label htmlFor="email">Email</label>

        <input
          id="email"
          type="email"
          placeholder="you@example.com"
          value={email}
          onChange={(event) => setEmail(event.target.value)}
          required
        />
      </div>

      <div className="form-group">
        <label htmlFor="password">Password</label>

        <input
          id="password"
          type="password"
          placeholder="Enter your password"
          value={password}
          onChange={(event) => setPassword(event.target.value)}
          required
        />
      </div>

      {error && (
        <p className="error-message">{error}</p>
      )}

      <button className="primary-button" type="submit">
        Sign in
      </button>
    </form>
  )
}

export default Login